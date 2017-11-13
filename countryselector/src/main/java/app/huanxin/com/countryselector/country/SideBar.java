/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015��3��18��	| duanbokan 	| 	create the file                       
 */

package app.huanxin.com.countryselector.country;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import app.huanxin.com.countryselector.R;


/**
 * 
 * ���Ҫ����
 * 
 * <p>
 * ����ϸ����
 * </p>
 * 
 * @author duanbokan
 * 
 */

public class SideBar extends View
{
	// ��ĸ�仯�����¼�
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	
	// ��ĸ����
	public static String[] b = { "*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
			"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#" };
	
	// ѡ��
	private int choose = -1;
	
	private Paint paint = new Paint();
	
	// �������ʾ��ǰѡ����ĸ
	private TextView mTextDialog;
	
	public void setTextView(TextView mTextDialog)
	{
		this.mTextDialog = mTextDialog;
	}
	
	public SideBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	public SideBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public SideBar(Context context)
	{
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		// ��ȡ��Ļ�߶ȺͿ��
		int height = getHeight();
		int width = getWidth();
		
		// ������ĸ�߶�
		float letterHeight = (height * 1f) / b.length;
		for (int i = 0; i < b.length; i++)
		{
			paint.setColor(Color.rgb(23, 122, 126));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			// �����
			paint.setAntiAlias(true);
			paint.setTextSize(20);
			
			if (i == choose)
			{
				paint.setColor(Color.BLUE);
				// ����Ϊ�Ӵ�����
				paint.setFakeBoldText(true);
			}
			// x��������м�-�ַ�����ȵ�һ��.
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = letterHeight * i + letterHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			// ���û���
			paint.reset();
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		final int action = event.getAction();
		final float touch_y = event.getY();
		final int oldChoose = choose;
		
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		// ���y������ռ�ܸ߶ȵı���*b����ĳ��Ⱦ͵��ڵ��b�еĸ���.
		final int c = (int) (touch_y / getHeight() * b.length);
		
		switch (action)
		{
			case MotionEvent.ACTION_UP:
				setBackgroundColor(Color.WHITE);
				choose = -1;
				invalidate();
				if (mTextDialog != null)
				{
					mTextDialog.setVisibility(View.INVISIBLE);
				}
				break;
			
			default:
				setBackgroundResource(R.drawable.sidebar_background);
				if (oldChoose != c)
				{
					if (c >= 0 && c < b.length)
					{
						if (listener != null)
						{
							listener.onTouchingLetterChanged(b[c]);
						}
						if (mTextDialog != null)
						{
							mTextDialog.setText(b[c]);
							mTextDialog.setVisibility(View.VISIBLE);
						}
						choose = c;
						invalidate();
					}
				}
				break;
		}
		
		return true;
	}
	
	/**
	 * ���⹫���ķ���
	 * 
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener)
	{
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}
	
	/**
	 * �ӿ�
	 * 
	 * @author coder
	 * 
	 */
	public interface OnTouchingLetterChangedListener
	{
		public void onTouchingLetterChanged(String s);
	}
	
}
