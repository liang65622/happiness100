package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/29.
 */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.manager.GroupImageManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.ClearWriteEditText;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.pinyin.CharacterParser;
import com.happiness100.app.ui.widget.pinyin.PinyinComparator;
import com.happiness100.app.ui.widget.pinyin.SideBar;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.happiness100.app.utils.NToast;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.BitmapUtils;
import com.justin.utils.GroupFaceUtil;
import com.justin.utils.MikyouAsyncTaskImageUtils;
import com.justin.utils.ToastUtils;
import com.justin.utils.UILUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * 作者：jiangsheng on 2016/8/29 16:50
 */
public class SponsorGroupChatActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    //带字母的集合
    private List<Friend> SourceDateList = new ArrayList<>();
    private List<Friend> CurrentSourceDateList;
    //是否显示最上面的两个项（面对面建群、加入一个群）
    private boolean ShowTitle;
    private List<Bitmap> mBitmapList;
    private View mView;
    /**
     * 自动搜索的 EditText
     */
    private ClearWriteEditText mAboutSerrch;
    /**
     * 好友列表的 ListView
     */
    private ListView mListView;
    /**
     * 好友列表的 adapter
     */
    private GroupChatAdapter adapter;
    /**
     * 右侧好友指示 Bar
     */
    private SideBar mSidBar;
    /**
     * 中部展示的字母提示
     */
    public TextView dialog;

    private TextView show_no_friends;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    /**
     * 存储选择的用户的id
     */
    private Map<String,Friend> mFriendsMap = new HashMap<String,Friend>();

    private String mGroupBitmapBase64;
    private List<String> mGroupMemberIdList;
    private Friend mDefaultFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //实例化汉字转拼音类
        mTitleViewTitle.setText("发起群聊");
        mTextBack.setText("返回");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText("保存");

        mGroupMemberIdList = new ArrayList<String>();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();
        mAboutSerrch = (ClearWriteEditText) findViewById(R.id.filter_edit);
        mListView = (ListView) findViewById(R.id.friendlistview);
        mSidBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        show_no_friends = (TextView) findViewById(R.id.show_no_friends);
        mSidBar.setTextView(dialog);
        mDefaultFriend = getIntent().getParcelableExtra("default");

        //设置右侧触摸监听
        mSidBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(ShowTitle?position+2:position);
                }
            }
        });

        //-------------- 获取好友列表数据 ---------------
        //TODO:获取好友列表
        Map<String,Friend> userMap = FriendsManager.getInstance().findFriends();
        for (Map.Entry<String, Friend> entry : userMap.entrySet()) {
            SourceDateList.add(entry.getValue());
        }
        filledData(SourceDateList); //过滤数据对象为友字母字段
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        mBitmapList = new ArrayList<Bitmap>();
        ShowTitle = true;
        CurrentSourceDateList =SourceDateList;
        adapter = new GroupChatAdapter(SourceDateList);
        mListView.setAdapter(adapter);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);
        //根据输入框输入值的改变来过滤搜索  顶部实时搜索
        mAboutSerrch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mFriendsMap.clear();
        checkCreateGroup();
    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private void filledData(List<Friend> lsit) {
        List<Friend> mFriendList = new ArrayList<Friend>();

        for (int i = 0; i < lsit.size(); i++) {
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(lsit.get(i).getNickname());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                lsit.get(i).setLetters(sortString.toUpperCase());
            } else {
                lsit.get(i).setLetters("#");
            }
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<Friend> filterDateList = new ArrayList<Friend>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
            ShowTitle = true;
        } else {
            ShowTitle = false;
            filterDateList.clear();
            for (Friend friendModel : SourceDateList) {
                String name = friendModel.getNickname();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(friendModel);
                }
            }
        }

        // 根据a-z进行排序
        CurrentSourceDateList =filterDateList;
        Collections.sort(CurrentSourceDateList, pinyinComparator);
        adapter.updateListView(CurrentSourceDateList);
    }

    public TextView getDialog() {
        return dialog;
    }

    public void setDialog(TextView dialog) {
        this.dialog = dialog;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(ShowTitle)
        {
            if(position == 0)
            {
                Intent myGroupIntent = new Intent(mContext,MyGroupActivity.class);
                startActivity(myGroupIntent);
            }
            else if (position == 1)
            {

            }
            else
            {

                ImageView checkImage = (ImageView)view.findViewById(R.id.SelectItem_SelectImage);
                Friend friend= CurrentSourceDateList.get(position-2);
                String friendxfId = friend.getXf()+"";

                if (mDefaultFriend != null && mDefaultFriend.getXf() == friend.getXf())
                {
                    return;
                }


                if (mFriendsMap.containsKey(friendxfId))
                {
                    checkImage.setImageResource(R.drawable.icon_select_normal);
                    mFriendsMap.remove(friendxfId);
                }
                else
                {
                    checkImage.setImageResource(R.drawable.icon_select_selected);
                    mFriendsMap.put(friendxfId,friend);
                }
                checkCreateGroup();
            }
        }
        else
        {
            ImageView checkImage = (ImageView)view.findViewById(R.id.SelectItem_SelectImage);
            Friend friend= CurrentSourceDateList.get(position);
            String friendxfId = friend.getXf()+"";
            if (mDefaultFriend != null && mDefaultFriend.getXf() == friend.getXf())
            {
                return;
            }

            if (mFriendsMap.containsKey(friendxfId))
            {
                checkImage.setImageResource(R.drawable.icon_select_normal);
                mFriendsMap.remove(friendxfId);
            }
            else
            {
                checkImage.setImageResource(R.drawable.icon_select_selected);
                mFriendsMap.put(friendxfId,friend);
            }
            checkCreateGroup();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    void checkCreateGroup()
    {
        if (mFriendsMap.size() > 0)
        {
            mTitleViewRight.setEnabled(true);
            mTextRight.setText("确定("+mFriendsMap.size()+")");
            mTextRight.setTextColor(getResources().getColor(R.color.white));
        }
        else
        {
            mTitleViewRight.setEnabled(false);
            mTextRight.setText("确定");
            mTextRight.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                if (mDefaultFriend == null && mFriendsMap.size() <= 1)
                {
                    for (Map.Entry<String, Friend> entry : mFriendsMap.entrySet()) {
                        Friend friend = entry.getValue();
                        startPrivateChat(friend.getXf()+"",friend.getNickname());
                        finish();
                    }
                }
                else
                {
                    CreateGroup();
                }
                break;
        }
    }


    void CreateGroup()
    {
        mBitmapList.clear();
        mGroupMemberIdList.clear();
        mGroupBitmapBase64 = "";
        String GroupName = mApplication.getUser().getNickname();
        if (RongIM.getInstance() != null && mFriendsMap.size() > 0) {
            int count = 0;
            ArrayList<String> uriList = new ArrayList<String>();
            uriList.add(mApplication.getHeadImage(mApplication.getUser().getXf()+"",mApplication.getUser().getHeadImage()));
            for (String key : mFriendsMap.keySet()) {
                Friend friend = FriendsManager.getInstance().findFriend(key);
                if (friend != null && friend.getXf() != mApplication.getUser().getXf()) {
                    mGroupMemberIdList.add(key);//申请的好友id列表
                    GroupName = GroupName+"、"+friend.getNickname();
                    if (count < 6)
                    {
                        if (friend.getHeadImage() == null || (friend.getHeadImage() != null && friend.getHeadImage().isEmpty())) //有图片就向服务器请求图片，没图片就自己搞
                        {
                            Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.icon_man);
                            mBitmapList.add(avatar);
                        }
                        else
                        {
                            uriList.add(mApplication.getHeadImage(friend.getXf()+"",friend.getHeadImage()));
                        }
                        count++;
                    }
                }
            }
            //默认拉进群聊的人
            if (mDefaultFriend != null&&mBitmapList.size()+uriList.size() < 6) {
                mGroupMemberIdList.add(mDefaultFriend.getXf()+"");
                if (GroupName.isEmpty())
                {
                    GroupName = mDefaultFriend.getNickname();
                }
                else {
                    GroupName = GroupName+"、"+mDefaultFriend.getNickname();
                }
                if(mBitmapList.size()+uriList.size() < 6)
                {
                    if (mDefaultFriend.getHeadImage() == null || (mDefaultFriend.getHeadImage() != null && mDefaultFriend.getHeadImage().isEmpty())) //有图片就向服务器请求图片，没图片就自己搞
                    {
                        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stub);
                        mBitmapList.add(avatar);
                    }
                    else
                    {
                        uriList.add(mApplication.getHeadImage(mDefaultFriend.getXf()+"",mDefaultFriend.getHeadImage()));
                    }
                }
            }
            final String GroupNickName = GroupName;
            MikyouAsyncTaskImageUtils mikyou = new MikyouAsyncTaskImageUtils(mContext);
            mikyou.execute(uriList);
            mikyou.setOnAsyncTaskImageListener(new MikyouAsyncTaskImageUtils.OnAsyncTaskImageListener() {
                @Override
                public void asyncTaskImageListener(List<Bitmap> bitmaps) {
                    for (int i = 0; i < bitmaps.size(); ++i)
                    {
                        mBitmapList.add(bitmaps.get(i));
                    }
                    Bitmap face = GroupFaceUtil.createGroupFace(GroupFaceUtil.FACETYPE_QQ, mContext, mBitmapList.toArray(new Bitmap[mBitmapList.size()]));
                    mGroupBitmapBase64 = BitmapUtils.bitmapToBase64(BitmapUtils.getBitmapFromBitmap(face,60,60));

                    RongIM.getInstance().createDiscussion(GroupNickName, mGroupMemberIdList, new RongIMClient.CreateDiscussionCallback() {
                        @Override
                        public void onSuccess(String s) {
                            String Ids = "";
                            for (int i = 0;i < mGroupMemberIdList.size();++i)
                            {
                                if (Ids.isEmpty())
                                {
                                    Ids = mGroupMemberIdList.get(i);
                                }
                                else
                                {
                                    Ids = Ids+","+mGroupMemberIdList.get(i);
                                }
                            }
                            Ids = Ids+","+mApplication.getUser().getXf();
                            postServerCreateGroup(s,GroupNickName,mGroupBitmapBase64,Ids);
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            ToastUtils.shortToast(mContext,"创建失败");
                        }
                    });
                }
            });
        } else {
            NToast.shortToast(mContext,"至少选择一位好友");
        }
    }

    void postServerCreateGroup(final String groupId,final String name,String ext,String ids)
    {
        Map<String,String> params = new LinkedHashMap<>();
        params.put("sessionid",mApplication.getUser().getSessionid());
        params.put("discus_id",groupId);
        params.put("opt_type","1");
        params.put("discus_name",name);
        params.put("ids",ids);
        params.put("ext",ext);
        APIClient.post(mContext, Constants.URL.SYN_GROUP, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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

                        RongIM.getInstance().startDiscussionChat(SponsorGroupChatActivity.this, groupId,name);
                        finish();
                    }
                });
            }
        });
    }

    public class GroupChatAdapter extends BaseAdapter implements SectionIndexer {

        List<Friend> list;

        public GroupChatAdapter(List<Friend> list)
        {
            this.list = list;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<Friend> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return ShowTitle ? list.size() + 2 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return ShowTitle ? list.get(position + 2) : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ShowTitle ? position + 2 : position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(SponsorGroupChatActivity.this).inflate(R.layout.activity_groupchat_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mSelectItemView.setVisibility(View.GONE);
            viewHolder.mTextItemView.setVisibility(View.GONE);
            viewHolder.mCatalog.setVisibility(View.GONE);
            if (ShowTitle) {
                if (position == 0) {
                    viewHolder.mTextItemView.setVisibility(View.VISIBLE);
                    viewHolder.mTextItemText.setText("选择一个群");
                } else if (position == 1) {
                    viewHolder.mTextItemView.setVisibility(View.VISIBLE);
                    viewHolder.mTextItemText.setText("面对面建群");
                    viewHolder.mTextItemText.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    position = position -2;
                    final Friend content = list.get(position);
                    viewHolder.mSelectItemView.setVisibility(View.VISIBLE);
                    //根据position获取分类的首字母的Char ascii值
                    int section = getSectionForPosition(position);
                    //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                    if (position == getPositionForSection(section)) {
                        viewHolder.mCatalog.setVisibility(View.VISIBLE);
                        viewHolder.mCatalog.setText(content.getLetters());
                    } else {
                        viewHolder.mCatalog.setVisibility(View.GONE);
                    }
                    viewHolder.mSelectItemNickName.setText(content.getNickname());
                    String url = mApplication.getHeadImage(content.getXf()+"",content.getHeadImage());
                    UILUtils.displayImage(url, viewHolder.mSelectItemHeadView);


                    String friendid = content.getXf()+"";
                    if (mFriendsMap.containsKey(friendid))
                    {
                        viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_selected);
                    }
                    else
                    {
                        viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_normal);
                    }

                    if (mDefaultFriend != null &&mDefaultFriend.getXf() == content.getXf())
                    {
                        viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_enablefalse);
                    }

                }
            } else {
                final Friend content = list.get(position);
                viewHolder.mSelectItemView.setVisibility(View.VISIBLE);
                //根据position获取分类的首字母的Char ascii值
                int section = getSectionForPosition(position);
                //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                if (position == getPositionForSection(section)) {
                    viewHolder.mCatalog.setVisibility(View.VISIBLE);
                    viewHolder.mCatalog.setText(content.getLetters());
                } else {
                    viewHolder.mCatalog.setVisibility(View.GONE);
                }
                viewHolder.mSelectItemNickName.setText(this.list.get(position).getNickname());
                String url = mApplication.getHeadImage(content.getXf()+"",content.getHeadImage());
                UILUtils.displayImage(url, viewHolder.mSelectItemHeadView);
                String friendid = content.getXf()+"";
                if (mFriendsMap.containsKey(friendid))
                {
                    viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_selected);
                }
                else
                {
                    viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_normal);
                }
                if (mDefaultFriend != null &&mDefaultFriend.getXf() == content.getXf())
                {
                    viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_enablefalse);
                }
            }

            return convertView;
        }

        @Override
        public Object[] getSections() {
            return new Object[0];
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        @Override
        public int getPositionForSection(int sectionIndex) {
            for (int i = 0; i < (ShowTitle?getCount()-2:getCount()); i++) {
                String sortStr = list.get(i).getLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == sectionIndex) {
                    return i;
                }
            }

            return -1;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        @Override
        public int getSectionForPosition(int position) {
            return list.get(position).getLetters().charAt(0);
        }

        class ViewHolder {
            @Bind(R.id.catalog)
            TextView mCatalog;
            @Bind(R.id.textItem_text)
            TextView mTextItemText;
            @Bind(R.id.textItem_View)
            LinearLayout mTextItemView;
            @Bind(R.id.SelectItem_SelectImage)
            ImageView mSelectItemSelectImage;
            @Bind(R.id.SelectItem_HeadView)
            ImageView mSelectItemHeadView;
            @Bind(R.id.SelectItem_NickName)
            TextView mSelectItemNickName;
            @Bind(R.id.SelectItem_View)
            RelativeLayout mSelectItemView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode)
        {
            switch (requestCode)
            {
                case Constants.ActivityIntentIndex.ConversationActivityIndex:
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
        }
    }
}

