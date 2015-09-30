package com.shandong.cloudtv.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shandong.cloudtv.settings.util.LocationPreference;
import com.shandong.cloudtv.settings.util.LocationUtils;
import com.shandong.cloudtv.settings.wheel.NewWheelAdapter;
import com.shandong.cloudtv.settings.wheel.WheelView;

public class LocationSettingsActivity extends Activity {

	private WheelView provinceWheelView, cityWheelView, zoneWheelView;
	private TextView textViewProvince, textViewCity, textViewZone;
	private NewWheelAdapter provinceWheelViewAdapter, cityWheelViewAdapter, zoneWheelViewAdapter;
	View provinceLayout, cityLayout, zoneLayout;
	private HashMap<String, HashMap<String, HashMap<String, String>>> dataMap;

	private ArrayList<String> provinceList;
	private ArrayList<ArrayList<String>> cityList;
	private ArrayList<ArrayList<ArrayList<String>>> zoneList;

	private String[] provinceArray, cityArray, zoneArray;
	private LocationPreference lpreference;
	private int curProvinceIndex, curCityIndex, curZoneIndex;
	private DisplayMetrics dm;
	private final String PREFERENCE = "locationsettings_pref";
	private final String PACKAGE_NAME = "com.hiveview.weather";
	private Context context = this;
	private Context c = null;
	private SharedPreferences.Editor editor;
	private SharedPreferences preference;
	private String mAction = "com.hiveview.cloudtv.LOCATION";
    private boolean mInitFlag= true;
	private RelativeLayout mRelativeLayout = null;
	
