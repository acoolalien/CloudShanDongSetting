package com.shandong.cloudtv.settings;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shandong.cloudtv.settings.download.IDownloadCallback;
import com.shandong.cloudtv.settings.download.IDownloadService;
import com.shandong.cloudtv.settings.download.DownloadService;
import com.shandong.cloudtv.settings.util.Utils;

public class TestSpeedActivity extends Activity {
	private static final String TAG = "TestSpeed";
	private static final boolean DEBUG = true;
	private static final int MSG_DOWNLOAD_START = 1;
	private static final int MSG_DOWNLOAD_FINISH = 2;
	private static final int MSG_DOWNLOAD_PROGRESS = 3;
	private static final int MSG_DOWNLOAD_FAILED = 4;
	private static final int MSG_DOWNLOAD_STOPED = 5;
	private static final int MSG_SPEED_REFRESH = 6;
	private static final int MSG_NETWORK_VALID = 7;
	private static final int MSG_NETWORK_INVALID = 8;
	private static final int MSG_DOWNLOAD_CANCLE = 9;
	private static final int MSG_DOWNLOAD_COMPLETE = 10;
	
	private boolean mIsBinded = false;
	private boolean mIsCancel = false;
	Runnable mCallbackBindOK = null;
	public MHandler mHandler = new MHandler(this);
	private IDownloadService mService = null;
	private CheckNetworkTask mCheckNetworkTask = null;

	private long mCurSpeed = 0;
	private long mAverageSpeed = 0;
	private long mMaxCurSpeed = 0;
	private long startTime = 0L;
	private float mRorateAngle;
	String[] dialDisplayData = new String[8];
	String[] dialPerData = new String[8];
	DecimalFormat mDecimalFormat = null;
	//view
	private Button mStartBtn = null;
	private TextView mCurSpeedTV1 = null;
	private TextView mCurSpeedTV2 = null;
	private TextView mCurSpeedTV3 = null;
	private TextView mAverageSpeedTV1 = null;
	private TextView mAverageSpeedTV2 = null;
	private TextView mAverageSpeedTV3 = null;
	private TextView mBroadbandSpeedTV1 = null;
	private TextView mBroadbandSpeedTV2 = null;
	private TextView mBroadbandSpeedTV3 = null;
	private TextView[] mRealspeedTV = new TextView[10];
	private TextView[] mRealspeedDecimalTV = new TextView[10];
	private TextView[] mRealspeedUnitTV = new TextView[10];
	//private ProgressBar mRealspeedProgressBar = null;
	//private ProgressBar mRealspeedBtnProgress = null;
	private ImageView mRealspeedImg = null;
	private ImageView mRealspeedProsImg = null;
	private ViewPropertyAnimator mRealspeedProsAniView = null;
	private LinearLayout mCurveLayout = null;
	private ImageView mCurveImg = null;
	private int mRealspeedImgWidth = 0;
	private int mRealspeedImgHeight = 0;
	private boolean isGetRealspeedParam = true;
	private long[] mRealspeeds = new long[10];
	private int [] mSpeedOffsetPoints = new int[10];
	private RelativeLayout[] mRealspeedReLayouts = new RelativeLayout[10];
	private Animation mRealspeedTextAnimation = null;
	private int mCurRealspeedIndex = 0;
	
