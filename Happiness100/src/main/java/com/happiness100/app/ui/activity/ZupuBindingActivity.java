package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/25.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.happiness100.app.R;
import com.happiness100.app.model.ClanRela;
import com.happiness100.app.model.ClanRela.PUnit;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.model.FamilyIndex.Member;
import com.happiness100.app.model.MatchContactsUser;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.pinyin.CharacterParser;
import com.happiness100.app.ui.widget.pinyin.PinyinMatchContactsUserComparator;
import com.happiness100.app.ui.widget.pinyin.SideBar;
import com.happiness100.app.utils.Constants;
import com.justin.utils.UILUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;


/**
 * 作者：justin on 2016/8/25 14:40
 */
public class ZupuBindingActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, BaseViewInterface {

    private static final int REQUEST_INVITATION = 2;
    private static final String TAG = "ZupuBindingActivity";
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
    private FamilyIndex mFamilyIndex;
    private ClanRela mClanRela;
    private List<PUnit> mPUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zp_binding_member);
        ButterKnife.bind(this);
        mContext = this;
        mClanRela = getIntent().getParcelableExtra("clanRela");
        mFamilyIndex = mApplication.getFamilyIndex();
        mPUserList = getIntent().getParcelableArrayListExtra("punits");
        initView();
        initData();
    }

    @Override
    public void initView() {
        //实例化汉字转拼音类
        mTitleViewTitle.setText("绑定家族成员");
        mTextBack.setText("返回");


        rgContactsType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_phone_contacts:
                        adapter.setData(mFamilyIndex.members);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.rb_friend:
                        adapter.setData(mFamilyIndex.members);
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


    void updateView() {

        //-------------- 获取好友列表数据 ---------------
//        //通讯录匹配
//        SourceDateList = filledData(mSourceDateList); //过滤数据对象为友字母字段
//        for (int i = 0; i < mSourceDateList.size(); i++) {
//            SourceDateList.get(i).setUserId(mSourceDateList.get(i).getUserId());
//            SourceDateList.get(i).setName(mSourceDateList.get(i).getName());
//            SourceDateList.get(i).setHeadImage(mSourceDateList.get(i).getHeadImage());
//        }
//        if (SourceDateList.size() > 0) {
//            show_no_friends.setVisibility(View.GONE);
//        }
//
//        mSourceDateList = null;
//        // 根据a-z进行排序源数据
//        Collections.sort(SourceDateList, pinyinComparator);
//        adapter = new MatchContactsUserAdapter(mContext, SourceDateList);
//        mListView.setAdapter(adapter);
//        mListView.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        mLoadDialog = new LoadDialog(mContext, false, "");
        mLoadDialog.show();

        //家族
        List<Member> members = new ArrayList<>(mFamilyIndex.members);
        members = filterFamilyMember(members);
        if (members != null && members.size() > 0) {
            mFamilyIndex.members = members;
            mFamilyIndex.members = filledData(mFamilyIndex.members);
            for (int i = 0; i < members.size(); i++) {
                mFamilyIndex.members.get(i).setUserid(members.get(i).getUserid());
                mFamilyIndex.members.get(i).setNickname(members.get(i).getNickname());
                mFamilyIndex.members.get(i).setHeadImage(members.get(i).getHeadImage());
            }

            adapter = new MatchContactsUserAdapter(mContext, mFamilyIndex.members);
            mListView.setAdapter(adapter);
        }
        mLoadDialog.dismiss();
    }

    //将已经绑定的过滤
    private List<FamilyIndex.Member> filterFamilyMember(List<Member> members) {

        if (members.size() == 0) {
            return null;
        }

        for (Member member : members) {
            if (member.getPhone() == null) {
                members.remove(member);
            }
        }

        for (Member member : members) {
            for (PUnit punit : mPUserList) {
                if (member.getUserid() == punit.userId) {
                    members.remove(member);
                    return filterFamilyMember(members);
                }
            }
        }
        return members;
    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private List<FamilyIndex.Member> filledData(List<FamilyIndex.Member> lsit) {
        List<Member> mFriendList = new ArrayList<>();

        for (int i = 0; i < lsit.size(); i++) {
            Member friendModel = new Member();
            friendModel.setNickname(lsit.get(i).getNickname());
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(lsit.get(i).getNickname());
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
                mContext.finish();
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

        private List<Member> list;

        public MatchContactsUserAdapter(Context context, List<Member> list) {
            this.context = context;
            this.list = list;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<Member> list) {
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
            final Member member = list.get(position);
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
            viewHolder.mBtnAddMember.setText("绑定");

            //根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(member.getLetters());
            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }
            final Member matchUser = list.get(position);
            viewHolder.tvTitle.setText(matchUser.getNickname());
//            if (matchUser.getIsWaitAuth() == 1) {
//                viewHolder.mBtnAddMember.setVisibility(View.GONE);
//                viewHolder.mWaitAuth.setVisibility(View.VISIBLE);
//
//            } else {
            viewHolder.mBtnAddMember.setVisibility(View.VISIBLE);
            viewHolder.mWaitAuth.setVisibility(View.GONE);
            viewHolder.mBtnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentControlPosition = position;
                    Intent data = new Intent();
                    data.putExtra("userId", matchUser.getUserid());
                    setResult(RESULT_OK, data);
                    mContext.finish();
                }
            });


            String url = this.list.get(position).getHeadImage();
            if (!TextUtils.isEmpty(url))

            {
                UILUtils.displayImage(mApplication.getHeadImage(matchUser.getUserid() + "", url), viewHolder.mImageView);
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

        public void setData(List<Member> data) {
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

    //

}
