package com.example.administrator.downloadmanager.Download;

import java.util.List;

/**
 * Created by huangweiliang on 2018/5/3.
 */

public interface DownloadDAO {
    // 新增一条线程信息
    public void insertThread(ThreadInfo threadInfo);

    // 删除一条线程信息(多线程下载，可能一个url对应多个线程，所以需要2个条件)
    public void deleteThread(String url, int thread_id);

    // 修改一条线程信息
    public void updateThread(String url, int thread_id, long finished);

    // 查询线程有关信息（根据url查询下载该url的所有线程信息）
    public List<ThreadInfo> getThreadInfo(String url);

    // 判断线程是否已经存在
    public boolean isExists(String url, int thread_id);

}