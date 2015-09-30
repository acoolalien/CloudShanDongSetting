package com.shandong.cloudtv.settings;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shandong.cloudtv.common.CommonItemList;
import com.shandong.cloudtv.settings.util.ImageSharePreference;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.CommonListView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.ResetDialog;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

public class CommonActivity extends Activity {
	final String TAG = "CommonActivity";
	//int[] flag = new int[12];
	List<CommonItemList> mCommonItemList = new ArrayList<CommonItemList>();
	private String[] mArrays;
	private Drawable[] pageLefts;
	private Drawable[] pageRights;
	private String[] mItemSettings;
	private long mKeyDownTime = 0L;
	private RelativeLayout mRelative = null;
	private CommonSettingOnList commonSettingOnListAdapter = null;
	private CommonListView commonSettingListView = null;
	public int gameSettingId = 0;
	public int monitorProtectId = 0;
	public int sleepTimeId = 0;
	public int themeColor = 0;
	public int timeTypeId = 0;
	private int mTvNameSelectId = 0;
	private int mPcmRawId =0;
	private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
	private LauncherFocusView focusView;
	public Context mContext = this;
	public Activity activity = this;
	public static Bitmap originalBitmap = null;
	public static Bitmap blurBitmap = null;
	public static final int RIGHT_ARROW_SMALL = 0;
	public static final int RIGHT_ARROW_BIG = 1;
	private int TV_NAME = 0;
	private int PROTECT_MONITOR_TIME = 1;
	private int TIME_TYPE = 2;
	private int PCM_RAW = 3;
	private int VIDEO_VOLUME=4;
	private int YAOKONG = 5;
	private int BLUETOOTH_SETTING = 6;
	private int KEDA_SETTING = 7;
	private int LOCATION_SETTINGS = 8;
	private int DEVELOPER_SETTINGS = 9;
	private int VERSION_CHECK = 10;
	private int RESET = 11;
	private int IMAGE_SETTING=999;
	
