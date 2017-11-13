package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/2.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * 作者：jiangsheng on 2016/9/2 09:46
 */
@Table(name = "NotifyMessage")
public class NotifyMessage extends Model implements Serializable{
    @Column(name = "type")
    private String type;
    @Column(name = "status")
    private int status;
    @Column(name = "content")
    private String content;
    @Column(name = "senderId")
    private String senderId;
    @Column(name = "userId")
    private String userId;
    @Column(name = "remark")
    private String remark;
    @Column(name = "senderName")
    private String senderName;
    @Column(name = "image")
    private String image;
    @Column(name = "soleIndex")
    private String soleIndex;//唯一标识
    private String Letters;

    public void setLetters(String Letters) {this.Letters =Letters;}

    public String getLetters() {return Letters;}

    public NotifyMessage()
    {
        soleIndex = System.currentTimeMillis()+"";
    }

    public String getNotifyType() {return type;}

    public void setNotifyType(String type) {this.type =type;}

    public int getStatus() {return status;}

    public void setStatus(int status) {this.status =status;}

    public String getContent() {return content;}

    public void setContent(String content) {this.content =content;}

    public String getSenderId() {return senderId;}

    public void setSenderId(String senderId) {this.senderId =senderId;}

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId =userId;}

    public String getSoleIndex() {return soleIndex;}

    public void setSoleIndex(String soleIndex) {this.soleIndex =soleIndex;}

    public String getSenderName() {return senderName;}

    public void setSenderName(String senderName) {this.senderName =senderName;}

    public String getRemark() {return remark;}

    public void setRemark(String remark) {this.remark =remark;}

    public String getImage() {return image;}

    public void setImage(String image) {this.image =image;}
}
