package com.shandong.cloudtv.settings;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.shandong.cloudtv.settings.util.Utils;

public class NetworkSettingsActivity extends Activity{

	private boolean isNativeGo = false;
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		isNativeGo = getIntent().getBooleanExtra("isNativeGo", false);
		LinearLayout layout = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
					LinearLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);
		layout.setBackgroundResource(R.drawable.inside_bg);
		setContentView(layout);
		jump2Nets();
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
	
	private void jump2Nets(){
		boolean ethState = Utils.checkEthState();
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiinfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		//if((ethState && wifiinfo.isConnected()) || !ethState ){
		Log.e("NetworkSettingsActivity", "ethernet state="+ethState);
		if(!ethState ){
			jump2Wifi();
		}else{
			((WifiManager)this.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
			((EthernetManager)this.getSystemService(Context.ETH_SERVICE)).setEthEnabled(true);
			new CheckCableTask().execute();
		}
	}
	
	private void jump2Ethernet(){
		Intent intent = new Intent(NetworkSettingsActivity.this, EthernetConnectedAcivity.class);
		startActivity(intent);
		NetworkSettingsActivity.this.finish();
	}
	
	private void jump2DisEthernet(){
		Intent intent = new Intent(NetworkSettingsActivity.this, EthernetDisconnectedActivity.class);
		intent.putExtra("isNativeGo", isNativeGo); //内部跳转，区分开机向导的跳转
		startActivity(intent);		
		NetworkSettingsActivity.this.finish();
	}
	
	private void jump2Wifi(){
		Intent intent = new Intent(NetworkSettingsActivity.this, WifiConnectActivity.class);
		intent.putExtra("isNativeGo", isNativeGo);
		startActivity(intent);
		NetworkSettingsActivity.this.finish();
	}
	
	class CheckCableTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			return Utils.pingHost("www.baidu.com");
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				jump2Ethernet();
			}else{
				jump2DisEthernet();
			}
		}
	}
}
