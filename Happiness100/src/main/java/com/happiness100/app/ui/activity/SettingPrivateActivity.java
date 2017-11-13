package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/13.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.SwitchView;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.SharedPreferencesContext;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/13 15:10
 */
public class SettingPrivateActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.search_by_mobile_switch)
    SwitchView mSearchByMobileSwitch;
    @Bind(R.id.recommend_friend_item_switch)
    SwitchView mRecommendFriendItemSwitch;
    @Bind(R.id.search_by_xf_switch)
    SwitchView mSearchByXfSwitch;
    @Bind(R.id.search_by_email_switch)
    SwitchView mSearchByemailSwitch;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEdit;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_private_activity);
        ButterKnife.bind(this);
        initData();
        initView();
    }
    void initData() {
        mUser = mApplication.getUser();
        mSharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
        mEdit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
    }
    private void initView() {
        Intent intent = new Intent();

        String backStr = intent.getStringExtra("back");
        if (backStr == null || backStr.isEmpty()) {
            backStr = "返回";
        }

        mTextBack.setText(backStr);
        mTitleViewTitle.setText("隐私");
        int mask =mUser.getSearchMask();
        boolean RECOMMEND = mSharedPreferences.getBoolean(Constants.Function.RECOMMEND+mUser.getXf(),true);
        boolean SEARCHBYEMAIL = (1&mask) == 0;
        boolean SEARCHBYXF = (1<<1&mask) == 0;
        boolean SEARCHBYMOBILE = (1<<2&mask) == 0;
        mSearchByMobileSwitch.toggleSwitch(SEARCHBYMOBILE);
        mSearchByXfSwitch.toggleSwitch(SEARCHBYXF);
        mRecommendFriendItemSwitch.toggleSwitch(RECOMMEND);
        mSearchByemailSwitch.toggleSwitch(SEARCHBYEMAIL);

        mSearchByemailSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                int mask = mUser.getSearchMask()&6;
                postMask(mask,mSearchByemailSwitch,true);
            }

            @Override
            public void toggleToOff() {
                int mask = mUser.getSearchMask()|1;
                postMask(mask,mSearchByemailSwitch,false);
            }
        });

        mSearchByMobileSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                int mask = mUser.getSearchMask()&3;
                postMask(mask,mSearchByMobileSwitch,true);
            }

            @Override
            public void toggleToOff() {
                int mask = mUser.getSearchMask()|1<<2;
                postMask(mask,mSearchByMobileSwitch,false);
            }
        });

        mRecommendFriendItemSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                mRecommendFriendItemSwitch.toggleSwitch(true);
                mEdit.putBoolean(Constants.Function.RECOMMEND+mUser.getXf(),true);
                mEdit.commit();
            }

            @Override
            public void toggleToOff() {
                mRecommendFriendItemSwitch.toggleSwitch(false);
                mEdit.putBoolean(Constants.Function.RECOMMEND+mUser.getXf(),false);
                mEdit.commit();
            }
        });

        mSearchByXfSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                int mask = mUser.getSearchMask()&5;
                postMask(mask,mSearchByXfSwitch,true);
            }

            @Override
            public void toggleToOff() {

                int mask = mUser.getSearchMask()|1<<1;
                postMask(mask,mSearchByXfSwitch,false);
            }
        });
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        finish();
    }


    void postMask(final int mask,final SwitchView control,final boolean value)
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("search_mask",mask+"");
        APIClient.postWithSessionId(mContext, Constants.URL.UPDATE_SEARCH_MASK, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }
            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                handleResponse(json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        mApplication.getUser().setSearchMask(mask);
                        control.toggleSwitch(value);
                    }
                });
            }
        });
    }
}
