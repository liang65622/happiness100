package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/16.
 */

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * 作者：justin on 2016/8/16 17:25
 */
public class HealthDetail {


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
    private List<HDatasBean> hDatas;
    @Expose
    private List<List<Double>> normalData;

    public List<HDatasBean> getHDatas() {
        return hDatas;
    }

    public void setHDatas(List<HDatasBean> hDatas) {
        this.hDatas = hDatas;
    }

    public List<List<Double>> getNormalData() {
        return normalData;
    }

    public void setNormalData(List<List<Double>> normalData) {
        this.normalData = normalData;
    }

    public static class HDatasBean {
        @Expose
        private float xueYa1;
        @Expose
        private float xueYa2;
        @Expose
        private float xueTang1;
        @Expose
        private float xueTang2;
        @Expose
        private float xinLv1;
        @Expose
        private float xinLv2;
        @Expose
        private float tiZhong;

        public float getXueYa1() {
            return xueYa1;
        }

        public void setXueYa1(float xueYa1) {
            this.xueYa1 = xueYa1;
        }

        public float getXueYa2() {
            return xueYa2;
        }

        public void setXueYa2(float xueYa2) {
            this.xueYa2 = xueYa2;
        }

        public float getXueTang1() {
            return xueTang1;
        }

        public void setXueTang1(float xueTang1) {
            this.xueTang1 = xueTang1;
        }

        public float getXueTang2() {
            return xueTang2;
        }

        public void setXueTang2(float xueTang2) {
            this.xueTang2 = xueTang2;
        }

        public float getXinLv1() {
            return xinLv1;
        }

        public void setXinLv1(float xinLv1) {
            this.xinLv1 = xinLv1;
        }

        public float getXinLv2() {
            return xinLv2;
        }

        public void setXinLv2(float xinLv2) {
            this.xinLv2 = xinLv2;
        }

        public float getTiZhong() {
            return tiZhong;
        }

        public void setTiZhong(float tiZhong) {
            this.tiZhong = tiZhong;
        }
    }
}
