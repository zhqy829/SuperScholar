/*
* 延时任务列表自带任务 #REFRESH 24:00
* 用于到0点时重置延时时间列表
* */

package com.superscholar.android.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.superscholar.android.R;
import com.superscholar.android.entity.TargetItem;
import com.superscholar.android.tools.DelayTask;
import com.superscholar.android.activity.LoginActivity;
import com.superscholar.android.tools.TargetBaseManager;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TargetRemindService extends Service {

    public interface OnRefreshListener{
        void onRefresh();
    }

    private OnRefreshListener listener;
    private List<DelayTask>delayTasks;
    private RemindBinder mBinder;
    private List<TargetItem>targetItems;

    private int delayTasksPointer;  //指向延时任务待执行的那一项

    public TargetRemindService() {
        delayTasks=new ArrayList<>();
        mBinder=new RemindBinder();
        delayTasksPointer=-1;
    }

    //清空延时任务列表
    private void clearDelayTasks(){
        delayTasksPointer=-1;
        for(int i=delayTasks.size()-1;i>=0;i--){
            delayTasks.remove(i);
        }
    }

    //生成延时任务列表
    private void generateDelayTasks() {
        long currentTimeStamp=DelayTask.getCurrentTimestamp();
        for(TargetItem targetItem:targetItems){
            if(targetItem.isValid()&&targetItem.isNeedRemind()){
                long remindTimeStamp=targetItem.getRemindTime().getMin()*60*1000+targetItem.getRemindTime().getHour()*60*60*1000;
                if(remindTimeStamp-currentTimeStamp>0){
                    delayTasks.add(new DelayTask(currentTimeStamp,remindTimeStamp,targetItem.getName()));
                }
            }
        }
        delayTasks.add(new DelayTask(currentTimeStamp,24,0,"#REFRESH"));  //刷新列表任务
        Collections.sort(delayTasks);
    }

    //服务重启后目标数据初始化
    private void initDataAfterServiceRestart(){
        targetItems=new ArrayList<>();
        TargetBaseManager manager=new TargetBaseManager(this);
        targetItems=manager.getTargetItemList();
        generateDelayTasks();
    }

    //处理延时任务需执行的操作
    private void handleDelayTask(){
        if(delayTasksPointer==-1) return; //第一次执行跳过
        DelayTask delayTask=delayTasks.get(delayTasksPointer);
        if(delayTask.getTaskName().equals("#REFRESH")){
            //24:00刷新延时任务列表
            clearDelayTasks();
            generateDelayTasks();
            //24:00刷新活动目标的数据
            listener.onRefresh();
        }else{
            //创建通知
            Intent intent=new Intent(this, LoginActivity.class);
            PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
            NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Notification notification =new NotificationCompat.Builder(this)
                    .setContentTitle("学霸修炼之路")
                    .setContentText("亲(＾▽＾) 您到时间 【"+delayTask.getTaskName()+"】了哦")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.icon)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0,1000,1000,1000})
                    .setContentIntent(pi)
                    .build();
            manager.notify(delayTasksPointer,notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleDelayTask();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        delayTasksPointer++;
        long triggerAtTime;
        if(delayTasks.size()==0){
            //服务重启时delayTasks为空，读取数据库生成delayTasks
            initDataAfterServiceRestart();
        }
        if(delayTasksPointer==0){
            triggerAtTime= SystemClock.elapsedRealtime()+delayTasks.get(delayTasksPointer).getDelayTime();
        }else{
            //第一项之后的每一项的延时时间都包括了前一项的延时
            triggerAtTime= SystemClock.elapsedRealtime()+
                    (delayTasks.get(delayTasksPointer).getDelayTime()-delayTasks.get(delayTasksPointer-1).getDelayTime());
        }
        Intent i=new Intent(this,TargetRemindService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        if(Build.VERSION.SDK_INT>=19) //Android 4.4
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        else
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class RemindBinder extends Binder{

        /*
        * 服务与活动的通信器
        * 方法在活动中调用
        * */

        public void setOnRefreshListener(OnRefreshListener listener){
            TargetRemindService.this.listener=listener;
        }

        //同步目标列表
        public void synchronizeTargetItems(List<TargetItem>targetItems){
            TargetRemindService.this.targetItems=targetItems;
            stopSelf();
            clearDelayTasks();
            generateDelayTasks();
            Intent intent=new Intent(TargetRemindService.this,TargetRemindService.class);
            startService(intent);
        }

        //停止提醒服务
        public void stopService(){
            stopSelf();
        }
    }
}
