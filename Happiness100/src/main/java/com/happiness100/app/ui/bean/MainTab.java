package com.happiness100.app.ui.bean;/**
 * Created by Administrator on 2016/8/3.
 */

import com.happiness100.app.R;
import com.happiness100.app.ui.fragment.DiscoverFragment;
import com.happiness100.app.ui.fragment.FamilyFragment;
import com.happiness100.app.ui.fragment.HealthFragment;
import com.happiness100.app.ui.fragment.IMFragment;
import com.happiness100.app.ui.fragment.MineFragment;

/**
 * 作者：justin on 2016/8/3 17:48
 */
public enum MainTab {
//    DAILY(0, R.string.main_tag_health, R.drawable.tab_icon_daily,
//            HealthFragment.class),
//
//
//    MESSAGE(1, R.string.main_tab_message, R.drawable.tab_icon_message,
//            IMFragment.class),
//
//
//    FAMILY(2, R.string.main_tab_family, R.drawable.tab_icon_new,
//            FamilyFragment.class),
//    SERVICE(3, R.string.main_tab_service, R.drawable.tab_icon_service,
//            DiscoverFragment.class),
//
//    ME(4, R.string.main_tab_my_page, R.drawable.tab_icon_my_page,
//            MineFragment.class);
    DAILY(0, R.string.empty, R.drawable.tab_icon_daily,
          HealthFragment.class),


    MESSAGE(1, R.string.empty, R.drawable.tab_icon_message,
            IMFragment.class),


    FAMILY(2, R.string.empty, R.drawable.tab_icon_new,
           FamilyFragment.class),
    SERVICE(3, R.string.empty, R.drawable.tab_icon_service,
            DiscoverFragment.class),

    ME(4, R.string.main_tab_my_page, R.drawable.tab_icon_my_page,
       MineFragment.class);
    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private MainTab(int idx, int resName, int resIcon, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIcon = resIcon;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
