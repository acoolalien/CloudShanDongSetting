package com.shandong.cloudtv.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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
import com.shandong.cloudtv.settings.widget.CustomProgressDialog;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.wifi.WifiDataEntity;

public class WifiShareActivity extends Activity {
	private static final String TAG = "WifiShare";
	private static final boolean DEBUG = true;

	private static final int OPEN_INDEX = 0;
	private static final int WPA_INDEX = 1;
	private static final int WPA2_INDEX = 2;
	private static final String DEFAULT_PASSWORD = "default_password";
	private static final String CLASS_NAME = "SoftAPConfigFragment";
	private RelativeLayout mWifiShareOnOffRlyout = null;
	private TextView mWifiShareOnOffTv = null;
	private Button mWifiShareOnOffLeftArrowBtn, mWifiShareOnOffRightArrowBtn;
	private RelativeLayout mWifiShareNameRlyout = null;
	private EditText mWifiShareNameEdit = null;
	private RelativeLayout mWifiShareSecurityRlyout = null;
	private TextView mWifiShareSecurityTv = null;
	private Button mWifiShareSecurityLeftArrowBtn, mWifiShareSecurityRightArrowBtn;
	private RelativeLayout mWifiSharePasswordRlyout = null;
	private EditText mWifiSharePasswordEdit = null;
	private TextView mWifiShareNameTitle = null;
	private TextView mWifiShareSecurityTitle = null;
	private TextView mWifiSharePasswordTitle = null;

	private String[] mOnOff = null;
	private int mOnOffIndex = 0;
	private String[] mSecuritys = null;
	private int mSecurityIndex = OPEN_INDEX;
	private LinearLayout mWifishareMainLayout = null;
	private Button mCommitBtn = null;

	private WifiDataEntity mWifiDataEntity = null;
	private SharedPreferences mSharePreferences = null;
	private WifiConfiguration mWifiConfig = null;
	private IntentFilter mIntentFilter = null;
	private CustomProgressDialog mProgressDialog = null;
	private String mPassword = null;
	private boolean mCommitClickFlag = false;

