package com.happiness100.app.manager;/**
 * Created by Administrator on 2016/9/14.
 */

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.App;
import com.happiness100.app.model.Remark;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：jiangsheng on 2016/9/14 15:07
 */
public class RemarkManager {
    private App mApp;
    private static RemarkManager manager;
    private Map<String, Remark> mRemarkMap;

    private RemarkManager() {
        mRemarkMap = new HashMap<String, Remark>();
    }

    public static RemarkManager getInstance() {
        if (manager == null) {
            manager = new RemarkManager();
        }
        return manager;
    }

    public boolean addRemark(Remark remark) {
        String id = "" + remark.getXf();
        if (id == null || id.isEmpty()) {
            Log.e("addRemark", "Id is null");
            return false;
        }

        if (mRemarkMap.containsKey(id)) {
            Log.w("addRemark", "remark is exist, id = " + id);
            return false;
        }
        remark.setUserId(mApp.getUser().getXf() + "");
        mRemarkMap.put(id, remark);
        remark.save();
        return true;
    }

    public boolean addRemarks(List<Remark> remarks) {

        if (remarks == null || remarks.isEmpty()) {
            Log.e("addRemarks", "addRemarks remarks is null");
            return false;
        }

        ActiveAndroid.beginTransaction();
        try {
            List<Remark> temp = new ArrayList<Remark>();
            for (int i = 0; i < remarks.size(); ++i) {
                Remark remark = remarks.get(i);
                if (!mRemarkMap.containsKey(remark.getXf() + "")) {
                    remark.setUserId(mApp.getUser().getXf() + "");
                    temp.add(remark);
                    remark.save();
                }
            }

            for (int i = 0; i < temp.size(); ++i) {
                Remark remark = temp.get(i);
                mRemarkMap.put(remark.getXf() + "", remark);
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        return true;
    }

    public boolean deleteRemark(String id) {
        if (id == null || id.isEmpty()) {
            Log.e("deleteRemark", "id is null");
            return false;
        }

        if (!mRemarkMap.containsKey(id)) {
            Log.w("deleteRemark", "remark not exist, id = " + id);
            return false;
        }
        new Delete().from(Remark.class).where("xf = ? and userId = ?", id, mApp.getUser().getXf() + "").execute();
        mRemarkMap.remove(id);
        return true;
    }

    public boolean updateRemark(String id, String updateArgs, String value) {
        ;
        if (id == null || id.isEmpty()) {
            Log.e("updateRemark", "id is null");
            return false;
        }
        if (value == null) {
            value = "";
        }
        if (!mRemarkMap.containsKey(id)) {
            Log.w("updateRemark", "Remark not exist, id = " + id);
            return false;
        }
        mRemarkMap.remove(id);
        new Update(Remark.class).set(updateArgs + "=" + value).where("xf = ? and userId = ?", id, mApp.getUser().getXf() + "").execute();
        Remark us = new Select().from(Remark.class).where("xf = ? and userId = ?", id, mApp.getUser().getXf() + "").executeSingle();
        mRemarkMap.put(id, us);
        return true;
    }

    public boolean updateRemark(Remark remark) {
        ;
        if (remark == null) {
            Log.e("updateRemark", "remark is null");
            return false;
        }

        if (!mRemarkMap.containsKey(remark.getXf() + "")) {
            Log.w("updateRemark", "remark not exist, id = " + remark.getXf());
            addRemark(remark);
            return true;
        }
        deleteRemark(remark.getXf() + "");
        addRemark(remark);
        return true;
    }


    public Remark findRemark(String id) {
        if (id == null || id.isEmpty()) {
            Log.e("findRemark", "Id is null");
            return new Remark();
        }

        if (!mRemarkMap.containsKey(id)) {
            Log.w("findRemark", "Remark not exist, id = " + id);
            return new Remark();
        }
        return mRemarkMap.get(id);
    }

    public void Init(App app) {
        mApp = app;
        mRemarkMap.clear();
        List<Remark> list = new Select().from(Remark.class).where("userId = ?", mApp.getUser().getSessionid()).execute();

        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); ++i) {
                Remark remark = list.get(i);
                mRemarkMap.put(remark.getXf() + "", remark);
            }
        } else {
            new Delete().from(Remark.class).where("userId = ?", mApp.getUser().getXf() + "").execute();
            Map<String, String> params = new LinkedHashMap<>();
            params.put("sessionid", mApp.getUser().getSessionid());
            APIClient.post(mApp, Constants.URL.USER_NOTE, params, new BaseVolleyListener(mApp) {
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
                            Gson gson = new Gson();
                            String[][] array = gson.fromJson(json, String[][].class);
                            if (array != null && array.length> 0)
                            {
                                List<Remark> remarks = new ArrayList<Remark>();
                                for (int i = 0; i< array.length;++i)
                                {
                                    String[] Info = array[i];
                                    Remark remark = new Remark();
                                    remark.setUserId(mApp.getUser().getXf()+"");
                                    remark.setXf(Integer.parseInt(Info[0]));
                                    remark.setNoteName(Info[1] == null ?"":Info[1]);
                                    remark.setMobiles(Info[2] == null?"":Info[2]);
                                    remarks.add(remark);
                                }
                                addRemarks(remarks);
                            }
                        }
                    });
                }
            });
        }
    }
}

