package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/8/24.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.happiness100.app.R;
import com.happiness100.app.ui.activity.AddFriendsActivity;
import com.happiness100.app.ui.activity.CaptureActivity;
import com.happiness100.app.ui.activity.SponsorGroupChatActivity;
import com.happiness100.app.ui.activity.UserInfo_codeview_Activity;

/**
 * 作者：justin on 2016/8/24 17:50
 */
public class ConversationListPopMenu extends PopupWindow {

    RelativeLayout mAddfriendsView;
    RelativeLayout mQunliaoView;
    RelativeLayout mCodeAddView;
    RelativeLayout mMycodeView;
    private View conentView;
    private Activity context;

    @SuppressLint("InflateParams")
    public ConversationListPopMenu(final Activity context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.menu_conversationliast_pop, null);
        this.context = context;
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
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


        mAddfriendsView = (RelativeLayout)conentView.findViewById(R.id.addfriendsView);
        mQunliaoView = (RelativeLayout)conentView.findViewById(R.id.qunliaoView);
        mCodeAddView = (RelativeLayout)conentView.findViewById(R.id.codeAddView);
        mMycodeView = (RelativeLayout)conentView.findViewById(R.id.mycodeView);


        mAddfriendsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, AddFriendsActivity.class);
                context.startActivity(intent);
                ConversationListPopMenu.this.dismiss();
            }
        });
        mQunliaoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GroupChatIntent = new Intent(context,SponsorGroupChatActivity.class);
                context.startActivity(GroupChatIntent);
                ConversationListPopMenu.this.dismiss();
            }
        });
        mCodeAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(context,CaptureActivity.class);
                context.startActivityForResult(openCameraIntent, CaptureActivity.Code_QRcode);
                ConversationListPopMenu.this.dismiss();
            }
        });
        mMycodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, UserInfo_codeview_Activity.class);
                context.startActivity(intent);
                ConversationListPopMenu.this.dismiss();
            }
        });

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
    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent,int x,int y) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, x, y);
        } else {
            this.dismiss();
        }
    }
}

