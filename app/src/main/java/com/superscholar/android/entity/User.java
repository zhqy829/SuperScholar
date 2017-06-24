package com.superscholar.android.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/23.
 */

public class User {

    private String username;
    private String password;
    private double grade;
    private String sID;
    private String email;
    private int currencyAmount;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double creditGrade) {
        this.grade = creditGrade;
    }

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(int currencyAmount) {
        this.currencyAmount = currencyAmount;
    }
}
