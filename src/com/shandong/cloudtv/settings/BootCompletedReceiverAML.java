package com.shandong.cloudtv.settings;

import java.io.FileInputStream;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.pppoe.PppoeStateTracker;
import android.net.wifi.WifiManager;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;

import com.shandong.cloudtv.settings.pppoe.PppoeDataEntity;
import com.shandong.cloudtv.settings.util.Utils;

public class BootCompletedReceiverAML extends BroadcastReceiver {
	private static final String TAG = "BootCompletedReceiverAML";
	private static final boolean DEBUG = false;
	private WifiManager mWifiManager = null;
	private EthernetManager mEthMng = null;
	private PppoeDataEntity mPppoeDataEntity = null;
	private Context mContext;
	public static boolean isPppoeConnectingFlag = false;
	public static Thread mCheckSwitchStateThread = null;
	private static boolean mSwitchNetState = false; 
	private static boolean mIsFirst = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		if(DEBUG){
			Log.e(TAG, "onReceive.......................");
		}
		if(SystemProperties.getInt("persist.sys.factory.mode", 0) == 1){
    		return;
    	}
		mContext = context;
		mPppoeDataEntity = PppoeDataEntity.getInstance(context);
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mEthMng = (EthernetManager) context.getSystemService(Context.ETH_SERVICE);
		if(mCheckSwitchStateThread == null){
			mCheckSwitchStateThread = new CheckSwitchNetThread(mContext);
		}

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			if(DEBUG){
				Log.e(TAG, "ACTION_BOOT_COMPLETED.......................");
			}
			disableWifiAP();
			if (mEthMng.getEthState() != EthernetManager.ETH_STATE_ENABLED) {
				mEthMng.setEthEnabled(true);
			}
			mSwitchNetState = Utils.checkEthState();
			
