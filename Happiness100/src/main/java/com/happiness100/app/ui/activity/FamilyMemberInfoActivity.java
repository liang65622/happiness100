package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/24.
 */

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.model.HealthData;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.ClanRoleEnum;
import com.happiness100.app.ui.bean.HealthItemStatusEnum;
import com.happiness100.app.ui.bean.HealthStatusEnum;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.MyDonutProgress;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.DateUtils;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;

import static com.happiness100.app.R.id.btn_iving_gifts;
import static com.happiness100.app.R.id.rl_more;

/**
 * 作者：justin on 2016/8/24 17:19
 */
public class FamilyMemberInfoActivity extends BaseActivity implements BaseViewInterface {

    private static final String TAG = "FamilyMemberInfoActivity";
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.iv_member_img)
    ImageView ivMemberImg;
    @Bind(R.id.tv_member_name)
    TextView tvMemberName;
    @Bind(R.id.tv_happiness_num)
    TextView tvHappinessNum;
    @Bind(R.id.content_item_address_text)
    TextView contentItemAddressText;
    @Bind(R.id.content_item_address_go)
    ImageView contentItemAddressGo;
    @Bind(R.id.content_item_address)
    RelativeLayout contentItemAddress;
    @Bind(R.id.tv_area_title)
    TextView tvAreaTitle;
    @Bind(R.id.tv_area)
    TextView tvArea;
    @Bind(R.id.rl_area)
    RelativeLayout rlArea;
    @Bind(R.id.tv_more_title)
    TextView tvMoreTitle;
    @Bind(R.id.btn_send_message)
    Button btnSendMessage;
    @Bind(btn_iving_gifts)
    Button btnIvingGifts;
    @Bind(R.id.btn_update_nickname)
    Button btnUpdateNickname;
    @Bind(R.id.btn_remove_from_family)
    Button btnRemoveFromFamily;
    @Bind(R.id.btn_set_manager)
    Button btnSetManager;
    @Bind(R.id.back_picture)
    ImageView backPicture;
    @Bind(R.id.text_back)
    TextView textBack;
    @Bind(R.id.title_view_back)
    LinearLayout titleViewBack;
    @Bind(R.id.image_right)
    ImageView imageRight;
    @Bind(R.id.text_right)
    TextView textRight;
    @Bind(R.id.title_view_right)
    RelativeLayout titleViewRight;
    @Bind(R.id.title_view)
    RelativeLayout titleView;
    @Bind(R.id.tv_health_score)
    TextView tvHealthScore;
    @Bind(R.id.tv_tips_score)
    TextView tvTipsScore;
    @Bind(R.id.tv_score_unit)
    TextView tvScoreUnit;
    @Bind(R.id.donut_progress_heartbeat)
    MyDonutProgress donutProgressHeartbeat;
    @Bind(R.id.tv_xintiao_data)
    TextView tvXintiaoData;
    @Bind(R.id.tv_xintiao_s)
    TextView tvXintiaoS;
    @Bind(R.id.tv_xintiao_unit)
    TextView tvXintiaoUnit;
    @Bind(R.id.ll_xintiao_data)
    LinearLayout llXintiaoData;
    @Bind(R.id.tv_xintiao_comp)
    TextView tvXintiaoComp;
    @Bind(R.id.tv_xintiao_status)
    TextView tvXintiaoStatus;
    @Bind(R.id.tv_xintiao_title)
    TextView tvXintiaoTitle;
    @Bind(R.id.view_heartbeat)
    LinearLayout viewHeartbeat;
    @Bind(R.id.donut_progress_blood_pressure)
    MyDonutProgress donutProgressBloodPressure;
    @Bind(R.id.tv_xueya_data)
    TextView tvXueyaData;
    @Bind(R.id.tv_xueya_s)
    TextView tvXueyaS;
    @Bind(R.id.tv_xueya_unit)
    TextView tvXueyaUnit;
    @Bind(R.id.ll_xueya_data)
    LinearLayout llXueyaData;
    @Bind(R.id.tv_xueya_comp)
    TextView tvXueyaComp;
    @Bind(R.id.tv_xueya_status)
    TextView tvXueyaStatus;
    @Bind(R.id.tv_blood_pressure_score)
    TextView tvBloodPressureScore;
    @Bind(R.id.view_blood_pressure)
    LinearLayout viewBloodPressure;
    @Bind(R.id.donut_progress_blood_sugar)
    MyDonutProgress donutProgressBloodSugar;
    @Bind(R.id.tv_xuetang_data)
    TextView tvXuetangData;
    @Bind(R.id.tv_xuetang_s)
    TextView tvXuetangS;
    @Bind(R.id.tv_xuetang_unit)
    TextView tvXuetangUnit;
    @Bind(R.id.ll_xuetang_data)
    LinearLayout llXuetangData;
    @Bind(R.id.tv_xuetang_comp)
    TextView tvXuetangComp;
    @Bind(R.id.tv_xuetang_status)
    TextView tvXuetangStatus;
    @Bind(R.id.rl_xuetang)
    RelativeLayout rlXuetang;
    @Bind(R.id.iv_xuetang_title)
    TextView ivXuetangTitle;
    @Bind(R.id.view_blood_sugar)
    LinearLayout viewBloodSugar;
    @Bind(R.id.donut_progress_weight)
    MyDonutProgress donutProgressWeight;
    @Bind(R.id.tv_tizhong_data)
    TextView tvTizhongData;
    @Bind(R.id.tv_tizhong_s)
    TextView tvTizhongS;
    @Bind(R.id.tv_tizhong_unit)
    TextView tvTizhongUnit;
    @Bind(R.id.ll_tizhong_data)
    LinearLayout llTizhongData;
    @Bind(R.id.tv_tizhong_comp)
    TextView tvTizhongComp;
    @Bind(R.id.tv_tizhong_status)
    TextView tvTizhongStatus;
    @Bind(rl_more)
    RelativeLayout rlMore;
    @Bind(R.id.btn_zufu)
    ImageView btnZufu;
    @Bind(R.id.tv_health_tips)
    TextView tvHealthTips;
    @Bind(R.id.tv_member_note)
    TextView tvMemberNote;

    private FamilyIndex.Member mUserBean;
    private String mClanId;
    private int mClanRole;
    HealthData mHealthData;
    List<Timer> mTimerList = new ArrayList<Timer>();
    private HealthData.HDataBean mHData;
    private Drawable mDrawablerawableOffUp;
    private Drawable mDrawablerawableOffDown;

    Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        ButterKnife.bind(this);
        mUserBean = getIntent().getParcelableExtra("memberInfo");
        mContext = this;
        //家族ID
        mClanId = SharedPreferencesContext.getInstance().getSharedPreferences().getString("clanId", null);
        mClanRole = getIntent().getIntExtra("clanRole", 0);
