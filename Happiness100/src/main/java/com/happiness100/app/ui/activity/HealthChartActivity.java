package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.model.HealthDetail;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.ClanRoleEnum;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.UILUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/15 18:23
 */
public class HealthChartActivity extends BaseActivity implements BaseViewInterface {

    private static final Integer XUEYA1 = 1;
    private static final Integer XUEYA2 = 2;
    private static final Integer XUETANG1 = 3;
    private static final Integer XUETANG2 = 4;
    private static final Integer XINTIAO1 = 5;
    private static final Integer XINTIAO2 = 6;
    private static final Integer TIZHONG = 7;
    private static final String TAG = "HealthChartActivity";
    @Bind(R.id.vp_contacts_family)
    ViewPager vpContactsFamily;
    @Bind(R.id.iv_add_family_member)
    ImageView ivAddFamilyMember;
    @Bind(R.id.tv_xintiao)
    RadioButton tvXintiao;
    @Bind(R.id.tv_xueya)
    RadioButton tvXueya;
    @Bind(R.id.tv_xuetang)
    RadioButton tvXuetang;
    @Bind(R.id.tv_tizhong)
    RadioButton tvTizhong;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    private List<GridView> mGvMatchList = new ArrayList<>();
    @Bind(R.id.tv_my_health_info_title)
    TextView tvMyHealthInfoTitle;
    @Bind(R.id.iv_headimg)
    ImageView ivHeadimg;
    private LineChart mLineChart;
    private Typeface mTf;
    private Activity mContext;
    private List<Float> mXueyaHightList = new ArrayList<>();
    private List<Float> mXueyaLowList = new ArrayList<>();

    private List<Float> mXueTangList1 = new ArrayList<>();
    private List<Float> mXueTangList2 = new ArrayList<>();


    private List<Float> mXinTiaoList1 = new ArrayList<>();
    private List<Float> mXinTiaoList2 = new ArrayList<>();

    private List<Float> mTiZhong = new ArrayList<>();


    private Float mMaxXueya2;
    private Float mMinXueya1;
    private float mMaxXueTang2;
    private float mMinXueTang1;

    private float mMaxXinTiao;
    private float mMinXinTiao;
    private float mMaxTiZhong;
    private float mMinTiZhong;

    Map<Integer, List<Float>> mDataMap = new HashMap<>();
    private HealthDetail mHealthDetail;
    private FamilyIndex mFamilyIndex;
    private int mType;
    private ContactsFamilyAdapter mContactsFamilyAdapter;
    private LayoutInflater mLayoutInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_chart);

        ButterKnife.bind(this);
        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mLineChart = (LineChart) findViewById(R.id.chart);
        mContext = this;
        mType = getIntent().getIntExtra("type", 0);
        initData();
        initView();

        mFamilyIndex = mApplication.getFamilyIndex();
        mLayoutInflater = getLayoutInflater();
