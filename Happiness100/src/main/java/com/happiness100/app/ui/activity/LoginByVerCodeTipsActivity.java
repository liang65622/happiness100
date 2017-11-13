package com.happiness100.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happiness100.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginByVerCodeTipsActivity extends BaseActivity {

    @Bind(R.id.layout_title_back)
    LinearLayout layoutTitleBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_title_right)
    TextView tvTitleRight;
    @Bind(R.id.tv_phone_msg_tips)
    TextView tvPhoneMsgTips;
    @Bind(R.id.btn_login_by_vercode)
    Button btnLoginByVercode;

    Activity mContext;
    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_ver_code_tips);
        ButterKnife.bind(this);
        mPhoneNum = getIntent().getStringExtra("phoneNum");
        mContext = this;
    }

    @OnClick({R.id.layout_title_back, R.id.btn_login_by_vercode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_title_back:
                mContext.finish();
                break;
            case R.id.btn_login_by_vercode:
                Intent intent = new Intent(mContext, LoginByVercodeActivity.class);
                intent.putExtra("phoneNum",mPhoneNum);
                startActivity(intent);
                break;
        }
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        mContext.finish();
    }
}
