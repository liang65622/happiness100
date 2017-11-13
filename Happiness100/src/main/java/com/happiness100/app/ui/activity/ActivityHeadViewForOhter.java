package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/31.
 */

import android.os.Bundle;
import android.widget.ImageView;

import com.happiness100.app.R;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：jiangsheng on 2016/8/31 10:48
 */

public class ActivityHeadViewForOhter extends BaseActivity {
    @Bind(R.id.headViewImage)

    ImageView mHeadViewImage;

    String uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_headview_other);
        ButterKnife.bind(this);
        initView();
    }

    void initView()
    {
        uri = getIntent().getStringExtra("Uri");
        if (uri == null)
        {
            ToastUtils.shortToast(mContext,"Uri == NULL");
            finish();
        }

        UILUtils.displayImage(uri, mHeadViewImage);
    }
}
