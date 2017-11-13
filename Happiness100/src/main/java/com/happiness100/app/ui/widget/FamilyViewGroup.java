package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/8/26.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.happiness100.app.model.ClanCwNote;
import com.happiness100.app.model.ClanRela;
import com.happiness100.app.model.ClanRela.FamilyUnit;
import com.happiness100.app.model.ClanRela.PUnit;
import com.happiness100.app.utils.AssetsUtils;
import com.happiness100.app.utils.FamilyDrawUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：justin on 2016/8/26 11:30
 */
public class FamilyViewGroup extends ViewGroup {

    private static final int HORIZONTALSPACING = 20;
    private static final int VERTICAL = 100;
    private static final String TAG = "FamilyViewGroup";
    private static final int FATHER_TYPE = 1;
    private static final int MOTHER_TYPE = 2;
    Context context;
    private ClanRela mClanRela;
    private PUnit user;
    FamilyDrawUtils mFamilyDrawUtils;
    private Map<Long, View> mViewMap = new HashMap<>();
    private int mCommonViewWidth;
    private int mCommonViewHeight;

    private Map<Integer, List<String>> midLineMap = new HashMap<>();//记录中间线的MAP ,KEY 是家长的辈份

    List<String> mMidLines = new ArrayList<>();

    public FamilyViewGroup(Context context) {
        super(context);
        this.context = context;
    }

    public FamilyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FamilyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */

