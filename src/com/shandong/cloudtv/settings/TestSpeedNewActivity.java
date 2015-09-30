package com.shandong.cloudtv.settings;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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
import android.graphics.RadialGradient;
import android.graphics.Shader;
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
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shandong.cloudtv.settings.download.IDownloadCallback;
import com.shandong.cloudtv.settings.download.IDownloadService;
import com.shandong.cloudtv.settings.download.DownloadService;
import com.shandong.cloudtv.settings.download.DownloadTask;
import com.shandong.cloudtv.settings.util.Utils;

public class TestSpeedNewActivity extends Activity {
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
	private static final int MSG_UPDATE_ISP = 11;
	
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
	private RelativeLayout mCurSpeedRlyout = null;
	private TextView mCurSpeedTV1 = null;
	private TextView mCurSpeedTV2 = null;
	private TextView mCurSpeedTV3 = null;
	private TextView mCurSpeedTestingTV = null;
	
	private RelativeLayout mAverageSpeedRlyout = null;
	private TextView mAverageSpeedTV1 = null;
	private TextView mAverageSpeedTV2 = null;
	private TextView mAverageSpeedTV3 = null;
	private TextView mAverageSpeedTestingTV = null;
	
	private RelativeLayout mBroadbandSpeedRlyout = null;
	private TextView mBroadbandSpeedTV1 = null;
	private TextView mBroadbandSpeedTV2 = null;
	private TextView mBroadbandSpeedTV3 = null;
	private TextView mBroadbandSpeedTestingTV = null;
	
	private TextView[] mRealspeedTV = new TextView[10];
	private TextView[] mRealspeedDecimalTV = new TextView[10];
	private TextView[] mRealspeedUnitTV = new TextView[10];
	private RelativeLayout[] mRealspeedPointRlyout = new RelativeLayout[10];
	private LinearLayout mRealspeedPointLlyout = null;

	private LinearLayout mCurveLayout = null;
	private ImageView mCurveImg = null;
	
	private long[] mRealspeeds = new long[10];
	private int [] mSpeedOffsetPoints = new int[10];
	private RelativeLayout[] mRealspeedReLayouts = new RelativeLayout[10];
	private Animation mRealspeedTextAnimation = null;
	private Animation mRealspeedPointAnimation = null;
	private Animation mUnderlineAnimation = null;
	private int mCurRealspeedIndex = 0;
	
	private RelativeLayout mBtnProgressLyout = null;
	private ImageView mBtnProgressFullImg = null;
	private ViewPropertyAnimator mBtnProgressAnimatorView = null;

	private ImageView[] mStarsImgViews = new ImageView[5];
	private TextView mStarsMoviesPerText = null;
	private TextView mStarsMoviesTestingTV = null;
	
	private LinearLayout mUnderLineUpLlyout = null;
    private LinearInterpolator interpolator  = new LinearInterpolator();
    private int pingStatus = -1;
    