	// focus view
	private View mItemFocusView = null;
	private LauncherFocusView mLauncherFocusView = null;
	private boolean mIsFirstIn = true;

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.wifi_share_main);
		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView) findViewById(R.id.wifi_share_focus_view);
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

		mWifiDataEntity = WifiDataEntity.getInstance(this);
		mSharePreferences = getSharedPreferences(CLASS_NAME, Context.MODE_PRIVATE);
		mWifiConfig = mWifiDataEntity.getWifiApConfiguration();
		mSecurityIndex = getSecurityTypeIndex();

		initView();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	private void initView() {
		mWifiShareOnOffRlyout = (RelativeLayout) findViewById(R.id.wifi_share_onoff_rlyout);
		mWifiShareOnOffRlyout.setOnKeyListener(mWifiShareOnOffOnKeyListener);
		mWifiShareOnOffRlyout.setOnFocusChangeListener(mWifiShareOnOffFocusChangeListener);
		mWifiShareOnOffTv = (TextView) findViewById(R.id.wifi_share_onoff_text);
		mWifiShareOnOffLeftArrowBtn = (Button) findViewById(R.id.wifi_share_onoff_leftarrow);
		mWifiShareOnOffRightArrowBtn = (Button) findViewById(R.id.wifi_share_onoff_rightarrow);

		mWifiShareNameRlyout = (RelativeLayout) findViewById(R.id.wifi_share_name_rlyout);
		mWifiShareNameRlyout.setOnFocusChangeListener(mNameFocusChangeListener);
		mWifiShareNameRlyout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mWifiShareNameRlyout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				mWifiShareNameEdit.setFocusable(true);
				mWifiShareNameEdit.requestFocus();
				InputMethodManager mng = (InputMethodManager) WifiShareActivity.this
						.getSystemService(Activity.INPUT_METHOD_SERVICE);
				mng.showSoftInput(mWifiShareNameEdit, InputMethodManager.SHOW_IMPLICIT);

			}
		});
		mWifiShareNameEdit = (EditText) findViewById(R.id.wifi_share_name_edit);
		mWifiShareNameEdit.setOnEditorActionListener(mEditorActionListener);
		//mWifiShareNameEdit.setText(mWifiConfig.SSID);
                mWifiShareNameEdit.setHint(mWifiConfig.SSID);
		mWifiShareNameEdit.setOnFocusChangeListener(mWifiShareNameEditOnFocusChangeListener);
		mWifiShareNameEdit.setOnEditorActionListener(mEditorActionListener);
		mWifiShareNameEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(mWifiShareNameEdit.getText().toString().length()>20){
					Toast.makeText(WifiShareActivity.this, getResources().
							getString(R.string.wifi_share_name_too_long), 1000).show();
					mWifiShareNameEdit.setText("");
				}
			}
		});

		mWifiShareSecurityRlyout = (RelativeLayout) findViewById(R.id.wifi_share_security_rlyout);
		mWifiShareSecurityRlyout.setOnKeyListener(mWifiShareSecurityOnKeyListener);
		mWifiShareSecurityRlyout.setOnFocusChangeListener(mSecurityFocusChangeListener);
		mWifiShareSecurityTv = (TextView) findViewById(R.id.wifi_share_security_text);
		mWifiShareSecurityLeftArrowBtn = (Button) findViewById(R.id.wifi_share_security_leftarrow);
		mWifiShareSecurityRightArrowBtn = (Button) findViewById(R.id.wifi_share_security_rightarrow);

		mWifiSharePasswordRlyout = (RelativeLayout) findViewById(R.id.wifi_share_password_rlyout);
		if (mSecurityIndex == OPEN_INDEX) {
			mWifiSharePasswordRlyout.setVisibility(View.INVISIBLE);
		}
		mWifiSharePasswordRlyout.setOnFocusChangeListener(mPasswordFocusChangeListener);
		mWifiSharePasswordRlyout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mWifiSharePasswordRlyout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				mWifiSharePasswordEdit.setFocusable(true);
				mWifiSharePasswordEdit.requestFocus();
				mWifiSharePasswordEdit.setSelection(mWifiSharePasswordEdit.getText().toString().length());
				InputMethodManager mng = (InputMethodManager) WifiShareActivity.this
						.getSystemService(Activity.INPUT_METHOD_SERVICE);
				mng.showSoftInput(mWifiSharePasswordEdit, InputMethodManager.SHOW_IMPLICIT);

			}
		});
		mWifiSharePasswordEdit = (EditText) findViewById(R.id.wifi_share_password_edit);
		mWifiSharePasswordEdit.setOnFocusChangeListener(mWifiSharePasswordEditOnFocusChangeListener);
		if (mSecurityIndex == WPA2_INDEX || mSecurityIndex == WPA_INDEX) {
			boolean isDefault_password = mSharePreferences.getBoolean(DEFAULT_PASSWORD, true);
			if (isDefault_password) {
				mWifiSharePasswordEdit.setText("");
			} else {
				mWifiSharePasswordEdit.setText(mWifiConfig.preSharedKey);
			}
			mWifiSharePasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
		}
        mWifiSharePasswordEdit.setOnEditorActionListener(mEditorActionListener);
		mOnOff = getResources().getStringArray(R.array.wifi_share_onoff);

		mSecuritys = getResources().getStringArray(R.array.wifi_softap_security);
		mWifiShareSecurityTv.setText(mSecuritys[mSecurityIndex]);
		mCommitBtn = (Button) findViewById(R.id.wifi_share_commit_btn);
		mCommitBtn.setOnClickListener(mCommitBtnOnClickListener);
		mCommitBtn.setOnFocusChangeListener(mCommitBtnFocusChangeListener);

		mWifiShareNameTitle = (TextView) findViewById(R.id.wifi_share_name_tv);
		mWifiShareSecurityTitle = (TextView) findViewById(R.id.wifi_share_security_tv);
		mWifiSharePasswordTitle = (TextView) findViewById(R.id.wifi_share_password_titile);

		mWifishareMainLayout = (LinearLayout) findViewById(R.id.widi_share_main_llyout);
