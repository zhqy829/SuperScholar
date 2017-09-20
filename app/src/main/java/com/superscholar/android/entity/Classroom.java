package com.superscholar.android.entity;

/**
 * Created by zhqy on 2017/9/17.
 */

public class Classroom {
    private String naem;
    private String rid;

    public Classroom(String classroom, String rid) {
        this.naem = classroom;
        this.rid = rid;
    }

    public String getName() {
        return naem;
    }

    public void setName(String naem) {
        this.naem = naem;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }
}
