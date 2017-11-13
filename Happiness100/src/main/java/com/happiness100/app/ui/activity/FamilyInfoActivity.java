package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/24.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.model.FamilyInfo;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.ClanRoleEnum;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.happiness100.app.utils.ScoreUtils;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.BitmapUtils;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/24 17:04
 */
public class FamilyInfoActivity extends BaseActivity implements BaseViewInterface {
    private static final String TAG = "FamilyInfoActivity";
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.iv_family_img)
    ImageView ivFamilyImg;
    @Bind(R.id.tv_family_name)
    TextView tvFamilyName;
    @Bind(R.id.tv_creater)
    TextView tvCreater;

    BaseActivity mContext;
    @Bind(R.id.ll_index)
    LinearLayout llManage;
    @Bind(R.id.ll_jion)
    LinearLayout llJion;
    @Bind(R.id.edit_family_remark)
    TextView editFamilyRemark;
    @Bind(R.id.tv_happiness_lv)
    TextView tvHappinessLv;
    @Bind(R.id.pb_happiness)
    ProgressBar pbHappiness;
    @Bind(R.id.tv_happiness_score)
    TextView tvHappinessScore;
    @Bind(R.id.tv_xiaosun_lv)
    TextView tvXiaosunLv;
    @Bind(R.id.pb_xiaosun)
    ProgressBar pbXiaosun;
    @Bind(R.id.tv_xiaosun_score)
    TextView tvXiaosunScore;
    @Bind(R.id.tv_guanai_lv)
    TextView tvGuanaiLv;
    @Bind(R.id.pb_guanai)
    ProgressBar pbGuanai;
    @Bind(R.id.tv_guanai_score)
    TextView tvGuanaiScore;
    @Bind(R.id.tv_cishan_lv)
    TextView tvCishanLv;
    @Bind(R.id.pb_cishan)
    ProgressBar pbCishan;
    @Bind(R.id.tv_cishan_score)
    TextView tvCishanScore;
    @Bind(R.id.view_family_score)
    LinearLayout viewFamilyScore;
    @Bind(R.id.title_view_right)
    RelativeLayout titleViewRight;
    private String mClanId;
    private FamilyInfo mFamilyInfo;

    private int mRole;
    private FamilyIndex mFamilyIndex;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_info);
        ButterKnife.bind(this);
        mContext = this;
        Intent data = getIntent();
        if (data != null) {
            mRole = data.getIntExtra("clanRole", 0);
            mFamilyIndex = (FamilyIndex) getIntent().getParcelableExtra("familyIndex");
        }
        initView();
        initData();
    }

    @OnClick({R.id.title_view_back, R.id.btn_jion_family, R.id.btn_exit_family, R.id.iv_family_img, R.id.title_view_right})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.title_view_right:
                updateGonggao();
                break;
            case R.id.iv_family_img:
                intent = intent.setClass(mContext, Family_HeadView_Activity.class);
                intent.putExtra("clanId", mClanId);
                intent.putExtra("clanRole", mRole);
                String url = mApplication.getImgUri() + "clanLogo/" + mFamilyInfo.getClanLogo();
                if (mUri != null) {
                    intent.putExtra("imgUri", mUri);

                } else {
                    intent.putExtra("imgUrl", url);
                }

                intent.putExtra("type", 1);
                startActivityForResult(intent, 1);
                break;
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.btn_jion_family:
                applyJionFamily();
                break;
            case R.id.btn_exit_family:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "提示", "是否推出" + mFamilyInfo.getName() + "家族？", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        exitFamily();
                    }

                    @Override
                    public void exectEditEvent(String editText) {
                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {
                    }
                });
                break;
        }
    }

    private void updateGonggao() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId + "");
        params.put("gong_gao", editFamilyRemark.getText().toString() + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_MODIFY_CLAN_GONG_GAO, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        mContext.showToast("修改成功");
                    }
                });
            }
        });

    }

    private void exitFamily() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_R_QUIT_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        ToastUtils.shortToast(mContext, "你已退出家族");
                        freshFamilyList();

                    }
                });
            }
        });
    }

    private void freshFamilyList() {
        Map<String, String> params = new HashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        APIClient.post(mContext, Constants.URL.GET_CLIAN_LIST, params, new BaseVolleyListener(mContext) {
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
                        Log.e(TAG, "parsRespone:" + json);
                        Type type = new TypeToken<ArrayList<FamilyBaseInfo>>() {
                        }.getType();
                        List<FamilyBaseInfo> familyBaseInfos = GsonUtils.parseJSONArray(json, type);
                        //如果家族列表为空，家族界面显示创建家族和搜索家族

                        if (familyBaseInfos == null || familyBaseInfos.size() == 0) {//TODO
//                            FamilyFragment familyFragment = (FamilyFragment) getSupportFragmentManager().getFragments().get(0);
//                            familyFragment.
                            mApplication.getUser().setFamilyBaseInfos(null);
                        } else {
                            mApplication.getUser().setFamilyBaseInfos(familyBaseInfos);
                            for (FamilyBaseInfo familyBaseInfo : familyBaseInfos) {
                                if (familyBaseInfo.isIsDefault() == true) {
                                    mApplication.getUser().setDefaultFamilyBaseInfo(familyBaseInfo);

                                }
                            }
                        }

                        Intent intent = new Intent();
                        intent.putExtra("type", Constants.ResultCode.EXIT_FAMILY);
                        setResult(RESULT_OK, intent);
                        mContext.finish();
                    }
                });
            }
        });
    }


    //申请加入家族
    private void applyJionFamily() {
        Intent intent = new Intent(mContext, SendApplyToFamilyActivity.class);
        intent.putExtra("clanId", mClanId);
        startActivity(intent);
    }

    @Override
    public void initView() {
        titleViewTitle.setText("家族");
        mClanId = getIntent().getStringExtra("clanId");
        if (mClanId == null) {
            mClanId = SharedPreferencesContext.getInstance().getSharedPreferences().getString("clanId", mClanId);
        }
        if (mClanId == null) {
            mClanId = mApplication.getUser().getDefaultClanId();
        }

        if (mRole != 0 && mRole != ClanRoleEnum.NORMAL.getCode()) {
            titleViewRight.setVisibility(View.VISIBLE);
        }



        //初始化积分
        if (mFamilyIndex != null && mFamilyIndex.getVals() != null) {
            int scoreHappiness = mFamilyIndex.getVals().get(0);
            tvHappinessLv.setText("幸福LV:" + ScoreUtils.getLv(scoreHappiness));
            int hMax = ScoreUtils.getUpLvExp(scoreHappiness);
            pbHappiness.setMax(hMax);
            pbHappiness.setProgress(scoreHappiness);
            tvHappinessScore.setText(scoreHappiness + "/" + hMax);

            int scoreXiaosun = mFamilyIndex.getVals().get(1);
            tvXiaosunLv.setText("孝顺LV:" + ScoreUtils.getLv(scoreXiaosun));
            int xMax = ScoreUtils.getUpLvExp(scoreXiaosun);
            pbXiaosun.setMax(xMax);
            pbXiaosun.setProgress(scoreXiaosun);
            tvXiaosunScore.setText(scoreXiaosun + "/" + xMax);

            int scoreGuanAi = mFamilyIndex.getVals().get(2);
            tvGuanaiLv.setText("关爱LV:" + ScoreUtils.getLv(scoreGuanAi));
            int gMax = ScoreUtils.getUpLvExp(scoreGuanAi);
            pbGuanai.setMax(gMax);
            pbGuanai.setProgress(scoreGuanAi);
            tvGuanaiScore.setText(scoreGuanAi + "/" + gMax);

            int scoreCishan = mFamilyIndex.getVals().get(3);
            tvCishanLv.setText("慈善LV:" + ScoreUtils.getLv(scoreCishan));
            int cMax = ScoreUtils.getUpLvExp(scoreCishan);
            pbCishan.setMax(cMax);
            pbCishan.setProgress(scoreCishan);
            tvCishanScore.setText(scoreCishan + "/" + cMax);

        } else {
            viewFamilyScore.setVisibility(View.GONE);
        }
        //根据界面类型 初始化界面

        String viewType = getIntent().getStringExtra("type");
        if (viewType != null && viewType.equals("index")) {

            if (mRole == ClanRoleEnum.NORMAL.getCode()) {
//                editFamilyRemark.setKeyListener(null);
                editFamilyRemark.setFocusable(false);
            }
            mClanId = mApplication.getUser().getDefaultClanId();
            llManage.setVisibility(View.VISIBLE);
            llJion.setVisibility(View.GONE);

        } else {
            editFamilyRemark.setFocusable(false);
            llManage.setVisibility(View.GONE);
            llJion.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        APIClient.postWithSessionId(mContext, Constants.URL.PREVIEW_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        mFamilyInfo = GsonUtils.parseJSON(json, FamilyInfo.class);
                        tvFamilyName.setText("家族：" + mFamilyInfo.getName());
                        tvCreater.setText("创建者：" + mFamilyInfo.getCreater());
                        editFamilyRemark.setText(mFamilyInfo.getGongGao());

                        UILUtils.displayImageWithRounder(mApplication.getImgUri() + "clanLogo/" + mFamilyInfo.getClanLogo(), ivFamilyImg, 60);
                    }
                });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode标示请求的标示   resultCode表示有数据
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    if (data != null) {
                        mUri = data.getParcelableExtra("img");
                        ivFamilyImg.setImageBitmap(BitmapUtils.getBitmapFromUri(mContext, mUri));
                    }
                    break;
            }
        }
    }


}
