package com.shandong.cloudtv.settings;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.pppoe.PppoeStateTracker;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
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

//import com.amlogic.pppoe.PppoeOperation;
import com.shandong.cloudtv.settings.pppoe.PppoeDataEntity;
import com.shandong.cloudtv.settings.util.Utils;
import com.shandong.cloudtv.settings.widget.CustomProgressDialog;
import com.shandong.cloudtv.settings.widget.LauncherFocusView;
import com.shandong.cloudtv.settings.widget.LauncherFocusView.FocusViewAnimatorEndListener;
import com.shandong.cloudtv.settings.wifi.WifiDataEntity;

public class PppoeConnectActivity extends Activity {
	private static final String TAG = "Pppoe";
	private static final boolean DEBUG = true;
	public static final int MSG_CONNECT_TIMEOUT = 0xabcd0000;
	public static final int MSG_DISCONNECT_TIMEOUT = 0xabcd0010;

	private RelativeLayout mPppoeUsernameRlyout = null;
	private RelativeLayout mPppoePasswordRlyout = null;
	private EditText mPppoeUsernameEdit = null;
	private EditText mPppoePasswordEdit = null;
	private Button mPppoeConnectBtn = null;
	private RelativeLayout layout_pppoe_connect_ok_btn = null;
	private LinearLayout mPppoeConnectMainLayout = null;
	private static CustomProgressDialog mProgressDialog = null;
	private static CustomProgressDialog mConnectingDialog = null;
	private Toast mToast = null;

	private int mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_UNDEFINED;
	private PppoeDataEntity mPppoeDataEntity = null;
	private Timer mConnectTimer = null;
	private Timer mDisconnectTimer = null;

	// focus view
	private View mItemFocusView = null;
	private LauncherFocusView mLauncherFocusView = null;
	private boolean mIsFirstIn = true;

	private boolean mPppoeState = PppoeDataEntity.PPPOE_EXTRA_STATE_DISCONNECT;
	private WifiDataEntity mWifiDataEntity = null;

	private boolean isNativeGo = false;