//        initChart(null);
    }

    @Override
    public void initView() {

        User user = mApplication.getUser();
        tvMyHealthInfoTitle.setText(user.getNickname() + "的健康数据");
//        UILUtils.displayImage(user.getHeadImageUri(),ivHeadimg);
        UILUtils.displayImageWithRounder(user.getHeadImageUri(), ivHeadimg, 12);

        mContactsFamilyAdapter = new ContactsFamilyAdapter();
        vpContactsFamily.setAdapter(mContactsFamilyAdapter);

    }

    //匹配通讯录的家族ViewPager Adapter
    class ContactsFamilyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mGvMatchList.size();
        }

        public ContactsFamilyAdapter() {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
//            return mImageViews[position % mImageViews.length];
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mGvMatchList.get(position), 0);
            return mGvMatchList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mGvMatchList.get(position));
        }
    }


    @Override
    public void initData() {
        titleViewTitle.setText("健康数据");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("mobile", mApplication.getUser().getMobile());

        APIClient.post(mContext, Constants.URL.EALTH_DETAIL, params, new BaseVolleyListener(mContext) {
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
                        mHealthDetail = GsonUtils.parseJSON(json, HealthDetail.class);
                        if (mHealthDetail.getHDatas() != null) {
                            initChatsData(mHealthDetail);
                            switch (mType) {
                                case 2:
                                    tvXintiao.setChecked(true);
                                    initChart(R.id.tv_xintiao);
                                    break;
                                case 1:
                                    tvXuetang.setChecked(true);
                                    initChart(R.id.tv_xuetang);
                                    break;
                                case 0:
                                    tvXueya.setChecked(true);
                                    initChart(R.id.tv_xueya);
                                    break;
                                case 3:
                                    tvTizhong.setChecked(true);
                                    initChart(R.id.tv_tizhong);
                                    break;
                            }
                        }
                    }
                });
            }
        });

        getFamilyMembers();
    }


    void initChatsData(HealthDetail healthDetail) {
        Float tempXueya1 = 0f;
        Float tempXueya2 = 0f;
        Float tempXueTang1 = 0f;
        Float tempXueTang2 = 0f;
        Float tempXinTiao1 = 0f;
        Float tempXinTiao2 = 0f;
        Float tempTiZhong = 0f;

        mMinXueya1 = 0f;
        mMaxXueya2 = 0f;
        mMinXueTang1 = 0f;
        mMaxXueTang2 = 0f;

        mMaxXinTiao = 0f;
        mMinXinTiao = 0f;

        mMaxTiZhong = 0f;
        mMinTiZhong = 0f;


        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                mXueyaLowList.add(0f);
                mXueyaHightList.add(0f);
                mXueTangList1.add(0f);
                mXueTangList2.add(0f);
                mXinTiaoList1.add(0f);
                mXinTiaoList2.add(0f);
                mTiZhong.add(0f);
            }
            try {
                HealthDetail.HDatasBean databean = healthDetail.getHDatas().get(i);
                if (databean != null) {
                    mXueyaLowList.add(databean.getXueYa1());
                    mXueyaHightList.add(databean.getXueYa2());
                    tempXueya2 = databean.getXueYa2();
                    tempXueya1 = databean.getXueYa1();
                    float tempXueYaMax = tempXueya1 < tempXueya2 ? tempXueya2 : tempXueya1;
                    float tempXueYaMin = tempXueya1 > tempXueya2 ? tempXueya2 : tempXueya1;
                    if (mMaxXueya2 < tempXueYaMax) {
                        mMaxXueya2 = tempXueYaMax;
                    }

                    if (mMinXueya1 > tempXueYaMin) {
                        mMinXueya1 = tempXueYaMin;
                    }

                    if (i == 0) {
                        mMinXueya1 = tempXueya1;
                    } else {
                        if (mMinXueya1 > tempXueya1) {
                            mMinXueya1 = tempXueya1;
                        }
                    }

                    mXueTangList1.add(databean.getXueTang1());
                    mXueTangList2.add(databean.getXueTang2());


                    tempXueTang1 = databean.getXueTang1();
                    tempXueTang2 = databean.getXueTang2();


                    float tempXueTangMax = tempXueTang1 < tempXueTang2 ? tempXueTang2 : tempXueTang1;
                    float tempXueTangMin = tempXueTang1 > tempXueTang2 ? tempXueTang2 : tempXueTang1;

                    if (i == 0) {
                        mMinXueya1 = tempXueTangMin;
                    } else {
                        if (mMinXueya1 > tempXueTangMin) {
                            mMinXueya1 = tempXueTangMin;
                        }
                    }


                    if (mMaxXueTang2 < tempXueTangMax) {
                        mMaxXueTang2 = tempXueTangMax;
                    }


                    mXinTiaoList1.add(databean.getXinLv1());
                    mXinTiaoList2.add(databean.getXinLv2());
                    tempXinTiao1 = databean.getXinLv1();
                    tempXinTiao2 = databean.getXinLv2();

                    float tempXinTiaoMax = tempXinTiao1 < tempXinTiao2 ? tempXinTiao2 : tempXinTiao1;
                    float tempXinTiaoMin = tempXinTiao1 > tempXinTiao2 ? tempXinTiao2 : tempXinTiao1;

                    if (i == 0) {
                        mMinXinTiao = tempXinTiaoMin;
                    } else {
                        if (mMinXinTiao > tempXinTiaoMin) {
                            mMinXinTiao = tempXinTiaoMin;
                        }
                    }


                    if (mMaxXinTiao < tempXinTiaoMax) {
                        mMaxXinTiao = tempXinTiaoMax;
                    }


                    mTiZhong.add(databean.getTiZhong());
                    tempTiZhong = databean.getTiZhong();
                    if (i == 0) {
                        mMinTiZhong = tempTiZhong;
                    } else {
                        if (mMinTiZhong > tempTiZhong) {
                            mMinTiZhong = tempTiZhong;
                        }
                    }

                    if (mMaxTiZhong < tempTiZhong) {
                        mMaxTiZhong = tempTiZhong;
                    }

                } else {
                    mXueyaHightList.add(0F);
                    mXueyaLowList.add(0F);

                    mXueTangList1.add(0F);
                    mXueTangList2.add(0F);

                    mXinTiaoList1.add(0F);
                    mXinTiaoList2.add(0F);
                    mTiZhong.add(0F);
                }


            } catch (Exception e) {

                mXueyaHightList.add(0F);
                mXueyaLowList.add(0F);

                mXueTangList1.add(0F);
                mXueTangList2.add(0F);

                mXinTiaoList1.add(0F);
                mXinTiaoList2.add(0F);
                mTiZhong.add(0F);
            }
        }

        mDataMap.put(XUEYA1, mXueyaLowList);
        mDataMap.put(XUEYA2, mXueyaHightList);
        mDataMap.put(XUETANG1, mXueTangList1);
        mDataMap.put(XUETANG2, mXueTangList2);
        mDataMap.put(XINTIAO1, mXinTiaoList1);
        mDataMap.put(XINTIAO2, mXinTiaoList2);
        mDataMap.put(TIZHONG, mTiZhong);
    }

    void initChart(int viewId) {
        mLineChart.clear();

        String lineLowStr = getLowStr(viewId);//线条说明
        String lineHightStr = getHightStr(viewId);//线条说明

        float maxNum = getMaxNum(viewId);
        float minNum = getMinNum(viewId);

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        List<Float> fList1 = null;
        List<Float> fList2 = null;
        switch (viewId) {
            case R.id.tv_xintiao:
                fList1 = mDataMap.get(XINTIAO1);
                fList2 = mDataMap.get(XINTIAO2);
                break;
            case R.id.tv_xueya:
                fList1 = mDataMap.get(XUEYA1);
                fList2 = mDataMap.get(XUEYA2);
                break;
            case R.id.tv_xuetang:
                fList1 = mDataMap.get(XUETANG1);
                fList2 = mDataMap.get(XUETANG2);
                break;
            case R.id.tv_tizhong:
                fList1 = mDataMap.get(TIZHONG);
                break;
            default:
                return;
        }

        boolean hasNum = false;
        for (int i = 0; i < 6; i++) {
            if (fList1.get(i) != 0) {
                e1.add(new Entry(i, fList1.get(i).floatValue()));
                hasNum = true;
            }
        }
        if (!hasNum) {
            return;
        }

        LineDataSet d1 = new LineDataSet(e1, lineLowStr + "");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[3]);
        d1.setDrawValues(false);


        ArrayList<Entry> e2 = new ArrayList<Entry>();
        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        if (viewId != R.id.tv_tizhong) {
            for (int i = 0; i < 6; i++) {
                if (fList2.get(i) != 0) {
                    e2.add(new Entry(i, fList2.get(i).floatValue()));
                }
            }

            LineDataSet d2 = new LineDataSet(e2, lineHightStr);
            d2.setLineWidth(2.5f);
            d2.setCircleRadius(4.5f);
            d2.setHighLightColor(Color.rgb(255, 255, 255));
            d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
            d2.setValueTextColor(Color.rgb(255, 255, 255));
            d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);

            d2.setDrawValues(true);
            sets.add(d2);
        }


        LineData cd = new LineData(sets);
        //TODO

        mLineChart.setDescription("");
        mLineChart.setBorderColor(Color.rgb(255, 255, 255));
        mLineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
