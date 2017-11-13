package com.justin.scalemodule.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.justin.scalemodule.R;

public class CustRelativeLayout extends RelativeLayout {
	private static final String TAG = "CustRelativeLayout";
	
	private boolean isAutoScale = false;
	private int deviceWidth = 0;
	private int deviceHeight = 0;

	public CustRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initFromAttributes(context, attrs);
	}

	public CustRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initFromAttributes(context, attrs);
	}

	public CustRelativeLayout(Context context) {
		super(context);		
	}
	
	private void initFromAttributes(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustRelativeLayout);
		isAutoScale = a.getBoolean(R.styleable.CustRelativeLayout_autoScale, false);
		Log.e(TAG, "in initFromAttributes(), isAutoScale = " + isAutoScale);
		
		deviceWidth = a.getInt(R.styleable.CustRelativeLayout_deviceWidth, 0);
		Log.e(TAG, "in initFromAttributes(), deviceWidth = " + deviceWidth);
		
		deviceHeight = a.getInt(R.styleable.CustRelativeLayout_deviceHeight, 0);
		Log.e(TAG, "in initFromAttributes(), deviceHeight = " + deviceHeight);
		
		a.recycle();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (isAutoScale) {
			float scale = getScale(this.getContext());
			if (scale != 0) {
				int count = getChildCount();
				for (int i = 0; i < count; i++) {
					View child = getChildAt(i);
					if (child.getVisibility() != GONE) {
						LayoutParams params = (LayoutParams) child.getLayoutParams();
						params.width = (int)(params.width * scale);
						Log.e(TAG, "in onMeasure(), params.width = " + params.width);
						params.height = (int)(params.height * scale);
						Log.e(TAG, "in onMeasure(), params.height = " + params.height);
					}
				}
			}
		}
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private float getScale(Context context) {
		float scale = 0;
		DisplayMetrics dm = new DisplayMetrics(); 
		dm = getResources().getDisplayMetrics();
		int screenWidth  = dm.widthPixels;      
		int screenHeight = dm.heightPixels;
		Log.e(TAG, "in getScale(), screenWidth = " + screenWidth);
		Log.e(TAG, "in getScale(), screenHeight = " + screenHeight);
		
		if ((deviceWidth != 0) && (deviceHeight != 0)) {
			float scaleW = (float)screenWidth / deviceWidth;
			Log.e(TAG, "in getScale(), scaleW = " + scaleW);
			
			float scaleH = (float)screenHeight / deviceHeight;
			Log.e(TAG, "in getScale(), scaleH = " + scaleH);
			
			scale = (scaleW > scaleH) ? scaleH : scaleW;
		}
		Log.e(TAG, "in getScale(), scale = " + scale);
		return scale;
	}

}
