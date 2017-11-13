package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/1.
 */

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/9/1 10:46
 */
public class FamilyRank {


    /**
     * clanName : ffff
     * creater : nan968
     * clanLogo : 3_1472212370142.jpg
     * clanId : 3
     * xfVal : 0
     */
    @Expose
    private String clanName;
    @Expose
    private String creater;
    @Expose
    private String clanLogo;
    @Expose
    private int clanId;
    @Expose
    private int xfVal;

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getClanLogo() {
        return clanLogo;
    }

    public void setClanLogo(String clanLogo) {
        this.clanLogo = clanLogo;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public int getXfVal() {
        return xfVal;
    }

    public void setXfVal(int xfVal) {
        this.xfVal = xfVal;
    }
}
