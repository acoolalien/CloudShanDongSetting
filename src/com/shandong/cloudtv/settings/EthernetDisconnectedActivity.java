package com.shandong.cloudtv.settings;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;







import com.shandong.cloudtv.settings.ethernet.EthernetItemEntity;
import com.shandong.cloudtv.settings.ethernet.EthernetItemHolder;
import com.shandong.cloudtv.settings.pppoe.PppoeDataEntity;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.wifi.WifiAccessPoint;
import com.shandong.cloudtv.settings.wifi.WifiInfoConfigEntity;

public class EthernetDisconnectedActivity extends Activity {
	private static final boolean DEBUG = true;
	private static final String TAG = "EthernetDisconnectedActivity";
	private ListView mDisItemList = null;
	private static final int ETHERNET_DIS_ITEM_USE_WIFI = 0;
	private static final int ETHERNET_DIS_ITEM_USE_PPPOE = 0;//1;
	private static final int ETHERNET_DIS_ITEM_MODIFY_NET = 1;//2;
	private static final int ETHERNET_DIS_ITEM_RECONNECT = 2;//3;
	private static final int ETHERNET_DIS_ITEM_CHECK_NET = 2;//4;
	private EthernetItemEntity mItemCurEntity = null;
	private EthernetItemHolder mItemCurHolder = null;
	
	private View mItemListCurView = null;
	private LauncherFocusView mLauncherFocusView = null;
	private boolean mIsFirstIn = true;
	private int mListItemCurPosition = 0;
	
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private long mKeyDownTime = 0L;
	
