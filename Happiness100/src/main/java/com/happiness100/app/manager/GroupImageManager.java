package com.happiness100.app.manager;/**
 * Created by Administrator on 2016/9/23.
 */

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.App;
import com.happiness100.app.model.GroupImageKeyValue;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：jiangsheng on 2016/9/23 16:50
 */
public class GroupImageManager {

    private App mApp;
    private static GroupImageManager instance;
    private Map<String, GroupImageKeyValue> manager;

    private GroupImageManager() {
        manager = new HashMap<String, GroupImageKeyValue>();
    }

    public static GroupImageManager getInstance() {
        if (instance == null) {
            instance = new GroupImageManager();
        }
        return instance;
    }

    private boolean Add(String key, String val) {
        if (key == null || key.isEmpty()) {
            Log.e("GroupImageManager", "Add  key is null");
            return false;
        }

        if (manager.containsKey(key)) {
            Log.w("GroupImageManager", "key is exist, id = " + key);
            return false;
        }
        GroupImageKeyValue iv = new GroupImageKeyValue();
        iv.setUserId(mApp.getUser().getXf() + "");
        iv.setIndex(key);
        iv.setUrl(val);
        manager.put(key, iv);
        iv.save();
        return true;
    }

    public GroupImageKeyValue find(String id) {
        if (id == null || id.isEmpty()) {
            Log.e("GroupImageManager", "find Id is null");
            return new GroupImageKeyValue();
        }

        if (!manager.containsKey(id)) {
            Log.w("GroupImageManager", "find not exist, id = " + id);
            return new GroupImageKeyValue();
        }
        return manager.get(id);
    }

    public boolean update(String key, String val) {
        ;
        if (key == null || key.isEmpty()) {
            Log.e("GroupImageManager", "update is null");
            return false;
        }

        if (!manager.containsKey(key)) {
            Log.w("GroupImageManager", "update not exist, id = " + key);
            Add(key, val);
            return true;
        }
        delete(key);
        Add(key, val);
        return true;
    }

    public boolean delete(String id) {
        if (id == null || id.isEmpty()) {
            Log.e("GroupImageManager", "id is null");
            return false;
        }

        if (!manager.containsKey(id)) {
            Log.w("GroupImageManager", "delete not exist, id = " + id);
            return false;
        }
        new Delete().from(GroupImageKeyValue.class).where("Tag = ? and userId = ?", id, mApp.getUser().getXf() + "").execute();
        manager.remove(id);
        return true;
    }

    public void Init(App app) {
        mApp = app;
        manager.clear();
//        Log.e("Session", mApp.getUser().getSessionid());
        List<GroupImageKeyValue> list = new Select().from(GroupImageKeyValue.class).where("userId = ?", mApp.getUser().getXf() + "").execute();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); ++i) {
                GroupImageKeyValue content = list.get(i);
                manager.put(content.getTag(), content);
            }
        } else {
            new Delete().from(GroupImageKeyValue.class).where("userId = ?", mApp.getUser().getXf() + "").execute();

            Map<String, String> params = new LinkedHashMap<>();
            params.put("sessionid", mApp.getUser().getSessionid());
            APIClient.post(mApp, Constants.URL.GET_GROUP_LIST, params, new BaseVolleyListener(mApp) {
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
                            Gson gson  = new Gson();
                            Log.e("Dddddddddddddddd",json);
                            String[][] res = gson.fromJson(json, String[][].class);
                            for (int i = 0; i < res.length; ++i) {
                                Add(res[i][0], res[i][2]);
                            }
                        }
                    });
                }
            });
        }
    }
}
