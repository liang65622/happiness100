package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/12.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.DialogAddressSelector;
import com.happiness100.app.ui.widget.LoadDialog;
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
 * 作者：jiangsheng on 2016/8/12 11:33
 */
public class UserInfo_newAddress_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.edit_name)
    EditText mEditName;
    @Bind(R.id.edit_mobile)
    EditText mEditMobile;
    @Bind(R.id.edit_zone)
    EditText mEditZone;
    @Bind(R.id.edit_address)
    EditText mEditAddress;
    @Bind(R.id.edit_addresscode)
    EditText mEditAddresscode;
    @Bind(R.id.back_picture)
    ImageView mBackPicture;
    @Bind(R.id.title_view_back)
    LinearLayout mTitleViewBack;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    private Activity mContext;
    DialogAddressSelector mDialog_addressSelector;
    String addressId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_newaddress);
        ButterKnife.bind(this);
        mContext =this;
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("修改地址");
        mTextBack.setText("取消");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText("保存");


        Intent intent = getIntent();
        addressId = intent.getStringExtra("addressId");
        mEditName.setText(intent.getStringExtra("Name"));
        mEditMobile.setText(intent.getStringExtra("Mobile"));
        mEditAddress.setText(intent.getStringExtra("DetailedAddress"));
        mEditZone.setText(intent.getStringExtra("address"));
        mEditAddresscode.setText(intent.getStringExtra("AddressCode"));

        mEditZone.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    OpenAddressSelector();
                } else {
                    if (mDialog_addressSelector != null && mDialog_addressSelector.isShowing()) {
                        mDialog_addressSelector.dismiss();
                    }
                }
            }
        });
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:

                if (mEditName.getText().toString().isEmpty())
                {
                    ToastUtils.shortToast(mContext,"收货人不能为空");
                    return;
                }
                if (mEditMobile.getText().toString().isEmpty())
                {
                    ToastUtils.shortToast(mContext,"手机号码不能为空");
                    return;
                }
                if (!AMUtils.isMobile(mEditMobile.getText().toString()))
                {
                    ToastUtils.shortToast(mContext,"非法的手机号");
                    return;
                }
                if (mEditZone.getText().toString().isEmpty())
                {
                    ToastUtils.shortToast(mContext,"地址不能为空");
                    return;
                }
                if (mEditAddress.getText().toString().isEmpty())
                {
                    ToastUtils.shortToast(mContext,"详细地址不能为空");
                    return;
                }
                if (mEditAddresscode.getText().toString().isEmpty())
                {
                    ToastUtils.shortToast(mContext,"邮政编码不能为空");
                    return;
                }

                if (addressId != null )
                {
                    postModifyAddress();
                }
                else
                {
                    postAddAddress();
                }

                break;
        }
    }



    private void OpenAddressSelector() {
        if (mDialog_addressSelector != null && mDialog_addressSelector.isShowing()) {
            mDialog_addressSelector.dismiss();
        }
        if (mDialog_addressSelector == null) {
            mDialog_addressSelector = new DialogAddressSelector(mContext,R.style.MMTheme_DataSheet_BottomIn,new DialogAddressSelector.PriorityListener(){
                @Override
                public void refreshPriorityUI(String Provice,String City,String District,String ZipCode)
                {
                    String address = Provice+" "+City+" "+District;
                    mEditZone.setText(address);
                    mEditZone.setSelection(address.length());
                    mEditAddresscode.setText(ZipCode);
                    mEditAddresscode.setSelection(ZipCode.length());
                }
            });
        }
        mDialog_addressSelector.show();
    }

    void postAddAddress()
    {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("receiver",mEditName.getText().toString());
        params.put("receiver_mobile",mEditMobile.getText().toString());
        params.put("zone",mEditZone.getText().toString());
        params.put("address",mEditAddress.getText().toString());
        params.put("post_code",mEditAddresscode.getText().toString());

        APIClient.post(mContext, Constants.URL.UPDATE_ADD_ADDRESS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        it.putExtra("json",json);
                        ToastUtils.shortToast(mContext,"添加成功");
                        setResult(RESULT_OK,it);
                        finish();
                    }
                });
            }
        });
    }

    void postModifyAddress()
    {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("address_id",addressId);
        params.put("receiver",mEditName.getText().toString());
        params.put("receiver_mobile",mEditMobile.getText().toString());
        params.put("zone",mEditZone.getText().toString());
        params.put("address",mEditAddress.getText().toString());
        params.put("post_code",mEditAddresscode.getText().toString());

        APIClient.post(mContext, Constants.URL.UPDATE_MODIFY_ADDRESS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        ToastUtils.shortToast(mContext,"修改成功");
                        Intent it = new Intent();
                        it.putExtra("json",json);
                        setResult(RESULT_OK,it);
                        finish();
                    }
                });
            }
        });
    }
}
