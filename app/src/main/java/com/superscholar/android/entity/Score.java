package com.superscholar.android.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zhqy on 2017/9/17.
 */

public class Score {
    @SerializedName("courseName")
    private String name;
    private String credit;
    private String score;

    public Score(String name, String credit, String score) {
        this.name = name;
        this.credit = credit;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
