package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/27.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.ClearWriteEditText;
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
 * 作者：jiangsheng on 2016/9/27 15:10
 */
public class SettingAccountEmailEditActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.newemail_edit)
    ClearWriteEditText mNewemailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_email_edit);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent it = getIntent();
        mTextBack.setText(it.getStringExtra("back") == null?"返回":it.getStringExtra("back"));
        mTitleViewTitle.setText("修改邮箱地址");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mImageRight.setVisibility(View.GONE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText("保存");
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                if (!EmailUtils.getInstance().checkEmail(mNewemailEdit.getText().toString())) {
                    ToastUtils.shortToast(mContext, "不是合法的邮箱格式");
                    return;
                }
                postBindEmail();
                break;
        }
    }

    void postBindEmail() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("mail", mNewemailEdit.getText().toString());
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
                        alterDialog.setMessage("一封验证邮件已发送至"+mNewemailEdit.getText().toString()+",请登录你的邮箱查收并通过验证.");
                        alterDialog.setCancelable(true);

                        alterDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mApplication.getUser().setEmail(mNewemailEdit.getText().toString());
                                SharedPreferences.Editor edit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
                                edit.putString(Constants.UserInfo.EMAIL, mApplication.getUser().getEmail());
                                edit.commit();
                                finish();
                            }
                        });

                        alterDialog.show();
                    }
                });
            }
        });
    }
}
