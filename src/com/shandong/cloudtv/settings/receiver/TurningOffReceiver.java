package com.shandong.cloudtv.settings.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shandong.cloudtv.settings.TurningOffActivity;

public class TurningOffReceiver extends BroadcastReceiver {
	String ACTION = "com.hiveview.cloudtv.yiqibang.turningoff";

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if (arg1.getAction().equals(ACTION)) {
			Intent startUpdate = new Intent();
			startUpdate.setClass(arg0, TurningOffActivity.class);
			startUpdate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			arg0.startActivity(startUpdate);
		}

	}

}
