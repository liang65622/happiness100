package com.happiness100.app.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.R;
import com.happiness100.app.model.HealthData;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.activity.HealthChartActivity;
import com.happiness100.app.ui.bean.HealthItemStatusEnum;
import com.happiness100.app.ui.bean.HealthStatusEnum;
import com.happiness100.app.ui.widget.MyDonutProgress;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.DateUtils;
import com.justin.utils.GsonUtils;
import com.justin.utils.UILUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HealthFragment extends BaseFragment implements BaseViewInterface, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "HealthFragment";
    private static final int FRESH_TEXT_TIMER = 0X2545;
    @Bind(R.id.tv_my_health_info_title)
    TextView tvMyHealthInfoTitle;
    @Bind(R.id.iv_headimg)
    ImageView ivHeadimg;
    @Bind(R.id.view_my_health_info)
    RelativeLayout viewMyHealthInfo;
    @Bind(R.id.view_health)
    LinearLayout viewHealth;
    @Bind(R.id.donut_progress_blood_pressure)
    MyDonutProgress donutProgressBloodPressure;
    @Bind(R.id.tv_blood_pressure_score)
    TextView tvBloodPressureScore;
    @Bind(R.id.view_blood_pressure)
    LinearLayout viewBloodPressure;
    @Bind(R.id.donut_progress_blood_sugar)
    MyDonutProgress donutProgressBloodSugar;
    @Bind(R.id.view_blood_sugar)
    LinearLayout viewBloodSugar;
    @Bind(R.id.donut_progress_heartbeat)
    MyDonutProgress donutProgressHeartbeat;
    @Bind(R.id.view_heartbeat)
    LinearLayout viewHeartbeat;
    @Bind(R.id.donut_progress_weight)
    MyDonutProgress donutProgressWeight;
    @Bind(R.id.view_experience_tips)
    LinearLayout viewExperienceTips;
    @Bind(R.id.iv_sign1)
    ImageView ivSign1;
    @Bind(R.id.iv_sign2)
    ImageView ivSign2;
    @Bind(R.id.iv_sign3)
    ImageView ivSign3;
    @Bind(R.id.iv_sign4)
    ImageView ivSign4;
    @Bind(R.id.iv_sign5)
    ImageView ivSign5;
    @Bind(R.id.iv_xuetang_title)
    TextView ivXuetangTitle;
    @Bind(R.id.tv_xintiao_title)
    TextView tvXintiaoTitle;
    @Bind(R.id.tv_day_1)
    TextView tvDay1;
    @Bind(R.id.tv_day_2)
    TextView tvDay2;
    @Bind(R.id.tv_day_3)
    TextView tvDay3;
    @Bind(R.id.tv_day_4)
    TextView tvDay4;
    @Bind(R.id.tv_day_5)
    TextView tvDay5;


    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static final int STATE_NOMORE = 3;
    public static final int STATE_PRESSNONE = 4;// 正在下拉但还没有到刷新的状态
    public static int mState = STATE_NONE;
    @Bind(R.id.tv_health_score)
    TextView tvHealthScore;
    @Bind(R.id.tv_tips_score)
    TextView tvTipsScore;
    @Bind(R.id.tv_score_unit)
    TextView tvScoreUnit;
    @Bind(R.id.tv_health_tips)
    TextView tvHealthTips;
    @Bind(R.id.tv_xueya_data)
    TextView tvXueyaData;
    @Bind(R.id.tv_xueya_unit)
    TextView tvXueyaUnit;
    @Bind(R.id.tv_xueya_comp)
    TextView tvXueyaComp;
    @Bind(R.id.tv_xueya_status)
    TextView tvXueyaStatus;
    @Bind(R.id.view_pressure_group)
    RelativeLayout viewPressureGroup;
    @Bind(R.id.tv_xuetang_data)
    TextView tvXuetangData;
    @Bind(R.id.tv_xuetang_unit)
    TextView tvXuetangUnit;
    @Bind(R.id.tv_xuetang_comp)
    TextView tvXuetangComp;
    @Bind(R.id.tv_xuetang_status)
    TextView tvXuetangStatus;
    @Bind(R.id.tv_xintiao_data)
    TextView tvXintiaoData;
    @Bind(R.id.tv_xintiao_unit)
    TextView tvXintiaoUnit;
    @Bind(R.id.tv_xintiao_comp)
    TextView tvXintiaoComp;
    @Bind(R.id.tv_xintiao_status)
    TextView tvXintiaoStatus;
    @Bind(R.id.tv_tizhong_data)
    TextView tvTizhongData;
    @Bind(R.id.tv_tizhong_unit)
    TextView tvTizhongUnit;
    @Bind(R.id.tv_tizhong_comp)
    TextView tvTizhongComp;
    @Bind(R.id.tv_tizhong_status)
    TextView tvTizhongStatus;
    @Bind(R.id.rl_health_chat)
    RelativeLayout rlHealthChat;
    @Bind(R.id.view_cut_line)
    View viewCutLine;
    @Bind(R.id.ll_xueya_data)
    LinearLayout llXueyaData;
    @Bind(R.id.ll_xuetang_data)
    LinearLayout llXuetangData;
    @Bind(R.id.ll_xintiao_data)
    LinearLayout llXintiaoData;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.tv_xueya_s)
    TextView tvXueyaS;
    @Bind(R.id.tv_xuetang_s)
    TextView tvXuetangS;
    @Bind(R.id.rl_xuetang)
    RelativeLayout rlXuetang;
    @Bind(R.id.tv_xintiao_s)
    TextView tvXintiaoS;
    @Bind(R.id.tv_tizhong_s)
    TextView tvTizhongS;
    @Bind(R.id.ll_tizhong_data)
    LinearLayout llTizhongData;


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FRESH_TEXT_TIMER:
                    freshText();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;
    private HealthData.HDataBean mHData;
    private int vpIndex;
    private Drawable mDrawablerawableOffUp;
    private Drawable mDrawablerawableOffDown;

    /**
     * Create a new instance of CountingFragment, providing "num" as an
     * argument.
     */


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    private int mNum;


    HealthData mHealthData;

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

        mContext = getActivity();

        mDrawablerawableOffUp = getResources().getDrawable(R.drawable.ic_score_up);
        mDrawablerawableOffUp.setBounds(0, 0, mDrawablerawableOffUp.getMinimumWidth(), mDrawablerawableOffUp.getMinimumHeight());
        mDrawablerawableOffDown = getResources().getDrawable(R.drawable.ic_score_down);
        mDrawablerawableOffDown.setBounds(0, 0, mDrawablerawableOffDown.getMinimumWidth(), mDrawablerawableOffDown.getMinimumHeight());

        mHealthData = mApplication.getHealthData();
        initData();
    }

    Activity mContext;
    View rootView;

    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_health, container, false);
        }
        MyDonutProgress donutProgress;
        ButterKnife.bind(this, rootView);
        swipeRefreshLayout.setRefreshing(true);
        cancelTimer();
        swipeRefreshLayout.setRefreshing(false);
        Log.e(TAG, "onCreateView");
        swipeRefreshLayout.setOnRefreshListener(this);

