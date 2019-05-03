package com.chuson.fantazaka;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class DBAdapter {
    protected final Context context;

    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    static final String DATABASE_NAME = "fantazaka.db";
    static final int DATABASE_VERSION = 1;

    static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public static final String TABLE_NAME = "fantazakaitems";
    public static final String COL_ID = "_id";
    public static final String COL_URL = "url";
    public static final String COL_MEMBER = "member";
    public static final String COL_ADDEDDATE = "addeddate";
    public static final String COL_LASTLOTTERYDATE = "lastlotterydate";
    public static final String COL_IMAGEURI = "imageuri";

    //
    // init
    //
    public DBAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    //
    // SQLiteOpenHelper
    //
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME + " ("
                            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_URL + " TEXT NOT NULL,"
                            + COL_MEMBER + " TEXT NOT NULL,"
                            + COL_ADDEDDATE + " TEXT NOT NULL,"
                            + COL_IMAGEURI + " TEXT NOT NULL,"
                            + COL_LASTLOTTERYDATE + " TEXT NOT NULL"
                    + ");"
            );
        }

        @Override public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion) {
            if(oldVersion < newVersion){}
            Log.d("log", "DB upgrade");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    //
    // Adapter Methods
    //
    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        dbHelper.close();
    }

    //
    // App Methods
    //
    public boolean deleteAllItems(){
        return db.delete(TABLE_NAME, null, null) > 0;
    }
    public boolean deleteItem(int id){
        return db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }
    public Cursor getAllItems(){
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    private ContentValues putValues(Item item){
        ContentValues values = new ContentValues();
        values.put(COL_URL, item.getUrl());
        values.put(COL_MEMBER, item.getMember());
        values.put(COL_ADDEDDATE, item.getAddedDate());
        values.put(COL_IMAGEURI, item.getImageUri());
        values.put(COL_LASTLOTTERYDATE, item.getLastLotteryDate());

        return values;
    }
    public void saveItem(Item item)
    {
        if(item.getAddedDate().equals("")){
            SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
            item.setAddedDate(sf.format(new Date()));
        }
        db.insertOrThrow(TABLE_NAME, null, putValues(item));
    }

    public void update(Item item)
    {
        db.update(TABLE_NAME, putValues(item),COL_ID + "=?", new String[]{""+ item.getId()});
    }

}
