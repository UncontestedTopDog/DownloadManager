package com.example.administrator.downloadmanager.Download;

import android.content.Context;
import android.content.Intent;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.List;

import static com.example.administrator.downloadmanager.Download.Downloader.UPDATE;

/**
 * Created by huangweiliang on 2018/5/7.
 */

public class DownloadTask {
    public static final byte DOWNLOADSTART = 0;
    public static final byte DOWNLOADING = 1;
    public static final byte DOWNLOADPAUSE = 2;
    public static final byte DOWNLOADFINISH = 3;
    public static final byte DOWNLOADFAIL = 4;
    public static final byte DOWNLOADCANCLE = 5;

    public static final String STATE = "STATE";
    public static final String PROGRESS = "PROGRESS";
    public static final String DOWNLOADED = "DOWNLOADED";
    public static final String TOTALLENGTH = "TOTALLENGTH";
    public static final String URL = "URL";

    private Context mContext;
    private DownloadDAO mDownloadDAO;
    private FileInfo mFileInfo;
    public boolean mIsPause = false;
    public boolean mIsCancle = false;

    public DownloadTask(Context context, FileInfo fileInfo) {
        mContext = context;
        mFileInfo = fileInfo;
        mDownloadDAO = new DownloadDAOImpl(context);
    }

    public void download() {
        //寻找数据库中的数据，看是否存在缓存数据
        List<ThreadInfo> threadInfos = mDownloadDAO.getThreadInfo(mFileInfo.getUrl());
        ThreadInfo threadInfo;
        //没有缓存数据
        if (threadInfos.size() == 0)
            threadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);
            //有缓存数据，获取第一个数据。
        else threadInfo = threadInfos.get(0);
        new Thread(new DownloadThread(threadInfo)).start();
    }

    private class DownloadThread implements Runnable {
        private ThreadInfo mThreadInfo;

        public DownloadThread(ThreadInfo threadInfo) {
            mThreadInfo = threadInfo;
        }

        @Override
        public void run() {
            /**
             * 添加该下载任务到数据库中。
             * 计算出该从哪个位置进行下载。
             * 生成临时文件。
             * 生成随机访问文件流，并设置文件数据的位置。
             * 开始下载。
             * 广播下载进度，改变UI。
             * 暂停下载。
             * 下载完毕，在数据库中删除数据。
             * 将文件的名字由临时下载文件名称改为正式名称。
             */
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            RandomAccessFile randomAccessFile = null;
            long mDownloaded = 0;//下载进度

            //数据库中没有该数据，就在数据库中添加该数据
            if (!mDownloadDAO.isExists(mThreadInfo.getUrl(), mThreadInfo.getThread_id()))
                mDownloadDAO.insertThread(mThreadInfo);
            Intent intent = new Intent(UPDATE);
            try {
                java.net.URL url = new URL(mThreadInfo.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // 设置连接超时时间
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");

                //计算出下载的数据的开始位置
                long start = mThreadInfo.getStart() + mThreadInfo.getDownloaded();
                // 设置请求属性
                // 参数一：Range头域可以请求实体的一个或者多个子范围(一半用于断点续传)，如果用户的请求中含有range
                // ，则服务器的相应代码为206。
                // 参数二：表示请求的范围：比如头500个字节：bytes=0-499
                httpURLConnection.setRequestProperty("range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                //生成临时文件
                File file = new File(mFileInfo.getDownloadPath(), mFileInfo.getTmpFileName());
                //生成随机访问文件流
                randomAccessFile = new RandomAccessFile(file, "rwd");
                // 设置从哪里开始写入，如参数为100，那就从101开始写入
                randomAccessFile.seek(start);
                mDownloaded = mThreadInfo.getDownloaded();

                if (httpURLConnection.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
                    inputStream = httpURLConnection.getInputStream();
                    // 设置字节数组缓冲区
                    byte[] data = new byte[1024 * 10];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    noticBroadcast(intent, mDownloaded , mFileInfo.getLength(), DOWNLOADSTART);
                    while ((len = inputStream.read(data)) != -1) {
                        // 读取成功,写入文件
                        randomAccessFile.write(data, 0, len);
                        //更新下载进度
                        mDownloaded += len;
                        if (System.currentTimeMillis() - time > 2000) {
                            time = System.currentTimeMillis();
                            // 把当前进度通过广播传递给UI
                            noticBroadcast(intent, mDownloaded, mFileInfo.getLength(), DOWNLOADING);
                        }
                        mDownloadDAO.updateThread(mThreadInfo.getUrl(), mThreadInfo.getThread_id(), mDownloaded);
                        if (mIsPause) {
                            // 暂停下载，更新进度到数据库
                            mDownloadDAO.updateThread(mThreadInfo.getUrl(), mThreadInfo.getThread_id(), mDownloaded);
                            noticBroadcast(intent, mDownloaded , mFileInfo.getLength(), DOWNLOADPAUSE);
                            return;
                        }

                        if (mIsCancle) {
                            // 暂停下载，更新进度到数据库
                            mDownloadDAO.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getThread_id());
                            if (file.exists())
                                file.delete();
                            mDownloaded = 0 ;
                            noticBroadcast(intent, mDownloaded , mFileInfo.getLength(), DOWNLOADCANCLE);
                            return;
                        }

                    }
                    //将文件的名字由临时下载文件名称改为正式名称。
                    file.renameTo(new File(mFileInfo.getDownloadPath(), mFileInfo.getFileName()));
                    noticBroadcast(intent, mDownloaded , mFileInfo.getLength(), DOWNLOADFINISH);
                    // 当下载执行完毕时，删除数据库线程信息
                    mDownloadDAO.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getThread_id());
                }
            } catch (SocketException e) {
                // 暂停下载，更新进度到数据库
                mDownloadDAO.updateThread(mThreadInfo.getUrl(), mThreadInfo.getThread_id(), mDownloaded);
                noticBroadcast(intent, mDownloaded , mFileInfo.getLength(), DOWNLOADFAIL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (randomAccessFile != null)
                    try {
                        randomAccessFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }

        }
    }

    private void noticBroadcast(Intent intent, long downloaded,long totalLength ,byte state) {
        intent.putExtra(DOWNLOADED, downloaded);
        intent.putExtra(TOTALLENGTH, totalLength);
        intent.putExtra(STATE, state);
        intent.putExtra(URL, mFileInfo.getUrl());
        mContext.sendBroadcast(intent);
    }

}
