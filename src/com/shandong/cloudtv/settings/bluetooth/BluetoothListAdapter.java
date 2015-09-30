package com.shandong.cloudtv.settings.bluetooth;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shandong.cloudtv.settings.BlueToothActivity;
import com.shandong.cloudtv.settings.R;

public class BluetoothListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context cont;
	List<BluetoothScan> list;
	private int selectItem;
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;

	class ViewHolder2 {
		TextView itemName;
		TextView bluetoothAddress;
		TextView itemSetting;
	}

	class ViewHolder1 {
		TextView itemName;
		ImageView pageLeft;
		TextView itemSetting;
		ImageView pageRight;

	}

	public BluetoothListAdapter(Context context, List<BluetoothScan> scan) {
		super();
		cont = context;
		mInflater = LayoutInflater.from(context);
		list = scan;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list != null ? list.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public int getItemViewType(int position) {
		int p = position;
		if (p == 0 || p == 1)
			return TYPE_1;
		else
			return TYPE_2;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public void setSelectItem(int position) {
		selectItem = position;
	}

	public int getSelectItem() {
		return selectItem;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder1 holder1 = null;
		ViewHolder2 holder2 = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case TYPE_2: {
				convertView = mInflater.inflate(R.layout.activity_bluetooth_item, null);
				holder2 = new ViewHolder2();
				holder2.itemName = (TextView) convertView.findViewById(R.id.item_name);
				holder2.itemSetting = (TextView) convertView.findViewById(R.id.item_setting);
				holder2.bluetoothAddress = (TextView) convertView.findViewById(R.id.bluetooth_item);

				convertView.setTag(holder2);
				break;
			}
			case TYPE_1: {
				convertView = mInflater.inflate(R.layout.activity_bluetooth_item_type1, null);
				holder1 = new ViewHolder1();
				holder1.itemName = (TextView) convertView.findViewById(R.id.item_name);
				holder1.itemSetting = (TextView) convertView.findViewById(R.id.item_setting);
				holder1.pageLeft = (ImageView) convertView.findViewById(R.id.page_left);
				holder1.pageRight = (ImageView) convertView.findViewById(R.id.page_right);
				convertView.setTag(holder1);
				break;
			}
			default:
				break;
			}

		} else {
			switch (type) {
			case TYPE_1: {
				holder1 = (ViewHolder1) convertView.getTag();
				break;
			}
			case TYPE_2: {
				holder2 = (ViewHolder2) convertView.getTag();
				break;
			}
			}

		}
		// config the view
		switch (type) {
		case TYPE_2: {
			if (null != list.get(position).getName() && !"".equals(list.get(position).getName())) {
				holder2.itemName.setText(list.get(position).getName());
				holder2.itemName.setVisibility(View.VISIBLE);
			} else {
				holder2.itemName.setVisibility(View.GONE);
			}
			if (null != list.get(position).getAddress()
					&& !"".equals(list.get(position).getAddress())) {
				holder2.bluetoothAddress.setText(list.get(position).getAddress());
			} else {
				holder2.bluetoothAddress.setVisibility(View.GONE);
			}

			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_CONNECTED) {
				holder2.itemSetting.setText(cont.getResources().getString(
						R.string.bluetooth_connected));
				holder2.itemSetting.setVisibility(View.VISIBLE);
			}

			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_DISCOVERING) {
				holder2.itemSetting.setText(cont.getResources().getString(
						R.string.bluetooth_discovering_device));
				holder2.itemSetting.setVisibility(View.VISIBLE);
			}

			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_NO_DISCOVER) {
				holder2.itemSetting.setText(null);
				holder2.itemSetting.setVisibility(View.GONE);
			}

			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_BONDED) {
				holder2.itemSetting.setText(cont.getResources()
						.getString(R.string.bluetooth_bonded));
				holder2.itemSetting.setVisibility(View.VISIBLE);
			}

			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_NOBOND) {
				holder2.itemSetting.setText(null);
				holder2.itemSetting.setVisibility(View.GONE);
			}

			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_CONNECTING) {
				holder2.itemSetting.setText(cont.getResources().getString(
						R.string.bluetooth_connecting));
				holder2.itemSetting.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_DISCONNECTING) {
				holder2.itemSetting.setText(cont.getResources().getString(
						R.string.bluetooth_disconnecting));
				holder2.itemSetting.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_DISCONNECTED) {
				holder2.itemSetting.setText(cont.getResources().getString(
						R.string.bluetooth_disconnected));
				holder2.itemSetting.setVisibility(View.VISIBLE);
			}
			if (list.get(position).getStatus() == BlueToothActivity.BLUETOOTH_BONDING) {
				holder2.itemSetting.setText(cont.getResources().getString(
						R.string.bluetooth_pairing));
				holder2.itemSetting.setVisibility(View.VISIBLE);
			}
			break;
		}
		case TYPE_1: {
			if (null != list.get(position).getName() && !"".equals(list.get(position).getName())) {
				holder1.itemName.setText(list.get(position).getName());
				holder1.itemName.setVisibility(View.VISIBLE);
			} else {
				holder1.itemName.setVisibility(View.GONE);
			}
			if (null != list.get(position).getAddress()
					&& !"".equals(list.get(position).getAddress())) {
				holder1.itemSetting.setText(list.get(position).getAddress());
				holder1.itemSetting.setVisibility(View.VISIBLE);
			} else {
				holder1.itemSetting.setVisibility(View.GONE);
			}
			if (null != list.get(position).getPageleft()) {
				holder1.pageLeft.setImageDrawable(list.get(position).getPageleft());
				holder1.pageLeft.setVisibility(View.VISIBLE);
			} else {
				holder1.pageLeft.setVisibility(View.GONE);
			}
			if (null != list.get(position).getPageright()) {
				holder1.pageRight.setImageDrawable(list.get(position).getPageright());
				holder1.pageRight.setVisibility(View.VISIBLE);
			} else {
				holder1.pageRight.setVisibility(View.GONE);
			}

			break;
		}

		}

		return convertView;
	}

}
