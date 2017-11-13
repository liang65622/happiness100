package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/31.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/8/31 14:26
 */


@Table(name = "Apply")
public class Apply extends Model implements Parcelable {

    /**
     * appJoinTimeSec : 1472555715
     * nickname : 412
     * headImage : 95_1472456613536.jpeg
     * note : 我是
     * userId : 95
     */
    @Column(name = "appJoinTimeSec") @Expose
    private int appJoinTimeSec;
    @Column(name = "nickname") @Expose
    private String nickname;
    @Column(name = "headImage") @Expose
    private String headImage;
    @Column(name = "note") @Expose
    private String note;
    @Column(name = "userId") @Expose
    private int userId;

    @Column(name = "isAgree") @Expose
    private int isAgree;

    public int getAppJoinTimeSec() {
        return appJoinTimeSec;
    }

    public void setAppJoinTimeSec(int appJoinTimeSec) {
        this.appJoinTimeSec = appJoinTimeSec;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(int isAgree) {
        this.isAgree = isAgree;
    }


    public Apply() {
    }

    @Override
    public String toString() {
        return "Apply{" +
                "appJoinTimeSec=" + appJoinTimeSec +
                ", nickname='" + nickname + '\'' +
                ", headImage='" + headImage + '\'' +
                ", note='" + note + '\'' +
                ", userId=" + userId +
                ", isAgree=" + isAgree +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.appJoinTimeSec);
        dest.writeString(this.nickname);
        dest.writeString(this.headImage);
        dest.writeString(this.note);
        dest.writeInt(this.userId);
        dest.writeInt(this.isAgree);
    }

    protected Apply(Parcel in) {
        this.appJoinTimeSec = in.readInt();
        this.nickname = in.readString();
        this.headImage = in.readString();
        this.note = in.readString();
        this.userId = in.readInt();
        this.isAgree = in.readInt();
    }

    public static final Parcelable.Creator<Apply> CREATOR = new Parcelable.Creator<Apply>() {
        @Override
        public Apply createFromParcel(Parcel source) {
            return new Apply(source);
        }

        @Override
        public Apply[] newArray(int size) {
            return new Apply[size];
        }
    };
}