//        mLineChart.setDrawGridBackground(false);
        XAxis xAxis = mLineChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawAxisLine(true);
        xAxis.setGranularity(1);
        xAxis.setAxisMinimum(0f);
        xAxis.setGridColor(Color.rgb(255, 255, 255));
        xAxis.setTextColor(Color.rgb(255, 255, 255));
        xAxis.setAxisLineColor(Color.rgb(255, 255, 255));
        xAxis.setAxisMaximum(6f);
        xAxis.setLabelCount(6);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Float v = value;
                if (v.intValue() == 0 || v.intValue() == 6) {
                    return "";
                }
                return "周" + (v.intValue());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(9, true);//Y轴显示格子数量
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.rgb(255, 255, 255));
        leftAxis.setTextColor(Color.rgb(255, 255, 255));
        //计算最高值

        maxNum = (maxNum + (maxNum - minNum) / 6);
        minNum = (minNum - (maxNum - minNum) / 6);
        if (maxNum == minNum) {
            maxNum = maxNum * 2;
            minNum = 0;
        }

        leftAxis.setAxisMaximum(maxNum);//最高值
        leftAxis.setAxisMinimum(minNum);//最低值
        leftAxis.setAxisLineColor(Color.rgb(255, 255, 255));

        //设置预警线