        //设置界面大小
//        setMeasuredDimension(10000,10000);
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : sizeWidth, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : sizeWidth);
    }

    // abstract method in viewgroup
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        layout(l-200,t-200,r+200,b+200);
        initViewTagMap();
        initViewMap();

        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        View view = getChildAt(0);
        if (view == null) {
            return;
        }
        initBeifenLeftAndRight(view);

        PUnit creater = (PUnit) view.getTag();
        int left = getMeasuredWidth() / 2;
        int top = layoutParamsMap.get(creater.bfIndex).top;
        int right = left + view.getMeasuredWidth();
        int bottom = layoutParamsMap.get(creater.bfIndex).bottom;
        view.layout(left, top, right, bottom);
        viewDrawTag.put(view.getId(), true);
        layoutParamsMap.get(creater.bfIndex).left = left;
        layoutParamsMap.get(creater.bfIndex).right = right;
        mViewMap.put(creater.unitId, view);

        View temp = mViewMap.get(creater.unitId);
        startDraw(creater, FATHER_TYPE);

        //画老婆，奶奶，曾奶奶的线
        PUnit mother = mFamilyDrawUtils.getMother(creater);
        if (mother != null) {
            startDrawMotherLine(mother);
        }
        PUnit wife = mFamilyDrawUtils.getMate(creater);
        if (wife!=null&&!isMan(wife)) {
            startDrawMotherLine(wife);
        }

    }

    List<Long> mAlreadyDrawUsers = new ArrayList<>();
    int layoutCount = 0;

    private void startDraw(PUnit user, int type) {
        if (user == null)
            return;
        mAlreadyDrawUsers.add(user.unitId);

        View view = mViewMap.get(user.unitId);
        PUnit father = mFamilyDrawUtils.getFater(user);
        PUnit mother = mFamilyDrawUtils.getMother(user);
        List<Long> broIds = mFamilyDrawUtils.getBrothers(user);//兄弟姐妹

        PUnit mate = mFamilyDrawUtils.getMate(user);

        try {
            if (mate != null) {
                if (!viewDrawTag.get((int) mate.unitId)) {
                    drawMate(user, view);
                }
            }

            //判断有没有上家庭，有上家庭但ID是0的就先画格子
            FamilyUnit uFamily = mClanRela.familyUnitMap.get(user.uFaId);
            if (father != null) {
                if (!viewDrawTag.get((int) father.unitId)) {
                    drawfaterAndMother(user, FATHER_TYPE);
                }
            }
            drawChild(user);

            drawBrothers(user, view);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
            e.printStackTrace();
        }

        if (father != null) {
            startDraw(father, type);
        }
    }

    private boolean isAlreadyDraw(PUnit user) {
        for (long drawId : mAlreadyDrawUsers) {
            if (user.unitId == drawId) {
            }
        }
        return false;
    }


    Map<Integer, MyLayoutParams> layoutParamsMap = new HashMap<>();


    private void drawfaterAndMother(PUnit me, int type) throws Exception {
        PUnit fater = mFamilyDrawUtils.getFater(me);
        PUnit mother = mFamilyDrawUtils.getMother(me);
        View userView = mViewMap.get(me.unitId);
        int fLeft = 0;
        int fRight = 0;
        int fTop = 0;
        int fBottom = 0;

        if (fater != null) {
            View view = mViewMap.get(fater.unitId);

            //获取当前辈份 最左边的坐标
            Log.e(TAG, "mFamilyDrawUtils:" + mFamilyDrawUtils + " ");
            int currentLeft = layoutParamsMap.get(fater.bfIndex).left;

            //如果是直系，直接画在创建者上面
            if (mFamilyDrawUtils.isCreaterFather(fater.unitId,1)) {
                fLeft = currentLeft;
            } else {
                if (mother != null) {
                    fLeft = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth() * 2;
                } else {
                    fLeft = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth();
                }
            }
            fTop = userView.getTop() - VERTICAL - view.getMeasuredHeight();
            fRight = fLeft + view.getMeasuredWidth();
            fBottom = userView.getTop() - VERTICAL;


            if (!viewDrawTag.get(view.getId())) {
                view.layout(fLeft, fTop, fRight, fBottom);
                viewDrawTag.put(view.getId(), true);
                layoutParamsMap.get(fater.bfIndex).left = fLeft;
                if (layoutParamsMap.get(fater.bfIndex).right < fRight) {
                    layoutParamsMap.get(fater.bfIndex).right = fRight;
                }
            }
        }

        if (mother != null) {
            View motherView = mViewMap.get(mother.unitId);
            //获取当前辈份 最左边的坐标
            if (fater != null) {
                int mLeft = fRight;
                int mTop = fTop;
                int mRight = fRight + motherView.getMeasuredWidth();
                int mBottom = fBottom;

                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    if (layoutParamsMap.get(fater.bfIndex).right < mRight) {
                        layoutParamsMap.get(fater.bfIndex).right = mRight;
                    }
                }
            } else {
                int currentLeft = layoutParamsMap.get(mother.bfIndex).left;

                int mLeft = currentLeft - HORIZONTALSPACING - motherView.getMeasuredWidth();
                int mTop = userView.getTop() - VERTICAL - motherView.getMeasuredHeight();
                int mRight = currentLeft - HORIZONTALSPACING;
                int mBottom = userView.getTop() - VERTICAL;
                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    if (layoutParamsMap.get(fater.bfIndex).right < mRight) {
                        layoutParamsMap.get(fater.bfIndex).right = mRight;
                    }
                }
            }
            //画妈妈这条线
        }
    }


    private void drawfaterAndMotherRight(PUnit me, int type) throws Exception {
        PUnit fater = mFamilyDrawUtils.getFater(me);
        PUnit mother = mFamilyDrawUtils.getMother(me);
        View userView = mViewMap.get(me.unitId);
        int fLeft = 0;
        int fRight = 0;
        int fTop = 0;
        int fBottom = 0;

        if (fater != null) {
            View view = mViewMap.get(fater.unitId);

            //获取当前辈份 最左边的坐标
            Log.e(TAG, "mFamilyDrawUtils:" + mFamilyDrawUtils + " ");
            int currentRight = layoutParamsMap.get(fater.bfIndex).right;
            fLeft = currentRight + HORIZONTALSPACING;

            fTop = userView.getTop() - VERTICAL - view.getMeasuredHeight();
            fRight = fLeft + view.getMeasuredWidth();
            fBottom = userView.getTop() - VERTICAL;


            if (!viewDrawTag.get(view.getId())) {
                view.layout(fLeft, fTop, fRight, fBottom);
                viewDrawTag.put(view.getId(), true);
                if (layoutParamsMap.get(fater.bfIndex).right < fRight) {
                    layoutParamsMap.get(fater.bfIndex).right = fRight;
                }
            }
        }

        if (mother != null) {
            View motherView = mViewMap.get(mother.unitId);
            //获取当前辈份 最左边的坐标
            if (fater != null) {
                int mLeft = fRight;
                int mTop = fTop;
                int mRight = fRight + motherView.getMeasuredWidth();
                int mBottom = fBottom;

                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    if (layoutParamsMap.get(fater.bfIndex).right < mRight) {
                        layoutParamsMap.get(fater.bfIndex).right = mRight;
                    }
                }
            } else {
                int currentLeft = layoutParamsMap.get(mother.bfIndex).left;
                int mLeft = currentLeft - HORIZONTALSPACING - motherView.getMeasuredWidth();
                int mTop = userView.getTop() - VERTICAL - motherView.getMeasuredHeight();
                int mRight = currentLeft - HORIZONTALSPACING;
                int mBottom = userView.getTop() - VERTICAL;
                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    if (layoutParamsMap.get(fater.bfIndex).right < mRight) {
                        layoutParamsMap.get(fater.bfIndex).right = mRight;
                    }
                }
            }

            //画妈妈这条线

        }
    }


    private void startDrawMotherLine(PUnit user) {

        View view = mViewMap.get(user.unitId);
        PUnit father = mFamilyDrawUtils.getFater(user);


        PUnit mate = mFamilyDrawUtils.getMate(user);

        try {
            if (mate != null) {
                if (!viewDrawTag.get((int) mate.unitId)) {
                    drawMate(user, view);
                }
            }

            //判断有没有上家庭，有上家庭但ID是0的就先画格子
            FamilyUnit uFamily = mClanRela.familyUnitMap.get(user.uFaId);
            if (father != null) {
                if (!viewDrawTag.get((int) father.unitId)) {
                    drawfaterAndMotherRight(user, FATHER_TYPE);
                }
            }

            drawBrothersRight(user, view);

//            drawChild(user);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + "");
            e.printStackTrace();
        }

        if (father != null) {
            startDrawMotherLine(father);
        }
    }


    private void drawfaterAndMother1(PUnit me, int type) throws Exception {
        PUnit fater = mFamilyDrawUtils.getFater(me);
        PUnit mother = mFamilyDrawUtils.getMother(me);
        View userView = mViewMap.get(me.unitId);
        int fLeft = 0;
        int fRight = 0;
        int fTop = 0;
        int fBottom = 0;

        if (fater != null) {
            View view = mViewMap.get(fater.unitId);

            //获取当前辈份 最左边的坐标
            Log.e(TAG, "mFamilyDrawUtils:" + mFamilyDrawUtils + " ");
            int currentLeft = layoutParamsMap.get(fater.bfIndex).left;

            fLeft = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth();
            fTop = userView.getTop() - VERTICAL - view.getMeasuredHeight();
            fRight = currentLeft - HORIZONTALSPACING;
            fBottom = userView.getTop() - VERTICAL;

            if (!viewDrawTag.get(view.getId())) {
                view.layout(fLeft, fTop, fRight, fBottom);
                viewDrawTag.put(view.getId(), true);
                layoutParamsMap.get(fater.bfIndex).left = fLeft;
            }
        }

        if (mother != null) {
            View motherView = mViewMap.get(mother.unitId);
            //获取当前辈份 最左边的坐标
            if (fater != null) {
                int mLeft = fRight;
                int mTop = fTop;
                int mRight = fRight + motherView.getMeasuredWidth();
                int mBottom = fBottom;

                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    layoutParamsMap.get(fater.bfIndex).right = mRight;
                }
            } else {
                int currentLeft = layoutParamsMap.get(mother.bfIndex).left;
                int mLeft = currentLeft - HORIZONTALSPACING - motherView.getMeasuredWidth();
                int mTop = userView.getTop() - VERTICAL - motherView.getMeasuredHeight();
                int mRight = currentLeft - HORIZONTALSPACING;
                int mBottom = userView.getTop() - VERTICAL;
                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    layoutParamsMap.get(fater.bfIndex).right = mRight;
                }
            }
        }
    }


    //母亲的线
    private void drawfaterAndMotherWithMotherLine(PUnit me) throws Exception {
        PUnit fater = mFamilyDrawUtils.getFater(me);
        PUnit mother = mFamilyDrawUtils.getMother(me);
        View userView = mViewMap.get(me.unitId);
        int fLeft = 0;
        int fRight = 0;
        int fTop = 0;
        int fBottom = 0;

        if (fater != null) {
            View view = mViewMap.get(fater.unitId);

            //获取当前辈份 最左边的坐标
            Log.e(TAG, "mFamilyDrawUtils:" + mFamilyDrawUtils + " ");
            int cuurentRight = layoutParamsMap.get(fater.bfIndex).right;

            fLeft = cuurentRight + HORIZONTALSPACING;
            fTop = userView.getTop() - VERTICAL - view.getMeasuredHeight();
            fRight = fLeft + view.getMeasuredWidth();
            fBottom = fTop + view.getMeasuredHeight();

            if (!viewDrawTag.get(view.getId())) {
                view.layout(fLeft, fTop, fRight, fBottom);
                viewDrawTag.put(view.getId(), true);
                layoutParamsMap.get(fater.bfIndex).right = fRight;
            }
        }

        if (mother != null) {
            View motherView = mViewMap.get(mother.unitId);
            //获取当前辈份 最左边的坐标
            if (fater != null) {
                int mLeft = fRight;
                int mTop = fTop;
                int mRight = mLeft + motherView.getMeasuredWidth();
                int mBottom = fBottom;

                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    layoutParamsMap.get(fater.bfIndex).right = mRight;
                    drawChildMotherLine(fater);
                }
            } else {
                int currentLeft = layoutParamsMap.get(mother.bfIndex).left;
                int mLeft = currentLeft - HORIZONTALSPACING - motherView.getMeasuredWidth();
                int mTop = userView.getTop() - VERTICAL - motherView.getMeasuredHeight();
                int mRight = currentLeft - HORIZONTALSPACING;
                int mBottom = userView.getTop() - VERTICAL;
                if (!viewDrawTag.get(motherView.getId())) {
                    motherView.layout(mLeft, mTop, mRight, mBottom);
                    viewDrawTag.put(motherView.getId(), true);
                    layoutParamsMap.get(fater.bfIndex).right = mRight;
                }
            }
        }
    }


    Map<Integer, Boolean> viewDrawTag = new HashMap<>();

    private void drawMate(PUnit user, View userView) throws Exception {
        PUnit mate = mFamilyDrawUtils.getMate(user);
        View view = mViewMap.get(mate.unitId);
        int left = 0;
        int right = 0;
        if (isMan(mate)) {
            left = userView.getLeft() - userView.getMeasuredWidth();
        } else {
            left = userView.getRight();
        }
        right = left + userView.getMeasuredWidth();
        int top = userView.getTop();
        int bottom = userView.getBottom();
        Log.e(TAG, "view.getId()" + view.getId());
        if (!viewDrawTag.get(view.getId())) {
            view.layout(left, top, right, bottom);
            viewDrawTag.put(view.getId(), true);
            if (user.unitId == 1) {
                layoutParamsMap.get(mate.bfIndex).right = right;
            }
        }
    }

    private void drawMateRight(PUnit user, View userView) throws Exception {
        PUnit mate = mFamilyDrawUtils.getMate(user);
        View view = mViewMap.get(mate.unitId);
        int left = 0;
        int right = 0;
        if (isMan(user)) {
            left = userView.getRight();
        } else {
            left = userView.getRight() + userView.getMeasuredWidth();
        }
        right = left + userView.getMeasuredWidth();
        int top = userView.getTop();
        int bottom = userView.getBottom();
        Log.e(TAG, "view.getId()" + view.getId());
        if (!viewDrawTag.get(view.getId())) {
            view.layout(left, top, right, bottom);
            viewDrawTag.put(view.getId(), true);
            if (layoutParamsMap.get(mate.bfIndex).right < right) {
                layoutParamsMap.get(mate.bfIndex).right = right;
            }
        }
    }


    private boolean isMan(PUnit mate) {

        if (!((mate.rank + "").length() == 3)) {
            return true;
        }
        return false;
    }


    private void drawBrothers(PUnit user, View userView) throws Exception {
        List<Long> ids = mFamilyDrawUtils.getBrothers(user);
        if (ids == null)
            return;
        //获取当前辈份 最左边的坐标

        int top = layoutParamsMap.get(user.bfIndex).top;
        int bottom = layoutParamsMap.get(user.bfIndex).bottom;
        for (Long id : ids) {

            //如果之前是画过了，就返回
            if (id != user.unitId) {
                View view = mViewMap.get(id);
                if (viewDrawTag.get(view.getId())) {
                    return;
                }
            } else {
                //如果是创建者的对象返回
                if (id == (mClanRela.creater)) {
                    PUnit mate = mFamilyDrawUtils.getMate(mClanRela.uIndex2Punit.get(mClanRela.creater));
                    View view = mViewMap.get(mate.unitId);
                    if (viewDrawTag.get(view.getId())) {
                        return;
                    }
                }
            }

            View view = mViewMap.get(id);
            PUnit currentUser = mClanRela.uIndex2Punit.get(id);
            PUnit mate = mFamilyDrawUtils.getMate(currentUser);
            int currentLeft = layoutParamsMap.get(user.bfIndex).left;
            if (mate != null) {
                int left = 0;
                int right = 0;
                if (isMan(mate)) {
                    left = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth() * 1;
                    right = left + userView.getMeasuredWidth();
                } else {
                    left = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth() * 2;
                    right = left + userView.getMeasuredWidth();
                }
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    viewDrawTag.put(view.getId(), true);
                    layoutParamsMap.get(user.bfIndex).left = left;
                }

                drawMate(currentUser, view);
                if (mate.uFaId != 0) {
//                   drawfaterAndMother(mate, FATHER_TYPE);
                    startDraw(mate, FATHER_TYPE);
                }
            } else {
                int left = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth();
                int right = currentLeft - HORIZONTALSPACING;
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    layoutParamsMap.get(user.bfIndex).left = left;
                    viewDrawTag.put(view.getId(), true);
                }
            }
            drawChild(currentUser);//画兄弟姐妹的孩子
        }
    }


    private void drawBrothersRight(PUnit user, View userView) throws Exception {
        List<Long> ids = mFamilyDrawUtils.getBrothers(user);
        if (ids == null)
            return;
        //获取当前辈份 最左边的坐标

        int top = layoutParamsMap.get(user.bfIndex).top;
        int bottom = layoutParamsMap.get(user.bfIndex).bottom;
        for (Long id : ids) {

            //如果之前是画过了，就返回
            if (id != user.unitId) {
                View view = mViewMap.get(id);
                if (viewDrawTag.get(view.getId())) {
                    return;
                }
            }

            View view = mViewMap.get(id);
            PUnit currentUser = mClanRela.uIndex2Punit.get(id);
            PUnit mate = mFamilyDrawUtils.getMate(currentUser);
            int currentRight = layoutParamsMap.get(user.bfIndex).right;
            if (mate != null) {
                int left = 0;
                int right = 0;
                if (isMan(currentUser)) {
                    left = currentRight + HORIZONTALSPACING;
                    right = left + userView.getMeasuredWidth();
                } else {
                    left = currentRight + HORIZONTALSPACING + view.getMeasuredWidth();
                    right = left + userView.getMeasuredWidth();
                }
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    viewDrawTag.put(view.getId(), true);
                    if (layoutParamsMap.get(user.bfIndex).right < right) {
                        layoutParamsMap.get(user.bfIndex).right = right;
                    }
                }

                drawMateRight(currentUser, view);
                if (mate.uFaId != 0) {
                    startDrawMotherLine(mate);
                }
            } else {
                int left = currentRight + HORIZONTALSPACING;
                int right = left + view.getMeasuredWidth();
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    if (layoutParamsMap.get(user.bfIndex).right < right) {
                        layoutParamsMap.get(user.bfIndex).right = right;
                    }
                    viewDrawTag.put(view.getId(), true);
                }
            }
            drawChildRight(currentUser);//画兄弟姐妹的孩子
        }
    }


    private void drawChild(PUnit user) throws Exception {
        List<Long> ids = mFamilyDrawUtils.getChilds(user);
        //获取 子辈份 最左边的坐标
        if (ids == null || ids.size() == 0) {
            return;
        }

        int top = layoutParamsMap.get(user.bfIndex - 1).top;
        int bottom = layoutParamsMap.get(user.bfIndex - 1).bottom;

        int index = 0;
        for (Long id : ids) {

            View view = mViewMap.get(id);
            PUnit currentUser = mClanRela.uIndex2Punit.get(id);

            int currentLeft = layoutParamsMap.get(user.bfIndex - 1).left;
            PUnit mate = mFamilyDrawUtils.getMate(currentUser);
            if (mate != null) {
                int left = 0;
                int right = 0;

                //如果是创建者直系 直接画
                if (isMan(currentUser)) {
                    left = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth() * 2;
                } else {
                    left = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth();
                }
                right = left + view.getMeasuredWidth();
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    viewDrawTag.put(view.getId(), true);
                    if (layoutParamsMap.get(user.bfIndex - 1).left > left) {
                        layoutParamsMap.get(user.bfIndex - 1).left = left;
                    }

                }
                drawMate(currentUser, view);

            } else {
                int left = currentLeft - HORIZONTALSPACING - view.getMeasuredWidth();
                int right = currentLeft - HORIZONTALSPACING;
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    layoutParamsMap.get(user.bfIndex - 1).left = left;
                    viewDrawTag.put(view.getId(), true);
                }
            }
            drawChild(currentUser);
        }
    }

    private void drawChildRight(PUnit user) throws Exception {
        List<Long> ids = mFamilyDrawUtils.getChilds(user);
        //获取 子辈份 最左边的坐标
        if (ids == null || ids.size() == 0) {
            return;
        }

        int top = layoutParamsMap.get(user.bfIndex - 1).top;
        int bottom = layoutParamsMap.get(user.bfIndex - 1).bottom;

        for (Long id : ids) {

            View view = mViewMap.get(id);
            PUnit currentUser = mClanRela.uIndex2Punit.get(id);

            int currentRight = layoutParamsMap.get(user.bfIndex - 1).right;
            PUnit mate = mFamilyDrawUtils.getMate(currentUser);
            if (mate != null) {
                int left = 0;
                int right = 0;
                if (isMan(currentUser)) {
                    left = currentRight + HORIZONTALSPACING;
                } else {
                    left = currentRight + HORIZONTALSPACING - view.getMeasuredWidth();
                }
                right = left + view.getMeasuredWidth();
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    viewDrawTag.put(view.getId(), true);
                    if (layoutParamsMap.get(user.bfIndex - 1).right < right) {
                        layoutParamsMap.get(user.bfIndex - 1).right = right;
                    }
                }
                drawMate(currentUser, view);
            } else {
                int left = currentRight + HORIZONTALSPACING;
                int right = left + view.getMeasuredWidth();
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    if (layoutParamsMap.get(user.bfIndex - 1).right < right) {
                        layoutParamsMap.get(user.bfIndex - 1).right = right;
                    }
                    viewDrawTag.put(view.getId(), true);
                }
            }
            drawChildRight(currentUser);
        }
    }

    private void drawChildMotherLine(PUnit user) throws Exception {
        List<Long> ids = mFamilyDrawUtils.getChilds(user);
        //获取 子辈份 最左边的坐标
        if (ids == null || ids.size() == 0) {
            return;
        }

        int top = layoutParamsMap.get(user.bfIndex - 1).top;
        int bottom = layoutParamsMap.get(user.bfIndex - 1).bottom;

        for (Long id : ids) {

            View view = mViewMap.get(id);
            PUnit currentUser = mClanRela.uIndex2Punit.get(id);
            int currentRight = layoutParamsMap.get(user.bfIndex - 1).right;
            PUnit mate = mFamilyDrawUtils.getMate(currentUser);
            if (mate != null) {
                int left = 0;
                int right = 0;
                if (!isMan(mate)) {
                    left = currentRight + HORIZONTALSPACING;
                } else {
                    left = currentRight + HORIZONTALSPACING + view.getMeasuredWidth();
                }
                right = left + view.getMeasuredWidth();
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    viewDrawTag.put(view.getId(), true);
                    drawChildMotherLine(currentUser);
                    layoutParamsMap.get(user.bfIndex - 1).right = right;
                }
                drawMate(user, view);
            } else {
                int left = currentRight + HORIZONTALSPACING;
                int right = left + view.getMeasuredWidth();
                if (!viewDrawTag.get(view.getId())) {
                    view.layout(left, top, right, bottom);
                    layoutParamsMap.get(user.bfIndex - 1).left = left;
                    viewDrawTag.put(view.getId(), true);
                }
            }
        }
    }

    private void drawMe(PUnit me) {
        View view = mViewMap.get(me.unitId);
        int left = getMeasuredWidth() / 2 - view.getMeasuredWidth() / 2;
        int top = getMeasuredHeight() / 2 - view.getMeasuredHeight();
        int right = getMeasuredWidth() / 2 - view.getMeasuredWidth() / 2;
        int bottom = getMeasuredHeight() / 2 + view.getMeasuredWidth() / 2;
        view.layout(left, top, right, bottom);
        //判断是否有配偶
    }

    private void addChildView(PUnit me) {
        View view = new View(context);
//        view.setTag();
    }

    private void layout(int childCount) {

        ClanRela clanRela = new ClanRela();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            //获取View 对象
            PUnit pUnit = (PUnit) view.getTag();
            //
        }
    }

    Paint mPaint;

    void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.rgb(66, 145, 241));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        if (mClanRela != null) {
            for (long i = 1; i < mClanRela.familyUnitMap.size() + 1; i++) {
                drawFamilyRelation(canvas, mClanRela.familyUnitMap.get(i));
            }
        }
