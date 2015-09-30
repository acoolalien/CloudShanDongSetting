package com.shandong.cloudtv.settings.widget;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shandong.cloudtv.settings.R;
import com.shandong.cloudtv.settings.util.ImageSharePreference;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.widget.ModeSettingDialog.MyAdapter.ViewHolder;
public class ModeSettingDialog extends Dialog{
    
	private int textStatus = PROFILES_MODE_NULL;
	private int currentMode = PROFILES_MODE_NORMAL;
	private Context mContext;
	private LayoutInflater mFactory = null;
    private View mView = null;
    private String TAG = "YQB";
    private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private ImageSharePreference mPreference = null;
	private AudioManager mAudioManager ;
	private LauncherFocusView focusView;
	
	private boolean bfocusViewInitStatus = true;
	private ListView listConnectDevice;
	private MyAdapter listItemAdapter;
	private ViewHolder viewHolder = null;
	private int mItemListCurPosition = -1;
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	/* 按键音开 */
	private int AUDIO_ON = 1;
	/* 按键音关 */
	private int AUDIO_OFF = 0;
	private static final int PROFILES_MODE_NULL = -1;
	private static final int PROFILES_MODE_NORMAL = 0;
	private static final int PROFILES_MODE_THEATER = 1;
	private static final int PROFILES_MODE_MUSIC = 2;
	private static final int PROFILES_MODE_GAME = 3;
	private static final int PROFILES_MODE_GORGEOUS = 4;
	private static final int PROFILES_MODE_SELF = 5;
	private static final int PROFILES_MODE_SIZE = 6;
	private ArrayList<HashMap<String, Object>> arrayListItem = new ArrayList<HashMap<String, Object>>();
	private View mItemListCurView = null;
	private int mIs_save = 1;
	private int mFinalSelection = -1;
	private int mFirstSelection = -1;
	private long mKeyDownTime = 0L;
	private int currentValue[];
	private long time = 0L;
	
