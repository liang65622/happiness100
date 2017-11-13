package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/12.
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.happiness100.app.R;
import com.happiness100.app.manager.RemarkManager;
import com.happiness100.app.model.Friend;
import com.happiness100.app.model.Remark;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.widget.ClearWriteEditText;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.OnTextChangeListener;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.Constants;
import com.justin.bean.Contacts;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.PhoneUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * 作者：jiangsheng on 2016/9/12 20:26
 */
public class RemarkActivity extends BaseActivity {

    @Bind(R.id.text_back)
    TextView mTextBack;
    @Bind(R.id.title_view_title)
    TextView mTitleViewTitle;
    @Bind(R.id.image_right)
    ImageView mImageRight;
    @Bind(R.id.text_right)
    TextView mTextRight;
    @Bind(R.id.title_view_right)
    RelativeLayout mTitleViewRight;
    @Bind(R.id.txl_remark)
    TextView mTxlRemark;
    @Bind(R.id.remark_read_txl_Item)
    RelativeLayout mRemarkReadTxlItem;
    @Bind(R.id.indexItem)
    RelativeLayout mIndexItem;
    @Bind(R.id.edit_remark)
    ClearWriteEditText mEditRemark;
    @Bind(R.id.mobile_item)
    LinearLayout mMobileItem;
    String mTxlName;
    Friend mfriend;
    List<String> mTelephonesList;
    int TelephonesCount;
    Remark mRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        ButterKnife.bind(this);
        initView();
    }

    void initView() {
        Intent intent = getIntent();
        String backStr = intent.getStringExtra("back");
        mfriend = intent.getParcelableExtra("friend");
        mRemark = RemarkManager.getInstance().findRemark(mfriend.getXf() + "");
        mTextBack.setText(backStr == null ? "返回" : backStr);
        mTitleViewTitle.setText("备注信息");
        mTitleViewRight.setVisibility(View.VISIBLE);
        mImageRight.setVisibility(View.GONE);
        mTextRight.setVisibility(View.VISIBLE);
        mTextRight.setText("保存");


        String NoteName;
        if (mRemark.getNoteName() != null && !mRemark.getNoteName().isEmpty()) {
            NoteName = mRemark.getNoteName();
        } else {
            NoteName = mfriend.getNickname();
        }

        mEditRemark.setText(NoteName);
        mEditRemark.setSelection(mEditRemark.getText().toString().length());
        mInflater = LayoutInflater.from(mContext);
        initPhones();
        updateTelePhonesItem();
    }

    void initPhones() {
        CheckConversationNote();
        mTelephonesList = new ArrayList<String>();
        TelephonesCount = 0;
        if (mTxlName != null && !mTxlName.isEmpty()) {
            String mobile = mfriend.getMobile();
            mTelephonesList.add(mobile);
            TelephonesCount++;
        }
        String phoneNums = "";

        if (mRemark.getMobiles() != null && !mRemark.getMobiles().isEmpty()) {
            phoneNums = mRemark.getMobiles();
        }

        if (phoneNums != null && !phoneNums.isEmpty()) {
            String[] phoneArray = phoneNums.split(",");
            for (int i = 0; i < phoneArray.length; ++i) {
                if (mTxlName != null && !mTxlName.isEmpty() && mfriend.getMobile().compareTo(phoneArray[i]) == 0) {
                    continue;
                }
                mTelephonesList.add(phoneArray[i]);
                TelephonesCount++;
            }
        }
    }

    void CheckConversationNote() {
        List<Contacts> tempList = mApplication.getContacts();
        if (tempList == null) {
            tempList = PhoneUtils.getAllContacts(this);
        }

        if (tempList.size() > 0) {
            for (int i = 0; i < tempList.size(); ++i) {
                Contacts contacts = tempList.get(i);
                List<String> phoneList = contacts.getPhones();
                for (int j = 0; j < phoneList.size(); ++j) {
                    String PhoneNum = phoneList.get(j);
                    if (mfriend.getMobile().compareTo("86 " + PhoneNum) == 0) {
                        mTxlName = contacts.getName();
                        if (!mTxlName.isEmpty() && mTxlName.compareTo(mRemark.getNoteName() == null ? "" : mRemark.getNoteName()) != 0)
                            mTxlRemark.setText("设置手机通讯录名字“" + mTxlName + "”为备注名");
                        else
                            mRemarkReadTxlItem.setVisibility(View.GONE);
                        return;
                    }
                }
            }
        }
        mRemarkReadTxlItem.setVisibility(View.GONE);
    }

    void addTelePhonesItem() {
        if (TelephonesCount == 5) {
            return;
        }
        TelephonesCount++;
        mTelephonesList.add("");
        updateTelePhonesItem();
    }

    void deleteTelePhoneItem(int deleteIdx) {
        TelephonesCount--;
        mTelephonesList.remove(deleteIdx);
        updateTelePhonesItem();
    }

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addTelePhonesItem();
        }
    };

    View.OnClickListener removeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            deleteTelePhoneItem((int) v.getTag());
        }
    };

    View.OnClickListener TipsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(mContext)
                    .setMessage("从手机通讯录中匹配的号码，无法修改")
                    .setPositiveButton("确定", null)
                    .show();
        }
    };

    OnTextChangeListener mTextChangeListener = new OnTextChangeListener() {
        @Override
        public void Change(View view) {
            ClearWriteEditText v = (ClearWriteEditText) view;
            mTelephonesList.set((int) v.getTag(), v.getText().toString() == null ? "" : v.getText().toString());
        }
    };

    void updateTelePhonesItem() {
        mMobileItem.removeAllViews();
        int Count = TelephonesCount + 1 > 5 ? TelephonesCount : TelephonesCount + 1;
        for (int i = 0; i < Count; ++i) {
            View vi = mInflater.inflate(R.layout.mobile_item, null);
            ViewHolder view = new ViewHolder(vi);
            view.mMobileItemGo.setVisibility(View.GONE);
            view.mMobileText.setVisibility(View.GONE);
            view.mMobileItemEdit.setVisibility(View.GONE);
            view.mMobileItemStart.setTag(i);
            view.mMobileItemEdit.setTag(i);
            if (TelephonesCount == 5) {
                view.mMobileItemStart.setImageResource(R.drawable.icon_delete3);
                view.mMobileItemStart.setOnClickListener(removeListener);

                if (i == 0 && mTxlName != null) {
                    view.mMobileText.setVisibility(View.VISIBLE);
                    view.mMobileText.setText(mTelephonesList.get(i));
                    view.mMobileItemGo.setVisibility(View.VISIBLE);
                    view.mMobileItemGo.setImageResource(R.drawable.icon_gth);
                    view.mMobileItemGo.setOnClickListener(TipsListener);
                } else {
                    view.mMobileItemEdit.setVisibility(View.VISIBLE);
                    view.mMobileItemEdit.setText(mTelephonesList.get(i));
                    view.mMobileItemEdit.setTextChangeListener(mTextChangeListener);
                }
            } else {
                if (i == TelephonesCount) {
                    view.mMobileItemGo.setVisibility(View.GONE);
                    view.mMobileText.setVisibility(View.VISIBLE);
                    view.mMobileItemStart.setImageResource(R.drawable.icon_add6);
                    view.mMobileItem.setOnClickListener(addListener);
                } else if (i == 0 && mTxlName != null) {
                    view.mMobileItemStart.setImageResource(R.drawable.icon_delete3);
                    view.mMobileItemStart.setOnClickListener(removeListener);
                    view.mMobileText.setVisibility(View.VISIBLE);
                    view.mMobileText.setText(mTelephonesList.get(i));
                    view.mMobileItemGo.setVisibility(View.VISIBLE);
                    view.mMobileItemGo.setImageResource(R.drawable.icon_gth);
                    view.mMobileItemGo.setOnClickListener(TipsListener);
                } else {
                    view.mMobileItemStart.setImageResource(R.drawable.icon_delete3);
                    view.mMobileItemStart.setOnClickListener(removeListener);
                    view.mMobileItemEdit.setVisibility(View.VISIBLE);
                    view.mMobileItemEdit.setTextChangeListener(mTextChangeListener);
                    view.mMobileItemEdit.setText(mTelephonesList.get(i));
                }
            }
            mMobileItem.addView(vi);
        }
    }

    public void postModifyRemark() {
        String phones = "";
        for (int i = 0; i < mTelephonesList.size(); ++i) {
            if (phones.isEmpty()) {
                phones = mTelephonesList.get(i);
            } else {
                if (!mTelephonesList.get(i).isEmpty()) {
                    phones = phones + "," + mTelephonesList.get(i);
                }
            }
        }
        final String phonesStr = phones;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("d_user_id", mfriend.getXf() + "");
        params.put("mobiles", phones);
        params.put("note_name", mEditRemark.getText().toString());
        APIClient.postWithSessionId(mContext, Constants.URL.FRIEND_MODIFY_NOTE, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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
                        String NoteName = mEditRemark.getText().toString();
                        mRemark.setXf(mfriend.getXf());
                        mRemark.setUserId(mfriend.getUserId());
                        mRemark.setMobiles(phonesStr);
                        mRemark.setNoteName(NoteName == null ? "" : NoteName);
                        RemarkManager.getInstance().updateRemark(mRemark);
                        if (NoteName == null || NoteName.isEmpty()) {
                            NoteName = mfriend.getNickname();
                        }
                        if (RongIM.getInstance() != null) {
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(mfriend.getXf() + "", NoteName, Uri.parse(mApplication.getHeadImage(mfriend.getXf() + "", mfriend.getHeadImage()))));
                        }
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }

    @OnClick({R.id.title_view_back, R.id.title_view_right, R.id.remark_setting, R.id.indexItem})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                finish();
                break;
            case R.id.title_view_right:
                postModifyRemark();
                break;
            case R.id.remark_setting:
                if (mTxlName != null) {
                    mEditRemark.setText(mTxlName);
                    mRemarkReadTxlItem.setVisibility(View.GONE);
                }
                break;
            case R.id.indexItem:
                break;
        }
    }

    class ViewHolder {
        @Bind(R.id.mobileItem_start)
        ImageView mMobileItemStart;
        @Bind(R.id.mobileItem_edit)
        ClearWriteEditText mMobileItemEdit;
        @Bind(R.id.mobileItem_go)
        ImageView mMobileItemGo;
        @Bind(R.id.mobileItem)
        RelativeLayout mMobileItem;
        @Bind(R.id.mobileItem_text)
        TextView mMobileText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
