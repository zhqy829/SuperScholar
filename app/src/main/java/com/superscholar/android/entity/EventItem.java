package com.superscholar.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.superscholar.android.tools.Date;
import com.superscholar.android.tools.Time;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by zhqy on 2017/9/4.
 */

public class EventItem implements Parcelable{

    private UUID uuid;
    private Date date;  //事件所属的日期
    private Time startTime;  //事件开始时间
    private Time endTime;  //事件结束时间
    private int lastedTime;  //事件持续时间，单位:分钟
    private String type;  //事件的类型
    private String name;  //事件的名字

    public EventItem() {
    }

    //数据库构造方法
    public EventItem(String uuid, Date date, Time startTime, Time endTime, int lastedTime, String type, String name) {
        this.uuid = UUID.fromString(uuid);
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastedTime = lastedTime;
        this.type = type;
        this.name = name;
    }

    //创建事件构造方法
    public EventItem(UUID uuid, Date date, Time startTime, Time endTime, String type, String name) {
        this.uuid = uuid;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastedTime = endTime.getTimeDifference(startTime);
        this.type = type;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public int getLastedTime() {
        return lastedTime;
    }

    public void setLastedTime(int lastedTime) {
        this.lastedTime = lastedTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(uuid);
        dest.writeParcelable(date,flags);
        dest.writeParcelable(startTime,flags);
        dest.writeParcelable(endTime,flags);
        dest.writeInt(lastedTime);
        dest.writeString(type);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<EventItem> CREATOR =new Parcelable.Creator<EventItem>(){
        @Override
        public EventItem createFromParcel(Parcel source) {
            EventItem eventItem=new EventItem();
            eventItem.uuid=(UUID)source.readSerializable();
            eventItem.date=source.readParcelable(Date.class.getClassLoader());
            eventItem.startTime=source.readParcelable(Time.class.getClassLoader());
            eventItem.endTime=source.readParcelable(Time.class.getClassLoader());
            eventItem.lastedTime=source.readInt();
            eventItem.type=source.readString();
            eventItem.name=source.readString();
            return eventItem;
        }

        @Override
        public EventItem[] newArray(int size) {
            return new EventItem[size];
        }
    };
}
