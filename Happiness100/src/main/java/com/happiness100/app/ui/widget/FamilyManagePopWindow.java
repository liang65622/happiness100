package com.happiness100.app.ui.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.happiness100.app.R;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.ui.activity.BaseActivity;
import com.happiness100.app.ui.activity.FamilyAddMemberActivity;
import com.happiness100.app.ui.activity.FamilyApplyListActivity;
import com.happiness100.app.ui.activity.FamilyInfoActivity;
import com.happiness100.app.ui.activity.FamilyListActivity;
import com.happiness100.app.utils.Constants;
import com.justin.utils.SharedPreferencesContext;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FamilyManagePopWindow extends PopupWindow {
    private final FamilyIndex mFamilyIndex;
    @Bind(R.id.rl_add_member)
    LinearLayout rlAddMember;
    @Bind(R.id.rl_apply_list)
    LinearLayout rlApplyList;

    @Bind(R.id.rl_change_family)
    LinearLayout rlChangeFamily;

    @Bind(R.id.rl_family_info)
    LinearLayout rlFamilyInfo;

    @Bind(R.id.iv_apply_tips)
    ImageView ivApplyTips;


    private View conentView;
    private BaseActivity context;


    @SuppressLint("InflateParams")
    public FamilyManagePopWindow(final BaseActivity context, FamilyIndex familyIndex) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.menu_family_index_pop, null);
        this.context = context;
        mFamilyIndex = familyIndex;
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        ButterKnife.bind(this, conentView);

        if (mFamilyIndex!=null&&mFamilyIndex.getClanRole() == 1) {
            rlAddMember.setVisibility(View.GONE);
            rlApplyList.setVisibility(View.GONE);
        }


        if (context.mApplication.getUser().getFamilyBaseInfos().size() < 2) {
            rlChangeFamily.setVisibility(View.GONE);
        }

        freshFamilyApplyTips();
//        RelativeLayout rlAddMember = (RelativeLayout) conentView.findViewById(R.id.rl_add_member);
//        RelativeLayout rlApplyList = (RelativeLayout) conentView.findViewById(R.id.rl_apply_list);
//        RelativeLayout rlFamilyInfo = (RelativeLayout) conentView.findViewById(R.id.rl_family_info);
//        re_addfriends.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(new Intent(context, SelectFriendsActivity.class));
//                intent.putExtra("createGroup", true);
//                context.startActivity(intent);
//                MorePopWindow.this.dismiss();
//
//            }
//
//        });
//        re_chatroom.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context, SelectFriendsActivity.class));
//                MorePopWindow.this.dismiss();
//
//            }
//
//        });
//        re_scanner.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context, SearchFriendActivity.class));
//                MorePopWindow.this.dismiss();
//            }
//        });
    }

    private void freshFamilyApplyTips() {
//        View MineView = mTabHost.getTabWidget().getChildAt(2);
        boolean hasApplyTips = SharedPreferencesContext.getInstance().getSharedPreferences().getBoolean(Constants.SpKey.HAS_APPLY_TIPS, false);
        if (hasApplyTips) {
            ivApplyTips.setVisibility(View.VISIBLE);
        } else {
            ivApplyTips.setVisibility(View.GONE);
        }
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    @OnClick({R.id.rl_add_member, R.id.rl_apply_list, R.id.rl_family_info, R.id.rl_change_family})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_add_member:
                context.startActivity(new Intent(context, FamilyAddMemberActivity.class));
                FamilyManagePopWindow.this.dismiss();
                break;
            case R.id.rl_apply_list:
                String clanId = SharedPreferencesContext.getInstance().getSharedPreferences().getString("clanId", "");
                intent = new Intent(context, FamilyApplyListActivity.class);
                SharedPreferencesContext.getInstance().getSharedPreferences().edit().putBoolean(Constants.SpKey.HAS_APPLY_TIPS,false).commit();
                intent.putExtra("clanId", clanId);
                context.startActivity(intent);
                FamilyManagePopWindow.this.dismiss();
                break;
            case R.id.rl_change_family:
                context.startActivityForResult(new Intent(context, FamilyListActivity.class), Constants.RequestCode.CHANGE_FAMILY);
                FamilyManagePopWindow.this.dismiss();
                break;
            case R.id.rl_family_info:
                intent = new Intent(context, FamilyInfoActivity.class);
                intent.putExtra("type", "index");
                intent.putExtra("clanRole", mFamilyIndex.getClanRole());
                intent.putExtra("familyIndex", mFamilyIndex);
                context.startActivityForResult(intent, Constants.RequestCode.REQUEST_SETTINGS);
                FamilyManagePopWindow.this.dismiss();
                break;
        }
    }
}
