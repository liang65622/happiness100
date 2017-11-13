package com.happiness100.app.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.github.lzyzsd.circleprogress.Utils;
import com.happiness100.app.R;


/**
 * package com.github.lzyzsd.circleprogress;
 * <p/>
 * import android.content.Context;
 * import android.content.res.TypedArray;
 * import android.graphics.Canvas;
 * import android.graphics.Color;
 * import android.graphics.Paint;
 * import android.graphics.RectF;
 * import android.os.Bundle;
 * import android.os.Parcelable;
 * import android.text.TextPaint;
 * import android.text.TextUtils;
 * import android.util.AttributeSet;
 * import android.view.View;
 * <p/>
 * /**
 * Created by bruce on 14-10-30.
 */
public class DonutProgress extends View {
    private Paint finishedPaint;
    private Paint unfinishedPaint;
    private Paint innerCirclePaint;
    protected Paint textPaint;
    protected Paint suffixPaint;

    private RectF finishedOuterRect = new RectF();
    private RectF unfinishedOuterRect = new RectF();

    private float textSize;
    private int textColor;
    private int progress = 0;
    private int max;
    private int finishedStrokeColor;
    private int unfinishedStrokeColor;
    private float finishedStrokeWidth;
    private float unfinishedStrokeWidth;
    private int innerBackgroundColor;

    private String contentText = "";
    private String prefixText = "";
    private String suffixText = "%";

    private final float default_stroke_width;
    private final int default_finished_color = Color.rgb(66, 145, 241);
    private final int default_unfinished_color = Color.rgb(204, 204, 204);
    private final int default_text_color = Color.rgb(66, 145, 241);
    private final int default_inner_background_color = Color.TRANSPARENT;
    private final int default_max = 100;
    private final float default_text_size;
    private final int min_size;

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width";
    private static final String INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width";
    private static final String INSTANCE_BACKGROUND_COLOR = "inner_background_color";
    private int mCurrentProgress;

    public DonutProgress(Context context) {
        this(context, null);
    }

