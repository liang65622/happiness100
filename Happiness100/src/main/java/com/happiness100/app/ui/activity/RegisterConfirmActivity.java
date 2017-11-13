package com.happiness100.app.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.BottomMenuDialog;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.BitmapUtils;
import com.justin.utils.GsonUtils;
import com.justin.utils.PhotoUtils;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterConfirmActivity extends BaseLoginActivity implements BaseViewInterface {


    private static final String TAG = "RegisterConfirmActivity";
    @Bind(R.id.iv_face)
    ImageView ivFace;
    @Bind(R.id.edit_nick_name)
    EditText editNickName;
    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private BaseActivity mContext;

    private BottomMenuDialog dialog;
    private PhotoUtils photoUtils;
    private Uri selectUri;
    private String mPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_confirm);
        ButterKnife.bind(this);
        mContext = this;
        mPhoneNum = getIntent().getStringExtra("phone_num");
        String verCode = getIntent().getStringExtra("ver_code");
        setPortraitChangeListener();
        initView();
    }


    @OnClick({R.id.tv_cancel, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }


    private void register() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("mobile", mPhoneNum);
        params.put("nickname", editNickName.getText().toString());

        Bitmap bitmap = BitmapUtils.getBitmapFromUri(mContext, selectUri);
        if (bitmap != null) {
            params.put("base64Image", BitmapUtils.bitmapToBase64(bitmap));
        }
        params.put("imageExt", "jpeg");
        params.put("client", "1");

        APIClient.post(mContext, Constants.URL.REGISTER, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,false,"")) {
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
                        User user = GsonUtils.parseJSON(json, User.class);
                        mApplication.saveUserInfo(user);
                        ToastUtils.shortToast(mContext, R.string.registet_success);
                        initHealthData(mContext);

                    }
                });
            }
        });
    }

    public static void main(String[] args) {
        String base64Str1 = Base64.encodeToString("test".getBytes(), Base64.DEFAULT);
        System.out.print(base64Str1);
    }

    @OnClick(R.id.iv_face)
    public void onClick() {
        showPhotoDialog();
    }

    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
//                    LoadDialog.show(mContext);
                    Bitmap bitmap = BitmapUtils.getBitmapFromUri(mContext, selectUri);
//                    Log.e(TAG,bitmap.getWidth()+" "+  bitmap.getHeight());
//                    UILUtils.displayImage("file:/" + uri.getPath(), ivFace);
                    ivFace.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onPhotoCancel() {
                Bitmap bitmap = BitmapUtils.getBitmapFromUri(mContext, selectUri);
                if (selectUri != null) {
                    UILUtils.displayImage("file:/" + selectUri.getPath(), ivFace);
                }
            }
        });
    }

    /**
     * 弹出底部框
     */
    private void showPhotoDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        dialog = new BottomMenuDialog(mContext);
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.takePicture(mContext);
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                photoUtils.selectPicture(mContext);
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(mContext, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void initView() {
        tvTitle.setText(R.string.pls_input_user_info);
    }

    @Override
    public void initData() {

    }
}
