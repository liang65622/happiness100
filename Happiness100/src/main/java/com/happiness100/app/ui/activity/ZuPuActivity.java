package com.happiness100.app.ui.activity;/**
 * Created by Administrator on 2016/9/7.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.R;
import com.happiness100.app.model.ClanCwNote;
import com.happiness100.app.model.ClanRela;
import com.happiness100.app.model.ClanRela.PUnit;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.bean.ClanRoleEnum;
import com.happiness100.app.ui.widget.DialogFamilyControl;
import com.happiness100.app.ui.widget.DialogWithYesOrNoUtils;
import com.happiness100.app.ui.widget.DialogZupuContral;
import com.happiness100.app.ui.widget.FamilyViewGroup;
import com.happiness100.app.ui.widget.LoadDialog;
import com.happiness100.app.utils.APIClient;
import com.happiness100.app.utils.AssetsUtils;
import com.happiness100.app.utils.SQLUtils;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;
import com.justin.utils.SharedPreferencesContext;
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
 * 作者：justin on 2016/9/7 16:03
 */
public class ZuPuActivity extends BaseActivity implements BaseViewInterface, DialogInterface.OnDismissListener {
    private static final String TAG = "ZuPuActivity";
    private static final int REQUEST_ADD_OR_EDIT_MEMBER = 1;
    private static final int REQUEST_UPDATE_MEMBER = 2;
    @Bind(R.id.family_view_group)
    FamilyViewGroup familyViewGroup;
    @Bind(R.id.title_view_title)
    TextView titleViewTitle;
    private ClanRela mClanRela;
    private List<PUnit> mPUnits;
    private ArrayList<PUnit> mUsers = new ArrayList<>();
    private DialogFamilyControl mControlDialog;
    int x = 0;
    float oldX = 0;
    float oldY = 0;
    private int mClanId;
    private int mClanRole;
    private String mClanCwJson;
    private List<ClanCwNote> mCwNoteListst;
    ZuPuActivity mContext;

    List<DialogZupuContral> mDialogZupuContrals = new ArrayList<DialogZupuContral>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zupu);
        ButterKnife.bind(this);
        mContext = this;
        Intent data = getIntent();
        mClanId = data.getIntExtra("clanId", 0);
        if (mClanId == 0) {
            showToast("请先刷新家族界面");
            finish();
        }
        mClanRole = data.getIntExtra("clanRole", 0);


        familyViewGroup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = x;
                        oldY = y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = x - oldX;
                        float moveY = y - oldY;
