package com.shandong.cloudtv.settings.pppoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.pppoe.PppoeDevInfo;
import android.net.pppoe.PppoeManager;
import android.os.SystemProperties;
import android.util.Log;

//import com.amlogic.pppoe.PppoeOperation;

public class PppoeDataEntity {
	private static final String TAG = "Pppoe";
    public static final int PPPOE_STATE_UNDEFINED = 0;
    public static final int PPPOE_STATE_DISCONNECTED = 1;
    public static final int PPPOE_STATE_CONNECTING = 2;
    public static final int PPPOE_STATE_DISCONNECTING = 3;
    public static final int PPPOE_STATE_CONNECT_FAILED = 4;
    public static final int PPPOE_STATE_CONNECTED = 5;
    
    public static final String PPPOE_RUNNING_FLAG = "net.pppoe.running";
    public static final String SYS_PRO_PPP_CONN_STA = "net.pppoe.isConnected";
    public static final String PPPOE_INTERNET_INTERFACE = "eth0";
    public static final String SHARE_PREFERENCE_FILE_NAME = "inputdata";
    public static final String INFO_AUTOCONNECT = "auto_connect";
    public static final String INFO_USERNAME = "name";
    public static final String INFO_PASSWORD = "passwd";
    public static final String ETHERNET_DHCP_REPEAT_FLAG = "net.dhcp.repeat";
    public static final String PPPOE_EXTRA_STATE = "pppoe_state";
    public static final boolean PPPOE_EXTRA_STATE_CONNECT = true;
    public static final boolean PPPOE_EXTRA_STATE_DISCONNECT = false;
    
    
	private Context mContext = null;
	private static PppoeDataEntity mPppoeDataEntity = null;
//	private PppoeOperation mPppOperation = null;
	private PppoeManager mPppoeManager = null;
	private PppoeDevInfo mPppoeDevinfo = null;
	
	public static PppoeDataEntity getInstance(Context context){
		if(mPppoeDataEntity==null){
			mPppoeDataEntity = new PppoeDataEntity(context);
		}
		return mPppoeDataEntity;
	}
	
	public PppoeDataEntity(Context context){
		mContext = context;
//		mPppOperation = new PppoeOperation();
		mPppoeManager = (PppoeManager) mContext.getSystemService(Context.PPPOE_SERVICE);
	}
	
	public String getPppoeName() {
		SharedPreferences sharedata = mContext.getSharedPreferences(SHARE_PREFERENCE_FILE_NAME, Context.MODE_MULTI_PROCESS);
		return sharedata.getString(PppoeDataEntity.INFO_USERNAME, null);
	}

	public void setPppoeName(String userName){
		SharedPreferences sharedata = mContext.getSharedPreferences(SHARE_PREFERENCE_FILE_NAME, Context.MODE_MULTI_PROCESS);
	    SharedPreferences.Editor editor = sharedata.edit();
	    editor.putString(INFO_USERNAME, userName);
	    editor.commit();
	}
	
	public String getPppoePassword() {
		SharedPreferences sharedata = mContext.getSharedPreferences(SHARE_PREFERENCE_FILE_NAME, Context.MODE_MULTI_PROCESS);
		return sharedata.getString(PppoeDataEntity.INFO_PASSWORD, null);
	}
	
	public void setPppoePassword(String password){
	    SharedPreferences sharedata = mContext.getSharedPreferences(SHARE_PREFERENCE_FILE_NAME, Context.MODE_MULTI_PROCESS);
	    SharedPreferences.Editor editor = sharedata.edit();
	    editor.putString(INFO_PASSWORD, password);
	    editor.commit();
	}
	
	public boolean getPppoeAutoConnectFlag(){
		SharedPreferences sharedata = mContext.getSharedPreferences(SHARE_PREFERENCE_FILE_NAME,Context.MODE_MULTI_PROCESS);
		return sharedata.getBoolean(PppoeDataEntity.INFO_AUTOCONNECT, true);
	}
	
	public void setPppoeAutoConnectFlag(boolean autoConnect){
		SharedPreferences sharedata = mContext.getSharedPreferences(SHARE_PREFERENCE_FILE_NAME,Context.MODE_MULTI_PROCESS);
		Editor editor = sharedata.edit();
		editor.putBoolean(PppoeDataEntity.INFO_AUTOCONNECT, autoConnect);
		editor.commit();
	}
	
	public void setPppoeRunningFlag() {
		SystemProperties.set(ETHERNET_DHCP_REPEAT_FLAG, "disabled");
		SystemProperties.set(PppoeDataEntity.PPPOE_RUNNING_FLAG, "100");
		String propVal = SystemProperties.get(PppoeDataEntity.PPPOE_RUNNING_FLAG);
		int n = 0;
		if (propVal.length() != 0) {
			try {
				n = Integer.parseInt(propVal);
			} catch (NumberFormatException e) {
			}
		} else {
			Log.e(TAG, "failed to setPppoeRunningFlag");
		}

		return;
	}

	public void clearPppoeRunningFlag() {
		SystemProperties.set(ETHERNET_DHCP_REPEAT_FLAG, "enabled");
		SystemProperties.set(PppoeDataEntity.PPPOE_RUNNING_FLAG, "0");
		String propVal = SystemProperties.get(PppoeDataEntity.PPPOE_RUNNING_FLAG);
		int n = 0;
		if (propVal.length() != 0) {
			try {
				n = Integer.parseInt(propVal);
			} catch (NumberFormatException e) {
			}
		} else {
			Log.e(TAG, "failed to clearPppoeRunningFlag");
		}

		return;
	}
	
//	public int getPppoeConnState() {
//		if(mPppOperation == null){
//			Log.e(TAG, "mPppOperation is null.......");
//			return -1;
//		}
//		return mPppOperation.status(PppoeDataEntity.PPPOE_INTERNET_INTERFACE);
//	}
	
//	public boolean connect(String device, String name, String password){
//		if(mPppOperation == null){
//			Log.e(TAG, "mPppOperation is null.......");
//			return false;
//		}
//		return mPppOperation.connect(device, name, password);
//	}
//	
//	public boolean disconnect(){
//		if(mPppOperation == null){
//			Log.e(TAG, "mPppOperation is null.......");
//			return false;
//		}
//		return  mPppOperation.disconnect();
//	}
	
	public void getPppoeDevInfo(){
		if(mPppoeManager!=null){
			mPppoeDevinfo = mPppoeManager.getSavedPppoeConfig();
		}
	}
	
	public String getPppoeIpAddress(){
		if(mPppoeDevinfo==null){
			Log.e(TAG, "getPppoeIpAddress mPppoeDevinfo is null.........");
			return "0.0.0.0";
		}
		return  mPppoeDevinfo.getIpAddress();
	}
	
	public String getPppoeNetmask(){
		if(mPppoeDevinfo==null){
			Log.e(TAG, "getPppoeNetmask mPppoeDevinfo is null.........");
			return "255.255.255.0";
		}
		return  mPppoeDevinfo.getNetMask();
	}
	
	public String getPppoeDnsAddress(){
		if(mPppoeDevinfo==null){
			Log.e(TAG, "getPppoeDnsAddress mPppoeDevinfo is null.........");			
			return "0.0.0.0";
			
		}
		return  mPppoeDevinfo.getDnsAddr();
	}
	
	public String getPppoeRouteAddress(){
		if(mPppoeDevinfo==null){
			Log.e(TAG, "getPppoeRouteAddress mPppoeDevinfo is null.........");
			return "0.0.0.0";
		}
		return  mPppoeDevinfo.getRouteAddr();
	}
}
