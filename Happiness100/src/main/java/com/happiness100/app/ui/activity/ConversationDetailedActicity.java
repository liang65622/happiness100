package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/30.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.RemarkManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.Remark;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.SwitchView;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 作者：jiangsheng on 2016/8/30 21:04
 */
public class ConversationDetailedActicity extends BaseActivity {

    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.headViewLayout_userHeadImage)
    ImageView mHeadViewLayoutUserHeadImage;
    @Bind(R.id.headViewLayout_userNameText)
    TextView mHeadViewLayoutUserNameText;
    @Bind(R.id.message_switch)
    SwitchView mMessageSwitch;
    String mUserId;
    Friend mfriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_detailed);
        ButterKnife.bind(this);
        mHeadViewLayoutUserHeadImage.setVisibility(View.GONE);
        mHeadViewLayoutUserNameText.setVisibility(View.GONE);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mfriend != null)
        {
            String notename = mfriend.getNickname();
            Remark remark = RemarkManager.getInstance().findRemark(mfriend.getXf()+"");
            if (remark.getNoteName() != null && !remark.getNoteName().isEmpty())
            {
                notename = remark.getNoteName();
            }
            RongIM.getInstance().refreshUserInfoCache(new UserInfo(""+mfriend.getXf()+"",notename, Uri.parse(mApplication.getHeadImage(mfriend.getXf()+"",mfriend.getHeadImage()))));
            UILUtils.displayImage(mApplication.getHeadImage(mfriend.getXf()+"",mfriend.getHeadImage()), mHeadViewLayoutUserHeadImage);
            mHeadViewLayoutUserNameText.setText(notename);
        }
    }

    void initView() {
        mTextBack.setText("返回");
        mTitleViewTitle.setText("聊天详情");
        mUserId = getIntent().getStringExtra("UserId");

        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.PRIVATE, mUserId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                mMessageSwitch.setState(conversationNotificationStatus.getValue() == 0);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });

        mMessageSwitch.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                setToggleStatus(Conversation.ConversationNotificationStatus.DO_NOT_DISTURB);
            }

            @Override
            public void toggleToOff() {
                setToggleStatus(Conversation.ConversationNotificationStatus.NOTIFY);
            }
        });
        getDetailData();
    }

    void setToggleStatus(final Conversation.ConversationNotificationStatus status)
    {
        RongIM.getInstance().setConversationNotificationStatus( Conversation.ConversationType.PRIVATE,mUserId,status, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                ToastUtils.shortToast(mContext,"设置成功");
                if (status == Conversation.ConversationNotificationStatus.NOTIFY)
                {
                    mMessageSwitch.setState(false);
                }
                else
                {
                    mMessageSwitch.setState(true);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.shortToast(mContext,"设置失败");
                if (status == Conversation.ConversationNotificationStatus.NOTIFY)
                {
                    mMessageSwitch.setState(true);
                }
                else
                {
                    mMessageSwitch.setState(false);
                }
            }
        });
    }

    void getDetailData()
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("friend_user_id",mUserId);
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
                        mHeadViewLayoutUserHeadImage.setVisibility(View.VISIBLE);
                        mHeadViewLayoutUserNameText.setVisibility(View.VISIBLE);
                        mfriend = GsonUtils.parseJSON(json,Friend.class);

                        String notename = mfriend.getNickname();
                        Remark remark = RemarkManager.getInstance().findRemark(mfriend.getXf()+"");
                        if (remark.getNoteName() != null && !remark.getNoteName().isEmpty())
                        {
                            notename = remark.getNoteName();
                        }

                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(""+mfriend.getXf()+"",notename, Uri.parse(mApplication.getHeadImage(mfriend.getXf()+"",mfriend.getHeadImage()))));
                        UILUtils.displayImage(mApplication.getHeadImage(mfriend.getXf()+"",mfriend.getHeadImage()), mHeadViewLayoutUserHeadImage);
                        mHeadViewLayoutUserNameText.setText(notename);
                        FriendsManager.getInstance().updateFriend(mfriend);
                    }
                });
            }
        });
    }


    @OnClick({R.id.conversationItem_talkImage_Layout, R.id.conversationItem_SetCurrentTalkBackgroud_Layout, R.id.conversationItem_findTalkContent_Layout, R.id.conversationItem_clearTalkRecord_Layout,R.id.title_view_back, R.id.headViewLayout, R.id.expandViewLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.headViewLayout:
                Intent friendDetailIntent = new Intent(mContext, FriendDetailActivity.class);
                friendDetailIntent.putExtra("friend",  mfriend);
                startActivityForResult(friendDetailIntent,Constants.ActivityIntentIndex.FriendDetailActivityIndex);
                break;
            case R.id.expandViewLayout:
                Intent SponsorGroupChatIntent = new Intent(mContext, SponsorGroupChatActivity.class);
                SponsorGroupChatIntent.putExtra("default",mfriend);
                startActivity(SponsorGroupChatIntent);
                break;
            case R.id.conversationItem_talkImage_Layout:
                break;
            case R.id.conversationItem_SetCurrentTalkBackgroud_Layout:
                Intent ModifybgIntent=  new Intent(mContext,ConversationModifyBackGroudActivity.class);
                ModifybgIntent.putExtra("back","聊天详情");
                ModifybgIntent.putExtra("id",mfriend.getXf()+"");
                startActivityForResult(ModifybgIntent,Constants.ActivityIntentIndex.ConversationModifyBackGroudActivityIndex);
                break;
            case R.id.conversationItem_findTalkContent_Layout:
                break;
            case R.id.conversationItem_clearTalkRecord_Layout:
                RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, mUserId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        ToastUtils.shortToast(mContext, "清除完成");
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        ToastUtils.shortToast(mContext, "清除失败");
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case Constants.ActivityIntentIndex.FriendDetailActivityIndex: {
                    int code = data.getIntExtra("code",Constants.FriendHandle.None);
                    if (code == Constants.FriendHandle.Delete)
                    {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code",Constants.FriendHandle.Delete);
                        setResult(RESULT_OK,intent1);
                        finish();
                    }
                    break;
                }

                case Constants.ActivityIntentIndex.ConversationModifyBackGroudActivityIndex:
                {
                    int code = data.getIntExtra("code",Constants.FriendHandle.None);
                    if (code == Constants.Function.RESULT_MODIFYPICTURE)
                    {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code",Constants.Function.RESULT_MODIFYPICTURE);
                        setResult(RESULT_OK,intent1);
                        finish();
                    }
                    break;
                }

            }
        }
    }
}
