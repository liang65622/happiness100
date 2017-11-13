package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/11.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.model.AddressData;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenu;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenuCreator;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenuItem;
import com.happiness100.app.ui.widget.swipemenulistview.SwipeMenuListView;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：jiangsheng on 2016/8/11 17:20
 */

public class UserInfo_address_Activity extends BaseActivity {
    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.address_list)
    SwipeMenuListView mAddressList;
    LayoutInflater mInflater;
    List<AddressData.AddresssBean> dataList = new ArrayList<AddressData.AddresssBean>();
    AddresslistAdapter mAdapter;
    final int Code_AddAddress = 1;
    final int Code_ModifyAddress = 2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo_address);
        ButterKnife.bind(this);
        mInflater = getLayoutInflater();
        initView();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    void initListView()
    {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {


                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF,
                        0x00, 0x00)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setTitle("删除");
                deleteItem.setTitleColor(getResources().getColor(R.color.white));
                deleteItem.setTitleSize(20);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        mAddressList.setMenuCreator(creator);
        mAdapter = new AddresslistAdapter(dataList);
        mAddressList.setAdapter(mAdapter);


        // step 2. listener item click event
        mAddressList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                AddressData.AddresssBean bean = dataList.get(position);
                deleteAddressByServer(bean.getAddressId()+"");
                return false;
            }
        });
    }

    private void initView() {
        //TODO:初始化,拉取数据
        mTitleViewTitle.setText("我的地址");
        mTextBack.setText("返回");
        getAddressByServer(true);
    }

    void deleteAddressByServer(String addressId)
    {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("address_id", addressId);
        APIClient.postWithSessionId(mContext, Constants.URL.UPDATE_DELETE_ADDRESS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        getAddressByServer(false);
                    }
                });
            }
        });
    }

    void getAddressByServer(final boolean init) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        APIClient.post(mContext, Constants.URL.UPDATE_QUERY_ADDRESS, params, new BaseVolleyListener(mContext,new LoadDialog(mContext,true,"")) {
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
                        dataList.clear();
                        AddressData addressData = GsonUtils.parseJSON(json, AddressData.class);
                        if (addressData != null && addressData.getAddresss() != null)
                        {
                            dataList = addressData.getAddresss();
                        }
                        if (init)
                        {
                            initListView();
                        }
                        else
                        {
                            mAdapter.updateListView(dataList);
                        }
                    }
                });
            }
        });
    }

    public class AddresslistAdapter extends BaseAdapter {

        List<AddressData.AddresssBean> list;
        public AddresslistAdapter(List<AddressData.AddresssBean> list)
        {
            this.list = list;
        }

        public void updateListView(List<AddressData.AddresssBean> list)
        {
            if (list == null)
                list = new ArrayList<AddressData.AddresssBean>();
            else
                this.list = list;
            notifyDataSetChanged();
        }

        public int getCount() {
            return dataList.size() + 1;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder holder;
            if (convertView == null) {
                view =  mInflater.inflate(R.layout.item_userinfo_address, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                view =  convertView;
                holder = (ViewHolder) view.getTag();
            }

            holder.mModifyAddressView.setVisibility(View.GONE);
            holder.mAddAddressView.setVisibility(View.GONE);
            if (position == dataList.size()) {
                holder.mAddAddressView.setVisibility(View.VISIBLE);
                holder.mAddAddressView.setOnClickListener(Listener_add);
            } else {
                holder.mModifyAddressView.setVisibility(View.VISIBLE);
                holder.mModifyAddressViewTextAddress.setText(dataList.get(position).getZone()+dataList.get(position).getAddress());
                holder.mModifyAddressViewTextName.setText(dataList.get(position).getReceiver());
                holder.mModifyAddressViewGo.setTag(position);
                holder.mModifyAddressViewGo.setOnClickListener(Listener_modify);
            }
            return view;
        }
         class ViewHolder {
            @Bind(R.id.modifyAddress_view_text_name)
            TextView mModifyAddressViewTextName;
            @Bind(R.id.modifyAddress_view_text_address)
            TextView mModifyAddressViewTextAddress;
            @Bind(R.id.modifyAddress_view_go)
            ImageView mModifyAddressViewGo;
            @Bind(R.id.modifyAddress_view)
            RelativeLayout mModifyAddressView;
            @Bind(R.id.addAddress_view)
            RelativeLayout mAddAddressView;
            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    View.OnClickListener Listener_modify = new View.OnClickListener() {
        public void onClick(View var1) {
            int tag = (int) var1.getTag();
            Intent intent = new Intent();
            AddressData.AddresssBean data = dataList.get(tag);
            intent.putExtra("addressId", ""+data.getAddressId());
            intent.putExtra("address", data.getZone());
            intent.putExtra("DetailedAddress", data.getAddress());
            intent.putExtra("Name", data.getReceiver());
            intent.putExtra("Mobile", data.getReceiverMobile());
            intent.putExtra("AddressCode", data.getPostCode());
            intent.setClass(UserInfo_address_Activity.this, UserInfo_newAddress_Activity.class);
            startActivityForResult(intent,Code_AddAddress);
        }
    };

    View.OnClickListener Listener_add = new View.OnClickListener() {
        public void onClick(View var1) {
            Intent intent = new Intent();
            intent.setClass(UserInfo_address_Activity.this, UserInfo_newAddress_Activity.class);
            startActivityForResult(intent,Code_ModifyAddress);
        }
    };

    @OnClick(R.id.title_view_back)
    public void onClick() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            switch (requestCode) {
                case Code_AddAddress:
                case Code_ModifyAddress:
                    String json = data.getStringExtra("json");
                    if (json == null ||json.isEmpty())
                    {
                        return;
                    }
                    dataList.clear();
                    AddressData addressData = GsonUtils.parseJSON(json, AddressData.class);
                    if (addressData != null) {
                        dataList = addressData.getAddresss();
                    }
                    mAdapter.updateListView(dataList);
                    break;
            }
        }

    }
}
