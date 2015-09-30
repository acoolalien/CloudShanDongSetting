package com.shandong.cloudtv.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shandong.cloudtv.common.CommonItemList;
import com.shandong.cloudtv.common.GeneraAdapter;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.ResetDialog;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

public class OtherSettingActivity extends Activity {

	private ListView mOtherSettingListView = null;
	List<CommonItemList> mCommonItemList = new ArrayList<CommonItemList>();
	private String[] mArrays = new String[6];
	private Drawable[] pageLefts = new Drawable[6];
	private Drawable[] pageRights = new Drawable[6];
	private String[] mItemSettings = new String[6];
	private Context mContext = this;
	private GeneraAdapter mOtherSettingItemListAdapter = null;
	private View mItemListCurView = null;
	private String[] mTemp = new String[4];
	private int mTvNameSelectId = 0;
	private LauncherFocusView focusView = null;
	private boolean isFirstInit = true;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	private GeneraAdapter.ViewHolder viewHolder = null;
	private int mItemListCurPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_setting);
		mOtherSettingListView = (ListView) findViewById(R.id.other_setting_list);
		this.CommonItemDataInit();
		this.CommonItemListInit();
		this.onBlindListener();
	}

	private void CommonItemDataInit() {
		// 初始化蓝牙名字
		if (null != mAdapter) {
			String name = mAdapter.getName();
			if (null != name) {
				if (name.equals(CommonActivity.getStringArrays(mContext, R.array.tv_name, 0))) {
					mTvNameSelectId = 0;
				} else if (name
						.equals(CommonActivity.getStringArrays(mContext, R.array.tv_name, 1))) {
					mTvNameSelectId = 1;
				} else if (name
						.equals(CommonActivity.getStringArrays(mContext, R.array.tv_name, 2))) {
					mTvNameSelectId = 2;
				} else {
					mTvNameSelectId = 0;
					mAdapter.setName(CommonActivity.getStringArrays(mContext, R.array.tv_name,
							mTvNameSelectId));

				}
			}
		}
		focusView = (LauncherFocusView) findViewById(R.id.activity_othersetting_focusview);

		mArrays = mContext.getResources().getStringArray(R.array.other_setting);
		mTemp = mContext.getResources().getStringArray(R.array.tv_name);
		pageLefts[0] = mContext.getResources().getDrawable(R.drawable.page_left);
		pageRights[0] = mContext.getResources().getDrawable(R.drawable.page_right);
		pageRights[1] = pageRights[2] = pageRights[3] = pageRights[4] = pageRights[5] = mContext
				.getResources().getDrawable(R.drawable.page_right_big_selected);
		mItemSettings[0] = CommonActivity.getStringArrays(mContext, R.array.tv_name,
				mTvNameSelectId);

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

	private void CommonItemListInit() {
		if (null == mOtherSettingItemListAdapter) {
			mOtherSettingItemListAdapter = new GeneraAdapter(mContext, mCommonItemList, mArrays);
			mOtherSettingListView.setAdapter(mOtherSettingItemListAdapter);
		} else {
			mOtherSettingItemListAdapter.notifyDataSetChanged();
		}
	}

	private void listTextColorSet() {
		if (viewHolder != null && mTextColorChangeFlag && mFocusAnimationEndFlag) {
			mTextColorChangeFlag = false;
			if (viewHolder.itemSetting != null) {

				viewHolder.itemName.setTextColor(this.getResources().getColor(R.color.white));

				viewHolder.itemSetting.setTextColor(this.getResources().getColor(R.color.white));
			} else {

				viewHolder.itemName.setTextColor(this.getResources().getColor(R.color.white));

				viewHolder.pageRight.setImageResource(R.drawable.page_right_big_selected);
			}
		}
	}

	private void onBlindListener() {
		mOtherSettingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {

				}
				// 图像
				if (position == 1) {
				}
				if (position == 2) {

				}
				if (position == 3) {
					startActivity(new Intent().setClass(mContext, DevloperModelActivity.class));
				}

				if (position == 4) {
					startActivity(new Intent().setClass(mContext, LocationSettingsActivity.class));
				}
				if (position == 5) {
					final ResetDialog dialog = new ResetDialog(mContext);
					Window dialogWindow = dialog.getWindow();
					WindowManager.LayoutParams lp = dialogWindow.getAttributes();
					lp.width = 946;
					lp.height = 754;
					dialogWindow.setAttributes(lp);
					dialog.show();
					TextView message = (TextView) dialog.findViewById(R.id.message1);
					message.setText(getString(R.string.clean_all_confirm));
					/*
					 * TextView titleTextView = (TextView)
					 * dialog.findViewById(R.id.textview1);
					 * titleTextView.setText(getString(R.string.reset_warning));
					 */
					Button button1 = (Button) dialog.findViewById(R.id.button1);
					Button button2 = (Button) dialog.findViewById(R.id.button2);
					button1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View paramView) {
							// TODO Auto-generated method stub
							mContext.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
							dialog.dismiss();
						}
					});
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View paramView) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					});

				}

			}
		});

		mOtherSettingListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				View x = mOtherSettingListView.getSelectedView();
				EditText xx = (EditText) x.findViewById(R.id.item_setting);
				ImageView imageRightTemp = (ImageView) x.findViewById(R.id.page_right);
				ImageView imageLeftTemp = (ImageView) x.findViewById(R.id.page_left);
				int selectItemId = mOtherSettingListView.getSelectedItemPosition();

				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (event.getRepeatCount() == 0) {
						mTextColorChangeFlag = true;
					} else {
						mTextColorChangeFlag = false;
					}

					if (!mFocusAnimationEndFlag) {
						mTextColorChangeFlag = false;
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {

						if (selectItemId == 0) {
							imageRightTemp.setImageDrawable(OtherSettingActivity.this
									.getResources().getDrawable(R.drawable.page_right_selected));
							mTvNameSelectId = (mTvNameSelectId + 1) % 3;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.tv_name,
									mTvNameSelectId));
							if (null != mAdapter)
								mAdapter.setName(CommonActivity.getStringArrays(mContext,
										R.array.tv_name, mTvNameSelectId));
						}

					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {

						if (selectItemId == 0) {
							imageLeftTemp.setImageDrawable(OtherSettingActivity.this.getResources()
									.getDrawable(R.drawable.page_left_selected));
							mTvNameSelectId = (mTvNameSelectId + 2) % 3;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.tv_name,
									mTvNameSelectId));
							if (null != mAdapter)
								mAdapter.setName(CommonActivity.getStringArrays(mContext,
										R.array.tv_name, mTvNameSelectId));
						}
					}
				} else if (KeyEvent.ACTION_UP == event.getAction()) {
					Log.i("BANGBANG", "----At the action up---" + mTextColorChangeFlag);
					if (!mTextColorChangeFlag) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}

					if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
						if (selectItemId == 0) {
							imageRightTemp.setImageDrawable(OtherSettingActivity.this
									.getResources().getDrawable(R.drawable.page_right));
						}
					}
					if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
						if (selectItemId == 0) {
							imageLeftTemp.setImageDrawable(OtherSettingActivity.this.getResources()
									.getDrawable(R.drawable.page_left));
						}
					}
				}

				return false;
			}

		});
		mOtherSettingListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> paramAdapterView, View paramView,
					int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				mItemListCurPosition = paramInt;
				mItemListCurView = paramView;
				if (isFirstInit) {
					focusView.initFocusView(paramView, false, 0);
				}

				focusView.moveTo(paramView);

				if (viewHolder != null) {
					Log.i("BANGBANG", "-----on SELECT------" + viewHolder);
					if (viewHolder.itemSetting != null) {
						viewHolder.itemName.setTextColor(mContext.getResources().getColor(
								R.color.grey5_color));
						viewHolder.itemSetting.setTextColor(mContext.getResources().getColor(
								R.color.grey5_color));
					} else {
						viewHolder.itemName.setTextColor(mContext.getResources().getColor(
								R.color.grey5_color));
						viewHolder.pageRight.setImageResource(R.drawable.page_right_big);
					}
				}

				viewHolder = (GeneraAdapter.ViewHolder) paramView.getTag();

				if (isFirstInit) {
					isFirstInit = false;
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {
				// TODO Auto-generated method stub

			}

		});
		focusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				// focusView.initFocusView(mItemListCurView, false, 0);
				mFocusAnimationEndFlag = true;
				listTextColorSet();
				// fixed the keyboard repeat mode
				if (!mTextColorChangeFlag) {
					if ((mItemListCurPosition == 0 || mItemListCurPosition == mOtherSettingListView
							.getCount() - 1)) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}
			}

			@Override
			public void OnAnimateStart(View currentFocusView) {
				// TODO Auto-generated method stub
				mFocusAnimationEndFlag = false;
			}

		});
	}
}
