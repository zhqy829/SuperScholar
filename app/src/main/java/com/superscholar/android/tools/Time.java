package com.superscholar.android.tools;

import java.util.Calendar;

/**
 * Created by zhqy on 2017/6/18.
 * 封装的时间类
 * 与BoundsTime结合使用
 */

public class Time {

    protected int hour;  //00-23
    protected int min;   //00-59

    public Time(){
    }

    public Time(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public Time(Calendar calendar){
        hour=calendar.get(Calendar.HOUR);
        min=calendar.get(Calendar.MINUTE);
    }

    public Time(Time time){
        hour=time.getHour();
        min=time.getMin();
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
