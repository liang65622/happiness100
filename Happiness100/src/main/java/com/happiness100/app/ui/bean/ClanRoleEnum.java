package com.happiness100.app.ui.bean;

/**
 * Created by Administrator on 2016/9/1.
 */
public enum ClanRoleEnum {
    NORMAL(1),
    CREATER(2),
    MANAGER(3);
    private int code;

    private ClanRoleEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
