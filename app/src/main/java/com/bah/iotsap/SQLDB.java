package com.bah.iotsap;

import android.provider.BaseColumns;

/**
 * Created by 591263 on 7/26/2017.
 */

public final class SQLDB {

    //Prevent accidental instantiation of SQLDB class
    private SQLDB() {}

    //Define Table Contents
    public static class DataTypes implements BaseColumns {
        public static final String TABLE_NAME = "devices";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TARGET_ID = "mac";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LON = "longitude";
        public static final String COLUMN_ALT = "altitude";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_RSSI = "rssi";
        public static final String COLUMN_TYPE = "type";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DataTypes.TABLE_NAME + " (" +
                    DataTypes._ID + " INTEGER PRIMARY KEY," +
                    DataTypes.COLUMN_DATE + " TEXT," +
                    DataTypes.COLUMN_TIME + " TEXT," +
                    DataTypes.COLUMN_TARGET_ID + " TEXT," +
                    DataTypes.COLUMN_NAME + " TEXT," +
                    DataTypes.COLUMN_LAT + " TEXT," +
                    DataTypes.COLUMN_LON + " TEXT," +
                    DataTypes.COLUMN_ALT + " TEXT," +
                    DataTypes.COLUMN_ID + " TEXT," +
                    DataTypes.COLUMN_RSSI + " TEXT," +
                    DataTypes.COLUMN_TYPE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DataTypes.TABLE_NAME;



}
