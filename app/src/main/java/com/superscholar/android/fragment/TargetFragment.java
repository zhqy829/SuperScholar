package com.superscholar.android.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.activity.TargetCreateActivity;
import com.superscholar.android.activity.TargetDetailActivity;
import com.superscholar.android.adapter.TargetAdapter;
import com.superscholar.android.control.SlidableFloatingActionButton;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.service.TargetRemindService;
import com.superscholar.android.tools.BoundsTime;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.LocationManager;
import com.superscholar.android.tools.ShakeManager;
import com.superscholar.android.tools.TargetBaseManager;
import com.superscholar.android.tools.Time;
import com.twotoasters.jazzylistview.effects.WaveEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by zhqy on 2017/6/14.
 */

public class TargetFragment extends Fragment{

    //requestCode
    public static final int REQUEST_CREATETARGET = 1;
    public static final int REQUEST_TARGETDETAIL = 2;

    //resultCode RESULT_OK==-1,RESULT_CANCELED==0,RESULT_FIRST_USER==1
    public static final int RESULT_TARGETDETAIL_UPDATE=2;
    public static final int RESULT_TARGETDETAIL_DELETE=3;

    private TargetAdapter adapter;
    private List<TargetItem> targetList=new ArrayList<>();;
    private TextView hintText;    //目标为空时提示信息显示
    private RecyclerView recyclerView;

    private TargetBaseManager targetBaseManager;  //目标数据库管理器

    private TargetRemindService.RemindBinder binder;  //活动和目标提醒服务通信器
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder=(TargetRemindService.RemindBinder)iBinder;
            binder.synchronizeTargetItems(targetList);
            binder.setOnRefreshListener(new TargetRemindService.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateTargetItemAt24();
                            binder.synchronizeTargetItems(targetList);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private ShakeManager shakeManager;  //摇晃管理器
    private LocationManager locationManager; //定位管理器
    private TargetAdapter.ViewHolder holder;
    private ProgressDialog shakeDialog;
    private ProgressDialog locationDialog;


    //变量初始化
    private void initVariable(View view){
        hintText=(TextView)view.findViewById(R.id.target_hintText);
        recyclerView =(RecyclerView)view.findViewById(R.id.taget_recyclerView);
    }

    //初始化目标数据
    private void initTargetBaseManager(){
        targetBaseManager=new TargetBaseManager(getActivity());
        targetList=targetBaseManager.getTargetItemList();

        if(targetList.isEmpty()){
            hintText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    //按钮初始化
    private void initButton(View view){
        SlidableFloatingActionButton fab =(SlidableFloatingActionButton) view.findViewById(R.id.target_fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Calendar calendar=Calendar.getInstance();
                if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
                    Toast.makeText(getActivity(),"只有星期天才能创建一周的目标哦",Toast.LENGTH_SHORT).show();
                    return;
                }
                */
                Intent intent=new Intent(getActivity(), TargetCreateActivity.class);
                startActivityForResult(intent,REQUEST_CREATETARGET);
            }
        });
    }

