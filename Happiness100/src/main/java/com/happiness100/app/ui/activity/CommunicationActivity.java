package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/31.
 */

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.MatchContactsUser;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.ClearWriteEditText;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.pinyin.CharacterParser;
import com.happiness100.app.ui.widget.pinyin.PinyinComparator;
import com.happiness100.app.ui.widget.pinyin.SideBar;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.bean.Contacts;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.PhoneUtils;
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

/**
 * 作者：jiangsheng on 2016/8/31 15:35
 */
public class CommunicationActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    //带字母的集合
    private List<CommunicationListData> SourceDateList;
    //当前集合
    private List<CommunicationListData> CurrentSourceDateList;
    public static FriendsActivity instance = null;

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
    private GroupChatAdapter adapter;
    /**
     * 右侧好友指示 Bar
     */
    private SideBar mSidBar;
    /**
     * 中部展示的字母提示
     */
    public TextView dialog;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        //实例化汉字转拼音类
        Intent intent = getIntent();
        mTitleViewTitle.setText("通讯录朋友");
        mTextBack.setText(intent.getStringExtra("back")==null?"返回":intent.getStringExtra("back"));

        characterParser = CharacterParser.getInstance();
        pinyinComparator = PinyinComparator.getInstance();
        mAboutSerrch = (ClearWriteEditText) findViewById(R.id.filter_edit);
        mListView = (ListView) findViewById(R.id.friendlistview);
        mSidBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
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
        SourceDateList = new ArrayList<CommunicationListData>();
        CurrentSourceDateList = new ArrayList<CommunicationListData>();
        // 根据a-z进行排序源数据
        adapter = new GroupChatAdapter(CurrentSourceDateList);
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
        CheckContacts();
    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private void filledData(List<CommunicationListData> lsit) {
        List<CommunicationListData> mFriendList = new ArrayList<CommunicationListData>();

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
        List<CommunicationListData> filterDateList = new ArrayList<CommunicationListData>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (CommunicationListData friendModel : SourceDateList) {
                String name = friendModel.getNickname();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(friendModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        CurrentSourceDateList =filterDateList;
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @OnClick({R.id.title_view_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;

        }
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CommunicationListData info = (CommunicationListData)v.getTag();
            Intent addFriendVerificationIntent = new Intent(mContext, AddFriendVerificationActivity.class);
            addFriendVerificationIntent.putExtra("friend", info);
            startActivityForResult(addFriendVerificationIntent,Constants.ActivityIntentIndex.AddFriendVerificationActivityIndex);
        }
    };



    public class GroupChatAdapter extends BaseAdapter implements SectionIndexer {

        List<CommunicationListData> list;

        public GroupChatAdapter(List<CommunicationListData> list) {
            this.list = list;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<CommunicationListData> list) {
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
                convertView = LayoutInflater.from(CommunicationActivity.this).inflate(R.layout.activity_communication_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final CommunicationListData content = list.get(position);
            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.mCatalog.setVisibility(View.VISIBLE);
                viewHolder.mCatalog.setText(content.getLetters());
            } else {
                viewHolder.mCatalog.setVisibility(View.GONE);
            }

            viewHolder.mAddFriendButton.setVisibility(View.GONE);
            viewHolder.mAddFriendText.setVisibility(View.GONE);
            if (content.getStatus() == 0)
            {
                viewHolder.mAddFriendText.setVisibility(View.VISIBLE);
                viewHolder.mAddFriendText.setText("已添加");
            }
            else if(content.getStatus() == 1)
            {
                viewHolder.mAddFriendButton.setVisibility(View.VISIBLE);
                viewHolder.mAddFriendButton.setTag(content);
                viewHolder.mAddFriendButton.setOnClickListener(listener);
            }
            else
            {
                viewHolder.mAddFriendText.setVisibility(View.VISIBLE);
                viewHolder.mAddFriendText.setText("等待验证");
            }
            viewHolder.mDesc.setText("手机联系人："+this.list.get(position).getNoteName());
            viewHolder.mNicknameText.setText(this.list.get(position).getNickname());
            String url = mApplication.getHeadImage(this.list.get(position).getXf()+"",this.list.get(position).getHeadImage());
            if (!TextUtils.isEmpty(url)) {
                UILUtils.displayImage(url,viewHolder.mHeadViewImage);
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

        class ViewHolder {
            @Bind(R.id.catalog)
            TextView mCatalog;
            @Bind(R.id.headViewImage)
            ImageView mHeadViewImage;
            @Bind(R.id.nicknameText)
            TextView mNicknameText;
            @Bind(R.id.desc)
            TextView mDesc;
            @Bind(R.id.addFriendButton)
            TextView mAddFriendButton;
            @Bind(R.id.addFriendText)
            TextView mAddFriendText;
            @Bind(R.id.frienditem)
            RelativeLayout mFrienditem;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


    void CheckContacts()
    {
        SourceDateList.clear();
        List<Contacts> tempList = mApplication.getContacts();
        if(tempList==null) {
            tempList = PhoneUtils.getAllContacts(this);
        }
        String Phones = "";
        Map<String,String> synMap=  new HashMap<>();
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
                        CommunicationListData con = new CommunicationListData(friend.getXf(),friend.getNickname(),contacts.getName(),friend.getMobile(),friend.getHeadImage());
                        SourceDateList.add(con);
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
                            synMap.put(PhoneNum,contacts.getName());
                        }
                        else
                        {

                            if (PhoneNum.length() == 11)
                            {
                                Phones = Phones + ",86 "+PhoneNum;
                                synMap.put("86 "+PhoneNum,contacts.getName());
                            }
                            else
                            {
                                Phones = Phones + ","+PhoneNum;
                                synMap.put(PhoneNum,contacts.getName());
                            }
                        }
                    }
                }
            }
        }

        if (!needNet)
        {
            filledData(SourceDateList); //过滤数据对象为友字母字段
            Collections.sort(SourceDateList, pinyinComparator);
            CurrentSourceDateList =SourceDateList;
            adapter.updateListView(CurrentSourceDateList);
            return;
        }

        final Map<String, String> finalSynMap = synMap;

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
                                String name = finalSynMap.get(data.getMobile());
                                CommunicationListData con = new CommunicationListData(data.getUserId(),data.getNickname(),name,data.getMobile(),data.getHeadImage());
                                con.setStatus(1);
                                SourceDateList.add(con);
                            }
                        }
                        filledData(SourceDateList); //过滤数据对象为友字母字段
                        Collections.sort(SourceDateList, pinyinComparator);
                        CurrentSourceDateList =SourceDateList;
                        adapter.updateListView(CurrentSourceDateList);
                    }
                });
            }
        });

    }

    public class CommunicationListData extends Friend
    {
        public CommunicationListData(int id,String name,String NoteName,String mobile,String Image) {
            setXf(id);
            setNickname(name);
            setMobile(mobile);
            setHeadImage(Image);
            this.NoteName = NoteName;
        }

        public CommunicationListData()
        {

        }

        private int Status;

        String NoteName;
        public void setStatus(int Status)
        {
            this.Status = Status;
        }

        public int getStatus()
        {
            return Status;
        }

        public void setNoteName(String NoteName)
        {
            this.NoteName = NoteName;
        }

        public String getNoteName()
        {
            return NoteName;
        }
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
                   for (int i = 0 ; i < SourceDateList.size();++i)
                   {
                       CommunicationListData Communicationdata = SourceDateList.get(i);
                       if (xfh.compareTo(Communicationdata.getXf()+"") == 0)
                       {
                           Communicationdata.setStatus(2);
                           SourceDateList.set(i,Communicationdata);
                           break;
                       }
                   }
                    filledData(SourceDateList); //过滤数据对象为友字母字段
                    Collections.sort(SourceDateList, pinyinComparator);
                    CurrentSourceDateList =SourceDateList;
                    adapter.updateListView(CurrentSourceDateList);
                    break;
            }
        }
    }
}

