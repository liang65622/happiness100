package com.happiness100.app.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.NotifyMessage;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.ClanRoleEnum;
import com.happiness100.app.ui.bean.MainTab;
import com.happiness100.app.ui.fragment.FamilyFragment;
import com.happiness100.app.ui.fragment.IMFragment;
import com.happiness100.app.ui.fragment.MineFragment;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.MyFragmentTabHost;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class MainActivity extends BaseActivity implements View.OnTouchListener, BaseViewInterface, TabHost.OnTabChangeListener {

    private static final String TAG = "MainActivity";
    @Bind(android.R.id.tabhost)
    public MyFragmentTabHost mTabHost;
    @Bind(R.id.apply_tips)
    ImageView mIvApplyTips;
    private boolean firstLoad = true;
    private BroadcastReceiver mBroadcastReceiver;
    boolean mIsBackTime = false;
    @Bind(R.id.iv_family)
    ImageView ivFamily;

    @Bind(R.id.tv_tab_family)
    TextView tvTabFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppBaseTheme_Light);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        initData();
        checkUpdate();

        int openidx = getIntent().getIntExtra("openIdx",-1);
        if (openidx != -1)
        {
            mTabHost.setCurrentTab(openidx);
        }
        //getToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
        freshFamilyApplyTips();