//        layout(getLeft()-1000, getTop()-1000, getRight()+1000, getBottom()+1000);
    }

    private void drawFamilyRelation(Canvas canvas, FamilyUnit familyUnit) {
        int startX = 0;
        int startY = 0;

        int bfIndex = mFamilyDrawUtils.getBeifen(familyUnit);//辈分

        //获取这个辈份目前的中间线
        List<String> midLines = midLineMap.get(bfIndex);

        MyLayoutParams layoutParams = layoutParamsMap.get(bfIndex);//该辈份的布局参数

        if (familyUnit.father != 0) {
            View fatherView = mViewMap.get(familyUnit.father);
            if (!viewDrawTag.get(fatherView.getId())) {
                return;
            }
            if (familyUnit.mother != 0) {
                startX = fatherView.getRight();
                startY = fatherView.getBottom();
            } else {
                startX = fatherView.getRight() - fatherView.getMeasuredWidth() / 2;
                startY = fatherView.getBottom();
            }
        } else if (familyUnit.mother != 0) {
            View motherView = mViewMap.get(familyUnit.mother);
            startX = motherView.getRight() - motherView.getMeasuredWidth() / 2;
            startY = motherView.getBottom();
        }
        List<Long> childIds = familyUnit.children;

        int endX = 0;
        int endY = 0;


        int midY = 0;
        //开始画一个家庭线之前，判断获取一个中间线
        if (midLines == null) {
            midY = layoutParams.bottom + VERTICAL / 2;
            midLines = new ArrayList<>();
            midLines.add(midY + "");
            midLineMap.put(bfIndex, midLines);
        } else {
            midY = getRandomMidY(midLines, layoutParams);
            midLines.add(midY + "");
            midLineMap.put(bfIndex, midLines);
        }


        if (childIds != null && childIds.size() > 0) {
            for (int i = 0; i < childIds.size(); i++) {
                View childView = mViewMap.get(childIds.get(i));
                if (!viewDrawTag.get(childView.getId())) {
                    continue;
                }
                endX = childView.getRight() - childView.getMeasuredWidth() / 2;
                endY = childView.getTop();


                //画完一个家庭后记录中间线的位置
                if (i == childIds.size() - 1) {
                    mMidLines.add("" + midY);
                }

                canvas.drawLine(startX, startY, startX, midY, mPaint);
                canvas.drawLine(startX, midY, endX, midY, mPaint);
                canvas.drawLine(endX, midY, endX, endY, mPaint);

                mMidLines.add(familyUnit.father + familyUnit.mother + "_" + midY + "");
            }
        }
        mPaint = getRandomPaint(mPaint);
    }

    private int getRandomMidY(List<String> midLines, MyLayoutParams layoutParams) {
        int randonHeight = ((Double) (Math.random() * (VERTICAL - 30))).intValue();//获取随机高度
        int midY = layoutParams.bottom + randonHeight;

        //如果线已经存在，或者相距太近，重新画
        if (midLines.contains(midY + "")) {
            return getRandomMidY(midLines, layoutParams);
        }
        for (String line : midLines) {
            int lineInt = Integer.parseInt(line);
            if (Math.abs(lineInt - midY) < 2) {
                return getRandomMidY(midLines, layoutParams);
            }
        }
        return midY;
    }


    public void readFamilyCw() {
        Type type = new TypeToken<ArrayList<ClanCwNote>>() {
        }.getType();
        String json = AssetsUtils.getFromAssets(context, "clan_cw.json");
        try {
            JSONArray jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        List<ClanCwNote> list = GsonUtils.parseJSONArray(getFromAssets("clan_cw.json"), type);
    }

    public void setData(ClanRela data) {
        mClanRela = data;
        mFamilyDrawUtils = new FamilyDrawUtils(mClanRela);
    }

    public void initViewTagMap() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            viewDrawTag.put((Integer) view.getId(), new Boolean(false));
        }
    }

    private void initBeifenLeftAndRight(View view) {
        for (Integer i = 20; i > -20; i--) {
            MyLayoutParams layoutParams = new MyLayoutParams();
            layoutParams.left = getMeasuredWidth() / 2;
            layoutParams.right = layoutParams.left + view.getMeasuredWidth();
            layoutParams.top = getTopByBf(i, view);
            layoutParams.bottom = layoutParams.top + view.getMeasuredHeight();
            layoutParamsMap.put(i, layoutParams);
        }
        mCommonViewWidth = view.getMeasuredWidth();
        mCommonViewHeight = view.getMeasuredHeight();
    }

    private int getTopByBf(Integer i, View view) {
        int hegit = getMeasuredHeight();
        int viewHight = view.getMeasuredHeight();
        int top = 0;
        top = hegit / 2 - ((VERTICAL + viewHight) * i) - viewHight / 2;
        return top;
    }


    private void initViewMap() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view == null) {
                continue;
            }
            PUnit pUnit = (PUnit) view.getTag();
            mViewMap.put(pUnit.unitId, view);
        }
    }

    public void setViewMap(Map<Long, View> viewMap) {
        this.mViewMap = viewMap;
    }

    public Paint getRandomPaint(Paint paint) {
        paint.setColor(Color.rgb(((Double) (Math.random() * 255)).intValue(), ((Double) (Math.random() * 255)).intValue(), ((Double) (Math.random() * 255)).intValue()));
        return paint;
    }

    private class MyLayoutParams {
        public int left;
        public int right;
        public int top;
        public int bottom;
    }
}
