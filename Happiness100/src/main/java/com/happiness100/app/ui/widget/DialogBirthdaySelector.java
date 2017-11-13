package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/8/13.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.happiness100.app.R;
import com.happiness100.app.ui.widget.wheel.OnWheelChangedListener;
import com.happiness100.app.ui.widget.wheel.WheelView;
import com.happiness100.app.ui.widget.wheel.adapters.ArrayWheelAdapter;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：jiangsheng on 2016/8/13 12:00
 */
public class DialogBirthdaySelector extends Dialog implements View.OnClickListener, OnWheelChangedListener {

    Activity mContext;
    PriorityListener mlistener;
    @Bind(R.id.id_year)
    WheelView mIdYear;
    @Bind(R.id.id_month)
    WheelView mIdMonth;
    @Bind(R.id.id_day)
    WheelView mIdDay;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;

    String mYear;
    String mMonth;
    String mDay;

    String yearData[] = new String[10000];
    protected Map<String, String[]> nonleap_year_data = new HashMap<String, String[]>();
    protected Map<String, String[]> leap_year_data = new HashMap<String, String[]>();
    int everyMonthDays[] = {31,28,31,30,31,30,31,31,30,31,30,31};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_birthday);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setUpListener();
        setUpData();
    }

    private void setUpListener() {
        // 添加change事件
        mIdYear.addChangingListener(this);
        // 添加change事件
        mIdMonth.addChangingListener(this);
        // 添加change事件
        mIdDay.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);
    }

    private void setUpData() {
        initDatas();
        mIdYear.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), yearData));
        // 设置可见条目数量
        mIdYear.setVisibleItems(5);
        mIdMonth.setVisibleItems(5);
        mIdDay.setVisibleItems(5);
        mIdYear.setCurrentItem(1995);
       updateMonthData();
        updateDayData();
    }

    private void initDatas()
    {
        for (int i = 1;i < 10000;++i)
        {
            yearData[i-1] = ""+i;
        }

    }

    /**
     * @param context
     * @param theme
     */
    public DialogBirthdaySelector(Activity context, int theme, PriorityListener listener) {
        super(context, theme);
        mContext = context;
        mlistener = listener;
    }

    private void updateDayData() {

        int pCurrent = mIdMonth.getCurrentItem();
        mMonth = ""+(pCurrent+1);

        String[] dayData;
        if (pCurrent == 1)
        {
            int yearCentent = mIdMonth.getCurrentItem()+1;

            if ((yearCentent/4 == 0 &&  yearCentent/100 != 0)||yearCentent/400 == 0)
            {
                dayData = leap_year_data.get(""+mMonth);
                if (dayData == null)
                {
                    dayData = new String[29];
                    for (int i = 1;i < 30;++i)
                    {
                        dayData[i-1] = ""+i;
                    }
                    leap_year_data.put(""+mMonth,dayData);
                }
            }
            else
            {
                dayData = nonleap_year_data.get(""+mMonth);
                if (dayData == null)
                {
                    dayData = new String[everyMonthDays[pCurrent]];
                    for (int i = 1;i <= everyMonthDays[pCurrent];++i)
                    {
                        dayData[i-1] = ""+i;
                    }
                    nonleap_year_data.put(""+mMonth,dayData);
                }
            }
        }
        else
        {
            dayData = nonleap_year_data.get(""+mMonth);
            if (dayData == null)
            {
                dayData = new String[everyMonthDays[pCurrent]];
                for (int i = 1;i <= everyMonthDays[pCurrent];++i)
                {
                    dayData[i-1] = ""+i;
                }
                nonleap_year_data.put(""+mMonth,dayData);
                leap_year_data.put(""+mMonth,dayData);
            }
        }
        mIdDay.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), dayData));
        mIdDay.setCurrentItem(0);
        mDay = ""+1;
    }

    private void updateMonthData() {

        int pCurrent = mIdYear.getCurrentItem();
        mYear = yearData[pCurrent];

        String[] monthData = {"1","2","3","4","5","6","7","8","9","10","11","12"};
        mIdMonth.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), monthData));
        mIdMonth.setCurrentItem(0);
        updateDayData();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mIdYear) {
            updateMonthData();
        } else if (wheel == mIdMonth) {
            updateDayData();
        } else if (wheel == mIdDay) {
            int pCurrent = mIdDay.getCurrentItem();
            mDay = ""+(pCurrent+1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                showSelectedResult();
                break;
            default:
                break;
        }
    }

    private void showSelectedResult() {
        Log.e("TAG",mYear+"-"+mMonth+"-"+mDay);

        mlistener.refreshPriorityUI(mYear,mMonth,mDay);
        dismiss();
    }

    public interface PriorityListener {
        public void refreshPriorityUI(String year, String mouth, String day);
    }
}
