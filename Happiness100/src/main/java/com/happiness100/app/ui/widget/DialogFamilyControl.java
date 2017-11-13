package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/9/9.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.happiness100.app.App;
import com.happiness100.app.R;
import com.happiness100.app.model.ClanCwNote;
import com.happiness100.app.model.ClanRela.PUnit;
import com.happiness100.app.network.ResponeInterface;
import com.happiness100.app.ui.BaseViewInterface;
import com.happiness100.app.ui.activity.BaseActivity;
import com.happiness100.app.ui.activity.ZuPuAddMemberActivity;
import com.happiness100.app.ui.bean.RelationshipEnum;
import com.happiness100.app.utils.APIClient;
import com.justin.utils.BaseVolleyListener;
import com.justin.utils.GsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：justin on 2016/9/9 10:59
 */
public class DialogFamilyControl extends Dialog implements BaseViewInterface, DialogInterface.OnShowListener {
    private View mView;
    BaseActivity mContext;
    @Bind(R.id.view_add_father)
    View  viewAddFather;
    @Bind(R.id.view_add_wife)
    View viewAddWife;
    @Bind(R.id.view_add_sun)
    View viewAddSun;
    @Bind(R.id.view_add_mother)
    View viewAddMother;
    @Bind(R.id.view_add_brother)
    View viewAddBrother;
    @Bind(R.id.view_add_dorther)
    View viewAddDorther;
    @Bind(R.id.dialog_root)
    RelativeLayout dialogRoot;
    private PUnit mUser;
    private String mClanCwJson;
    private List<ClanCwNote> mCwNoteListst;
    private ClanCwNote mClanCwNote;
    ArrayList<PUnit> mPunits;

    public DialogFamilyControl(BaseActivity context, View v) {
        super(context, R.style.Theme_FAMILY_CONTROL_DIALOG);
        mView = v;
        mContext = context;
    }


    public void setUser(PUnit user) {
        mUser = user;
    }

    public DialogFamilyControl(Context context, int themeResId) {
        super(context, R.style.Theme_FAMILY_CONTROL_DIALOG);
    }

    protected DialogFamilyControl(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_family_control);
        ButterKnife.bind(this);
        initView();

        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        dialogRoot.measure(w, h);
        setDialogSize();
        setOnShowListener(this);
    }

    public int geWidth() {
        return dialogRoot.getMeasuredWidth();
    }

    public int geHeight() {
        return dialogRoot.getMeasuredHeight();
    }


    private void setDialogSize() {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);


//显示的坐标
        int[] location = new int[2];
        mView.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        lp.x = x - geWidth() / 2 + mView.getMeasuredWidth() / 2;
        lp.y = y - geHeight() / 2;
//dialog的大小
//        lp.width = 100;
//        lp.height = 100;
        dialogWindow.setAttributes(lp);
    }


    @OnClick({R.id.view_mid_line, R.id.view_add_father, R.id.view_add_wife, R.id.view_add_mother, R.id.view_add_brother, R.id.view_add_dorther, R.id.view_add_sun})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.view_mid_line:
//                addUnit(mUser.unitId,7);
                break;
            case R.id.view_add_father:
                gotoAddZuPuMember(RelationshipEnum.FATHER.getCode());
//                addUnit(mUser, 3);
                break;
            case R.id.view_add_wife:
//                addUnit(mUser, 2);
                gotoAddZuPuMember(RelationshipEnum.WIFE.getCode());
                break;
            case R.id.view_add_mother:
//                addUnit(mUser, 4);
                gotoAddZuPuMember(RelationshipEnum.MOTHER.getCode());

                break;
            case R.id.view_add_brother:
//                addUnit(mUser, 5);
                gotoAddZuPuMember(RelationshipEnum.BROTHER.getCode());
                break;
            case R.id.view_add_dorther:
//                addUnit(mUser, 6);
//                addMyself();
                gotoAddZuPuMember(RelationshipEnum.DOUGHTER.getCode());
                break;
            case R.id.view_add_sun:
                gotoAddZuPuMember(RelationshipEnum.SUN.getCode());
//                addUnit(mUser, 6);
//                addMyself();
                break;
        }
    }

    private void gotoAddZuPuMember(int type) {
        Intent intent = new Intent(mContext, ZuPuAddMemberActivity.class);
        intent.putExtra("opType", type);
        intent.putExtra("user", mUser);
        intent.putParcelableArrayListExtra("punits", mPunits);

        this.dismiss();
        mContext.startActivityForResult(intent, 1);
    }

    private void addUnit(PUnit user, int optType) {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", "5");
        params.put("rank", user.rank + "");
        params.put("name_note", user.nameNote + "");
        params.put("opt_type", optType + "");
        params.put("unit_id", user.unitId + "");
        params.put("d_user_id", user.dUserId + "");
        params.put("cw", user.cw);

        APIClient.addZupuMember(mContext, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, false, "")) {
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

                    }
                });
            }
        });
    }


    private void addMyself() {
        Map<String, String> params = new HashMap<>();
        params.put("clan_id", "5");
        params.put("rank", "201");
        params.put("name_note", "梁星海");
        params.put("opt_type", 7 + "");
        params.put("unit_id", ((App) mContext.getApplication()).getUser().getXf() + "");
        params.put("d_user_id", ((App) mContext.getApplication()).getUser().getXf() + "");
        params.put("cw", "创建者");

        APIClient.addZupuMember(mContext, params, new BaseVolleyListener(mContext, new LoadDialog(mContext, false, "")) {
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

                    }
                });
            }
        });
    }

    @Override
    public void initView() {


        if (mUser.rank != 0 && (mUser.rank + "").length() == 3) {
//            viewAddWife.setText("妻子");
        } else {
//            viewAddWife.setText("丈夫");
        }
    }

    public void hideCw() {
        if (viewAddWife == null) {
            return;
        }
        Type type = new TypeToken<ArrayList<ClanCwNote>>() {
        }.getType();
        mCwNoteListst = GsonUtils.parseJSONArray(mClanCwJson, type);
        mClanCwNote = getClanCwNote();

        if (mClanCwNote.getHusband() == null && mClanCwNote.getWife() == null) {
            viewAddWife.setVisibility(View.GONE);
        } else {
            viewAddWife.setVisibility(View.VISIBLE);
        }
        if (mClanCwNote.getSon() == null) {
            viewAddSun.setVisibility(View.GONE);
        } else {
            viewAddSun.setVisibility(View.VISIBLE);
        }
        if (mClanCwNote.getDaughter() == null) {
            viewAddDorther.setVisibility(View.GONE);
        } else {
            viewAddDorther.setVisibility(View.VISIBLE);
        }
        if (mClanCwNote.getFather() == null) {
            viewAddFather.setVisibility(View.GONE);
        } else {
            viewAddFather.setVisibility(View.VISIBLE);
        }

        if (mClanCwNote.getMother() == null) {
            viewAddMother.setVisibility(View.GONE);
        } else {
            viewAddMother.setVisibility(View.VISIBLE);
        }

        if (mClanCwNote.getYoungBrother() == null && mClanCwNote.getElderBrother() == null && mClanCwNote.getElderSister() == null && mClanCwNote.getYoungSister() == null) {
            viewAddBrother.setVisibility(View.GONE);
        } else {
            viewAddBrother.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void initData() {

    }

    public void setClanCwJson(String clanCwJson) {
        mClanCwJson = clanCwJson;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        hideCw();
    }

    public void setPunits(ArrayList<PUnit> punits) {
        mPunits = punits;
    }
}