//        initView();
////        initProgressView();
//        if (mHealthData != null) {
//            initSignView();
//        }

        return rootView;
    }

    @OnClick({R.id.donut_progress_blood_pressure, R.id.donut_progress_blood_sugar, R.id.donut_progress_heartbeat, R.id.donut_progress_weight, R.id.helpItem})
    public void onClick(View view) {
        Intent intent = new Intent(mContext, HealthChartActivity.class);

        switch (view.getId()) {
            case R.id.donut_progress_blood_pressure:
                intent.putExtra("type", 0);
                break;
            case R.id.donut_progress_blood_sugar:
                intent.putExtra("type", 1);
                break;
            case R.id.donut_progress_heartbeat:
                intent.putExtra("type", 2);
                break;
            case R.id.donut_progress_weight:
                intent.putExtra("type", 3);
                break;
            case R.id.helpItem:
                Uri uri = Uri.parse("http://119.29.102.152:8080/right/health_help.html");
                Intent itUri = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(itUri);
                if (1 == 1)
                    return;
                break;
        }

        mContext.startActivity(intent);
    }

    @Override
    public void onRefresh() {
//        if (mState == STATE_REFRESH) {
//            return;
//        }
//        // 设置顶部正在刷新
//        setSwipeRefreshLoadingState();
//        mState = STATE_REFRESH;
//       initData();

        initHealthData();
    }

    @OnClick(R.id.rl_health_chat)
    public void onClick() {
        Intent intent = new Intent(mContext, HealthChartActivity.class);
        mContext.startActivity(intent);
    }

    class BannerPager extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position % mImageViews.length]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
