package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/8.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.StringResponeBean;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/8 17:17
 */
public class CodeVerificationActivity extends BaseLoginActivity {
    private static final String TAG = "CodeVerificationActivity";
    private static int mWaitSeconds = 40;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.edit_vcode)
    EditText editVcode;
    @Bind(R.id.btn_get_vcode)
    Button btnGetVcode;
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.tv_phone_msg_tips)
    TextView tvPhoneMsgTips;
    private String mPhoneNum;
    private BaseActivity mContext;
    private int mTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verication);
        ButterKnife.bind(this);
        mPhoneNum = getIntent().getStringExtra("phone_num");
        tvPhoneMsgTips.setText("验证码短信已经发送到+" + mPhoneNum);
        mTodo = getIntent().getIntExtra("todo", 0);
        mContext = this;
        waitGetVcode();
        if (mTodo == Constants.NetWork.LOGIN) {
            reqLoginVercode();
        } else {
            reqRegisterVercode();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void reqLoginVercode() {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mPhoneNum);
        APIClient.post(mContext, Constants.URL.LOGIN_MSG_VERCODE, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, false, "")) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Log.e("reqRegisterVercode", "onResponse(): " + json);
                        StringResponeBean responeBean = GsonUtils.parseJSON(json, StringResponeBean.class);
                    }
                });

            }
        });
    }

    private void reqRegisterVercode() {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mPhoneNum);
        APIClient.post(mContext, Constants.URL.REQ_VERCODE, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, false, "")) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Log.e("reqRegisterVercode", "onResponse(): " + json);
                        StringResponeBean responeBean = GsonUtils.parseJSON(json, StringResponeBean.class);
                    }
                });

            }
        });

//        APIClient.getVerCode(this, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,false,"")) {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                super.onErrorResponse(volleyError);
//            }
//
//            @Override
//            public void onResponse(String s) {
//                super.onResponse(s);
//                APIClient.handleResponse(mContext, s, new ResponeInterface() {
//                    @Override
//                    public void parseResponse(String json) {
//                        if (TextUtils.isEmpty(json)) {
//                            Log.e("newsList", "onResponse() return is null");
//                            return;
//                        }
//
//                        StringResponeBean responeBean = GsonUtils.parseJSON(json, StringResponeBean.class);
//
//                        if (responeBean.getCode() == 0) {
//                            String vercode = responeBean.getData() + "";
//                            editVcode.setText(vercode);
//                        }
//                        Log.e("onRespone", s);
//                    }
//                });
//
//            }
//        });
    }

    private void waitGetVcode() {
        mWaitSeconds = 40;
        btnGetVcode.setClickable(false);
        new Thread() {
            public void run() {
                while (mWaitSeconds > 0) {
                    try {
                        Thread.sleep(1000);
                        mWaitSeconds--;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnGetVcode.setText(mWaitSeconds + "秒后重试");
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnGetVcode.setText("获取验证码");
                        btnGetVcode.setClickable(true);
                    }
                });
            }
        }.start();
    }

    @OnClick({R.id.tv_cancel, R.id.btn_get_vcode, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            case R.id.btn_get_vcode:
                waitGetVcode();
                if (mTodo == Constants.NetWork.LOGIN) {
                    reqLoginVercode();
                } else {
                    reqRegisterVercode();
                }
                break;
            case R.id.btn_next:
                //核对验证码
                regValidVercode();

//                register();
                break;
        }
    }

    //核对验证码

    private void regValidVercode() {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mPhoneNum);
        params.put("vercode", editVcode.getText().toString());
        params.put("client", "!");

        String url;
        if (mTodo == Constants.NetWork.LOGIN) {
            url = Constants.URL.LOGIN_MSG_VALID_VERCODE;
        } else {
            url = Constants.URL.REQ_VALID_VERCODE;
        }
        APIClient.post(this, url, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String s) {
                super.onResponse(s);
                if (TextUtils.isEmpty(s)) {
                    Log.e("newsList", "onResponse() return is null");
                    return;
                }
                handleResponse(s, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        if (mTodo == Constants.NetWork.LOGIN) {
                            //解析json，保存用户数据，跑转到登录界面
                            User user = GsonUtils.parseJSON(json, User.class);
                            mApplication.saveUserInfo(user);
                            initHealthData(mContext);
                        } else {
                            //跳转到确认注册结果界面
                            Intent intent = new Intent(mContext, RegisterConfirmActivity.class);
                            intent.putExtra("phone_num", mPhoneNum);
                            intent.putExtra("ver_code", editVcode.getText().toString());
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getVerCode() {
        Map<String, String> params = new HashMap<>();

        params.put("mibile", mPhoneNum);
//        params.put()
        APIClient.getVerCode(this, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String s) {
                if (TextUtils.isEmpty(s)) {
                    Log.e("newsList", "onResponse() return is null");
                    return;
                }
                StringResponeBean responeBean = GsonUtils.parseJSON(s, StringResponeBean.class);

                if (responeBean.getCode() == Constants.NetWork.SUCCESS) {
                    String vercode = responeBean.getData() + "";
                    editVcode.setText(vercode);
                }
                Log.e("onRespone", s);
            }
        });
    }


}
