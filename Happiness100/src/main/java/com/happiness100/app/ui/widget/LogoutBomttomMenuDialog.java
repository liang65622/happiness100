package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/9/21.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.happiness100.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/21 01:02
 */
public class LogoutBomttomMenuDialog extends Dialog{

    private Activity mContext;
    private View.OnClickListener listener;
    /**
     * @param context
     */
    public LogoutBomttomMenuDialog(Context context) {
        super(context, R.style.dialogFullscreen);
    }

    /**
     * @param context
     * @param theme
     */
    public LogoutBomttomMenuDialog(Activity context, int theme) {
        super(context, theme);
        mContext = context;
    }

    /**
     * @param context
     */
    public LogoutBomttomMenuDialog(Activity context) {
        super(context, R.style.dialogFullscreen);
        mContext = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_logout);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }

    public void setLogoutListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @OnClick({R.id.localPhotosBtn, R.id.cancelBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.localPhotosBtn:
                    if (listener != null)
                    {
                        listener.onClick(view);
                    }
                break;
            case R.id.cancelBtn:
                dismiss();
                break;
        }
    }


}
