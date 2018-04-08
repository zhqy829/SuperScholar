package com.superscholar.android.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.superscholar.android.R;
import com.superscholar.android.activity.BindPhoneActivity;
import com.superscholar.android.entity.EventItem;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.tools.CreditDir;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.EventBaseManager;
import com.superscholar.android.tools.ServerConnector;
import com.superscholar.android.tools.TargetBaseManager;
import com.superscholar.android.tools.UserLib;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by zhqy on 2017/6/14.
 */

public class AnalysisFragment extends Fragment{

    private EventBaseManager manager;

    private LineChart eventChart;
    private ScrollView formView;
    private TextView hintText;

    private Date startDate;  //周报数据起始日期
    private Date endDate;  //周报数据结尾日期
    private int []investMinData;  //各天投资时间
    private int []regularMinData;  //各天固定时间
    private int []sleepMinData;  //各天睡眠时间
    private int []wasteMinData;  //各天浪费时间
    private int []aimNum;  //目标数，完成，未完成，总数
    private String summary;  //总评文字

    private TextView []tableText;
    private TextView dateText;
    private TextView summaryText;

    //变量初始化
    private void initVariable(View view){
        manager=new EventBaseManager(getActivity().getApplicationContext());

        formView=(ScrollView)view.findViewById(R.id.analysis_scroll_form);
        hintText=(TextView)view.findViewById(R.id.analysis_text_hint);

        tableText=new TextView[6];
        tableText[0]=(TextView)view.findViewById(R.id.analysis_text_aim_complete_num);
        tableText[1]=(TextView)view.findViewById(R.id.analysis_text_aim_complete_scale);
        tableText[2]=(TextView)view.findViewById(R.id.analysis_text_aim_incomplete_num);
        tableText[3]=(TextView)view.findViewById(R.id.analysis_text_aim_incomplete_scale);
        tableText[4]=(TextView)view.findViewById(R.id.analysis_text_aim_total_num);
        tableText[5]=(TextView)view.findViewById(R.id.analysis_text_aim_total_scale);

        dateText=(TextView)view.findViewById(R.id.analysis_text_date);
        summaryText=(TextView)view.findViewById(R.id.analysis_text_summary);
    }

    //线形图配置初始化
    private void initChart(View view){
        eventChart=(LineChart)view.findViewById(R.id.analysis_chart_event);

        eventChart.setDescription("一周事件分析图");
        eventChart.setDescriptionColor(Color.BLUE);
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        eventChart.setDescriptionPosition(width-40*(width/540),20*(height/960));
        eventChart.setNoDataText("暂无周报数据");
        eventChart.setNoDataTextDescription("请先点击右下角按钮获取周报");
        eventChart.setTouchEnabled(true);
        eventChart.setDragEnabled(true);// 可拖曳
        eventChart.setScaleEnabled(true);// 可缩放
        eventChart.setDrawGridBackground(false);
        eventChart.setPinchZoom(true);
        eventChart.setBackgroundColor(Color.WHITE);// 设置图表的背景颜色

        // 图表的注解(只有当数据集存在时候才生效)
        Legend l = eventChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_RIGHT);// 可以修改图表注解部分的位置
        l.setForm(Legend.LegendForm.CIRCLE);// 线性，也可是圆
        l.setTextColor(Color.BLUE); // 颜色

