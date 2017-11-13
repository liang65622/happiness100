package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/11.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
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
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 作者：jiangsheng on 2016/8/11 14:40
 */
public class Family_HeadView_Activity extends BaseActivity {
    private static final String TAG = "Family_HeadView_Activity";
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
    private String mClanId;
    private int mClanRole;
    private int mType;
    private String mImgUrl;
    private Uri mUri;
    private Bitmap mBitmap;
    private String mBase64Temp;
    PhotoViewAttacher mAttacher;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_head);
        ButterKnife.bind(this);
        mContext = this;
        mClanId = getIntent().getStringExtra("clanId");
        mClanRole = getIntent().getIntExtra("clanRole", 0);

        mImgUrl = getIntent().getStringExtra("imgUrl");
        if (mImgUrl == null) {
            mUri = getIntent().getParcelableExtra("imgUri");
        }
        mType = getIntent().getIntExtra("type", 0);
        initView();
    }

    private void initView() {
        if (mType == 1) {
            mTitleViewTitle.setText("家族头像");
        } else {
            mTitleViewTitle.setText("个人头像");
        }
        mTextBack.setText("取消");

        if (mClanRole != 0 && mClanRole != 1) {
            mTitleViewRight.setVisibility(View.VISIBLE);
            mImageRight.setVisibility(View.VISIBLE);
        }


        // Set the Drawable displayed
//        Drawable bitmap = getResources().getDrawable(R.drawable.wallpaper);
//        mImageView.setImageDrawable(bitmap);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        // (not needed unless you are going to change the drawable later)

        mTextRight.setVisibility(View.GONE);

        mImageRight.setImageResource(R.drawable.icon_dian);

        mPhotoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {


            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
//                    UILUtils.displayImage("file:/" + uri.getPath(), mHeadImg);
                    mBitmap = BitmapUtils.getBitmapFromUri(mContext, uri);
                    File file = new File(uri.getPath());
                    try {
                        BitmapUtils.compressAndGenImage(mBitmap,file.getAbsolutePath(),100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (file != null) {
                        mBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    }
                    try {
                        mBase64Temp = BitmapUtils.encodeBase64File(file.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mHeadImg.setImageBitmap(mBitmap);
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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
        params.put("image_ext", "jpg");
        params.put("clan_id", mClanId);
        if (mBitmap != null) {
            if (mBase64Temp != null) {

                params.put("base64_image", mBase64Temp);
            } else {
                params.put("base64_image", BitmapUtils.bitmapToBase64(mBitmap));
            }
        }

        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_MODIFY_CLAN_LOGO, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, false, "")) {
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
                        Intent intent = new Intent();
                        intent.putExtra("img", selectUri);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
    }


    private void initHeadView() {
        if (mUri != null) {
            mHeadImg.setImageBitmap(BitmapUtils.getBitmapFromUri(mContext, mUri));
        } else {
            UILUtils.displayImage(mImgUrl, mHeadImg);
        }
        mAttacher = new PhotoViewAttacher(mHeadImg);
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
