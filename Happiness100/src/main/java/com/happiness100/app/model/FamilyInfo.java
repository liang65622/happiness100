package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/30.
 */

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/8/30 16:39
 */
public class FamilyInfo {

    /**
     * clanId : 3
     * clanLogo : 3_1472212370142.jpg
     * name : ffff
     * gongGao : 1111
     * creater : nan968
     */
    @Expose
    private int clanId;
    @Expose
    private String clanLogo;
    @Expose
    private String name;
    @Expose
    private String gongGao;
    @Expose
    private String creater;

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public String getClanLogo() {
        return clanLogo;
    }

    public void setClanLogo(String clanLogo) {
        this.clanLogo = clanLogo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGongGao() {
        return gongGao;
    }

    public void setGongGao(String gongGao) {
        this.gongGao = gongGao;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }
}
