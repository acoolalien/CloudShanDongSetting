package com.shandong.cloudtv.settings;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shandong.cloudtv.settings.bluetooth.BluetoothCallback;
import com.shandong.cloudtv.settings.bluetooth.BluetoothEventManager;
import com.shandong.cloudtv.settings.bluetooth.BluetoothListAdapter;
import com.shandong.cloudtv.settings.bluetooth.BluetoothScan;
import com.shandong.cloudtv.settings.bluetooth.CachedBluetoothDevice;
import com.shandong.cloudtv.settings.bluetooth.CachedBluetoothDeviceManager;
import com.shandong.cloudtv.settings.bluetooth.LocalBluetoothAdapter;
import com.shandong.cloudtv.settings.bluetooth.LocalBluetoothManager;
import com.shandong.cloudtv.settings.bluetooth.LocalBluetoothProfileManager;
import com.shandong.cloudtv.settings.widget.CloundMenuWindow;
import com.shandong.cloudtv.settings.widget.CommonListView;
import com.shandong.cloudtv.settings.widget.ConnectDialog;
import com.shandong.cloudtv.settings.widget.CustomProgressDialog;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.CloundMenuWindow.MenuItemEntity;
import com.shandong.cloudtv.settings.widget.CloundMenuWindow.OnSelectedItemClickListener;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

public class BlueToothActivity extends Activity implements BluetoothCallback {
	private String TAG = "BlueToothActivity";
	private int mBlueToothSwitch = 0;
	// public BluetoothAdapter adapter;
	private int mCanSeeOrNotId;
	private Context mContext = this;
	private int UNPAIR = 999;
	private CommonListView mListView;
	private static final int NOTIFICATION_ID = android.R.drawable.stat_sys_data_bluetooth;

	public LocalBluetoothManager mLocalManager;
	public LocalBluetoothAdapter mLocalAdapter;
	public LocalBluetoothProfileManager mLocalProfileManager;
	public CachedBluetoothDeviceManager mCachedDeviceManager;
	public BluetoothEventManager mEventManager;
	public BluetoothDevice mDevice;
	private BluetoothScan scan1 = new BluetoothScan();
	private BluetoothScan scan2 = new BluetoothScan();
	private BluetoothScan scan4 = new BluetoothScan();
	private BluetoothScan mDiscoverScan = new BluetoothScan();
	public BluetoothListAdapter mListViewAdapter;
	public ListView listView;
	public ViewHolder viewHolder = new ViewHolder();
	public BluetoothScan mBluetoothScanItem = new BluetoothScan();
	public List<BluetoothScan> list = new ArrayList<BluetoothScan>();
	public final static int BLUETOOTH_CONNECTED = 0;
	public final static int BLUETOOTH_BONDED = 1;
	public final static int BLUETOOTH_NOBOND = 6;
	public final static int BLUETOOTH_CONNECTING = 2;
	public final static int BLUETOOTH_DISCONNECTING = 3;
	public final static int BLUETOOTH_DISCONNECTED = 4;
	public final static int BLUETOOTH_BONDING = 5;
	public final static int BLUETOOTH_DISCOVERING = -1;
	public final static int BLUETOOTH_NO_DISCOVER = -2;
	public final static int BLUETOOTH_DEVICE_ON_OFF = -4;
	public final static int BLUETOOTH_DEVICE_CAN_SEE_OR_NOT = -3;
	public int itemPosition =0;
	
	private LauncherFocusView focusView = null;
	private boolean isFirstInit = true;
	private CustomProgressDialog mCustomProgressDialog = null;
	private int mItemListCurPosition = -1;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private TextView mTextView = null;
	private TextView mTextViewSetting = null;
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	private boolean mDisapp = false;
	private boolean mMenuSwitch = false;
	final WeakHashMap<CachedBluetoothDevice, BluetoothScan> mDevicePreferenceMap = new WeakHashMap<CachedBluetoothDevice, BluetoothScan>();
    private String mItemClickAddress = null;
    private int offsetY = 106;
	
	
	class ViewHolder {
		TextView itemName;
		TextView bluetoothAddress;
		TextView itemSetting;
	}
	
