package com.happiness100.app;/**
 * Created by Administrator on 2016/7/28.
 */

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.android.volley.VolleyError;
import com.happiness100.app.manager.ConversationBackGroudImageManager;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.GroupImageManager;
import com.happiness100.app.manager.RemarkManager;
import com.happiness100.app.manager.RongCloudProviderManager;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.model.HealthData;
import com.happiness100.app.model.User;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.bean.Contacts;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.SharedPreferencesContext;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * 作者：justin on 2016/7/28 10:39
 */
public class App extends Application {

    public static final String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "happiness100"
            + File.separator + "download" + File.separator;
    ;
    private static String PREF_NAME = "creativelocker.pref";
    public static String mTargetID = "47582";
    private User mUser;
    private String RongCloudToken = "";
    private String imgUri = "";
    HealthData healthData;
    private static DisplayImageOptions options;
    private static Context mContext;
    private FamilyIndex mFamilyIndex;
    private boolean mIsActive = true;

    public void setActive(boolean val) {
        mIsActive = val;
    }

    public boolean getActive() {
        return mIsActive;
    }

    ArrayList<Contacts> contacts;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
        }


        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.rp_default_head)
                .showImageOnFail(R.drawable.rp_default_head)
                .showImageOnLoading(R.drawable.rp_default_head)
                .displayer(new FadeInBitmapDisplayer(300))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
