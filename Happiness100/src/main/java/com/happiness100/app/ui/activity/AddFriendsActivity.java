package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/26.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/26 11:27
 */
public class AddFriendsActivity extends BaseActivity {

    @Bind(R.id.back_picture)
    ImageView mBackPicture;
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.myUserId)
    TextView mMyUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        initView();
    }

    void initView()
    {
        Intent itent = getIntent();
        mTextBack.setText(itent.getStringExtra("back") == null?"返回":itent.getStringExtra("back"));
        mTitleViewTitle.setText("添加朋友");
        mMyUserId.setText("我的账号：XF"+mApplication.getUser().getXf());
    }

    @OnClick({R.id.title_view_back, R.id.address_list,R.id.scanview,R.id.search_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.address_list:
                Intent intent = new Intent();
                intent.setClass(AddFriendsActivity.this,CommunicationActivity.class);
                startActivity(intent);
                break;
            case R.id.scanview:
                Intent openCameraIntent = new Intent(mContext,CaptureActivity.class);
                startActivityForResult(openCameraIntent, CaptureActivity.Code_QRcode);
                break;
            case R.id.search_view:
                Intent it_search = new Intent();
                it_search.setClass(AddFriendsActivity.this,AddFriendSearchActivity.class);
                startActivity(it_search);
                break;
        }
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
