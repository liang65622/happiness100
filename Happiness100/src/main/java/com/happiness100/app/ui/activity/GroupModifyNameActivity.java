package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/5.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Discussion;

/**
 * 作者：jiangsheng on 2016/9/5 21:31
 */
public class GroupModifyNameActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.edit)
    EditText mEdit;
    String mId;
    String mBackStr;//返回显示文字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_name_modify);
        ButterKnife.bind(this);
        initView();
    }

    void initView()
    {

        Intent intent = getIntent();
        mBackStr = intent.getStringExtra("back");
        mId = intent.getStringExtra("id");
        mTextRight.setText("确定");
        mTextRight.setVisibility(View.VISIBLE);
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextBack.setText(mBackStr == null ?"返回":mBackStr);
        mEdit.setText(intent.getStringExtra("name") == null?"":intent.getStringExtra("name"));
        mEdit.setSelection(mEdit.getText().toString().length());
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                LoadDialog.show(mContext);
                final String name = mEdit.getText().toString();

                RongIM.getInstance().setDiscussionName(mId, name, new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        LoadDialog.dismiss(mContext);
                        ModifyGroup(mId,name);
                    }
                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        LoadDialog.dismiss(mContext);
                    }
                });

                break;
        }
    }

    void ModifyGroup(final String groupId,final String name)
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("discus_id",groupId);
        params.put("opt_type","3");
        params.put("discus_name",name);
        APIClient.post(mContext, Constants.URL.SYN_GROUP, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        RongIM.getInstance().refreshDiscussionCache(new Discussion(groupId,name));
                        Intent it =new Intent();
                        it.putExtra("name",name);
                        setResult(RESULT_OK,it);
                        finish();
                    }
                });
            }
        });
    }
}
