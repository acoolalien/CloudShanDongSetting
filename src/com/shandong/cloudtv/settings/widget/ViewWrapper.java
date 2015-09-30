package com.shandong.cloudtv.settings.widget;

import android.util.Log;
import android.view.View;

public class ViewWrapper {

	private View mTargetView;

	public ViewWrapper(View target) {
		mTargetView = target;
	}

	public int getWidth() {
		return mTargetView.getLayoutParams().width;
	}

	public void setWidth(int width) {
		if(mTargetView == null){
			Log.e("TEST", "target view null");
		}
		if(mTargetView.getLayoutParams() == null){
			Log.e("TEST", "target view params null");
		}
		mTargetView.getLayoutParams().width = width;
		mTargetView.requestLayout();
	}

	public int getHeight() {
		return mTargetView.getLayoutParams().height;
	}

	public void setHeight(int height) {
		mTargetView.getLayoutParams().height = height;
		mTargetView.requestLayout();
	}
}
