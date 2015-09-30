package com.shandong.cloudtv.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.shandong.cloudtv.settings.BlueToothActivity;
import com.shandong.cloudtv.settings.R;

public class BluetoothScan implements Comparable<BluetoothScan>, CachedBluetoothDevice.Callback {
	private String name;
	private String address;
	private int status;
	private Drawable pageright;
	private Drawable pageleft;
	private final CachedBluetoothDevice mCachedDevice;
	private Context mContext;

	public CachedBluetoothDevice getCachedDevice() {
		return mCachedDevice;
	}

	public void askConnect() {
		mCachedDevice.disconnect();
	}

	public void pair() {
		if (!mCachedDevice.startPairing()) {
			Utils.showError(mContext, mCachedDevice.getName(),
					R.string.bluetooth_pairing_error_message);
		}
	}

	public BluetoothScan() {
		mCachedDevice = null;
	}

	public BluetoothScan(Context context, CachedBluetoothDevice cachedDevice) {
		mCachedDevice = cachedDevice;
		mContext = context;
		mCachedDevice.registerCallback(this);
		onDeviceAttributesChanged();
		name = mCachedDevice.getName();
		address = mCachedDevice.getDevice().getAddress();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int compareTo(BluetoothScan another) {
		// TODO Auto-generated method stub

		if (status < another.status)
			return -1;

		if (status > another.status)
			return 1;

		return 0;

	}

	public Drawable getPageright() {
		return pageright;
	}

	public void setPageright(Drawable pageright) {
		this.pageright = pageright;
	}

	public Drawable getPageleft() {
		return pageleft;
	}

	public void setPageleft(Drawable pageleft) {
		this.pageleft = pageleft;
	}

	@Override
	public void onDeviceAttributesChanged() {
		// TODO Auto-generated method stub
		this.setStatus(getConnectionSummary());
		Intent intent = new Intent();
		intent.setAction("com.hiveview.cloudtv.settings.bluetoothchange");
		mContext.sendBroadcast(intent);
	}

	private int getConnectionSummary() {
		final CachedBluetoothDevice cachedDevice = mCachedDevice;

		boolean profileConnected = false; // at least one profile is connected
		boolean a2dpNotConnected = false; // A2DP is preferred but not connected
		boolean headsetNotConnected = false; // Headset is preferred but not
												// connected

		for (LocalBluetoothProfile profile : cachedDevice.getProfiles()) {
			int connectionStatus = cachedDevice.getProfileConnectionState(profile);

			switch (connectionStatus) {
			case BluetoothProfile.STATE_CONNECTING:
				return BlueToothActivity.BLUETOOTH_CONNECTING;
			case BluetoothProfile.STATE_DISCONNECTING:
				return BlueToothActivity.BLUETOOTH_DISCONNECTING;

			case BluetoothProfile.STATE_CONNECTED:
				profileConnected = true;
				break;

			case BluetoothProfile.STATE_DISCONNECTED:
				if (profile.isProfileReady() && profile.isPreferred(cachedDevice.getDevice())) {
					if (profile instanceof A2dpProfile) {
						a2dpNotConnected = true;
					} else if (profile instanceof HeadsetProfile) {
						headsetNotConnected = true;
					}
				}
				break;
			}
		}

		if (profileConnected) {
			if (a2dpNotConnected && headsetNotConnected) {
				// return R.string.bluetooth_connected_no_headset_no_a2dp;
				return BlueToothActivity.BLUETOOTH_CONNECTED;
			} else if (a2dpNotConnected) {
				// return R.string.bluetooth_connected_no_a2dp;
				return BlueToothActivity.BLUETOOTH_CONNECTED;
			} else if (headsetNotConnected) {
				// return R.string.bluetooth_connected_no_headset;
				return BlueToothActivity.BLUETOOTH_CONNECTED;
			} else {
				// return R.string.bluetooth_connected;
				return BlueToothActivity.BLUETOOTH_CONNECTED;
			}
		}

		switch (cachedDevice.getBondState()) {
		case BluetoothDevice.BOND_BONDING:
			// return R.string.bluetooth_pairing;
			return BlueToothActivity.BLUETOOTH_BONDING;
		case BluetoothDevice.BOND_BONDED:
			return BlueToothActivity.BLUETOOTH_BONDED;
		case BluetoothDevice.BOND_NONE:
			return BlueToothActivity.BLUETOOTH_NOBOND;
		default:
			return 0;
		}
	}

}
