package edu.usc.imsc.sbus.service;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.usc.imsc.sbus.dao.ServerStatics;
import edu.usc.imsc.sbus.activity.WelcomeActivity;
import edu.usc.imsc.sbus.basicClass.Hub;
import edu.usc.imsc.sbus.basicClass.Stop;
import edu.usc.imsc.sbus.dao.DatabaseContract;

/**
 * Created by danielCantwell on 11/2/15.
 * Copyright (c) Cantwell Code 2015. All Rights Reserved
 */
public class StopsRequest {

    private final DataRequestListener.RequestType mRequestType;
    private Activity mActivity;
    private DataRequestListener mListener;
    private boolean mProgressUpdate;

    public StopsRequest(DataRequestListener.RequestType rt) {
        mRequestType = rt;
        mProgressUpdate = false;
    }

    public void getAllStops(Activity activity, DataRequestListener listener) {
        mActivity = activity;
        mListener = listener;

        GetAllStops task = new GetAllStops();
        task.execute();
    }

    /**
     * GET ALL STOPS AND THE HUBS
     * AJAX call to the API
     */

    private class GetAllStops extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = "GetAllStops";

        @Override
        protected Void doInBackground(Void... params) {

            // Local Stops Request
            if (mRequestType.equals(DataRequestListener.RequestType.Local)) {
                DatabaseHelper dbh = new DatabaseHelper(mActivity);
                //retrieve all hubs
                Cursor cursorForHub = dbh.retrieveAllHubs();
                List<Hub> hubs = new ArrayList<>();

                if(cursorForHub.moveToFirst()){
                    do{
                        String id = cursorForHub.getString(cursorForHub.getColumnIndexOrThrow(DatabaseContract.DataHub.COLUMN_NAME_HUB_ID));
                        double lat = cursorForHub.getDouble(cursorForHub.getColumnIndexOrThrow(DatabaseContract.DataHub.COLUMN_NAME_LATITUDE));
                        double lon = cursorForHub.getDouble(cursorForHub.getColumnIndexOrThrow(DatabaseContract.DataHub.COLUNM_NAME_LONGITUDE));

                        hubs.add(new Hub(id,lat,lon));
                    } while (cursorForHub.moveToNext());
                }
                cursorForHub.close();
                //  mListener.HubsResponse(hubs);

                Cursor cursor = dbh.retrieveAllStops();
                List<Stop> stops = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        String id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataStop.COLUMN_NAME_STOP_ID));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataStop.COLUMN_NAME_STOP_NAME));
                        double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.DataStop.COLUMN_NAME_LATITUDE));
                        double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.DataStop.COLUMN_NAME_LONGITUDE));
                        String hubId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DataStop.COLUMN_NAME_HUB_ID));
                        stops.add(new Stop(id, name, lat, lon, hubId));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                dbh.close();
                //mListener.StopsResponse(stops);
                mListener.HubsResponse(hubs,stops);


                // Server Stops Request
            } else if (mRequestType.equals(DataRequestListener.RequestType.Server)) {

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGetCount = new HttpGet(ServerStatics.HOST + ServerStatics.STOPS_COUNT);

                try {

                    HttpResponse countExecute = client.execute(httpGetCount);
                    InputStream countContent = countExecute.getEntity().getContent();

                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(countContent));
                    StringBuilder stringBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        stringBuilder.append(inputStr);

                    JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                    JSONObject messageObject = jsonObject.getJSONObject("message");
                    int pageCount = messageObject.getInt("number_of_pages");

                    //download hub information, added by Mengjia
                    HttpGet httpGetHubCount = new HttpGet(ServerStatics.HOST + ServerStatics.HUBS_COUNT);
                    HttpResponse countHubExecute  = client.execute(httpGetHubCount);
                    InputStream countHubContent = countHubExecute.getEntity().getContent();

                    BufferedReader countHubReader = new BufferedReader(new InputStreamReader(countHubContent));
                    StringBuilder countHubStringBuilder = new StringBuilder();

                    String countHubInputStr;
                    while((countHubInputStr = countHubReader.readLine())!= null)
                        countHubStringBuilder.append(countHubInputStr);

                    JSONObject jsonObjectCountHub = new JSONObject(countHubStringBuilder.toString());
                    JSONObject countHubMessageObject = jsonObjectCountHub.getJSONObject("message");
                    int hubPageCount = countHubMessageObject.getInt("number_of_pages");

//                    Log.d(LOG_TAG, "Stops Pages: " + String.valueOf(pageCount));
                    if (mProgressUpdate) ((WelcomeActivity) mActivity).setProgressMax(pageCount+hubPageCount);

                    /* For each page of hubs, load the hub. Added by Mengjia*/
                    DatabaseHelper dbh = new DatabaseHelper((mActivity));
                    SQLiteDatabase db = dbh.beginWriting();
                    for (int i = 1; i <= hubPageCount; i++){
                        HttpGet httpGetHubsPage = new HttpGet(ServerStatics.HOST + ServerStatics.HUBS_PAGE + String.valueOf(i));
                        HttpResponse executeHub = client.execute(httpGetHubsPage);
                        InputStream contentHub = executeHub.getEntity().getContent();

                        if (mProgressUpdate) {
                            ((WelcomeActivity) mActivity).setProgressCurrent(i);
//                            Log.d(LOG_TAG, "updating progress");
                        }
                        BufferedReader hubContentReader = new BufferedReader(new InputStreamReader(contentHub));
                        StringBuilder hubContentStringBuilder = new StringBuilder();
                        String hubContentInputStr;
                        while((hubContentInputStr = hubContentReader.readLine()) != null)
                            hubContentStringBuilder.append(hubContentInputStr);

                        JSONObject hubJsonObject = new JSONObject(hubContentStringBuilder.toString());
                        JSONArray hubArray = hubJsonObject.getJSONArray("message");

                        for(int j = 0; j<hubArray.length(); j++){
                            JSONArray tmpHub = hubArray.getJSONArray(j);
                            Object hubId = tmpHub.get(0);
                            Object hubLat = tmpHub.get(1);
                            Object hubLon = tmpHub.get(2);
                            // Log.d("Hub Test", "hub Number is "+ j);
                            Hub h = new Hub(hubId.toString(),Double.valueOf(hubLat.toString()),Double.valueOf(hubLon.toString()));
                            dbh.insertHub(db,h);
                        }


                    }

                    /* For each page of stops, load the stops */
                    //the original regex is wrong and when I modify it, it still will lose some information, so I changed it to JSON analysis. Mengjia
                    for (int i = 1; i <= pageCount; i++) {

//                        Log.d(LOG_TAG, "Reading page " + String.valueOf(i) + " of stops");

                        HttpGet httpGetStopsPage = new HttpGet(ServerStatics.HOST + ServerStatics.STOPS_PAGE + String.valueOf(i));
                        HttpResponse execute = client.execute(httpGetStopsPage);
                        InputStream content = execute.getEntity().getContent();

                        //DataInputStream data = new DataInputStream(content);

                        // Create database helper to enter data
                        //DatabaseHelper dbh = new DatabaseHelper(mActivity);
                        // SQLiteDatabase db = dbh.beginWriting();

                        // Create pattern for the scanner to search for in the input stream
                        //Pattern regex = Pattern.compile("\"([^\"]+)\",\"([^\"]+)\",([^,]+),([^\\]]+),\\d+");
//                        Scanner sc = new Scanner(data);
                        //sc.useDelimiter("\\[");

                        if (mProgressUpdate) {
                            ((WelcomeActivity) mActivity).setProgressCurrent(i+hubPageCount);
//                            Log.d(LOG_TAG, "updating progress");
                        }

                        BufferedReader stopContentReader = new BufferedReader(new InputStreamReader(content));
                        StringBuilder stopContentStringBuilder = new StringBuilder();
                        String stopContentInputStr;
                        while((stopContentInputStr = stopContentReader.readLine())!=null)
                            stopContentStringBuilder.append(stopContentInputStr);

                        JSONObject stopJsonObject = new JSONObject(stopContentStringBuilder.toString());
                        JSONArray stopArray = stopJsonObject.getJSONArray("message");

                        for(int j = 0; j<stopArray.length() ; j++){
                            JSONArray tmpStop = stopArray.getJSONArray(j);
                            Object stopId = tmpStop.get(0);
                            Object stopName = tmpStop.get(1);
                            Object stopLat = tmpStop.get(2);
                            Object stopLon = tmpStop.get(3);
                            Object hubId = tmpStop.get(4);

                            if(stopId.toString().equals(5151))
                                Log.d("hus test","founded 5151");

                            Stop s = new Stop(stopId.toString(), stopName.toString(), Double.valueOf(stopLat.toString()),Double.valueOf(stopLon.toString()),hubId.toString());
                            dbh.insertStop(db,s);
                        }
                    }
                    dbh.endWriting(db);
                    dbh.close();

                } catch (IOException e) {
//                    Log.d(LOG_TAG, "Task Execution Failed");
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //mListener.StopsResponse(null);
                mListener.HubsResponse(null, null);
            }

            return null;
        }
    }

    public void requestProgressUpdate() {
        mProgressUpdate = true;
//        Log.d("PROGRESS BAR", "requesting progress update");
    }
}
