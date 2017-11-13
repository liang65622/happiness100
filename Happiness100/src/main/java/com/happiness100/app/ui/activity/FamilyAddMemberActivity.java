package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/25.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.model.Apply;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.MatchContactsUser;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.pinyin.CharacterParser;
import com.happiness100.app.ui.widget.pinyin.PinyinMatchContactsUserComparator;
import com.happiness100.app.ui.widget.pinyin.SideBar;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.UILUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;

/**
 * 作者：justin on 2016/8/25 14:40
 */
public class FamilyAddMemberActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, BaseViewInterface {

    private static final String TAG = "";
    private static final int REQUEST_INVITATION = 2;
    private static final int REQUEST_CODE = 1;
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.rg_contacts_type)
    RadioGroup rgContactsType;
    //不带字母的数据集合
    private List<MatchContactsUser> mSourceDateList = new ArrayList<>();
    //带字母的集合
    private List<MatchContactsUser> SourceDateList = new ArrayList<>();

    private List<MatchContactsUser> mFriendsList = new ArrayList<>();

    private List<MatchContactsUser> mMatchUsers;
    //private FriendDao friendDao;
    private View mView;
    /**
     * 自动搜索的 EditText
     */
    /**
     * 好友列表的 ListView
     */
    private ListView mListView;
    /**
     * 好友列表的 adapter
     */
    private MatchContactsUserAdapter adapter;
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
    private PinyinMatchContactsUserComparator pinyinComparator;
    private HashMap<String, String> mContactsMap = new HashMap<>();
    private String mClanId;
    private int mCurrentControlPosition;
    private LoadDialog mLoadDialog;
    private ArrayList<String> mMemberIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);
        ButterKnife.bind(this);
        mContext = this;
        mClanId = SharedPreferencesContext.getInstance().getSharedPreferences().getString("clanId", null);
        if (mClanId == null) {
            mClanId = mApplication.getUser().getDefaultClanId();
        }

        Intent data = new Intent();
        if (data != null) {
            mMemberIds = mApplication.getMembersIds();
        }
        if (mMemberIds == null) {
            mMemberIds = new ArrayList<>();
        }
        initView();
        initData();
    }

    @Override
    public void initView() {
        //实例化汉字转拼音类
        mTitleViewTitle.setText("添加成员");
        mTextBack.setText("返回");

        rgContactsType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_phone_contacts:
                        adapter.setData(SourceDateList);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.rb_friend:
                        adapter.setData(mFriendsList);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinMatchContactsUserComparator.getInstance();
        mListView = (ListView) findViewById(R.id.friendlistview);
        mSidBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        show_no_friends = (TextView) findViewById(R.id.show_no_friends);
        mSidBar.setTextView(dialog);
        //设置右侧触摸监听
        mSidBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });

    }

    class ReadContacts extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String[] params) {
            String contactsStr = readAllContacts();
            return contactsStr;
        }

        @Override
        protected void onPostExecute(String contactsStr) {
            super.onPostExecute(contactsStr);
            initContacts(contactsStr);
        }
    }

    private List<MatchContactsUser> initFriends() {
        List<MatchContactsUser> list = new ArrayList<>();
        List<Friend> friends = FriendsManager.getInstance().findFriendList();
        for (Friend friend : friends) {
            if (friend.getXf() != 0) {
                MatchContactsUser matchUser = new MatchContactsUser();
                matchUser.setName(friend.getNickname());
                matchUser.setUserId(friend.getXf());
                matchUser.setHeadImage(friend.getHeadImage());
                matchUser.setMobile(friend.getMobile());
                list.add(matchUser);
            }
        }
        return list;
    }

    void updateView() {

        //-------------- 获取好友列表数据 ---------------
        //通讯录匹配
        SourceDateList = filledData(mSourceDateList); //过滤数据对象为友字母字段
        for (int i = 0; i < mSourceDateList.size(); i++) {
            SourceDateList.get(i).setUserId(mSourceDateList.get(i).getUserId());
            SourceDateList.get(i).setName(mSourceDateList.get(i).getName());
            SourceDateList.get(i).setHeadImage(mSourceDateList.get(i).getHeadImage());
        }

        for (MatchContactsUser user : SourceDateList) {
            if (user != null && mMemberIds.contains(user.getUserId() + "")) {
                user.setIsInFamily(1);
            }
        }


        //初始化是否加入家族


        if (SourceDateList.size() > 0) {
            show_no_friends.setVisibility(View.GONE);
        }

        mSourceDateList = null;
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);

        adapter = new MatchContactsUserAdapter(mContext, SourceDateList);
        mListView.setAdapter(adapter);
