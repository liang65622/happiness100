package com.happiness100.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginHasProblemActivity extends BaseActivity implements BaseViewInterface {

    @Bind(R.id.layout_title_back)
    LinearLayout layoutTitleBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_title_right)
    TextView tvTitleRight;
    @Bind(R.id.tv_phone_msg_tips)
    TextView tvPhoneMsgTips;
    @Bind(R.id.can_not_receive)
    Button canNotReceive;
    @Bind(R.id.btn_can_receive)
    Button btnCanReceive;
    Activity mContext;
    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_has_problem);
        ButterKnife.bind(this);
        mContext = this;
        initView();

    }

    @OnClick({R.id.can_not_receive, R.id.btn_can_receive})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.can_not_receive:
                postGetEmailStatus();
                break;
            case R.id.btn_can_receive:
                Intent intent = new Intent(mContext, LoginByVerCodeTipsActivity.class);
                intent.putExtra("phoneNum",mPhoneNum.replace("86 ",""));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void initView() {
        mPhoneNum = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.UserInfo.PHONE, "");
        tvPhoneMsgTips.setText("你的手机号+" + mPhoneNum + "能不能接收短信？");
    }


    void OpenLoginHasProblemEmailActivity()
    {
        String email = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.UserInfo.EMAIL, "");
        if(email == null ||email.isEmpty())
        {
            ToastUtils.shortToast(mContext, "请联系客户进行申述。");
        }
        else
        {
            Intent it = new Intent(mContext, LoginHasProblemEmailActivity.class);
            startActivity(it);
        }
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.layout_title_back)
    public void onClick() {
        mContext.finish();
    }


    void postGetEmailStatus()
    {
        String session = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.UserInfo.SESSION_ID, "");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", session);
        APIClient.post(mContext, Constants.URL.GET_EMAIL_STATUS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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

                        if (json == null ||json.isEmpty())
                            ToastUtils.shortToast(mContext, "请联系客户进行申述。");
                        else if (json.compareTo("1") == 0)
                            OpenLoginHasProblemEmailActivity();
                        else
                            ToastUtils.shortToast(mContext, "请联系客户进行申述。");
                    }
                });
            }
        });
    }

}
