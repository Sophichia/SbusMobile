package edu.usc.imsc.sbus.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.usc.imsc.sbus.basicClass.Halt;
import edu.usc.imsc.sbus.basicClass.Hub;
import edu.usc.imsc.sbus.basicClass.Stop;
import edu.usc.imsc.sbus.basicClass.Vehicle;
import edu.usc.imsc.sbus.dao.DatabaseContract;

/**
 * Created by danielCantwell on 11/2/15.
 * Copyright (c) Cantwell Code 2015. All Rights Reserved
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SBus.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.SQL_CREATE_STOP_ENTRIES);
        db.execSQL(DatabaseContract.SQL_CREATE_VEHICLE_ENTRIES);
        db.execSQL(DatabaseContract.SQL_CREATE_HUB_ENTRIES);
        db.execSQL(DatabaseContract.SQL_CREATE_CONNECTION_ENTRIES);
        db.execSQL(DatabaseContract.SQL_CREATE_HALT_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(DatabaseContract.SQL_DELETE_STOP_ENTRIES);
        db.execSQL(DatabaseContract.SQL_DELETE_HUB_ENTRIES);
        db.execSQL(DatabaseContract.SQL_DELETE_VEHICLE_ENTRIES);
        db.execSQL(DatabaseContract.SQL_DELETE_CONNECTION_ENTRIES);
        db.execSQL(DatabaseContract.SQL_DELETE_HALT_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /****************************************************************
     *              Helper Methods For Inserting Data
     ****************************************************************/

    public SQLiteDatabase beginWriting() {
        //        Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        return db;
    }

    public void endWriting(SQLiteDatabase db) {
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public long insertStop(SQLiteDatabase db, Stop s) {


//        Create a new map of values where the column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataStop.COLUMN_NAME_STOP_ID, s.getId());
        values.put(DatabaseContract.DataStop.COLUMN_NAME_STOP_NAME, s.getName());
        values.put(DatabaseContract.DataStop.COLUMN_NAME_LATITUDE, s.getLatitude());
        values.put(DatabaseContract.DataStop.COLUMN_NAME_LONGITUDE, s.getLongitude());
        values.put(DatabaseContract.DataStop.COLUMN_NAME_HUB_ID, s.getHubId());

        // Insert the new row, returning the primary key value for the new row
        return db.insert(DatabaseContract.DataStop.TABLE_NAME, "null", values);
    }

    //    Added by Mengjia
    public long insertHub(SQLiteDatabase db, Hub h) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataHub.COLUMN_NAME_HUB_ID, h.getId());
        values.put(DatabaseContract.DataHub.COLUMN_NAME_LATITUDE, h.getLatitude());
        values.put(DatabaseContract.DataHub.COLUNM_NAME_LONGITUDE, h.getLongitude());

        return db.insert(DatabaseContract.DataHub.TABLE_NAME, "null", values);
    }

    public long insertHalt(SQLiteDatabase db, String stopId, String stopName,Halt halt){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataHalt.COLUMN_NAME_STOP_ID, stopId);
        values.put(DatabaseContract.DataHalt.COLUMN_NAME_STOP_NAME, stopName);
        values.put(DatabaseContract.DataHalt.COLUMN_NAME_ROUTE, halt.getRoute());
        values.put(DatabaseContract.DataHalt.COLUMN_NAME__TIME,halt.getTime());

        return db.insert(DatabaseContract.DataHalt.TABLE_NAME, "null", values);
    }
    public long insertVehicle(Vehicle v) {
//        Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

//        Create a new map of values where the column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataVehicle.COLUMN_NAME_ROUTE_ID, v.getServiceId());
        values.put(DatabaseContract.DataVehicle.COLUMN_NAME_SERVICE_ID, v.getServiceId());
        values.put(DatabaseContract.DataVehicle.COLUMN_NAME_TRIP_ID, v.getServiceId());
        values.put(DatabaseContract.DataVehicle.COLUMN_NAME_SHAPE_ID, v.getServiceId());
        values.put(DatabaseContract.DataVehicle.COLUMN_NAME_ROUTE_LONG_NAME, v.getServiceId());
        values.put(DatabaseContract.DataVehicle.COLUMN_NAME_ROUTE_SHORT_NAME, v.getServiceId());
        values.put(DatabaseContract.DataVehicle.COLUMN_NAME_SERVICE_ID, v.getServiceId());

//        Insert the new row, returning the primary key value for the new row
        return db.insert(DatabaseContract.DataVehicle.TABLE_NAME, "null", values);
    }

    public long insertConnection(long vehicleId, long stopId) {
        //        Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

//        Create a new map of values where the column names are the keys
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DataConnection.COLUMN_NAME_STOP_ID, vehicleId);
        values.put(DatabaseContract.DataConnection.COLUMN_NAME_VEHICLE_ID, stopId);

