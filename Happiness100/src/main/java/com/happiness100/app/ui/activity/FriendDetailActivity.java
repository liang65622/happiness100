package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/30.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.RemarkManager;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.MatchContactsUser;
import com.happiness100.app.model.Remark;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.bean.ClanRoleEnum;
import com.happiness100.app.ui.widget.DialogFamilyListSelector;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 作者：jiangsheng on 2016/8/30 11:50
 */
public class FriendDetailActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.userHeadImage)
    ImageView mUserHeadImage;
    @Bind(R.id.userNameText)
    TextView mUserNameText;
    @Bind(R.id.userSexImage)
    ImageView mUserSexImage;
    @Bind(R.id.xfText)
    TextView mXfText;
    @Bind(R.id.zoneText)
    TextView mZoneText;
    @Bind(R.id.addFriendsView_addFriendsListButton)
    Button mAddFriendsViewAddFriendsListButton;
    @Bind(R.id.addFriendsView_addToFamilyButton)
    Button mAddFriendsViewAddToFamilyButton;
    @Bind(R.id.addFriendsView)
    LinearLayout mAddFriendsView;
    @Bind(R.id.NormalView_SendMessageButton)
    Button mNormalViewSendMessageButton;
    @Bind(R.id.NormalView_addToFamilyButton)
    Button mNormalViewAddToFamilyButton;
    @Bind(R.id.NormalView)
    LinearLayout mNormalView;
    OpenMode mOpenMode;
    Friend mfriend;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.remark_phone_item)
    RelativeLayout mRemarkPhoneItem;
    @Bind(R.id.phoneNumberListView)
    LinearLayout mPhoneNumberListView;
    @Bind(R.id.NickNameItem)
    LinearLayout mNickNameItem;
    @Bind(R.id.nicknameText)
    TextView mNicknameText;
    String mXF;
    public DialogFamilyListSelector mDialog_FamilyListSelector;

    public static enum OpenMode {
        None,
        addFirend,
        FriendDetailed,
        End,
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detailed);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mfriend != null)
        {
            updateView();
        }
    }

    void initView() {
        mTextBack.setText("返回");
        Intent it = getIntent();
        mfriend = (Friend) it.getParcelableExtra("friend");
        mXF = it.getStringExtra("xf");

        if (mfriend != null)
        {
            updateView();
        }

        if (mXF == null || mXF.isEmpty())
        {
            mXF =mfriend.getXf()+"";
        }
        getFriendDetailData();
    }

    void updateView() {
        if (FriendsManager.getInstance().findFriend(mfriend.getXf() + "") != null) {
            mOpenMode = OpenMode.FriendDetailed;
        } else {
            mOpenMode = OpenMode.addFirend;
        }
        SetActicityMode(mOpenMode);
        mUserNameText.setText(mfriend.getNickname());
        mUserSexImage.setImageResource(mfriend.getSex() == 0 ? R.drawable.icon_boy : R.drawable.icon_girl);
        mXfText.setText("XF"+mfriend.getXf());
        mZoneText.setText(mfriend.getZone());
        UILUtils.displayImage(mApplication.getHeadImage(mfriend.getXf() + "", mfriend.getHeadImage()), mUserHeadImage);

        Remark remark = RemarkManager.getInstance().findRemark(mfriend.getXf() + "");
        mRemarkPhoneItem.setVisibility(View.VISIBLE);
        if (remark.getXf() == 0 || (remark.getMobiles() == null || remark.getMobiles().isEmpty())) {
            mRemarkPhoneItem.setVisibility(View.GONE);

        } else {
            updatePhoneList();
        }

        if (remark.getNoteName() == null||remark.getNoteName().isEmpty())
        {
            mNickNameItem.setVisibility(View.GONE);
        }
        else
        {
            mNickNameItem.setVisibility(View.VISIBLE);
            mNicknameText.setText(mfriend.getNickname());
            mUserNameText.setText(remark.getNoteName());
        }

    }

    View.OnClickListener maekMobileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           final String phoneNum = (String) v.getTag();


            //TODO:打电话
            DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "拨号", phoneNum, new DialogWithYesOrNoUtils.DialogCallBack() {
                @Override
                public void exectEvent() {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum.replace(" ","")));
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);//内部类
                }

                @Override
                public void exectEditEvent(String editText) {

                }

                @Override
                public void updatePassword(String oldPassword, String newPassword) {

                }
            });
        }
    };

    void updatePhoneList()
    {
        mPhoneNumberListView.removeAllViews();
        Remark remark = RemarkManager.getInstance().findRemark(mfriend.getXf()+"");
        if (remark.getXf() == 0||(remark.getMobiles() == null ||remark.getMobiles().isEmpty())) {
            mRemarkPhoneItem.setVisibility(View.GONE);
            return;
        }
        String[] phoneArray = remark.getMobiles().split(",");
        for (int i = 0 ;i <phoneArray.length;++i)
        {
           View vi = mInflater.inflate(R.layout.friend_detailed_mobile_item,null);
           TextView txt= (TextView)vi.findViewById(R.id.phoneNumText);
            txt.setText(phoneArray[i]);
            View makeMobile= vi.findViewById(R.id.make_mobile);

            makeMobile.setTag(phoneArray[i]);
            makeMobile.setOnClickListener(maekMobileListener);
            mPhoneNumberListView.addView(vi);
        }
    }



    void getFriendDetailData()
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("friend_user_id",mXF);
        APIClient.post(this, Constants.URL.INFO_FRIENDS, params, new BaseVolleyListener(this,new LoadDialog(mContext,true,"")) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }
            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext,json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        mfriend = GsonUtils.parseJSON(json,Friend.class);

                        if (mfriend == null)
                        {
                            ToastUtils.shortToast(mContext,"找不到此用户");
                            finish();
                            return;
                        }

                        Remark remark = RemarkManager.getInstance().findRemark(mfriend.getXf()+"");
                        String nickname=  remark.getNoteName();
                        if (nickname == null ||!nickname.isEmpty())
                        {
                            nickname = mfriend.getNickname();
                        }
                        FriendsManager.getInstance().updateFriend(mfriend);
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(""+mfriend.getXf()+"",nickname, Uri.parse(mApplication.getHeadImage(mfriend.getXf()+"",mfriend.getHeadImage()))));
                        updateView();
                    }
                });
            }
        });
    }

    void SetActicityMode(OpenMode mode) {
        mAddFriendsView.setVisibility(View.GONE);
        mNormalView.setVisibility(View.GONE);
        mTitleViewTitle.setText("自己");
        if (mfriend.getXf() == mApplication.getUser().getXf())
        {
            mNormalViewSendMessageButton.setVisibility(View.VISIBLE);
            return;
        }
        if (mode == OpenMode.addFirend) {
            mTitleViewTitle.setText("添加朋友");
            mAddFriendsView.setVisibility(View.VISIBLE);
            mAddFriendsViewAddFriendsListButton.setVisibility(View.VISIBLE);
            mAddFriendsViewAddToFamilyButton.setVisibility(View.GONE);
            //// TODO: 2016/8/31 需要判断是不是家族族长
            List<FamilyBaseInfo> list =  mApplication.getUser().getFamilyBaseInfos();
            if (list != null)
            {
                for (int i = 0;i <  list.size();++i)
                {
                    FamilyBaseInfo info = list.get(i);
                    if(info.getClanRole()!= ClanRoleEnum.NORMAL.getCode())
                    {
                        mAddFriendsViewAddToFamilyButton.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        } else {
            mTitleViewTitle.setText("详细信息");
            mTitleViewRight.setVisibility(View.VISIBLE);
            mImageRight.setVisibility(View.VISIBLE);
            mImageRight.setImageResource(R.drawable.icon_dian);
            mTextRight.setVisibility(View.GONE);
            mNormalView.setVisibility(View.VISIBLE);
            mNormalViewSendMessageButton.setVisibility(View.VISIBLE);
            mNormalViewAddToFamilyButton.setVisibility(View.GONE);
            ////TODO: 2016/8/31 需要判断是不是家族族长
            List<FamilyBaseInfo> list =  mApplication.getUser().getFamilyBaseInfos();
            if (list != null)
            {
                for (int i = 0;i <  list.size();++i)
                {
                    FamilyBaseInfo info = list.get(i);
                    if(info.getClanRole()!= ClanRoleEnum.NORMAL.getCode())
                    {
                        mNormalViewAddToFamilyButton.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

        }
        mOpenMode = mode;
    }

    @OnClick({R.id.title_view_right,R.id.moreView, R.id.indexView, R.id.title_view_back, R.id.userHeadImage, R.id.addFriendsView_addFriendsListButton, R.id.addFriendsView_addToFamilyButton, R.id.NormalView_SendMessageButton, R.id.NormalView_addToFamilyButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.userHeadImage:
                Intent headviewIntent = new Intent(mContext, ActivityHeadViewForOhter.class);
                headviewIntent.putExtra("Uri", mApplication.getHeadImage(mfriend.getXf()+"",mfriend.getHeadImage()));
                startActivity(headviewIntent);
                break;
            case R.id.addFriendsView_addFriendsListButton:
                Intent addFriendVerificationIntent = new Intent(mContext, AddFriendVerificationActivity.class);
                addFriendVerificationIntent.putExtra("friend", mfriend);
                startActivity(addFriendVerificationIntent);
                break;
            case R.id.addFriendsView_addToFamilyButton:
                OpenFamilyListSelector();
                break;
            case R.id.NormalView_SendMessageButton:
                if (RongIM.getInstance() != null) {
                    //RongIM.getInstance().startPrivateChat(mContext, mfriend.getXf() + "", mfriend.getNickname());
                    startPrivateChatForResult(mfriend.getXf() + "", mfriend.getNickname());
                }
                break;
            case R.id.NormalView_addToFamilyButton:
                OpenFamilyListSelector();
                break;
            case R.id.indexView:
                Intent RemarkIntent = new Intent(mContext, RemarkActivity.class);
                RemarkIntent.putExtra("friend", mfriend);
                RemarkIntent.putExtra("back", mTitleViewTitle.getText().toString());
                startActivityForResult(RemarkIntent,Constants.ActivityIntentIndex.RemarkActivityIndex);
                break;
            case R.id.moreView:
                ToastUtils.shortToast(mContext,"功能暂未开放");
                break;
            case R.id.title_view_right:
                Intent dataSetIntent = new Intent(mContext, FriendDataSetActivity.class);
                dataSetIntent.putExtra("friend", mfriend);
                startActivityForResult(dataSetIntent,Constants.ActivityIntentIndex.FriendDataSetActivityIndex);
                break;
        }
    }

    private void OpenFamilyListSelector() {
        if (mDialog_FamilyListSelector != null && mDialog_FamilyListSelector.isShowing()) {
            mDialog_FamilyListSelector.dismiss();
        }
        if (mDialog_FamilyListSelector == null) {
            Map<String,FamilyBaseInfo> Datas = new HashMap<String,FamilyBaseInfo>();
            List<FamilyBaseInfo> list =  mApplication.getUser().getFamilyBaseInfos();
            for (int i = 0;i <  list.size();++i)
            {
                FamilyBaseInfo info = list.get(i);
                if(info.getClanRole()!= ClanRoleEnum.NORMAL.getCode())
                    Datas.put(info.getClanName(),info);
            }
            mDialog_FamilyListSelector = new DialogFamilyListSelector(mContext,Datas,R.style.MMTheme_DataSheet_BottomIn,new DialogFamilyListSelector.PriorityListener(){
                @Override
                public void refreshPriorityUI(String familyId)
                {
                    MatchContactsUser user = new MatchContactsUser();
                    user.setUserId(mfriend.getXf());
                    user.setHeadImage(mfriend.getHeadImage());
                    user.setMobile(mfriend.getMobile());
                    user.setNickname(mfriend.getNickname());
                    Intent intent = new Intent(mContext, AddMemberCommitActiivty.class);
                        intent.putExtra("user" ,user);
                        intent.putExtra("clanid" ,familyId);
                    startActivity(intent);

                    Log.e("familyId","familyId = "+familyId);
                    mDialog_FamilyListSelector.dismiss();
                }
            });
        }
        mDialog_FamilyListSelector.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case Constants.ActivityIntentIndex.FriendDataSetActivityIndex:
                    int FriendDataCode = data.getIntExtra("code",Constants.FriendHandle.None);
                    if (FriendDataCode == Constants.FriendHandle.Delete)
                    {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code", Constants.FriendHandle.Delete);
                        setResult(RESULT_OK,intent1);
                        finish();
                    }
                    else
                    {
                        getFriendDetailData();
                    }

                    break;
                case Constants.ActivityIntentIndex.ConversationActivityIndex:
                    int code = data.getIntExtra("code",Constants.FriendHandle.None);
                    if (code == Constants.FriendHandle.Delete)
                    {
                        Intent intent2 = new Intent();
                        intent2.putExtra("code",Constants.FriendHandle.Delete);
                        setResult(RESULT_OK,intent2);
                        finish();
                    }
                    break;
            }
        }
    }
}
