package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/8/23.
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.AppContext;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.model.MatchContactsFamily;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.activity.FamilyAddMemberActivity;
import com.happiness100.app.ui.activity.BaseActivity;
import com.happiness100.app.ui.activity.CreateFamilyActiivty;
import com.happiness100.app.ui.activity.FamilyDynamicActivity;
import com.happiness100.app.ui.activity.FamilyInfoActivity;
import com.happiness100.app.ui.activity.FamilyListActivity;
import com.happiness100.app.ui.activity.FamilyRankActivity;
import com.happiness100.app.ui.activity.FamilyMemberInfoActivity;
import com.happiness100.app.ui.activity.SearchFamilyActivity;
import com.happiness100.app.ui.widget.FamilyManagePopWindow;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;
import com.justin.utils.UILUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：justin on 2016/8/23 09:25
 */
public class FamilyFragmentBAK0902 extends BaseFragment implements BaseViewInterface, View.OnClickListener {
    private static final int VP_COUNT = 4;
    private static final String TAG = "FamilyFragment";
    private static final int MATCH_FAMILY_PAGE_COUNT = 4;
    private static final int FAMILY_MEMBER_COUNT = 4;
    private static final int CHANGE_FAMILY = 5;
    BaseActivity mContext;
    private ViewPager mVpEdler;
    private ViewPager mVpPeers;
    private ViewPager mVpYounger;

    private List<GridView> mGvMatchList = new ArrayList<>();
    private List<GridView> mGvEdlerList = new ArrayList<>();
    private List<GridView> mGvPeersList = new ArrayList<>();
    private List<GridView> mGvYoungerList = new ArrayList<>();

    List<GridView> mElderExpandGridViews = new ArrayList<>();
    List<GridView> mPeersExpandGridViews = new ArrayList<>();
    List<GridView> mYoungerExpandGridViews = new ArrayList<>();

    private LayoutInflater mLayoutInflater;
    private Button mBtnCreateFamily;
    private ViewPager mVpContactsFamily;
    private Map<String, String> mContactsMap = new HashMap<>();
    private List<MatchContactsFamily> mMactchFamilys;//通讯录匹配族列表
    private PagerAdapter mContactsFamilyAdapter;
    private List<FamilyBaseInfo> mFamilyBaseInfos;
    private TextView mTvFamily;
    private View mMenuView;
    private TextView mTvHappinessLevel;
    private TextView mTvDutifulLevel;
    private TextView mTvCareForLevel;
    private TextView mTvCharitableLevel;
    private FamilyIndex mFamilyIndex;
    private PagerAdapter mAdpElder;
    private PagerAdapter mAdpPeers;
    private SeniorityAdapter mAdpYounger;
    private TextView mTvElderTitle;
    private TextView mTvPeersTitle;
    private TextView mTvYoungerTitle;
    private ListView mLvFamilyDynamic;
    private String mClanId;
    private FamilyBaseInfo mDefaultFamilyBaseInfo;

