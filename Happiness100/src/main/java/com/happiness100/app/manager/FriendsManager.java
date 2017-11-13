package com.happiness100.app.manager;/**
 * Created by Administrator on 2016/8/31.
 */

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.App;
import com.happiness100.app.model.Friend;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：jiangsheng on 2016/8/31 16:39
 */
public class FriendsManager {
    private App mApp;
    private static FriendsManager manager;
    private Map<String, Friend> mFriendMap;
    private Map<String, Friend> mFriendMap_ByPhone;

    private FriendsManager() {
        mFriendMap = new HashMap<String, Friend>();
        mFriendMap_ByPhone = new HashMap<String, Friend>();
    }

    public static FriendsManager getInstance() {
        if (manager == null) {
            manager = new FriendsManager();
        }
        return manager;
    }

    public boolean addFriend(Friend friend) {
        String friendId = "" + friend.getXf();
        if (friendId == null || friendId.isEmpty()) {
            Log.e("addFriend", "friendId is null");
            return false;
        }

        if (mFriendMap.containsKey(friendId)) {
            Log.w("addFriend", "friend is exist, id = " + friendId);
            return false;
        }
        friend.setUserId(mApp.getUser().getXf() + "");
        friend.setUserSession(mApp.getUser().getSessionid());
        mFriendMap.put(friendId, friend);
        mFriendMap_ByPhone.put(friend.getMobile(), friend);
        friend.save();
        return true;
    }

    public boolean addFriends(List<Friend> friends) {

        if (friends == null || friends.isEmpty()) {
            Log.e("addFriends", "friendId is null");
            return false;
        }

        ActiveAndroid.beginTransaction();
        try {
            List<Friend> temp = new ArrayList<Friend>();
            for (int i = 0; i < friends.size(); ++i) {
                Friend friend = friends.get(i);
                if (!mFriendMap.containsKey(friend.getXf() + "")) {
                    friend.setUserId(mApp.getUser().getXf() + "");
                    friend.setUserSession(mApp.getUser().getSessionid());
                    temp.add(friend);
                    friend.save();
                }
            }

            for (int i = 0; i < temp.size(); ++i) {
                Friend friend = temp.get(i);
                mFriendMap.put(friend.getXf() + "", friend);
                mFriendMap_ByPhone.put(friend.getMobile(), friend);
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        return true;
    }

    public boolean deleteFriend(String friendId) {
        if (friendId == null || friendId.isEmpty()) {
            Log.e("deleteFriend", "friendId is null");
            return false;
        }

        if (!mFriendMap.containsKey(friendId)) {
            Log.w("deleteFriend", "friend not exist, id = " + friendId);
            return false;
        }
        Friend friend = mFriendMap.get(friendId);
        new Delete().from(Friend.class).where("xf = ? and userSession = ?", friendId, mApp.getUser().getSessionid() + "").execute();
        mFriendMap.remove(friendId);
        mFriendMap_ByPhone.remove(friend.getMobile());
        return true;
    }

    public boolean updateFriend(String friendId, String updateArgs, String value) {
        ;
        if (friendId == null || friendId.isEmpty()) {
            Log.e("updateFriend", "friendId is null");
            return false;
        }
        if (value == null) {
            value = "";
        }
        if (!mFriendMap.containsKey(friendId)) {
            Log.w("updateFriend", "friend not exist, id = " + friendId);
            return false;
        }
        mFriendMap.remove(friendId);
        new Update(Friend.class).set(updateArgs + "=" + value).where("xf = ? and userSession = ?", friendId, mApp.getUser().getSessionid() + "").execute();
        Friend us = new Select().from(Friend.class).where("xf = ? and userSession = ?", friendId, mApp.getUser().getSessionid() + "").executeSingle();
        mFriendMap.put(friendId, us);
        mFriendMap_ByPhone.put(us.getMobile(), us);
        return true;
    }

    public List<Friend> getAllFriends() {
        List<Friend> list = new Select().from(Friend.class).execute();
        return list;
    }


    public boolean updateFriend(Friend friend) {
        ;
        if (friend == null) {
            Log.e("updateFriend", "friend is null");
            return false;
        }

        if (!mFriendMap.containsKey(friend.getXf() + "")) {
            Log.w("updateFriend", "friend not exist, id = " + friend.getXf());
            return false;
        }
        deleteFriend(friend.getXf() + "");
        addFriend(friend);
        return true;
    }


    public Friend findFriend(String friendId) {
        if (friendId == null || friendId.isEmpty()) {
            Log.e("findFriend", "friendId is null");
            return null;
        }

        if (!mFriendMap.containsKey(friendId)) {
            Log.w("findFriend", "friend not exist, id = " + friendId);
            return null;
        }
        return mFriendMap.get(friendId);
    }

    public Friend findFriendByPhone(String PhoneNum) {
        if (PhoneNum == null || PhoneNum.isEmpty()) {
            Log.e("selectFriend", "PhoneNum is null");
            return null;
        }

        if (PhoneNum.length() == 11) {
            PhoneNum = "86 " + PhoneNum;
        }

        if (!mFriendMap_ByPhone.containsKey(PhoneNum)) {
            Log.w("selectFriend", "friend not exist, PhoneNum = " + PhoneNum);
            return null;
        }
        return mFriendMap_ByPhone.get(PhoneNum);
    }

    public Map<String, Friend> findFriends() {
        return mFriendMap;
    }

    public List<Friend> findFriendList() {
        List<Friend> result = new ArrayList<>();

        for (Map.Entry<String, Friend> entry : mFriendMap.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }


    public void Init(App app) {
        mApp = app;
        mFriendMap.clear();
        mFriendMap_ByPhone.clear();
//        Log.e("Session", mApp.getUser().getSessionid());
        List<Friend> list = new Select().from(Friend.class).where("userSession = ?", mApp.getUser().getSessionid()).execute();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); ++i) {
                Friend friend = list.get(i);
                mFriendMap.put(friend.getXf() + "", friend);
                mFriendMap_ByPhone.put(friend.getMobile(), friend);
            }
        } else {
            new Delete().from(Friend.class).where("userId = ?", mApp.getUser().getXf() + "").execute();
            Map<String, String> params = new LinkedHashMap<>();
            params.put("sessionid", mApp.getUser().getSessionid());
            APIClient.post(mApp, Constants.URL.GET_FRIENDS, params, new BaseVolleyListener(mApp) {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    super.onErrorResponse(volleyError);
                }

                @Override
                public void onResponse(String json) {
                    super.onResponse(json);
                    APIClient.handleResponse(mApp, json, new ResponeInterface() {
                        @Override
                        public void parseResponse(String json) {
                            Type type = new TypeToken<ArrayList<Friend>>() {
                            }.getType();
                            List<Friend> friends = GsonUtils.parseJSONArray(json, type);

                            if (friends != null && friends.size() > 0) {
                                addFriends(friends);
                            }
                        }
                    });
                }
            });
        }
    }

    public int getFriendCount() {
        return mFriendMap.size();
    }
}
