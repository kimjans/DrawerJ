package com.example.stest;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class viewPagerTemp extends ViewPager {

	/**
	 * @param context
	 * @param attrs
	 */
	public viewPagerTemp(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	
	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
//		switch ( arg0.getAction() ) {
//			case MotionEvent.ACTION_DOWN:
//				Log.d("tag", "down + viewPagerTemp");
//				return true;
//				
//			case MotionEvent.ACTION_MOVE :
//				Log.d("tag", "move + viewPagerTemp");
//				return false;
//				
//		}
//		return false;
		return super.onTouchEvent(arg0);
	}
	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(arg0);
//		switch ( arg0.getAction() ) {
//			case MotionEvent.ACTION_DOWN:
//				Log.d("tag", "down + viewPagerTemp2");
//				return true;
//				
//			case MotionEvent.ACTION_MOVE :
//				Log.d("tag", "move + viewPagerTemp2");
//				return false;
//				
//		}
//		return false;
	}



}
