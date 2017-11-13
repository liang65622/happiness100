package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/8/26.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
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
public class FamilyViewGroupBak extends ViewGroup {

    private static final int HORIZONTALSPACING = 40;
    private static final int VERTICAL = 100;
    Context context;
    private ClanRela mClanRela;
    private PUnit user;
    FamilyDrawUtils mFamilyDrawUtils = new FamilyDrawUtils(mClanRela);

    public FamilyViewGroupBak(Context context) {
        super(context);
        this.context = context;
    }

    public FamilyViewGroupBak(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FamilyViewGroupBak(Context context, AttributeSet attrs, int defStyleAttr) {
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
         * 记录如果是wrap_content是设置的宽和高
         */
        int width = 0;
        int height = 0;

        int cCount = getChildCount();

        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;

        // 用于计算左边两个childView的高度
        int lHeight = 0;
        // 用于计算右边两个childView的高度，最终高度取二者之间大值
        int rHeight = 0;

        // 用于计算上边两个childView的宽度
        int tWidth = 0;
        // 用于计算下面两个childiew的宽度，最终宽度取二者之间大值
        int bWidth = 0;

        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        for (int i = 0; i < cCount; i++) {
            View childView = getChildAt(i);
            cWidth = childView.getMeasuredWidth();
            cHeight = childView.getMeasuredHeight();
            cParams = (MarginLayoutParams) childView.getLayoutParams();

            // 上面两个childView
            if (i == 0 || i == 1) {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 2 || i == 3) {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin;
            }

            if (i == 0 || i == 2) {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

            if (i == 1 || i == 3) {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin;
            }

        }

        width = Math.max(tWidth, bWidth);
        height = Math.max(lHeight, rHeight);
        /**
         * 如果是wrap_content设置为我们计算的值
         * 否则：直接设置为父容器计算的值
         */
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (heightMode == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }

    // abstract method in viewgroup
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int cCount = getChildCount();
        int cWidth = 0;
        int cHeight = 0;
        MarginLayoutParams cParams = null;
        /**
         * 遍历所有childView根据其宽和高，以及margin进行布局
         */
        View view = getChildAt(0);
        int left = getWidth() / 2 - view.getWidth() / 2;
        int top = getHeight() / 2 - view.getHeight();
        int right = getWidth() / 2 - view.getWidth() / 2;
        int bottom = getHeight() / 2 + view.getWidth() / 2;
        view.layout(left, top, right, bottom);

//        for (int i = 0; i < cCount; i++) {
//            View childView = getChildAt(i);
//            cWidth = childView.getMeasuredWidth();
//            cHeight = childView.getMeasuredHeight();
//            cParams = (MarginLayoutParams) childView.getLayoutParams();
//
//            int cl = 0, ct = 0, cr = 0, cb = 0;
//
//            switch (i) {
//                case 0:
//                    cl = cParams.leftMargin;
//                    ct = cParams.topMargin;
//                    break;
//                case 1:
//                    cl = getWidth() - cWidth - cParams.leftMargin
//                            - cParams.rightMargin;
//                    ct = cParams.topMargin;
//
//                    break;
//                case 2:
//                    cl = cParams.leftMargin;
//                    ct = getHeight() - cHeight - cParams.bottomMargin;
//                    break;
//                case 3:
//                    cl = getWidth() - cWidth - cParams.leftMargin
//                            - cParams.rightMargin;
//                    ct = getHeight() - cHeight - cParams.bottomMargin;
//                    break;
//
//            }
//            cr = cl + cWidth;
//            cb = cHeight + ct;
//            childView.layout(cl, ct, cr, cb);
//        }

    }

    List<View> childViews = new ArrayList<>();
    List<PUnit> userList = new ArrayList<>();

    private void initData() {
        mClanRela = new ClanRela();

        Map<Long, PUnit> userMap = mClanRela.uIndex2Punit;
        Map<Long, FamilyUnit> familyMap = mClanRela.familyUnitMap;


        //创建者       PUnit user = userMap.get(mClanRela.creater);
        View meView = params.get(user.userId);
        drawMe(user);
        drawWife(user, meView);
        drawfaterAndMother(user, meView);
        drawBrothers(user, meView);
        drawChild(user, meView);


         /* 自己 */
        //设置自己和老婆的位置
        // 设置父母的位置
        // 设置兄弟姐妹的位置 （设置时设置好他们的配偶）

        // 设置 父母 的父母位置
        //设置 父母 的父母兄弟姐妹（设置时设置好他们的配偶）

        // 设置 父母 的父母的父母 位置 递归
        //设置 父母 的父母的父母 的兄弟姐妹（设置时设置好他们的配偶）递归

        //父亲 兄弟姐妹的儿子 女儿

        //父亲的父亲 兄弟姐妹的儿子 女儿 递归

        //兄弟姐妹 配偶的父母

        //父亲兄弟姐妹 配偶的父母 递归


        /* 自己 老婆 */

        // 设置 老婆 父母的位置
        // 设置 老婆 兄弟姐妹的位置 （设置时设置好他们的配偶）

        // 设置 老婆 父母 的父母位置
        //设置  老婆 父母 的父母兄弟姐妹（设置时设置好他们的配偶）

        // 设置 老婆 父母 的父母的父母 位置 递归
        //设置  老婆 父母 的父母的父母 的兄弟姐妹（设置时设置好他们的配偶）递归

        //父亲 兄弟姐妹的儿子 女儿

        //父亲的父亲 兄弟姐妹的儿子 女儿 递归

        //兄弟姐妹 配偶的父母

        //父亲兄弟姐妹 配偶的父母 递归


        //自己的子女以及配偶
        //子女的 子女以及配偶 递归

        //子女配偶的兄弟姐妹
        //

        addChildView(user);

    }


    Map<Long, Integer> leftMap = new HashMap<>();// KEY 辈份 ，value 当前辈份最左坐标

    Map<Long, Integer> rightMap = new HashMap<>();// KEY 辈份 ，value 当前辈份最右坐标


    private void drawfaterAndMother(PUnit me, View userView) {
        PUnit fater = mFamilyDrawUtils.getFater(me);
        PUnit mother = mFamilyDrawUtils.getMother(me);

        int fLeft = 0;
        int fRight = 0;
        int fTop = 0;
        int fBottom = 0;

        if (fater != null) {
            View view = params.get(fater.userId);
            //获取当前辈份 最左边的坐标
            int currentLeft = leftMap.get(mFamilyDrawUtils.getBeifen(fater.userId));

            fLeft = currentLeft - HORIZONTALSPACING - view.getWidth();
            fTop = userView.getTop() + VERTICAL + view.getHeight();
            fRight = currentLeft - HORIZONTALSPACING;
            fBottom = userView.getTop() + VERTICAL;
            view.layout(fLeft, fTop, fRight, fBottom);
        }

        if (mother != null) {
            View motherView = params.get(mother.userId);
            //获取当前辈份 最左边的坐标
            if (fater != null) {
                int mLeft = fRight;
                int mTop = fTop;
                int mRight = fRight + motherView.getWidth();
                int mBottom = fBottom;
                motherView.layout(mLeft, mTop, mRight, mBottom);
            } else {
                int currentLeft = leftMap.get(mFamilyDrawUtils.getBeifen(mother.userId));
                int mLeft = currentLeft - HORIZONTALSPACING - motherView.getWidth();
                int mTop = userView.getTop() + VERTICAL + motherView.getHeight();
                int mRight = currentLeft - HORIZONTALSPACING;
                int mBottom = userView.getTop() + VERTICAL;
                motherView.layout(mLeft, mTop, mRight, mBottom);
            }
        }
    }


    private void drawWife(PUnit user, View userView) {
        PUnit wife = mFamilyDrawUtils.getWife(user);
        View view = params.get(wife.userId);

        int left = userView.getRight();
        int top = userView.getTop();
        int right = userView.getRight() + view.getWidth();
        int bottom = userView.getBottom();
        view.layout(left, top, right, bottom);
    }

    Map<Long, View> params = new HashMap<>();

    private void drawBrothers(PUnit user, View userView) {
        List<Long> ids = mFamilyDrawUtils.getBrothers(user);
        //获取当前辈份 最左边的坐标
        int currentLeft = leftMap.get(mFamilyDrawUtils.getBeifen(user.userId));
        for (Long id : ids) {
            View view = params.get(id);
            PUnit wife = mFamilyDrawUtils.getWife(mClanRela.uIndex2Punit.get(id));
            if (wife != null) {
                int left = currentLeft - HORIZONTALSPACING - view.getWidth() * 2;
                int top = userView.getTop();
                int right = currentLeft - HORIZONTALSPACING - view.getWidth();
                int bottom = userView.getBottom();
                view.layout(left, top, right, bottom);
                drawWife(user, view);
            } else {
                int left = currentLeft - HORIZONTALSPACING - view.getWidth();
                int top = userView.getTop();
                int right = currentLeft - HORIZONTALSPACING;
                int bottom = userView.getBottom();
                view.layout(left, top, right, bottom);
            }
        }
    }

    private void drawChild(PUnit user, View userView) {
        List<Long> ids = mFamilyDrawUtils.getChilds(user);
        //获取 子辈份 最左边的坐标
        if (ids == null || ids.size() == 0) {
            return;
        }
        int currentLeft = leftMap.get(mFamilyDrawUtils.getBeifen(ids.get(0)));
        for (Long id : ids) {
            View view = params.get(id);

            PUnit wife = mFamilyDrawUtils.getWife(mClanRela.uIndex2Punit.get(id));
            if (wife != null) {
                int left = currentLeft - HORIZONTALSPACING - view.getWidth() * 2;
                int top = userView.getTop();
                int right = currentLeft - HORIZONTALSPACING - view.getWidth();
                int bottom = userView.getBottom();
                view.layout(left, top, right, bottom);
                drawWife(user, view);
            } else {
                int left = currentLeft - HORIZONTALSPACING - view.getWidth();
                int top = userView.getTop();
                int right = currentLeft - HORIZONTALSPACING;
                int bottom = userView.getBottom();
                view.layout(left, top, right, bottom);
            }
        }


    }


    private void drawMe(PUnit me) {
        View view = params.get(me.userId);
        int left = getWidth() / 2 - view.getWidth() / 2;
        int top = getHeight() / 2 - view.getHeight();
        int right = getWidth() / 2 - view.getWidth() / 2;
        int bottom = getHeight() / 2 + view.getWidth() / 2;
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
        layout(-100, getTop(), getRight(), getBottom());

        initPaint();
        int childCount = getChildCount();

        int width1 = 0;
        int height1 = 0;

        int width2 = 0;
        int height2 = 0;

        int width3 = 0;
        int height3 = 0;

        int width4 = 0;
        int height4 = 0;


        View childView1 = getChildAt(0);
        width1 = childView1.getRight();
        height1 = childView1.getHeight() / 2;


        View childView2 = getChildAt(1);
        width2 = childView2.getLeft();
        height2 = childView2.getHeight() / 2;

        View childView3 = getChildAt(2);
        width3 = childView3.getRight();
        height3 = childView3.getTop();


        View childView4 = getChildAt(3);
        width4 = childView4.getLeft();
        height4 = childView4.getBottom() / 2;


        canvas.drawLine(width1, height1, width2, height2, mPaint);

        canvas.drawLine((width2 + childView2.getWidth()) / 2, height2, (width2 + childView2.getWidth()) / 2, height4, mPaint);

        canvas.drawLine((width2 + childView2.getWidth()) / 2, height4, width1 / 2, height4, mPaint);

        canvas.drawLine((width2 + childView2.getWidth()) / 2, height4, childView4.getRight() - childView4.getWidth() / 2, height4, mPaint);
        canvas.drawLine(childView4.getRight() - childView4.getWidth() / 2, height4, childView4.getRight() - childView4.getWidth() / 2, childView4.getTop(), mPaint);

        canvas.drawLine(width1 / 2, height4, width1 / 2, height3, mPaint);
//
//        canvas.drawLine((width2-width1)/2,(height4-height2)/2,(width4)/2,(height4-height2)/2,mPaint);

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
}
