package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/16.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.country.CountryActivity;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.AMUtils;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/16 16:50
 */
public class UserInfo_mobileChange_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.text_mobile)
    TextView mTextMobile;
    @Bind(R.id.tv_country)
    TextView mTvCountry;
    @Bind(R.id.tv_country_num)
    TextView mTvCountryNum;
    @Bind(R.id.edit_phone)
    EditText mEditPhone;
    @Bind(R.id.view_select_country)
    RelativeLayout mViewSelectCountry;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_changemobile);
        ButterKnife.bind(this);
        initView();
    }

    void initView() {
        mTextBack.setText("返回");
        mTitleViewTitle.setText("更换手机号");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText("下一步");
        mTextMobile.setText(mApplication.getUser().getMobile());
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right,R.id.view_select_country})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.view_select_country:
                Intent intent = new Intent();
                intent.setClass(mContext, CountryActivity.class);
                startActivityForResult(intent, 12);
                break;
            case R.id.title_view_right:
                String countryNum = mTvCountryNum.getText().toString();
                String phoneNum = mEditPhone.getText().toString();
                if (countryNum.equals("+86")) {
                    if (!AMUtils.isMobile(phoneNum)) {
                        ToastUtils.shortToast(mContext, R.string.phone_num_format_error);
                        return;
                    }
                }

                final String fPhoneNum = countryNum +" "+ phoneNum;
                DialogWithYesOrNoUtils.getInstance().showDialog(this, "确认手机号码", "我们将发送验证码到手机号:\n " + fPhoneNum, new DialogWithYesOrNoUtils.DialogCallBack() {
                    @Override
                    public void exectEvent() {
                        Map<String,String> params = new LinkedHashMap<>();
                        params.put("sessionid",mApplication.getUser().getSessionid());
                        params.put("new_mobile",fPhoneNum.replace("+",""));
                        APIClient.post(mContext, Constants.URL.EX_MOBILE_BIND_VERCODE, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                super.onErrorResponse(volleyError);
                            }
                            @Override
                            public void onResponse(String json) {
                                super.onResponse(json);
                                handleResponse(json, new ResponeInterface() {
                                    @Override
                                    public void parseResponse(String json) {
                                        Intent it = new Intent();
                                        it.setClass(UserInfo_mobileChange_Activity.this,UserInfo_verifyMoblieMessage_Activity.class);
                                        it.putExtra("NewMobileNum",fPhoneNum);
                                        //it.putExtra("data",json);
                                        startActivityForResult(it,1);
                                    }
                                });
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode) {
                case 12:
                    Bundle bundle = data.getExtras();
                    String countryName = bundle.getString("countryName");
                    String countryNumber = bundle.getString("countryNumber");
                    mTvCountry.setText(countryName);
                    mTvCountryNum.setText(countryNumber);

                    break;
                case 1:
                    setResult(RESULT_OK);
                    finish();
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
