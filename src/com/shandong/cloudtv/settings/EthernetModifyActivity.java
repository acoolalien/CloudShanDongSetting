package com.shandong.cloudtv.settings;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

public class EthernetModifyActivity extends Activity {
	private static final boolean DEBUG = true;
	private static final String TAG = "EthernetModifyActivity";
	private static final int ETHERNET_MODIFY_ITEM_IPV4 = 18;//0;
	private static final int ETHERNET_MODIFY_ITEM_IPV4_MANUAL_ADJUST = 0;//1;
	private static final int ETHERNET_MODIFY_ITEM_IPV4_IP_ADDRESS = 1;//2;
	private static final int ETHERNET_MODIFY_ITEM_IPV4_NET_MASK = 2;//3;
	private static final int ETHERNET_MODIFY_ITEM_IPV4_GATEWAY = 3;//4;
	private static final int ETHERNET_MODIFY_ITEM_IPV4_DNS = 4;//5;
	private static final int ETHERNET_MODIFY_ITEM_IPV6 = 6;
	private static final int ETHERNET_MODIFY_ITEM_IPV6_MANUAL_ADJUST = 7;
	private static final int ETHERNET_MODIFY_ITEM_IPV6_IP_ADDRESS = 8;
	private static final int ETHERNET_MODIFY_ITEM_IPV6_NET_MASK = 9;
	private static final int ETHERNET_MODIFY_ITEM_IPV6_GATEWAY = 10;
	private static final int ETHERNET_MODIFY_ITEM_IPV6_DNS = 11;

	private ListView mItemModifyList = null;
	private int mItemListCurPosition = -1;
	private EthernetItemHolder mItemCurHolder = null;
	private EthernetItemEntity mItemCurEntity = null;
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

	private Toast mToast = null;
	private LinearLayout mMainLayout = null;
	private boolean mKeyboardOKFlag = false;
	private boolean mTextEditFlag = false;

