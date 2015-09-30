package com.shandong.cloudtv.settings.receiver;

import com.shandong.cloudtv.settings.SettingsApplication;
import com.shandong.cloudtv.settings.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetStateTracker;
import android.net.pppoe.PppoeManager;
import android.net.pppoe.PppoeStateTracker;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class NetStateReceiver extends BroadcastReceiver {
	private static final boolean DEBUG = false;
	private static final String TAG = "NetStateReceiver";
	public static final String HIVEVIEW_NETWORK_STATE_CHANGED_ACTION = "com.hiveview.tv.ACTION_INTERNET_CONNECTION_STATUS";
	public static final String HIVEVIEW_EXTRA_KEY = "connection_status";
	//wifi
	public static final int HIVEVIEW_NETWORK_WIFI_STATE_CONNECTED = 100003;
	public static final int HIVEVIEW_NETWORK_WIFI_STATE_DISCONNECTED = 100004;
	//pppoe
    public static final int HIVEVIEW_NETWORK_PPPOE_STATE_CONNECTED = 100001;
    public static final int HIVEVIEW_NETWORK_PPPOE_STATE_DISCONNECTED = 100002;
	//ethernet
    public static final int HIVEVIEW_NETWORK_ETH_STATE_CONNECTED = 100005;
    public static final int HIVEVIEW_NETWORK_ETH_STATE_DISCONNECTED = 100006;
    
	public static final int HIVEVIEW_NETWORK_STATE_OTHER = 110000;

	private Context mContext = null;
	private ConnectivityManager connectivity = null;
	private NetworkInfo pppoeinfo = null;
	private NetworkInfo wifiinfo = null;
	private NetworkInfo ethernetInfo = null;
	private boolean isRealConnected = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		pppoeinfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_PPPOE);
		wifiinfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		ethernetInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		
		if (DEBUG) {
			Log.e(TAG, "receive NET_CHANGE_STATE action=" + intent.getAction());
		}
		
		isRealConnected = Utils.pingHost("www.baidu.com");
		String action = intent.getAction();
		
		if (action.equals(EthernetManager.ETH_STATE_CHANGED_ACTION)) {
			int event = intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE, EthernetManager.ETH_STATE_UNKNOWN);

			if (DEBUG) {
				Log.e(TAG, "receive ETH_STATE_CHANGED_ACTION event=" + event);
			}

			switch (event) {
			case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_SUCCEEDED:
			case EthernetStateTracker.EVENT_HW_CONNECTED:
			case EthernetStateTracker.EVENT_HW_PHYCONNECTED:
				if(isRealConnected){
					postNetStateNotification(HIVEVIEW_NETWORK_ETH_STATE_CONNECTED);
				}else{
					postNetStateNotification(HIVEVIEW_NETWORK_ETH_STATE_DISCONNECTED);
				}
				break;
			case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_FAILED:
			case EthernetStateTracker.EVENT_HW_DISCONNECTED:
			case EthernetStateTracker.EVENT_HW_PHYDISCONNECTED:
				postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
				break;
			case EthernetStateTracker.EVENT_HW_CHANGED:
				if(ethernetInfo.isConnectedOrConnecting()){
					if(isRealConnected){
						postNetStateNotification(HIVEVIEW_NETWORK_ETH_STATE_CONNECTED);
					}else{
						postNetStateNotification(HIVEVIEW_NETWORK_ETH_STATE_DISCONNECTED);
					}
				}else{
					postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
				}
				break;
			default:
				break;
			}

		} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			if (DEBUG) {
				Log.e(TAG, "receive NETWORK_STATE_CHANGED_ACTION");
			}
			if(ethernetInfo.isConnectedOrConnecting()){
				return;
			}
			
			if(wifiinfo.isConnectedOrConnecting()){
				if(isRealConnected){
					postNetStateNotification(HIVEVIEW_NETWORK_WIFI_STATE_CONNECTED);
				}else{
					postNetStateNotification(HIVEVIEW_NETWORK_WIFI_STATE_DISCONNECTED);
				}
			}else{
				postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
			}
		} else if (action.equals(PppoeManager.PPPOE_STATE_CHANGED_ACTION)) {
			int event = intent.getIntExtra(PppoeManager.EXTRA_PPPOE_STATE, PppoeManager.PPPOE_STATE_UNKNOWN);

			if (DEBUG) {
				Log.e(TAG, "receive PPPOE_STATE_CHANGED_ACTION event=" + event);
			}

			switch (event) {
			case PppoeStateTracker.EVENT_CONNECTED:
				if(isRealConnected){
					postNetStateNotification(HIVEVIEW_NETWORK_PPPOE_STATE_CONNECTED);
				}else{
					postNetStateNotification(HIVEVIEW_NETWORK_PPPOE_STATE_DISCONNECTED);
				}
				break;
			case PppoeStateTracker.EVENT_DISCONNECTED:
			case PppoeStateTracker.EVENT_CONNECT_FAILED:
				postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
				break;
			}
		}else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			sendNetChangedState(mContext);
		}else if(action.equalsIgnoreCase("com.hiveview.pingswanserver.notifiy")){
			Bundle b = intent.getBundleExtra("ping");
			int status = b.getInt("status", -999);
			if(DEBUG){
				Log.e(TAG, "receive message com.hiveview.pingswanserver.notifiy  status="+status
						+"....."+SettingsApplication.getInstance().getPingStatus());
			}
			if(status == 0 && status!=SettingsApplication.getInstance().getPingStatus()){
				isRealConnected = true;
				sendNetChangedState(mContext);
			}
			SettingsApplication.getInstance().setPingStatus(status);
		}
	}

	private void postNetStateNotification(int state) {
		if (DEBUG) {
			Log.e(TAG, "receive postNetStateNotification state=" + state);
		}
		
		Intent hiveviewIntent = new Intent(HIVEVIEW_NETWORK_STATE_CHANGED_ACTION);
		hiveviewIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT);
		hiveviewIntent.putExtra(HIVEVIEW_EXTRA_KEY, state);
		mContext.sendStickyBroadcast(hiveviewIntent);
	}
	
	private void sendNetChangedState(Context context){
		isRealConnected = Utils.pingHost("www.baidu.com");
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

				if (networkInfo.isConnected() && networkInfo.isAvailable()) {
					if(isRealConnected){
						postNetStateNotification(HIVEVIEW_NETWORK_WIFI_STATE_CONNECTED);
					}else{
						postNetStateNotification(HIVEVIEW_NETWORK_WIFI_STATE_DISCONNECTED);
					}
				}else{
					postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
				}
			}else if (networkInfo.getType() == ConnectivityManager.TYPE_PPPOE) {
				if (networkInfo.isConnected() && networkInfo.isAvailable()) {
					if(isRealConnected){
						postNetStateNotification(HIVEVIEW_NETWORK_PPPOE_STATE_CONNECTED);
					}else{
						postNetStateNotification(HIVEVIEW_NETWORK_PPPOE_STATE_DISCONNECTED);
					}
				}else{
					postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
				}
			} else if (networkInfo.getTypeName().toString()
					.equalsIgnoreCase("ETHERNET")) {
				if (networkInfo.isConnected() && networkInfo.isAvailable()) {
					if(isRealConnected){
						postNetStateNotification(HIVEVIEW_NETWORK_ETH_STATE_CONNECTED);
					}else{
						postNetStateNotification(HIVEVIEW_NETWORK_ETH_STATE_DISCONNECTED);
					}
				}else{
					postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
				}

			} else {
				postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
			}
		}else{
			postNetStateNotification(HIVEVIEW_NETWORK_STATE_OTHER);
		}
	}
}
