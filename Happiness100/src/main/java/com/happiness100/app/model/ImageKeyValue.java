package com.happiness100.app.model;/**
 * Created by Administrator on 2016/9/23.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * 作者：jiangsheng on 2016/9/23 16:42
 */
@Table(name = "ImageKeyValue")
public class ImageKeyValue extends Model{
    @Column(name = "userId")
    String userId;
    @Column(name = "Tag")
    String Tag;
    @Column(name = "Url")
    String Url;

    public String getTag() {
        return Tag;
    }

    public void setIndex(String Tag) {
        this.Tag = Tag;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
