/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shandong.cloudtv.settings.wifi;

import android.content.Context;
import android.net.LinkProperties;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.shandong.cloudtv.settings.R;

public class WifiAccessPoint implements java.lang.Comparable<WifiAccessPoint> {
	private static final String TAG = "WifiAccessPoint";

	private static final String KEY_DETAILEDSTATE = "key_detailedstate";
	private static final String KEY_WIFIINFO = "key_wifiinfo";
	private static final String KEY_SCANRESULT = "key_scanresult";
	private static final String KEY_CONFIG = "key_config";
	public static final int NONE = -1, STATE_CONNECTING = 1, STATE_CONNECTED = 2, STATE_DISABLE = 3, STATE_ERROR = 4;

	public static final int[] STATE_SECURED = { R.attr.state_encrypted };
	public static final int[] STATE_NONE = {};

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	public static final int SECURITY_NONE = 0;
	public static final int SECURITY_WEP = 1;
	public static final int SECURITY_PSK = 2;
	public static final int SECURITY_EAP = 3;
	
	//wifi mode 
	public static final int STATIC = 0;
	public static final int  DHCP = 1;
	public static final int  UNASSIGNED = 2;

	enum PskType {
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	public String ssid;
	public String bssid;
	public int security;
	public int networkId;
	boolean wpsAvailable = false;

	PskType pskType = PskType.UNKNOWN;

	private WifiConfiguration mConfig;
	/* package */ScanResult mScanResult;

	private int mRssi;
	private WifiInfo mInfo;
	private DetailedState mState;
	Context mContext;
	RefershCallbacks mCallbacks;
	public String pointState = "";
	public int wifiConnState = NONE;
	public LinkProperties mLinkProperties;
	public long errTime = -1L;
	public boolean isERRShow = false;

	// 1 :connecting , 2:connected,3:fail connect

	public interface RefershCallbacks {
		public void refersh();
	}

	static int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	private static int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	public String getSecurityString(boolean concise) {
		switch (security) {
		case SECURITY_EAP:
			return concise ? mContext.getString(R.string.wifi_security_short_eap) : mContext
					.getString(R.string.wifi_security_eap);
		case SECURITY_PSK:
			switch (pskType) {
			case WPA:
				return concise ? mContext.getString(R.string.wifi_security_short_wpa) : mContext
						.getString(R.string.wifi_security_wpa);
			case WPA2:
				return concise ? mContext.getString(R.string.wifi_security_short_wpa2) : mContext
						.getString(R.string.wifi_security_wpa2);
			case WPA_WPA2:
				return concise ? mContext.getString(R.string.wifi_security_short_wpa_wpa2) : mContext
						.getString(R.string.wifi_security_wpa_wpa2);
			case UNKNOWN:
			default:
				return concise ? mContext.getString(R.string.wifi_security_short_psk_generic) : mContext
						.getString(R.string.wifi_security_psk_generic);
			}
		case SECURITY_WEP:
			return concise ? mContext.getString(R.string.wifi_security_short_wep) : mContext
					.getString(R.string.wifi_security_wep);
		case SECURITY_NONE:
		default:
			return concise ? "" : mContext.getString(R.string.wifi_security_none);
		}
	}

	private static PskType getPskType(ScanResult result) {
		boolean wpa = result.capabilities.contains("WPA-PSK");
		boolean wpa2 = result.capabilities.contains("WPA2-PSK");
		if (wpa2 && wpa) {
			return PskType.WPA_WPA2;
		} else if (wpa2) {
			return PskType.WPA2;
		} else if (wpa) {
			return PskType.WPA;
		} else {
			Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
			return PskType.UNKNOWN;
		}
	}

	public WifiAccessPoint(Context context, WifiConfiguration config, RefershCallbacks mCallbacks) {
		mContext = context;
		loadConfig(config);
		this.mCallbacks = mCallbacks;
	}

	public WifiAccessPoint(Context context, ScanResult result, RefershCallbacks mCallbacks) {
		mContext = context;
		loadResult(result);
		this.mCallbacks = mCallbacks;
	}

	public WifiAccessPoint(Context context, Bundle savedState, RefershCallbacks mCallbacks) {
		mContext = context;
		this.mCallbacks = mCallbacks;
		mConfig = savedState.getParcelable(KEY_CONFIG);
		if (mConfig != null) {
			loadConfig(mConfig);
		}
		mScanResult = (ScanResult) savedState.getParcelable(KEY_SCANRESULT);
		if (mScanResult != null) {
			loadResult(mScanResult);
		}
		mInfo = (WifiInfo) savedState.getParcelable(KEY_WIFIINFO);
		if (savedState.containsKey(KEY_DETAILEDSTATE)) {
			mState = DetailedState.valueOf(savedState.getString(KEY_DETAILEDSTATE));
		}
		update(mInfo, mState);
	}

	public void saveWifiState(Bundle savedState) {
		savedState.putParcelable(KEY_CONFIG, mConfig);
		savedState.putParcelable(KEY_SCANRESULT, mScanResult);
		savedState.putParcelable(KEY_WIFIINFO, mInfo);
		if (mState != null) {
			savedState.putString(KEY_DETAILEDSTATE, mState.toString());
		}
	}

