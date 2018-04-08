package com.superscholar.android.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.ServerResponse;
import com.superscholar.android.entity.StorableSubjectBean;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.SubjectBeanConverter;
import com.superscholar.android.tools.UserLib;
import com.zhuangfei.timetable.core.SubjectBean;
import com.zhuangfei.timetable.core.TimetableView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TimetableActivity extends BaseActivity {

    private static final int MODE_JUMP_TO_WEEK = 0;
    private static final int MODE_SET_CURRENT_WEEK = 1;

    private static final String TITLE_JUMP_TO_WEEK = "查看其他周";
    private static final String TITLE_SET_CURRENT_WEEK = "设置当前周";

    private static final String HINT_NO_INPUT = "请输入周数";
    private static final String HINT_WEEK_ILLEGAL = "周数不正确";
    private static final String HINT_NETWORK_ERROR = "网络异常，请检查网络设置";
    private static final String HINT_NO_COURSE = "未查到课程信息";

    private static final int WEEK_MAX = 18;

    private TextView mWeekTextView;
    private TimetableView mTimetableView;
    private List<SubjectBean> mList;
    private ProgressDialog mProgressDialog;
    private int mCurrentWeek = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        initToolbar();
        initView();
        initData();
    }

    private void initToolbar(){
        Toolbar toolbar = findViewById(R.id.timetable_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initView(){
        mWeekTextView = findViewById(R.id.tv_timetable_week);
        mTimetableView = findViewById(R.id.time_table_view_timetable);

        mProgressDialog = new ProgressDialog(TimetableActivity.this);
        mProgressDialog.setTitle("课程表");
        mProgressDialog.setMessage("数据获取中，请稍候...");
        mProgressDialog.setCancelable(false);
    }

    private void createSettingDialog(final int mode){
        String title;
        if(mode == MODE_JUMP_TO_WEEK){
            title = TITLE_JUMP_TO_WEEK;
        } else {
            title = TITLE_SET_CURRENT_WEEK;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_timetable, (ViewGroup)findViewById(android.R.id.content), false);
        final EditText editText = view.findViewById(R.id.et_dialog_timetable_week);
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(view)
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();
                        if("".equals(input)){
                            Toast.makeText(TimetableActivity.this, HINT_NO_INPUT, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int week = Integer.parseInt(input);
                        if(week > WEEK_MAX){
                            Toast.makeText(TimetableActivity.this, HINT_WEEK_ILLEGAL, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(mode == MODE_SET_CURRENT_WEEK){
                            onSetCurrentWeekMenuItemSelected(week);
                            mTimetableView.changeWeek(week,true);
                            mWeekTextView.setText("第" + String.valueOf(week) + "周");
                        } else {
                            mTimetableView.changeWeek(week,false);
                            mWeekTextView.setText("第" + String.valueOf(week) + "周");
                        }
                    }
                })
                .show();
    }

    private void initData(){

        mProgressDialog.show();

        if(isGetData()){
            getTimetableFromCache();
        } else {
            getTimetableFromServer();
        }

    }

    private boolean isGetData(){
        SharedPreferences pref = getSharedPreferences("data_timetable",0);
        boolean isGet = pref.getBoolean("isGet", false);
        if(isGet){
            String refDateStr = pref.getString("refDate", "2018/1/1");
            Date refDate = Date.parseDate(refDateStr);
            mCurrentWeek = pref.getInt("refWeek", 1);
            Date today = new Date();
            refDate.dayAdd(7);
            while(!refDate.isLate(today)){
                mCurrentWeek++;
                refDate.dayAdd(7);
            }
            mWeekTextView.setText("第" + String.valueOf(mCurrentWeek) + "周");
        }
        return isGet;
    }

    private void setData(int week){
        mTimetableView.setDataSource(mList)
                .setCurWeek(week)
	            .setMax(true)
                .showTimetableView();
        mTimetableView.changeWeek(mCurrentWeek, true);
    }

    private void getTimetableFromCache(){
        mList = new ArrayList<>();
        List<StorableSubjectBean> list = DataSupport.findAll(StorableSubjectBean.class);
        for(StorableSubjectBean bean : list){
            mList.add(SubjectBeanConverter.toReal(bean));
        }
        setData(mCurrentWeek);
        mProgressDialog.dismiss();
    }

    private void getTimetableFromServer(){
        ServerConnector.getInstance().queryTimetable(UserLib.getInstance().getUser().getUsername(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        Toast.makeText(TimetableActivity.this, HINT_NETWORK_ERROR, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                final ServerResponse<List<StorableSubjectBean>> data = new Gson()
                        .fromJson(resp, new TypeToken<ServerResponse<List<StorableSubjectBean>>>(){}.getType());
                if(data.getCode() != 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String msg = data.getMessage();
                            Toast.makeText(TimetableActivity.this, msg, Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });
                } else {
                    mList = new ArrayList<>();
                    List<StorableSubjectBean> list = data.getData();
                    for(StorableSubjectBean bean : list){
                        bean.save();
                        mList.add(SubjectBeanConverter.toReal(bean));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mList.size() == 0){
                                Toast.makeText(TimetableActivity.this, HINT_NO_COURSE, Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences.Editor editor = getSharedPreferences("data_timetable",0).edit();
                                Date refDate = new Date();
                                Calendar calendar = Calendar.getInstance();
                                while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                                    refDate.dayAddOne();
                                    calendar.set(refDate.getYear(), refDate.getMonth() - 1, refDate.getDay());
                                }
                                mCurrentWeek = 1;
                                editor.putString("refDate", refDate.toString());
                                editor.putInt("refWeek", mCurrentWeek);
                                editor.putBoolean("isGet", true);
                                editor.apply();
                                mWeekTextView.setText("第" + String.valueOf(mCurrentWeek) + "周");

                                setData(mCurrentWeek);
                            }
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void onGetDataMenuItemClicked(){
        mProgressDialog.show();
        ServerConnector.getInstance().queryTimetable(UserLib.getInstance().getUser().getUsername(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        Toast.makeText(TimetableActivity.this, HINT_NETWORK_ERROR, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body().string();
                final ServerResponse<List<StorableSubjectBean>> data = new Gson()
                        .fromJson(resp, new TypeToken<ServerResponse<List<StorableSubjectBean>>>(){}.getType());
                if(data.getCode() != 0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String msg = data.getMessage();
                            Toast.makeText(TimetableActivity.this, msg, Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<StorableSubjectBean> list = data.getData();
                            if(list.size() == 0){
                                Toast.makeText(TimetableActivity.this, HINT_NO_COURSE, Toast.LENGTH_SHORT).show();
                            } else {
                                DataSupport.deleteAll(StorableSubjectBean.class);
                                Iterator<SubjectBean> iterator = mList.iterator();
                                while(iterator.hasNext()){
                                    iterator.next();
                                    iterator.remove();
                                }
                                for(StorableSubjectBean bean : list){
                                    bean.save();
                                    mList.add(SubjectBeanConverter.toReal(bean));
                                }
                                mTimetableView.notifyDataSourceChanged();

                                SharedPreferences.Editor editor = getSharedPreferences("data_timetable",0).edit();
                                Date refDate = new Date();
                                Calendar calendar = Calendar.getInstance();
                                while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                                    refDate.dayAddOne();
                                    calendar.set(refDate.getYear(), refDate.getMonth() - 1, refDate.getDay());
                                }
                                mCurrentWeek = 1;
                                editor.putString("refDate", refDate.toString());
                                editor.putInt("refWeek", mCurrentWeek);
                                editor.putBoolean("isGet", true);
                                editor.apply();


                                mWeekTextView.setText("第" + String.valueOf(mCurrentWeek) + "周");

                                setData(mCurrentWeek);
                            }
                            mProgressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }


    private void onSetCurrentWeekMenuItemSelected(int week){
        mCurrentWeek = week;
        SharedPreferences.Editor editor = getSharedPreferences("data_timetable",0).edit();
        Date refDate = new Date();
        Calendar calendar = Calendar.getInstance();
        while(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
            refDate.dayAddOne();
            calendar.set(refDate.getYear(), refDate.getMonth() - 1, refDate.getDay());
        }
        editor.putString("refDate", refDate.toString());
        editor.putInt("refWeek", week);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.jumpToWeek:
                createSettingDialog(MODE_JUMP_TO_WEEK);
                break;
            case R.id.setCurrentWeek:
                createSettingDialog(MODE_SET_CURRENT_WEEK);
                break;
            case R.id.getDataAgain:
                onGetDataMenuItemClicked();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
