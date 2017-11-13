package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/16.
 */

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
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
 * 作者：justin on 2016/8/16 15:16
 */
public class UserInfo_PersonSign_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.personSign_content)
    EditText mPersonSignContent;
    @Bind(R.id.personSign_count)
    TextView mPersonSignCount;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_personsign);
        ButterKnife.bind(this);
        initView();
    }

    void initView() {
        mTitleViewTitle.setText("个性签名");
        mTextBack.setText("返回");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText("保存");
        mPersonSignContent.setText(mApplication.getUser().getPersonSign());
        mPersonSignContent.setSelection(mPersonSignContent.getText().length());
        mPersonSignContent.addTextChangedListener(ChangeListener);
        mPersonSignCount.setText("" + (30 - mPersonSignContent.getText().length()));
    }

    TextWatcher ChangeListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            mPersonSignCount.setText("" + (30 - (start + count)));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick({R.id.title_view_back, R.id.title_view_right,R.id.edit_touch_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                postPersonSign();
                break;
            case R.id.edit_touch_view:
                mPersonSignContent.requestFocus();
                InputMethodManager manage = (InputMethodManager) mPersonSignContent.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manage.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
                break;
        }
    }

    void postPersonSign()
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("personSign",""+mPersonSignContent.getText().toString());
        APIClient.post(mContext, Constants.URL.UPDATE_PERSON_SIGN, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        ToastUtils.shortToast(mContext,"修改成功");
                        mApplication.getUser().setPersonSign(mPersonSignContent.getText().toString());
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }
}
