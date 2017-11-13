package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/18.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
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
 * 作者：jiangsheng on 2016/8/18 11:38
 */
public class Setting_account_email_Activity extends BaseActivity {

    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.email_edit)
    EditText mEmailEdit;
    @Bind(R.id.desc)
    TextView mDesc;
    @Bind(R.id.unbind)
    Button mUnbind;
    @Bind(R.id.resend)
    Button mResend;
    private int mWaitSeconds;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account_email);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("邮箱绑定");
        mTextBack.setText("返回");
        mTextRight.setText("保存");
        mTextRight.setVisibility(View.VISIBLE);
        mTitleViewRight.setVisibility(View.VISIBLE);
        String email = mApplication.getUser().getEmail();
        if (email == null || email.isEmpty())
            updateStatus(false);
        else
            postGetEmailStatus();
    }
    void updateStatus(boolean check)
    {
        mResend.setVisibility(View.GONE);
        mUnbind.setVisibility(View.GONE);
        mDesc.setVisibility(View.GONE);
        mTitleViewRight.setVisibility(View.GONE);
        mEmailEdit.setFocusable(true);
        String email = mApplication.getUser().getEmail();

        if (email == null || email.isEmpty())
        {
            mTextRight.setText("保存");
            mTextRight.setVisibility(View.VISIBLE);
            mTitleViewRight.setVisibility(View.VISIBLE);
            mDesc.setVisibility(View.VISIBLE);
            mDesc.setText("请输入邮箱地址,你可以用验证过的邮箱来找回密码");
            mDesc.setTextColor(getResources().getColor(R.color.gray));
        }
        else
        {
            mEmailEdit.setText(email);
            mEmailEdit.setFocusable(false);
            if (check)
            {
                mUnbind.setVisibility(View.VISIBLE);
            }
            else
            {
                mTextRight.setText("编辑");
                mTextRight.setVisibility(View.VISIBLE);
                mTitleViewRight.setVisibility(View.VISIBLE);
                mDesc.setVisibility(View.VISIBLE);
                mDesc.setText("该邮箱还未验证,请登录你的邮箱查收邮件并验证");
                mDesc.setTextColor(getResources().getColor(R.color.red));
                mResend.setVisibility(View.VISIBLE);
            }
        }
    }


    @OnClick({R.id.title_view_back, R.id.title_view_right,R.id.unbind, R.id.resend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                String str= mTextRight.getText().toString();
                if (str.compareTo("保存") == 0)
                {
                    if (!EmailUtils.getInstance().checkEmail(mEmailEdit.getText().toString())) {
                        ToastUtils.shortToast(mContext, "不是合法的邮箱格式");
                        return;
                    }
                    postBindEmail();
                }
                else if (str.compareTo("编辑") == 0)
                {
                    Intent it=  new Intent(mContext,SettingAccountEmailEditActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.unbind:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext,"解除邮箱绑定","你确定要解除绑定邮箱？", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        postUnBindEmail();
                    }

                    @Override
                    public void exectEditEvent(String editText) {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });

                break;
            case R.id.resend:
                waitGetVcode();
                postCheckBindEmail();
                break;
        }
    }

    void postGetEmailStatus()
    {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
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
                            updateStatus(false);
                        else if (json.compareTo("1") == 0)
                            updateStatus(true);
                        else
                            updateStatus(false);
                    }
                });
            }
        });
    }

    void postCheckBindEmail()
    {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        APIClient.post(mContext, Constants.URL.CHECK_BIND_EMAIL, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        AlertDialog.Builder alterDialog = new AlertDialog.Builder(mContext);
                        alterDialog.setMessage("一封验证邮件已发送至"+mEmailEdit.getText().toString()+",请登录你的邮箱查收并通过验证.");
                        alterDialog.setCancelable(true);

                        alterDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postGetEmailStatus();
                            }
                        });
                        alterDialog.show();
                    }
                });
            }
        });
    }

    void postBindEmail() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("mail", mEmailEdit.getText().toString());
        APIClient.post(mContext, Constants.URL.BIND_EMAIL, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        AlertDialog.Builder alterDialog = new AlertDialog.Builder(mContext);
                        alterDialog.setMessage("一封验证邮件已发送至"+mEmailEdit.getText().toString()+",请登录你的邮箱查收并通过验证.");
                        alterDialog.setCancelable(true);

                        alterDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mApplication.getUser().setEmail(mEmailEdit.getText().toString());
                                SharedPreferences.Editor edit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
                                edit.putString(Constants.UserInfo.EMAIL, mApplication.getUser().getEmail());
                                edit.commit();
                                postGetEmailStatus();
                            }
                        });

                        alterDialog.show();
                    }
                });
            }
        });
    }

    void postUnBindEmail() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        APIClient.post(mContext, Constants.URL.UNBIND_EMAIL, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        mApplication.getUser().setEmail("");
                        SharedPreferences.Editor edit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
                        edit.putString(Constants.UserInfo.EMAIL, mApplication.getUser().getEmail());
                        edit.commit();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }

    private void waitGetVcode() {
        mWaitSeconds = 60;
        mResend.setEnabled(false);
        new Thread() {
            public void run() {
                while (mWaitSeconds > 0) {
                    try {
                        Thread.sleep(1000);
                        mWaitSeconds--;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mResend.setText(mWaitSeconds + "秒后重试");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mResend.setEnabled(true);
                        mResend.setText("重新发送邮件");
                    }
                });
            }
        }.start();
    }
}
