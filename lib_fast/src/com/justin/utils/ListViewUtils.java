package com.justin.utils;/**
 * Created by Administrator on 2016/9/8.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * 作者：justin on 2016/9/8 10:05
 */
public class ListViewUtils {
    /**
     * 当GridView外层有ScrollView时，需要动态设置GridView高度
     *
     * @param gridview
     */
    public static void setListViewHeightBasedOnChildren(GridView gridview) {
        if (gridview == null) return;
        ListAdapter listAdapter = gridview.getAdapter();
        if (listAdapter == null) return;

        int totalHeight;
        //向上取整
        int count = (int) Math.ceil(listAdapter.getCount() / 4.0);
        //获取一个子view
        View itemView = listAdapter.getView(0, null, gridview);
        //测量View的大小
        itemView.measure(0, 0);
        totalHeight = itemView.getMeasuredHeight();
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        //设置GridView的布局高度

        if(params==null){
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//也可设置成固定大小
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100,100);

            gridview.setLayoutParams(params);
        }
        params.height = totalHeight * count;
        gridview.setLayoutParams(params);
    }
}