	//true为一体机，false为分体机
	private boolean SeparateOrIntegrade(){
		String model = null;
		if(null!=model&&!"".equals(model)){
			if(model.indexOf("32")!=-1){
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected void onDestroy() {
		// unregisterReceiver(connectChange);

		if (mReceiver != null)
			unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocalManager == null)
			return;

		// removeAllDevices();
		// mLocalManager.setForegroundActivity(null);
		mLocalManager.getEventManager().unregisterCallback(this);
		mLocalAdapter.stopScanning();
	}

	@Override
	protected void onResume() {
		addCachedDevices();
		mLocalManager.getEventManager().registerCallback(this);
		super.onResume();
	}

	void dismissDialog() {
		if (mCustomProgressDialog != null) {
			mCustomProgressDialog.stopLoading();
			mCustomProgressDialog.dismiss();
			mCustomProgressDialog = null;
		}
	}

	void showDialog() {
		mCustomProgressDialog = new CustomProgressDialog(mContext);
		mCustomProgressDialog.setCancelable(true);
		mCustomProgressDialog.show();
		mCustomProgressDialog.startLoading();
	}

	void addCachedDevices() {
		Collection<CachedBluetoothDevice> cachedDevices = mLocalManager.getCachedDeviceManager()
				.getCachedDevicesCopy();

		for (CachedBluetoothDevice cachedDevice : cachedDevices) {
			onDeviceAdded(cachedDevice);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blue_tooth);
		initView();
		initData();
		test();	
		this.regReceiver();
		handler.sendEmptyMessageDelayed(1024, 8000);
	}

	private void initDiscoveryScan() {
		mDiscoverScan.setName(mContext.getResources()
				.getString(R.string.bluetooth_discovery_device));
		mDiscoverScan
				.setStatus(mLocalAdapter.isDiscovering() ? BlueToothActivity.BLUETOOTH_DISCOVERING
						: BlueToothActivity.BLUETOOTH_NO_DISCOVER);
	}

	private void initData() {
		mLocalManager = LocalBluetoothManager.getInstance(this);
		mLocalAdapter = mLocalManager.getBluetoothAdapter();
		mLocalProfileManager = mLocalManager.getProfileManager();
		mCachedDeviceManager = mLocalManager.getCachedDeviceManager();
		mEventManager = mLocalManager.getEventManager();
		initScan();
		scan1.setName(mContext.getResources().getString(R.string.blue_teeth_switch));
		scan1.setStatus(BlueToothActivity.BLUETOOTH_DEVICE_ON_OFF);
		scan1.setPageleft(mContext.getResources().getDrawable(R.drawable.page_left));
		scan1.setPageright(mContext.getResources().getDrawable(R.drawable.page_right));
		scan2.setName(mContext.getResources().getString(R.string.see_or_ort));
		scan2.setStatus(BlueToothActivity.BLUETOOTH_DEVICE_CAN_SEE_OR_NOT);
		scan2.setPageleft(mContext.getResources().getDrawable(R.drawable.page_left));
		scan2.setPageright(mContext.getResources().getDrawable(R.drawable.page_right));
		list.add(scan1);
		scan4 = scan2;
		initDiscoveryScan();
		if (mLocalAdapter.getBluetoothState() == BluetoothAdapter.STATE_ON) {
			list.add(scan2);
			list.add(mDiscoverScan);
		}

	}

	private void threeDimenItemListInit(List<BluetoothScan> list) {
		if (mListViewAdapter == null) {
			mListViewAdapter = new BluetoothListAdapter(this, list);
			mListView.setAdapter(mListViewAdapter);
		} else {
			mListViewAdapter.notifyDataSetChanged();

		}
	}

	// 设置蓝牙的可见性
	private void setCanSeeOrNot() {
		if (mCanSeeOrNotId == 0) {
			if (mLocalAdapter.isEnabled())
				setDiscoverableTimeout(300);
		} else {
			if (mLocalAdapter.isEnabled())
				closeDiscoverableTimeout();
		}
	}

	public void test() {
		mLocalAdapter.startScanning(true);
		Set<BluetoothDevice> templist = mLocalAdapter.getBondedDevices();
		if (templist.size() == 0 && mLocalAdapter.isEnabled()) {
		//	showDialog();
		}
		initBluetoothAdapter();
		upgradeUI();
		mListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				int selectItem = mItemListCurPosition;
				mCurKeycode = keyCode;
				// 对解配对做处理
				if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN
						&& mMenuSwitch) {
					View x = mListView.getSelectedView();
					TextView tv = (TextView) x.findViewById(R.id.item_setting);

					BluetoothScan unpair = new BluetoothScan();
					unpair = (BluetoothScan) mListView.getItemAtPosition(selectItem);

					final CachedBluetoothDevice temp = unpair.getCachedDevice();
					if ((!temp.isConnected()) && temp.getBondState() == BluetoothDevice.BOND_BONDED) {

						MenuItemEntity entity = new MenuItemEntity();
						entity.setItemName(mContext.getResources().getString(
								R.string.bluetooth_unpair));
						entity.setItemIconFocusResId(R.drawable.setting_focus);
						entity.setItemPosition(UNPAIR);
						entity.setItemIconResId(R.drawable.setting_focus);

						List<MenuItemEntity> menuList = new ArrayList<CloundMenuWindow.MenuItemEntity>();
						menuList.add(entity);
						CloundMenuWindow window = new CloundMenuWindow(mContext, menuList);

						window.setItemSelectedListener(new OnSelectedItemClickListener() {

							@Override
							public void selectedItemClick(MenuItemEntity entity) {
								// TODO Auto-generated method stub
								Log.i("YQB", entity.getItemName());
								if (temp.isConnected()) {
									temp.disconnect();
								}
								temp.unpair();
								

							}
						});
						window.show();
					}

				}

				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (event.getRepeatCount() == 0) {
						mTextColorChangeFlag = true;
					} else {
						mTextColorChangeFlag = false;
					}

					if (!mFocusAnimationEndFlag) {
						mTextColorChangeFlag = false;
					}
				} else if (event.getAction() == KeyEvent.ACTION_UP) {
					if (!mTextColorChangeFlag) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}

				if ((selectItem == 0 || selectItem == 1)
						&& (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
					View x = mListView.getSelectedView();
					if(x==null)
						return true;
					TextView tv = (TextView) x.findViewById(R.id.item_setting);
					ImageView imageRightTemp = (ImageView) x.findViewById(R.id.page_right);
					ImageView imageLeftTemp = (ImageView) x.findViewById(R.id.page_left);
					if (event.getAction() == KeyEvent.ACTION_DOWN) {

						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right_selected));

							break;
						}
						case KeyEvent.KEYCODE_DPAD_LEFT: {
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left_selected));

