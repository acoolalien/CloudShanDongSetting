package com.shandong.cloudtv.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shandong.cloudtv.settings.util.ImageSharePreference;
import com.shandong.cloudtv.settings.util.Utils;

public class BootCompletedProfilesSettings extends BroadcastReceiver {
	private ImageSharePreference mPreference = null;
    private boolean mSwitch = false;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			mPreference = new ImageSharePreference(context);
			int position = mPreference.getModel();
			int time = mPreference.getProtectTime();
			// 第一次开机设置屏幕保护时间
			//Settings.System.putInt(context.getContentResolver(), SCREEN_OFF_TIMEOUT, time);
			Log.i("YQB", "------Position-------" + position + "------ProtectTime------" + time);			
		}else if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
			Log.i("YQB", "------DOLBY-------");
		}else if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			if(state == BluetoothAdapter.STATE_ON){
				mPreference = new ImageSharePreference(context);
				BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
				mAdapter.setName(mPreference.getTVNAME());
				Log.i("YQB", "------the set name----"+mPreference.getTVNAME()+"-----the get name new--"+mAdapter.getName());
			}
		}
	}
	
	private void setLanguageFile(String a){
		 String address ="/cache/recovery/last_locale";
		 File file = new File(address);
		 if(!file.exists()){
			 try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		 }
		 BufferedWriter output;
			try {
				output = new BufferedWriter(new FileWriter(file));
			//	Log.i(TAG, "-----write----"+a);
				output.write(a);
				output.flush();
				output.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

}