//        mListView.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mLoadDialog = new LoadDialog(mContext, false, "");
        mLoadDialog.show();
        new ReadContacts().execute();

        //好友
        List<MatchContactsUser> friends = initFriends();
        mFriendsList = filledData(friends);
        for (int i = 0; i < mFriendsList.size(); i++) {
            mFriendsList.get(i).setUserId(friends.get(i).getUserId());
            mFriendsList.get(i).setName(friends.get(i).getName());
            mFriendsList.get(i).setHeadImage(friends.get(i).getHeadImage());
        }


        adapter = new MatchContactsUserAdapter(mContext, SourceDateList);
        mListView.setAdapter(adapter);
    }

    private void initContacts(String contactsStr) {
        mLoadDialog.dismiss();

        Map<String, String> params = new HashMap<>();
        /*params.put("phones", readAllContacts());*/
        params.put("phones", contactsStr);
        APIClient.postWithSessionId(mContext, Constants.URL.TXL_MATCH_ACCOUNT, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        //
                        Type type = new TypeToken<ArrayList<MatchContactsUser>>() {
                        }.getType();
                        mSourceDateList = GsonUtils.parseJSONArray(json, type);
                        if (mSourceDateList == null) {
                            return;
                        }
                        for (MatchContactsUser user : mSourceDateList) {
                            user.setName(mContactsMap.get(user.getMobile()));
                        }
                        updateView();
                        adapter.notifyDataSetChanged();
                        mLoadDialog.dismiss();
                    }
                });
            }
        });
    }

    private void invitation(final int userId) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId + "");
        params.put("clan_id", mClanId);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_INVITE_JOIN_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        if (json.contains("已经")) {
                            DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "提示", json, new DialogWithYesOrNoUtils.DialogCallBack() {
                                @Override
                                public void exectEvent() {
                                    Intent intent = new Intent(mContext, CompleteMemberInfoActivity.class);
                                    Apply apply = new Apply();
                                    apply.setUserId(userId);
                                    intent.putExtra("apply", apply);
                                    startActivityForResult(intent, REQUEST_CODE);
                                }

                                @Override
                                public void exectEditEvent(String editText) {
                                }

                                @Override
                                public void updatePassword(String oldPassword, String newPassword) {
                                }
                            });
                        } else {

                            Intent intent = new Intent(mContext, AddMemberCommitActiivty.class);
                            if (rgContactsType.getCheckedRadioButtonId() == R.id.rb_phone_contacts) {
                                intent.putExtra("user", SourceDateList.get(mCurrentControlPosition));
                            } else {
                                intent.putExtra("user", mFriendsList.get(mCurrentControlPosition));
                            }
                            startActivityForResult(intent, REQUEST_INVITATION);
//                        SourceDateList.get(mCurrentControlPosition).setIsWaitAuth(1);
//                        adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    /*
      * 读取联系人的信息
      */
    public String readAllContacts() {
        Cursor cursor = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }

        StringBuilder sb = new StringBuilder();

        while (cursor.moveToNext()) {
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            Log.e(TAG, contactId);
            Log.e(TAG, name);

            /*
             * 查找该联系人的phone信息
             */
            Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if (phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }

            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                phoneNumber = phoneNumber.replaceAll("-", "").replaceAll(" ", "");
                sb.append("86 ");
                sb.append(phoneNumber);
                mContactsMap.put("86 " + phoneNumber, name);
                sb.append(",");

                Log.e(TAG, phoneNumber);
            }
        }
        String result = sb.toString();
        if (result.length() > 11) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<MatchContactsUser> filledData(List<MatchContactsUser> lsit) {
        List<MatchContactsUser> mFriendList = new ArrayList<>();

        for (int i = 0; i < lsit.size(); i++) {
            MatchContactsUser friendModel = new MatchContactsUser();
            friendModel.setName(lsit.get(i).getName());
            friendModel.setNickname(lsit.get(i).getNickname());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(lsit.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                friendModel.setLetters(sortString.toUpperCase());
            } else {
                friendModel.setLetters("#");
            }

            mFriendList.add(friendModel);
        }
        return mFriendList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<MatchContactsUser> filterDateList = new ArrayList<>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (MatchContactsUser friendModel : SourceDateList) {
                String name = friendModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(friendModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
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
        if (RongIM.getInstance() != null) {
            //RongIM.getInstance().startPrivateChat(mContext, SourceDateList.get(position).getXf()+"", SourceDateList.get(position).getNickname());
            startPrivateChatForResult(SourceDateList.get(position).getUserId() + "", SourceDateList.get(position).getName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.ActivityIntentIndex.ConversationActivityIndex:
                    int code = data.getIntExtra("code", Constants.FriendHandle.None);
                    if (code == Constants.FriendHandle.Delete) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code", Constants.FriendHandle.Delete);
                        setResult(RESULT_OK, intent1);
                        finish();
                    }

                    break;
                case REQUEST_INVITATION:

                    if (rgContactsType.getCheckedRadioButtonId() == R.id.rb_phone_contacts) {
                        SourceDateList.get(mCurrentControlPosition).setIsWaitAuth(1);
                    } else {
                        mFriendsList.get(mCurrentControlPosition).setIsWaitAuth(1);
                    }
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    class MatchContactsUserAdapter extends BaseAdapter implements SectionIndexer {

        private Context context;

        private List<MatchContactsUser> list;

        public MatchContactsUserAdapter(Context context, List<MatchContactsUser> list) {
            this.context = context;
            this.list = list;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<MatchContactsUser> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            final MatchContactsUser mContent = list.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_contacts, null);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.friendname);
                viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
                viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.frienduri);
                viewHolder.mBtnAddMember = (Button) convertView.findViewById(R.id.btn_add_family_member);
                viewHolder.mWaitAuth = (TextView) convertView.findViewById(R.id.tv_wait_auth);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(mContent.getLetters());
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }
            final MatchContactsUser matchUser = list.get(position);
            viewHolder.tvTitle.setText(matchUser.getName());
            if (matchUser.getIsWaitAuth() == 1) {
                viewHolder.mBtnAddMember.setVisibility(View.GONE);
                viewHolder.mWaitAuth.setVisibility(View.VISIBLE);

            } else {
                viewHolder.mBtnAddMember.setVisibility(View.VISIBLE);
                viewHolder.mWaitAuth.setVisibility(View.GONE);
                viewHolder.mBtnAddMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentControlPosition = position;
                        invitation(matchUser.getUserId());
                    }
                });
            }

            if (matchUser.getIsInFamily() == 1) {
                viewHolder.mBtnAddMember.setVisibility(View.GONE);
                viewHolder.mWaitAuth.setVisibility(View.VISIBLE);
                viewHolder.mWaitAuth.setText("已是家族成员");
            } else {
                viewHolder.mBtnAddMember.setVisibility(View.VISIBLE);
                viewHolder.mWaitAuth.setVisibility(View.GONE);
                viewHolder.mBtnAddMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentControlPosition = position;
                        invitation(matchUser.getUserId());
                    }
                });
            }

            String url = this.list.get(position).getHeadImage();
            if (!TextUtils.isEmpty(url)) {
                UILUtils.displayImage(mApplication.getHeadImage(matchUser.getUserId() + "", url), viewHolder.mImageView);
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
            for (int i = 0; i < getCount(); i++) {
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

        public void setData(List<MatchContactsUser> data) {
            List<MatchContactsUser> result = new ArrayList<>();
            for (MatchContactsUser user : data) {
                if (user != null && mMemberIds.contains(user.getUserId() + "")) {
                    user.setIsInFamily(1);
                }
            }
            list = data;
        }

        class ViewHolder {
            /**
             * 首字母
             */
            TextView tvLetter;
            /**
             * 昵称
             */
            TextView tvTitle;
            /**
             * 头像
             */
            ImageView mImageView;


            TextView mWaitAuth;

            Button mBtnAddMember;
            /**
             * userid
             */
//        TextView tvUserId;
        }


        /**
         * 提取英文的首字母，非英文字母用#代替。unused
         *
         * @param str
         * @return
         */
//    private String getAlpha(String str) {
//        String sortStr = str.trim().substring(0, 1).toUpperCase();
//        // 正则表达式，判断首字母是否是英文字母
//        if (sortStr.matches("[A-Z]")) {
//            return sortStr;
//        } else {
//            return "#";
//        }
//    }
    }
}
