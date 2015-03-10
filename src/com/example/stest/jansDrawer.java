package com.example.stest;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by Jans on 15. 3. 3..
 */
public class jansDrawer extends RelativeLayout {

	private boolean mAnimating;
	private static final int MSG_ANIMATE = 1000;
	private static final int ANIMATION_FRAME_DURATION = 1000 / 80;
	
	private long mCurrentAnimationTime;
	
	private final Handler mHandler = new SlidingHandler();
	
	private VelocityTracker mVelocityTracker;
	
	private int startY= 0;
	private int startX= 0;
	private int mTapThreshold;
    
    private static final int TAP_THRESHOLD = 6;
    private static final int EXPANDED_FULL_OPEN = -10001;
	private static final int COLLAPSED_FULL_CLOSED = -10002;
	
	private static final float MAXIMUM_TAP_VELOCITY = 100.0f;
	private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
	private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
	private static final float MAXIMUM_ACCELERATION =  10000.0f; //원래 2000
	private static final int VELOCITY_UNITS = 1000;
	
    private boolean mTracking;
    private boolean mMoving = false;;
    private boolean mExpanded = false;
    private  int mMaximumTapVelocity;
	private int mMaximumMinorVelocity;
	private int mMaximumMajorVelocity;
	private int mMaximumAcceleration;
	private  int mVelocityUnits;
	
	private ViewGroup skin = null;
    public int mTouchDelta  = 0 ;
    private   float density;
    
    
    private int mTopOffset = 58; // 상단 오프
    private int mMiddleOffset = 200; // 중간에 멈춰야할 부분.
    private int mBottomOffset  = 100; //하단오프
    
    private int mCurrentTopOffset; // 현재 움직이고 있는 범위의 Top
    private int mCurrentBottomOffset; // 현재 움직이고 있는 범위 Bottom
    
