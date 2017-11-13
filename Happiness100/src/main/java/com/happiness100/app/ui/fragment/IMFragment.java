package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/8/23.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.activeandroid.query.Select;
import com.android.volley.VolleyError;
import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.GroupImageManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.GroupImageKeyValue;
import com.happiness100.app.model.NotifyMessage;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.activity.CaptureActivity;
import com.happiness100.app.ui.activity.FriendDetailActivity;
import com.happiness100.app.ui.activity.FriendsActivity;
import com.happiness100.app.ui.activity.QRScanResultActivity;
import com.happiness100.app.ui.widget.ConversationListPopMenu;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 作者：justin on 2016/8/23 14:17
 */
public class IMFragment extends BaseFragment implements RongIM.ConversationListBehaviorListener {
    private static final String TAG = "IMFragment";
    @Bind(R.id.expand_warn)
    ImageView mExpandWarn;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                Log.e(TAG, "removeView()");
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.fragment_im_list, null);
            Log.e(TAG, "onCreateView()");
        }
        enterFragment();
        isReconnect();
        ButterKnife.bind(this, rootView);
        checkWarn();
        RongIM.setConversationListBehaviorListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume()");
        super.onResume();
        checkWarn();
        isReconnect();
    }

    public void checkWarn() {
        if (mExpandWarn != null) {
            mExpandWarn.setVisibility(View.GONE);
            List<NotifyMessage> tempList = new Select().from(NotifyMessage.class).where("UserId = ?", mApplication.getUser().getXf() + "").execute();
            for (int i = tempList.size() - 1; i >= 0; --i) {
                if (tempList.get(i).getStatus() == 0) {
                    mExpandWarn.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState()");
    }

    /**
     * 加载 会话列表 ConversationListFragment
     */
    private void enterFragment() {

        Log.d(TAG, "enterFragment");
        ConversationListFragment fragment = (ConversationListFragment) getChildFragmentManager().findFragmentById(R.id.conversationlist);
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();
        fragment.setUri(uri);
        fragment.setAdapter(new ConversationListAdapterEx(mContext));
    }


    public class ConversationListAdapterEx extends ConversationListAdapter {
        public ConversationListAdapterEx(Context context) {
            super(context);
        }

        @Override
        protected View newView(Context context, int position, ViewGroup group) {
            return super.newView(context, position, group);
        }

        @Override
        protected void bindView(View v, int position, UIConversation data) {
            if (data.getConversationType().equals(Conversation.ConversationType.SYSTEM))
            {
                data.setUIConversationTitle("系统消息");
            }
            if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
            {
                GroupImageKeyValue iv= GroupImageManager.getInstance().find(data.getConversationTargetId());
                if (iv != null && iv.getUrl() != null && !iv.getUrl().isEmpty())
                {
                    data.setIconUrl(Uri.parse(mApplication.getGroupImage(iv.getUrl())));
                }

            }

            super.bindView(v, position, data);
        }
    }

    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect() {

        Intent intent = getActivity().getIntent();
        String token = mApplication.getRongCloudToken();
        if(token == null || token.isEmpty())
        {
            mApplication.getToken();
            return;
        }

        Log.e(TAG,"isReconnect token = "+token);
        //push，通知或新消息过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {
                reconnect(token);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
                    reconnect(token);
                } else {
                    enterFragment();
                }
            }
        }
    }

    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {
        Log.e(TAG,"IMFragment reconnect");
        if (getActivity().getApplicationInfo().packageName.equals(App.getCurProcessName(getActivity().getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                    Log.e(TAG,"IMFragment onTokenIncorrect");
                }

                @Override
                public void onSuccess(String s) {
                    Log.e(TAG, "onSuccess:" + s);
                    enterFragment();
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    Log.e(TAG, "onError:" + errorCode);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.title_expand_item, R.id.title_address_Item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_expand_item:
                ConversationListPopMenu pop = new ConversationListPopMenu(getActivity());
                pop.showPopupWindow(view,0,23);
                break;
            case R.id.title_address_Item:
                Intent intent = new Intent();
                intent.setClass(getActivity(), FriendsActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
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
                        ToastUtils.shortToast(getActivity(), "不能添加自己到通讯录");
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

    void postSearchFriend(String searchStr) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("search_word", "xf"+searchStr);
        APIClient.postWithSessionId(getActivity(), Constants.URL.SEARCH_FRIENDS, params, new BaseVolleyListener(getActivity(),new LoadDialog(mContext,true,"")) {
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


    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }
    /**
     * 长按会话列表中的 item 时执行。
     *
     * @param context        上下文。
     * @param view           触发点击的 View。
     * @param uiConversation 长按时的会话条目。
     * @return 如果用户自己处理了长按会话后的逻辑处理，则返回 true， 否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }
    /**
     * 点击会话列表中的 item 时执行。
     *
     * @param context        上下文。
     * @param view           触发点击的 View。
     * @param uiConversation 会话条目。
     * @return 如果用户自己处理了点击会话后的逻辑处理，则返回 true， 否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }
}
