package com.example.administrator.downloadmanager.Download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.File;

import static com.example.administrator.downloadmanager.Download.DownloadTask.*;


/**
 * Created by huangweiliang on 2018/5/7.
 */

public class Downloader {
    private Context mContext;
    private DownloadListener mDownloadListener;
    private FileInfo mFileInfo;
    private boolean mIsDownloading = true;
    public static final String UPDATE = "UPDATE";

    public Downloader(Context context, DownloadListener downloadListener, FileInfo fileInfo) {
        mContext = context;
        mDownloadListener = downloadListener;
        mFileInfo = fileInfo;
        // 注册广播接收者
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE);
        mContext.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(broadcastReceiver);
    }

    public void download() {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.setAction(DownloadService.START);
        intent.putExtra(mFileInfo.getClass().getName(), mFileInfo);
        mContext.startService(intent);
        mIsDownloading = true;
    }

    public void pause() {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.setAction(DownloadService.PAUSE);
        intent.putExtra(mFileInfo.getClass().getName(), mFileInfo);
        mContext.startService(intent);
        mIsDownloading = false;
    }

    public void cancle() {
        Intent intent = new Intent(mContext, DownloadService.class);
        intent.setAction(DownloadService.CANCLE);
        intent.putExtra(mFileInfo.getClass().getName(), mFileInfo);
        mContext.startService(intent);
        mIsDownloading = false;
    }

    public boolean isDownloading() {
        return mIsDownloading;
    }

    // 广播接收者
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UPDATE)) {
                int progress = intent.getIntExtra(PROGRESS, 0);
                final long downloaded = intent.getLongExtra(DOWNLOADED, 0);
                final long totallength = intent.getLongExtra(TOTALLENGTH, 0);
                final String url = intent.getStringExtra(URL);
                final int state = intent.getByteExtra(STATE, (byte) -1);
                if (progress != 100)
                    progress = (int) (downloaded * 100 / totallength);
                Log.i("BaseDownloadListener", state + "    " + progress);
                if (!url.equals(mFileInfo.getUrl()))
                    return;
                switch (state) {
                    case DOWNLOADSTART:
                        mDownloadListener.onDownloadStart(progress);
                        break;
                    case DOWNLOADING:
                        mDownloadListener.onDownloading(progress);
                        break;
                    case DOWNLOADFINISH:
                        mDownloadListener.onDownloadFinish(url);
                        break;
                    case DOWNLOADPAUSE:
                        mDownloadListener.onDownloadPause(url);
                        break;
                    case DOWNLOADFAIL:
                        mDownloadListener.onDownloadFail(url);
                        break;
                    case DOWNLOADCANCLE:
                        mDownloadListener.onDownloadCancle(url);
                        break;
                    default:
                        break;

                }
            }
        }
    };
}