	private RelativeLayout mBtnProgressLyout = null;
	private ImageView mBtnProgressFullImg = null;
	private ViewPropertyAnimator mBtnProgressAnimatorView = null;
	private int[] mKeduProgressIds = {R.drawable.test_speed_kedu_progress_0,
			R.drawable.test_speed_kedu_progress_1,
			R.drawable.test_speed_kedu_progress_2,
			R.drawable.test_speed_kedu_progress_3,
			R.drawable.test_speed_kedu_progress_4,
			R.drawable.test_speed_kedu_progress_5,
			R.drawable.test_speed_kedu_progress_6,
			R.drawable.test_speed_kedu_progress_7,
			R.drawable.test_speed_kedu_progress_8,
			R.drawable.test_speed_kedu_progress_9};
	private ImageView[] mStarsImgViews = new ImageView[5];
	private TextView mStarsPerText = null;
	private TextView mStarsMoviesPerText = null;
	private RelativeLayout mStarsRelativeLayout = null;
	private ViewPropertyAnimator mStarsRlyoutAnimator = null;
	private Animation mStarsRlyAnimation = null;
	private TextView mStarsTitle1 = null;
	private TextView mStarsTitle2 = null;
	private TextView mStarsTitle3 = null;
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.test_speed_main);
		initView();
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
		if(mHandler != null){
			removeAll();
			mHandler = null;
		}
		try {
			if(mService != null){
				mService.unregisterCallback(mCallback);
				mService.stopDownload();
				mService = null;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unbind();
	}

	private void removeAll(){
		mHandler.removeMessages(MSG_DOWNLOAD_START);
		mHandler.removeMessages(MSG_DOWNLOAD_FINISH);
		mHandler.removeMessages(MSG_DOWNLOAD_PROGRESS);
		mHandler.removeMessages(MSG_DOWNLOAD_FAILED);
		mHandler.removeMessages(MSG_DOWNLOAD_STOPED);
		mHandler.removeMessages(MSG_DOWNLOAD_COMPLETE);
		mHandler.removeMessages(MSG_DOWNLOAD_CANCLE);
		mHandler.removeMessages(MSG_NETWORK_VALID);
		mHandler.removeMessages(MSG_NETWORK_INVALID);
		mHandler.removeMessages(MSG_SPEED_REFRESH);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	private void initView(){
		mStartBtn = (Button)findViewById(R.id.test_speed_begin_btn);
		mStartBtn.setOnClickListener(mStartTestOnClickListener);
		
		mCurSpeedTV1 = (TextView)findViewById(R.id.test_speed_curspeed_tv1);
		mCurSpeedTV2 = (TextView)findViewById(R.id.test_speed_curspeed_tv2);
		mCurSpeedTV3 = (TextView)findViewById(R.id.test_speed_curspeed_tv3);
		mAverageSpeedTV1 = (TextView)findViewById(R.id.test_speed_average_tv1);
		mAverageSpeedTV2 = (TextView)findViewById(R.id.test_speed_average_tv2);
		mAverageSpeedTV3 = (TextView)findViewById(R.id.test_speed_average_tv3);
		mBroadbandSpeedTV1 = (TextView)findViewById(R.id.test_speed_broadband_tv1);
		mBroadbandSpeedTV2 = (TextView)findViewById(R.id.test_speed_broadband_tv2);
		mBroadbandSpeedTV3 = (TextView)findViewById(R.id.test_speed_broadband_tv3);
		
		mCurveLayout = (LinearLayout)findViewById(R.id.test_speed_canvas_llyout);
		mCurveImg = (ImageView)findViewById(R.id.test_speed_canvas_img);
		initRealspeedView();
		initStarsView();
	}
	
	@SuppressLint("NewApi")
	private void initRealspeedView(){
		mRealspeedTV[0] = (TextView)findViewById(R.id.test_speed_realspeed_tv1);
		mRealspeedTV[1] = (TextView)findViewById(R.id.test_speed_realspeed_tv2);
		mRealspeedTV[2] = (TextView)findViewById(R.id.test_speed_realspeed_tv3);
		mRealspeedTV[3] = (TextView)findViewById(R.id.test_speed_realspeed_tv4);
		mRealspeedTV[4] = (TextView)findViewById(R.id.test_speed_realspeed_tv5);
		mRealspeedTV[5] = (TextView)findViewById(R.id.test_speed_realspeed_tv6);
		mRealspeedTV[6] = (TextView)findViewById(R.id.test_speed_realspeed_tv7);
		mRealspeedTV[7] = (TextView)findViewById(R.id.test_speed_realspeed_tv8);
		mRealspeedTV[8] = (TextView)findViewById(R.id.test_speed_realspeed_tv9);
		mRealspeedTV[9] = (TextView)findViewById(R.id.test_speed_realspeed_tva);
		
		mRealspeedDecimalTV[0] = (TextView)findViewById(R.id.test_speed_realspeed_tv1_decimal);
		mRealspeedDecimalTV[1] = (TextView)findViewById(R.id.test_speed_realspeed_tv2_decimal);
		mRealspeedDecimalTV[2] = (TextView)findViewById(R.id.test_speed_realspeed_tv3_decimal);
		mRealspeedDecimalTV[3] = (TextView)findViewById(R.id.test_speed_realspeed_tv4_decimal);
		mRealspeedDecimalTV[4] = (TextView)findViewById(R.id.test_speed_realspeed_tv5_decimal);
		mRealspeedDecimalTV[5] = (TextView)findViewById(R.id.test_speed_realspeed_tv6_decimal);
		mRealspeedDecimalTV[6] = (TextView)findViewById(R.id.test_speed_realspeed_tv7_decimal);
		mRealspeedDecimalTV[7] = (TextView)findViewById(R.id.test_speed_realspeed_tv8_decimal);
		mRealspeedDecimalTV[8] = (TextView)findViewById(R.id.test_speed_realspeed_tv9_decimal);
		mRealspeedDecimalTV[9] = (TextView)findViewById(R.id.test_speed_realspeed_tva_decimal);
		
		mRealspeedUnitTV[0] = (TextView)findViewById(R.id.test_speed_realspeed_tv1_unit);
		mRealspeedUnitTV[1] = (TextView)findViewById(R.id.test_speed_realspeed_tv2_unit);
		mRealspeedUnitTV[2] = (TextView)findViewById(R.id.test_speed_realspeed_tv3_unit);
		mRealspeedUnitTV[3] = (TextView)findViewById(R.id.test_speed_realspeed_tv4_unit);
		mRealspeedUnitTV[4] = (TextView)findViewById(R.id.test_speed_realspeed_tv5_unit);
		mRealspeedUnitTV[5] = (TextView)findViewById(R.id.test_speed_realspeed_tv6_unit);
		mRealspeedUnitTV[6] = (TextView)findViewById(R.id.test_speed_realspeed_tv7_unit);
		mRealspeedUnitTV[7] = (TextView)findViewById(R.id.test_speed_realspeed_tv8_unit);
		mRealspeedUnitTV[8] = (TextView)findViewById(R.id.test_speed_realspeed_tv9_unit);
		mRealspeedUnitTV[9] = (TextView)findViewById(R.id.test_speed_realspeed_tva_unit);
		
		mRealspeedImg = (ImageView)findViewById(R.id.test_speed_realspeed_underline);
		mRealspeedProsImg = (ImageView)findViewById(R.id.test_speed_realspeed_progs_line);
		mRealspeedProsImg.setPivotX(0.3f);
		mRealspeedProsAniView = mRealspeedProsImg.animate();
		mRealspeedProsAniView.scaleX(0f);
		//mRealspeedProgressBar = (ProgressBar)findViewById(R.id.test_speed_realspeed_progress);

		mBtnProgressLyout = (RelativeLayout)findViewById(R.id.test_speed_btn_progress_flyout);
		mBtnProgressFullImg = (ImageView)findViewById(R.id.test_speed_progress_btn_full_img);
		mBtnProgressFullImg.setPivotX(0.3f);
		mBtnProgressAnimatorView = mBtnProgressFullImg.animate();
		mBtnProgressAnimatorView.scaleX(0f);
		
		mRealspeedReLayouts[0] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout1);
		mRealspeedReLayouts[1] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout2);
		mRealspeedReLayouts[2] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout3);
		mRealspeedReLayouts[3] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout4);
		mRealspeedReLayouts[4] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout5);
		mRealspeedReLayouts[5] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout6);
		mRealspeedReLayouts[6] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout7);
		mRealspeedReLayouts[7] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout8);
		mRealspeedReLayouts[8] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyout9);
		mRealspeedReLayouts[9] = (RelativeLayout)findViewById(R.id.test_speed_realspeed_cur_rlyouta);
		
		mRealspeedTextAnimation = AnimationUtils.loadAnimation(TestSpeedActivity.this, R.anim.test_speed_text);
	}
	
	@SuppressLint("NewApi")
	private void initStarsView(){
		mStarsImgViews[0] = (ImageView)findViewById(R.id.test_speed_stars_img1);
		mStarsImgViews[1] = (ImageView)findViewById(R.id.test_speed_stars_img2);
		mStarsImgViews[2] = (ImageView)findViewById(R.id.test_speed_stars_img3);
		mStarsImgViews[3] = (ImageView)findViewById(R.id.test_speed_stars_img4);
		mStarsImgViews[4] = (ImageView)findViewById(R.id.test_speed_stars_img5);
		
		mStarsMoviesPerText = (TextView)findViewById(R.id.test_speed_stars_movies_per_tv);
		mStarsPerText = (TextView)findViewById(R.id.test_speed_stars_per_tv);
		
		mStarsTitle1 = (TextView)findViewById(R.id.test_speed_stars_tv1);
		mStarsTitle2 = (TextView)findViewById(R.id.test_speed_stars_tv2);
		mStarsTitle3 = (TextView)findViewById(R.id.test_speed_stars_tv3);
		
		mStarsRelativeLayout = (RelativeLayout)findViewById(R.id.test_speed_stars_rlyout);
		//mStarsRlyoutAnimator = mStarsRelativeLayout.animate();
		//mStarsRlyoutAnimator.translationX(0f);
		mStarsRlyAnimation = AnimationUtils.loadAnimation(TestSpeedActivity.this, R.anim.test_speed_from_r2l);
		
	}
	
	View.OnClickListener mStartTestOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(DEBUG)
				Log.d(TAG,"start test speed.....................");
			restoreAndredownload();
		}
	};
	
	void updateTotalSpeedView() {
		if(DEBUG){
			Log.e(TAG, "cur speed="+trans2bit(mCurSpeed));
			Log.e(TAG, "average speed="+trans2bit(mAverageSpeed));
			Log.e(TAG, "broadband speed="+trans2Band(mMaxCurSpeed));
		}
		String curSpeed = trans2bit(mCurSpeed);
		String[] curSpeedSplit = curSpeed.split("\\.");
		if(curSpeedSplit.length!=3){
			Log.e(TAG, "get cur speed error "+curSpeedSplit.length);
			return;
		}
		
		String averageSpeed = trans2bit(mAverageSpeed);
		String[] averageSpeedSplit = averageSpeed.split("\\.");
		if(averageSpeedSplit.length!=3){
			Log.e(TAG, "get average speed error");
			return;
		}
		
		mCurSpeedTV1.setText(curSpeedSplit[0]);
		mCurSpeedTV2.setText("."+curSpeedSplit[1]);
		if(curSpeedSplit[2].equalsIgnoreCase("K/S")){
			mCurSpeedTV3.setText(curSpeedSplit[2]);
		}
		
		mAverageSpeedTV1.setText(averageSpeedSplit[0]);
		mAverageSpeedTV2.setText("."+averageSpeedSplit[1]);
		if(averageSpeedSplit[2].equalsIgnoreCase("K/S")){
			mAverageSpeedTV3.setText(averageSpeedSplit[2]);
		}
	}
	
	@SuppressLint("NewApi")
	private void updateRealSpeedView(){
		if(DEBUG){
			Log.e(TAG, "get real speed "+trans2bit(mRealspeeds[mCurRealspeedIndex]));
		}
		String realSpeed = trans2bit(mRealspeeds[mCurRealspeedIndex]);
		String[] realSpeedSplit = realSpeed.split("\\.");
		if(realSpeedSplit.length!=3){
			Log.e(TAG, "get cur speed error "+realSpeedSplit.length);
			return;
		}
		
		mRealspeedTV[mCurRealspeedIndex].setText(realSpeedSplit[0]);
		mRealspeedDecimalTV[mCurRealspeedIndex].setText("."+realSpeedSplit[1]);
		if(realSpeedSplit[2].equalsIgnoreCase("K/S")){
			mRealspeedUnitTV[mCurRealspeedIndex].setText(realSpeedSplit[2]);
		}
		mRealspeedReLayouts[mCurRealspeedIndex].startAnimation(mRealspeedTextAnimation);
		//mRealspeedProgressBar.setProgress(mCurRealspeedIndex+1);
		
		mRealspeedProsImg.setVisibility(View.VISIBLE);
		setViewProgress(mRealspeedProsAniView, mCurRealspeedIndex+1, 100);
		
		mRealspeedImg.setVisibility(View.VISIBLE);
		mRealspeedImg.setBackgroundResource(mKeduProgressIds[mCurRealspeedIndex]);
		
		//btn progress
		mStartBtn.setVisibility(View.GONE);
		mBtnProgressLyout.setVisibility(View.VISIBLE);
		mBtnProgressFullImg.setVisibility(View.VISIBLE);
		setViewProgress(mBtnProgressAnimatorView,mCurRealspeedIndex, 100);
		mCurRealspeedIndex++;
	}

	@SuppressLint("NewApi")
	public void setViewProgress(ViewPropertyAnimator view,int progress,int duration) {
		float scaleX = 1.0f*progress/10;
		view.scaleX(scaleX).setDuration(duration).start();
		//if(progress == 10){
		//	view.scaleX(1).setDuration(1000).start();
		//}
	}
	
	private void updateBroadbandView(){
		String broadbandSpeed = trans2Band(mMaxCurSpeed);
		String[] broadbandSpeedSplit = broadbandSpeed.split("\\.");
		if(broadbandSpeedSplit.length!=3){
			Log.e(TAG, "get broadband speed error");
			return;
		}
		
		mBroadbandSpeedTV1.setText(broadbandSpeedSplit[0]);
		mBroadbandSpeedTV2.setText("."+broadbandSpeedSplit[1]);
		if(broadbandSpeedSplit[2].equalsIgnoreCase("K/S")){
			mBroadbandSpeedTV3.setText(broadbandSpeedSplit[2]);
		}
		mStartBtn.setText(R.string.test_speed_repeat_test);
	}
	
	private void updateStarsView(){
		String broadbandSpeed = trans2Band(mMaxCurSpeed);
		String[] broadbandSpeedSplit = broadbandSpeed.split("\\.");
		if(broadbandSpeedSplit.length!=3){
			Log.e(TAG, "get broadband speed error");
			return;
		}
		
		int broadBand = Integer.valueOf(broadbandSpeedSplit[0]);
		if(broadBand<=4){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsTitle1.setText(getString(R.string.test_speed_broadband_level_1));
			mStarsPerText.setVisibility(View.GONE);
			mStarsTitle2.setVisibility(View.GONE);
			mStarsTitle3.setText(getString(R.string.test_speed_broadband_level_1_1));
			mStarsMoviesPerText.setVisibility(View.GONE);
		}else if(broadBand>4&&broadBand<=10){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsTitle1.setText(getString(R.string.test_speed_broadband_title_1));
			mStarsPerText.setText(getString(R.string.test_speed_per_30));
			mStarsTitle2.setText(getString(R.string.test_speed_broadband_title_2));
			mStarsTitle3.setText(getString(R.string.test_speed_broadband_title_3));
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_720p));
		}else if(broadBand>10&&broadBand<=20){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsTitle1.setText(getString(R.string.test_speed_broadband_title_1));
			mStarsPerText.setText(getString(R.string.test_speed_per_50));
			mStarsTitle2.setText(getString(R.string.test_speed_broadband_title_2));
			mStarsTitle3.setText(getString(R.string.test_speed_broadband_title_3));
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_1080p));
		}else if(broadBand>20&&broadBand<=50){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsTitle1.setText(getString(R.string.test_speed_broadband_title_1));
			mStarsPerText.setText(getString(R.string.test_speed_per_70));
			mStarsTitle2.setText(getString(R.string.test_speed_broadband_title_2));
			mStarsTitle3.setText(getString(R.string.test_speed_broadband_title_3));
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_1080p));
		}else if(broadBand>50){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsTitle1.setText(getString(R.string.test_speed_broadband_title_1));
			mStarsPerText.setText(getString(R.string.test_speed_per_90));
			mStarsTitle2.setText(getString(R.string.test_speed_broadband_title_2));
			mStarsTitle3.setText(getString(R.string.test_speed_broadband_title_3));
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_4k));
		}
		mStarsRelativeLayout.setVisibility(View.VISIBLE);

		//mStarsRelativeLayout.setAnimation(mStarsRlyAnimation);
		mStarsRelativeLayout.startAnimation(mStarsRlyAnimation);
	}
	
	String trans2bit(long speed) {
		if(mDecimalFormat == null){
			mDecimalFormat = new DecimalFormat("#.00");
		}
		if (speed >= 1024) {
			float size = (float) speed / 1024;
			return mDecimalFormat.format(size) + ".MB/S";
		} else {
			return mDecimalFormat.format(speed) + ".K/S";
		}
	}

	private String trans2Band(long speed){
		long band = speed * 8;
		
		if (band >= 1024) {
			long size = (long) band / 1024;
			return size + ".00.Mb/S";
		} else {
			return band + ".00.K/S";
		}
	}
	
	public boolean isBinded() {
		return (mIsBinded && null != mService);
	}
	
	public void unbind() {
		if (!isBinded()) {
			return;
		}

		TestSpeedActivity.this.unbindService(mConnection);
	}
	
	/**
	 * bind service
	 */
	public void bindDownloadService(Runnable bindOK) {
		mCallbackBindOK = bindOK;
		mIsBinded = TestSpeedActivity.this.bindService(new Intent(TestSpeedActivity.this, DownloadService.class), 
				mConnection,Context.BIND_AUTO_CREATE);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {

			mService = IDownloadService.Stub.asInterface(service);
			try {
				mService.registerCallback(mCallback);
				if (null != mCallbackBindOK) {
					mCallbackBindOK.run();
				}
			} catch (Exception e) {
				Log.e(TAG, "Exception", e);
			}
		}

		public void onServiceDisconnected(ComponentName name) {
			try {
				if (null != mService) {
					mService.unregisterCallback(mCallback);
					mService = null;
				}

			} catch (Exception e) {
				Log.e(TAG, "Exception", e);
			}
		}
	};
	
	private IDownloadCallback.Stub mCallback = new IDownloadCallback.Stub() {

		@Override
		public void networkConnected(boolean result) throws RemoteException {
			if (!result) {
				mHandler.sendEmptyMessage(MSG_DOWNLOAD_FAILED);
			}
		}

		@Override
		public void downloadStopedFinish() throws RemoteException {
			mHandler.sendEmptyMessage(MSG_DOWNLOAD_FAILED);
		}

		@Override
		public void downloadStart() throws RemoteException {
			mHandler.sendEmptyMessage(MSG_DOWNLOAD_START);
			startTime = System.currentTimeMillis();
		}

		@Override
		public void downloadFinish() throws RemoteException {
			mHandler.removeMessages(MSG_DOWNLOAD_FINISH);
			mHandler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
		}

		@Override
		public void downloadFailed(String cause) throws RemoteException {
			Message message = new Message();
			message.what = MSG_DOWNLOAD_FAILED;
			message.obj = cause;
			mHandler.sendMessage(message);
		}

		@Override
		public void downloadProgress(long curspeed, long averspeed) throws RemoteException {
			long time = System.currentTimeMillis() - startTime;
//			current = curspeed * 8;
			mCurSpeed = curspeed;
			mMaxCurSpeed = (mMaxCurSpeed > mCurSpeed)? mMaxCurSpeed: mCurSpeed;
//			average = averspeed * 8;
			mAverageSpeed = averspeed;
			
			//mHandler.sendEmptyMessageDelayed(MSG_DOWNLOAD_COMPLETE, 10000);
			
			if(mCurRealspeedIndex>=10){
				mHandler.sendEmptyMessage(MSG_DOWNLOAD_CANCLE);
				return;
			}
			mRealspeeds[mCurRealspeedIndex] = mCurSpeed;

			Message message = new Message();
			message.what = MSG_DOWNLOAD_PROGRESS;
			mHandler.sendMessage(message);
		}
	};
	
	private void restoreAndredownload() {
		startTime = 0L;
		mCurSpeed = 0;
		mAverageSpeed = 0;
		mMaxCurSpeed = 0;
		mIsCancel = false;
		mCurRealspeedIndex = 0;
		startCheckNetworkTask(true);
	}

	private void startCheckNetworkTask(boolean isload){
		if((mCheckNetworkTask != null) && (mCheckNetworkTask.getStatus() != AsyncTask.Status.FINISHED)){
			mCheckNetworkTask.cancel(true);
		}
		mCheckNetworkTask = new CheckNetworkTask(isload);
		mIsCancel = false;
		try {
			if(mService != null){
				mService.stopDownload();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		mCheckNetworkTask.execute();
	}
	
	public class CheckNetworkTask extends AsyncTask<Void, Void, Integer> {
		private boolean load = false;
		private static final int ERROR_LOCAL = 0, ERROR_INTERNET = 1, NETWORK_OK = 2;

		private CheckNetworkTask(boolean needReDownload) {
			this.load = needReDownload;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			String gw = Utils.getGateway(TestSpeedActivity.this);
			Log.d(TAG, "---heyf---gateway:" + gw);
			if (gw == null) {
				return ERROR_LOCAL;
			} else {
				HttpURLConnection conn = null;
				try {
					String str = "ping -c 1 -w 5 " + gw;
					Process p = Runtime.getRuntime().exec(str);
					int status = p.waitFor();
					Log.d(TAG, "---heyf---status:" + status);
					if (status == 0) {
						URL url = new URL("http://www.baidu.com");
						conn = (HttpURLConnection) url.openConnection();
						if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
							return NETWORK_OK;
						} else {
							return ERROR_INTERNET;
						}
					} else {
						return ERROR_LOCAL;
					}
				} catch (Exception e) {
					Log.e(TAG, "error:" + e.getMessage());
				} finally {
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
				}
			}
			return ERROR_INTERNET;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == ERROR_LOCAL) {
				Log.d(TAG, "networker status : ERROR LOCAL");
			} else if (result == ERROR_INTERNET) {
				Log.d(TAG, "networker status : ERROR INTERNET");
			} else if (result == NETWORK_OK) {
				Log.d(TAG, "networker status : NETWORK OK");
				Log.d(TAG, "wherther to start down load:" + load);
				if (load) {
					if (isBinded()) {
						try {
							if(!mIsCancel){
								mService.startDownload(true);
							}
						} catch (RemoteException e) {
							Log.e(TAG, "" + e.getMessage());
						}
					} else {
						bindDownloadService(new Runnable() {
							@Override
							public void run() {
								try {
									if (isBinded()){
										if(!mIsCancel){
											mService.startDownload(true);
										}
									}										
								} catch (RemoteException e) {
									Log.e(TAG, "" + e.getMessage());
								}
							}
						});
					}
				} else {
				}
			}

		}

	}
	
	public static class MHandler extends Handler {
		// private Handler mHandler = new Handler() {

		private final WeakReference<TestSpeedActivity> weakaci;

		MHandler(TestSpeedActivity activity) {
			weakaci = new WeakReference<TestSpeedActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (null == msg) {
				return;
			}

			TestSpeedActivity aci = weakaci.get();
			if (aci == null)
				return;

			aci.handleMessage(msg);
		};
	};

	protected void handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_DOWNLOAD_START:
			break;
		case MSG_DOWNLOAD_FINISH:
			updateBroadbandView();
			updateStarsView();
			//mRealspeedBtnProgress.setVisibility(View.GONE);
			mBtnProgressLyout.setVisibility(View.GONE);
			mStartBtn.setVisibility(View.VISIBLE);
			showCurverImage();
			break;
		case MSG_DOWNLOAD_PROGRESS:
			updateTotalSpeedView();
			updateRealSpeedView();
			break;
		case MSG_DOWNLOAD_FAILED:
			break;
		case MSG_DOWNLOAD_STOPED:
			break;
		case MSG_DOWNLOAD_COMPLETE:
			try {
				if(mService != null){
					mService.stopDownload();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.removeMessages(MSG_DOWNLOAD_COMPLETE);
			mHandler.removeMessages(MSG_DOWNLOAD_FINISH);
			mHandler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
			mHandler.removeMessages(MSG_DOWNLOAD_PROGRESS);
			updateBroadbandView();
			updateStarsView();
			//mRealspeedBtnProgress.setVisibility(View.GONE);
			mBtnProgressLyout.setVisibility(View.GONE);
			mStartBtn.setVisibility(View.VISIBLE);
			showCurverImage();
			break;
		case MSG_DOWNLOAD_CANCLE:
			mIsCancel = true;
			if((mCheckNetworkTask != null) && (mCheckNetworkTask.getStatus() != AsyncTask.Status.FINISHED)){
				mCheckNetworkTask.cancel(true);
			}
			try {
				if(mService != null){
					mService.stopDownload();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "hezc --- download service stop error");
				e.printStackTrace();
			}
			mHandler.removeMessages(MSG_DOWNLOAD_COMPLETE);
			updateBroadbandView();
			updateStarsView();
			//mRealspeedBtnProgress.setVisibility(View.GONE);
			mBtnProgressLyout.setVisibility(View.GONE);
			mStartBtn.setVisibility(View.VISIBLE);
			showCurverImage();
			break;
		case MSG_NETWORK_VALID:
			break;
		case MSG_NETWORK_INVALID:
			mCurSpeed = 0;
			mAverageSpeed = 0;
			mMaxCurSpeed = 0;
			break;
		case MSG_SPEED_REFRESH:

			break;
		}
	}
	
	public float getDisPlay() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.density;
	}
	
	public  void getOffsetPoints(){
		int[][] mKSPoints = new int[10][4];
		int mKSPointsIndex = 0;
		int[][] mMSPoints = new int[10][4];
		int mMSPointsIndex = 0;
		int mKSMax = 0;
		int mKSMin = 0;
		int mMSMax = 0;
		int mMSMin = 0;
		
		for(int i=0;i<10;i++){
			String tmpStr = trans2bit(mRealspeeds[i]);
			String[] tmpStrAarray = tmpStr.split("\\.");
			if(tmpStrAarray.length != 3){
				Log.e(TAG, "[getOffsetPoints] split speed array error!");
				return;
			}
			int curIndexSpeed = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(0, 1));
			int curIndexSpeed1 = 0;
			if(Long.toString(mRealspeeds[i]).length()>2){
				curIndexSpeed1 = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(1, 2));
			}
			int flag = 0;
			if(tmpStrAarray[2].equalsIgnoreCase("K/S")){
				mKSPoints[mKSPointsIndex][0] = i;
				mKSPoints[mKSPointsIndex][1] = curIndexSpeed;
				mKSPoints[mKSPointsIndex][2] = curIndexSpeed1;
				mKSPoints[mKSPointsIndex][3] = flag;
				mKSMax = mKSMax>=curIndexSpeed?mKSMax:curIndexSpeed;
				mKSMin = mKSMin>=curIndexSpeed?curIndexSpeed:mKSMin;
				mKSPointsIndex++;
			}else if(tmpStrAarray[2].equalsIgnoreCase("MB/S")){
				mMSPoints[mMSPointsIndex][0] = i;
				mMSPoints[mMSPointsIndex][1] = curIndexSpeed;
				mMSPoints[mMSPointsIndex][2] = curIndexSpeed1;
				mMSPoints[mMSPointsIndex][3] = flag;
				mMSMax = mMSMax>=curIndexSpeed?mMSMax:curIndexSpeed;
				mMSMin = mMSMin>=curIndexSpeed?curIndexSpeed:mMSMin;
				mMSPointsIndex++;
			}
		}
		if(mKSPointsIndex!=0){
			if(DEBUG){
				Log.e(TAG, "enter k/s speed");
			}
			if(mKSPoints.length == 10){
				for (int j = 0; j < mKSPoints.length ; j++) {			
					for (int i = 0; i < mKSPoints.length - 1; i++) {				
						int[] tmp;
						if ((mKSPoints[i][1]>(mKSPoints[i + 1][1]))) {					
							tmp = mKSPoints[i];	
							mKSPoints[i] = mKSPoints[i + 1];					
							mKSPoints[i + 1] = tmp;									
						  }	else if((mKSPoints[i][1] == mKSPoints[i+1][1]) &&(mKSPoints[i][2]>mKSPoints[i+1][2])){
							tmp = mKSPoints[i];	
							mKSPoints[i] = mKSPoints[i + 1];					
							mKSPoints[i + 1] = tmp;		
						  }
					}		
				}

			}else{
				if((mKSPoints.length+mMSPoints.length) != 10){
					Log.e(TAG, "loss array member");
					return;
				}
				for(int i=0;i<mMSPointsIndex;i++){
					mMSPoints[i][1] = mMSPoints[i][1]+6;
					mKSPoints[mKSPointsIndex+i+1] = mMSPoints[i];
				}
				for (int j = 0; j < mKSPoints.length ; j++) {			
					for (int i = 0; i < mKSPoints.length - 1; i++) {				
						int[] tmp;
						if ((mKSPoints[i][1]>(mKSPoints[i + 1][1]))) {					
							tmp = mKSPoints[i];	
							mKSPoints[i] = mKSPoints[i + 1];					
							mKSPoints[i + 1] = tmp;									
						  }else if((mKSPoints[i][1] == mKSPoints[i+1][1]) && mKSPoints[i][2]>mKSPoints[i+1][2]){
							tmp = mKSPoints[i];	
							mKSPoints[i] = mKSPoints[i + 1];					
							mKSPoints[i + 1] = tmp;	
						  }
					}		
				}
			}
			
			for(int i=0;i<10;i++){
				mKSPoints[i][1] = mKSPoints[i][1]+i;
				mSpeedOffsetPoints[mKSPoints[i][0]] = mKSPoints[i][1];
			}
		}else{
			if(DEBUG){
				Log.e(TAG, "enter m/s speed");
			}
			for (int j = 0; j < mMSPoints.length ; j++) {			
				for (int i = 0; i < mMSPoints.length - 1; i++) {				
					int[] tmp;
					if ((mMSPoints[i][1]>(mMSPoints[i + 1][1]))) {					
						tmp = mMSPoints[i];	
						mMSPoints[i] = mMSPoints[i + 1];					
						mMSPoints[i + 1] = tmp;		
					 }else if((mMSPoints[i][1] == mMSPoints[i+1][1])&&mMSPoints[i][2]>mMSPoints[i+1][2]){
						tmp = mMSPoints[i];	
						mMSPoints[i] = mMSPoints[i + 1];					
						mMSPoints[i + 1] = tmp;		
					 }
				}		
			}
			for(int i=0;i<10;i++){
				if(DEBUG){
					Log.e(TAG, "paixu 111: index "+i+" :"+mMSPoints[i][1]);
				}
				mMSPoints[i][1] = mMSPoints[i][1]+i;
				mSpeedOffsetPoints[mMSPoints[i][0]] = mMSPoints[i][1];
			}
		}
	}
	
	public void showCurverImage() {
		if(mRealspeeds.length <10){
			Log.e(TAG, "the speed count is loss");
			return;
		}
		int[] mPoints = new int[10];
		
		int height = mCurveLayout.getHeight() / 2;
		int width = mCurveLayout.getWidth();
		if(DEBUG){
			Log.e(TAG, "get layout width = "+width+" height="+height);
			for(int i=0;i<10;i++){
				Log.e(TAG, "real speed "+mRealspeeds[i]);				
			}
		}
		for(int i=0;i<10;i++){
			mPoints[i] = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(0,1)) ;
			if(DEBUG){
				//Log.e(TAG, "point = "+mPoints[i]);
			}
		}
		getOffsetPoints();
		if(DEBUG){
			for(int i=0;i<10;i++){
				Log.e(TAG, "point = "+mSpeedOffsetPoints[i]);
			}
		}
		Drawable curveBmp = drawCurve(TestSpeedActivity.this,mSpeedOffsetPoints,width, height, 1);
		
		if (curveBmp != null) {
			if(DEBUG){
				Log.e(TAG, "i'm here....................");
			}
			mCurveImg.setImageDrawable(curveBmp);
		}
	}
	
	public Drawable drawCurve(Context ctx, int[] array,int width,int height, int frist) {
		int pointRadius = 5;
		int nMax = array[0];
		int nMin = array[0];
		int temp = 0;
		int useHeight = height - 15; // make some padding
		int size = array.length > 10 ? 10 : array.length;
		
		if (width <= 0 || height < (10 + 2 * pointRadius)) {
			Log.e(TAG, "width and height is bad");
			return null;
		}
		
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
				R.drawable.test_speed_point).copy(Bitmap.Config.ARGB_8888, true);
		
		for (int i = 1; i < size; i++) {
			temp = array[i];
			if (nMax < temp) {
				nMax = temp;
			} else if (nMin > temp) {
				nMin = temp;
			}
		}

		float xLen = ((float) width) / 10;
		float xInit = 20;

		ArrayList<Point> xy = new ArrayList<Point>(size);

		if (nMax == nMin) {
			if(DEBUG){
				Log.e(TAG, "I'm here.................1");
			}
			for (int i = 0; i < size; i++) {
				xy.add(new Point((int) (xInit + i * xLen), height / 2));
			}
		} else {
			//int yLen = 5;
			int yLen = (useHeight - 2 * pointRadius) / (nMax - nMin + 2);
			if(DEBUG){
				Log.e(TAG, "get ylen="+yLen);
			}

			for (int i = 0; i < size; i++) {
				temp = array[i];
				xy.add(new Point((int) (xInit + i * xLen)+i, (int)((nMax - temp)) * yLen));
			}
		}
		if(DEBUG){
			for(int i=0;i<size;i++){
				Log.e(TAG, "point "+i+" x="+xy.get(i).x+" y="+xy.get(i).y);
			}
	
		}
		
		Bitmap bmp = null;
		try {
			bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

		Canvas canvas = new Canvas(bmp);

		Paint paint = new Paint();
		int oldColor = paint.getColor();
		Point pt;
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(ctx.getResources().getColor(R.color.settings_0c9bf9));
		Point end;
		paint.setStrokeWidth(1);
		
		int xOffset = getResources().getDimensionPixelSize(R.dimen.test_speed_point_x_offset);
		int yOffset = getResources().getDimensionPixelSize(R.dimen.test_speed_point_y_offset);
		for (int i = 0; i < size - 1; i++) {
			pt = xy.get(i);
			end = xy.get(i + 1);
			
			canvas.drawLine(pt.x+xOffset, pt.y+yOffset, end.x+xOffset, end.y+yOffset, paint);
			paint.setColor(ctx.getResources().getColor(R.color.settings_0c9bf9));
		}

		paint.setStrokeWidth(0);
		for (int i = 0; i < size; i++) {
			pt = xy.get(i);
			canvas.drawBitmap(bitmap, pt.x, pt.y, paint);
		}

		paint.setColor(oldColor);
		return new BitmapDrawable(bmp);
	}
}
