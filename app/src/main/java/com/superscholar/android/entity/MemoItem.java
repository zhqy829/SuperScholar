package com.superscholar.android.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/1.
 * 备忘录子项类
 */

public class MemoItem extends DataSupport{
    private String title;
    private String time;
    private String content;

    public MemoItem(String title,String time,String content){
        this.title=title;
        this.time=time;
        this.content=content;
    }

    public MemoItem(){}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
