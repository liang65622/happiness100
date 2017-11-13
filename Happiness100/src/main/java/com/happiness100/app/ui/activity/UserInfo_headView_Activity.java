package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/11.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.BottomMenuDialog;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.BitmapUtils;
import com.justin.utils.PhotoUtils;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import uk.co.senab.photoview.PhotoView;

/**
 * 作者：jiangsheng on 2016/8/11 14:40
 */
public class UserInfo_headView_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.headImg)
    PhotoView mHeadImg;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.text_right)
    TextView mTextRight;
    private Uri selectUri;
    final int Code_OpenPhoto = 10;
    final int Code_OpenCamera = 11;
    Activity mContext;
    BottomMenuDialog mDialog_ModifyHeadview;
    private PhotoUtils mPhotoUtils;
    private String mBase64Temp;
    private Bitmap mBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_headview);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    private void initView() {
        mTitleViewTitle.setText("个人头像");
        mTextBack.setText("取消");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.GONE);
        mImageRight.setVisibility(View.VISIBLE);
        mImageRight.setImageResource(R.drawable.icon_dian);

        mPhotoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {

            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    File file = new File(uri.getPath());
                    mBitmap = BitmapUtils.getBitmapFromUri(mContext, uri);
//
//                    try {
//                        NativeUtil.compressBitmap(mBitmap, uri.getPath());
//                    }catch (Exception e){
//                        try {
                    try {
                        BitmapUtils.compressAndGenImage(mBitmap,file.getAbsolutePath(),100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                        } catch (IOException e1) {
//                            e1.printStackTrace();
//                        }
//                    }

                    if (file != null) {
                        mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    }
                    try {

                        mBase64Temp = BitmapUtils.encodeBase64File(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mHeadImg.setImageBitmap(mBitmap);


                    UILUtils.displayImage("file:/" + uri.getPath(), mHeadImg);
                    //mHeadImg.setImageBitmap(BitmapUtils.getBitmapFromUri(mContext,uri));
                    postModifyHeadView();
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });

        initHeadView();
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

    void postModifyHeadView() {
        Map<String, String> params = new LinkedHashMap<>();
        if (mBitmap == null) {
            mBitmap = BitmapUtils.getBitmapFromUri(mContext, selectUri);
        }
        params.put("sessionid", mApplication.getUser().getSessionid());


        if (mBitmap != null) {
            if (mBase64Temp != null) {

                params.put("base64Image", mBase64Temp);
            } else {
                params.put("base64Image", BitmapUtils.bitmapToBase64(mBitmap));
            }
        }

        params.put("imageExt", "jpg");
        APIClient.post(mContext, Constants.URL.UPDATE_HEADVIEW, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext, "修改成功");
                        mApplication.getUser().setHeadImageUri(mApplication.getHeadImage(mApplication.getUser().getXf() + "", json));
                        mApplication.getUser().setHeadImage(json);
                        User user = mApplication.getUser();
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(user.getXf() + "", user.getNickname(), Uri.parse(user.getHeadImageUri())));
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }


    private void initHeadView() {

        UILUtils.displayImage(mApplication.getUser().getHeadImageUri(), mHeadImg);
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                openModifyHeadviewDialog();
                break;
        }
    }


    void openModifyHeadviewDialog() {
        if (mDialog_ModifyHeadview != null && mDialog_ModifyHeadview.isShowing()) {
            mDialog_ModifyHeadview.dismiss();
        }
        if (mDialog_ModifyHeadview == null) {
            mDialog_ModifyHeadview = new BottomMenuDialog(mContext, R.style.MMTheme_DataSheet_BottomIn);

            mDialog_ModifyHeadview.setConfirmListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDialog_ModifyHeadview != null && mDialog_ModifyHeadview.isShowing()) {
                        mDialog_ModifyHeadview.dismiss();
                    }
                    mPhotoUtils.takePicture(mContext);
                }
            });
            mDialog_ModifyHeadview.setMiddleListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (mDialog_ModifyHeadview != null && mDialog_ModifyHeadview.isShowing()) {
                        mDialog_ModifyHeadview.dismiss();
                    }
                    mPhotoUtils.selectPicture(mContext);
                }
            });
            mDialog_ModifyHeadview.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog_ModifyHeadview.dismiss();
                }
            });
        }
        mDialog_ModifyHeadview.show();
    }
}
