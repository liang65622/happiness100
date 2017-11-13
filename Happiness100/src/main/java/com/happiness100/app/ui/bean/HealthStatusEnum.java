package com.happiness100.app.ui.bean;/**
 * Created by Administrator on 2016/8/17.
 */

/**
 * 作者：justin on 2016/8/17 14:35
 */
public enum HealthStatusEnum {


    NORMAL(1, "正常"),
    GOOD(2, "良好"),
    NO_GOOD(3, "亚健康"),
    BAD(4, "不健康");

    private int status;
    private String name;

    private HealthStatusEnum(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getNameByCode(Integer status) {
        for (HealthStatusEnum healthStatusEnum : values()) {
            if (healthStatusEnum.getStatus() == status) {
                return healthStatusEnum.getName();
            }
        }
        return "";
    }
}
