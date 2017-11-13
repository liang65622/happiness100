package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/30.
 */

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.util.Log;
import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.ToastUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/30 16:49
 */
public class SendApplyToFamilyActivity extends BaseActivity implements BaseViewInterface {
    private static final String TAG = "SendApplyToFamilyActivity";
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.edit_apply_msg)
    EditText editApplyMsg;
    private String mClanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_apply_to_family);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("申请加入家族");
    }

    @Override
    public void initData() {
        mClanId = getIntent().getStringExtra("clanId");
    }

    @OnClick({R.id.title_view_back, R.id.btn_send_apply_msg})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.btn_send_apply_msg:
                applyJoinFamily();
                break;
        }
    }


    private void applyJoinFamily() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        params.put("note", editApplyMsg.getText().toString());
        APIClient.postWithSessionId(mContext, Constants.URL.APP_JOIN_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                Log.e(TAG, "APP_JOIN_CLAN:" + json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Log.e(TAG, "APP_JOIN_CLAN parseResponse:" + json);
                        if (json.equals(Constants.NetWork.ALREADY_INVITE + "")) {
                            DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "提示", "你已经被此家族邀请，确认加入此家族吗？", new DialogWithYesOrNoUtils.DialogCallBack() {
                                @Override
                                public void exectEvent() {
                                    agreeJoinFamily(mClanId);
                                }

                                @Override
                                public void exectEditEvent(String editText) {
                                }

                                @Override
                                public void updatePassword(String oldPassword, String newPassword) {
                                }
                            });
                        } else {
                            ToastUtils.shortToast(mContext, "发送成功 ！");
                            mContext.finish();
                        }
                    }
                });

            }
        });
    }

    private void agreeJoinFamily(String clanId) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("clan_id", clanId);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_INVITE_AGREE_JOIN, params, new BaseVolleyListener(mContext) {
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
                        showToast("加入成功");
                    }
                });
            }
        });
    }
}