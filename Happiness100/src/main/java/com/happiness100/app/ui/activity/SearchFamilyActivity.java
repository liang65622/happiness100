package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/8/25.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.SearchFamily;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
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
 * 作者：justin on 2016/8/25 15:47
 */
public class SearchFamilyActivity extends BaseActivity implements BaseViewInterface {

    @Bind(R.id.edit_search_family)
    EditText editSearchFamily;
    @Bind(R.id.lv_search_family_result)
    ListView lvSearchFamilyResult;

    Activity mContext;
    @Bind(R.id.text_back)
    TextView textBack;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    private String mPage = "1";
    private List<SearchFamily> mFamilys = new ArrayList<>();
    private BaseAdapter mFamilyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_family);
        ButterKnife.bind(this);
        mContext = this;
        initView();
    }

    @Override
    public void initView() {
        textBack.setText("返回");
        titleViewTitle.setText("搜索家族");
        editSearchFamily.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editSearchFamily.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchFamily(v.getText().toString());

                return false;
            }
        });
        mFamilyAdapter = new FamilyAdapter();
        lvSearchFamilyResult.setAdapter(mFamilyAdapter);
        lvSearchFamilyResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, FamilyInfoActivity.class);
                intent.putExtra("clanId", mFamilys.get(position).getClanId() + "");
                startActivity(intent);
            }
        });
    }

    private void searchFamily(String searchStr) {

        Map<String, String> params = new HashMap<>();
        params.put("clan_name", searchStr);
        params.put("cur_page", mPage);
        APIClient.postWithSessionId(mContext, Constants.URL.TXT_SEARCH_CLAN, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        if (json.isEmpty()) {
                            ToastUtils.shortToast(mContext, "找不到相关信息");
                        } else {
                            Type type = new TypeToken<ArrayList<SearchFamily>>() {
                            }.getType();
                            mFamilys = GsonUtils.parseJSONArray(json, type);
                            mFamilyAdapter.notifyDataSetChanged();
                            if (mFamilys.size() > 0) {
                                lvSearchFamilyResult.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void initData() {
//        mFailys = null;
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        setResult(RESULT_CANCELED);
        mContext.finish();
    }


    class FamilyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mFamilys.size();
        }

        @Override
        public SearchFamily getItem(int i) {
            return mFamilys.get(i);
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
            SearchFamily family = mFamilys.get(i);
            UILUtils.displayImageWithRounder(mApplication.getImgUri() + "clanLogo/" + family.getClanLogo(), holder.ivFamilyImg, 60);
            holder.tvFamilyName.setText("家族：" + family.getName());
            holder.tvFamilyRemark.setText("公告：" + family.getGongGao());

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

    private class Family {

    }
}