							break;
						}
						}
					} else if (event.getAction() == KeyEvent.ACTION_UP) {

						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							imageRightTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_right));
							if (selectItem == 0) {
								if(mCustomProgressDialog != null)
									return false;
								// Log.i("YQB", "--------FJF-------");
								mBlueToothSwitch = (mBlueToothSwitch + 1) % 2;
								tv.setText(CommonActivity.getStringArrays(mContext,
										R.array.open_close, mBlueToothSwitch));

								if (mBlueToothSwitch == 1) {
									mLocalAdapter.setBluetoothEnabled(false);
									list.get(0).setAddress(
											mContext.getResources().getString(R.string.close));
									showDialog();
								} else if (mBlueToothSwitch == 0) {
									// mLocalAdapter.enable();
									mLocalAdapter.setBluetoothEnabled(true);
									showDialog();
									list.get(0).setAddress(
											mContext.getResources().getString(R.string.open));

								}
							} else if (selectItem == 1) {
								mCanSeeOrNotId = (mCanSeeOrNotId + 1) % 2;
								tv.setText(CommonActivity.getStringArrays(mContext,
										R.array.can_see_or_not, mCanSeeOrNotId));
								savePreference(mCanSeeOrNotId);

								if (mCanSeeOrNotId == 0) {
									// setDiscoverableTimeout(300);
									setCanSeeOrNot();
									list.get(1).setAddress(
											mContext.getResources().getString(R.string.can_see));
								}
								if (mCanSeeOrNotId == 1) {
									// closeDiscoverableTimeout();
									setCanSeeOrNot();
									list.get(1)
											.setAddress(
													mContext.getResources().getString(
															R.string.can_not_see));
								}
							}
							break;
						}
						case KeyEvent.KEYCODE_DPAD_LEFT: {
							imageLeftTemp.setImageDrawable(mContext.getResources().getDrawable(
									R.drawable.page_left));
							if (selectItem == 0) {
								if(mCustomProgressDialog != null)
									return false;
								mBlueToothSwitch = (mBlueToothSwitch + 1) % 2;
								tv.setText(CommonActivity.getStringArrays(mContext,
										R.array.open_close, mBlueToothSwitch));
								if (mBlueToothSwitch == 1) {
									mLocalAdapter.disable();
									list.get(0).setAddress(
											mContext.getResources().getString(R.string.close));
									showDialog();
								} else if (mBlueToothSwitch == 0) {
									mLocalAdapter.enable();
									showDialog();
									list.get(0).setAddress(
											mContext.getResources().getString(R.string.open));
								}
							} else if (selectItem == 1) {
								mCanSeeOrNotId = (mCanSeeOrNotId + 1) % 2;
								tv.setText(CommonActivity.getStringArrays(mContext,
										R.array.can_see_or_not, mCanSeeOrNotId));
								savePreference(mCanSeeOrNotId);
								if (mCanSeeOrNotId == 0) {
									// setDiscoverableTimeout(300);
									setCanSeeOrNot();
								}
								if (mCanSeeOrNotId == 1) {
									// closeDiscoverableTimeout();
									setCanSeeOrNot();
								}
							}
							break;
						}

						}

					}

				}

				return false;
			}

		});
		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> paramAdapterView, View view, int position,
					long paramLong) {
				Log.i(TAG, "-----position---"+position+"-----first view position----");
				itemPosition = position;
				mItemListCurPosition = position;
				//focusView.initFocusView(view, false, 0);
				if (isFirstInit) {
				    focusView.initFocusViewNew(view, false, 0);
				}
				if(mDisapp){
					Log.i(TAG, "-----onItemselect---"+mDisapp);
					focusView.initFocusViewNew(view, false, 0);
					mDisapp=false;
				}
				
				if (mTextView != null) {
					mTextView.setTextColor(mContext.getResources().getColor(R.color.grey5_color));
				}
				if (mTextViewSetting != null) {
					mTextViewSetting.setTextColor(mContext.getResources().getColor(
							R.color.grey5_color));
				}
				mTextView = (TextView) view.findViewById(R.id.item_name);
				mTextViewSetting = (TextView) view.findViewById(R.id.item_setting);
				if (isFirstInit) {
					isFirstInit = false;
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
				if (mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN) {

					if (position < 5
							|| position > mListView.getCount() - 2
							|| (mListView.getFirstVisiblePosition() == 0 && view.getTop() < (view
									.getHeight() * 4))
							|| (mListView.getFirstVisiblePosition() != 0 && view.getTop() < view
									.getHeight() * 5)) {
						
						//Log.i(TAG, "------move to------down");
						focusView.moveTo(view);
					} else {
						listTextColorSet();
						mListView.setSelectionFromTop(position, view.getTop() - view.getHeight());

					}
				} else if (mCurKeycode == KeyEvent.KEYCODE_DPAD_UP) {
					if ((mItemListCurPosition == 0 || mListView.getFirstVisiblePosition() == 0
							&& view.getTop() >= (view.getHeight()))
							|| (mListView.getFirstVisiblePosition() != 0 && view.getTop() >= view
									.getHeight())) {
						//Log.i(TAG, "------move to------up");
						focusView.moveTo(view);
					} else {
						listTextColorSet();
						mListView.setSelectionFromTop(mItemListCurPosition, view.getHeight());
					}

				} else if (mCurKeycode == KeyEvent.KEYCODE_PAGE_UP
						|| mCurKeycode == KeyEvent.KEYCODE_PAGE_DOWN) {
					focusView.moveTo(view);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {

			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView, View view, int position,
					long paramLong) {
				if (position != 0 && position != 1 && position != 2) {
					
					mBluetoothScanItem = (BluetoothScan) paramAdapterView.getAdapter().getItem(
							position);
					CachedBluetoothDevice temp = mBluetoothScanItem.getCachedDevice();
					int bondState = temp.getBondState();
					mLocalAdapter.stopScanning();
					if (temp.isConnected()) {
						mBluetoothScanItem.askConnect();
						//mItemClickAddress= mBluetoothScanItem.getAddress();
					} else if (bondState == BluetoothDevice.BOND_BONDED) {
						// temp.connect(true);
						showBluetoothDialog(position);
					} else if (bondState == BluetoothDevice.BOND_NONE) {
						mBluetoothScanItem.pair();
						//mItemClickAddress= mBluetoothScanItem.getAddress();
					}
					
				}
				if (position == 1) {
					//focusView.initFocusView(mListView.getSelectedView(), false, 0);
					//mListView.setSelection(3);
				}
				if (position == 2) {
					if (mLocalAdapter.getBluetoothState() == BluetoothAdapter.STATE_ON) {
						mLocalAdapter.startScanning(true);
					}
				}
			}
		});

	}

	@SuppressLint("NewApi")
	private void initView() {
		focusView = (LauncherFocusView) findViewById(R.id.activity_three_dimen_focusview);
		focusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

			@Override
			public void OnAnimateStart(View currentFocusView) {

				mFocusAnimationEndFlag = false;
			}

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				mFocusAnimationEndFlag = true;
				listTextColorSet();
				// fixed the keyboard repeat mode
				if (!mTextColorChangeFlag) {
					if ((mItemListCurPosition == 0 || mItemListCurPosition == mListView.getCount() - 1)) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}
			}
		});
		mListView = (CommonListView) findViewById(R.id.line3);

	}

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

	private void showBluetoothDialog(int position) {
		final ConnectDialog mDialog = new ConnectDialog(mContext);
		mDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = 1025;
		lp.height = 338;
		dialogWindow.setAttributes(lp);
		mDialog.show();
		Button button1 = (Button) mDialog.findViewById(R.id.button1);
		Button button2 = (Button) mDialog.findViewById(R.id.button2);
		final CachedBluetoothDevice temp = mBluetoothScanItem.getCachedDevice();
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				temp.unpair();
				//mItemClickAddress = temp.getDevice().getAddress();
				mDialog.dismiss();
			}

		});
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				temp.connect(true);
				//mItemClickAddress = temp.getDevice().getAddress();
				mDialog.dismiss();
			}
		});
	}

	private void savePreference(int id) {
		SharedPreferences canSeeOrNot = getSharedPreferences(TAG, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = canSeeOrNot.edit();
		editor.putInt("canSeeOrNot", id);
		editor.commit();
	}

	private int readPreference() {
		SharedPreferences canSeeOrNot = getSharedPreferences(TAG, mContext.MODE_PRIVATE);
		int a = canSeeOrNot.getInt("canSeeOrNot", 1);
		return a;
	}

	private void initScan() {
		// 设定页面初始化时开关的值
		if (mLocalAdapter.isEnabled()) {
			scan1.setAddress(mContext.getResources().getString(R.string.open));
			mBlueToothSwitch = 0;
		} else {
			scan1.setAddress(mContext.getResources().getString(R.string.close));
			mBlueToothSwitch = 1;
		}
		mCanSeeOrNotId = readPreference();
		this.setCanSeeOrNot();
		scan2.setAddress(CommonActivity.getStringArrays(mContext, R.array.can_see_or_not,
				mCanSeeOrNotId));
	}

	private void initBluetoothAdapter() {

		if (mLocalAdapter == null) {
			System.out.println("Can't find bluetooth adapter");
			return;
		} else {
			initScan();
		}
	}

	// 一直保持可见性的方法，timeout值不会生效的
	public void setDiscoverableTimeout(int timeout) {
		try {
			BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
			Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod(
					"setDiscoverableTimeout", int.class);
			setDiscoverableTimeout.setAccessible(true);
			Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class,
					int.class);
			setScanMode.setAccessible(true);

			setDiscoverableTimeout.invoke(madapter, timeout);
			setScanMode.invoke(madapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,
					timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 设置设备不可见
	public void closeDiscoverableTimeout() {
		BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();
		try {
			Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod(
					"setDiscoverableTimeout", int.class);
			setDiscoverableTimeout.setAccessible(true);
			Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode", int.class,
					int.class);
			setScanMode.setAccessible(true);

			setDiscoverableTimeout.invoke(madapter, 1);
			setScanMode.invoke(madapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void regReceiver() {
		// 注册Receiver来获取蓝牙设备相关的结果
		IntentFilter intent = new IntentFilter();
		intent.addAction("com.hiveview.cloudtv.settings.bluetoothchange");// 用BroadcastReceiver来取得搜索结果

		registerReceiver(mReceiver, intent);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context paramContext, Intent paramIntent) {
			// TODO Auto-generated method stub
			if (paramIntent.getAction().equals("com.hiveview.cloudtv.settings.bluetoothchange")) {
				upgradeUI();
			}
		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == 1024) {
				dismissDialog();
				Log.i("YQB", "----handler----msg====1024");
			}
			if (msg.what == 1026) {
				if (mLocalAdapter.isEnabled() && (!mLocalAdapter.isDiscovering())) {
					Log.i("YQB", "----handler----msg====1026");
				}
				addCachedDevices();
			}
		}
	};

	@Override
	public void onBluetoothStateChanged(int bluetoothState) {
		// TODO Auto-generated method stub
		if (bluetoothState == BluetoothAdapter.STATE_ON) {
			Log.i(TAG, "-----bluetooth Adapter-----STATE_ON");
			list.add(scan4);
			initDiscoveryScan();
			list.add(mDiscoverScan);
			upgradeUI();
			addCachedDevices();
			// 启动后，重新设定可见性
			setCanSeeOrNot();
			handler.sendEmptyMessageDelayed(1024, 8000);
			handler.sendEmptyMessage(1026);

		} else if (bluetoothState == BluetoothAdapter.STATE_OFF) {
			Log.i(TAG, "-----bluetooth Adapter-----STATE_OFF");
			mDevicePreferenceMap.clear();
			BluetoothScan scan3 = list.get(0);
			scan4 = list.get(1);
			list.clear();
			list.add(scan3);
			upgradeUI();
			dismissDialog();
		}

	}

	@Override
	public void onScanningStateChanged(boolean started) {
		// TODO Auto-generated method stub
		boolean mDiscoverying = mLocalAdapter.isDiscovering();
		Log.i("YQB", "-------DiscoveryState-------" + mDiscoverying);
		initDiscoveryScan();
		upgradeUI();

	}

	@Override
	public void onDeviceAdded(CachedBluetoothDevice cachedDevice) {
		// TODO Auto-generated method stub
		String name = cachedDevice.getDevice().getName();
		if (null != name && "CLOUDSCREEN".equals(name)&&!SeparateOrIntegrade()) {
			return;
		}
		Log.i("YQB", "----the Add Device name----CLOUDSCREEN" + name);
		if (mDevicePreferenceMap.get(cachedDevice) != null) {
			return;
		}

		dismissDialog();
		// Prevent updates while the list shows one of the state messages
		if (mLocalAdapter.getBluetoothState() != BluetoothAdapter.STATE_ON)
			return;
		createBluetoothScan(cachedDevice);
	}

	private void createBluetoothScan(CachedBluetoothDevice cachedDevice) {
		BluetoothScan scan = new BluetoothScan(mContext, cachedDevice);
		list.add(scan);
		upgradeUI();
		mDevicePreferenceMap.put(cachedDevice, scan);
	}

	private void upgradeUI() {
		//mDisapp = false;
		Collections.sort(list);		
		threeDimenItemListInit(list);
		/*if(null != mItemClickAddress&&!"".equals(mItemClickAddress)){
			int temp = findTheOrder();
			if(temp != 0&&(itemPosition!=temp)){
				
				mDisapp = true;
			mListView.setSelection(temp);
			mItemClickAddress = null;
			Log.i(TAG, "---upgradeUI---temp="+temp+"----mDisapp---"+mDisapp);
			}
		}*/
		
	}
	
	private int findTheOrder(){
		for(int i = 0;i<list.size();i++){
		   String temp = list.get(i).getAddress();
		   if(null != temp && !"".equals(temp)){
			   if(mItemClickAddress.equals(temp))
				   return i;
		   }
		}
		return 0;
	}

	@Override
	public void onDeviceDeleted(CachedBluetoothDevice cachedDevice) {
		// TODO Auto-generated method stub
		Log.i("YQB", "-----disapperare-----");
		BluetoothScan scan = mDevicePreferenceMap.get(cachedDevice);
		mDevicePreferenceMap.remove(cachedDevice);
		list.remove(scan);
		Log.i("YQB", "-----disapperare-----" + list.size());
		upgradeUI();
	}

	private void removeAllDevice() {

		BluetoothScan scan1 = list.get(0);
		BluetoothScan scan2 = list.get(1);
		initDiscoveryScan();
		mLocalAdapter.stopScanning();
		mDevicePreferenceMap.clear();
		list.clear();
		list.add(scan1);
		list.add(scan2);
		list.add(mDiscoverScan);
	}

	private void updateContent(int bluetoothState) {
		// mDevicePreferenceMap.clear();
		removeAllDevice();
		switch (bluetoothState) {
		case BluetoothAdapter.STATE_ON: {
			addCachedDevices();
			break;
		}
		}
	}

	@Override
	public void onDeviceBondStateChanged(CachedBluetoothDevice cachedDevice, int bondState) {
		// TODO Auto-generated method stub
		Log.i("YQB", "-----------bondState---------" + bondState+"----address---"+cachedDevice.getDevice().getAddress());
		
		updateContent(mLocalAdapter.getBluetoothState());
		if(bondState == BluetoothDevice.BOND_BONDING){
			mItemClickAddress = cachedDevice.getDevice().getAddress();
			UpgradeUiFor();
		}else{
			mItemClickAddress = null;
		}

	}
	
	private void UpgradeUiFor(){
		Collections.sort(list);		
		threeDimenItemListInit(list);
		if(null != mItemClickAddress&&!"".equals(mItemClickAddress)){
			int temp = findTheOrder();
			if(temp != 0&&(itemPosition!=temp)){
				
				mDisapp = true;
			mListView.setSelectionFromTop(temp,offsetY);
			mItemClickAddress = null;
			Log.i(TAG, "---upgradeUI---temp="+temp+"----mDisapp---"+mDisapp);
			}
		}
	}
}
