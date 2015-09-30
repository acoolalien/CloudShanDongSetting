package com.shandong.cloudtv.settings;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.ethernet.EthernetManager;

import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.CloundMenuWindow;
import com.shandong.cloudtv.settings.widget.CommonListView;
import com.shandong.cloudtv.settings.widget.CustomProgressDialog;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.CloundMenuWindow.MenuItemEntity;
import com.shandong.cloudtv.settings.widget.CloundMenuWindow.OnSelectedItemClickListener;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.wifi.WifiAccessPoint;
import com.shandong.cloudtv.settings.wifi.WifiDataEntity;
import com.shandong.cloudtv.settings.wifi.WifiInfoConfigEntity;
import com.shandong.cloudtv.settings.wifi.WifiItemHolder;


public class WifiConnectActivity extends Activity {
	private static final String TAG = "WifiConnectActivity";
	private static final boolean DEBUG = true;
	// Combo scans can take 5-6s to complete - set to 10s.
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
	private static final int WIFI_CONNECT_STATE_MSG = 0x0001;
	private static final int WIFI_CONNECT_STATE_FAILED_MSG = 0x0002;
	private static final int WIFI_CONNECT_STATE_SUCCESS_MSG = 0x0003;
	
	private static final int WIFI_OP_RE_CONNECT = 1000;
	private static final int WIFI_OP_FORGET = 999;
	
	private CommonListView mWifiList = null;
	private WifiItemHolder mItemCurHolder = null;
	private WifiConnectAdapter mWifiAdapter = null;
	private IntentFilter mWifiMSGFilter = null;
	private BroadcastReceiver mWifiMSGReceiver = null;
	private Scanner mScanner;
	private WifiDataEntity mWifiDataEntity = null;
	private List<WifiAccessPoint> mAccessPoints;
	private AtomicBoolean mConnected = new AtomicBoolean(false);
	private WifiAccessPoint mSelectedAccessPoint = null;
	private DetailedState mLastState;
	private WifiInfo mLastInfo;
	private ImageView mCurWifiConnectStateImg = null;
	private Handler mHandler = null;
	private int mErrNetId = -1;
	private int mListCurPosition = -1;
	private LinkProperties mLinkProperties = null;
	private CustomProgressDialog mProgressDialog = null;

	// list focus view
	private View mItemListCurView = null;
	private LauncherFocusView mLauncherFocusView = null;
	private boolean mIsFirstIn = true;
	
	private int curPingStatus = 0;
	
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	private Animation mWifiPasswordErrorAnimation = null;

	private boolean mTextColorChangeFlag = false;
	private boolean mEditTextFocusFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private long mKeyDownTime = 0L;
	
	private WifiItemHolder mCurConnectHolder = null;
	private boolean connectingWifi = false;
	private boolean goTop = false;
	
	private boolean isNativeGo = false;
	private boolean forgetSucessToast = true;
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.wifi_connect_main);
		mWifiMSGFilter = new IntentFilter();
		mWifiMSGFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mWifiMSGFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mWifiMSGFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mWifiMSGFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mWifiMSGFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mWifiMSGFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mWifiMSGFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mWifiMSGFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mWifiMSGReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleEvent(context, intent);
			}
		};
		mWifiDataEntity = WifiDataEntity.getInstance(this);
		isNativeGo = getIntent().getBooleanExtra("isNativeGo", true);
		Log.v(TAG, "isNativeGo=="+isNativeGo);
		if(mWifiDataEntity.getWifiApState() != WifiManager.WIFI_AP_STATE_DISABLED){
			mWifiDataEntity.setWifiApEnabled(null, false);
		}
		
