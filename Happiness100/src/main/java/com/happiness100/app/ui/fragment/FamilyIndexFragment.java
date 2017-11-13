package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/9/5.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.AppContext;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.FamilyDynamic;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.activity.FamilyAddMemberActivity;
import com.happiness100.app.ui.activity.FamilyDynamicActivity;
import com.happiness100.app.ui.activity.FamilyMemberInfoActivity;
import com.happiness100.app.ui.activity.FamilyRankActivity;
import com.happiness100.app.ui.activity.MainActivity;
import com.happiness100.app.ui.activity.SearchFamilyActivity;
import com.happiness100.app.ui.activity.ZuPuActivity;
import com.happiness100.app.ui.adapter.FamilyDynamicIndexAdapter;
import com.happiness100.app.ui.bean.ClanRoleEnum;
import com.happiness100.app.ui.widget.FamilyManagePopWindow;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.happiness100.app.utils.ScoreUtils;
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
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 作者：justin on 2016/9/5 10:05
 */
public class FamilyIndexFragment extends BaseFragment implements BaseViewInterface, View.OnClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int VP_COUNT = 4;
    private static final String TAG = "FamilyIndexFragment";
    private static final int MATCH_FAMILY_PAGE_COUNT = 4;
    private static final int FAMILY_MEMBER_COUNT = 4;
    public static final int REQUEST_CREATE_FAMILY = 6;
    private static final int REQUEST_UPDATE_MEMBER = 7;
    MainActivity mContext;


    private SeniorityGridViewAdapter mEdlerAdapter;
    private LayoutInflater mLayoutInflater;
    private TextView mTvFamily;
    private View mMenuView;
    private TextView mTvHappinessLevel;
    private TextView mTvDutifulLevel;
    private TextView mTvCareForLevel;
    private TextView mTvCharitableLevel;
    private FamilyIndex mFamilyIndex;
    private ListView mLvFamilyDynamic;
    private FamilyBaseInfo mDefaultFamilyBaseInfo;
    private GridView mGvEdler;
    private boolean mIsElderExpand;
    private View mRooView;
    private FamilyDynamicIndexAdapter mFamilyDynamicAdapter;
    private View mIvApplyTips;
    private View mFreshViwe;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (MainActivity) getActivity();
        mLayoutInflater = getLayoutInflater(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRooView = inflater.inflate(R.layout.fragment_family_index, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRooView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        initView();
        return mRooView;
    }

    public void updateView() {
        initFamilyView(mRooView);
        freshFamilyApplyTips();
        initData();
    }

    private void initFamilyView(View v) {
        v.findViewById(R.id.title_view_back).setVisibility(View.GONE);
        mIvApplyTips = v.findViewById(R.id.iv_apply_tips);
        mTvFamily = (TextView) v.findViewById(R.id.tv_family_name);


        //菜单选项
        mMenuView = v.findViewById(R.id.title_view_right);
        mMenuView.setOnClickListener(this);

        //排行榜图标
        View view = v.findViewById(R.id.rl_rank);
        view.setOnClickListener(this);

        //家谱图标
        View view_zupu = v.findViewById(R.id.rl_zupu);
        view_zupu.setOnClickListener(this);

        //指数
        mTvHappinessLevel = (TextView) v.findViewById(R.id.tv_happiness_level);
        mTvDutifulLevel = (TextView) v.findViewById(R.id.tv_dutiful_level);
        mTvCareForLevel = (TextView) v.findViewById(R.id.tv_care_for_level);
        mTvCharitableLevel = (TextView) v.findViewById(R.id.tv_charitable_level);


        //更多动态
        View viewMore = v.findViewById(R.id.view_more);
        viewMore.setOnClickListener(this);

        mLvFamilyDynamic = (ListView) v.findViewById(R.id.lv_family_dynamic);

        mFamilyDynamicAdapter = new FamilyDynamicIndexAdapter(mContext);
        mLvFamilyDynamic.setAdapter(mFamilyDynamicAdapter);
//        lv_family_dynamic

        //初始化动态数据
        initDynamic();

        mFreshViwe = v.findViewById(R.id.ll_top);

        //成员VIEW
        mGvEdler = (GridView) v.findViewById(R.id.gv_elder);
        mGvEdler.setOnItemClickListener(this);

        mEdlerAdapter = new SeniorityGridViewAdapter();
        mGvEdler.setAdapter(mEdlerAdapter);

        mGvEdler.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //初始化 健康主页数据
        initFamilyIdexData(mDefaultFamilyBaseInfo.getClanId());


    }

    private void initDynamic() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mDefaultFamilyBaseInfo.getClanId() + "");
        params.put("xfdt_type", "2");
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

                        Type type = new TypeToken<ArrayList<FamilyDynamic.XingFuDongTaiListBean>>() {
                        }.getType();
                        List<FamilyDynamic.XingFuDongTaiListBean> dynamics = GsonUtils.parseJSONArray(json, type);
                        if (dynamics == null || dynamics.size() == 0) {
                            mLvFamilyDynamic.setVisibility(View.GONE);
                        } else {
                            mLvFamilyDynamic.setVisibility(View.VISIBLE);
                            mFamilyDynamicAdapter.setDynamics(dynamics);
                            mFamilyDynamicAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

    private void initFamilyIdexData(int clanId) {
        mSwipeRefreshLayout.setRefreshing(true);
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", clanId + "");
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_INDEX, params, new BaseVolleyListener(mContext) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String json) {
                super.onResponse(json);
                APIClient.handleResponse(mContext, json, new ResponeInterface() {
                    @Override
                    public void parseResponse(String json) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mFamilyIndex = GsonUtils.parseJSON(json, FamilyIndex.class);

                        //保存当前家族权限
                        AppContext.getPreferences().edit().putInt("clanRole", mFamilyIndex.getCreateID()).commit();

                        mTvHappinessLevel.setText("幸福LV：" + ScoreUtils.getLv(mFamilyIndex.getVals().get(0)));
                        mTvDutifulLevel.setText("孝顺LV：" + ScoreUtils.getLv(mFamilyIndex.getVals().get(1)));
                        mTvCareForLevel.setText("关爱LV：" + ScoreUtils.getLv(mFamilyIndex.getVals().get(2)));
                        mTvCharitableLevel.setText("慈善LV：" + ScoreUtils.getLv(mFamilyIndex.getVals().get(3)));

//                        mTvElderTitle.setText("长辈（" + mFamilyIndex.getBigers().size() + "）");
                        mTvFamily.setText(mDefaultFamilyBaseInfo.getClanName() + "(" + mFamilyIndex.members.size() + ")");
//                        mFamilyIndex.getMembers().add(new FamilyIndex.Member());
                        mApplication.setFamilyIndex(mFamilyIndex);
                        initHealthMsg();
                        List<FamilyIndex.Member> members = new ArrayList<FamilyIndex.Member>(mFamilyIndex.getMembers());

                        members.add(new FamilyIndex.Member());
                        members = sortMembers(members);

                        mEdlerAdapter.setMembers(members);

                        initMemberImage();

                        setGridViewHeight(mGvEdler);

                    }
                });
            }
        });
    }


    public void setGridViewHeight(GridView gridview) {
        if (gridview == null) return;

        if (mFreshViwe == null) {
            return;
        }


        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        //设置GridView的布局高度


        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//也可设置成固定大小
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100,100);

            gridview.setLayoutParams(params);
        }
        params.height = mFreshViwe.getMeasuredHeight();
        gridview.setLayoutParams(params);
    }

    private List<FamilyIndex.Member> sortMembers(List<FamilyIndex.Member> members) {
        List<FamilyIndex.Member> result = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            FamilyIndex.Member member = members.get(i);
            if (member.getClanRole() == ClanRoleEnum.CREATER.getCode()) {
                result.add(member);
            }
        }

        for (int i = 0; i < members.size(); i++) {
            FamilyIndex.Member member = members.get(i);
            if (member.getClanRole() == ClanRoleEnum.MANAGER.getCode()) {
                result.add(member);
            }
        }


        for (int i = 0; i < members.size(); i++) {
            FamilyIndex.Member member = members.get(i);
            if (member.getClanRole() != ClanRoleEnum.CREATER.getCode() && member.getClanRole() != ClanRoleEnum.MANAGER.getCode()) {
                result.add(member);
            }
        }
        return result;
    }
    //初始化健康消息

    private void initHealthMsg() {

        String today = mFamilyIndex.getToday();
        List<String> haveHealthPhones = mFamilyIndex.getHaveHealthPhones();
        int clanId = mDefaultFamilyBaseInfo.getClanId();
        for (FamilyIndex.Member member : mFamilyIndex.members) {
            String phone = member.getPhone();
            String spKey = today + "_" + clanId + "_" + phone;
            if (haveHealthPhones.contains(phone) && isClickHealth(spKey) != 1) {
                member.setHasHealMsg(1);
                mApplication.setSpInt(spKey, 2);
            }
        }
    }

    //判断是否点击过
    private int isClickHealth(String key) {
        int hasClickHealth = mApplication.getSpInt(key);
        return hasClickHealth;
    }


    private void initMemberImage() {
        try {
            new Delete().from(FamilyIndex.Member.class).where("clanId=?", mDefaultFamilyBaseInfo.getClanId()).execute();
        } catch (Exception e) {
        }
        for (FamilyIndex.Member member : mFamilyIndex.members) {
            member.setClanId(mDefaultFamilyBaseInfo.getClanId());
            member.save();
        }
    }


    private List<FamilyIndex.Member> getHidMembers(List<FamilyIndex.Member> members) {

        final List<FamilyIndex.Member> list = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            if (i < FAMILY_MEMBER_COUNT) {
                list.add(members.get(i));
            }
        }

        return list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.view_search_family:
                startActivity(new Intent(mContext, SearchFamilyActivity.class));
                break;
            case R.id.title_view_right:
                FamilyManagePopWindow popWindow = new FamilyManagePopWindow(mContext, mFamilyIndex);
                popWindow.showPopupWindow(view);
                break;
            case R.id.view_more:
                intent = new Intent(mContext, FamilyDynamicActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_change_family:
                break;
            case R.id.iv_expand_elder:
                //展开
//                ToastUtils.shortToast(mContext, "dd");
//                if (mIsElderExpand) {
//                    mEdlerAdapter.setMembers(getHidMembers(mFamilyIndex.getMembers()));
//                    mIsElderExpand = false;
//                } else {
//                    mEdlerAdapter.setMembers(mFamilyIndex.getMembers());
//                    mIsElderExpand = true;
//                }
//
//                mEdlerAdapter.notifyDataSetChanged();
//                ListViewUtils.setListViewHeightBasedOnChildren(mGvEdler);
                break;
            case R.id.rl_rank:
                startActivity(new Intent(mContext, FamilyRankActivity.class));
                break;
            case R.id.rl_zupu:
                intent = new Intent(mContext, ZuPuActivity.class);
                intent.putExtra("clanId", mDefaultFamilyBaseInfo.getClanId());
                intent.putExtra("clanRole", mDefaultFamilyBaseInfo.getClanRole());
                startActivity(intent);
//                mContext.showToast("功能暂未开放");
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


    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<FamilyIndex.Member> members = ((SeniorityGridViewAdapter) parent.getAdapter()).getMembers();
        FamilyIndex.Member member = members.get(position);
        if (member.getPhone() == null) {
            if (mDefaultFamilyBaseInfo.getClanRole() != ClanRoleEnum.NORMAL.getCode()) {
                Intent intent = new Intent(mContext, FamilyAddMemberActivity.class);
                ArrayList<String> memberIds = new ArrayList<String>();
                for (int i = 0; i < members.size() - 1; i++) {
                    memberIds.add(members.get(i).getUserid() + "");
                }
                intent.putStringArrayListExtra("ids", memberIds);
                mApplication.setMembersIds(memberIds);
                startActivity(intent);

            }
        } else {

            //去除健康信息
            String key = mFamilyIndex.getToday() + "_" + mDefaultFamilyBaseInfo.getClanId() + "_" + member.getPhone();
            member.setHasHealMsg(0);

            if (mApplication.getSpInt(key) == 2) {
                mApplication.setSpInt(key, 1);
            }
            Intent intent = new Intent(mContext, FamilyMemberInfoActivity.class);
            intent.putExtra("clanRole", mFamilyIndex.getClanRole());
            intent.putExtra("memberInfo", members.get(position));
            startActivityForResult(intent, REQUEST_UPDATE_MEMBER);


        }
    }

    public void setFamilyIndex(FamilyIndex familyIndex) {
        mFamilyIndex = familyIndex;
    }

    @Override
    public void onRefresh() {

        initFamilyIdexData(mDefaultFamilyBaseInfo.getClanId());
    }

    class SeniorityGridViewAdapter extends BaseAdapter {

        List<FamilyIndex.Member> members = new ArrayList<>();
        private Drawable mDrawableCreater;
        private Drawable mDrawableManager;

        public SeniorityGridViewAdapter(List<FamilyIndex.Member> members) {
            this.members = members;
        }

        public SeniorityGridViewAdapter() {

            mDrawableCreater = getResources().getDrawable(R.drawable.ic_mark_creater);
            mDrawableCreater.setBounds(0, 0, mDrawableCreater.getMinimumWidth(), mDrawableCreater.getMinimumHeight());
            mDrawableManager = getResources().getDrawable(R.drawable.ic_mark_manager);
            mDrawableManager.setBounds(0, 0, mDrawableManager.getMinimumWidth(), mDrawableManager.getMinimumHeight());
        }

        Map<Long, Integer> isHasMsg = new HashMap<>();

        public List<FamilyIndex.Member> getMembers() {
            return members;
        }

        public void setMembers(List<FamilyIndex.Member> members) {

            this.members = members;
            for (int i = 0; i < members.size(); i++) {
                final FamilyIndex.Member member = members.get(i);
                final int index = i;
                final int count = members.size() - 1;
                final long userId = member.getUserid();
                RongIM.getInstance().getUnreadCount(Conversation.ConversationType.PRIVATE, userId + "", new RongIMClient.ResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        Log.e("onSuccess", "count:" + integer);
                        if (integer > 0) {
                            isHasMsg.put(userId, 1);
                        } else {
                            isHasMsg.put(userId, 0);
                        }
                        Log.e(TAG, "INDEX:" + index + "  count:" + count + " mapsize:" + isHasMsg.size());
                        if (index == (count)) {
                            SeniorityGridViewAdapter.this.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        Log.e("onError", "error:" + errorCode);
                        if (index == (count)) {
                            SeniorityGridViewAdapter.this.notifyDataSetChanged();
                        }
                    }
                });
            }
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

                if (mApplication.getImgUri() == null || mApplication.getImgUri().isEmpty()) {
//                    holder.ivMemberImg.setImageDrawable(null);
                } else {
                    UILUtils.displayCircleImage(mApplication.getImgUri() + "headImage/" + xfStr + "/" + member.getHeadImage(), holder.ivMemberImg);
                }

                if (isHasMsg.size() > 0) {
                    if (isHasMsg.get(member.getUserid()) > 0) {
                        holder.tvMsgCount.setVisibility(View.VISIBLE);
                    } else {
                        holder.tvMsgCount.setVisibility(View.GONE);
                    }

                }

                //健康推送
                if (member.getHasHealMsg() == 1) {
                    holder.ivMemberGifts.setVisibility(View.VISIBLE);
                } else {
                    holder.ivMemberGifts.setVisibility(View.GONE);
                }

//                UILUtils.displayImageWithRounder(mApplication.getImgUri() + "headImage/" + xfStr + "/" + member.getHeadImage(), holder.ivMemberImg,1);
//                UILUtils.displayImage(mApplication.getImgUri() + "headImage/" + xfStr + "/" + member.getHeadImage(), holder.ivMemberImg);
//                if(member.getCwNote()!=null){
//                    holder.tvMemberName.setText(member.getCwNote());
//                }else {
                holder.tvMemberName.setText(member.getNickname());
//                }
                if (!member.getCwNote().isEmpty()) {
                    holder.tvMemberNote.setText("(" + member.getCwNote() + ")");
                } else {
                    holder.tvMemberNote.setText("(无称谓)");
                }
//                Log.d(TAG,)
                if (member.getClanRole() == ClanRoleEnum.CREATER.getCode()) {
                    holder.tvMemberName.setCompoundDrawables(mDrawableCreater, null, null, null);

                    holder.tvMemberNote.setText("(创建者)");
                } else if (member.getClanRole() == ClanRoleEnum.MANAGER.getCode()) {
                    holder.tvMemberName.setCompoundDrawables(mDrawableManager, null, null, null);
                } else {
                    holder.tvMemberName.setCompoundDrawables(null, null, null, null);
                }
                return view;
            } else {
                holder.headView.setVisibility(View.INVISIBLE);
                if (mDefaultFamilyBaseInfo.getClanRole() != ClanRoleEnum.NORMAL.getCode()) {
                    holder.ivAddFamilyMember.setVisibility(View.VISIBLE);
                    holder.llFamilyMember.setVisibility(View.INVISIBLE);
                }
            }
            //是否显示礼物消息
            return view;
        }

        class ViewHolder {
            @Bind(R.id.iv_member_img)
            ImageView ivMemberImg;
            @Bind(R.id.tv_msg_count)
            ImageView tvMsgCount;
            @Bind(R.id.iv_member_gifts)
            ImageView ivMemberGifts;
            @Bind(R.id.tv_member_name)
            TextView tvMemberName;
            @Bind(R.id.tv_member_note)
            TextView tvMemberNote;
            @Bind(R.id.headImg)
            View headView;
            @Bind(R.id.iv_add_family_member)
            ImageView ivAddFamilyMember;
            @Bind(R.id.ll_family_member)
            LinearLayout llFamilyMember;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (resultCode == REQUEST_UPDATE_MEMBER) {
                //
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mDefaultFamilyBaseInfo = mApplication.getUser().getDefaultFamilyBaseInfo();
        if (mDefaultFamilyBaseInfo == null && mApplication.getUser().getFamilyBaseInfos() != null) {
            mDefaultFamilyBaseInfo = mApplication.getUser().getFamilyBaseInfos().get(0);
        }

        initFamilyView(mRooView);

        freshFamilyApplyTips();
        Log.e(TAG, "onResume()");
    }

    public void freshFamilyApplyTips() {
//        View MineView = mTabHost.getTabWidget().getChildAt(2);
        boolean hasApplyTips = SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.SpKey.HAS_APPLY_TIPS, false);
        if (hasApplyTips && mIvApplyTips != null) {
            mIvApplyTips.setVisibility(View.VISIBLE);
        } else {
            mIvApplyTips.setVisibility(View.GONE);
        }
    }


    BroadcastReceiver applyTips = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mApplication.getUser() == null) {
                return;
            }
            if (action.equals("com.happniess100.app.applymsg")) {
                freshFamilyApplyTips();
            }
        }
    };

}