        // x坐标轴
        XAxis xl = eventChart.getXAxis();
        xl.setLabelsToSkip(0);
        xl.setTextColor(Color.BLUE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);// 如果false，那么x坐标轴将不可见
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);// 将X坐标轴放置在底部，默认是在顶部。

        // 图表左边的y坐标轴线
        YAxis leftAxis = eventChart.getAxisLeft();
        leftAxis.setTextColor(0xc4c4c4);
        // 最小值
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawLabels(true);
        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.format("%.1f",value)+"h";
            }
        });

        // 不一定要从0开始
        leftAxis.setStartAtZero(false);
        leftAxis.setSpaceTop(20);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = eventChart.getAxisRight();
        rightAxis.setEnabled(false);// 不显示图表的右边y坐标轴线

    }

    //设置线状图数据
    private void setLineChartData(){
        String []weeks={"星期","日","一","二","三","四","五","六",""};
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < weeks.length; i++) {
            xVals.add(weeks[i]);
        }

        List<ILineDataSet>dataSets=new ArrayList<>();
        dataSets.add(createInvestDataSet());
        dataSets.add(createRegularDataSet());
        dataSets.add(createSleepDataSet());
        dataSets.add(createWasteDataSet());

        LineData data = new LineData(xVals,dataSets);

        eventChart.setData(data);
    }

    //设置表格视图数据
    private void setTableViewData(){
        if(aimNum[2]==0){
            tableText[1].setText("0%");
            tableText[3].setText("0%");
            tableText[5].setText("0%");
        }else{
            tableText[1].setText(String.valueOf(((aimNum[0]*100)/aimNum[2]))+"%");
            tableText[3].setText(String.valueOf(((aimNum[1]*100)/aimNum[2]))+"%");
            tableText[5].setText("100%");
        }
        tableText[0].setText(String.valueOf(aimNum[0]));
        tableText[2].setText(String.valueOf(aimNum[1]));
        tableText[4].setText(String.valueOf(aimNum[2]));

    }

    //从缓存中获取数据
    private void getDataFromCache(){
        SharedPreferences pref=getActivity().getSharedPreferences("data_report",Context.MODE_PRIVATE);
        boolean exist=pref.getBoolean("exist",false);
        if(exist){
            startDate=Date.parseDate(pref.getString("startDate",""));
            endDate=Date.parseDate(pref.getString("endDate",""));

            investMinData=new int[7];
            regularMinData=new int[7];
            sleepMinData=new int[7];
            wasteMinData=new int[7];

            //key eventXY:X-第X项 0-3 投资 固定 睡眠 浪费 Y-第Y天 Y=0星期天 Y=1星期一
            //event00：投资-星期天  event36：浪费-星期六
            for(int i=0;i<7;i++){
                String key="event0"+String.valueOf(i);
                investMinData[i]=pref.getInt(key,0);
            }
            for(int i=0;i<7;i++){
                String key="event1"+String.valueOf(i);
                regularMinData[i]=pref.getInt(key,0);
            }
            for(int i=0;i<7;i++){
                String key="event2"+String.valueOf(i);
                sleepMinData[i]=pref.getInt(key,0);
            }
            for(int i=0;i<7;i++){
                String key="event3"+String.valueOf(i);
                wasteMinData[i]=pref.getInt(key,0);
            }

            aimNum=new int[3];

            for(int i=0;i<3;i++){
                String key="aim"+String.valueOf(i);
                aimNum[i]=pref.getInt(key,0);
            }

            summary=pref.getString("summary","");

            dateText.setText("数据日期:"+
                    String.format("%04d/%02d/%02d",startDate.getYear(),startDate.getMonth(),startDate.getDay())
                    +"-"+
                    String.format("%04d/%02d/%02d",endDate.getYear(),endDate.getMonth(),endDate.getDay()));
            setLineChartData();
            setTableViewData();
            summaryText.setText(summary);

        }else{
            formView.setVisibility(View.GONE);
            hintText.setVisibility(View.VISIBLE);
        }
    }

    //从数据库生成新的数据
    private void getDataFromDB(){
        final ProgressDialog pd=new ProgressDialog(getActivity());
        pd.setTitle("生成周报");
        pd.setMessage("周报生成中，请稍候...");
        pd.setCancelable(false);
        pd.show();

        Date today=new Date(Calendar.getInstance());
        endDate=(Date) today.clone();
        endDate.daySubtractOne();
        startDate=(Date)today.clone();
        startDate.daySubtract(7);

        investMinData=new int[7];
        regularMinData=new int[7];
        sleepMinData=new int[7];
        wasteMinData=new int[7];

        manager=new EventBaseManager(getActivity());
        for(int i=7,j=0;i>0;i--,j++){
            Date date=(Date)today.clone();
            date.daySubtract(i);
            List<EventItem>eventItems=manager.getItemsByDate(date.toString());
            for(EventItem item:eventItems){
                if(item.getType().equals("投资")){
                    investMinData[j]+=item.getLastedTime();
                }else if(item.getType().equals("固定")){
                    regularMinData[j]+=item.getLastedTime();
                }else if(item.getType().equals("睡眠")){
                    sleepMinData[j]+=item.getLastedTime();
                }else if(item.getType().equals("浪费")){
                    wasteMinData[j]+=item.getLastedTime();
                }
            }
        }

        double credit = 0;
        for(int i = 0;i < 7;i++){
            credit = credit + (investMinData[i] / 60) * CreditDir.Event.INVEST_PER_HOUR;
            if(regularMinData[i] / 60 > 3){
                credit = credit + (regularMinData[i] / 60) * CreditDir.Event.REGULAR_PER_HOUR;
            }
            if(sleepMinData[i] / 60 > 12){
                credit = credit + (sleepMinData[i] / 60) * CreditDir.Event.SLEEP_PER_HOUR;
            }
            if(wasteMinData[i] / 60 > 6){
                credit = credit + (wasteMinData[i] / 60) * CreditDir.Event.REGULAR_PER_HOUR;
            }
        }

        ServerConnector.getInstance().sendGradeChange(UserLib.getInstance().getUser().getUsername(),
                credit, "事件记录结算");
        UserLib.getInstance().getUser().gradeChange(credit);

        aimNum=new int[3];

        TargetBaseManager targetBaseManager=new TargetBaseManager(getActivity().getApplicationContext());
        List<TargetItem>targetItems=targetBaseManager.getTargetItemList();
        for(TargetItem item:targetItems){
            if(item.getCurrentWeek()==1) continue;
            int aim=item.getTimesPerWeek();
            int actual=item.getWeekSignTimes()[item.getCurrentWeek()-2];
            if(actual>=aim){
                aimNum[0]++;
                aimNum[2]++;
            }else{
                aimNum[1]++;
                aimNum[2]++;
            }
        }

        ServerConnector.getInstance().sendAnalysisData(generateJsonData(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startDate=null;
                        endDate=null;
                        Toast.makeText(getActivity(),"网络异常，请检查网络设置",Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(resp);
                    String p4=jsonObject.getString("s1");
                    String p7=jsonObject.getString("s2");

                    String ptn="您本周的平均投资时间为{0}小时，" +
                            "平均固定时间为{1}小时，" +
                            "平均睡眠时间为{2}小时，" +
                            "平均浪费时间为{3}小时，" +
                            "{4}。" +
                            "本周目标数共{5}个，" +
                            "目标完成率为{6}%，" +
                            "{7}。";

                    String p0=String.format("%.1f",sumOf(investMinData)/420.0f);
                    String p1=String.format("%.1f",sumOf(regularMinData)/420.0f);
                    String p2=String.format("%.1f",sumOf(sleepMinData)/420.0f);
                    String p3=String.format("%.1f",sumOf(wasteMinData)/420.0f);
                    String p5=String.valueOf(aimNum[2]);
                    String p6=null;
                    if(aimNum[2]!=0){
                        p6=String.valueOf(aimNum[0]*100/aimNum[2]);
                    }else{
                        p6="0";
                    }

                    summary=MessageFormat.format(ptn,p0,p1,p2,p3,p4,p5,p6,p7);

                    saveDataToCache();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLineChartData();
                            setTableViewData();
                            summaryText.setText(summary);
                            formView.setVisibility(View.VISIBLE);
                            hintText.setVisibility(View.GONE);
                            pd.dismiss();
                            Toast.makeText(getActivity(),"周报生成成功",Toast.LENGTH_SHORT).show();
                            //清除之前的事件记录数据
                            manager.deleteItemsExceptDate(new Date(Calendar.getInstance()).toString());
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startDate=null;
                            endDate=null;
                            Toast.makeText(getActivity(),"发生异常",Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    });
                }
            }
        });

    }

    private int sumOf(int []array){
        int sum=0;
        for(int i:array){
            sum+=i;
        }
        return sum;
    }

    //生成报告后存储于缓存中
    private void saveDataToCache(){
        SharedPreferences pref=getActivity().getSharedPreferences("data_report",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();

        //标志
        editor.putBoolean("exist",true);

        //日期缓存
        editor.putString("startDate",startDate.toString());
        editor.putString("endDate",endDate.toString());

        //事件数据缓存
        for(int i=0;i<7;i++){
            String key="event0"+i;
            editor.putInt(key,investMinData[i]);
        }
        for(int i=0;i<7;i++){
            String key="event1"+i;
            editor.putInt(key,regularMinData[i]);
        }
        for(int i=0;i<7;i++){
            String key="event2"+i;
            editor.putInt(key,sleepMinData[i]);
        }
        for(int i=0;i<7;i++){
            String key="event3"+i;
            editor.putInt(key,wasteMinData[i]);
        }

        //目标数据缓存
        for(int i=0;i<3;i++){
            String key="aim"+String.valueOf(i);
            editor.putInt(key,aimNum[i]);
        }

        //总评数据缓存
        editor.putString("summary",summary);

        editor.apply();
    }

    //生成投资数据集
    private LineDataSet createInvestDataSet(){
        ArrayList<Entry> values = new ArrayList<>();
        for(int i=0;i<investMinData.length;i++){
            values.add(new Entry(investMinData[i]/60.0f, i+1));
        }

        LineDataSet set = new LineDataSet(values, "投资");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(Color.parseColor("#3ed494"));
        set.setCircleColor(Color.GRAY);
        set.setLineWidth(5f);
        set.setCircleSize(4f);
        set.setCircleHoleRadius(2f);
        set.setCircleColorHole(Color.WHITE);
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.parseColor("#1C86EE"));
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setValueFormatter(new MyValueFormatter(investMinData));

        return set;
    }

    //生成固定数据集
    private LineDataSet createRegularDataSet(){
        ArrayList<Entry> values = new ArrayList<>();
        for(int i=0;i<regularMinData.length;i++){
            values.add(new Entry(regularMinData[i]/60.0f, i+1));
        }

        LineDataSet set = new LineDataSet(values, "固定");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(Color.parseColor("#ff911c"));
        set.setCircleColor(Color.GRAY);
        set.setLineWidth(5f);
        set.setCircleSize(4f);
        set.setCircleHoleRadius(2f);
        set.setCircleColorHole(Color.WHITE);
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.parseColor("#1C86EE"));
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setValueFormatter(new MyValueFormatter(regularMinData));

        return set;
    }

    //生成睡眠数据集
    private LineDataSet createSleepDataSet(){
        ArrayList<Entry> values = new ArrayList<>();
        for(int i=0;i<sleepMinData.length;i++){
            values.add(new Entry(sleepMinData[i]/60.0f, i+1));
        }

        LineDataSet set = new LineDataSet(values, "睡眠");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(Color.parseColor("#081a66"));
        set.setCircleColor(Color.GRAY);
        set.setLineWidth(5f);
        set.setCircleSize(4f);
        set.setCircleHoleRadius(2f);
        set.setCircleColorHole(Color.WHITE);
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.parseColor("#1C86EE"));
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setValueFormatter(new MyValueFormatter(sleepMinData));

        return set;
    }

    //生成浪费数据集
    private LineDataSet createWasteDataSet(){
        ArrayList<Entry> values = new ArrayList<>();
        for(int i=0;i<wasteMinData.length;i++){
            values.add(new Entry(wasteMinData[i]/60.0f, i+1));
        }


        LineDataSet set = new LineDataSet(values, "浪费");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(Color.parseColor("#ec4e3b"));
        set.setCircleColor(Color.GRAY);
        set.setLineWidth(5f);
        set.setCircleSize(4f);
        set.setCircleHoleRadius(2f);
        set.setCircleColorHole(Color.WHITE);
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.parseColor("#1C86EE"));
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setValueFormatter(new MyValueFormatter(wasteMinData));

        return set;
    }

    //获取周报前检查
    private int checkBeforeGetReport(){
        Calendar calendar=Calendar.getInstance();
        if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
            //只有星期天才可以获取周报
            return 1;
        }
        if(startDate!=null&endDate!=null){
            //存在周报数据，避免重复获取
            Date today=new Date(calendar);
            Date invalidDate=(Date)endDate.clone();
            invalidDate.dayAdd(8);
            if(!today.isEquals(invalidDate)){
                return 2;
            }
        }
        return 0;
    }

    //生成传送至服务器的json数据
    private String generateJsonData(){
        StringBuilder sb=new StringBuilder();
        sb.append("{\"date\":\"");
        sb.append(new Date(Calendar.getInstance()).toString());
        sb.append("\",\"username\":\"");
        sb.append(UserLib.getInstance().getUser().getUsername());
        sb.append("\",\"use\":[");
        for(int i=0;i<investMinData.length;i++){
            sb.append("{");
            sb.append("\"invest\":");
            sb.append(""+investMinData[i]);
            sb.append(",\"fixed\":");
            sb.append(""+regularMinData[i]);
            sb.append(",\"sleep\":");
            sb.append(""+sleepMinData[i]);
            sb.append(",\"waste\":");
            sb.append(""+wasteMinData[i]);
            sb.append("}");
            if(i<investMinData.length-1){
                sb.append(",");
            }
        }
        sb.append("],\"finishNum\":");
        sb.append(aimNum[0]);
        sb.append(",\"undoneNum\":");
        sb.append(aimNum[1]);
        sb.append(",\"totalNum\":");
        sb.append(aimNum[2]);
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.page_analysis,container,false);
        initVariable(view);
        initChart(view);
        getDataFromCache();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            //fragment可见时为true
            eventChart.animateX(5000);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.analysis_fragment_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.report:
               switch(checkBeforeGetReport()){
                   case 0:
                       getDataFromDB();
                       break;
                   case 1:
                       Toast.makeText(getActivity(),"请在星期日再来获取上一周的周报哦",Toast.LENGTH_SHORT).show();
                       break;
                   case 2:
                       Toast.makeText(getActivity(),"上一周周报已经生成啦",Toast.LENGTH_SHORT).show();
                       break;
                   default:
               }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //线形图数据格式化输出显示
    public class MyValueFormatter implements ValueFormatter{

        private int []array;

        public MyValueFormatter(int []array){
            this.array=array;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            int index=entry.getXIndex()-1;
            int hour=array[index]/60;
            int min=array[index]%60;
            StringBuilder sb=new StringBuilder();
            if(hour>0){
                sb.append(String.valueOf(hour));
                sb.append("h");
                if(min!=0){
                    sb.append(String.valueOf(min));
                    sb.append("m");
                }
            }else{
                sb.append(String.valueOf(min));
                sb.append("m");
            }
            return sb.toString();
        }
    }
}

