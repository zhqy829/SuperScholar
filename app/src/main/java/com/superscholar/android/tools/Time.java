package com.superscholar.android.tools;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by zhqy on 2017/6/18.
 * 封装的时间类
 * 与BoundsTime结合使用
 */

public class Time implements Parcelable {

    protected int hour;  //00-23
    protected int min;   //00-59

    public Time(){
    }

    public Time(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public Time(Calendar calendar){
        hour=calendar.get(Calendar.HOUR_OF_DAY);
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

    public int getTimeDifference(Time time){
        int a=hour*60+min;
        int b=time.hour*60+time.min;
        return a>b?a-b:b-a;
    }

    public long getTimestamp(){
       return hour*60*60*1000+min*60*1000;
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d",hour,min);
    }

    public static Time parseTime(String time){
        Time t=new Time();
        String []array=time.split(":");
        t.hour=Integer.parseInt(array[0]);
        t.min=Integer.parseInt(array[1]);
        return t;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hour);
        dest.writeInt(min);
    }

    public static final Parcelable.Creator<Time> CREATOR =new Parcelable.Creator<Time>(){
        @Override
        public Time createFromParcel(Parcel source) {
            Time time=new Time();
            time.hour=source.readInt();
            time.min=source.readInt();
            return time;
        }

        @Override
        public Time[] newArray(int size) {
            return new Time[size];
        }
    };

}
