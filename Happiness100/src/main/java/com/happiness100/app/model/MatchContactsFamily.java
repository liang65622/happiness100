package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/29.
 */

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/8/29 17:08
 */
public class MatchContactsFamily {

    /**
     * clanName : ffff
     * clanLogo : 3_1472212370142.jpg
     * ty : 1
     * mobile : 86 13900000000
     * createNickname : nan968
     * gongGao : 1111
     */
    @Expose
    private String clanName;
    @Expose
    private String clanLogo;
    @Expose
    private int ty;
    @Expose
    private String mobile;
    @Expose
    private long clanId;
    @Expose
    private String createNickname;
    @Expose
    private String gongGao;

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getClanLogo() {
        return clanLogo;
    }

    public void setClanLogo(String clanLogo) {
        this.clanLogo = clanLogo;
    }

    public int getTy() {
        return ty;
    }

    public void setTy(int ty) {
        this.ty = ty;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreateNickname() {
        return createNickname;
    }

    public void setCreateNickname(String createNickname) {
        this.createNickname = createNickname;
    }

    public String getGongGao() {
        return gongGao;
    }

    public void setGongGao(String gongGao) {
        this.gongGao = gongGao;
    }

    public long getClanId() {
        return clanId;
    }

    public void setClanId(long clanId) {
        this.clanId = clanId;
    }
}