    //recyclerView初始化
    private void initRecyclerView(){
        JazzyRecyclerViewScrollListener jazzyScrollListener=new JazzyRecyclerViewScrollListener();
        recyclerView.addOnScrollListener(jazzyScrollListener);
        jazzyScrollListener.setTransitionEffect(new WaveEffect());

        final LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter=new TargetAdapter(targetList);

        adapter.setOnItemClickedListener(new TargetAdapter.OnItemClickedListener() {
            @Override
            public void onButtonClicked(TargetAdapter.ViewHolder holder) {
                TargetItem targetItem=targetList.get(holder.getAdapterPosition());
                if(targetItem.isCheck()){
                    switch(targetItem.getName()){
                        case "早起":
                            if(!targetItem.isValid()){
                                Toast.makeText(getActivity(),"目标已失效，可以领取学分绩了",Toast.LENGTH_SHORT).show();
                            }else if(targetItem.isTodaySign()){
                                Toast.makeText(getActivity(),"今天已打卡，明天再来哦",Toast.LENGTH_SHORT).show();
                            }else{
                                Time nowTime=new Time(Calendar.getInstance());
                                if(targetItem.getRemindTime().isInBounds(nowTime)){
                                    shakeDialog.setMessage("检测中，请晃动手机...");
                                    shakeDialog.show();
                                    shakeManager.start();
                                    TargetFragment.this.holder=holder;
                                }else{
                                    Toast.makeText(getActivity(),"只能在设定的打卡时间范围内打卡哦",Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        case "自习":
                            if(!targetItem.isValid()){
                                Toast.makeText(getActivity(),"目标已失效，可以领取学分绩了",Toast.LENGTH_SHORT).show();
                            }else if(targetItem.isTodaySign()){
                                Toast.makeText(getActivity(),"今天已打卡，明天再来哦",Toast.LENGTH_SHORT).show();
                            }else {
                                Time nowTime = new Time(Calendar.getInstance());
                                if (targetItem.getRemindTime().isInBounds(nowTime)) {
                                    locationDialog.show();
                                    locationManager.startLocation(LocationManager.MODE_STUDY);
                                    TargetFragment.this.holder=holder;
                                } else {
                                    Toast.makeText(getActivity(), "只能在设定的打卡时间范围内打卡哦", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        case "运动":
                            if(!targetItem.isValid()){
                                Toast.makeText(getActivity(),"目标已失效，可以领取学分绩了",Toast.LENGTH_SHORT).show();
                            }else if(targetItem.isTodaySign()){
                                Toast.makeText(getActivity(),"今天已打卡，明天再来哦",Toast.LENGTH_SHORT).show();
                            }else {
                                Time nowTime = new Time(Calendar.getInstance());
                                if (targetItem.getRemindTime().isInBounds(nowTime)) {
                                    locationDialog.show();
                                    locationManager.startLocation(LocationManager.MODE_EXER);
                                    TargetFragment.this.holder=holder;
                                } else {
                                    Toast.makeText(getActivity(), "只能在设定的打卡时间范围内打卡哦", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }else{
                    if(!targetItem.isValid()){
                        Toast.makeText(getActivity(),"目标已失效，可以领取学分绩了",Toast.LENGTH_SHORT).show();
                    }else if(targetItem.isTodaySign()){
                        Toast.makeText(getActivity(),"今天已打卡，明天再来哦",Toast.LENGTH_SHORT).show();
                    }else{
                        Date today=new Date();
                        targetItem.getSignDates().add(today);
                        targetItem.setTodaySign(true);

                        if(targetItem.isYesterdaySign()){
                            targetItem.setConsecutiveSignDays(targetItem.getConsecutiveSignDays()+1);
                        }else{
                            targetItem.setConsecutiveSignDays(1);
                        }

                        targetItem.setSignDays(targetItem.getSignDays()+ 1);

                        targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]++;

                        targetBaseManager.updateItem(targetItem);
                        Toast.makeText(getActivity(),"恭喜你，打卡成功",Toast.LENGTH_SHORT).show();

                        holder.statusText.setText("本周打卡"+targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]+"次，目标" +
                                targetItem.getTimesPerWeek()+"次");
                        holder.statusText.setTextColor(Color.parseColor("#1C86EE"));
                    }
                }
            }

            @Override
            public void onViewClicked(int p) {
                //进入详细页
                Intent intent=new Intent(getActivity(),TargetDetailActivity.class);
                intent.putExtra("targetItem",targetList.get(p));
                intent.putExtra("position",p);
                startActivityForResult(intent,REQUEST_TARGETDETAIL);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    //24点时更新目标数据
    private void updateTargetItemAt24(){
        Date todayDate=new Date(Calendar.getInstance());
        for(TargetItem targetItem:targetList){
            if(!targetItem.isValid()){
                continue;
            }

            if(targetItem.isTodaySign()){
                targetItem.setYesterdaySign(true);
            }else{
                targetItem.setYesterdaySign(false);
            }
            targetItem.setTodaySign(false);

            Date endDate=(Date)targetItem.getStartDate().clone();//失效日期，该天已失效
            endDate.dayAdd(targetItem.getLastedWeek()*7);
            if(!endDate.isLate(todayDate)){
                targetItem.setValid(false);
                targetItem.setCurrentWeek(targetItem.getLastedWeek());
            }else{
                int currentWeek=1;
                Date startDate=(Date)targetItem.getStartDate().clone();
                startDate.dayAdd(7);
                while(!startDate.isLate(todayDate)){
                    currentWeek++;
                    startDate.dayAdd(7);
                }
                targetItem.setValid(true);
                targetItem.setCurrentWeek(currentWeek);
            }
        }
    }

    //创建提醒服务
    private void createRemindService(){
        Intent bindIntent=new Intent(getActivity(),TargetRemindService.class);
        getActivity().bindService(bindIntent,serviceConnection,BIND_AUTO_CREATE);
    }

    //摇晃管理器初始化
    private void initShakeManager(){
        shakeManager=new ShakeManager(getActivity());
        shakeManager.setOnResponseListener(new ShakeManager.OnResponseListener() {
            @Override
            public void onShake(int count) {
                shakeDialog.setMessage("已晃动"+count+"次，目标"+ShakeManager.SUCCESS_COUNT+"次");
            }

            @Override
            public void onSuccess() {
                shakeDialog.dismiss();

                TargetItem targetItem=targetList.get(holder.getAdapterPosition());

                Date today=new Date();
                targetItem.getSignDates().add(today);
                targetItem.setTodaySign(true);

                if(targetItem.isYesterdaySign()){
                    targetItem.setConsecutiveSignDays(targetItem.getConsecutiveSignDays()+1);
                }else{
                    targetItem.setConsecutiveSignDays(1);
                }

                targetItem.setSignDays(targetItem.getSignDays()+ 1);

                targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]++;

                targetBaseManager.updateItem(targetItem);
                Toast.makeText(getActivity(),"恭喜你，打卡成功",Toast.LENGTH_SHORT).show();

                holder.statusText.setText("本周打卡"+targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]+"次，目标" +
                        targetItem.getTimesPerWeek()+"次");
                holder.statusText.setTextColor(Color.parseColor("#1C86EE"));
            }

            @Override
            public void onTimeOut() {
                shakeDialog.dismiss();
                Toast.makeText(getActivity(),"打卡失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //定位管理器初始化
    private void initLocationManager(){
        locationManager=new LocationManager(getActivity());
        locationManager.setComparisonResultListener(new LocationManager.ComparisonResultListener() {
            @Override
            public void onPassed(int mode, String name) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationDialog.dismiss();

                        TargetItem targetItem=targetList.get(holder.getAdapterPosition());

                        Date today=new Date();
                        targetItem.getSignDates().add(today);
                        targetItem.setTodaySign(true);

                        if(targetItem.isYesterdaySign()){
                            targetItem.setConsecutiveSignDays(targetItem.getConsecutiveSignDays()+1);
                        }else{
                            targetItem.setConsecutiveSignDays(1);
                        }

                        targetItem.setSignDays(targetItem.getSignDays()+ 1);

                        targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]++;

                        targetBaseManager.updateItem(targetItem);
                        Toast.makeText(getActivity(),"恭喜你，打卡成功",Toast.LENGTH_SHORT).show();

                        holder.statusText.setText("本周打卡"+targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]+"次，目标" +
                                targetItem.getTimesPerWeek()+"次");
                        holder.statusText.setTextColor(Color.parseColor("#1C86EE"));
                    }
                });
            }

            @Override
            public void onFailed(int mode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationDialog.dismiss();
                        Toast.makeText(getActivity(),"打卡失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(final int error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        locationDialog.dismiss();
                        Toast.makeText(getActivity(),"定位出错，错误码："+error,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //对话框初始化
    private void initDialog(){
        shakeDialog=new ProgressDialog(getActivity());
        shakeDialog.setTitle(ShakeManager.TIME_LIMIT+"秒晃动检测");
        shakeDialog.setMessage("检测中，请晃动手机...");
        shakeDialog.setCancelable(false);

        locationDialog=new ProgressDialog(getActivity());
        locationDialog.setTitle("定位检测");
        locationDialog.setMessage("定位中，请稍候...");
        locationDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_target,container,false);
        initVariable(view);
        initTargetBaseManager();
        createRemindService();
        initRecyclerView();
        initDialog();
        initShakeManager();
        initLocationManager();
        initButton(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CREATETARGET:
                if(resultCode==RESULT_OK){
                    TargetItem targetItem=data.getParcelableExtra("targetCreate");
                    targetBaseManager.insertItem(targetItem);
                    targetList.add(targetItem);
                    adapter.notifyItemInserted(targetList.size()-1);
                    Toast.makeText(getActivity(),"创建成功",Toast.LENGTH_SHORT).show();
                    binder.synchronizeTargetItems(targetList);
                    if(hintText.getVisibility()==View.VISIBLE){
                        hintText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case REQUEST_TARGETDETAIL:
                if(resultCode==RESULT_TARGETDETAIL_UPDATE){
                    int position=data.getIntExtra("position",0);
                    TargetItem targetItem=data.getParcelableExtra("targetItem");
                    targetList.set(position,targetItem);
                    adapter.notifyItemChanged(position);
                    targetBaseManager.updateItem(targetItem);
                    if(!targetItem.isCheck())
                        binder.synchronizeTargetItems(targetList);
                }else if(resultCode==RESULT_TARGETDETAIL_DELETE){
                    int position=data.getIntExtra("position",0);
                    TargetItem targetItem=targetList.get(position);
                    targetList.remove(position);
                    adapter.notifyItemRemoved(position);
                    targetBaseManager.deleteItem(targetItem);
                    binder.synchronizeTargetItems(targetList);
                    if(targetList.isEmpty()){
                        hintText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                break;
            default:
        }
    }
}
