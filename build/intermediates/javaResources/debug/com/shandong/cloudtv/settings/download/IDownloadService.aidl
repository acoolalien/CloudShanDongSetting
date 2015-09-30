package com.shandong.cloudtv.settings.download;

import com.shandong.cloudtv.settings.download.IDownloadCallback;

interface IDownloadService {
    void registerCallback(IDownloadCallback cb);
    void unregisterCallback(IDownloadCallback cb);
    void startDownload(boolean isGroupUser);
    void stopDownload();
    boolean isDownloading();
}