    public FamilyFragmentBAK0902() {
        Log.e(TAG, "FamilyFragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        mLayoutInflater = getLayoutInflater(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        mFamilyBaseInfos = mApplication.getUser().getFamilyBaseInfos();
        mDefaultFamilyBaseInfo = mApplication.getUser().getDefaultFamilyBaseInfo();

        if (mFamilyBaseInfos != null && mFamilyBaseInfos.size() > 0) {//TODO 如果已经有家族，进入族界面
            v = inflater.inflate(R.layout.fragment_family, container, false);
            initFamilyView(v);
            SharedPreferencesContext.getInstance().getSharedPreferences().edit().putString("clanId", mDefaultFamilyBaseInfo.getClanId() + "").commit();
        } else {//无家族进入创建家族界面
            v = inflater.inflate(R.layout.fragment_create_family_tips, container, false);
            initCreateFamilyView(v);
            initContactsInfo();
        }
        return v;
    }

    private void initCreateFamilyView(View v) {
        View viewSearchFamily = v.findViewById(R.id.view_search_family);
        viewSearchFamily.setOnClickListener(this);
        mVpContactsFamily = (ViewPager) v.findViewById(R.id.vp_contacts_family);
        mBtnCreateFamily = (Button) v.findViewById(R.id.btn_create_family);

        mBtnCreateFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreateFamilyActiivty.class);
                startActivity(intent);
            }
        });

        mContactsFamilyAdapter = new ContactsFamilyAdapter();
        mVpContactsFamily.setAdapter(mContactsFamilyAdapter);


    }

    private void initFamilyView(View v) {
        v.findViewById(R.id.title_view_back).setVisibility(View.GONE);
        mTvFamily = (TextView) v.findViewById(R.id.tv_family_name);
        mTvFamily.setText("家族名（" + mDefaultFamilyBaseInfo.getClanName() + ")");

        //更换家族
        View viewChangeFamily = v.findViewById(R.id.iv_change_family);

        if (mApplication.getUser().getFamilyBaseInfos().size() < 2) {
            viewChangeFamily.setVisibility(View.GONE);
        } else {
            viewChangeFamily.setVisibility(View.VISIBLE);
        }
        viewChangeFamily.setOnClickListener(this);

        //菜单选项
        mMenuView = v.findViewById(R.id.image_right);
        mMenuView.setOnClickListener(this);

        //排行榜图标
        View view = v.findViewById(R.id.rl_rank);
        view.setOnClickListener(this);

        //指数
        mTvHappinessLevel = (TextView) v.findViewById(R.id.tv_happiness_level);
        mTvDutifulLevel = (TextView) v.findViewById(R.id.tv_dutiful_level);
        mTvCareForLevel = (TextView) v.findViewById(R.id.tv_care_for_level);
        mTvCharitableLevel = (TextView) v.findViewById(R.id.tv_charitable_level);


        //更多动态
        View viewMore = v.findViewById(R.id.view_more);
        viewMore.setOnClickListener(this);

        mLvFamilyDynamic = (ListView) v.findViewById(R.id.lv_family_dynamic);
//        lv_family_dynamic

        //初始化动态数据
        initDynamic();

        //初始化 健康主页数据
        initFamilyIdexData(mDefaultFamilyBaseInfo.getClanId());

        mTvElderTitle = (TextView) v.findViewById(R.id.tv_elder_count);
        mTvPeersTitle = (TextView) v.findViewById(R.id.tv_peers_count);
        mTvYoungerTitle = (TextView) v.findViewById(R.id.tv_younger_count);

        mVpEdler = (ViewPager) v.findViewById(R.id.vp_elder);
        mAdpElder = new SeniorityAdapter(mGvEdlerList);
        mVpEdler.setAdapter(mAdpElder);

        mVpPeers = (ViewPager) v.findViewById(R.id.vp_peers);
        mAdpPeers = new SeniorityAdapter(mGvPeersList);
        mVpPeers.setAdapter(mAdpPeers);

        mVpYounger = (ViewPager) v.findViewById(R.id.vp_younger);
        mAdpYounger = new SeniorityAdapter(mGvYoungerList);
        mVpYounger.setAdapter(mAdpYounger);

        //展开图标
        ImageView mExpandElder = (ImageView) v.findViewById(R.id.iv_expand_elder);
        mExpandElder.setOnClickListener(this);

        //展开图标
        ImageView mExpandPeers = (ImageView) v.findViewById(R.id.iv_expand_peers);
        mExpandElder.setOnClickListener(this);

        //展开图标
        ImageView mExpandYounger = (ImageView) v.findViewById(R.id.iv_expand_younger);
        mExpandElder.setOnClickListener(this);

    }

    private void initDynamic() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mDefaultFamilyBaseInfo.getClanId() + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_XING_FU_DTON_TAI, params, new BaseVolleyListener(mContext) {
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
                        if (true) {
                            mLvFamilyDynamic.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    private void initFamilyIdexData(int clanId) {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", clanId + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_INDEX, params, new BaseVolleyListener(mContext) {
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
                        mFamilyIndex = GsonUtils.parseJSON(json, FamilyIndex.class);

                        //保存当前家族权限
                        AppContext.getPreferences().edit().putInt("clanRole", mFamilyIndex.getCreateID()).commit();

                        mTvHappinessLevel.setText("幸福LV：" + mFamilyIndex.getVals().get(0));
                        mTvDutifulLevel.setText("孝顺LV：" + mFamilyIndex.getVals().get(1));
                        mTvCareForLevel.setText("关爱LV：" + mFamilyIndex.getVals().get(2));
                        mTvCharitableLevel.setText("慈善LV：" + mFamilyIndex.getVals().get(3));


                        mGvEdlerList.clear();
                        mGvPeersList.clear();
                        mGvMatchList.clear();

                        mAdpElder.notifyDataSetChanged();
                        mAdpPeers.notifyDataSetChanged();
                        mAdpYounger.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_search_family:
                startActivity(new Intent(mContext, SearchFamilyActivity.class));
                break;
            case R.id.image_right:
                FamilyManagePopWindow popWindow = new FamilyManagePopWindow(mContext, mFamilyIndex);
                popWindow.showPopupWindow(view);
                break;
            case R.id.view_more:
                Intent intent = new Intent(mContext, FamilyDynamicActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_change_family:
                startActivityForResult(new Intent(mContext, FamilyListActivity.class), CHANGE_FAMILY);
                break;
            case R.id.iv_expand_elder:
                //展开
                mVpEdler.setAdapter(new SeniorityAdapter(mElderExpandGridViews));
                //收回
                break;

            case R.id.iv_expand_peers:
                //展开
                mVpPeers.setAdapter(new SeniorityAdapter(mElderExpandGridViews));
                //收回
                break;

            case R.id.iv_expand_younger:
                //展开
                mVpEdler.setAdapter(new SeniorityAdapter(mElderExpandGridViews));
                //收回
                break;

            case R.id.rl_rank:
                startActivity(new Intent(mContext, FamilyRankActivity.class));
                break;
            default:
                break;
        }

        //管理员
//        if (true) {//TODO 判断是否是管理员
//            FamilyManagePopWindow popWindow = new FamilyManagePopWindow(mContext);
//            popWindow.showPopupWindow(view);
//        } else {
//
//        }
    }


    void initGridList(List<GridView> gridViews, List<FamilyIndex.Member> members, int position) {
        int elderMemberSize = members.size();

        int elderVpSize = 0;
        if (elderMemberSize % VP_COUNT > 0) {
            elderVpSize = elderMemberSize / VP_COUNT + 1;
        } else {
            elderVpSize = elderMemberSize / VP_COUNT;
        }

        initExpandGridViews();


        if (elderVpSize == 0) {
            GridView gridView = (GridView) mLayoutInflater.inflate(R.layout.gridview_family_seniority, null);
            List<FamilyIndex.Member> list = new ArrayList<>();
            list.add(new FamilyIndex.Member());
            gridView.setAdapter(new SeniorityGridViewAdapter(list));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(mContext, FamilyAddMemberActivity.class));
                }
            });

            gridViews.add(gridView);

        } else {
            for (int i = 0; i < elderVpSize; i++) {
                //家族成员 分页
                final List<FamilyIndex.Member> list = new ArrayList<>();

                for (int j = 0; j < FAMILY_MEMBER_COUNT; j++) {

                    if ((i * FAMILY_MEMBER_COUNT + j) < members.size()) {

                        list.add(members.get(i * MATCH_FAMILY_PAGE_COUNT + j));
                    }

                }

                if (list.size() < FAMILY_MEMBER_COUNT) {
                    list.add(new FamilyIndex.Member());
                }

                GridView gridView = (GridView) mLayoutInflater.inflate(R.layout.gridview_family_seniority, null);
                gridView.setAdapter(new SeniorityGridViewAdapter(list));
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (list.get(position).getNickname() == null) {
                            startActivity(new Intent(mContext, FamilyAddMemberActivity.class));
                        } else {

                            Intent intent = new Intent(mContext, FamilyMemberInfoActivity.class);
                            intent.putExtra("clanRole", mFamilyIndex.getClanRole());
                            intent.putExtra("memberInfo", list.get(position));
                            startActivity(intent);
                        }
                    }
                });
                gridViews.add(gridView);
            }
        }
    }

    private void initExpandGridViews() {
        //展开的GridView
        final List<FamilyIndex.Member> expandList = new ArrayList<>();
//        expandList.addAll(members);
        expandList.add(new FamilyIndex.Member());

        GridView expandGridView = (GridView) mLayoutInflater.inflate(R.layout.gridview_family_seniority, null);
        expandGridView.setAdapter(new SeniorityGridViewAdapter(expandList));
        expandGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (expandList.get(position).getNickname() == null) {
                    startActivity(new Intent(mContext, FamilyAddMemberActivity.class));
                } else {
                    startActivity(new Intent(mContext, FamilyMemberInfoActivity.class));
                }
            }
        });

