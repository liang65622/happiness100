package com.happiness100.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.StringResponeBean;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.country.CountryActivity;
import com.happiness100.app.utils.APIClient;
import com.justin.utils.AMUtils;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements BaseViewInterface {

    @Bind(R.id.tv_country)
    TextView tvCountry;
    @Bind(R.id.tv_country_num)
    TextView tvCountryNum;
    @Bind(R.id.edit_phone)
    EditText editPhone;
    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private Activity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    @OnClick({R.id.tv_cancel, R.id.btn_register,R.id.textAgreement})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            case R.id.btn_register:
                String countryNum = tvCountryNum.getText().toString();
                String phoneNum = editPhone.getText().toString();
                if (countryNum.equals("+86")) {
                    if (!AMUtils.isMobile(phoneNum)) {
                        ToastUtils.shortToast(mContext, R.string.phone_num_format_error);
                        return;
                    }
                }


                final String fPhoneNum = countryNum + " " + phoneNum;
                DialogWithYesOrNoUtils.getInstance().showDialog(this, "确认手机号", "我们将发送验证码到手机号: " + fPhoneNum, new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        Map<String, String> params = new HashMap<>();
                        params.put("mobile", fPhoneNum.replace("+", ""));
                        Intent intent = new Intent(mContext, CodeVerificationActivity.class);
                        intent.putExtra("phone_num", fPhoneNum.replace("+", ""));
                        startActivity(intent);

                        if (false)
                            APIClient.getVerCode(mContext, params, new BaseVolleyListener(mContext) {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    super.onErrorResponse(volleyError);
                                }

                                @Override
                                public void onResponse(String s) {
                                    if (TextUtils.isEmpty(s)) {
                                        Log.e("newsList", "onResponse() return is null");
                                        return;
                                    }
                                    StringResponeBean responeBean = GsonUtils.parseJSON(s, StringResponeBean.class);
                                    if (responeBean.getCode() == 0) {
                                        String vercode = responeBean.getData() + "";
                                        Intent intent = new Intent(mContext, CodeVerificationActivity.class);
                                        intent.putExtra("phone_num", fPhoneNum.replace("+", ""));
                                        startActivity(intent);
                                    } else {
                                        ToastUtils.shortToast(mContext, responeBean.getMsg());
                                    }
                                    Log.e("onRespone", s);


                                }
                            });


                    }

                    @Override
                    public void exectEditEvent(String editText) {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.textAgreement:
                Uri uri = Uri.parse("http://119.29.102.152:8080/right/xy.html");
                Intent itUri = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(itUri);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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


    @OnClick(R.id.view_select_country)
    public void onClick() {
        Intent intent = new Intent();
        intent.setClass(mContext, CountryActivity.class);
        startActivityForResult(intent, 12);
    }

    @Override
    public void initView() {
        tvTitle.setText("请输入你的手机号码");
    }

    @Override
    public void initData() {

    }
}
