package com.shandong.cloudtv.settings;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.shandong.cloudtv.settings.ethernet.EthernetDataEntity;
import com.shandong.cloudtv.settings.ethernet.EthernetItemEntity;
import com.shandong.cloudtv.settings.ethernet.EthernetItemHolder;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.wifi.WifiAccessPoint;
import com.shandong.cloudtv.settings.wifi.WifiInfoConfigEntity;

public class WifiModifyActivity extends Activity {
	private static final boolean DEBUG = true;
	private static final String TAG = "WifiModifyActivity";
	private static final int WIFI_MODIFY_ITEM_TEST_SPEED = 0;
	private static final int WIFI_MODIFY_ITEM_IPV4 = 18;//1;
	private static final int WIFI_MODIFY_ITEM_IPV4_MANUAL_ADJUST = 1;//2;
	private static final int WIFI_MODIFY_ITEM_IPV4_IP_ADDRESS = 2;//3;
	private static final int WIFI_MODIFY_ITEM_IPV4_NET_MASK = 3;//4;
	private static final int WIFI_MODIFY_ITEM_IPV4_GATEWAY = 4;//5;
	private static final int WIFI_MODIFY_ITEM_IPV4_DNS = 5;//6;
	private static final int WIFI_MODIFY_ITEM_IPV6 = 7;
	private static final int WIFI_MODIFY_ITEM_IPV6_MANUAL_ADJUST = 8;
	private static final int WIFI_MODIFY_ITEM_IPV6_IP_ADDRESS = 9;
	private static final int WIFI_MODIFY_ITEM_IPV6_NET_MASK = 10;
	private static final int WIFI_MODIFY_ITEM_IPV6_GATEWAY = 11;
	private static final int WIFI_MODIFY_ITEM_IPV6_DNS = 12;
	private static final int WIFI_MODIFY_ITEM_DISABLE_NET = 6;//13;

	private ListView mWifiItemList = null;
	private int mItemListCurPosition = -1;
	private EthernetItemHolder mItemCurHolder = null;
	private EthernetItemEntity mItemCurEntity = null;
	private WifiInfoConfigEntity mWifiInfoConfig = null;
	private WifiModifyAdapter mAdapter = null;
	//list focus view
	private View mItemListCurView = null;
	private LauncherFocusView mLauncherFocusView = null;
	private boolean mIsFirstIn = true;
	
	private Toast mToast = null;
	private LinearLayout mMainLayout = null;
	private boolean mKeyboardOKFlag = false;
	private boolean mTextEditFlag = false;
	
	private int mIPv4Assignment = WifiAccessPoint.UNASSIGNED;
	private String mV4IPAddress = null;
	private String mV4GatewayAddress = null;
	private String mV4DNSAddress = null;
	private String mV4NetmaskAddress = null;
	private boolean mSaveFlag = false;
	
