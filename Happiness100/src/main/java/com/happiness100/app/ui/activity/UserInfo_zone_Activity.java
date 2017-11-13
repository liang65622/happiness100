package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.DialogZoneSelector;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/17 18:12
 */
public class UserInfo_zone_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.zone_text)
    TextView mZoneText;
    DialogZoneSelector mDialog_zone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_zone);
        ButterKnife.bind(this);
        initView();
    }

    void initView() {
        mTitleViewTitle.setText("填写验证码");
        mTextBack.setText("返回");
        mZoneText.setText(mApplication.getUser().getZone());
    }

    @OnClick({R.id.title_view_back, R.id.zone_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.zone_view:
                openZoneSelector();
                break;
        }
    }

    void openZoneSelector()
    {
        if (mDialog_zone != null && mDialog_zone.isShowing()) {
            mDialog_zone.dismiss();
        }
        if (mDialog_zone == null) {
            mDialog_zone = new DialogZoneSelector((Activity) mContext,R.style.MMTheme_DataSheet_BottomIn,new DialogZoneSelector.PriorityListener()
            {
                @Override
                public void refreshPriorityUI(String Provice,String City)
                {
                    mZoneText.setText(Provice+" "+City);
                    mDialog_zone.dismiss();
                    postZone();
                }
            });
        }
        mDialog_zone.show();
    }

    void postZone()
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("zone",mZoneText.getText().toString());
        APIClient.post(mContext, Constants.URL.UPDATE_ZONE, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        mApplication.getUser().setZone(mZoneText.getText().toString());
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }
}
