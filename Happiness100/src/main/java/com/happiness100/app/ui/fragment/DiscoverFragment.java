package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/9/17.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.happiness100.app.R;
import com.justin.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/9/17 21:23
 */
public class DiscoverFragment extends BaseFragment {
    @Bind(R.id.title_view_back)
    LinearLayout mTitleViewBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.itemGridView)
    GridView mItemGridView;
    @Bind(R.id.itemGridView2)
    GridView mItemGridView2;
    @Bind(R.id.Item_ContentList)
    LinearLayout mItemContentList;
    private View rootView;
    private List<Map<String, Object>> data_list;
    private List<Map<String, Object>> data_list2;
    private SimpleAdapter sim_adapter;
    private SimpleAdapter sim_adapter2;
    private int[] icon = { R.drawable.icon_discover_jz, R.drawable.icon_discover_zb,
            R.drawable.icon_discover_ms, R.drawable.icon_discover_gw};
    private String[] iconName = { "家族聚会", "周边服务", "美食", "幸福商城"};
    private int[] icon2 = { R.drawable.icon_discover_lc, R.drawable.icon_discover_ax,R.drawable.icon_discover_yl,R.drawable.icon_discover_yx};
    private String[] iconName2 = {  "基金理财", "爱心捐赠","养老","游戏"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(R.layout.discover_fragment, null);
            ButterKnife.bind(this, rootView);
            initView();
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void initView() {
        mTitleViewBack.setVisibility(View.GONE);
        mTitleViewTitle.setText("发现");
        data_list = new ArrayList<Map<String, Object>>();
        data_list2 = new ArrayList<Map<String, Object>>();
        String [] from ={"view","text"};
        int [] to = {R.id.view,R.id.text};
        getData();
        getData2();
        sim_adapter = new SimpleAdapter(mContext, data_list, R.layout.discover_title_item, from, to);
        sim_adapter2 = new SimpleAdapter(mContext, data_list2, R.layout.discover_title_item, from, to);
        //配置适配器
        mItemGridView.setAdapter(sim_adapter);
        mItemGridView.setOnItemClickListener(GridItemClickListener);
        setListViewHeightBasedOnChildren(mItemGridView);
        mItemGridView2.setAdapter(sim_adapter2);
        mItemGridView2.setOnItemClickListener(GridItemClickListener2);
        setListViewHeightBasedOnChildren(mItemGridView2);
        updateContentList();
    }

    /**
     * 当GridView外层有ScrollView时，需要动态设置GridView高度
     *
     * @param gridview
     */
    protected void setListViewHeightBasedOnChildren(GridView gridview) {
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
        totalHeight = itemView.getMeasuredHeight()+10;
        ViewGroup.LayoutParams params = gridview.getLayoutParams();
        //设置GridView的布局高度
        params.height = totalHeight * count;
        gridview.setLayoutParams(params);
    }

    private void updateContentList() {

        int[] guanggao = {R.drawable.discover_gg1,R.drawable.discover_gg2,R.drawable.discover_gg3,R.drawable.discover_gg4,R.drawable.discover_gg5,R.drawable.discover_gg6};


        for (int i = 0; i < 6; ++i)
        {
            ImageView view = new ImageView(mContext);
            view.setImageResource(guanggao[i]);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i = (int)view.getTag();
                    if (i == 1)
                    {
                        Uri uri = Uri.parse("http://www.kypwe.com/");
                        Intent itUri = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(itUri);
                    }
                    else
                    {
                        Uri uri = Uri.parse("http://www.qlsme.com/wap/");
                        Intent itUri = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(itUri);
                    }
                }
            });
            mItemContentList.addView(view);

            View interval= new View(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(10,15);
            interval.setLayoutParams(params);
            mItemContentList.addView(interval);
        }

    }

    AdapterView.OnItemClickListener GridItemClickListener2  = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToastUtils.shortToast(mContext,"功能暂未开放");
        }
    };

    AdapterView.OnItemClickListener GridItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if (i == 3)
            {
                Uri uri = Uri.parse("http://www.qlsme.com/wap/");
                Intent itUri = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(itUri);
            }
            else
            {
                ToastUtils.shortToast(mContext,"功能暂未开放");
            }

        }
    };

    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("view", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }

        return data_list;
    }

    public List<Map<String, Object>> getData2(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon2.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("view", icon2[i]);
            map.put("text", iconName2[i]);
            data_list2.add(map);
        }

        return data_list2;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.title_view_right)
    public void onClick() {
    }
}
