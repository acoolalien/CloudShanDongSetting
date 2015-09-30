package com.shandong.cloudtv.common;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import com.shandong.cloudtv.settings.R;

public class CheckVersionAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context cont;
	private int selectItem;
	List<CommonItemList> mCommonItemList;
	String[] mArrays;
	final int VIEW_TYPE = 2;
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;

	class ViewHolder1 {
		TextView itemName;

		TextView itemSetting;

	}

	class ViewHolder2 {
		TextView itemName;

		ImageView pageRight;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return mArrays != null ? mArrays.length : 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public CheckVersionAdapter(Context context, List<CommonItemList> list,
			String[] a) {
		super();
		//
		cont = context;
		mInflater = LayoutInflater.from(context);
		mCommonItemList = list;
		mArrays = a;
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
		ViewHolder1 holder1 = new ViewHolder1();
		ViewHolder2 holder2 = new ViewHolder2();
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case TYPE_1: {
				convertView = mInflater.inflate(R.layout.activity_common_2,
						null);
				holder1.itemName = (TextView) convertView
						.findViewById(R.id.item_name);
				holder1.itemSetting = (TextView) convertView
						.findViewById(R.id.item_setting);
				convertView.setTag(holder1);
				break;
			}
			case TYPE_2: {
				convertView = mInflater.inflate(R.layout.activity_common_list2,
						null);
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
			case TYPE_1:
				holder1 = (ViewHolder1) convertView.getTag();
				break;
			case TYPE_2:
				holder2 = (ViewHolder2) convertView.getTag();
				break;
			}
		}

		switch (type) {
		case TYPE_1: {
			if (null != mCommonItemList.get(0).getItemName()) {
				holder1.itemName.setText(mCommonItemList.get(0).getItemName());
				holder1.itemName.setVisibility(View.VISIBLE);
			}
			if (null != mCommonItemList.get(0).getItemSetting()) {
				holder1.itemSetting.setText(mCommonItemList.get(0)
						.getItemSetting());
				holder1.itemSetting.setVisibility(View.VISIBLE);
			}
			break;
		}

		case TYPE_2: {
			holder2.itemName.setText(mCommonItemList.get(1).getItemName());

			break;
		}
		}
		return convertView;
	}

}