	private Integer[] targetLine = new Integer[]{mTopOffset, mBottomOffset}; //초기값.
		
	
    public jansDrawer(Context context) {
        super(context);
    }
    public jansDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        
    }
    public jansDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public boolean isMoving() {
		return mTracking || mAnimating;
	}
    
    
    private View NestedScrollView = null; 
    @Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		
		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			
			if (isMoving()) {
				 return false;
			}
			if(!isInView(skin, event.getRawX(), event.getRawY()) ){ //스킨화면위를 클릭했는지.
				return false;
			}
			if(hitTestClickableView(skin, event.getRawX(), event.getRawY()) ){ //스킨화면위를 클릭했는지.
				//return false;
			}
			
			NestedScrollView = findScrollView( skin, event.getRawX(), event.getRawY() );
			 
			if(NestedScrollView != null ){
				MotionEvent ev = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,  event.getX(), event.getY(), 0);
				NestedScrollView.onTouchEventMine(event);
				//NestedScrollView.dispatchTouchEvent(event);
			}
			
			prepareTracking( event );
			
			return false;
		}
		
		if (action == MotionEvent.ACTION_MOVE) {
			if(!mTracking){ return false; }
			if( mMoving  ) { return false; }
			
			if(isOverThreshold(event)){
				
				mMoving = true;  // 움직이기 시작함.
				return true;
				
			}
			return false;
		}
		if (action == MotionEvent.ACTION_UP) {
			stopTracking();
		}
		return false;
	};
    

	/**
	 *  움직임이 시작되는 임계값을 지나쳤는지 판단한다. 
	 */
	private boolean  isOverThreshold(  MotionEvent event  ){
		
		int xdiff = Math.abs((int) event.getX() - startX);
		int ydiff = Math.abs((int) event.getY() - startY);
		if (Math.hypot(xdiff, ydiff) >  15) {
			if (xdiff < ydiff) {
				return true; // move 시
			} else {
				// 취소.
				stopTracking();
				return false;
			}
		}
		
		return false;
	}

    private void prepareTracking(MotionEvent event ) {
    	
    	mTouchDelta = (int) event.getY() - skin.getTop();
		mTracking = true;
		mVelocityTracker = VelocityTracker.obtain();
		mVelocityTracker.addMovement(event);

		startX = (int) event.getX();
		startY = (int) event.getY();
		
		mMoving = false;

	}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				if(!isInView(skin, event.getRawX(), event.getRawY()) ){ //스킨화면위를 클릭했는지.
					return false;
				}
				touchEventHandler(event);
				return true;
			case MotionEvent.ACTION_MOVE :
			case MotionEvent.ACTION_UP :
			case MotionEvent.ACTION_CANCEL :
			default :
				touchEventHandler(event);
				break;
		}
    	return false;
    };
    
    private boolean focusInScroll  = false; // 스크롤View가 동작하고 있는지.
    private boolean touchEventHandler(MotionEvent event){
    	
    	if(mTracking){
    		mVelocityTracker.addMovement(event);
    	}
    	
    	switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				prepareTracking(event);
				break;
			case MotionEvent.ACTION_MOVE :
				
				if(!mTracking){return false;}
				if(!mMoving){
					if(isOverThreshold(  event )){
						mMoving  = true;  //움직이기 시작함/
					};
					return false;
				}
				prepareTargetLine( skin.getTop(), true); //움직임을 전체범위로세팅.
				final int moveOffset = (int)event.getY() - mTouchDelta;
				
				if(NestedScrollView != null ){
					if(skin.getTop() == topOffseInSet &&  moveOffset < topOffseInSet ){
						
						if(!focusInScroll){
							//MotionEvent ev = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,  event.getX(), event.getY(), 0);
							event.setAction(MotionEvent.ACTION_DOWN);
							NestedScrollView.onTouchEventMine(event);
						}else{
							//MotionEvent ev = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,  event.getX(), event.getY(), 0);
							NestedScrollView.onTouchEventMine(event);
						}
						//sliding은 더이상 올라갈곳이 없는데, 더 밀어올리는경
						//스크롤이있으면 올림.
						NestedScrollView.onTouchEventMine(event);
						focusInScroll = true;
						return false;
					}
					if(skin.getTop() == topOffseInSet  &&  NestedScrollView.getScrollY() >  0 &&  moveOffset > topOffseInSet){
						//sliding은 더이상 올라갈곳이 없는데,      스크롤을 내려갈곳이 있고,                     방향이 아래로 향함.
						//스크롤이있으면 올림.
						NestedScrollView.onTouchEventMine(event);
						focusInScroll = true;
						return false;
					}
				}
				if(focusInScroll){
					//스크롤을 움직이다가 다시 sliding을 움직이기 시작한경
					focusInScroll = false;
					mTouchDelta = (int) event.getY() - skin.getTop();
					return false;
					
				}
				moveHandle(  moveOffset );
				mTouchDelta = (int) event.getY() - skin.getTop();
				
				break;
			case MotionEvent.ACTION_UP :
			case MotionEvent.ACTION_CANCEL :
				
				if(NestedScrollView != null ){
						NestedScrollView.onTouchEventMine(event);
						NestedScrollView.requestLayout();
				}
				if(focusInScroll){ // 스크롤링중이었으면 취소.
					stopTracking();
					return false;
				}
				prepareTargetLine( skin.getTop(), false ); //움직임을 현저 범위안으로 세팅.
				
				if(!mTracking){return false;}
				if(!mMoving){return false;}
		    		
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
	    		
	    		int position = skin.getTop();
	    		//현재위치에서 3의 가속도로 올라감.
	    		performFling(  position, velocity , false  );
		    		
				break;
			
    	}
    	return true;
    	
    }
 
    private void stopTracking() {

    	mTracking = false;
    	NestedScrollView = null;
    	mMoving = false;

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
    
    private void moveHandle(int position) {
    	
		final View handle = skin;

		if (position == EXPANDED_FULL_OPEN) {
			
			handle.offsetTopAndBottom(mCurrentTopOffset - handle.getTop());
			invalidate();
				
		} else if (position == COLLAPSED_FULL_CLOSED) {
			
			int a = handle.getTop();
			handle.offsetTopAndBottom(mCurrentBottomOffset -  handle.getTop());
			invalidate();
				
		} else {
			
			final int top = handle.getTop();
			int deltaY = position - top;
			if (position < mCurrentTopOffset ) {
				//최상단보다 더 올라가면 최상단으로 맞춤.
				deltaY = mCurrentTopOffset - top;
			} else if (deltaY > mCurrentBottomOffset - top) {
				//핸들의 최하단 보다 더 밑으로 내려가면. 최하단으로 맞춤.
				deltaY = mCurrentBottomOffset  - top;
			}
			
			handle.offsetTopAndBottom(deltaY);
			invalidate();
			
		}
		
	}
    
    private int topOffseInSet;
	private ViewGroup drawerContent; 
	//컨텐츠가 움직일 범위를 세팅한다.
	private void prepareTargetLine(int position, boolean fullRange){
		
		
		topOffseInSet =  targetLine[0]; //가장 위에있는 라인
		
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
	
    
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
        return px;
    }
    public void updateOffset(){
    	updateOffset(0,0,0);
    }
    public void updateOffset(int top, int middle, int bottom){
		mTopOffset	= top == 0 ? mTopOffset :  dpToPx(top);
		mMiddleOffset = middle == 0 ? mMiddleOffset : dpToPx(middle);
		mBottomOffset = bottom == 0 ? mBottomOffset : dpToPx(bottom);
		if(mMiddleOffset == 0 ){
			targetLine = new Integer[]{mTopOffset, getHeight() - mBottomOffset};
		}else{
			targetLine = new Integer[]{mTopOffset, mMiddleOffset, getHeight() - mBottomOffset};
		}
	}
    
    @Override
    public void onFinishInflate(){
    	
    	mTopOffset	= dpToPx(mTopOffset);
		mMiddleOffset = dpToPx(mMiddleOffset);
		mBottomOffset = dpToPx(mBottomOffset);
		
    	density = getResources().getDisplayMetrics().density;
    	
    	mMaximumTapVelocity = (int) (MAXIMUM_TAP_VELOCITY * density + 0.5f);
		mMaximumMinorVelocity = (int) (MAXIMUM_MINOR_VELOCITY * density + 0.5f);
		mMaximumMajorVelocity = (int) (MAXIMUM_MAJOR_VELOCITY * density + 0.5f);
		mMaximumAcceleration = (int) (MAXIMUM_ACCELERATION * density + 0.5f);
		mVelocityUnits = (int) (VELOCITY_UNITS * density + 0.5f);
		mTapThreshold = (int) (TAP_THRESHOLD * density + 0.5f);

        skin = (ViewGroup) findViewById(R.id.wrap);
        drawerContent = (ViewGroup ) findViewById(R.id.drawerContent);
        
        drawerContent.setDrawingCacheEnabled(false);
        skin.setDrawingCacheEnabled(false);
        setDrawingCacheEnabled(false);
        
        //Create시점에 height를 가져오지 못하기때문에 이곳에서 체크함.
        this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {
	            if (!mMeasured) {
	                // Here your view is already layed out and measured for the first time
	            	
	            	updateOffset();
	            	prepareTargetLine( 0, true);
	            	closeDrawer();
	            	//skin.getLayoutParams().height = 200;
	            	//skin.requ
	                mMeasured = true; // Some optional flag to mark, that we already got the sizes
	            }
	        }
	    });
        
    }
    private boolean mMeasured = false;
    
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
			
			c1 = velocity > mMaximumMajorVelocity ; //속도가 최고속도보다 크다.
			//c2 =  position > mTopOffset + (mHandleHeight )8;
			c2 =  position > mCurrentTopOffset + mThresHold ; // 위치가 top보다 아래에 있다.
			c3 =  velocity > -mMaximumMajorVelocity; //속도가  -최고속도보다 크다.
			
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
		
		if( checkAnimationFrame() ){ //빠른속도로 움직이고 있으면.
			prepareTargetLine( 0, true); //최상단, 최하단까지 움직일수있도록 변경.
		}
		
    	long now = SystemClock.uptimeMillis();
    	mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
		mAnimationLastTime = now;
    	mAnimating = true;
    	mHandler.removeMessages(MSG_ANIMATE);
		mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
		stopTracking();
    	
    }
    
    
    
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
		
		if (mTracking || mAnimating) {
			
			final Bitmap cache2 = drawerContent.getDrawingCache();
			if (cache2 != null) {
				canvas.drawBitmap(cache2, 0, drawerContent.getBottom(), null);
			} else {
				canvas.save();
				drawChild(canvas, drawerContent, drawingTime);
				canvas.restore();
			}
			
			final Bitmap cache = skin.getDrawingCache();
			if (cache != null) {
				canvas.drawBitmap(cache, 0, skin.getBottom(), null);
			} else {
				canvas.save();
				//canvas.translate(0, skin.getTop());
				drawChild(canvas, skin, drawingTime);
				canvas.restore();
			}
			
			invalidate();
		}else{
			drawChild(canvas, drawerContent, drawingTime);
			drawChild(canvas, skin, drawingTime);
		}
		
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		if (mTracking || mAnimating) {
//			return;
//		}
		final int width = r - l;
		final int height = b - t;
		Log.d("tag", "" +t +"__" +b);
		drawerContent.layout(l, t ,r, b - mBottomOffset);
		skin.layout(0, skin.getTop(), skin.getMeasuredWidth(), skin.getTop() +skin.getMeasuredHeight());
		
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
		drawerContent.measure(MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY));
		skin.measure(MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY), 
				MeasureSpec.makeMeasureSpec(heightSpecSize - mTopOffset, MeasureSpec.EXACTLY));

		setMeasuredDimension(widthSpecSize, heightSpecSize);
		
	}
	
	private Rect mViewRectInWindow = new Rect();
	
	private boolean isInView(View skinView, float rawX, float rawY) {
		int viewLocationInWindow[] = getLocationInWindow(skinView);
		mViewRectInWindow.left = viewLocationInWindow[0];
		mViewRectInWindow.top = viewLocationInWindow[1];
		mViewRectInWindow.right = mViewRectInWindow.left + skinView.getWidth();
		mViewRectInWindow.bottom = mViewRectInWindow.top + skinView.getHeight();
		if (mViewRectInWindow.contains((int) rawX, (int) rawY)) {
			return true;
		}

		return false;
	}
	private boolean hitTestClickableView(View view, float rawX, float rawY) {
		
		if (view != skin && view.isClickable()) {
			int viewLocationInWindow[] = getLocationInWindow(view);
			mViewRectInWindow.left = viewLocationInWindow[0];
			mViewRectInWindow.top = viewLocationInWindow[1];
			mViewRectInWindow.right = mViewRectInWindow.left + view.getWidth();
			mViewRectInWindow.bottom = mViewRectInWindow.top + view.getHeight();
			if (mViewRectInWindow.contains((int) rawX, (int) rawY)) {
				return true;
			}
		}

		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			int size = viewGroup.getChildCount();
			for (int i = 0; i < size; i++) {
				if (hitTestClickableView(viewGroup.getChildAt(i), rawX, rawY)) {
					return true;
				}
			}
		}

		return false;
	}
	
	private View findScrollView(View view, float rawX, float rawY){
		
		if( view instanceof ScrollView || view instanceof ListView){
			int viewLocationInWindow[] = getLocationInWindow(view);
			mViewRectInWindow.left = viewLocationInWindow[0];
			mViewRectInWindow.top = viewLocationInWindow[1];
			mViewRectInWindow.right = mViewRectInWindow.left + view.getWidth();
			mViewRectInWindow.bottom = mViewRectInWindow.top + view.getHeight();
			if (mViewRectInWindow.contains((int) rawX, (int) rawY)) {
				return view;
			}
			return null;
		}
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			int size = viewGroup.getChildCount();
			for (int i = 0; i < size; i++) {
				View v = findScrollView(viewGroup.getChildAt(i), rawX, rawY);
				if( v != null){
					return v;
				}
			}
		}
		return null;
	}
	
	
	public static int[] getLocationInWindow(View view) {
		int location[] = new int[2];
		view.getLocationInWindow(location);
		return location;
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


	/**
	 * 
	 */
	public void init() {
		prepareTargetLine(0, true);
        closeDrawer();
	}
    
}