//        initHealthData();
        initView();
        initData();
    }

    @OnClick({R.id.rl_more,R.id.rl_area,R.id.title_view_back, R.id.btn_send_message, R.id.btn_iving_gifts, R.id.btn_update_nickname, R.id.btn_remove_from_family, R.id.btn_set_manager, R.id.btn_zufu, R.id.content_item_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.btn_send_message:
                //打开会话界面
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(mContext, mUserBean.getUserid() + "", mUserBean.getNickname());
                }
                break;
            case btn_iving_gifts:
                //送礼物
                showToast("该功能暂未开放");
                break;
            case R.id.btn_update_nickname:
                //修改称谓
                DialogWithYesOrNoUtils.getInstance().showEditDialog(mContext, "修改称谓", "", "请填写称谓", "保存", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                    }

                    @Override
                    public void exectEditEvent(String editText) {
                        updateNickname(editText);
                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {
                    }
                });
                break;
            case R.id.btn_set_manager:
                //提升管理员


                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "提示", "是否将" + mUserBean.getNickname() + btnSetManager.getText() + "?", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {

                        if (!btnSetManager.getText().toString().contains("降")) {
                            setManager();
                        } else {
                            setNormal();
                        }
                    }

                    @Override
                    public void exectEditEvent(String editText) {
                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {
                    }
                });
                break;
            case R.id.btn_remove_from_family:
                //移出家族
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "提示", "是否将" + mUserBean.getNickname() + "移出家族？", new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        removeFromFamily();
                    }

                    @Override
                    public void exectEditEvent(String editText) {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.btn_zufu:
                zufu();
                break;
            case R.id.content_item_address:
                showToast("该功能暂未开放");
//                Intent RemarkIntent = new Intent(mContext, RemarkActivity.class);
////                RemarkIntent.putExtra("friend", mfriend);
////                RemarkIntent.putExtra("back", mTitleViewTitle.getText().toString());
//                startActivityForResult(RemarkIntent, Constants.ActivityIntentIndex.RemarkActivityIndex);
                break;
            case R.id.rl_area:
                showToast("该功能暂未开放");
                break;
            case R.id.rl_more:
                showToast("该功能暂未开放");
                break;
        }
    }

    private void updateNickname(final String cwNote) {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        params.put("user_id", mUserBean.getUserid() + "");
        params.put("cw_note", cwNote);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_MODIFY_CW_NOTE, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext, "修改成功");
                        tvMemberName.setText(mUserBean.getNickname());
                        tvMemberNote.setText("称谓：" + cwNote);
                    }
                });
            }
        });
    }

    private void setNormal() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        params.put("user_id", mUserBean.getUserid() + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_ROLE_SET_DOWN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext, "已将" + mUserBean.getNickname() + "降为普通成员");
                        btnSetManager.setText("提升为管理员");
                    }
                });
            }
        });

    }

    private void setManager() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        params.put("user_id", mUserBean.getUserid() + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_ROLE_SET_UP, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext, "已将" + mUserBean.getNickname() + "提升为管理员");
                        btnSetManager.setText("降职为普通成员");
                    }
                });
            }
        });
    }

    private void removeFromFamily() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        params.put("user_id", mUserBean.getUserid() + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_TICK_OUT_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext, "已将" + mUserBean.getNickname() + "移出家族");
                        Intent intent = new Intent();
                        intent.putExtra("control_type", "update_members");
                        setResult(RESULT_OK);
                        mContext.finish();
                    }
                });
            }
        });
    }

    //取消任务
    private void cancelTimer() {
        for (Timer timer : mTimerList) {
            timer.cancel();
        }
    }

    private void initHealthData() {
        cancelTimer();//取消任务
//        swipeRefreshLayout.setRefreshing(true);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("mobile", mApplication.getUser().getMobile());

        APIClient.post(mContext, Constants.URL.EALTH_INDEX, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 1000);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 1000);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String parseJson) {
                        Gson gson = new Gson();
//                        mHealthData = GsonUtils.parseJSON(json, HealthData.class);
                        Log.e("parseJson", "" + parseJson);
                        mHealthData = gson.fromJson(parseJson, HealthData.class);
                        Log.e("parseJson", "" + mHealthData);

//                        mApplication.setHealthData(mHealthData);
                        initView();

                        if (mHealthData != null) {
                            initHealthView();
                        }
                    }
                });
            }
        });
    }

    private void initHealthView() {
        mHData = mHealthData.getHData();

        if (mHData == null) {
            if (DateUtils.getWeekOfDate(new Date()) == 6 || DateUtils.getWeekOfDate(new Date()) == 7) {
                tvHealthTips.setText("休息日不更新健康数据");
            } else if (mHealthData.getHealthScore() > 0) {
                tvHealthTips.setText(HealthStatusEnum.getNameByCode(mHealthData.getHealthStatus()));
            } else {
                tvHealthTips.setText("今天还未进行健康检测");
            }
            return;
        }

        //血压
        donutProgressBloodPressure.setProgress(0);
        donutProgressBloodPressure.setContentText(0 + "");
        List<Float> perList = mHealthData.getPer();

        if (perList == null) {
            return;
        }


        int bloodPressure = perList.get(0).intValue();
        startAnimProgress(donutProgressBloodPressure, bloodPressure, mHData.getXueYa1());

        //血糖
        int bloodSugarPer1 = mHealthData.getPer().get(1).intValue();
        donutProgressBloodSugar.setProgress(0);
        startAnimProgress(donutProgressBloodSugar, bloodSugarPer1, mHData.getXueTang1());
        //心跳
        int heartbeatPer1 = mHealthData.getPer().get(4).intValue();
        donutProgressHeartbeat.setProgress(0);
        startAnimProgress(donutProgressHeartbeat, heartbeatPer1, mHData.getXinLv1());
        //体重
        int weightPer1 = mHealthData.getPer().get(5).intValue();
        donutProgressWeight.setProgress(0);
        startAnimProgress(donutProgressWeight, weightPer1, mHData.getTiZhong());
        donutProgressWeight.setContentText(mHData.getTiZhong() + "");
        //健康总分
        startHealthScoreAnim(mHealthData.getHealthScore());

        tvXueyaData.setText("!");
        startTxtScoreAnim(1, mHData.getXueYa1());
        startTxtScoreAnim(2, mHData.getXueTang1());
        startTxtScoreAnim(3, mHData.getXinLv1());
        startTxtScoreAnim(4, mHData.getTiZhong());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTextTimer();
            }
        }, 3000);

    }


    @Override
    public void initView() {

        mDrawablerawableOffUp = getResources().getDrawable(R.drawable.ic_score_up);
        mDrawablerawableOffUp.setBounds(0, 0, mDrawablerawableOffUp.getMinimumWidth(), mDrawablerawableOffUp.getMinimumHeight());
        mDrawablerawableOffDown = getResources().getDrawable(R.drawable.ic_score_down);
        mDrawablerawableOffDown.setBounds(0, 0, mDrawablerawableOffDown.getMinimumWidth(), mDrawablerawableOffDown.getMinimumHeight());


        if (getUser().getXf() == mUserBean.getUserid()) {
            btnZufu.setVisibility(View.GONE);
        }
        String xfStr = mUserBean.getUserid() + "";

        xfStr = xfStr.substring(xfStr.length() - 1, xfStr.length());
        String headImgUri = mApplication.getImgUri() + "headImage/" + xfStr + "/" + mUserBean.getHeadImage();

        UILUtils.displayImageWithRounder(headImgUri, ivMemberImg, 20);

        titleViewTitle.setText("成员信息");
        String disName = mUserBean.getNickname();

        tvMemberName.setText(disName);
        tvMemberNote.setText("称谓：" + mUserBean.getCwNote());
        tvHappinessNum.setText("幸福号：xf" + mUserBean.getUserid());


        //根据用户权限和成员权限初始化操作按钮
        if (mClanRole == ClanRoleEnum.CREATER.getCode()) {
            if (mUserBean.getClanRole() == ClanRoleEnum.NORMAL.getCode()) {
                btnRemoveFromFamily.setVisibility(View.VISIBLE);
                btnSetManager.setVisibility(View.VISIBLE);
                btnUpdateNickname.setVisibility(View.VISIBLE);
                btnSetManager.setText("提升为管理员");
            } else if (mUserBean.getClanRole() == ClanRoleEnum.MANAGER.getCode()) {
                btnRemoveFromFamily.setVisibility(View.VISIBLE);
                btnUpdateNickname.setVisibility(View.VISIBLE);
                btnSetManager.setVisibility(View.VISIBLE);
                btnSetManager.setText("降为普通成员");
            } else {

            }
        } else if (mClanRole == ClanRoleEnum.MANAGER.getCode()) {
            if (mUserBean.getClanRole() == ClanRoleEnum.NORMAL.getCode()) {
                btnUpdateNickname.setVisibility(View.VISIBLE);
                btnRemoveFromFamily.setVisibility(View.VISIBLE);
            } else if (mUserBean.getClanRole() == ClanRoleEnum.MANAGER.getCode()) {
            } else {
                btnSetManager.setVisibility(View.GONE);
            }
        }
        if (mUserBean.getUserid() == mApplication.getUser().getXf()) {
            btnSendMessage.setVisibility(View.GONE);
            btnIvingGifts.setVisibility(View.GONE);
        }

    }

    int i = 0;

    private void startTextTimer() {
        final Timer textTimer = new Timer();
        textTimer.schedule(new TimerTask() {

                               @Override
                               public void run() {
                                   mContext.runOnUiThread(new Runnable() {

                                                              @Override
                                                              public void run() {
                                                                  if (donutProgressBloodPressure != null) {

                                                                      //状态 和 颜色
                                                                      tvXueyaStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(i)));
                                                                      tvXueyaStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(i))));


                                                                      tvXuetangStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(i + 2)));
                                                                      tvXuetangStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(i + 2))));

                                                                      tvXintiaoStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(i + 4)));
                                                                      tvXintiaoStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(i + 4))));


                                                                      if (i == 0) {
                                                                          donutProgressBloodPressure.setProgress(((Float) mHealthData.getPer().get(i)).intValue());
                                                                          donutProgressBloodSugar.setProgress(((Float) mHealthData.getPer().get(2)).intValue());
                                                                          donutProgressHeartbeat.setProgress(((Float) mHealthData.getPer().get(4)).intValue());

                                                                          float xueyaOff = mHealthData.getOff().get(0);
                                                                          freshCompText(tvXueyaComp, xueyaOff, "kpa");

                                                                          float xuetangOff = mHealthData.getOff().get(2);
                                                                          freshCompText(tvXuetangComp, xuetangOff, "mg.dl");

                                                                          float xintiaoOff = mHealthData.getOff().get(4);
                                                                          freshCompText(tvXintiaoComp, xintiaoOff, "次/分");

                                                                          double xuaya1 = mHData.getXueYa1();
                                                                          freshScoreText(tvXueyaData, tvXueyaS, xuaya1);

                                                                          double xuetang1 = mHData.getXueTang1();
                                                                          freshScoreText(tvXuetangData, tvXuetangS, xuetang1);
                                                                          double xintiao1 = mHData.getXinLv1();
                                                                          freshScoreText(tvXintiaoData, tvXintiaoS, xintiao1);


                                                                          tvBloodPressureScore.setText("血压-舒张压");
                                                                          ivXuetangTitle.setText("血糖-餐前");
                                                                          tvXintiaoTitle.setText("心跳-上午");
                                                                      } else {
                                                                          //血压
                                                                          float xueyaOff = mHealthData.getOff().get(i);
                                                                          freshCompText(tvXueyaComp, xueyaOff, "kpa");

                                                                          float xuetangOff = mHealthData.getOff().get(i + 2);
                                                                          freshCompText(tvXuetangComp, xuetangOff, "mg.dl");

                                                                          float xintiaoOff = mHealthData.getOff().get(i + 4);
                                                                          freshCompText(tvXintiaoComp, xintiaoOff, "次/分");


                                                                          double xuaya2 = mHData.getXueYa2();
                                                                          freshScoreText(tvXueyaData, tvXueyaS, xuaya2);
                                                                          double xuetang2 = mHData.getXueTang2();
                                                                          freshScoreText(tvXuetangData, tvXuetangS, xuetang2);
                                                                          double xintiao2 = mHData.getXinLv2();
                                                                          freshScoreText(tvXintiaoData, tvXintiaoS, xintiao2);

                                                                          donutProgressBloodPressure.setProgress(((Float) mHealthData.getPer().get(1)).intValue());
                                                                          donutProgressBloodSugar.setProgress(((Float) mHealthData.getPer().get(3)).intValue());
                                                                          donutProgressHeartbeat.setProgress(((Float) mHealthData.getPer().get(5)).intValue());

                                                                          ivXuetangTitle.setText("血糖-餐后");
                                                                          tvBloodPressureScore.setText("血压-收缩压");
                                                                          tvXintiaoTitle.setText("心跳-下午");
                                                                      }
                                                                      if (i == 0) {
                                                                          i = 1;
                                                                      } else {
                                                                          i = 0;
                                                                      }
                                                                  } else

                                                                  {
                                                                      textTimer.cancel();
                                                                  }
                                                              }
                                                          }
                                   );
                               }
                           }
                , 10, 4000);
        mTimerList.add(textTimer);
    }


    //驱动文本动画
    private void startTxtScoreAnim(final int i, final double itemScore) {

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int score = 0;

            @Override
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int flag = 0;
                        TextView textView = null;
                        TextView sText = null;
                        switch (i) {
                            case 1:
                                textView = tvXueyaData;
                                sText = tvXueyaS;
                                break;
                            case 2:
                                textView = tvXuetangData;
                                sText = tvXuetangS;
                                break;
                            case 3:
                                textView = tvXintiaoData;
                                sText = tvXintiaoS;
                                break;
                            case 4:
                                textView = tvTizhongData;
                                sText = tvTizhongS;
                                break;
                        }
                        if (score <= itemScore) {
                            if (textView != null) {
                                textView.setText(score++ + "");
                            }
                        } else {
                            if (textView != null) {
                                freshScoreText(textView, sText, itemScore);
                            }
                            timer.cancel();
                        }
                    }
                });
            }
        }, 10, 20);
        mTimerList.add(timer);
    }

    private void freshScoreText(TextView tvXueyaData, TextView tvXueyaS, double xuaya1) {
        if (xuaya1 == 0) {
            tvXueyaData.setText("--");
            tvXueyaUnit.setText("");
        } else {
            String[] datas = (xuaya1 + "").split("\\.");
            tvXueyaData.setText(datas[0]);
            tvXueyaS.setText("." + datas[1]);
        }
    }

    private void startHealthScoreAnim(final double healthScore) {

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int score = 0;

            @Override
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int flag = 0;
                        if (score <= healthScore) {
                            if (tvHealthScore != null) {
                                tvHealthScore.setText(score++ + "");
                            }
                        } else {
                            if (tvHealthScore != null) {
                                tvHealthScore.setText(healthScore + "");
                                freshCompText(tvTipsScore, mHealthData.getScoreOff(), "");
                                if (mHealthData.getHealthScore() > 0) {
                                    tvHealthTips.setText(HealthStatusEnum.getNameByCode(mHealthData.getHealthStatus()));
                                } else {
                                    tvHealthTips.setText("今天还未进行健康检测");
                                }
                            }
                            timer.cancel();
                        }
                    }
                });
            }
        }, 10, 20);
        mTimerList.add(timer);
    }


    private void freshCompText(TextView tvXueyaComp, float xueyaOff, String unit) {
        tvXueyaComp.setText(xueyaOff + unit);
        if (xueyaOff > 0) {
            tvXueyaComp.setVisibility(View.VISIBLE);
            tvXueyaComp.setCompoundDrawables(mDrawablerawableOffUp, null, null, null);
        } else if (xueyaOff == 0) {
            tvXueyaComp.setVisibility(View.GONE);
        } else {
            tvXueyaComp.setVisibility(View.VISIBLE);
            tvXueyaComp.setCompoundDrawables(mDrawablerawableOffDown, null, null, null);
        }
    }

    private void startAnimProgress(final MyDonutProgress myDonutProgress, final int bloodPressure, final double scoreInt) {

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int score = 0;

            @Override
            public void run() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (myDonutProgress != null) {
                            int flag = 0;
                            if (score <= scoreInt) {
                                myDonutProgress.setContentText(score++ + "");
                                flag++;
                            }
                            if (myDonutProgress.getProgress() < bloodPressure) {
                                myDonutProgress.setProgress(myDonutProgress.getProgress() + 1);
                                flag++;
                            }
                            if (flag == 0) {
                                timer.cancel();
                            }
                        } else {
                            timer.cancel();
                        }
                    }
                });
            }
        }, 10, 20);
        mTimerList.add(timer);
    }

    @Override
    public void initData() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", mUserBean.getUserid() + "");
        params.put("mobile", mUserBean.getPhone());
        params.put("clan_id", mClanId);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_VIEW_OTHER_USER, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        Gson gson = new Gson();
//                        mHealthData = GsonUtils.parseJSON(json, HealthData.class);
                        mHealthData = gson.fromJson(json, HealthData.class);
                        initView();
                        if (mHealthData != null) {
                            initHealthView();
                        }
                    }
                });

            }
        });
    }

    public void zufu() {

        Map<String, String> params = new HashMap<>();
        params.put("user_id", mUserBean.getUserid() + "");
        params.put("clan_id", mClanId);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_VIEW_OTHER_USER_DIAN_ZAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
//                        mHealthData = GsonUtils.parseJSON(json, HealthData.class);
                        mContext.showToast("祝福成功");
                        btnZufu.setVisibility(View.GONE);
                    }
                });

            }
        });
    }
}
