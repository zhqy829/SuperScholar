package com.superscholar.android.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsw.calendar.entity.CalendarInfo;
import com.dsw.calendar.views.CircleCalendarView;
import com.superscholar.android.R;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.tools.BoundsTime;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.Time;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TargetDetailActivity extends BaseActivity {

    private final int RESULT_TARGETDETAIL_ALTER=2;
    private final int RESULT_TARGETDETAIL_DELETE=3;

    private CircleCalendarView circleCalendarView;
    private TargetItem targetItem;
    private ListView listView;  //显示信息的列表
    private TextView todayStatus;  //日历下边显示今天打卡状态
    private String []listViewData=new String[8];
    private ArrayAdapter<String>adapter;
    private int position;
    private Date endDate;

    //变量初始化
    private void initVariable(){
        circleCalendarView=(CircleCalendarView)findViewById(R.id.target_detail_calenderView);

        targetItem=getIntent().getParcelableExtra("targetItem");

        listView=(ListView)findViewById(R.id.target_detail_listView);

        todayStatus=(TextView)findViewById(R.id.target_detail_todayStatusText);

        position=getIntent().getIntExtra("position",0);

        endDate=Date.parseDate(targetItem.getStartDate().toString());
        endDate.dayAdd(targetItem.getLastedWeek()*7);
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.target_detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        TextView targetNameText=(TextView)findViewById(R.id.target_detail_targetName);
        targetNameText.setText(targetItem.getName()); //菜单栏标题设置为目标名
    }

    //日历信息初始化
    private void initCalendar(){
        List<Date>signDates=targetItem.getSignDates();
        List<CalendarInfo> infoList = new ArrayList<>();
        Iterator<Date>iterator=signDates.iterator();
        while(iterator.hasNext()){
            Date date=iterator.next();
            infoList.add(new CalendarInfo(date.getYear(),date.getMonth(),date.getDay(),"已打卡"));
        }
        circleCalendarView.setCalendarInfos(infoList);
    }

    //显示今日打卡状态
    private void initTodayStatusShow(){
        if(targetItem.isTodaySign()){
            todayStatus.setText("今日已打卡");
        }else{
            todayStatus.setText("今日未打卡");
        }
    }

    //详细信息数据初始化
    private void initListView(){

        listView.setFocusable(false);

        String endDateString=endDate.toString();

        String remindTimeStr;
        if(!targetItem.isNeedRemind()){
            remindTimeStr="提醒时间:"+"不需要提醒";
        }else if(!targetItem.isCheck()){
            String hour,min;
            BoundsTime remindTime=targetItem.getRemindTime();
            if(remindTime.getHour()<10) hour="0"+String.valueOf(remindTime.getHour());
            else hour=String.valueOf(remindTime.getHour());
            if(remindTime.getMin()<10) min="0"+String.valueOf(remindTime.getMin());
            else min=String.valueOf(remindTime.getMin());
            remindTimeStr="提醒时间:"+hour+":"+min;
        }else{
            String lowerHour,lowerMin,upperHour,upperMin;
            BoundsTime remindTime=targetItem.getRemindTime();
            Time lowerTime=remindTime.getLowerTime();
            Time upperTime=remindTime.getUpperTime();

            if(lowerTime.getHour()<10) lowerHour="0"+String.valueOf(lowerTime.getHour());
            else lowerHour=String.valueOf(lowerTime.getHour());
            if(lowerTime.getMin()<10) lowerMin="0"+String.valueOf(lowerTime.getMin());
            else lowerMin=String.valueOf(lowerTime.getMin());

            if(upperTime.getHour()<10) upperHour="0"+String.valueOf(upperTime.getHour());
            else upperHour=String.valueOf(upperTime.getHour());
            if(upperTime.getMin()<10) upperMin="0"+String.valueOf(upperTime.getMin());
            else upperMin=String.valueOf(upperTime.getMin());
            remindTimeStr="打卡限制时间:"+lowerHour+":"+lowerMin+"--"+upperHour+":"+upperMin;
        }

        String currentWeekStatusText;
        if(!targetItem.isValid()){
            currentWeekStatusText="本周情况：目标已结束";
        } else{
            currentWeekStatusText="本周情况：本周打卡"+targetItem.getWeekSignTimes()[targetItem.getCurrentWeek()-1]+"次，" +
                    "目标"+targetItem.getTimesPerWeek()+"次";
        }

        String lastedWeekStatusText;
        if(!targetItem.isValid()){
            lastedWeekStatusText="持续情况：目标已结束";
        } else{
            lastedWeekStatusText="持续情况：当前第"+targetItem.getCurrentWeek()+"周，" +
                    "共"+targetItem.getLastedWeek()+"周";
        }

        listViewData[0]="开始日期:"+targetItem.getStartDate().toString();
        listViewData[1]="失效日期:"+endDateString;
        listViewData[2]="总打卡天数:"+String.valueOf(targetItem.getSignDays())+"天";
        listViewData[3]="连续打卡天数:"+String.valueOf(targetItem.getConsecutiveSignDays())+"天";
        listViewData[4]=remindTimeStr;
        listViewData[5]=currentWeekStatusText;
        listViewData[6]=lastedWeekStatusText;
        listViewData[7]="每次打卡您将获得"+targetItem.getCurrencyReward()+"奖励币";

        adapter=new ArrayAdapter<>(TargetDetailActivity.this,android.R.layout.simple_list_item_1,listViewData);
        listView.setAdapter(adapter);
    }

    //menu修改目标项被点击事件
    private void onAlterRemindTimeClicked(){
        Intent intent=new Intent(this,TargetUpdateActivity.class);
        intent.putExtra("targetItem",targetItem);
        startActivityForResult(intent,0);
    }

    //menu放弃目标项被点击事件
    private void onDeleteTargetClicked(){
        Snackbar.make(circleCalendarView,
                "放弃目标您将不会得到任何学分绩，同时会扣除一定学分绩作为惩罚，确定放弃？",
                Snackbar.LENGTH_SHORT).setAction("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //服务器通信语句
                // TODO: 2017/6/23
                Intent intent=new Intent();
                //intent传递学分绩变化
                // TODO: 2017/6/23
                intent.putExtra("position",position);
                setResult(RESULT_TARGETDETAIL_DELETE,intent);
                finish();
            }
        }).show();
    }

    //领取学分绩按钮初始化
    private void initButton(){
        Button getGradeButton=(Button)findViewById(R.id.target_detail_getGrade);
        getGradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(targetItem.isValid()){
                    Toast.makeText(TargetDetailActivity.this,"请等到截止日期后再来领取学分绩哦",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(TargetDetailActivity.this,"恭喜你达成目标，学分绩领取成功",Toast.LENGTH_SHORT).show();
                    //服务器通信语句
                    // TODO: 2017/6/23
                    Intent intent=new Intent();
                    //intent传递学分绩变化
                    // TODO: 2017/6/23
                    intent.putExtra("position",position);
                    setResult(RESULT_TARGETDETAIL_DELETE,intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_detail);

        initVariable();
        initToolbar();
        initCalendar();
        initTodayStatusShow();
        initListView();
        initButton();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.target_detail_menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 0:
                if(resultCode==RESULT_OK){
                    if(targetItem.isCheck()){
                        targetItem=data.getParcelableExtra("targetUpdate");
                        listViewData[7]="每次打卡您将获得"+targetItem.getCurrencyReward()+"奖励币";
                        adapter.notifyDataSetChanged();
                        Toast.makeText(TargetDetailActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        intent.putExtra("position",position);
                        intent.putExtra("targetItem",targetItem);
                        setResult(RESULT_TARGETDETAIL_ALTER,intent);
                    }else{
                        targetItem=data.getParcelableExtra("targetUpdate");
                        listViewData[7]="每次打卡您将获得"+targetItem.getCurrencyReward()+"奖励币";
                        if(targetItem.isNeedRemind()){
                            String hour,min;
                            BoundsTime remindTime=targetItem.getRemindTime();
                            if(remindTime.getHour()<10) hour="0"+String.valueOf(remindTime.getHour());
                            else hour=String.valueOf(remindTime.getHour());
                            if(remindTime.getMin()<10) min="0"+String.valueOf(remindTime.getMin());
                            else min=String.valueOf(remindTime.getMin());
                            listViewData[4]="提醒时间:"+hour+":"+min;
                        }else{
                            listViewData[4]="提醒时间:"+"不需要提醒";
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(TargetDetailActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent();
                        intent.putExtra("position",position);
                        intent.putExtra("targetItem",targetItem);
                        setResult(RESULT_TARGETDETAIL_ALTER,intent);
                    }
                }
                break;
            default:
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.deleteTarget:
                onDeleteTargetClicked();
                break;
            case R.id.alterRemindTime:
                onAlterRemindTimeClicked();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
