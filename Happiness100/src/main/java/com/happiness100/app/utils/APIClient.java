package com.happiness100.app.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.happiness100.app.App;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.activity.BaseActivity;
import com.happiness100.app.ui.activity.WellcomActivity;
import com.justin.api.MD5;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.HTTPUtils;
import com.justin.utils.MD5Utils;
import com.justin.utils.ToastUtils;
import com.justin.utils.VolleyListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class APIClient {

    //通用post方法
    public static void post(Context context, String url, Map<String, String> params, BaseVolleyListener listener) {
        try {
            if (params != null)
                params = getSignParams(params);

            HTTPUtils.post(context, url, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }

    //必带sessionId的 post方法，
    public static void postWithSessionId(Activity context, String url, Map<String, String> params, BaseVolleyListener listener) {
        try {
            params.put("sessionid", ((App) context.getApplication()).getUser().getSessionid());
            params = getSignParams(params);
            HTTPUtils.post(context, url, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }


    //通用post方法
    public static void post(Context context, String url, Map<String, String> header, Map<String, String> params, BaseVolleyListener listener) {
        try {
            params = getSignParams(params);
            HTTPUtils.post(context, url, header, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }

    //通用post方法
    public static void GetRongToken(Context context, User user, BaseVolleyListener listener) {

        String text = "";
        //开发环境
//        String AppKey = "x4vkb1qpvvzzk";
//        String SecretKey ="6hSTAERMqWZhPR";
        //发布环境
        String AppKey = "pvxdm17jxxoor";
        String SecretKey ="9B0oYDIXLbEc";

        int randonInt = (int) (Math.random() * 10000);
        Log.e("IMFragment", "randonInt= " + randonInt);

        long time = System.currentTimeMillis() / 1000;
        Log.e("IMFragment", "time= " + time);
        text = SecretKey + randonInt + time;

        final Map<String, String> header = new LinkedHashMap<>();
        header.put("App-Key", AppKey);

        header.put("Nonce", "" + randonInt);
        header.put("Timestamp", "" + time);

        final String sign = MD5Utils.encryptToSHA(text);
        Log.e("sign", "sign= " + sign);
        header.put("Signature", sign);

        final Map<String, String> params = new LinkedHashMap<>();
        params.put("userId", "" + user.getXf());
        params.put("name", user.getNickname());
        params.put("portraitUri", user.getHeadImageUri());

        try {
            HTTPUtils.post(context, "http://api.cn.ronghub.com/user/getToken.json", header, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }


    //增加家谱成员
    public static void addZupuMember(final BaseActivity context, Map<String, String> params, BaseVolleyListener listener) {

        try {
            params.put("sessionid", ((App) context.getApplication()).getUser().getSessionid());
            params = getSignParams(params);
            HTTPUtils.post(context, Constants.URL.CLAN_PU_ADD_MEMBER, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }

    //获取族谱数据
    public static void getZupuData(final BaseActivity context, Map<String, String> params, BaseVolleyListener listener) {

        try {
            params.put("sessionid", ((App) context.getApplication()).getUser().getSessionid());
            params = getSignParams(params);
            HTTPUtils.post(context, Constants.URL.CLAN_PU_VIEW, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }


    public static void handleResponse(Context context, String json, ResponeInterface responeInterface) {
        try {
            JSONObject responseJO = new JSONObject(json);

            int responseCode = responseJO.getInt("code");

            switch (responseCode) {
                case Constants.NetWork.SUCCESS:
                    if (responseJO.get("data") instanceof JSONObject) {
                        responeInterface.parseResponse(responseJO.getJSONObject("data").toString());
                    } else if (responseJO.get("data") instanceof JSONArray) {
                        responeInterface.parseResponse(responseJO.getJSONArray("data").toString());
                    } else if (responseJO.get("data") instanceof String) {
                        responeInterface.parseResponse("");
                    } else if (responseJO.isNull("data")) {
                        responeInterface.parseResponse(null);
                    } else {
                        responeInterface.parseResponse("");
                        //TODO
                    }
                    break;

                case Constants.NetWork.ALREADY_INVITE:
                    responeInterface.parseResponse(Constants.NetWork.ALREADY_INVITE + "");
                    break;

                case Constants.NetWork.LOGIN_OTHER_DEVICE:
                    try {

                        if (!(context instanceof Application)) {
                            App app = ((BaseActivity) context).mApplication;

                            Intent wellcom = new Intent(context, WellcomActivity.class);
                            wellcom.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            app.clearUserInfo();
                            context.startActivity(wellcom);
                            ToastUtils.shortToast(context, "您的账号在其他设备登录");

                            ((BaseActivity) context).finish();
                        }

                    } catch (Exception e) {

                    }
                    break;
                case 100:
                    responeInterface.parseResponse(responseJO.getString("msg"));
                    break;
                default:
                    ToastUtils.longToast(context, responseJO.getString("msg"));
                    break;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void getVerCode(Context context, Map<String, String> params, VolleyListener listener) {
        try {
            params.put("sessionid", ((App) context.getApplicationContext()).getUser().getSessionid());
            params = getSignParams(params);
            HTTPUtils.post(context, Constants.URL.REQ_VERCODE, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }

    public static void getLoginVerCode(Context context, Map<String, String> params, VolleyListener listener) {
        try {
            params = getSignParams(params);
            HTTPUtils.post(context, Constants.URL.LOGIN_MSG_VERCODE, params, listener);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }


    private APIClient() {
    }

    public static Map<String, String> getSignParams(Map<String, String> params) {

        Collection<String> keyset = params.keySet();
        List<String> list = new ArrayList<String>(keyset);
        Collections.sort(list);
        String paramsStr = "";
        for (int i = 0; i < list.size(); i++) {
            paramsStr += list.get(i) + "=" + params.get(list.get(i)) + "&";
        }
        paramsStr += "appkey=123456";
        paramsStr.trim();
        try {
            paramsStr = URLEncoder.encode(paramsStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        params.put("sign", MD5.digest(paramsStr));
        return params;
    }
}