//        if (firstLoad) {
//            mTabHost.setCurrentTab(2);
//            firstLoad = false;
//        }
        checkWarn();
    }

    @Override
    public void initView() {

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }
        initTabs();

        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(this);

    }

    //检查更新
    private void checkUpdate() {

        if (!SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.SpKey.HAS_NEW_VERSION, false)) {
            return;
        }
        View MineView = mTabHost.getTabWidget().getChildAt(4);
        final ImageView warn = (ImageView) MineView.findViewById(R.id.warn);
        warn.setVisibility(View.VISIBLE);
        final File file = new File(Uri.fromFile(Environment.getExternalStorageDirectory()).buildUpon().build().getPath());
        DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "提示", "有新的版本，是否现在下载？", new DialogWithYesOrNoUtils.DialogCallBack() {
            @Override
            public void exectEvent() {
                String downloadUrl = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.SpKey.VERSION_APP_URL, "");
                downloadUrl = downloadUrl.replaceAll("\n", "");
                if (!downloadUrl.isEmpty()) {
//                    DownloadFile.upgrade(mContext, downloadUrl, file.getAbsolutePath(), MainActivity.class);
//                    UIHelper.openDownLoadService(mContext, mUpdate.getUpdate().getAndroid().getDownloadUrl(), mUpdate.getUpdate().getAndroid().getVersionName());
//                    UpdateService.Builder.create(downloadUrl)
//                            .setStoreDir("update/flag")
//                            .setDownloadSuccessNotificationFlag(Notification.DEFAULT_ALL)
//                            .setDownloadErrorNotificationFlag(Notification.DEFAULT_ALL)
//                            .build(mContext);
                    Uri uri = Uri.parse(downloadUrl);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                    mApplication.setSpBoolean(Constants.SpKey.HAS_NEW_VERSION, false);
                    warn.setVisibility(View.GONE);
                }
            }

            @Override
            public void exectEditEvent(String editText) {
            }

            @Override
            public void updatePassword(String oldPassword, String newPassword) {
            }
        });
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                new UpdateManager(MainActivity.this, false).checkUpdate();
//            }
//        }, 2000);
    }


    void SetUnReadMessageListener() {
        RongIM.getInstance().setOnReceiveUnreadCountChangedListener(new RongIM.OnReceiveUnreadCountChangedListener() {
            @Override
            public void onMessageIncreased(int i) {
                if (mApplication.getUser() == null) {
                    return;
                }
                View MineView = mTabHost.getTabWidget().getChildAt(1);
                ImageView warn = (ImageView) MineView.findViewById(R.id.warn);
                if (i > 0) {
                    warn.setVisibility(View.VISIBLE);
                } else {
                    List<NotifyMessage> tempList = new Select().from(NotifyMessage.class).where("UserId = ?", mApplication.getUser().getXf() + "").execute();
                    for (int j = tempList.size() - 1; j >= 0; --j) {
                        if (tempList.get(j).getStatus() == 0) {
                            warn.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    warn.setVisibility(View.GONE);
                }
            }
        }, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.SYSTEM);
    }

    @Override
    public void initData() {
        initFamilyList();
        registerBroadcast();
        checkWarn();
    }

    void checkWarn() {
        PassWordWarn();
        NewMessageWarn();
    }

    void NewMessageWarn() {
        RongIM.getInstance().getTotalUnreadCount(new RongIMClient.ResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                View MineView = mTabHost.getTabWidget().getChildAt(1);
                ImageView warn = (ImageView) MineView.findViewById(R.id.warn);

                if (integer.intValue() > 0) {
                    warn.setVisibility(View.VISIBLE);
                } else {
                    List<NotifyMessage> tempList = new Select().from(NotifyMessage.class).where("UserId = ?", mApplication.getUser().getXf() + "").execute();
                    for (int i = tempList.size() - 1; i >= 0; --i) {
                        if (tempList.get(i).getStatus() == 0) {
                            warn.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                    warn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                View MineView = mTabHost.getTabWidget().getChildAt(1);
                ImageView warn = (ImageView) MineView.findViewById(R.id.warn);
                List<NotifyMessage> tempList = new Select().from(NotifyMessage.class).where("UserId = ?", mApplication.getUser().getXf() + "").execute();
                for (int i = tempList.size() - 1; i >= 0; --i) {
                    if (tempList.get(i).getStatus() == 0) {
                        warn.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                warn.setVisibility(View.GONE);
            }
        });
    }

    void PassWordWarn() {
        User user = mApplication.getUser();
        if (user == null) {
            return;
        }
        View MineView = mTabHost.getTabWidget().getChildAt(4);
        ImageView warn = (ImageView) MineView.findViewById(R.id.warn);
        if (!SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.SpKey.HAS_NEW_VERSION, false)) {
            warn.setVisibility(user.isPwdIsNull() ? View.VISIBLE : View.GONE);
        }
    }


    void registerBroadcast() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("新的消息");
        myIntentFilter.addAction("融云连接成功");
        myIntentFilter.addAction("com.happniess100.app.applymsg");
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (mApplication.getUser() == null) {
                    return;
                }

                String action = intent.getAction();
                if (action.equals("新的消息")) {
                    IMFragment imf = (IMFragment) getSupportFragmentManager().findFragmentByTag(MainTab.MESSAGE.getIdx() + "");
                    if (imf != null) {
                        imf.checkWarn();
                    }
                    NewMessageWarn();
                } else if (action.equals("融云连接成功")) {
                    NewMessageWarn();
                    SetUnReadMessageListener();
                } else if (action.equals("com.happniess100.app.applymsg")) {
                    freshFamilyApplyTips();
                }
            }
        };
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    private void freshFamilyApplyTips() {
//        View MineView = mTabHost.getTabWidget().getChildAt(2);
        boolean hasApplyTips = SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.SpKey.HAS_APPLY_TIPS, false);

        FamilyBaseInfo familyBaseInfo = getUser().getDefaultFamilyBaseInfo();
        if (familyBaseInfo != null && familyBaseInfo.getClanRole() != ClanRoleEnum.NORMAL.getCode()) {
            if (hasApplyTips) {
                mIvApplyTips.setVisibility(View.VISIBLE);
            } else {
                mIvApplyTips.setVisibility(View.GONE);
            }
            FamilyFragment ff = (FamilyFragment) getSupportFragmentManager().findFragmentByTag(getResources().getString(MainTab.FAMILY.getResName()));
            if (ff != null) {
                ff.onResume();
            }
        }
    }


    private void initFamilyList() {
        Map<String, String> params = new HashMap<>();
        APIClient.postWithSessionId(mContext, Constants.URL.GET_CLIAN_LIST, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Log.e(TAG, "onErrorResponse");
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Log.e(TAG, "initFamilyList parsRespone:" + json);
                        Type type = new TypeToken<ArrayList<FamilyBaseInfo>>() {
                        }.getType();
                        List<FamilyBaseInfo> familyBaseInfos = GsonUtils.parseJSONArray(json, type);

                        if (familyBaseInfos == null || familyBaseInfos.size() == 0) {//TODO

//                            FamilyFragment familyFragment = (FamilyFragment) getSupportFragmentManager().getFragments().get(0);
//                            familyFragment.
                        } else {
                            mApplication.getUser().setFamilyBaseInfos(familyBaseInfos);
                            for (FamilyBaseInfo familyBaseInfo : familyBaseInfos) {
                                if (familyBaseInfo.isIsDefault() == true) {
                                    mApplication.getUser().setDefaultFamilyBaseInfo(familyBaseInfo);
                                    SharedPreferencesContext.getInstance().getSharedPreferences().edit().putString("clanId", familyBaseInfo.getClanId() + "").commit();
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    private void initTabs() {
        MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];
            TabHost.TabSpec tab = mTabHost.newTabSpec(getString(mainTab.getResName()) + i);
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab_title);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
                    null);
            if (i == 2) {
                indicator.setVisibility(View.INVISIBLE);
//                mTabHost.setNoTabChangedTag(getString(mainTab.getResName()));
            }
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            mTabHost.addTab(tab, mainTab.getClz(), null);
            if (mainTab.equals(MainTab.ME)) {
                View cn = indicator.findViewById(R.id.tab_mes);
//                mBvNotice = new BadgeView(MainActivity.this, cn);
//                mBvNotice.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//                mBvNotice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//                mBvNotice.setBackgroundResource(R.drawable.notification_bg);
//                mBvNotice.setGravity(Gravity.CENTER);
            }

            mTabHost.setCurrentTab(2);
            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onTabChanged(String s) {
        if (!s.equals(MainTab.FAMILY.name())) {
            ivFamily.setImageDrawable(getResources().getDrawable(R.drawable.tab_family_normal));
            tvTabFamily.setTextColor(getResources().getColor(R.color.tab_family_tab_pressed));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, requestCode + "");
        if (resultCode == Activity.RESULT_OK) {
            FamilyFragment ff = (FamilyFragment) getSupportFragmentManager().findFragmentByTag(MainTab.FAMILY.getIdx() + "");
            if (ff != null) {
                ff.onActivityResult(requestCode, resultCode, data);
            }

            MineFragment mf = (MineFragment) getSupportFragmentManager().findFragmentByTag(MainTab.ME.getIdx() + "");
            if (mf != null) {
                mf.onActivityResult(requestCode, resultCode, data);
            }

            IMFragment imf = (IMFragment) getSupportFragmentManager().findFragmentByTag(MainTab.MESSAGE.getIdx() + "");
            if (imf != null) {
                imf.onActivityResult(requestCode, resultCode, data);
            }
            switch (requestCode) {
                case FamilyFragment.REQUEST_CREATE_FAMILY:
                    mTabHost.setCurrentTab(0);
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!mIsBackTime) {
            showToast("再按一次退出幸福100");
            new Thread() {
                public void run() {
                    try {
                        mIsBackTime = true;
                        Thread.sleep(1000);
                        mIsBackTime = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            mApplication.setIsLogout(false);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("activity", "MainActivity   onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

    }

    @OnClick(R.id.ll_family_tab)
    public void onClick() {
        mTabHost.setCurrentTab(2);
        ivFamily.setImageDrawable(getResources().getDrawable(R.drawable.tab_family_pressed));
        tvTabFamily.setTextColor(getResources().getColor(R.color.tab_family_tab_pressed));
    }
}
