package com.happiness100.app.ui.adapter;/**
 * Created by Administrator on 2016/9/5.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.happiness100.app.R;
import com.happiness100.app.model.FamilyDynamic;
import com.happiness100.app.ui.activity.BaseActivity;
import com.justin.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 作者：justin on 2016/9/5 15:43
 */
public class FamilyDynamicAdapter extends BaseAdapter {

    List<FamilyDynamic.XingFuDongTaiListBean> mDynamics =  new ArrayList<>();
    BaseActivity mContext;

    public FamilyDynamicAdapter(BaseActivity context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDynamics.size();
    }

    @Override
    public FamilyDynamic.XingFuDongTaiListBean getItem(int position) {
        return mDynamics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.lv_item_family_dynamic, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        FamilyDynamic.XingFuDongTaiListBean dynamic = mDynamics.get(position);

        holder.tvMsg.setText(dynamic.getDongTai());
        holder.tvDynamicScore.setText("积分+" + dynamic.getDongTaiVal());
        holder.tvTime.setText(DateUtils.getStringDate(dynamic.getCreateTime()));

        return view;
    }

    public void setDynamics(List<FamilyDynamic.XingFuDongTaiListBean> dynamics) {
        mDynamics = dynamics;
    }

    class ViewHolder {
        @Bind(R.id.tv_msg)
        TextView tvMsg;
        @Bind(R.id.tv_dynamic_score)
        TextView tvDynamicScore;
        @Bind(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