//        initLimitLine(leftAxis, viewId);

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setTypeface(mTf);

        rightAxis.setLabelCount(9, true);
        rightAxis.setDrawGridLines(true);
        rightAxis.setAxisMaximum(maxNum);
        rightAxis.setAxisLineColor(Color.rgb(255, 255, 255));
        rightAxis.setGridColor(Color.rgb(255, 255, 255));
        rightAxis.setTextColor(Color.rgb(255, 255, 255));
        ;//最高值
        rightAxis.setAxisMinimum(minNum); // 最低值

        // set data
        mLineChart.setData((LineData) cd);
        mLineChart.setMaxVisibleValueCount(9);//刻度格数
        // do not forget to refresh the chart
        // holder.chart.invalidate();
        mLineChart.invalidate();
        mLineChart.animateX(750);
    }

    private float getMinNum(int viewId) {
        switch (viewId) {
            case R.id.tv_xintiao:
                return mMinXinTiao;
            case R.id.tv_xueya:
                return mMinXueya1;
            case R.id.tv_xuetang:
                return mMinXueTang1;
            case R.id.tv_tizhong:
                return mMinTiZhong;
            default:
                return mMinXinTiao;
        }
    }

    private float getMaxNum(int viewId) {
        switch (viewId) {
            case R.id.tv_xintiao:
                return mMaxXinTiao;
            case R.id.tv_xueya:
                return mMaxXueya2;
            case R.id.tv_xuetang:
                return mMaxXueTang2;
            case R.id.tv_tizhong:
                return mMaxTiZhong;
            default:
                return mMaxXinTiao;
        }
    }

    private String getHightStr(int viewId) {
        switch (viewId) {
            case R.id.tv_xintiao:
                return "下午";
            case R.id.tv_xueya:
                return "收缩压";
            case R.id.tv_xuetang:
                return "餐后";
            default:
                return "";
        }
    }

    private String getLowStr(int viewId) {
        switch (viewId) {
            case R.id.tv_xintiao:
                return "上午";
            case R.id.tv_xueya:
                return "舒张压";
            case R.id.tv_xuetang:
                return "餐前";

            case R.id.tv_tizhong:
                return "体重";
            default:
                return "";
        }
    }

    private void initLimitLine(YAxis leftAxis, int viewId) {

        //设置警戒线：
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        leftAxis.removeAllLimitLines();
        float limitLineLow1 = 0;
        float limitLineLow2 = 0;

        float limitLineHight1 = 0;
        float limitLineHight2 = 0;

        switch (viewId) {
            case R.id.tv_xueya:
                limitLineLow1 = mHealthDetail.getNormalData().get(0).get(0).floatValue();
                limitLineLow2 = mHealthDetail.getNormalData().get(0).get(1).floatValue();
                limitLineHight1 = mHealthDetail.getNormalData().get(1).get(0).floatValue();
                limitLineHight2 = mHealthDetail.getNormalData().get(1).get(1).floatValue();
                break;
            case R.id.tv_xuetang:
                limitLineLow1 = mHealthDetail.getNormalData().get(2).get(0).floatValue();
                limitLineLow2 = mHealthDetail.getNormalData().get(2).get(1).floatValue();
                limitLineHight1 = mHealthDetail.getNormalData().get(3).get(0).floatValue();
                limitLineHight2 = mHealthDetail.getNormalData().get(3).get(1).floatValue();
                break;
            case R.id.tv_xintiao:
                limitLineLow1 = mHealthDetail.getNormalData().get(4).get(0).floatValue();
                limitLineLow2 = mHealthDetail.getNormalData().get(4).get(1).floatValue();
                break;
            case R.id.tv_tizhong:
                return;
            default:
                limitLineHight1 = mHealthDetail.getNormalData().get(4).get(0).floatValue();
                limitLineHight2 = mHealthDetail.getNormalData().get(4).get(1).floatValue();
                break;
        }

        LimitLine ll1 = new LimitLine(limitLineLow1, "");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLineColor(R.color.comment_yellow);

//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);
        ll1.setTypeface(tf);

        LimitLine ll2 = new LimitLine(limitLineLow2, "");
        ll2.setLineWidth(2f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLineColor(R.color.common_light_blue);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);
        ll2.setTypeface(tf);


        LimitLine ll3 = new LimitLine(limitLineHight1, "");
        ll3.setLineWidth(2f);
        ll3.enableDashedLine(10f, 10f, 0f);
        ll3.setLineColor(R.color.comment_yellow);
//        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll3.setTextSize(10f);
        ll3.setTypeface(tf);

        LimitLine ll4 = new LimitLine(limitLineHight2, "");
        ll4.setLineWidth(2f);
        ll4.enableDashedLine(10f, 10f, 0f);
        ll4.setLineColor(R.color.common_light_blue);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll4.setTextSize(10f);
        ll4.setTypeface(tf);

        // .. and more styling options

        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        if (viewId != R.id.tv_tizhong) {
            leftAxis.addLimitLine(ll3);
            leftAxis.addLimitLine(ll4);
        }

    }


    @OnClick({R.id.tv_xueya, R.id.tv_xuetang, R.id.tv_xintiao, R.id.tv_tizhong})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_xueya:
                initChart(R.id.tv_xueya);
                break;
            case R.id.tv_xuetang:
                initChart(R.id.tv_xuetang);
                break;
            case R.id.tv_xintiao:
                initChart(R.id.tv_xintiao);
                break;
            case R.id.tv_tizhong:
                initChart(R.id.tv_tizhong);
                break;
        }
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        mContext.finish();
    }

    public void getFamilyMembers() {

        if (mFamilyIndex == null) {
            if (mApplication.getUser().getDefaultClanId() != null) {
                Map<String, String> params = new HashMap<>();
                params.put("clan_id", mApplication.getUser().getDefaultClanId() + "");
                APIClient.postWithSessionId(mContext, Constants.URL.CLAN_INDEX, params, new BaseVolleyListener(mContext) {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        super.onErrorResponse(volleyError);
                    }

                    @Override
                    public void onResponse(String json) {
                        super.onResponse(json);

                        JSONObject responseJO = null;
                        try {
                            responseJO = new JSONObject(json);
                            int responseCode = responseJO.getInt("code");
                            switch (responseCode) {
                                case Constants.NetWork.SUCCESS:
                                    APIClient.handleResponse(mContext, json, new ResponeInterface() {
                                        @Override
                                        public void parseResponse(String json) {
                                            mFamilyIndex = GsonUtils.parseJSON(json, FamilyIndex.class);
//                                ListViewUtils.setListViewHeightBasedOnChildren(mGvEdler);
                                            if (mFamilyIndex.members != null && mFamilyIndex.members.size() > 1) {
                                                vpContactsFamily.setVisibility(View.VISIBLE);
                                                ivAddFamilyMember.setVisibility(View.GONE);
                                                initMatchUserInfo();
                                            }
                                        }
                                    });
                                    break;
                                default:

                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }


                    }
                });
            }
        }
    }

    //初始化配置到的家族信息
    private void initMatchUserInfo() {

        int matchFamilySize = mFamilyIndex.members.size();

        int vpSize;

        if (matchFamilySize % 4 > 0) {
            vpSize = matchFamilySize / 4 + 1;
        } else {
            vpSize = matchFamilySize / 4;
        }


        //将匹配的信息对象 分页
        for (int i = 0; i < vpSize; i++) {
            List<FamilyIndex.Member> list = new ArrayList<>();

            for (int j = 0; j < 4; j++) {
                if ((i * 4 + j) < matchFamilySize) {
                    list.add(mFamilyIndex.members.get(i * 4 + j));
                }
            }

            GridView gridView = (GridView) getLayoutInflater().inflate(R.layout.gridview_family_match_contacts, null);
            final SeniorityGridViewAdapter adapter = new SeniorityGridViewAdapter(list);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<FamilyIndex.Member> members = ((SeniorityGridViewAdapter) parent.getAdapter()).getMembers();
                    Intent intent = new Intent(mContext, FamilyMemberInfoActivity.class);
                    intent.putExtra("clanRole", mFamilyIndex.getClanRole());
                    intent.putExtra("memberInfo", members.get(position));
                    startActivity(intent);
                }
            });

            mGvMatchList.add(gridView);
        }
        mContactsFamilyAdapter.notifyDataSetChanged();

        //int vpMatchSize = familyBaseInfos.size()/4+;
    }

    class SeniorityGridViewAdapter extends BaseAdapter {
        private Drawable mDrawableCreater;
        private Drawable mDrawableManager;
        List<FamilyIndex.Member> members = new ArrayList<>();

        public SeniorityGridViewAdapter(List<FamilyIndex.Member> members) {
            mDrawableCreater = getResources().getDrawable(R.drawable.ic_mark_creater);
            mDrawableCreater.setBounds(0, 0, mDrawableCreater.getMinimumWidth(), mDrawableCreater.getMinimumHeight());
            mDrawableManager = getResources().getDrawable(R.drawable.ic_mark_manager);
            mDrawableManager.setBounds(0, 0, mDrawableManager.getMinimumWidth(), mDrawableManager.getMinimumHeight());
            this.members = members;
        }

        public SeniorityGridViewAdapter() {

        }

        public List<FamilyIndex.Member> getMembers() {
            return members;
        }

        public void setMembers(List<FamilyIndex.Member> members) {
            this.members = members;
            Log.e(TAG, "members:" + members.size());
        }

        @Override
        public int getCount() {
            return members.size();
        }

        @Override
        public FamilyIndex.Member getItem(int i) {
            return members.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.gridview_item_chart_family_member, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            //如果是最后项，显示添加按钮
            FamilyIndex.Member member = members.get(i);
            Log.e(TAG, "getView:" + member.getNickname());
            if (member.getNickname() != null) {
                holder.ivAddFamilyMember.setVisibility(View.GONE);
                holder.llFamilyMember.setVisibility(View.VISIBLE);
                holder.ivAddFamilyMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳转到添加家族成员的佛罗里达面
                        Intent intent = new Intent(mContext, FamilyAddMemberActivity.class);
                        startActivity(intent);
                    }
                });

                //设置图片
                String xfStr = member.getUserid() + "";
                xfStr = xfStr.substring(xfStr.length() - 1, xfStr.length());
                UILUtils.displayImageWithRounder(mApplication.getImgUri() + "headImage/" + xfStr + "/" + member.getHeadImage(), holder.ivMemberImg, 20);
