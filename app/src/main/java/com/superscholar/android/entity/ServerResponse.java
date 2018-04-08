package com.superscholar.android.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author zhqy
 * @date 2018/4/7
 */

public class ServerResponse<T> {

    private int code;
    private String message;
    private T data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
