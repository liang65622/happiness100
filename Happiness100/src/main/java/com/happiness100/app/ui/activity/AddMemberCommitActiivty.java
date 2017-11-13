package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/31.
 */

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.MatchContactsUser;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.UILUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 邀请加入家族 界面
 * 作者：justin on 2016/8/31 10:18
 */
public class AddMemberCommitActiivty extends BaseActivity implements BaseViewInterface {
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.iv_member_img)
    ImageView ivMemberImg;
    @Bind(R.id.tv_member_name)
    TextView tvMemberName;
    @Bind(R.id.tv_member_phone)
    TextView tvMemberPhone;
    @Bind(R.id.tv_beifen_content)
    TextView tvBeifenContent;
    @Bind(R.id.tv_name_remark)
    TextView tvNameRemark;
    @Bind(R.id.edit_name_remark)
    EditText editNameRemark;
    private MatchContactsUser mUser;
    private String mClanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_commit);
        ButterKnife.bind(this);
        mContext = this;
        mClanId = getIntent().getStringExtra("clanid");
        if(mClanId==null){
            mClanId =mApplication.getUser().getDefaultClanId();
        }
        mUser = getIntent().getParcelableExtra("user");

        initView();
        initData();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("添加成员");
    }

    @Override
    public void initData() {
        UILUtils.displayImage(mApplication.getHeadImage(mUser.getUserId() + "", mUser.getHeadImage()), ivMemberImg);
        tvMemberName.setText(mUser.getName());
        tvMemberPhone.setText(mUser.getMobile());
    }

    @OnClick({R.id.title_view_back, R.id.rl_select_beifen, R.id.btn_add_member_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.rl_select_beifen:
                break;
            case R.id.btn_add_member_commit:
                addMemberCommit();
                break;
        }
    }

    private void addMemberCommit() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", mUser.getUserId()+"");
        params.put("clan_id", mClanId);
        params.put("bei_fen", 0 + "");
        params.put("cw_note", editNameRemark.getText().toString());

        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_INVITE_CFM_JOIN_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        setResult(RESULT_OK);
                        mContext.finish();
                    }
                });
            }
        });
    }
}
