package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/18.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
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
 * 作者：jiangsheng on 2016/8/18 14:28
 */
public class Setting_account_password extends BaseActivity {

    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.item_account_value)
    TextView mItemAccountValue;
    @Bind(R.id.password_edit)
    EditText mPasswordEdit;
    @Bind(R.id.enterpassword_edit)
    EditText mEnterpasswordEdit;
    String oldPassWord = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("设置幸福100密码");
        mTextBack.setText("返回");
        mTextRight.setText("保存");
        mTextRight.setVisibility(View.VISIBLE);
        mTitleViewRight.setVisibility(View.VISIBLE);

        User user = mApplication.getUser();
        mItemAccountValue.setText("XF" + user.getXf());
        oldPassWord = getIntent().getStringExtra("oldpassword");
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                String pwd = mPasswordEdit.getText().toString();
                String enterpwd = mEnterpasswordEdit.getText().toString();

                if (pwd == null ||pwd.isEmpty()||pwd.length() < 6)
                {
                    ToastUtils.shortToast((Activity)mContext,"密码必须是大于6-20位英文字母、数字组合（不能是纯数字）");
                    mPasswordEdit.setText("");
                    mEnterpasswordEdit.setText("");
                    return;
                }

                if (pwd.compareTo(enterpwd) != 0)
                {
                    ToastUtils.shortToast((Activity)mContext,"两次输入的密码不一致");
                    return;
                }

                postSetPassWord(pwd);
                break;
        }
    }

    void postSetPassWord(final String pwd)
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("org_password",oldPassWord);
        params.put("password",pwd);

        APIClient.post(mContext, Constants.URL.MODIFY_PASSWORD, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        ToastUtils.shortToast((Activity)mContext,"设置成功");
                        mApplication.getUser().setPwdIsNull(false);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }
}
