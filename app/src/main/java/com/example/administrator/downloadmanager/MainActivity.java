package com.example.administrator.downloadmanager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.administrator.downloadmanager.Download.BaseDownloadListener;
import com.example.administrator.downloadmanager.Download.DownloadManager;
import com.example.administrator.downloadmanager.Download.FileInfo;

public class MainActivity extends AppCompatActivity {

    private String downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloads/";
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setMax(100);
        String fileName = "haha.mp4";
        String url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
        final FileInfo fileInfo = new FileInfo(fileName, url, downloadPath);
        findViewById(R.id.pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausedownloadfile(fileInfo);
            }
        });
        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadfile(fileInfo);
            }
        });
    }

    private void pausedownloadfile(FileInfo fileInfo) {
        DownloadManager.INSTANCE().pause(fileInfo);
    }

    private void downloadfile(FileInfo fileInfo) {

        DownloadManager.INSTANCE().download(MainActivity.this, fileInfo, new BaseDownloadListener() {
            @Override
            public void onDownloading(int progress) {
                super.onDownloading(progress);
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onDownloadStart(int progress) {
                super.onDownloadStart(progress);
            }

            @Override
            public void onDownloadPause(String url) {
                super.onDownloadPause(url);
            }

            @Override
            public void onDownloadFinish(String url) {
                super.onDownloadFinish(url);
                mProgressBar.setProgress(100);
            }

            @Override
            public void onDownloadFail(String url) {
                super.onDownloadFail(url);
            }

            @Override
            public void onDownloadCancle(String url) {
                super.onDownloadCancle(url);
            }
        });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }
}
