package com.shandong.cloudtv.settings.download;

import com.shandong.cloudtv.settings.download.IDownloadCallback;
import com.shandong.cloudtv.settings.download.IDownloadService;

import android.R.bool;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class DownloadService extends Service {
	private static final String TAG = "TestSpeed";

	public static final int DOWNLOAD_SERVICE_STATUS = 100;

	public static final int ERR_CODE_SUCCESS = 0;
	public static final int ERR_CODE_NO_SPACE = -1;
	public static final int ERR_CODE_NO_ITEM = -2;
	public static final int ERR_CODE_TASK_IS_RUNNING = -3;
	public static final int ERR_CODE_NO_GPRS = -4;

	private static final int MSG_DOWNLOAD_START = 1;
	private static final int MSG_DOWNLOAD_FINISH = 2;
	private static final int MSG_DOWNLOAD_PROGRESS = 3;
	private static final int MSG_DOWNLOAD_FAILED = 4;
	private static final int MSG_DOWNLOAD_STOPED = 5;
	private static final int MSG_NETWORK_VALID = 9;
	private static final int MSG_NETWORK_INVALID = 10;
	private static DownloadService mInstance = null;
	private DownloadTask mDownloadTask = null;
	private DownloadListener mDownloadListener;
	private BroadcastReceiver mNetworkChangeListener = null;
	private boolean mCallbackRegisted = false;
	private boolean mDataConnected = true;
	private boolean isStoped = true;
	private boolean mSDCardExist = true;
	private boolean mStopRequest = true;
	private boolean mConnForDownload = false;
	private String mSoftId;

	private boolean isGroup = false;
	
	private Looper mConnLooper;

	private final RemoteCallbackList<IDownloadCallback> mCallbacks = new RemoteCallbackList<IDownloadCallback>();

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			final int callbacks = mCallbacks.beginBroadcast();
			if (callbacks > 0 && mCallbackRegisted) {
				IDownloadCallback item = mCallbacks.getBroadcastItem(0);
				try {
					switch (msg.what) {
					case MSG_DOWNLOAD_START:
						Log.i(TAG, "Download start.....");
						isStoped = false;
						item.downloadStart();
						break;
					case MSG_DOWNLOAD_FINISH:
						Log.i(TAG, "Download finish.....");
						item.downloadFinish();
						break;
					case MSG_DOWNLOAD_PROGRESS:
						if (!isStoped) {
							Log.i(TAG, "Download progress.....");
							item.downloadProgress(msg.arg1,msg.arg2);
						}
						break;
					case MSG_DOWNLOAD_FAILED:
						Log.i(TAG, "Download failed.....");
						String cause = (String) msg.obj;
						item.downloadFailed(cause);
						break;
					case MSG_DOWNLOAD_STOPED:
						Log.i(TAG, "Download cancel.....");
						isStoped = true;
						item.downloadStopedFinish();
						break;
					case MSG_NETWORK_VALID:
						Log.i(TAG, "Network is ok " + mDataConnected);
						if (mConnForDownload) {
							if (!mStopRequest) {
								startDownloadThread(mSoftId);
							} else {
								Log.i(TAG, "Need to send stop");
								mHandler.sendEmptyMessage(MSG_DOWNLOAD_STOPED);
							}
						} else {
							item.networkConnected(mDataConnected);
						}
						mConnForDownload = false;
						break;
					case MSG_NETWORK_INVALID:
						Log.e(TAG, "Network invalid");
						item.networkConnected(false);
						break;
					}
				} catch (RemoteException e) {
					Log.e(TAG, "RemoteException ", e);
				}

				mCallbacks.finishBroadcast();
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	private final IDownloadService.Stub mBinder = new IDownloadService.Stub() {
		@Override
		public void registerCallback(IDownloadCallback cb) throws RemoteException {
			Log.i(TAG, "mCallbacks registered");
			if (cb != null) {
				mCallbacks.register(cb);
				mCallbackRegisted = true;
			}
		}

		@Override
		public void unregisterCallback(IDownloadCallback cb) throws RemoteException {
			if (cb != null) {
				Log.i(TAG, "ungregister");
				mCallbacks.unregister(cb);
				mCallbackRegisted = false;
			}
		}

		@Override
		public void startDownload(boolean isGroupUser) throws RemoteException {
			isGroup = isGroupUser;
			DownloadService.this.startDownload();
		}

		@Override
		public void stopDownload() throws RemoteException {
			DownloadService.this.stopDownloadTask();
		}

		@Override
		public boolean isDownloading() throws RemoteException {
			return (mDownloadTask == null) ? false : true;
		}
	};

	public static void startService(final Context context) {
		if (mInstance != null) {
			return;
		}
		context.startService(new Intent(context, DownloadService.class));
	}

	public static void stopService() {
		if (mInstance == null) {
			return;
		}
		mInstance.stopSelf();
	}

	public DownloadService() {
		mInstance = this;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerNetworkListener();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopDownloadTask();

		if (mNetworkChangeListener != null) {
			unregisterReceiver(mNetworkChangeListener);
			mNetworkChangeListener = null;
		}

		mInstance = null;

		if (mConnLooper != null) {
			mConnLooper.quit();
		}

		mCallbacks.kill();
	}

	private void registerNetworkListener() {
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		if (mNetworkChangeListener == null) {
			mNetworkChangeListener = new BroadcastReceiver() {

				@Override
				public void onReceive(Context arg0, Intent arg1) {
					ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()
							&& networkInfo.isAvailable()) {
						mDataConnected = true;
						mHandler.sendEmptyMessage(MSG_NETWORK_VALID);
					} else {
						mDataConnected = false;
						mHandler.sendEmptyMessage(MSG_NETWORK_INVALID);
					}

				}

			};
		}
		registerReceiver(mNetworkChangeListener, filter);
	}

	public void startDownload() {
		mStopRequest = false;
		if (mDataConnected) {
			startDownloadThread(mSoftId);
		} else {
			mConnForDownload = true;
			Log.i(TAG, "dc in start.....");
		}
	}
	
	public boolean getDownloadStatus(){
		return mStopRequest;
	}

	private void startDownloadThread(String softId) {
		if (mDownloadTask == null) {
			Log.d(TAG, " --- DownloadTask is null");
			mHandler.removeMessages(MSG_DOWNLOAD_STOPED);
			mDownloadListener = new DownloadListener();
			mDownloadTask = new DownloadTask(this, mDownloadListener,isGroup);
			new Thread(mDownloadTask).start();
		}else {
			mDownloadTask.cancelTask();
			//mDownloadListener = new DownloadListener();
			mDownloadTask = new DownloadTask(this, mDownloadListener,isGroup);
			new Thread(mDownloadTask).start();
		}
	}

	public synchronized void stopDownloadTask() {
		Log.e(TAG, "stopDownloadTask..........");
		mStopRequest = true;
		isStoped = true;
		if (mDownloadTask != null) {
			mDownloadTask.cancelTask();
			//mDownloadTask = null;
		}
	}

	public static final int NEW_ITEM_ID = -1;

	/*
	 * App Must call this, when download complete.
	 */
	public void downloadComplete() {
		if (mDownloadTask == null) {
			return;
		}
		mDownloadTask = null;

	}

	public boolean isDownloading() {
		return (mDownloadTask == null) ? false : true;
	}

	public class DownloadListener implements TaskListener {
		@Override
		public void taskCancelled(Task task) {
			stopDownloadTask();
		}

		@Override
		public void taskStarted(Task task) {
			mHandler.removeMessages(MSG_DOWNLOAD_STOPED);
			mHandler.sendEmptyMessage(MSG_DOWNLOAD_START);
		}

		@Override
		public void taskFailed(Task task, String cause) {
			stopDownloadTask();
			Message msg = mHandler.obtainMessage(MSG_DOWNLOAD_FAILED);
			msg.obj = cause;
			mHandler.sendMessage(msg);
		}

		@Override
		public void taskProgress(Task task, long value, long max) {
			Message msg = mHandler.obtainMessage(MSG_DOWNLOAD_PROGRESS);
			mHandler.removeMessages(MSG_DOWNLOAD_PROGRESS);
			msg.arg1 = (int) value;
			msg.arg2 = (int) max;
			mHandler.sendMessage(msg);
		}

		@Override
		public void taskCompleted(Task task) {
			downloadComplete();
//			mHandler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
		}

		@Override
		public void taskCancelCompleted(Task task) {
			if (mDownloadTask != null) {
				mDownloadTask = null;
				Log.e(TAG, "taskCancelCompleted................");
				mHandler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
			}
		}
	}
}
