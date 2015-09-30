package com.shandong.cloudtv.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.shandong.cloudtv.settings.ProfilesSettingsActivity.MyAdapter.ViewHolder;
import com.shandong.cloudtv.settings.util.ImageSharePreference;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

@SuppressLint("NewApi")
public class ProfilesSettingsActivity extends Activity {
	private static final String TAG = "ProfilesSettingsActivity";
	private Context mContext;
	private MyAdapter listItemAdapter;
	private long mKeyDownTime = 0L;
	private int mItemListCurPosition = -1;
	private boolean aniFlag = false;
	private ListView listConnectDevice;
	/* 按键音开 */
	private int AUDIO_ON = 1;
	/* 按键音关 */
	private int AUDIO_OFF = 0;
	private VideoView mVideoView;
	private LauncherFocusView focusView;
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	private RelativeLayout layoutTitle;
	private boolean bfocusViewInitStatus;
	private AudioManager mAudioManager ;
	private boolean mTextColorChangeFlag = false;
	private boolean mFocusAnimationEndFlag = false;
	private ArrayList<HashMap<String, Object>> arrayListItem = new ArrayList<HashMap<String, Object>>();
	private int textStatus = PROFILES_MODE_NULL;
	private int currentMode = PROFILES_MODE_NORMAL;
	private ImageSharePreference mPreference = null;
	private static final int PROFILES_MODE_NULL = -1;
	private static final int PROFILES_MODE_NORMAL = 0;
	private static final int PROFILES_MODE_THEATER = 1;
	private static final int PROFILES_MODE_MUSIC = 2;
	private static final int PROFILES_MODE_GAME = 3;
	private static final int PROFILES_MODE_GORGEOUS = 4;
	private static final int PROFILES_MODE_SELF = 5;
	private static final int PROFILES_MODE_SIZE = 6;
	private int currentTime;
	private int musicDuration;
	private int reduceTime = 1000;
	private int mFinalSelection = -1;
	private int mFirstSelection = -1;
	public Timer timer = new Timer();
	public TimerTask timerTask;
	private View mItemListCurView = null;
	private ViewHolder viewHolder = null;
	private int mIs_save = 1;
	private  boolean mPrepareFlage=false;
	private boolean mFirstFlag = true;
	private boolean mHome = false;
	private int currentValue[];
    private boolean isSepOrNot = false;
	private String SETTING_TO_MODE="com.hiveview.cloudtv.SETTING_TO_MODE";
    
    
	private void timeSetter(int time) {
		timerTask = new TimerTask() {
			@Override
			public void run() {
				Message m = new Message();
				m.what = 1;
				handler.sendMessage(m);
			}

		};
		// 设置定时器发送消息去激活搜索
		timer.schedule(timerTask, 0, time);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				//Log.e("huxing", "huxing--------handler----1");
				if (mVideoView.isPlaying()) {
					mVideoView.seekTo(0);
				} else {
					//mVideoView.start();
				}
			}

			if (msg.what == 2) {
				// AnimationSet set = new AnimationSet(false);

				Animation animation;
				animation = AnimationUtils.loadAnimation(mContext, R.anim.list_profile_anim);
				LayoutAnimationController controller = new LayoutAnimationController(animation, 1);
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation paramAnimation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation paramAnimation) {
						// TODO Auto-generated method stub

						focusView.initFocusView(mItemListCurView, false, 0);

					}

