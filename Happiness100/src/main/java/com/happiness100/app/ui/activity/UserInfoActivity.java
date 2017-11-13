package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/10.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.UILUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/10 15:45
 */
public class UserInfoActivity extends BaseActivity {

    @Bind(R.id.content_item_picture_value)
    ImageView mContentItemPictureValue;
    @Bind(R.id.content_item_mobile_value)
    TextView mContentItemMobileValue;
    @Bind(R.id.content_item_nickName_value)
    TextView mContentItemNickNameValue;
    @Bind(R.id.content_item_code_value)
    ImageView mContentItemCodeValue;
    @Bind(R.id.content_item_sex_value)
    TextView mContentItemSexValue;
    @Bind(R.id.content_item_birthday_value)
    TextView mContentItemBirthdayValue;
    @Bind(R.id.content_item_region_value)
    TextView mContentItemRegionValue;
    @Bind(R.id.content_item_desc_value)
    TextView mContentItemDescValue;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.text_back)
    TextView TextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    App mApplication;
    Activity mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        ButterKnife.bind(this);
        mApplication = (App) getApplication();
        mContext = this;
        initView();
    }

    protected void initView() {

        mTitleViewRight.setVisibility(View.GONE);
        TextBack.setText("返回");
        mTitleViewTitle.setText("个人信息");
        SharedPreferencesContext.init(getApplicationContext());

        updateView();
        updatePersonSign();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView();
    }

    void updateView()
    {
        User user = mApplication.getUser();

        mContentItemNickNameValue.setText(user.getNickname());
        mContentItemMobileValue.setText(user.getMobile());

        UILUtils.displayImage(user.getHeadImageUri(),mContentItemPictureValue);

        mContentItemSexValue.setText(user.getSex()==0?"男":"女");
        mContentItemBirthdayValue.setText(user.getBirthday());
        mContentItemRegionValue.setText(user.getZone());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getKeyCode())
        {
            case KeyEvent.KEYCODE_BACK:
                setResult(RESULT_OK);
                finish();
            break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.title_view_back, R.id.content_item_picture, R.id.content_item_mobile, R.id.content_item_nickName, R.id.content_item_code, R.id.content_item_address, R.id.content_item_sex, R.id.content_item_birthday, R.id.content_item_region, R.id.content_item_desc})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.title_view_back:
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.content_item_picture:
                intent.setClass(UserInfoActivity.this, UserInfo_headView_Activity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.content_item_mobile:
                intent.setClass(UserInfoActivity.this, UserInfo_mobile_Activity.class);
                startActivityForResult(intent,2);
                break;
            case R.id.content_item_nickName:
                intent.setClass(UserInfoActivity.this, UserInfo_modifyNickname_Activity.class);
                startActivityForResult(intent,3);
                break;
            case R.id.content_item_code:
                intent.setClass(UserInfoActivity.this, UserInfo_codeview_Activity.class);
                startActivityForResult(intent,4);
                break;
            case R.id.content_item_address:
                intent.setClass(UserInfoActivity.this, UserInfo_address_Activity.class);
                startActivityForResult(intent,5);
                break;
            case R.id.content_item_sex:
                intent.setClass(UserInfoActivity.this, UserInfo_sex_Activity.class);
                startActivityForResult(intent, 6);
                break;
            case R.id.content_item_birthday:
                intent.setClass(UserInfoActivity.this, UserInfo_birthday_Activity.class);
                startActivityForResult(intent,7);
                break;
            case R.id.content_item_region:
                intent.setClass(UserInfoActivity.this, UserInfo_zone_Activity.class);
                startActivityForResult(intent,8);
                break;
            case R.id.content_item_desc:
                intent.setClass(UserInfoActivity.this, UserInfo_PersonSign_Activity.class);
                startActivityForResult(intent,9);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode标示请求的标示   resultCode表示有数据
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    UILUtils.displayImage(mApplication.getUser().getHeadImageUri(),mContentItemPictureValue);
                    break;
                case 2:
                    mContentItemMobileValue.setText(mApplication.getUser().getMobile());
                    break;
                case 3:
                    mContentItemNickNameValue.setText(mApplication.getUser().getNickname());
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    postModifySex(data.getIntExtra("Sex", 0));
                    break;
                case 7:
                    mContentItemBirthdayValue.setText(mApplication.getUser().getBirthday());
                    break;
                case 8:
                    mContentItemRegionValue.setText(mApplication.getUser().getZone());
                    break;
                case 9:
                    updatePersonSign();
                    break;
            }
        }
    }

    void updatePersonSign()
    {
        User user = mApplication.getUser();
        String sign = user.getPersonSign();
        if(sign == null || sign.isEmpty())
            mContentItemDescValue.setText("未填写");
        else
            mContentItemDescValue.setText(sign);
    }

    private void postModifySex(final int sex) {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("gender",""+sex);
        APIClient.post(mContext, Constants.URL.UPDATE_SEX, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        mApplication.getUser().setSex(sex);
                        mContentItemSexValue.setText(sex == 0?"男" : "女");
                    }
                });
            }
        });
    }

}
