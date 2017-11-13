package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/9/6.
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
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.ui.widget.wheel.OnWheelChangedListener;
import com.happiness100.app.ui.widget.wheel.WheelView;
import com.happiness100.app.ui.widget.wheel.adapters.ArrayWheelAdapter;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：jiangsheng on 2016/9/6 23:17
 */
public class DialogFamilyListSelector extends Dialog implements View.OnClickListener, OnWheelChangedListener {

    @Bind(R.id.id_family)
    WheelView mFamily;
    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    private Activity mContext;
    private PriorityListener mlistener;//activity监听回调
    private Map<String,FamilyBaseInfo> mDatas;
    private String mCurrentSelectId;
    private String[] mFamilyArray;
    /**
     * @param context
     * @param theme
     */
    public DialogFamilyListSelector(Activity context, Map<String,FamilyBaseInfo> Datas, int theme, PriorityListener listener) {
        super(context, theme);
        mContext = context;
        mlistener = listener;
        mDatas = Datas;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_item_family);
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
        mFamily.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);
    }

    private void setUpData() {
        mFamilyArray = new String[mDatas.size()];
        int i =  0;
        for (Map.Entry<String, FamilyBaseInfo> entry : mDatas.entrySet()) {
            mFamilyArray[i] = entry.getValue().getClanName();
            i++;
        }

        mFamily.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), mFamilyArray));
        // 设置可见条目数量
        mFamily.setVisibleItems(7);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mFamily) {
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
        int pos = mFamily.getCurrentItem();
        mCurrentSelectId = mDatas.get(mFamilyArray[pos]).getClanId()+"";
        mlistener.refreshPriorityUI(mCurrentSelectId);
        dismiss();
    }

    public interface PriorityListener {
        public void refreshPriorityUI(String  familyId);
    }
}
