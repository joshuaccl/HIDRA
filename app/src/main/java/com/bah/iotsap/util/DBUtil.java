package com.bah.iotsap.util;

import android.content.ContentValues;
import android.location.Location;

import com.bah.iotsap.SQLDB;

/**
 * Created by 591263 on 7/27/2017.
 */

public class DBUtil {

    public static ContentValues insert(String date, String time, String deviceMac, String deviceName,
                                       Location location, int id, int rssi, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLDB.DataTypes.COLUMN_DATE, date);
        contentValues.put(SQLDB.DataTypes.COLUMN_TIME, time);
        contentValues.put(SQLDB.DataTypes.COLUMN_TARGET_ID, deviceMac);
        contentValues.put(SQLDB.DataTypes.COLUMN_NAME, deviceName);
        contentValues.put(SQLDB.DataTypes.COLUMN_LAT, location.getLatitude());
        contentValues.put(SQLDB.DataTypes.COLUMN_LON, location.getLongitude());
        contentValues.put(SQLDB.DataTypes.COLUMN_ALT, location.getAltitude());
        contentValues.put(SQLDB.DataTypes.COLUMN_ID, id);
        contentValues.put(SQLDB.DataTypes.COLUMN_RSSI, rssi);
        contentValues.put(SQLDB.DataTypes.COLUMN_TYPE, type);
        return contentValues;
    }
}
