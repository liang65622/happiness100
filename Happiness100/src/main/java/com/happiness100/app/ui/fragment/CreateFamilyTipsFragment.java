package com.happiness100.app.ui.fragment;/**
 * Created by Administrator on 2016/8/16.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.FamilyIndex;
import com.happiness100.app.model.MatchContactsFamily;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.activity.BaseActivity;
import com.happiness100.app.ui.activity.CreateFamilyActiivty;
import com.happiness100.app.ui.activity.FamilyInfoActivity;
import com.happiness100.app.ui.activity.SearchFamilyActivity;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.bean.Contacts;
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

/**
 * 作者：justin on 2016/8/16 15:36
 */
public class CreateFamilyTipsFragment extends BaseFragment implements View.OnClickListener {
    private BaseActivity mContext;
    private ViewPager mVpContactsFamily;
    private Button mBtnCreateFamily;
    private List<GridView> mGvMatchList = new ArrayList<>();
    private List<MatchContactsFamily> mMactchFamilys;//通讯录匹配家族列表
    private PagerAdapter mContactsFamilyAdapter;
    private static final int VP_COUNT = 4;
    private static final String TAG = "FamilyFragment";
    private static final int MATCH_FAMILY_PAGE_COUNT = 4;
    private static final int FAMILY_MEMBER_COUNT = 4;
    private static final int CHANGE_FAMILY = 5;
    public static final int REQUEST_CREATE_FAMILY = 6;
    private LayoutInflater mLayoutInflater;
    private Map<String, String> mContactsMap = new HashMap<>();
    private FamilyIndex mFamilyIndex;
    private Dialog mDialog;
    //    @Bind(R.id.tv_title)
//    TextView tvTitle;
//    @Bind(R.id.tv_title_right)
//    TextView tvTitleRight;
//    @Bind(R.id.vp_family_member)
//    ViewPager vpFamilyMember;
//    @Bind(R.id.edit_search)
//    EditText editSearch;
//    @Bind(R.id.btn_create_family)
//    Button btnCreateFamily;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
        mLayoutInflater = getLayoutInflater(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    View mRooView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRooView == null) {
            mRooView = inflater.inflate(R.layout.fragment_create_family_tips, container, false);
            initCreateFamilyView(mRooView);
            mDialog = new LoadDialog(mContext, false, "");
            updateView();
        }


