package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/25.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.activeandroid.query.Select;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.NotifyMessage;
import com.happiness100.app.ui.widget.ClearWriteEditText;
import com.happiness100.app.ui.widget.pinyin.CharacterParser;
import com.happiness100.app.ui.widget.pinyin.PinyinComparator;
import com.happiness100.app.ui.widget.pinyin.SideBar;
import com.happiness100.app.utils.Constants;
import com.justin.utils.UILUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/25 15:14
 */
public class FriendsActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
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
    private boolean ShowTitle;
    //不带字母的数据集合
    private List<Friend> mSourceDateList = new ArrayList<>();
    //带字母的集合
    private List<Friend> SourceDateList= new ArrayList<>();
    private List<Friend> CurrentSourceDateList;
    public static FriendsActivity instance = null;
    //private FriendDao friendDao;

    public static FriendsActivity getInstance() {
        if (instance == null) {
            instance = new FriendsActivity();
        }
        return instance;
    }

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
    private MyFriendAdapter adapter;
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

    private BroadcastReceiver mBroadcastReceiver;
    NotifyMessage mLastNotifyMessage;
    int mUntreatedMessageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rp_friend_fragment);
        ButterKnife.bind(this);

        initData();
        initView();
    }


    void initData()
    {
        updateWarnInfo();
        //注册消息监听
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("新的消息");

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("新的消息")) {
                    updateWarnInfo();
                    if (mAboutSerrch.getText().toString().isEmpty()) {
                        adapter.updateListView(CurrentSourceDateList);
                    }
                }
            }
        };
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWarnInfo();
    }

    private void initView() {
        //实例化汉字转拼音类
        mTitleViewTitle.setText("通讯录");
        mTextBack.setText("返回");
        mTextRight.setVisibility(View.GONE);
        mTitleViewRight.setVisibility(View.VISIBLE);
        mImageRight.setVisibility(View.VISIBLE);
        mImageRight.setImageResource(R.drawable.icon_add);

        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();
        mAboutSerrch = (ClearWriteEditText) findViewById(R.id.filter_edit);
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
                mListView.setSelection(ShowTitle ? position + 2 : position);
            }
            }
        });
        //-------------- 获取好友列表数据 ---------------
        Map<String, Friend> userMap = FriendsManager.getInstance().findFriends();
        for (Map.Entry<String, Friend> entry : userMap.entrySet()) {
            SourceDateList.add(entry.getValue());
        }

        ShowTitle = true;
        filledData(SourceDateList); //过滤数据对象为友字母字段
        if (SourceDateList.size() > 0) {
            show_no_friends.setVisibility(View.GONE);
        }

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        CurrentSourceDateList = SourceDateList;
        adapter = new MyFriendAdapter(SourceDateList);
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
    }

    void updateWarnInfo() {
        mUntreatedMessageCount = 0;
        List<NotifyMessage> tempList = new Select().from(NotifyMessage.class).where("UserId = ?", mApplication.getUser().getXf() + "").execute();
        for (int i = 0; i < tempList.size(); ++i) {
            NotifyMessage msg = tempList.get(i);
            if (msg.getStatus() == 0) {
                mUntreatedMessageCount++;
                mLastNotifyMessage = msg;
            }
        }
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
        CurrentSourceDateList = filterDateList;
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

        if (ShowTitle) {
            if (position == 0) {
                Intent notifyIntent = new Intent(mContext, NotifyActivity.class);
                startActivity(notifyIntent);
                finish();
            } else if (position == 1) {
                Intent myGroupIntent = new Intent(mContext, MyGroupActivity.class);
                startActivity(myGroupIntent);
                finish();
            } else {
                Friend friend = CurrentSourceDateList.get(position - 2);
                Intent friendDetailIntent = new Intent(mContext, FriendDetailActivity.class);
                friendDetailIntent.putExtra("friend", friend);
                startActivityForResult(friendDetailIntent, Constants.ActivityIntentIndex.FriendDetailActivityIndex);
                finish();
            }
        } else {
            Friend friend = CurrentSourceDateList.get(position);
            Intent friendDetailIntent = new Intent(mContext, FriendDetailActivity.class);
            friendDetailIntent.putExtra("friend", friend);
            startActivityForResult(friendDetailIntent, Constants.ActivityIntentIndex.FriendDetailActivityIndex);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:

                Intent intent = new Intent();
                intent.putExtra("back","通讯录");
                intent.setClass(mContext, AddFriendsActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }

    public class MyFriendAdapter extends BaseAdapter implements SectionIndexer {

        List<Friend> list;

        public MyFriendAdapter(List<Friend> list) {
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
                convertView = LayoutInflater.from(FriendsActivity.this).inflate(R.layout.friend_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mFrienditem.setVisibility(View.GONE);
            viewHolder.mTitleItem.setVisibility(View.GONE);
            viewHolder.mCatalog.setVisibility(View.GONE);
            viewHolder.mWarnView.setVisibility(View.GONE);
            if (ShowTitle) {
                if (position == 0) {
                    viewHolder.mTitleItem.setVisibility(View.VISIBLE);
                    viewHolder.mTitleItemText2.setVisibility(View.GONE);
                    viewHolder.mTitleItemIamge.setImageResource(R.drawable.icon_new);
                    viewHolder.mTitleItemText1.setText("新的动态");

                    if (mUntreatedMessageCount > 0)
                    {
                        viewHolder.mWarnView.setVisibility(View.VISIBLE);
                        viewHolder.mTitleItemText2.setVisibility(View.VISIBLE);
                        if (mUntreatedMessageCount < 99)
                        {
                            viewHolder.mWarnText.setText(mUntreatedMessageCount+"");
                        }
                        else
                        {
                            viewHolder.mWarnText.setText("99+");
                        }
                        if (mLastNotifyMessage.getNotifyType().compareTo("100") == 0)
                        {
                            viewHolder.mTitleItemText2.setText(mLastNotifyMessage.getSenderName()+"申请加你为好友");
                        }
                        else if (mLastNotifyMessage.getNotifyType().compareTo("102") == 0)
                        {
                            viewHolder.mTitleItemText2.setText(mLastNotifyMessage.getSenderName()+"邀请你加入家族");
                        }
                        else if(mLastNotifyMessage.getNotifyType().compareTo("103") == 0)
                        {
                            viewHolder.mTitleItemText2.setText(mLastNotifyMessage.getRemark());
                        }
                    }
                } else if (position == 1) {
                    viewHolder.mTitleItem.setVisibility(View.VISIBLE);
                    viewHolder.mTitleItemIamge.setImageResource(R.drawable.icon_qun);
                    viewHolder.mTitleItemText1.setText("群聊");
                    viewHolder.mTitleItemText2.setVisibility(View.GONE);
                } else {
                    viewHolder.mFrienditem.setVisibility(View.VISIBLE);
                    position = position - 2;
                    final Friend content = list.get(position);
                    //根据position获取分类的首字母的Char ascii值
                    int section = getSectionForPosition(position);
                    //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                    if (position == getPositionForSection(section)) {
                        viewHolder.mCatalog.setVisibility(View.VISIBLE);
                        viewHolder.mCatalog.setText(content.getLetters());
                    } else {
                        viewHolder.mCatalog.setVisibility(View.GONE);
                    }
                    viewHolder.mFriendname.setText(content.getNickname());
                    String url = mApplication.getHeadImage(content.getXf() + "", content.getHeadImage());
                    if (!TextUtils.isEmpty(url)) {
                        UILUtils.displayImage(url, viewHolder.mFrienduri);
                    }
                }
            } else {
                final Friend content = list.get(position);
                viewHolder.mFrienditem.setVisibility(View.VISIBLE);
                //根据position获取分类的首字母的Char ascii值
                int section = getSectionForPosition(position);
                //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                if (position == getPositionForSection(section)) {
                    viewHolder.mCatalog.setVisibility(View.VISIBLE);
                    viewHolder.mCatalog.setText(content.getLetters());
                } else {
                    viewHolder.mCatalog.setVisibility(View.GONE);
                }
                viewHolder.mFriendname.setText(this.list.get(position).getNickname());
                String url = mApplication.getHeadImage(this.list.get(position).getXf() + "", this.list.get(position).getHeadImage());
                if (!TextUtils.isEmpty(url)) {
                    UILUtils.displayImage(url, viewHolder.mFrienduri);
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
            for (int i = 0; i < (ShowTitle ? getCount() - 2 : getCount()); i++) {
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
            @Bind(R.id.frienduri)
            ImageView mFrienduri;
            @Bind(R.id.friendname)
            TextView mFriendname;
            @Bind(R.id.frienditem)
            LinearLayout mFrienditem;
            @Bind(R.id.titleItem_iamge)
            ImageView mTitleItemIamge;
            @Bind(R.id.titleItem_text1)
            TextView mTitleItemText1;
            @Bind(R.id.titleItem_text2)
            TextView mTitleItemText2;
            @Bind(R.id.titleItem)
            RelativeLayout mTitleItem;
            @Bind(R.id.warnText)
            TextView mWarnText;
            @Bind(R.id.warnView)
            RelativeLayout mWarnView;
            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.ActivityIntentIndex.FriendDetailActivityIndex:
                    int code = data.getIntExtra("code", Constants.FriendHandle.None);
                    if (code == Constants.FriendHandle.Delete) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code", Constants.FriendHandle.Delete);
                        setResult(RESULT_OK, intent1);
                        finish();
                    }
                    break;
            }
        }
    }
}

