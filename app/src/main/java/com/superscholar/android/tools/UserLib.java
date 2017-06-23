package com.superscholar.android.tools;

import android.support.annotation.Nullable;

import com.superscholar.android.entity.User;

/**
 * Created by zhqy on 2017/6/15.
 * 存储当前登录用户信息的单例类
 */

public class UserLib {

    private static UserLib sUserLib;

    private User user;

    static{
        sUserLib=new UserLib();
    }

    private UserLib(){
        user=new User();
    }

    public static UserLib getInstance(){
        return sUserLib;
    }

    public User getUser(){
        return user;
    }
}
