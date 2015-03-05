package com.example.stest;

//import com.naver.sally.view.SlidingDrawer.SlidingHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.DeniedByServerException;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Jans on 15. 3. 3..
 */
public class ScrollView extends RelativeLayout {

	private boolean mAnimating;
	private static final int MSG_ANIMATE = 1000;
	private static final int ANIMATION_FRAME_DURATION = 1000 / 80;
	
	private long mCurrentAnimationTime;
	
	private final Handler mHandler = new SlidingHandler();
	
	private VelocityTracker mVelocityTracker;
	
		
		
    public ScrollView(Context context) {
        super(context);
    }
    public ScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }
    public ScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	
    	switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				//("tag", "intercepDown");
				startY = (int)event.getY();
				startX = (int)event.getX();
				touchEventHandler(event);
				
				return false;
			case MotionEvent.ACTION_MOVE :
				
				if(Math.abs(startX -(int)event.getX()) > 20){
					//stopTracking();
				}	
					
				if( Math.abs(startY -(int)event.getY()) > 20 ){
					return true;
				}
				//Log.d("tag", "intercepMove");
				
				return false;
			case MotionEvent.ACTION_UP :
			case MotionEvent.ACTION_CANCEL :
				//Log.d("tag", "touch cancel");
				break;
		}
    	return false;
    	
    };
    private int startY= 0;
    private int startX= 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				return true;
			case MotionEvent.ACTION_UP :
			case MotionEvent.ACTION_CANCEL :
				Log.d("tag", "touch cancel");
			default :
				touchEventHandler(event);
				break;
		}
    	return false;
    };
    
    private boolean touchEventHandler(MotionEvent event){
    	
    	//if(!mTracking){return false;}
    	
    	if(mTracking){
    		mVelocityTracker.addMovement(event);
    	}
    	
    	switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				mTouchDelta = (int) event.getY() - content.getTop();
				prepareTracking(content.getTop());
				Log.d("tag", "" + content.getTop());
				break;
			case MotionEvent.ACTION_MOVE :
				
				prepareTargetLine( content.getTop(), true); //움직임을 전체범위로세팅.
				//content.setTop(   (int) event.getY() - mTouchDelta  );
				moveHandle((int)event.getY() - mTouchDelta);
				break;
			case MotionEvent.ACTION_UP :
			case MotionEvent.ACTION_CANCEL :
				prepareTargetLine( content.getTop(), false ); //움직임을 현저 범위안으로 세팅.
				
				if(mTracking){
		    		
		    		final VelocityTracker velocityTracker = mVelocityTracker;
		    		velocityTracker.computeCurrentVelocity(mVelocityUnits);
		    		
		    		float yVelocity = velocityTracker.getYVelocity();
					float xVelocity = velocityTracker.getXVelocity();
					
					boolean negative;
					negative = yVelocity < 0;
					if (xVelocity < 0) {
						xVelocity = -xVelocity;
					}
					// fix by Maciej Ciemięga.
					if (xVelocity > mMaximumMinorVelocity) {
						xVelocity = mMaximumMinorVelocity;
					}
		    		
		    		float velocity = (float) Math.hypot(xVelocity, yVelocity);
		    		if (negative) {
						velocity = -velocity;
					}
		    		
		    		int position = content.getTop();
		    		//현재위치에서 3의 가속도로 올라감.
		    		performFling(  position, velocity , false  );
		    		
		    	}
				
				break;
			
    	}
    	return true;
    	
    }
    
    private int mTapThreshold;
    
    private static final int TAP_THRESHOLD = 6;
    private static final int EXPANDED_FULL_OPEN = -10001;
	private static final int COLLAPSED_FULL_CLOSED = -10002;
	private int mTopOffset = 0;
	private int mBottomOffset  = 0;
	
    private void moveHandle(int position) {
    	
		final View handle = content;
		 //int mHandleHeight = handle.getHeight();
		 mHandleHeight = 300; //핸들 높이를 임의로 잡음.

		if (position == EXPANDED_FULL_OPEN) {
			
				//handle.offsetTopAndBottom(mTopOffset - handle.getTop());
			   handle.offsetTopAndBottom(mCurrentTopOffset - handle.getTop());
				invalidate();
				
		} else if (position == COLLAPSED_FULL_CLOSED) {
			
				handle.offsetTopAndBottom(mCurrentBottomOffset -  handle.getTop());
				invalidate();
				
		} else {
			
			final int top = handle.getTop();
			int deltaY = position - top;
			int containerBottom =  getBottom();
			int containerTop = getTop();
			if (position < mCurrentTopOffset ) {
				//최상단보다 더 올라가면 최상단으로 맞춤.
				deltaY = mCurrentTopOffset - top;
			} else if (deltaY > mCurrentBottomOffset - top) {
				//핸들의 최하단 보다 더 밑으로 내려가면. 최하단으로 맞춤.
				deltaY = mCurrentBottomOffset  - top;
			}

			handle.offsetTopAndBottom(deltaY);

			//final Rect frame = mFrame;
			//final Rect region = mInvalidate;

			//handle.getHitRect(frame);
			//region.set(frame);

			//region.union(frame.left, frame.top - deltaY, frame.right, frame.bottom - deltaY);
			//region.union(0, frame.bottom - deltaY, getWidth(), frame.bottom - deltaY + mContent.getHeight());

			//invalidate(region);
		}
		
	}
    
    private static final float MAXIMUM_TAP_VELOCITY = 100.0f;
	private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
	private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
	private static final float MAXIMUM_ACCELERATION =  10000.0f; //원래 2000
	private static final int VELOCITY_UNITS = 1000;
	
	
    private boolean mTracking;
    private boolean mExpanded = false;
    private  int mMaximumTapVelocity;
	private int mMaximumMinorVelocity;
	private int mMaximumMajorVelocity;
	private int mMaximumAcceleration;
	private  int mVelocityUnits;
	
	private int mHandleHeight = 300;
	
	private Integer[] targetLine;
	
	
	//컨텐츠가 움직일 범위를 세팅한다.
	private void prepareTargetLine(int position, boolean fullRange){
		
		targetLine = new Integer[]{0, 300, getHeight() - mHandleHeight};
		
		if(fullRange){
			setMoveRange( targetLine[0], targetLine[ targetLine.length -1 ] );
			return;
		}
		
		if( position < targetLine[0]){ //최소값보다 작은경우;
			setMoveRange( targetLine[0], targetLine[1] );
			return;
		}
		if( position > targetLine[targetLine.length -1]){ //최대값보다 큰경
			setMoveRange( targetLine[targetLine.length-2], targetLine[targetLine.length-1] );
			return;
		}
		for(int i = 0 ; i < targetLine.length -1 ; ++i){
			
			if(targetLine[i] <= position && position <= targetLine[i+1]){
				setMoveRange( targetLine[i], targetLine[i+1] );
				return;
			}
		}
		
	}
	
    private void prepareTracking(int position) {
		mTracking = true;
		mVelocityTracker = VelocityTracker.obtain();
		boolean opening = !mExpanded;

		if (opening) {
			mAnimatedAcceleration = mMaximumAcceleration;
			mAnimatedVelocity = mMaximumMajorVelocity;
			mAnimationPosition = mBottomOffset + ( getHeight() - mHandleHeight );
			moveHandle((int) mAnimationPosition);
			mAnimating = true;
			mHandler.removeMessages(MSG_ANIMATE);
			long now = SystemClock.uptimeMillis();
			mAnimationLastTime = now;
			mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
			mAnimating = true;
		} else {
			if (mAnimating) {
				mAnimating = false;
				mHandler.removeMessages(MSG_ANIMATE);
			}
			moveHandle(position);
		}
	}
    private void stopTracking() {
		//mHandle.setPressed(false);
		mTracking = false;

//		if (mOnDrawerScrollListener != null) {
//			mOnDrawerScrollListener.onScrollEnded();
//		}

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
    
    
    private ViewGroup content = null;
    public int mTouchDelta  = 0 ;
    private   float density;
    
    @Override
    public void onFinishInflate(){
    	
    	
    	density = getResources().getDisplayMetrics().density;
    	
    	mMaximumTapVelocity = (int) (MAXIMUM_TAP_VELOCITY * density + 0.5f);
		mMaximumMinorVelocity = (int) (MAXIMUM_MINOR_VELOCITY * density + 0.5f);
		mMaximumMajorVelocity = (int) (MAXIMUM_MAJOR_VELOCITY * density + 0.5f);
		mMaximumAcceleration = (int) (MAXIMUM_ACCELERATION * density + 0.5f);
		mVelocityUnits = (int) (VELOCITY_UNITS * density + 0.5f);
		mTapThreshold = (int) (TAP_THRESHOLD * density + 0.5f);

        content = (LinearLayout) findViewById(R.id.wrap);

//        test.setOnTouchListener(new View.OnTouchListener() {
//
//            public int mTouchDelta  = 0 ;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mTouchDelta = (int) event.getY() - v.getTop();
//                    _("test", event.getY() + " " + v.getTop());
//
//                }else if(event.getAction() == MotionEvent.ACTION_UP){
//                    mTouchDelta  = 0;
//                }else if(event.getAction() == MotionEvent.ACTION_MOVE){
//                    v.offsetTopAndBottom( (int) event.getY() - mTouchDelta );
//                    //v.offsetTopAndBottom( 1 );
//                }
//                return true;
//
//            }
//        });
        
        openDrawer();
        
    }
    private void performFling(int position, float velocity, boolean always) {
    	
    	mAnimationPosition = position;
		mAnimatedVelocity = velocity;
		
		boolean c1;
		boolean c2;
		boolean c3;
		
		
		 final int mThresHold =( mCurrentBottomOffset - mCurrentTopOffset) / 3;  // 가까운 경계선에 붙어버릴 기준의 임계점.
		 if( Math.abs(mCurrentTopOffset - position)   < Math.abs(  mCurrentBottomOffset - position) ){
			 mExpanded = true;
		 }else{
			 mExpanded = false;
		 }
		 invalidate();
		if (mExpanded) {
			
			int bottom = getBottom();
			int handleHeight = mHandleHeight;

			// Log.d(LOG_TAG, "position: " + position + ", velocity: " + velocity + ", mMaximumMajorVelocity: " + mMaximumMajorVelocity);
			c1 = velocity > mMaximumMajorVelocity ; //속도가 최고속도보다 크다.
			//c2 =  position > mTopOffset + (mHandleHeight )8;
			c2 =  position > mCurrentTopOffset + mThresHold ; // 위치가 top보다 아래에 있다.
			c3 =  velocity > -mMaximumMajorVelocity; //속도가  -최고속도보다 크다.
			
			// Log.d(LOG_TAG, "EXPANDED. c1: " + c1 + ", c2: " + c2 + ", c3: " + c3);
			if (always || (c1 || (c2 && c3))) {
				// We are expanded, So animate to CLOSE!
				mAnimatedAcceleration = mMaximumAcceleration;
				if (velocity < 0) {
					mAnimatedVelocity = 0;
				}
				
			} else {
				
				// We are expanded, but they didn't move sufficiently to cause
				// us to retract. Animate back to the expanded position. so animate BACK to expanded!
				mAnimatedAcceleration = -mMaximumAcceleration;
				if (velocity > 0) {
					mAnimatedVelocity = 0;
				}
				
			}
			
		} else {
			// WE'RE COLLAPSED
			c1 =  velocity > mMaximumMajorVelocity;
			//c2 = position > getHeight() - mThresHold*density;//(position > (getHeight()) / 2);
			c2 = position > mCurrentBottomOffset - mThresHold;//(position > (getHeight()) / 2);
			c3 = velocity > -mMaximumMajorVelocity;
			//Log.d("test",  position +"___" + mThresHold+"____" + density);
			if (!always && (c1 || (c2 && c3))) {
				mAnimatedAcceleration = mMaximumAcceleration;
				if (velocity < 0) {
					mAnimatedVelocity = 0;
				}
			} else {
				mAnimatedAcceleration = -mMaximumAcceleration;
				if (velocity > 0) {
					mAnimatedVelocity = 0;
				}
			}
		}
		if( checkAnimationFrame() ){
			//Log.d("test", "yes");
			prepareTargetLine( 0, true); //움직임을 전체범위로세팅.
		}else{
			
		//	Log.d("test", "no");
			
		}
		
    	long now = SystemClock.uptimeMillis();
    	mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
		mAnimationLastTime = now;
    	mAnimating = true;
    	mHandler.removeMessages(MSG_ANIMATE);
		mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
		stopTracking();
    	
    }
    
    
    
    private int mCurrentTopOffset;
    private int mCurrentBottomOffset;
    
    //움직일 구간에 대한 상단, 하단값을 세팅한다.
    private void setMoveRange(int  top, int bottom){
    	
    	mCurrentTopOffset = top; 
    	mCurrentBottomOffset = bottom;
    	
    }
    private void doAnimation() {
    	
    	if (mAnimating) {
    		
			incrementAnimation();
			
			boolean c1 = mAnimationPosition >= mCurrentBottomOffset  - 1; //최하단보다 더 내려간경우.
			boolean c2 = mAnimationPosition < mCurrentTopOffset ; //최상단보다 더 올라간경
			
			if (c1) { //최하단보다 더 내려간경우.
				mAnimating = false;
				closeDrawer();
			} else if (c2) { //최상단보다 더 올라간경
				mAnimating = false;
				openDrawer();
			} else {
				moveHandle((int) mAnimationPosition);
				mCurrentAnimationTime += ANIMATION_FRAME_DURATION;
				mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
			}
		}
    
	}
    private void closeDrawer() {
		moveHandle(COLLAPSED_FULL_CLOSED);
		if (!mExpanded) {
			return;
		}
		mExpanded = false;

	}

	private void openDrawer() {
		moveHandle(EXPANDED_FULL_OPEN);

		if (mExpanded) {
			return;
		}
		mExpanded = true;

	}
    
    private float mAnimatedAcceleration;
	private float mAnimatedVelocity;
	private float mAnimationPosition;
	private long mAnimationLastTime;
	
	private void incrementAnimation() {
		long now = SystemClock.uptimeMillis();
		float t = (now - mAnimationLastTime) / 1000.0f; // ms -> s
		final float position = mAnimationPosition;
		final float v = mAnimatedVelocity; // px/s
		float a = mAnimatedAcceleration; // px/s/s
		//Log.d("test", mAnimatedVelocity + "");
		
		mAnimationPosition = position + (v * t) + (0.5f * a * t * t); // px
		mAnimatedVelocity = v + (a * t); // px/s
		mAnimationLastTime = now; // ms
	}
	
	
	//지정된 시간에 모두 올라가는지 판단.
	private boolean checkAnimationFrame(){
		
		float mStartAnimationPosition  = mAnimationPosition;
		
		float t = 0.04f; // s
		final float position = mAnimationPosition;
		final float v = mAnimatedVelocity; // px/s
		final float a = mAnimatedAcceleration; // px/s/s
		float mDestAnimationPosition = position + (v * t) + (0.5f * a * t * t); //
		boolean c1 = mDestAnimationPosition >= mCurrentBottomOffset  - 1; //최하단보다 더 내려간경우.
		boolean c2 = mDestAnimationPosition < mCurrentTopOffset ; //최상단보다 더 올라간경
		
		if(c1 || c2) return true;
		return false;
		
	}
	@Override
	protected void dispatchDraw(Canvas canvas) {
		final long drawingTime = getDrawingTime();
		//drawChild(canvas, content, drawingTime);
		
		if (mTracking || mAnimating) {
			final Bitmap cache = content.getDrawingCache();
			if (cache != null) {
				canvas.drawBitmap(cache, 0, content.getBottom(), null);
			} else {
				canvas.save();
				//canvas.translate(0, content.getTop());
				drawChild(canvas, content, drawingTime);
				canvas.restore();
			}
			invalidate();
		}else{
			drawChild(canvas, content, drawingTime);
		}
	}

	public static final String LOG_TAG = "Sliding";

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if (mTracking) {
			return;
		}

		final int width = r - l;
		final int height = b - t;
		content.layout(0, content.getTop(), content.getMeasuredWidth(), content.getTop() +content.getMeasuredHeight());		
		
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
			throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
		}


		int height = content.getMeasuredHeight();
		content.measure(MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY));

		setMeasuredDimension(widthSpecSize, heightSpecSize);
	}
	
    private class SlidingHandler extends Handler {

		public void handleMessage(Message m) {
			switch (m.what) {
			case MSG_ANIMATE:
				doAnimation();
				break;
			}
		}
	}
    
}
