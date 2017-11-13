package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/24.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.manager.ConversationBackGroudImageManager;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.RemarkManager;
import com.happiness100.app.model.ConversationBackGroudImage;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.Remark;
import com.happiness100.app.utils.Constants;
import com.justin.utils.SharedPreferencesContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.TypingMessage.TypingStatus;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * 作者：jiangsheng on 2016/8/24 15:00
 */
public class ConversationActivity extends BaseActivity implements RongIM.ConversationBehaviorListener,RongIMClient.TypingStatusListener {

    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.text_right)
    TextView mTextRight;
    private String mTargetId;
    String mGroupBitmapBase64;
    String mTitle;
    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        ButterKnife.bind(this);
        initView();
        RongIM.setConversationBehaviorListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mConversationType == Conversation.ConversationType.PRIVATE)
        {
            Remark remark = RemarkManager.getInstance().findRemark(mTargetId);

            if (remark.getNoteName() == null||remark.getNoteName().isEmpty())
            {
                mTitleViewTitle.setText(mTitle);
            }
            else
            {
                mTitle = remark.getNoteName();
                mTitleViewTitle.setText(mTitle);
            }
        }
        SharedPreferences sharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
        ConversationBackGroudImage backgroud = ConversationBackGroudImageManager.getInstance().find(Constants.Function.BACKGROUD+mApplication.getUser().getXf()+"to"+mTargetId);
        if (backgroud == null)
        {
            ConversationBackGroudImage allImage = ConversationBackGroudImageManager.getInstance().find("All");
            if (allImage != null)
            {
                byte[] bitmapArray = Base64.decode(allImage.getImageData(), Base64.DEFAULT);
                Bitmap bg = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
                getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(),bg));
            }
            else
            {
                getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(),R.drawable.conversationbg)));
            }
        }
        else
        {
            byte[] bitmapArray = Base64.decode(backgroud.getImageData(), Base64.DEFAULT);
            Bitmap bg = BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
            getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(),bg));
        }
    }

    void initView() {
        Intent intent = getIntent();
        getIntentDate(intent);
        isReconnect(intent);
        mTitle = getIntent().getData().getQueryParameter("title"); //获取昵称

        Log.e("ConversationActivity","mConversationType = "+mConversationType+",mTargetId = "+mTargetId);
        mTextBack.setText("返回");
        mTitleViewTitle.setText(mTitle);
        mTextRight.setVisibility(View.GONE);

        switch (mConversationType) {
            case GROUP:
                mImageRight.setVisibility(View.VISIBLE);
                mTitleViewRight.setVisibility(View.VISIBLE);
                break;
            case PRIVATE:
                mImageRight.setImageResource(R.drawable.icon_private);
                mImageRight.setVisibility(View.VISIBLE);
                mTitleViewRight.setVisibility(View.VISIBLE);
                break;
            case DISCUSSION:
                mImageRight.setImageResource(R.drawable.icon_discussion);
                mImageRight.setVisibility(View.VISIBLE);
                mTitleViewRight.setVisibility(View.VISIBLE);
                mGroupBitmapBase64 = getIntent().getStringExtra("ext");
                if (mGroupBitmapBase64 == null)
                {
                    mGroupBitmapBase64 = "";
                }
                break;
            case SYSTEM:
                mTitleViewTitle.setText("系统消息");
                break;
        }
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {
        mTargetId = intent.getData().getQueryParameter("targetId");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        enterFragment(mConversationType, mTargetId);
    }

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {
        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();
        fragment.setUri(uri);
    }


    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect(Intent intent) {


        String token = mApplication.getRongCloudToken();
        if(token == null || token.isEmpty())
        {
            mApplication.getToken();
            return;
        }
        //push或通知过来
        //if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {
                reconnect(token);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

                    reconnect(token);
                } else {
                    enterFragment(mConversationType, mTargetId);
                }
            }
       //}
    }


    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                Intent MainIntent = new Intent(mContext, MainActivity.class);
                MainIntent.putExtra("openIdx",1);
                startActivity(MainIntent);
                finish();
                break;
            case R.id.title_view_right:
                //TODO:
                switch (mConversationType) {
                    case GROUP:

                        break;
                    case PRIVATE:
                        Intent ConversationDetailIntent = new Intent(mContext, ConversationDetailedActicity.class);
                        Friend friend = FriendsManager.getInstance().findFriend(mTargetId);
                        ConversationDetailIntent.putExtra("UserId", mTargetId);
                        startActivityForResult(ConversationDetailIntent, Constants.ActivityIntentIndex.ConversationDetailedActivtyIndex);
                        break;
                    case DISCUSSION:
                        Intent GroupDetailActivityIntent = new Intent(mContext, GroupDetailActivity.class);
                        GroupDetailActivityIntent.putExtra("TargetId", mTargetId);
                        GroupDetailActivityIntent.putExtra("ext",mGroupBitmapBase64);
                        startActivityForResult(GroupDetailActivityIntent, Constants.ActivityIntentIndex.GroupDetailActivityIndex);
                        break;
                    case SYSTEM:
                        break;
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int code = 0;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.ActivityIntentIndex.ConversationDetailedActivtyIndex:
                    code = data.getIntExtra("code", 0);
                    if (code == Constants.FriendHandle.Delete) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code", Constants.FriendHandle.Delete);
                        setResult(RESULT_OK, intent1);
                        finish();
                    }
                    break;
                case Constants.ActivityIntentIndex.GroupDetailActivityIndex:
                    code = data.getIntExtra("code", 0);
                    if (code == Constants.FriendHandle.Delete) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code", Constants.FriendHandle.Delete);
                        setResult(RESULT_OK, intent1);
                        finish();
                    } else if (code == Constants.FriendHandle.Modify) {
                        String name = data.getStringExtra("name");
                        if (name != null) {
                            mTitle = name;
                            mTitleViewTitle.setText(mTitle);
                        }

                    }
                    break;
            }
        }
    }

    /**
     * 当点击用户头像后执行。
     *
     * @param context           上下文。
     * @param conversationType  会话类型。
     * @param userInfo          被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        Intent ConversationDetailIntent = new Intent(mContext, FriendDetailActivity.class);
        ConversationDetailIntent.putExtra("xf", userInfo.getUserId());
        startActivityForResult(ConversationDetailIntent, Constants.ActivityIntentIndex.ConversationDetailedActivtyIndex);
        return false;
    }

    /**
     * 当长按用户头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param userInfo         被点击的用户的信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }
    /**
     * 当点击消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被点击的消息的实体信息。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        return false;
    }

    /**
     * 当长按消息时执行。
     *
     * @param context 上下文。
     * @param view    触发点击的 View。
     * @param message 被长按的消息的实体信息。
     * @return 如果用户自己处理了长按后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
    /**
     * 当点击链接消息时执行。
     *
     * @param context 上下文。
     * @param link    被点击的链接。
     * @return 如果用户自己处理了点击后的逻辑处理，则返回 true， 否则返回 false, false 走融云默认处理方式。
     */
    @Override
    public boolean onMessageLinkClick(Context context, String link) {
        return false;
    }

    @Override
    public void onTypingStatusChanged(Conversation.ConversationType type, String targetId, Collection<TypingStatus> typingStatusSet) {
        //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
        if (type.equals(mConversationType) && targetId.equals(mTargetId)) {            //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
            int count = typingStatusSet.size();            if (count > 0) {
                Iterator iterator = typingStatusSet.iterator();
                TypingStatus status = (TypingStatus) iterator.next();
                String objectName = status.getTypingContentType();

                MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);                //匹配对方正在输入的是文本消息还是语音消息
                if (objectName.equals(textTag.value())) {                    //显示“对方正在输入”
                    mTitleViewTitle.setText("对方正在输入");
                } else if (objectName.equals(voiceTag.value())) {                    //显示"对方正在讲话"
                    mTitleViewTitle.setText("对方正在讲话");
                }
            } else {                //当前会话没有用户正在输入，标题栏仍显示原来标题
                mTitleViewTitle.setText(mTitle);
            }
        }
    }
}
