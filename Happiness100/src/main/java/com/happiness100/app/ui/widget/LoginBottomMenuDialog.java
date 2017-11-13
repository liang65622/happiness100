package com.happiness100.app.ui.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.happiness100.app.R;
import com.happiness100.app.ui.activity.FindPasswordActivity;
import com.happiness100.app.ui.activity.LoginActivity;
import com.happiness100.app.ui.activity.RegisterActivity;
import com.justin.utils.SharedPreferencesContext;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * [底部弹出dialog]
 **/
public class LoginBottomMenuDialog extends Dialog implements View.OnClickListener {


    @Bind(R.id.btn_find_back_password)
    Button btnFindBackPassword;
    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.btn_go_to_security_center)
    Button btnGoToSecurityCenter;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;
    private View.OnClickListener middleListener;

    private String confirmText;
    private String middleText;
    private String cancelText;

    private Activity mContext;
    private int mLoginStep;

    /**
     * @param context
     */
    public LoginBottomMenuDialog(Context context) {
        super(context, R.style.dialogFullscreen);
    }

    /**
     * @param context
     * @param theme
     */
    public LoginBottomMenuDialog(Activity context, int theme) {
        super(context, theme);
        mContext = context;
    }

    /**
     * @param context
     * @param theme
     */
    public LoginBottomMenuDialog(Activity context, int theme,int loginStep) {
        super(context, theme);
        mLoginStep = loginStep;
        mContext = context;
    }

    /**
     * @param context
     */
    public LoginBottomMenuDialog(Activity context, String confirmText, String middleText) {
        super(context, R.style.dialogFullscreen);
        this.confirmText = confirmText;
        this.middleText = middleText;
        mContext = context;
    }

    /**
     * @param context
     */
    public LoginBottomMenuDialog(Activity context, String confirmText, String middleText, String cancelText) {
        super(context, R.style.dialogFullscreen);
        this.confirmText = confirmText;
        this.middleText = middleText;
        this.cancelText = cancelText;
        mContext = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_bottom_login);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);

        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        ButterKnife.bind(this);

        if(mLoginStep==2){
            btnFindBackPassword.setText("切换账号");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }


//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.photographBtn) {
//            if (confirmListener != null) {
//                confirmListener.onClick(v);
//            }
//            return;
//        }
//        if (id == R.id.localPhotosBtn) {
//            if (middleListener != null) {
//                middleListener.onClick(v);
//            }
//            return;
//        }
//        if (id == R.id.cancelBtn) {
//            if (cancelListener != null) {
//                cancelListener.onClick(v);
//            }
//            dismiss();
//            return;
//        }
//    }

    public View.OnClickListener getConfirmListener() {
        return confirmListener;
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public View.OnClickListener getCancelListener() {
        return cancelListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public View.OnClickListener getMiddleListener() {
        return middleListener;
    }

    public void setMiddleListener(View.OnClickListener middleListener) {
        this.middleListener = middleListener;
    }

    @OnClick({R.id.btn_find_back_password, R.id.btn_register, R.id.btn_go_to_security_center, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_find_back_password:

                if(mLoginStep==2){
                    SharedPreferencesContext.getInstance().getSharedPreferences().edit().clear().commit();
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    mContext.finish();
                }
                else{
                    Intent it = new Intent(mContext, FindPasswordActivity.class);
                    mContext.startActivity(it);
                    this.dismiss();
                }
                break;
            case R.id.btn_register:
                mContext.startActivity(new Intent(mContext, RegisterActivity.class));
                this.dismiss();
                break;
            case R.id.btn_go_to_security_center:

                break;
            case R.id.btn_cancel:
                this.dismiss();
                break;
        }
    }
}
