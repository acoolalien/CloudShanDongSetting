package com.shandong.cloudtv.settings.download;

interface IDownloadCallback {
	void downloadProgress(long downloadSize,long max);
	void downloadFinish();
	void downloadFailed(String cause);
	void downloadStart();
	void downloadStopedFinish();
	void networkConnected(boolean result);
}