//                if(member.getCwNote()!=null){
//                    holder.tvMemberName.setText(member.getCwNote());
//                }else {
                holder.tvMemberName.setText(member.getNickname());
//                }

//                Log.d(TAG,)
                holder.tvMemberName.setText(member.getNickname());
//                }
                if (!member.getCwNote().isEmpty()) {
                    holder.tvMemberNote.setText("(" + member.getCwNote() + ")");
                } else {
                    holder.tvMemberNote.setText("(无称谓)");
                }
//                Log.d(TAG,)
                if (member.getClanRole() == ClanRoleEnum.CREATER.getCode()) {
                    holder.tvMemberName.setCompoundDrawables(mDrawableCreater, null, null, null);

                    holder.tvMemberNote.setText("(创建者)");
                } else if (member.getClanRole() == ClanRoleEnum.MANAGER.getCode()) {
                    holder.tvMemberName.setCompoundDrawables(mDrawableManager, null, null, null);
                } else {
                    holder.tvMemberName.setCompoundDrawables(null, null, null, null);
                }
                return view;
            } else {
                if (mApplication.getUser().getDefaultFamilyBaseInfo().getClanRole() != ClanRoleEnum.NORMAL.getCode()) {
                    holder.ivAddFamilyMember.setVisibility(View.VISIBLE);
                    holder.llFamilyMember.setVisibility(View.GONE);
                }
            }
            //是否显示礼物消息

            return view;
        }

        class ViewHolder {
            @Bind(R.id.iv_member_img)
            ImageView ivMemberImg;
            @Bind(R.id.tv_member_name)
            TextView tvMemberName;
            @Bind(R.id.tv_member_note)
            TextView tvMemberNote;

            @Bind(R.id.iv_add_family_member)
            ImageView ivAddFamilyMember;
            @Bind(R.id.ll_family_member)
            LinearLayout llFamilyMember;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
