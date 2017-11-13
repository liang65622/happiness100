package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/30.
 */

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/8/30 18:16
 */
public class SearchFamily {

    /**
     * clanId : 3
     * clanLogo : 3_1472212370142.jpg
     * name : ffff
     * gongGao : 1111
     */
    @Expose
    private int clanId;
    @Expose
    private String clanLogo;
    @Expose
    private String name;
    @Expose
    private String gongGao;

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
}
