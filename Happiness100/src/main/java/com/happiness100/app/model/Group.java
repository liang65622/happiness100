package com.happiness100.app.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table GROUP.
 */
public class Group {

    /**
     * id : 6
     * userId : 58
     * discusId : 12f7b443-abe5-47cf-821d-7e62f6779afc
     * discusName : 群聊
     * ext :
     */

    private int id;
    private int userId;
    private String discusId;
    private String discusName;
    private String ext;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDiscusId() {
        return discusId;
    }

    public void setDiscusId(String discusId) {
        this.discusId = discusId;
    }

    public String getDiscusName() {
        return discusName;
    }

    public void setDiscusName(String discusName) {
        this.discusName = discusName;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
