package com.bah.iotsap.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.bah.iotsap.db.SQLDB;

/**
 * Created by 591263 on 7/27/2017.
 */
public class DBUtil {


    //returns a ContentValues object to later insert into the database

    public static ContentValues insert(String date, String time, String deviceMac, String deviceName,
                                       Location location, int id, int rssi, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("mac", deviceMac);
        contentValues.put("name", deviceName);
        if(location!=null) {
            contentValues.put("latitude", location.getLatitude());
            contentValues.put("longitude", location.getLongitude());
            contentValues.put("altitude", location.getAltitude());
        }
        contentValues.put("id", id);
        contentValues.put("rssi", rssi);
        contentValues.put("type", type);
        contentValues.put("new", "new");
        return contentValues;
    }

    //Returns a Cursor object that has all the new data from the last scan
    public static Cursor read(SQLiteDatabase db, String tablename) {
        String[] projection = {
                SQLDB.DataTypes.COLUMN_DATE,
                SQLDB.DataTypes.COLUMN_TIME,
                SQLDB.DataTypes.COLUMN_TARGET_ID,
                SQLDB.DataTypes.COLUMN_NAME,
                SQLDB.DataTypes.COLUMN_LAT,
                SQLDB.DataTypes.COLUMN_LON,
                SQLDB.DataTypes.COLUMN_ALT,
                SQLDB.DataTypes.COLUMN_ID,
                SQLDB.DataTypes.COLUMN_RSSI,
                SQLDB.DataTypes.COLUMN_TYPE
        };

        String selection = SQLDB.DataTypes.COLUMN_NEW + " = ?";
        String[] selectionArgs = {"new"};
        String sortOrder =
                SQLDB.DataTypes.COLUMN_TIME + " DESC";

        Cursor cursor = db.query(
                tablename,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        return cursor;
    }

    //Delete all old entries
    public static void delete(SQLiteDatabase db, String tablename) {
        // Define 'where' part of query.
        String selection = SQLDB.DataTypes.COLUMN_NEW + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "new" };
        // Issue SQL statement.
        db.delete(tablename, selection, selectionArgs);
    }

    //Modify all old devices with old tag
    public static void update(SQLiteDatabase db, String tablename) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(SQLDB.DataTypes.COLUMN_NEW, "old");

        // Which row to update, based on the title
        String selection = SQLDB.DataTypes.COLUMN_NEW + " LIKE ?";
        String[] selectionArgs = { "new" };

        int count = db.update(
                tablename,
                values,
                selection,
                selectionArgs);
    }
}