	private boolean mDisModifyFlag = false;
	private int mDisModifyBuoy = 0;
	
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private long mKeyDownTime = 0L;
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.wifi_modify_main);
		
		mWifiInfoConfig = (WifiInfoConfigEntity)getIntent().getParcelableExtra(WifiInfoConfigEntity.KEY);
		mDisModifyFlag = getIntent().getBooleanExtra(WifiInfoConfigEntity.WIFI_DISCONNECT_MODIFY_KEY, false);
		if(mDisModifyFlag){
			mDisModifyBuoy = 1;
		}
		initWifiInfoData(true);
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initView(){
		mWifiItemList = (ListView)findViewById(R.id.wifi_modify_list);
		mWifiItemList.setOnItemSelectedListener(mItemListOnItemSelectedListener);
		mWifiItemList.setOnKeyListener(mItemListOnKeyListener);
		mWifiItemList.setOnItemClickListener(mItemListOnItemClickListener);
		mWifiItemList.setLayoutAnimationListener(new Animation.AnimationListener() {
			
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
				if(mWifiItemList!=null){
					//mWifiItemList.setDivider(getResources().getDrawable(R.drawable.under_line));
				}
			}
		});
		//list focus view
		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView)findViewById(R.id.wifi_modify_focus_view);
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
		
		mMainLayout = (LinearLayout)findViewById(R.id.wifi_modify_main_llyout);
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
	
	private void initWifiInfoData(boolean repeat) {
		if(repeat){
			mIPv4Assignment = mWifiInfoConfig.getIPv4Assignment();
		}
		mV4IPAddress = mWifiInfoConfig.getIPv4IPAddr();
		mV4NetmaskAddress = mWifiInfoConfig.getIPv4NetmaskAddr();
		mV4GatewayAddress = mWifiInfoConfig.getIPv4GatewayAddr();
		mV4DNSAddress = mWifiInfoConfig.getIPv4DNSAddr();
		mKeyboardOKFlag = false;
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
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:{
			Log.v(TAG, "back");
			updateWifiInfoConfig();     
			//修改从WifiDisconnectedActivity 跳转到这里后，原来的setResult无法返回数据给WifiConnectActivity 的问题
			Intent intent = new Intent("com.hiveview.cloudtv.settings.wifi.modify.save");
			Bundle bundle = new Bundle();  
			bundle.putParcelable(WifiInfoConfigEntity.KEY, mWifiInfoConfig);
			intent.putExtra(WifiInfoConfigEntity.SAVE_CONFIG, mSaveFlag);
			intent.putExtras(bundle);
			sendBroadcast(intent);
			
//			WifiModifyActivity.this.setResult(WifiInfoConfigEntity.RESULT_WIFI_MODIFY_OK, intent);   //RESULT_OK是返回状态码  
			WifiModifyActivity.this.finish(); //会触发onDestroy();  
		}
		return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, keyEvent);
	}

	private void updateWifiInfoConfig(){
		if(mWifiInfoConfig.getIPv4Assignment() != mIPv4Assignment){
			mSaveFlag = true;
		}
		mWifiInfoConfig.setIPv4Assignment(mIPv4Assignment);
		mWifiInfoConfig.setIPv4IPAddr(mV4IPAddress);
		mWifiInfoConfig.setIPv4DNSAddr(mV4DNSAddress);
		mWifiInfoConfig.setIPv4NetmaskAddr(mV4NetmaskAddress);
		mWifiInfoConfig.setIPv4GatewayAddr(mV4GatewayAddress);
		
	}
	
	AdapterView.OnItemSelectedListener mItemListOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if(DEBUG)
				Log.e(TAG,"item list select position="+position);
			mItemListCurPosition = position;
			
			//list focus view 
			mItemListCurView = view;
			if(mIsFirstIn){
				mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
			}
			
			if(mItemCurHolder != null && mItemCurEntity!=null&&
					EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT == mItemCurEntity.getItemCategory()){
				mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_unselected);
				mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_unselected);
			}
			if(mItemCurEntity != null && 
					EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT==mItemCurEntity.getItemCategory()){
				if(mItemCurHolder.mItemContentEdit.isFocused()){
					if(DEBUG)
						Log.e(TAG, "item edit is focusable");
					mItemCurHolder.mItemContentEdit.setFocusable(false);
					mItemCurHolder.mItemContentEdit.requestFocus();
					mWifiItemList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
					//mItemList.setFocusable(true);
				}
			}
			//text color clear
			if(mItemCurHolder!=null){
				mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
				if(mItemCurEntity!=null){
					if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT 
							== mItemCurEntity.getItemCategory()){
						mItemCurHolder.mItemContentTv.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
					}else if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT
							==mItemCurEntity.getItemCategory()){
						mItemCurHolder.mItemContentEdit.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
					}else if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER 
							== mItemCurEntity.getItemCategory()){
						mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_unselected);
					}
				}
			}
			
			mItemCurEntity = (EthernetItemEntity)((WifiModifyAdapter)mWifiItemList.getAdapter()).getItem(position);
			mItemCurHolder = (EthernetItemHolder)view.getTag();
			
			if(mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN){
				/*
				if(mItemListCurPosition == mWifiItemList.getCount()-2){
					mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				}
				*/
			
				if(mItemListCurPosition<5 || mItemListCurPosition>mWifiItemList.getCount()-2
						||(mWifiItemList.getFirstVisiblePosition()==0&&view.getTop()<(view.getHeight()*4))
						||(mWifiItemList.getFirstVisiblePosition()!=0&&view.getTop()<view.getHeight()*5)){
					mLauncherFocusView.moveTo(mItemListCurView);
				}else{
					mWifiItemList.setSelectionFromTop(mItemListCurPosition, view.getTop()-view.getHeight());
					listTextColorSet();
				}
			}else if(mCurKeycode == KeyEvent.KEYCODE_DPAD_UP){
				//mLauncherFocusView.moveTo(mItemListCurView);
				/*
				if(mItemListCurPosition == 1){
					mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				}
				*/
				Log.e(TAG, "list getheight:"+mWifiItemList.getHeight()+
						" view getTop:"+view.getTop()+"height:"+
						view.getHeight()+" visible first:"+mWifiItemList.getFirstVisiblePosition());
				
				if((mItemListCurPosition == 0||mWifiItemList.getFirstVisiblePosition()==0&&view.getTop()>=(view.getHeight()))
						||(mWifiItemList.getFirstVisiblePosition()!=0&&view.getTop()>view.getHeight())){
					mLauncherFocusView.moveTo(mItemListCurView);
				}else{
					listTextColorSet();
					mWifiItemList.setSelectionFromTop(mItemListCurPosition, view.getHeight());
				}
			}
			
			//text color set in mLaunchFocusView onAnimationEnd()
			if(mIsFirstIn){
				mIsFirstIn = false;
				mTextColorChangeFlag = true;
				listTextColorSet();
			}
			
			if(!mTextColorChangeFlag && mFocusAnimationEndFlag){
				if(mItemListCurPosition == 0 
						|| mItemListCurPosition==mWifiItemList.getCount()-1){
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			if(DEBUG)
				Log.e(TAG,"item list unselect ");
			mWifiItemList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

		}
		
	};
	
	private void listTextColorSet(){
		if(mItemCurHolder!=null && mTextColorChangeFlag&&mFocusAnimationEndFlag){
			mTextColorChangeFlag = false;
			mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_ffffff));
			if(mItemCurEntity!=null){
				if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT 
						== mItemCurEntity.getItemCategory()){
					mItemCurHolder.mItemContentTv.setTextColor(getResources().getColor(R.color.settings_ffffff));
				}else if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT
						==mItemCurEntity.getItemCategory()){
					if (mIPv4Assignment == WifiAccessPoint.STATIC) {
						mItemCurHolder.mItemContentEdit.setTextColor(getResources().getColor(
								R.color.settings_ffffff));
					}
				}else if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER 
						== mItemCurEntity.getItemCategory()){
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_selected);
				}
			}
		}
	}
	
	AdapterView.OnItemClickListener mItemListOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if(DEBUG)
				Log.e(TAG,"item click position="+position);
			if(WIFI_MODIFY_ITEM_TEST_SPEED == position){
				if(!mDisModifyFlag){
					//Intent intent = new Intent(WifiModifyActivity.this, TestSpeedActivity.class);
					Intent intent = new Intent(WifiModifyActivity.this, TestSpeedNewActivity.class);
					startActivity(intent);
				}
			}else if(WIFI_MODIFY_ITEM_DISABLE_NET == position){
				Intent intent = new Intent(WifiModifyActivity.this,WifiConnectActivity.class);
				WifiModifyActivity.this.setResult(WifiInfoConfigEntity.RESULT_WIFI_FORGET,intent);
				WifiModifyActivity.this.finish();
			}
			
			if(mItemCurEntity!=null && 
					EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT == mItemCurEntity.getItemCategory()){
				if(DEBUG)
					Log.e(TAG,"item edit click...............");
				if(mIPv4Assignment == WifiAccessPoint.STATIC){
					mWifiItemList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
						//mItemList.setFocusable(false);
					mItemCurHolder.mItemContentEdit.setFocusable(true);
					mItemCurHolder.mItemContentEdit.requestFocus();
					mItemCurHolder.mItemContentEdit.setSelection(mItemCurHolder.mItemContentEdit.getText().toString().length());
				
					mItemCurHolder.mItemContentEdit.addTextChangedListener(mTextWatcher);
					mItemCurHolder.mItemContentEdit.setOnEditorActionListener(mEditorActionListener);
					mItemCurHolder.mItemContentEdit.setOnKeyListener(mEditTextOnKeyListener);
					InputMethodManager mng = (InputMethodManager) WifiModifyActivity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.showSoftInput(mItemCurHolder.mItemContentEdit, InputMethodManager.SHOW_IMPLICIT);
				}else{
					Utils.startListFocusAnimation(WifiModifyActivity.this, mLauncherFocusView, R.anim.list_focus_anim);
				}
			}
		}
		
	};
	
	View.OnKeyListener mItemListOnKeyListener = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				System.out.println("item posion="+mItemListCurPosition);
				if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN
						||keyCode == KeyEvent.KEYCODE_DPAD_UP){
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
				if((mItemCurEntity == null || mItemCurHolder == null)){
					if(DEBUG)
						Log.e(TAG,"item cur entity null");
					return false;
				}
				if(mItemCurEntity.getItemCategory() != EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT){
					return false;
				}
				int position = mItemListCurPosition;
				if(mDisModifyFlag){
					position = position+mDisModifyBuoy;
				}
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					if(WIFI_MODIFY_ITEM_IPV4 == position ){
						if(EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						}else if(EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}else if(WIFI_MODIFY_ITEM_IPV4_MANUAL_ADJUST == position){
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
							mIPv4Assignment = WifiAccessPoint.STATIC;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_STATIC);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
							mIPv4Assignment = WifiAccessPoint.DHCP;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_DHCP);
						}
					}else if (WIFI_MODIFY_ITEM_IPV6 == position) {
						if(EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						}else if(EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}else if(WIFI_MODIFY_ITEM_IPV6_MANUAL_ADJUST == position){
						if(EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						}else if(EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}
					mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_selected);
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_unselected);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					if(WIFI_MODIFY_ITEM_IPV4 == position){
						if(EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						}else if(EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}else if(WIFI_MODIFY_ITEM_IPV4_MANUAL_ADJUST == position){
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
							mIPv4Assignment = WifiAccessPoint.STATIC;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_STATIC);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
							mIPv4Assignment = WifiAccessPoint.DHCP;
							mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_DHCP);
						}
					}else if (WIFI_MODIFY_ITEM_IPV6 == position) {
						if(EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						}else if(EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}else if(WIFI_MODIFY_ITEM_IPV6_MANUAL_ADJUST == position){
						if(EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						}else if(EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()){
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}
					mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_unselected);
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_selected);
					break;
				default:
					break;
				}
			}else if(event.getAction() == KeyEvent.ACTION_UP){
				if(!mTextColorChangeFlag){
					mTextColorChangeFlag = true;
					listTextColorSet();
				}
			}
			return false;
		}
	};
	
	private void initItemList(){
		ArrayList<EthernetItemEntity> itemList = new ArrayList<EthernetItemEntity>();
	    String[] item = getResources().getStringArray(R.array.wifi_modify_item_list);
	    String[] onOff = getResources().getStringArray(R.array.on_off);
		for(int i=0;i<item.length;i++){
			EthernetItemEntity entity = new EthernetItemEntity();
			entity.setItemTitle(item[i]);
			switch (i) {
			case WIFI_MODIFY_ITEM_IPV4:
			case WIFI_MODIFY_ITEM_IPV4_MANUAL_ADJUST:
			case WIFI_MODIFY_ITEM_IPV6:
			case WIFI_MODIFY_ITEM_IPV6_MANUAL_ADJUST:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT);
				entity.setItemContents(onOff);
				break;
			case WIFI_MODIFY_ITEM_IPV4_IP_ADDRESS:
			case WIFI_MODIFY_ITEM_IPV4_NET_MASK:
			case WIFI_MODIFY_ITEM_IPV4_GATEWAY:
			case WIFI_MODIFY_ITEM_IPV4_DNS:
			case WIFI_MODIFY_ITEM_IPV6_IP_ADDRESS:
			case WIFI_MODIFY_ITEM_IPV6_NET_MASK:
			case WIFI_MODIFY_ITEM_IPV6_GATEWAY:
			case WIFI_MODIFY_ITEM_IPV6_DNS:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT);
				break;
			case WIFI_MODIFY_ITEM_TEST_SPEED:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER);
				if(mDisModifyFlag){
					if(DEBUG){
						Log.e(TAG, "disconnect mode modify...test speed");
					}
					continue;
				}
				break;
			case WIFI_MODIFY_ITEM_DISABLE_NET:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_NULL);
				if(mDisModifyFlag){
					if(DEBUG){
						Log.e(TAG, "disconnect mode modify... disable net");
					}
					continue;
				}
				break;
			default:
				break;
			}
			
			itemList.add(entity);
		}
		mAdapter = new WifiModifyAdapter(WifiModifyActivity.this, itemList);
		mWifiItemList.setAdapter(mAdapter);
	}
	
	
	private void initItemView(EthernetItemHolder holder,EthernetItemEntity itemEntity,int position){
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
	
	private void initItemData(EthernetItemHolder holder,EthernetItemEntity itemEntity,int position){
		int pos = position;
		if(mDisModifyFlag){
			pos = pos+mDisModifyBuoy;
		}
		switch (pos) {
		case WIFI_MODIFY_ITEM_IPV4:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			break;
		case WIFI_MODIFY_ITEM_IPV4_MANUAL_ADJUST:
			if(mIPv4Assignment == WifiAccessPoint.STATIC){
				holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			}else if(mIPv4Assignment == WifiAccessPoint.DHCP){
				holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			}else{
				holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			}
			break;
		case WIFI_MODIFY_ITEM_IPV4_IP_ADDRESS:
			holder.mItemContentEdit.setText(mV4IPAddress);
			break;
		case WIFI_MODIFY_ITEM_IPV4_DNS:
			holder.mItemContentEdit.setText(mV4DNSAddress);
			break;
		case WIFI_MODIFY_ITEM_IPV4_NET_MASK:
			holder.mItemContentEdit.setText(mV4NetmaskAddress);
			break;
		case WIFI_MODIFY_ITEM_IPV4_GATEWAY:
			holder.mItemContentEdit.setText(mV4GatewayAddress);
			break;
		case WIFI_MODIFY_ITEM_IPV6:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			break;
		case WIFI_MODIFY_ITEM_IPV6_MANUAL_ADJUST:
			int ipv6Assignment = mWifiInfoConfig.getIPv6Assignment();
			if(ipv6Assignment == WifiAccessPoint.STATIC){
				holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			}else if(ipv6Assignment == WifiAccessPoint.DHCP){
				holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			}else{
				holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			}
			break;
		case WIFI_MODIFY_ITEM_IPV6_IP_ADDRESS:
			holder.mItemContentEdit.setText(mWifiInfoConfig.getIPv6IPAddr());
			break;
		case WIFI_MODIFY_ITEM_IPV6_DNS:
			holder.mItemContentEdit.setText(mWifiInfoConfig.getIPv6DNSAddr());
			break;
		case WIFI_MODIFY_ITEM_IPV6_NET_MASK:
			holder.mItemContentEdit.setText(mWifiInfoConfig.getIPv6NetmaskAddr());
			break;
		case WIFI_MODIFY_ITEM_IPV6_GATEWAY:
			holder.mItemContentEdit.setText(mWifiInfoConfig.getIPv6GatewayAddr());
			break;
		default:
			break;
		}
	}
	
	class WifiModifyAdapter extends BaseAdapter{
		LayoutInflater mInflater = null;
		private Context mContext = null;
		private ArrayList<EthernetItemEntity> mList = null;
		
		public WifiModifyAdapter(Context context,ArrayList<EthernetItemEntity> list){
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
			if(convertView == null){
				holder = new EthernetItemHolder();
				convertView = mInflater.inflate(R.layout.ethernet_item_list, null);
				holder.mItemTitle = (TextView)convertView.findViewById(R.id.ethernet_item_title);
				holder.mItemLeftArrow = (ImageView)convertView.findViewById(R.id.ethernet_item_left_arrow);
				holder.mItemContentTv = (TextView)convertView.findViewById(R.id.ethernet_item_content_tv);
				holder.mItemContentEdit = (EditText)convertView.findViewById(R.id.ethernet_item_content_edit);
				holder.mItemRightArrow = (ImageView)convertView.findViewById(R.id.ethernet_item_right_arrow);
				convertView.setTag(holder);
			}else{
				holder = (EthernetItemHolder)convertView.getTag();
			}
			final EthernetItemEntity itemEntity = mList.get(position);
			
			initItemView(holder,itemEntity,position);
			initItemData(holder,itemEntity,position);

			return convertView;
		}
		
	} 
	
	
	private void showToast(String content) {
		if (mToast != null) {
			mToast.cancel();
		}

		mToast = Toast.makeText(WifiModifyActivity.this, content, Toast.LENGTH_SHORT);
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
					mKeyboardOKFlag = true;
					updateWifiInfo(getItemEditText());
					InputMethodManager mng = (InputMethodManager) WifiModifyActivity.this
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
			if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_IP_ADDRESS) {
				mItemCurHolder.mItemContentEdit.setText(mV4IPAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4IPAddress.length());
			} else if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_DNS) {
				mItemCurHolder.mItemContentEdit.setText(mV4DNSAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4DNSAddress.length());
			} else if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_GATEWAY) {
				mItemCurHolder.mItemContentEdit.setText(mV4GatewayAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4GatewayAddress.length());
			} else if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_NET_MASK) {
				mItemCurHolder.mItemContentEdit.setText(mV4NetmaskAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4NetmaskAddress.length());
			}
		}
	}

	private void updateWifiInfo(String str) {
		if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_IP_ADDRESS) {
			mV4IPAddress = str;
		} else if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_DNS) {
			mV4DNSAddress = str;
		} else if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_GATEWAY) {
			mV4GatewayAddress = str;
		} else if (mItemListCurPosition == WIFI_MODIFY_ITEM_IPV4_NET_MASK) {
			mV4NetmaskAddress = str;
		}
		mSaveFlag = true;
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

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case EthernetDataEntity.MSG_IPV4_ADJUST_DHCP:
				initWifiInfoData(false);
				mAdapter.notifyDataSetChanged();
				break;
			case EthernetDataEntity.MSG_IPV4_ADJUST_STATIC:
				mAdapter.notifyDataSetChanged();
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
			default:
				break;
			}
		}
	};
}
