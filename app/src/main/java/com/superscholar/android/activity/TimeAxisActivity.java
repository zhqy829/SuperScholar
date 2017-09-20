package com.superscholar.android.activity;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.adapter.EventItemAdapter;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.EventItem;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.EventBaseManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeAxisActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private Button []buttons; //星期选择按钮
    private TextView hintText;
    private Date[] week;  //当前周的各天的日期
    private int pos;  //当前显示数据在week中的位置
    private EventBaseManager manager;

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.time_axis_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRecyclerView(){
        recyclerView=(RecyclerView)findViewById(R.id.time_axis_recycler_view);
        hintText=(TextView)findViewById(R.id.time_axis_hint);
        manager=new EventBaseManager(this);

        List<EventItem> events=manager.getItemsByDate(week[pos].toString());
        if(events.size()==0){
            recyclerView.setVisibility(View.GONE);
            hintText.setVisibility(View.VISIBLE);
        }

        EventItemAdapter adapter=new EventItemAdapter(events);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(TimeAxisActivity.this));
    }

    private void initButton(){
        buttons=new Button[7];
        buttons[0]=(Button)findViewById(R.id.time_axis_button_0);
        buttons[1]=(Button)findViewById(R.id.time_axis_button_1);
        buttons[2]=(Button)findViewById(R.id.time_axis_button_2);
        buttons[3]=(Button)findViewById(R.id.time_axis_button_3);
        buttons[4]=(Button)findViewById(R.id.time_axis_button_4);
        buttons[5]=(Button)findViewById(R.id.time_axis_button_5);
        buttons[6]=(Button)findViewById(R.id.time_axis_button_6);

        for(int i=0;i<7;i++){
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(this);
        }

        for(int i=pos+1;i<7;i++){
            buttons[i].setBackgroundColor(Color.parseColor("#C4C4C4"));
            buttons[i].setTextColor(Color.parseColor("#FFFFFF"));
            buttons[i].setEnabled(false);
        }

        buttons[pos].setTextColor(Color.parseColor("#FFFFFF"));
        buttons[pos].setBackgroundColor(Color.parseColor("#1C86EE"));
    }

    private void initWeekDate(){
        Calendar calendar=Calendar.getInstance();
        week=new Date[7];
        pos=calendar.get(Calendar.DAY_OF_WEEK)-1;
        week[pos]=new Date(calendar);
        for(int i=pos+1;i<7;i++){
            Date date=new Date(calendar);
            date.dayAdd(i-pos);
            week[i]=date;
        }
        for(int i=0;i<pos;i++){
            Date date=new Date(calendar);
            date.daySubtract(pos-i);
            week[i]=date;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_axis);

        initToolbar();
        initWeekDate();
        initButton();
        initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Button button=(Button)v;
        int num=(int)button.getTag();
        if(pos!=num){
            buttons[pos].setTextColor(Color.parseColor("#4F4F4F"));
            buttons[pos].setBackgroundColor(Color.parseColor("#FFFFFF"));
            pos=num;
            List<EventItem> events=manager.getItemsByDate(week[pos].toString());
            if(events.size()==0){
                recyclerView.setVisibility(View.GONE);
                hintText.setVisibility(View.VISIBLE);
            }else{
                recyclerView.setVisibility(View.VISIBLE);
                hintText.setVisibility(View.GONE);
                EventItemAdapter adapter=new EventItemAdapter(events);
                recyclerView.setAdapter(adapter);
            }
            buttons[pos].setTextColor(Color.parseColor("#FFFFFF"));
            buttons[pos].setBackgroundColor(Color.parseColor("#1C86EE"));
        }
    }
}
