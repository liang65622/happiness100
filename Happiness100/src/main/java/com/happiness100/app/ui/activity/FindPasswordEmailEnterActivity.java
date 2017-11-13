package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/18.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.happiness100.app.R;
import com.justin.utils.EmailUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/18 21:38
 */
public class FindPasswordEmailEnterActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.email_text)
    TextView mEmailText;
    @Bind(R.id.openBtn)
    Button mOpenBtn;
    String emailAddressBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password_email_enter);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent it = getIntent();
        String email = it.getStringExtra("email");
        mEmailText.setText(email);
        mTitleViewTitle.setText("密码找回");
        String address =  EmailUtils.getInstance().getEmailAddress(email);
        if(address == null)
        {
            mOpenBtn.setVisibility(View.GONE);
            return;
        }
        emailAddressBrowser = address;
    }

    @OnClick({R.id.title_view_back, R.id.openBtn, R.id.errorBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.openBtn:
                openBrowser();
                break;
            case R.id.errorBtn:
                break;
        }
    }

    private void openBrowser() {
        Uri uri = Uri.parse(emailAddressBrowser);
        Intent itUri = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(itUri);
    }
}
