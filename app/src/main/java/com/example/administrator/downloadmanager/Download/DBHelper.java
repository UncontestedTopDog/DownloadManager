package com.example.administrator.downloadmanager.Download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by huangweiliang on 2018/5/3.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "download.db";
    public static final int VERSION = 1;
    public static final String TABLE = "threadinfo";
    public static final String CREATE_DB = "create table threadinfo (_id integer primary key autoincrement,thread_id integer,url text,start integer,end integer,finished integer) ";
    public static final String DROP_DB = "drop table if exists threadinfo";

    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_DB);
        db.execSQL(CREATE_DB);
    }

}