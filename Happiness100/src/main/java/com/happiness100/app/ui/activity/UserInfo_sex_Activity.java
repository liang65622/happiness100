package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/12.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.happiness100.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/12 18:39
 */
public class UserInfo_sex_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.select_boy)
    ImageView mSelectBoy;
    @Bind(R.id.select_girl)
    ImageView mSelectGirl;
    int sex;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_sex);
        ButterKnife.bind(this);
        initView();
    }

    void initView() {
        mTitleViewTitle.setText("性别");
        mTextBack.setText("返回");
        if (mApplication.getUser().getSex() == 0)
            selectBoy();
        else
            selecGirl();
    }


    @OnClick({R.id.title_view_back, R.id.sex_boy, R.id.sex_girl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                Intent intent=new Intent();
                intent.putExtra("Sex",sex);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.sex_boy:
                selectBoy();
                break;
            case R.id.sex_girl:
                selecGirl();
                break;
        }
    }

    void selectBoy()
    {
        mSelectBoy.setVisibility(View.VISIBLE);
        mSelectGirl.setVisibility(View.GONE);
        sex =0;
    }

    void selecGirl()
    {
        mSelectBoy.setVisibility(View.GONE);
        mSelectGirl.setVisibility(View.VISIBLE);
        sex =1;
    }
}