	private boolean isNativeGo = false;
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.ethernet_disconnected_main);
		initView();
		regeditReceiver();
		
		isNativeGo = getIntent().getBooleanExtra("isNativeGo", true);
		Log.v(TAG, "isNativeGo=="+isNativeGo);
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unRegeditReceiver();
	}
	
 

	private void initView(){
		mIsFirstIn = true;
		
		mDisItemList = (ListView)findViewById(R.id.ethernet_dis_list);
		mDisItemList.setOnItemClickListener(mItemListOnItemClickListener);
		mDisItemList.setOnItemSelectedListener(mOnItemSelectedListener);
		mDisItemList.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				
				if(event.getAction() == KeyEvent.ACTION_UP){
					if(!mTextColorChangeFlag){
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}else if(event.getAction() == KeyEvent.ACTION_DOWN){
					if(event.getRepeatCount() == 0){
						mTextColorChangeFlag = true;
						mKeyDownTime = event.getDownTime();
					}else {
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
				return false;
			}
		});
		mDisItemList.setLayoutAnimationListener(new Animation.AnimationListener() {
			
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
				if(mDisItemList!=null){
					//mDisItemList.setDivider(getResources().getDrawable(R.drawable.under_line));
				}
			}
		});
		
		mLauncherFocusView = (LauncherFocusView)findViewById(R.id.ethernet_disconnect_focus_view);
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
		
		initItemList();
	}
	
	private void initItemList(){
		ArrayList<EthernetItemEntity> itemList = new ArrayList<EthernetItemEntity>();
	    String[] item = getResources().getStringArray(R.array.ethernet_disconnected_item_list);
		for(int i=0;i<item.length;i++){
			EthernetItemEntity entity = new EthernetItemEntity();
			entity.setItemTitle(item[i]);
			switch (i) {
			//case ETHERNET_DIS_ITEM_USE_WIFI:
			case ETHERNET_DIS_ITEM_USE_PPPOE:
			case ETHERNET_DIS_ITEM_MODIFY_NET:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER);
				break;
//			case ETHERNET_DIS_ITEM_RECONNECT:
			case ETHERNET_DIS_ITEM_CHECK_NET:
				entity.setItemCategory(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_NULL);
				break;
			default:
				break;
			}
			itemList.add(entity);
		}
		mDisItemList.setAdapter(new EthernetDisconnectAdapter(EthernetDisconnectedActivity.this, itemList));
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

	AdapterView.OnItemClickListener mItemListOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			if(DEBUG)
				Log.e(TAG,"item click position="+position);
			/*if(ETHERNET_DIS_ITEM_USE_WIFI == position){
				Intent intent = new Intent(EthernetDisconnectedActivity.this, WifiConnectActivity.class);
				startActivity(intent);
			}else 
			
			*/
			if(ETHERNET_DIS_ITEM_USE_PPPOE == position){
				Utils.setApplicationBGBitmap(EthernetDisconnectedActivity.this);
				Intent intent = new Intent(EthernetDisconnectedActivity.this, PppoeConnectActivity.class);
				intent.putExtra(PppoeDataEntity.PPPOE_EXTRA_STATE, PppoeDataEntity.PPPOE_EXTRA_STATE_CONNECT);
				startActivity(intent);
				finish();
			}else if(ETHERNET_DIS_ITEM_MODIFY_NET == position){
				Intent intent = new Intent(EthernetDisconnectedActivity.this,EthernetModifyActivity.class);
				startActivity(intent);
			}else if(ETHERNET_DIS_ITEM_CHECK_NET == position){
				Intent intent = new Intent(EthernetDisconnectedActivity.this, CheckNetworkActivity.class);
				startActivity(intent);
			}
		}
		
	};
	
	AdapterView.OnItemSelectedListener mOnItemSelectedListener  = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			//list focus view 
			mItemListCurView = view;
			mListItemCurPosition = position;
			if(mIsFirstIn){
				mLauncherFocusView.initFocusView(mItemListCurView, false, 0f);
			}
			mLauncherFocusView.moveTo(mItemListCurView);
			
			//text color clear
			if(mItemCurHolder!=null){
				mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
				if(mItemCurEntity!=null){
				if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER 
							== mItemCurEntity.getItemCategory()){
						mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_unselected);
					}
				}
			}
			mItemCurEntity = (EthernetItemEntity)((EthernetDisconnectAdapter)mDisItemList.getAdapter()).getItem(position);
			mItemCurHolder = (EthernetItemHolder)view.getTag();
			
			//text color set in mLuncherFocusView onAnimationEnd()
			if(mIsFirstIn){
				mIsFirstIn = false;
				mTextColorChangeFlag = true;
				listTextColorSet();
			}
			if(!mTextColorChangeFlag && mFocusAnimationEndFlag){
				if(mListItemCurPosition == 0 
						|| mListItemCurPosition==mDisItemList.getCount()-1){
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
		if(mItemCurHolder!=null && mTextColorChangeFlag&& mFocusAnimationEndFlag){
			mTextColorChangeFlag = false;
			mItemCurHolder.mItemTitle.setTextColor(getResources().getColor(R.color.settings_ffffff));
			if(mItemCurEntity!=null){
				if(EthernetItemEntity.ETHERNET_CONNECT_ITEM_CATEGORY_ENTER 
						== mItemCurEntity.getItemCategory()){
					mItemCurHolder.mItemRightArrow.setImageResource(R.drawable.right_enter_point_selected);
				}
			}
		}
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
		switch (position) {
		default:
			break;
		}
	}
	
	class EthernetDisconnectAdapter extends BaseAdapter{
		LayoutInflater mInflater = null;
		private Context mContext = null;
		private ArrayList<EthernetItemEntity> mList = null;
		
		public EthernetDisconnectAdapter(Context context,ArrayList<EthernetItemEntity> list){
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
                 Log.v(TAG, "eth status=="+Utils.checkEthState());
                 Log.v(TAG, "network change>>>>>"+Utils.checkNetworkIsActive(EthernetDisconnectedActivity.this));
                 ////如果是开机向导执行的，连接上ap,就直接关闭当前窗口
                 if(!isNativeGo && Utils.checkNetworkIsActive(EthernetDisconnectedActivity.this)){
     				finish();
     			 }
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
