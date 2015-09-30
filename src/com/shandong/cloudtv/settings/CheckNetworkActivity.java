package com.shandong.cloudtv.settings;

import com.shandong.cloudtv.settings.util.Utils;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckNetworkActivity extends Activity{
	private static final int CHECK_NETEORK_ROUTER_TO_INTERNET_FAILED = 1000;
	private static final int CHECK_NETWORK_LOCAL_TO_ROUTER = 1001;
	private static final int CHECK_NETWORK_ROUTER_TO_INTERNET = 1002;
	private static final int CHECK_NETWORK_ROUTER_TO_INTERNET_CLEAR = 1003;
	private static final int CHECK_NETWORK_ROUTER_STATE = 1004;
	private static final int CHECK_NETWORK_INTERNET_STATE = 1005;
	private ImageView mRouterStateImg = null;
	private ImageView mInternetStateImg = null;
	private ImageView mLocal2RouterUnSelectedImg[] = new ImageView[10];
	private ImageView mLocal2RouterSelectedImg[] = new ImageView[10];
	private ImageView mRouter2InternetUnSelectedImg[] = new ImageView[10];
	private ImageView mRouter2InternetSelectedImg[] = new ImageView[10];
	private boolean mCheckNetworkExitFlag = false;
	private boolean mNetworkOK = false;
	private Handler mHandler = null;
	private TextView mCheckEndState = null;
	private ImageView mNetworkFailedState = null;
	private int mCount = 0;

	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.check_network_main);
		initView();
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				int index = msg.arg1;
				switch (msg.what) {
				case CHECK_NETWORK_INTERNET_STATE:
					mInternetStateImg.setBackgroundResource(R.drawable.check_network_internet_selected);
					mCheckEndState.setVisibility(View.VISIBLE);
					mCheckEndState.setText(getResources().getText(R.string.check_network_connect_success));
					break;
				case CHECK_NETWORK_ROUTER_STATE:
					mRouterStateImg.setBackgroundResource(R.drawable.check_network_router_selected);
					break;
				case CHECK_NETWORK_LOCAL_TO_ROUTER:
					mLocal2RouterUnSelectedImg[index].setVisibility(View.GONE);
					mLocal2RouterSelectedImg[index].setVisibility(View.VISIBLE);
					break;
				case CHECK_NETWORK_ROUTER_TO_INTERNET:
					mRouter2InternetUnSelectedImg[index].setVisibility(View.GONE);
					mRouter2InternetSelectedImg[index].setVisibility(View.VISIBLE);
					break;
				case CHECK_NETWORK_ROUTER_TO_INTERNET_CLEAR:
					for (int i = 0; i < 10; i++) {
						mRouter2InternetUnSelectedImg[i].setVisibility(View.VISIBLE);
						mRouter2InternetSelectedImg[i].setVisibility(View.GONE);
					}
					break;
				case CHECK_NETEORK_ROUTER_TO_INTERNET_FAILED:
					mNetworkFailedState.setVisibility(View.VISIBLE);
					mCheckEndState.setVisibility(View.VISIBLE);
					mCheckEndState.setText(getResources().getText(R.string.check_network_connect_failed));
					break;
				default:
					break;
				}
			}

		};
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					for (int i = 0; i < 10; i++) {
						Message msg = new Message();
						msg.what = CHECK_NETWORK_LOCAL_TO_ROUTER;
						msg.arg1 = i;
						mHandler.sendMessage(msg);
						Thread.sleep(200);
					}
					int type = Utils.getActiveNetworkType(CheckNetworkActivity.this);
					if (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_PPPOE
							|| type == ConnectivityManager.TYPE_ETHERNET) {
						mHandler.sendEmptyMessage(CHECK_NETWORK_ROUTER_STATE);
					}
					
					for (int i = 0; i < 10; i++) {
						Message msg = new Message();
						msg.what = CHECK_NETWORK_ROUTER_TO_INTERNET;
						msg.arg1 = i;
						mHandler.sendMessage(msg);
						if (i == 9 && !mCheckNetworkExitFlag) {
							i = -1;
							mHandler.sendEmptyMessage(CHECK_NETWORK_ROUTER_TO_INTERNET_CLEAR);
							mCount++;
							if(mCount == 5){
								mNetworkOK = false;
								break;
							}
						}
						Thread.sleep(200);
					}
					if (mNetworkOK) {
						mHandler.sendEmptyMessage(CHECK_NETWORK_INTERNET_STATE);
					}else{
						mHandler.sendEmptyMessage(CHECK_NETEORK_ROUTER_TO_INTERNET_FAILED);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(5000);
					if (Utils.pingHost("www.baidu.com")) {
						mCheckNetworkExitFlag = true;
						mNetworkOK = true;
					} else {
						mNetworkOK = false;
					}
				} catch (Exception e) {
					// TODO: handle exception
					mNetworkOK = false;
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void initView() {
		mRouterStateImg = (ImageView) findViewById(R.id.check_network_router);
		mInternetStateImg = (ImageView) findViewById(R.id.check_network_internet);
		mCheckEndState = (TextView)findViewById(R.id.check_network_end_state);
		mNetworkFailedState = (ImageView)findViewById(R.id.check_network_not_connect_img);

		mLocal2RouterUnSelectedImg[0] = (ImageView) findViewById(R.id.check_network_local_router_point00);
		mLocal2RouterUnSelectedImg[1] = (ImageView) findViewById(R.id.check_network_local_router_point10);
		mLocal2RouterUnSelectedImg[2] = (ImageView) findViewById(R.id.check_network_local_router_point20);
		mLocal2RouterUnSelectedImg[3] = (ImageView) findViewById(R.id.check_network_local_router_point30);
		mLocal2RouterUnSelectedImg[4] = (ImageView) findViewById(R.id.check_network_local_router_point40);
		mLocal2RouterUnSelectedImg[5] = (ImageView) findViewById(R.id.check_network_local_router_point50);
		mLocal2RouterUnSelectedImg[6] = (ImageView) findViewById(R.id.check_network_local_router_point60);
		mLocal2RouterUnSelectedImg[7] = (ImageView) findViewById(R.id.check_network_local_router_point70);
		mLocal2RouterUnSelectedImg[8] = (ImageView) findViewById(R.id.check_network_local_router_point80);
		mLocal2RouterUnSelectedImg[9] = (ImageView) findViewById(R.id.check_network_local_router_point90);

		mLocal2RouterSelectedImg[0] = (ImageView) findViewById(R.id.check_network_local_router_point01);
		mLocal2RouterSelectedImg[1] = (ImageView) findViewById(R.id.check_network_local_router_point11);
		mLocal2RouterSelectedImg[2] = (ImageView) findViewById(R.id.check_network_local_router_point21);
		mLocal2RouterSelectedImg[3] = (ImageView) findViewById(R.id.check_network_local_router_point31);
		mLocal2RouterSelectedImg[4] = (ImageView) findViewById(R.id.check_network_local_router_point41);
		mLocal2RouterSelectedImg[5] = (ImageView) findViewById(R.id.check_network_local_router_point51);
		mLocal2RouterSelectedImg[6] = (ImageView) findViewById(R.id.check_network_local_router_point61);
		mLocal2RouterSelectedImg[7] = (ImageView) findViewById(R.id.check_network_local_router_point71);
		mLocal2RouterSelectedImg[8] = (ImageView) findViewById(R.id.check_network_local_router_point81);
		mLocal2RouterSelectedImg[9] = (ImageView) findViewById(R.id.check_network_local_router_point91);

		mRouter2InternetUnSelectedImg[0] = (ImageView) findViewById(R.id.check_network_router_internet_point00);
		mRouter2InternetUnSelectedImg[1] = (ImageView) findViewById(R.id.check_network_router_internet_point10);
		mRouter2InternetUnSelectedImg[2] = (ImageView) findViewById(R.id.check_network_router_internet_point20);
		mRouter2InternetUnSelectedImg[3] = (ImageView) findViewById(R.id.check_network_router_internet_point30);
		mRouter2InternetUnSelectedImg[4] = (ImageView) findViewById(R.id.check_network_router_internet_point40);
		mRouter2InternetUnSelectedImg[5] = (ImageView) findViewById(R.id.check_network_router_internet_point50);
		mRouter2InternetUnSelectedImg[6] = (ImageView) findViewById(R.id.check_network_router_internet_point60);
		mRouter2InternetUnSelectedImg[7] = (ImageView) findViewById(R.id.check_network_router_internet_point70);
		mRouter2InternetUnSelectedImg[8] = (ImageView) findViewById(R.id.check_network_router_internet_point80);
		mRouter2InternetUnSelectedImg[9] = (ImageView) findViewById(R.id.check_network_router_internet_point90);

		mRouter2InternetSelectedImg[0] = (ImageView) findViewById(R.id.check_network_router_internet_point01);
		mRouter2InternetSelectedImg[1] = (ImageView) findViewById(R.id.check_network_router_internet_point11);
		mRouter2InternetSelectedImg[2] = (ImageView) findViewById(R.id.check_network_router_internet_point21);
		mRouter2InternetSelectedImg[3] = (ImageView) findViewById(R.id.check_network_router_internet_point31);
		mRouter2InternetSelectedImg[4] = (ImageView) findViewById(R.id.check_network_router_internet_point41);
		mRouter2InternetSelectedImg[5] = (ImageView) findViewById(R.id.check_network_router_internet_point51);
		mRouter2InternetSelectedImg[6] = (ImageView) findViewById(R.id.check_network_router_internet_point61);
		mRouter2InternetSelectedImg[7] = (ImageView) findViewById(R.id.check_network_router_internet_point71);
		mRouter2InternetSelectedImg[8] = (ImageView) findViewById(R.id.check_network_router_internet_point81);
		mRouter2InternetSelectedImg[9] = (ImageView) findViewById(R.id.check_network_router_internet_point91);
	}
}
