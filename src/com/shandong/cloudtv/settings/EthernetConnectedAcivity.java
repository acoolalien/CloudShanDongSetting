package com.shandong.cloudtv.settings;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shandong.cloudtv.settings.ethernet.EthernetDataEntity;
import com.shandong.cloudtv.settings.ethernet.EthernetItemEntity;
import com.shandong.cloudtv.settings.ethernet.EthernetItemHolder;
import com.shandong.cloudtv.settings.pppoe.PppoeDataEntity;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.CommonListView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.wifi.WifiDataEntity;

public class EthernetConnectedAcivity extends Activity {
	private static final String TAG = "EthernetActivity";
	private static final boolean DEBUG = true;
	private static final int ETHERNET_ITEM_TEST_SPEED = 0;
	private static final int ETHERNET_ITEM_WIFI_SHARE = 1;
	private static final int ETHERNET_ITEM_IPV4 = 18;//2;
	private static final int ETHERNET_ITEM_IPV4_MANUAL_ADJUST = 2;//3;
	private static final int ETHERNET_ITEM_IPV4_IP_ADDRESS = 3;//4;
	private static final int ETHERNET_ITEM_IPV4_NET_MASK = 4;//5;
	private static final int ETHERNET_ITEM_IPV4_GATEWAY = 5;//6;
	private static final int ETHERNET_ITEM_IPV4_DNS = 6;//7;
	private static final int ETHERNET_ITEM_IPV6 = 19;//8;
	private static final int ETHERNET_ITEM_IPV6_MANUAL_ADJUST = 9;
	private static final int ETHERNET_ITEM_IPV6_IP_ADDRESS = 10;
	private static final int ETHERNET_ITEM_IPV6_NET_MASK = 11;
	private static final int ETHERNET_ITEM_IPV6_GATEWAY = 12;
	private static final int ETHERNET_ITEM_IPV6_DNS = 13;
	private static final int ETHERNET_ITEM_DISABLE_NET = 7;//14;
	private static final int ETHERNET_ITEM_PPPOE_AUTO_CONNECT = 8;//15;

	private CommonListView mItemList = null;
	private EthernetAdapter mItemlistAdapter = null;
	private int mItemListCurPosition = -1;
	private EthernetItemEntity mItemCurEntity = null;
	private EthernetItemHolder mItemCurHolder = null;
	// list focus view
	private View mItemListCurView = null;
	private LauncherFocusView mLauncherFocusView = null;
	private boolean mIsFirstIn = true;
	// ethernet data
	private EthernetDataEntity mEthernetDataEntity = null;
	private boolean isAutoFlag = false;
	private String mV4IPAddress = null;
	private String mV4GatewayAddress = null;
	private String mV4DNSAddress = null;
	private String mV4NetmaskAddress = null;
	private WifiDataEntity mWifiDataEntity = null;
	private PppoeDataEntity mPppoeDataEntity = null;
	
	private Toast mToast = null;
	private LinearLayout mMainLayout = null;
	private boolean mKeyboardOKFlag = false;
	private boolean mTextEditFlag = false;
	
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private long mKeyDownTime = 0L;
	
