package com.shandong.cloudtv.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;

import com.shandong.cloudtv.common.CheckVersionAdapter;
import com.shandong.cloudtv.common.CommonItemList;
import com.shandong.cloudtv.settings.R;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;

public class VersionCheckActivity extends Activity {
	private ViewPager mPager;
	private List<View> listViews;
	private int currIndex = 0;
	public Context mContext = this;

	/*--------------------------------------------------*/
	final String TAG = "DevloperModelActivity";
	List<CommonItemList> mCommonItemList = new ArrayList<CommonItemList>();
	private String[] mArrays = new String[2];

	private Drawable[] pageRights = new Drawable[2];
	private String[] mItemSettings = new String[2];
	private CheckVersionAdapter threeDimenSettingOnListAdapter = null;
	private ListView commonSettingListView = null;
	public int threeDimenId = 0;
	public int polarizeId = 0;
	private LauncherFocusView focusView = null;
    private boolean isFirstInit = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version_check);
		commonSettingListView = (ListView) findViewById(R.id.version_check);
		TextView x = (TextView) findViewById(R.id.now_version_message_detail);
		x.setMovementMethod(ScrollingMovementMethod.getInstance());
		this.threeDimenItemDataInit();
		this.threeDimenItemListInit(mCommonItemList, mArrays);
		this.onBlindListener();
	}

	private void onBlindListener() {
		// TODO Auto-generated method stub
		commonSettingListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (KeyEvent.ACTION_DOWN == event.getAction()) {
					int selectItems = commonSettingListView
							.getSelectedItemPosition();
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT: {

						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						break;
					}
					}
				}
				return false;
			}

		});

		commonSettingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 1) {
					startActivity(new Intent().setClass(mContext,
							UpdateActivity.class));
				}
			}
		});
		commonSettingListView.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				if(isFirstInit) {
					Log.e("huxing","huxing-------------foucus--1");
					focusView.initFocusView(paramView, false, 0);
					isFirstInit = false;
				} else {
					Log.e("huxing","huxing-------------foucus--2");
					focusView.moveTo(paramView);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	private void threeDimenItemListInit(List<CommonItemList> list,
			String[] array) {
		// TODO Auto-generated method stub
		if (threeDimenSettingOnListAdapter == null) {
			threeDimenSettingOnListAdapter = new CheckVersionAdapter(this,
					list, array);
			commonSettingListView.setAdapter(threeDimenSettingOnListAdapter);
		} else {
			threeDimenSettingOnListAdapter.notifyDataSetChanged();
		}
	}

	private void threeDimenItemDataInit() {
		// TODO Auto-generated method stub
        focusView = (LauncherFocusView) findViewById(R.id.activity_version_check_focusview);
        
		mArrays = this.getResources().getStringArray(R.array.check_version);
		pageRights[0] = this.getResources().getDrawable(
				R.drawable.page_right_big_selected);
		mItemSettings[0] = "1.11.1";
		for (int i = 0; i < mArrays.length; i++) {
			CommonItemList mTemp = new CommonItemList();
			mTemp.setItemName(mArrays[i]);

			if (null != pageRights[i]) {
				mTemp.setPageRight(pageRights[i]);
			}
			if (null != mItemSettings[i]) {
				mTemp.setItemSetting(mItemSettings[i]);
			}

			mCommonItemList.add(mTemp);
		}
	}
}
