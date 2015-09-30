package com.shandong.cloudtv.settings.ethernet;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.text.TextUtils;
import android.util.Log;

public class EthernetDataEntity {
	private static final String TAG = "EthernetDataEntity";
	private static final boolean DEBUG = true; 
	public static final int ALERTMSG_IPINVALID = 0x101;
	public static final int ALERTMSG_IPNOGATEWAY = 0x102;
	public static final int ALERTMSG_MAX_ITEM_255 = 0x103;
	public static final int ALERTMSG_EXCEED_CHRATER = 0x104;
	public static final int ALERTMSG_IP_EQUAL_GATEWAY = 0x105;
	public static final int ALERTMSG_NETSTATE_PROGRESS = 0x106;
	
	public static final int MSG_IPV4_ADJUST_DHCP = 0x1000;
	public static final int MSG_IPV4_ADJUST_STATIC = 0x1001;
	public static final int MSG_IPV6_ADJUST_DHCP = 0x1002;
	public static final int MSG_IPV6_ADJUST_STATIC = 0x1003;
	public static final int MSG_IPV4_GET_DATA = 0x1004;
	
	public static final int IP_MAX_LENGTH = 15;
	private static EthernetDataEntity mEthernetDataEntity = null;
	private EthernetManager mEthernetManager = null;
    private EthernetDevInfo mEthernetDevInfo = null;
    private boolean isAutoConfig = false;
    private DhcpInfo mEthernetDhcpInfo = null;
    private Context mContext = null;
    private String mIPAddress = null;
    private String mDNSAddress = null;
    private String mGatewayAddress = null;
    private String mNetmaskAddress = null;
    
	public static EthernetDataEntity getInstance(Context context){
		if(mEthernetDataEntity == null){
			mEthernetDataEntity = new EthernetDataEntity(context);
		}
		return mEthernetDataEntity;
	}
	
	public EthernetDataEntity(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mEthernetManager = (EthernetManager)context.getSystemService(Context.ETH_SERVICE);
		mEthernetDevInfo = mEthernetManager.getSavedEthConfig();
		if (mEthernetDevInfo.getConnectMode().equals(EthernetDevInfo.ETH_CONN_MODE_DHCP)) {
			isAutoConfig = true;
		} else {
			isAutoConfig = false;
		}
		if(isAutoConfig){
			mEthernetDhcpInfo = mEthernetManager.getDhcpInfo();
		}
	}
	
	public DhcpInfo getEthernetDhcpInfo(boolean isReget){
		if(isReget){
			mEthernetDhcpInfo = mEthernetManager.getDhcpInfo();
		}
		
		return mEthernetDhcpInfo;
	}
	
	public EthernetDevInfo getEthernetManualInfo(boolean isReget){
		if(isReget){
			mEthernetDevInfo = mEthernetManager.getSavedEthConfig();
		}
		return mEthernetDevInfo;
	}
	
	public boolean getAutoFlag(boolean isReget){
		if(isReget){
			mEthernetDevInfo = mEthernetManager.getSavedEthConfig();
			if (mEthernetDevInfo.getConnectMode().equals(EthernetDevInfo.ETH_CONN_MODE_DHCP)) {
				isAutoConfig = true;
			} else {
				isAutoConfig = false;
			}
		}
		return isAutoConfig;
	}
	
	public String getIPAddress(boolean isAutoFlag){
		if(isAutoFlag){
			if(mEthernetDhcpInfo == null){
				Log.e(TAG, "mEthernetDhcpInfo is null");
				return null;
			}
			mIPAddress = getAddress(mEthernetDhcpInfo.ipAddress);
		}else{
			if(mEthernetDevInfo == null){
				Log.e(TAG, "mEthernetManualInfo is null");
				return null;
			}
			mIPAddress = mEthernetDevInfo.getIpAddress();
			if(TextUtils.isEmpty(mIPAddress)){
				mIPAddress = null;
				Log.e(TAG, "get manual info ipaddress is null");
			}
		}
		
		return mIPAddress;
	}
	
	public void setIPAddress(String ipAddr){
		this.mIPAddress = ipAddr;
	}
	
	public String getGWAddress(boolean isAutoFlag){
		if(isAutoFlag){
			if(mEthernetDhcpInfo == null){
				Log.e(TAG, "mEthernetDhcpInfo is null");
				return null;
			}
			mGatewayAddress = getAddress(mEthernetDhcpInfo.gateway);
		}else{
			if(mEthernetDevInfo == null){
				Log.e(TAG, "mEthernetManualInfo is null");
				return null;
			}
			mGatewayAddress = mEthernetDevInfo.getRouteAddr();
			if(TextUtils.isEmpty(mGatewayAddress)){
				mGatewayAddress = null;
				Log.e(TAG, "get manual info gateway is null");
			}
		}
		
		return mGatewayAddress;
	}
	
	public void setGWAddress(String gateway){
		this.mGatewayAddress = gateway;
	}
	
	public String getDNSAddress(boolean isAutoFlag){
		if(isAutoFlag){
			if(mEthernetDhcpInfo == null){
				Log.e(TAG, "mEthernetDhcpInfo is null");
				return null;
			}
			mDNSAddress = getAddress(mEthernetDhcpInfo.dns1);
		}else{
			if(mEthernetDevInfo == null){
				Log.e(TAG, "mEthernetManualInfo is null");
				return null;
			}
			mDNSAddress = mEthernetDevInfo.getDnsAddr();
			if(TextUtils.isEmpty(mDNSAddress) || mDNSAddress.equals("0.0.0.0")){
				mDNSAddress = null;
				Log.e(TAG, "get manual info dns is null");
			}
		}
		
		return mDNSAddress;
	}
	
	public void setDNSAddress(String dns){
		this.mDNSAddress = dns;
	}
	
	public String getMaskAddress(boolean isAutoFlag){
		if(isAutoFlag){
			if(mEthernetDhcpInfo == null){
				Log.e(TAG, "mEthernetDhcpInfo is null");
				return null;
			}
			mNetmaskAddress = getAddress(mEthernetDhcpInfo.netmask);
		}else{
			if(mEthernetDevInfo == null){
				Log.e(TAG, "mEthernetManualInfo is null");
				return null;
			}
			mNetmaskAddress = mEthernetDevInfo.getNetMask();
			if(TextUtils.isEmpty(mNetmaskAddress)){
				mNetmaskAddress = null;
				Log.e(TAG, "get manual info net mask is null");
			}
		}
		
		return mNetmaskAddress;
	}
	
	public void setMaskAddress(String mask){
		this.mNetmaskAddress = mask;
	}
	
	public void updateEthDevInfo(boolean isAutoFlag,String ipAddr,String mask,String dns,String gateway) {
		EthernetDevInfo info = new EthernetDevInfo();
		info.setIfName("eth0");
		if(DEBUG){
			Log.e(TAG, "ip:"+ipAddr+" gw:"+gateway+" dns"+dns+" mask:"+mask);
		}
		
		if (isAutoFlag) {
			info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP);
			info.setIpAddress(null);
			info.setRouteAddr(null);
			info.setDnsAddr(dns);
			info.setNetMask(null);
		} else {
			info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_MANUAL);
			info.setIpAddress(ipAddr);
			info.setRouteAddr(gateway);
			info.setDnsAddr(dns);
			info.setNetMask(mask);
		}

		mEthernetManager.updateEthDevInfo(info);
	}
	
	private String getAddress(int addr) {
		return NetworkUtils.intToInetAddress(addr).getHostAddress();
	}
	
	
}
