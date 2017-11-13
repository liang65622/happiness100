package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/5.
 */

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * 作者：justin on 2016/9/5 15:09
 */
public class FamilyDynamic {


    /**
     * yesdayJf : 0
     * todayJf : 0
     * xingFuDongTaiList : [{"id":1,"clanId":26,"dongTai":"6546加入家族","dongTaiVal":300,"createTime":1473059323000,"dongTaiType":2,"cdate":0}]
     */
    @Expose
    private int yesdayJf;
    @Expose
    private int todayJf;
    /**
     * id : 1
     * clanId : 26
     * dongTai : 6546加入家族
     * dongTaiVal : 300
     * createTime : 1473059323000
     * dongTaiType : 2
     * cdate : 0
     */
    @Expose
    private List<XingFuDongTaiListBean> xingFuDongTaiList;

    public int getYesdayJf() {
        return yesdayJf;
    }

    public void setYesdayJf(int yesdayJf) {
        this.yesdayJf = yesdayJf;
    }

    public int getTodayJf() {
        return todayJf;
    }

    public void setTodayJf(int todayJf) {
        this.todayJf = todayJf;
    }

    public List<XingFuDongTaiListBean> getXingFuDongTaiList() {
        return xingFuDongTaiList;
    }

    public void setXingFuDongTaiList(List<XingFuDongTaiListBean> xingFuDongTaiList) {
        this.xingFuDongTaiList = xingFuDongTaiList;
    }

    public static class XingFuDongTaiListBean {
        @Expose
        private int id;
        @Expose
        private int clanId;
        @Expose
        private String dongTai;
        @Expose
        private int dongTaiVal;
        @Expose
        private long createTime;
        @Expose
        private int dongTaiType;
        @Expose
        private int cdate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getClanId() {
            return clanId;
        }

        public void setClanId(int clanId) {
            this.clanId = clanId;
        }

        public String getDongTai() {
            return dongTai;
        }

        public void setDongTai(String dongTai) {
            this.dongTai = dongTai;
        }

        public int getDongTaiVal() {
            return dongTaiVal;
        }

        public void setDongTaiVal(int dongTaiVal) {
            this.dongTaiVal = dongTaiVal;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getDongTaiType() {
            return dongTaiType;
        }

        public void setDongTaiType(int dongTaiType) {
            this.dongTaiType = dongTaiType;
        }

        public int getCdate() {
            return cdate;
        }

        public void setCdate(int cdate) {
            this.cdate = cdate;
        }
    }
}
