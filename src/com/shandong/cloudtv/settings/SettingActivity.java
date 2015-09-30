package com.shandong.cloudtv.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shandong.cloudtv.settings.util.Utils;

public class SettingActivity extends Activity {
	private static final String TAG = "SettingActivity";

	// private RelativeLayout mMainLayout1;
	private RelativeLayout mMainLayout2;
	private RelativeLayout mMainLayout3;
	private RelativeLayout mMainLayout4;
	private ImageView imageView;
	final Context mContext = this;
	private String extraKey = "connection_status";
	private int defaultValue = 110000;
	private final int LAN_SUCCESS = 100005;
	private final int LAN_ERROR = 100006;
	private final int WIFI_SUCCESS = 100003;
	private final int WIFI_ERROR = 100004;
	private final int PPPOE_SUCCESS = 100001;
	private final int PPPOE_ERROR = 100002;
	private final int NETWORK_ERROR = 110000;
	private final int NETWOEK_CHECK = 110001;

	private int curPingStatus = 0;
	// private ConnectivityManager connectivity = null;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case NETWOEK_CHECK:
				setNetState(curPingStatus == 0 ? true : false);
				break;
			}
		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1024 && data != null) {
			int result = data.getIntExtra("pass", 0);
			if (result == 1) {
				Log.e("huxing", "-------------TTTT----12121");
				finish();
			}
		}
	};

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		this.activeActivity();
		setIN(false);
		setAnimation();
		// connectivity = (ConnectivityManager)
		// mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		Log.i(TAG, "-----on creat----");
	}

	private void setIN(boolean flag) {
		// mMainLayout1.setFocusable(flag);
		mMainLayout2.setFocusable(flag);
		mMainLayout3.setFocusable(flag);
		mMainLayout4.setFocusable(flag);
		// mMainLayout1.setFocusableInTouchMode(flag);
		mMainLayout2.setFocusableInTouchMode(flag);
		mMainLayout3.setFocusableInTouchMode(flag);
		mMainLayout4.setFocusableInTouchMode(flag);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setNetState(Utils.pingHost("www.baidu.com"));
	}

	/*
	 * 0 网络通 -999 网络不通 512 网络超时
	 */
	private void setNetState(Boolean isRealConnect) {

		// if (Utils.checkEthState()) {
		// if
		// (connectivity.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnected()
		// ||
		// connectivity.getNetworkInfo(ConnectivityManager.TYPE_PPPOE).isConnected())
		// {
		// if (isRealConnect) {
		// imageView.setImageResource(R.drawable.lan_success);
		// } else {
		// imageView.setImageResource(R.drawable.lan_error);
		// }
		// } else {
		// imageView.setImageResource(R.drawable.network_error);
		// }
		// } else {
		// if
		// (connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected())
		// {
		// if (isRealConnect) {
		// imageView.setImageResource(R.drawable.wifi_success);
		// } else {
		// imageView.setImageResource(R.drawable.wifi_error);
		// }
		// } else
		// imageView.setImageResource(R.drawable.network_error);
		// }
	}

	protected void onDestroy() {

		super.onDestroy();
	}

	private void setAnimation() {
		Animation animation = AnimationUtils.loadAnimation(mContext,
				R.anim.setting_anim);
		Animation animation2 = AnimationUtils.loadAnimation(mContext,
				R.anim.setting_anim2);
		Animation animation3 = AnimationUtils.loadAnimation(mContext,
				R.anim.setting_anim3);
		Animation animation4 = AnimationUtils.loadAnimation(mContext,
				R.anim.setting_anim4);
		LayoutAnimationController lac = new LayoutAnimationController(animation);
		lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
		lac.setDelay(0.5f);

		LayoutAnimationController lac2 = new LayoutAnimationController(
				animation2);
		lac2.setOrder(LayoutAnimationController.ORDER_NORMAL);
		lac2.setDelay(0.5f);

		LayoutAnimationController lac3 = new LayoutAnimationController(
				animation3);
		lac3.setOrder(LayoutAnimationController.ORDER_NORMAL);
		lac3.setDelay(0.5f);

		LayoutAnimationController lac4 = new LayoutAnimationController(
				animation4);
		lac4.setOrder(LayoutAnimationController.ORDER_NORMAL);
		lac4.setDelay(0.5f);

		// mMainLayout1.setLayoutAnimation(lac);
		mMainLayout2.setLayoutAnimation(lac);
		mMainLayout3.setLayoutAnimation(lac2);
		mMainLayout4.setLayoutAnimation(lac3);
		animation2.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation paramAnimation) {
				// TODO Auto-generated method stub
			}

			@SuppressLint("NewApi")
			@Override
			public void onAnimationEnd(Animation paramAnimation) {
				// TODO Auto-generated method stub
				// mMainLayout1.requestFocus();
				setIN(true);
				// mMainLayout1.setBackground(mContext.getResources().getDrawable(
				// R.drawable.cycle_style));
				mMainLayout2.setBackground(mContext.getResources().getDrawable(
						R.drawable.cycle_style));
				mMainLayout3.setBackground(mContext.getResources().getDrawable(
						R.drawable.cycle_style));
				mMainLayout4.setBackground(mContext.getResources().getDrawable(
						R.drawable.cycle_style));
			}

			@Override
			public void onAnimationRepeat(Animation paramAnimation) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void activeActivity() {
		imageView = (ImageView) findViewById(R.id.network_image);
		// mMainLayout1 = (RelativeLayout) findViewById(R.id.layout1);
		mMainLayout2 = (RelativeLayout) findViewById(R.id.layout2);
		mMainLayout3 = (RelativeLayout) findViewById(R.id.layout3);
		mMainLayout4 = (RelativeLayout) findViewById(R.id.layout4);
		// 关于本机
		mMainLayout4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				startActivity(new Intent().setClass(mContext,
						DeviceinfoActivity.class));
			}
		});

		mMainLayout3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent().setClass(mContext,
						CommonActivity.class));
			}
		});
		mMainLayout2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent().setClass(mContext,
				// NetworkSettingsActivity.class);
				// intent.putExtra("isNativeGo", true);
				Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				startActivity(intent);
			}
		});
		// mMainLayout1.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// startActivity(new Intent().setClass(mContext,
		// ProfilesSettingsActivity.class));
		// /*Intent service = new Intent(mContext,ModeSettingService.class);
		// mContext.startService(service);*/
		// }
		// });
	}
}