//            if(mImageViews[position % mImageViews.length].getParent()==null) {
            ((ViewPager) container).addView(mImageViews[position % mImageViews.length], 0);
//            }
            return mImageViews[position % mImageViews.length];
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume()");
        cancelTimer();
        User user = mApplication.getUser();
        tvMyHealthInfoTitle.setText(user.getNickname() + "的健康数据");
        Log.e(TAG, "DateUtils.getWeekOfDate(new Date()):" + DateUtils.getWeekOfDate(new Date()));
        if (mHealthData == null) {
            return;
        }
        if (DateUtils.getWeekOfDate(new Date()) == 6 || DateUtils.getWeekOfDate(new Date()) == 7) {
            tvHealthTips.setText("休息日不更新健康数据");
        } else if (mHealthData.getHealthScore() > 0) {
            tvHealthTips.setText(HealthStatusEnum.getNameByCode(mHealthData.getHealthStatus()));
        } else {
            tvHealthTips.setText("今天还未进行健康检测");
        }

        UILUtils.displayImageWithRounder(user.getHeadImageUri(), ivHeadimg, 12);
        Log.e(TAG, "onResume()");

//        判断是否隔日
//        if (mHealthData != null && mHealthData.getHData() != null) {
//            initProgressView();
//        } else {
        initHealthData();
