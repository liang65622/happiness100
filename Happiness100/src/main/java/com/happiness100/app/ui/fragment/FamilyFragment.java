package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/8/23.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.activity.MainActivity;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：justin on 2016/8/23 09:25
 */
public class FamilyFragment extends BaseFragment {
    private static final int VP_COUNT = 4;
    private static final String TAG = "FamilyFragment";
    private static final int CHANGE_FAMILY = 5;
    public static final int REQUEST_CREATE_FAMILY = 6;
    MainActivity mContext;

    private List<FamilyBaseInfo> mFamilyBaseInfos;
    private FamilyBaseInfo mDefaultFamilyBaseInfo;
    private View mRooView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mMFragmentTransaction;
    private FragmentTransaction mFragmentTransaction;
    private boolean mIsOnActivityResult = false;
    private FamilyIndexFragment mFamilyIndex;
    private CreateFamilyTipsFragment mCreateFamilyTipsFragment;

    public FamilyFragment() {
        Log.e(TAG, "FamilyFragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (MainActivity) getActivity();
    }

    boolean isFirstLoad = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "onCreateView()");
        if (mRooView == null) {
            mFamilyBaseInfos = mApplication.getUser().getFamilyBaseInfos();
            mDefaultFamilyBaseInfo = mApplication.getUser().getDefaultFamilyBaseInfo();
            mRooView = inflater.inflate(R.layout.fragment_family, container, false);

            mFragmentManager = getChildFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            if (mFamilyBaseInfos != null && mFamilyBaseInfos.size() > 0) {//TODO 如果已经有家族，进入族界面
                mFamilyIndex = new FamilyIndexFragment();
                mFragmentTransaction.add(R.id.view_root, mFamilyIndex);
                mFragmentTransaction.commit();

//            initFamilyView(mRooView);
//            SharedPreferencesContext.getInstance().getSharedPreferences().edit().putString("clanId", mDefaultFamilyBaseInfo.getClanId() + "").commit();
            } else {//无家族进入创建家族界面

                mCreateFamilyTipsFragment = new CreateFamilyTipsFragment();
                mFragmentTransaction.add(R.id.view_root, mCreateFamilyTipsFragment);
                mFragmentTransaction.commit();
//            mRooView = inflater.inflate(R.layout.fragment_create_family_tips, container, false);
//            initCreateFamilyView(mRooView);
//            initContactsInfo();
            }
        }
        return mRooView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
//        freshUI();

        if (resultCode == Activity.RESULT_OK) {
//            initFamilyList();
            int type = 0;
            if (data != null) {
                type = data.getIntExtra("type", 0);
            }
            if (type == Constants.ResultCode.EXIT_FAMILY) {
                mIsOnActivityResult = true;
                freshUI();
                return;
            }

//            initFamilyList();
            if (requestCode == CHANGE_FAMILY) {
//                mIsOnActivityResult = true;

                FamilyIndex familyIndex = data.getParcelableExtra("familyIndex");
//                freshUI();
            } else {
                //更新家族数据 TODO
            }
        }
    }

    private void freshUI() {


        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFamilyBaseInfos = mApplication.getUser().getFamilyBaseInfos();

        if (mFamilyBaseInfos != null && mFamilyBaseInfos.size() > 0) {//TODO 如果已经有家族，进入族界面
            if (mFamilyIndex == null) {
                mFamilyIndex = new FamilyIndexFragment();
            }

            if (mCreateFamilyTipsFragment != null && mCreateFamilyTipsFragment.isAdded()) {
                mFragmentTransaction.replace(R.id.view_root, mFamilyIndex);
                mFragmentTransaction.commitAllowingStateLoss();
            } else {
                if (!mFamilyIndex.isAdded()) {
                    mFragmentTransaction.add(R.id.view_root, mFamilyIndex);
                    mFragmentTransaction.commitAllowingStateLoss();
                }
            }
        } else {
            if (mCreateFamilyTipsFragment == null) {
                mCreateFamilyTipsFragment = new CreateFamilyTipsFragment();
            }

            if (mFamilyIndex != null && mFamilyIndex.isAdded()) {
                mFragmentTransaction.replace(R.id.view_root, mCreateFamilyTipsFragment);
                mFragmentTransaction.commitAllowingStateLoss();
            } else {
                if (!mCreateFamilyTipsFragment.isAdded()) {
                    mFragmentTransaction.add(R.id.view_root, mCreateFamilyTipsFragment);
                    mFragmentTransaction.commitAllowingStateLoss();
                }
            }
        }
    }

    private void initFamilyList() {
        Map<String, String> params = new HashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        APIClient.post(mContext, Constants.URL.GET_CLIAN_LIST, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Log.e(TAG, "parsRespone:" + json);
                        Type type = new TypeToken<ArrayList<FamilyBaseInfo>>() {
                        }.getType();
                        List<FamilyBaseInfo> familyBaseInfos = GsonUtils.parseJSONArray(json, type);

                        if (familyBaseInfos == null || familyBaseInfos.size() == 0) {//TODO
//                            FamilyFragment familyFragment = (FamilyFragment) getSupportFragmentManager().getFragments().get(0);
//                            familyFragment.
                            mApplication.getUser().setDefaultFamilyBaseInfo(new FamilyBaseInfo());
                            mApplication.getUser().setFamilyBaseInfos(null);
                            freshUI();
                        } else {
                            mApplication.getUser().setFamilyBaseInfos(familyBaseInfos);
                            for (FamilyBaseInfo familyBaseInfo : familyBaseInfos) {
                                if (familyBaseInfo.isIsDefault() == true) {
                                    mApplication.getUser().setDefaultFamilyBaseInfo(familyBaseInfo);
                                    freshUI();
                                }
                            }
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
        Log.e(TAG, "mIsOnActivityResult()" + mIsOnActivityResult);
        if (!mIsOnActivityResult) {
            initFamilyList();
            mIsOnActivityResult = false;
        }
        mIsOnActivityResult = false;
    }
}