//        Insert the new row, returning the primary key value for the new row
        return db.insert(DatabaseContract.DataConnection.TABLE_NAME, "null", values);
    }

    /**********************************************************************
     *                  Helper Methods for Retrieving Data
     ***********************************************************************/

    public Cursor retrieveAllStops() {
//        Gets the data repository in read mode
        SQLiteDatabase db = this.getReadableDatabase();

//        Defines a projection that specifies which columns from
//        the database you will actually use after this query
        String[] projection = {
                DatabaseContract.DataStop.COLUMN_NAME_STOP_ID,
                DatabaseContract.DataStop.COLUMN_NAME_STOP_NAME,
                DatabaseContract.DataStop.COLUMN_NAME_LATITUDE,
                DatabaseContract.DataStop.COLUMN_NAME_LONGITUDE,
                DatabaseContract.DataStop.COLUMN_NAME_HUB_ID,
        };

        return db.query(
                DatabaseContract.DataStop.TABLE_NAME,   // the table to query
                projection,                             // the columns to return
                null,                                   // the columns for the WHERE clause
                null,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row group
                null                                    // the sort order
        );
    }

    public Cursor retrieveAllHalt(){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DatabaseContract.DataHalt.COLUMN_NAME_STOP_ID,
                DatabaseContract.DataHalt.COLUMN_NAME_STOP_NAME,
                DatabaseContract.DataHalt.COLUMN_NAME_ROUTE,
                DatabaseContract.DataHalt.COLUMN_NAME__TIME,
        };

        return db.query(DatabaseContract.DataHalt.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
                );
    }

    public long retrieveHaltsNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.DataHalt.TABLE_NAME, null);
        cursor.moveToFirst();
        Log.d("Halt Test","The number of halt is "+cursor.getLong(0));
        return cursor.getLong(0);
    }

    public long retrieveStopNumberById(String stopId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.DataStop.TABLE_NAME
                +" WHERE "+DatabaseContract.DataStop.COLUMN_NAME_STOP_ID +
                "='"+stopId+"'"
                , null);
        cursor.moveToFirst();
        Log.d("Halt Test","The number of halt is "+cursor.getLong(0));
        return cursor.getLong(0);
    }

    public String retrieveStopName(String stopId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DatabaseContract.DataStop.TABLE_NAME +
                " WHERE "+
                DatabaseContract.DataStop.COLUMN_NAME_STOP_ID +
                "='"+stopId+"'",null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(DatabaseContract.DataStop.COLUMN_NAME_STOP_NAME));
    }
    public Cursor retrieveAllHubs(){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                DatabaseContract.DataHub.COLUMN_NAME_HUB_ID,
                DatabaseContract.DataHub.COLUMN_NAME_LATITUDE,
                DatabaseContract.DataHub.COLUNM_NAME_LONGITUDE
        };

        return db.query(
                DatabaseContract.DataHub.TABLE_NAME,   // the table to query
                projection,                             // the columns to return
                null,                                   // the columns for the WHERE clause
                null,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row group
                null                                    // the sort order
        );
    }

    public Cursor retrieveAllTransit() {
//        Gets the data repository in read mode
        SQLiteDatabase db = this.getReadableDatabase();

//        Defines a projection that specifies which columns from
//        the database you will actually use after this query
        String[] projection = {
                DatabaseContract.DataVehicle.COLUMN_NAME_ROUTE_ID,
                DatabaseContract.DataVehicle.COLUMN_NAME_SERVICE_ID,
                DatabaseContract.DataVehicle.COLUMN_NAME_TRIP_ID,
                DatabaseContract.DataVehicle.COLUMN_NAME_SHAPE_ID,
                DatabaseContract.DataVehicle.COLUMN_NAME_ROUTE_LONG_NAME,
                DatabaseContract.DataVehicle.COLUMN_NAME_ROUTE_SHORT_NAME
        };

        return db.query(
                DatabaseContract.DataVehicle.TABLE_NAME,   // the table to query
                projection,                             // the columns to return
                null,                                   // the columns for the WHERE clause
                null,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row group
                null                                    // the sort order
        );
    }

    public Cursor retrieveConnections(Stop s) {
//        Gets the data repository in read mode
        SQLiteDatabase db = this.getReadableDatabase();

//        Defines a projection that specifies which columns from
//        the database you will actually use after this query
        String[] projection = {
                DatabaseContract.DataVehicle.COLUMN_NAME_ROUTE_ID,
                DatabaseContract.DataVehicle.COLUMN_NAME_SERVICE_ID,
        };

        return db.query(
                DatabaseContract.DataConnection.TABLE_NAME,   // the table to query
                projection,                             // the columns to return
                null,                                   // the columns for the WHERE clause
                null,                                   // the values for the WHERE clause
                null,                                   // don't group the rows
                null,                                   // don't filter by row group
                null                                    // the sort order
        );
    }

    public void deleteWatchedItem(String stopId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ DatabaseContract.DataHalt.TABLE_NAME+" WHERE "+ DatabaseContract.DataHalt.COLUMN_NAME_STOP_ID+"=?",new Object[]{stopId});
        db.close();
    }

}
