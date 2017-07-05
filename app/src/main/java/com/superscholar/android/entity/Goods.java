package com.superscholar.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by zhqy on 2017/6/28.
 */

public class Goods implements Parcelable{

    private UUID uuid;
    private String name;
    private int price;
    private String detail;


    public Goods(String name,String detail,int price){
        this.uuid=UUID.randomUUID();
        this.name=name;
        this.detail=detail;
        this.price=price;
    }

    public Goods(){

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(uuid);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(detail);
    }

    public static final Parcelable.Creator<Goods> CREATOR =new Parcelable.Creator<Goods>(){
        @Override
        public Goods createFromParcel(Parcel source) {
            Goods goods=new Goods();
            goods.uuid=(UUID)source.readSerializable();
            goods.name=source.readString();
            goods.price=source.readInt();
            goods.detail=source.readString();
            return goods;
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };
}
