/*
 * @(#)CategoryListContentView.java 2014. 9. 6.
 *
 * Copyright 2014 NAVER Corp. All rights Reserved. 
 * NAVER PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.stest;

import com.example.stest.CategoryListContentViewCell.CategoryListContentViewCellEventListener;
import com.example.stest.CategoryListContentViewChildCell.CategoryListContentViewChildCellEventListener;
import com.example.stest.CategoryModelList.CategoryModel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;


public class CategoryListContentView extends LinearLayout implements CategoryListContentViewCellEventListener,
		CategoryListContentViewChildCellEventListener {

	public static final String TAG = CategoryListContentView.class.getSimpleName();

	// interface CategoryListContentViewEventListener
	public interface CategoryListContentViewEventListener {
		void onTapCategoryListContentView(CategoryListContentView contentView, CategoryModel model);
	}

	private static final CategoryListContentViewEventListener nullListener = new CategoryListContentViewEventListener() {
		@Override
		public void onTapCategoryListContentView(CategoryListContentView contentView, CategoryModel model) {
		}
	};

	private CategoryListContentViewEventListener eventListener = nullListener;

	public void setEventListener(CategoryListContentViewEventListener eventListener) {
		if (eventListener == null) {
			eventListener = nullListener;
		}
		this.eventListener = eventListener;
	}

	private CategoryListContentViewAdapter fAdapter;
	protected CategoryModelList fCategoryModelList;

	public CategoryListContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initContentView();
	}

	public CategoryListContentView(Context context) {
		super(context);
		initContentView();
	}

	private void initContentView() {
//		setSelector(android.R.color.transparent);
//		setDividerHeight(0);
//		setGroupIndicator(null);
//		setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_DISABLED);
//		setFastScrollEnabled(true);

		fAdapter = new CategoryListContentViewAdapter();
	}

	public void setCategoryModels(CategoryModelList models) {
		fCategoryModelList = models;
		//setAdapter(fAdapter);
		fAdapter.notifyDataSetChanged();
	}

	// implements CategoryListContentViewCellEventListener
	@Override
	public void onTapCell(CategoryListContentViewCell cell) {
		eventListener.onTapCategoryListContentView(this, cell.getCategoryModel());
	}

	// implements CategoryListContentViewCellEventListener
	@Override
	public void onTapRightButton(CategoryListContentViewCell cell) {
		int position = cell.getGroupPosition();
		if (isGroupExpanded(position) == true) {
			collapseGroup(position);
		} else {
			expandGroup(position);
		}
		fAdapter.notifyDataSetChanged();
	}

	private void expandGroup(int position) {
		// TODO Auto-generated method stub
		
	}

	private void collapseGroup(int position) {
		// TODO Auto-generated method stub
		
	}

	private boolean isGroupExpanded(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	// implements CategoryListContentViewChildCellEventListener
	@Override
	public void onTapCell(CategoryListContentViewChildCell cell, boolean isTotal) {
		if (isTotal == true) {
			eventListener.onTapCategoryListContentView(this, cell.getParentCategoryModel());
		} else {
			eventListener.onTapCategoryListContentView(this, cell.getCategoryModel());
		}
	}

	// class CategoryListContentViewAdapter
	class CategoryListContentViewAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			if (fCategoryModelList != null) {
				return fCategoryModelList.getCategoryCount();
			}
			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (fCategoryModelList != null && fCategoryModelList.get(groupPosition) != null) {
				return fCategoryModelList.get(groupPosition).getSubCategoryCount();
			}
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return fCategoryModelList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return fCategoryModelList.get(groupPosition).sub_dir_infos.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		/*
		 * @Override public void onGroupExpanded(int groupPosition) { super.onGroupExpanded(groupPosition);
		 * 
		 * for (int i = 0; i < getGroupCount(); i++) { if (!(i == groupPosition)) collapseGroup(i); } }
		 */

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			CategoryListContentViewCell view = (CategoryListContentViewCell) convertView;

			if (view == null) {
				view = new CategoryListContentViewCell(getContext());
				view.setEventListener(CategoryListContentView.this);
			}

			CategoryModel model = (CategoryModel) getGroup(groupPosition);
			view.setCategoryModel(model);
			view.setGroupPosition(groupPosition);

			view.initCategoryIcon(model.category_code);

			if (model.getSubCategoryCount() > 0) {
				view.changeRightButtonVisibilityToVisible();
			} else {
				view.changeRightButtonVisibilityToGone();
			}

			view.changeTopLineVisibilityToGone();
			view.changeBottomLineColorToNormalMode();
			if (isExpanded == false) {
				if (groupPosition == 0) {
					changeBottomLineColor(groupPosition, view);
				} else if (groupPosition < getGroupCount() - 1) {
					changeBottomLineColor(groupPosition, view);
					changeTopLineVisibility(groupPosition, view);
				} else {
					changeTopLineVisibility(groupPosition, view);
				}
				view.changeIconAndTextColorsToNormalMode();
			} else {
				changeTopLineVisibility(groupPosition, view);
				view.changeIconAndTextColorsToHighlightMode();
			}

			return view;
		}

		private void changeTopLineVisibility(int groupPosition, CategoryListContentViewCell view) {
			if (isGroupExpanded(groupPosition - 1)) {
				view.changeTopLineVisibilityToVisible();
			} else {
				view.changeTopLineVisibilityToGone();
			}
		}

		private void changeBottomLineColor(int groupPosition, CategoryListContentViewCell view) {
			if (isGroupExpanded(groupPosition + 1)) {
				view.changeBottomLineColorToHighlightMode();
			} else {
				view.changeBottomLineColorToNormalMode();
			}
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			CategoryListContentViewChildCell view = (CategoryListContentViewChildCell) convertView;

			if (view == null) {
				view = new CategoryListContentViewChildCell(getContext());
				view.setEventListener(CategoryListContentView.this);
			}

			CategoryModel parentModel = (CategoryModel) getGroup(groupPosition);
			CategoryModel model = (CategoryModel) getChild(groupPosition, childPosition);
			view.setCategoryModel(parentModel, model);

			if (childPosition == 0) {
				view.displayTotalLayout();
				// view.displayTopLine();
			} else {
				view.goneTotalLayout();
				// view.goneTopLine();
			}

			if (isLastChild == true) {
				view.displayBottomLine();
				// smoothScrollToPosition(groupPosition);
			} else {
				view.goneBottomLine();
			}

			return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}
}
