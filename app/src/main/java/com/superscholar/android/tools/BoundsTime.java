package com.superscholar.android.tools;

import java.util.Calendar;

/**
 * Created by zhqy on 2017/6/19.
 * 带边界的时间类
 */

public class BoundsTime extends Time {

    //上下限时间生成幅度
    private static final int TIME_RANGE=30;

    //下限时间
    private Time lowerTime;

    //上限时间
    private Time upperTime;

    public BoundsTime(){
    }

    public BoundsTime(int hour,int min){
        super(hour,min);
        generateBounds();
    }

    public BoundsTime(Calendar calendar){
        super(calendar);
        generateBounds();
    }

    public BoundsTime(Time time){
        super(time);
        generateBounds();
    }

    //生成上下限时间
    private void generateBounds(){
        lowerTime=new Time(hour,min);
        upperTime=new Time(hour,min);

        upperTime.min+=TIME_RANGE;
        if(upperTime.min>=60){
            upperTime.min-=60;
            upperTime.hour++;
        }
        if(upperTime.hour>=24){
            upperTime.hour=23;
            upperTime.min=59;
        }

        lowerTime.min-=TIME_RANGE;
        if(lowerTime.min<0){
            lowerTime.min+=60;
            lowerTime.hour--;
        }
        if(lowerTime.hour<0){
            lowerTime.hour=0;
            lowerTime.min=0;
        }
    }

    public Time getLowerTime(){
        return lowerTime;
    }

    public Time getUpperTime(){
        return upperTime;
    }

    //传入一个时间，判断是否在上下限范围内，在返回true
    public boolean isInBounds(Time time){
        int timeMin=time.hour*60+time.min;
        int upperTimeMin=upperTime.hour*60+upperTime.min;
        int lowerTimeMin=lowerTime.hour*60+lowerTime.min;
        if(timeMin<=upperTimeMin&&timeMin>=lowerTimeMin) return true;
        return false;
    }
}
