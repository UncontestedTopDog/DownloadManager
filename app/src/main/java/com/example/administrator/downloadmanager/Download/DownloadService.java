package com.example.administrator.downloadmanager.Download;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.administrator.downloadmanager.Download.DownloadTask.*;
import static com.example.administrator.downloadmanager.Download.Downloader.UPDATE;


/**
 * Created by huangweiliang on 2018/5/7.
 */

public class DownloadService extends Service {
    public static final int INIT = 0;
    public static final String START = "STRAT";
    public static final String PAUSE = "PAUSE";
    public static final String CANCLE = "CANCLE";
    private Map<String, DownloadTask> mDownloadTasks = new HashMap<>();
    private DownloadDAO mDownloadDAO;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    // 新建下载任务，并进行下载，添加到下载任务栈
                    DownloadTask downloadTask = new DownloadTask(DownloadService.this, fileInfo);
                    mDownloadTasks.put(fileInfo.getUrl(), downloadTask);
                    mDownloadTasks.get(fileInfo.getUrl()).download();
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDownloadDAO = new DownloadDAOImpl(this);
        FileInfo fileInfo = new FileInfo();
        if (intent != null) {
            fileInfo = (FileInfo) intent.getSerializableExtra(fileInfo.getClass().getName());
            if (fileInfo != null) {
                if (intent.getAction().equals(START)) {
                    File file = new File(fileInfo.getDownloadPath(), fileInfo.getFileName());
                    if (file.exists())
                        sendDownloadFinishBC(fileInfo.getUrl());//如果该文件是存在的话，就代表已经下载完成
                    else
                        new Thread(new DownloadFileThread(fileInfo)).start();
                } else if (intent.getAction().equals(PAUSE)) {
                    if (mDownloadTasks.get(fileInfo.getUrl()) != null)
                        mDownloadTasks.get(fileInfo.getUrl()).mIsPause = true;//暂停下载
                } else if (intent.getAction().equals(CANCLE)) {
                    if (mDownloadTasks.get(fileInfo.getUrl()) != null)
                        mDownloadTasks.get(fileInfo.getUrl()).mIsPause = true;//暂停下载
                    List<ThreadInfo> threadInfos = mDownloadDAO.getThreadInfo(fileInfo.getUrl());
                    for (ThreadInfo threadInfo : threadInfos)
                        mDownloadDAO.deleteThread(threadInfo.getUrl(), threadInfo.getThread_id());
                    File file = new File(fileInfo.getDownloadPath(), fileInfo.getTmpFileName());
                    if (file.exists())
                        file.delete();
                    Intent newintent = new Intent(UPDATE);
                    newintent.putExtra(PROGRESS, 0);
                    newintent.putExtra(STATE, DOWNLOADCANCLE);
                    newintent.putExtra(DownloadTask.URL, fileInfo.getUrl());
                    this.sendBroadcast(newintent);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DownloadFileThread implements Runnable {
        private FileInfo mFileInfo;

        //初始化文件操作获取网络资源大小长度，开辟子线程
        public DownloadFileThread(FileInfo fileInfo) {
            mFileInfo = fileInfo;
        }

        @Override
        public void run() {
            /*
             * 1、打开网络连接，获取文件长度 2、创建本地文件，长度和网络文件相等
             */
            HttpURLConnection httpURLConnection = null;
            RandomAccessFile randomAccessFile = null;
            try {
                URL url = new URL(mFileInfo.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                //除了下载，一律用POST
                httpURLConnection.setRequestMethod("GET");
                int length = -1;
                //判断网络连接是否成功
                if (httpURLConnection.getResponseCode() == HttpStatus.SC_OK)
                    length = httpURLConnection.getContentLength();
                //文件长度小于等于0为错误，跳出
                if (length <= 0)
                    return;
                File dir = new File(mFileInfo.getDownloadPath());
                if (!dir.exists())
                    dir.mkdir();
                //判断是否存在缓存文件，如果没有，那么就要将数据库中的信息相应的删除。
                //出现这种数据库中有数据但是没有缓存文件的情况，应该是用户手动打开文件管理器进行删除。
                File file = new File(mFileInfo.getDownloadPath(), mFileInfo.getTmpFileName());
                if (!file.exists()) {
                    List<ThreadInfo> threadInfos = mDownloadDAO.getThreadInfo(mFileInfo.getUrl());
                    for (ThreadInfo ti : threadInfos)
                        mDownloadDAO.deleteThread(ti.getUrl(), ti.getThread_id());
                }
                //创建随机访问文件流，权限为read/write/delete。
                randomAccessFile = new RandomAccessFile(file, "rwd");
                //设置文件的大小长度
                randomAccessFile.setLength(length);
                mFileInfo.setLength(length);
                handler.obtainMessage(INIT, mFileInfo).sendToTarget();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDownloadFinishBC(String url) {
        Intent newIntent = new Intent(UPDATE);
        newIntent.putExtra(PROGRESS, 100);
        newIntent.putExtra(DownloadTask.URL, url);
        newIntent.putExtra(STATE, DOWNLOADFINISH);
        this.sendBroadcast(newIntent);
    }

}