	private boolean isPPPOE = false;

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.ethernet_connected_main);
		isPPPOE = Utils.getActiveNetworkType(EthernetConnectedAcivity.this) != ConnectivityManager.TYPE_ETHERNET?true:false;
		//Log.v(TAG, "isPPPOE=="+isPPPOE);
		mWifiDataEntity = WifiDataEntity.getInstance(this);
		mPppoeDataEntity = PppoeDataEntity.getInstance(this);
		initEthernetData(true);
		initView();
		regeditReceiver();
	}

	private void initView() {
		mItemList = (CommonListView) findViewById(R.id.ethernet_list);
		mItemList.setOnItemClickListener(mItemListOnItemClickListener);
		mItemList.setOnKeyListener(mItemListOnKeyListener);
		mItemList.setOnItemSelectedListener(mItemListOnItemSelectedListener);
		mItemList.setLayoutAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				if(mItemList!=null){
					//mItemList.setDivider(getResources().getDrawable(R.drawable.under_line));
				}
			}
		});
		
		
		// list focus view
		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView) findViewById(R.id.ethernet_connect_focus_view);
		mLauncherFocusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {
			@Override
			public void OnAnimateStart(View currentFocusView) {
				if(DEBUG){
					Log.e(TAG, "onAnimationStart...");
				}
				mFocusAnimationEndFlag = false;
			}
			
			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				//mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				if(DEBUG){
					Log.e(TAG, "OnAnimateEnd...");
				}
				mFocusAnimationEndFlag = true;
				listTextColorSet();

				
			}
			
		});
		
		mMainLayout = (LinearLayout)findViewById(R.id.ethernet_connect_main_llyout);
		mMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				if(DEBUG){
					Log.e(TAG, "I'm here to listen keyboard event ");
				}
				if(!mKeyboardOKFlag && mTextEditFlag){
					if(DEBUG){
						Log.e(TAG, "I'm here to listen keyboard event and recovery data");
					}
					recoveryItemEditText();
				}
			}
		});
		initItemList();
	}

	private void initEthernetData(boolean isRepeat) {
		if (isRepeat) {
			mEthernetDataEntity = EthernetDataEntity.getInstance(this);
			isAutoFlag = mEthernetDataEntity.getAutoFlag(true);
			mEthernetDataEntity.getEthernetDhcpInfo(true);
			mEthernetDataEntity.getEthernetManualInfo(true);
		}
		
		if(isPPPOE){
			mPppoeDataEntity.getPppoeDevInfo();
			mV4IPAddress = mPppoeDataEntity.getPppoeIpAddress();
			mV4NetmaskAddress = mPppoeDataEntity.getPppoeNetmask();
			mV4GatewayAddress = mPppoeDataEntity.getPppoeRouteAddress();
			mV4DNSAddress = mPppoeDataEntity.getPppoeDnsAddress();
		}else{
			mV4IPAddress = mEthernetDataEntity.getIPAddress(isAutoFlag);
			mV4NetmaskAddress = mEthernetDataEntity.getMaskAddress(isAutoFlag);
			mV4GatewayAddress = mEthernetDataEntity.getGWAddress(isAutoFlag);
			mV4DNSAddress = mEthernetDataEntity.getDNSAddress(isAutoFlag);
		}
		mKeyboardOKFlag = false;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegeditReceiver();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mItemlistAdapter!=null){
			mItemlistAdapter.notifyDataSetChanged();
		}
	}

	AdapterView.OnItemClickListener mItemListOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.e(TAG, "item click position=" + position);
			if (ETHERNET_ITEM_TEST_SPEED == position) {
				//Intent intent = new Intent(EthernetConnectedAcivity.this, TestSpeedActivity.class);
				Intent intent = new Intent(EthernetConnectedAcivity.this, TestSpeedNewActivity.class);
				startActivity(intent);
			} else if (ETHERNET_ITEM_WIFI_SHARE == position) {
				Utils.setApplicationBGBitmap(EthernetConnectedAcivity.this);
				Intent intent = new Intent(EthernetConnectedAcivity.this, WifiShareActivity.class);
				startActivity(intent);
			} else if (ETHERNET_ITEM_DISABLE_NET == position) {
				//mPppoeDataEntity.disconnect();
				Utils.setApplicationBGBitmap(EthernetConnectedAcivity.this);
				Intent intent = new Intent(EthernetConnectedAcivity.this, PppoeConnectActivity.class);
				intent.putExtra(PppoeDataEntity.PPPOE_EXTRA_STATE, PppoeDataEntity.PPPOE_EXTRA_STATE_DISCONNECT);
				startActivity(intent);
			}  
			// edit focus get
			if (mItemCurEntity != null
					&& EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT == mItemCurEntity.getItemCategory()) {
				if (DEBUG)
					Log.e(TAG, "item edit click...............");
				if (!isAutoFlag) {
					mItemCurHolder.mItemContentEdit.setCursorVisible(true);
					mItemCurHolder.mItemContentEdit.setVisibility(View.VISIBLE);
//					mItemList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
					// mItemList.setFocusable(false);
					mItemCurHolder.mItemContentEdit.setFocusable(true);
					mItemCurHolder.mItemContentEdit.requestFocus();
					mItemCurHolder.mItemContentEdit.setSelection(mItemCurHolder.mItemContentEdit.getText().toString()
							.length());
					mItemCurHolder.mItemContentEdit.addTextChangedListener(mTextWatcher);
					mItemCurHolder.mItemContentEdit.setOnEditorActionListener(mEditorActionListener);
					mItemCurHolder.mItemContentEdit.setOnKeyListener(mEditTextOnKeyListener);
					InputMethodManager mng = (InputMethodManager) EthernetConnectedAcivity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.showSoftInput(mItemCurHolder.mItemContentEdit, InputMethodManager.SHOW_IMPLICIT);
				}else{
					Utils.startListFocusAnimation(EthernetConnectedAcivity.this, mLauncherFocusView, R.anim.list_focus_anim);
				}
			}
		}

	};

	
	@SuppressLint("NewApi")
	View.OnKeyListener mItemListOnKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == KeyEvent.ACTION_DOWN) {

				if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN || keyCode==KeyEvent.KEYCODE_DPAD_UP){
					mCurKeycode = keyCode;
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
				}
				System.out.println("item posion=" + mItemListCurPosition);
				if (mItemCurEntity == null || mItemCurHolder == null) {
					if (DEBUG)
						Log.e(TAG, "item cur entity null");
					return false;
				}
				if (mItemCurEntity.getItemCategory() != EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT) {
					return false;
				}
				int activeEthType = Utils.getActiveNetworkType(EthernetConnectedAcivity.this);
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					if (ETHERNET_ITEM_IPV4 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_ITEM_IPV4_MANUAL_ADJUST == mItemListCurPosition) {
						if(activeEthType != ConnectivityManager.TYPE_ETHERNET){
							return false;
						}
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
							isAutoFlag = false;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_STATIC);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
							isAutoFlag = true;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_DHCP);
						}
					} else if (ETHERNET_ITEM_IPV6 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_ITEM_IPV6_MANUAL_ADJUST == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}else if(ETHERNET_ITEM_PPPOE_AUTO_CONNECT == mItemListCurPosition){
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
							mPppoeDataEntity.setPppoeAutoConnectFlag(true);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
							mPppoeDataEntity.setPppoeAutoConnectFlag(false);
						}
					}
					mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_selected);
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_unselected);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					if (ETHERNET_ITEM_IPV4 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_ITEM_IPV4_MANUAL_ADJUST == mItemListCurPosition) {
						if(activeEthType != ConnectivityManager.TYPE_ETHERNET){
							return false;
						}
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
							isAutoFlag = false;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_STATIC);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
							isAutoFlag = true;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_DHCP);
						}
					} else if (ETHERNET_ITEM_IPV6 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_ITEM_IPV6_MANUAL_ADJUST == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}else if(ETHERNET_ITEM_PPPOE_AUTO_CONNECT == mItemListCurPosition){
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
							mPppoeDataEntity.setPppoeAutoConnectFlag(true);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
							mPppoeDataEntity.setPppoeAutoConnectFlag(false);
						}
					}
					mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_unselected);
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_selected);
					break;
				case KeyEvent.KEYCODE_BACK:
					if(DEBUG){
						Log.e(TAG, "keycode return ..................");
					}
					break;
				default:
					break;
				}
			}else if(event.getAction() == KeyEvent.ACTION_UP){

				if(DEBUG){
					Log.e(TAG, "keycode action up");
				}
				if(!mTextColorChangeFlag){
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}
			return false;
		}
	};

	AdapterView.OnItemSelectedListener mItemListOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.e(TAG,
						"item list select position=" + position + "visible pos=" + mItemList.getLastVisiblePosition());
			mItemListCurPosition = position;
			// list focus view
			mItemListCurView = view;
			if (mIsFirstIn) {
				mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
			}
			// arrow clear
			if (mItemCurHolder != null && mItemCurEntity != null
					&& EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT == mItemCurEntity.getItemCategory()) {
				mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_unselected);
				mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_unselected);
			}
			// text color clear
			if (mItemCurHolder != null) {
				mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
				if (mItemCurEntity != null) {
					if (EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT == mItemCurEntity.getItemCategory()) {
						mItemCurHolder.mItemContentTv.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
					} else if (EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT == mItemCurEntity
							.getItemCategory()) {
						mItemCurHolder.mItemContentEdit.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
					} else if (EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER == mItemCurEntity
							.getItemCategory()) {
						mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_unselected);
					}
				}
			}
			// focus recovery
			if (mItemCurEntity != null
					&& EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT == mItemCurEntity.getItemCategory()) {
				if (mItemCurHolder.mItemContentEdit.isFocused()) {
					if (DEBUG){
						Log.e(TAG, "item edit is focusable");
					}
					mItemCurHolder.mItemContentEdit.setFocusable(false);
					mItemCurHolder.mItemContentEdit.requestFocus();
					mItemList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
					//mItemCurHolder.mItemContentEdit.addTextChangedListener(null);
					//mItemCurHolder.mItemContentEdit.setOnEditorActionListener(null);
					// mItemList.setFocusable(true);
				}
			}

			mItemCurEntity = (EthernetItemEntity) ((EthernetAdapter) mItemList.getAdapter()).getItem(position);
			mItemCurHolder = (EthernetItemHolder) view.getTag();
			
			// text color set in mLauncherFocusView onAnimationEnd()
			if(mIsFirstIn){
				mIsFirstIn = false;
				mTextColorChangeFlag = true;
				listTextColorSet();
			}
			
			if(mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN){
				/*
				if(mItemListCurPosition == mItemList.getCount()-2){
					mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				}
				*/
				Log.e(TAG, "list getheight:"+mItemList.getHeight()+
						" view getTop:"+view.getTop()+"height:"+
						view.getHeight()+" visible first:"+mItemList.getFirstVisiblePosition());
				
				if(mItemListCurPosition<5 || mItemListCurPosition>mItemList.getCount()-2
						||(mItemList.getFirstVisiblePosition()==0&&view.getTop()<(view.getHeight()*4))
						||(mItemList.getFirstVisiblePosition()!=0&&view.getTop()<view.getHeight()*5)){
					mLauncherFocusView.moveTo(mItemListCurView);
				}else{
					listTextColorSet();
					mItemList.setSelectionFromTop(mItemListCurPosition, view.getTop()-view.getHeight());
				}
			}else if(mCurKeycode == KeyEvent.KEYCODE_DPAD_UP){
				//mLauncherFocusView.moveTo(mItemListCurView);
				/*
				if(mItemListCurPosition == 1){
					mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				}
				*/
				Log.e(TAG, "list getheight:"+mItemList.getHeight()+
						" view getTop:"+view.getTop()+"height:"+
						view.getHeight()+" visible first:"+mItemList.getFirstVisiblePosition());
				
				if((mItemListCurPosition == 0||mItemList.getFirstVisiblePosition()==0&&view.getTop()>=(view.getHeight()))
						||(mItemList.getFirstVisiblePosition()!=0&&view.getTop()>view.getHeight())){
					mLauncherFocusView.moveTo(mItemListCurView);
				}else{
					listTextColorSet();
					mItemList.setSelectionFromTop(mItemListCurPosition, view.getHeight());
				}
			}
			
			//fixed the keyboard repeat mode
			if(!mTextColorChangeFlag && mFocusAnimationEndFlag){
				if((mItemListCurPosition == 0 
						|| mItemListCurPosition==mItemList.getCount()-1)){
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.e(TAG, "item list unselect ");
			mItemList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

		}

	};

	private void listTextColorSet(){
		if (mItemCurHolder != null && mTextColorChangeFlag && mFocusAnimationEndFlag) {
			mTextColorChangeFlag = false;
			mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_ffffff));
			if (mItemCurEntity != null) {
				if (EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT == mItemCurEntity.getItemCategory()) {
					mItemCurHolder.mItemContentTv.setTextColor(getResources().getColor(R.color.settings_ffffff));
				} else if (EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT == mItemCurEntity
						.getItemCategory()) {
					if (!isAutoFlag) {
						mItemCurHolder.mItemContentEdit.setTextColor(getResources().getColor(
								R.color.settings_ffffff));
					}
				} else if (EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER == mItemCurEntity
						.getItemCategory()) {
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_selected);
				}
			}
		}	
	}
	
	private void initItemList() {
		ArrayList<EthernetItemEntity> itemList = new ArrayList<EthernetItemEntity>();
		String[] item = getResources().getStringArray(R.array.ethernet_connected_item_list);
		String[] onOff = getResources().getStringArray(R.array.on_off);
		int activeType = Utils.getActiveNetworkType(EthernetConnectedAcivity.this);

		for (int i = 0; i < item.length; i++) {
			EthernetItemEntity entity = new EthernetItemEntity();
			entity.setItemTitle(item[i]);
			switch (i) {
			case ETHERNET_ITEM_TEST_SPEED:
			case ETHERNET_ITEM_WIFI_SHARE:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER);
				break;
			case ETHERNET_ITEM_IPV4:
			case ETHERNET_ITEM_IPV4_MANUAL_ADJUST:
			case ETHERNET_ITEM_IPV6:
			case ETHERNET_ITEM_IPV6_MANUAL_ADJUST:
			case ETHERNET_ITEM_PPPOE_AUTO_CONNECT:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT);
				entity.setItemContents(onOff);
				if(activeType == ConnectivityManager.TYPE_ETHERNET && i==ETHERNET_ITEM_PPPOE_AUTO_CONNECT){
					continue;
				}
				break;
			case ETHERNET_ITEM_IPV4_IP_ADDRESS:
			case ETHERNET_ITEM_IPV4_NET_MASK:
			case ETHERNET_ITEM_IPV4_GATEWAY:
			case ETHERNET_ITEM_IPV4_DNS:
			case ETHERNET_ITEM_IPV6_IP_ADDRESS:
			case ETHERNET_ITEM_IPV6_NET_MASK:
			case ETHERNET_ITEM_IPV6_GATEWAY:
			case ETHERNET_ITEM_IPV6_DNS:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT);
				break;
			case ETHERNET_ITEM_DISABLE_NET:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_NULL);
				if(activeType == ConnectivityManager.TYPE_ETHERNET){
					continue;
				}
				break;
			default:
				break;
			}
			itemList.add(entity);
		}
		mItemlistAdapter = new EthernetAdapter(EthernetConnectedAcivity.this, itemList);
		mItemList.setAdapter(mItemlistAdapter);
	}

	private void initItemView(EthernetItemHolder holder, EthernetItemEntity itemEntity, int position) {
		holder.mItemTitle.setText(itemEntity.getItemTitle());
		switch (itemEntity.getItemCategory()) {
		case EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT:
			holder.mItemLeftArrow.setVisibility(View.GONE);
			holder.mItemRightArrow.setVisibility(View.GONE);
			holder.mItemContentTv.setVisibility(View.GONE);
			holder.mItemContentEdit.setVisibility(View.VISIBLE);
			break;
		case EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER:
			holder.mItemLeftArrow.setVisibility(View.GONE);
			holder.mItemRightArrow.setVisibility(View.VISIBLE);
			holder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_unselected);
			holder.mItemContentTv.setVisibility(View.GONE);
			holder.mItemContentEdit.setVisibility(View.GONE);
			break;
		case EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_NULL:
			holder.mItemLeftArrow.setVisibility(View.GONE);
			holder.mItemRightArrow.setVisibility(View.GONE);
			holder.mItemContentTv.setVisibility(View.GONE);
			holder.mItemContentEdit.setVisibility(View.GONE);
			break;
		case EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT:
			holder.mItemLeftArrow.setVisibility(View.VISIBLE);
			holder.mItemRightArrow.setVisibility(View.VISIBLE);
			holder.mItemRightArrow.setImageResource(R.drawable.right_arrow_unselected);
			holder.mItemContentTv.setVisibility(View.VISIBLE);
			holder.mItemContentEdit.setVisibility(View.GONE);
			break;
		default:
			break;
		}

	}

	private void initItemData(EthernetItemHolder holder, EthernetItemEntity itemEntity, int position) {
		switch (position) {
		case ETHERNET_ITEM_TEST_SPEED:
			break;
		case ETHERNET_ITEM_WIFI_SHARE:
			int wifiApState = mWifiDataEntity.getWifiApState();
			if (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED || wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) {
				holder.mItemContentTv.setVisibility(View.VISIBLE);
				holder.mItemContentTv.setText(getResources().getText(R.string.wifi_share_opened).toString());
			}else{
				holder.mItemContentTv.setVisibility(View.INVISIBLE);
			}
			break;
		case ETHERNET_ITEM_IPV4:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			break;
		case ETHERNET_ITEM_IPV4_MANUAL_ADJUST:
			if (isAutoFlag) {
				holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			} else {
				holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			}
			break;
		case ETHERNET_ITEM_IPV4_IP_ADDRESS:
			holder.mItemContentEdit.setText(mV4IPAddress);
			break;
		case ETHERNET_ITEM_IPV4_NET_MASK:
			holder.mItemContentEdit.setText(mV4NetmaskAddress);
			break;
		case ETHERNET_ITEM_IPV4_GATEWAY:
			holder.mItemContentEdit.setText(mV4GatewayAddress);
			break;
		case ETHERNET_ITEM_IPV4_DNS:
			holder.mItemContentEdit.setText(mV4DNSAddress);
			break;
		case ETHERNET_ITEM_IPV6:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			break;
		case ETHERNET_ITEM_IPV6_MANUAL_ADJUST:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			break;
		case ETHERNET_ITEM_IPV6_IP_ADDRESS:
			holder.mItemContentEdit.setText("");
			break;
		case ETHERNET_ITEM_IPV6_NET_MASK:
			holder.mItemContentEdit.setText("");
			break;
		case ETHERNET_ITEM_IPV6_GATEWAY:
			holder.mItemContentEdit.setText("");
			break;
		case ETHERNET_ITEM_IPV6_DNS:
			holder.mItemContentEdit.setText("");
			break;
		case ETHERNET_ITEM_DISABLE_NET:
			break;
		case ETHERNET_ITEM_PPPOE_AUTO_CONNECT:
			if(mPppoeDataEntity.getPppoeAutoConnectFlag()){
				holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			}else{
				holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			}
			break;

		default:
			break;
		}
	}

	class EthernetAdapter extends BaseAdapter {
		LayoutInflater mInflater = null;
		private Context mContext = null;
		private ArrayList<EthernetItemEntity> mList = null;

		public EthernetAdapter(Context context, ArrayList<EthernetItemEntity> list) {
			mContext = context;
			mList = list;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			EthernetItemHolder holder = null;
			if (convertView == null) {
				holder = new EthernetItemHolder();
				convertView = mInflater.inflate(R.layout.ethernet_item_list, null);
				holder.mItemTitle = (TextView) convertView.findViewById(R.id.ethernet_item_title);
				holder.mItemLeftArrow = (ImageView) convertView.findViewById(R.id.ethernet_item_left_arrow);
				holder.mItemContentTv = (TextView) convertView.findViewById(R.id.ethernet_item_content_tv);
				holder.mItemContentEdit = (EditText) convertView.findViewById(R.id.ethernet_item_content_edit);
				holder.mItemRightArrow = (ImageView) convertView.findViewById(R.id.ethernet_item_right_arrow);
				convertView.setTag(holder);
			} else {
				holder = (EthernetItemHolder) convertView.getTag();
			}

			final EthernetItemEntity itemEntity = mList.get(position);

			initItemView(holder, itemEntity, position);
			initItemData(holder, itemEntity, position);

			return convertView;
		}

	}

	private void showToast(String content) {
		if (mToast != null) {
			mToast.cancel();
		}

		mToast = Toast.makeText(EthernetConnectedAcivity.this, content, Toast.LENGTH_SHORT);
		mToast.show();
	}

	View.OnKeyListener mEditTextOnKeyListener = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(DEBUG){
				Log.e(TAG, "I'm here......................onkey");
			}
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
					if(mItemCurHolder!=null){
						mItemCurHolder.mItemContentEdit.requestFocus();
					}
				}
			}
			return false;
		}
	};
	
	private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				try {
					if (DEBUG) {
						Log.e(TAG, "getEditText()=" + getItemEditText());
					}

					if (!Utils.isIpAddress(getItemEditText(),mHandler)) {
						Log.e(TAG, "!isIpAddress(getEditText())");
						recoveryItemEditText();
						return false;
					}
					
					mItemCurHolder.mItemContentEdit.setCursorVisible(false);
					mKeyboardOKFlag = true;
					updateEthernetInfo(getItemEditText());
					InputMethodManager mng = (InputMethodManager) EthernetConnectedAcivity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					
				} catch (Exception e) {
					Log.e(TAG, "Hide key broad error", e);
				}
			}
			return false;
		}
	};

	private String getItemEditText() {
		String result = null;
		if (mItemCurHolder != null) {
			result = mItemCurHolder.mItemContentEdit.getText().toString();
		}
		return result;
	}

	private void recoveryItemEditText() {
		mTextEditFlag = false;
		mKeyboardOKFlag = false;
		if (mItemCurHolder != null) {
			if (mItemListCurPosition == ETHERNET_ITEM_IPV4_IP_ADDRESS) {
				mItemCurHolder.mItemContentEdit.setText(mV4IPAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4IPAddress.length());
			} else if (mItemListCurPosition == ETHERNET_ITEM_IPV4_DNS) {
				mItemCurHolder.mItemContentEdit.setText(mV4DNSAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4DNSAddress.length());
			} else if (mItemListCurPosition == ETHERNET_ITEM_IPV4_GATEWAY) {
				mItemCurHolder.mItemContentEdit.setText(mV4GatewayAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4GatewayAddress.length());
			} else if (mItemListCurPosition == ETHERNET_ITEM_IPV4_NET_MASK) {
				mItemCurHolder.mItemContentEdit.setText(mV4NetmaskAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4NetmaskAddress.length());
			}
		}
	}

	private void updateEthernetInfo(String str) {
		if (mItemListCurPosition == ETHERNET_ITEM_IPV4_IP_ADDRESS) {
			mV4IPAddress = str;
		} else if (mItemListCurPosition == ETHERNET_ITEM_IPV4_DNS) {
			mV4DNSAddress = str;
		} else if (mItemListCurPosition == ETHERNET_ITEM_IPV4_GATEWAY) {
			mV4GatewayAddress = str;
		} else if (mItemListCurPosition == ETHERNET_ITEM_IPV4_NET_MASK) {
			mV4NetmaskAddress = str;
		}
		mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_STATIC);
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		public void onTextChanged(CharSequence sequence, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
			// TODO Auto-generated method stub
			if (DEBUG) {
				Log.e(TAG, "sequence=" + sequence);
				Log.e(TAG, "start=" + start);
				Log.e(TAG, "count=" + count);
				Log.e(TAG, "after=" + after);
			}
		}

		public void afterTextChanged(Editable editable) {
			// TODO Auto-generated method stub
			try {
				String tempString = editable.toString();
				if (DEBUG) {
					Log.e(TAG, "tempString=" + tempString);
				}
				mTextEditFlag = true;
				if (tempString.length() > EthernetDataEntity.IP_MAX_LENGTH) {
					Message msg = Message.obtain();
					msg.what = EthernetDataEntity.ALERTMSG_EXCEED_CHRATER;
					mHandler.sendMessage(msg);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};

	private void showDhcpInfo() {
		try {
			// DhcpInfo dhcpInfo = mEthManager.getDhcpInfo();
			ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();

			if (info != null && info.getType() == ConnectivityManager.TYPE_ETHERNET) {
				LinkProperties properties = manager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
				String ipAddr = null;
				String gateway = null;
				String dns = null;
				Iterator<InetAddress> iter = properties.getAddresses().iterator();
				while (iter.hasNext()) {
					InetAddress inet = iter.next();
					if(!inet.isLoopbackAddress()){
						ipAddr = inet.getHostAddress();
						break;
					}
				}

				mV4IPAddress = ipAddr;
				String gw = properties.getRoutes().toString();
				if (gw.contains(">")) {
			        gw = gw.substring(gw.lastIndexOf('>') + 2, gw.length() - 1);
			    }
				mV4GatewayAddress=gw;
				
				Iterator<InetAddress> iterDns = properties.getDnses().iterator();
				while (iterDns.hasNext()) {
					InetAddress inet = iterDns.next();
					if(!inet.isLoopbackAddress()){
						dns = inet.getHostAddress();
						break;
					}
				}
				mV4DNSAddress = dns;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (DEBUG) {
			Log.e(TAG, "get ip11111:" + mV4IPAddress + " mask:" + mV4NetmaskAddress + " gw:" + mV4GatewayAddress
					+ " dns:" + mV4DNSAddress);
		}
	}
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case EthernetDataEntity.MSG_IPV4_ADJUST_DHCP:
				mEthernetDataEntity.getEthernetDhcpInfo(true);
				initEthernetData(false);
				mEthernetDataEntity.updateEthDevInfo(true, null, null, mV4DNSAddress, null);
				if (DEBUG) {
					Log.e(TAG, "get ip:" + mV4IPAddress + " mask:" + mV4NetmaskAddress + " gw:" + mV4GatewayAddress
							+ " dns:" + mV4DNSAddress);
				}
				mItemlistAdapter.notifyDataSetChanged();
				mHandler.sendEmptyMessageDelayed(EthernetDataEntity.MSG_IPV4_GET_DATA, 2000);
				break;
			case EthernetDataEntity.MSG_IPV4_ADJUST_STATIC:
				if (DEBUG) {
					Log.e(TAG, "set ip:" + mV4IPAddress + " mask:" + mV4NetmaskAddress + " gw:" + mV4GatewayAddress
							+ " dns:" + mV4DNSAddress);
				}
                
//				mItemlistAdapter.notifyDataSetChanged();
				if (DEBUG) {
					Log.e(TAG, "I'm here static.................3");
				}
				mEthernetDataEntity.updateEthDevInfo(false, mV4IPAddress, mV4NetmaskAddress, mV4DNSAddress,
						mV4GatewayAddress);
				mEthernetDataEntity.getEthernetManualInfo(true);
				initEthernetData(false);
				break;
			case EthernetDataEntity.ALERTMSG_EXCEED_CHRATER:
				showToast(getResources().getString(R.string.str_ip_full_characters));
				break;
			case EthernetDataEntity.ALERTMSG_IPNOGATEWAY:
				showToast(getResources().getString(R.string.str_ip_no_gateway));
				break;
			case EthernetDataEntity.ALERTMSG_IPINVALID:
				showToast(getResources().getString(R.string.str_ip_address_invalid));
				break;
			case EthernetDataEntity.ALERTMSG_MAX_ITEM_255:
				showToast(getResources().getString(R.string.str_ip_max_value));
				break;
			case EthernetDataEntity.ALERTMSG_IP_EQUAL_GATEWAY:
				showToast(getResources().getString(R.string.str_ip_equal_gateway));
				break;
			case EthernetDataEntity.MSG_IPV4_GET_DATA:
				showDhcpInfo();
				mItemlistAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};
	
	
	private void regeditReceiver() {
		IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                 Log.v(TAG, "eth status=="+Utils.checkEthState());
                 if(!Utils.checkEthState()){
                	 jump2Wifi();
                 }
            }
        }
    };
    
    private void unRegeditReceiver(){
    	unregisterReceiver(mReceiver);
    }
    
    private void jump2Wifi(){
		Intent intent = new Intent(this, WifiConnectActivity.class);
		startActivity(intent);
		finish();
	}
}
