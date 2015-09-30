package com.shandong.cloudtv.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shandong.cloudtv.common.CommonItemList;
import com.shandong.cloudtv.common.GeneralList;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

public class DevloperModelActivity extends Activity {

	final String TAG = "DevloperModelActivity";
	List<CommonItemList> mCommonItemList = new ArrayList<CommonItemList>();
	private String[] mArrays = new String[2];
	private Drawable[] pageLefts = new Drawable[2];
	private Drawable[] pageRights = new Drawable[2];
	private String[] mItemSettings = new String[2];
	private GeneralList threeDimenSettingOnListAdapter = null;
	private ListView commonSettingListView = null;
	public int adbId = 0;
	public int uTestId = 0;
	public int allowInstallId = 1;
	private Context mContext = this;
	private Activity mActivity = this;
	private LauncherFocusView focusView = null;
	private boolean isFirstInit = true;
	private TextView mTextViewName = null;
	private TextView mTextViewSetting = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devloper_model);
		commonSettingListView = (ListView) findViewById(R.id.devloper_model_list);
		this.threeDimenItemDataInit();
		this.threeDimenItemListInit(mCommonItemList, mArrays);
		this.onBlindListener();
	}

	private void setListColor() {
		if (mTextViewName != null) {
			mTextViewName.setTextColor(mContext.getResources().getColor(
					R.color.white));

		}
		if (mTextViewSetting != null) {
			mTextViewSetting.setTextColor(mContext.getResources().getColor(
					R.color.white));
		}
	}

	private void onBlindListener() {
		// TODO Auto-generated method stub

		focusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

			@Override
			public void OnAnimateStart(View currentFocusView) {
				// TODO Auto-generated method stub

			}

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				setListColor();
			}

		});
		commonSettingListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				View temp = (View) commonSettingListView.getSelectedView();
				if(temp==null)
					return true;
				TextView textTemp = (TextView) temp
						.findViewById(R.id.item_setting);
				ImageView imageRightTemp = (ImageView) temp
						.findViewById(R.id.page_right);
				ImageView imageLeftTemp = (ImageView) temp
						.findViewById(R.id.page_left);
				if (KeyEvent.ACTION_DOWN == event.getAction()) {
					int selectItems = commonSettingListView
							.getSelectedItemPosition();

					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT: {
						imageRightTemp
								.setImageDrawable(DevloperModelActivity.this
										.getResources().getDrawable(
												R.drawable.page_right_selected));
						if (selectItems == 0) {
							adbId = (adbId + 1) % 2;
							textTemp.setText(CommonActivity.getStringArrays(
									DevloperModelActivity.this,
									R.array.open_close, adbId));
							if (adbId == 0) {
								Utils.setAdbDebug(mContext, 1);
							} else if (adbId == 1) {
								Utils.setAdbDebug(mContext, 0);
							}
						}

						if (selectItems == 1) {
							allowInstallId = (allowInstallId + 1) % 2;
							textTemp.setText(CommonActivity.getStringArrays(
									DevloperModelActivity.this,
									R.array.open_close, allowInstallId));
							if (allowInstallId == 0) {
								Utils.setNonMarketAppsAllowed(mActivity,
										mContext, true);
							} else if (allowInstallId == 1) {
								Utils.setNonMarketAppsAllowed(mActivity,
										mContext, false);
							}
						}
						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						imageLeftTemp
								.setImageDrawable(DevloperModelActivity.this
										.getResources().getDrawable(
												R.drawable.page_left_selected));
						if (selectItems == 0) {
							adbId = (adbId + 1) % 2;
							textTemp.setText(CommonActivity.getStringArrays(
									DevloperModelActivity.this,
									R.array.open_close, adbId));
							if (adbId == 0) {
								Utils.setAdbDebug(mContext, 1);
							} else if (adbId == 1) {
								Utils.setAdbDebug(mContext, 0);
							}
						}

						if (selectItems == 1) {
							allowInstallId = (allowInstallId + 1) % 2;
							textTemp.setText(CommonActivity.getStringArrays(
									DevloperModelActivity.this,
									R.array.open_close, allowInstallId));
							if (allowInstallId == 0) {
								Utils.setNonMarketAppsAllowed(mActivity,
										mContext, true);
							} else if (allowInstallId == 1) {
								Utils.setNonMarketAppsAllowed(mActivity,
										mContext, false);
							}
						}
						break;
					}
					}
				} else if (KeyEvent.ACTION_UP == event.getAction()) {

					switch (keyCode) {

					case KeyEvent.KEYCODE_DPAD_RIGHT: {
						imageRightTemp
								.setImageDrawable(DevloperModelActivity.this
										.getResources().getDrawable(
												R.drawable.page_right));
						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						imageLeftTemp
								.setImageDrawable(DevloperModelActivity.this
										.getResources().getDrawable(
												R.drawable.page_left));
						break;
					}
					}
				}
				return false;
			}

		});
		commonSettingListView
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> paramAdapterView,
							View paramView, int paramInt, long paramLong) {
						// TODO Auto-generated method stub
						if (isFirstInit) {
							Log.e("huxing", "huxing-------------foucus--1");
							focusView.initFocusView(paramView, false, 0);
							isFirstInit = false;
						} else {
							Log.e("huxing", "huxing-------------foucus--2");
							focusView.moveTo(paramView);
						}

						if (mTextViewName != null) {
							mTextViewName.setTextColor(mContext.getResources()
									.getColor(R.color.grey5_color));
						}
						if (mTextViewSetting != null) {
							mTextViewSetting.setTextColor(mContext
									.getResources().getColor(
											R.color.grey5_color));
						}

						mTextViewName = (TextView) paramView
								.findViewById(R.id.item_name);
						mTextViewSetting = (TextView) paramView
								.findViewById(R.id.item_setting);

					}

					@Override
					public void onNothingSelected(
							AdapterView<?> paramAdapterView) {
						// TODO Auto-generated method stub

					}

				});
	}

	private void threeDimenItemListInit(List<CommonItemList> list,
			String[] array) {
		// TODO Auto-generated method stub
		if (threeDimenSettingOnListAdapter == null) {
			threeDimenSettingOnListAdapter = new GeneralList(this, list, array);
			commonSettingListView.setAdapter(threeDimenSettingOnListAdapter);
		} else {
			threeDimenSettingOnListAdapter.notifyDataSetChanged();
		}
	}

	private void threeDimenItemDataInit() {
		// TODO Auto-generated method stub
		// 初始化是否允许未知来源的应用
		if (Utils.isNonMarketAppsAllowed(mContext)) {
			allowInstallId = 0;
		} else {
			allowInstallId = 1;
		}
		if (Utils.isAdbOrNot(mContext)) {
			adbId = 0;
		} else {
			adbId = 1;
		}
		Log.i("YQB", String.valueOf(allowInstallId));
		focusView = (LauncherFocusView) findViewById(R.id.activity_devloper_focusview);
		mArrays = this.getResources().getStringArray(R.array.devloper_model);

		pageLefts[0] = pageLefts[1] = this.getResources().getDrawable(
				R.drawable.page_left);
		pageRights[0] = pageRights[1] = this.getResources().getDrawable(
				R.drawable.page_right);
		// TODO

		mItemSettings[0] = CommonActivity.getStringArrays(
				DevloperModelActivity.this, R.array.open_close, adbId);
		mItemSettings[1] = CommonActivity.getStringArrays(
				DevloperModelActivity.this, R.array.open_close, allowInstallId);
		for (int i = 0; i < mArrays.length; i++) {
			CommonItemList mTemp = new CommonItemList();
			mTemp.setItemName(mArrays[i]);

			if (null != pageLefts[i]) {
				mTemp.setPageLeft(pageLefts[i]);
			}
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