	public ModeSettingDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}
	
	
	public ModeSettingDialog(Context context) {
		super(context, R.style.CustomProgressNewDialog);
		mContext = context;
		mFactory = LayoutInflater.from(mContext);
		mView = mFactory.inflate(R.layout.activity_mode_setting, null);
		
		final WindowManager.LayoutParams WMLP = this.getWindow().getAttributes();
		WMLP.gravity = Gravity.LEFT;
		WMLP.width =1280;
		WMLP.height =1080;
		WMLP.type = WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY;
		WMLP.privateFlags |=WindowManager.LayoutParams.PRIVATE_FLAG_FORCE_SHOW_NAV_BAR;
		this.getWindow().setAttributes(WMLP);
		/*this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);*/
		
		/*final WindowManager.LayoutParams WMLP = this.getWindow().getAttributes();
		WMLP.gravity = Gravity.CENTER;
		this.getWindow().setAttributes(WMLP);*/
		
		this.setContentView(mView);
	}
	@Override
	protected void onCreate(Bundle bundle){
		Log.i(TAG, "----Dialog----onCreate-");
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		initWidget();
	}
	@Override
	protected void onStart(){
		Log.i(TAG, "----Dialog----onStart");
		currentMode = mPreference.getModel();
		textStatus = mPreference.getModel();
		mFirstSelection = textStatus;
		mFinalSelection = textStatus;
		listConnectDevice.setSelection(textStatus);
		super.onStart();
	}
	@Override
	protected void onStop(){
		Log.i(TAG, "----Dialog----onStop");
		super.onStop();
	}
	
	
	private void initWidget(){
		mPreference = new ImageSharePreference(mContext.getApplicationContext());
		focusView = (LauncherFocusView) findViewById(R.id.focusview_1);
		focusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				Log.i(TAG, "-----AnimateEnd----"+ (System.currentTimeMillis()-time));
				mFocusAnimationEndFlag = true;
				listTextColorSet();
				/*//设置静音
				int audioStatus = mTV.GetAudioMuteKeyStatus();
				    mTV.SetAudioMuteKeyStatus(1);
				
				//设置生效
				Utils.SetAudioMode(mContext,mItemListCurPosition);
				//恢复声音	
			if(!isSepOrNot){
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				//mTV.SetAudioMuteKeyStatus(0);
				mTV.SetAudioMuteKeyStatus(audioStatus);
				
				setPqMode(mItemListCurPosition);
					
				mPreference.setModel(mItemListCurPosition);
				mFinalSelection = mItemListCurPosition;
				
				 Log.i(TAG, "----the sound mode D55s----"+manager.setMaxxProfilesMode());
               // Log.i(TAG, "----the sound mode D32L----"+tv.GetAudioSoundMode());
				int value[] = new int[7];
				
				value[0] = mTV.GetColorTemperature(mSource);
				value[1] = mTV.GetBrightness(mSource);
				value[2] = mTV.GetContrast(mSource);
				value[3] = mTV.GetSaturation(mSource);
				value[4] = mTV.GetHue(mSource);
				value[5] = mTV.GetSharpness(mSource);
				value[6] = mTV.GetBacklight(mSource);
				for(int i =0;i<7;i++){
					Log.i(TAG, "-------the new------"+value[i]+"---PqMode----"+mTV.GetPQMode(mSource));
				}*/
			}

			@Override
			public void OnAnimateStart(View currentFocusView) {
				// TODO Auto-generated method stub
				mFocusAnimationEndFlag = false;
			}

		});
		//bfocusViewInitStatus = false;
		listConnectDevice = (ListView) findViewById(R.id.listView_profiles_show);
		listConnectDevice.requestFocus();
		listConnectDevice.setVisibility(View.VISIBLE);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemTitle", mContext.getResources().getString(R.string.listview_profiles_mode_title_1));
		map.put("ItemDescription",
				mContext.getResources().getString(R.string.listview_profiles_mode_discription_1));
		map.put("ItemFlag", PROFILES_MODE_NORMAL);
		arrayListItem.add(map);

		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("ItemTitle", mContext.getResources()
				.getString(R.string.listview_profiles_mode_title_2));
		map1.put("ItemDescription",
				mContext.getResources().getString(R.string.listview_profiles_mode_discription_2));
		map1.put("ItemFlag", PROFILES_MODE_THEATER);
		arrayListItem.add(map1);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("ItemTitle", mContext.getResources()
				.getString(R.string.listview_profiles_mode_title_3));
		map2.put("ItemDescription",
				mContext.getResources().getString(R.string.listview_profiles_mode_discription_3));
		map2.put("ItemFlag", PROFILES_MODE_MUSIC);
		arrayListItem.add(map2);

		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("ItemTitle", mContext.getResources()
				.getString(R.string.listview_profiles_mode_title_4));
		map3.put("ItemDescription",
				mContext.getResources().getString(R.string.listview_profiles_mode_discription_4));
		map3.put("ItemFlag", PROFILES_MODE_GAME);
		arrayListItem.add(map3);

		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("ItemTitle", mContext.getResources()
				.getString(R.string.listview_profiles_mode_title_5));
		map4.put("ItemDescription",
				mContext.getResources().getString(R.string.listview_profiles_mode_discription_5));
		map4.put("ItemFlag", PROFILES_MODE_GORGEOUS);
		arrayListItem.add(map4);
		
		listItemAdapter = new MyAdapter(mContext, arrayListItem);

		listConnectDevice.setAdapter(listItemAdapter);

		listConnectDevice.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mItemListCurView = view;
				mItemListCurPosition = position;
				time = System.currentTimeMillis();
                Log.i(TAG, "---onselect---position--"+position+"---view--"+view);
				if (bfocusViewInitStatus) {
					focusView.initFocusView(view, false, 0);
				}
				
				/*if (viewHolder != null) {
					if (viewHolder.txTitle != null) {
						viewHolder.txTitle.setTextColor(0xFFFFFFFF);
						viewHolder.image.setImageDrawable(mContext.getResources().getDrawable(
								R.drawable.settings_profiles_item_ns));
					}
				}*/
				viewHolder = (ViewHolder) view.getTag();
				
				if (mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN) {

					if (position < 5
							|| position > listConnectDevice.getCount() - 2
							|| (listConnectDevice.getFirstVisiblePosition() == 0 && view
									.getTop() < (view.getHeight() * 4))
							|| (listConnectDevice.getFirstVisiblePosition() != 0 && view
									.getTop() < view.getHeight() * 5)) {
						//focusView.moveTo(view);
					} else {
						listTextColorSet();
						listConnectDevice.setSelectionFromTop(position,
								view.getTop() - view.getHeight());

					}
				} else if (mCurKeycode == KeyEvent.KEYCODE_DPAD_UP) {
					if ((mItemListCurPosition == 0 || listConnectDevice
							.getFirstVisiblePosition() == 0 && view.getTop() > (view.getHeight()))
							|| (listConnectDevice.getFirstVisiblePosition() != 0 && view
									.getTop() >= view.getHeight())) {
						//focusView.moveTo(view);
					} else {
						listTextColorSet();
						listConnectDevice.setSelectionFromTop(mItemListCurPosition,
								view.getHeight());
					}

				} else if (mCurKeycode == KeyEvent.KEYCODE_PAGE_UP
						|| mCurKeycode == KeyEvent.KEYCODE_PAGE_DOWN) {
					//focusView.moveTo(view);
				}
				/*if (bfocusViewInitStatus) {
					bfocusViewInitStatus = false;
					mTextColorChangeFlag = true;
					listTextColorSet();
				}*/
				// fixed the keyboard repeat mode
				/*if (!mTextColorChangeFlag && mFocusAnimationEndFlag) {
					if ((mItemListCurPosition == 0 || mItemListCurPosition == listConnectDevice
							.getCount() - 1)) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				/*textStatus = -1;
				listItemAdapter.notifyDataSetChanged();*/
			}
		});

		listConnectDevice.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				mCurKeycode = keyCode;
				/*if (event.getAction() == KeyEvent.ACTION_DOWN) {
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
					}else if(keyCode == KeyEvent.KEYCODE_BACK){
						Log.i(TAG, "-----get the keycode_back-----");
						// mVideoView.stopPlayback();						 
						 if (mFinalSelection == mFirstSelection) {
								Toast.makeText(mContext, mContext.getResources().getString(R.string.modify_not), Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(mContext, mContext.getResources().getString(R.string.modify_success), Toast.LENGTH_SHORT).show();
							}
					}
				}
				if (event.getAction() == KeyEvent.ACTION_UP) {
					if (!mTextColorChangeFlag) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
					
				}*/
				return false;
			}
			
		});
		
		listConnectDevice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView, View view, int position,
					long paramLong) {
				
					/*Utils.SetAudioMode(mContext, position);
					setPqMode(position);
					mPreference.setModel(position);
					mFinalSelection = position;*/
			}

		});
	}
	
	private void listTextColorSet() {
		if (viewHolder != null && mTextColorChangeFlag&&mFocusAnimationEndFlag) {
			mTextColorChangeFlag = false;
			//viewHolder.txTitle.setTextColor(mContext.getResources().getColor(R.color.white));
			/*viewHolder.image.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.settings_profiles_item_s));*/
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Log.i("YQB", "-----dialog dismiss----");
			this.dismiss();
		}
		return false;
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<HashMap<String, Object>> mData;

		public MyAdapter(Context context, ArrayList<HashMap<String, Object>> array) {
			mInflater = LayoutInflater.from(context);
			mData = array;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			// convertView为null锟斤拷时锟斤拷锟绞硷拷锟絚onvertView锟斤拷
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.dialog_item, null);
				holder.txTitle = (TextView) convertView
						.findViewById(R.id.textView_profiles_items_name);
				holder.textDescription = (TextView) convertView
						.findViewById(R.id.textView_profiles_items_description);
				holder.image = (ImageView) convertView
						.findViewById(R.id.imageView_profiles_items_choice);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			//holder.txTitle.setText(mData.get(position).get("ItemTitle").toString());
			/*holder.textDescription.setText(mData.get(position).get("ItemDescription").toString());
			holder.flag = (Integer) mData.get(position).get("ItemFlag");
			if (currentMode == holder.flag)
				holder.image.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.settings_profiles_item_s));
			else
				holder.image.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.settings_profiles_item_ns));

			if (textStatus == position) {
				holder.txTitle.setTextColor(0xFFFFFFFF);
			} else {
				holder.txTitle.setTextColor(0xFFFFFFFF);
			}*/
			return convertView;
		}

		public final class ViewHolder {
			public TextView txTitle;
			public TextView textDescription;
			public int flag;
			public ImageView image;
		}
	}
	
	/*private void setPqMode(int position) {
		if (position == 0) {
			for(SourceInput_Type source : souceValue)
			mTV.SetPQMode(Tv.Pq_Mode.PQ_MODE_STANDARD, source, mIs_save);
		} else if (position == 1) {
			for(SourceInput_Type source : souceValue)
			mTV.SetPQMode(Tv.Pq_Mode.PQ_MODE_MOVIE, source, mIs_save);
		} else if (position == 2) {
			for(SourceInput_Type source : souceValue)
			mTV.SetPQMode(Tv.Pq_Mode.PQ_MODE_STANDARD, source, mIs_save);
		} else if (position == 3) {
			for(SourceInput_Type source : souceValue)
			mTV.SetPQMode(Tv.Pq_Mode.PQ_MODE_SOFTNESS, source, mIs_save);
		} else if (position == 4) {
			for(SourceInput_Type source : souceValue)
			mTV.SetPQMode(Tv.Pq_Mode.PQ_MODE_COLORFUL, source, mIs_save);
		} else if (position == 5) {
			currentValue = new int[7];
	    	currentValue[0] = mPreference.getColorTemperature();
	    	currentValue[1] = mPreference.getBrightness();
	    	currentValue[2] = mPreference.getContrast();
	    	currentValue[3] = mPreference.getSaturation();
	    	currentValue[4] = mPreference.getHue();
	    	currentValue[5] = mPreference.getSharpness();
	    	currentValue[6] = mPreference.getBacklight();	
	    	setMyMode();
		}
	}
	//自定义模式的设置方法
		private void setMyMode(){
			
			Tv.color_temperature temperature = null;
			if (currentValue[0] == 0) {
				temperature = Tv.color_temperature.COLOR_TEMP_STANDARD;
			} else if (currentValue[0] == 1) {
				temperature = Tv.color_temperature.COLOR_TEMP_WARM;
			} else if (currentValue[0] == 2) {
				temperature = Tv.color_temperature.COLOR_TEMP_COLD;
			} else if (currentValue[0] == 3) {
				temperature = Tv.color_temperature.COLOR_TEMP_MAX;
			}
			for(SourceInput_Type source :souceValue){
				setUserMode(source);
			mTV.SetColorTemperature(temperature, source, mIs_save);
			mTV.SetBrightness(currentValue[1], source, mIs_save);
			mTV.SetContrast(currentValue[2], source, mIs_save);
			mTV.SetSaturation(currentValue[3], source,mTV.GetCurrentSignalInfo().fmt, mIs_save);
			mTV.SetHue(currentValue[4], source,mTV.GetCurrentSignalInfo().fmt, mIs_save);
			mTV.SetSharpness(currentValue[5], source, 1, 0, mIs_save);
			if (SystemProperties.getInt("persist.tv.auto_backlight", -1) == 1) {
				mTV.SaveBacklight(currentValue[6], source);
			} else {
				mTV.SetBacklight(currentValue[6], source, mIs_save);
				SystemProperties.set("tv.real_backlight", "" + currentValue[6]);
			}
			}
		}
		
		private void setUserMode(SourceInput_Type source){
			if(mTV.GetPQMode(source)!=Tv.Pq_Mode.PQ_MODE_USER.toInt()){
				mTV.SetPQMode(Tv.Pq_Mode.PQ_MODE_USER, source, mIs_save);
			}
		}*/
}
