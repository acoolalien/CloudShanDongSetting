package com.shandong.cloudtv.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.CommonListView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

@SuppressLint("ResourceAsColor")
public class DeviceinfoActivity extends Activity {
	/**
	 * 设置项
	 */
	private String[] mArrays;
	/**
	 * 设置项内容
	 */
	private String[] mInfoArrays;
	private View mItemListCurView = null;
	private CommonListView mListView;
	private String mYunSn = null;
	private boolean mIsFirstIn = true;
	private LauncherFocusView mLauncherFocusView = null;
	private int mCurKeycode = -1;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private TextView mItemName = null;
	private TextView mItemSetting = null;
	private Context mContext = this;
	private int mItemListCurPosition = -1;
	private long mKeyDownTime = 0l;
	private String mVersion = null;
	SimpleAdapter listItemAdapt;
	ArrayList<HashMap<String, Object>> listItem;
	private int clickCount = 0;
	// 分体电视
	private final int SEPARATE = 0;
	// 一体电视
	private final int INTEGRATED = 1;
	// 电视类型
	private int mTvType;

	private String mTotalRom = null;

	private String mCurrentModel = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				mInfoArrays[7] = mVersion;
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("infoitem", mArrays[7]);
				map.put("deviceinfo", mVersion);
				listItem.set(7, map);

				mInfoArrays[6] = mYunSn;
				HashMap<String, Object> map1 = new HashMap<String, Object>();
				map1.put("infoitem", mArrays[6]);
				map1.put("deviceinfo", mYunSn);
				listItem.set(6, map1);

