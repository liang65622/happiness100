package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/26.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.manager.FriendsManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.CircleImageView;
import com.happiness100.app.ui.widget.ClearWriteEditText;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.wheel.adapters.BaseAdapter;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：jiangsheng on 2016/8/26 15:38
 */
public class AddFriendSearchActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ClearWriteEditText searchEdit;
    List<MyListData> mListDatas;
    private ListView refreshlistview;
    private MySearchListAdapter listAdapter;

    //private SearchEmailResponse mailRes;
    //private SearchUserNameResponse userNameRes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_search);
        initView();
    }

    public class MyListData {
        int dataType;
        String content;

        void setDataType(int dataType) {
            this.dataType = dataType;
        }

        int getDataType() {
            return dataType;
        }

        void setContent(String content) {
            this.content = content;
        }

        String getContent() {
            return content;
        }

        public MyListData(int dataType, String content) {
            this.dataType = dataType;
            this.content = content;
        }

        public MyListData() {
            this.dataType = dataType;
            this.content = content;
        }
    }


    public class MySearchListAdapter extends BaseAdapter {

        @Bind(R.id.search_img_head)
        CircleImageView mSearchImgHead;
        @Bind(R.id.title)
        TextView mTitle;
        @Bind(R.id.content)
        TextView mContent;
        private List<MyListData> list;

        public MySearchListAdapter(Context context,List<MyListData> list) {
            super(context);
            this.mContext = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public void updateList(List<MyListData> list)
        {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.searchlist_item_layout, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mNouserItem.setVisibility(View.GONE);
            holder.mSelectItem.setVisibility(View.GONE);
            MyListData data = list.get(position);
            holder.mSelectItem.setTag(data);
            if (data.getDataType() == 0)
            {
                holder.mSelectItem.setVisibility(View.VISIBLE);
                holder.mContent.setText(data.getContent());
            }
            else
            {
                holder.mNouserItem.setVisibility(View.VISIBLE);
            }


            return convertView;
        }


         class ViewHolder {
            @Bind(R.id.search_img_head)
            CircleImageView mSearchImgHead;
            @Bind(R.id.title)
            TextView mTitle;
            @Bind(R.id.content)
            TextView mContent;
            @Bind(R.id.select_item)
            RelativeLayout mSelectItem;
            @Bind(R.id.nouser_item)
            LinearLayout mNouserItem;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }


    void updateListView(int type) {
        mListDatas.clear();
        mListDatas.add(new MyListData(type,searchEdit.getText().toString()));
        listAdapter.updateList(mListDatas);
    }

    private void initView() {
        mListDatas = new ArrayList<MyListData>();
        View text = findViewById(R.id.search_commit);
        text.setOnClickListener(this);
        refreshlistview = (ListView) findViewById(R.id.refreshlistview);
        searchEdit = (ClearWriteEditText) findViewById(R.id.search_friend);
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                updateListView(0);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    return true;
                }
                return false;
            }
        });

        refreshlistview.setOnItemClickListener(this);
        listAdapter = new MySearchListAdapter(this,mListDatas);
        refreshlistview.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_commit:
                finish();
                break;
        }
    }


    void postSearchFriend(String searchStr) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("search_word", searchStr);
        APIClient.postWithSessionId(mContext, Constants.URL.SEARCH_FRIENDS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        refreshlistview.setVisibility(View.VISIBLE);
                        if (json != null && !json.isEmpty()) {
                            Friend friend = GsonUtils.parseJSON(json, Friend.class);

                            if (friend == null) {
                                updateListView(1);
                                return;
                            }

                            if (friend.getXf() == mApplication.getUser().getXf()) {
                                ToastUtils.shortToast(mContext, "不能添加自己到通讯录");
                                updateListView(0);
                                return;
                            }
                            Friend result = FriendsManager.getInstance().findFriend(friend.getXf() + "");
                            Intent friendDetailIntent = new Intent(AddFriendSearchActivity.this, FriendDetailActivity.class);
                            friendDetailIntent.putExtra("friend", friend);
                            startActivityForResult(friendDetailIntent, Constants.ActivityIntentIndex.FriendDetailActivityIndex);
                            finish();
                        }
                        else
                        {
                            updateListView(1);
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyListData data = (MyListData)(view.findViewById(R.id.select_item)).getTag();
        if (data.getDataType() == 0)
        {
            postSearchFriend(data.getContent());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.ActivityIntentIndex.FriendDetailActivityIndex:
                    int code = data.getIntExtra("code", Constants.FriendHandle.None);
                    if (code == Constants.FriendHandle.Delete) {
                        Intent intent1 = new Intent();
                        intent1.putExtra("code", Constants.FriendHandle.Delete);
                        setResult(RESULT_OK, intent1);
                        finish();
                    }
                    break;
            }
        }
    }
}
