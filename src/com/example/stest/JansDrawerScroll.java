package com.example.stest;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class JansDrawerScroll extends ScrollView {

	/**
	 * @param context
	 * @param attrs
	 */
	public JansDrawerScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
			//이벤트들을 drawer에서 호출하기 때문에 모두 제거
			return false;
	}
	public boolean onTouchEventMine(MotionEvent ev) {
			
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
		//이벤트들을 drawer에서 호출하기 때문에 모두 제거		
		return false;
	}

}
