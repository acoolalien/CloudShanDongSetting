package com.shandong.cloudtv.common;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shandong.cloudtv.settings.R;

public class GeneraAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context cont;
	private int selectItem;
	List<CommonItemList> mCommonItemList;
	String[] mArrays;
	private final int TYPE_1 = 0;
	private final int TYPE_2 = 1;

	public class ViewHolder {
		public TextView itemName;
		public ImageView pageLeft;
		public EditText itemSetting;
		public ImageView pageRight;
	}

	public GeneraAdapter(Context context, List<CommonItemList> list, String[] a) {
		super();
		cont = context;
		mInflater = LayoutInflater.from(context);
		mCommonItemList = list;
		mArrays = a;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int i = mArrays != null ? mArrays.length : 0;
		Log.i("length", String.valueOf(i));
		return mArrays != null ? mArrays.length : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setSelectItem(int position) {
		selectItem = position;
	}

	public int getSelectItem() {
		return selectItem;
	}

	public int getItemViewType(int position) {
		int p = position;
		if (p == 0)
			return TYPE_1;
		else
			return TYPE_2;
	}

	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder1 = null;
		ViewHolder holder2 = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case (TYPE_1): {
				convertView = mInflater.inflate(
						R.layout.other_setting_item_list, null);
				holder1 = new ViewHolder();
				holder1.itemName = (TextView) convertView
						.findViewById(R.id.item_name);
				holder1.itemSetting = (EditText) convertView
						.findViewById(R.id.item_setting);
				holder1.pageLeft = (ImageView) convertView
						.findViewById(R.id.page_left);
				holder1.pageRight = (ImageView) convertView
						.findViewById(R.id.page_right);

				convertView.setTag(holder1);
				break;
			}
			case (TYPE_2): {
				convertView = mInflater.inflate(R.layout.activity_common_list2,
						null);
				holder2 = new ViewHolder();
				holder2.itemName = (TextView) convertView
						.findViewById(R.id.item_name);
				holder2.pageRight = (ImageView) convertView
						.findViewById(R.id.page_right);
				convertView.setTag(holder2);
				break;
			}
			}
		} else {
			switch (type) {
			case TYPE_1: {
				holder1 = (ViewHolder) convertView.getTag();
				break;
			}
			case TYPE_2: {
				holder2 = (ViewHolder) convertView.getTag();
				break;
			}

			}
		}

		switch (type) {
		case (TYPE_1): {
			if (null != mCommonItemList.get(position).getItemName()) {
				holder1.itemName.setText(mCommonItemList.get(position)
						.getItemName());

			}
			if (null != mCommonItemList.get(position).getPageLeft()) {
				holder1.pageLeft.setImageDrawable(mCommonItemList.get(position)
						.getPageLeft());
				holder1.pageLeft.setVisibility(View.VISIBLE);
				holder1.itemSetting.setVisibility(View.VISIBLE);

			} else {
				holder1.pageLeft.setImageDrawable(null);
				holder1.pageLeft.setVisibility(View.GONE);
			}
			if (null != mCommonItemList.get(position).getItemSetting()) {
				// TODO
				holder1.itemSetting.setText(mCommonItemList.get(position)
						.getItemSetting());
				holder1.itemSetting.setVisibility(View.VISIBLE);
			} else {
				holder1.itemSetting.setText(null);
				holder1.itemSetting.setVisibility(View.GONE);
			}
			if (null != mCommonItemList.get(position).getPageRight()) {
				holder1.pageRight.setImageDrawable(mCommonItemList
						.get(position).getPageRight());
				holder1.pageRight.setVisibility(View.VISIBLE);
			} else {
				holder1.pageRight.setImageDrawable(null);
				holder1.pageRight.setVisibility(View.GONE);
			}

			break;
		}
		case (TYPE_2): {
			if (null != mCommonItemList.get(position).getItemName()) {
				holder2.itemName.setText(mCommonItemList.get(position)
						.getItemName());

			}
			break;
		}
		}

		return convertView;
	}
}
