package com.kevinlu.airquality;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "stations.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME = "Station";
    public static final String COLUMN_NAME = "_name";
    public static final String COLUMN_JSON = "json";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME + " BLOB NOT NULL, " +
                COLUMN_JSON + " BLOB NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * create record
     **/
    public void saveStationRecord(String stationJSON) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        //Create a new Station object and use Gson to deserialize JSON data
        //into the Station object
        Station station = gson.fromJson(stationJSON, Station.class);
        values.put(COLUMN_NAME, station.getData().getCity());
        values.put(COLUMN_JSON, stationJSON);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * delete record
     **/
    public void deleteStationRecord(String name, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _name='" + name + "'");
    }
}