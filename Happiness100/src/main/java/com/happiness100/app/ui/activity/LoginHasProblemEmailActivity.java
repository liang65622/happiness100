package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/20.
 */

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
import com.justin.utils.EmailUtils;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/20 23:42
 */
public class LoginHasProblemEmailActivity extends BaseActivity implements BaseViewInterface {

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
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_has_problem);
        ButterKnife.bind(this);
        mContext = this;
        initView();

    }

    @OnClick({R.id.can_not_receive, R.id.btn_can_receive,R.id.layout_title_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.can_not_receive:
                ToastUtils.shortToast(mContext, "请联系客户进行申述。");
                break;
            case R.id.btn_can_receive:
                postfindPassword();
                break;
            case R.id.layout_title_back:
                finish();
                break;
        }
    }

    @Override
    public void initView() {
        mEmail = SharedPreferencesContext.getInstance().getSharedPreferences().getString(Constants.UserInfo.EMAIL, "");
        tvPhoneMsgTips.setText("你的邮箱" + mEmail + "目前能不能登录？");
    }

    @Override
    public void initData() {

    }


    void postfindPassword()
    {

        if (!EmailUtils.getInstance().checkEmail(mEmail))
        {
            ToastUtils.shortToast(mContext, "不是合法的邮箱格式");
            return;
        }

        Map<String,String> params = new LinkedHashMap<>();
        params.put("mail",mEmail);
        APIClient.post(mContext, Constants.URL.FIND_PASSWORD_MAIL, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        Intent itent = new Intent(mContext,FindPasswordEmailEnterActivity.class);
                        itent.putExtra("email",mEmail);
                        startActivity(itent);
                    }
                });
            }
        });
    }
}
