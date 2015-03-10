/*
 * @(#)CategoryModelList.java 2014. 7. 22.
 *
 * Copyright 2014 NAVER Corp. All rights Reserved. 
 * NAVER PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.example.stest;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryModelList extends ArrayList<CategoryModelList.CategoryModel> {
	private static final long serialVersionUID = 6773652246045958074L;

	public int getCategoryCount() {
		return this.size();
	}

	public static class CategoryModel implements Parcelable {
		public String dir_num;
		public String category_code;
		public String category_count;
		public List<TitleInfo> title_infos = new ArrayList<TitleInfo>();
		public List<CategoryModel> sub_dir_infos = new ArrayList<CategoryModel>();
		public transient boolean local;

		public static class TitleInfo implements Parcelable {
			public String lang;
			public String title;
			public String display_title;

			public TitleInfo(Parcel source) {
				this.lang = source.readString();
				this.title = source.readString();
				this.display_title = source.readString();
			}

			@Override
			public int describeContents() {
				return 0;
			}

			@Override
			public void writeToParcel(Parcel dest, int flags) {
				dest.writeString(this.lang);
				dest.writeString(this.title);
				dest.writeString(this.display_title);
			}

			public static final Parcelable.Creator<TitleInfo> CREATOR = new Parcelable.Creator<TitleInfo>() {
				public TitleInfo createFromParcel(Parcel source) {
					return new TitleInfo(source);
				}

				@Override
				public TitleInfo[] newArray(int size) {
					return new TitleInfo[size];
				}
			};
		}

		public CategoryModel() {

		}

		public CategoryModel(Parcel source) {
			this.dir_num = source.readString();
			this.category_code = source.readString();
			this.category_count = source.readString();
			source.readTypedList(title_infos, TitleInfo.CREATOR);
			source.readTypedList(sub_dir_infos, CategoryModel.CREATOR);
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(this.dir_num);
			dest.writeString(this.category_code);
			dest.writeString(this.category_count);
			dest.writeTypedList(this.title_infos);
			dest.writeTypedList(this.sub_dir_infos);
		}

		public static final Parcelable.Creator<CategoryModel> CREATOR = new Parcelable.Creator<CategoryModel>() {
			public CategoryModel createFromParcel(Parcel source) {
				return new CategoryModel(source);
			}

			@Override
			public CategoryModel[] newArray(int size) {
				return new CategoryModel[size];
			}
		};

		public int getSubCategoryCount() {
			if (sub_dir_infos != null) {
				return sub_dir_infos.size();
			}
			return 0;
		}

		public String getCategoryDisplayTitle() {
			if (title_infos != null) {
				for (TitleInfo titleInfo : title_infos) {
						if (titleInfo.display_title != null && ("").equals(titleInfo.display_title) == false) {
							return titleInfo.display_title;
						} else {
							return titleInfo.title;
						}
				}
			}

			return null;
		}

		public String getCategoryTitle() {
			if (title_infos != null) {
				for (TitleInfo titleInfo : title_infos) {
						if (titleInfo.title != null && ("").equals(titleInfo.title) == false) {
							return titleInfo.title;
						}
					}
			}

			return null;
		}
		
		public String getCategoryCount() {
			return UIUtil.putStringInBrackets(category_count);
		}

	}

}
