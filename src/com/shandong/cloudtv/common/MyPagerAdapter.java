package com.shandong.cloudtv.common;

import java.util.List;

import com.shandong.cloudtv.settings.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ImageView;

public class MyPagerAdapter extends PagerAdapter {
	public List<View> mListView;
	public ImageView mLeft;
	public ImageView mRight;
	public Context mContext;

	public MyPagerAdapter(List<View> mListView, ImageView a, ImageView b,
			Context c) {
		this.mListView = mListView;
		mLeft = a;
		mRight = b;
		mContext = c;
	}

	@Override
	public int getCount() {
		return mListView.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {

		return arg0 == (arg1);
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(mListView.get(arg1), 0);
		mListView.get(arg1).setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				Log.i("yiqibang", "yiqibang");
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT: {
						mRight.setImageDrawable(mContext.getResources()
								.getDrawable(R.drawable.update_right_selected));
						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						mLeft.setImageDrawable(mContext.getResources()
								.getDrawable(R.drawable.update_left_selected));
						break;
					}
					}

				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT: {

						mRight.setImageDrawable(mContext.getResources()
								.getDrawable(R.drawable.update_right));

						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						mLeft.setImageDrawable(mContext.getResources()
								.getDrawable(R.drawable.update_left));
						break;
					}
					}
				}
				return false;
			}

		});
		return mListView.get(arg1);

	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(mListView.get(arg1));
	}

}
