package com.shandong.cloudtv.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class ThreeDimenActivity extends Activity {

	final String TAG = "CommonActivity";
	List<CommonItemList> mCommonItemList = new ArrayList<CommonItemList>();
	private String[] mArrays = new String[4];
	private Drawable[] pageLefts = new Drawable[4];
	private Drawable[] pageRights = new Drawable[4];
	private String[] mItemSettings = new String[4];
	private GeneralList threeDimenSettingOnListAdapter = null;
	private ListView commonSettingListView = null;
	private Context mContext = this;
	public int threeDimenId = 0;
	public int polarizeId = 0;
	private int mLeftRight = 0;
	private int mTopBottom = 0;
	private LauncherFocusView focusView = null;
	private boolean isFirstInit = true;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private TextView mTextView = null;
	private TextView mTextViewSetting = null;
	private int mItemListCurPosition = -1;
	private int m3DModel = -1;
	private int m3DTo2D = -1;
	private int mSwitch = -1;
	private CommonItemList mLeftRightList = null;
	private CommonItemList mTopBottomList = null;

	private void listTextColorSet() {
		if (mTextColorChangeFlag && mFocusAnimationEndFlag) {
			if (mTextView != null) {
				mTextView.setTextColor(this.getResources().getColor(R.color.white));
			}
			if (mTextViewSetting != null) {
				mTextViewSetting.setTextColor(this.getResources().getColor(R.color.white));
			}
			mTextColorChangeFlag = false;

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_three_dimen);
		commonSettingListView = (ListView) findViewById(R.id.three_dimen_list);
		this.threeDimenItemDataInit();
		this.threeDimenItemListInit(mCommonItemList, mArrays);
		this.onBlindListener();
	}

	/**
	 * 
	 */
	private void onBlindListener() {
		// TODO Auto-generated method stub
		commonSettingListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				View x = commonSettingListView.getSelectedView();
				TextView xx = (TextView) x.findViewById(R.id.item_setting);
				TextView mTextViewName = (TextView) x.findViewById(R.id.item_name);
				ImageView imageRightTemp = (ImageView) x.findViewById(R.id.page_right);
				ImageView imageLeftTemp = (ImageView) x.findViewById(R.id.page_left);
				int selectItems = commonSettingListView.getSelectedItemPosition();
				if (KeyEvent.ACTION_DOWN == event.getAction()) {
					if (event.getRepeatCount() == 0) {
						mTextColorChangeFlag = true;
					} else {
						mTextColorChangeFlag = false;
					}
					if (!mFocusAnimationEndFlag) {
						mTextColorChangeFlag = false;
					}

					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT: {

						if (selectItems == 0) {
							threeDimenId = (threeDimenId + 1) % 4;
							mCommonItemList.get(0).setItemSetting(
									CommonActivity.getStringArrays(mContext,
											R.array.dimen_type_selection, threeDimenId));
							mCommonItemList.get(0).setPageRight(
									mContext.getResources().getDrawable(
											R.drawable.page_right_selected));
							mCommonItemList.get(0).setPageLeft(
									mContext.getResources().getDrawable(R.drawable.page_left));
							if (threeDimenId == 2) {
								if (mCommonItemList.size() > 2)
									mCommonItemList.remove(2);
								if (mSwitch == 0) {
									mLeftRightList.setItemSetting(mContext.getResources()
											.getString(R.string.right));
									mLeftRight = 1;
								} else if (mSwitch == 1) {
									mLeftRightList.setItemSetting(mContext.getResources()
											.getString(R.string.left));
									mLeftRight = 0;
								}
								mCommonItemList.add(mLeftRightList);

							} else if (threeDimenId == 3) {
								if (mCommonItemList.size() > 2)
									mCommonItemList.remove(2);
								mCommonItemList.add(mTopBottomList);

							} else if ((threeDimenId == 0 || threeDimenId == 1)
									&& mCommonItemList.size() > 2) {
								while (mCommonItemList.size() > 2) {
									mCommonItemList.remove(2);
								}

							}
							threeDimenItemListInit(mCommonItemList, mArrays);
						}
						if (selectItems == 1) {
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right_selected));
							polarizeId = (polarizeId + 1) % 2;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.open_close,
									polarizeId));
						}
						if (mTextViewName.getText().equals(
								mContext.getResources().getString(R.string.left_right_mode))) {
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right_selected));
							mLeftRight = (mLeftRight + 1) % 2;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.left_right,
									mLeftRight));
						}
						if (mTextViewName.getText().equals(
								mContext.getResources().getString(R.string.top_bottom_mode))) {
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right_selected));
							mTopBottom = (mTopBottom + 1) % 2;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.top_bottom,
									mTopBottom));
						}
						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {

						if (selectItems == 0) {
							threeDimenId = (threeDimenId + 3) % 4;
							mCommonItemList.get(0).setItemSetting(
									CommonActivity.getStringArrays(mContext,
											R.array.dimen_type_selection, threeDimenId));
							mCommonItemList.get(0).setPageLeft(
									mContext.getResources().getDrawable(
											R.drawable.page_left_selected));
							mCommonItemList.get(0).setPageRight(
									mContext.getResources().getDrawable(R.drawable.page_right));
							if (threeDimenId == 2) {
								if (mCommonItemList.size() > 2)
									mCommonItemList.remove(2);
								if (mSwitch == 0) {
									mLeftRightList.setItemSetting(mContext.getResources()
											.getString(R.string.right));
									mLeftRight = 1;
								} else if (mSwitch == 1) {
									mLeftRightList.setItemSetting(mContext.getResources()
											.getString(R.string.left));
									mLeftRight = 0;
								}
								mCommonItemList.add(mLeftRightList);

							} else if (threeDimenId == 3) {
								if (mCommonItemList.size() > 2)
									mCommonItemList.remove(2);
								if (mSwitch == 0) {
									mTopBottomList.setItemSetting(mContext.getResources()
											.getString(R.string.bottom));
									mTopBottom = 1;
								} else if (mSwitch == 1) {
									mTopBottomList.setItemSetting(mContext.getResources()
											.getString(R.string.top));
									mTopBottom = 0;
								}
								mCommonItemList.add(mTopBottomList);

							} else if ((threeDimenId == 0 || threeDimenId == 1)
									&& mCommonItemList.size() > 2) {
								while (mCommonItemList.size() > 2) {
									mCommonItemList.remove(2);
								}

							}
							threeDimenItemListInit(mCommonItemList, mArrays);
						}
						if (selectItems == 1) {
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left_selected));
							polarizeId = (polarizeId + 1) % 2;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.open_close,
									polarizeId));
						}
						if (mTextViewName.getText().equals(
								mContext.getResources().getString(R.string.left_right_mode))) {
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left_selected));
							mLeftRight = (mLeftRight + 1) % 2;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.left_right,
									mLeftRight));
						}
						if (mTextViewName.getText().equals(
								mContext.getResources().getString(R.string.top_bottom_mode))) {
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left_selected));
							mTopBottom = (mTopBottom + 1) % 2;
							xx.setText(CommonActivity.getStringArrays(mContext, R.array.top_bottom,
									mTopBottom));
						}
						break;
					}
					}
				} else if (KeyEvent.ACTION_UP == event.getAction()) {
					if (!mTextColorChangeFlag) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT: {
						if (selectItems == 0) {
							mCommonItemList.get(0).setPageLeft(
									mContext.getResources().getDrawable(R.drawable.page_left));
							mCommonItemList.get(0).setPageRight(
									mContext.getResources().getDrawable(R.drawable.page_right));
							threeDimenItemListInit(mCommonItemList, mArrays);
						} else {
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right));
						}
						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						if (selectItems == 0) {
							mCommonItemList.get(0).setPageLeft(
									mContext.getResources().getDrawable(R.drawable.page_left));
							mCommonItemList.get(0).setPageRight(
									mContext.getResources().getDrawable(R.drawable.page_right));
							threeDimenItemListInit(mCommonItemList, mArrays);
						} else {
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left));
						}
						break;
					}
					}
				}
				return false;
			}

		});

		commonSettingListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View paramView, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mItemListCurPosition = arg2;
				if (isFirstInit) {
					focusView.initFocusView(paramView, false, 0);
				}
				focusView.moveTo(paramView);
				if (mTextView != null) {
					mTextView.setTextColor(mContext.getResources().getColor(R.color.grey5_color));
				}
				if (mTextViewSetting != null) {
					mTextViewSetting.setTextColor(mContext.getResources().getColor(
							R.color.grey5_color));
				}
				mTextView = (TextView) paramView.findViewById(R.id.item_name);
				mTextViewSetting = (TextView) paramView.findViewById(R.id.item_setting);
				if (isFirstInit) {
					isFirstInit = false;
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void threeDimenItemListInit(List<CommonItemList> list, String[] array) {
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
		focusView = (LauncherFocusView) findViewById(R.id.activity_three_dimen_focusview);
		focusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

			@Override
			public void OnAnimateStart(View currentFocusView) {
				// TODO Auto-generated method stub
				mFocusAnimationEndFlag = false;
			}

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				mFocusAnimationEndFlag = true;
				listTextColorSet();
				// fixed the keyboard repeat mode
				if (!mTextColorChangeFlag) {
					if ((mItemListCurPosition == 0 || mItemListCurPosition == commonSettingListView
							.getCount() - 1)) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}
			}

		});
		mArrays = this.getResources().getStringArray(R.array.three_dimen);

		pageLefts[0] = pageLefts[1] = pageLefts[2] = pageLefts[3] = this.getResources()
				.getDrawable(R.drawable.page_left);
		pageRights[0] = pageRights[1] = pageRights[2] = pageRights[3] = this.getResources()
				.getDrawable(R.drawable.page_right);
		// Log.i("YQBBANG", "----3DModel=" + tv.Get3DMode());
		mItemSettings[0] = this.getResources().getString(R.string.close);
		mItemSettings[1] = this.getResources().getString(R.string.open);
		mItemSettings[2] = this.getResources().getString(R.string.left);
		mItemSettings[3] = this.getResources().getString(R.string.top);
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
		mLeftRightList = mCommonItemList.get(2);
		mTopBottomList = mCommonItemList.get(3);

	}

}
