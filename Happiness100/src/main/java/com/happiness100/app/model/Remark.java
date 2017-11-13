package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/13.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * 作者：jiangsheng on 2016/9/13 11:15
 */
@Table(name = "Remark")
public class Remark extends Model{
    @Column(name = "userId") @Expose
    private String userId;
    @Column(name = "xf") @Expose
    private int xf;
    @Column(name = "noteName") @Expose
    private String noteName;
    @Column(name = "mobiles") @Expose
    private String mobiles;

    public int getXf() {return xf;}

    public void setXf(int xf) {
        this.xf = xf;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getMobiles() {
        return mobiles;
    }

    public void setMobiles(String mobiles) {
        this.mobiles = mobiles;
    }
}