//                        Log.e(TAG,"oldX:" + oldX + " oldY:" +oldY + " x:" + x + " y:" + y);
                        familyViewGroup.offsetLeftAndRight((int) moveX);
                        familyViewGroup.offsetTopAndBottom((int) moveY);
                        oldX = x;
                        oldY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        oldX = 0;
                        oldY = 0;
                        break;
                }
                return true;
            }
        });
        initView();
        initData();
    }

    @Override
    public void initView() {
        titleViewTitle.setText("家谱");
    }

    @Override
    public void initData() {

        mClanCwJson = SharedPreferencesContext.getInstance().getSharedPreferences().getString("clan_cw", null);
        if (mClanCwJson == null) {
            mClanCwJson = AssetsUtils.getFromAssets(mContext, "clan_cw.json");
            SharedPreferencesContext.getInstance().getSharedPreferences().edit().putString("clan_cw", mClanCwJson).commit();
        }
        Type type = new TypeToken<ArrayList<ClanCwNote>>() {
        }.getType();
        mCwNoteListst = GsonUtils.parseJSONArray(mClanCwJson, type);

        freshZupu();
    }

    Map<Long, View> viewMap = new HashMap<>();

    private void freshZupu() {
        String json = AssetsUtils.getFromAssets(mContext, "test.json");
//        mClanRela = GsonUtils.parseJSON(json, ClanRela.class);
//        //init views
//        mUsers = getPUnits();
//        int index = 1;
//        Log.e(TAG, mUsers.size() + "");
//        familyViewGroup.removeAllViews();
//        for (final PUnit user : mUsers) {
//            Button btn = new Button(mContext);
//            Log.e(TAG, user.unitId + "");
//            btn.setText(user.cw + "");
//            btn.setTag(user);
//            btn.setId((int) user.unitId);
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mControlDialog == null) {
//                        mControlDialog = new FamilyControlDial1og(mContext);
//                    }
//                    mControlDialog.setUser(user);
//                    setDialogSize(mControlDialog, v);
//                    mControlDialog.show();
//                }
//            });
//            viewMap.put(user.unitId, btn);
//            familyViewGroup.addView(btn);
//        }
//        familyViewGroup.setData(mClanRela);
//        familyViewGroup.setViewMap(viewMap);
//
//        familyViewGroup.invalidate();
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", mClanId + "");
        APIClient.getZupuData(mContext, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, true, "")) {
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

                        Gson gson = new Gson();
                        mClanRela = gson.fromJson(json, ClanRela.class);
//                        mClanRela = GsonUtils.parseJSON(json, ClanRela.class);


                        if (mClanRela == null || mClanRela.uIndex2Punit.size() == 0) {
                            if (mClanRole != ClanRoleEnum.CREATER.getCode()) {
                                showToast("家谱树还没建立。");
                                return;
                            }


                            DialogWithYesOrNoUtils.getInstance().showDialog(mContext, "提示", "先完善自己的家族信息。", new DialogWithYesOrNoUtils.DialogCallBack() {
                                @Override
                                public void exectEvent() {
                                    Intent intent = new Intent(mContext, ZuPuAddMemberActivity.class);
                                    intent.putExtra("opType", 7);
                                    intent.putExtra("cwId", 100);
                                    intent.putParcelableArrayListExtra("punits", mUsers);
                                    startActivityForResult(intent, REQUEST_ADD_OR_EDIT_MEMBER);
                                }

                                @Override
                                public void exectEditEvent(String editText) {

                                }

                                @Override
                                public void updatePassword(String oldPassword, String newPassword) {
                                }
                            });
                            return;
                        } else {
                            //init views
                            mUsers = getPUnits();
                            final int index = 1;
                            familyViewGroup.removeAllViews();

                            //遍历家族树成员 ，初始化家族界面
                            for (final PUnit user : mUsers) {

                                View view = getLayoutInflater().inflate(R.layout.layout_zupu_member, null);
                                ImageView ivMemberImg = (ImageView) view.findViewById(R.id.iv_member_img);
                                TextView tvReaName = (TextView) view.findViewById(R.id.tv_real_name);
                                TextView tvCw = (TextView) view.findViewById(R.id.tv_cw);

                                UILUtils.displayImage(mApplication.getHeadImage(user.userId + "", SQLUtils.getMemberImage(user.userId).getHeadImage()), ivMemberImg);
                                tvReaName.setText(user.nameNote);
                                tvCw.setText(user.cw);
                                Log.e(TAG, user.unitId + "");
                                view.setId((int) user.unitId);
                                view.setTag(user);


                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        //普通成员 跳转到成员信息界面
                                        int clanRole = mApplication.getUser().getDefaultFamilyBaseInfo().getClanRole();
                                        if (clanRole == ClanRoleEnum.NORMAL.getCode()) {
                                            return;
                                        }

                                        //创建者及管理员可以编辑家族树

                                        final DialogZupuContral dialogContral = new DialogZupuContral(mContext, v);
                                        DialogZupuContral.ContralListener contralListener = new DialogZupuContral.ContralListener() {
                                            @Override
                                            public void onAdd() {
                                                DialogFamilyControl mControlDialog = new DialogFamilyControl(mContext, v);
                                                mControlDialog.setUser(user);
                                                ClanCwNote clanCwNote = getClanCwNote(user);
                                                mControlDialog.setClanCwJson(mClanCwJson);
                                                mControlDialog.setPunits(mUsers);
                                                mControlDialog.dismiss();
//                                            setDialogSize(mControlDialog, v);
                                                if (!isCwPass(clanCwNote)) {
                                                    ToastUtils.shortToast(mContext, "不能为该节点添加关系!");
                                                } else {
                                                    mControlDialog.show();
                                                    dialogContral.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCheck() {
                                                Intent intent = new Intent(mContext, ZuPuAddMemberActivity.class);
                                                intent.putExtra("do", "check");
                                                intent.putExtra("user", user);
                                                intent.putParcelableArrayListExtra("punits", mUsers);
                                                startActivityForResult(intent, REQUEST_ADD_OR_EDIT_MEMBER);
                                                dialogContral.dismiss();
                                            }

                                            @Override
                                            public void onEdit() {
                                                Intent intent = new Intent(mContext, ZuPuAddMemberActivity.class);
                                                intent.putExtra("do", "edit");
                                                intent.putExtra("user", user);
                                                intent.putParcelableArrayListExtra("punits", mUsers);
                                                startActivityForResult(intent, REQUEST_ADD_OR_EDIT_MEMBER);
                                                dialogContral.dismiss();
                                            }
                                        };
                                        dialogContral.setContralListener(contralListener);
                                        dialogContral.show();

//                                        dialogEdit.show();
//                                        dialogCheck.show();


//                                        int clanRole = mApplication.getUser().getDefaultFamilyBaseInfo().getClanRole();
//                                        if (clanRole != ClanRoleEnum.NORMAL.getCode()) {
//                                            if (mControlDialog == null) {
//                                                mControlDialog = new DialogFamilyControl(mContext,v);
//                                            }
//                                            mControlDialog.setUser(user);
//                                            ClanCwNote clanCwNote = getClanCwNote(user);
//                                            mControlDialog.setClanCwJson(mClanCwJson);
//                                            mControlDialog.setPunits(mUsers);
//                                            mControlDialog.dismiss();
////                                            setDialogSize(mControlDialog, v);
//                                            if (!isCwPass(clanCwNote)) {
//                                                ToastUtils.shortToast(mContext, "不能为该节点添加关系!");
//                                            } else {
//                                                mControlDialog.show();
//                                            }
//                                        } else {
//
//                                            if (user.userId > 0) {
////                                                Intent intent = new Intent(mContext, FamilyMemberInfoActivity.class);
////                                                intent.putExtra("clanRole", clanRole);
////                                                intent.putExtra("memberInfo", SQLUtils.getFamilyMember(mClanId, user.userId));
////
////                                                startActivityForResult(intent, REQUEST_UPDATE_MEMBER);
//                                            }
//                                        }
                                    }
                                });
                                viewMap.put(user.unitId, view);
                                familyViewGroup.addView(view);
                            }
                            familyViewGroup.setData(mClanRela);
                            familyViewGroup.setViewMap(viewMap);
                            familyViewGroup.invalidate();
                        }

