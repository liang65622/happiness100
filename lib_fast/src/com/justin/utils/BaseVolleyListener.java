package com.justin.utils;/**
 * Created by Administrator on 2016/8/10.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;

/**
 * 作者：justin on 2016/8/10 09:48
 */
public abstract class BaseVolleyListener implements VolleyListener {

    private static final String TAG = "BaseVolleyListener";
    Context context;

    Dialog dialog;

    public BaseVolleyListener(Context context) {
        this.context = context;

    }

    public BaseVolleyListener(Activity context, Dialog dialog) {
        this.context = context;
        this.dialog = dialog;
        dialog.show();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("onErrorResponse", "" + volleyError.getMessage());
        if (dialog != null) {
            dialog.dismiss();
        }
        if (!PhoneStatusUtils.isNetworkConnected(context)) {
            Toast.makeText(context, "网络连接失败,请检查您的网络", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                if (new String(htmlBodyBytes).contains("html")) {
                    Toast.makeText(context, "连接服务器失败！", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.e("BaseVolleyListener", new String(htmlBodyBytes), volleyError);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (volleyError.getMessage() == null) {
            Toast.makeText(context, "连接超时，请重试！", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "连接服务器失败！", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String json) {
        Log.e("onResponse", "" + json);


        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