    public DonutProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DonutProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        default_text_size = Utils.sp2px(getResources(), 18);
        min_size = (int) Utils.dp2px(getResources(), 100);
        default_stroke_width = Utils.dp2px(getResources(), 10);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, com.github.lzyzsd.circleprogress.R.styleable.DonutProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);


        suffixPaint = new TextPaint();
        suffixPaint.setColor(textColor);
        suffixPaint.setTextSize(17);
        suffixPaint.setAntiAlias(true);

        finishedPaint = new Paint();
        finishedPaint.setColor(finishedStrokeColor);
        finishedPaint.setStyle(Paint.Style.STROKE);
        finishedPaint.setAntiAlias(true);
        finishedPaint.setStrokeWidth(finishedStrokeWidth);

        unfinishedPaint = new Paint();
        unfinishedPaint.setColor(unfinishedStrokeColor);
        unfinishedPaint.setStyle(Paint.Style.STROKE);
        unfinishedPaint.setAntiAlias(true);
        unfinishedPaint.setStrokeWidth(unfinishedStrokeWidth);

        innerCirclePaint = new Paint();
        innerCirclePaint.setStyle(Paint.Style.STROKE);
        innerCirclePaint.setColor(finishedStrokeColor);
        innerCirclePaint.setStrokeWidth(2);
        innerCirclePaint.setAntiAlias(true);
    }

    protected void initByAttributes(TypedArray attributes) {
        finishedStrokeColor = attributes.getColor(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_finished_color, default_finished_color);
        unfinishedStrokeColor = attributes.getColor(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_unfinished_color, default_unfinished_color);
        textColor = attributes.getColor(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_text_color, default_text_color);
        textSize = attributes.getDimension(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_text_size, default_text_size);

        setMax(attributes.getInt(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_max, default_max));

//        mCurrentProgress =
        setProgress(attributes.getInt(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_progress, 0));

        finishedStrokeWidth = attributes.getDimension(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_finished_stroke_width, default_stroke_width);
        unfinishedStrokeWidth = attributes.getDimension(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_unfinished_stroke_width, default_stroke_width);
        if (attributes.getString(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_prefix_text) != null) {
            prefixText = attributes.getString(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_prefix_text);
        }
        if (attributes.getString(R.styleable.DonutProgress_donut_content_text) != null) {
            contentText = attributes.getString(R.styleable.DonutProgress_donut_content_text);
        }

        if (attributes.getString(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_suffix_text) != null) {
            suffixText = attributes.getString(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_suffix_text);
        }
        innerBackgroundColor = attributes.getColor(com.github.lzyzsd.circleprogress.R.styleable.DonutProgress_donut_background_color, default_inner_background_color);
    }

    public float getFinishedStrokeWidth() {
        return finishedStrokeWidth;
    }

    public void setFinishedStrokeWidth(float finishedStrokeWidth) {
        this.finishedStrokeWidth = finishedStrokeWidth;
    }

    public float getUnfinishedStrokeWidth() {
        return unfinishedStrokeWidth;
    }

    public void setUnfinishedStrokeWidth(float unfinishedStrokeWidth) {
        this.unfinishedStrokeWidth = unfinishedStrokeWidth;
    }

    private float getProgressAngle() {
        return getProgress() / (float) max * 360f;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getFinishedStrokeColor() {
        return finishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.finishedStrokeColor = finishedStrokeColor;
    }

    public int getUnfinishedStrokeColor() {
        return unfinishedStrokeColor;
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.unfinishedStrokeColor = unfinishedStrokeColor;
    }

    public String getSuffixText() {
        return suffixText;
    }

    public void setSuffixText(String suffixText) {
        this.suffixText = suffixText;
    }

    public String getPrefixText() {
        return prefixText;
    }

    public void setPrefixText(String prefixText) {
        this.prefixText = prefixText;
    }

    public int getInnerBackgroundColor() {
        return innerBackgroundColor;
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        this.innerBackgroundColor = innerBackgroundColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    private int measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = min_size;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (finishedStrokeWidth > unfinishedStrokeWidth) {
            finishedOuterRect.set(finishedStrokeWidth,
                    finishedStrokeWidth,
                    getWidth() - finishedStrokeWidth,
                    getHeight() - finishedStrokeWidth);
            float delta = (finishedStrokeWidth - unfinishedStrokeWidth) / 2f;
            unfinishedOuterRect.set(unfinishedStrokeWidth / 2,
                    unfinishedStrokeWidth / 2,
                    getWidth() - unfinishedStrokeWidth / 2 - delta,
                    getHeight() - unfinishedStrokeWidth / 2 - delta);
        } else {
            float delta = (unfinishedStrokeWidth - finishedStrokeWidth) / 2f;
            finishedOuterRect.set(unfinishedStrokeWidth / 2 + 10,
                    unfinishedStrokeWidth / 2 + 10,
                    getWidth() - finishedStrokeWidth / 2 - delta - 10,
                    getHeight() - finishedStrokeWidth / 2 - delta - 10);
            unfinishedOuterRect.set(unfinishedStrokeWidth / 2,
                    unfinishedStrokeWidth / 2,
                    getWidth() - unfinishedStrokeWidth / 2,
                    getHeight() - unfinishedStrokeWidth / 2);
        }
        float innerCircleRadius = getWidth() / 2f - 2;
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, innerCircleRadius, innerCirclePaint);
//        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, innerCircleRadius, innerCirclePaint);
        canvas.drawArc(finishedOuterRect, 270, getProgressAngle(), false, finishedPaint);
//        canvas.drawArc(unfinishedOuterRect,270+getProgressAngle() ,360-getProgressAngle(), false, unfinishedPaint);

        String text = prefixText + contentText;
        String data = "";
        String[] dataStrs = text.split("\\.");
        if (dataStrs.length == 0 || contentText.isEmpty() || Float.parseFloat(text) == 0) {
            data = "--";
        } else {
            data = dataStrs[0];
        }

        String suffixTxt = "";
        if (dataStrs.length > 1) {
            if (Float.parseFloat(text) != 0) {
                suffixTxt = "." + dataStrs[1] + suffixText;
            } else {
                suffixTxt = suffixText;
            }
        } else {
            suffixTxt = suffixText;
        }

        //画数值
//        if (!TextUtils.isEmpty(text)) {
//            float textHeight = textPaint.descent() + textPaint.ascent();
//            canvas.drawText(data, (getWidth() - textPaint.measureText(data) - suffixPaint.measureText(suffixText)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
//        } else {
//            data = "--";
//            float textHeight = textPaint.descent() + textPaint.ascent();
//            canvas.drawText(data, (getWidth() - textPaint.measureText(data) - suffixPaint.measureText(suffixText)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
//        }
//
//        if (!TextUtils.isEmpty(suffixText)) {
//            float textHeight = suffixPaint.descent() + suffixPaint.ascent();
//            canvas.drawText(suffixTxt, (getWidth() - textPaint.measureText(data) - suffixPaint.measureText(suffixText)) / 2.0f + textPaint.measureText(data), (getWidth() - textHeight) / 2.0f, suffixPaint);
//        }
        prefixText = "";
        contentText = "";
        int progress = getProgress();

//        if(progress<mCurrentProgress){
//            j
//        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());
        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        bundle.putString(INSTANCE_PREFIX, getPrefixText());
        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth());
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth());
        bundle.putInt(INSTANCE_BACKGROUND_COLOR, getInnerBackgroundColor());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            textColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            textSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            finishedStrokeColor = bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR);
            unfinishedStrokeColor = bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR);
            finishedStrokeWidth = bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH);
            unfinishedStrokeWidth = bundle.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH);
            innerBackgroundColor = bundle.getInt(INSTANCE_BACKGROUND_COLOR);
            initPainters();
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            prefixText = bundle.getString(INSTANCE_PREFIX);
            suffixText = bundle.getString(INSTANCE_SUFFIX);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

}