					@Override
					public void onAnimationRepeat(Animation paramAnimation) {
						// TODO Auto-generated method stub

					}
				});
				listConnectDevice.setVisibility(View.VISIBLE);
				listConnectDevice.setLayoutAnimation(controller);
				listConnectDevice.requestFocus();

				Animation animation1 = AnimationUtils.loadAnimation(mContext,
						R.anim.title_profile_anim);

				LayoutAnimationController controller1 = new LayoutAnimationController(animation1, 1);

				layoutTitle.setVisibility(View.VISIBLE);
				layoutTitle.setLayoutAnimation(controller1);
			}
		};
	};

	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profilessettings);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		initWidget();
		initMain();
		playModeVideo(textStatus);
	}

	private void initWidget() {
		mPreference = new ImageSharePreference(getApplicationContext());
		focusView = (LauncherFocusView) findViewById(R.id.focusview_1);
		focusView.setFocusBackgroundResource(R.drawable.new_profile_focus);
		focusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

			@Override
			public void OnAnimateEnd(View currentFocusView) {
				// TODO Auto-generated method stub
				
				mFocusAnimationEndFlag = true;
				listTextColorSet();
				// TODO 设置情景模式
				//设置静音
				if(aniFlag)	{
			if(!isSepOrNot){
				try {
					Thread.sleep(80);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				//mTV.SetAudioMuteKeyStatus(0);
				
				Intent intent = new Intent();
				intent.setAction(SETTING_TO_MODE);
				intent.putExtra("mode", mItemListCurPosition);
				mContext.sendBroadcast(intent);	
				mPreference.setModel(mItemListCurPosition);
				mFinalSelection = mItemListCurPosition;
				
               // Log.i(TAG, "----the sound mode D32L----"+tv.GetAudioSoundMode());
				int value[] = new int[7];
				
				}
			}

			@Override
			public void OnAnimateStart(View currentFocusView) {
				// TODO Auto-generated method stub
				mFocusAnimationEndFlag = false;
			}

		});
		layoutTitle = (RelativeLayout) findViewById(R.id.layout_title);

		bfocusViewInitStatus = false;
		mVideoView = (VideoView) findViewById(R.id.videoView1);
		mVideoView.requestFocus();
		mVideoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mVideoView.start();
				Log.i(TAG, "------Video VIEW-----start---");
				currentTime = 0;
				musicDuration = mVideoView.getDuration();
				handler.sendEmptyMessage(1);
				handler.sendEmptyMessage(2);
				timeSetter(musicDuration - reduceTime);
				mPrepareFlage =true;

			}
		});

		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				// playModeVideo(textStatus);
			}

		});
		
		listConnectDevice = (ListView) findViewById(R.id.listView_profiles_show);
		listConnectDevice.requestFocus();
		listConnectDevice.setVisibility(View.GONE);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemTitle", this.getResources().getString(R.string.listview_profiles_mode_title_1));
		map.put("ItemDescription",
				this.getResources().getString(R.string.listview_profiles_mode_discription_1));
		map.put("ItemFlag", PROFILES_MODE_NORMAL);
		arrayListItem.add(map);

		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("ItemTitle", this.getResources()
				.getString(R.string.listview_profiles_mode_title_2));
		map1.put("ItemDescription",
				this.getResources().getString(R.string.listview_profiles_mode_discription_2));
		map1.put("ItemFlag", PROFILES_MODE_THEATER);
		arrayListItem.add(map1);

		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("ItemTitle", this.getResources()
				.getString(R.string.listview_profiles_mode_title_3));
		map2.put("ItemDescription",
				this.getResources().getString(R.string.listview_profiles_mode_discription_3));
		map2.put("ItemFlag", PROFILES_MODE_MUSIC);
		arrayListItem.add(map2);

		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("ItemTitle", this.getResources()
				.getString(R.string.listview_profiles_mode_title_4));
		map3.put("ItemDescription",
				this.getResources().getString(R.string.listview_profiles_mode_discription_4));
		map3.put("ItemFlag", PROFILES_MODE_GAME);
		arrayListItem.add(map3);

		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("ItemTitle", this.getResources()
				.getString(R.string.listview_profiles_mode_title_5));
		map4.put("ItemDescription",
				this.getResources().getString(R.string.listview_profiles_mode_discription_5));
		map4.put("ItemFlag", PROFILES_MODE_GORGEOUS);
		arrayListItem.add(map4);
		
		if(isSepOrNot){
		HashMap<String, Object> map5 = new HashMap<String, Object>();
		map5.put("ItemTitle", this.getResources()
				.getString(R.string.listview_profiles_mode_title_6));
		map5.put("ItemDescription",
				this.getResources().getString(R.string.listview_profiles_mode_discription_6));
		map5.put("ItemFlag", PROFILES_MODE_SELF);
		arrayListItem.add(map5);
		}

		listItemAdapter = new MyAdapter(this, arrayListItem);

		listConnectDevice.setAdapter(listItemAdapter);

		listConnectDevice.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mItemListCurView = view;
				mItemListCurPosition = position;
                
				if (bfocusViewInitStatus) {
					focusView.initFocusView(view, false, 0);
				}
				
				if (viewHolder != null) {
					if (viewHolder.txTitle != null) {
						viewHolder.txTitle.setTextColor(0xFFFFFFFF);
						viewHolder.image.setImageDrawable(getResources().getDrawable(
								R.drawable.settings_profiles_item_ns));
					}
				}
				viewHolder = (ViewHolder) view.getTag();
				
				if (mCurKeycode == KeyEvent.KEYCODE_DPAD_DOWN) {

					if (position < 5
							|| position > listConnectDevice.getCount() - 2
							|| (listConnectDevice.getFirstVisiblePosition() == 0 && view
									.getTop() < (view.getHeight() * 4))
							|| (listConnectDevice.getFirstVisiblePosition() != 0 && view
									.getTop() < view.getHeight() * 5)) {
						focusView.moveTo(view);
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
						focusView.moveTo(view);
					} else {
						listTextColorSet();
						listConnectDevice.setSelectionFromTop(mItemListCurPosition,
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
					if ((mItemListCurPosition == 0 || mItemListCurPosition == listConnectDevice
							.getCount() - 1)) {
						mTextColorChangeFlag = true;
						listTextColorSet();
					}
				}
								
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				textStatus = -1;
				listItemAdapter.notifyDataSetChanged();
			}
		});

		listConnectDevice.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				mCurKeycode = keyCode;
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
							|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
						aniFlag = true;
						if (event.getRepeatCount() == 0) {
							mTextColorChangeFlag = true;
							if(!(event.getDownTime() - mKeyDownTime>=Utils.KEYDOWN_TIME))
								return true;
							mKeyDownTime = event.getDownTime();
						} else {
								return true;
							
						}

						if (!mFocusAnimationEndFlag) {
							mTextColorChangeFlag = false;
						}
					}
					if(keyCode == KeyEvent.KEYCODE_BACK){
						Log.i(TAG, "-----get the keycode_back-----");
						 mVideoView.stopPlayback();						 
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
					
				}
				return false;
			}
		});
		listConnectDevice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView, View view, int position,
					long paramLong) {
				
					Intent intent = new Intent();
					intent.setAction(SETTING_TO_MODE);
					intent.putExtra("mode", mItemListCurPosition);
					mContext.sendBroadcast(intent);	
					mPreference.setModel(position);
					mFinalSelection = position;
			}

		});

	}

	

	private void initMain() {
		mContext = this;
	}

	private void listTextColorSet() {
		if (viewHolder != null && mTextColorChangeFlag&&mFocusAnimationEndFlag) {
			mTextColorChangeFlag = false;
			//viewHolder.txTitle.setTextColor(mContext.getResources().getColor(R.color.white));
			viewHolder.image.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.settings_profiles_item_s));
		}
	}

	private void playModeVideo(int mode) {
		//Uri mUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.backmovie);
		Uri mUri = Uri.parse("/system/media/backmovie.mp4");
		mVideoView.setVideoURI(mUri);
		Log.i(TAG, "-----set the video uri------");
	}

	

	private void setAudio(int OpenOrClose) {
		mAudioManager.loadSoundEffects();	
		Settings.System.putInt(getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED,
				OpenOrClose);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setAudio(AUDIO_OFF);
		currentMode = mPreference.getModel();
		textStatus = mPreference.getModel();
		mFirstSelection = textStatus;
		mFinalSelection = textStatus;
		listConnectDevice.setSelection(textStatus);
		if(mPrepareFlage){
			mVideoView.start();
		}
		
		super.onResume();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onPause() {
		setAudio(AUDIO_ON);	
		super.onPause();		
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		timer.cancel();
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
				convertView = mInflater.inflate(R.layout.list_profilessettings_items, null);
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

			holder.txTitle.setText(mData.get(position).get("ItemTitle").toString());
			holder.textDescription.setText(mData.get(position).get("ItemDescription").toString());
			holder.flag = (Integer) mData.get(position).get("ItemFlag");
			if (currentMode == holder.flag)
				holder.image.setImageDrawable(getResources().getDrawable(
						R.drawable.settings_profiles_item_s));
			else
				holder.image.setImageDrawable(getResources().getDrawable(
						R.drawable.settings_profiles_item_ns));

			if (textStatus == position) {
				holder.txTitle.setTextColor(0xFFFFFFFF);
			} else {
				holder.txTitle.setTextColor(0xFFFFFFFF);
			}
			return convertView;
		}

		public final class ViewHolder {
			public TextView txTitle;
			public TextView textDescription;
			public int flag;
			public ImageView image;
		}
	}

}
