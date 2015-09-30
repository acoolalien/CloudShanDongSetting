package com.shandong.cloudtv.settings.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.shandong.cloudtv.settings.R;

public class ImageSharePreference {
	private SharedPreferences mPreference;
	private SharedPreferences.Editor mEditor;
	private String PREFERENCES = "image_settings";
	private String mUSER_DEFINE = "user_define";
	private String mCOLORTEMPERATURE = "colortemperature";
	private String mBRIGHTNESS = "brightness";
	private String mCONTRAST = "contrast";
	private String mSATURATION = "saturation";
	private String mHUE = "hue";
	private String mSHARPNESS = "sharpness";
	private String mBACKLIGHT = "backlight";
	private String mModel = "model";
	private String mTV_NAME ="tv_name";
	private Context mContext;

	public ImageSharePreference(Context context) {
		mPreference = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		mEditor = mPreference.edit();
		mContext = context;
	}
	
	public void setTVNAME(String name){
		mEditor.putString(mTV_NAME, name);
		mEditor.commit();
	}

	public void setModel(int a) {
		mEditor.putInt("model", a);
		mEditor.commit();
	}

	public void setProtectTime(int a) {
		mEditor.putInt("protecttime", a);
		mEditor.commit();
	}

	public void setUserDefine(boolean a) {
		mEditor.putBoolean(mUSER_DEFINE, a);
		mEditor.commit();
	}

	public void setColorTemperature(int a) {
		mEditor.putInt(mCOLORTEMPERATURE, a);
		mEditor.commit();
	}

	public void setBrightness(int a) {
		mEditor.putInt(mBRIGHTNESS, a);
		mEditor.commit();
	}

	public void setContrast(int a) {
		mEditor.putInt(mCONTRAST, a);
		mEditor.commit();
	}

	public void setSaturation(int a) {
		mEditor.putInt(mSATURATION, a);
		mEditor.commit();
	}

	public void setHue(int a) {
		mEditor.putInt(mHUE, a);
		mEditor.commit();
	}

	public void setSharpness(int a) {
		mEditor.putInt(mSHARPNESS, a);
		mEditor.commit();
	}

	public void setBacklight(int a) {
		mEditor.putInt(mBACKLIGHT, a);
		mEditor.commit();
	}
	
	public String getTVNAME(){
		return mPreference.getString(mTV_NAME, mContext.getResources().getString(R.string.cloud_tv));
	}

	public boolean getUserDefine() {
		return mPreference.getBoolean(mUSER_DEFINE, false);
	}

	public int getColorTemperature() {
		return mPreference.getInt(mCOLORTEMPERATURE, 0);
	}

	public int getBrightness() {
		return mPreference.getInt(mBRIGHTNESS, 50);
	}

	public int getContrast() {
		return mPreference.getInt(mCONTRAST, 50);
	}

	public int getSaturation() {
		return mPreference.getInt(mSATURATION, 50);
	}

	public int getHue() {
		return mPreference.getInt(mHUE, 50);
	}

	public int getSharpness() {
		return mPreference.getInt(mSHARPNESS, 50);
	}

	public int getBacklight() {
		return mPreference.getInt(mBACKLIGHT, 50);
	}

	public int getProtectTime() {
		return mPreference.getInt("protecttime", 900000);
	}

	public int getModel() {
		return mPreference.getInt(mModel, 0);
	}

}
