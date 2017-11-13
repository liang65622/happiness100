package com.happiness100.app.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.happiness100.app.R;
import com.happiness100.app.manager.GroupImageManager;
import com.happiness100.app.model.Group;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.pulltorefresh.PullToRefreshBase;
import com.happiness100.app.ui.widget.pulltorefresh.PullToRefreshListView;
import com.happiness100.app.ui.widget.wheel.adapters.BaseAdapter;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.UILUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by AMing on 15/11/23.
 * Company RongCloud
 */
public class MyGroupActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {

    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    private PullToRefreshListView mListView;

    private MyGroupAdapter mAdapter;

    private TextView mTextView;

    private List<Group> mGroupList;
    @Bind(R.id.text_back)
    TextView mTextBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mygroup_activity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mListView = (PullToRefreshListView) findViewById(R.id.mygrouplistview);
        mTextView = (TextView) findViewById(R.id.nomygroup);
        mTitleViewTitle.setText("选择一个群");
        mGroupList = new ArrayList<Group>();
        mAdapter = new MyGroupAdapter(mContext, mGroupList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Group item = (Group) mAdapter.getItem(position-1);
                startDiscussionChatForResult(item.getDiscusId(), item.getDiscusName());
                finish();
            }
        });
        mTextView.setVisibility(View.VISIBLE);
        //TODO:拉取群组列表
        getGroupListByServer();
    }

    void getGroupListByServer() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        APIClient.post(mContext, Constants.URL.GET_GROUP_LIST, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        Gson gson = new Gson();
                        mGroupList = new ArrayList<Group>();
                        String[][] res = gson.fromJson(json, String[][].class);
                        for (int i = 0; i < res.length; ++i) {

                            Group gp = new Group();
                            gp.setDiscusId(res[i][0]);
                            gp.setDiscusName(res[i][1]);
                            gp.setExt(res[i][2]);
                            mGroupList.add(gp);
                            GroupImageManager.getInstance().update(res[i][0],res[i][2]);

                        }

                        if (mGroupList.size() > 0) {
                            mAdapter.updateList(mGroupList);
                            mTextView.setVisibility(View.GONE);
                        } else {
                            mTextView.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {//可见
        super.onResume();
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        finish();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    class MyGroupAdapter extends BaseAdapter  {

        private ViewHolder holder;

        List<Group> list;

        public MyGroupAdapter(Context context, List<Group> data) {
            super(context, data);
            list = data;
        }

        public void updateList(List<Group> data) {
            list = data;
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
            return  position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.mygroup_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Group bean = list.get(position);
            holder.mMyGroupName.setText(bean.getDiscusName());
            mTitleViewTitle.setVisibility(View.GONE);
            final String url = mApplication.getGroupImage(bean.getExt());
            UILUtils.displayImage(url,holder.mMyGroupHead);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.my_group_head)
            ImageView mMyGroupHead;
            @Bind(R.id.my_group_name)
            TextView mMyGroupName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
