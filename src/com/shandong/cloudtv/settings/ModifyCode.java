package com.shandong.cloudtv.settings;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;

import javax.security.auth.login.LoginException;

//import com.amlogic.pppoe.PppoeOperation;
import com.shandong.cloudtv.common.CommonItemList;
import com.shandong.cloudtv.settings.CommonActivity.CommonSettingOnList;
import com.shandong.cloudtv.settings.CommonActivity.CommonSettingOnList.ViewHolder;
import com.shandong.cloudtv.settings.pppoe.PppoeDataEntity;
import com.shandong.cloudtv.settings.util.ImageSharePreference;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.ContentResolver;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.net.pppoe.PppoeManager;

public class ModifyCode extends Activity{
	final String TAG = "ModifyCode";
	private final static String SUPUR_PASSWORD = "600804";
	private final static String DEFAULT_PASSWORD = "201508";
	int[] flag = new int[4];
	List<CommonItemList> mCommonItemList = new ArrayList<CommonItemList>();
	public Context mContext = this;	
	private boolean mFocusAnimationEndFlag = false;
	private int mCurKeycode = KeyEvent.KEYCODE_0;
	private long mKeyDownTime = 0L;
	private View mView;
	public boolean bfocusViewInitStatus = true;
	private LauncherFocusView mLauncherFocusView = null;
	private View mItemFocusView = null;
	private RelativeLayout mPppoeUsernameRlyout = null;
	private EditText mPppoeUsernameEdit = null;
	private static final boolean DEBUG = true;
	private RelativeLayout mPppoePasswordRlyout = null;
	private boolean mIsFirstIn = true;
	private EditText mPppoePasswordEdit = null;
	private EditText mPppoePasswordEdit1 = null;
	private RelativeLayout mPppoePasswordRlyout1 = null;
	private Button mPppoeConnectBtn1 = null;
	private Button mPppoeConnectBtn2 = null;
	private LinearLayout mPppoeConnectMainLayout = null;
	int mode=MODE_WORLD_READABLE+MODE_WORLD_WRITEABLE;
	SharedPreferences perfer;
	SharedPreferences.Editor editor;
	
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modifycode);
		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView) findViewById(R.id.activity_modifycode_focus_view);
		perfer=getSharedPreferences("code000",mode);		

	mLauncherFocusView.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

		@Override
		public void OnAnimateStart(View currentFocusView) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void OnAnimateEnd(View currentFocusView) {
			// TODO Auto-generated method stub
			//mLauncherFocusView.initFocusView(mItemFocusView, false, 0f);
		}
	});
	initView();
}

	
	private void initView() {
		mPppoeUsernameRlyout = (RelativeLayout) findViewById(R.id.pppoe_connect_name_rlyout);     
		mPppoeUsernameEdit = (EditText) findViewById(R.id.pppoe_connect_user_edit_1);
		mPppoeUsernameEdit.setOnFocusChangeListener(mPppoeUsernameEditOnFocusChangeListener);		
		mPppoeUsernameEdit.setKeyListener(new DigitsKeyListener(false,true));

		mPppoePasswordRlyout = (RelativeLayout) findViewById(R.id.pppoe_connect_password_rlyout);
		mPppoePasswordEdit = (EditText) findViewById(R.id.pppoe_connect_password_edit_1);
		mPppoePasswordEdit.setOnFocusChangeListener(mPppoePasswordEditOnFocusChangeListener);
		mPppoePasswordEdit.setKeyListener(new DigitsKeyListener(false,true));
		
		
		mPppoePasswordRlyout1 = (RelativeLayout) findViewById(R.id.confirmnewcode);                         
		mPppoePasswordEdit1 = (EditText) findViewById(R.id.pppoe_connect_password_edit_2);
		mPppoePasswordEdit1.setOnFocusChangeListener(mPppoePassword1EditOnFocusChangeListener);
		mPppoePasswordEdit1.setKeyListener(new DigitsKeyListener(false,true));
	

		mPppoeConnectBtn1 = (Button) findViewById(R.id.button1);
		mPppoeConnectBtn1.setOnFocusChangeListener(mConnectBtn1LayoutOnFocusChangeListener);
		mPppoeConnectBtn1.setOnClickListener(mConnectBtn1OnClickListener);
		
		mPppoeConnectBtn2 = (Button) findViewById(R.id.button2);
		mPppoeConnectBtn2.setOnFocusChangeListener(mConnectBtn2LayoutOnFocusChangeListener);
		mPppoeConnectBtn2.setOnClickListener(mConnectBtn2OnClickListener);

		mPppoeConnectMainLayout = (LinearLayout) findViewById(R.id.pppoe_connect_main_llyout);

	}

	View.OnFocusChangeListener mPppoeUsernameEditOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			
			mItemFocusView = mPppoeUsernameRlyout;
			if (mIsFirstIn) {
				mIsFirstIn = false;
				mLauncherFocusView.initFocusView(mItemFocusView, false, 0f);
			} else {
				mLauncherFocusView.setVisibility(View.VISIBLE);	
				mLauncherFocusView.moveTo(mItemFocusView);
			}    
		}
	};	

	View.OnFocusChangeListener mPppoePasswordEditOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mItemFocusView = mPppoePasswordRlyout;
				mLauncherFocusView.setVisibility(View.VISIBLE);	
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};	
	
	View.OnFocusChangeListener mPppoePassword1EditOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				Log.e("huxing", "huxing------------focuse");
				mItemFocusView = mPppoePasswordRlyout1;
				mLauncherFocusView.setVisibility(View.VISIBLE);	
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};
	
	View.OnFocusChangeListener mConnectBtn1LayoutOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mLauncherFocusView.setVisibility(View.INVISIBLE);
			}
		}
	};	
	
	View.OnFocusChangeListener mConnectBtn2LayoutOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mLauncherFocusView.setVisibility(View.INVISIBLE);	
			}
		}
	};                        
	
	
	View.OnClickListener mConnectBtn2OnClickListener =new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	};

	View.OnClickListener mConnectBtn1OnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub	
			String name_1 = (String) mPppoeUsernameEdit.getText().toString(); 
			String passwd_1 = (String)mPppoePasswordEdit.getText().toString();
			String passwd1_1 = (String)mPppoePasswordEdit1.getText().toString();
			
			
			if(name_1.length() < 6 ) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.orginputless6), Toast.LENGTH_SHORT).show();
				return;
			}
			
			if( passwd_1.length() < 6) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.newinputless6), Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(passwd1_1.length() < 6) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.confirminputless6), Toast.LENGTH_SHORT).show();
				return;
			} 
			
			if(!name_1.equals(SUPUR_PASSWORD)) {
				String dbpassword = perfer.getString("password",null);
				if(dbpassword == null) 
					dbpassword = DEFAULT_PASSWORD;
				
			    if(!name_1.equals(dbpassword))
				{
			    	Toast.makeText(mContext, mContext.getResources().getString(R.string.initcodewrong), Toast.LENGTH_SHORT).show();
			    	mPppoeUsernameEdit.setText("");
			    	return;
				}
			}
			
		    if(!passwd_1.equals(passwd1_1)){
				Toast.makeText(mContext, mContext.getResources().getString(R.string.modifywrongcode), Toast.LENGTH_SHORT).show();
				mPppoePasswordEdit1.setText("");
				return;
		    }		    
		    
		    if(passwd_1.equals(passwd1_1)){ 	
				editor=perfer.edit();
				editor.putString("password", passwd_1);
				editor.commit(); 	           	
				Toast.makeText(mContext, mContext.getResources().getString(R.string.finishcodemodify), Toast.LENGTH_SHORT).show();
		    	finish();
		    }		   
		}                                 
	};
}                 
	
	


	

	















	
