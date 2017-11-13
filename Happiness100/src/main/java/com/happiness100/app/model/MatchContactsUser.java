package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/31.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * 作者：justin on 2016/8/31 19:48
 */
public class MatchContactsUser implements Parcelable {
    /**
     * userId : 95
     * mobile : 86 13677777777
     * headImage : 95_1472456613536.jpeg
     */
    @Expose
    private int userId;
    @Expose
    private String name;
    @Expose
    private String nickname;
    @Expose
    private String mobile;
    @Expose
    private String headImage;
    @Expose
    private String letters;
    @Expose
    private int isWaitAuth;

    private int isInFamily;

    public int getIsInFamily() {
        return isInFamily;
    }

    public void setIsInFamily(int isInFamily) {
        this.isInFamily = isInFamily;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getIsWaitAuth() {
        return isWaitAuth;
    }

    public void setIsWaitAuth(int isWaitAuth) {
        this.isWaitAuth = isWaitAuth;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.mobile);
        dest.writeString(this.headImage);
        dest.writeString(this.letters);
        dest.writeInt(this.isWaitAuth);
    }

    public MatchContactsUser() {
    }

    protected MatchContactsUser(Parcel in) {
        this.userId = in.readInt();
        this.name = in.readString();
        this.mobile = in.readString();
        this.headImage = in.readString();
        this.letters = in.readString();
        this.isWaitAuth = in.readInt();
        this.nickname = in.readString();
    }

    public static final Parcelable.Creator<MatchContactsUser> CREATOR = new Parcelable.Creator<MatchContactsUser>() {
        @Override
        public MatchContactsUser createFromParcel(Parcel source) {
            return new MatchContactsUser(source);
        }

        @Override
        public MatchContactsUser[] newArray(int size) {
            return new MatchContactsUser[size];
        }
    };
}
