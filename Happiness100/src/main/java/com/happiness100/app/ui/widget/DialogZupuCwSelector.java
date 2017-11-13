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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.model.ClanCwNote;
import com.happiness100.app.ui.widget.wheel.OnWheelChangedListener;
import com.happiness100.app.ui.widget.wheel.WheelView;
import com.happiness100.app.ui.widget.wheel.adapters.ArrayWheelAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：jiangsheng on 2016/8/13 12:00
 */
public class DialogZupuCwSelector extends Dialog implements View.OnClickListener, OnWheelChangedListener {

    private static final String TAG = "DialogZupuCwSelector";
    Activity mContext;
    PriorityListener mlistener;
    @Bind(R.id.wv_select_cw)
    WheelView wvSelectCw;
    @Bind(R.id.wv_select_rank)
    WheelView wvSelectRank;

    @Bind(R.id.btn_confirm)
    Button mBtnConfirm;
    @Bind(R.id.tv_title_cw)
    TextView tvTitleCw;
    @Bind(R.id.tv_title_rank)
    TextView tvTitleRank;
    @Bind(R.id.view_title)
    LinearLayout viewTitle;

    private String[] mCwStrsWithoutId;
    private boolean mIsHasRank;
    private ClanCwNote mClanCwNote;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_zupu_cw);
        ButterKnife.bind(this);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setUpListener();

        if (mIsHasRank) {
//            wvSelectRank.setVisibility(View.GONE);
        }

        wvSelectCw.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), mCwStrsWithoutId));
        updateRank(0);
    }

    private void setUpListener() {
        // 添加change事件
        wvSelectCw.addChangingListener(this);
        // 添加change事件
        wvSelectRank.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);
    }


    /**
     * @param context
     * @param theme
     */
    public DialogZupuCwSelector(Activity context, int theme, PriorityListener listener) {
        super(context, theme);
        mContext = context;
        mlistener = listener;
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wvSelectCw) {
            updateRank(wvSelectCw.getCurrentItem());
        } else if (wheel == wvSelectRank) {
            setSelectData();
        }
    }

    private void setSelectData() {
    }

    String[] cwRank = new String[]{"大", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};
    String[] nRank = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};

    private void updateRank(int cwIndex) {

        String cw = mCwStrsWithoutId[cwIndex];
        String gender = getGender(cw);

        if (gender.equals("1")) {
            tvTitleRank.setText("姐妹排行");
        } else {
            tvTitleRank.setText("兄弟排行");
        }

        String[] rankStrs = new String[cwRank.length];
        for (int i = 0; i < cwRank.length; i++) {
            rankStrs[i] = cwRank[i] + cw;
        }
        if (mIsHasRank) {
            wvSelectRank.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), rankStrs));
        } else {
            wvSelectRank.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), nRank));
        }
        wvSelectRank.setCurrentItem(0);
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

        int cwIndex = wvSelectCw.getCurrentItem();
        int rankIndex = wvSelectRank.getCurrentItem();
        mlistener.refreshPriorityUI(cwIndex, rankIndex);
        dismiss();
    }

    public void setData(String[] cwStrs, boolean isHasRank, ClanCwNote clanCwNote) {

        mIsHasRank = isHasRank;
        mCwStrsWithoutId = cwStrs;
        mClanCwNote = clanCwNote;
    }

    public interface PriorityListener {
        public void refreshPriorityUI(int cwIndex, int rankIndex);
    }

    private String getGender(String cw) {

        if (mClanCwNote.getYoungSister() != null && mClanCwNote.getYoungSister().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getElderSister() != null && mClanCwNote.getElderSister().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getWife() != null && mClanCwNote.getWife().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getDaughter() != null && mClanCwNote.getDaughter().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getMother() != null && mClanCwNote.getMother().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getFather() != null && mClanCwNote.getFather().contains(cw)) {
            return "";
        } else if (
                mClanCwNote.getYoungBrother() != null && mClanCwNote.getYoungBrother().contains(cw)) {
            return "";
        } else if (mClanCwNote.getElderBrother() != null && mClanCwNote.getElderBrother().contains(cw)) {
            return "";
        } else if (mClanCwNote.getHusband() != null && mClanCwNote.getHusband().contains(cw)) {
            return "";
        } else if (mClanCwNote.getSon() != null && mClanCwNote.getSon().contains(cw)) {
            return "";
        }
        return "";
    }
}
