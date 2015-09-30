package com.shandong.cloudtv.common;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shandong.cloudtv.settings.R;

public class GeneralList extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context cont;
	private int selectItem;
	List<CommonItemList> mCommonItemList;
	String[] mArrays;

	public class ViewHolder {
		TextView itemName;
		ImageView pageLeft;
		TextView itemSetting;
		ImageView pageRight;
	}

	public GeneralList(Context context, List<CommonItemList> list, String[] a) {
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
		return mCommonItemList != null ? mCommonItemList.size() : 0;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activity_common_list, null);
			holder = new ViewHolder();
			holder.itemName = (TextView) convertView.findViewById(R.id.item_name);
			holder.itemSetting = (TextView) convertView.findViewById(R.id.item_setting);
			holder.pageLeft = (ImageView) convertView.findViewById(R.id.page_left);
			holder.pageRight = (ImageView) convertView.findViewById(R.id.page_right);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Log.i("position", String.valueOf(position));
		if (null != mCommonItemList.get(position).getItemName()) {
			holder.itemName.setText(mCommonItemList.get(position).getItemName());

		}
		if (null != mCommonItemList.get(position).getPageLeft()) {
			holder.pageLeft.setImageDrawable(mCommonItemList.get(position).getPageLeft());
			holder.pageLeft.setVisibility(View.VISIBLE);
			holder.itemSetting.setVisibility(View.VISIBLE);

		} else {
			holder.pageLeft.setImageDrawable(null);
			holder.pageLeft.setVisibility(View.GONE);
		}
		if (null != mCommonItemList.get(position).getItemSetting()) {
			// TODO
			holder.itemSetting.setText(mCommonItemList.get(position).getItemSetting());
			holder.itemSetting.setVisibility(View.VISIBLE);
		} else {
			holder.itemSetting.setText(null);
			holder.itemSetting.setVisibility(View.GONE);
		}
		if (null != mCommonItemList.get(position).getPageRight()) {
			holder.pageRight.setImageDrawable(mCommonItemList.get(position).getPageRight());
			holder.pageRight.setVisibility(View.VISIBLE);
		} else {
			holder.pageRight.setImageDrawable(null);
			holder.pageRight.setVisibility(View.GONE);
		}

		return convertView;
	}

}
