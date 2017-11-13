package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/1.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.Friend;
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
 * 作者：jiangsheng on 2016/9/1 20:27
 */
public class AddFriendVerificationActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.edit_content)
    EditText mEdit;
    Friend mNeedAddFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_verification);
        ButterKnife.bind(this);
        initView();
    }

    void initView()
    {
        mNeedAddFriend = (Friend)getIntent().getParcelableExtra("friend");
        String backstr =getIntent().getStringExtra("back");
        if (backstr == null || backstr.isEmpty())
            backstr = "返回";
        mTextBack.setText(backstr);
        mTitleViewTitle.setText("朋友验证");
        mTextRight.setText("发送");
        mTextRight.setVisibility(View.VISIBLE);
        mTitleViewRight.setVisibility(View.VISIBLE);
        mEdit.setText("我是"+mApplication.getUser().getNickname());
        mEdit.setSelection(mEdit.getText().length());
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                postAddFriend();
                break;
        }
    }

    void postAddFriend()
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("user_id",mNeedAddFriend.getXf()+"");
        params.put("app_name",mApplication.getUser().getNickname());
        params.put("note",mEdit.getText().toString());
        APIClient.post(mContext, Constants.URL.APPLY_FRIENDS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext,"发送成功");
                        Intent it = new Intent();
                        it.putExtra("xf",mNeedAddFriend.getXf()+"");
                        setResult(RESULT_OK,it);
                        finish();
                    }
                });
            }
        });
    }
}
