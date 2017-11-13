package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/5.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.GroupImageManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.BottomMenuDialog;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.SwitchView;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.BitmapUtils;
import com.justin.utils.GroupFaceUtil;
import com.justin.utils.MikyouAsyncTaskImageUtils;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.UserInfo;

/**
 * 作者：jiangsheng on 2016/9/5 15:26
 */

public class GroupDetailActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.memberGridView)
    GridView mMemberGridView;
    LayoutInflater mInfater;
    ArrayList<Friend> mGroupMemberList;
    ImageAdapter mAdapter;
    String mId;
    Boolean isCreator = false;
    String GroupName;
    List<String> mGroupMemberIdList;
    @Bind(R.id.NoDisturbing)
    SwitchView mNoDisturbing;
    @Bind(R.id.name_text)
            TextView mNameText;
    BottomMenuDialog mDialog_Delete;
    BottomMenuDialog mDialog_Clear;
    String mGroupBitmapBase64;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupdetail);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    void initData() {
        mId = getIntent().getStringExtra("TargetId");
        mGroupMemberList = new ArrayList<Friend>();
        mInfater = LayoutInflater.from(mContext);
        mTitleViewTitle.setText("聊天详情");
        mGroupBitmapBase64 = getIntent().getStringExtra("ext");
        updateMemberList();
    }

    void updateMemberList() {
        LoadDialog.show(mContext);
        RongIM.getInstance().getDiscussion(mId, new RongIMClient.ResultCallback<Discussion>() {
            @Override
            public void onSuccess(Discussion discussion) {

                if ((mApplication.getUser().getXf() + "").compareTo(discussion.getCreatorId()) == 0) {
                    isCreator = true;
                }
                mGroupMemberIdList = discussion.getMemberIdList();
                GroupName = discussion.getName();
                mNameText.setText(GroupName);
                getMemberList(mGroupMemberIdList);
                LoadDialog.dismiss(mContext);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LoadDialog.dismiss(mContext);
            }
        });
    }


    void getMemberList(List<String> friendIdList) {
        mGroupMemberList.clear();
        if (friendIdList.size() == 0) {
            return;
        }

        String IdStr = friendIdList.get(0);
        for (int i = 0; i < friendIdList.size(); ++i) {
            if (IdStr.compareTo(friendIdList.get(i)) != 0) {
                IdStr = IdStr + "," + friendIdList.get(i);
            }
            String friendId = friendIdList.get(i);
            Friend friend = FriendsManager.getInstance().findFriend(friendId);//从本地内存中找
            if (friend == null) {
                UserInfo info = RongContext.getInstance().getUserInfoFromCache(friendId);//融云缓存中查找

                if (info != null) {
                    friend = new Friend();
                    friend.setHeadImageUri(info.getPortraitUri().toString());
                    friend.setXf(Integer.parseInt(info.getUserId()));
                    friend.setNickname(info.getName());
                }
            }
            if (friend != null) {
                mGroupMemberList.add(friend);
            }
        }

        if (mGroupMemberList.size() == friendIdList.size()) {
            updateGridView();
            return;
        }
        mGroupMemberList.clear();

        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("ids", IdStr);
        APIClient.post(mContext, Constants.URL.ALL_HEAD_IMAGE, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);

                JSONObject responseJO = null;
                String ArrayStr = null;
                try {
                    responseJO = new JSONObject(json);
                    ArrayStr = responseJO.getString("data").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                String[][] array = gson.fromJson(ArrayStr, String[][].class);

                for (int i = 0; i < array.length; ++i) {
                    Friend friend = new Friend();
                    friend.setXf(Integer.parseInt(array[i][0]));
                    friend.setHeadImage(array[i][0] == null ? "" : array[i][1]);
                    friend.setHeadImageUri(mApplication.getHeadImage(friend.getXf()+"",friend.getHeadImage()));
                    friend.setNickname(array[i][2]);
                    mGroupMemberList.add(friend);
                }
                updateGridView();
            }
        });
    }


    void initView() {
        mAdapter = new ImageAdapter(mGroupMemberList);
        mTextBack.setText("返回");
        mMemberGridView.setAdapter(mAdapter);
        mMemberGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == mGroupMemberList.size()) {
                    Intent addMemberIntent = new Intent(mContext, GroupAddMemberActivity.class);
                    addMemberIntent.putExtra("id", mId);
                    addMemberIntent.putExtra("back", "聊天详情");
                    addMemberIntent.putExtra("name",GroupName);
                    addMemberIntent.putExtra("list", mGroupMemberList);
                    startActivityForResult(addMemberIntent, Constants.ActivityIntentIndex.GroupAddMemberActivityIndex);
                } else if (position == mGroupMemberList.size() + 1) {
                    Intent removeMemberIntent = new Intent(mContext, GroupRemoveMemberActivity.class);
                    removeMemberIntent.putExtra("id", mId);
                    removeMemberIntent.putExtra("back", "聊天详情");
                    removeMemberIntent.putExtra("list", mGroupMemberList);
                    removeMemberIntent.putExtra("name",GroupName);
                    startActivityForResult(removeMemberIntent, Constants.ActivityIntentIndex.GroupRemoveMemberActivityIndex);
                } else {
                    Intent friendDetailIntent = new Intent(mContext, FriendDetailActivity.class);
                    friendDetailIntent.putExtra("friend", mGroupMemberList.get(position));
                    startActivityForResult(friendDetailIntent, Constants.ActivityIntentIndex.FriendDetailActivityIndex);
                }
            }
        });

        RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.DISCUSSION, mId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                mNoDisturbing.setState(conversationNotificationStatus.getValue() == 0);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });


        mNoDisturbing.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                setToggleStatus(Conversation.ConversationNotificationStatus.DO_NOT_DISTURB);
            }

            @Override
            public void toggleToOff() {
                setToggleStatus(Conversation.ConversationNotificationStatus.NOTIFY);
            }
        });

        updateView();
    }

    void setToggleStatus(final Conversation.ConversationNotificationStatus status)
    {
        RongIM.getInstance().setConversationNotificationStatus( Conversation.ConversationType.DISCUSSION,mId,status, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
            @Override
            public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {
                ToastUtils.shortToast(mContext,"设置成功");
                if (status == Conversation.ConversationNotificationStatus.NOTIFY)
                {
                    mNoDisturbing.setState(false);
                }
                else
                {
                    mNoDisturbing.setState(true);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.shortToast(mContext,"设置失败");
                if (status == Conversation.ConversationNotificationStatus.NOTIFY)
                {
                    mNoDisturbing.setState(true);
                }
                else
                {
                    mNoDisturbing.setState(false);
                }
            }
        });
    }

    void updateView() {
        updateGridView();
    }

    void updateGridView() {
        mAdapter.updateList(mGroupMemberList);
        setListViewHeightBasedOnChildren(mMemberGridView);
    }

    /**
     * 当GridView外层有ScrollView时，需要动态设置GridView高度
     *
     * @param gridview
     */
    protected void setListViewHeightBasedOnChildren(GridView gridview) {
        if (gridview == null) return;
        ListAdapter listAdapter = gridview.getAdapter();
        if (listAdapter == null) return;

        int totalHeight;
        //向上取整
        int count = (int) Math.ceil(listAdapter.getCount() / 4.0);
        //获取一个子view
        View itemView = listAdapter.getView(0, null, gridview);
        //测量View的大小
       itemView.measure(0, 0);
        totalHeight = itemView.getMeasuredHeight();
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        //设置GridView的布局高度
        params.height = totalHeight * count;
        gridview.setLayoutParams(params);
    }


    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<Friend> mList;

        public ImageAdapter(List<Friend> list) {
            mList = list;
        }

        public void updateList(List<Friend> list) {
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mList.size() > 0) {
                return isCreator ? mList.size() + 2 : mList.size() + 1;
            }
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position > mList.size()) {
                return null;
            }
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            //if (convertView == null) {
                convertView = LayoutInflater.from(GroupDetailActivity.this).inflate(R.layout.member_info, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            //} else {
            //   viewHolder = (ViewHolder) convertView.getTag();
            //}
            if (position == mList.size()) {
                viewHolder.mText.setVisibility(View.GONE);
                viewHolder.mView.setImageResource(R.drawable.icon_add5);
            } else if (position == mList.size() + 1) {
                viewHolder.mText.setVisibility(View.GONE);
                viewHolder.mView.setImageResource(R.drawable.icon_delete2);
            } else {
                if (mList.size() == 0)
                {
                    return convertView;
                }

                Friend friend = mList.get(position);
                String headimage= friend.getHeadImage();
                if (headimage == null ||headimage.isEmpty())
                {
                    headimage = friend.getHeadImageUri();
                }
                else
                {
                    headimage = mApplication.getHeadImage(friend.getXf()+"",headimage);
                }
                UILUtils.displayImage(headimage, viewHolder.mView);
                viewHolder.mText.setVisibility(View.VISIBLE);
                viewHolder.mText.setText(friend.getNickname());
            }
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.view)
            ImageView mView;
            @Bind(R.id.text)
            TextView mText;
            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private  String BitmapBase64 = "";
    private List<Bitmap>mGroupMemberBitMapList = new ArrayList<>();
    void RemoveGroup(final String groupId) {
        mGroupMemberBitMapList.clear();
        if(mGroupMemberList.size() < 5 && mGroupMemberList.size() > 1)
        {

            Map<String,Friend> tempMemberMap =new HashMap<String,Friend>();
            for(int i = 0 ; i < mGroupMemberList.size();++i)
            {
                Friend fd = mGroupMemberList.get(i);
                if (mApplication.getUser().getXf() != fd.getXf())
                {
                    tempMemberMap.put(fd.getXf()+"",fd);
                }
            }

            //得到图片
            List<String> uriList = new ArrayList<String>();
            for (String key : tempMemberMap.keySet()) {
                Friend member = tempMemberMap.get(key);
                if (member != null) {
                    if (member.getHeadImage() == null || (member.getHeadImage() != null && member.getHeadImage().isEmpty())) //有图片就向服务器请求图片，没图片就自己搞
                    {
                        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stub);
                        mGroupMemberBitMapList.add(avatar);
                    } else {
                        uriList.add(mApplication.getHeadImage(member.getXf() + "", member.getHeadImage()));
                    }
                }
            }
            //拿图片并平凑
            MikyouAsyncTaskImageUtils mikyou = new MikyouAsyncTaskImageUtils(mContext);
            mikyou.setOnAsyncTaskImageListener(new MikyouAsyncTaskImageUtils.OnAsyncTaskImageListener() {
                @Override
                public void asyncTaskImageListener(List<Bitmap> bitmaps) {
                    for (int i = 0; i < bitmaps.size(); ++i) {
                        mGroupMemberBitMapList.add(bitmaps.get(i));
                    }
                    Bitmap face = GroupFaceUtil.createGroupFace(GroupFaceUtil.FACETYPE_QQ, mContext, mGroupMemberBitMapList.toArray(new Bitmap[mGroupMemberBitMapList.size()]));
                    mGroupBitmapBase64 = BitmapUtils.bitmapToBase64(BitmapUtils.getBitmapFromBitmap(face,60,60));
                    DeleteAndOutGroup(groupId);
                }
            });
            mikyou.execute(uriList);
        }
        else
        {
            BitmapBase64 = "";
            DeleteAndOutGroup(groupId);
        }
    }

    void DeleteAndOutGroup(final String groupId)
    {

        RongIM.getInstance().quitDiscussion(groupId, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                RongIM.getInstance().removeConversation(Conversation.ConversationType.DISCUSSION, groupId, new RongIMClient.ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        Map<String, String> params = new LinkedHashMap<>();
                        params.put("sessionid", mApplication.getUser().getSessionid());
                        params.put("discus_id", groupId);
                        params.put("opt_type", "2");
                        params.put("ext",BitmapBase64);
                        APIClient.post(mContext, Constants.URL.SYN_GROUP, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                                        //更新图片
                                        if(json != null && !json.isEmpty())
                                        {
                                            GroupImageManager.getInstance().update(groupId,json);
                                        }
                                        LoadDialog.dismiss(mContext);
                                        Intent intent1 = new Intent();
                                        intent1.putExtra("code", Constants.FriendHandle.Delete);
                                        setResult(RESULT_OK, intent1);
                                        finish();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @OnClick({R.id.title_view_back, R.id.NameItem, R.id.clearContentButton, R.id.outAndDeleteButton,R.id.conversationItem_SetCurrentTalkBackgroud_Layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                Intent intent1 = new Intent();
                intent1.putExtra("code", Constants.FriendHandle.Modify);
                intent1.putExtra("name", GroupName);
                setResult(RESULT_OK, intent1);
                finish();
                break;
            case R.id.NameItem:
                Intent GroupModifyNameIntent = new Intent(mContext, GroupModifyNameActivity.class);
                GroupModifyNameIntent.putExtra("id", mId);
                GroupModifyNameIntent.putExtra("back", "聊天信息");
                GroupModifyNameIntent.putExtra("name", GroupName);
                startActivityForResult(GroupModifyNameIntent, Constants.ActivityIntentIndex.GroupModifyNameActivityIndex);
                break;
            /*
            case R.id.QRcodeItem:
                Intent GroupQRIntent = new Intent(mContext, GroupQRCodeActivity.class);
                GroupQRIntent.putExtra("id", mId);
                GroupQRIntent.putExtra("back", "聊天信息");
                GroupQRIntent.putExtra("name", GroupName);
                GroupQRIntent.putExtra("ext",mGroupBitmapBase64);
                startActivityForResult(GroupQRIntent, Constants.ActivityIntentIndex.GroupQRCodeActivityIndex);
                break;
            case R.id.MyGroupNickNameItem:

                break;
                */
            case R.id.clearContentButton:
                RongIM.getInstance().clearMessages(Conversation.ConversationType.DISCUSSION, mId, new RongIMClient.ResultCallback<Boolean>() {
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
            case R.id.outAndDeleteButton:
                openDeleteDialog();
                break;
            case R.id.conversationItem_SetCurrentTalkBackgroud_Layout:
                Intent ModifybgIntent=  new Intent(mContext,ConversationModifyBackGroudActivity.class);
                ModifybgIntent.putExtra("back","聊天详情");
                ModifybgIntent.putExtra("id",mId);
                startActivityForResult(ModifybgIntent,Constants.ActivityIntentIndex.ConversationModifyBackGroudActivityIndex);
                break;
        }
    }

    void openDeleteDialog() {
        if (mDialog_Delete != null && mDialog_Delete.isShowing()) {
            mDialog_Delete.dismiss();
        }
        if (mDialog_Delete == null) {
            mDialog_Delete = new BottomMenuDialog(mContext, 2,"","退出并删除", "取消");
            mDialog_Delete.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog_Delete.dismiss();
                }
            });

            mDialog_Delete.setMiddleListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RemoveGroup(mId);
                }
            });
        }
        mDialog_Delete.show();
    }
    void openClearDialog() {
        if (mDialog_Clear != null && mDialog_Clear.isShowing()) {
            mDialog_Clear.dismiss();
        }
        if (mDialog_Clear == null) {
            mDialog_Clear = new BottomMenuDialog(mContext, 2,"","清空聊天记录", "取消");
            mDialog_Clear.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog_Clear.dismiss();
                }
            });

            mDialog_Clear.setMiddleListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadDialog.show(mContext);
                    RongIM.getInstance().deleteMessages(Conversation.ConversationType.DISCUSSION, mId, new RongIMClient.ResultCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            ToastUtils.shortToast(mContext, "清除成功");
                            LoadDialog.dismiss(mContext);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            LoadDialog.dismiss(mContext);
                        }
                    });
                }
            });
        }
        mDialog_Clear.show();
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
                case Constants.ActivityIntentIndex.GroupModifyNameActivityIndex:
                    GroupName = data.getStringExtra("name");
                    mNameText.setText(GroupName);
                    break;
                case Constants.ActivityIntentIndex.GroupRemoveMemberActivityIndex:
                    updateMemberList();
                    break;
                case Constants.ActivityIntentIndex.GroupAddMemberActivityIndex:
                    updateMemberList();
                    break;
                case Constants.ActivityIntentIndex.GroupQRCodeActivityIndex:

                    break;
                case Constants.ActivityIntentIndex.ConversationModifyBackGroudActivityIndex:
                    code = data.getIntExtra("code",Constants.FriendHandle.None);
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
