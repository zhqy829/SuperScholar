package com.superscholar.android.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shinelw.library.ColorArcProgressBar;
import com.superscholar.android.R;
import com.superscholar.android.control.BaseActivity;
import com.superscholar.android.service.ConcentrationService;
import com.superscholar.android.activity.ConcentrationRuleActivity;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by zhqy on 2017/6/14.
 */

public class ConcentrationTimeFragment extends Fragment{
    private boolean isTimeStart=false;  //专心时间计时开始
    private TextView errorText;  //提示文字 进入后台或满120分钟时显示
    private int concentrationMin=0;  //专心分钟
    private ColorArcProgressBar progressBar;  //专心时间进度条
    private ConcentrationService.ConcentrationBinder binder;   //活动和刷新专心时间服务通信器
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder=(ConcentrationService.ConcentrationBinder)iBinder;
            binder.swtOnMinutePassedListener(new ConcentrationService.OnMinutePassedListener() {
                @Override
                public void onMinutePassed() {
                    concentrationMin++;
                    progressBar.setCurrentValues(concentrationMin);
                    if(concentrationMin>=120){
                        binder.stopConcentrationService();
                        isTimeStart=false;
                        BaseActivity.stopEnterBackgroundListening();
                        errorText.setTextColor(Color.parseColor("#1C86EE"));
                        errorText.setText("本次专心时间已满120分钟，计时结束，获得"+String.valueOf(70)+"学分绩");
                        errorText.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    
    private void initControl(View view){
        errorText=(TextView)view.findViewById(R.id.event_concentration_errorText);
        progressBar=(ColorArcProgressBar)view.findViewById(R.id.event_concentration_progressBar);
    }
    
    private void initButton(View view){
        ImageButton startButton=(ImageButton)view.findViewById(R.id.event_concentration_startButton);
        ImageButton stopButton=(ImageButton)view.findViewById(R.id.event_concentration_stopButton);
        TextView ruleDetailText=(TextView)view.findViewById(R.id.event_concentration_ruleText);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTimeStart){
                    Toast.makeText(getActivity(),"计时已经开始啦",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if(errorText.getVisibility()==View.VISIBLE) errorText.setVisibility(View.INVISIBLE);
                    BaseActivity.startEnterBackgroundListening();
                    isTimeStart=true;
                    concentrationMin=0;
                    progressBar.setCurrentValues(0);
                    Intent intent=new Intent(getActivity(),ConcentrationService.class);
                    getActivity().startService(intent);
                    Toast.makeText(getActivity(),"计时开始",Toast.LENGTH_SHORT).show();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isTimeStart){
                    Toast.makeText(getActivity(),"请先开始后再停止哦",Toast.LENGTH_SHORT).show();
                    return;
                }else if(concentrationMin<30){
                    Snackbar.make(view,"专心时间还不满30分钟哦，此时停止将不返还学分绩，确认停止吗？",Snackbar.LENGTH_SHORT)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    isTimeStart=false;
                                    binder.stopConcentrationService();
                                    BaseActivity.stopEnterBackgroundListening();

                                    Toast.makeText(getActivity(),"计时停止，本次专心时间"+String.valueOf(concentrationMin)+"分钟，获得"+String.valueOf(0)+"学分绩",Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                } else {
                    int obtainGrade=55;
                    if(concentrationMin>=90) obtainGrade=65;
                    else if(concentrationMin>=60) obtainGrade=60;
                    isTimeStart=false;
                    binder.stopConcentrationService();
                    BaseActivity.stopEnterBackgroundListening();
                    Toast.makeText(getActivity(),"计时停止，本次专心时间"+String.valueOf(concentrationMin)+"分钟，获得"+String.valueOf(obtainGrade)+"学分绩",Toast.LENGTH_SHORT).show();
                }
            }
        });

        ruleDetailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ConcentrationRuleActivity.class);
                startActivity(intent);
            }
        });
    }

    //初始化专心时间
    private void initConcentrationTime(){
        BaseActivity.setOnEnterBackgroundListener(new BaseActivity.OnEnterBackgroundListener() {
            @Override
            public void onEnterBackground() {
                binder.stopConcentrationService();
                isTimeStart=false;
                errorText.setTextColor(Color.parseColor("#FF0000"));
                int obtainGrade=0;
                if(concentrationMin>=90) obtainGrade=65;
                else if(concentrationMin>=60) obtainGrade=60;
                else if(concentrationMin>=30) obtainGrade=55;
                errorText.setText("软件进入后台，计时停止，专心时间"+String.valueOf(concentrationMin)+
                        "分钟，获得"+String.valueOf(obtainGrade)+"学分绩");
                errorText.setVisibility(View.VISIBLE);
            }
        });
        
        Intent bindIntent=new Intent(getActivity(),ConcentrationService.class);
        getActivity().bindService(bindIntent,serviceConnection,BIND_AUTO_CREATE);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.event_concentration,container,false);
        initControl(view);
        initButton(view);
        initConcentrationTime();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(binder.isTimingRefreshStart()) binder.stopConcentrationService();
        getActivity().unbindService(serviceConnection);
    }
}
