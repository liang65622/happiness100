package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/11.
 */

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/8/11 14:27
 */
public class ResponeBean {
    @Expose
    private int code;
    @Expose
    private String msg;
    @Expose
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