//		mWifiDataEntity.updateWifi();
		
		if(Utils.pingHost("www.baidu.com")){
			curPingStatus = 0;
		}else {
			curPingStatus = 1;
		}
		//Log.v(TAG, "curpingStatus=="+curPingStatus);
		
		mScanner = new Scanner();
		

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case WIFI_CONNECT_STATE_MSG:
					curPingStatus = msg.arg1; 
					//Log.v(TAG, "update wifi ping state=="+curPingStatus);
			        WifiConnectAdapter adapter = (WifiConnectAdapter) mWifiList.getAdapter();
			        if(adapter!=null)
			           adapter.notifyDataSetChanged();
					break;
				case WIFI_CONNECT_STATE_FAILED_MSG:
					if(mItemCurHolder!=null){
						mItemCurHolder.mItemContentEdit.startAnimation(mWifiPasswordErrorAnimation);
						mItemCurHolder.mItemContentEdit.setText("");
					}
					mEditTextFocusFlag = false;
					Toast.makeText(WifiConnectActivity.this, 
							getResources().getString(R.string.wifi_password_error), Toast.LENGTH_SHORT).show();
					connectingWifi = false;
					break;
				case WIFI_CONNECT_STATE_SUCCESS_MSG:					 
					goTop = true;
					mWifiList.setSelection(1);
					updateAccessPoints();
					break;
				default:
					break;
				}
			}
		};

		
		initView();
		regeditReceiver();
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode) { //根据状态码，处理返回结果  
        case WifiInfoConfigEntity.RESULT_WIFI_MODIFY_OK:  
        	  boolean saveFlag = data.getBooleanExtra(WifiInfoConfigEntity.SAVE_CONFIG, false);
        	  if(DEBUG){
        		  Log.e(TAG, "get save flag "+saveFlag);
        	  }
        	  if(saveFlag){
        		  WifiInfoConfigEntity dataConfig = data.getParcelableExtra(WifiInfoConfigEntity.KEY);
        		  WifiConfiguration wifiConfig = getWifiPointConfig(dataConfig);
        		  save(wifiConfig);
        	  }
        	break;
        case WifiInfoConfigEntity.RESULT_WIFI_FORGET:
        	forget();
        	break;
        case WifiInfoConfigEntity.RESULT_WIFI_ADD:
        	String ssid = data.getStringExtra(WifiInfoConfigEntity.SSID_KEY);
        	String password = data.getStringExtra(WifiInfoConfigEntity.PASSWORD_KEY);
        	int security = data.getIntExtra(WifiInfoConfigEntity.SECURITY_KEY, WifiAccessPoint.SECURITY_NONE);
        	WifiConfiguration config = getWifiAddConfig(ssid, password, security);
			if (config != null) {
				mWifiDataEntity.save(config, mSaveListener);
			}
			updateAccessPoints();
			mWifiList.setSelection(mWifiList.getAdapter().getCount() - 2);
        	break;
        default:  
        break;  
        }    
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegeditReceiver();
	}

	private void initView() {
		mWifiList = (CommonListView) findViewById(R.id.wifi_list);
		mWifiList.setOnItemClickListener(mWifiOnItemClickListener);
		mWifiList.setOnItemSelectedListener(mWifiOnItemSelectedListener);
		mWifiList.setOnKeyListener(mWifiListOnKeyListener);
		

		// list focus view
		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView) findViewById(R.id.wifi_connect_focus_view);
		mLauncherFocusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {
			
			@Override
			public void OnAnimateStart(View currentFocusView) {
				// TODO Auto-generated method stub
				mFocusAnimationEndFlag = false;
			}

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				//mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				mFocusAnimationEndFlag = true;
				listTextColorSet();
			}
		});

		mWifiPasswordErrorAnimation = AnimationUtils.loadAnimation(this, R.anim.wifi_password_error);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mScanner.pause();
		this.unregisterReceiver(mWifiMSGReceiver);
		mHandler.removeMessages(WIFI_CONNECT_STATE_MSG);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mWifiDataEntity.setWifiEnabled(true);
		registerReceiver(mWifiMSGReceiver, mWifiMSGFilter);
		if (mWifiDataEntity.isWifiEnabled()) {
			mScanner.resume();
		}
		startProgressDialog(getResources().getString(R.string.wifi_scaning));
		
		updateAccessPoints();
		
		super.onResume();
	}

	AdapterView.OnItemClickListener mWifiOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			// TODO Auto-generated method stub
			if (position == 0) {
				Utils.setApplicationBGBitmap(WifiConnectActivity.this);
				Intent intent = new Intent(WifiConnectActivity.this, ManualAddWifiAcvitity.class);
				startActivityForResult(intent, 0);
			}
			
			Log.v(TAG, "onItemClick");
			mSelectedAccessPoint = (WifiAccessPoint)(((WifiConnectAdapter)mWifiList.getAdapter()).getItem(position));
			
			if (position != 0 && mItemCurHolder != null && mSelectedAccessPoint!=null) {
				if (mSelectedAccessPoint.wifiConnState == WifiAccessPoint.STATE_DISABLE || mSelectedAccessPoint.getLevel()==-1) {
					showConnectOrForgetDialog();
					return;
				}
                connectingWifi = false;

				if (mSelectedAccessPoint.wifiConnState == WifiAccessPoint.STATE_CONNECTED) {
					onKeyRight();
					return;
				}

				if (mSelectedAccessPoint.security == WifiAccessPoint.SECURITY_NONE) {
					connect();
				}else{
					WifiConfiguration config = mSelectedAccessPoint.getConfig();
					if (config != null) {
//						Log.v(TAG, "3333=="+config.disableReason);
						//有时候wifi还没有进行校验，就直接保存了WIFI信息，这个时候需要能够忘记网络
						//&& config.disableReason != WifiConfiguration.DISABLED_AUTH_FAILURE
						if (hasPassword(config)) {
							showConnectOrForgetDialog();
						}
					}else{
//						Log.v(TAG, "2222");
						mItemCurHolder.mItemContentEdit.setVisibility(View.VISIBLE);
						mItemCurHolder.mItemContentEdit.setFocusable(true);
						mWifiList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
						mItemCurHolder.mItemContentEdit.setText("");
						mItemCurHolder.mItemContentEdit.requestFocus();
						mItemCurHolder.mItemContentEdit.setCursorVisible(true);
						mItemCurHolder.mItemContentEdit.setOnEditorActionListener(mEditorActionListener);
						InputMethodManager mng = (InputMethodManager) WifiConnectActivity.this
								.getSystemService(Activity.INPUT_METHOD_SERVICE);
						mng.showSoftInput(mItemCurHolder.mItemContentEdit, InputMethodManager.SHOW_IMPLICIT);
						mEditTextFocusFlag = true;
					}
				}
			}else{
				if(DEBUG){
					Log.e(TAG, "enter here mItemCurHolder state:"+(mItemCurHolder==null)+
							" mSelectedAccessPoint state:"+(mSelectedAccessPoint==null));
				}
			}
		}

	};

	AdapterView.OnItemSelectedListener mWifiOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
			// TODO Auto-generated method stub
			// list focus view
			mItemListCurView = view;
			mListCurPosition = position;
			mSelectedAccessPoint = (WifiAccessPoint)(((WifiConnectAdapter)mWifiList.getAdapter()).getItem(position));
			if (mIsFirstIn) {
				mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
			}
			if(DEBUG){
				Log.e(TAG, "cur keycode :"+mCurKeycode+"mListCurPosition=="+mListCurPosition);
			}


			if (position != 0 && mItemCurHolder != null) {
				if (mItemCurHolder.mItemContentEdit.isFocusable()) {
					mItemCurHolder.mItemContentEdit.setFocusable(false);
					mItemCurHolder.mItemContentEdit.setVisibility(View.INVISIBLE);
					mItemCurHolder.mItemContentEdit.setCursorVisible(false);
					mWifiList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
					mItemCurHolder.mItemContentEdit.requestFocus();
					mEditTextFocusFlag = false;
				}
			}
			// text color reset
			if (mItemCurHolder != null) {
				mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
				mItemCurHolder.mItemContentEdit.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			}
			mItemCurHolder = (WifiItemHolder) view.getTag();
			
			if(goTop){
				goTop = false;
				mLauncherFocusView.moveToWifiTop(mItemListCurView);
				Log.v(TAG, "moveToWifiTop");
				return;
			}
 
			if(mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN){
 
				if(mListCurPosition<5 || mListCurPosition>mWifiList.getCount()-2
						||(mWifiList.getFirstVisiblePosition()==0&&view.getTop()<(view.getHeight()*4))
						||(mWifiList.getFirstVisiblePosition()!=0&&view.getTop()<view.getHeight()*5)){
					mLauncherFocusView.moveTo(mItemListCurView);
//					Log.v(TAG, ">>>>>move");
				}else{
					mWifiList.setSelectionFromTop(mListCurPosition, view.getTop()-view.getHeight());
					listTextColorSet();
				}
 
			}else if(mCurKeycode == KeyEvent.KEYCODE_DPAD_UP){
				//mLauncherFocusView.moveTo(mItemListCurView);
				
				if(mListCurPosition == 1){
					mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				}
				
				Log.e(TAG, "list getheight:"+mWifiList.getHeight()+
						" view getTop:"+view.getTop()+"height:"+
						view.getHeight()+" visible first:"+mWifiList.getFirstVisiblePosition());
				
				if((mListCurPosition == 0||mWifiList.getFirstVisiblePosition()==0&&view.getTop()>=(view.getHeight()))
						||(mWifiList.getFirstVisiblePosition()!=0&&view.getTop()>view.getHeight())){
					mLauncherFocusView.moveTo(mItemListCurView);
				}else{
					listTextColorSet();
					mWifiList.setSelectionFromTop(mListCurPosition, view.getHeight());
				}
			}
 
			
			// text color set in mLaunchFocusView onAnimationEnd()
			if(mIsFirstIn){
				mIsFirstIn = false;
				mTextColorChangeFlag = true;
				listTextColorSet();
			}
			if(!mTextColorChangeFlag && mFocusAnimationEndFlag){
				if(mListCurPosition == 0 
						|| mListCurPosition==mWifiList.getCount()-1){
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}

	};

	private void listTextColorSet(){
		Log.v(TAG, "mTextColorChangeFlag=="+mTextColorChangeFlag+",mFocusAnimationEndFlag="+mFocusAnimationEndFlag);
		if (mItemCurHolder != null && mTextColorChangeFlag&&mFocusAnimationEndFlag) {
//			mTextColorChangeFlag = false;
			mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mItemCurHolder.mItemContentEdit.setTextColor(getResources().getColor(R.color.settings_ffffff));
			//mItemCurHolder.mItemSignalImg.setImageState(new int[] { R.attr.state_selected }, true);
//			Log.v(TAG, "set color white");
		}
	}
	
	View.OnKeyListener mWifiListOnKeyListener = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			if(keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_PAGE_UP){
	    		return true;
	    	}
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN 
						|| keyCode==KeyEvent.KEYCODE_DPAD_UP){
					mCurKeycode =  keyCode;
					
					if(event.getRepeatCount() == 0){
						mTextColorChangeFlag = true;
						mKeyDownTime = event.getDownTime();
					}else{
						mTextColorChangeFlag = false;
						if(event.getDownTime()-mKeyDownTime>=Utils.KEYDOWN_DELAY_TIME){
							Log.e("KeyEvent", "time="+(event.getDownTime()-mKeyDownTime)+" count"+event.getRepeatCount());
							mKeyDownTime = event.getDownTime();
						}else{
							return true;
						}
					}
					if(!mFocusAnimationEndFlag){
						mTextColorChangeFlag = false;
					}
				}else if(keyCode  == KeyEvent.KEYCODE_MENU){
					
					if(mWifiDataEntity.getConnectionInfo().getSSID().equals("0x")){
						return true;  //如果没有已经连接上的热点，那么就不弹出断开对话框
					}
					
					 
					MenuItemEntity entity = new MenuItemEntity();
					entity.setItemName(WifiConnectActivity.this.getResources().getString(
							R.string.ethernet_item_disable_net));
					entity.setItemIconFocusResId(R.drawable.setting_focus);
					entity.setItemPosition(999);
					entity.setItemIconResId(R.drawable.setting_focus);

					List<MenuItemEntity> menuList = new ArrayList<CloundMenuWindow.MenuItemEntity>();
					menuList.add(entity);
					CloundMenuWindow window = new CloundMenuWindow(WifiConnectActivity.this, menuList);

					window.setItemSelectedListener(new OnSelectedItemClickListener() {

						@Override
						public void selectedItemClick(MenuItemEntity entity) {
							// TODO Auto-generated method stub

							WifiAccessPoint accessPoint = (WifiAccessPoint)(((WifiConnectAdapter)mWifiList.getAdapter()).getItem(1));
							if(DEBUG){
								Log.i(TAG, "cur wifi connect state:"+accessPoint.wifiConnState);
							}
							if (accessPoint.wifiConnState == WifiAccessPoint.STATE_CONNECTED) {
								if (accessPoint.networkId == INVALID_NETWORK_ID) {
									// Should not happen, but a monkey seems to triger it
									Log.e(TAG, "Failed to forget invalid network " + mSelectedAccessPoint.getConfig());
									return;
								}
                                connectingWifi = false;
								mWifiDataEntity.forget(accessPoint.networkId, mForgetListener);

								if (mWifiDataEntity.isWifiEnabled()) {
									mScanner.resume();
								}
								updateAccessPoints();
							}

						}
					});
					window.show();
				}
			}else if(event.getAction() == KeyEvent.ACTION_UP){
				if(!mTextColorChangeFlag){
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	
	private void showConnectOrForgetDialog(){
		List<MenuItemEntity> menuList = new ArrayList<CloundMenuWindow.MenuItemEntity>();
		if(mSelectedAccessPoint.getLevel()!=-1){
	        MenuItemEntity connect_entity = new MenuItemEntity();
	        connect_entity.setItemName(WifiConnectActivity.this.getResources().getString(
					R.string.ethernet_dis_reconnect));
	        connect_entity.setItemIconFocusResId(R.drawable.setting_focus);
	        connect_entity.setItemPosition(WIFI_OP_RE_CONNECT);
	        connect_entity.setItemIconResId(R.drawable.setting_focus);
	        
	        menuList.add(connect_entity);
		}
        
		MenuItemEntity entity = new MenuItemEntity();
		entity.setItemName(WifiConnectActivity.this.getResources().getString(
				R.string.ethernet_item_disable_net));
		entity.setItemIconFocusResId(R.drawable.setting_focus);
		entity.setItemPosition(WIFI_OP_FORGET);
		entity.setItemIconResId(R.drawable.setting_focus);
		menuList.add(entity);
		
		
		CloundMenuWindow window = new CloundMenuWindow(WifiConnectActivity.this, menuList);

		window.setItemSelectedListener(new OnSelectedItemClickListener() {

			@Override
			public void selectedItemClick(MenuItemEntity entity) {
				// TODO Auto-generated method stub
				Log.v(TAG, "entity ===" + entity.getItemPosition());

				if (mSelectedAccessPoint.networkId == INVALID_NETWORK_ID)
					return;
				if (entity.getItemPosition() == WIFI_OP_RE_CONNECT) {
					connect();
				} else if (entity.getItemPosition() == WIFI_OP_FORGET) {
					connectingWifi = false;
					mWifiDataEntity.forget(mSelectedAccessPoint.networkId,mForgetListener);
					if (mWifiDataEntity.isWifiEnabled()) {
						mScanner.resume();
					}
					updateAccessPoints();
				}
			}
		});
		window.show();
	}

	WifiManager.ActionListener mConnectListener = new WifiManager.ActionListener() {
		public void onSuccess() {
			if(DEBUG){
				Log.e(TAG, "wifi connect success!");
			}
			mEditTextFocusFlag = false;
			mHandler.sendEmptyMessage(WIFI_CONNECT_STATE_SUCCESS_MSG);
		}

		public void onFailure(int reason) {
			if(DEBUG){
				Log.e(TAG, "wifi connect failed!");
			}
			mHandler.sendEmptyMessage(WIFI_CONNECT_STATE_FAILED_MSG);
		}
	};

	WifiManager.ActionListener mSaveListener = new WifiManager.ActionListener() {
		public void onSuccess() {
		}

		public void onFailure(int reason) {
			Toast.makeText(WifiConnectActivity.this, R.string.wifi_failed_save_message, Toast.LENGTH_SHORT)
						.show();
		}
	};

	WifiManager.ActionListener mForgetListener = new WifiManager.ActionListener() {
		public void onSuccess() {
			if(!forgetSucessToast){
				forgetSucessToast = true;
				return;
			}
			Toast.makeText(WifiConnectActivity.this, R.string.wifi_sucess_forget_message,
					Toast.LENGTH_SHORT).show();
		}

		public void onFailure(int reason) {
			Toast.makeText(WifiConnectActivity.this, R.string.wifi_failed_forget_message,
						Toast.LENGTH_SHORT).show();
		}
	};

	private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			Log.v(TAG, "mEditorActionListener");
			if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				try {
					if (DEBUG) {
						Log.e(TAG, "onclick enter password=");
					}
					InputMethodManager mng = (InputMethodManager) WifiConnectActivity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					
					if(view.getText().toString().trim().equals("")){
						Toast.makeText(WifiConnectActivity.this, getResources().getString(R.string.wifi_password_null), 1500).show();
						return true;
					}
					connect();
					
					
					if (mItemCurHolder.mItemContentEdit.isFocusable()) {
						mItemCurHolder.mItemContentEdit.setFocusable(false);
						mItemCurHolder.mItemContentEdit.setVisibility(View.INVISIBLE);
						mItemCurHolder.mItemContentEdit.setCursorVisible(false);
						mWifiList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
						mItemCurHolder.mItemContentEdit.requestFocus();
						mEditTextFocusFlag = false;
					}
				} catch (Exception e) {
					Log.e(TAG, "Hide key broad error", e);
				}
			}
			return false;
		}
	};
	
	public void startWifiConnectLoading() {
		if(mListCurPosition == 0){
			return;
		}
        if(mCurConnectHolder!=null){
        	mCurConnectHolder.mItemSignalImg.setImageBitmap(null);
        	mCurConnectHolder.mItemSignalImg.setBackgroundResource(R.drawable.wifi_connect_progress);
    		AnimationDrawable animationDrawable = (AnimationDrawable)mCurConnectHolder.mItemSignalImg.getBackground();  
            
            if(!animationDrawable.isRunning())
            {  
                animationDrawable.start();  
            }
        }
	}

	@SuppressLint("NewApi")
	public void stopWifiConnectLoading(){
        if(mCurConnectHolder!=null){
        	mCurConnectHolder.mItemSignalImg.setBackgroundResource(R.drawable.wifi_connect_progress);
        	AnimationDrawable animationDrawable = (AnimationDrawable)mCurConnectHolder.mItemSignalImg.getBackground();  
            
            if(animationDrawable.isRunning())
            {  
                animationDrawable.stop();  
            }
            mCurConnectHolder.mItemSignalImg.setBackground(null);
        }

	}
	
	private void connect(){
		if (mSelectedAccessPoint.wifiConnState == WifiAccessPoint.STATE_DISABLE || mSelectedAccessPoint.getLevel()==-1) {
			Toast.makeText(WifiConnectActivity.this, R.string.wifi_not_in_range, Toast.LENGTH_SHORT).show();
			return;
		}

		if (mSelectedAccessPoint.wifiConnState == WifiAccessPoint.STATE_CONNECTED) {
			onKeyRight();
			return;
		}
		stopWifiConnectLoading();
		mCurConnectHolder = mItemCurHolder;
		
        connectingWifi = true;

        
		if (mSelectedAccessPoint.security == WifiAccessPoint.SECURITY_NONE) {
			mSelectedAccessPoint.generateOpenNetworkConfig();
			mWifiDataEntity.connect(mSelectedAccessPoint.getConfig(), mConnectListener);
		} else {
			WifiConfiguration config = mSelectedAccessPoint.getConfig();
			if (config != null) {
				if (hasPassword(config)
						&& config.disableReason != WifiConfiguration.DISABLED_AUTH_FAILURE) {
					mWifiDataEntity.disconnect();
					mWifiDataEntity.connect(config, mConnectListener);
				} else {
					WifiConfiguration tmpconfig = getConfig(mSelectedAccessPoint, 
							mItemCurHolder.mItemContentEdit.getText().toString());
					mWifiDataEntity.connect(tmpconfig, mConnectListener);
				}
			} else {
				WifiConfiguration tmpconfig = getConfig(mSelectedAccessPoint, 
						mItemCurHolder.mItemContentEdit.getText().toString());
				mWifiDataEntity.connect(tmpconfig, mConnectListener);
			}
		}
	}
	
	private void save(WifiConfiguration config){
		if (config == null) {
			if (mSelectedAccessPoint != null && mSelectedAccessPoint.networkId != INVALID_NETWORK_ID) {
				Log.i(TAG, "-1--heyf---ssid:" + mSelectedAccessPoint.ssid + " config:"
						+ mSelectedAccessPoint.getConfig().toString());
				mWifiDataEntity.connect(mSelectedAccessPoint.networkId, mConnectListener);
			}
		} else if (config.networkId != INVALID_NETWORK_ID) {
			if (hasPassword(config)) {
				if (config.disableReason != WifiConfiguration.DISABLED_AUTH_FAILURE) {
					mWifiDataEntity.disconnect();
					Log.i(TAG, "-2--heyf---ssid:" + mSelectedAccessPoint.ssid + " config:"
							+ config.ipAssignment + " state:" + config.status);
					mWifiDataEntity.connect(config, mConnectListener);
					mWifiDataEntity.saveConfiguration();
				} else {
					mWifiDataEntity.save(config, mSaveListener);
				}
			}
		} else {
			mWifiDataEntity.save(config, mSaveListener);
		}
		
		if (mWifiDataEntity.isWifiEnabled()) {
			mScanner.forceScan();
		}
		updateAccessPoints();
	}
	
	void forget() {
		connectingWifi = false;
		if (mSelectedAccessPoint.networkId == INVALID_NETWORK_ID) {
			// Should not happen, but a monkey seems to triger it
			Log.e(TAG, "Failed to forget invalid network " + mSelectedAccessPoint.getConfig());
			return;
		}
         
		mWifiDataEntity.forget(mSelectedAccessPoint.networkId, mForgetListener);

		if (mWifiDataEntity.isWifiEnabled()) {
			mScanner.resume();
		}
		updateAccessPoints();
	}
	
	@SuppressLint("NewApi")
	private boolean hasPassword(WifiConfiguration config) {
		boolean hasPassword = false;
		if (config != null) {
			switch (mSelectedAccessPoint.security) {
			case WifiAccessPoint.SECURITY_WEP:
				if(DEBUG){
					Log.i(TAG, "--heyf--password--" + config.wepKeys[0] + "----");
				}
				if (!TextUtils.isEmpty(config.wepKeys[0])) {
					hasPassword = true;
				}
				break;

			case WifiAccessPoint.SECURITY_PSK:
				if(DEBUG){
					Log.i(TAG, "--heyf--passpord--" + config.preSharedKey + "----");
				}
				if (!TextUtils.isEmpty(config.preSharedKey)) {
					hasPassword = true;
				}

				break;

			case WifiAccessPoint.SECURITY_EAP:
				if(DEBUG){
					Log.i(TAG, "--heyf--passpord--" + config.enterpriseConfig.getPassword() + "----");
				}
				//if (!TextUtils.isEmpty(config.password.value())) { //new api
				if (!TextUtils.isEmpty(config.enterpriseConfig.getPassword())) {
					hasPassword = true;
				}
				break;

			default:
				break;
			}
		}
//		Log.v(TAG, "haspassword=="+hasPassword);
		return hasPassword;

	}
	
	@SuppressLint("NewApi")
	private WifiConfiguration getConfig(WifiAccessPoint accessPoint,String password) {
		WifiConfiguration config = mSelectedAccessPoint.getConfig();
		if (config == null) {
			config = new WifiConfiguration();
		}

		if (accessPoint == null) {
			config.SSID = accessPoint.convertToQuotedString(accessPoint.ssid);
			// If the user adds a network manually, assume that it is hidden.
			config.hiddenSSID = true;
		} else if (accessPoint.networkId == WifiConfiguration.INVALID_NETWORK_ID) {
			config.SSID = WifiAccessPoint.convertToQuotedString(accessPoint.ssid);
		} else {
			config.networkId = mSelectedAccessPoint.networkId;
		}

		switch (accessPoint.security) {
		case WifiAccessPoint.SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case WifiAccessPoint.SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (password.length() != 0) {
				int length = password.length();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;

		case WifiAccessPoint.SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			if (password.length() != 0) {
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;

		case WifiAccessPoint.SECURITY_EAP:
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			if (password.length() != 0) {
				//config.password.setValue(password);
				config.enterpriseConfig.setPassword(password);
			}
			break;

		default:
			return null;
		}

		return config;
	}
	
	private void onKeyRight(){
		if(curPingStatus==0){  //0网络通外网，1网络不通外网
			Intent intent = new Intent(WifiConnectActivity.this, WifiModifyActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable(WifiInfoConfigEntity.KEY,getWifiInfoConfig());
			intent.putExtras(bundle);
			intent.putExtra(WifiInfoConfigEntity.WIFI_DISCONNECT_MODIFY_KEY, false);
			startActivityForResult(intent, 0);
		}else if(curPingStatus==1){
			Intent intent = new Intent(WifiConnectActivity.this,WifiDisconnectedActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable(WifiInfoConfigEntity.KEY,getWifiInfoConfig());
			intent.putExtras(bundle);
			startActivityForResult(intent,0);
		}
	}
	
	private void handleEvent(Context context, Intent intent) {
		String action = intent.getAction();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			for (String key : bundle.keySet()) {
//				Log.d(TAG, "---action: " + action + "---key:" + key + "--value:" + bundle.get(key));
			}
		}
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
		} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
				|| WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
				|| WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
//			Log.e(TAG, "#############receive action " + action);
			updateAccessPoints();
		} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
			SupplicantState state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
				updateConnectionState(WifiInfo.getDetailedStateOf(state));
			}
			
			int authState = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
			if (authState == WifiManager.ERROR_AUTHENTICATING) {
				if(DEBUG){
					Log.e(TAG, "auth failed!.........");
				}
				Toast.makeText(WifiConnectActivity.this, 
						getResources().getString(R.string.wifi_password_error), Toast.LENGTH_SHORT).show();
				connectingWifi = false;
				//int networkId = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR_NETWORKID, -1);
				//int networkId = 1;
				//mWifiDataEntity.forget(networkId, mForgetListener);
//				
				if(mWifiDataEntity!=null && mWifiList!=null){
					forgetSucessToast = false;
					mWifiDataEntity.forget(((WifiAccessPoint)(((WifiConnectAdapter)mWifiList.getAdapter()).getItem(1))).networkId, 
							mForgetListener);
				}
				
				updateConnectionState(WifiInfo.getDetailedStateOf(state));
				updateAccessPoints();
				
			}
		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			final List<WifiAccessPoint> accessPoints = constructAccessPoints();
			mAccessPoints = accessPoints;
			for (WifiAccessPoint accessPoint : mAccessPoints) {
				//if (accessPoint.ssid.equals(mSelectedAccessPoint.ssid)) {
				//	break;
				//}
			}
			mConnected.set(info.isConnected());
			updateAccessPoints();
			updateConnectionState(info.getDetailedState());

		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
			updateConnectionState(null);
		}
	}
	
	private WifiInfoConfigEntity getWifiInfoConfig(){
		WifiInfoConfigEntity config = new WifiInfoConfigEntity();
		WifiConfiguration wifiConfig = null;
		
		if(mSelectedAccessPoint!=null && mSelectedAccessPoint.networkId != WifiConfiguration.INVALID_NETWORK_ID){
			wifiConfig = mSelectedAccessPoint.getConfig();
		}
		config.setWifiPointSecurity((mSelectedAccessPoint == null) ? WifiAccessPoint.SECURITY_NONE
				: mSelectedAccessPoint.security);
	
		if(wifiConfig!=null){
			if(wifiConfig.ipAssignment == IpAssignment.STATIC){
				config.setIPv4Assignment(WifiAccessPoint.STATIC);
			}else if(wifiConfig.ipAssignment == IpAssignment.DHCP){
				config.setIPv4Assignment(WifiAccessPoint.DHCP);
			}else{
				config.setIPv4Assignment(WifiAccessPoint.UNASSIGNED);
			}
			
			LinkProperties linkProperties = wifiConfig.linkProperties;
			Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
			if (iterator.hasNext()) {
				LinkAddress linkAddress = iterator.next();
				config.setIPv4IPAddr(linkAddress.getAddress().getHostAddress());
				config.setIPv4NetmaskAddr(Utils.getAddress(NetworkUtils.prefixLengthToNetmaskInt(linkAddress
						.getNetworkPrefixLength())));
			}

			for (RouteInfo route : linkProperties.getRoutes()) {
				if (route.isDefaultRoute()) {
					config.setIPv4GatewayAddr(route.getGateway().getHostAddress());
					break;
				}
			}

			Iterator<InetAddress> dnsIterator = linkProperties.getDnses().iterator();
			if (dnsIterator.hasNext()) {
				config.setIPv4DNSAddr(dnsIterator.next().getHostAddress());
			}
		}
		return config;
	}
	
	private WifiConfiguration getWifiPointConfig(WifiInfoConfigEntity infoConfig){
		WifiConfiguration config = mSelectedAccessPoint.getConfig();
		if (config == null)
			config = new WifiConfiguration();

		if (mSelectedAccessPoint.networkId == INVALID_NETWORK_ID) {
			config.SSID = WifiAccessPoint.convertToQuotedString(mSelectedAccessPoint.ssid);
		} else {
			config.networkId = mSelectedAccessPoint.networkId;
		}

		switch (mSelectedAccessPoint.security) {
		case WifiAccessPoint.SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case WifiAccessPoint.SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			break;

		case WifiAccessPoint.SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			break;

		case WifiAccessPoint.SECURITY_EAP:
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			break;

		default:
			return null;
		}
		int ipv4Assignment = infoConfig.getIPv4Assignment();
		if(ipv4Assignment == WifiAccessPoint.STATIC){
			config.ipAssignment = IpAssignment.STATIC; 
		}else if(ipv4Assignment == WifiAccessPoint.DHCP){
			config.ipAssignment = IpAssignment.DHCP;
		}else{
			config.ipAssignment = IpAssignment.UNASSIGNED;
		}
		mLinkProperties = new LinkProperties();
		updateLinkProperties(infoConfig);
		
		config.linkProperties = new LinkProperties(mLinkProperties);

		return config;
	}
	
	private WifiConfiguration getWifiAddConfig(String ssid,String password,int security){
		WifiConfiguration config = new WifiConfiguration();

		config.SSID = WifiAccessPoint.convertToQuotedString(ssid);
		config.hiddenSSID = true;

		switch (security) {
		case WifiAccessPoint.SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;
		case WifiAccessPoint.SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (password.length() != 0) {
				int length = password.length();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;

		case WifiAccessPoint.SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			if (password.length() != 0) {
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;

		default:
			break;
		}

		return config;
	}
	
	private void updateLinkProperties(WifiInfoConfigEntity config){
		String ipAddr = config.getIPv4IPAddr();
		InetAddress inetAddr = null;
		try {
			if (!TextUtils.isEmpty(ipAddr) && !ipAddr.equals("0.0.0.0")) {
				inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
			} else {
				Log.e(TAG, "ipv4 ip address is invalid");
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + "");
			Log.e(TAG, "ipv4 ip address is invalid...");
		}

		String gateway = config.getIPv4GatewayAddr();
		if (TextUtils.isEmpty(gateway) || gateway.equals("0.0.0.0")) {
			Log.e(TAG, "ipv4 gateway address is invalid");
		} else {
			InetAddress gatewayAddr = null;
			try {
				gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
				mLinkProperties.addRoute(new RouteInfo(gatewayAddr));
			} catch (Exception e) {
				Log.e(TAG, e.getMessage() + "");
				Log.e(TAG, "ipv4 gateway address is invalid...");
			}
		}

		int networkPrefixLength = -1;
		try {
			networkPrefixLength = Utils.getMask(config.getIPv4NetmaskAddr());
			if (networkPrefixLength < 0 || networkPrefixLength > 32) {
				Log.e(TAG, "ipv4 net mask address is invalid");
			} else {
				if (inetAddr != null)
					mLinkProperties.addLinkAddress(new LinkAddress(inetAddr, networkPrefixLength));
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + "");
			Log.e(TAG, "ipv4 net mask address is invalid...");
		}

		String dns = config.getIPv4DNSAddr();
		InetAddress dnsAddr = null;
		if (TextUtils.isEmpty(dns) || dns.equals("0.0.0.0")) {
			Log.e(TAG, "ipv4 dns address is invalid");
		} else {
			try {
				dnsAddr = NetworkUtils.numericToInetAddress(dns);
				mLinkProperties.addDns(dnsAddr);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, e.getMessage() + "");
				Log.e(TAG, "ipv4 net mask address is invalid...");
			}
		}
	}
	
	private void updateConnectionState(DetailedState state) {
		/* sticky broadcasts can call this when wifi is disabled */
		if(mWifiDataEntity == null){
			mWifiDataEntity = WifiDataEntity.getInstance(WifiConnectActivity.this);
		}
		if (!mWifiDataEntity.isWifiEnabled()) {
			mScanner.pause();
			return;
		}

		if (state == DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
		} else {
			mScanner.resume();
		}

		mLastInfo = mWifiDataEntity.getConnectionInfo();
		if (state != null) {
			mLastState = state;
		}

		try {
			for (int i = 0; i < mAccessPoints.size(); i++) {
				if (null != mAccessPoints.get(i)) {
					mAccessPoints.get(i).update(mLastInfo, mLastState);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void updateWifiState(int state) {
		switch (state) {
		case WifiManager.WIFI_STATE_ENABLED:
			Log.d(TAG, "updateWifiState heyf wifi enabled....");
			mScanner.resume();
			return; // not break, to avoid the call to pause() below

		case WifiManager.WIFI_STATE_ENABLING:
			Log.d(TAG, "updateWifiState heyf wifi enabling....");
			break;

		case WifiManager.WIFI_STATE_DISABLED:
			Log.d(TAG, "updateWifiState heyf wifi disabled....");
			break;
		}

		mLastInfo = null;
		mLastState = null;
		mScanner.pause();
	}

	private void updateAccessPoints() {
		final int wifiState = mWifiDataEntity.getWifiState();

		if(mEditTextFocusFlag){
			if(DEBUG){
				Log.e(TAG, "mEditTextFocusFlag ...... flag="+mEditTextFocusFlag);
			}
			return;
		}
		
		switch (wifiState) {
		case WifiManager.WIFI_STATE_ENABLED:
//			Log.d(TAG, "updateAccessPoints heyf wifi enabled....");
			final List<WifiAccessPoint> accessPoints = constructAccessPoints();
			mAccessPoints = accessPoints;
			break;

		case WifiManager.WIFI_STATE_ENABLING:
//			Log.d(TAG, "updateAccessPoints heyf wifi enabling....");
			if (null != mAccessPoints) {
				mAccessPoints.clear();
			}
			break;

		case WifiManager.WIFI_STATE_DISABLING:
//			Log.d(TAG, "updateAccessPoints heyf wifi disableing....");
			break;

		case WifiManager.WIFI_STATE_DISABLED:
//			Log.d(TAG, "updateAccessPoints heyf wifi disabled....");
			break;
		}
	}

	/** Returns sorted list of access points */
	private List<WifiAccessPoint> constructAccessPoints() {
		ArrayList<WifiAccessPoint> accessPoints = new ArrayList<WifiAccessPoint>();
		/**
		 * Lookup table to more quickly update AccessPoints by only considering
		 * objects with the correct SSID. Maps SSID -> List of AccessPoints with
		 * the given SSID.
		 */
		Multimap<String, WifiAccessPoint> apMap = new Multimap<String, WifiAccessPoint>();

		final List<WifiConfiguration> configs = mWifiDataEntity.getConfiguredNetworks();
		if (configs != null) {
		}

		final List<ScanResult> results = mWifiDataEntity.getScanResults();
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
					continue;
				}

				boolean found = false;
				for (WifiAccessPoint accessPoint : apMap.getAll(result.SSID)) {
					if (accessPoint.update(result))
						found = true;
				}
			}
		}

		// Pre-sort accessPoints to speed preference insertion
		Collections.sort(accessPoints);

		return accessPoints;
	}

	private class Multimap<K, V> {
		private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

		/** retrieve a non-null list of values with key K */
		List<V> getAll(K key) {
			List<V> values = store.get(key);
			return values != null ? values : Collections.<V> emptyList();
		}

		void put(K key, V val) {
			List<V> curVals = store.get(key);
			if (curVals == null) {
				curVals = new ArrayList<V>(3);
				store.put(key, curVals);
			}
			curVals.add(val);
		}
	}

	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void forceScan() {
			removeMessages(0);
			sendEmptyMessage(0);
		}

		void pause() {
			mRetry = 0;
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {
			if (mWifiDataEntity.startScanActive()) {
				mRetry = 0;
			} else if (++mRetry >= 3) {
				mRetry = 0;
				Toast.makeText(WifiConnectActivity.this, R.string.wifi_fail_to_scan, Toast.LENGTH_LONG).show();
				return;
			}
			sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
		}
	}


	class WifiConnectAdapter extends BaseAdapter {
		LayoutInflater mInflater = null;
		private Context mContext = null;
		List<WifiAccessPoint> mList = null;
		int index = 0;

		public WifiConnectAdapter(Context context, List<WifiAccessPoint> wifiList) {
			mContext = context;
			if (wifiList == null) {
				mList = new ArrayList<WifiAccessPoint>();
			} else {
				mList = wifiList;
				WifiConfiguration config = new WifiConfiguration();
				config.SSID = context.getResources().getString(R.string.wifi_manual_add);
			}
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public void setIndex(int selected) {
			index = selected;
		}

		public void notifyData(List<WifiAccessPoint> wifiList) {
			mList.clear();
			mList.addAll(wifiList);

			WifiConfiguration config = new WifiConfiguration();
			config.SSID = mContext.getResources().getString(R.string.wifi_manual_add);

			notifyDataSetChanged();
		}

		public void notifyState() {
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			WifiItemHolder holder = null;
			if (convertView == null) {
				holder = new WifiItemHolder();
				convertView = mInflater.inflate(R.layout.wifi_item_list, null);
				holder.mItemTitle = (TextView) convertView.findViewById(R.id.wifi_item_title);
				holder.mItemSecurityImg = (ImageView) convertView.findViewById(R.id.wifi_item_security_img);
				holder.mItemContentEdit = (EditText) convertView.findViewById(R.id.wifi_item_password_edit);
				holder.mItemSignalImg = (ImageView) convertView.findViewById(R.id.wifi_item_signal_img);
				holder.mItemMoreImg = (ImageView) convertView.findViewById(R.id.wifi_item_more_img);
				holder.mItemSignalStateImg = (ImageView)convertView.findViewById(R.id.wifi_item_signal_state_img);
				convertView.setTag(holder);
			} else {
				holder = (WifiItemHolder) convertView.getTag();
			}
			
			String pointState = "";
			final WifiAccessPoint accessPoint = mList.get(position);
			
			int wifiLevelInt = accessPoint.getLevel();
			switch (accessPoint.wifiConnState) {
			case WifiAccessPoint.STATE_CONNECTING:
				pointState = accessPoint.pointState;
				connectingWifi = true;
				mSelectedAccessPoint = accessPoint;    //增加动态调整，因为断开网络的时候，无法确定那个是已经被选择的SSID
				break;
			case WifiAccessPoint.STATE_CONNECTED:
				holder.mItemMoreImg.setVisibility(View.VISIBLE);
				pointState = accessPoint.pointState;
				if (mErrNetId == accessPoint.networkId) {
					mErrNetId = -1;
				}
				break;
			case WifiAccessPoint.STATE_ERROR:
//				Log.d(TAG, "---heyf-ssid:" + accessPoint.ssid + "--iserrshow:" + accessPoint.isERRShow
//						+ "------pointState:" + accessPoint.pointState);
				if (accessPoint.isERRShow) {
					pointState = accessPoint.pointState;
				} else {
					pointState = "";
				}
                connectingWifi = false;
				mErrNetId = accessPoint.networkId;
				break;
			default:
				pointState = "";
				break;
			}
			
			//初始左边安全状态
			if(accessPoint.security == WifiAccessPoint.SECURITY_NONE){
				holder.mItemSecurityImg.setImageDrawable(null);
			}else{
				holder.mItemSecurityImg.setImageResource(R.drawable.wifi_state_locked);
			}
			
			holder.mItemSignalImg.setTag(position);
			if (position == 0) {
				holder.mItemSignalImg.setImageBitmap(null);
				holder.mItemMoreImg.setVisibility(View.INVISIBLE);
				holder.mItemSecurityImg.setImageResource(R.drawable.wifi_state_add);
				holder.mItemSignalImg.setVisibility(View.GONE);
			}else if(position ==1){
				holder.mItemSignalImg.setVisibility(View.VISIBLE);
				holder.mItemSignalImg.setBackgroundResource(R.drawable.wifi_connect_progress);

				AnimationDrawable animationDrawable = (AnimationDrawable)holder.mItemSignalImg.getBackground();  	          
				if (mSelectedAccessPoint!=null && accessPoint.ssid.equals(mSelectedAccessPoint.ssid) 
					&& accessPoint.wifiConnState!=WifiAccessPoint.NONE 
					&& accessPoint.wifiConnState != WifiAccessPoint.STATE_CONNECTED 
					&& connectingWifi) {

					if(!animationDrawable.isRunning()){
		                animationDrawable.start();  
		            }
				} else {
					if(animationDrawable.isRunning()){  
		                animationDrawable.stop();  
		                holder.mItemSignalImg.setBackgroundResource(android.R.color.transparent);
		            }
				}				
				
				if(accessPoint.wifiConnState == WifiAccessPoint.STATE_CONNECTED){
					//右边的箭头状态
					holder.mItemMoreImg.setVisibility(View.VISIBLE);
					//右边的ping外网状态
					holder.mItemSignalStateImg.setVisibility(curPingStatus==0?View.INVISIBLE:View.VISIBLE);
					//wifi信号
					holder.mItemSignalImg.setImageLevel(accessPoint.getLevel());
					holder.mItemSignalImg.setImageResource(R.drawable.wifi_signal_selected);
					
					//左边wifi安全为勾选，已经连接上状态
					holder.mItemSecurityImg.setImageResource(R.drawable.wifi_state_connected);
				}else {
					//右边的箭头状态
					holder.mItemMoreImg.setVisibility(View.INVISIBLE);
					//右边的ping外网状态
					holder.mItemSignalStateImg.setVisibility(View.INVISIBLE);
					
					//wifi信号
					holder.mItemSignalImg.setImageLevel(accessPoint.getLevel());
					holder.mItemSignalImg.setImageResource(R.drawable.wifi_signal_unselected);
					
					//左边wifi安全状态//顶部已经初始化了
				}

			}else {
				holder.mItemSignalImg.setVisibility(View.VISIBLE);
				holder.mItemSignalStateImg.setVisibility(View.INVISIBLE);
				holder.mItemMoreImg.setVisibility(View.INVISIBLE);
				holder.mItemSignalImg.setBackgroundResource(R.drawable.wifi_connect_progress);
				AnimationDrawable animationDrawable = (AnimationDrawable)holder.mItemSignalImg.getBackground();  
	            if(animationDrawable.isRunning()){  
	                animationDrawable.stop();  
	                holder.mItemSignalImg.setBackgroundResource(android.R.color.transparent);
	            }	
				
	            //右边的wifi信号图标
				if(wifiLevelInt!=-1){
					holder.mItemSignalImg.setImageLevel(wifiLevelInt);
					holder.mItemSignalImg.setImageResource(R.drawable.wifi_signal_unselected);
				}else {
					holder.mItemSignalImg.setImageDrawable(null);
				}
				
			}
			
			holder.mItemTitle.setText(getSSIDAndState(accessPoint.ssid, 
					pointState,accessPoint.wifiConnState));
			return convertView;
		}

	}

	String getSSIDAndState(String ssid, String pointState,int wifiConnState) {
		String text = null;
		if(wifiConnState == WifiAccessPoint.STATE_CONNECTED){
			text = ssid + getResources().getString(R.string.wifi_connected_success);
		}else{
			text  =  ssid + " " + pointState;
		}
		return text;
	}

	private void setWifiConnectState() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = WIFI_CONNECT_STATE_MSG;
				if (pingStatus()) {
					msg.arg1 = 0;
				} else {
					msg.arg1 = 1;
				}
				SettingsApplication.getInstance().setPingStatus(msg.arg1);
				curPingStatus = msg.arg1;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	private void startProgressDialog(String msg) {
		if (mProgressDialog == null) {
			mProgressDialog = new CustomProgressDialog(WifiConnectActivity.this);
		}
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
		mProgressDialog.startLoading();
	}

	private void stopProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.stopLoading();
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	
	private void regeditReceiver() {
		IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.addAction(EthernetManager.ETH_STATE_CHANGED_ACTION);
        mFilter.addAction("com.hiveview.cloudtv.settings.wifi.modify.save");
        mFilter.addAction("android.net.pppoe.PPPOE_STATE_CHANGED");
        registerReceiver(mReceiver, mFilter);
	}
	
	private boolean isWaiting = true;
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            Log.v("aa", action+"==state=="+Utils.checkEthState());
            if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
            	Log.v(TAG, "network change>>>>>"+Utils.checkNetworkIsActive(WifiConnectActivity.this));
            	setWifiConnectState();
            	//如果是开机向导执行的，连接上ap,就直接关闭当前窗口
            	 
    			if(!isNativeGo && Utils.checkNetworkIsActive(WifiConnectActivity.this)){
    				finish();
    			}
            }else if(action.equals("com.hiveview.cloudtv.settings.wifi.modify.save")){
            	Log.v(TAG, "get wifi modify message");
            	boolean saveFlag = intent.getBooleanExtra(WifiInfoConfigEntity.SAVE_CONFIG, false);
          	    if(saveFlag){
          		  WifiInfoConfigEntity dataConfig = intent.getParcelableExtra(WifiInfoConfigEntity.KEY);
          		  WifiConfiguration wifiConfig = getWifiPointConfig(dataConfig);
          		  save(wifiConfig);
          	   }
            }
            if(Utils.checkEthState()){
            	if(isWaiting){
            		isWaiting = false;
            		jump2Network();
            	}
			}
        }
    };
    
    private void unRegeditReceiver(){
    	unregisterReceiver(mReceiver);
    }
    
    private void jump2Network(){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000); 
					if(WifiConnectActivity.this.isFinishing()){
//						Log.v("aa", ">>>finish");
						return;
					}
					
					
					
					if(pingStatus()){
            		   ConnectivityManager connectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                       NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
                       if(info.isConnected())
            		      jump2Ethernet();
            	    }else {
					   jump2DisEthernet();
            	    }
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
		
	}
    
    private void jump2DisEthernet(){
		Intent intent = new Intent(this, EthernetDisconnectedActivity.class);
		intent.putExtra("isNativeGo", isNativeGo);
		startActivity(intent);
		finish();
	}
    
    private void jump2Ethernet(){
		Intent intent = new Intent(this, EthernetConnectedAcivity.class);
		intent.putExtra("isNativeGo", isNativeGo);
		startActivity(intent);
		finish();
	}
    
 
    private boolean pingStatus(){
    	boolean isConnected = false;
    	try {
    		HttpURLConnection conn = null;
    		URL url = new URL("http://www.baidu.com");
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setConnectTimeout(2000);
    		conn.setReadTimeout(2000);
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			isConnected = true; 
    		} else {
    			isConnected = false;
    		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return isConnected;
    }
}