    private boolean mTestSpeedOnFlag = false;
    private boolean isGroupUser = false; 
    private TextView speed_tipsTextView;
    
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.test_speed_new_main);
		initView();
		mStartBtn.performClick();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
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
		mStartBtn = (Button)findViewById(R.id.test_speed_new_begin_btn);
		mStartBtn.setOnClickListener(mStartTestOnClickListener);
		
		mCurSpeedTV1 = (TextView)findViewById(R.id.test_speed_new_curspeed_tv1);
		//mCurSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_30));
		mCurSpeedTV2 = (TextView)findViewById(R.id.test_speed_new_curspeed_tv2);
		mCurSpeedTV3 = (TextView)findViewById(R.id.test_speed_new_curspeed_tv3);
		mCurSpeedTestingTV = (TextView)findViewById(R.id.test_speed_new_curspeed_testing);
		mCurSpeedRlyout = (RelativeLayout)findViewById(R.id.test_speed_new_curspeed_rlyout);
		
		mAverageSpeedTV1 = (TextView)findViewById(R.id.test_speed_new_average_tv1);
		//mAverageSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_30));
		mAverageSpeedTV2 = (TextView)findViewById(R.id.test_speed_new_average_tv2);
		mAverageSpeedTV3 = (TextView)findViewById(R.id.test_speed_new_average_tv3);
		mAverageSpeedTestingTV = (TextView)findViewById(R.id.test_speed_new_average_testing);
		mAverageSpeedRlyout = (RelativeLayout)findViewById(R.id.test_speed_new_average_rlyout);
		
		mBroadbandSpeedTV1 = (TextView)findViewById(R.id.test_speed_new_broadband_tv1);
		//mBroadbandSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_30));
		mBroadbandSpeedTV2 = (TextView)findViewById(R.id.test_speed_new_broadband_tv2);
		mBroadbandSpeedTV3 = (TextView)findViewById(R.id.test_speed_new_broadband_tv3);
		mBroadbandSpeedTestingTV = (TextView)findViewById(R.id.test_speed_new_broadband_testing);
		mBroadbandSpeedRlyout = (RelativeLayout)findViewById(R.id.test_speed_new_broadband_rlyout);
		
		mCurveLayout = (LinearLayout)findViewById(R.id.test_speed_new_canvas_llyout);
		mCurveImg = (ImageView)findViewById(R.id.test_speed_new_canvas_img);
		initRealspeedView();
		initStarsView();
		
		speed_tipsTextView = (TextView)findViewById(R.id.speed_tips);
	}
	
	@SuppressLint("NewApi")
	private void initRealspeedView(){
		mRealspeedTV[0] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv1);
		mRealspeedTV[1] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv2);
		mRealspeedTV[2] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv3);
		mRealspeedTV[3] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv4);
		mRealspeedTV[4] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv5);
		mRealspeedTV[5] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv6);
		mRealspeedTV[6] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv7);
		mRealspeedTV[7] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv8);
		mRealspeedTV[8] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv9);
		mRealspeedTV[9] = (TextView)findViewById(R.id.test_speed_new_realspeed_tva);
		
		mRealspeedDecimalTV[0] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv1_decimal);
		mRealspeedDecimalTV[1] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv2_decimal);
		mRealspeedDecimalTV[2] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv3_decimal);
		mRealspeedDecimalTV[3] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv4_decimal);
		mRealspeedDecimalTV[4] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv5_decimal);
		mRealspeedDecimalTV[5] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv6_decimal);
		mRealspeedDecimalTV[6] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv7_decimal);
		mRealspeedDecimalTV[7] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv8_decimal);
		mRealspeedDecimalTV[8] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv9_decimal);
		mRealspeedDecimalTV[9] = (TextView)findViewById(R.id.test_speed_new_realspeed_tva_decimal);
		
		mRealspeedUnitTV[0] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv1_unit);
		mRealspeedUnitTV[1] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv2_unit);
		mRealspeedUnitTV[2] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv3_unit);
		mRealspeedUnitTV[3] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv4_unit);
		mRealspeedUnitTV[4] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv5_unit);
		mRealspeedUnitTV[5] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv6_unit);
		mRealspeedUnitTV[6] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv7_unit);
		mRealspeedUnitTV[7] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv8_unit);
		mRealspeedUnitTV[8] = (TextView)findViewById(R.id.test_speed_new_realspeed_tv9_unit);
		mRealspeedUnitTV[9] = (TextView)findViewById(R.id.test_speed_new_realspeed_tva_unit);
		
		mBtnProgressLyout = (RelativeLayout)findViewById(R.id.test_speed_new_btn_progress_flyout);
		mBtnProgressFullImg = (ImageView)findViewById(R.id.test_speed_new_progress_btn_full_img);
		mBtnProgressFullImg.setPivotX(0.1f);
		mBtnProgressAnimatorView = mBtnProgressFullImg.animate();
		mBtnProgressAnimatorView.scaleX(0f);
		
		mRealspeedReLayouts[0] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout1);
		mRealspeedReLayouts[1] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout2);
		mRealspeedReLayouts[2] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout3);
		mRealspeedReLayouts[3] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout4);
		mRealspeedReLayouts[4] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout5);
		mRealspeedReLayouts[5] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout6);
		mRealspeedReLayouts[6] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout7);
		mRealspeedReLayouts[7] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout8);
		mRealspeedReLayouts[8] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyout9);
		mRealspeedReLayouts[9] = (RelativeLayout)findViewById(R.id.test_speed_new_realspeed_cur_rlyouta);
		
		mRealspeedTextAnimation = AnimationUtils.loadAnimation(TestSpeedNewActivity.this, R.anim.test_speed_text);
		
		mRealspeedPointRlyout[0] = (RelativeLayout)findViewById(R.id.test_speed_new_point1_rlyout);
		mRealspeedPointRlyout[1] = (RelativeLayout)findViewById(R.id.test_speed_new_point2_rlyout);
		mRealspeedPointRlyout[2] = (RelativeLayout)findViewById(R.id.test_speed_new_point3_rlyout);
		mRealspeedPointRlyout[3] = (RelativeLayout)findViewById(R.id.test_speed_new_point4_rlyout);
		mRealspeedPointRlyout[4] = (RelativeLayout)findViewById(R.id.test_speed_new_point5_rlyout);
		mRealspeedPointRlyout[5] = (RelativeLayout)findViewById(R.id.test_speed_new_point6_rlyout);
		mRealspeedPointRlyout[6] = (RelativeLayout)findViewById(R.id.test_speed_new_point7_rlyout);
		mRealspeedPointRlyout[7] = (RelativeLayout)findViewById(R.id.test_speed_new_point8_rlyout);
		mRealspeedPointRlyout[8] = (RelativeLayout)findViewById(R.id.test_speed_new_point9_rlyout);
		mRealspeedPointRlyout[9] = (RelativeLayout)findViewById(R.id.test_speed_new_pointa_rlyout);
		
		mUnderLineUpLlyout = (LinearLayout)findViewById(R.id.test_speed_new_under_line_up_llyout);
		mRealspeedPointLlyout = (LinearLayout)findViewById(R.id.test_speed_new_realspeed_point_llyout);
		mRealspeedPointAnimation = AnimationUtils.loadAnimation(TestSpeedNewActivity.this, R.anim.test_speed_point_anim);
		
		mUnderlineAnimation = AnimationUtils.loadAnimation(TestSpeedNewActivity.this, R.anim.test_speed_underline_anim);
	}
	
	@SuppressLint("NewApi")
	private void initStarsView(){
		mStarsImgViews[0] = (ImageView)findViewById(R.id.test_speed_new_stars_img1);
		mStarsImgViews[1] = (ImageView)findViewById(R.id.test_speed_new_stars_img2);
		mStarsImgViews[2] = (ImageView)findViewById(R.id.test_speed_new_stars_img3);
		mStarsImgViews[3] = (ImageView)findViewById(R.id.test_speed_new_stars_img4);
		mStarsImgViews[4] = (ImageView)findViewById(R.id.test_speed_new_stars_img5);
		
		mStarsMoviesPerText = (TextView)findViewById(R.id.test_speed_new_tv_level_tv2);
		//mStarsMoviesPerText.setTextSize(getResources().getDimension(R.dimen.text_size_30));
		
		mStarsMoviesTestingTV = (TextView)findViewById(R.id.test_speed_new_tv_level_testing);
	}
	
	@SuppressLint("NewApi")
	private void restoreUI(){
		mUnderLineUpLlyout.setVisibility(View.INVISIBLE);
		mCurveLayout.setVisibility(View.INVISIBLE);
		
		mRealspeedPointLlyout.setVisibility(View.VISIBLE);
		mRealspeedPointRlyout[0].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[1].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[2].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[3].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[4].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[5].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[6].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[7].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[8].setVisibility(View.INVISIBLE);
		mRealspeedPointRlyout[9].setVisibility(View.INVISIBLE);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.bottomMargin = 0;
		
//		mCurSpeedTV1.setText(getResources().getString(R.string.test_speed_testing_count));
//		mCurSpeedTV1.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
//		mCurSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_30));
//		params.addRule(RelativeLayout.RIGHT_OF, R.id.test_speed_new_curspeed_tv);  
//		mCurSpeedTV1.setLayoutParams(params);
		mCurSpeedTestingTV.setVisibility(View.VISIBLE);
		mCurSpeedTV1.setVisibility(View.INVISIBLE);
		mCurSpeedTV2.setVisibility(View.INVISIBLE);
		mCurSpeedTV3.setVisibility(View.INVISIBLE);
		
//		mAverageSpeedTV1.setText(getResources().getString(R.string.test_speed_testing_count));
//		mAverageSpeedTV1.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
//		mAverageSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_30));
//		params.addRule(RelativeLayout.RIGHT_OF, R.id.test_speed_new_average_tv);  
//		mAverageSpeedTV1.setLayoutParams(params);
		mAverageSpeedTestingTV.setVisibility(View.VISIBLE);
		mAverageSpeedTV1.setVisibility(View.INVISIBLE);
		mAverageSpeedTV2.setVisibility(View.INVISIBLE);
		mAverageSpeedTV3.setVisibility(View.INVISIBLE);
		
//		mBroadbandSpeedTV1.setText(getResources().getString(R.string.test_speed_testing_count));
//		mBroadbandSpeedTV1.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
//		mBroadbandSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_30));
//		params.addRule(RelativeLayout.RIGHT_OF, R.id.test_speed_new_broadband_tv); 
//		mBroadbandSpeedTV1.setLayoutParams(params);
		
		mBroadbandSpeedTestingTV.setVisibility(View.VISIBLE);
		mBroadbandSpeedTV1.setVisibility(View.INVISIBLE);
		mBroadbandSpeedTV2.setVisibility(View.INVISIBLE);
		mBroadbandSpeedTV3.setVisibility(View.INVISIBLE);
		
		for(int i=0;i<5;i++){
			mStarsImgViews[i].setBackgroundResource(R.drawable.test_speed_star_unselected);
		}
		
		for(int i=0;i<10;i++){
			mRealspeedTV[i].setText("");
			mRealspeedDecimalTV[i].setText("");
			mRealspeedUnitTV[i].setText("");
		}
		
//		mStarsMoviesPerText.setTextColor(getResources().getColor(R.color.settings_9a9a9a));
//		mStarsMoviesPerText.setText(getResources().getString(R.string.test_speed_testing_count));
//		mStarsMoviesPerText.setTextSize(getResources().getDimension(R.dimen.text_size_30));
		mStarsMoviesTestingTV.setVisibility(View.VISIBLE);
		mStarsMoviesPerText.setVisibility(View.INVISIBLE);
		mStarsMoviesPerText.setBackground(null);
	}
	
	View.OnClickListener mStartTestOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			if(mTestSpeedOnFlag){
				if(DEBUG){
					Log.e(TAG, "test speed is on ,so exit");
				}
				return;
			}
			if(DEBUG)
				Log.d(TAG,"start test speed.....................");
			if(mStartBtn.getText().toString().equalsIgnoreCase(getResources().getString(R.string.test_speed_repeat_test))){
				restoreUI();
			}
			mHandler.removeMessages(MSG_DOWNLOAD_COMPLETE);
			mHandler.removeMessages(MSG_DOWNLOAD_CANCLE);
			restoreAndredownload();
		}
	};
		
	void updateTotalSpeedView() {
		if(DEBUG){
			Log.e(TAG, "cur speed="+trans2bit(mCurSpeed));
			Log.e(TAG, "average speed="+trans2bit(mAverageSpeed));
			Log.e(TAG, "broadband speed="+trans2Band(mMaxCurSpeed));
		}
		String curSpeed = trans2bit(mMaxCurSpeed);
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
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.bottomMargin = -10;
		
		
		mCurSpeedTV1.setText(curSpeedSplit[0]);
		mCurSpeedTV1.setTextColor(getResources().getColor(R.color.settings_ffffff));
//		mCurSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_48));
//		params.addRule(RelativeLayout.RIGHT_OF, R.id.test_speed_new_curspeed_tv); 
//		mCurSpeedTV1.setLayoutParams(params);
		
		mCurSpeedTV2.setText("."+curSpeedSplit[1]);
		/*
		RelativeLayout.LayoutParams tv2params = (RelativeLayout.LayoutParams)mCurSpeedTV2.getLayoutParams();
		tv2params.addRule(RelativeLayout.RIGHT_OF, R.id.test_speed_new_curspeed_tv1);
		mCurSpeedTV2.setLayoutParams(tv2params);
		*/
		mCurSpeedTestingTV.setVisibility(View.GONE);
		mCurSpeedTV1.setVisibility(View.VISIBLE);
		mCurSpeedTV2.setVisibility(View.VISIBLE);
		mCurSpeedTV3.setVisibility(View.VISIBLE);
		if(curSpeedSplit[2].equalsIgnoreCase("KB/S")){
			mCurSpeedTV3.setText(curSpeedSplit[2]);
		}else{
			mCurSpeedTV3.setText(getResources().getString(R.string.test_speed_bigm_unit));
		}
		//mCurSpeedRlyout.startAnimation(mRealspeedTextAnimation);
		mCurSpeedTV1.startAnimation(mRealspeedTextAnimation);
		mCurSpeedTV2.startAnimation(mRealspeedTextAnimation);
		mCurSpeedTV3.startAnimation(mRealspeedTextAnimation);
		
		mAverageSpeedTV1.setText(averageSpeedSplit[0]);
		mAverageSpeedTV1.setTextColor(getResources().getColor(R.color.settings_ffffff));
//		mAverageSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_48));
//		params.addRule(RelativeLayout.RIGHT_OF, R.id.test_speed_new_average_tv);  
//		mAverageSpeedTV1.setLayoutParams(params);
		
		mAverageSpeedTV2.setText("."+averageSpeedSplit[1]);
		mAverageSpeedTestingTV.setVisibility(View.GONE);
		mAverageSpeedTV1.setVisibility(View.VISIBLE);
		mAverageSpeedTV2.setVisibility(View.VISIBLE);
		mAverageSpeedTV3.setVisibility(View.VISIBLE);
		if(averageSpeedSplit[2].equalsIgnoreCase("KB/S")){
			mAverageSpeedTV3.setText(averageSpeedSplit[2]);
		}else{
			mAverageSpeedTV3.setText(getResources().getString(R.string.test_speed_bigm_unit));
		}
		//mAverageSpeedRlyout.startAnimation(mRealspeedTextAnimation);
		mAverageSpeedTV1.startAnimation(mRealspeedTextAnimation);
		mAverageSpeedTV2.startAnimation(mRealspeedTextAnimation);
		mAverageSpeedTV3.startAnimation(mRealspeedTextAnimation);
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
		if(realSpeedSplit[2].equalsIgnoreCase("KB/S")){
			mRealspeedUnitTV[mCurRealspeedIndex].setText(realSpeedSplit[2]);
		}else{
			mRealspeedUnitTV[mCurRealspeedIndex].setText(getResources().getString(R.string.test_speed_bigm_unit));
		}
		mRealspeedReLayouts[mCurRealspeedIndex].startAnimation(mRealspeedTextAnimation);
				
		//point
		mRealspeedPointRlyout[mCurRealspeedIndex].setVisibility(View.VISIBLE);
		mRealspeedPointRlyout[mCurRealspeedIndex].startAnimation(mRealspeedPointAnimation);
		
		//btn progress
		mStartBtn.setVisibility(View.VISIBLE); //View.GONE
		mStartBtn.setText("");
		mBtnProgressLyout.setVisibility(View.VISIBLE);
		mBtnProgressFullImg.setVisibility(View.VISIBLE);
		if(mCurRealspeedIndex == 0){
			setViewProgress(mBtnProgressAnimatorView,mCurRealspeedIndex, DownloadTask.UPDATE_THRESHOLD*10);
		}
		mCurRealspeedIndex++;
	}

	@SuppressLint("NewApi")
	public void setViewProgress(ViewPropertyAnimator view,int progress,int duration) {
		if(DEBUG){
			Log.e(TAG, "test speed dowload progress");
		}
		mBtnProgressFullImg.setScaleX(0f);
		mBtnProgressFullImg.setTranslationX(0f);
		//view.scaleX(0f);
		//float scaleX = 1.0f*progress/10;
		float scaleX = 1.0f;
		view.scaleX(scaleX).setDuration(duration).translationX((-0.5f)*10).setInterpolator(interpolator).start();//.translationX((-0.5f)*10)
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
		mBroadbandSpeedTV1.setTextColor(getResources().getColor(R.color.settings_ffffff));
//		mBroadbandSpeedTV1.setTextSize(getResources().getDimension(R.dimen.text_size_48));
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//		params.bottomMargin = -10;
//		params.addRule(RelativeLayout.RIGHT_OF, R.id.test_speed_new_broadband_tv); 
//		mBroadbandSpeedTV1.setLayoutParams(params);
		
//		mBroadbandSpeedTV1.setGravity(Gravity.LEFT|Gravity.BOTTOM);
		mBroadbandSpeedTV2.setText("."+broadbandSpeedSplit[1]);
		mBroadbandSpeedTestingTV.setVisibility(View.GONE);
		mBroadbandSpeedTV1.setVisibility(View.VISIBLE);
		mBroadbandSpeedTV2.setVisibility(View.VISIBLE);
		mBroadbandSpeedTV3.setVisibility(View.VISIBLE);
		if(broadbandSpeedSplit[2].equalsIgnoreCase("KB/S")){
			mBroadbandSpeedTV3.setText(broadbandSpeedSplit[2]);
		}
		mStartBtn.setText(R.string.test_speed_repeat_test);
		//mBroadbandSpeedRlyout.startAnimation(mRealspeedTextAnimation);
		mBroadbandSpeedTV1.startAnimation(mRealspeedTextAnimation);
		mBroadbandSpeedTV2.startAnimation(mRealspeedTextAnimation);
		mBroadbandSpeedTV3.startAnimation(mRealspeedTextAnimation);
	}
	
	private void updateStarsView(){
		
		/*String broadbandSpeed = trans2Band(mMaxCurSpeed);
		Log.v(TAG, "broadbandSpeed=="+broadbandSpeed);
		String[] broadbandSpeedSplit = broadbandSpeed.split("\\.");
		if(broadbandSpeedSplit.length!=3){
			Log.e(TAG, "get broadband speed error");
			return;
		}

		int broadBand = Integer.valueOf(broadbandSpeedSplit[0]);
		if(DEBUG){
			Log.e(TAG, "update stars view..."+broadBand);
		}*/
		long broadBand = mMaxCurSpeed * 8;
 
		if(broadBand<=4*1024){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			
			mStarsMoviesTestingTV.setVisibility(View.GONE);
			mStarsMoviesPerText.setVisibility(View.VISIBLE);
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_360p));
			mStarsMoviesPerText.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mStarsMoviesPerText.setTextSize(getResources().getDimension(R.dimen.text_size_20));
			mStarsMoviesPerText.setBackgroundResource(R.drawable.test_speed_resolution_bg);
		}else if(broadBand>4*1024&&broadBand<=10*1024){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsMoviesTestingTV.setVisibility(View.GONE);
			mStarsMoviesPerText.setVisibility(View.VISIBLE);
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_720p));
			mStarsMoviesPerText.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mStarsMoviesPerText.setTextSize(getResources().getDimension(R.dimen.text_size_20));
			mStarsMoviesPerText.setBackgroundResource(R.drawable.test_speed_resolution_bg);
		}else if(broadBand>10*1024&&broadBand<=20*1024){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsMoviesTestingTV.setVisibility(View.GONE);
			mStarsMoviesPerText.setVisibility(View.VISIBLE);
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_1080p));
			mStarsMoviesPerText.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mStarsMoviesPerText.setTextSize(getResources().getDimension(R.dimen.text_size_20));
			mStarsMoviesPerText.setBackgroundResource(R.drawable.test_speed_resolution_bg);
		}else if(broadBand>20*1024&&broadBand<=50*1024){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_unselected);
			mStarsMoviesTestingTV.setVisibility(View.GONE);
			mStarsMoviesPerText.setVisibility(View.VISIBLE);
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_1080p));
			mStarsMoviesPerText.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mStarsMoviesPerText.setTextSize(getResources().getDimension(R.dimen.text_size_20));
			mStarsMoviesPerText.setBackgroundResource(R.drawable.test_speed_resolution_bg);
		}else if(broadBand>50*1024){
			mStarsImgViews[0].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[1].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[2].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[3].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsImgViews[4].setBackgroundResource(R.drawable.test_speed_star_selected);
			mStarsMoviesTestingTV.setVisibility(View.GONE);
			mStarsMoviesPerText.setVisibility(View.VISIBLE);
			mStarsMoviesPerText.setText(getString(R.string.test_speed_movie_4k));
			mStarsMoviesPerText.setTextColor(getResources().getColor(R.color.settings_ffffff));
			mStarsMoviesPerText.setTextSize(getResources().getDimension(R.dimen.text_size_20));
			mStarsMoviesPerText.setBackgroundResource(R.drawable.test_speed_resolution_bg);
		}
	}
	
	String trans2bit(long speed) {
		if(mDecimalFormat == null){
			mDecimalFormat = new DecimalFormat("#.00");
		}
		if (speed >= 1024) {
			float size = (float) speed / 1024;
			return mDecimalFormat.format(size) + ".MB/S";
		} else {
			return mDecimalFormat.format(speed) + ".KB/S";
		}
	}

	private String trans2Band(long speed){
		long band = speed * 8;
		
		if (band >= 1024) {
			long size = (long) band / 1024;
			return size + ".00.Mb/S";
		} else {
			return band + ".00.KB/S";
		}
	}
	
	public boolean isBinded() {
		return (mIsBinded && null != mService);
	}
	
	public void unbind() {
		if (!isBinded()) {
			return;
		}

		TestSpeedNewActivity.this.unbindService(mConnection);
	}
	
	/**
	 * bind service
	 */
	public void bindDownloadService(Runnable bindOK) {
		mCallbackBindOK = bindOK;
		mIsBinded = TestSpeedNewActivity.this.bindService(new Intent(TestSpeedNewActivity.this, DownloadService.class), 
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
				if(DEBUG){
					Log.e(TAG, "send MSG_DOWNLOAD_CANCLE "+mCurRealspeedIndex);
				}
//				mHandler.removeMessages(MSG_DOWNLOAD_CANCLE);
//				mHandler.sendEmptyMessage(MSG_DOWNLOAD_CANCLE);
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
		mTestSpeedOnFlag = true;
	}
	
	public class CheckNetworkTask extends AsyncTask<Void, Void, Integer> {
		private boolean load = false;
		private static final int ERROR_LOCAL = 0, ERROR_INTERNET = 1, NETWORK_OK = 2;

		private CheckNetworkTask(boolean needReDownload) {
			this.load = needReDownload;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			String gw = Utils.getGateway(TestSpeedNewActivity.this);
			Log.d(TAG, "---heyf---gateway:" + gw);
			if (gw == null) {
				return ERROR_LOCAL;
			} else {
				HttpURLConnection conn = null;
				try {
					if(pingStatus !=0){
						String str = "ping -c 1 -w 5 " + gw;
						Process p = Runtime.getRuntime().exec(str);
					    pingStatus = p.waitFor();
					}
					Log.d(TAG, "---heyf---status:" + pingStatus);
					if (pingStatus == 0) {
						URL url = new URL("http://www.baidu.com");
						conn = (HttpURLConnection) url.openConnection();
						if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
							checkNetworkISP();
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
				toastNetworkError();
			} else if (result == ERROR_INTERNET) {
				Log.d(TAG, "networker status : ERROR INTERNET");
				toastNetworkError();
			} else if (result == NETWORK_OK) {
				Log.d(TAG, "networker status : NETWORK OK");
				Log.d(TAG, "wherther to start down load:" + load);
				if (load) {
					if (isBinded()) {
						try {
							if(!mIsCancel){
								mService.startDownload(isGroupUser);
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
											mService.startDownload(isGroupUser);
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

	private void toastNetworkError(){
		Toast.makeText(TestSpeedNewActivity.this, getResources().getString(R.string.network_error), 2000).show();
		finish();
	}
	
	public static class MHandler extends Handler {
		// private Handler mHandler = new Handler() {

		private final WeakReference<TestSpeedNewActivity> weakaci;

		MHandler(TestSpeedNewActivity activity) {
			weakaci = new WeakReference<TestSpeedNewActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (null == msg) {
				return;
			}

			TestSpeedNewActivity aci = weakaci.get();
			if (aci == null)
				return;

			aci.handleMessage(msg);
		};
	};

	protected void handleMessage(Message msg) {
		if(DEBUG){
			Log.e(TAG, "handleMessage --------------"+msg.what);
		}
		switch (msg.what) {
		case MSG_DOWNLOAD_START:
			break;
		case MSG_DOWNLOAD_FINISH:
			mIsCancel = true;
			try {
				if(mService.isDownloading())
					return;
			} catch (Exception e) {
				// TODO: handle exception
			}
			/*
			if(mCurRealspeedIndex!=10){
				if(DEBUG){
					Log.e(TAG, "exception exit");
				}
				for(int i=0;i<(10-mCurRealspeedIndex);i++){
					updateRealSpeedView();
					try{
						Thread.sleep(DownloadTask.UPDATE_THRESHOLD);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			*/
			updateBroadbandView();
			updateTotalSpeedView();
			updateStarsView();
			//mRealspeedBtnProgress.setVisibility(View.GONE);
			mBtnProgressLyout.setVisibility(View.GONE);
			mStartBtn.setVisibility(View.VISIBLE);
			showCurverImage();
			mTestSpeedOnFlag = false;
			break;
		case MSG_DOWNLOAD_PROGRESS:
			//updateTotalSpeedView();
			updateRealSpeedView();
			break;
		case MSG_DOWNLOAD_FAILED:
			break;
		case MSG_DOWNLOAD_STOPED:
			break;
		case MSG_DOWNLOAD_COMPLETE:
			mIsCancel = true;
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
			updateTotalSpeedView();
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
			updateTotalSpeedView();
			updateStarsView();
			//mRealspeedBtnProgress.setVisibility(View.GONE);
			mBtnProgressLyout.setVisibility(View.GONE);
			mStartBtn.setVisibility(View.VISIBLE);
			showCurverImage();
			mTestSpeedOnFlag = false;
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
		case MSG_UPDATE_ISP:
			updateNetSpeedTips();
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
		int flag = 0;//0 is MB/S mode 1 is KB/S mode
		
		for(int i=0;i<10;i++){
			flag = 0;
			String tmpStr = trans2bit(mRealspeeds[i]);
			String[] tmpStrAarray = tmpStr.split("\\.");
			if(tmpStrAarray.length != 3){
				Log.e(TAG, "[getOffsetPoints] split speed array error!");
				return;
			}
			if(DEBUG){
				Log.e(TAG, "speed  = "+Long.toString(mRealspeeds[i]));
			}
			
			int curIndexSpeed = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(0, 1));
			int curIndexSpeed1 = 0;
			if(Long.toString(mRealspeeds[i]).length()>1){
				curIndexSpeed1 = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(1, 2));
				if(tmpStrAarray[2].equalsIgnoreCase("KB/S")){
					curIndexSpeed1 = (int)mRealspeeds[i];
					flag = 1;
				}
			}
			if(Long.toString(mRealspeeds[i]).length()>4){
				curIndexSpeed = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(0, 2));
			}

			if(tmpStrAarray[2].equalsIgnoreCase("KB/S")){
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
			if(mKSPointsIndex == 10){
                                //fix all kb data bug modiry by carter
				for(int i=0;i<10;i++){
					String temString = Long.toString(mRealspeeds[i]);
					if(temString.length()==1){
						mKSPoints[i][1] = Integer.valueOf(Long.toString(mRealspeeds[i]));
					}else if(temString.length()==2){
						mKSPoints[i][1] = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(0, 1))+10;
					}else if(temString.length()>=3){
						mKSPoints[i][1] = Integer.valueOf(Long.toString(mRealspeeds[i]).substring(0, 2));
					}
				}

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
				if((mKSPointsIndex+mMSPointsIndex) != 10){
					Log.e(TAG, "loss array member"+mKSPointsIndex+":"+mMSPointsIndex);
					return;
				}
				int msCount = 0 ;
				for(int i=0;i<mMSPointsIndex;i++){
					if(mMSPoints[i][1] != 0){
						msCount+=1;
					}
				}
 
				
				for(int i=0;i<mKSPointsIndex;i++){
					String temString = Long.toString(mRealspeeds[mKSPoints[i][0]]);
//					Log.v(TAG, "tempString=="+temString);
					if(temString.length()>=4){
						mKSPoints[i][1] = Integer.valueOf(temString.substring(0, 2));
					}
				}
				
				
				for(int i=0;i<msCount;i++){
					mMSPoints[i][1] = mMSPoints[i][1]+10;
					mKSPoints[mKSPointsIndex+i] = mMSPoints[i];
				}
				for (int j = 0; j < mKSPoints.length ; j++) {			
					for (int i = 0; i < mKSPoints.length - 1; i++) {				
						int[] tmp;
						if ((mKSPoints[i][1]>(mKSPoints[i + 1][1]) || 
								(mKSPoints[i][3])==1&&mKSPoints[i][2]>mKSPoints[i+1][2])) {	
							if(mKSPoints[i][3] == 1){
								tmp = mKSPoints[i];	
								mKSPoints[i] = mKSPoints[i + 1];					
								mKSPoints[i + 1] = tmp;	
							}else{
								tmp = mKSPoints[i];	
								mKSPoints[i] = mKSPoints[i + 1];					
								mKSPoints[i + 1] = tmp;		
							}
						  }else if((mKSPoints[i][1] == mKSPoints[i+1][1]) && mKSPoints[i][2]>mKSPoints[i+1][2]){
							tmp = mKSPoints[i];	
							mKSPoints[i] = mKSPoints[i + 1];					
							mKSPoints[i + 1] = tmp;	
						  }
					}		
				}
			}
			
			for(int i=0;i<10;i++){
				if(DEBUG){
					Log.e(TAG, "paixu 222: index "+i+" :"+mKSPoints[i][1]);
				}
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
		
		int height = mCurveLayout.getHeight();
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
		mUnderLineUpLlyout.setVisibility(View.VISIBLE);
		mUnderLineUpLlyout.startAnimation(mUnderlineAnimation);
		mRealspeedPointLlyout.setVisibility(View.INVISIBLE);
		mCurveLayout.setVisibility(View.VISIBLE);
		
		Drawable curveBmp = drawCurve(TestSpeedNewActivity.this,mSpeedOffsetPoints,width, height, 1);
		
		if (curveBmp != null) {
			if(DEBUG){
				Log.e(TAG, "i'm here....................");
			}
			mCurveImg.setImageDrawable(curveBmp);
			mCurveLayout.startAnimation(mUnderlineAnimation);
		}
	}
	
	public int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public Drawable drawCurve(Context ctx, int[] array,int width,int height, int frist) {
		int pointRadius = 10;
		int nMax = array[0];
		int nMin = array[0];
		int temp = 0;
		int useHeight = height - 15; // make some padding
		int size = array.length > 10 ? 10 : array.length;
		
		if (width <= 0 || height < (10 + 2 * pointRadius)) {
			Log.e(TAG, "width and height is bad");
			return null;
		}
		
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), 
//				R.drawable.test_speed_point_new).copy(Bitmap.Config.ARGB_8888, true);
		
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
			bmp = Bitmap.createBitmap(dip2px(this, width), dip2px(this, height), Bitmap.Config.ARGB_8888);
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
		paint.setStrokeWidth(0.5f);
		paint.setAntiAlias(true);  
		
		int xOffset = getResources().getDimensionPixelSize(R.dimen.test_speed_new_point_x_offset);
		int yOffset = getResources().getDimensionPixelSize(R.dimen.test_speed_new_point_y_offset);
		for (int i = 0; i < size - 1; i++) {
			pt = xy.get(i);
			end = xy.get(i + 1);
			 
			canvas.drawLine(dip2px(this, pt.x+xOffset), dip2px(this, pt.y+yOffset),
					dip2px(this, end.x+xOffset), dip2px(this, end.y+yOffset), paint);
			paint.setColor(ctx.getResources().getColor(R.color.settings_0c9bf9));
		}

		paint.setStrokeWidth(2f);
		/*
		for (int i = 0; i < size; i++) {
			pt = xy.get(i);
			canvas.drawBitmap(bitmap, pt.x, pt.y, paint);
		}
       */
		for (int i = 0; i < size; i++) {
			pt = xy.get(i);
			/*
			Shader mShader = new LinearGradient(pt.x+xOffset, pt.y+yOffset, pt.x + 20,
					pt.y + 20, new int[] {
						ctx.getResources().getColor(R.color.settings_ffffff),
						ctx.getResources().getColor(R.color.settings_blue_007eff),
						},
					null, Shader.TileMode.REPEAT);
					*/
			Shader mShader = new RadialGradient(pt.x+xOffset, pt.y+yOffset,10, 
					new int[] {
						ctx.getResources().getColor(R.color.settings_ffffff),
						ctx.getResources().getColor(R.color.settings_blue_007eff),
					},
					new float[]{
						0.6f,
						0.8f
					}, 
					Shader.TileMode.REPEAT);
			
				//paint.setShader(mShader);
			    paint.setColor(ctx.getResources().getColor(R.color.settings_ffffff));
		
				canvas.drawCircle(dip2px(this, pt.x+xOffset), dip2px(this, pt.y+yOffset), pointRadius, paint);
				 
		}
		paint.setColor(oldColor);
		return new BitmapDrawable(bmp);
	}
	
	private void checkNetworkISP() {

		try {
			Log.v(TAG, "checkNetworkISP");
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String url = "http://api.pthv.gitv.tv/api/ip/isGroupUser.json";
			HttpPost httpost = new HttpPost(url);
			httpost.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("userIP", ""));
			nvps.add(new BasicNameValuePair("version", "1.0"));
			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			response = httpclient.execute(httpost);

			HttpEntity entity = response.getEntity();
			String retSrc = EntityUtils.toString(entity);

//			Log.v(TAG, "result>>>" + retSrc);
	 
			JSONObject result = new JSONObject(retSrc);
			String token = result.getString("result");
			JSONObject result2 = new JSONObject(token);
			String isGroup = result2.getString("isGroupUser");
			if (isGroup.equals("true")) {
				isGroupUser = true;
			}else {
				mHandler.sendEmptyMessage(MSG_UPDATE_ISP);
			}
			Log.v(TAG, "re>>>" + isGroup);
			// TODO: handle exception
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateNetSpeedTips(){
		speed_tipsTextView.setText(getResources().getString(R.string.test_speed_unit_converter)+"   "+
				getResources().getString(R.string.net_speed_tips));
	}
	
}
