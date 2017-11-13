package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/23.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyDynamic;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.adapter.FamilyDynamicAdapter;
import com.happiness100.app.ui.widget.EmptyLayout;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/23 12:07
 */
public class FamilyDynamicActivity extends BaseActivity implements BaseViewInterface {
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.tv_y_income)
    TextView tvYIncome;
    @Bind(R.id.tv_happiness_score)
    TextView tvHappinessScore;
    @Bind(R.id.lv_family_dynamic)
    ListView lvFamilyDynamic;
    @Bind(R.id.error_layout)
    EmptyLayout errorLayout;
    Activity mContext;
    private String mClanId;
    private List<FamilyDynamic.XingFuDongTaiListBean> mDynamics = new ArrayList<>();
    private FamilyDynamicAdapter mFamilyDynamicAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_dynamic);
        ButterKnife.bind(this);
        mContext = this;
        mClanId = SharedPreferencesContext.getInstance().getSharedPreferences().getString("clanId", null);
        if(mClanId==null)
        {
            mClanId= mApplication.getUser().getDefaultClanId();
        }
//        mClanId = getIntent().getStringExtra("clanId");
        initView();
        initData();
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        mContext.finish();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("家族动态");
        mFamilyDynamicAdapter = new FamilyDynamicAdapter(this);
        lvFamilyDynamic.setAdapter(mFamilyDynamicAdapter);
    }

    @Override
    public void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_XING_FU_DTON_TAI, params, new BaseVolleyListener(mContext) {
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
                        FamilyDynamic familyDynamic = GsonUtils.parseJSON(json,FamilyDynamic.class);
                        mDynamics = familyDynamic.getXingFuDongTaiList();
                        if (mDynamics.size() <= 0) {
                            errorLayout.setErrorType(EmptyLayout.NODATA);

//                            tvHappinessScore.setText(familyDynamic.get);
//                            mLvFamilyDynamic.setVisibility(View.GONE);
                        }else {
                            tvYIncome.setText(familyDynamic.getYesdayJf()+"");
                            tvHappinessScore.setText(familyDynamic.getTodayJf()+"");
                            mFamilyDynamicAdapter.setDynamics(mDynamics);
                            mFamilyDynamicAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }


}
