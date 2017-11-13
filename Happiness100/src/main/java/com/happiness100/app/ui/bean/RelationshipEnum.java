package com.happiness100.app.ui.bean;/**
 * Created by Administrator on 2016/9/9.
 */

/**
 * 作者：justin on 2016/9/9 22:07
 */
public enum RelationshipEnum {
    HASBAND(1),
    FATHER(3),
    MOTHER(4),
    WIFE(2),
    BROTHER(5),
    SUN(8),
    DOUGHTER(6);
    private int code;

    private RelationshipEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
