package com.example.administrator.downloadmanager.Download;

import java.io.Serializable;

/**
 * Created by huangweiliang on 2018/5/3.
 */

public class ThreadInfo implements Serializable {

    private int thread_id;
    private String url;
    private long start;
    private long end;
    private long downloaded;

    public ThreadInfo() {
    }

    public ThreadInfo(int thread_id, String url, long start, long end, long downloaded) {
        this.thread_id = thread_id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.downloaded = downloaded;
    }

    public int getThread_id() {
        return thread_id;
    }

    public void setThread_id(int thread_id) {
        this.thread_id = thread_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return "ThreadInfo [thread_id=" + thread_id + ", url=" + url + ", start=" + start + ", end=" + end + ", downloaded=" + downloaded + "]";
    }

}