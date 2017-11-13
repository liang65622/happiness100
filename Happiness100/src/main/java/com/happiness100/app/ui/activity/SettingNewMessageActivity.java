package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/13.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.ui.widget.SwitchView;
import com.happiness100.app.utils.Constants;
import com.happiness100.app.utils.NotificationsStatusUtils;
import com.justin.utils.SharedPreferencesContext;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/13 11:21
 */
public class SettingNewMessageActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.notify_detailed_switch)
    SwitchView mNotifyDetailedSwitch;
    @Bind(R.id.audio_item_switch)
    SwitchView mAudioItemSwitch;
    @Bind(R.id.shake_item_switch)
    SwitchView mShakeItemSwitch;
    @Bind(R.id.notify_text)
    TextView mNotifyText;
    User mUser;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_newmessage);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    void initView() {
        Intent intent = new Intent();

        String backStr = intent.getStringExtra("back");
        if (backStr == null || backStr.isEmpty()) {
            backStr = "返回";
        }

        mTextBack.setText(backStr);
        mTitleViewTitle.setText("新消息通知");
    }

    void initData()
    {
        mUser = mApplication.getUser();
        mSharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
        mEdit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();

        Boolean audioStatus = mSharedPreferences.getBoolean(Constants.Function.AUDIO+mUser.getXf(),true);
        Boolean shakeStatus = mSharedPreferences.getBoolean(Constants.Function.SHAKE+mUser.getXf(),true);
        Boolean notifyDetailedStatus = mSharedPreferences.getBoolean(Constants.Function.SHOWNOTIFYCONTENT+mUser.getXf(),true);

        mAudioItemSwitch.toggleSwitch(audioStatus);
        mShakeItemSwitch.toggleSwitch(shakeStatus);
        mNotifyDetailedSwitch.toggleSwitch(notifyDetailedStatus);

        mNotifyText.setText(NotificationsStatusUtils.isNotificationEnabled(mContext)?"已开启":"未开启");

        mNotifyDetailedSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                mNotifyDetailedSwitch.toggleSwitch(true);
                mEdit.putBoolean(Constants.Function.SHOWNOTIFYCONTENT+mUser.getXf(),true);
                mEdit.commit();
            }

            @Override
            public void toggleToOff() {
                mNotifyDetailedSwitch.toggleSwitch(false);
                mEdit.putBoolean(Constants.Function.SHOWNOTIFYCONTENT+mUser.getXf(),false);
                mEdit.commit();
            }
        });

        mAudioItemSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                mAudioItemSwitch.toggleSwitch(true);
                mEdit.putBoolean(Constants.Function.AUDIO+mUser.getXf(),true);
                mEdit.commit();
            }

            @Override
            public void toggleToOff() {
                mAudioItemSwitch.toggleSwitch(false);
                mEdit.putBoolean(Constants.Function.AUDIO+mUser.getXf(),false);
                mEdit.commit();
            }
         });

        mShakeItemSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                mShakeItemSwitch.toggleSwitch(true);
                mEdit.putBoolean(Constants.Function.SHAKE+mUser.getXf(),true);
                mEdit.commit();
            }

            @Override
            public void toggleToOff() {
                mShakeItemSwitch.toggleSwitch(false);
                mEdit.putBoolean(Constants.Function.SHAKE+mUser.getXf(),false);
                mEdit.commit();
            }
        });
    }

    @OnClick({R.id.title_view_back,R.id.notify_no_disturbing})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.notify_no_disturbing:
                Intent it = new Intent(mContext,SettingNewMessageNoDisturbingActivity.class);
                it.putExtra("back","新消息通知");
                startActivity(it);
                break;
        }
    }
}