			if(mCheckSwitchStateThread !=null &&!mCheckSwitchStateThread.isAlive()){
				mCheckSwitchStateThread.start();
			}else{
				if(DEBUG){
					Log.e(TAG, "the check switch thread is alive");
				}
			}
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					doNetWork(mContext, false);
					connectPPPoe(mContext);
				}
			}).start();
			
		} else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			if(DEBUG){
				Log.e(TAG, "CONNECTIVITY_ACTION.......................donetwork false");
			}
			disableWifiAP();
			if(mIsFirst){
				mIsFirst = false;
				if (mEthMng.getEthState() != EthernetManager.ETH_STATE_ENABLED) {
					mEthMng.setEthEnabled(true);
				}
				mSwitchNetState = Utils.checkEthState();
			}
			if(mCheckSwitchStateThread !=null && !mCheckSwitchStateThread.isAlive()){
				mCheckSwitchStateThread.start();
			}else{
				if(DEBUG){
					Log.e(TAG, "the check switch thread is alive");
				}
			}
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					doNetWork(mContext, false);
				}
			}).start();
			

		} else if (intent.getAction().equals(PppoeManager.PPPOE_STATE_CHANGED_ACTION)) {
			int event = intent.getIntExtra(PppoeManager.EXTRA_PPPOE_STATE, PppoeManager.PPPOE_STATE_UNKNOWN);
			if (event == PppoeStateTracker.EVENT_CONNECTED) {
				if(DEBUG){
					Log.e(TAG, "EVENT_CONNECTED ");
				}
				SystemProperties.set(PppoeDataEntity.SYS_PRO_PPP_CONN_STA, "true");
				// ((WifiManager)mContext.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
				isPppoeConnectingFlag = false;
			} else if (event == PppoeStateTracker.EVENT_DISCONNECTED) {
				if(DEBUG){
					Log.e(TAG, "EVENT_DISCONNECTED ");
				}
				mPppoeDataEntity.clearPppoeRunningFlag();
				SystemProperties.set(PppoeDataEntity.SYS_PRO_PPP_CONN_STA, "false");
				isPppoeConnectingFlag = false;
				Toast.makeText(mContext, mContext.getString(R.string.pppoe_disconnect_ok), Toast.LENGTH_SHORT).show();
			} else if (event == PppoeStateTracker.EVENT_CONNECT_FAILED) {
				if(DEBUG){
					Log.e(TAG, "EVENT_CONNECT_FAILED ");
				}
				String ppp_err = intent.getStringExtra(PppoeManager.EXTRA_PPPOE_ERRCODE);
				mPppoeDataEntity.clearPppoeRunningFlag();
				if (ppp_err.equals("691")) {
					if(DEBUG){
						Log.e(TAG, "pppoe connect 691 error!");
					}
					Toast.makeText(mContext, mContext.getString(R.string.pppoe_connect_failed_auth), Toast.LENGTH_SHORT)
							.show();
				} else if (ppp_err.equals("650")) {
					if(DEBUG){
						Log.e(TAG, "pppoe connect 650 error!");
					}
					Toast.makeText(mContext, mContext.getString(R.string.pppoe_connect_failed_server_no_response),
							Toast.LENGTH_SHORT).show();
				}
				SystemProperties.set(PppoeDataEntity.SYS_PRO_PPP_CONN_STA, "false");
				// ((EthernetManager)
				// mContext.getSystemService(Context.ETH_SERVICE)).setEthEnabled(false);
				// ((EthernetManager)
				// mContext.getSystemService(Context.ETH_SERVICE)).setEthEnabled(true);
				isPppoeConnectingFlag = false;
			}
		}
	}


	private void disableWifiAP(){
		if(mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED 
				|| mWifiManager.getWifiApState() ==WifiManager.WIFI_AP_STATE_ENABLING){
			mWifiManager.setWifiApEnabled(null, false);
		}
	}
	
	@SuppressLint("NewApi")
	private void connectPPPoe(Context context) {
		final String net_file = "/sys/class/net/eth0/operstate";
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(net_file);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		if(DEBUG){
			Log.e(TAG, "CONNECTIVITY_ACTION.......................pppoe 2222222222222-------" + Utils.checkEthState());
		}
		// if(res!=null && "up".equals(res.trim())){
		if (Utils.checkEthState()) {

			String name = mPppoeDataEntity.getPppoeName();
			String password =mPppoeDataEntity.getPppoePassword();

			if(Utils.getGateway(mContext) != null){
				return;
			}
			try {

				boolean autoConnect = mPppoeDataEntity.getPppoeAutoConnectFlag();
				if(DEBUG){
					Log.e(TAG, "CONNECTIVITY_ACTION.......................pppoe 111111111");
				}
				if (autoConnect) {
					mPppoeDataEntity.setPppoeAutoConnectFlag(autoConnect);
					
					if(DEBUG){
//						Log.e(TAG, "CONNECTIVITY_ACTION.......................pppoe state-- " + mPppoeDataEntity.getPppoeConnState());
					}
					if (null == name || name.isEmpty() || null == password || password.isEmpty()) {
						if(DEBUG){
							Log.e(TAG, "name  or password is null ");
						}
						return;
					}
//					if (mPppoeDataEntity.getPppoeConnState() != PppoeOperation.PPP_STATUS_CONNECTED) {
//						isPppoeConnectingFlag = true;
//						((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
//
//						mPppoeDataEntity.setPppoeRunningFlag();
//						mPppoeDataEntity.connect(PppoeDataEntity.PPPOE_INTERNET_INTERFACE, name, password);
//						if(DEBUG){
//							Log.e(TAG, "CONNECTIVITY_ACTION.......................pppoe connecting");
//						}
//
//					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static boolean checkEthState(){
		final String net_file = "/sys/class/net/eth0/operstate";
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(net_file);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(res!=null&& "up".equals(res.trim())){
			return true;
		}
		return false;
	}
	
	private void doNetWork(Context context, boolean needConnectPpp) {
		// boolean mEthlinkState = Utils.checkEthState();


			//if (checkEthState()) {
			if (Utils.checkEthState()) {
				ConnectivityManager connectivity = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo wifiinfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo pppoeinfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_PPPOE);
				// if (wifiinfo.isConnected() ||
				// pppoeinfo.isConnectedOrConnecting()) {
				if (pppoeinfo.isConnectedOrConnecting()) {
					return;
				}
				if(DEBUG){
					Log.e(TAG, "CONNECTIVITY_ACTION.......................ethnet");
				}
				if (mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(false);
					if(DEBUG){
						Log.i(TAG, "wifi close......................");
					}
				}
				if (mEthMng.getEthState() != EthernetManager.ETH_STATE_ENABLED) {
					mEthMng.setEthEnabled(true);
				}
			} else {
				if(DEBUG){
					Log.e(TAG, "CONNECTIVITY_ACTION.......................wifi");
				}
				mEthMng.setEthEnabled(false);
				if (!mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(true);
					if(DEBUG){
						Log.i(TAG, "wifi open....................");
					}
				}
			}
	}
	
	public static class CheckSwitchNetThread extends Thread{
		private Context mContext = null; 
		public CheckSwitchNetThread(Context context){
			mContext = context;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			do{
				try{
					boolean state = Utils.checkEthState();
					//boolean state = checkEthState();
					//if(state != mSwitchNetState || (Utils.getGateway(mContext) == null)){
					if(state != mSwitchNetState ){
						Log.e(TAG, "switch net state old state="+mSwitchNetState+" cur state="+state);
						mSwitchNetState = state;
						if(state){
							if(((WifiManager)mContext.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled()){
								((WifiManager)mContext.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
							}
							if(((EthernetManager) mContext.getSystemService(Context.ETH_SERVICE)).getEthState()
									!= EthernetManager.ETH_STATE_ENABLED){
							((EthernetManager) mContext.getSystemService(Context.ETH_SERVICE)).setEthEnabled(true);
							}
						}else{
							if(((EthernetManager) mContext.getSystemService(Context.ETH_SERVICE)).getEthState()
									== EthernetManager.ETH_STATE_ENABLED){
								((EthernetManager) mContext.getSystemService(Context.ETH_SERVICE)).setEthEnabled(false);
							}
							if(!((WifiManager)mContext.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled()){
								((WifiManager)mContext.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);
							}
						}
					}
					
					Thread.sleep(1000*6);
				}catch (Exception e) {
					// TODO: handle exception
				}
			}while (true);
		}
		
	}
}
