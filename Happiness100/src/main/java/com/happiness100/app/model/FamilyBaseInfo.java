package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/27.
 */

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * 作者：justin on 2016/8/27 09:13
 */
public class FamilyBaseInfo implements Serializable {


    /**
     * clanId : 3
     * clanName : ffff
     * isDefault : true
     * clanLogo : 3_1472212370142.jpg
     * gongGao : 1111
     */
    @Expose
    private int clanId;
    @Expose
    private String clanName;
    @Expose
    private boolean isDefault;
    @Expose
    private String clanLogo;
    @Expose
    private String gongGao;

    @Expose
    private int clanRole;


    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public boolean isIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getClanLogo() {
        return clanLogo;
    }

    public void setClanLogo(String clanLogo) {
        this.clanLogo = clanLogo;
    }

    public String getGongGao() {
        return gongGao;
    }

    public void setGongGao(String gongGao) {
        this.gongGao = gongGao;
    }

    public int getClanRole() {
        return clanRole;
    }

    public void setClanRole(int clanRole) {
        this.clanRole = clanRole;
    }
}
