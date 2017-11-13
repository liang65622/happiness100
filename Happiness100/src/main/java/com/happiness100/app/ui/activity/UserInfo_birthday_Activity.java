package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/13.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.DialogBirthdaySelector;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.ToastUtils;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/13 11:31
 */
public class UserInfo_birthday_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.edit_age)
    TextView mEditAge;
    @Bind(R.id.edit_constellation)
    TextView mEditConstellation;
    DialogBirthdaySelector mDialog_BirthdaySelector;
    private Activity mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_birthday);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    void initView() {
        mTitleViewTitle.setText("选择出生日期");
        mTextBack.setText("返回");

        String date = mApplication.getUser().getBirthday();

        if (date != null &&!date.isEmpty())
        {
            String info[]= date.split("-");
            mEditAge.setText(GetAgeByDate(Integer.parseInt(info[0]),Integer.parseInt(info[1]),Integer.parseInt(info[2])));
            mEditConstellation.setText(GetConstellationByDate(Integer.parseInt(info[1]),Integer.parseInt(info[2])));
        }
/*
        mEditAge.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (mDialog_BirthdaySelector != null && mDialog_BirthdaySelector.isShowing()) {
                    mDialog_BirthdaySelector.dismiss();
                }
                else
                {
                    OpenBirthDaySelector();
                }

            }
        });

        mEditConstellation.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mDialog_BirthdaySelector != null && mDialog_BirthdaySelector.isShowing()) {
                    mDialog_BirthdaySelector.dismiss();
                }
                else
                {
                    OpenBirthDaySelector();
                }
            }
        });
        */
    }

    @OnClick({R.id.title_view_back,R.id.edit_age,R.id.edit_constellation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.edit_age:
            case R.id.edit_constellation:
                if (mDialog_BirthdaySelector != null && mDialog_BirthdaySelector.isShowing()) {
                    mDialog_BirthdaySelector.dismiss();
                }
                else
                {
                    OpenBirthDaySelector();
                }
                break;
        }
    }

    private void OpenBirthDaySelector() {
        if (mDialog_BirthdaySelector != null && mDialog_BirthdaySelector.isShowing()) {
            mDialog_BirthdaySelector.dismiss();
        }
        if (mDialog_BirthdaySelector == null) {
            mDialog_BirthdaySelector = new DialogBirthdaySelector(mContext,R.style.MMTheme_DataSheet_BottomIn,new DialogBirthdaySelector.PriorityListener(){
                    @Override
                    public void refreshPriorityUI(String year, String month, String day)
                    {
                    String Constellation =  GetConstellationByDate(Integer.parseInt(month),Integer.parseInt(day));
                    mEditConstellation.setText(Constellation);
                    mEditAge.setText(GetAgeByDate(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day)));
                    postBirthDay(""+year+"-"+month+"-"+day);
                }
            });
        }
        mDialog_BirthdaySelector.show();
    }

    private void postBirthDay(final String brithday) {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("birthday",brithday);
        APIClient.post(mContext, Constants.URL.UPDATE_BIRTHDAY, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        mApplication.getUser().setBirthday(brithday);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }

    private String GetConstellationByDate(int month,int day)
    {
        String[] astro = new String[] { "摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座",
                "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座" };
        int[] arr = new int[] { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };// 两个星座分割日
        int index = month;
        // 所查询日期在分割日之前，索引-1，否则不变
        if (day < arr[month - 1]) {
            index = index - 1;
        }
        // 返回索引指向的星座string
        return astro[index];
    }

    private  String GetAgeByDate(int year,int month,int day)
    {
        Calendar now = Calendar.getInstance();
        return ""+(now.get(Calendar.YEAR)-year);
    }
}