	/*
	 * private int mInfactProvince = -1; private int mInfactCity = -1; private
	 * int mInfactZone = -1;
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_locationsettings);
		initFromRemote();
		initWidget();
		initMain();
		IntentFilter intent = new IntentFilter();
		intent.addAction("com.hiveview.cloudtv.LOCATION");
		registerReceiver(mRecevier, intent);

	}

	private void initFromRemote() {
		lpreference = new LocationPreference(this);
		int mProvinceIndex = lpreference.getLastProvinceIndex();
		int mCityIndex = lpreference.getLastCityIndex();
		int mZoneIndex = lpreference.getLastZoneIndex();
		try {
			c = context.createPackageContext(PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (c != null) {
			preference = c.getSharedPreferences("weather_pref", Context.MODE_MULTI_PROCESS);
			// 读不到就取自己的preference的值
			if (!preference.getAll().isEmpty()) {
				this.editor = preference.edit();

				mProvinceIndex = preference.getInt("provinceIndex", 0);
				mCityIndex = preference.getInt("cityIndex", 0);
				mZoneIndex = preference.getInt("zoneIndex", 0);
			}
		}

		lpreference.setLastProvinceIndex(mProvinceIndex);
		lpreference.setLastZoneIndex(mZoneIndex);
		lpreference.setLastCityIndex(mCityIndex);
		/*
		 * mInfactProvince = mProvinceIndex; mInfactCity = mCityIndex;
		 * mInfactZone = mZoneIndex;
		 */

	}

	private void initMain() {
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// LocationUtils.LoadFile(this);
		dataMap = LocationUtils.LoadFile(this);
		LocationUtils.LoadCityFile(this);
		LocationUtils.LoadWheelList(this);
		provinceList = LocationUtils.getProvinceList();
		cityList = LocationUtils.getCityList();
		zoneList = LocationUtils.getZoneList();

		lpreference = new LocationPreference(this);
		curProvinceIndex = lpreference.getLastProvinceIndex();
		curCityIndex = lpreference.getLastCityIndex();
		curZoneIndex = lpreference.getLastZoneIndex();
		Log.i("BANGBANG", "!---P:" + curProvinceIndex + "!---C:" + curCityIndex + "!---Z:"
				+ curZoneIndex);
		updateWheelViewDraw(curProvinceIndex, curCityIndex, curZoneIndex);
	}
	

	private void updateWheelViewDraw(int pindex, int cindex, int zindex) {

		textViewProvince.setVisibility(View.VISIBLE);
		textViewCity.setVisibility(View.GONE);
		textViewZone.setVisibility(View.GONE);

		provinceArray = (String[]) provinceList.toArray(new String[] {});
		provinceWheelViewAdapter = new NewWheelAdapter(context, provinceArray);
		provinceWheelView.setAdapter(provinceWheelViewAdapter);
		provinceWheelView.setSelectionAfterDataSetChanged(pindex);
		textViewProvince.setText(provinceArray[pindex]);
		

		cityArray = (String[]) cityList.get(pindex).toArray(new String[] {});
		cityWheelViewAdapter = new NewWheelAdapter(context, cityArray);
		cityWheelView.setAdapter(cityWheelViewAdapter);
		cityWheelView.setSelectionAfterDataSetChanged(cindex);
		textViewCity.setText(cityArray[cindex]);

		zoneArray = (String[]) zoneList.get(pindex).get(cindex).toArray(new String[] {});
		zoneWheelViewAdapter = new NewWheelAdapter(context, zoneArray);
		zoneWheelView.setAdapter(zoneWheelViewAdapter);
		zoneWheelView.setSelectionAfterDataSetChanged(zindex);
		textViewZone.setText(zoneArray[zindex]);
	}

	private void updateSecond(int pindex, int cindex, int zindex) {
		cityArray = (String[]) cityList.get(pindex).toArray(new String[] {});
		if (cityWheelViewAdapter == null) {
			cityWheelViewAdapter = new NewWheelAdapter(context, cityArray);
			cityWheelView.setAdapter(cityWheelViewAdapter);
		} else {
			cityWheelViewAdapter.setList(cityArray);
			cityWheelViewAdapter.notifyDataSetChanged();
		}
		cityWheelView.setSelectionAfterDataSetChanged(cindex);

		zoneArray = (String[]) zoneList.get(pindex).get(cindex).toArray(new String[] {});
		if (zoneWheelViewAdapter == null) {
			zoneWheelViewAdapter = new NewWheelAdapter(context, zoneArray);
			zoneWheelView.setAdapter(zoneWheelViewAdapter);
		} else {
			zoneWheelViewAdapter.setList(zoneArray);
			zoneWheelViewAdapter.notifyDataSetChanged();
		}
		zoneWheelView.setSelectionAfterDataSetChanged(zindex);
		textViewCity.setText(cityArray[cindex]);
		textViewZone.setText(zoneArray[zindex]);

	}

	private void updateThird(int pindex, int cindex, int zindex) {
		zoneArray = (String[]) zoneList.get(pindex).get(cindex).toArray(new String[] {});
		if (zoneWheelViewAdapter == null) {
			zoneWheelViewAdapter = new NewWheelAdapter(context, zoneArray);
			zoneWheelView.setAdapter(zoneWheelViewAdapter);
		} else {
			zoneWheelViewAdapter.setList(zoneArray);
			zoneWheelViewAdapter.notifyDataSetChanged();
		}
		zoneWheelView.setSelectionAfterDataSetChanged(zindex);
	}

	private BroadcastReceiver mRecevier = new BroadcastReceiver() {

		@Override
		public void onReceive(Context paramContext, Intent paramIntent) {
			// TODO Auto-generated method stub
			Log.i("BANGBANG",
					"-----TEST MY BROADCAST---"
							+ paramIntent.getIntExtra("provinceIndex", 0)
							+ "----------"
							+ paramIntent.getIntExtra("cityIndex", 0)
							+ "---------"
							+ paramIntent.getIntExtra("zoneIndex", 0)
							+ "----------"
							+ paramIntent
									.getStringExtra("com.hiveview.weather.PREFERENCE_WEATHER_IMAGE"));
		}
	};

	private void initWidget() {
		textViewProvince = (TextView) findViewById(R.id.textview_location_province);
		textViewCity = (TextView) findViewById(R.id.textview_location_city);
		textViewZone = (TextView) findViewById(R.id.textview_location_zone);

		provinceLayout = (View) findViewById(R.id.layout_province);
		cityLayout = (View) findViewById(R.id.layout_city);
		zoneLayout = (View) findViewById(R.id.layout_zone);

		provinceWheelView = (WheelView) findViewById(R.id.province_wheelview);
		// provinceWheelView.setCyclic(true);
		provinceWheelView.setOnFocusChangeListener(onWheelFocusChangeListener);
		provinceWheelView.setOnItemClickListener(onWheelClickListener);
		provinceWheelView.setOnKeyListener(onWheelKeyListener);
		provinceWheelView.setOnItemSelectedListener(onWheelItemSelectListener);

		cityWheelView = (WheelView) findViewById(R.id.city_wheelview);
		// cityWheelView.setCyclic(true);
		cityWheelView.setOnFocusChangeListener(onWheelFocusChangeListener);
		cityWheelView.setOnItemClickListener(onWheelClickListener);
		cityWheelView.setOnKeyListener(onWheelKeyListener);
		cityWheelView.setOnItemSelectedListener(onWheelItemSelectListener);
		zoneWheelView = (WheelView) findViewById(R.id.zone_wheelview);
		// zoneWheelView.setCyclic(true);
		zoneWheelView.setOnFocusChangeListener(onWheelFocusChangeListener);
		zoneWheelView.setOnItemClickListener(onWheelClickListener);
		zoneWheelView.setOnKeyListener(onWheelKeyListener);
		zoneWheelView.setOnItemSelectedListener(onWheelItemSelectListener);
		
		mRelativeLayout =(RelativeLayout) findViewById(R.id.layout_title);
		//mRelativeLayout.seta
		LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, R.anim.list_anim_layout);
		mRelativeLayout.setLayoutAnimation(animation);
		mRelativeLayout.setLayoutAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				Log.i("YQB", "----onAnimationStart----");
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Log.i("YQB", "----onAnimationEnd----");
				provinceWheelView.setAlpha(1);
				textViewCity.setVisibility(View.VISIBLE);
				textViewZone.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		mRelativeLayout.startLayoutAnimation();
	}

	private OnItemSelectedListener onWheelItemSelectListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt,
				long paramLong) {
			// TODO Auto-generated method stub
			int id = ((WheelView) paramView.getParent()).getId();
			switch (id) {
			case R.id.province_wheelview: {

				break;
			}
			case R.id.city_wheelview: {

				break;
			}
			case R.id.zone_wheelview: {

				break;
			}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> paramAdapterView) {
			// TODO Auto-generated method stub

		}

	};

	private OnKeyListener onWheelKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View view, int keyCode, KeyEvent event) {

			if (event.getAction() == KeyEvent.ACTION_UP) {
				int provinceIndex = provinceWheelView.getCurrentItem();
				int cityIndex = cityWheelView.getCurrentItem();
				int zoneIndex = zoneWheelView.getCurrentItem();
				switch (view.getId()) {

				case R.id.province_wheelview:

					break;
				case R.id.city_wheelview:

					break;
				case R.id.zone_wheelview:

					break;

				}
			}

			return false;

		}
	};

	private OnItemClickListener onWheelClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt,
				long paramLong) {
			int id = ((WheelView) paramView.getParent()).getId();
			switch (id) {
			case R.id.province_wheelview:
				int tempProvinceIndex = provinceWheelView.getCurrentItem();
				curProvinceIndex = tempProvinceIndex;
				curCityIndex = 0;
				curZoneIndex = 0;
				lpreference.setLastProvinceIndex(curProvinceIndex);
				lpreference.setLastCityIndex(curCityIndex);
				lpreference.setLastZoneIndex(curZoneIndex);
				// mInfactProvince = tempProvinceIndex;
				updateSecond(curProvinceIndex, curCityIndex, curZoneIndex);
				cityWheelView.requestFocus();
				break;
			case R.id.city_wheelview:
				int tempCityIndex = cityWheelView.getCurrentItem();

				curCityIndex = tempCityIndex;
				curZoneIndex = 0;
				lpreference.setLastCityIndex(curCityIndex);
				lpreference.setLastZoneIndex(curZoneIndex);
				// mInfactCity = tempCityIndex;
				updateThird(curProvinceIndex, curCityIndex, curZoneIndex);
				zoneWheelView.requestFocus();
				break;
			case R.id.zone_wheelview:
				int tempZoneIndex = zoneWheelView.getCurrentItem();
				curZoneIndex = zoneWheelView.getCurrentItem();
				// mInfactZone = tempZoneIndex;
				lpreference.setLastZoneIndex(curZoneIndex);
				String mTomorrowKey = provinceWheelViewAdapter.getCurrentContent(provinceWheelView
						.getCurrentItem());
				HashMap<String, HashMap<String, String>> mCityMap = dataMap.get(mTomorrowKey);

				String mAfterTomorrowKey = cityWheelViewAdapter.getCurrentContent(cityWheelView
						.getCurrentItem());
				HashMap<String, String> mZoneMap = mCityMap.get(mAfterTomorrowKey);
				String mThreeDaysKey = zoneWheelViewAdapter.getCurrentContent(zoneWheelView
						.getCurrentItem());
				String id1 = mZoneMap.get(mThreeDaysKey);
				lpreference.setCityId(id1);
				provinceWheelView.requestFocus();
				Intent intent = new Intent();
				intent.setAction(mAction);
				intent.putExtra("provinceIndex", curProvinceIndex);
				intent.putExtra("cityIndex", curCityIndex);
				intent.putExtra("zoneIndex", curZoneIndex);
				intent.putExtra("com.hiveview.weather.PREFERENCE_WEATHER_IMAGE", id1);
				context.sendBroadcast(intent);
				break;
			}
		}
	};

	/**
	 * 
	 * @Fields onWheelFocusChangeListener:TODO鐒︾偣鐩戝惉浜嬩�?
	 */
	private OnFocusChangeListener onWheelFocusChangeListener = new OnFocusChangeListener() {

		@SuppressLint("NewApi")
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			switch (view.getId()) {
			case R.id.province_wheelview:
				Log.e("huxing", "huxing----foucs-------1");
				if (hasFocus) {
					textViewProvince.setTextColor(0xFFFFFFFF);
					provinceLayout.setVisibility(View.VISIBLE);
					if(mInitFlag){
						provinceWheelView.setAlpha(0);
					}else{
						provinceWheelView.setAlpha(1);
					}
					mInitFlag=false;
					textViewProvince.setVisibility(View.GONE);
				} else {
					textViewProvince.setTextColor(0xFF9A9A9A);
					provinceLayout.setVisibility(View.GONE);
					provinceWheelView.setAlpha(0);
					textViewProvince.setVisibility(View.VISIBLE);
					if (provinceWheelView.getCurrentItem() != curProvinceIndex) {
						provinceWheelViewAdapter.notifyDataSetChanged();
						provinceWheelView.setSelectionAfterDataSetChanged(curProvinceIndex);
					}
					// curProvinceIndex = mInfactProvince;
					textViewProvince.setText(provinceArray[curProvinceIndex]);
				}

				break;
			case R.id.city_wheelview:
				Log.e("huxing", "huxing----foucs-------2");
				if (hasFocus) {
					textViewCity.setTextColor(0xFFFFFFFF);
					textViewCity.setVisibility(View.GONE);
					cityLayout.setVisibility(View.VISIBLE);
					cityWheelView.setAlpha(1);
				} else {
					textViewCity.setTextColor(0xFF9A9A9A);
					textViewCity.setVisibility(View.VISIBLE);
					cityLayout.setVisibility(View.GONE);
					cityWheelView.setAlpha(0);
					if (curCityIndex != cityWheelView.getCurrentItem()) {
						cityWheelViewAdapter.notifyDataSetChanged();
						cityWheelView.setSelectionAfterDataSetChanged(curCityIndex);
					}
					// curCityIndex = mInfactCity;
					textViewCity.setText(cityArray[curCityIndex]);
				}
				// cityWheelView.setCurrentItem(curCityIndex);

				break;
			case R.id.zone_wheelview:
				Log.e("huxing", "huxing----foucs-------3");
				if (hasFocus) {
					textViewZone.setTextColor(0xFFFFFFFF);
					textViewZone.setVisibility(View.GONE);
					zoneLayout.setVisibility(View.VISIBLE);
					zoneWheelView.setAlpha(1);
				} else {
					textViewZone.setTextColor(0xFF9A9A9A);
					textViewZone.setVisibility(View.VISIBLE);
					zoneLayout.setVisibility(View.GONE);
					zoneWheelView.setAlpha(0);
					if (curZoneIndex != zoneWheelView.getCurrentItem()) {
						zoneWheelViewAdapter.notifyDataSetChanged();
						zoneWheelView.setSelectionAfterDataSetChanged(curZoneIndex);
					}
					// curZoneIndex = mInfactZone;
					textViewZone.setText(zoneArray[curZoneIndex]);
				}
				// zoneWheelView.setCurrentItem(curZoneIndex);

				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		this.unregisterReceiver(mRecevier);
		super.onDestroy();
	}

}
