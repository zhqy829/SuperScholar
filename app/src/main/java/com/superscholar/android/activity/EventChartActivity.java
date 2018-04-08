package com.superscholar.android.activity;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.entity.EventItem;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.EventBaseManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventChartActivity extends BaseActivity implements View.OnClickListener{

    private PieChart pieChart;
    private Button[]buttons; //星期选择按钮
    private Date[] week;  //当前周的各天的日期
    private int pos;  //当前显示数据在week中的位置
    private EventBaseManager manager;

    //饼状图初始化
    private void initPieChart(){
        manager=new EventBaseManager(getApplicationContext());

        pieChart=(PieChart)findViewById(R.id.event_chart_pie);
        pieChart.setUsePercentValues(true);  //显示成百分比


        pieChart.setDescription("");

        pieChart.setExtraOffsets(0, 0, 0, 50);//设置饼状图偏移 右 下 左 上

        pieChart.setDragDecelerationFrictionCoef(0.95f);//设置阻尼系数,范围在[0,1]之间,越小饼状图转动越困难

        pieChart.setDrawHoleEnabled(true);//是否绘制饼状图中间的圆
        pieChart.setHoleColor(Color.WHITE);//饼状图中间的圆的绘制颜色
        pieChart.setHoleRadius(58f);//饼状图中间的圆的半径大小

        pieChart.setDrawCenterText(true);//是否绘制中间的文字
        pieChart.setCenterTextColor(Color.parseColor("#1C86EE"));//中间的文字颜色
        pieChart.setCenterTextSize(24);//中间的文字字体大小
        pieChart.setCenterText("时间使用比");

        pieChart.setTransparentCircleColor(Color.WHITE);//设置圆环的颜色
        pieChart.setTransparentCircleAlpha(110);//设置圆环的透明度[0,255]
        pieChart.setTransparentCircleRadius(60f);//设置圆环的半径值

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);//设置饼状图是否可以旋转，默认为true
        pieChart.setRotationAngle(5);//设置松手后旋转的度数

        pieChart.setHighlightPerTapEnabled(true);//设置旋转的时候点中的tab是否高亮，默认为true

        Legend l = pieChart.getLegend();
        l.setTextSize(14);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setXEntrySpace(0f);
        l.setYEntrySpace(0f);//设置tab之间Y轴方向上的空白间距值
        l.setYOffset(0f);

        pieChart.animateY(2000, Easing.EasingOption.EaseInQuad);//设置Y轴上的绘制动画

        pieChart.highlightValues(null);
        pieChart.invalidate();

        getPieData();
    }

    //获取数据
    private void getPieData(){

        int []mins={0,0,0,0,0};
        List<EventItem>items=manager.getItemsByDate(week[pos].toString());
        for(EventItem item:items){
            if(item.getType().equals("投资")){
                mins[0]+=item.getLastedTime();
            }else if(item.getType().equals("固定")){
                mins[1]+=item.getLastedTime();
            }else if(item.getType().equals("睡眠")){
                mins[2]+=item.getLastedTime();
            }else if(item.getType().equals("浪费")){
                mins[3]+=item.getLastedTime();
            }
        }
        mins[4]=1440-mins[0]-mins[1]-mins[2]-mins[3];

        String []names={"投资","固定","睡眠","浪费","未知"};
        ArrayList<String> xValues = new ArrayList<>();  //xVals用来表示每个饼块上的内容
        ArrayList<Entry> yValues = new ArrayList<>();  //yVals用来表示封装每个饼块的实际数据
        for (int i = 0; i <5; i++) {
            xValues.add(names[i]);  //饼块上显示
            yValues.add(new Entry(mins[i]/1440.0f,i));
        }

        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "");//显示在比例图上
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

        //格式化数据显示
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format("%.2f",value)+"%";
            }
        });

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(0x3e, 0xd4, 0x94));  //绿色 投资
        colors.add(Color.rgb(0xff, 0x91, 0x1c));  //橙色 固定
        colors.add(Color.rgb(0x08, 0x1a, 0x66));  //紫色 睡眠
        colors.add(Color.rgb(0xec, 0x4e, 0x3b));  //红色 浪费
        colors.add(Color.rgb(0xcf, 0xcf, 0xcf));  //灰色 未知

        pieDataSet.setColors(colors);

        pieDataSet.setSelectionShift(5f); // 选中态多出的长度

        PieData pieData = new PieData(xValues, pieDataSet);

        //设置每项文字颜色
        pieData.setValueTextColor(Color.WHITE);
        pieData.setValueTextSize(10f);

        pieChart.setData(pieData);

        pieChart.animateY(2000, Easing.EasingOption.EaseInQuad);//设置Y轴上的绘制动画

        pieChart.invalidate();
    }

    //菜单栏初始化
    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.event_chart_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initButton(){
        buttons=new Button[7];
        buttons[0]=(Button)findViewById(R.id.event_chart_button_0);
        buttons[1]=(Button)findViewById(R.id.event_chart_button_1);
        buttons[2]=(Button)findViewById(R.id.event_chart_button_2);
        buttons[3]=(Button)findViewById(R.id.event_chart_button_3);
        buttons[4]=(Button)findViewById(R.id.event_chart_button_4);
        buttons[5]=(Button)findViewById(R.id.event_chart_button_5);
        buttons[6]=(Button)findViewById(R.id.event_chart_button_6);

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

    public void onClick(View v) {
        Button button=(Button)v;
        int num=(int)button.getTag();
        if(pos!=num){
            buttons[pos].setTextColor(Color.parseColor("#4F4F4F"));
            buttons[pos].setBackgroundColor(Color.parseColor("#FFFFFF"));
            pos=num;
            getPieData();
            buttons[pos].setTextColor(Color.parseColor("#FFFFFF"));
            buttons[pos].setBackgroundColor(Color.parseColor("#1C86EE"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_chart);

        initToolbar();
        initWeekDate();
        initButton();
        initPieChart();
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
}
