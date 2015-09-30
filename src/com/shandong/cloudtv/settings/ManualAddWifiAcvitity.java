package com.shandong.cloudtv.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.wifi.WifiAccessPoint;
import com.shandong.cloudtv.settings.wifi.WifiInfoConfigEntity;

public class ManualAddWifiAcvitity extends Activity{
	
	private RelativeLayout mManualWifiNameRelativeLayout = null;
	private EditText mManualWifiNameEdit = null;
	private RelativeLayout mManualWifiPasswordRelativeLayout = null;
	private EditText mManualWifiPasswordEdit = null;
	private RelativeLayout layout_manual_add_wifi_btn = null;
	
	private RelativeLayout mManualWifiSecurityRelativeLayout = null;
	private TextView mManualWifiSecurityText = null;
	private Button mSecurityLeftArrowBtn,mSecurityRightArrowBtn;
	private String[] wifiSecurity = null;
	private int mWifiSecurity = WifiAccessPoint.SECURITY_NONE;
	private LinearLayout mMainLlyout = null;
	private Button mCommitBtn = null;
	//focus view
	private View mItemFocusView = null;
	private LauncherFocusView mLauncherFocusView = null;
	private boolean mIsFirstIn = true;
	
	private InputMethodManager mng;
	private String TAG = "ManualAddWifiAcvitity";
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.manual_add_wifi);
		
		wifiSecurity = getResources().getStringArray(R.array.wifi_security_no_eap);
		
		mng = (InputMethodManager) ManualAddWifiAcvitity.this
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		
		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView)findViewById(R.id.manual_add_wifi_focus_view);
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void showKeyboard(View view){
		mng.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);	
	}
	
	
	private void hideKeyboard(View view){
		mng.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	@SuppressLint("NewApi")
	private void initView(){
		mManualWifiNameRelativeLayout = (RelativeLayout)findViewById(R.id.manual_wifi_name_rlyout);
		mManualWifiNameRelativeLayout.setOnFocusChangeListener(mNameLayoutFocusChangeListener);
		mManualWifiNameRelativeLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("name lyt onclick listener...............1");
				mManualWifiNameRelativeLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				mManualWifiNameEdit.setFocusable(true);
				mManualWifiNameEdit.requestFocus();
				showKeyboard(mManualWifiNameEdit);
			}
		});
		
		mManualWifiNameEdit = (EditText)findViewById(R.id.manual_wifi_name_edit);
		mManualWifiNameEdit.setSelection(mManualWifiNameEdit.getText().length());
		mManualWifiNameEdit.setOnFocusChangeListener(mManualWifiNameEditFocusChangeListener);
		mManualWifiNameEdit.setOnEditorActionListener(mEditorActionListener);
		
		mManualWifiSecurityRelativeLayout = (RelativeLayout)findViewById(R.id.manual_wifi_safe_rlyout);
		mManualWifiSecurityRelativeLayout.setOnKeyListener(mManualWifiSecurityOnKeyListener);
		mManualWifiSecurityRelativeLayout.setOnFocusChangeListener(mSecurityLayoutFocusChangeListener);
		mManualWifiSecurityText = (TextView)findViewById(R.id.manual_wifi_security_text);
		mManualWifiSecurityText.setText(wifiSecurity[mWifiSecurity]);
		mSecurityLeftArrowBtn = (Button)findViewById(R.id.manual_wifi_leftarrow);
		mSecurityRightArrowBtn = (Button)findViewById(R.id.manual_wifi_rightarrow);
		
		mManualWifiPasswordRelativeLayout = (RelativeLayout)findViewById(R.id.manual_wifi_password_rlyout);
		if(mWifiSecurity == WifiAccessPoint.SECURITY_NONE){
			mManualWifiPasswordRelativeLayout.setVisibility(View.INVISIBLE);
		}
		mManualWifiPasswordRelativeLayout.setOnFocusChangeListener(mPasswordLayoutFocusChangeListener);
		mManualWifiPasswordRelativeLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("password lyt onclick listener...............1");
				mManualWifiPasswordRelativeLayout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				mManualWifiPasswordEdit.setFocusable(true);
				mManualWifiPasswordEdit.requestFocus();
				showKeyboard(mManualWifiPasswordEdit);
			}
		});
		
		mManualWifiPasswordEdit = (EditText)findViewById(R.id.manual_wifi_password_edit);
		mManualWifiPasswordEdit.setSelection(mManualWifiPasswordEdit.getText().length());
		mManualWifiPasswordEdit.setOnFocusChangeListener(mManualWifiPasswordEditFocusChangeListener);
		mManualWifiPasswordEdit.addTextChangedListener(new EditChangedListener());
		mManualWifiPasswordEdit.setOnEditorActionListener(mEditorActionListener);
		/*mCommitBtn = (Button)findViewById(R.id.manual_add_wifi_btn);
		mCommitBtn.setOnFocusChangeListener(mCommitBtnOnFocusChangeListener);
		mCommitBtn.setOnClickListener(mCommitBtnOnClickListener);*/
		
		layout_manual_add_wifi_btn = (RelativeLayout)findViewById(R.id.layout_manual_add_wifi_btn);
		layout_manual_add_wifi_btn.setOnFocusChangeListener(mCommitBtnOnFocusChangeListener);
		layout_manual_add_wifi_btn.setOnClickListener(mCommitBtnOnClickListener);
		
		mMainLlyout =(LinearLayout)findViewById(R.id.manual_add_wifi_main_llyout);

		//mMainLlyout.setBackground(new BitmapDrawable(getResources(), Utils.getApplicationBGBitmap()));
		
	}
	
	
	private TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			Log.v(TAG, "mEditorActionListener");

			if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				try {
					InputMethodManager mng = (InputMethodManager) ManualAddWifiAcvitity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
					Log.e(TAG, "Hide key broad error", e);
				}
			}
			return false;
		}
	};
	
	
	 class EditChangedListener implements TextWatcher {

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        }

	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	        }

	        @Override
	        public void afterTextChanged(Editable s) {
	           String temp =  mManualWifiPasswordEdit.getText().toString().trim();
	           if(temp.length()>=64){
	        	   Toast.makeText(ManualAddWifiAcvitity.this, 
	        			   getResources().getString(R.string.wifi_password_error2), 1500).show();
	        	   mManualWifiPasswordEdit.setText("");
	           }
	        }
	};
	
	
	View.OnFocusChangeListener mManualWifiNameEditFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			System.out.println("name edit arg1 == "+arg1);
			if(!arg1){
				mManualWifiNameRelativeLayout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
				mManualWifiNameEdit.setFocusable(false);
				mManualWifiNameEdit.requestFocus();
				hideKeyboard(arg0);
			} 
		}
	};
	
	View.OnFocusChangeListener mManualWifiPasswordEditFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			System.out.println("password edit arg1 == "+arg1);
			if(!arg1){
				mManualWifiPasswordRelativeLayout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
				mManualWifiPasswordEdit.setFocusable(false);
				mManualWifiPasswordEdit.requestFocus();
			}
		}
	};
	
	View.OnKeyListener mManualWifiSecurityOnKeyListener = new View.OnKeyListener() {
		
		@Override
		public boolean onKey(View arg0, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					mWifiSecurity--;
					if(mWifiSecurity<0){
						mWifiSecurity = wifiSecurity.length-1;
					}
					mSecurityLeftArrowBtn.setSelected(true);
					mSecurityRightArrowBtn.setSelected(false);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					mWifiSecurity++;
					if(mWifiSecurity>=wifiSecurity.length){
						mWifiSecurity = 0;
					}
					mSecurityLeftArrowBtn.setSelected(false);
					mSecurityRightArrowBtn.setSelected(true);
					break;
				default:
					break;
				}
				mManualWifiSecurityText.setText(wifiSecurity[mWifiSecurity]);
				enablePasswordView();
			}
			return false;
		}
	};
	
	void enablePasswordView() {
		if (mWifiSecurity == WifiAccessPoint.SECURITY_NONE) {
			mManualWifiPasswordRelativeLayout.setVisibility(View.INVISIBLE);
		} else {
			mManualWifiPasswordRelativeLayout.setVisibility(View.VISIBLE);
		}
	}
	
	View.OnFocusChangeListener mNameLayoutFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mItemFocusView = view;
				if(mIsFirstIn){
					mIsFirstIn = false;
					mLauncherFocusView.initFocusView(mItemFocusView, false, 0f);
				}else{
					mLauncherFocusView.moveTo(mItemFocusView);
				}
			}
		}
	};
	
	View.OnFocusChangeListener mSecurityLayoutFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mItemFocusView = view;
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};
	
	View.OnFocusChangeListener mPasswordLayoutFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if(arg1){
				mItemFocusView = view;
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};
	
	View.OnClickListener mCommitBtnOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(checkDataValid()){
				String ssid = mManualWifiNameEdit.getText().toString();
				String password = mManualWifiPasswordEdit.getText().toString();
				
				Intent intent = new Intent(ManualAddWifiAcvitity.this, WifiConnectActivity.class);
				intent.putExtra(WifiInfoConfigEntity.SSID_KEY, ssid);
				intent.putExtra(WifiInfoConfigEntity.PASSWORD_KEY, password);
				intent.putExtra(WifiInfoConfigEntity.SECURITY_KEY, mWifiSecurity);
				ManualAddWifiAcvitity.this.setResult(WifiInfoConfigEntity.RESULT_WIFI_ADD, intent);
				ManualAddWifiAcvitity.this.finish();
			}
		}
	};
	
	private boolean checkDataValid(){
		String ssidErrMsg = null;
		String passwordErrMsg = null;
		boolean isSsidValid = false;
		boolean isPasswordValid = false;
		
		String nameValue = mManualWifiNameEdit.getText().toString();
		if (TextUtils.isEmpty(nameValue) || nameValue.length() > 20) {
			ssidErrMsg = getString(R.string.wifi_ssid_error);
			isSsidValid = false;
		} else {
			isSsidValid = true;
		}
		
		if (mWifiSecurity == WifiAccessPoint.SECURITY_NONE) {
			isPasswordValid = true;
		} else {
			String passwordValue = mManualWifiPasswordEdit.getText().toString();
			if (mWifiSecurity == WifiAccessPoint.SECURITY_PSK && passwordValue.length() < 8) {
				passwordErrMsg = getString(R.string.wifi_password_error2);
				isPasswordValid = false;
			} else if (mWifiSecurity == WifiAccessPoint.SECURITY_WEP
					&& TextUtils.isEmpty(passwordValue)) {
				passwordErrMsg = getString(R.string.wifi_password_error1);
				isPasswordValid = false;
			}else if (passwordValue.length() > 63) {
				passwordErrMsg = getString(R.string.wifi_password_error2);
				isPasswordValid = false;
			}else {
				isPasswordValid = true;
			}
		}
		if(!isSsidValid){
			Toast.makeText(ManualAddWifiAcvitity.this, ssidErrMsg, Toast.LENGTH_SHORT).show();
			return isSsidValid;
		}
		
		if(!isPasswordValid){
			Toast.makeText(ManualAddWifiAcvitity.this, passwordErrMsg, Toast.LENGTH_SHORT).show();
			return isPasswordValid;
		}
		
		return isSsidValid && isPasswordValid;
	}
	
	View.OnFocusChangeListener mCommitBtnOnFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			/*if(arg1){
				//mItemFocusView = view;
				//mLauncherFocusView.moveTo(mItemFocusView);
				mLauncherFocusView.setVisibility(View.INVISIBLE);
			}else{
				if(mManualWifiPasswordRelativeLayout.isFocused()||mManualWifiSecurityRelativeLayout.isFocused()){
					mLauncherFocusView.setVisibility(View.VISIBLE);
				}
			}*/
			
			if (arg1) {
				mItemFocusView = view;
				if (mIsFirstIn) {
					mIsFirstIn = false;
					mLauncherFocusView.initFocusView(mItemFocusView, false, 0f);
				} else {
					mLauncherFocusView.moveToLayout(mItemFocusView);
				}
			}
		}
	};
}