//		mWifishareMainLayout.setBackground(new BitmapDrawable(getResources(), Utils.getApplicationBGBitmap()));

		initSoftApState();
	}

	private void initSoftApState() {
		int wifiApState = mWifiDataEntity.getWifiApState();

		if (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED || wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) {
			mOnOffIndex = 1;
			mWifiShareNameRlyout.setEnabled(true);
			mWifiShareNameRlyout.setFocusable(true);
			mWifiShareNameEdit.setEnabled(true);
			mWifiShareNameEdit.setFocusable(true);
			mWifiShareNameEdit.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mWifiShareNameTitle.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mWifiShareNameRlyout.setVisibility(View.VISIBLE);

			mWifiShareSecurityRlyout.setEnabled(true);
			mWifiShareSecurityRlyout.setFocusable(true);
			mWifiShareSecurityTv.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mWifiShareSecurityTitle.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mWifiShareSecurityRlyout.setVisibility(View.VISIBLE);

			mWifiSharePasswordRlyout.setEnabled(true);
			mWifiSharePasswordRlyout.setFocusable(true);
			mWifiSharePasswordEdit.setEnabled(true);
			mWifiSharePasswordEdit.setFocusable(true);
			mWifiSharePasswordEdit.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mWifiSharePasswordTitle.setTextColor(getResources().getColor(R.color.settings_ffffff));
			
			if(mSecurityIndex != OPEN_INDEX){
				mWifiSharePasswordRlyout.setVisibility(View.VISIBLE);
			}
			mCommitBtn.setEnabled(true);
			mCommitBtn.setFocusable(true);
			mCommitBtn.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mCommitBtn.setVisibility(View.VISIBLE);

		} else {
			mOnOffIndex = 0;
			mWifiShareNameRlyout.setEnabled(false);
			mWifiShareNameRlyout.setFocusable(false);
			mWifiShareNameEdit.setEnabled(false);
			mWifiShareNameEdit.setFocusable(false);
			mWifiShareNameEdit.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			mWifiShareNameTitle.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			mWifiShareNameRlyout.setVisibility(View.INVISIBLE);

			mWifiShareSecurityRlyout.setEnabled(false);
			mWifiShareSecurityRlyout.setFocusable(false);
			mWifiShareSecurityTv.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			mWifiShareSecurityTitle.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			mWifiShareSecurityRlyout.setVisibility(View.INVISIBLE);

			mWifiSharePasswordRlyout.setEnabled(false);
			mWifiSharePasswordRlyout.setFocusable(false);
			mWifiSharePasswordEdit.setEnabled(false);
			mWifiSharePasswordEdit.setFocusable(false);
			mWifiSharePasswordEdit.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			mWifiSharePasswordTitle.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			mWifiSharePasswordRlyout.setVisibility(View.INVISIBLE);
			
			mCommitBtn.setEnabled(false);
			mCommitBtn.setFocusable(false);
			mCommitBtn.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
			mCommitBtn.setVisibility(View.INVISIBLE);
			
		}
		mWifiShareOnOffTv.setText(mOnOff[mOnOffIndex]);
		enablePasswordView();
		stopProgressDialog();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mIntentFilter = new IntentFilter(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
		registerReceiver(mReceiver, mIntentFilter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	private View.OnFocusChangeListener mWifiShareNameEditOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (!arg1) {
				mWifiShareNameEdit.setFocusable(false);
				mWifiShareNameEdit.requestFocus();
				mWifiShareNameRlyout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
			}

		}
	};
	

	private View.OnFocusChangeListener mWifiSharePasswordEditOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (!arg1) {
				mWifiSharePasswordEdit.setFocusable(false);
				mWifiSharePasswordEdit.requestFocus();
				mWifiSharePasswordRlyout.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
				mPassword = mWifiSharePasswordEdit.getText().toString();
			}

		}
	};

	View.OnKeyListener mWifiShareOnOffOnKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View arg0, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					mOnOffIndex--;
					if (mOnOffIndex < 0) {
						mOnOffIndex = mOnOff.length - 1;
					}
					mWifiShareOnOffLeftArrowBtn.setSelected(true);
					mWifiShareOnOffRightArrowBtn.setSelected(false);
					setWifiShareOnOff();
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					mOnOffIndex++;
					if (mOnOffIndex >= mOnOff.length) {
						mOnOffIndex = 0;
					}
					mWifiShareOnOffLeftArrowBtn.setSelected(false);
					mWifiShareOnOffRightArrowBtn.setSelected(true);
					setWifiShareOnOff();
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
				case KeyEvent.KEYCODE_DPAD_UP:
					mWifiShareOnOffLeftArrowBtn.setSelected(false);
					mWifiShareOnOffRightArrowBtn.setSelected(false);
					break;
				default:
					break;
				}
				mWifiShareOnOffTv.setText(mOnOff[mOnOffIndex]);
			}
			return false;
		}
	};

	View.OnKeyListener mWifiShareSecurityOnKeyListener = new View.OnKeyListener() {

		@Override
		public boolean onKey(View arg0, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_LEFT:
					mSecurityIndex--;
					if (mSecurityIndex < 0) {
						mSecurityIndex = mSecuritys.length - 1;
					}
					mWifiShareSecurityLeftArrowBtn.setSelected(true);
					mWifiShareSecurityRightArrowBtn.setSelected(false);
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					mSecurityIndex++;
					if (mSecurityIndex >= mSecuritys.length) {
						mSecurityIndex = 0;
					}
					mWifiShareSecurityLeftArrowBtn.setSelected(false);
					mWifiShareSecurityRightArrowBtn.setSelected(true);

					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
				case KeyEvent.KEYCODE_DPAD_UP:
					mWifiShareSecurityLeftArrowBtn.setSelected(false);
					mWifiShareSecurityRightArrowBtn.setSelected(false);
					break;
				default:
					break;
				}
				enablePasswordView();
				mWifiShareSecurityTv.setText(mSecuritys[mSecurityIndex]);
			}
			return false;
		}
	};

	View.OnFocusChangeListener mWifiShareOnOffFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mItemFocusView = view;
				if (mIsFirstIn) {
					mIsFirstIn = false;
					mLauncherFocusView.initFocusView(mItemFocusView, false, 0f);
				} else {
					mLauncherFocusView.moveTo(mItemFocusView);
				}
			}
		}
	};

	View.OnFocusChangeListener mNameFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mItemFocusView = view;
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};

	View.OnFocusChangeListener mSecurityFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mItemFocusView = view;
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};

	View.OnFocusChangeListener mPasswordFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mItemFocusView = view;
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};

	View.OnClickListener mCommitBtnOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			mCommitClickFlag = true;
			if (DEBUG) {
				Log.e(TAG, "wifi share onclick...");
			}
			if(mWifiShareNameEdit.getText().toString().trim().equals("")){
				mWifiShareNameEdit.setText(mWifiConfig.SSID);
			}
			if (submitWifiConfig()) {
				if (DEBUG) {
					Log.e(TAG, " submitWifiConfig() success");
				}
				startProgressDialog(getString(R.string.wifi_share_start_soft));
			}
		}
	};

	View.OnFocusChangeListener mCommitBtnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mLauncherFocusView.setVisibility(View.INVISIBLE);
			} else {
				if (mWifiSharePasswordRlyout.isFocused() || mWifiShareSecurityRlyout.isFocused()) {
					mLauncherFocusView.setVisibility(View.VISIBLE);
				}
			}
		}
	};

	private void setWifiShareOnOff() {
		int wifiApState = mWifiDataEntity.getWifiApState();
		if(DEBUG){
			Log.e(TAG, "wifi share state="+wifiApState);
		}
		if (wifiApState == WifiManager.WIFI_AP_STATE_ENABLED || wifiApState == WifiManager.WIFI_AP_STATE_ENABLING) {
			startProgressDialog(getResources().getString(R.string.wifi_share_close_softap));
			setSoftapEnabled(false);
			//initSoftApState();

		} else if (wifiApState == WifiManager.WIFI_AP_STATE_DISABLED || wifiApState == WifiManager.WIFI_AP_STATE_DISABLING
				|| wifiApState == WifiManager.WIFI_AP_STATE_FAILED) {
			startProgressDialog(getResources().getString(R.string.wifi_share_open_softap));
			setSoftapEnabled(true);
			//initSoftApState();
		}
	}

	@SuppressLint("NewApi")
	private void setSoftapEnabled(boolean enable) {
		final ContentResolver cr = WifiShareActivity.this.getContentResolver();
		/**
		 * Disable Wifi if enabling tethering
		 */
		int wifiState = mWifiDataEntity.getWifiState();
		if (enable && ((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
			mWifiDataEntity.setWifiEnabled(false);
			Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 1);
		}

		if (mWifiDataEntity.setWifiApEnabled(null, enable)) {
			/* Disable here, enabled on receiving success broadcast */
			if(DEBUG){
				Log.e(TAG, "setWifiApEnabled return true...");
			}
		} else {
			Toast.makeText(WifiShareActivity.this, getString(R.string.wifi_share_error_1), Toast.LENGTH_SHORT).show();
		}

		/**
		 * If needed, restore Wifi on tether disable
		 */
		if (!enable) {
			int wifiSavedState = 0;
			try {
				wifiSavedState = Settings.Global.getInt(cr, Settings.Global.WIFI_SAVED_STATE);
			} catch (Settings.SettingNotFoundException e) {
				;
			}
			if (wifiSavedState == 1) {
				mWifiDataEntity.setWifiEnabled(true);
				Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 0);
			}
		}
	}

	private boolean submitWifiConfig() {
		if (mWifiShareNameEdit.getText() != null && !TextUtils.isEmpty(mWifiShareNameEdit.getText())) {
			if (mSecurityIndex == OPEN_INDEX) {
				mWifiConfig = new WifiConfiguration();
				mWifiConfig.SSID = mWifiShareNameEdit.getText().toString();
				mWifiConfig.allowedKeyManagement.set(KeyMgmt.NONE);
				setSoftApConfiguration();
				setSoftapEnabled(true);
				return true;
			}else if (mSecurityIndex == WPA_INDEX) {
				mWifiConfig = new WifiConfiguration();
				mWifiConfig.SSID = mWifiShareNameEdit.getText().toString();
				mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
				mWifiConfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
				String password = null;
				password = mPassword;
				if (mPassword != null || mSharePreferences.getBoolean(DEFAULT_PASSWORD, true)) {
					password = mPassword;
				} else {
					password = mWifiDataEntity.getWifiApConfiguration().preSharedKey;
				}

				if (password != null && password.length() >= 8 && password.length() <= 63) {
					mWifiConfig.preSharedKey = password;
					mSharePreferences.edit().putBoolean(DEFAULT_PASSWORD, false).commit();
					setSoftApConfiguration();
					setSoftapEnabled(true);
					return true;
				} else {
					Toast.makeText(WifiShareActivity.this, R.string.wifi_password_error2, Toast.LENGTH_LONG).show();
					return false;
				}
			} 
			else if (mSecurityIndex == WPA2_INDEX) {
				mWifiConfig = new WifiConfiguration();
				mWifiConfig.SSID = mWifiShareNameEdit.getText().toString();
				mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
				mWifiConfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
				String password = null;
				password = mPassword;
				if (mPassword != null || mSharePreferences.getBoolean(DEFAULT_PASSWORD, true)) {
					password = mPassword;
				} else {
					password = mWifiDataEntity.getWifiApConfiguration().preSharedKey;
				}

				if (password != null && password.length() >= 8 && password.length() <= 63) {
					mWifiConfig.preSharedKey = password;
					mSharePreferences.edit().putBoolean(DEFAULT_PASSWORD, false).commit();
					setSoftApConfiguration();
					setSoftapEnabled(true);
					return true;
				} else {
					Toast.makeText(WifiShareActivity.this, R.string.wifi_password_error2, Toast.LENGTH_LONG).show();
					return false;
				}
			} else {
				return false;
			}
		} else {
			Toast.makeText(WifiShareActivity.this, R.string.wifi_share_error_2, Toast.LENGTH_LONG).show();
			return false;
		}

	}

	private void setSoftApConfiguration() {
		// TODO Auto-generated method stub
    	if (mWifiConfig != null) {
            /**
             * if soft AP is stopped, bring up
             * else restart with new config
             * TODO: update config on a running access point when framework support is added
             */
            if (mWifiDataEntity.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
            	mWifiDataEntity.setWifiApEnabled(null, false);
            	mWifiDataEntity.setWifiApEnabled(mWifiConfig, true);
            } else {
            	mWifiDataEntity.setWifiApConfiguration(mWifiConfig);
            }
        }
	}
	
	private void enablePasswordView() {
		if (mSecurityIndex == OPEN_INDEX) {
			mWifiSharePasswordRlyout.setVisibility(View.INVISIBLE);
		} else {
			if(mWifiDataEntity.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED){
				mWifiSharePasswordRlyout.setVisibility(View.VISIBLE);
			}
		}
	}

	private int getSecurityTypeIndex() {
		if (mWifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
			return WPA2_INDEX;
		}else if(mWifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)){
			return WPA_INDEX;
		}
		return OPEN_INDEX;
	}

	private void handleWifiApStateChanged(int state) {
		switch (state) {
		case WifiManager.WIFI_AP_STATE_ENABLING:
			if (DEBUG) {
				Log.e(TAG, "WIFI_AP_STATE_ENABLING...");
			}
			break;
		case WifiManager.WIFI_AP_STATE_ENABLED:
			if (DEBUG) {
				Log.e(TAG, "WIFI_AP_STATE_ENABLED...");
			}
			if(mCommitClickFlag){
				mCommitClickFlag = false;
				Toast.makeText(WifiShareActivity.this, 
						mWifiShareNameEdit.getText().toString().trim()+""+getResources().getText(R.string.wifi_share_open_success), Toast.LENGTH_SHORT).show();
				WifiShareActivity.this.finish();
			}
			initSoftApState();
			break;
		case WifiManager.WIFI_AP_STATE_DISABLING:
			if (DEBUG) {
				Log.e(TAG, "WIFI_AP_STATE_DISABLING...");
			}
			break;
		case WifiManager.WIFI_AP_STATE_DISABLED:
			if (DEBUG) {
				Log.e(TAG, "WIFI_AP_STATE_DISABLED...");
			}
			if(!mCommitClickFlag){
				initSoftApState();
			}
			break;
		case WifiManager.WIFI_AP_STATE_FAILED:
			if (DEBUG) {
				Log.e(TAG, "WIFI_AP_STATE_FAILED...");
			}
			initSoftApState();
			break;
		default:
		}
	}

	private void startProgressDialog(String msg) {
		if (mProgressDialog == null) {
			mProgressDialog = new CustomProgressDialog(WifiShareActivity.this);
		}
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
		mProgressDialog.startLoading();
	}

	private void stopProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.stopLoading();
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int message = -1;
			int rel = -1;
			String action = intent.getAction();

			if (WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
				handleWifiApStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE,
						WifiManager.WIFI_AP_STATE_FAILED));
			}
		}
	};
	
	
	private EditText.OnEditorActionListener mEditorActionListener = new EditText.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			// TODO Auto-generated method stub
			if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
					|| actionId == EditorInfo.IME_ACTION_NEXT) {
				try {
					if (DEBUG) {
						Log.e(TAG, "onclick enter =");
					}
					InputMethodManager mng = (InputMethodManager) WifiShareActivity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
 
				} catch (Exception e) {
					Log.e(TAG, "Hide key broad error", e);
				}
			}
			return false;
		}
	};
}
