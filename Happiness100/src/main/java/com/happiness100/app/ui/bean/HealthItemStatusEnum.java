package com.happiness100.app.ui.bean;/**
 * Created by Administrator on 2016/8/17.
 */

import com.happiness100.app.R;

/**
 * 作者：justin on 2016/8/17 14:35
 */
public enum HealthItemStatusEnum {

    NONE(0, "没有", R.color.health_normal),
    NORMAL(1, "正常", R.color.health_normal),
    HIGHT(2, "偏高", R.color.health_low),
    LOW(3, "偏低", R.color.health_low);
    //0:没有 1:正常 2：偏高 3：偏低
    private final int status;
    private final String name;
    private final int color;

    private HealthItemStatusEnum(int status, String name, int color) {
        this.status = status;
        this.name = name;
        this.color = color;
    }

    public int getStatus() {
        return status;
    }

    public int getColor() {
        return color;
    }

    public String getName() {
        return name;
    }


    public static String getNameByCode(Integer status) {
        for (HealthItemStatusEnum healthStatusEnum : values()) {
            if (healthStatusEnum.getStatus() == status) {
                return healthStatusEnum.getName();
            }
        }
        return "";
    }

    public static int getColor(Integer status) {
        for (HealthItemStatusEnum healthStatusEnum : values()) {
            if (healthStatusEnum.getStatus() == status) {
                return healthStatusEnum.getColor();
            }
        }
        return 0;
    }

}
