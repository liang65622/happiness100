package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/31.
 */

import android.content.Intent;
import android.os.Bundle;
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
import com.happiness100.app.model.FamilyRank;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.widget.LoadDialog;
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
 * 作者：justin on 2016/8/31 09:31
 */
public class FamilyRankActivity extends BaseActivity implements BaseViewInterface {

    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.lv_family_rank)
    ListView lvFamilyRank;
    private List<FamilyRank> mFamilyRanks
            = new ArrayList<>();
    private FamilyRankAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_rank);
        ButterKnife.bind(this);
        mContext = this;
        initView();
        initData();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("幸福排行");
        mAdapter = new FamilyRankAdapter();
        lvFamilyRank.setAdapter(mAdapter);
        lvFamilyRank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(mContext, FamilyInfoActivity.class);
                intent.putExtra("type", "read");
                intent.putExtra("clanId", mFamilyRanks.get(position).getClanId() + "");
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

        Map<String, String> params = new HashMap<>();
        params.put("rank_type", 1 + "");


        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_RANK, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        Type type = new TypeToken<ArrayList<FamilyRank>>() {
                        }.getType();
                        mFamilyRanks = GsonUtils.parseJSONArray(json, type);
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }


    class FamilyRankAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFamilyRanks.size();
        }

        @Override
        public FamilyRank getItem(int i) {
            return mFamilyRanks.get(i);
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
                view = inflater.inflate(R.layout.lv_item_family_rank, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            FamilyRank familyRank = mFamilyRanks.get(i);

            String url = mApplication.getImgUri() + "clanLogo/" + familyRank.getClanLogo();
            UILUtils.displayImageWithRounder(url, holder.ivFamilyImg, 60);
            holder.tvRankNum.setText(++i + "");
            holder.tvFamilyName.setText(familyRank.getClanName());
            holder.tvCreater.setText("创建者：" + familyRank.getCreater());
            holder.tvFamilyScore.setText("积分：\n" + familyRank.getXfVal());
            if (i == 1) {
                holder.ivRankHight.setVisibility(View.VISIBLE);
                holder.ivRankHight.setImageDrawable(getResources().getDrawable(R.drawable.iv_rank_no1));
                holder.tvRankNum.setVisibility(View.GONE);
            } else if (i == 2) {
                holder.ivRankHight.setVisibility(View.VISIBLE);
                holder.ivRankHight.setImageDrawable(getResources().getDrawable(R.drawable.iv_rank_no2));
                holder.tvRankNum.setVisibility(View.VISIBLE);
            } else if (i == 3) {
                holder.ivRankHight.setVisibility(View.VISIBLE);
                holder.ivRankHight.setImageDrawable(getResources().getDrawable(R.drawable.iv_rank_no3));
                holder.tvRankNum.setVisibility(View.VISIBLE);
            } else {
                holder.tvRankNum.setVisibility(View.VISIBLE);
                holder.ivRankHight.setVisibility(View.GONE);
            }

            return view;
        }


        class ViewHolder {
            @Bind(R.id.tv_rank_num)
            TextView tvRankNum;
            @Bind(R.id.iv_family_img)
            ImageView ivFamilyImg;
            @Bind(R.id.tv_family_name)
            TextView tvFamilyName;
            @Bind(R.id.tv_creater)
            TextView tvCreater;
            @Bind(R.id.tv_family_score)
            TextView tvFamilyScore;

            @Bind(R.id.iv_rank_hight)
            ImageView ivRankHight;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        mContext.finish();
    }
}
