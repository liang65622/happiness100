package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/18.
 */

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 作者：justin on 2016/9/18 14:35
 */
public class LoadingActivity extends BaseLoginActivity implements BaseViewInterface {
    private static final String TAG = "LoadingActivity";
    private boolean mLogin;
    private User mUser;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mUser = mApplication.getUser();
        mDialog = new LoadDialog(mContext, false, "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }, 1000);

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        //判断是否退出登录
        if (isAutoLogin()) {
            login(mUser.getMobile(), mUser.getPassword());
        } else {
            startActivity(new Intent(mContext, WellcomActivity.class));
            mContext.finish();
        }
    }

    private void login(String phone, final String password) {

        Map<String, String> params = new LinkedHashMap<>();

        APIClient.postWithSessionId(mContext, Constants.URL.LOGIN_AUTO, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                startActivity(new Intent(mContext, WellcomActivity.class));
            }

            @Override
            public void onResponse(String s) {
                super.onResponse(s);
                mDialog.dismiss();
                try {
                    JSONObject responseJO = new JSONObject(s);
                    if (responseJO.getInt("code") != Constants.NetWork.SUCCESS) {
                        startActivity(new Intent(mContext, WellcomActivity.class));
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    startActivity(new Intent(mContext, WellcomActivity.class));
                    return;
                }

                handleResponse(s, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        User user = null;
                        if (Build.VERSION.SDK_INT == 23) {
                            user = GsonUtils.parseJSON(json, User.class);
                        } else {
                            Gson gson = new Gson();
                            user = gson.fromJson(json, User.class);
                            user.setPassword(password);
                        }

                        mApplication.saveUserInfo(user);
                        initHealthData(mContext);
                    }
                });
            }
        });
    }

    public boolean isAutoLogin() {
        if (mUser == null || mUser.getMobile() == null) {

            return false;
        }
        if (SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.UserInfo.LOGOUT, false))
            return false;
        else
            return true;
    }
}
