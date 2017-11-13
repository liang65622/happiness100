package com.happiness100.app.ui.widget;/**
 * Created by Administrator on 2016/8/13.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.github.lzyzsd.circleprogress.Utils;
import com.happiness100.app.R;

/**
 * 作者：justin on 2016/8/13 09:48
 */
public class MyDonutProgress extends DonutProgress {
    private final int default_title_color = Color.rgb(66, 145, 241);
    private float default_title_size;

    private final int default_tips_color = Color.rgb(66, 145, 241);
    private float default_tips_size;


    String title;
    String tips;
    private int titleColor;
    private float titleSize;

    private int tipsColor;
    private float tipsSize;


    private Paint titlePaint;
    private Paint tipsPaint;

    public MyDonutProgress(Context context) {
        super(context);
    }

    public MyDonutProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDonutProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_title_size = Utils.sp2px(getResources(), 18);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, com.github.lzyzsd.circleprogress.R.styleable.DonutProgress, defStyleAttr, 0);
        initMyAttributes(attributes);
        attributes.recycle();

        initMyPaint();
    }

    void initMyPaint() {
        titlePaint = new TextPaint();
        titlePaint.setColor(titleColor);
        titlePaint.setTextSize(titleSize);
        titlePaint.setAntiAlias(true);

        tipsPaint = new TextPaint();
        tipsPaint.setColor(tipsColor);
        tipsPaint.setTextSize(tipsSize);
        tipsPaint.setAntiAlias(true);
    }

    public MyDonutProgress(Context context, String text) {
        super(context);
        this.title = text;
    }

    void initMyAttributes(TypedArray attributes) {
        if (attributes.getString(R.styleable.DonutProgress_donut_title_text) != null) {
            title = attributes.getString(R.styleable.DonutProgress_donut_title_text);
        }
        titleColor = attributes.getColor(R.styleable.DonutProgress_donut_title_color, default_title_color);
        titleSize = attributes.getDimension(R.styleable.DonutProgress_donut_title_size, default_title_size);

        if (attributes.getString(R.styleable.DonutProgress_donut_tips_text) != null) {
            tips = attributes.getString(R.styleable.DonutProgress_donut_tips_text);
        }

        tipsColor = attributes.getColor(R.styleable.DonutProgress_donut_tips_color, default_title_color);
        tipsSize = attributes.getDimension(R.styleable.DonutProgress_donut_tips_size, default_title_size);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        float textHeight = titlePaint.descent() + titlePaint.ascent();
//        if (!TextUtils.isEmpty(tips)) {
//            canvas.drawText(tips, (getWidth() - tipsPaint.measureText(tips)) / 2.0f, getHeight() / 3, tipsPaint);
//        }
//        if (!TextUtils.isEmpty(title)) {
//            canvas.drawText(title, (getWidth() - titlePaint.measureText(title)) / 2.0f, getHeight() / 4 * 3, titlePaint);
//        }
//        canvas.drawText
    }

    public float getDefault_title_size() {
        return default_title_size;
    }

    public void setDefault_title_size(float default_title_size) {
        this.default_title_size = default_title_size;
    }

    public int getDefault_tips_color() {
        return default_tips_color;
    }

    public float getDefault_tips_size() {
        return default_tips_size;
    }

    public void setDefault_tips_size(float default_tips_size) {
        this.default_tips_size = default_tips_size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
//        titlePaint.setColor(titleColor);
    }

    public float getTitleSize() {
        return titleSize;
    }

    public void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    public int getTipsColor() {
        return tipsColor;
    }

    public void setTipsColor(int tipsColor) {
        this.tipsColor = tipsColor;
    }

    public float getTipsSize() {
        return tipsSize;
    }

    public void setTipsSize(float tipsSize) {
        this.tipsSize = tipsSize;
    }

    public Paint getTitlePaint() {
        return titlePaint;
    }

    public void setTitlePaint(Paint titlePaint) {
        this.titlePaint = titlePaint;
    }

    public Paint getTipsPaint() {
        return tipsPaint;
    }

    public void setTipsPaint(Paint tipsPaint) {
        this.tipsPaint = tipsPaint;
    }

    public int getDefault_title_color() {
        return default_title_color;
    }
}
