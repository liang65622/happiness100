package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/24.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 作者：jiangsheng on 2016/9/24 10:48
 */

@Table(name = "ConversationBackGroudImage")
public class ConversationBackGroudImage extends Model {
    @Column(name = "userId")
    String userId;
    @Column(name = "Tag")
    String Tag;
    @Column(name = "data")
    String Imagedata;

    public String getTag() {
        return Tag;
    }

    public void setIndex(String Tag) {
        this.Tag = Tag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageData() {
        return Imagedata;
    }

    public void setImageData(String Imagedata) {
        this.Imagedata = Imagedata;
    }
}