        return mRooView;
    }

    public void updateView() {
        mDialog.show();
        List<Contacts> contactses = mApplication.getContacts();
        if (contactses == null) {
            new ReadContacts().execute();
        } else {
            initContactsInfo(contactsToStr(contactses));
            mDialog.dismiss();
        }
    }

    private String contactsToStr(List<Contacts> contactses) {
        StringBuilder sb = new StringBuilder();
        for (Contacts contacts : contactses) {
            for (String phone : contacts.getPhones()) {

                phone = phone.replaceAll("-", "").replaceAll(" ", "");
                phone.replace("+86","");
                sb.append("86 ");
                sb.append(phone);
                mContactsMap.put("86 " + phone, contacts.getName());
                sb.append(",");
            }
        }
        String result = sb.toString();
        return result;
    }


    private void initCreateFamilyView(View v) {
        View viewSearchFamily = v.findViewById(R.id.view_search_family);
        viewSearchFamily.setOnClickListener(this);
        mVpContactsFamily = (ViewPager) v.findViewById(R.id.vp_contacts_family);
        mBtnCreateFamily = (Button) v.findViewById(R.id.btn_create_family);

        mBtnCreateFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreateFamilyActiivty.class);
                startActivityForResult(intent, REQUEST_CREATE_FAMILY);
            }
        });

        mContactsFamilyAdapter = new ContactsFamilyAdapter();
        mVpContactsFamily.setAdapter(mContactsFamilyAdapter);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class ReadContacts extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String[] params) {
            String contactsStr = readAllContacts();
            return contactsStr;
        }

        @Override
        protected void onPostExecute(String contactsStr) {
            super.onPostExecute(contactsStr);
            mDialog.dismiss();
            initContactsInfo(contactsStr);

        }
    }

    private void initContactsInfo(String contactsStr) {

        Map<String, String> params = new HashMap<>();
        params.put("sessionid", mApplication.getUser().getSessionid());
        params.put("phones", contactsStr);
        APIClient.post(mContext, Constants.URL.TXL_MATCH_CLAN, params, new BaseVolleyListener(mContext) {
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

                        Type type = new TypeToken<ArrayList<MatchContactsFamily>>() {
                        }.getType();
                        mMactchFamilys = GsonUtils.parseJSONArray(json, type);
                        if (mMactchFamilys == null || mMactchFamilys.size() == 0) {//TODO
                            mVpContactsFamily.setVisibility(View.GONE);

                        } else {
                            mVpContactsFamily.setVisibility(View.VISIBLE);
                            initMatchUserInfo();
//                            mVpContactsFamily.setAdapter();
                        }
                    }
                });
            }
        });
    }

    //匹配通讯录的家族ViewPager Adapter
    class ContactsFamilyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mGvMatchList.size();
        }

        public ContactsFamilyAdapter() {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
//            return mImageViews[position % mImageViews.length];
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mGvMatchList.get(position), 0);
            return mGvMatchList.get(position);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mGvMatchList.get(position));
        }
    }

    //初始化配置到的家族信息
    private void initMatchUserInfo() {

        int matchFamilySize = mMactchFamilys.size();

        int vpSize;

        if (matchFamilySize % VP_COUNT > 0) {
            vpSize = matchFamilySize / MATCH_FAMILY_PAGE_COUNT + 1;
        } else {
            vpSize = matchFamilySize / MATCH_FAMILY_PAGE_COUNT;
        }


        //将匹配的信息对象 分页
        for (int i = 0; i < vpSize; i++) {
            List<MatchContactsFamily> list = new ArrayList<>();

            for (int j = 0; j < MATCH_FAMILY_PAGE_COUNT; j++) {
                if ((i * MATCH_FAMILY_PAGE_COUNT + j) < mMactchFamilys.size()) {
                    list.add(mMactchFamilys.get(i * MATCH_FAMILY_PAGE_COUNT + j));
                }
            }

            GridView gridView = (GridView) mLayoutInflater.inflate(R.layout.gridview_family_match_contacts, null);
            final MatchContactsFamilyAdapter adapter = new MatchContactsFamilyAdapter(list);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    long clanId = adapter.getItem(position).getClanId();
                    Intent intent = new Intent(mContext, FamilyInfoActivity.class);
//                    if(mFamilyIndex.getClanRole()== ClanRoleEnum.NORMAL.getCode()) {
                    intent.putExtra("type", "join");
//                    }else{
//                        intent.putExtra("type", "");
//                    }
                    intent.putExtra("clanId", clanId + "");
                    startActivity(intent);
                }
            });

            mGvMatchList.add(gridView);
        }
        mContactsFamilyAdapter.notifyDataSetChanged();


        //int vpMatchSize = familyBaseInfos.size()/4+;
    }

    class MatchContactsFamilyAdapter extends BaseAdapter {
        List<MatchContactsFamily> list;

        MatchContactsFamilyAdapter(List<MatchContactsFamily> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MatchContactsFamily getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_match_family, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MatchContactsFamily matchFamily = list.get(position);


            UILUtils.displayImageWithRounder(mApplication.getImgUri() + "clanLogo/" + matchFamily.getClanLogo(), holder.ivFamilyImg, 20);
            holder.tvFamilyName.setText(matchFamily.getClanName() + "家族");
            String type = "已加入";
            if (matchFamily.getTy() == 1) {
                type = "创建";
            }
            holder.tvFamilyMatch.setText(mContactsMap.get(matchFamily.getMobile()) + type);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_family_img)
            ImageView ivFamilyImg;
            @Bind(R.id.tv_family_name)
            TextView tvFamilyName;
            @Bind(R.id.tv_family_match)
            TextView tvFamilyMatch;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

    }


    /*
     * 读取联系人的信息
     */
    public String readAllContacts() {
        Cursor cursor = this.getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        int contactIdIndex = 0;
        int nameIndex = 0;

        if (cursor == null) {
            return "";
        }
        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }

        StringBuilder sb = new StringBuilder();


        while (cursor.moveToNext()) {
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            Log.e(TAG, contactId);
            Log.e(TAG, name);

            /*
             * 查找该联系人的phone信息
             */
            Cursor phones = this.getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId,
                    null, null);
            int phoneIndex = 0;
            if (phones.getCount() > 0) {
                phoneIndex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            }

            while (phones.moveToNext()) {
                String phoneNumber = phones.getString(phoneIndex);
                phoneNumber = phoneNumber.replaceAll("-", "").replaceAll(" ", "");
                sb.append("86 ");
                sb.append(phoneNumber);
                mContactsMap.put("86 " + phoneNumber, name);
                sb.append(",");

                Log.e(TAG, phoneNumber);
            }
        }
        return sb.toString();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_title_right:
                break;
            case R.id.btn_create_family:
                break;
            case R.id.view_search_family:
                startActivity(new Intent(mContext, SearchFamilyActivity.class));
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHANGE_FAMILY) {
//                int clanId = data.getIntExtra("clanId", mDefaultFamilyBaseInfo.getClanId());
//                initFamilyIdexData(clanId);
            } else {
                //更新家族数据 TODO
//                mContext.getSupportFragmentManager().findFragmentByTag(getResources().getString(MainTab.FAMILY.getResName()));
            }
        }
    }

}
