package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.happiness100.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/16 16:33
 */
public class UserInfo_mobile_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_mobile_number)
    TextView mTextMobileNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_mobile);
        ButterKnife.bind(this);
        initView();
    }

    void initView() {
        mTextBack.setText("返回");
        mTitleViewTitle.setText("手机绑定");
        mTextMobileNumber.setText("你的手机号："+mApplication.getUser().getMobile());
    }

    @OnClick({R.id.title_view_back, R.id.look_book_button, R.id.change_mobileNumber_button})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.look_book_button:
                Intent intentCommunication = new Intent();
                intentCommunication.setClass(mContext,CommunicationActivity.class);
                startActivity(intentCommunication);
                break;
            case R.id.change_mobileNumber_button:
                Intent intent = new Intent();
                intent.setClass(this,UserInfo_mobileChange_Activity.class);
                startActivityForResult(intent,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
