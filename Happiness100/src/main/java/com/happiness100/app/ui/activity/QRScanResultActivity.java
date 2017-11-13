package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.happiness100.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/17 15:47
 */
public class QRScanResultActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.scan_content_text)
    TextView mScanContentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_result_layout);
        ButterKnife.bind(this);
        initView();
    }

    void initView()
    {
        Intent intent = getIntent();
        mScanContentText.setText(intent.getStringExtra("content"));
        mTextBack.setText("返回");
        mTitleViewTitle.setText("扫描结果");
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        finish();
    }
}
