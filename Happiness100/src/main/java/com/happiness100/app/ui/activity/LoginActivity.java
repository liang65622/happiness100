package com.happiness100.app.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.LoginBottomMenuDialog;
import com.happiness100.app.ui.widget.country.CountryActivity;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.AMUtils;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class LoginActivity extends BaseLoginActivity {
    private static final int REQUEST_LOGIN = 1;
    @Bind(R.id.tv_country)
    TextView tvCountry;
    @Bind(R.id.view_select_country)
    RelativeLayout viewSelectCountry;
    @Bind(R.id.tv_country_num)
    TextView tvCountryNum;
    @Bind(R.id.edit_phone)
    EditText editPhone;
    @Bind(R.id.edit_password)
    EditText editPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_login_by_vcode)
    TextView tvLoginByVcode;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private BaseActivity mContext;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mContext = this;
        tvTitle.setText("请填写手机号/邮箱地址/幸福号");
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

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

                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {

                    Log.d("LoginActivity", "--onSuccess" + userid);
                    setResult(RESULT_OK);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }


    @OnClick({R.id.tv_cancel, R.id.view_select_country, R.id.btn_login, R.id.tv_login_by_vcode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            case R.id.view_select_country:
                Intent intent = new Intent();
                intent.setClass(mContext, CountryActivity.class);
                startActivityForResult(intent, 12);
                break;
            case R.id.btn_login:

                Map<String, String> params = new LinkedHashMap<>();
                String countryNum = tvCountryNum.getText().toString().replace("+", "");

                String account = editPhone.getText().toString();

                if (AMUtils.isMobile(account)) {
                    params.put("account", countryNum + " " + account);
                } else {
                    params.put("account", editPhone.getText().toString());
                }

//                params.put("account", countryNum + " " + editPhone.getText().toString());
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
            case R.id.tv_login_by_vcode:
//                startActivityForResult(new Intent(mContext, LoginByVercodeActivity.class), REQUEST_LOGIN);
                startActivity(new Intent(mContext, LoginByVercodeActivity.class));
                break;
        }
    }


    private boolean checkParams() {
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String countryName = bundle.getString("countryName");
                    String countryNumber = bundle.getString("countryNumber");
                    tvCountry.setText(countryName);
                    tvCountryNum.setText(countryNumber);
                }
                break;
            case REQUEST_LOGIN:
                setResult(RESULT_OK);
                mContext.finish();
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.tv_more)
    public void onClick() {
        showBottomDialog();
    }

    /**
     * 弹出底部框
     */
    private void showBottomDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (mDialog == null) {
            mDialog = new LoginBottomMenuDialog(mContext, R.style.MMTheme_DataSheet_BottomIn);
        }

        mDialog.show();
    }


}
