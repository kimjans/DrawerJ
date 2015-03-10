/*
 * @(#)CategoryListContentViewCell.java 2014. 9. 6.
 *
 * Copyright 2014 NAVER Corp. All rights Reserved. 
 * NAVER PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.stest;

import com.example.stest.CategoryModelList.CategoryModel;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


public class CategoryListContentViewCell extends RelativeLayout implements OnClickListener {

	public static final String TAG = CategoryListContentViewCell.class.getSimpleName();

	public interface CategoryListContentViewCellEventListener {
		public void onTapCell(CategoryListContentViewCell cell);

		public void onTapRightButton(CategoryListContentViewCell cell);
	}

	private static final CategoryListContentViewCellEventListener nullListener = new CategoryListContentViewCellEventListener() {
		@Override
		public void onTapCell(CategoryListContentViewCell cell) {
		}

		@Override
		public void onTapRightButton(CategoryListContentViewCell cell) {
		}
	};

	private CategoryListContentViewCellEventListener fEventListener = nullListener;

	public void setEventListener(CategoryListContentViewCellEventListener eventListener) {
		if (eventListener == null) {
			eventListener = nullListener;
		}
		this.fEventListener = eventListener;
	}

	private View fCommonLayout;
	private ImageView fCategoryListContentViewCellIcon;
	private TextView fCommonTitleView;
	private TextView fCountView;
	private ToggleButton fCommonRightButton;

	private View fTopLine;
	private View fBottomLine;

	private CategoryModel fCategoryModel;

	private int fGroupPosition;

	public CategoryListContentViewCell(Context context) {
		super(context);
		initContentView();
	}

	public CategoryListContentViewCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContentView();
	}

	private void initContentView() {
		View.inflate(getContext(), R.layout.category_list_content_view_cell, this);

		fCommonLayout = findViewById(R.id.LinearLayout_CategoryListContentViewCell_common);
		fCategoryListContentViewCellIcon = (ImageView) findViewById(R.id.ImageView_CategoryListContentViewCell_icon);
		fCommonTitleView = (TextView) findViewById(R.id.TextView_CategoryListContentViewCell_result);
		fCountView = (TextView) findViewById(R.id.TextView_CategoryListContentViewCell_result_count);
		
		fCommonRightButton = (ToggleButton) findViewById(R.id.ToggleButton_CategoryListContentViewCell_RightBtn);

		fTopLine = findViewById(R.id.View_TopLine_category_list_activity);
		fBottomLine = findViewById(R.id.View_BottomLine_category_list_activity);

		fCommonLayout.setOnClickListener(this);
		fCommonRightButton.setOnClickListener(this);
	}

	public void setCategoryModel(CategoryModel categoryModel) {
		fCategoryModel = categoryModel;
		fCommonLayout.setVisibility(VISIBLE);
		fCommonTitleView.setText(categoryModel.getCategoryTitle());
		fCountView.setText(categoryModel.getCategoryCount());
	}

	public CategoryModel getCategoryModel() {
		return fCategoryModel;
	}

	// Implements OnClickListener
	@Override
	public void onClick(View v) {
		/*
		 * if (v.equals(fCommonRightButton)) { fEventListener.onTapRightButton(this); } else { fEventListener.onTapCell(this); }
		 */
		if (fCategoryModel != null && fCategoryModel.getSubCategoryCount() > 0) {
			fEventListener.onTapRightButton(this);
		} else {
			fEventListener.onTapCell(this);
		}
	}

	public void setGroupPosition(int groupPosition) {
		fGroupPosition = groupPosition;
	}

	public int getGroupPosition() {
		return fGroupPosition;
	}

	public void changeRightButtonVisibilityToVisible() {
		fCommonRightButton.setVisibility(VISIBLE);
	}

	public void changeRightButtonVisibilityToGone() {
		fCommonRightButton.setVisibility(GONE);
	}

	public void changeBottomLineColorToHighlightMode() {
		fBottomLine.setBackgroundColor(Color.parseColor("#D7D7DA"));
	}

	public void changeBottomLineColorToNormalMode() {
		fBottomLine.setBackgroundColor(Color.parseColor("#EBEBEE"));
	}

	public void changeTopLineVisibilityToVisible() {
		fTopLine.setVisibility(VISIBLE);
	}

	public void changeTopLineVisibilityToGone() {
		fTopLine.setVisibility(GONE);
	}

	public void changeIconAndTextColorsToHighlightMode() {
		fCategoryListContentViewCellIcon.setSelected(true);
		fCommonTitleView.setSelected(true);
		fCommonRightButton.setChecked(true);
	}

	public void changeIconAndTextColorsToNormalMode() {
		fCategoryListContentViewCellIcon.setSelected(false);
		fCommonTitleView.setSelected(false);
		fCommonRightButton.setChecked(false);
	}

	public void initCategoryIcon(String categoryId) {
		int categoryIconIdentifier = UIUtil.getIdentifier(getResources(), "indoormap_category_icon_b_" + categoryId, "drawable");
		if (categoryIconIdentifier != 0) {
			fCategoryListContentViewCellIcon.setImageDrawable(getContext().getResources().getDrawable(categoryIconIdentifier));
		} else {
			fCategoryListContentViewCellIcon.setImageDrawable(getContext().getResources().getDrawable(
					R.drawable.ic_launcher));
		}
	}
}
