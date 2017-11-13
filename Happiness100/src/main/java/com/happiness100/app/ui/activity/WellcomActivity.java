package com.happiness100.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.happiness100.app.R;
import com.happiness100.app.utils.Constants;
import com.justin.utils.SharedPreferencesContext;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WellcomActivity extends BaseActivity {

    private static final int REQUEST_LOGIN = 1;
    private static final String TAG = "WellcomActivity";
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_register)
    Button btnRegister;
    BaseActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcom);
        ButterKnife.bind(this);
        mContext = this;
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onClick(View paramView) {
        switch (paramView.getId()) {

            case R.id.btn_login:
                if (getUser() == null) {
                    startActivityForResult(new Intent(this.mContext, LoginActivity.class), 1);
                    return;
                }
                startActivity(new Intent(this.mContext, LoginSecondActivity.class));
                return;
            case R.id.btn_register:
                startActivityForResult(new Intent(this.mContext, RegisterActivity.class), 1);
                break;
            default:
                return;
        }


    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        Log.e("WellcomActivity", "onActivityResult.isLogout():" + this.mApplication.isLogout());
        if (paramInt2 == -1) {
            Log.e("WellcomActivity", "mApplication.isLogout():" + this.mApplication.isLogout());
            if (!this.mApplication.isLogout()) {
                this.mContext.finish();
            }
            this.mApplication.setIsLogout(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
