package com.shandong.cloudtv.settings.wifi;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

public class WifiDataEntity {
	private static final String TAG = "WifiDataEntity";
	private Context mContext = null;
	private static WifiDataEntity mWifiDataEntity = null;
	private WifiManager mWifiManager = null;
	private NetworkInfo mWifiNetworkInfo = null;

	public static WifiDataEntity getInstance(Context context) {
		if (mWifiDataEntity == null) {
			mWifiDataEntity = new WifiDataEntity(context);
		}
		return mWifiDataEntity;
	}

	public WifiDataEntity(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		ConnectivityManager connectivity = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiNetworkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	}

	@SuppressLint("NewApi")
	public void updateWifi() {
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return;
		}
		//((EthernetManager) mContext.getSystemService(Context.ETH_SERVICE)).setEthEnabled(false);
		mWifiManager.setWifiEnabled(true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (mWifiNetworkInfo == null || !mWifiNetworkInfo.isConnectedOrConnecting()) {
					mWifiManager.setWifiEnabled(false);
					try {
						Thread.sleep(1000);
						Log.d(TAG, "wifi enable??" + mWifiManager.isWifiEnabled() + "open wifi----");
						mWifiManager.setWifiEnabled(true);
						Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.WIFI_SAVED_STATE, 0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public int getWifiState(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return -1;
		}
		return mWifiManager.getWifiState();
	}
	
	public boolean startScanActive(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return false;
		}
		//return  mWifiManager.startScanActive();
		return  mWifiManager.startScan();
	}
	
	public  List<WifiConfiguration> getConfiguredNetworks(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return null;
		}
		return mWifiManager.getConfiguredNetworks();
	}
	
	public boolean isWifiEnabled(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return false;
		}
		return mWifiManager.isWifiEnabled();
	}
	
	public boolean setWifiEnabled(boolean enable){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return false;
		}
		return mWifiManager.setWifiEnabled(enable);
	}
	
	public WifiInfo getConnectionInfo(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return null;
		}
		return mWifiManager.getConnectionInfo();
	}
	
	public List<ScanResult> getScanResults(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return null;
		}
		return mWifiManager.getScanResults();
	}
	
	public void connect(WifiConfiguration config,WifiManager.ActionListener listener){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return ;
		}
		mWifiManager.connect(config, listener);
	}
	
	public void connect(int networkId ,WifiManager.ActionListener listener){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return ;
		}
		mWifiManager.connect(networkId, listener);
	}
	
	public boolean saveConfiguration(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return false;
		}
		return mWifiManager.saveConfiguration();
	}
	
	public void save(WifiConfiguration config,WifiManager.ActionListener listener){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return ;
		}
		mWifiManager.save(config, listener);
	}
	
	public void forget(int networkid,WifiManager.ActionListener listener){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return ;
		}
		mWifiManager.forget(networkid, listener);
	}
	
	public boolean disconnect(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return false;
		}
		return mWifiManager.disconnect();
	}
	
	//soft ap
	public int getWifiApState(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return -1;
		}
		return mWifiManager.getWifiApState();
	}
	
	public WifiConfiguration getWifiApConfiguration(){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return null;
		}
		return mWifiManager.getWifiApConfiguration();
	}
	
	public boolean setWifiApEnabled(WifiConfiguration config,boolean enable){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return false;
		}
		return mWifiManager.setWifiApEnabled(config, enable);
	}
	
	public boolean setWifiApConfiguration(WifiConfiguration config){
		if(mWifiManager == null){
			Log.e(TAG, "mWifiManager is null");
			return false;
		}
		return mWifiManager.setWifiApConfiguration(config);
	}
}
