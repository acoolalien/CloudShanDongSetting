package com.shandong.cloudtv.settings;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.shandong.cloudtv.settings.util.ImageSharePreference;

public class BootCompletedReceiver extends BroadcastReceiver{

	private String MODE_TO_SETTING ="com.hiveview.cloudtv.MODE_TO_SETTING";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)||intent.getAction().equals("com.hiveview.cloudtv.yiqibangfjf")){
			Log.i("YQB", "----ModeSettingService Get the Boot Completed---- ");
			setProtectTime(context);
			/*Intent service = new Intent(context,ModeSettingService.class);
			context.startService(service);*/
		}else if(intent.getAction().equals(MODE_TO_SETTING)){
			int mode = intent.getIntExtra("mode", 0);
			ImageSharePreference mPreference = new ImageSharePreference(context);
			Log.i("YQB", "-----get the mode-----"+mode);
			mPreference.setModel(mode);
		}
	}

	private void setProtectTime(Context context){
		ImageSharePreference mPreference = null;
		mPreference = new ImageSharePreference(context);
		int time = mPreference.getProtectTime();
		Log.i("YQB","------ProtectTime------" + time);
		Settings.System.putInt(context.getContentResolver(), SCREEN_OFF_TIMEOUT, time);
	}
	
}
