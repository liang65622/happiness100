package com.happiness100.app.ui.bean;

/**
 * Created by Administrator on 2016/9/1.
 */
public enum YesOrNoEnum {
    YES(1),
    NO(2);
    private int code;

    private YesOrNoEnum(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

}