	@Override
	protected void onCreate(Bundle budle) {
		// TODO Auto-generated method stub
		super.onCreate(budle);
		setContentView(R.layout.pppoe_connect);

		mIsFirstIn = true;
		mLauncherFocusView = (LauncherFocusView) findViewById(R.id.pppoe_connect_focus_view);
		mLauncherFocusView
				.setAnimatorEndListener(new FocusViewAnimatorEndListener() {

					@Override
					public void OnAnimateStart(View currentFocusView) {
						// TODO Auto-generated method stub

					}

					@Override
					public void OnAnimateEnd(View currentFocusView) {
						// TODO Auto-generated method stub
						mLauncherFocusView.initFocusView(mItemFocusView, false,
								0f);
					}
				});

		isNativeGo = getIntent().getBooleanExtra("isNativeGo", true);
		Log.v(TAG, "isNativeGo==" + isNativeGo);

		mPppoeDataEntity = PppoeDataEntity.getInstance(this);
		mWifiDataEntity = WifiDataEntity.getInstance(this);
		if (mWifiDataEntity.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED
				|| mWifiDataEntity.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLING) {
			mWifiDataEntity.setWifiApEnabled(null, false);
		}
		mPppoeState = getIntent().getBooleanExtra(
				PppoeDataEntity.PPPOE_EXTRA_STATE, false);
		initView();
		regeditReceiver();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mPppoeReceiver);
		unRegeditReceiver();
	}

	private EditText.OnEditorActionListener mEditorActionListener = new EditText.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// TODO Auto-generated method stub
			if (actionId == EditorInfo.IME_ACTION_DONE
					|| actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
				try {
					if (DEBUG) {
						Log.e(TAG, "onclick enter =");
					}
					InputMethodManager mng = (InputMethodManager) PppoeConnectActivity.this
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					mng.hideSoftInputFromWindow(view.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

				} catch (Exception e) {
					Log.e(TAG, "Hide key broad error", e);
				}
			}
			return false;
		}
	};

	@SuppressLint("NewApi")
	private void initView() {
		String name = mPppoeDataEntity.getPppoeName();
		String passwd = mPppoeDataEntity.getPppoePassword();

		mPppoeUsernameRlyout = (RelativeLayout) findViewById(R.id.pppoe_connect_name_rlyout);
		mPppoeUsernameRlyout
				.setOnFocusChangeListener(mUsernameLayoutOnFocusChangeListener);

		mPppoeUsernameRlyout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPppoeUsernameRlyout
						.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				mPppoeUsernameEdit.setFocusable(true);
				mPppoeUsernameEdit.requestFocus();
				mPppoeUsernameEdit.setSelection(mPppoeUsernameEdit.getText()
						.length());
				InputMethodManager mng = (InputMethodManager) PppoeConnectActivity.this
						.getSystemService(Activity.INPUT_METHOD_SERVICE);
				mng.showSoftInput(mPppoeUsernameEdit,
						InputMethodManager.SHOW_IMPLICIT);

			}
		});

		mPppoeUsernameEdit = (EditText) findViewById(R.id.pppoe_connect_user_edit);
		mPppoeUsernameEdit.setText(name);
		mPppoeUsernameEdit.setOnEditorActionListener(mEditorActionListener);
		mPppoeUsernameEdit
				.setOnFocusChangeListener(mPppoeUsernameEditOnFocusChangeListener);

		mPppoePasswordRlyout = (RelativeLayout) findViewById(R.id.pppoe_connect_password_rlyout);
		mPppoePasswordRlyout
				.setOnFocusChangeListener(mPasswordLayoutOnFocusChangeListener);

		mPppoePasswordRlyout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPppoePasswordRlyout
						.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				mPppoePasswordEdit.setFocusable(true);
				mPppoePasswordEdit.requestFocus();
				mPppoePasswordEdit.setSelection(mPppoePasswordEdit.getText()
						.length());
				InputMethodManager mng = (InputMethodManager) PppoeConnectActivity.this
						.getSystemService(Activity.INPUT_METHOD_SERVICE);
				mng.showSoftInput(mPppoePasswordEdit,
						InputMethodManager.SHOW_IMPLICIT);
			}
		});
		mPppoePasswordEdit = (EditText) findViewById(R.id.pppoe_connect_password_edit);
		mPppoePasswordEdit.setText(passwd);
		mPppoePasswordEdit.setOnEditorActionListener(mEditorActionListener);
		mPppoePasswordEdit
				.setOnFocusChangeListener(mPppoePasswordEditOnFocusChangeListener);

		// mPppoeConnectBtn = (Button) findViewById(R.id.pppoe_connect_ok_btn);
		// mPppoeConnectBtn.setOnFocusChangeListener(mConnectBtnLayoutOnFocusChangeListener);
		// mPppoeConnectBtn.setOnClickListener(mConnectBtnOnClickListener);

		layout_pppoe_connect_ok_btn = (RelativeLayout) findViewById(R.id.layout_pppoe_connect_ok_btn);
		layout_pppoe_connect_ok_btn
				.setOnFocusChangeListener(mConnectBtnLayoutOnFocusChangeListener);
		layout_pppoe_connect_ok_btn
				.setOnClickListener(mConnectBtnOnClickListener);

		mPppoeConnectMainLayout = (LinearLayout) findViewById(R.id.pppoe_connect_main_llyout);
		// mPppoeConnectMainLayout.setBackground(new
		// BitmapDrawable(getResources(), Utils.getApplicationBGBitmap()));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Log.v(TAG, "ppp status==" + mPppoeState);
		if (mPppoeState == PppoeDataEntity.PPPOE_EXTRA_STATE_CONNECT) {
			// if (mPppoeDataEntity.getPppoeConnState() ==
			// PppoeOperation.PPP_STATUS_CONNECTING
			// || BootCompletedReceiverAML.isPppoeConnectingFlag) {
			if (BootCompletedReceiverAML.isPppoeConnectingFlag) {
				startConnectingDialog();
			}
		} else {
//			if (mPppoeDataEntity.getPppoeConnState() == PppoeOperation.PPP_STATUS_DISCONNECTED) {
//				showToast(getResources().getString(
//						R.string.pppoe_state_disconnected));
//				finish();
//			} else {
//				handleStopDial();
//			}
		}
	}

	View.OnFocusChangeListener mPppoeUsernameEditOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (!arg1) {
				System.out.println("pppoe username arg1=" + arg1);
				mPppoeUsernameEdit.setFocusable(false);
				mPppoeUsernameEdit.requestFocus();
				mPppoeUsernameRlyout
						.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
				mPppoeDataEntity.setPppoeName(mPppoeUsernameEdit.getText()
						.toString());
			} else {
				mPppoeUsernameRlyout.requestFocus();
			}
		}
	};

	View.OnFocusChangeListener mPppoePasswordEditOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub
			if (!arg1) {
				System.out.println("pppoe password arg1=" + arg1);
				mPppoePasswordEdit.setFocusable(false);
				mPppoePasswordEdit.requestFocus();
				mPppoePasswordRlyout
						.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
				mPppoeDataEntity.setPppoePassword(mPppoePasswordEdit.getText()
						.toString());
			} else {
				mPppoePasswordRlyout.requestFocus();
			}
		}
	};

	View.OnFocusChangeListener mUsernameLayoutOnFocusChangeListener = new View.OnFocusChangeListener() {

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

	View.OnFocusChangeListener mPasswordLayoutOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View view, boolean arg1) {
			// TODO Auto-generated method stub
			if (arg1) {
				mItemFocusView = view;
				mLauncherFocusView.moveTo(mItemFocusView);
			}
		}
	};

	View.OnClickListener mConnectBtnOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (DEBUG) {
				Log.e(TAG, "pppoe connect click............");
			}