	private void loadConfig(WifiConfiguration config) {
		ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
		bssid = config.BSSID;
		security = getSecurity(config);
		networkId = config.networkId;
		mRssi = Integer.MAX_VALUE;
		mConfig = config;
	}

	private void loadResult(ScanResult result) {
		ssid = result.SSID;
		bssid = result.BSSID;
		security = getSecurity(result);
		wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
		if (security == SECURITY_PSK)
			pskType = getPskType(result);
		networkId = -1;
		mRssi = result.level;
		mScanResult = result;
	}

	@Override
	public int hashCode() {
		int result = 0;
		if (mInfo != null)
			result += 13 * mInfo.hashCode();
		result += 19 * mRssi;
		result += 23 * networkId;
		result += 29 * ssid.hashCode();
		return result;
	}

	public boolean update(ScanResult result) {
		if (ssid.equals(result.SSID) && security == getSecurity(result)) {
			if (WifiManager.compareSignalLevel(result.level, mRssi) > 0) {
				int oldLevel = getLevel();
				mRssi = result.level;
				if (getLevel() != oldLevel) {
					mCallbacks.refersh();
				}
			}
			// This flag only comes from scans, is not easily saved in config
			if (security == SECURITY_PSK) {
				pskType = getPskType(result);
			}
			refresh();
			return true;
		}
		return false;
	}

	public void update(WifiInfo info, DetailedState state) {
		boolean reorder = false;
		if (info != null && networkId != WifiConfiguration.INVALID_NETWORK_ID && networkId == info.getNetworkId()) {
			reorder = (mInfo == null);
			mRssi = info.getRssi();
			mInfo = info;
			mState = state;
			refresh();
		} else if (mInfo != null) {
			reorder = true;
			mInfo = null;
			mState = null;
			refresh();
		}

	}

	public int getLevel() {
		if (mRssi == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 4);
	}

	public WifiConfiguration getConfig() {
		return mConfig;
	}

	public WifiInfo getInfo() {
		return mInfo;
	}

	public DetailedState getState() {
		return mState;
	}

	static String removeDoubleQuotes(String string) {
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
			return string.substring(1, length - 1);
		}
		return string;
	}

	public static String convertToQuotedString(String string) {
		return "\"" + string + "\"";
	}

	public void refresh() {
		// TODO Auto-generated method stub
		if (mState != null) { // This is the active connection
			if (mState == DetailedState.CONNECTING || mState == DetailedState.AUTHENTICATING
					|| mState == DetailedState.OBTAINING_IPADDR) {
				wifiConnState = STATE_CONNECTING;
				pointState = Summary.get(mContext, mState);
			} else if (mState == DetailedState.CONNECTED) {
				wifiConnState = STATE_CONNECTED;
				pointState = "";
			}
		} else if (mConfig != null && mConfig.status == WifiConfiguration.Status.DISABLED) {
			switch (mConfig.disableReason) {
			case WifiConfiguration.DISABLED_AUTH_FAILURE:
				wifiConnState = STATE_ERROR;
				pointState = mContext.getResources().getString(R.string.wifi_disabled_password_failure);
				errTime = System.currentTimeMillis();
				break;
			case WifiConfiguration.DISABLED_DHCP_FAILURE:
			case WifiConfiguration.DISABLED_DNS_FAILURE:
				// case WifiConfiguration.DISABLED_UNKNOWN_REASON:
				wifiConnState = STATE_ERROR;
				pointState = mContext.getString(R.string.wifi_connected_failed);
				errTime = System.currentTimeMillis();
				break;
			}
		} 
		mCallbacks.refersh();

	}

	/**
	 * Generate and save a default wifiConfiguration with common values. Can
	 * only be called for unsecured networks.
	 * 
	 * @hide
	 */
	public void generateOpenNetworkConfig() {
		if (security != SECURITY_NONE)
			throw new IllegalStateException();
		if (mConfig != null)
			return;
		mConfig = new WifiConfiguration();
		mConfig.SSID = WifiAccessPoint.convertToQuotedString(ssid);
		mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
	}

	public int compareTo(WifiAccessPoint other) {

		// Active one goes first.
		if (mInfo != null && other.mInfo == null)
			return -1;
		if (mInfo == null && other.mInfo != null)
			return 1;
		// Reachable one goes before unreachable one.
		if (mRssi != Integer.MAX_VALUE && other.mRssi == Integer.MAX_VALUE)
			return -1;
		if (mRssi == Integer.MAX_VALUE && other.mRssi != Integer.MAX_VALUE)
			return 1;
		// Configured one goes before unconfigured one.
		if (networkId != WifiConfiguration.INVALID_NETWORK_ID
				&& other.networkId == WifiConfiguration.INVALID_NETWORK_ID)
			return -1;
		else if (networkId == WifiConfiguration.INVALID_NETWORK_ID
				&& other.networkId != WifiConfiguration.INVALID_NETWORK_ID)
			return 1;
		else if (networkId != WifiConfiguration.INVALID_NETWORK_ID
				&& other.networkId != WifiConfiguration.INVALID_NETWORK_ID) {
			return (int) (errTime - other.errTime);
		}

		// Sort by signal strength.
		int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
		if (difference != 0) {
			return difference;
		}
		// Sort by ssid.
		return ssid.compareToIgnoreCase(other.ssid);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof WifiAccessPoint))
			return false;
		return (compareTo((WifiAccessPoint) other) == 0);
	}
}
