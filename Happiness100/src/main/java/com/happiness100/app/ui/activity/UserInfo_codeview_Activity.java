package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/11.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.zxing.WriterException;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.BottomMenuDialog;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.QrCodeUtils;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/11 16:23
 */
public class UserInfo_codeview_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.content_info_headview)
    ImageView mContentInfoHeadview;
    @Bind(R.id.content_content)
    ImageView mContentContent;
    @Bind(R.id.content_info_name)
    TextView mContentInfoName;
    @Bind(R.id.content_info_sex)
    ImageView mContentInfoSex;
    @Bind(R.id.content_info_address)
    TextView mContentInfoAddress;
    @Bind(R.id.content_desc)
    TextView mContentDesc;
    BottomMenuDialog mDialog_QRCodeOperation;
    Activity mContext;
    Bitmap QRCode;
    @Bind(R.id.text_right)
    TextView mTextRight;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_codeview);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    public void initView() {
        mTitleViewTitle.setText("我的二维码");
        mTextBack.setText("返回");
        mTextRight.setVisibility(View.GONE);
        mTitleViewRight.setVisibility(View.VISIBLE);
        mImageRight.setVisibility(View.VISIBLE);
        mImageRight.setImageResource(R.drawable.icon_dian);

        User user = mApplication.getUser();
        mContentInfoSex.setImageResource(user.getSex() == 0?R.drawable.icon_boy:R.drawable.icon_girl);
        UILUtils.displayImage(user.getHeadImageUri(), mContentInfoHeadview);
        try {
            QRCode = QrCodeUtils.Create2DCode(Constants.QrCode.QrCodeIndex + user.getXf(), 800, 800);
            mContentContent.setImageBitmap(QRCode);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mContentInfoName.setText(user.getNickname());
        mContentInfoAddress.setText(user.getZone());
        //TODO:页面内容初始化
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                openQRCodeOperation();
                break;
        }
    }

    void openQRCodeOperation() {
        if (mDialog_QRCodeOperation != null && mDialog_QRCodeOperation.isShowing()) {
            mDialog_QRCodeOperation.dismiss();
        }
        if (mDialog_QRCodeOperation == null) {
            mDialog_QRCodeOperation = new BottomMenuDialog(mContext, "保存图片", "扫描二维码", "取消");
            mDialog_QRCodeOperation.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog_QRCodeOperation.dismiss();
                }
            });
            mDialog_QRCodeOperation.setMiddleListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openCameraIntent = new Intent(UserInfo_codeview_Activity.this, CaptureActivity.class);
                    startActivityForResult(openCameraIntent, CaptureActivity.Code_QRcode);
                    mDialog_QRCodeOperation.dismiss();
                }
            });

            mDialog_QRCodeOperation.setConfirmListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveImageToGallery(mContext, QRCode);
                    ToastUtils.shortToast(mContext, "保存成功");
                    if (mDialog_QRCodeOperation != null && mDialog_QRCodeOperation.isShowing()) {
                        mDialog_QRCodeOperation.dismiss();
                    }
                }
            });
        }
        mDialog_QRCodeOperation.show();
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent1 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent1.setData(uri);
        context.sendBroadcast(intent1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CaptureActivity.Code_QRcode:
                    String info = data.getStringExtra("Content");
                    if (info.indexOf("http://") == 0||info.indexOf("www.") == 0)
                    {
                        if(info.indexOf("www.") == 0)
                        {
                            info = "http://"+info;
                        }
                        Uri uri = Uri.parse(info);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                    }
                    else if (info.indexOf(Constants.QrCode.QrCodeIndex) == 0)
                    {
                        String scanStr = info.substring(Constants.QrCode.QrCodeIndex.length(),info.length());

                        if (scanStr.compareTo(mApplication.getUser().getXf()+"") == 0) {
                            ToastUtils.shortToast(mContext, "不能添加自己到通讯录");
                            return;
                        }
                        postSearchFriend(scanStr);
                    }
                    else
                    {
                        Intent resultIntent = new Intent(mContext, QRScanResultActivity.class);
                        resultIntent.putExtra("content",info);
                        startActivity(resultIntent);
                    }
                default:
                    break;
            }
        }
    }
    void postSearchFriend(String searchStr) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("search_word", "xf"+searchStr);
        APIClient.postWithSessionId(mContext, Constants.URL.SEARCH_FRIENDS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        if (json != null && !json.isEmpty()) {
                            Friend friend = GsonUtils.parseJSON(json, Friend.class);
                            if (friend == null) {
                                ToastUtils.shortToast(mContext, "不能识别");
                                return;
                            }
                            if (friend.getXf() == mApplication.getUser().getXf()) {
                                ToastUtils.shortToast(mContext, "不能添加自己到通讯录");
                                return;
                            }
                            Friend result = FriendsManager.getInstance().findFriend(friend.getXf() + "");
                            Intent friendDetailIntent = new Intent(mContext, FriendDetailActivity.class);
                            friendDetailIntent.putExtra("friend", friend);
                            startActivityForResult(friendDetailIntent, Constants.ActivityIntentIndex.FriendDetailActivityIndex);

                        }
                        else
                        {
                            ToastUtils.shortToast(mContext, "不能识别");
                        }

                    }
                });
            }
        });
    }
}
