package com.superscholar.android.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

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
        Calendar calendar=Calendar.getInstance();
        int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);
        int sec=calendar.get(Calendar.SECOND);
        long currentTimeStamp=sec*1000+min*60*1000+hour*60*60*1000;
        return currentTimeStamp;
    }

    @Override
    public int compareTo(DelayTask delayTask) {
        if(delayTime>delayTask.delayTime) return 1;
        else if(delayTime<delayTask.delayTime) return -1;
        else return 0;
    }
}
