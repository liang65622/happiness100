package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/2.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.MatchContactsUser;
import com.happiness100.app.model.NotifyMessage;
import com.happiness100.app.model.User;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.ClearWriteEditText;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.pinyin.CharacterParser;
import com.happiness100.app.ui.widget.swipemenulistview.BaseSwipListAdapter;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenu;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenuCreator;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenuItem;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenuListView;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.bean.Contacts;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.PhoneUtils;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.UILUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/2 09:38
 */
public class NotifyActivity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.edit_select)
    ClearWriteEditText mEditSelect;
    @Bind(R.id.list_notify)
    SwipeMenuListView mListNotify;
    private BroadcastReceiver mBroadcastReceiver;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<NotifyMessage> mListNotifyMessage;
    private MyNotifyAdapter adapter;
    private SharedPreferences mSharedPreferences;
    private User mUser;
    private List<NotifyMessage> mCurrentListNotifyMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        ButterKnife.bind(this);
        mListNotifyMessage = new ArrayList<NotifyMessage>();
        mCurrentListNotifyMessage = new ArrayList<NotifyMessage>();
        mUser = mApplication.getUser();
        mSharedPreferences = SharedPreferencesContext.getInstance().getSharedPreferences();
        initView();
        initData();
    }

    void initData() {
        //注册消息监听
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("新的消息");

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("新的消息")) {
                    updateList();
                }
            }
        };
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    void initListView()
    {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {


                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF,
                        0x00, 0x00)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(getResources().getColor(R.color.white));
                deleteItem.setTitleSize(20);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        mListNotify.setMenuCreator(creator);
        adapter = new MyNotifyAdapter(mListNotifyMessage);
        mListNotify.setAdapter(adapter);


        // step 2. listener item click event
        mListNotify.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                NotifyMessage msg = mListNotifyMessage.get(index);
                new Delete().from(NotifyMessage.class).where("soleIndex = ?",msg.getSoleIndex()).execute();
                mListNotifyMessage.remove(index);
                mCurrentListNotifyMessage = mListNotifyMessage;
                adapter.updateListView(mCurrentListNotifyMessage);
                return false;
            }
        });

        // set SwipeListener
        mListNotify.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        // set MenuStateChangeListener
        mListNotify.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
            }

            @Override
            public void onMenuClose(int position) {
            }
        });
    }

    void initView() {
        mTextBack.setText("返回");
        mTextRight.setText("添加朋友");
        mTitleViewTitle.setText("新的动态");
        mTextRight.setVisibility(View.VISIBLE);
        mTitleViewRight.setVisibility(View.VISIBLE);
        characterParser = CharacterParser.getInstance();
        mEditSelect.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                filterData(s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initListView();
        updateList();
    }


    private void filterData(String filterStr) {
        List<NotifyMessage> filterDateList = new ArrayList<NotifyMessage>();
        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mListNotifyMessage;
        } else {
            filterDateList.clear();
            for (NotifyMessage model : mListNotifyMessage) {
                String name = model.getSenderName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(model);
                }
            }
        }
        mCurrentListNotifyMessage = filterDateList;
        adapter.updateListView(mCurrentListNotifyMessage);
    }

    //刷一下数据源
    void updateListByDB() {
        mListNotifyMessage.clear();
        List<NotifyMessage> tempList = new Select().from(NotifyMessage.class).where("UserId = ?", mApplication.getUser().getXf() + "").execute();
        for (int i = tempList.size() - 1; i >= 0; --i) {
            mListNotifyMessage.add(tempList.get(i));
        }
    }

    void updateList()
    {
        if (!mEditSelect.getText().toString().isEmpty()) {
            return;
        }

        updateListByDB();
        if (mSharedPreferences.getBoolean(Constants.Function.RECOMMEND+mUser.getXf(),true))
        {
            UpdateContacts();
        }
        else
        {
            filledData(mListNotifyMessage);
            mCurrentListNotifyMessage = mListNotifyMessage;
            adapter.updateListView(mCurrentListNotifyMessage);
        }
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right, R.id.openContactsView})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                Intent intent = new Intent();
                intent.setClass(mContext, AddFriendsActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.openContactsView:
                Intent CommunicationActivityIntent = new Intent();
                CommunicationActivityIntent.putExtra("back","新的动态");
                CommunicationActivityIntent.setClass(mContext, CommunicationActivity.class);
                mContext.startActivity(CommunicationActivityIntent);
                break;
        }
    }

    View.OnClickListener AgreeLiscener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NotifyMessage content = (NotifyMessage) v.getTag();
            String type = content.getNotifyType();
            switch (type)
            {
                case "100":
                    postAgreeAddFriend(content);
                    break;
                case "102":
                    postAgreeJoinFamily(content);
                    break;
                case "601"://通讯录匹配的能添加为好友的信息
                    OnClickAddFriend(content);
                    break;
                default:
                    Log.e("AgreeLiscener","AgreeLiscenerError type = "+type);
                    break;
            }
        }
    };

    private void OnClickAddFriend(NotifyMessage content) {
        Intent addFriendVerificationIntent = new Intent(mContext, AddFriendVerificationActivity.class);
        Friend friend = new Friend();
        friend.setXf(Integer.parseInt(content.getSenderId()));
        friend.setNickname(content.getSenderName());
        friend.setHeadImage(content.getImage());
        friend.setHeadImageUri(mApplication.getHeadImage(content.getSenderId(),content.getImage()));
        addFriendVerificationIntent.putExtra("friend", friend);
        addFriendVerificationIntent.putExtra("back","新的动态");
        startActivityForResult(addFriendVerificationIntent,Constants.ActivityIntentIndex.AddFriendVerificationActivityIndex);
    }


    public class MyNotifyAdapter extends BaseSwipListAdapter implements SectionIndexer {

        List<NotifyMessage> list;

        public MyNotifyAdapter(List<NotifyMessage> list) {
            this.list = list;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<NotifyMessage> list) {
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
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.notify_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final NotifyMessage content = list.get(position);
            if (content.getNotifyType().compareTo("100") == 0) {
                viewHolder.mButton.setVisibility(View.GONE);
                viewHolder.mRightText.setVisibility(View.GONE);
                if (content.getStatus() == 0) {
                    viewHolder.mButton.setTag(content);
                    viewHolder.mButton.setVisibility(View.VISIBLE);
                    viewHolder.mButton.setTextColor(getResources().getColor(R.color.white));
                    viewHolder.mButton.setText("接受");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.mButton.setBackground(getResources().getDrawable(R.drawable.btn_common_pink));
                    }
                    else
                    {
                        viewHolder.mButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_common_pink));
                    }

                    viewHolder.mButton.setOnClickListener(AgreeLiscener);
                } else {
                    viewHolder.mRightText.setVisibility(View.VISIBLE);
                    viewHolder.mRightText.setText("已接受");
                }
            }
            else if (content.getNotifyType().compareTo("102") == 0){
                viewHolder.mButton.setVisibility(View.GONE);
                viewHolder.mRightText.setVisibility(View.GONE);
                if (content.getStatus() == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.mButton.setBackground(getResources().getDrawable(R.drawable.btn_common_pink));
                    }
                    else
                    {
                        viewHolder.mButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_common_pink));
                    }
                    viewHolder.mButton.setTag(content);
                    viewHolder.mButton.setVisibility(View.VISIBLE);
                    viewHolder.mButton.setText("加入");
                    viewHolder.mButton.setTextColor(getResources().getColor(R.color.white));
                    viewHolder.mButton.setOnClickListener(AgreeLiscener);
                } else {
                    viewHolder.mRightText.setVisibility(View.VISIBLE);
                    viewHolder.mRightText.setText("已加入");
                }
            }
            else if(content.getNotifyType().compareTo("601") == 0){
                viewHolder.mRightText.setVisibility(View.GONE);
                viewHolder.mButton.setVisibility(View.GONE);

                if (content.getStatus() == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        viewHolder.mButton.setVisibility(View.VISIBLE);
                        viewHolder.mButton.setBackground(getResources().getDrawable(R.drawable.btn_common_white));
                        viewHolder.mButton.setTag(content);
                        viewHolder.mButton.setText("添加");
                        viewHolder.mButton.setTextColor(getResources().getColor(R.color.black));
                        viewHolder.mButton.setOnClickListener(AgreeLiscener);
                    }
                    else
                    {
                        viewHolder.mButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_common_white));
                    }
                }
                else
                {
                    viewHolder.mRightText.setVisibility(View.VISIBLE);
                    viewHolder.mRightText.setText("等待验证");
                }
            }
            else if(content.getNotifyType().compareTo("103") == 0){
                viewHolder.mRightText.setVisibility(View.GONE);
                viewHolder.mButton.setVisibility(View.GONE);
                new Update(NotifyMessage.class).set("status = ?", 1).where("soleIndex = ? and userId = ?", content.getSoleIndex(),mApplication.getUser().getXf()+"").execute();
            }
            else{
            }
            viewHolder.mText1.setText(content.getSenderName().isEmpty()?"":content.getSenderName());
            viewHolder.mText2.setText(content.getRemark().isEmpty()?"":content.getRemark());
            String url = this.list.get(position).getImage();
            if (!TextUtils.isEmpty(url)) {
                url = mApplication.getHeadImage(content.getSenderId(), content.getImage());
                UILUtils.displayImage(url,viewHolder.mViewImage);
            }

            return convertView;
        }

        @Override
        public Object[] getSections() {
            return new Object[0];
        }


        @Override
        public int getPositionForSection(int sectionIndex) {

            return sectionIndex;
        }


        @Override
        public int getSectionForPosition(int position) {
            return position;
        }

        @Override
        public boolean getSwipEnableByPosition(int position) {
            return true;
        }
         class ViewHolder {
            @Bind(R.id.ViewImage)
            ImageView mViewImage;
            @Bind(R.id.text1)
            TextView mText1;
            @Bind(R.id.text2)
            TextView mText2;
             @Bind(R.id.button)
             Button mButton;
            @Bind(R.id.rightText)
            TextView mRightText;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    private void filledData(List<NotifyMessage> lsit) {
        List<Friend> mFriendList = new ArrayList<Friend>();

        for (int i = 0; i < lsit.size(); i++) {
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(lsit.get(i).getSenderName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                lsit.get(i).setLetters(sortString.toUpperCase());
            } else {
                lsit.get(i).setLetters("#");
            }
        }
    }

    void postAgreeAddFriend(final NotifyMessage message) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("app_id", message.getSenderId());
        params.put("app_name", message.getSenderName());

        APIClient.post(mContext, Constants.URL.AGREE_FRIENDS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        new Update(NotifyMessage.class).set("status = ?", 1).where("soleIndex = ? and userId = ?", message.getSoleIndex(),mApplication.getUser().getXf()+"").execute();
                        updateList();
                        Friend friend = GsonUtils.parseJSON(json,Friend.class);
                        FriendsManager.getInstance().addFriend(friend);
                    }
                });
            }
        });
    }

    void postAgreeJoinFamily(final NotifyMessage message) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("clan_id",message.getSenderId());
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_INVITE_AGREE_JOIN, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        new Update(NotifyMessage.class).set("status = ?", 1).where("soleIndex = ? and userId = ?", message.getSoleIndex(),mApplication.getUser().getXf()+"").execute();
                        updateList();
                    }
                });
            }
        });
    }

    void UpdateContacts()
    {
        Map<String,String> phonesnameMap = new HashMap<>();
        List<Contacts> tempList = mApplication.getContacts();
        if(tempList==null) {
            tempList = PhoneUtils.getAllContacts(this);
        }
        String Phones = "";
        boolean needNet = false;
        if (tempList.size()> 0)
        {
            for (int i = 0;i <tempList.size();++i)
            {
                Contacts contacts = tempList.get(i);
                List<String> phoneList = contacts.getPhones();
                for (int j = 0;j <phoneList.size();++j)
                {
                    String PhoneNum = phoneList.get(j);
                    Friend friend = FriendsManager.getInstance().findFriendByPhone(PhoneNum);
                    if (friend != null)
                    {

                    }
                    else
                    {
                        if (PhoneNum.compareTo(mApplication.getUser().getMobile()) == 0)
                            continue;
                        needNet = true;
                        if (Phones.isEmpty())
                        {
                            if (PhoneNum.length() == 11)
                                Phones ="86 "+PhoneNum;
                            else
                                Phones =PhoneNum;
                        }
                        else
                        {
                            if (PhoneNum.length() == 11)
                                Phones = Phones + ",86 "+PhoneNum;
                            else
                                Phones = Phones + ","+PhoneNum;
                        }

                        if (PhoneNum.length() == 11)
                            phonesnameMap.put("86 "+PhoneNum,contacts.getName());
                        else
                            phonesnameMap.put(PhoneNum,contacts.getName());
                    }
                }
            }
        }

        if (!needNet)
        {
            filledData(mListNotifyMessage);
            mCurrentListNotifyMessage =mListNotifyMessage;
            adapter.updateListView(mCurrentListNotifyMessage);
            return;
        }
        final Map<String,String> phonesnameMapFinal = phonesnameMap;
        Map<String, String> params = new HashMap<>();
        params.put("phones", Phones);
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

                        Type type = new TypeToken<ArrayList<MatchContactsUser>>() {
                        }.getType();
                        List<MatchContactsUser> datas = GsonUtils.parseJSONArray(json, type);
                        if (datas != null && datas.size() > 0)
                        {
                            for (int i = 0;i <datas.size();++i)
                            {
                                MatchContactsUser data = datas.get(i);
                                NotifyMessage msg = new NotifyMessage();
                                msg.setNotifyType("601");
                                msg.setSenderName(data.getNickname());
                                msg.setSenderId(data.getUserId()+"");
                                msg.setImage(data.getHeadImage());
                                String notename = phonesnameMapFinal.get(data.getMobile());
                                msg.setRemark("手机联系人："+notename);
                                mListNotifyMessage.add(msg);
                            }
                        }
                        filledData(mListNotifyMessage);
                        mCurrentListNotifyMessage =mListNotifyMessage;
                        adapter.updateListView(mCurrentListNotifyMessage);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case Constants.ActivityIntentIndex.AddFriendVerificationActivityIndex:
                    String xfh = data.getStringExtra("xf");
                    for (int i = 0 ; i < mListNotifyMessage.size();++i)
                    {
                        NotifyMessage msg = mListNotifyMessage.get(i);
                        if (xfh.compareTo(msg.getSenderId()+"") == 0)
                        {
                            msg.setStatus(2);
                            mListNotifyMessage.set(i,msg);
                            break;
                        }
                    }
                    mCurrentListNotifyMessage = mListNotifyMessage;
                    adapter.updateListView(mCurrentListNotifyMessage);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}


