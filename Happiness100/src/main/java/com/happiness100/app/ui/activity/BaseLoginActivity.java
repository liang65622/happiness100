package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.activeandroid.util.Log;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.manager.RongCloudEventManager;
import com.happiness100.app.model.HealthData;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.bean.Contacts;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.PhoneUtils;
import com.justin.utils.SharedPreferencesContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 作者：justin on 2016/8/17 12:03
 */
public class BaseLoginActivity extends BaseActivity {

    private Dialog mDialog;

    protected void initHealthData(final BaseActivity context) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("mobile", mApplication.getUser().getMobile());
        mDialog = new LoadDialog(context, false, "");
        mDialog.show();
        mContext = context;
        APIClient.post(context, Constants.URL.EALTH_INDEX, params, new BaseVolleyListener(context) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                mDialog.dismiss();
                startActivity(new Intent(context, MainActivity.class));
                context.finish();
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(context, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Gson gson = new Gson();
//                        HealthData healthData = GsonUtils.parseJSON(json, HealthData.class);
                        HealthData healthData = gson.fromJson(json, HealthData.class);
                        mApplication.setHealthData(healthData);
                        RongCloudEventManager.getInstance().Init(mApplication);
                        checkUpdate(context);
                    }
                });
            }
        });
    }

    void checkUpdate(final Activity context) {
        //检查
        APIClient.post(mContext, Constants.URL.APP_UPDATE, null, new BaseVolleyListener(context) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                mDialog.dismiss();
                startActivity(new Intent(context, MainActivity.class));
                context.finish();
            }
            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                String[] strs = json.split("\\|");
                Log.e("TAG", strs[0]);
                Log.e("TAG", strs[1]);
                if (strs[0] != null && !strs[0].isEmpty()) {
                    String currentVersion = getVersionName();
                    float versionCode = Float.parseFloat(currentVersion);
                    float serverVersion = Float.parseFloat(strs[0]);
                    if (versionCode <serverVersion) {
                        SharedPreferencesContext.getInstance().getSharedPreferences().edit().putBoolean(Constants.SpKey.HAS_NEW_VERSION, true).commit();
                        SharedPreferencesContext.getInstance().getSharedPreferences().edit().putString(Constants.SpKey.VERSION_APP_URL, strs[1]).commit();
                    } else {
                        SharedPreferencesContext.getInstance().getSharedPreferences().edit().putBoolean(Constants.SpKey.HAS_NEW_VERSION, false).commit();
                    }
                }

                new ReadContacts().execute();
            }
        });
    }

    class ReadContacts extends AsyncTask<Context, Integer, ArrayList<Contacts>> {
        @Override
        protected ArrayList<Contacts> doInBackground(Context[] params) {
            ArrayList<Contacts> list = PhoneUtils.getAllContacts(mContext);
            return list;
        }
        @Override
        protected void onPostExecute(ArrayList<Contacts> contactsStr) {
            super.onPostExecute(contactsStr);
            mDialog.dismiss();
            startActivity(new Intent(mContext, MainActivity.class));
            mContext.finish();
            mApplication.setContacts(contactsStr);
        }
    }


}
