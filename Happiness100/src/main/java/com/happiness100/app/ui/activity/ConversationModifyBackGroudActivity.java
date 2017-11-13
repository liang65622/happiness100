package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/22.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.manager.ConversationBackGroudImageManager;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BitmapUtils;
import com.justin.utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/22 15:15
 */
public class ConversationModifyBackGroudActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    private String mTargetId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_conversation_backgroud);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent it = getIntent();
        mTextBack.setText(it.getStringExtra("back") == null ?"返回":it.getStringExtra("back"));
        mTitleViewTitle.setText("聊天背景");
        mTargetId = it.getStringExtra("id");
    }

    private Bitmap createThumbnail(String filepath, int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = i;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(filepath, options);
    }

    private Bitmap createThumbnail(Bitmap bm,int i )
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
        BitmapFactory.Options newOpts =  new  BitmapFactory.Options();
        newOpts.inSampleSize = i;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        ByteArrayInputStream isBm =  new  ByteArrayInputStream(out.toByteArray());
        return BitmapFactory.decodeStream(isBm,  null ,  newOpts );
    }

    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    @OnClick({R.id.title_view_back, R.id.selectItem, R.id.makeItem})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.selectItem:
                PhotoPicture();
                break;
            case R.id.makeItem:
                CapturePicture();
                break;
        }
    }
    public static final String CROP_FILE_NAME = "crop_file.jpg";
    private Uri buildUri(Activity activity) {
        if (CommonUtils.checkSDCard()) {
            return Uri.fromFile(Environment.getExternalStorageDirectory()).buildUpon().appendPath(CROP_FILE_NAME).build();
        } else {
            return Uri.fromFile(activity.getCacheDir()).buildUpon().appendPath(CROP_FILE_NAME).build();
        }
    }
    public void CapturePicture() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // 获取拍照后未压缩的原图片，并保存在uri路径中
        camera.putExtra(MediaStore.EXTRA_OUTPUT, buildUri(mContext));
        startActivityForResult(camera, 1);
    }

    public void PhotoPicture() {
        Intent picture = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 2);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        File outDir = null;
        String state = Environment.getExternalStorageState();
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (new File(buildUri(mContext).getPath()).exists()) {
                bitmap = BitmapUtils.getBitmapFromUrl(buildUri(mContext).getPath(),getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight());
                bitmap = createThumbnail(bitmap, 2);
                ConversationBackGroudImageManager.getInstance().update(Constants.Function.BACKGROUD+mApplication.getUser().getXf()+"to"+mTargetId,convertIconToString(bitmap));
                Intent it = new Intent();
                it.putExtra("code",Constants.Function.RESULT_MODIFYPICTURE);
                setResult(RESULT_OK,it);
                finish();
            }
        }// 相册显示界面
        else if (requestCode == 2 && data != null && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(
                    selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);// 取出图片路径
            c.close();
            // 调用压缩方法压缩图片
            bitmap = createThumbnail(picturePath, 2);
            ConversationBackGroudImageManager.getInstance().update(Constants.Function.BACKGROUD+mApplication.getUser().getXf()+"to"+mTargetId,convertIconToString(bitmap));
            Intent it = new Intent();
            it.putExtra("code",Constants.Function.RESULT_MODIFYPICTURE);
            setResult(RESULT_OK,it);
            finish();

        }
    }
}
