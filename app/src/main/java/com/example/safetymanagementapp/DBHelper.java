package com.example.safetymanagementapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    String sql;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "SafetyManagementDB.db";
    private static final String TAG = "DBHelper";
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        //공지 테이블
        sql = "create table Notice ("
                + "id integer NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "detail TEXT NOT NULL, "
                + "date INTEGER NOT NULL "
                + ")";
        db.execSQL(sql);
    }

    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE Notice;");
        this.onCreate(db);
    }

    public void onOpen(SQLiteDatabase db){super.onOpen(db);}

}
