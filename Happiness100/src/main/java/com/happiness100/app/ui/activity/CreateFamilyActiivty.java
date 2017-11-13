package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyBaseInfo;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/16 15:52
 */
public class CreateFamilyActiivty extends BaseActivity implements BaseViewInterface {

    private static final String TAG = "CreateFamilyActiivty";
    @Bind(R.id.text_back)
    TextView textBack;
    @Bind(R.id.title_view_back)
    LinearLayout titleViewBack;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.iv_family_img)
    ImageView ivFamilyImg;
    @Bind(R.id.edit_family_name)
    EditText editFamilyName;
    @Bind(R.id.edit_family_remark)
    EditText editFamilyRemark;
    private PhotoUtils mPhotoUtils;
    Uri selectUri;
    private BottomMenuDialog dialog;
    Activity mContext;
    private LoadDialog mLoadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family);
        ButterKnife.bind(this);
        initView();
        mContext = this;
        mLoadDialog = new LoadDialog(mContext, false, "");
        setPortraitChangeListener();
    }

    @OnClick({R.id.title_view_back, R.id.btn_create_family, R.id.iv_family_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.btn_create_family:
                createFamily();
                break;
            case R.id.iv_family_img:
                showPhotoDialog();
                break;
        }
    }

    private void createFamily() {

        //非空验证
        if (TextUtils.isEmpty(editFamilyName.getText())) {
            showToast("请填写家族名");
            return;
        }


        Map<String, String> params = new HashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("clan_name", editFamilyName.getText().toString());
        params.put("gong_gao", editFamilyRemark.getText().toString());
        params.put("image_ext", "jpg");
        Bitmap bitmap = BitmapUtils.getBitmapFromUri(mContext, selectUri);
        if (bitmap != null) {
            params.put("base64_image", BitmapUtils.bitmapToBase64(bitmap));
        }
        APIClient.post(mContext, Constants.URL.CREATE_CLAN, params, new BaseVolleyListener(mContext, mLoadDialog) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                mLoadDialog.dismiss();
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        ToastUtils.shortToast(mContext, "创建成功");
                        //TODO 加载个人家族信息
                        initFamilyList();
                    }
                });
            }
        });
    }

    private void initFamilyList() {
        Map<String, String> params = new HashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        APIClient.post(mContext, Constants.URL.GET_CLIAN_LIST, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        Log.e(TAG, "parsRespone:" + json);
                        Type type = new TypeToken<ArrayList<FamilyBaseInfo>>() {
                        }.getType();
                        List<FamilyBaseInfo> familyBaseInfos = GsonUtils.parseJSONArray(json, type);
                        //如果家族列表为空，家族界面显示创建家族和搜索家族

                        if (familyBaseInfos == null || familyBaseInfos.size() == 0) {//TODO
//                            FamilyFragment familyFragment = (FamilyFragment) getSupportFragmentManager().getFragments().get(0);
//                            familyFragment.
                        } else {
                            mApplication.getUser().setFamilyBaseInfos(familyBaseInfos);
                            for (FamilyBaseInfo familyBaseInfo : familyBaseInfos) {
                                if (familyBaseInfo.isIsDefault() == true) {
                                    mApplication.getUser().setDefaultFamilyBaseInfo(familyBaseInfo);
                                }
                            }
                            setResult(RESULT_OK);
                            mContext.finish();

                        }
                    }
                });
            }
        });

    }

    private void setPortraitChangeListener() {
        mPhotoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
//                    LoadDialog.show(mContext);
                    Bitmap bitmap = BitmapUtils.getBitmapFromUri(mContext, selectUri);
//                    Log.e(TAG,bitmap.getWidth()+" "+  bitmap.getHeight());
//                    UILUtils.displayImage("file:/" + uri.getPath(), ivFace);
                    ivFamilyImg.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onPhotoCancel() {
                Bitmap bitmap = BitmapUtils.getBitmapFromUri(mContext, selectUri);
                UILUtils.displayImage("file:/" + selectUri.getPath(), ivFamilyImg);
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
                mPhotoUtils.takePicture(mContext);
            }
        });
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                mPhotoUtils.selectPicture(mContext);
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
                mPhotoUtils.onActivityResult(mContext, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void initView() {
        titleViewTitle.setText("创建家族");
    }

    @Override
    public void initData() {

    }
}
