package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/6.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.happiness100.app.R;
import com.happiness100.app.ui.widget.BottomMenuDialog;
import com.happiness100.app.utils.Constants;
import com.justin.utils.Base64Util;
import com.justin.utils.QrCodeUtils;
import com.justin.utils.ToastUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/6 22:25
 */
public class GroupQRCodeActivity extends BaseActivity {
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
    @Bind(R.id.name)
    TextView mName;
    BottomMenuDialog mDialog_QRCodeOperation;
    Activity mContext;
    Bitmap QRCode;
    @Bind(R.id.text_right)
    TextView mTextRight;
    String mId;
    Bitmap mGroupBitmap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_qrcode_activity);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    public void initView() {
        Intent intent = getIntent();
        mTitleViewTitle.setText("群二维码");
        mTextBack.setText(intent.getStringExtra("back") == null?"":intent.getStringExtra("back"));
        mId = intent.getStringExtra("id");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mImageRight.setVisibility(View.VISIBLE);
        mImageRight.setImageResource(R.drawable.icon_dian);
        mTextRight.setVisibility(View.GONE);
        String base64 = intent.getStringExtra("ext");

        if (base64 != null &&!base64.isEmpty())
        {
            InputStream is= null;
            try {
                is = new ByteArrayInputStream( Base64Util.decode(base64));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            BufferedInputStream bis=new BufferedInputStream(is);
            mGroupBitmap = BitmapFactory.decodeStream(bis);
            if (mGroupBitmap != null)
            {
                mContentInfoHeadview.setImageBitmap(mGroupBitmap);
            }

            try {
                QRCode = QrCodeUtils.Create2DCode(Constants.QrCode.QrCodeIndex+mId, 800, 800);
                mContentContent.setImageBitmap(QRCode);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        mName.setText(intent.getStringExtra("name"));
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
                    Intent openCameraIntent = new Intent(GroupQRCodeActivity.this, CaptureActivity.class);
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
                    ToastUtils.shortToast(mContext, info);
                    break;
                default:
                    break;
            }
        }
    }
}
