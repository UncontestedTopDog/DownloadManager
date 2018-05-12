package com.example.administrator.downloadmanager.Download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangweiliang on 2018/5/3.
 */

public class DownloadDAOImpl implements DownloadDAO {

    private DBHelper dbHelper;

    public DownloadDAOImpl(Context context) {
        dbHelper = new DBHelper(context);
    }

    @Override
    public void insertThread(ThreadInfo threadInfo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("thread_id", threadInfo.getThread_id());
        values.put("url ", threadInfo.getUrl());
        values.put("start ", threadInfo.getStart());
        values.put("end ", threadInfo.getEnd());
        values.put("finished ", threadInfo.getDownloaded());
        db.insert(DBHelper.TABLE, null, values);
        db.close();
    }

    @Override
    public void deleteThread(String url, int thread_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DBHelper.TABLE, "url=? and thread_id=?", new String[]{url, String.valueOf(thread_id)});
        db.close();
    }

    @Override
    public void updateThread(String url, int thread_id, long finished) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("update threadinfo set finished = ? where url = ? and thread_id=?", new Object[]{finished, url, thread_id});
        db.close();
    }

    @Override
    public List<ThreadInfo> getThreadInfo(String url) {
        List<ThreadInfo> list = new ArrayList<ThreadInfo>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE, null, "url=?", new String[]{url}, null, null, null);
        while (cursor.moveToNext()) {
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.setThread_id(cursor.getInt(cursor.getColumnIndex("thread_id")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            threadInfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            threadInfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threadInfo.setDownloaded(cursor.getInt(cursor.getColumnIndex("finished")));
            list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int thread_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE, null, "url=? and thread_id=?", new String[]{url, String.valueOf(thread_id)}, null, null, null);
        boolean isExists = cursor.moveToNext();
        db.close();
        return isExists;
    }

}