//        }
    }

    private void initHealthData() {

        mHandler.removeCallbacks(runnable);
        if (swipeRefreshLayout == null) {
            return;
        }
        cancelTimer();//取消任务
        swipeRefreshLayout.setRefreshing(true);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("mobile", mApplication.getUser().getMobile());

        APIClient.post(mContext, Constants.URL.EALTH_INDEX, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Gson gson = new Gson();
                        mHealthData = GsonUtils.parseJSON(json, HealthData.class);
                        mHealthData = gson.fromJson(json, HealthData.class);
                        mApplication.setHealthData(mHealthData);
                        initView();

                        if (mHealthData != null) {
                            initSignView();
                            initProgressView();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelTimer();
        mHandler.removeCallbacks(runnable);
        Log.e(TAG, "onPause()");
    }

    private void cancelTimer() {
        Log.e(TAG, "timer size:" + mTimerList.size());
        for (Timer timer : mTimerList) {
            timer.cancel();
            timer.cancel();
        }
        mTimerList.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void initView() {


        if (tvMyHealthInfoTitle == null) {
            return;
        }
        User user = mApplication.getUser();
        tvMyHealthInfoTitle.setText(user.getNickname() + "的健康数据");
        UILUtils.displayImageWithRounder(user.getHeadImageUri(), ivHeadimg, 12);

        if (mHealthData != null) {
            List<Integer> dayList = mHealthData.getDays();
            if (dayList != null) {
                tvDay1.setText(dayList.get(0) + "");
                tvDay2.setText(dayList.get(1) + "");
                tvDay3.setText(dayList.get(2) + "");
                tvDay4.setText(dayList.get(3) + "");
                tvDay5.setText(dayList.get(4) + "");
            }
        }
        float healthScore = (float) mHealthData.getHealthScore();
        if (healthScore == 0) {
            tvHealthScore.setText("--");
        } else {
            tvHealthScore.setText(healthScore + "");
        }

        tvTipsScore.setVisibility(View.VISIBLE);

        if (DateUtils.getWeekOfDate(new Date()) == 6 || DateUtils.getWeekOfDate(new Date()) == 7) {
            tvHealthTips.setText("休息日不更新健康数据");
        } else if (mHealthData.getHealthScore() > 0) {
            tvHealthTips.setText(HealthStatusEnum.getNameByCode(mHealthData.getHealthStatus()));
        } else {
            tvHealthTips.setText("今天还未进行健康检测");
        }


    }


    @Override
    public void initData() {

    }

    private void initProgressView() {
        initSignView();
        mHData = mHealthData.getHData();

        if (mHData == null || donutProgressBloodPressure == null) {
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
        startAnimProgress(donutProgressWeight, 95, mHData.getTiZhong());
        donutProgressWeight.setContentText(mHData.getTiZhong() + "");
        //健康总分
        startHealthScoreAnim(mHealthData.getHealthScore());

        tvXueyaData.setText("");
        startTxtScoreAnim(1, mHData.getXueYa1());
        startTxtScoreAnim(2, mHData.getXueTang1());
        startTxtScoreAnim(3, mHData.getXinLv1());
        startTxtScoreAnim(4, mHData.getTiZhong());

        mHandler.postDelayed(runnable, 5000);//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startTextTimer();
//            }
//        }, 5000);
    }

    int i = 0;
    int scoreTest = 0;

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
                                if (DateUtils.getWeekOfDate(new Date()) == 6 || DateUtils.getWeekOfDate(new Date()) == 7) {
                                    tvHealthTips.setText("休息日不更新健康数据");
                                } else if (mHealthData.getHealthScore() > 0) {
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
                                float xueyaOff = mHealthData.getOff().get(0);
                                freshCompText(tvXueyaComp, xueyaOff, "kpa");

                                float xuetangOff = mHealthData.getOff().get(2);
                                freshCompText(tvXuetangComp, xuetangOff, "mg.dl");

                                float xintiaoOff = mHealthData.getOff().get(4);
                                freshCompText(tvXintiaoComp, xintiaoOff, "次/分");

                                tvXueyaStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(0)));
                                tvXueyaStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(0))));


                                tvXuetangStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(2)));
                                tvXuetangStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(2))));

                                tvXintiaoStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(4)));
                                tvXintiaoStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(4))));


                            }
                            timer.cancel();
                        }
                    }
                });
            }
        }, 10, 15);
        mTimerList.add(timer);
    }


    List<Timer> mTimerList = new ArrayList<Timer>();


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            mHandler.sendEmptyMessage(FRESH_TEXT_TIMER);
            mHandler.postDelayed(this, 4200);
        }
    };

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
                                                                          donutProgressBloodPressure.setProgress(((Float) mHealthData.getPer().get(0)).intValue());
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
                                                                  } else {
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


    public void freshText() {
        if (donutProgressBloodPressure != null) {

            //状态 和 颜色
            tvXueyaStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(i)));
            tvXueyaStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(i))));


            tvXuetangStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(i + 2)));
            tvXuetangStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(i + 2))));

            tvXintiaoStatus.setText(HealthItemStatusEnum.getNameByCode(mHealthData.getStatus().get(i + 4)));
            tvXintiaoStatus.setTextColor(getResources().getColor(HealthItemStatusEnum.getColor(mHealthData.getStatus().get(i + 4))));


            if (i == 0) {
                donutProgressBloodPressure.setProgress(((Float) mHealthData.getPer().get(0)).intValue());
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
        }
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

    private int getStatusColor(Integer status) {
        if (status == HealthItemStatusEnum.NORMAL.getStatus()) {
            return R.color.health_normal;
        } else if (status == HealthItemStatusEnum.LOW.getStatus()) {
            return R.color.health_low;
        } else if (status == HealthItemStatusEnum.HIGHT.getStatus()) {
            return R.color.health_hight;
        } else if (status == HealthItemStatusEnum.NONE.getStatus()) {
            return R.color.health_normal;
        }
        return R.color.health_normal;
    }

    void initSignView() {
        if (ivSign1 == null || ivSign2 == null || ivSign3 == null || ivSign4 == null || ivSign5 == null) {
            return;
        }
        List<Boolean> signList = mHealthData.getSigns();
        int signSize = signList.size();
        for (int i = 0; i < 5; i++) {
            if (i < signSize) {
                switch (i) {
                    case 0:
                        if (signList.get(i)) {
                            ivSign1.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_sign));
                        } else {
                            ivSign1.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        }
                        break;
                    case 1:
                        if (signList.get(i)) {
                            ivSign2.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_sign));
                        } else {
                            ivSign2.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        }
                        break;
                    case 2:
                        if (signList.get(i)) {
                            ivSign3.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_sign));
                        } else {
                            ivSign3.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        }
                        break;
                    case 3:
                        if (signList.get(i)) {
                            ivSign4.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_sign));
                        } else {
                            ivSign4.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        }
                        break;
                    case 4:
                        if (signList.get(i)) {
                            ivSign5.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_sign));
                        } else {
                            ivSign5.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        }
                        break;
                }
            } else {
                switch (i) {
                    case 0:
                        ivSign1.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        break;
                    case 1:
                        ivSign2.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        break;
                    case 2:
                        ivSign3.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        break;
                    case 3:
                        ivSign4.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        break;
                    case 4:
                        ivSign5.setImageDrawable(getResources().getDrawable(R.drawable.ic_health_no_sign));
                        break;
                }

            }
        }
    }

    /**
     * 设置顶部正在加载的状态
     */
    private void setSwipeRefreshLoadingState() {
    }

}