//        CrashHandler.getInstance().init(this);
        init();
    }


    private void init() {
//        HTTPUtils.
        mContext = getApplicationContext();
        SharedPreferencesContext.init(this);
        initImageLoader(this);
        ActiveAndroid.initialize(this);
    }

    public static synchronized App context() {
        return (App) mContext;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences getPreferences() {
        SharedPreferences pre = context().getSharedPreferences(PREF_NAME,
                Context.MODE_MULTI_PROCESS);
        return pre;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static SharedPreferences getPreferences(String prefName) {
        return context().getSharedPreferences(prefName,
                Context.MODE_MULTI_PROCESS);
    }


    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


    public void setRongCloudToken(String token) {
        RongCloudToken = token;
    }

    public String getRongCloudToken() {
        if (RongCloudToken == null || RongCloudToken.isEmpty())
            return "";
        return RongCloudToken;
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public void setUser(User user) {
        setLogout(false);
        mUser = user;
        getToken();
        FriendsManager.getInstance().Init(this);
        RemarkManager.getInstance().Init(this);
        RongCloudProviderManager.getInstance().Init(this);
        GroupImageManager.getInstance().Init(this);
        ConversationBackGroudImageManager.getInstance().Init(this);
    }

    public HealthData getHealthData() {
        return healthData;
    }

    public void setHealthData(HealthData healthData) {
        this.healthData = healthData;
    }


    public String getImgUri() {
        if (imgUri == null) {
            SharedPreferences sharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
            String spUri = sharedPreferences.getString("Constants.URL.IMG_URI", null);
            if (spUri != null) {
                setImgUri(spUri);
            }
            return spUri;

        }
        return imgUri;
    }

    public void setImgUri(String imgUri) {
        SharedPreferences.Editor edit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
        edit.putString(Constants.URL.IMG_URI, imgUri);
        edit.commit();
        this.imgUri = imgUri;
    }

    //保存用户信息
    public void saveUserInfo(User user) {
        SharedPreferences.Editor edit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
        edit.putString(Constants.UserInfo.PHONE, user.getMobile());
        String headImgUri = "";
        if (user.getXf() != 0) {
            String xfStr = user.getXf() + "";
            xfStr = xfStr.substring(xfStr.length() - 1, xfStr.length());
            headImgUri = user.getHeadImageUri() + "headImage/" + xfStr + "/" + user.getHeadImage();
        }
        setImgUri(user.getHeadImageUri());
        edit.putString(Constants.UserInfo.HEAD_IMG_URI, headImgUri);
        edit.putString(Constants.UserInfo.HEAD_IMG, user.getHeadImage());
        edit.putString(Constants.UserInfo.PASSWORD, user.getPassword());
        edit.putString(Constants.UserInfo.STATIC_URI, user.getHeadImageUri());
        edit.putString(Constants.UserInfo.NICK_NAME, user.getNickname());
        edit.putLong(Constants.UserInfo.HAPPINESS_NUM, user.getXf());
        edit.putString(Constants.UserInfo.EMAIL, user.getEmail());
        edit.putInt(Constants.UserInfo.SEX, user.getSex());
        edit.putString(Constants.UserInfo.ZONE, user.getZone());
        edit.putString(Constants.UserInfo.PERSON_SIGN, user.getPersonSign());
        edit.putString(Constants.UserInfo.SESSION_ID, user.getSessionid());
        edit.putBoolean(Constants.UserInfo.IS_PWD_NULL, user.isPwdIsNull());
        edit.putInt(Constants.UserInfo.SEARCHMASK, user.getSearchMask());
        getCount:
        edit.commit();
        user.setHeadImageUri(headImgUri);
        setUser(user);
    }


    public User getUser() {
        if (mUser == null || mUser.getMobile() == null) {

            SharedPreferences sharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
            String sessionId = sharedPreferences.getString(Constants.UserInfo.SESSION_ID, "");
            if (sessionId.isEmpty()) {
                return null;
            }
            User user = new User();
            user.setMobile(sharedPreferences.getString(Constants.UserInfo.PHONE, ""));
            user.setHeadImageUri(sharedPreferences.getString(Constants.UserInfo.HEAD_IMG_URI, ""));
            user.setHeadImage(sharedPreferences.getString(Constants.UserInfo.HEAD_IMG, ""));
            user.setNickname(sharedPreferences.getString(Constants.UserInfo.NICK_NAME, ""));
            user.setPassword(sharedPreferences.getString(Constants.UserInfo.PASSWORD, ""));
            user.setXf(sharedPreferences.getLong(Constants.UserInfo.HAPPINESS_NUM, 0l));
            user.setEmail(sharedPreferences.getString(Constants.UserInfo.EMAIL, ""));
            user.setSex(sharedPreferences.getInt(Constants.UserInfo.SEX, 0));
            user.setZone(sharedPreferences.getString(Constants.UserInfo.ZONE, ""));
            user.setPersonSign(sharedPreferences.getString(Constants.UserInfo.PERSON_SIGN, ""));
            user.setSessionid(sharedPreferences.getString(Constants.UserInfo.SESSION_ID, ""));
            user.setPwdIsNull(sharedPreferences.getBoolean(Constants.UserInfo.IS_PWD_NULL, false));
            user.setSearchMask(sharedPreferences.getInt(Constants.UserInfo.SEARCHMASK, 0));
            mUser = user;
            //setUser(user);
            return user;
        }
        return mUser;
    }

    public static DisplayImageOptions getOptions() {
        return options;
    }

    public static void setOptions(DisplayImageOptions options) {
        App.options = options;
    }

    public String getHeadImage(String senderId, String image) {
        senderId = senderId.substring(senderId.length() - 1, senderId.length());
        String headImgUri = getImgUri() + "headImage/" + senderId + "/" + image;
        return headImgUri;
    }

    public String getGroupImage(String DiscusImage) {
        String headImgUri = getImgUri() + "discus_image/" + DiscusImage;
        return headImgUri;
    }

    public void getToken() {

        if (mUser == null) {
            return;
        }

        Log.e("getToken", "~~~getToken~~~~~");
        APIClient.GetRongToken(this, getUser(), new BaseVolleyListener(this) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Log.e("getToken", "Error  " + volleyError.getMessage());
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                Log.e("getToken", "json = " + json);
                JSONObject responseJO = null;
                try {
                    responseJO = new JSONObject(json);
                    String token = responseJO.getString("token");

                    connect(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */

    private void connect(final String token) {
        Log.e("AppConnectR", "--connect");
        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    getToken();
                    Log.e("AppConnectR", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {

                    Log.e("AppConnectR", "--onSuccess" + userid);
                    setRongCloudToken(token);
                    Intent mIntent = new Intent("融云连接成功");
                    //发送广播
                    sendBroadcast(mIntent);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.e("AppConnectR", "--onError" + errorCode);
                }
            });
        }
    }

    public void logout() {

        //clearUserInfo();
        setLogout(true);
        User user = new User();
        mUser = user;
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().logout();
        }
    }

    public void setLogout(boolean val) {
        SharedPreferences.Editor edit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
        edit.putBoolean(Constants.UserInfo.LOGOUT, val);
        edit.commit();
    }

    //保存用户信息
    public void clearUserInfo() {
        User user = new User();
        SharedPreferences.Editor edit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
        edit.putString(Constants.UserInfo.PHONE, user.getMobile());
        String headImgUri = "";
        if (user.getXf() != 0) {
            String xfStr = user.getXf() + "";
            xfStr = xfStr.substring(xfStr.length() - 1, xfStr.length());
            headImgUri = user.getHeadImageUri() + "headImage/" + xfStr + "/" + user.getHeadImage();
        }
        setImgUri(user.getHeadImageUri());
        edit.putString(Constants.UserInfo.HEAD_IMG_URI, headImgUri);
        edit.putString(Constants.UserInfo.STATIC_URI, user.getHeadImageUri());
        edit.putString(Constants.UserInfo.NICK_NAME, user.getNickname());
        edit.putLong(Constants.UserInfo.HAPPINESS_NUM, user.getXf());
        edit.putString(Constants.UserInfo.EMAIL, user.getEmail());
        edit.putInt(Constants.UserInfo.SEX, user.getSex());
        edit.putString(Constants.UserInfo.ZONE, user.getZone());
        edit.putString(Constants.UserInfo.PERSON_SIGN, user.getPersonSign());
        edit.putString(Constants.UserInfo.SESSION_ID, user.getSessionid());
        edit.putBoolean(Constants.UserInfo.IS_PWD_NULL, user.isPwdIsNull());
        edit.commit();
        String mobile = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.UserInfo.PHONE, "");
        String a = "D";
    }

    public FamilyIndex getFamilyIndex() {
        return mFamilyIndex;
    }

    public void setFamilyIndex(FamilyIndex familyIndex) {
        mFamilyIndex = familyIndex;
    }

    boolean mIsLogout = false;

    public void setIsLogout(boolean isLogout) {
        mIsLogout = isLogout;
    }

    public boolean isLogout() {
        return mIsLogout;
    }

    public boolean getSpBoolean(String key) {
        return SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(key, false);
    }

    public String getSpString(String key) {
        return SharedPreferencesContext.getInstance().getSharedPreferences().getString(key, null);
    }

    public int getSpInt(String key) {
        return SharedPreferencesContext.getInstance().getSharedPreferences().getInt(key, -1);
    }

    public void setSpInt(String key, int value) {
        SharedPreferencesContext.getInstance().getSharedPreferences().edit().putInt(key, value).commit();
    }

    public void setSpBoolean(String key, boolean value) {
        SharedPreferencesContext.getInstance().getSharedPreferences().edit().putBoolean(key, value).commit();
    }

    public ArrayList<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contacts> contactsStr) {
        contacts = contactsStr;
    }

    ArrayList<String> mMembersIds;

    public ArrayList<String> getMembersIds() {
        return mMembersIds;
    }

    public void setMembersIds(ArrayList<String> membersIds) {
        mMembersIds = membersIds;
    }
}
