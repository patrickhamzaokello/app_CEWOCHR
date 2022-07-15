package com.pkasemer.MyFamlinkApp.localDatabase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pkasemer.MyFamlinkApp.Models.Case;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Belal on 1/27/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "NamesDB";
    public static final String TABLE_NAME = "names";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "name";
    public static final String COLUMN_CASE_CATEGORY = "case_category";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    //database version
    private static final int DB_VERSION = 2;

    List<Case> caseList;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TITLE +
                " VARCHAR, "+ COLUMN_CASE_CATEGORY+ " VARCHAR, "+ COLUMN_LOCATION+ " VARCHAR, " + COLUMN_DESCRIPTION+ " VARCHAR, " + COLUMN_STATUS +
                " TINYINT);";
        db.execSQL(sql);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    /*
     * This method is taking five arguments
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addCase(String title,String casecategory,String location, String description, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_CASE_CATEGORY, casecategory);
        contentValues.put(COLUMN_LOCATION, location);
        contentValues.put(COLUMN_DESCRIPTION, description);
        contentValues.put(COLUMN_STATUS, status);
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public List<Case> list_DB_Cases() {
        String sql = "select * from " + TABLE_NAME + " order by "+ COLUMN_ID + " DESC ";
        SQLiteDatabase db = this.getReadableDatabase();
        caseList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                String case_category = cursor.getString(2);
                String location = cursor.getString(3);
                String description = cursor.getString(4);
                int status = Integer.parseInt(cursor.getString(5));
                caseList.add(new Case(name,case_category,location, description, status));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return caseList;
    }

    public void deleteReport(String id_str) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{id_str});
    }

    public int countRecords(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from " +TABLE_NAME , null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();

        return count;
    }


    public void clearReports() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }




}