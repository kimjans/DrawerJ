/*
 * @(#)UIUtil.java 2014. 9. 6.
 *
 * Copyright 2014 NAVER Corp. All rights Reserved. 
 * NAVER PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.stest;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;


public class UIUtil {
	public static final String TAG = UIUtil.class.getSimpleName();

	public static final Bitmap EMPTY_IMAGE = Bitmap.createBitmap(10, 10, Bitmap.Config.RGB_565);
	static {
		EMPTY_IMAGE.eraseColor(0xFFFF0000);
	}
	public static LinkedBlockingQueue<Runnable> sQueue = new LinkedBlockingQueue<Runnable>(20);
	public static ExecutorService sListPool = new ThreadPoolExecutor(0, 1, 10, TimeUnit.SECONDS, sQueue);

	public static final int getOsVersion() {
		return (android.os.Build.VERSION.SDK_INT);
	}

	public static final int pxFromDp(float dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}

	public static final float dpFromPx(int px) {
		return px / Resources.getSystem().getDisplayMetrics().density;
	}

	public static final float density() {
		return Resources.getSystem().getDisplayMetrics().density;
	}

	public static final int screenWidthInPixel() {
		return Resources.getSystem().getDisplayMetrics().widthPixels;
	}

	public static final int screenHeightInPixel() {
		return Resources.getSystem().getDisplayMetrics().heightPixels;
	}

	public static final int screenHeightWithoutStatusBarInPixel() {
		return screenHeightInPixel() - getStatusBarHeight();
	}

	public static int[] getLocationOnScreen(View view) {
		int location[] = new int[2];
		view.getLocationOnScreen(location);
		return location;
	}

	public static int[] getLocationInWindow(View view) {
		int location[] = new int[2];
		view.getLocationInWindow(location);
		return location;
	}

	public static int getStatusBarHeight() {
		int result = 0;
		int resId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
		if (resId > 0) {
			result = Resources.getSystem().getDimensionPixelSize(resId);
		}
		return result;
	}

	public static int getTopStatusBarHeight(Activity activity) {
		Rect rect = new Rect();

		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

		return rect.top;
	}

	public static boolean isBottomStatusBar(Activity activity) {
		if (getTopStatusBarHeight(activity) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void showShortToast(Context context, String text) {
		// Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void showLongToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void setTextVisibleOrGone(CharSequence text, TextView view) {
		if (TextUtils.isEmpty(text) == false) {
			view.setText(text);
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	public static double round(double d, int n) {
		return Math.round(d * Math.pow(10, n)) / Math.pow(10, n);
	}

	public static boolean isAnimating(View view) {
		Animation a = view.getAnimation();
		if (a != null && !a.hasEnded()) {
			return true;
		}
		return false;
	}

	/**
	 * TextView의 일부 글자만 색상 바꾸기
	 * 
	 * @param view
	 * @param fullText
	 * @param partialText
	 * @param color
	 */
	public static void SetTextViewChangePartialColor(TextView view, String fullText, String partialText, int color) {
		if (partialText == null) {
			view.setText(fullText);
			return;
		}
		String tempFullText = fullText.toLowerCase(Locale.US);
		String tempPartialText = partialText.toLowerCase(Locale.US);
		view.setText(fullText, TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) view.getText();
		int startIndex = tempFullText.indexOf(tempPartialText);
		if (startIndex != -1) {
			str.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + partialText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * TextView의 일부 글자만 색상 바꾸기(XLT에 문장부호로 묶인 문자열의 색상 변경 시)
	 * 
	 * @param view
	 * @param fullText
	 * @param partialText
	 * @param color
	 */
	public static void SetTextViewChangePartialColorWithMarkCharacter(TextView view, String fullText, String partialText, int color) {
		if (partialText == null) {
			view.setText(fullText);
			return;
		}
		String tempFullText = fullText.toLowerCase(Locale.US);
		String tempPartialText = partialText.toLowerCase(Locale.US);
		view.setText(fullText, TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) view.getText();
		int startIndex = tempFullText.indexOf(tempPartialText) - 1;
		if (startIndex != -1) {
			str.setSpan(new ForegroundColorSpan(color), startIndex, startIndex + partialText.length() + 2,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	/**
	 * 3자리 수마다 , 찍어서 string 으로 리턴
	 * 
	 * @param num
	 * @return
	 */
	public static String setNumberFormat(int num) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(num);
	}

	public static int getIdentifier(Resources resources, String name, String defType) {
		int identifier = resources.getIdentifier(name, defType,  "__LINE_IDENTYTY__");
		return identifier;
	}

	public static int getJellyBeanApiVersion() {
		return 18;
	}

	public static String ellipsisText(String text) {
		final int MAX_ELLIPSIS_LIMIT = 25;
		final int ELLIPSIS_LIMIT = 25;

		return ellipsisText(text, MAX_ELLIPSIS_LIMIT, ELLIPSIS_LIMIT);
	}

	public static String ellipsisText(String text, int maxEllipsisLimit, int ellipsisLimit) {
		byte[] bs = text.getBytes();
		final String POSTFIX = "...";

		if (bs.length > maxEllipsisLimit) {
			String ss = subStringBytes(text, ellipsisLimit);
			return ss.getBytes().length == bs.length ? ss : ss + POSTFIX;
		}
		return text;
	}

	public static String subStringBytes(String str, int byteLength) {
		int retLength = 0;
		int tempSize = 0;
		int asc;
		int length = str.length();

		for (int i = 1; i <= length; i++) {
			asc = (int) str.charAt(i - 1);
			if (asc > 127) {
				if (byteLength >= tempSize + 2) {
					tempSize += 2;
					retLength++;
				} else {
					return str.substring(0, retLength);
				}
			} else {
				if (byteLength > tempSize) {
					tempSize++;
					retLength++;
				}
			}
		}

		return str.substring(0, retLength);
	}

	public static String putStringInBrackets(String rawString) {
		if (rawString != null) {
			return "(" + rawString + ")";
		} else {
			return "";
		}
	}

	public static float measureTextLengthToPixel(float textSize, String text) {
		Paint p = new Paint();
		p.setTextSize(textSize);
		return p.measureText(text, 0, text.length());
	}

	public static boolean isCategoryNewLine(TextView view, String text, float leftMargin, float middleMargin, float rightMargin) {
		Rect bounds = new Rect();
		Paint paint = view.getPaint();
		paint.getTextBounds(text, 0, text.length(), bounds);
		if ((UIUtil.screenWidthInPixel() - UIUtil.pxFromDp(leftMargin) - UIUtil.pxFromDp(rightMargin) - UIUtil.pxFromDp(middleMargin)) < bounds
				.width()) {
			return true;
		}
		return false;
	}

	public static String getCategoryCodeToKorTitle(String code) {
		if (code.equals("09040000")) {
			return "화장실";
		} else if (code.equals("09220000")) {
			return "흡연실";
		} else if (code.equals("09160000")) {
			return "수유실";
		} else if (code.equals("09120000")) {
			return "ATM";
		} else if (code.equals("09140000")) {
			return "코인락커";
		} else if (code.equals("01000000")) {
			return "음식";
		} else if (code.equals("02000000")) {
			return "카페";
		} else if (code.equals("05000000")) {
			return "엔터테인먼트";
		} else {
			return "기타";
		}
	}
}
