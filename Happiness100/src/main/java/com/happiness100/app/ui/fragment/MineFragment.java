package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/8/10.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.ui.activity.BaseActivity;
import com.happiness100.app.ui.activity.Setting_Activity;
import com.happiness100.app.ui.activity.UserInfoActivity;
import com.happiness100.app.utils.Constants;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/10 14:46
 */
public class MineFragment extends BaseFragment {
    @Bind(R.id.info_view_picture)
    ImageView mInfoViewPicture;
    @Bind(R.id.info_view_name)
    TextView mInfoViewName;
    @Bind(R.id.info_view_sex)
    ImageView mInfoViewSex;
    @Bind(R.id.info_userId)
    TextView mInfoUserId;
    @Bind(R.id.title_view_back)
    LinearLayout mTitleViewBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    private View rootView;
    @Bind(R.id.setting_warn)
    ImageView mSettingWarn;
    BaseActivity mMainActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_mine_page, null);
        }
        ButterKnife.bind(this, rootView);
        initView();
        return rootView;
    }


    void initView() {
        User user = mApplication.getUser();
        if (user == null) {
            return;
        }

        UILUtils.displayImage(user.getHeadImageUri(), mInfoViewPicture);
        mInfoViewName.setText(user.getNickname());
        mInfoUserId.setText("幸福号：XF" + user.getXf());
        mInfoViewSex.setImageResource(user.getSex() == 0 ? R.drawable.icon_boy : R.drawable.icon_girl);
        mTitleViewBack.setVisibility(View.GONE);
        mTitleViewTitle.setText("我的");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.info_view, R.id.info_setting, R.id.info_shop, R.id.info_log, R.id.info_collect, R.id.info_wallet})
    public void onClick(View view) {
        Intent it = new Intent();
        switch (view.getId()) {
            case R.id.info_view:
                it.setClass(getActivity(), UserInfoActivity.class);
                startActivityForResult(it, Constants.ActivityIntentIndex.UserInfoActivityIndex);
                break;
            case R.id.info_setting:
                it.setClass(getActivity(), Setting_Activity.class);
                startActivityForResult(it, Constants.RequestCode.REQUEST_SETTINGS);
                break;
            case R.id.info_shop:
                Uri uri = Uri.parse("http://www.qlsme.com/wap/");
                Intent itUri = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(itUri);
                break;
            case R.id.info_log:
            case R.id.info_collect:
            case R.id.info_wallet:
                ToastUtils.shortToast(mContext, "功能暂未开放");
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        boolean hasNewVersion = SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.SpKey.HAS_NEW_VERSION, false);

        if (hasNewVersion||mApplication.getUser().isPwdIsNull()) {
            mSettingWarn.setVisibility(View.VISIBLE);
        } else {
            mSettingWarn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.ActivityIntentIndex.UserInfoActivityIndex:
                    initView();
                    break;
                case Constants.RequestCode.REQUEST_SETTINGS:
                    if (!mApplication.isLogout()) {
                        mMainActivity.finish();
                    }
                    break;
            }
        }
    }
}
