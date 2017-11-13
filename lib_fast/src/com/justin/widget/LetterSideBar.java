package com.justin.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LetterSideBar extends View {
	// 26个字母
	private static final String[] LETTERS = { "#", "A", "B", "C", "D", "E",
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" };
	private Paint mPaint;
	private boolean isPressed;
	private int mCurrPos = -1;
	private OnLetterChangedListener listener;

	@Override
	protected void onDraw(Canvas canvas) {
		if (isPressed){
			canvas.drawColor(Color.parseColor("#40000000"));
		}
		int sideBarHeight = getHeight();
		int height = sideBarHeight / LETTERS.length;
		for (int i = 0; i < LETTERS.length; i++) {
			mPaint.setColor(Color.BLACK);
			// 水平居中
			float xPos = (getWidth() - mPaint.measureText(LETTERS[i])) / 2;
			float yPos = height * (i + 1);
			if (i == mCurrPos){
				mPaint.setColor(Color.parseColor("#3399ff"));
			}
			canvas.drawText(LETTERS[i], xPos, yPos, mPaint);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float pressY = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mCurrPos = (int) (pressY / getHeight() * LETTERS.length);
			listener.onLetterChanged(LETTERS[mCurrPos]);
			isPressed  = true;
//			Log.e("ontouch", "按下");
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrPos = (int) (pressY / getHeight() * LETTERS.length);
			listener.onLetterChanged(LETTERS[mCurrPos]);
			Log.e("ontouch", "移动");
			break;
		case MotionEvent.ACTION_UP:
			listener.onActionUp();
			mCurrPos = -1;
			Log.e("ontouch", "抬起");
			isPressed  = false;
			break;
		default:
			break;
		}
		invalidate();
		// 返回必须为true，否则无法监听move和up事件
		return true;
	}

	public LetterSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(20);
		mPaint.setColor(Color.BLACK);
	}
	
	public interface OnLetterChangedListener {
		void onLetterChanged(String letter);

		void onActionUp();
	}

	public void setOnLetterChangedListener(
			OnLetterChangedListener listener) {
		this.listener = listener;
		
	}
	
	
	
	
	
	
	
	
	
	
	

}
