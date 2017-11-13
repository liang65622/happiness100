package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/7.
 */

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/9/7 16:23
 */
public class ClanCwNote {


    /**
     * cwId : 100
     * cw : 自己
     * husband : 丈夫
     * wife : 妻子
     * father : 父亲
     * mother : 母亲
     * elderBrother : 哥
     * youngBrother : 弟
     * elderSister : 姐
     * youngSister : 妹
     * son : 儿子
     * daughter : 女儿
     */

    @Expose
    private String cwId;
    @Expose
    private String cw;
    @Expose
    private String husband;
    @Expose
    private String wife;
    @Expose
    private String father;
    @Expose
    private String mother;
    @Expose
    private String elderBrother;
    @Expose
    private String youngBrother;
    @Expose
    private String elderSister;
    @Expose
    private String youngSister;
    @Expose
    private String son;
    @Expose
    private String daughter;

    public String getCwId() {
        return cwId;
    }

    public void setCwId(String cwId) {
        this.cwId = cwId;
    }

    public String getCw() {
        return cw;
    }

    public void setCw(String cw) {
        this.cw = cw;
    }

    public String getHusband() {
        return husband;
    }

    public void setHusband(String husband) {
        this.husband = husband;
    }

    public String getWife() {
        return wife;
    }

    public void setWife(String wife) {
        this.wife = wife;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getElderBrother() {
        return elderBrother;
    }

    public void setElderBrother(String elderBrother) {
        this.elderBrother = elderBrother;
    }

    public String getYoungBrother() {
        return youngBrother;
    }

    public void setYoungBrother(String youngBrother) {
        this.youngBrother = youngBrother;
    }

    public String getElderSister() {
        return elderSister;
    }

    public void setElderSister(String elderSister) {
        this.elderSister = elderSister;
    }

    public String getYoungSister() {
        return youngSister;
    }

    public void setYoungSister(String youngSister) {
        this.youngSister = youngSister;
    }

    public String getSon() {
        return son;
    }

    public void setSon(String son) {
        this.son = son;
    }

    public String getDaughter() {
        return daughter;
    }

    public void setDaughter(String daughter) {
        this.daughter = daughter;
    }
}
