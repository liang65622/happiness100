package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/13.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 作者：justin on 2016/9/13 17:41
 */
@Table(name = "MemberImage")
public class MemberImage extends Model {
    @Column(name = "userId")
    private long userId;
    @Column(name = "headImg")
    private String headImg;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {

        this.userId = userId;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
