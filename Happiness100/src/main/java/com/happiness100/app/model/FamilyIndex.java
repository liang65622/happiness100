package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/30.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：justin on 2016/8/30 11:26
 */
public class FamilyIndex implements Parcelable {


    /**
     * clanName : ffff
     * vals : [0,0,0,0]
     * members : [{"cwNote":"我","nickname":"nan968","headImage":"44_1472741456437.jpg","userid":44,"phone":"86 13900000000","clanRole":2},{"cwNote":"5555","nickname":"111111","headImage":"98_1472731569095.jpeg","userid":98,"phone":"86 13633333333","clanRole":3},{"cwNote":"fff","nickname":"ertert","headImage":"99_1472741042008.jpeg","userid":99,"phone":"86 13622222222","clanRole":3},{"cwNote":"ffff","nickname":"147","headImage":null,"userid":86,"phone":"86 13611111111","clanRole":1},{"cwNote":"dfsdfsdf","nickname":"ggggg","headImage":"100_1472803175282.jpeg","userid":100,"phone":"86 13655555555","clanRole":3},{"cwNote":"sdfsdf","nickname":"111111","headImage":"101_1472805139312.jpeg","userid":101,"phone":"86 13666666666","clanRole":1}]
     * haveHealthPhones : []
     * today : 2016-09-05
     * createID : 44
     * clanRole : 2
     */
    @Expose
    private String clanName;
    @Expose
    private String today;
    @Expose
    private int createID;
    @Expose
    private int clanRole;
    @Expose
    private List<Integer> vals;
    /**
     * cwNote : 我
     * nickname : nan968
     * headImage : 44_1472741456437.jpg
     * userid : 44
     * phone : 86 13900000000
     * clanRole : 2
     */
    @Expose
    public List<Member> members;
    @Expose
    private List<String> haveHealthPhones;

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public int getCreateID() {
        return createID;
    }

    public void setCreateID(int createID) {
        this.createID = createID;
    }

    public int getClanRole() {
        return clanRole;
    }

    public void setClanRole(int clanRole) {
        this.clanRole = clanRole;
    }

    public List<Integer> getVals() {
        return vals;
    }

    public void setVals(List<Integer> vals) {
        this.vals = vals;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public List<String> getHaveHealthPhones() {
        return haveHealthPhones;
    }

    public void setHaveHealthPhones(List<String> haveHealthPhones) {
        this.haveHealthPhones = haveHealthPhones;
    }

    @Table(name = "Member")
    public static class Member extends Model implements Parcelable {
        @Column(name = "cwNote")
        @Expose
        private String cwNote;
        @Column(name = "nickname")
        @Expose
        private String nickname;
        @Column(name = "headImage")
        @Expose
        private String headImage;
        @Column(name = "userid")
        @Expose
        private long userid;
        @Column(name = "phone")
        @Expose
        private String phone;
        @Column(name = "clanRole")
        @Expose
        private int clanRole;

        @Column(name = "clanId")
        private int clanId;
        private String letters;

        public int getHasHealMsg() {
            return hasHealMsg;
        }

        public void setHasHealMsg(int hasHealMsg) {
            this.hasHealMsg = hasHealMsg;
        }

        private int hasHealMsg;
        public String getLetters() {
            return letters;
        }

        public void setLetters(String letters) {
            this.letters = letters;
        }

        public int getClanId() {
            return clanId;
        }

        public void setClanId(int clanId) {
            this.clanId = clanId;
        }

        public String getCwNote() {
            return cwNote;
        }

        public void setCwNote(String cwNote) {
            this.cwNote = cwNote;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadImage() {
            return headImage;
        }

        public void setHeadImage(String headImage) {
            this.headImage = headImage;
        }

        public long getUserid() {
            return userid;
        }

        public void setUserid(long userid) {
            this.userid = userid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getClanRole() {
            return clanRole;
        }

        public void setClanRole(int clanRole) {
            this.clanRole = clanRole;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cwNote);
            dest.writeString(this.nickname);
            dest.writeString(this.headImage);
            dest.writeLong(this.userid);
            dest.writeString(this.phone);
            dest.writeInt(this.clanRole);
            dest.writeInt(this.clanId);
            dest.writeString(this.letters);
        }

        public Member() {
        }

        protected Member(Parcel in) {
            this.cwNote = in.readString();
            this.nickname = in.readString();
            this.headImage = in.readString();
            this.userid = in.readLong();
            this.phone = in.readString();
            this.clanRole = in.readInt();
            this.clanId = in.readInt();
            this.letters = in.readString();
        }

        public static final Parcelable.Creator<Member> CREATOR = new Parcelable.Creator<Member>() {
            @Override
            public Member createFromParcel(Parcel source) {
                return new Member(source);
            }

            @Override
            public Member[] newArray(int size) {
                return new Member[size];
            }
        };


    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.clanName);
        dest.writeString(this.today);
        dest.writeInt(this.createID);
        dest.writeInt(this.clanRole);
        dest.writeList(this.vals);
        dest.writeTypedList(this.members);
        dest.writeStringList(this.haveHealthPhones);
    }

    public FamilyIndex() {
    }

    protected FamilyIndex(Parcel in) {
        this.clanName = in.readString();
        this.today = in.readString();
        this.createID = in.readInt();
        this.clanRole = in.readInt();
        this.vals = new ArrayList<Integer>();
        in.readList(this.vals, Integer.class.getClassLoader());
        this.members = in.createTypedArrayList(Member.CREATOR);
        this.haveHealthPhones = in.createStringArrayList();
    }

    public static final Parcelable.Creator<FamilyIndex> CREATOR = new Parcelable.Creator<FamilyIndex>() {
        @Override
        public FamilyIndex createFromParcel(Parcel source) {
            return new FamilyIndex(source);
        }

        @Override
        public FamilyIndex[] newArray(int size) {
            return new FamilyIndex[size];
        }
    };
}
