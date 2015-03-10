/*
 * @(#)CategoryListContentViewChildCell.java 2014. 9. 6.
 *
 * Copyright 2014 NAVER Corp. All rights Reserved. 
 * NAVER PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.stest;

import com.example.stest.CategoryModelList.CategoryModel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 서브카테고리 목록 리스트
 * 
 * @author SHINING.SOO
 * 
 */
public class CategoryListContentViewChildCell extends RelativeLayout implements OnClickListener {

	public static final String TAG = CategoryListContentViewChildCell.class.getSimpleName();

	public interface CategoryListContentViewChildCellEventListener {
		public void onTapCell(CategoryListContentViewChildCell cell, boolean isTotal);
	}

	private static final CategoryListContentViewChildCellEventListener nullListener = new CategoryListContentViewChildCellEventListener() {
		@Override
		public void onTapCell(CategoryListContentViewChildCell cell, boolean isTotal) {
		}
	};

	private CategoryListContentViewChildCellEventListener fEventListener = nullListener;

	public void setEventListener(CategoryListContentViewChildCellEventListener eventListener) {
		if (eventListener == null) {
			eventListener = nullListener;
		}
		this.fEventListener = eventListener;
	}

	private View fTotalLayout;

	private View fLayout;
	private View fTopLine;
	private TextView fTitleView;
	private TextView fCountView;
	private View fBottomLine;

	private CategoryModel fParentCategoryModel;
	private CategoryModel fCategoryModel;

	public CategoryListContentViewChildCell(Context context) {
		super(context);
		initContentView();
	}

	public CategoryListContentViewChildCell(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContentView();
	}

	private void initContentView() {
		View.inflate(getContext(), R.layout.category_list_content_view_child_cell, this);

		fTotalLayout = findViewById(R.id.LinearLayout_CategoryListContentViewChildCell_Total);

		fLayout = findViewById(R.id.LinearLayout_CategoryListContentViewChildCell);
		fTopLine = findViewById(R.id.View_TopLine_CategoryListContentViewChildCell);
		fTitleView = (TextView) findViewById(R.id.TextView_CategoryListContentViewChildCell);
		fCountView = (TextView) findViewById(R.id.TextView_CategoryListContentViewChildCell_count);
		
		fBottomLine = findViewById(R.id.View_BottomLine_CategoryListContentViewChildCell);

		fTotalLayout.setOnClickListener(this);
		fLayout.setOnClickListener(this);
	}

	public void setCategoryModel(CategoryModel parentCategoryModel, CategoryModel categoryModel) {
		fParentCategoryModel = parentCategoryModel;
		fCategoryModel = categoryModel;
		fLayout.setVisibility(VISIBLE);
		fTitleView.setText(categoryModel.getCategoryTitle());
		fCountView.setText(categoryModel.getCategoryCount());
	}

	public CategoryModel getParentCategoryModel() {
		return fParentCategoryModel;
	}

	public CategoryModel getCategoryModel() {
		return fCategoryModel;
	}

	public void displayTotalLayout() {
		fTotalLayout.setVisibility(VISIBLE);
	}

	public void goneTotalLayout() {
		fTotalLayout.setVisibility(GONE);
	}

	public void displayTopLine() {
		fTopLine.setVisibility(VISIBLE);
	}

	public void goneTopLine() {
		fTopLine.setVisibility(GONE);
	}

	public void displayBottomLine() {
		fBottomLine.setVisibility(VISIBLE);
	}

	public void goneBottomLine() {
		fBottomLine.setVisibility(GONE);
	}

	// Implements OnClickListener
	@Override
	public void onClick(View v) {
		if (v.equals(fTotalLayout)) {
			fEventListener.onTapCell(this, true);
		} else {
			fEventListener.onTapCell(this, false);
		}
	}
}
