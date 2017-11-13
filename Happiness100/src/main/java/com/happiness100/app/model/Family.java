package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/27.
 */

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/8/27 10:55
 */
public class Family {

    /**
     * ty : 1
     * gongGao : 1111
     * clanName : ffff
     * createNickname : nan968
     * clanLogo : 3_1472212370142.jpg
     * mobile : 86 13900000000
     */
    @Expose
    private int ty;
    @Expose
    private String gongGao;
    @Expose
    private String clanName;
    @Expose
    private String createNickname;
    @Expose
    private String clanLogo;
    @Expose
    private String mobile;

    public int getTy() {
        return ty;
    }

    public void setTy(int ty) {
        this.ty = ty;
    }

    public String getGongGao() {
        return gongGao;
    }

    public void setGongGao(String gongGao) {
        this.gongGao = gongGao;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getCreateNickname() {
        return createNickname;
    }

    public void setCreateNickname(String createNickname) {
        this.createNickname = createNickname;
    }

    public String getClanLogo() {
        return clanLogo;
    }

    public void setClanLogo(String clanLogo) {
        this.clanLogo = clanLogo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
