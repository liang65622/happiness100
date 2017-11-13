package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/18 11:17
 */
public class Setting_account_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.item_account_value)
    TextView mAccountValue;
    @Bind(R.id.item_email_value)
    TextView mEmailValue;
    @Bind(R.id.item_phone_value)
    TextView mPhoneValue;
    @Bind(R.id.password_warn)

    ImageView mPasswordWarn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("账号与安全");
        mTextBack.setText("返回");

        User user = mApplication.getUser();
        mAccountValue.setText("XF"+user.getXf());
        updateEmail();
        updatePhone();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEmail();
        updatePhone();
        mPasswordWarn.setVisibility(mApplication.getUser().isPwdIsNull()?View.VISIBLE:View.GONE);
    }

    @OnClick({R.id.title_view_back, R.id.item_email, R.id.item_phone, R.id.item_password, R.id.item_secure})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.item_email:
                intent.setClass(this,Setting_account_email_Activity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.item_phone:
                intent.setClass(this,UserInfo_mobile_Activity.class);
                startActivityForResult(intent,2);
                break;
            case R.id.item_password:
                openSettingPassWord();
                break;
            case R.id.item_secure:
                ToastUtils.shortToast(mContext,"功能暂未开放");
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode) {
                case 1:
                    updateEmail();
                    break;
                case 2:
                    updatePhone();
                case 3:
                    break;
            }
        }
    }

    void updateEmail()
    {
        String email = mApplication.getUser().getEmail();
        if (email != null && !email.isEmpty())
            mEmailValue.setText(email);
        else
            mEmailValue.setText("未绑定");
    }
    void updatePhone()
    {
        mPhoneValue.setText(mApplication.getUser().getMobile());
    }

    void openSettingPassWord()
    {
        if (mApplication.getUser().isPwdIsNull()) {
            startPassWordActivity("");
        }
        else
        {
            DialogWithYesOrNoUtils.getInstance().showEditPasswordDialog(this, "验证原密码", "为保障你的数据安全，修改密码前请填写原密码", "请输入密码","确定",new DialogWithYesOrNoUtils.DialogCallBack() {
                @Override
                public void exectEvent()
                {

                }
                public void exectEditEvent(String editText)
                {
                    checkPassword(editText);
                }
                public void updatePassword(String oldPassword, String newPassword)
                {

                }
            });
        }
    }

    void checkPassword(final String password)
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("password",password);

        APIClient.post(mContext, Constants.URL.CHECK_PASSWORD, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        if (json.compareTo(Constants.NetWork.TRUE) == 0)
                        {
                            startPassWordActivity(password);
                        }
                        else
                        {
                            ToastUtils.shortToast(mContext,"密码错误");
                        }

                    }
                });
            }
        });
    }

    void startPassWordActivity(String oldPassword)
    {
        Intent intent = new Intent();
        intent.setClass(Setting_account_Activity.this,Setting_account_password.class);
        intent.putExtra("oldpassword",oldPassword);
        startActivityForResult(intent,3);
    }

}
