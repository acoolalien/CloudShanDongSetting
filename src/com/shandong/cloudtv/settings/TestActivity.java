package com.shandong.cloudtv.settings;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shandong.cloudtv.settings.util.Utils;

public class TestActivity extends Activity {

	Button mManualWifiBtn = null;
	Button mWifiShareBtn = null;
	Button mPppoeConnectBtn = null;
	Button mEthernetBtn = null;
	Button mEthernetDisBtn = null;
	Button mWifiBtn = null;
	Button mTestSpeedBtn = null;
	Button mTestDeviceId = null;
	SettingsApplication mApplication = null;
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
			Toast.makeText(TestActivity.this, "device id : " + tm.getDeviceId(), Toast.LENGTH_LONG).show();
		}
	};
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen);
		
		Configuration config = getResources().getConfiguration();
		System.out.println("config==="+config);
		mApplication = (SettingsApplication)this.getApplication();
		initView();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@SuppressLint("NewApi")
	private void initView(){
		mManualWifiBtn = (Button)findViewById(R.id.manual_wifi_button);
		mManualWifiBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("manual wifi onclick............");
				Utils.setApplicationBGBitmap(TestActivity.this);
				
				Intent intent =new Intent(TestActivity.this, ManualAddWifiAcvitity.class);
				startActivity(intent);
			}
		});
		
		mWifiShareBtn = (Button)findViewById(R.id.wifi_share_button);
		mWifiShareBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Utils.setApplicationBGBitmap(TestActivity.this);
				
				Intent intent = new Intent(TestActivity.this,WifiShareActivity.class);
				startActivity(intent);
			}
		});
		
		mPppoeConnectBtn = (Button)findViewById(R.id.pppoe_connect_button);
		mPppoeConnectBtn.setOnClickListener(new  View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Utils.setApplicationBGBitmap(TestActivity.this);
				Intent intent = new Intent(TestActivity.this, PppoeConnectActivity.class);
				startActivity(intent);
			}
		});
		
		mEthernetBtn = (Button)findViewById(R.id.ethernet_button);
		mEthernetBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestActivity.this, EthernetConnectedAcivity.class);
				startActivity(intent);
			}
		});
		
		mEthernetDisBtn = (Button)findViewById(R.id.ethernet_dis_button);
		mEthernetDisBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestActivity.this,EthernetDisconnectedActivity.class);
				startActivity(intent);
			}
		});
		
		mWifiBtn = (Button)findViewById(R.id.wifi_button);
		mWifiBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestActivity.this, WifiConnectActivity.class);
				startActivity(intent);
			}
		});
		
		mTestSpeedBtn = (Button)findViewById(R.id.test_speed_button);
		mTestSpeedBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestActivity.this, TestSpeedNewActivity.class);
				startActivity(intent);
			}
		});
		mTestDeviceId = (Button) findViewById(R.id.test_device_id);
		mTestDeviceId.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				handler.sendEmptyMessage(1);
			}
		});
	}
	

}
