package com.superscholar.android.tools;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhqy on 2017/6/17.
 */

public class Date implements Parcelable,Cloneable,Serializable{

    private int year;
    private int month;
    private int day;

    public Date(){
        Calendar calendar=Calendar.getInstance();
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH)+1;
        day=calendar.get(Calendar.DAY_OF_MONTH);
    }

    public Date(Calendar calendar){
        year=calendar.get(Calendar.YEAR);
        month=calendar.get(Calendar.MONTH)+1;
        day=calendar.get(Calendar.DAY_OF_MONTH);
    }

    public Date(int year,int month,int day){
        this.year=year;
        this.month=month;
        this.day=day;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    //日期时间字符串转DateTime
    public static Date parseDate(String dateString){
        String regex="\\d{1,}";
        int []date=new int[3];
        Pattern p=Pattern.compile(regex);
        Matcher m=p.matcher(dateString);
        int i=0;
        while(m.find()){
            date[i]=Integer.valueOf(m.group());
            i++;
        }
        return new Date(date[0],date[1],date[2]);
    }

    //获取月份的最大天数
    private int getMaxDays(){
        if(month>12||month<1) return -1;
        if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
            return 31;
        } else if(month==4||month==6||month==9||month==11) {
            return 30;
        } else{
            if(((year%4==0)&&year%100!=0)||(year%400==0)){
                return 29;
            } else{
                return 28;
            }
        }
    }

    //天数加后日期调整
    private void adjustDateAfterDayAdd(){
        int maxDays=getMaxDays();
        while(day>maxDays){
            day-=maxDays;
            month++;
            if(month>12){
                month-=12;
                year++;
            }
            maxDays=getMaxDays();
        }
    }

    //天数加一
    public void dayAddOne(){
        day++;
        adjustDateAfterDayAdd();
    }

    //天数加
    public void dayAdd(int days){
        day+=days;
        adjustDateAfterDayAdd();
    }

    //天数减后日期调整
    private void adjustDateAfterDaySubtract(){
        int maxDays;
        while(day<=0){
            month--;
            if(month==0){
                year--;
                month=12;
            }
            maxDays=getMaxDays();
            day+=maxDays;
        }
    }

    //天数减一
    public void daySubtractOne(){
        day--;
        adjustDateAfterDaySubtract();
    }

    //天数减
    public void daySubtract(int days){
        day-=days;
        adjustDateAfterDaySubtract();
    }

    //获取当前日期对象的时间戳
    public long getTimestamp(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(year,month-1,day,0,0,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();
    }

    //获取当前日期对象与参数日期对象的天数差，return this-aimDate
    public int getDateDifference(Date aimDate){
        long thisTimestamp=getTimestamp();
        long aimTimestamp=aimDate.getTimestamp();
        return (int)((thisTimestamp-aimTimestamp)/(24*60*60*1000));
    }

    //比较当前日期对象和参数日期对象，当前日期对象早于参数日期对象或同一天返回false,晚于返回true
    public boolean isLate(Date aimDate){
        long thisTimestamp=getTimestamp();
        long aimTimestamp=aimDate.getTimestamp();
        if(thisTimestamp<=aimTimestamp)return false;
        return true;
    }

    //比较当前日期对象和参数日期对象，当前日期对象晚于参数日期对象或同一天返回false,早于返回true
    public boolean isEarly(Date aimDate){
         long thisTimestamp=getTimestamp();
         long aimTimestamp=aimDate.getTimestamp();
         if(thisTimestamp>=aimTimestamp)return false;
         return true;
     }

    //比较当前日期对象和参数日期对象，同一天返回true，否则返回false
    public boolean isEquals(Date aimDate){
         if(year==aimDate.year&&month==aimDate.month&&day==aimDate.day)
             return true;
         return false;
     }

   //获得一个同日期Date对象
    @Override
    public Object clone(){
        Date date=null;
        try {
            date = (Date) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return date;
    }

    //Date转字符串日期
    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d",year,month,day);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
    }

    public static final Parcelable.Creator<Date> CREATOR =new Parcelable.Creator<Date>(){
        @Override
        public Date createFromParcel(Parcel source) {
            Date date =new Date();
            date.year=source.readInt();
            date.month=source.readInt();
            date.day=source.readInt();
            return date;

        }

        @Override
        public Date[] newArray(int size) {
            return new Date[size];
        }
    };
}
