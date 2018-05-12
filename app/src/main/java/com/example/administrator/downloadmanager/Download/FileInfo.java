package com.example.administrator.downloadmanager.Download;

import java.io.Serializable;

/**
 * Created by huangweiliang on 2018/5/3.
 */

public class FileInfo implements Serializable {

    private int id;
    private String fileName;
    private String url;
    private long length;
    private long downloaded;
    private String tmpFileName;
    private String downloadPath;

    public FileInfo() {
    }

    public FileInfo(String fileName, String url, String downloadPath) {
        this.id = 0;
        this.fileName = fileName;
        this.tmpFileName = fileName + ".tmp";
        this.url = url;
        this.length = 0;
        this.downloaded = 0;
        this.downloadPath = downloadPath;
    }

    public FileInfo(int id, String fileName, String url, int length,
                    int downloaded, String downloadPath) {
        super();
        this.id = id;
        this.fileName = fileName;
        this.tmpFileName = fileName + ".tmp";
        this.url = url;
        this.length = length;
        this.downloaded = downloaded;
        this.downloadPath = downloadPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public String getTmpFileName() {
        return tmpFileName;
    }

    public void setTmpFileName(String tmpFileName) {
        this.tmpFileName = tmpFileName;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    @Override
    public String toString() {
        return "FileInfo [id=" + id + ", fileName=" + fileName + ", url=" + url
                + ", length=" + length + ", downloaded=" + downloaded + ", downloadPath=" + downloadPath + "]";
    }

}