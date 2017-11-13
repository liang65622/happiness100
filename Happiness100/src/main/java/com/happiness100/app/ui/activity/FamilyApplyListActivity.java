package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/24.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.Apply;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.YesOrNoEnum;
import com.happiness100.app.ui.widget.EmptyLayout;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.ToastUtils;
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
 * 作者：justin on 2016/8/24 17:03
 */
public class FamilyApplyListActivity extends BaseActivity implements BaseViewInterface {

    private static final int REQUEST_CODE = 1;
    private static final int USER_ID_KEY = 2;
    Activity mContext;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.iv_apply)
    ListView ivApply;
    @Bind(R.id.error_layout)
    EmptyLayout errorLayout;
    private String mClanId;
    private List<Apply> mApplys = new ArrayList<>();
    private ApplyAdapter mAdapter;
    private Apply mApply;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_applylist);
        ButterKnife.bind(this);
        mContext = this;
        mClanId = mApplication.getUser().getDefaultClanId();
        initView();
        initData();
    }

    @Override
    public void initView() {
        mAdapter = new ApplyAdapter();
        ivApply.setAdapter(mAdapter);
        titleViewTitle.setText("申请列表");
    }

    @Override
    public void initData() {

        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_APP_LIST, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        Type type = new TypeToken<ArrayList<Apply>>() {
                        }.getType();
                        mApplys = GsonUtils.parseJSONArray(json, type);

                        List<Apply> localApplys = getLocalApplys();
                        mApplys.addAll(localApplys);
                        filterApplys(localApplys);
                        mAdapter.notifyDataSetChanged();

                        if (mApplys.size() == 0) {
                            errorLayout.setErrorType(EmptyLayout.NODATA);
                        }
                    }
                });
            }
        });
    }

    private void filterApplys(List<Apply> localApplys) {
//        for ()
    }

    public List<Apply> getLocalApplys() {
        List<Apply> applys = new Select().from(Apply.class).execute();
        return applys;
    }


    class ApplyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mApplys.size();
        }

        @Override
        public Apply getItem(int i) {
            return mApplys.get(i);
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
                view = inflater.inflate(R.layout.lv_item_apply, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Apply apply = mApplys.get(i);
            String xfStr = apply.getUserId() + "";
            xfStr = xfStr.substring(xfStr.length() - 1, xfStr.length());
            UILUtils.displayImage(mApplication.getImgUri() + "headImage/" + xfStr + "/" + apply.getHeadImage(), holder.ivUserImg);

            holder.tvUserName.setText(apply.getNickname());
            holder.tvApplyMsg.setText(apply.getNote());

            if (apply.getIsAgree() != 0) {
                holder.llControll.setVisibility(View.GONE);
                holder.tvRefuse.setVisibility(View.VISIBLE);
                if (apply.getIsAgree() == YesOrNoEnum.YES.getCode()) {
                    holder.tvRefuse.setText("已接受");
                } else {
                    holder.tvRefuse.setText("已拒绝");
                }
            } else {
                holder.llControll.setVisibility(View.VISIBLE);
                holder.tvRefuse.setVisibility(View.GONE);
                holder.btnAgree.setTag(apply);
                holder.btnRefuse.setTag(apply);
            }
//            new Select().from(Family.class)
            return view;
        }

        class ViewHolder {
            @Bind(R.id.iv_user_img)
            ImageView ivUserImg;
            @Bind(R.id.tv_user_name)
            TextView tvUserName;
            @Bind(R.id.tv_apply_msg)
            TextView tvApplyMsg;
            @Bind(R.id.tv_time)
            TextView tvTime;
            @Bind(R.id.btn_agree)
            Button btnAgree;
            @Bind(R.id.btn_refuse)
            Button btnRefuse;
            @Bind(R.id.ll_controll)
            LinearLayout llControll;
            @Bind(R.id.tv_refuse)
            Button tvRefuse;

            @OnClick({R.id.btn_agree, R.id.btn_refuse})
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btn_agree:
                        Intent intent = new Intent(mContext, CompleteMemberInfoActivity.class);
                        mApply = (Apply) view.getTag();
                        intent.putExtra("apply", mApply);
                        startActivityForResult(intent, REQUEST_CODE);
//                    agree()
                        break;
                    case R.id.btn_refuse:
                        refuse((Apply) view.getTag());
                        break;
                }
            }

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }


    }

    private void refuse(final Apply apply) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", apply.getUserId() + "");
        params.put("clan_id", mClanId);
        APIClient.postWithSessionId(mContext, Constants.URL.CLAN_APP_DEFUSED, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        ToastUtils.shortToast(mContext, "操作成功！");
                        apply.setIsAgree(YesOrNoEnum.NO.getCode());
                        apply.save();
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    @OnClick(R.id.title_view_back)
    public void onClick() {
        mContext.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    mApply.setIsAgree(YesOrNoEnum.YES.getCode());
                    mApply.save();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
