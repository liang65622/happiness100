package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/30.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyBaseInfo;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.UILUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/8/30 10:21
 */
public class FamilyListActivity extends BaseActivity implements BaseViewInterface {
    private static final String TAG = "FamilyListActivity";
    Activity mContext;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.lv_family)
    ListView lvFamily;
    private FamilyAdapter mFamilyAdapter;
    private List<FamilyBaseInfo> mFamilyBaseInfos = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_list);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        mContext.finish();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("家族列表");

        mFamilyAdapter = new FamilyAdapter();
        lvFamily.setAdapter(mFamilyAdapter);
        lvFamily.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setDefaultFamily(position);
            }
        });
    }

    void setDefaultFamily(int position) {
        Map<String, String> params = new HashMap<>();
        final long clanId =  mFamilyBaseInfos.get(position).getClanId();
        params.put("clan_id",  mFamilyBaseInfos.get(position).getClanId() + "");
        params.put("change_clan","1");
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
                        FamilyIndex familyIndex = GsonUtils.parseJSON(json, FamilyIndex.class);
                        Intent intent = new Intent();
                        mApplication.getUser().setDefaultFamilyId(clanId);
                        intent.putExtra("familyIndex",familyIndex);
                        setResult(RESULT_OK, intent);
                        mContext.finish();

                    }
                });
            }
        });
    }

    private void requestFamilys() {

        Map<String, String> params = new HashMap<>();
        APIClient.postWithSessionId(mContext, Constants.URL.GET_CLIAN_LIST, params, new BaseVolleyListener(mContext) {
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
                        Type type = new TypeToken<ArrayList<FamilyBaseInfo>>() {
                        }.getType();
                        mFamilyBaseInfos = GsonUtils.parseJSONArray(json, type);
                        //如果家族列表为空，家族界面显示创建家族和搜索家族
                        mApplication.getUser().setFamilyBaseInfos(mFamilyBaseInfos);

                        if (mFamilyBaseInfos == null || mFamilyBaseInfos.size() == 0) {//TODO
//                            FamilyFragment familyFragment = (FamilyFragment) getSupportFragmentManager().getFragments().get(0);
//                            familyFragment.
                        }
                        mFamilyAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    class FamilyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFamilyBaseInfos.size();
        }

        @Override
        public FamilyBaseInfo getItem(int i) {
            return mFamilyBaseInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                view = inflater.inflate(R.layout.lv_item_family, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            FamilyBaseInfo family = mFamilyBaseInfos.get(i);
            String url = mApplication.getImgUri() + "clanLogo/" + family.getClanLogo();

            UILUtils.displayImage(url, holder.ivFamilyImg);
            holder.tvFamilyName.setText(family.getClanName());
            holder.tvFamilyRemark.setText(family.getGongGao());
//            new Select().from(Family.class)
            return view;
        }

        class ViewHolder {
            @Bind(R.id.iv_family_img)
            ImageView ivFamilyImg;
            @Bind(R.id.tv_family_name)
            TextView tvFamilyName;
            @Bind(R.id.tv_family_remark)
            TextView tvFamilyRemark;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    public void initData() {
        requestFamilys();
    }
}
