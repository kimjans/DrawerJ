package com.example.stest;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScrollViewTemp extends ScrollView {

	/**
	 * @param context
	 * @param attrs
	 */
	public ScrollViewTemp(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
			return false;
	}
	public boolean onTouchEventMine(MotionEvent ev) {
			// TODO Auto-generated method stub
			
			switch ( ev.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				Log.d("test",  "ACTION_DOWN"+ "");
				break;
			case MotionEvent.ACTION_MOVE :
				Log.d("test",  "ACTION_MOVE"+ "");
				break;
			case MotionEvent.ACTION_UP :
				Log.d("test",  "ACTION_UP"+ "");
				break;
			case MotionEvent.ACTION_CANCEL :
				Log.d("test",  "ACTION_CANCEL"+ "");
				break;
			default :
				break;
		}
		
		return super.onTouchEvent(ev);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		return false;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		
		super.onLayout(changed, l, t, r, b);
	}
	

}
