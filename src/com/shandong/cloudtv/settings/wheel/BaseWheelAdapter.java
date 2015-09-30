/**
 * @Title BaseWheelAdapter.java
 * @Package com.example.testlistviewfortv
 * @author haozening
 * @date 2014年9月29日 下午6:11:00
 * @Description 
 * @version V1.0
 */
package com.shandong.cloudtv.settings.wheel;

import android.view.View;
import android.widget.BaseAdapter;

/**
 * @ClassName BaseWheelAdapter
 * @Description
 * @author haozening
 * @date 2014年9月29日 下午6:11:00
 * 
 */
public abstract class BaseWheelAdapter extends BaseAdapter {

	/**
	 * 获取当前Item
	 * 
	 * @Title currentItem
	 * @author haozening
	 * @Description
	 * @param currentView
	 */
	public void currentView(View currentView) {

	}

	/**
	 * 获取前一个Item
	 * 
	 * @Title preItem
	 * @author haozening
	 * @Description
	 * @param preView
	 */
	public void preView(View preView) {

	}

	/**
	 * 获取后一个Item
	 * 
	 * @Title nextItem
	 * @author haozening
	 * @Description
	 * @param nextView
	 */
	public void nextView(View nextView) {

	}

}