//                            //init views
//                            mUsers = getPUnits();
//
//                            int index = 1;
//                            Log.e(TAG, mUsers.size() + "");
//                            for (PUnit user : mUsers) {
//                                Button btn = new Button(mContext);
//                                Log.e(TAG, user.unitId + "");
//                                btn.setText(index++ + "");
//                                btn.setTag(user);
//                                familyViewGroup.addView(btn);
//                            }
//                            familyViewGroup.invalidate();
                    }
                });
            }
        });
    }

    private void setDialogSize(DialogFamilyControl dg, View v) {
        Window dialogWindow = dg.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);


//显示的坐标
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        lp.x = x - dg.geWidth() / 2;
        lp.y = y - dg.geHeight() / 2;
//dialog的大小
//        lp.width = 100;
//        lp.height = 100;
        dialogWindow.setAttributes(lp);
    }


    public ArrayList<PUnit> getPUnits() {
        ArrayList<PUnit> list = new ArrayList<>();
        int size = mClanRela.uIndex2Punit.size();
        for (Integer i = 1; i < size + 1; i++) {
            Long index = i.longValue();
            PUnit pUnit = mClanRela.uIndex2Punit.get(index);
            list.add(pUnit);
        }
        return list;
    }

    public ClanCwNote getClanCwNote(PUnit user) {
        for (int i = 0; i < mCwNoteListst.size(); i++) {
            ClanCwNote clanCwNote = mCwNoteListst.get(i);
            if (clanCwNote.getCwId().equals(user.cwId + "")) {
                return clanCwNote;
            }
        }
        return null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ADD_OR_EDIT_MEMBER:
                    freshZupu();
                    break;
            }
        }
    }

    //如果全部主空，则弹不可添加提示
    public boolean isCwPass(ClanCwNote clanCwNote) {
        boolean isPass = true;
        if (clanCwNote.getHusband() != null || clanCwNote.getWife() != null) {
            return true;
        }
        if (clanCwNote.getSon() != null) {
            return true;
        }
        if (clanCwNote.getDaughter() != null) {
            return true;
        }
        if (clanCwNote.getFather() != null) {
            return true;
        }

        if (clanCwNote.getMother() != null) {
            return true;
        }

        if (clanCwNote.getYoungBrother() != null || clanCwNote.getElderBrother() != null || clanCwNote.getElderSister() != null || clanCwNote.getYoungSister() != null) {
            return true;
        }
        return false;
//        return mCwPass;
    }

    @OnClick(R.id.title_view_back)
    public void onClick() {
        mContext.finish();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        for (DialogZupuContral dialogContral : mDialogZupuContrals) {
            if (dialogContral.isShowing()) {
                dialogContral.dismiss();
            }
        }
    }
}