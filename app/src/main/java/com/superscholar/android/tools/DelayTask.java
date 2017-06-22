package com.superscholar.android.tools;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/18.
 */

public class DelayTask implements Comparable<DelayTask>{
    private long delayTime;
    private String taskName;

    public DelayTask(int remindHour,int remindMin,String taskName){
        long currentTimeStamp=getCurrentTimestamp();
        long remindTimeStamp=remindMin*60*1000+remindHour*60*60*1000;
        delayTime=remindTimeStamp-currentTimeStamp;
        this.taskName=taskName;
    }

    public DelayTask(long currentTimeStamp,int remindHour,int remindMin,String taskName){
        long remindTimeStamp=remindMin*60*1000+remindHour*60*60*1000;
        delayTime=remindTimeStamp-currentTimeStamp;
        this.taskName=taskName;
    }

    public DelayTask (long currentTimeStamp,long remindTimeStamp,String taskName){
        delayTime=remindTimeStamp-currentTimeStamp;
        this.taskName=taskName;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public static long getCurrentTimestamp(){
        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        int currentHour=Integer.parseInt(df.format(date));
        df = new SimpleDateFormat("mm");
        int currentMin=Integer.parseInt(df.format(date));
        df = new SimpleDateFormat("ss");
        int currentSec=Integer.parseInt(df.format(date));
        long currentTimeStamp=currentSec*1000+currentMin*60*1000+currentHour*60*60*1000;
        return currentTimeStamp;
    }

    @Override
    public int compareTo(DelayTask delayTask) {
        if(delayTime>delayTask.delayTime) return 1;
        else if(delayTime<delayTask.delayTime) return -1;
        else return 0;
    }
}
