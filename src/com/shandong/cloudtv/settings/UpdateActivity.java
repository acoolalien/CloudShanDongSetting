package com.shandong.cloudtv.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.shandong.cloudtv.common.MyPagerAdapter;
import com.shandong.cloudtv.common.MyViewPager;
import com.shandong.cloudtv.settings.R;

public class UpdateActivity extends Activity {
	private MyViewPager mPager;
	private List<View> listViews;
	private int currIndex = 0;
	public Context mContext = this;
	public ImageView mLeft;
	public ImageView mRight;
	public static int mPosition;
	public static int mListSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		InitViewPager();
	}

	private void InitViewPager() {
		mPager = (MyViewPager) findViewById(R.id.vPager);
		mLeft = (ImageView) findViewById(R.id.update_left);
		mRight = (ImageView) findViewById(R.id.update_right);
		mPager.setmRight(mRight);
		mPager.setmLeft(mLeft);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		listViews.add(mInflater.inflate(R.layout.view_pager_1, null));
		listViews.add(mInflater.inflate(R.layout.view_pager_2, null));
		listViews.add(mInflater.inflate(R.layout.view_pager_2, null));
		listViews.add(mInflater.inflate(R.layout.view_pager_2, null));
		mListSize = listViews.size();
		mPager.setAdapter(new MyPagerAdapter(listViews, mLeft, mRight, mContext));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

				mPosition = arg0;
			}

		});
	}
}
