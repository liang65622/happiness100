package com.happiness100.app.model;/**
 * Created by Administrator on 2016/8/11.
 */

import com.activeandroid.Model;
import com.google.gson.annotations.Expose;
import com.justin.utils.SharedPreferencesContext;

import java.util.List;

/**
 * 作者：justin on 2016/8/11 11:38
 */
public class User extends Model {

    /**
     * headImage : 7.jpeg
     * nickname : test
     * xf : 7
     * email :
     * sex : 0
     * zone :
     * personSign :
     * sessionid : c70ef200-f3b0-472d-a16e-96f4d911dba1
     * mobile : 18078818796
     */
    @Expose
    private String headImage;
    @Expose
    private String nickname;
    @Expose
    private long xf;
    @Expose
    private String email;
    @Expose
    private int sex;
    @Expose
    private String zone;
    @Expose
    private String personSign;
    @Expose
    private String sessionid;
    @Expose
    private String headImageUri;
    @Expose
    private String mobile;
    @Expose
    private String birthday;
    @Expose
    private boolean pwdIsNull = true;
    //家族列表
    private String password;

    private List<FamilyBaseInfo> familyBaseInfos;

    //默认家族
    private FamilyBaseInfo defaultFamilyBaseInfo;
    private long mDefaultFamilyId;

    public FamilyBaseInfo getDefaultFamilyBaseInfo() {
        return defaultFamilyBaseInfo;
    }
    @Expose
    private int searchMask;

    public int getSearchMask() {
        return searchMask;
    }

    public void setSearchMask(int searchMask) {
        this.searchMask = searchMask;
    }

    public void setDefaultFamilyBaseInfo(FamilyBaseInfo defaultFamilyBaseInfo) {
        SharedPreferencesContext.getInstance().getSharedPreferences().edit().putString("clanId", defaultFamilyBaseInfo.getClanId() + "").commit();
        this.defaultFamilyBaseInfo = defaultFamilyBaseInfo;
    }

    public List<FamilyBaseInfo> getFamilyBaseInfos() {
        return familyBaseInfos;
    }

    public void setFamilyBaseInfos(List<FamilyBaseInfo> familyBaseInfos) {
        if (familyBaseInfos != null) {
            this.familyBaseInfos = familyBaseInfos;
            for (FamilyBaseInfo familyBaseInfo : familyBaseInfos) {
                if (familyBaseInfo.isIsDefault()) {
                    this.defaultFamilyBaseInfo = familyBaseInfo;
                }
            }
        } else {
            this.defaultFamilyBaseInfo = null;
            this.familyBaseInfos = null;
        }
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

    public long getXf() {
        return xf;
    }

    public void setXf(long xf) {
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isPwdIsNull() {
        return pwdIsNull;
    }

    public void setPwdIsNull(boolean pwdIsNull) {
        this.pwdIsNull = pwdIsNull;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String ToString() {
        return "";
    }

    public User() {

    }

    public User(User user) {
        headImage = user.getHeadImage();
        nickname = user.getNickname();
        xf = user.getXf();
        email = user.getEmail();
        sex = user.getSex();
        zone = user.getZone();
        personSign = user.getPersonSign();
        sessionid = user.getSessionid();
        headImageUri = user.getHeadImageUri();
        mobile = user.getMobile();
        birthday = user.getBirthday();
        pwdIsNull = user.isPwdIsNull();
        familyBaseInfos = user.getFamilyBaseInfos();
        defaultFamilyBaseInfo = user.getDefaultFamilyBaseInfo();
        searchMask = user.getSearchMask();
    }

    public String getDefaultClanId() {
        String clanId = SharedPreferencesContext.getInstance().getSharedPreferences().getString("clanId", null);
        if (clanId == null) {
            if (defaultFamilyBaseInfo != null) {
                return defaultFamilyBaseInfo.getClanId() + "";
            } else {
                if (familyBaseInfos != null && familyBaseInfos.size() > 0) {
                    defaultFamilyBaseInfo = familyBaseInfos.get(0);
                    SharedPreferencesContext.getInstance().getSharedPreferences().edit().putString("clanId", defaultFamilyBaseInfo.getClanId() + "").commit();
                    return familyBaseInfos.get(0).getClanId() + "";
                }
            }
        }

        return clanId;
    }

    @Override
    public String toString() {
        return "User{" +
                "headImage='" + headImage + '\'' +
                ", nickname='" + nickname + '\'' +
                ", xf=" + xf +
                ", email='" + email + '\'' +
                ", sex=" + sex +
                ", zone='" + zone + '\'' +
                ", personSign='" + personSign + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", headImageUri='" + headImageUri + '\'' +
                ", mobile='" + mobile + '\'' +
                ", birthday='" + birthday + '\'' +
                ", pwdIsNull=" + pwdIsNull +
                ", familyBaseInfos=" + familyBaseInfos +
                ", defaultFamilyBaseInfo=" + defaultFamilyBaseInfo +
                ", searchMask=" + searchMask +
                '}';
    }

    public void setDefaultFamilyId(long defaultFamilyId) {

        mDefaultFamilyId = defaultFamilyId;
        if (defaultFamilyId != 0) {

            if (familyBaseInfos != null) {
                for (FamilyBaseInfo family : familyBaseInfos) {
                    if (family.getClanId() == defaultFamilyId) {
                        this.defaultFamilyBaseInfo = family;
                    }
                }
            }

        }
    }
}
