package com.superscholar.android.entity;

/**
 * Created by Administrator on 2017/4/5.
 */

public class Menu {
    private String text;
    private int imageId;

    public Menu(){}

    public Menu(String text,int imageId){
        this.text=text;
        this.imageId=imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
