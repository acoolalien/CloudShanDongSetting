package com.shandong.cloudtv.settings.wheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

@SuppressLint("NewApi")
public class NewWheelAdapter extends BaseWheelAdapter {
	private String[] index;
	private Context mContext;

	public NewWheelAdapter(Context m, String[] a) {
		index = a;
		mContext = m;
	}

	public String getCurrentContent(int postion) {
		postion = postion % index.length;
		return index[postion];
	}

	public void setList(String[] a) {
		index = a;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return index.length;
	}

	@Override
	public Object getItem(int paramInt) {
		// TODO Auto-generated method stub
		return index[paramInt];
	}

	@Override
	public long getItemId(int paramInt) {
		// TODO Auto-generated method stub
		return paramInt;
	}

	@Override
	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		// TODO Auto-generated method stub
		LayoutParams layoutParams = new LayoutParams(370, 110);
		TextView view = new TextView(mContext);
		view.setText(index[paramInt]);
		view.setTextColor(Color.parseColor("#FFFFFF"));
		view.setTextSize(24);
		view.setGravity(Gravity.CENTER);
		view.setLayoutParams(layoutParams);
		return view;

	}

	@Override
	public void currentView(View currentView) {
		if (null != currentView) {
			TextView view = (TextView) currentView;
			view.animate().alpha(1f).scaleX(1.33f).scaleY(1.33f).start();
		}
	}

	@Override
	public void nextView(View nextView) {
		if (null != nextView) {
			TextView view = (TextView) nextView;
			view.animate().alpha(0.3f).scaleX(1f).scaleY(1f).start();
		}
	}

	@Override
	public void preView(View preView) {
		if (null != preView) {
			TextView view = (TextView) preView;
			view.animate().alpha(0.3f).scaleX(1f).scaleY(1f).start();
		}
	}

}
