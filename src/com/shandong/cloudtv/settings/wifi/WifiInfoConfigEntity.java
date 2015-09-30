package com.shandong.cloudtv.settings.wifi;

import android.os.Parcel;
import android.os.Parcelable;

public class WifiInfoConfigEntity implements Parcelable{
	public static final String KEY = "wifi_config";
	public static final String SAVE_CONFIG = "save_config";
	public static final int RESULT_WIFI_MODIFY_OK = 0x1001;
	public static final int RESULT_WIFI_FORGET = 0x1002;
	public static final int RESULT_WIFI_ADD = 0x1003;
	public static final String SSID_KEY = "ssid_key";
	public static final String PASSWORD_KEY = "password_key";
	public static final String SECURITY_KEY = "security_key";
	public static final String WIFI_DISCONNECT_MODIFY_KEY = "wifi_dis_modify";
	
	private int mWifiPointSecurity = WifiAccessPoint.SECURITY_NONE;
	private int mIPv4Assignment = WifiAccessPoint.UNASSIGNED;
	private String mIPv4IPAddr = "0.0.0.0";
	private String mIPv4DNSAddr = "0.0.0.0";
	private String mIPv4GatewayAddr = "0.0.0.0";
	private String mIPv4NetmaskAddr = "255.255.255.0";
	
	private int mIPv6Assignment = WifiAccessPoint.UNASSIGNED;
	private String mIPv6IPAddr = "";
	private String mIPv6DNSAddr = "";
	private String mIPv6GatewayAddr = "";
	private String mIPv6NetmaskAddr = "";
	
	
	public void setWifiPointSecurity(int security){
		mWifiPointSecurity = security;
	}
	
	public int getWifiPointSecurity(){
		return mWifiPointSecurity;
	}
	
	public void setIPv4Assignment(int assignment){
		mIPv4Assignment = assignment;
	}
	
	public int getIPv4Assignment(){
		return mIPv4Assignment;
	}
	
	public void setIPv4IPAddr(String ipaddr){
		mIPv4IPAddr = ipaddr;
	}
	
	public String getIPv4IPAddr(){
		return mIPv4IPAddr;
	}
	
	public void setIPv4DNSAddr(String dns){
		mIPv4DNSAddr = dns;
	}
	
	public String getIPv4DNSAddr(){
		return mIPv4DNSAddr;
	}
	
	public void setIPv4GatewayAddr(String gateway){
		mIPv4GatewayAddr = gateway;
	}
	
	public String getIPv4GatewayAddr(){
		return mIPv4GatewayAddr;
	}
	
	public void setIPv4NetmaskAddr(String mask){
		mIPv4NetmaskAddr = mask;
	}
	
	public String getIPv4NetmaskAddr(){
		return mIPv4NetmaskAddr;
	}
	
	public void setIPv6Assignment(int assignment){
		mIPv6Assignment = assignment;
	}
	
	public int getIPv6Assignment(){
		return mIPv6Assignment;
	}
	
	public void setIPv6IPAddr(String ipaddr){
		mIPv6IPAddr = ipaddr;
	}
	
	public String getIPv6IPAddr(){
		return mIPv6IPAddr;
	}
	
	public void setIPv6DNSAddr(String dns){
		mIPv6DNSAddr = dns;
	}
	
	public String getIPv6DNSAddr(){
		return mIPv6DNSAddr;
	}
	
	public void setIPv6GatewayAddr(String gateway){
		mIPv6GatewayAddr = gateway;
	}
	
	public String getIPv6GatewayAddr(){
		return mIPv6GatewayAddr;
	}
	
	public void setIPv6NetmaskAddr(String mask){
		mIPv6NetmaskAddr = mask;
	}
	
	public String getIPv6NetmaskAddr(){
		return mIPv6NetmaskAddr;
	}
	
    public static final Parcelable.Creator<WifiInfoConfigEntity> CREATOR = new Creator<WifiInfoConfigEntity>() {    
        public WifiInfoConfigEntity createFromParcel(Parcel source) {    
        	WifiInfoConfigEntity mWifiConfig = new WifiInfoConfigEntity();    
        	mWifiConfig.mWifiPointSecurity = source.readInt();
        	mWifiConfig.mIPv4Assignment = source.readInt();
        	mWifiConfig.mIPv4IPAddr = source.readString();
        	mWifiConfig.mIPv4DNSAddr = source.readString();
        	mWifiConfig.mIPv4GatewayAddr = source.readString();
        	mWifiConfig.mIPv4NetmaskAddr = source.readString();
        	mWifiConfig.mIPv6Assignment = source.readInt();
        	mWifiConfig.mIPv6IPAddr = source.readString();
        	mWifiConfig.mIPv6DNSAddr = source.readString();
        	mWifiConfig.mIPv6GatewayAddr = source.readString();
        	mWifiConfig.mIPv6NetmaskAddr = source.readString();
            return mWifiConfig;    
        }    
        public WifiInfoConfigEntity[] newArray(int size) {    
            return new WifiInfoConfigEntity[size];    
        }    
    };  
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		// TODO Auto-generated method stub
    	parcel.writeInt(mWifiPointSecurity);
    	parcel.writeInt(mIPv4Assignment);
    	parcel.writeString(mIPv4IPAddr); 
    	parcel.writeString(mIPv4DNSAddr);
    	parcel.writeString(mIPv4GatewayAddr);
    	parcel.writeString(mIPv4NetmaskAddr);
    	parcel.writeInt(mIPv6Assignment);
    	parcel.writeString(mIPv6IPAddr); 
    	parcel.writeString(mIPv6DNSAddr);
    	parcel.writeString(mIPv6GatewayAddr);
    	parcel.writeString(mIPv6NetmaskAddr);
	}

}
