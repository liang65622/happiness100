package com.happiness100.app.ui.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.happiness100.app.App;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.utils.Constants;
import com.justin.bean.Contacts;
import com.justin.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class BaseActivity extends AppCompatActivity {

    protected BaseActivity mContext;

    public App mApplication;
    LayoutInflater mInflater;
    private Toast mToast;

    User getUser() {
        return mApplication.getUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            try {
                mApplication.setContacts(savedInstanceState.<Contacts>getParcelableArrayList(Constants.CONTACTS));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// 使得音量键控制媒体声音
        mContext = this;
        mApplication = (App) getApplication();
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelableArrayList(Constants.CONTACTS, mApplication.getContacts());

    }

    void handleResponse(String json, ResponeInterface responeInterface) {
        try {
            JSONObject responseJO = new JSONObject(json);
            if (responseJO.getInt("code") == Constants.NetWork.SUCCESS) {
                if (responseJO.get("data") instanceof JSONObject) {
                    responeInterface.parseResponse(responseJO.getJSONObject("data").toString());
                } else if (responseJO.get("data") instanceof String) {
                    responeInterface.parseResponse(responseJO.getString("data"));
                } else if (responseJO.get("data") instanceof Boolean) {
                    if (responseJO.getBoolean("data"))
                        responeInterface.parseResponse(Constants.NetWork.TRUE);
                    else
                        responeInterface.parseResponse(Constants.NetWork.FALSE);
                } else if (responseJO.get("data") instanceof Integer) {
                    responeInterface.parseResponse(responseJO.getInt("data") + "");
                } else {
                    //TODO
                    if (responseJO.get("data") != null) {
                        responeInterface.parseResponse(responseJO.get("data").toString());
                    } else {
                        responeInterface.parseResponse(null);
                    }
                }
            } else if (responseJO.getInt("code") == Constants.NetWork.LOGIN_OTHER_DEVICE) {

            } else {
                ToastUtils.longToast(mContext, responseJO.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "1.1";
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public void startPrivateChat(String targetUserId, String title) {
        if (this != null && !TextUtils.isEmpty(targetUserId)) {
            if (RongContext.getInstance() == null) {
                throw new ExceptionInInitializerError("RongCloud SDK not init");
            } else {
                Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
                this.startActivity(new Intent("android.intent.action.VIEW", uri));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void startPrivateChatForResult(String targetUserId, String title) {
        if (this != null && !TextUtils.isEmpty(targetUserId)) {
            if (RongContext.getInstance() == null) {
                throw new ExceptionInInitializerError("RongCloud SDK not init");
            } else {
                Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
                this.startActivityForResult(new Intent("android.intent.action.VIEW", uri), Constants.ActivityIntentIndex.ConversationActivityIndex);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void startDiscussionChat(String targetDiscussionId, String title) {
        if (this != null && !TextUtils.isEmpty(targetDiscussionId)) {
            Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase()).appendQueryParameter("targetId", targetDiscussionId).appendQueryParameter("title", title).build();
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            this.startActivity(intent);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void startDiscussionChatForResult(String targetDiscussionId, String title) {
        if (this != null && !TextUtils.isEmpty(targetDiscussionId)) {
            Uri uri = Uri.parse("rong://" + this.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase()).appendQueryParameter("targetId", targetDiscussionId).appendQueryParameter("title", title).build();
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            this.startActivityForResult(intent, Constants.ActivityIntentIndex.ConversationActivityIndex);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        Log.e("BaseActivity", "onStop");
        if (!isAppOnForeground()) {
            //app 进入后台
            RongIM.getInstance().disconnect();
            Log.e("BaseActivity", "onStop  isAppOnForeground");
            //全局变量isActive = false 记录当前已经进入后台
            mApplication.setActive(false);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e("BaseActivity", "onResume");

        if (!mApplication.getActive()) {
            //app 从后台唤醒，进入前台
            mApplication.setActive(true);
            Log.e("BaseActivity", "onResume  true");
            reconnect();
            //isActive = true;
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    private void reconnect() {

        final String token = mApplication.getRongCloudToken();
        if (token == null || token.isEmpty()) {
            mApplication.getToken();
            return;
        }

        Log.e("BaseActivity", "BaseActivity reconnect");
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                mApplication.getToken();
                Log.e("BaseActivity", "BaseActivity onTokenIncorrect");
            }

            @Override
            public void onSuccess(String s) {
                Log.e("BaseActivity", "BaseActivity onSuccess:" + s);
                mApplication.setRongCloudToken(token);
                Intent mIntent = new Intent("融云连接成功");
                sendBroadcast(mIntent);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("BaseActivity", "BaseActivity onError:" + errorCode);
            }
        });
    }


    public void showToast(String tips) {
        if (mToast == null) {
            mToast = Toast.makeText(this, tips, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(tips);
        }
        mToast.show();
    }

    public void showToastLong(int tips) {
        if (mToast == null) {
            mToast = Toast.makeText(this, tips, Toast.LENGTH_LONG);
        } else {
            mToast.setText(tips);
        }
        mToast.show();

    }

    public void showToastLong(String tips) {
        if (mToast == null) {
            mToast = Toast.makeText(this, tips, Toast.LENGTH_LONG);
        } else {
            mToast.setText(tips);
        }
        mToast.show();
    }

}

