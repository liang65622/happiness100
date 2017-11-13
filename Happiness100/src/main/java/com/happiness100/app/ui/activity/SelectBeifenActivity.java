package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/31.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.ui.BaseViewInterface;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择辈份
 * 作者：justin on 2016/8/31 15:24
 */
public class SelectBeifenActivity extends BaseActivity implements BaseViewInterface {

    @Bind(R.id.title_view_title)
    TextView titleViewTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_beifen
        );
        ButterKnife.bind(this);
        mContext = this;

        initView();
        initData();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("选择辈份");
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.title_view_back, R.id.rl_select_beifen, R.id.rl_select_peers, R.id.rl_select_younger})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.rl_select_beifen:
                intent.putExtra("beifen",1);
                setResult(RESULT_OK,intent);
                mContext.finish();
                break;
            case R.id.rl_select_peers:
                intent.putExtra("beifen",2);
                setResult(RESULT_OK,intent);
                mContext.finish();
                break;
            case R.id.rl_select_younger:
                intent.putExtra("beifen",3);
                setResult(RESULT_OK,intent);
                mContext.finish();
                break;
        }
    }
}