	private EthernetModifyAdapter mModifyItemlistAdapter = null;
	private int mCurKeycode = KeyEvent.KEYCODE_0;

	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private long mKeyDownTime = 0L;
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.ethernet_modify_main);
		initEthernetData(true);
		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initView() {
		mItemModifyList = (ListView) findViewById(R.id.ethernet_modify_list);
		mItemModifyList.setOnItemSelectedListener(mItemListOnItemSelectedListener);
		mItemModifyList.setOnKeyListener(mItemListOnKeyListener);
		mItemModifyList.setOnItemClickListener(mItemListOnItemClickListener);
        mItemModifyList.setLayoutAnimationListener(new Animation.AnimationListener() {
			
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
				if(mItemModifyList!=null){
					//mItemModifyList.setDivider(getResources().getDrawable(R.drawable.under_line));
				}
			}
		});

		// list focus view
		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView) findViewById(R.id.ethernet_modify_focus_view);
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

		mMainLayout = (LinearLayout) findViewById(R.id.ethernet_modify_main_llyout);
		mMainLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				if (DEBUG) {
					Log.e(TAG, "I'm here to listen keyboard event ");
				}
				if (!mKeyboardOKFlag && mTextEditFlag) {
					if (DEBUG) {
						Log.e(TAG, "I'm here to listen keyboard event and recovery data");
					}
					recoveryItemEditText();
				}
			}
		});

		initItemList();
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

	private void initEthernetData(boolean isRepeat) {
		if (isRepeat) {
			mEthernetDataEntity = EthernetDataEntity.getInstance(this);
			isAutoFlag = mEthernetDataEntity.getAutoFlag(true);
		}

		mV4IPAddress = mEthernetDataEntity.getIPAddress(isAutoFlag);
		mV4NetmaskAddress = mEthernetDataEntity.getMaskAddress(isAutoFlag);
		mV4GatewayAddress = mEthernetDataEntity.getGWAddress(isAutoFlag);
		mV4DNSAddress = mEthernetDataEntity.getDNSAddress(isAutoFlag);
		mKeyboardOKFlag = false;
	}

	AdapterView.OnItemSelectedListener mItemListOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.e(TAG, "item list select position=" + position);
			mItemListCurPosition = position;
			// list focus view
			mItemListCurView = view;
			if (mIsFirstIn) {
				mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
			}


			if (mItemCurHolder != null && mItemCurEntity != null
					&& EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT == mItemCurEntity.getItemCategory()) {
				mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_unselected);
				mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_unselected);
			}
			if (mItemCurEntity != null
					&& EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT == mItemCurEntity.getItemCategory()) {
				if (mItemCurHolder.mItemContentEdit.isFocused()) {
					if (DEBUG)
						Log.e(TAG, "item edit is focusable");
					mItemCurHolder.mItemContentEdit.setFocusable(false);
					mItemCurHolder.mItemContentEdit.requestFocus();
					mItemModifyList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
					// mItemList.setFocusable(true);
				}
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
					}
				}
			}

			mItemCurEntity = (EthernetItemEntity) ((EthernetModifyAdapter) mItemModifyList.getAdapter())
					.getItem(position);
			mItemCurHolder = (EthernetItemHolder) view.getTag();

			if(mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN){
				/*
				if(mItemListCurPosition == mItemModifyList.getCount()-2){
					mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				}
			    */
				if(mItemListCurPosition<5 || mItemListCurPosition>mItemModifyList.getCount()-2
						||(mItemModifyList.getFirstVisiblePosition()==0&&view.getTop()<(view.getHeight()*4))
						||(mItemModifyList.getFirstVisiblePosition()!=0&&view.getTop()<view.getHeight()*5)){
					mLauncherFocusView.moveTo(mItemListCurView);
				}else{
					mItemModifyList.setSelectionFromTop(mItemListCurPosition, view.getTop()-view.getHeight());
					listTextColorSet();
				}
			}else if(mCurKeycode == KeyEvent.KEYCODE_DPAD_UP){
				//mLauncherFocusView.moveTo(mItemListCurView);
				/*
				if(mItemListCurPosition == 1){
					mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
				}
				*/
				Log.e(TAG, "list getheight:"+mItemModifyList.getHeight()+
						" view getTop:"+view.getTop()+"height:"+
						view.getHeight()+" visible first:"+mItemModifyList.getFirstVisiblePosition());
				
				if((mItemListCurPosition == 0||mItemModifyList.getFirstVisiblePosition()==0&&view.getTop()>=(view.getHeight()))
						||(mItemModifyList.getFirstVisiblePosition()!=0&&view.getTop()>view.getHeight())){
					mLauncherFocusView.moveTo(mItemListCurView);
				}else{
					listTextColorSet();
					mItemModifyList.setSelectionFromTop(mItemListCurPosition, view.getHeight());
				}
			}
			
			// text color set in mLaunchFocusView onAnimaitonEnd()
			if(mIsFirstIn){
				mIsFirstIn = false;
				mTextColorChangeFlag = true;
				listTextColorSet();
			}

			if(!mTextColorChangeFlag && mFocusAnimationEndFlag){
				if(mItemListCurPosition == 0 
						|| mItemListCurPosition==mItemModifyList.getCount()-1){
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
			mItemModifyList.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

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
						mItemCurHolder.mItemContentEdit.setTextColor(getResources().getColor(R.color.settings_ffffff));
					}
				}
			}
		}
	}
	
	AdapterView.OnItemClickListener mItemListOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			// TODO Auto-generated method stub
			if (DEBUG)
				Log.e(TAG, "item click position=" + position);

			if (mItemCurEntity != null
					&& EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT == mItemCurEntity.getItemCategory()) {
				if (DEBUG)
					Log.e(TAG, "item edit click...............");
				if (!isAutoFlag) {
					mItemModifyList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
					// mItemList.setFocusable(false);
					mItemCurHolder.mItemContentEdit.setFocusable(true);
					mItemCurHolder.mItemContentEdit.requestFocus();
					mItemCurHolder.mItemContentEdit.setSelection(mItemCurHolder.mItemContentEdit.getText().toString()
							.length());

					mItemCurHolder.mItemContentEdit.addTextChangedListener(mTextWatcher);
					mItemCurHolder.mItemContentEdit.setOnEditorActionListener(mEditorActionListener);
					mItemCurHolder.mItemContentEdit.setOnKeyListener(mEditTextOnKeyListener);
					InputMethodManager mng = (InputMethodManager) EthernetModifyActivity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.showSoftInput(mItemCurHolder.mItemContentEdit, InputMethodManager.SHOW_IMPLICIT);
				}else{
					Utils.startListFocusAnimation(EthernetModifyActivity.this, mLauncherFocusView, R.anim.list_focus_anim);
				}
			}
		}

	};

	View.OnKeyListener mItemListOnKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				System.out.println("item posion=" + mItemListCurPosition);

				if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN 
						|| keyCode == KeyEvent.KEYCODE_DPAD_UP){
					
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
				if (mItemCurEntity == null || mItemCurHolder == null) {
					if (DEBUG)
						Log.e(TAG, "item cur entity null");
					return false;
				}
				if (mItemCurEntity.getItemCategory() != EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT) {
					return false;
				}

				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					if (ETHERNET_MODIFY_ITEM_IPV4 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_MODIFY_ITEM_IPV4_MANUAL_ADJUST == mItemListCurPosition) {
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
					} else if (ETHERNET_MODIFY_ITEM_IPV6 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_MODIFY_ITEM_IPV6_MANUAL_ADJUST == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					}
					mItemCurHolder.mItemLeftArrow.setImageResource(R.drawable.left_arrow_selected);
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_arrow_unselected);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					if (ETHERNET_MODIFY_ITEM_IPV4 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_MODIFY_ITEM_IPV4_MANUAL_ADJUST == mItemListCurPosition) {
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
					} else if (ETHERNET_MODIFY_ITEM_IPV6 == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[0]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
						}
					} else if (ETHERNET_MODIFY_ITEM_IPV6_MANUAL_ADJUST == mItemListCurPosition) {
						if (EthernetItemEntity.SELECT_OFF == mItemCurEntity.getItemSelectFlag()) {
							mItemCurHolder.mItemContentTv.setText(mItemCurEntity.getItemContents()[1]);
							mItemCurEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
						} else if (EthernetItemEntity.SELECT_ON == mItemCurEntity.getItemSelectFlag()) {
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

	private void initItemList() {
		ArrayList<EthernetItemEntity> itemList = new ArrayList<EthernetItemEntity>();
		String[] item = getResources().getStringArray(R.array.ethernet_modify_net_item_list);
		String[] onOff = getResources().getStringArray(R.array.on_off);
		for (int i = 0; i < item.length; i++) {
			EthernetItemEntity entity = new EthernetItemEntity();
			entity.setItemTitle(item[i]);
			switch (i) {
			case ETHERNET_MODIFY_ITEM_IPV4:
			case ETHERNET_MODIFY_ITEM_IPV4_MANUAL_ADJUST:
			case ETHERNET_MODIFY_ITEM_IPV6:
			case ETHERNET_MODIFY_ITEM_IPV6_MANUAL_ADJUST:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_SELECT);
				entity.setItemContents(onOff);
				break;
			case ETHERNET_MODIFY_ITEM_IPV4_IP_ADDRESS:
			case ETHERNET_MODIFY_ITEM_IPV4_NET_MASK:
			case ETHERNET_MODIFY_ITEM_IPV4_GATEWAY:
			case ETHERNET_MODIFY_ITEM_IPV4_DNS:
			case ETHERNET_MODIFY_ITEM_IPV6_IP_ADDRESS:
			case ETHERNET_MODIFY_ITEM_IPV6_NET_MASK:
			case ETHERNET_MODIFY_ITEM_IPV6_GATEWAY:
			case ETHERNET_MODIFY_ITEM_IPV6_DNS:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_EDIT);
				break;
			default:
				break;
			}
			itemList.add(entity);
		}
		mModifyItemlistAdapter = new EthernetModifyAdapter(EthernetModifyActivity.this, itemList);
		mItemModifyList.setAdapter(mModifyItemlistAdapter);
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
			holder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_selected);
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
		case ETHERNET_MODIFY_ITEM_IPV4:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			break;
		case ETHERNET_MODIFY_ITEM_IPV4_MANUAL_ADJUST:
			if (isAutoFlag) {
				holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			} else {
				holder.mItemContentTv.setText(itemEntity.getItemContents()[1]);
				itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_ON);
			}
			break;
		case ETHERNET_MODIFY_ITEM_IPV4_IP_ADDRESS:
			holder.mItemContentEdit.setText(mV4IPAddress);
			break;
		case ETHERNET_MODIFY_ITEM_IPV4_NET_MASK:
			holder.mItemContentEdit.setText(mV4NetmaskAddress);
			break;
		case ETHERNET_MODIFY_ITEM_IPV4_GATEWAY:
			holder.mItemContentEdit.setText(mV4GatewayAddress);
			break;
		case ETHERNET_MODIFY_ITEM_IPV4_DNS:
			holder.mItemContentEdit.setText(mV4DNSAddress);
			break;
		case ETHERNET_MODIFY_ITEM_IPV6:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			break;
		case ETHERNET_MODIFY_ITEM_IPV6_MANUAL_ADJUST:
			holder.mItemContentTv.setText(itemEntity.getItemContents()[0]);
			itemEntity.setItemSelectFlag(EthernetItemEntity.SELECT_OFF);
			break;
		case ETHERNET_MODIFY_ITEM_IPV6_IP_ADDRESS:
			holder.mItemContentEdit.setText("");
			break;
		case ETHERNET_MODIFY_ITEM_IPV6_NET_MASK:
			holder.mItemContentEdit.setText("");
			break;
		case ETHERNET_MODIFY_ITEM_IPV6_GATEWAY:
			holder.mItemContentEdit.setText("");
			break;
		case ETHERNET_MODIFY_ITEM_IPV6_DNS:
			holder.mItemContentEdit.setText("");
			break;
		default:
			break;
		}
	}

	class EthernetModifyAdapter extends BaseAdapter {
		LayoutInflater mInflater = null;
		private Context mContext = null;
		private ArrayList<EthernetItemEntity> mList = null;

		public EthernetModifyAdapter(Context context, ArrayList<EthernetItemEntity> list) {
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

		mToast = Toast.makeText(EthernetModifyActivity.this, content, Toast.LENGTH_SHORT);
		mToast.show();
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
				mEthernetDataEntity.getEthernetDhcpInfo(true);
				initEthernetData(false);
				mModifyItemlistAdapter.notifyDataSetChanged();
				mEthernetDataEntity.updateEthDevInfo(true, null, null, mV4DNSAddress, null);
				break;
			case EthernetDataEntity.MSG_IPV4_ADJUST_STATIC:
				if (DEBUG) {
					Log.e(TAG, "set ip:" + mV4IPAddress + " mask:" + mV4NetmaskAddress + " gw:" + mV4GatewayAddress
							+ " dns:" + mV4DNSAddress);
				}

				mModifyItemlistAdapter.notifyDataSetChanged();
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
			default:
				break;
			}
		}
	};

	View.OnKeyListener mEditTextOnKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (DEBUG) {
				Log.e(TAG, "I'm here......................onkey");
			}
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
					if (mItemCurHolder != null) {
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
					updateEthernetInfo(getItemEditText());
					InputMethodManager mng = (InputMethodManager) EthernetModifyActivity.this
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
			if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_IP_ADDRESS) {
				mItemCurHolder.mItemContentEdit.setText(mV4IPAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4IPAddress.length());
			} else if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_DNS) {
				mItemCurHolder.mItemContentEdit.setText(mV4DNSAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4DNSAddress.length());
			} else if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_GATEWAY) {
				mItemCurHolder.mItemContentEdit.setText(mV4GatewayAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4GatewayAddress.length());
			} else if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_NET_MASK) {
				mItemCurHolder.mItemContentEdit.setText(mV4NetmaskAddress);
				mItemCurHolder.mItemContentEdit.setSelection(mV4NetmaskAddress.length());
			}
		}
	}

	private void updateEthernetInfo(String str) {
		if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_IP_ADDRESS) {
			mV4IPAddress = str;
		} else if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_DNS) {
			mV4DNSAddress = str;
		} else if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_GATEWAY) {
			mV4GatewayAddress = str;
		} else if (mItemListCurPosition == ETHERNET_MODIFY_ITEM_IPV4_NET_MASK) {
			mV4NetmaskAddress = str;
		}
		mHandler.sendEmptyMessage(EthernetDataEntity.MSG_IPV4_ADJUST_STATIC);
	}

}
