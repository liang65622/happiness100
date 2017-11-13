package com.happiness100.app.manager;/**
 * Created by Administrator on 2016/9/13.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.VolleyError;
import com.happiness100.app.App;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.Remark;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.GroupUserInfo;
import io.rong.imlib.model.UserInfo;

/**
 * 作者：jiangsheng on 2016/9/13 09:41
 */
public class RongCloudProviderManager implements RongIM.UserInfoProvider ,RongIM.GroupUserInfoProvider,RongIM.LocationProvider{
    static RongCloudProviderManager mInstance;
    App mApp;
    private RongCloudProviderManager() {};
    static public RongCloudProviderManager getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new RongCloudProviderManager();
        }
        return mInstance;
    }
    public void Init(App app)
    {
        mApp = app;
        RongIM.setUserInfoProvider(this, true);
        RongIM.setGroupUserInfoProvider(this,true);
        RongIM.setLocationProvider(this);
    }
    @Override
    public UserInfo getUserInfo(String s) {
        if(mApp == null)
        {
            return null;
        }
        Friend friend = FriendsManager.getInstance().findFriend(s);
        if (friend != null)
        {
            Remark remark = RemarkManager.getInstance().findRemark(friend.getXf()+"");
            String NoteName=  remark.getNoteName();
            if (NoteName == null ||NoteName.isEmpty())
            {
                NoteName = friend.getNickname();
            }
            Log.e("ImageUri","s = "+s+",ImageUri = "+mApp.getHeadImage(friend.getXf()+"",friend.getHeadImage()));
            return new UserInfo(""+friend.getXf()+"",NoteName, Uri.parse(mApp.getHeadImage(friend.getXf()+"",friend.getHeadImage())));
        }
        else
        {
            User user =  mApp.getUser();
            if (s.compareTo(user.getXf()+"") == 0)
            {
                Log.e("ImageUri","s = "+s+",ImageUri1 = "+mApp.getHeadImage(user.getXf()+"",user.getHeadImage()) + "   " + user.getHeadImage());
                return new UserInfo(user.getXf()+"",user.getNickname(),Uri.parse(mApp.getHeadImage(user.getXf()+"",user.getHeadImage())));
            }

            postGetUserInfo(s,null);
            return null;
        }
    }

    @Override
    public GroupUserInfo getGroupUserInfo(String s, String s1) {
        if(mApp == null)
        {
            return null;
        }

        Friend friend = FriendsManager.getInstance().findFriend(s);
        if (friend != null)
        {
            Remark remark = RemarkManager.getInstance().findRemark(friend.getXf()+"");
            String NoteName=  remark.getNoteName();
            if (NoteName == null ||NoteName.isEmpty())
            {
                NoteName = friend.getNickname();
            }
            return new GroupUserInfo(s,""+friend.getXf()+"",NoteName);
        }
        else
        {
            User user =  mApp.getUser();
            if (s1.compareTo(user.getXf()+"") == 0)
            {
                return new GroupUserInfo(s,user.getXf()+"",user.getNickname());
            }
            postGetUserInfo(s1,s);
            return null;
        }
    }

    void postGetUserInfo(String UserId,final String  groupId)
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApp.getUser().getSessionid());
        params.put("friend_user_id",UserId);
        APIClient.post(mApp, Constants.URL.INFO_FRIENDS, params, new BaseVolleyListener(mApp) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }
            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mApp,json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Friend friend = GsonUtils.parseJSON(json,Friend.class);
                        if(friend!=null) {
                            Remark remark = RemarkManager.getInstance().findRemark(friend.getXf()+"");
                            String NoteName=  remark.getNoteName();
                            if (NoteName == null ||NoteName.isEmpty())
                            {
                                NoteName = friend.getNickname();
                            }
                            if(groupId == null)
                            {
                                RongIM.getInstance().refreshUserInfoCache(new UserInfo("" + friend.getXf() + "", NoteName, Uri.parse(mApp.getHeadImage(friend.getXf() + "", friend.getHeadImage()))));
                            }
                            else
                            {
                                RongIM.getInstance().refreshGroupUserInfoCache(new GroupUserInfo(groupId,"" + friend.getXf() + "", NoteName));
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
        ToastUtils.shortToast(context,"功能暂未开放");
    }
}
