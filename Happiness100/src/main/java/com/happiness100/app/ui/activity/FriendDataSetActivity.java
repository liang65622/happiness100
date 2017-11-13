package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/2.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.RemarkManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.NotifyMessage;
import com.happiness100.app.model.Remark;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 作者：jiangsheng on 2016/9/2 20:53
 */
public class FriendDataSetActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.remarkText)
    TextView mRemarkText;
    Friend mFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataset);
        ButterKnife.bind(this);
        initView();
    }

    void initView()
    {
        Intent it = getIntent();
        mFriend = it.getParcelableExtra("friend");
        mTextBack.setText(it.getStringExtra("back") == null?"返回":it.getStringExtra("back"));
        mTitleViewTitle.setText("资料设置");

        Remark remark = RemarkManager.getInstance().findRemark(mFriend.getXf()+"");
        if(remark.getNoteName() != null && !remark.getNoteName().isEmpty())
        {
            mRemarkText.setText(remark.getNoteName());
        }
        else
        {
            mRemarkText.setVisibility(View.GONE);
        }
    }


    @OnClick({R.id.record, R.id.button,R.id.title_view_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record:
                Intent RemarkIntent = new Intent(mContext, RemarkActivity.class);
                RemarkIntent.putExtra("friend", mFriend);
                RemarkIntent.putExtra("back", mTitleViewTitle.getText().toString());
                startActivityForResult(RemarkIntent,Constants.ActivityIntentIndex.RemarkActivityIndex);
                break;
            case R.id.button:
                DialogWithYesOrNoUtils.getInstance().showDialog(mContext,"删除好友","好友的聊天记录和好友会一并删除，您确认要删除这个好友吗。",new DialogWithYesOrNoUtils.DialogCallBack() {

                    @Override
                    public void exectEvent() {
                        postDeleteFriend();
                    }

                    @Override
                    public void exectEditEvent(String editText) {

                    }

                    @Override
                    public void updatePassword(String oldPassword, String newPassword) {

                    }
                });
                break;
            case R.id.title_view_back:
                finish();
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
                case Constants.ActivityIntentIndex.RemarkActivityIndex:
                    Intent intent = new Intent();
                    intent.putExtra("code",Constants.FriendHandle.Modify);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;
            }
        }
    }

    void postDeleteFriend()
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("friend_user_id",mFriend.getXf()+"");
        APIClient.post(mContext, Constants.URL.REMOVE_FRIENDS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext, true, "")) {
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
                        FriendsManager.getInstance().deleteFriend(mFriend.getXf()+"");
                        new Delete().from(NotifyMessage.class).where("senderId = ? and userId = ?",mFriend.getXf()+"",mApplication.getUser().getXf()+"").execute();
                        RemoveTargetConversationList(mFriend.getXf()+"");
                        Intent intent = new Intent();
                        intent.putExtra("code",Constants.FriendHandle.Delete);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
            }
        });
    }

    void RemoveTargetConversationList(String target)
    {
        RongIM.getInstance().clearMessages(Conversation.ConversationType.SYSTEM, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        RongIM.getInstance().removeConversation(Conversation.ConversationType.SYSTEM, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
        RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE, target, new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {

            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


}