//        if (position == 0) {
//            mElderExpandGridViews.add(expandGridView);
//        }

    }


    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    private void initContactsInfo() {

        Map<String, String> params = new HashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        String contactsStr = readAllContacts();
        params.put("phones", contactsStr);
        APIClient.post(mContext, Constants.URL.TXL_MATCH_CLAN, params, new BaseVolleyListener(mContext) {
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
                        Log.e(TAG, "parsRespone:" + json);

                        Type type = new TypeToken<ArrayList<MatchContactsFamily>>() {
                        }.getType();
                        mMactchFamilys = GsonUtils.parseJSONArray(json, type);
                        if (mMactchFamilys == null || mMactchFamilys.size() == 0) {//TODO
                            mVpContactsFamily.setVisibility(View.GONE);

                        } else {
                            mVpContactsFamily.setVisibility(View.VISIBLE);
                            initMatchUserInfo();
//                            mVpContactsFamily.setAdapter();
                        }
                    }
                });
            }
        });
    }


    //初始化配置到的家族信息
    private void initMatchUserInfo() {

        int matchFamilySize = mMactchFamilys.size();

        int vpSize;

        if (matchFamilySize % VP_COUNT > 0) {
            vpSize = matchFamilySize / MATCH_FAMILY_PAGE_COUNT + 1;
        } else {
            vpSize = matchFamilySize / MATCH_FAMILY_PAGE_COUNT;
        }


        //将匹配的信息对象 分页
        for (int i = 0; i < vpSize; i++) {
            List<MatchContactsFamily> list = new ArrayList<>();

            for (int j = 0; j < MATCH_FAMILY_PAGE_COUNT; j++) {
                if ((i * MATCH_FAMILY_PAGE_COUNT + j) < mMactchFamilys.size()) {
                    list.add(mMactchFamilys.get(i * MATCH_FAMILY_PAGE_COUNT + j));
                }
            }

            GridView gridView = (GridView) mLayoutInflater.inflate(R.layout.gridview_family_match_contacts, null);
            final MatchContactsFamilyAdapter adapter = new MatchContactsFamilyAdapter(list);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    long clanId = adapter.getItem(position).getClanId();
                    Intent intent = new Intent(mContext, FamilyInfoActivity.class);
//                    if(mFamilyIndex.getClanRole()== ClanRoleEnum.NORMAL.getCode()) {
                    intent.putExtra("type", "join");
                    intent.putExtra("clanRole", mFamilyIndex.getClanRole());
//                    }else{
//                        intent.putExtra("type", "");
//                    }

                    intent.putExtra("clanId", clanId + "");
                    startActivity(intent);
                }
            });

            mGvMatchList.add(gridView);
        }
        mContactsFamilyAdapter.notifyDataSetChanged();
        //int vpMatchSize = familyBaseInfos.size()/4+;
    }


    /*
     * 读取联系人的信息
     */
    public String readAllContacts() {
        Cursor cursor = this.getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
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
            Cursor phones = this.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
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
        return sb.toString();
    }


    private class SeniorityAdapter extends PagerAdapter {
        List<GridView> gridViews;

        public SeniorityAdapter(List<GridView> gridViews) {
            this.gridViews = gridViews;
        }

        @Override
        public int getCount() {
            return gridViews.size();
        }


        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(gridViews.get(position), 0);
            return gridViews.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
//            return mImageViews[position % mImageViews.length];
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(gridViews.get(position));
        }
    }

    class SeniorityGridViewAdapter extends BaseAdapter {

        List<FamilyIndex.Member> members;

        public SeniorityGridViewAdapter(List<FamilyIndex.Member> members) {
            this.members = members;
        }

        @Override
        public int getCount() {
            return members.size();
        }

        @Override
        public FamilyIndex.Member getItem(int i) {
            return members.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = mLayoutInflater.inflate(R.layout.gridview_item_family_seniority, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            //如果是最后项，显示添加按钮
            FamilyIndex.Member member = members.get(i);
            if (member.getNickname() != null) {
                holder.ivAddFamilyMember.setVisibility(View.GONE);
                holder.llFamilyMember.setVisibility(View.VISIBLE);
                holder.ivAddFamilyMember.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //跳转到添加族成员的佛罗里达面
                        Intent intent = new Intent(mContext, FamilyAddMemberActivity.class);
                        startActivity(intent);
                    }
                });
                //设置图片
                String xfStr = member.getUserid() + "";
                xfStr = xfStr.substring(xfStr.length() - 1, xfStr.length());
                UILUtils.displayImage(mApplication.getImgUri() + "headImage/" + xfStr + "/" + member.getHeadImage(), holder.ivMemberImg);
                holder.tvMemberName.setText(member.getNickname());
                holder.tvMemberNote.setText("(" + member.getCwNote() + ")");
                return view;
            } else {
                holder.ivAddFamilyMember.setVisibility(View.VISIBLE);
                holder.llFamilyMember.setVisibility(View.GONE);
            }
            //是否显示礼物消息

            return view;
        }

        class ViewHolder {
            @Bind(R.id.iv_member_img)
            ImageView ivMemberImg;
            @Bind(R.id.tv_msg_count)
            TextView tvMsgCount;
            @Bind(R.id.iv_member_gifts)
            ImageView ivMemberGifts;
            @Bind(R.id.tv_member_name)
            TextView tvMemberName;
            @Bind(R.id.tv_member_note)
            TextView tvMemberNote;

            @Bind(R.id.iv_add_family_member)
            ImageView ivAddFamilyMember;
            @Bind(R.id.ll_family_member)
            LinearLayout llFamilyMember;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    //匹配通讯录的家族ViewPager Adapter
    class ContactsFamilyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mGvMatchList.size();
        }

        public ContactsFamilyAdapter() {
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mGvMatchList.get(position), 0);
            return mGvMatchList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return true;
        }
    }

    class MatchContactsFamilyAdapter extends BaseAdapter {
        List<MatchContactsFamily> list;

        MatchContactsFamilyAdapter(List<MatchContactsFamily> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MatchContactsFamily getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_match_family, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MatchContactsFamily matchFamily = list.get(position);

            UILUtils.displayImage(mApplication.getImgUri() + "clanLogo/" + matchFamily.getClanLogo(), holder.ivFamilyImg);
            holder.tvFamilyName.setText(matchFamily.getClanName() + "家族");
            String type = "已加入";
            if (matchFamily.getTy() == 1) {
                type = "创建";
            }
            holder.tvFamilyMatch.setText(mContactsMap.get(matchFamily.getMobile()) + type);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_family_img)
            ImageView ivFamilyImg;
            @Bind(R.id.tv_family_name)
            TextView tvFamilyName;
            @Bind(R.id.tv_family_match)
            TextView tvFamilyMatch;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHANGE_FAMILY) {
                int clanId = data.getIntExtra("clanId", mDefaultFamilyBaseInfo.getClanId());
                initFamilyIdexData(clanId);
            }
        }
    }
}
