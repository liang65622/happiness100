package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/16.
 */

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;

/**
 * 作者：justin on 2016/8/16 16:36
 */
public class HealthData implements Serializable{

    /**
     * healthScore : 48.3
     * healthStatus : 4
     * status : [3,3,2,2,3,1]
     * off : [0,0,0,0,0,0]
     * per : [40,40,40,40,40,90]
     * hData : {"xueYa1":10,"xueYa2":50,"xueTang1":20,"xueTang2":30,"xinLv1":10,"xinLv2":50,"tiZhong":12}
     * signs : [false,false,true]
     */
    @Expose
    private double healthScore;
    @Expose
    private int healthStatus;
    /**
     * xueYa1 : 10.0
     * xueYa2 : 50.0
     * xueTang1 : 20.0
     * xueTang2 : 30.0
     * xinLv1 : 10.0
     * xinLv2 : 50.0
     * tiZhong : 12.0
     */
    @Expose
    private HDataBean hData;
    @Expose
    private List<Integer> status;
    @Expose
    private List<Float> off;
    @Expose
    private List<Float> per;
    @Expose
    private List<Integer> days;
    @Expose
    private List<Boolean> signs;
    @Expose
    private float scoreOff;

    public HDataBean gethData() {
        return hData;
    }

    public void sethData(HDataBean hData) {
        this.hData = hData;
    }

    public float getScoreOff() {
        return scoreOff;
    }

    public void setScoreOff(float scoreOff) {
        this.scoreOff = scoreOff;
    }

    public double getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(double healthScore) {
        this.healthScore = healthScore;
    }

    public int getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(int healthStatus) {
        this.healthStatus = healthStatus;
    }

    public HDataBean getHData() {
        return hData;
    }

    public void setHData(HDataBean hData) {
        this.hData = hData;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public List<Float> getOff() {
        return off;
    }

    public void setOff(List<Float> off) {
        this.off = off;
    }

    public List<Float> getPer() {
        return per;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public void setPer(List<Float> per) {
        this.per = per;
    }

    public List<Boolean> getSigns() {
        return signs;
    }

    public void setSigns(List<Boolean> signs) {
        this.signs = signs;
    }

    public static class HDataBean {
        private double xueYa1;
        private double xueYa2;
        private double xueTang1;
        private double xueTang2;
        private double xinLv1;
        private double xinLv2;
        private double tiZhong;

        public double getXueYa1() {
            return xueYa1;
        }

        public void setXueYa1(double xueYa1) {
            this.xueYa1 = xueYa1;
        }

        public double getXueYa2() {
            return xueYa2;
        }

        public void setXueYa2(double xueYa2) {
            this.xueYa2 = xueYa2;
        }

        public double getXueTang1() {
            return xueTang1;
        }

        public void setXueTang1(double xueTang1) {
            this.xueTang1 = xueTang1;
        }

        public double getXueTang2() {
            return xueTang2;
        }

        public void setXueTang2(double xueTang2) {
            this.xueTang2 = xueTang2;
        }

        public double getXinLv1() {
            return xinLv1;
        }

        public void setXinLv1(double xinLv1) {
            this.xinLv1 = xinLv1;
        }

        public double getXinLv2() {
            return xinLv2;
        }

        public void setXinLv2(double xinLv2) {
            this.xinLv2 = xinLv2;
        }

        public double getTiZhong() {
            return tiZhong;
        }

        public void setTiZhong(double tiZhong) {
            this.tiZhong = tiZhong;
        }
    }

    @Override
    public String toString() {
        return "HealthData{" +
                "healthScore=" + healthScore +
                ", healthStatus=" + healthStatus +
                ", hData=" + hData +
                ", status=" + status +
                ", off=" + off +
                ", per=" + per +
                ", days=" + days +
                ", signs=" + signs +
                ", scoreOff=" + scoreOff +
                '}';
    }
}
