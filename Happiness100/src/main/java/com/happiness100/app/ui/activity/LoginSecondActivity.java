package com.happiness100.app.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.LoginBottomMenuDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.UILUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/12 11:19
 */
public class LoginSecondActivity extends BaseLoginActivity {
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @Bind(R.id.tv_country)
    TextView tvCountry;
    @Bind(R.id.view_select_country)
    RelativeLayout viewSelectCountry;
    @Bind(R.id.edit_password)
    EditText editPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_login_has_problem)
    TextView tvLoginHasProblem;
    @Bind(R.id.tv_more)
    TextView tvMore;

    BaseActivity mContext;
    private LoginBottomMenuDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_second);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        SharedPreferences sp = SharedPreferencesContext.getInstance().getSharedPreferences();

        String headImgUrl = sp.getString(Constants.UserInfo.HEAD_IMG_URI, "");
        String phoneNum = sp.getString(Constants.UserInfo.PHONE, "");

        UILUtils.displayImage(headImgUrl, ivHead);
        tvPhoneNum.setText(phoneNum);
    }

    @OnClick({R.id.tv_cancel, R.id.btn_login, R.id.tv_login_has_problem, R.id.tv_more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            case R.id.btn_login:

                Map<String, String> params = new LinkedHashMap<>();
                String phoneNum = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.UserInfo.PHONE, "");
                params.put("account", phoneNum);
                params.put("password", editPassword.getText().toString());
                params.put("client", "1");

                boolean isParamsSuccess = checkParams();

                APIClient.post(mContext, Constants.URL.LOGIN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }

                    @Override
                    public void onResponse(String s) {
                        super.onResponse(s);
                        handleResponse(s, new ResponeInterface() {
                            @Override
                            public void parseResponse(String json) {
                                User user = GsonUtils.parseJSON(json, User.class);
                                user.setPassword(editPassword.getText().toString());
                                mApplication.saveUserInfo(user);
                                initHealthData(mContext);
                            }
                        });
                    }
                });
                break;
            case R.id.tv_login_has_problem:
                //用短信登录或邮箱找回密码
                startActivity(new Intent(mContext, LoginHasProblemActivity.class));
                break;
            case R.id.tv_more:
                showBottomDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
        }
    }

    private boolean checkParams() {
        return true;
    }

    /**
     * 弹出底部框
     */
    private void showBottomDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (mDialog == null) {
            mDialog = new LoginBottomMenuDialog(mContext, R.style.MMTheme_DataSheet_BottomIn, 2);
        }
        mDialog.show();
    }
}