	private int FALLBACK_SCREEN_TIMEOUT_VALUE = 300000;
	public boolean bfocusViewInitStatus = true;
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private int mItemListCurPosition = -1;
	private TextView mTextView = null;
	private TextView mTextViewSetting = null;
	private ImageView mImageView = null;
	public int volumeSelected = 0;
	private View mView;
	private String mKEDA = "com.iflytek.showcomesettings";
	private ImageSharePreference mPreference;
	final int BT_AUTOPAIR_FAILED = 0;
	final int BT_AUTOPAIR_NOTPAIR = 1;
	final int BT_AUTOPAIR_PAIRING = 2;
	final int BT_AUTOPAIR_PAIRED = 3;
	final int BT_AUTOPAIR_CONNECTING = 4;
	final int BT_AUTOPAIR_CONNECTED = 5;
	final int BT_AUTOPAIR_CONNECT_OK = 7;
	final int BT_AUTOPAIR_NEED_CONNECT = 8;
	private boolean bluetoothstate = false;
	private long time = 0L;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common);
		commonSettingListView = (CommonListView) findViewById(R.id.com_setting_list);
		this.CommonItemDataInit();
		this.CommonItemListInit();
		this.onBlindListener();
		registerReceiver();
	}
	
	@Override
	protected void onResume() {
		bluetoothstate = mAdapter.isEnabled();
		super.onResume();
	};

	@Override
	protected void onDestroy() {
		this.unregisterReceiver(bluetoothReceiver);
		super.onDestroy();
	}

	private void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.hiveview.bluetooth.le.btautopair.state");
		registerReceiver(bluetoothReceiver, intentFilter);
	}

	private void startBLService() {
		try {
			Intent intent = new Intent();
			intent.setAction("com.hiveview.bluetooth.le.BtAutoPair");
			startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void broadTime(){
		Intent timeIntent = new Intent(Intent.ACTION_TIME_CHANGED);
		mContext.sendBroadcast(timeIntent);
	}

	private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {

		@SuppressLint("ShowToast")
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			int state = intent.getIntExtra("state", 0);
			switch (state) {
			case BT_AUTOPAIR_FAILED: {
				// mStatus.setText(mContext.getResources().getString(R.string.bluetooth_error));
				break;
			}
			case BT_AUTOPAIR_NOTPAIR: {
				// mStatus.setText(mContext.getResources().getString(R.string.find_yaokongqi));
				break;
			}
			case BT_AUTOPAIR_PAIRING: {
				// mStatus.setText(mContext.getResources().getString(R.string.bluetooth_pairing));
				break;
			}
			case BT_AUTOPAIR_PAIRED: {
				// mStatus.setText(mContext.getResources().getString(R.string.bluetooth_pair_success));
				break;
			}
			case BT_AUTOPAIR_CONNECTING: {
				// mStatus.setText(mContext.getResources().getString(R.string.bluetooth_connecting));
				break;
			}
			case BT_AUTOPAIR_CONNECTED: {
				// mStatus.setText(mContext.getResources().getString(
				// R.string.bluetooth_connect_success));
				break;
			}
			case BT_AUTOPAIR_NEED_CONNECT: {

				startActivity(new Intent().setClass(mContext, ControlActivity.class));
				break;
			}

			case BT_AUTOPAIR_CONNECT_OK: {
				Toast.makeText(mContext,
						mContext.getResources().getString(R.string.yaokongqi_connect),
						Toast.LENGTH_SHORT).show();
				;

				break;
			}

			}
		}

	};
	
	

	private void CommonItemDataInit() {
		mPreference = new ImageSharePreference(mContext);
		// 初始化蓝牙名字
		if (null != mAdapter) {
			//String name = mAdapter.getName();
			String name = mPreference.getTVNAME();
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
		// 获取时间格式
		if (Utils.is24Hour(mContext)) {
			timeTypeId = 1;
		} else {
			timeTypeId = 0;
		}
		
		String k=	SystemProperties.get("ubootenv.var.init_vol", "0");
		Log.i("YQB", "------audio-----"+k);
	 if (null!=k){
		 if(k.equals("0")){
			 volumeSelected = 0;
		 }
		 else if(k.equals("5")){
			 volumeSelected = 1;
		 }
		 else if(k.equals("10")){
			 volumeSelected = 2;
		 }
		 else if(k.equals("15")){
			 volumeSelected = 3;
		 }
		 else if(k.equals("20")){
			 volumeSelected = 4;
		 }
		 else if(k.equals("25")){
			 volumeSelected = 5;
		 }
		 else if(k.equals("30")){
			 volumeSelected = 6;
		 }
		 else if(k.equals("35")){
			 volumeSelected = 7;
		 }
		 else if(k.equals("40")){
			 volumeSelected = 8;
		 }
		 else if(k.equals("45")){
			 volumeSelected = 9;
		 }
		 else if(k.equals("50")){
			 volumeSelected = 10;
		 }
		 else if(k.equals("55")){
			 volumeSelected = 11;
		 }
		 else if(k.equals("60")){
			 volumeSelected = 12;
		 }
		 else if(k.equals("65")){
			 volumeSelected = 13;
		 }
		 else if(k.equals("70")){
			 volumeSelected = 14;
		 }
		 else if(k.equals("75")){
			 volumeSelected = 15;
		 }
		 else if(k.equals("80")){
			 volumeSelected = 16;
		 }
		 else if(k.equals("85")){
			 volumeSelected = 17;
		 }
		 else if(k.equals("90")){
			 volumeSelected = 18;
		 }
		 else if(k.equals("95")){
			 volumeSelected = 19;
		 }
		 else if(k.equals("100")){
			 volumeSelected = 20;
		 }
	 }

		focusView = (LauncherFocusView) findViewById(R.id.activity_common_focusview);
		// mRelative = (RelativeLayout) findViewById(R.id.activity_common_id);
		focusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

			@Override
			public void OnAnimateStart(View currentFocusView) {
				// TODO Auto-generated method stub
				mFocusAnimationEndFlag = false;
			}

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				Log.i(TAG, "-----AnimateEnd----"+ (System.currentTimeMillis()-time));
				mFocusAnimationEndFlag = true;
				listTextColorSet();
			}

		});
		String[] mTempArray = this.getResources().getStringArray(R.array.commonsetting);

		

		final long currentTimeout = Settings.System.getLong(mContext.getContentResolver(),
				SCREEN_OFF_TIMEOUT, FALLBACK_SCREEN_TIMEOUT_VALUE);
		if (currentTimeout == 300000) {
			monitorProtectId = 0;
		} else if (currentTimeout == 600000) {
			monitorProtectId = 1;
		} else if (currentTimeout == 900000) {
			monitorProtectId = 2;
		} else {
			monitorProtectId = 3;
		}
		
		//中文的时候显示位置信息，英文就隐藏
		Configuration conf = getResources().getConfiguration();
        String language = conf.locale.getLanguage();
		
        pageLefts = new Drawable[mArrays.length];
        pageRights = new Drawable[mArrays.length];
        mItemSettings = new String[mArrays.length];
        
        pageLefts[0] = pageLefts[1] = pageLefts[2] =pageLefts[3] = pageLefts[4] = this.getResources().getDrawable(
				R.drawable.page_left);
		pageRights[0] = pageRights[1] = pageRights[2] =pageRights[3] = pageRights[4] =this.getResources().getDrawable(
				R.drawable.page_right);
		
		// 获取选项值
		mItemSettings[0] = CommonActivity.getStringArrays(mContext, R.array.tv_name,
						mTvNameSelectId);
	    mItemSettings[1] = CommonActivity.getStringArrays(mContext, R.array.time_array1,
						monitorProtectId);
		mItemSettings[2] = CommonActivity
						.getStringArrays(mContext, R.array.time_array3, timeTypeId);
		mItemSettings[3] = CommonActivity
						.getStringArrays(mContext, R.array.pcm_raw, mPcmRawId);
		mItemSettings[4] = CommonActivity.getStringArrays(mContext, R.array.volume_array,
				volumeSelected);
		
		List<String> list = new ArrayList();
		for (int i = 0; i < mArrays.length; i++) {
			list.add(mArrays[i]);
		}
		for (int i = 0; i < list.size(); i++) {
			CommonItemList mTemp = new CommonItemList();
			mTemp.setItemName(list.get(i));

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
		if (commonSettingOnListAdapter == null) {
			commonSettingOnListAdapter = new CommonSettingOnList(this);
			commonSettingListView.setAdapter(commonSettingOnListAdapter);
		} else {
			commonSettingOnListAdapter.notifyDataSetChanged();
		}
		commonSettingListView.setSelection(0);
	}

	private void onBlindListener() {
		// TODO Auto-generated method stub
		commonSettingListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				mCurKeycode = keyCode;
				int selectItems = commonSettingListView.getSelectedItemPosition();
				View x = commonSettingListView.getSelectedView();
				if(x==null)
					return true;
				TextView tv = (TextView) x.findViewById(R.id.item_setting);
				ImageView imageRightTemp = (ImageView) x.findViewById(R.id.page_right);
				ImageView imageLeftTemp = (ImageView) x.findViewById(R.id.page_left);
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
							|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {

						if (event.getRepeatCount() == 0) {
							mTextColorChangeFlag = true;
							mKeyDownTime = event.getDownTime();
						} else {
							mTextColorChangeFlag = false;
							if (event.getDownTime() - mKeyDownTime >= Utils.KEYDOWN_DELAY_TIME) {
								Log.e("KeyEvent", "time=" + (event.getDownTime() - mKeyDownTime)
										+ " count" + event.getRepeatCount());
								mKeyDownTime = event.getDownTime();
							} else {
								return true;
							}
						}

						if (!mFocusAnimationEndFlag) {
							mTextColorChangeFlag = false;
						}
					}
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_RIGHT: {

						Log.i(TAG, "selectItems:" + selectItems);
						if (selectItems == PROTECT_MONITOR_TIME) {
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right_selected));
							monitorProtectId = (monitorProtectId + 1) % 4;
							int timeForProtect = Integer.parseInt(getStringArrays(mContext,
									R.array.time_array4, monitorProtectId));
							mPreference.setProtectTime(timeForProtect);
							int t = mPreference.getProtectTime();
							Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT,
									timeForProtect);
							tv.setText(getStringArrays(mContext, R.array.time_array1,
									monitorProtectId));
							mCommonItemList.get(PROTECT_MONITOR_TIME).setItemSetting(getStringArrays(mContext, R.array.time_array1,
									monitorProtectId));

						} else if (selectItems == TIME_TYPE) {
							// TODO
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right_selected));
							timeTypeId = (timeTypeId + 1) % 2;

							tv.setText(getStringArrays(mContext, R.array.time_array3, timeTypeId));
							if (timeTypeId == 0) {
								Utils.set24Hour(false, mContext);
							} else if (timeTypeId == 1) {
								Utils.set24Hour(true, mContext);
							}
							mCommonItemList.get(TIME_TYPE).setItemSetting(getStringArrays(mContext, R.array.time_array3,
									timeTypeId));
							broadTime();
						} else if (selectItems == TV_NAME) {
							imageRightTemp.setImageDrawable(CommonActivity.this.getResources()
									.getDrawable(R.drawable.page_right_selected));
							mTvNameSelectId = (mTvNameSelectId + 1) % 3;
							tv.setText(CommonActivity.getStringArrays(mContext, R.array.tv_name,
									mTvNameSelectId));
							Log.i(TAG, "----bluetooth statue-----"+mAdapter.isEnabled());
							if (null != mAdapter)
								mAdapter.setName(CommonActivity.getStringArrays(mContext,
										R.array.tv_name, mTvNameSelectId));
							mCommonItemList.get(TV_NAME).setItemSetting(getStringArrays(mContext, R.array.tv_name,
									mTvNameSelectId));
							mPreference.setTVNAME(CommonActivity.getStringArrays(mContext,
										R.array.tv_name, mTvNameSelectId));
						}else if (selectItems == PCM_RAW){
							imageRightTemp.setImageDrawable(CommonActivity.this.getResources()
									.getDrawable(R.drawable.page_right_selected));
							mPcmRawId = (mPcmRawId + 1) % 2;
							tv.setText(getStringArrays(mContext, R.array.pcm_raw, mPcmRawId));
							//TODO
							mCommonItemList.get(PCM_RAW).setItemSetting(getStringArrays(mContext, R.array.pcm_raw,
									mPcmRawId));
						}
						else if (selectItems == VIDEO_VOLUME) {
							imageRightTemp.setImageDrawable(CommonActivity.this.getResources()
									.getDrawable(R.drawable.page_right_selected));
							if(volumeSelected < 20)
								{
								volumeSelected = (volumeSelected + 1) % 21;														
								}
							else if(volumeSelected > 19)
							{
								volumeSelected=20;
							}
				/*    		tv.setText(CommonActivity.getStringArrays(mContext, R.array.volume_array,
				    				volumeSelected));                 */
				    		
				    		
				    		Log.i("YQB", "------ID------"+volumeSelected);
				     		if(volumeSelected == 0)
				    		{
				     			tv.setText(getStringArrays(mContext, R.array.volume_array,
				    			0));
				    			SystemProperties.set("ubootenv.var.init_vol", "0");
				    		}
				    		else if(volumeSelected ==1)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array,1));						    			
				    			SystemProperties.set("ubootenv.var.init_vol", "5");
				    		}
				    		else if(volumeSelected ==2)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 2));						    			
				    			SystemProperties.set("ubootenv.var.init_vol", "10");
				    		}
				    		else if(volumeSelected ==3)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 3));
				    			SystemProperties.set("ubootenv.var.init_vol", "15");
				    		}
				    		else if(volumeSelected ==4)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 4));
				    			SystemProperties.set("ubootenv.var.init_vol", "20");
				    		}
				    		else if(volumeSelected ==5)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 5));
				    			SystemProperties.set("ubootenv.var.init_vol", "25");
				    		}
				    		else if(volumeSelected ==6)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 6));
				    			SystemProperties.set("ubootenv.var.init_vol", "30");
				    		}
				    		else if(volumeSelected ==7)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 7));
				    			SystemProperties.set("ubootenv.var.init_vol", "35");
				    		}
				    		else if(volumeSelected ==8)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 8));
				    			SystemProperties.set("ubootenv.var.init_vol", "40");
				    		}
				    		else if(volumeSelected ==9)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 9));
				    			SystemProperties.set("ubootenv.var.init_vol", "45");
				    		}
				    		else if(volumeSelected ==10)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 10));
				    			SystemProperties.set("ubootenv.var.init_vol", "50");
				    		}
				    		else if(volumeSelected ==11)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 11));
				    			SystemProperties.set("ubootenv.var.init_vol", "55");
				    		}
				    		else if(volumeSelected ==12)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 12));
				    			SystemProperties.set("ubootenv.var.init_vol", "60");
				    		}
				    		else if(volumeSelected ==13)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 13));
				    			SystemProperties.set("ubootenv.var.init_vol", "65");
				    		}
				    		else if(volumeSelected ==14)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 14));
				    			SystemProperties.set("ubootenv.var.init_vol", "70");
				    		}
				    		else if(volumeSelected ==15)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 15));
				    			SystemProperties.set("ubootenv.var.init_vol", "75");
				    		}
				    		else if(volumeSelected ==16)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 16));
				    			SystemProperties.set("ubootenv.var.init_vol", "80");
				    		}
				    		else if(volumeSelected ==17)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 17));
				    			SystemProperties.set("ubootenv.var.init_vol", "85");
				    		}
				    		else if(volumeSelected ==18)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 18));
				    			SystemProperties.set("ubootenv.var.init_vol", "90");
				    		}
				    		else if(volumeSelected ==19)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 19));
				    			SystemProperties.set("ubootenv.var.init_vol", "95");
				    		}
				    		else if(volumeSelected ==20)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 20));
				    			SystemProperties.set("ubootenv.var.init_vol", "100");
				    		}  
				     		mCommonItemList.get(VIDEO_VOLUME).setItemSetting(getStringArrays(mContext, R.array.volume_array,
				    				volumeSelected));
						}
						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						if (selectItems == PROTECT_MONITOR_TIME) {
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left_selected));
							monitorProtectId = (monitorProtectId + 3) % 4;
							int timeForProtect = Integer.parseInt(getStringArrays(mContext,
									R.array.time_array4, monitorProtectId));
							mPreference.setProtectTime(timeForProtect);
							Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT,
									timeForProtect);
							tv.setText(getStringArrays(mContext, R.array.time_array1,
									monitorProtectId));
							mCommonItemList.get(PROTECT_MONITOR_TIME).setItemSetting(getStringArrays(mContext, R.array.time_array1,
									monitorProtectId));
						} else if (selectItems == TIME_TYPE) {

							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left_selected));
							timeTypeId = (timeTypeId + 1) % 2;

							tv.setText(getStringArrays(mContext, R.array.time_array3, timeTypeId));
							if (timeTypeId == 0) {
								Utils.set24Hour(false, mContext);
							} else if (timeTypeId == 1) {
								Utils.set24Hour(true, mContext);
							}
							mCommonItemList.get(TIME_TYPE).setItemSetting(getStringArrays(mContext, R.array.time_array3,
									timeTypeId));
							broadTime();
						}else if(selectItems == PCM_RAW){
							
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left_selected));
							mPcmRawId = (mPcmRawId + 1) % 2;
							
							tv.setText(getStringArrays(mContext, R.array.pcm_raw, mPcmRawId));							
								
							mCommonItemList.get(PCM_RAW).setItemSetting(getStringArrays(mContext, R.array.pcm_raw,
									mPcmRawId));
						}
						else if (selectItems == TV_NAME) {
							imageLeftTemp.setImageDrawable(CommonActivity.this.getResources()
									.getDrawable(R.drawable.page_left_selected));
							mTvNameSelectId = (mTvNameSelectId + 2) % 3;
							tv.setText(CommonActivity.getStringArrays(mContext, R.array.tv_name,
									mTvNameSelectId));
							if (null != mAdapter)
								mAdapter.setName(CommonActivity.getStringArrays(mContext,
										R.array.tv_name, mTvNameSelectId));
							mCommonItemList.get(TV_NAME).setItemSetting(getStringArrays(mContext, R.array.tv_name,
									mTvNameSelectId));
							mPreference.setTVNAME(CommonActivity.getStringArrays(mContext,
									R.array.tv_name, mTvNameSelectId));
						}
						
						else if (selectItems == VIDEO_VOLUME) {
							imageLeftTemp.setImageDrawable(CommonActivity.this.getResources()
									.getDrawable(R.drawable.page_left_selected));
								if(volumeSelected < 22 && volumeSelected > 0)
                                 {
									volumeSelected = (volumeSelected + 20) % 21;																									
                                 }
								if(volumeSelected == 0)
									{
									volumeSelected = 0;
									}
			/*				tv.setText(CommonActivity.getStringArrays(mContext, R.array.volume_array,
									volumeSelected));             */
							
							
							
						  	if(volumeSelected == 0)
				    		{
						  		tv.setText(getStringArrays(mContext, R.array.volume_array, 0));
				    			SystemProperties.set("ubootenv.var.init_vol", "0");
				    		}
				    		else if(volumeSelected ==1)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 1));
				    			SystemProperties.set("ubootenv.var.init_vol", "5");
				    		}
				    		else if(volumeSelected ==2)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 2));
				    			SystemProperties.set("ubootenv.var.init_vol", "10");
				    		}
				    		else if(volumeSelected ==3)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 3));
				    			SystemProperties.set("ubootenv.var.init_vol", "15");
				    		}
				    		else if(volumeSelected ==4)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 4));
				    			SystemProperties.set("ubootenv.var.init_vol", "20");
				    		}
				    		else if(volumeSelected ==5)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 5));
				    			SystemProperties.set("ubootenv.var.init_vol", "25");
				    		}
				    		else if(volumeSelected ==6)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 6));
				    			SystemProperties.set("ubootenv.var.init_vol", "30");
				    		}
				    		else if(volumeSelected ==7)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 7));
				    			SystemProperties.set("ubootenv.var.init_vol", "35");
				    		}
				    		else if(volumeSelected ==8)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 8));
				    			SystemProperties.set("ubootenv.var.init_vol", "40");
				    		}
				    		else if(volumeSelected ==9)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 9));
				    			SystemProperties.set("ubootenv.var.init_vol", "45");
				    		}
				    		else if(volumeSelected ==10)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 10));
				    			SystemProperties.set("ubootenv.var.init_vol", "50");
				    		}
				    		else if(volumeSelected ==11)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 11));
				    			SystemProperties.set("ubootenv.var.init_vol", "55");
				    		}
				    		else if(volumeSelected ==12)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 12));
				    			SystemProperties.set("ubootenv.var.init_vol", "60");
				    		}
				    		else if(volumeSelected ==13)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 13));
				    			SystemProperties.set("ubootenv.var.init_vol", "65");
				    		}
				    		else if(volumeSelected ==14)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 14));
				    			SystemProperties.set("ubootenv.var.init_vol", "70");
				    		}
				    		else if(volumeSelected ==15)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 15));
				    			SystemProperties.set("ubootenv.var.init_vol", "75");
				    		}
				    		else if(volumeSelected ==16)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 16));
				    			SystemProperties.set("ubootenv.var.init_vol", "80");
				    		}
				    		else if(volumeSelected ==17)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 17));
				    			SystemProperties.set("ubootenv.var.init_vol", "85");
				    		}
				    		else if(volumeSelected ==18)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 18));
				    			SystemProperties.set("ubootenv.var.init_vol", "90");
				    		}
				    		else if(volumeSelected ==19)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 19));
				    			SystemProperties.set("ubootenv.var.init_vol", "95");
				    		}
				    		else if(volumeSelected ==20)
				    		{
				    			tv.setText(getStringArrays(mContext, R.array.volume_array, 20));
				    			SystemProperties.set("ubootenv.var.init_vol", "100");
				    		}
						  	mCommonItemList.get(VIDEO_VOLUME).setItemSetting(getStringArrays(mContext, R.array.volume_array,
				    				volumeSelected));
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
						if (selectItems == TV_NAME || selectItems == PROTECT_MONITOR_TIME
								|| selectItems == TIME_TYPE )
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right));

						break;
					}
					case KeyEvent.KEYCODE_DPAD_LEFT: {
						if (selectItems == PROTECT_MONITOR_TIME || selectItems == TV_NAME
								|| selectItems == TIME_TYPE ) {
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

		commonSettingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub

				if (position == YAOKONG) {
					startBLService();
				}

				if (position == VERSION_CHECK) {
					Intent intent = new Intent();
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setPackage("com.hiveview.upgrade");
					intent.setClassName("com.hiveview.upgrade","com.hiveview.upgrade.ManulActivity");
					mContext.startActivity(intent);
					
					
				}
				
				if (position == KEDA_SETTING) {
					Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mKEDA);
					if (intent != null) {
						mContext.startActivity(intent);
					}
				}
				if (position == DEVELOPER_SETTINGS) {
					startActivity(new Intent().setClass(mContext, DevloperModelActivity.class));
				}
				if (position == BLUETOOTH_SETTING) {

					startActivity(new Intent().setClass(mContext, BlueToothActivity.class));
				}
				if (position == LOCATION_SETTINGS) {
					startActivity(new Intent().setClass(mContext, LocationSettingsActivity.class));
				}
				if (position == RESET) {

					final ResetDialog dialog = new ResetDialog(mContext);
					Window dialogWindow = dialog.getWindow();
					WindowManager.LayoutParams lp = dialogWindow.getAttributes();
					lp.width = 946;
					lp.height = 754;
					dialogWindow.setAttributes(lp);
					dialog.show();
					TextView message = (TextView) dialog.findViewById(R.id.message1);
					message.setText(getString(R.string.clean_all_confirm));

					Button button1 = (Button) dialog.findViewById(R.id.button1);
					Button button2 = (Button) dialog.findViewById(R.id.button2);
					button1.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View paramView) {
							// TODOAuto-generated method stub
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

		commonSettingListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> paramAdapterView, View view, int position,
					long paramLong) {
				mItemListCurPosition = position;
				mView = view;
				time = System.currentTimeMillis();
				if(view == null)
					return;
				if (bfocusViewInitStatus) {
					focusView.initFocusView(mView, false, 0);
				}
				if (mTextView != null) {
					mTextView.setTextColor(mContext.getResources().getColor(R.color.grey5_color));
				}
				if (mTextViewSetting != null) {
					mTextViewSetting.setTextColor(mContext.getResources().getColor(
							R.color.grey5_color));
				}
				if (mImageView != null) {
					mImageView.setImageResource(R.drawable.page_right_big);
				}

				mTextView = (TextView) view.findViewById(R.id.item_name);
				if ((position == 0 || position == 1 || position == 2 )) {
					mTextViewSetting = (TextView) view.findViewById(R.id.item_setting);
				} else {
					mTextViewSetting = null;
				}

				if ((position == 7|| position == 11 || position == 5
						|| position == 6 || position == 8 || position == 9||position==10)) {
					mImageView = (ImageView) view.findViewById(R.id.page_right);
				} else {
					mImageView = null;
				}

				if (mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN) {

					if (position < 5
							|| position > commonSettingListView.getCount() - 2
							|| (commonSettingListView.getFirstVisiblePosition() == 0 && view
									.getTop() < (view.getHeight() * 4))
							|| (commonSettingListView.getFirstVisiblePosition() != 0 && view
									.getTop() < view.getHeight() * 5)) {
						focusView.moveTo(mView);
					} else {
						listTextColorSet();
						commonSettingListView.setSelectionFromTop(position,
								view.getTop() - view.getHeight());

					}
				} else if (mCurKeycode == KeyEvent.KEYCODE_DPAD_UP) {
					if ((mItemListCurPosition == 0 || commonSettingListView
							.getFirstVisiblePosition() == 0 && view.getTop() > (view.getHeight()))
							|| (commonSettingListView.getFirstVisiblePosition() != 0 && view
									.getTop() >= view.getHeight())) {
						focusView.moveTo(mView);
					} else {
						listTextColorSet();
						commonSettingListView.setSelectionFromTop(mItemListCurPosition,
								view.getHeight());
					}

				} else if (mCurKeycode == KeyEvent.KEYCODE_PAGE_UP
						|| mCurKeycode == KeyEvent.KEYCODE_PAGE_DOWN) {
					focusView.moveTo(view);
				}
				if (bfocusViewInitStatus) {
					bfocusViewInitStatus = false;
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
				// fixed the keyboard repeat mode
				if (!mTextColorChangeFlag && mFocusAnimationEndFlag) {
					if ((mItemListCurPosition == 0 || mItemListCurPosition == commonSettingListView
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

	public static String getStringArrays(Context mContext, int id, int position) {
		String[] arrays = mContext.getResources().getStringArray(id);
		if (null != arrays) {
			return arrays[position];
		}
		return "";
	}

	private void listTextColorSet() {
		if (mTextColorChangeFlag && mFocusAnimationEndFlag) {
			if (mTextView != null) {
				mTextView.setTextColor(this.getResources().getColor(R.color.white));
			}
			if (mTextViewSetting != null) {
				mTextViewSetting.setTextColor(this.getResources().getColor(R.color.white));
			}
			if (mImageView != null) {
				mImageView.setImageResource(R.drawable.page_right_big_selected);
			}
			mTextColorChangeFlag = false;

		}
	}

	class CommonSettingOnList extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context cont;
		private int selectItem;
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;

		class ViewHolder {
			TextView itemName;
			ImageView pageLeft;
			TextView itemSetting;
			ImageView pageRight;
		}

		public CommonSettingOnList(Context context) {
			super();
			cont = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			int i = mArrays != null ? mArrays.length : 0;
			Log.i("length", String.valueOf(i));
			return mArrays != null ? mArrays.length : 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public void setSelectItem(int position) {
			selectItem = position;
		}

		public int getSelectItem() {
			return selectItem;
		}

		public int getItemViewType(int position) {
			int p = position;
			if (p == 7  || p == 11 || p == 5 || p == 6 || p == 8 || p == 9 || p == 10)
				{
				  return TYPE_1;
				
			}else{
				return TYPE_2;
			}
				
		}

		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder1 = null;
			ViewHolder holder2 = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case TYPE_2: {
					convertView = mInflater.inflate(R.layout.activity_item_for_common, null);
					holder1 = new ViewHolder();
					holder1.itemName = (TextView) convertView.findViewById(R.id.item_name);
					holder1.itemSetting = (TextView) convertView.findViewById(R.id.item_setting);
					holder1.pageLeft = (ImageView) convertView.findViewById(R.id.page_left);
					holder1.pageRight = (ImageView) convertView.findViewById(R.id.page_right);
					convertView.setTag(holder1);
					break;
				}
				case TYPE_1: {
					convertView = mInflater.inflate(R.layout.activity_item_forcommon2, null);
					holder2 = new ViewHolder();
					holder2.itemName = (TextView) convertView.findViewById(R.id.item_name);
					holder2.pageRight = (ImageView) convertView.findViewById(R.id.page_right);
					convertView.setTag(holder2);
					break;
				}
				}
			} else {
				switch (type) {
				case TYPE_1: {
					holder2 = (ViewHolder) convertView.getTag();
					break;
				}
				case TYPE_2: {
					holder1 = (ViewHolder) convertView.getTag();
					break;
				}
				}
			}

			switch (type) {

			case (TYPE_2): {
				if (null != mCommonItemList.get(position).getItemName()) {
					holder1.itemName.setText(mCommonItemList.get(position).getItemName());

				}
				if (null != mCommonItemList.get(position).getPageLeft()) {
					holder1.pageLeft.setImageDrawable(mCommonItemList.get(position).getPageLeft());
					holder1.pageLeft.setVisibility(View.VISIBLE);
					holder1.itemSetting.setVisibility(View.VISIBLE);

				} else {
					holder1.pageLeft.setImageDrawable(null);
					holder1.pageLeft.setVisibility(View.GONE);
				}
				if (null != mCommonItemList.get(position).getItemSetting()) {
					// TODO
					holder1.itemSetting.setText(mCommonItemList.get(position).getItemSetting());
					holder1.itemSetting.setVisibility(View.VISIBLE);
				} else {
					holder1.itemSetting.setText(null);
					holder1.itemSetting.setVisibility(View.GONE);
				}
				if (null != mCommonItemList.get(position).getPageRight()) {

					holder1.pageRight
							.setImageDrawable(mCommonItemList.get(position).getPageRight());
					holder1.pageRight.setVisibility(View.VISIBLE);

				} else {

					holder1.pageLeft.setImageDrawable(null);
					holder1.pageLeft.setVisibility(View.GONE);

				}
				break;
			}
			case (TYPE_1): {
				if (null != mCommonItemList.get(position).getItemName()) {
					holder2.itemName.setText(mCommonItemList.get(position).getItemName());

				}
				break;
			}
			}

			return convertView;
		}
	}

}
