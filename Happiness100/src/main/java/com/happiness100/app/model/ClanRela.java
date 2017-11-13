package com.happiness100.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClanRela {


    @Expose
    public long familyIdIndex = 0l;
    @Expose
    public long uIndex = 0l;
    @Expose
    public final Map<Long, FamilyUnit> familyUnitMap = new HashMap<Long, FamilyUnit>();

    @Expose//单位列表,做重复性检测等
    public final Map<Long, Long> pUserId2IndexMap = new HashMap<Long, Long>();

    @Expose//单位列表，根据单位索引获取单位
    public final Map<Long, PUnit> uIndex2Punit = new HashMap<Long, PUnit>();

    //排行称谓
    //public static transient final Map<Integer, String> rankCw = new HashMap<Integer, String>();

    @Expose//TODO 当前空对象数量，达到30提示需要填充
    public int blankPUnitCount = 0;
    /*static{
        rankCw.put(1, "大");
		rankCw.put(2, "二");
		rankCw.put(3, "三");
		rankCw.put(4, "四");
		rankCw.put(5, "五");
		rankCw.put(6, "六");
		rankCw.put(7, "七");
		rankCw.put(8, "八");
		rankCw.put(9, "九");
		rankCw.put(10, "十");
		rankCw.put(11, "十一");
		rankCw.put(12, "十二");
		rankCw.put(13, "十三");
		rankCw.put(14, "十四");
		rankCw.put(15, "十五");
		rankCw.put(16, "十六");
		rankCw.put(17, "十七");
		rankCw.put(18, "十八");
		rankCw.put(19, "十九");
		rankCw.put(20, "二十");
	}*/

    @Expose//自己
    public long creater;

    public static class PUnit implements Parcelable {

        @Expose //索引唯一值，客户端需要
        public long unitId = 0l;

        @Expose //扩展标识，这里表示用户Id
        public long userId;
        @Expose //为0不存在
        public long uFaId;
        @Expose//为0不存在
        public long dFaId;
        @Expose //排行，男女分离，最大20，自己选择, 001-999 女 1000~ 男, 男女称谓添加时候，判断rank的值是否符合性别
        public int rank;
        //1男 2女
        //public int gender;
        @Expose//称谓
        public String cw;

        @Expose//名字备注
        public String nameNote;
        @Expose//辈份号
        public int bfIndex;

        @Expose
        public String dUserId;

        @Expose
        public int cwId;

        //头像

		/*private PUnit(long id, long uFaId, long dFaId, int rank){
			this.id = id;
			this.uFaId = uFaId;
			this.dFaId = dFaId;
			this.rank = rank;
		}*/

        @Override
        public String toString() {
            return "PUnit{" +
                    "unitId=" + unitId +
                    ", userId=" + userId +
                    ", uFaId=" + uFaId +
                    ", dFaId=" + dFaId +
                    ", rank=" + rank +
                    ", cw='" + cw + '\'' +
                    ", nameNote='" + nameNote + '\'' +
                    ", bfIndex=" + bfIndex +
                    ", dUserId='" + dUserId + '\'' +
                    ", cwId=" + cwId +
                    '}';
        }

        public PUnit() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.unitId);
            dest.writeLong(this.userId);
            dest.writeLong(this.uFaId);
            dest.writeLong(this.dFaId);
            dest.writeInt(this.rank);
            dest.writeString(this.cw);
            dest.writeString(this.nameNote);
            dest.writeInt(this.bfIndex);
            dest.writeString(this.dUserId);
            dest.writeInt(this.cwId);
        }

        protected PUnit(Parcel in) {
            this.unitId = in.readLong();
            this.userId = in.readLong();
            this.uFaId = in.readLong();
            this.dFaId = in.readLong();
            this.rank = in.readInt();
            this.cw = in.readString();
            this.nameNote = in.readString();
            this.bfIndex = in.readInt();
            this.dUserId = in.readString();
            this.cwId = in.readInt();
        }

        public static final Creator<PUnit> CREATOR = new Creator<PUnit>() {
            @Override
            public PUnit createFromParcel(Parcel source) {
                return new PUnit(source);
            }

            @Override
            public PUnit[] newArray(int size) {
                return new PUnit[size];
            }
        };
    }

    public static class FamilyUnit implements Parcelable {
        //public long familyIdIndex = 0l;
        @Expose//public static long atomicLong = 0;
        private long faId;
        @Expose
        public long father;
        @Expose
        public long mother;
        @Expose
        public List<Long> children;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.faId);
            dest.writeLong(this.father);
            dest.writeLong(this.mother);
            dest.writeList(this.children);
        }

        public FamilyUnit() {
        }

        protected FamilyUnit(Parcel in) {
            this.faId = in.readLong();
            this.father = in.readLong();
            this.mother = in.readLong();
            this.children = new ArrayList<Long>();
            in.readList(this.children, Long.class.getClassLoader());
        }

        public static final Parcelable.Creator<FamilyUnit> CREATOR = new Parcelable.Creator<FamilyUnit>() {
            @Override
            public FamilyUnit createFromParcel(Parcel source) {
                return new FamilyUnit(source);
            }

            @Override
            public FamilyUnit[] newArray(int size) {
                return new FamilyUnit[size];
            }
        };
    }

    @Override
    public String toString() {
        return "ClanRela{" +
                "familyIdIndex=" + familyIdIndex +
                ", uIndex=" + uIndex +
                ", familyUnitMap=" + familyUnitMap +
                ", pUserId2IndexMap=" + pUserId2IndexMap +
                ", uIndex2Punit=" + uIndex2Punit +
                ", blankPUnitCount=" + blankPUnitCount +
                ", creater=" + creater +
                '}';
    }
}
