package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/11.
 */

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.ClearWriteEditText;
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
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 作者：jiangsheng on 2016/8/11 10:27
 */
public class UserInfo_modifyNickname_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextSave;
    @Bind(R.id.edit_nickname)
    ClearWriteEditText mEditNickname;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;

    Activity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_nickname);
        ButterKnife.bind(this);
        initView();
        mContext = this;
    }

    private void initView() {
        mTitleViewTitle.setText("昵称");
        mTextBack.setText("取消");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextSave.setVisibility(View.VISIBLE);
        mTextSave.setText("保存");
        User user = mApplication.getUser();
        mEditNickname.setText(user.getNickname());
        mEditNickname.setSelection(user.getNickname().length());
    }


    @OnClick({R.id.title_view_back,R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case  R.id.title_view_right:
                String nickname = mEditNickname.getText().toString();
                if (nickname.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "昵称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                postModifyNickName(nickname);
                break;
        }
    }

    private void postModifyNickName(final String nick) {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("nickname",nick);
        APIClient.post(mContext, Constants.URL.UPDATE_NICK_NAME, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        mApplication.getUser().setNickname(nick);
                        User user = mApplication.getUser();
                        if (RongIM.getInstance() != null)
                        {
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(user.getXf()+"",user.getNickname(), Uri.parse(user.getHeadImageUri())));
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }

}
