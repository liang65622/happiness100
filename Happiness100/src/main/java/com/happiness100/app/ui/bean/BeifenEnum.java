package com.happiness100.app.ui.bean;

/**
 * Created by Administrator on 2016/8/31.
 */
public enum BeifenEnum {
    ELDER(1, "长辈"),
    PEERS(2, "同辈"),
    YOUNGERD(3, "晚辈");

    private int code;
    private String name;

    private BeifenEnum(int status, String name) {
        this.code = status;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByCode(Integer status) {
        for (BeifenEnum beifen : values()) {
            if (beifen.getCode() == status) {
                return beifen.getName();
            }
        }
        return "";
    }
}
