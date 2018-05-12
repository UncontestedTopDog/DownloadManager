package com.example.administrator.downloadmanager.Download;

import android.util.Log;

/**
 * Created by huangweiliang on 2018/5/8.
 */

public abstract class BaseDownloadListener implements DownloadListener {
    private static final String TAG = "BaseDownloadListener";

    @Override
    public void onDownloading(int progress) {
        Log.i(TAG,"downloading");
    }

    @Override
    public void onDownloadStart(int progress) {
        Log.i(TAG,"download start");
    }

    @Override
    public void onDownloadPause(String url) {
        Log.i(TAG,"download pause");
    }

    @Override
    public void onDownloadFinish(String url) {
        Log.i(TAG,"download finish");
        dissolveRelations(url);
    }

    @Override
    public void onDownloadFail(String url) {
        Log.i(TAG,"download fail");
        dissolveRelations(url);
    }

    @Override
    public void onDownloadCancle(String url) {
        Log.i(TAG,"download cancle");
        dissolveRelations(url);
    }

    private void dissolveRelations(String url){
        DownloadManager.INSTANCE().unregisterReceiver(url);
        DownloadManager.INSTANCE().removeDownloader(url);
    }
}
