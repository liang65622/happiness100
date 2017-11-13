package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/9.
 */

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.model.ClanCwNote;
import com.happiness100.app.model.ClanRela;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.RelationshipEnum;
import com.happiness100.app.ui.widget.DialogWheelOneItemSelector;
import com.happiness100.app.ui.widget.DialogZupuCwSelector;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.ui.widget.wheel.OnWheelChangedListener;
import com.happiness100.app.ui.widget.wheel.WheelView;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.AssetsUtils;
import com.happiness100.app.utils.Constants;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.happiness100.app.R.id.view_select_gender;

/**
 * 作者：justin on 2016/9/9 11:37
 */
public class ZuPuAddMemberActivity extends BaseActivity implements OnWheelChangedListener, BaseViewInterface {
    private static final int REQUEST_BINDING = 1;
    private static final String TAG = "ZuPuAddMemberActivity";
    @Bind(R.id.edit_realname)
    EditText editRealname;

    List<String> mChengwei = new ArrayList<>();
    @Bind(R.id.title_view_right)
    RelativeLayout titleViewRight;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.tv_select_cw)
    TextView tvSelectCw;
    @Bind(R.id.tv_gender)
    TextView tvGender;
    @Bind(R.id.tv_title_rank)
    TextView tvTitleRank;
    @Bind(R.id.tv_rank)
    TextView tvRank;
    @Bind(view_select_gender)
    RelativeLayout viewSelectGender;
    @Bind(R.id.view_select_rank)
    RelativeLayout viewSelectRank;
    private List<ClanCwNote> mCwNoteListst;
    private int mOpType;
    private ClanCwNote mClanCwNote;
    private String mRank;
    private ClanRela.PUnit mUser;
    private String mCw;
    private String mClanId;
    private DialogZupuCwSelector mDialogZupuCwSelector;
    private Dialog mLoadingDialog;
    String[] cwRank = new String[]{"大", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};
    String[] defaltRank = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十"};
    private boolean mIsHasRank;
    String mDo;
    private String mGender = "";
    private String mCreaterRank;
    private ArrayList<ClanRela.PUnit> mPUserList;
    private long mBindingUserId;
    private DialogWheelOneItemSelector mGenderDialog;
    private DialogWheelOneItemSelector mRankDialog;

    int mCwId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zupu_add_member);
        ButterKnife.bind(this);
        Intent data = getIntent();

        mDo = data.getStringExtra("do");
        if (mDo == null) {
            mDo = "add";
        }

        mUser = data.getParcelableExtra("user");
        //为空的话是第一次创建
        if (mUser == null) {
            mUser = new ClanRela.PUnit();
            mUser.cwId = 100;
        }

        mPUserList = getIntent().getParcelableArrayListExtra("punits");
        mClanId = mApplication.getUser().getDefaultClanId();
        mOpType = data.getIntExtra("opType", 0);
        initView();
        initData();
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
    }

    @Override
    public void initView() {

        titleViewTitle.setText("添加关系");
        titleViewRight.setVisibility(View.VISIBLE);
        //
        if (mUser == null) {//创建者完善自己的信息
            if (mOpType == 7) {
                tvSelectCw.setText("自己");
                mCw = "自己";
            }
            viewSelectGender.setVisibility(View.VISIBLE);
            viewSelectRank.setVisibility(View.VISIBLE);
            initGengerDialog();

            if (mDo.equals("edit")) {
            } else {
                if (mOpType == 7) {
                    tvAccount.setText("XF" + mApplication.getUser().getXf());
                }
            }
        } else if (mUser.cwId == 100) {//创建者添加自己的关系

        }

        if (mDo != null && mDo.equals("edit")) {
            if (mUser.cw != null) {
                tvSelectCw.setText(mUser.cw);
            } else {

            }

            //编辑自己显示排名控件
            if (mUser.cwId == 100) {
                viewSelectGender.setVisibility(View.VISIBLE);
                viewSelectRank.setVisibility(View.VISIBLE);
                int rank = mUser.rank;
                String rankStr = rank + "";
                if (rank >= 100) {
                    if (rank == 100) {
                        rank = 101;
                    }
                    tvGender.setText("女");
                    tvRank.setText(defaltRank[rank - 100 - 1]);
                    mGenderIndex = "1";
                } else {
                    tvGender.setText("男");
                    tvRank.setText(defaltRank[rank - 1]);
                    mGenderIndex = "";
                }
                initGengerDialog();

            }

            if (mUser.userId == 0) {
                tvAccount.setText("绑定幸福号");
            } else {
                tvAccount.setText("XF" + mUser.userId + "");
            }

            editRealname.setText(mUser.nameNote);
            mRank = mUser.rank + "";
        }
    }

    String[] mGenders = new String[]{"男", "女"};
    private String mGenderIndex;
    private int mRankIndex;

    private void initGengerDialog() {

        mGenderDialog = new DialogWheelOneItemSelector(mContext, mGenders, R.style.MMTheme_DataSheet_BottomIn, new DialogWheelOneItemSelector.PriorityListener() {

            @Override
            public void refreshPriorityUI(int selectIndex) {
                tvGender.setText(mGenders[selectIndex]);
                tvTitleRank.setText(selectIndex == 0 ? "兄弟排行" : "姐妹排行");
                mGenderIndex = selectIndex == 0 ? "" : "1";

            }
        });
        mRankDialog = new DialogWheelOneItemSelector(mContext, defaltRank, R.style.MMTheme_DataSheet_BottomIn, new DialogWheelOneItemSelector.PriorityListener() {


            @Override
            public void refreshPriorityUI(int selectIndex) {
                tvRank.setText(defaltRank[selectIndex]);
                mRankIndex = selectIndex + 1;
            }
        });


        if (TextUtils.isEmpty(tvGender.getText())) {
            tvGender.setText("男");
            mGenderIndex = "";

            tvTitleRank.setText("兄弟排行");
            tvRank.setText("一");
            mRankIndex = 1;
        } else {

        }
    }

    @Override
    public void initData() {
        mLoadingDialog = new LoadDialog(this, false, "");
        mLoadingDialog.show();
        new ReadCW().execute();
    }

    //
    private String[] getCwStrs(ClanCwNote clanCwNote) {
        String cwStr = "";
        if (mOpType == RelationshipEnum.FATHER.getCode()) {
            cwStr = clanCwNote.getFather();
        } else if (mOpType == RelationshipEnum.MOTHER.getCode()) {
            cwStr = clanCwNote.getMother();
        } else if (mOpType == RelationshipEnum.HASBAND.getCode()) {
            cwStr = clanCwNote.getHusband();
        } else if (mOpType == RelationshipEnum.WIFE.getCode()) {
            cwStr = clanCwNote.getWife();
        } else if (mOpType == RelationshipEnum.DOUGHTER.getCode()) {
            cwStr = clanCwNote.getDaughter();
        } else if (mOpType == RelationshipEnum.SUN.getCode()) {
            cwStr = clanCwNote.getSon();
        } else if (mOpType == RelationshipEnum.BROTHER.getCode()) {
            cwStr = clanCwNote.getElderBrother() + "/" + clanCwNote.getYoungBrother() + "/" + clanCwNote.getElderSister() + "/" + clanCwNote.getYoungSister();
        }
        String[] cws = cwStr.split("/");
        List<String> cwList = Arrays.asList(cws);
        ArrayList<String> newList = new ArrayList<>(cwList);
        removeDuplicateWithOrder(newList);
        String[] result = newList.toArray(new String[newList.size()]);
        return result;
    }

    //加载称谓表
    class ReadCW extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String[] params) {
            Type type = new TypeToken<ArrayList<ClanCwNote>>() {
            }.getType();
            mCwNoteListst = GsonUtils.parseJSONArray(AssetsUtils.getFromAssets(mContext, "clan_cw.json"), type);

            return null;
        }

        @Override
        protected void onPostExecute(String contactsStr) {
            super.onPostExecute(contactsStr);
            mLoadingDialog.dismiss();
            if (mDialogZupuCwSelector == null) {
                mDialogZupuCwSelector = new DialogZupuCwSelector(mContext, R.style.MMTheme_DataSheet_BottomIn, new DialogZupuCwSelector.PriorityListener() {
                    @Override
                    public void refreshPriorityUI(int cwIndex, int rankIndex) {
                        Log.e(TAG, "refreshPriorityUI(): cwIndex:" + cwIndex + " rankIndex:" + rankIndex);
                        //要添加关系的称谓ID
                        mCwId = Integer.parseInt(mCwIds[cwIndex]);
                        String rankStr = "";

                        //获取要加称谓的性别
                        String gender = getGender(mCwStrsWithoutId[cwIndex]);
                        if (gender.equals("1")) {
                            if (rankIndex < 10)
                                rankStr = "0";
                        }

                        mRank = gender + rankStr + (rankIndex + 1) + "";
                        if (rankIndex > -1) {
                            Log.e(TAG, "refreshPriorityUI(): cwIndex:" + cwIndex + " rankIndex:" + rankIndex + " cwRank:" + cwRank[rankIndex]);
                            if (mIsHasRank) {
                                tvSelectCw.setText(cwRank[rankIndex] + mCwStrsWithoutId[cwIndex]);
                            } else {
                                tvSelectCw.setText(mCwStrsWithoutId[cwIndex]);
                            }
                        } else {
                            tvSelectCw.setText(mCwStrsWithoutId[cwIndex]);
                        }
                    }
                });

                mClanCwNote = getClanCwNote();
                mCwStrs = getCwStrs(mClanCwNote);//未处理 称谓数组

                initWheelData();

                int currentCwId = mUser.cwId;

//                if (mCwStrs.length == 1 && mCwStrs[0].contains("N_")) {
//                    String cw = mCwStrs[0].split("C_")[0].replace("N_", "");
//                    tvSelectCw.setText(cw);
//                    mRank = getGender(cw) + 1 + "";
//                    mCwId = Integer.parseInt(mCwStrs[0].split("C_")[1]);
//                } else {

                mIsHasRank = !mCwStrs[0].contains("N_");

                if (mDo.equals("edit")) {

                    tvSelectCw.setText(mClanCwNote.getCw());

                } else {
                    mDialogZupuCwSelector.setData(mCwStrsWithoutId, mIsHasRank, mClanCwNote);
                }
//                }
            }
        }
    }

    private String getGender(String cw) {

        if (mClanCwNote.getYoungSister() != null && mClanCwNote.getYoungSister().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getElderSister() != null && mClanCwNote.getElderSister().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getWife() != null && mClanCwNote.getWife().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getDaughter() != null && mClanCwNote.getDaughter().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getMother() != null && mClanCwNote.getMother().contains(cw)) {
            return "1";
        } else if (mClanCwNote.getFather() != null && mClanCwNote.getFather().contains(cw)) {
            return "";
        } else if (
                mClanCwNote.getYoungBrother() != null && mClanCwNote.getYoungBrother().contains(cw)) {
            return "";
        } else if (mClanCwNote.getElderBrother() != null && mClanCwNote.getElderBrother().contains(cw)) {
            return "";
        } else if (mClanCwNote.getHusband() != null && mClanCwNote.getHusband().contains(cw)) {
            return "";
        } else if (mClanCwNote.getSon() != null && mClanCwNote.getSon().contains(cw)) {
            return "";
        }
        return "";
    }

    //去除重复元素
    public static void removeDuplicateWithOrder(List list) {

        HashSet hSet = new HashSet(list);
        list.clear();
        list.addAll(hSet);
        System.out.println(list);
    }

    public ClanCwNote getClanCwNote() {

        for (int i = 0; i < mCwNoteListst.size(); i++) {
            ClanCwNote clanCwNote = mCwNoteListst.get(i);
            if (clanCwNote.getCwId().equals(mUser.cwId + "")) {
                return clanCwNote;
            }
        }
        return null;
    }


    @OnClick({R.id.ll_select_cw, R.id.view_binding_account, R.id.title_view_back, view_select_gender, R.id.view_select_rank, R.id.title_view_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_view_back:
                mContext.finish();
                break;
            case R.id.ll_select_cw:
                if (mUser != null && !mDo.equals("edit")) {
                    mDialogZupuCwSelector.show();
                }
                break;

            case R.id.view_binding_account:
                if (mOpType != 7||(mUser.cwId==100&&!mDo.equals("edit"))) {
                    Intent intent = new Intent(mContext, ZupuBindingActivity.class);
                    intent.putParcelableArrayListExtra("punits", mPUserList);
                    startActivityForResult(intent, REQUEST_BINDING);
                }
                break;
            case view_select_gender:
                mGenderDialog.show();
                break;
            case R.id.view_select_rank:
                mRankDialog.show();

                break;
            case R.id.title_view_right:
                if (mDo != null && mDo.equals("edit")) {
                    editPUint();
                } else {
                    addPUint();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_BINDING:
                    long userId = data.getLongExtra("userId", 0);
                    mBindingUserId = userId;
                    tvAccount.setText("XF" + userId);
                    break;
                default:
                    break;
            }
        }
    }

    private void addPUint() {

        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);

        if (TextUtils.isEmpty(editRealname.getText())) {
            showToast("请填写姓名");
            return;
        }

        if (mOpType == 7) {
            if (TextUtils.isEmpty(tvGender.getText())) {
                showToast("请选择性别");
                return;
            }

            if (TextUtils.isEmpty(tvRank.getText())) {
                showToast("请选择排行");
                return;
            }
            String rankStr = mRankIndex >= 10 ? mRankIndex + "" : "0" + mRankIndex;
            mRank = mGenderIndex + rankStr;
        }


        if (mOpType == RelationshipEnum.SUN.getCode()) {
            params.put("opt_type", "6");
        } else {
            params.put("opt_type", mOpType + "");
        }
        params.put("rank", mRank);
        params.put("name_note", editRealname.getText().toString());
        if (mUser != null) {
            params.put("unit_id", mUser.unitId + "");
        } else {
            params.put("unit_id", ((App) mContext.getApplication()).getUser().getXf() + "");
        }

        params.put("cw", tvSelectCw.getText().toString());


        //幸福号
        if (!TextUtils.isEmpty(tvAccount.getText()) && tvAccount.getText().toString().contains("XF")) {

            params.put("d_user_id", tvAccount.getText().toString().replace("XF", ""));
        } else if (mOpType == 7) {
            params.put("d_user_id", mApplication.getUser().getXf() + "");
        } else {
            params.put("d_user_id", "0");
        }


        if (mOpType == RelationshipEnum.BROTHER.getCode()) {
            params.put("fCwId", mClanCwNote.getFather().split("C_")[1]);
            params.put("mCwId", mClanCwNote.getMother().split("C_")[1]);
        }

        params.put("cw_id", mCwId + "");

        String url = Constants.URL.CLAN_PU_ADD_MEMBER;
        if (mDo.equals("edit")) {
            url = Constants.URL.CLAN_PU_EDIT_MEMBER;
        }

        APIClient.postWithSessionId(mContext, url, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, false, "")) {
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
                        setResult(RESULT_OK);
                        mContext.finish();
                    }
                });
            }
        });
    }


    private void editPUint() {

        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId);

        if (mCreaterRank != null)
            mRank = mCreaterRank;

        if (mUser.cwId == 100) {
            String rankStr = mRankIndex >= 10 ? mRankIndex + "" : "0" + mRankIndex;
            mRank = mGenderIndex + rankStr;
        }

        params.put("rank", mRank);
        params.put("name_note", editRealname.getText().toString());
        params.put("unit_id", mUser.unitId + "");
        params.put("cw", tvSelectCw.getText().toString());
        if (!TextUtils.isEmpty(tvAccount.getText())) {
            params.put("d_user_id", tvAccount.getText().toString().replace("XF", ""));
        } else {
            if (mOpType == 7) {
                params.put("d_user_id", mApplication.getUser().getXf() + "");
            } else {
                params.put("d_user_id", "0");
            }
        }

        if (mUser.cwId == 100) {
            params.put("cw_id", mUser.cwId + "");
        } else {

        }
        String url = Constants.URL.CLAN_PU_EDIT_MEMBER;
        APIClient.postWithSessionId(mContext, url, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, false, "")) {
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
                        setResult(RESULT_OK);
                        mContext.finish();
                    }
                });
            }
        });
    }


    private String[] mCwStrs;
    ;//未处理 称谓数组
    private String[] mCwStrsWithoutId;
    ;//未处理 称谓数组
    private String[] mCwIds;//称谓ID数组

    //初始化 称谓数据
    private void initWheelData() {

        mCwStrsWithoutId = new String[mCwStrs.length];
        mCwIds = new String[mCwStrs.length];
        for (int i = 0; i < mCwStrsWithoutId.length; i++) {

            //分割称谓和称谓ID
            String[] tempCw = mCwStrs[i].split("C_");
            if (tempCw.length > 1) {
                mCwIds[i] = tempCw[1];
            } else {
                mCwIds[i] = "0";
            }

            if (tempCw[0].contains("N_")) {
//                wvSelectRank.setVisibility(View.GONE);
            }
            mCwStrsWithoutId[i] = tempCw[0].replace("N_", "");
        }
        if (mCwStrsWithoutId.length == 1) {
//            tvSelectCw.setText();//TODO
//            mlistener.refreshPriorityUI(mCwStrsWithoutId[0], mCwIds[0], 0);
        }
        //更新 DIALOG
//        wvSelectCw.setViewAdapter(new ArrayWheelAdapter<String>(getContext(), mCwStrsWithoutId));
//        wvSelectRank.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cwRank));
//        updateRank(0);
    }
}
