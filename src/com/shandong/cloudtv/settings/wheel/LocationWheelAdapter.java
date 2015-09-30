package com.shandong.cloudtv.settings.wheel;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class LocationWheelAdapter implements WheelViewAdapter {
	
	private String[] array;
	private Context context;
	private DisplayMetrics dmc;
	
	public LocationWheelAdapter(Context context,String[] array, DisplayMetrics dm) {
		
		this.array = array;
		this.context = context;
		dmc = dm;
	}
	
	public String getCurrentContent(int index){
		index = index%array.length;
		return array[index];
	}

	@Override
	public int getItemsCount() {
		return array.length;
	}

	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		TextView tv = new TextView(context);
		LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,(int) (80*dmc.density));
		tv.setLayoutParams(params);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(0xFF9A9A9A);
		tv.setTextSize(25);
		tv.setText(array[index]);
		return tv;
	}

	@Override
	public View getEmptyItem(View convertView, ViewGroup parent) {
		return null;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		
	}
	

}
