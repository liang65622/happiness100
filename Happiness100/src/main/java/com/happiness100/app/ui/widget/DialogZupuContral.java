

package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/9/9.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.happiness100.app.R;
import com.happiness100.app.model.ClanRela.PUnit;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/9/9 10:59
 */
public class DialogZupuContral extends Dialog implements BaseViewInterface, DialogInterface.OnShowListener {
    @Bind(R.id.btn_check)
    ImageView btnCheck;
    @Bind(R.id.btn_edit)
    ImageView btnEdit;
    @Bind(R.id.btn_add)
    ImageView btnAdd;
    private View mView;
    BaseActivity mContext;
    @Bind(R.id.dialog_root)
    LinearLayout dialogRoot;
    int type;

    ContralListener mContralListener;

    public static final int ADD = 0;
    public static final int CHECK = 1;
    public static final int EDIT = 2;


    public DialogZupuContral(BaseActivity context, View v) {
        super(context, R.style.Theme_FAMILY_CONTROL_DIALOG);
        mView = v;
        mContext = context;
    }

    public void setUser(PUnit user) {

    }

    public DialogZupuContral(Context context, int themeResId) {
        super(context, R.style.Theme_FAMILY_CONTROL_DIALOG);
    }

    protected DialogZupuContral(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_zp_unit_control);
        ButterKnife.bind(this);
        initView();
        Resources resources = mContext.getResources();
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        dialogRoot.measure(w, h);
        setDialogSize();
        setOnShowListener(this);
    }

    public int getWidth() {
        return dialogRoot.getMeasuredWidth();
    }

    public int geHeight() {
        return dialogRoot.getMeasuredHeight();
    }


    private void setDialogSize() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        //显示的坐标
        int[] location = new int[2];
        mView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        lp.x = x + mView.getMeasuredWidth() / 2 - getWidth() / 2;
        lp.y = y - mView.getMeasuredHeight() / 2 - btnEdit.getMeasuredHeight();

        //dialog的大小
        //lp.width = 100;
        //lp.height = 100;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {

    }

    @Override
    public void onShow(DialogInterface dialog) {
    }

    @OnClick({R.id.btn_check, R.id.btn_edit, R.id.btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_check:
                mContralListener.onCheck();
                break;
            case R.id.btn_edit:
                mContralListener.onEdit();
                break;
            case R.id.btn_add:
                mContralListener.onAdd();
                break;
        }
    }

    public void setContralListener(ContralListener contralListener) {
        mContralListener = contralListener;
    }

    public interface ContralListener {
        public void onAdd();

        public void onCheck();

        public void onEdit();
    }
}

