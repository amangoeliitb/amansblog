package com.blogspot.amangoeliitb.amansblog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "postDB.db" ;
    public static final String POSTS_TABLE_NAME = "postTable" ;
    public static final String POSTS_COLUMN_ID = "id" ;
    public static final String POSTS_COLUMN_TITLE = "title" ;
    public static final String POSTS_COLUMN_CONTENT = "content" ;
    public static final String POSTS_COLUMN_DATE = "date" ;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1) ;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table postTable (id integer primary key, title text, content text, date text)") ;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS postTable");
        onCreate(db);
    }

    public boolean insertPost(String title, String content, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("content", content);
        contentValues.put("date", date);
        db.insert("postTable", null, contentValues);
        return true ;
    }

    public void clearDatabase() {
        this.getReadableDatabase().execSQL("delete from postTable");
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase() ;
        return (int) DatabaseUtils.queryNumEntries(db, POSTS_TABLE_NAME);
    }

    public ArrayList <String> getAllTitles() {
        ArrayList <String> each_title = new ArrayList<String>() ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from postTable", null);
        res.moveToFirst();
        while(!res.isAfterLast()) {
            each_title.add(res.getString(res.getColumnIndex(POSTS_COLUMN_TITLE)));
            res.moveToNext();
        }
        res.close();
        return each_title ;
    }

    public ArrayList <String> getAllContents() {
        ArrayList <String> each_content = new ArrayList<String>() ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from postTable", null);
        res.moveToFirst();
        while(!res.isAfterLast()) {
            each_content.add(res.getString(res.getColumnIndex(POSTS_COLUMN_CONTENT)));
            res.moveToNext();
        }
        res.close();
        return each_content ;
    }

    public ArrayList <String> getAlldates() {
        ArrayList <String> each_date = new ArrayList<String>() ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from postTable", null);
        res.moveToFirst();
        while(!res.isAfterLast()) {
            each_date.add(res.getString(res.getColumnIndex(POSTS_COLUMN_DATE)));
            res.moveToNext();
        }
        res.close();
        return each_date ;
    }
}
