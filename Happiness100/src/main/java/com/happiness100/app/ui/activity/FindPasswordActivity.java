package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/18.
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
 * 作者：jiangsheng on 2016/9/18 20:37
 */
public class FindPasswordActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("找回密码");
    }

    @OnClick({R.id.title_view_back, R.id.mobile_item, R.id.email_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.mobile_item:
                Intent it = new Intent(mContext,LoginByVercodeActivity.class);
                startActivity(it);
                break;
            case R.id.email_item:
                Intent it1 = new Intent(mContext,FindPasswordEmailActivity.class);
                startActivity(it1);
                break;
        }
    }
}