//			if (mPppoeDataEntity.getPppoeConnState() == PppoeOperation.PPP_STATUS_CONNECTED) {
//				handleStopDial();
//			}
			handleStartDial();
		}
	};

	View.OnFocusChangeListener mConnectBtnLayoutOnFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View arg0, boolean arg1) {
			// TODO Auto-generated method stub

			if (arg1) {
				mItemFocusView = arg0;
				if (mIsFirstIn) {
					mIsFirstIn = false;
					mLauncherFocusView.initFocusView(mItemFocusView, false, 0f);
				} else {
					mLauncherFocusView.moveToLayout(mItemFocusView);
				}
			}
		}
	};

	@SuppressLint("NewApi")
	private void handleStartDial() {
		String name = mPppoeDataEntity.getPppoeName();
		String passwd = mPppoeDataEntity.getPppoePassword();
		if ((name == null || TextUtils.isEmpty(name))
				|| (passwd == null || TextUtils.isEmpty(passwd))) {
			showToast(getResources().getString(
					R.string.pppoe_name_or_psw_is_null));
			return;
		}

		startConnectingDialog();

		((EthernetManager) PppoeConnectActivity.this
				.getSystemService(Context.ETH_SERVICE)).setEthEnabled(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		((WifiManager) PppoeConnectActivity.this
				.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (name != null && passwd != null) {
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case MSG_CONNECT_TIMEOUT:
						stopConnectingDialog();
						Log.e(TAG, "handleMessage: MSG_CONNECT_TIMEOUT");
						mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_CONNECT_FAILED;
						showToast(getResources().getString(
								R.string.pppoe_connect_failed));
						SystemProperties.set(
								PppoeDataEntity.SYS_PRO_PPP_CONN_STA, "false");
						break;
					}

					super.handleMessage(msg);
				}
			};

			mConnectTimer = new Timer();
			TimerTask checkTask = new TimerTask() {
				public void run() {
					Looper.prepare();
					Message message = new Message();
					showToast(getResources().getString(
							R.string.pppoe_connect_timeout));
					message.what = MSG_CONNECT_TIMEOUT;
					handler.sendMessage(message);
					Looper.loop();
				}
			};

			mConnectTimer.schedule(checkTask, 60000 * 2);

			mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_CONNECTING;
			mPppoeDataEntity.setPppoeRunningFlag();

//			mPppoeDataEntity.connect(PppoeDataEntity.PPPOE_INTERNET_INTERFACE,
//					name, passwd);

		}
		if (DEBUG) {
			Log.e(TAG, "handleStartDial end");
		}
	}

	private void handleStopDial() {
		mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_DISCONNECTING;
//		boolean result = mPppoeDataEntity.disconnect();

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_DISCONNECT_TIMEOUT:
					stopConnectingDialog();
					Log.e(TAG, "handleMessage: MSG_DISCONNECT_TIMEOUT");
					showToast(getResources().getString(
							R.string.pppoe_disconnect_failed));
//					if (mPppoeDataEntity.getPppoeConnState() == PppoeOperation.PPP_STATUS_CONNECTED) {
//						mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_CONNECTED;
//					} else {
//						mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_DISCONNECTED;
//					}

					mPppoeDataEntity.clearPppoeRunningFlag();
					SystemProperties.set(PppoeDataEntity.SYS_PRO_PPP_CONN_STA,
							"false");
					break;
				}

				super.handleMessage(msg);
			}
		};

		mDisconnectTimer = new Timer();
		TimerTask checkTask = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = MSG_DISCONNECT_TIMEOUT;
				handler.sendMessage(message);
			}
		};

		// Timeout after 50 seconds
		mDisconnectTimer.schedule(checkTask, 50000);
		// showLoadingDialog(getResources().getString(R.string.pppoe_disconnect_waiting_msg));
		startConnectingDialog();
	}

	/*
	 * private void showLoadingDialog(String message) { if (mProgressDialog ==
	 * null) { mProgressDialog = new
	 * CustomProgressDialog(PppoeConnectActivity.this); }
	 * mProgressDialog.setMessage(message); mProgressDialog.setCancelable(true);
	 * mProgressDialog.show(); mProgressDialog.startLoading(); }
	 * 
	 * private void stopLoadingDialog() { if (mProgressDialog != null &&
	 * mProgressDialog.isShowing()) { mProgressDialog.stopLoading();
	 * mProgressDialog.cancel(); } }
	 */

	private void startConnectingDialog() {
		if (mConnectingDialog != null && mConnectingDialog.isShowing()) {
			return;
		}

		if (null == mConnectingDialog) {
			mConnectingDialog = new CustomProgressDialog(
					PppoeConnectActivity.this);
		}
		mConnectingDialog.setMessage(getResources().getString(
				R.string.pppoe_connecting));
		mConnectingDialog.setCancelable(true);
		mConnectingDialog.show();
		mConnectingDialog.startLoading();

	}

	private void stopConnectingDialog() {
		if (null != mConnectingDialog) {
			mConnectingDialog.stopLoading();
			mConnectingDialog.dismiss();
			mConnectingDialog = null;
		}
	}

	private BroadcastReceiver mPppoeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (DEBUG) {
				Log.e(TAG, "mPppoeReceiver action=" + action);
			}
			if (action.equals(PppoeManager.PPPOE_STATE_CHANGED_ACTION)) {
				int event = intent.getIntExtra(PppoeManager.EXTRA_PPPOE_STATE,
						PppoeManager.PPPOE_STATE_UNKNOWN);
				if (DEBUG) {
					Log.e(TAG, "mPppoeReceiver event=" + event);
				}
				if (event == PppoeStateTracker.EVENT_CONNECTED) {
					if (DEBUG) {
						Log.e(TAG, "EVENT_CONNECTED ");
					}
					stopConnectingDialog();

					if (mPppoeStateValue == PppoeDataEntity.PPPOE_STATE_CONNECTING) {
						mConnectTimer.cancel();
					}
					mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_CONNECTED;

					if (null != mConnectingDialog) {
						stopConnectingDialog();
					}
					showToast(context.getString(R.string.pppoe_connect_success));
					SystemProperties.set(PppoeDataEntity.SYS_PRO_PPP_CONN_STA,
							"true");
					BootCompletedReceiverAML.isPppoeConnectingFlag = false;
					jump2EthernetConnect();
				} else if (event == PppoeStateTracker.EVENT_DISCONNECTED) {
					if (DEBUG) {
						Log.e(TAG, "EVENT_DISCONNECTED ");
					}
					stopConnectingDialog();
					if (mPppoeStateValue == PppoeDataEntity.PPPOE_STATE_DISCONNECTING) {
						mDisconnectTimer.cancel();
						mPppoeDataEntity.clearPppoeRunningFlag();
					}
					mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_DISCONNECTED;

					if (null != mConnectingDialog) {
						stopConnectingDialog();
					}
					showToast(context.getString(R.string.pppoe_disconnect_ok));
					SystemProperties.set(PppoeDataEntity.SYS_PRO_PPP_CONN_STA,
							"false");
					BootCompletedReceiverAML.isPppoeConnectingFlag = false;
					jump2EthernetDisconnect();
				} else if (event == PppoeStateTracker.EVENT_CONNECT_FAILED) {
					if (DEBUG) {
						Log.e(TAG, "EVENT_CONNECT_FAILED ");
					}
					String ppp_err = intent
							.getStringExtra(PppoeManager.EXTRA_PPPOE_ERRCODE);

					stopConnectingDialog();

					if (mPppoeStateValue == PppoeDataEntity.PPPOE_STATE_CONNECTING) {
						mConnectTimer.cancel();
						mPppoeDataEntity.clearPppoeRunningFlag();
					}
					mPppoeStateValue = PppoeDataEntity.PPPOE_STATE_CONNECT_FAILED;

					String reason = "";
					if (ppp_err.equals("691")) {
						reason = context.getResources().getString(
								R.string.pppoe_connect_failed_auth);
						mPppoeDataEntity.setPppoePassword("");
					} else if (ppp_err.equals("650")) {
						reason = context
								.getResources()
								.getString(
										R.string.pppoe_connect_failed_server_no_response);
					}
					if (null != mConnectingDialog) {
						stopConnectingDialog();
					}
					showToast(context.getResources().getString(
							R.string.pppoe_connect_failed)
							+ "\n" + reason);
					SystemProperties.set(PppoeDataEntity.SYS_PRO_PPP_CONN_STA,
							"false");
					BootCompletedReceiverAML.isPppoeConnectingFlag = false;
					jump2EthernetDisconnect();
				}
			}
		}
	};

	private void showToast(String content) {
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}
		mToast = Toast.makeText(PppoeConnectActivity.this, content,
				Toast.LENGTH_LONG);
		mToast.show();
	}

	private void jump2EthernetConnect() {
		if (!isNativeGo) {
			return;
		}
		Intent intent = new Intent(PppoeConnectActivity.this,
				EthernetConnectedAcivity.class);
		PppoeConnectActivity.this.startActivity(intent);
		PppoeConnectActivity.this.finish();
	}

	private void jump2EthernetDisconnect() {
		Intent intent = new Intent(PppoeConnectActivity.this,
				EthernetDisconnectedActivity.class);
		PppoeConnectActivity.this.startActivity(intent);
		PppoeConnectActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mConnectingDialog != null && mConnectingDialog.isShowing()) {
				mConnectingDialog.dismiss();
				return true;
			}

			Intent intent = new Intent(PppoeConnectActivity.this,
					EthernetDisconnectedActivity.class);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

	private void regeditReceiver() {

		IntentFilter pppoeFilter = new IntentFilter(
				PppoeManager.PPPOE_STATE_CHANGED_ACTION);
		pppoeFilter.addAction(PppoeManager.NETWORK_STATE_CHANGED_ACTION);
		registerReceiver(mPppoeReceiver, pppoeFilter);

		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.v(TAG, "eth status==" + Utils.checkEthState());
				Log.v(TAG,
						"network change>>>>>"
								+ Utils.checkNetworkIsActive(PppoeConnectActivity.this));
				// //如果是开机向导执行的，拨号成功后,就直接关闭当前窗口
				if (!isNativeGo
						&& Utils.checkNetworkIsActive(PppoeConnectActivity.this)) {
					finish();
				}

				if (!Utils.checkEthState()) {
					jump2Wifi();
				}
			}
		}
	};

	private void unRegeditReceiver() {
		unregisterReceiver(mReceiver);
	}

	private void jump2Wifi() {
		Intent intent = new Intent(this, WifiConnectActivity.class);
		startActivity(intent);
		finish();
	}

}
