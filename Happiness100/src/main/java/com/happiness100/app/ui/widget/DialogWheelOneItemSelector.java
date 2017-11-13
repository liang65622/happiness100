package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/8/13.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：jiangsheng on 2016/8/13 12:00
 */
public class DialogWheelOneItemSelector extends Dialog implements View.OnClickListener, OnWheelChangedListener {

    private static final String TAG = "DialogWheelOneItemSelector";
    Activity mContext;
    PriorityListener mlistener;
    @Bind(R.id.wv_select)
    WheelView wvSelectData;

    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;


    private String[] mData;
    private boolean mIsHasRank;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wheel_one_item);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setUpListener();
        wvSelectData.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), mData));
    }

    private void setUpListener() {
        // 添加change事件
        wvSelectData.addChangingListener(this);
        // 添加change事件
        mBtnConfirm.setOnClickListener(this);
    }

    /**
     * @param context
     * @param theme
     */
    public DialogWheelOneItemSelector(Activity context, String[] data, int theme, PriorityListener listener) {
        super(context, theme);
        mContext = context;
        mData = data;
        mlistener = listener;
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        mlistener.refreshPriorityUI(newValue);
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
        mlistener.refreshPriorityUI(wvSelectData.getCurrentItem());
        dismiss();
    }


    public interface PriorityListener {
        public void refreshPriorityUI(int selectIndex);
    }


}
