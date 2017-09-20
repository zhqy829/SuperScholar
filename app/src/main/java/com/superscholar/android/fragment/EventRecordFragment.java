package com.superscholar.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.superscholar.android.R;
import com.superscholar.android.activity.EventChartActivity;
import com.superscholar.android.activity.EventHelpActivity;
import com.superscholar.android.activity.TimeAxisActivity;
import com.superscholar.android.entity.EventItem;
import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.Time;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by zhqy on 2017/6/14.
 */

public class EventRecordFragment extends Fragment implements View.OnClickListener{

    private ArrayList<EventItem>events=new ArrayList<>();

    private Date startDate;  //记录开始日期，为null时未开始记录
    private Time startTime;  //记录开始时间，为null时未开始记录
    private int positionTemp;  //按钮位置暂存

    private ImageView[] recordButtons;  //4个记录按钮数组
    private String[] types={"投资","固定","睡眠","浪费"};  //类型，与按钮数组对应
    private int[] normalImageResArray={
            R.drawable.event_record_invest,    //投资
            R.drawable.event_record_regular,   //固定
            R.drawable.event_record_sleep,     //睡眠
            R.drawable.event_record_waste,     //浪费
    };
    private int[] selectedImageResArray={
            R.drawable.event_record_invest_selected,    //投资
            R.drawable.event_record_regular_selected,   //固定
            R.drawable.event_record_sleep_selected,     //睡眠
            R.drawable.event_record_waste_selected,     //浪费
    };

    private int[] disableImageResArray={
            R.drawable.event_record_invest_disable,    //投资
            R.drawable.event_record_regular_disable,   //固定
            R.drawable.event_record_sleep_disable,     //睡眠
            R.drawable.event_record_waste_disable,     //浪费
    };

    //记录按钮初始化
    private void initRecordButton(View view){
        recordButtons=new ImageView[4];
        recordButtons[0]=(ImageView)view.findViewById(R.id.event_record_button_type_invest);
        recordButtons[1]=(ImageView)view.findViewById(R.id.event_record_button_type_regular);
        recordButtons[2]=(ImageView)view.findViewById(R.id.event_record_button_type_sleep);
        recordButtons[3]=(ImageView)view.findViewById(R.id.event_record_button_type_waste);

        for(ImageView button:recordButtons){
            button.setOnClickListener(this);
        }

    }

    //跳转按钮初始化
    private void initJumpButton(View view){

        ImageView help=(ImageView)view.findViewById(R.id.event_record_help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), EventHelpActivity.class);
                startActivity(intent);
            }
        });

        ImageView time=(ImageView)view.findViewById(R.id.event_record_button_timeAxis);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), TimeAxisActivity.class);
                startActivity(intent);
            }
        });

        ImageView chart=(ImageView)view.findViewById(R.id.event_record_button_pieChart);
        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), EventChartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        ImageView button=(ImageView)v;

        if(startTime==null) {
            Calendar calendar=Calendar.getInstance();
            startDate=new Date(calendar);
            startTime=new Time(calendar);
            for(int i=0;i<4;i++){
                ImageView b=recordButtons[i];
                if(button==b){
                    positionTemp=i;
                    b.setImageResource(selectedImageResArray[i]);
                }else{
                    b.setEnabled(false);
                    b.setImageResource(disableImageResArray[i]);
                }
            }
        }else{
            Calendar calendar=Calendar.getInstance();
            Date endDate=new Date(calendar);
            Time endTime=new Time(calendar);

            int time=endTime.getTimeDifference(startTime);
            if(time>0){
                Bundle args=new Bundle();
                args.putString("type",types[positionTemp]);
                args.putInt("time",time);
                args.putParcelable("startDate",startDate);
                args.putParcelable("startTime",startTime);
                args.putParcelable("endDate",endDate);
                args.putParcelable("endTime",endTime);

                FragmentManager manager=getFragmentManager();
                EventRecordDialogFragment dialog=new EventRecordDialogFragment();
                dialog.setArguments(args);
                dialog.setTargetFragment(this,0);
                dialog.show(manager,"EventRecordDialog");
            }

            for(int i=0;i<4;i++){
                ImageView b=recordButtons[i];
                if(button==b){
                    b.setImageResource(normalImageResArray[positionTemp]);
                }else{
                    b.setEnabled(true);
                    b.setImageResource(normalImageResArray[i]);
                }
            }
            startDate=null;
            startTime=null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.event_record,container,false);
        initJumpButton(view);
        initRecordButton(view);

        return view;
    }
}
