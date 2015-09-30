package com.shandong.cloudtv.settings.util;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationPreference {

	private final String PREFERENCE = "locationsettings_pref";
	private final String PACKAGE_NAME = "com.hiveview.weather";
	public static final String WEATHER_INFO = "com.hiveview.weather.PREFERENCE_WEATHER_INFO";
	public static final String WEATHER_IMAGE = "com.hiveview.weather.PREFERENCE_WEATHER_IMAGE";
	private Context c = null;
	public static final String CITIESID = "com.hiveview.tv.cities";

	private SharedPreferences.Editor editor;
	private SharedPreferences preference;
	private Context context;

	@SuppressWarnings("unused")
	private LocationPreference() {
	};

	public LocationPreference(Context context) {
		/*
		 * try { c = context.createPackageContext(PACKAGE_NAME,
		 * Context.CONTEXT_IGNORE_SECURITY); } catch (NameNotFoundException e) {
		 * e.printStackTrace(); } preference =
		 * c.getSharedPreferences(PREFERENCE, Context.MODE_MULTI_PROCESS);
		 */

		preference = context.getSharedPreferences(PREFERENCE,
				Context.MODE_WORLD_READABLE | Context.MODE_MULTI_PROCESS);

		this.editor = preference.edit();
		this.context = context;
	}

	public SharedPreferences getSharedPreferences() {
		return preference;
	}

	public void setLastProvinceIndex(int index) {
		editor.putInt("provinceIndex", index);
		editor.commit();
	}

	public void setLastCityIndex(int index) {
		editor.putInt("cityIndex", index);
		editor.commit();
	}

	public void setLastZoneIndex(int index) {
		editor.putInt("zoneIndex", index);
		editor.commit();
	}

	public int getLastProvinceIndex() {
		return preference.getInt("provinceIndex", 0);
	}

	public int getLastCityIndex() {
		return preference.getInt("cityIndex", 0);
	}

	public int getLastZoneIndex() {
		return preference.getInt("zoneIndex", 0);
	}

	public String getWeahterImageUrl() {

		return preference.getString(WEATHER_IMAGE, null);
	}

	public void setCityId(String cityId) {
		editor.putString(CITIESID, cityId);
		editor.commit();
	}

	public String getCityID() {
		String cityId = preference.getString(CITIESID, "101010100");
		return cityId;
	}

}
