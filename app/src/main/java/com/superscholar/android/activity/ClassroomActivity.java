package com.superscholar.android.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.adapter.ClassroomAdapter;
import com.superscholar.android.adapter.ScoreAdapter;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.Classroom;
import com.superscholar.android.entity.Score;
import com.superscholar.android.fragment.GradeRemindDialogFragment;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.UserLib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ClassroomActivity extends BaseActivity {

    private List<Classroom> classroomList;
    private RecyclerView recyclerView;
    private TextView hintText;

    private void initVariable(){
        recyclerView=(RecyclerView)findViewById(R.id.classroom_recycler);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        hintText=(TextView)findViewById(R.id.classroom_hint);
    }

    private void initData(){
        classroomList=new ArrayList<>();
        ServerConnector.getInstance().queryClassroom( new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ClassroomActivity.this,"网络异常，请检查网络设置",Toast.LENGTH_SHORT).show();
                        hintText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp=response.body().string();
                try {
                    JSONArray jsonArray=new JSONArray(resp);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("room");
                        JSONArray ridArray=jsonObject.getJSONArray("num");
                        StringBuilder sb=new StringBuilder();
                        for(int j=0;j<ridArray.length();j++){
                            sb.append(ridArray.getString(j));
                            if(j!=ridArray.length()-1){
                                sb.append(",");
                            }
                        }
                        String rid=sb.toString();
                        Classroom classroom=new Classroom(name,rid);
                        classroomList.add(classroom);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(classroomList.size()==0){
                                hintText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }else{
                                ClassroomAdapter adapter=new ClassroomAdapter(classroomList);
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ClassroomActivity.this,"发生异常",Toast.LENGTH_SHORT).show();
                            hintText.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.classroom_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        initToolbar();
        initVariable();
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
