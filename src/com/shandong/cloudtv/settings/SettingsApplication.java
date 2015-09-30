package com.shandong.cloudtv.settings;

import android.app.Application;
import android.graphics.Bitmap;

public class SettingsApplication extends Application {
	private Bitmap mBGBitmap = null;
	private static SettingsApplication mApplication = null;
	private int mPingStatus = -999;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
	}
	
	public static SettingsApplication getInstance(){
		if(mApplication == null){
			mApplication = new SettingsApplication();
		}
		return mApplication;
	}
	
	public Bitmap getBGBitmap(){
		return mBGBitmap;
	}
	
	public void setBGBitmap(Bitmap bitmap){
		mBGBitmap = bitmap;
	}
	
	public void setPingStatus(int status){
		mPingStatus = status;
	}
	
	public int getPingStatus(){
		return mPingStatus;
	}
}
