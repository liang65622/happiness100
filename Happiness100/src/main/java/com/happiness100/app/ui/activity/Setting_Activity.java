package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/18.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.ui.widget.LogoutBomttomMenuDialog;
import com.happiness100.app.utils.Constants;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/18 11:02
 */
public class Setting_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.account_warn)
    ImageView mAccountWarn;
    @Bind(R.id.iv_update_tips)
    ImageView ivUpdateTips;
    @Bind(R.id.tv_update_version)
    TextView tvUpdateVersion;
    @Bind(R.id.tv_version)
    TextView tvVersion;
    private LogoutBomttomMenuDialog mDialog_Logout;
    private boolean mHasNewVersion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_activity);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("设置");
        mTextBack.setText("返回");
        tvVersion.setText(getVersionName());
        User user = mApplication.getUser();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHasNewVersion = SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.SpKey.HAS_NEW_VERSION, false);

        if (mHasNewVersion) {
            ivUpdateTips.setVisibility(View.VISIBLE);
        } else {
            ivUpdateTips.setVisibility(View.GONE);
        }

        mAccountWarn.setVisibility(mApplication.getUser().isPwdIsNull() ? View.VISIBLE : View.GONE);

    }

    @OnClick({R.id.title_view_back, R.id.setting_item_account, R.id.setting_item_notify, R.id.setting_item_privacy, R.id.setting_item_common, R.id.setting_item_out, R.id.setting_update})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.setting_update:

                if (mHasNewVersion) {
                    String downloadUrl = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.SpKey.VERSION_APP_URL, "");
                    if (!downloadUrl.isEmpty()) {
                        Uri uri = Uri.parse(downloadUrl);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                        SharedPreferencesContext.getInstance().getSharedPreferences().edit().putBoolean(Constants.SpKey.HAS_NEW_VERSION, false).commit();
                        ivUpdateTips.setVisibility(View.GONE);
                    }
                }else{
                    showToast("当前已经是最新版本");
                }
                break;
            case R.id.setting_item_account:
                Intent intent = new Intent();
                intent.setClass(this, Setting_account_Activity.class);
                startActivity(intent);
                break;
            case R.id.setting_item_notify:
                Intent intentNewMessage = new Intent();
                intentNewMessage.putExtra("back", "设置");
                intentNewMessage.setClass(this, SettingNewMessageActivity.class);
                startActivity(intentNewMessage);
                break;
            case R.id.setting_item_privacy:
                Intent intentPrivate = new Intent();
                intentPrivate.putExtra("back", "设置");
                intentPrivate.setClass(this, SettingPrivateActivity.class);
                startActivity(intentPrivate);
                break;
            case R.id.setting_item_common:
                ToastUtils.shortToast(mContext, "功能暂未开放");
                break;
            case R.id.setting_item_out:
                clickOut();
                break;
        }
    }

    void clickOut() {

        openLogoutDialog();
    }

    void jumpToLogin() {
        mApplication.logout();
        setResult(RESULT_OK);

        mApplication.setIsLogout(true);
        Intent it = new Intent();
        it.setClass(Setting_Activity.this, WellcomActivity.class);
        startActivity(it);
        mContext.finish();
    }

    void startPassWordActivity() {
        Intent intent = new Intent();
        intent.setClass(Setting_Activity.this, Setting_account_password.class);
        intent.putExtra("oldpassword", "");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    jumpToLogin();
                    break;
                default:
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void openLogoutDialog() {
        if (mDialog_Logout != null && mDialog_Logout.isShowing()) {
            mDialog_Logout.dismiss();
        }
        if (mDialog_Logout == null) {
            mDialog_Logout = new LogoutBomttomMenuDialog(mContext, R.style.MMTheme_DataSheet_BottomIn);
            mDialog_Logout.setLogoutListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mApplication.getUser().isPwdIsNull()) {
                        startPassWordActivity();
                    } else {
                        jumpToLogin();
                    }
                }
            });
        }
        mDialog_Logout.show();
    }

    @OnClick(R.id.setting_update)
    public void onClick() {
    }
}
