package com.happiness100.app.message;/**
 * Created by Administrator on 2016/9/2.
 */

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.common.ParcelUtils;
import io.rong.common.RLog;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * 作者：jiangsheng on 2016/9/2 11:17
 */
@MessageTag(value = "CU:Info100", flag = MessageTag.NONE)
public class DynamicMessage extends MessageContent {
    String content;
    public static final Creator<DynamicMessage> CREATOR = new Creator() {
        public DynamicMessage createFromParcel(Parcel source) {
            return new DynamicMessage(source);
        }

        public DynamicMessage[] newArray(int size) {
            return new DynamicMessage[size];
        }
    };

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.putOpt("content", this.content);
        } catch (JSONException var4) {
            RLog.e("DynamicMessage", "JSONException " + var4.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, content);
    }

    public DynamicMessage(Parcel in) {
        this.content = ParcelUtils.readFromParcel(in);
    }

    private DynamicMessage() {
    }
    public static DynamicMessage obtain(String text) {
        DynamicMessage model = new DynamicMessage();
        model.setContent(text);
        return model;
    }

    public DynamicMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException var5) {
            ;
        }
        try {
            JSONObject e = new JSONObject(jsonStr);
            this.setContent(e.optString("content"));
            if(e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }
        } catch (JSONException var4) {
            RLog.e("DynamicMessage", "JSONException " + var4.getMessage());
        }
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

