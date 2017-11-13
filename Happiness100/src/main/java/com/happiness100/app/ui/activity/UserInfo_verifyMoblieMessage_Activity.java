package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/17.
 */

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.BottomMenuDialog;
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
 * 作者：jiangsheng on 2016/8/17 15:02
 */
public class UserInfo_verifyMoblieMessage_Activity extends BaseActivity {

    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.mobile_text)
    TextView mMobileText;
    @Bind(R.id.mobile_verify_code)
    EditText mMobileVerifyEdit;
    String newMobileNum;
    @Bind(R.id.help)
    RelativeLayout mHelp;
    @Bind(R.id.help_text)
    TextView mHelpText;
    private int mWaitSeconds;
    BottomMenuDialog mDialog_Resend;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_recvmessage);
        ButterKnife.bind(this);
        newMobileNum = getIntent().getStringExtra("NewMobileNum");

        initView();
        waitGetVcode();
    }

    void initView() {
        mTitleViewTitle.setText("填写验证码");
        mTextBack.setText("返回");
        mMobileText.setText(newMobileNum);

        //String verifyCode = getIntent().getStringExtra("data");
        //mMobileVerifyEdit.setText(verifyCode);
    }

    @OnClick({R.id.commit, R.id.help, R.id.title_view_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commit:
                if (mMobileVerifyEdit.getText().toString().isEmpty()) {
                    ToastUtils.shortToast(mContext, "验证码不能为空");
                    return;
                }
                CommitEx();
                break;
            case R.id.help:
                if (mDialog_Resend != null && mDialog_Resend.isShowing()) {
                    mDialog_Resend.dismiss();
                }
                if (mDialog_Resend == null) {
                    mDialog_Resend = new BottomMenuDialog(mContext,2,"取消","重新获取验证码","");


                    mDialog_Resend.setMiddleListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ReSend();
                            waitGetVcode();
                            if (mDialog_Resend != null && mDialog_Resend.isShowing()) {
                                mDialog_Resend.dismiss();
                            }
                        }
                    });
                    mDialog_Resend.setCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog_Resend.dismiss();
                        }
                    });
                }
                mDialog_Resend.show();
                break;
            case R.id.title_view_back:
                finish();
                break;
        }
    }

    void CommitEx() {
        Map<String, String> params = new LinkedHashMap<>();
        User user = mApplication.getUser();
        params.put("sessionid", user.getSessionid());
        params.put("vercode", mMobileVerifyEdit.getText().toString());
        params.put("new_mobile", newMobileNum.replace("+",""));

        APIClient.post(mContext, Constants.URL.EX_MOBILE_BIND_CONFIRM, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        ToastUtils.shortToast(mContext,"更换手机成功");
                        mApplication.getUser().setMobile(json);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }

    private void ReSend()
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("new_mobile",newMobileNum.replace("+",""));
        APIClient.post(mContext, Constants.URL.EX_MOBILE_BIND_VERCODE, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        mMobileVerifyEdit.setText(json);
                    }
                });
            }
        });
    }

    private void waitGetVcode() {
        mWaitSeconds = 60;
        mHelp.setClickable(false);
        new Thread() {
            public void run() {
                while (mWaitSeconds > 0) {
                    try {
                        Thread.sleep(1000);
                        mWaitSeconds--;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mHelpText.setText(mWaitSeconds + "秒后重试");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mHelpText.setText("收不到验证码？");
                        mHelp.setClickable(true);
                    }
                });
            }
        }.start();
    }
}
