package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/13.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.utils.Constants;
import com.justin.utils.SharedPreferencesContext;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/13 14:47
 */
public class SettingNewMessageNoDisturbingActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.open_item_sele)
    ImageView mOpenItemSele;
    @Bind(R.id.only_night_open_item_select)
    ImageView mOnlyNightOpenItemSelect;
    @Bind(R.id.close_item_sele)
    ImageView mCloseItemSele;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEdit;
    User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_notify_nodisturbing);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        Intent intent = new Intent();

        String backStr = intent.getStringExtra("back");
        if (backStr == null || backStr.isEmpty()) {
            backStr = "返回";
        }

        mTextBack.setText(backStr);
        mTitleViewTitle.setText("消息免打扰");
        setClickStatus(mSharedPreferences.getInt(Constants.Function.NODISTURBING+mUser.getXf(),Constants.NoDisturbStatus.CLOSE));
    }

    void initData() {
        mUser = mApplication.getUser();
        mSharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
        mEdit = SharedPreferencesContext.getInstance().getSharedPreferences().edit();
    }
    @OnClick({R.id.title_view_back, R.id.open_item, R.id.only_night_open_item, R.id.close_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.open_item:
                setClickStatus(Constants.NoDisturbStatus.OPEN);
                break;
            case R.id.only_night_open_item:
                setClickStatus(Constants.NoDisturbStatus.OPENLIMITE);
                break;
            case R.id.close_item:
                setClickStatus(Constants.NoDisturbStatus.CLOSE);
                break;
        }
    }

    void setClickStatus(int status)
    {
        mOpenItemSele.setVisibility(View.GONE);
        mOnlyNightOpenItemSelect.setVisibility(View.GONE);
        mCloseItemSele.setVisibility(View.GONE);
        switch (status)
        {
            case Constants.NoDisturbStatus.OPEN:
                mOpenItemSele.setVisibility(View.VISIBLE);
                mEdit.putInt(Constants.Function.NODISTURBING+mUser.getXf(),Constants.NoDisturbStatus.OPEN);
                mEdit.commit();
                break;
            case Constants.NoDisturbStatus.OPENLIMITE:
                mOnlyNightOpenItemSelect.setVisibility(View.VISIBLE);
                mEdit.putInt(Constants.Function.NODISTURBING+mUser.getXf(),Constants.NoDisturbStatus.OPENLIMITE);
                mEdit.commit();
                break;
            case Constants.NoDisturbStatus.CLOSE:
                mCloseItemSele.setVisibility(View.VISIBLE);
                mEdit.putInt(Constants.Function.NODISTURBING+mUser.getXf(),Constants.NoDisturbStatus.CLOSE);
                mEdit.commit();
                break;
        }
    }

}
