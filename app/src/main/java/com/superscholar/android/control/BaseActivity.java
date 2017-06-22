package com.superscholar.android.control;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;


import com.superscholar.android.tools.ActivityCollector;


/**
 * Created by Administrator on 2017/3/25.
 *
 * isScreenOn 屏幕亮灭                screenFlag
 * true 亮起已解锁或亮起未解锁        true 黑屏或亮起未解锁
 * false 黑屏                         false 亮起已解锁
 *
 * isScreenOn && !screenFlag   亮起已解锁  代表程序进入后台
 * isScreenOn && screenFlag    亮起未解锁  代表用户将屏幕唤醒但未解锁屏幕
 * !isScreenOn && screenFlag   黑屏        代表用户按下锁屏键
 * !isScreenOn && !screenFlag  空
 */

public class BaseActivity extends AppCompatActivity {

    //进入后台回调接口
    public interface OnEnterBackgroundListener{
        void onEnterBackground();
    }

    //进入后台监听器
    private static OnEnterBackgroundListener listener;

    //激活的活动数
    private static int activeActivityCount=0;

    //进入后台监听是否开启
    private static boolean isEnterBackgroundListening=false;

    public static void setOnEnterBackgroundListener(OnEnterBackgroundListener listener){
        if(isEnterBackgroundListening) return;
        BaseActivity.listener=listener;
    }

    //开启进入后台监听
    public static void startEnterBackgroundListening(){
        if(listener==null) return;
        isEnterBackgroundListening=true;
    }

    //关闭进入后台监听
    public static void stopEnterBackgroundListening(){
        isEnterBackgroundListening=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activeActivityCount++;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //强制竖屏
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        activeActivityCount--;
        if(!isEnterBackgroundListening) return;
        if(activeActivityCount==0){

            KeyguardManager mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
            boolean screenFlag = mKeyguardManager.inKeyguardRestrictedInputMode();

            PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();

            if(isScreenOn&&!screenFlag) {
                //程序进入后台或关闭
                listener.onEnterBackground();
                stopEnterBackgroundListening();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}
