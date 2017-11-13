package com.happiness100.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.country.CountryActivity;
import com.happiness100.app.utils.Constants;
import com.justin.utils.AMUtils;
import com.justin.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginByVercodeActivity extends BaseActivity implements BaseViewInterface {


    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_country)
    TextView tvCountry;
    @Bind(R.id.tv_country_num)
    TextView tvCountryNum;
    @Bind(R.id.edit_phone)
    EditText editPhone;
    @Bind(R.id.btn_next)
    Button btnNext;
    Activity mContext;
    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_by_vercode);
        ButterKnife.bind(this);
        mPhoneNum = getIntent().getStringExtra("phoneNum");
        initView();
        mContext = this;
    }


    @OnClick({R.id.tv_country, R.id.btn_next, R.id.view_select_country,R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            case R.id.tv_country:
                break;
            case R.id.btn_next:
                String str = editPhone.getText().toString();
                if (str == null || str.isEmpty())
                {
                    ToastUtils.shortToast(mContext,"手机号码不能为空");
                    return;
                }

                if(!AMUtils.isMobile(str))
                {
                    ToastUtils.shortToast(mContext,"非法的手机号");
                    return;
                }
                Intent intent = new Intent(mContext, CodeVerificationActivity.class);
                intent.putExtra("phone_num", tvCountryNum.getText().toString().replace("+", "") + " " + editPhone.getText());
                intent.putExtra("todo", Constants.NetWork.LOGIN);
                startActivity(intent);
                break;
            case R.id.view_select_country:
                Intent intentSelectCountry = new Intent();
                intentSelectCountry.setClass(mContext, CountryActivity.class);
                startActivityForResult(intentSelectCountry, 12);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String countryName = bundle.getString("countryName");
                    String countryNumber = bundle.getString("countryNumber");
                    tvCountry.setText(countryName);
                    tvCountryNum.setText(countryNumber);
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.tv_cancel)
    public void onClick() {
        mContext.finish();
    }

    @Override
    public void initView() {
        editPhone.setText(mPhoneNum);
    }

    @Override
    public void initData() {

    }
}
