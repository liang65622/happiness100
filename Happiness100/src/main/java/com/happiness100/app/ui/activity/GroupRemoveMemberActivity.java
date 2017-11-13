package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/6.
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
 * 作者：jiangsheng on 2016/9/6 09:35
 */
public class GroupRemoveMemberActivity extends BaseActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    //带字母的集合
    private List<Friend> SourceDateList;

    //当前集合
    private List<Friend> CurrentSourceDateList;

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

    String mId;//群Id
    String DeleteId = "";//准备删除的id
    String mGroupBitmapBase64 = " ";//群图片
    List<Bitmap> mGroupMemberBitMapList;//群成员头像
    Map<String,Friend> mGroupMemberInfoMap;//群成员信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        String back = intent.getStringExtra("back");
        ArrayList<Friend> sourctList = intent.getParcelableArrayListExtra("list");

        mGroupMemberBitMapList = new ArrayList<Bitmap>();
        mGroupMemberInfoMap = new HashMap<String,Friend>();

        for (int i = 0; i < sourctList.size();++i)
        {
            Friend fd = sourctList.get(i);
            mGroupMemberInfoMap.put(fd.getXf()+"",fd);
        }

        //实例化汉字转拼音类
        mTitleViewTitle.setText("选择联系人");
        mTextBack.setText(back==null?"返回":back);
        mTitleViewRight.setVisibility(View.VISIBLE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText("删除");
        mTextRight.setTextColor(getResources().getColor(R.color.white));

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

        filledData(sourctList); //过滤数据对象为友字母字段
        SourceDateList = new ArrayList<Friend>();
        for (int i = 0; i < sourctList.size(); i++) {
            int xf = sourctList.get(i).getXf();
            if (xf != mApplication.getUser().getXf())
            {
                Friend item = new Friend();
                item.setXf(sourctList.get(i).getXf());
                item.setNickname(sourctList.get(i).getNickname());
                item.setHeadImageUri(mApplication.getHeadImage(sourctList.get(i).getXf()+"",sourctList.get(i).getHeadImage()));
                item.setHeadImage(sourctList.get(i).getHeadImage());
                item.setLetters(sourctList.get(i).getLetters());
                SourceDateList.add(item);
            }
        }

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        CurrentSourceDateList =SourceDateList;
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
        checkGroup();
    }


    /**
     * 为ListView填充数据
     *
     * @param
     * @return
     */
    private void filledData(List<Friend> lsit) {
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
        } else {
            filterDateList.clear();
            for (Friend friendModel : SourceDateList) {
                String name = friendModel.getNickname();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(friendModel);
                }
            }
        }
        CurrentSourceDateList =filterDateList;
        // 根据a-z进行排序
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
        ImageView checkImage = (ImageView)view.findViewById(R.id.SelectItem_SelectImage);
        Friend friend= CurrentSourceDateList.get(position);
        String friendxfId = friend.getXf()+"";
        if (DeleteId.compareTo(friendxfId) != 0)
            DeleteId = friendxfId;
        checkGroup();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    void checkGroup()
    {
        if (!DeleteId.isEmpty())
        {
            mTitleViewRight.setEnabled(true);
            mTextRight.setTextColor(getResources().getColor(R.color.white));
        }
        else
        {
            mTitleViewRight.setEnabled(false);
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
                if (DeleteId == null ||DeleteId.isEmpty())
                {
                    return;
                }
                RemoveMemberByGroup();
                break;
        }
    }

    void RemoveMemberByGroup()
    {
        mGroupBitmapBase64 = "";
        mGroupMemberBitMapList.clear();
        if (mGroupMemberInfoMap.size() <= 5)
        {
//遍历成员
            Map<String,Friend> tempMemberMap =new HashMap<String,Friend>();

            for (String key:mGroupMemberInfoMap.keySet())
            {
                if (tempMemberMap.size() < 6)
                {
                    tempMemberMap.put(key,mGroupMemberInfoMap.get(key));
                }
            }
            tempMemberMap.remove(DeleteId);
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
                    //删除成员
                    RemoveMember(mId,DeleteId,mGroupBitmapBase64);
                }
            });
            mikyou.execute(uriList);
        }
        else
        {
            RemoveMember(mId,DeleteId,mGroupBitmapBase64);
        }
    }

    void RemoveMember(final String groupId,final String ID,final String BitmapBase64)
    {
        RongIM.getInstance().removeMemberFromDiscussion(groupId, ID, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                Map<String,String> params = new LinkedHashMap<>();
                params.put("sessionid",mApplication.getUser().getSessionid());
                params.put("discus_id",groupId);
                params.put("opt_type","2");
                params.put("remover_id",ID);
                params.put("ext",BitmapBase64);
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

                                LoadDialog.dismiss(mContext);
                                Intent intent = new Intent();
                                intent.putExtra("code",Constants.FriendHandle.Delete);
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                ToastUtils.shortToast(mContext,"删除失败 errorCode = "+errorCode.getMessage());
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.group_exchange_member_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.mCatalog.setVisibility(View.GONE);
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
            viewHolder.mSelectItemNickName.setText(this.list.get(position).getNickname());
            String url = this.list.get(position).getHeadImageUri();
            if (!TextUtils.isEmpty(url)) {
                UILUtils.displayImage(url,viewHolder.mSelectItemHeadView);
            }

            String friendid = content.getXf()+"";
            if (DeleteId.compareTo(friendid) == 0)
            {
                viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_selected);
            }
            else
            {
                viewHolder.mSelectItemSelectImage.setImageResource(R.drawable.icon_select_normal);
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
            @Bind(R.id.SelectItem_SelectImage)
            ImageView mSelectItemSelectImage;
            @Bind(R.id.SelectItem_HeadView)
            ImageView mSelectItemHeadView;
            @Bind(R.id.SelectItem_NickName)
            TextView mSelectItemNickName;
            @Bind(R.id.SelectItem_View)
            LinearLayout mSelectItemView;

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