				if (listItemAdapt != null) {
					listItemAdapt.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		setContentView(R.layout.activity_deviceinfo);
		mListView = (CommonListView) findViewById(R.id.device_info_list);
		mLauncherFocusView = (LauncherFocusView) findViewById(R.id.deviceinfo_focus_view);
		initListener();
		mLauncherFocusView
				.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

					@Override
					public void OnAnimateEnd(View currentFocusView) {
						mFocusAnimationEndFlag = true;
						listTextColorSet();
					}

					@Override
					public void OnAnimateStart(View currentFocusView) {
						// TODO Auto-generated method stub
						mFocusAnimationEndFlag = false;
					}

				});

		// 获取电视类型
		// if(mTvType==SEPARATE){
		// 初始化item信息
		mArrays = new String[5];
		mInfoArrays = new String[5];
		getDeviceInfo(this);
		mArrays = getResources().getStringArray(R.array.deviceinfo);
		// }
		// else{
		// mArrays = new String[8];
		// mInfoArrays = new String[8];
		// mArrays =
		// getResources().getStringArray(R.array.device_info_for_separete);
		// getDeviceInfo_INTEGRADE(this);
		// }

		// 获取设备信息,写入mInfoArrays中

		// 绑定Layout里面的Listview
		ListView list = (ListView) findViewById(R.id.device_info_list);
		listItem = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < mArrays.length; i++) { // 设置listitem的资源和内容的映射
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("infoitem", mArrays[i]);
			// TODO
			map.put("deviceinfo", mInfoArrays[i]);
			listItem.add(map);

		}
		// TODO
		listItemAdapt = new SimpleAdapter(this, listItem,
				R.layout.activity_deviceinfo_detail, new String[] { "infoitem",
						"deviceinfo" }, new int[] { R.id.infoitem,
						R.id.detailitem });
		list.setAdapter(listItemAdapt);

		Utils.formatSize(this, getRomTotalSize());
		Utils.formatSize(this, getRomAvailableSize());
		if (mTvType == SEPARATE) {
		}

	}

	private void initListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub

				Utils.startListFocusAnimation(DeviceinfoActivity.this,
						mLauncherFocusView, R.anim.list_focus_anim);

				if (paramInt == (mListView.getCount() - 2)) {
					clickCount++;
				} else if (paramInt == (mListView.getCount() - 1)) {
					if (clickCount == 3) {
						startFactoryTest();
					}
					clickCount = 0;
				}
			}

		});
		mListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View paramView, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				mCurKeycode = keyCode;
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
							|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						mCurKeycode = keyCode;
						if (event.getRepeatCount() == 0) {
							mTextColorChangeFlag = true;
							mKeyDownTime = event.getDownTime();
						} else {
							mTextColorChangeFlag = false;
							if (event.getDownTime() - mKeyDownTime >= Utils.KEYDOWN_DELAY_TIME) {
								Log.e("KeyEvent",
										"time="
												+ (event.getDownTime() - mKeyDownTime)
												+ " count"
												+ event.getRepeatCount());
								mKeyDownTime = event.getDownTime();
							} else {
								return true;
							}
						}

						if (!mFocusAnimationEndFlag) {
							mTextColorChangeFlag = false;
						}
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					if (!mTextColorChangeFlag) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}

				return false;
			}

		});
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@SuppressLint("ResourceAsColor")
			@Override
			public void onItemSelected(AdapterView<?> paramAdapterView,
					View paramView, int paramInt, long paramLong) {
				// TODO Auto-generated method stub
				mItemListCurPosition = paramInt;
				mItemListCurView = paramView;
				if (mIsFirstIn) {
					mLauncherFocusView.initFocusView(paramView, false, 0f);

				}
				if (mItemName != null) {
					mItemName.setTextColor(mContext.getResources().getColor(
							R.color.grey5_color));
				}
				if (mItemSetting != null) {
					mItemSetting.setTextColor(mContext.getResources().getColor(
							R.color.grey5_color));
				}
				mItemName = (TextView) paramView.findViewById(R.id.infoitem);
				mItemSetting = (TextView) paramView
						.findViewById(R.id.detailitem);

				if (mIsFirstIn) {
					Log.i("BANGBANG", "----At the onitemselect---"
							+ mTextColorChangeFlag);
					mIsFirstIn = false;
					mTextColorChangeFlag = true;
					listTextColorSet();
				}

				if (mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN) {

					if (paramInt < 5
							|| paramInt > mListView.getCount() - 2
							|| (mListView.getFirstVisiblePosition() == 0 && paramView
									.getTop() < (paramView.getHeight() * 4))
							|| (mListView.getFirstVisiblePosition() != 0 && paramView
									.getTop() < paramView.getHeight() * 5)) {
						mLauncherFocusView.moveTo(paramView);
					} else {
						listTextColorSet();
						mListView.setSelectionFromTop(paramInt,
								paramView.getTop() - paramView.getHeight());
					}
				} else if (mCurKeycode == KeyEvent.KEYCODE_DPAD_UP) {
					if ((mItemListCurPosition == 0 || mListView
							.getFirstVisiblePosition() == 0
							&& paramView.getTop() > (paramView.getHeight()))
							|| (mListView.getFirstVisiblePosition() != 0 && paramView
									.getTop() >= paramView.getHeight())) {
						mLauncherFocusView.moveTo(paramView);
					} else {
						listTextColorSet();
						mListView.setSelectionFromTop(mItemListCurPosition,
								paramView.getHeight());
					}
				} else if (mCurKeycode == KeyEvent.KEYCODE_PAGE_UP
						|| mCurKeycode == KeyEvent.KEYCODE_PAGE_DOWN) {
					mLauncherFocusView.moveTo(paramView);
				}
				// fixed the keyboard repeat mode
				if (!mTextColorChangeFlag && mFocusAnimationEndFlag) {
					if ((mItemListCurPosition == 0 || mItemListCurPosition == mListView
							.getCount() - 1)) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {
				// TODO Auto-generated method stub

			}

		});

	}

	// TODO
	private void getDeviceInfo(Context context) {

		try {
			getTheRomSize();
			String macAddress = null, ip = null;
			WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr
					.getConnectionInfo());
			if (null != info) {
				macAddress = info.getMacAddress();
			}
			TelephonyManager tm = (TelephonyManager) getBaseContext()
					.getSystemService(Context.TELEPHONY_SERVICE);

			mInfoArrays[0] = Utils.getDefaultIpAddresses(context);
			mInfoArrays[1] = macAddress;
			mInfoArrays[2] = Utils
					.getAndroidOsSystemProperties("ro.serialno");
			mInfoArrays[3] = getString(R.string.availablesize)
					+ Utils.formatSize(this, getRomAvailableSize())
					+ getString(R.string.totalsize) + mTotalRom;
			if (tm != null)
				mInfoArrays[4] = tm.getDeviceId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getTheRomSize() {
		mTotalRom = Utils.getStorageInfo(mContext);
	}

	private void getDeviceInfo_INTEGRADE(Context context) {
		try {
			getTheRomSize();
			mInfoArrays[3] = Utils.getDefaultIpAddresses(context);
			mInfoArrays[7] = getString(R.string.availablesize)
					+ Utils.formatSize(this, getRomAvailableSize())
					+ getString(R.string.totalsize) + mTotalRom;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获得机身内存大小
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	private long getRomTotalSize() {
		File path = Environment.getDataDirectory();
		StatFs statFs = new StatFs(path.getPath());
		long blockSize = statFs.getBlockSizeLong();
		long tatalBlocks = statFs.getBlockCountLong();
		return blockSize * tatalBlocks;
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	private long getRomAvailableSize() {
		File path = Environment.getDataDirectory();
		StatFs statFs = new StatFs(path.getPath());
		long blockSize = statFs.getBlockSizeLong();
		long availableBlocks = statFs.getAvailableBlocksLong();
		return blockSize * availableBlocks;
	}

	private void listTextColorSet() {
		if (mItemName != null && mTextColorChangeFlag && mFocusAnimationEndFlag) {
			mItemName.setTextColor(this.getResources().getColor(R.color.white));
		}
		if (mItemSetting != null && mTextColorChangeFlag
				&& mFocusAnimationEndFlag) {
			mItemSetting.setTextColor(this.getResources().getColor(
					R.color.white));
		}
		if (mTextColorChangeFlag && mFocusAnimationEndFlag) {
			mTextColorChangeFlag = false;
		}

	}

	private void startFactoryTest() {
		try {
			final String MUSIC_PKG_NAME = "com.hiveview.factorytest";
			final String MUSIC_ACT_NAME = "com.hiveview.factorytest.MenuMain";
			ComponentName componentName = new ComponentName(MUSIC_PKG_NAME,
					MUSIC_ACT_NAME);
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setComponent(componentName);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
