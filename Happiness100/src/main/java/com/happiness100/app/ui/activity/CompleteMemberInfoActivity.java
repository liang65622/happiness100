package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/31.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.Apply;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.BeifenEnum;
import com.happiness100.app.ui.bean.YesOrNoEnum;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * 作者：justin on 2016/8/31 15:03
 */
public class CompleteMemberInfoActivity extends BaseActivity implements BaseViewInterface {

    private static final int REQUEST_CODE = 1;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.text_right)
    TextView textRight;
    @Bind(R.id.tv_beifen_content)
    TextView tvBeifenContent;
    @Bind(R.id.edit_name_remark)
    EditText editNameRemark;
    @Bind(R.id.title_view_right)
    RelativeLayout titleViewRight;
    private String mClanId;
    private int mBeifen;
    private String mNote;
    private Apply mApply;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_member_info);
        ButterKnife.bind(this);
        mContext = this;
        mClanId = getIntent().getStringExtra("clanId");
        if(mClanId==null){
            mClanId = mApplication.getUser().getDefaultClanId();
        }
        mApply = (Apply) getIntent().getParcelableExtra("apply");
        initView();
        initData();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("完善资料");
        textRight.setText("保存");
        titleViewRight.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.title_view_back, R.id.title_view_right, R.id.rl_select_beifen})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.title_view_right:
                agree();
                break;
            case R.id.rl_select_beifen:
                startActivityForResult(new Intent(mContext, SelectBeifenActivity.class), REQUEST_CODE);
                break;
        }
    }

    private void agree() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", mApply.getUserId()+"");
        params.put("clan_id", mClanId);
        mBeifen = 0;
        params.put("bei_fen", mBeifen + "");
        mNote = editNameRemark.getText().toString();
        params.put("cw_note", mNote);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_ACCEPT_JOIN_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext,"操作成功！");
                        mApply.setIsAgree(YesOrNoEnum.YES.getCode());
                        setResult(RESULT_OK);
                        mContext.finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mBeifen = data.getIntExtra("beifen", mBeifen);
            tvBeifenContent.setText(BeifenEnum.getNameByCode(mBeifen));
        }
    }

    @OnClick(R.id.title_view_right)
    public void onClick() {
    }
}


