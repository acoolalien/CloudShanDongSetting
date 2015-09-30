package com.shandong.cloudtv.settings.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import com.shandong.cloudtv.settings.R;

import android.content.Context;
import android.util.Xml;

public class LocationUtils {


	public static HashMap<String, HashMap<String, HashMap<String, String>>> LoadFile(Context context) {

		try {
			InputStream inputStream1 = context.getResources().openRawResource(R.raw.weathercn);
			return loadData(inputStream1);
		} catch (Throwable e) {

		}

		return null;

	}
	
	public static HashMap<String, String> LoadCityFile(Context context) {

		try {
			InputStream inputStream1 = context.getResources().openRawResource(R.raw.weathercn);
			return loadCityData(inputStream1);
		} catch (Throwable e) {

		}

		return null;

	}
	

	public static HashMap<String, String> loadCityData(InputStream inStream) throws Throwable {

		HashMap<String, String> cityMap = new HashMap<String, String>();

		String name = null;
		String attribute = "";

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:

				break;

			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (parser.getAttributeCount() > 0) {
					attribute = parser.getAttributeName(0);
				}

				if (attribute.equals("did")) {
					cityMap.put(parser.getAttributeValue(0), name);
				}

				break;

			case XmlPullParser.END_TAG:

				break;
			}

			eventType = parser.next();

		}
		return cityMap;

	}

	public static HashMap<String, HashMap<String, HashMap<String, String>>> loadData(InputStream inStream) throws Throwable {

		HashMap<String, HashMap<String, HashMap<String, String>>> dataMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();

		HashMap<String, HashMap<String, String>> districtMap = null;

		HashMap<String, String> cityMap = null;

		String name = null;
		String attribute = "";

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:

				break;

			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (parser.getAttributeCount() > 0) {
					attribute = parser.getAttributeName(0);
				}

				if (attribute.equals("pid")) {
					districtMap = new HashMap<String, HashMap<String, String>>();
					dataMap.put(name, districtMap);
				}

				if (attribute.equals("did")) {
					cityMap = new HashMap<String, String>();
					districtMap.put(name, cityMap);
				}

				if (attribute.equals("cid")) {
					cityMap.put(name, parser.getAttributeValue(0));
				}
				break;

			case XmlPullParser.END_TAG:

				break;
			}

			eventType = parser.next();

		}

		return dataMap;

	}

	public static void LoadWheelList(Context context) {

		try {

			InputStream inputStream1 = context.getResources().openRawResource(R.raw.weathercn);
			loadWheelList(inputStream1);

		} catch (Throwable e) {

		}

	}

	private static ArrayList<String> provinceList = new ArrayList<String>();
	private static ArrayList<ArrayList<String>> cityList = new ArrayList<ArrayList<String>>();
	private static ArrayList<ArrayList<ArrayList<String>>> zoneList = new ArrayList<ArrayList<ArrayList<String>>>();

	public static void loadWheelList(InputStream inStream) throws Throwable {

		provinceList = new ArrayList<String>();

		cityList = new ArrayList<ArrayList<String>>();
		ArrayList<String> cityContentList = null;

		zoneList = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> zoneContentList = new ArrayList<ArrayList<String>>();
		ArrayList<String> zoneSubContentList = new ArrayList<String>();

		String name = null;
		String attribute = "";

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			switch (eventType) {

			case XmlPullParser.START_DOCUMENT:

				break;

			case XmlPullParser.START_TAG:

				name = parser.getName();

				if (parser.getAttributeCount() > 0) {

					attribute = parser.getAttributeName(0);

				}

				if (attribute.equals("pid")) {

					provinceList.add(name);
					cityContentList = new ArrayList<String>();
					cityList.add(cityContentList);

					zoneContentList = new ArrayList<ArrayList<String>>();
					zoneList.add(zoneContentList);

				}

				if (attribute.equals("did")) {

					cityContentList.add(name);

					zoneSubContentList = new ArrayList<String>();
					zoneContentList.add(zoneSubContentList);

				}

				if (attribute.equals("cid")) {

					zoneSubContentList.add(name);

				}
				break;

			case XmlPullParser.END_TAG:

				break;
			}
			eventType = parser.next();
		}

	}

	public static ArrayList<String> getProvinceList() {
		return provinceList;
	}
	
	public static ArrayList<ArrayList<String>> getCityList() {
		return cityList;
	}

	public static ArrayList<ArrayList<ArrayList<String>>> getZoneList() {
		return zoneList;
	}
}
