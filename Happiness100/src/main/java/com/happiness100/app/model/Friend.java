package com.happiness100.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

/**
 * 作者：jiangsheng on 2016/9/1 15:06
 */

@Table(name = "Friend")
public class Friend extends Model implements Parcelable{

    /**
     * headImage :
     * nickname : ddd
     * xf : 14
     * email :
     * sex : 0
     * zone :
     * personSign :
     * sessionid : f394bf09-8762-427a-97e7-491681ecc879
     * headImageUri : http://127.0.0.1:8887/xf100_static/
     * mobile : 111
     * pwdIsNull : true
     * birthday : null
     * friendId : 1
     * friendCard :
     * friendTag :
     */
    @Column(name = "headImage") @Expose
    private String headImage;
    @Column(name = "nickname") @Expose
    private String nickname;
    @Column(name = "xf") @Expose
    private int xf;
    @Column(name = "email") @Expose
    private String email;
    @Column(name = "sex") @Expose
    private int sex;
    @Column(name = "zone") @Expose
    private String zone;
    @Column(name = "personSign") @Expose
    private String personSign;
    @Column(name = "sessionid") @Expose
    private String sessionid;
    @Column(name = "headImageUri") @Expose
    private String headImageUri;
    @Column(name = "mobile") @Expose
    private String mobile;
    @Column(name = "pwdIsNull") @Expose
    private boolean pwdIsNull = true;
    @Column(name = "birthday") @Expose
    private String birthday;
    @Column(name = "friendCard") @Expose
    private String friendCard;
    @Column(name = "friendTag") @Expose
    private String friendTag;
    @Column(name = "userId") @Expose
    private String userId;
    @Column(name = "userSession") @Expose
    private String userSession;
    private String letters;

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSession() {
        return userSession;
    }

    public void setUserSession(String userSession) {
        this.userSession = userSession;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getXf() {
        return xf;
    }

    public void setXf(int xf) {
        this.xf = xf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPersonSign() {
        return personSign;
    }

    public void setPersonSign(String personSign) {
        this.personSign = personSign;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getHeadImageUri() {
        return headImageUri;
    }

    public void setHeadImageUri(String headImageUri) {
        this.headImageUri = headImageUri;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isPwdIsNull() {
        return pwdIsNull;
    }

    public void setPwdIsNull(boolean pwdIsNull) {
        this.pwdIsNull = pwdIsNull;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFriendCard() {
        return friendCard;
    }

    public void setFriendCard(String friendCard) {
        this.friendCard = friendCard;
    }

    public String getFriendTag() {
        return friendTag;
    }

    public void setFriendTag(String friendTag) {
        this.friendTag = friendTag;
    }



    public Friend() {
    }

    protected Friend(Parcel in) {
        this.headImage = in.readString();
        this.nickname = in.readString();
        this.xf = in.readInt();
        this.email = in.readString();
        this.sex = in.readInt();
        this.zone = in.readString();
        this.personSign = in.readString();
        this.sessionid = in.readString();
        this.headImageUri = in.readString();
        this.mobile = in.readString();
        this.pwdIsNull = (in.readByte() == 0 ? false : true);;
        this.birthday = in.readString();
        this.friendCard = in.readString();
        this.friendTag = in.readString();
        this.userId = in.readString();
        this.userSession = in.readString();
        this.letters = in.readString();
    }

    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel source) {
            return new Friend(source);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.headImage);
        dest.writeString(this.nickname);
        dest.writeInt(this.xf);
        dest.writeString(this.email);
        dest.writeInt(this.sex);
        dest.writeString(this.zone);
        dest.writeString(this.personSign);
        dest.writeString(this.sessionid);
        dest.writeString(this.headImageUri);
        dest.writeString(this.mobile);
        dest.writeByte(this.pwdIsNull? (byte)1 : (byte)0);
        dest.writeString(this.birthday);
        dest.writeString(this.friendCard);
        dest.writeString(this.friendTag);
        dest.writeString(this.userId);
        dest.writeString(this.userSession);
        dest.writeString(this.letters);
    }
}
