package com.kevinlu.airquality;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {
    Context c;
    SQLiteDatabase db;
    DatabaseHelper helper;

    public DatabaseAdapter(Context c) {
        this.c = c;
        helper = new DatabaseHelper(c);
    }

    //OPEN DB
    public void openDB() {
        try {
            db = helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //CLOSE DB
    public void closeDB() {
        try {
            helper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //INSERT/SAVE
    public boolean add(String name) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseConstants.NAME, name);

            db.insert(DatabaseConstants.TB_NAME, DatabaseConstants.ROW_ID, cv);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //SELECT/RETRIEVE
    public Cursor retrieve() {
        String[] columns = {DatabaseConstants.ROW_ID, DatabaseConstants.NAME};

        return db.query(DatabaseConstants.TB_NAME, columns, null, null, null, null, null);
    }

    //DELETE/REMOVE
    public boolean delete(int id) {
        try {
            int result = db.delete(DatabaseConstants.TB_NAME, DatabaseConstants.ROW_ID + " =?", new String[]{String.valueOf(id)});
            if (result > 0) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
