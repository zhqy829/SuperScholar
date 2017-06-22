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

    @Nullable
    public static UserLib getInstance(@Nullable User user){
        if(sUserLib==null&&user==null){
            return null;
        }else if(sUserLib==null){
            sUserLib=new UserLib(user);
        }
        return sUserLib;
    }

    private UserLib(User user){
        this.user=user;
    }

    public User getUser(){
        return user;
    }
}
