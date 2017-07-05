package com.superscholar.android.entity;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zhqy on 2017/7/2.
 */

public class ConsumptionDetail {

    private static final SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMAT=new SimpleDateFormat("HH:mm:ss");

    private UUID uuid;
    private String date;
    private String time;
    private String name;
    private int price;

    public ConsumptionDetail(UUID uuid,String date,String time,String name,int price){
        this.uuid=uuid;
        this.date=date;
        this.time=time;
        this.name=name;
        this.price=price;
    }

    public ConsumptionDetail(String name,int price){
        this.uuid=UUID.randomUUID();
        Date date=new Date();
        this.date=DATE_FORMAT.format(date);
        this.time=TIME_FORMAT.format(date);
        this.name=name;
        this.price=price;
    }

    public ConsumptionDetail(){

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
