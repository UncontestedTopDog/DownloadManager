package com.example.administrator.downloadmanager.Download;

/**
 * Created by huangweiliang on 2018/5/8.
 */

public interface DownloadListener {
    void onDownloading(int progress);

    void onDownloadPause(String url);

    void onDownloadStart(int progress);

    void onDownloadFinish(String url);

    void onDownloadFail(String url);

    void onDownloadCancle(String url);
}
