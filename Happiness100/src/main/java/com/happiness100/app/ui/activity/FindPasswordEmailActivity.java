package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/18 21:02
 */
public class FindPasswordEmailActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.edit)
    ClearWriteEditText mEdit;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_email);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("找回密码");
        mTextBack.setText("返回");
    }

    @OnClick({R.id.title_view_back, R.id.nextBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.nextBtn:
                postfindPassword();
                break;
        }
    }

    void postfindPassword()
    {
        final String email = mEdit.getText().toString();

        if (!EmailUtils.getInstance().checkEmail(email))
        {
            ToastUtils.shortToast(mContext, "不是合法的邮箱格式");
            return;
        }

        Map<String,String> params = new LinkedHashMap<>();
        params.put("mail",mEdit.getText().toString());
        APIClient.post(mContext, Constants.URL.FIND_PASSWORD_MAIL, params, new BaseVolleyListener(mContext, new LoadDialog(mContext,true,"")) {
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
                        itent.putExtra("email",mEdit.getText().toString());
                        startActivity(itent);
                    }
                });
            }
        });
    }

}
