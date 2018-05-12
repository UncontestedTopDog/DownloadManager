package com.example.administrator.downloadmanager.Download;

import android.content.Context;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by huangweiliang on 2018/5/8.
 */

public class DownloadManager {

    private Map<String, Downloader> mDownloaderMap = new HashMap();

    private static DownloadManager mDownloadManager = null;

    public DownloadManager() {
    }

    public static DownloadManager INSTANCE() {
        if (mDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (mDownloadManager == null) {
                    mDownloadManager = new DownloadManager();
                }
            }
        }
        return mDownloadManager;
    }
    public void download(Context context, FileInfo fileInfo, DownloadListener downloadListener) {
        if (mDownloaderMap.containsKey(fileInfo.getUrl())) {
            if (!mDownloaderMap.get(fileInfo.getUrl()).isDownloading())
                mDownloaderMap.get(fileInfo.getUrl()).download();
        } else {
            final Downloader downloader = new Downloader(context, downloadListener, fileInfo);
            mDownloaderMap.put(fileInfo.getUrl(), downloader);
            mDownloaderMap.get(fileInfo.getUrl()).download();
        }
    }

    public void pause(FileInfo fileInfo) {
        if (mDownloaderMap.containsKey(fileInfo.getUrl()))
            mDownloaderMap.get(fileInfo.getUrl()).pause();
    }

    public void cancle(FileInfo fileInfo) {
        if (mDownloaderMap.containsKey(fileInfo.getUrl())) {
            mDownloaderMap.get(fileInfo.getUrl()).cancle();
        }

    }

    public void removeDownloader(String url) {
        if (mDownloaderMap.containsKey(url))
            mDownloaderMap.remove(url);
    }

    public void unregisterReceiver(String url) {
        if (mDownloaderMap.containsKey(url))
            mDownloaderMap.get(url).unregisterReceiver();
    }

    public void onDestroy() {
        for (Downloader downloader : mDownloaderMap.values()) {
            downloader.pause();
            downloader.unregisterReceiver();
        }
    }

}
