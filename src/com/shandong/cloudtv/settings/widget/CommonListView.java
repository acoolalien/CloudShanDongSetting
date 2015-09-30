package com.shandong.cloudtv.settings.widget;

 
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;

 
public class CommonListView extends ListView {

	//add for KEYCODE_PAGE_DOWN KEYCODE_PAGE_UP foucs confusion
	private String TAG = "CommonListView";

	public CommonListView(Context context) {
		super(context);
	}

	public CommonListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CommonListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_PAGE_UP){
//    		Log.v(TAG, "onKeyDown>>>>>>>>>>>>");
			return true;
    	}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_PAGE_UP){
//    		Log.v(TAG, "onKeyUp>>>>>>>>>>>>");
			return true;
    	}
		return super.onKeyUp(keyCode, event);
	}
}
