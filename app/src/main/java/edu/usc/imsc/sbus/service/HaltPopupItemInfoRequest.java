package edu.usc.imsc.sbus.service;

import android.os.AsyncTask;

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

import edu.usc.imsc.sbus.basicClass.Halt;
import edu.usc.imsc.sbus.dao.ServerStatics;

/**
 * Created by Mengjia on 16/1/19.
 */
public class HaltPopupItemInfoRequest {
    private String mStopId;
    private DataRequestListener mListener;
    public void getPopupItem(String stopId, DataRequestListener listener){
        mStopId = stopId;
        mListener = listener;
        GetPopupItem task = new GetPopupItem();
        task.execute();
    }

    private class GetPopupItem extends AsyncTask<Void, Void, Void> {
        private List<Halt> mPopupItems;

        @Override
        protected  Void doInBackground(Void... params){
            mPopupItems = new ArrayList<>();

            //get the halt information
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(ServerStatics.SearchPopupItemInfor(mStopId));

            try{
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader streamReader = new BufferedReader(new InputStreamReader(content));
                StringBuilder stringBuilder = new StringBuilder();

                String inputStr;
                while((inputStr = streamReader.readLine())!=null)
                    stringBuilder.append(inputStr);

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONArray haltArray = jsonObject.getJSONArray("message");
                Halt halt;

                for(int i = 0; i<haltArray.length(); i++){
                    JSONArray tempHalt = haltArray.getJSONArray(i);
                    Object haltRoute = tempHalt.get(2);
                    Object haltArriveTime = tempHalt.get(1);
                    //Object haltStopName = tempHalt.get(2);

                    halt = new Halt(haltRoute.toString(),haltArriveTime.toString());
                    // Log.d("halt test","the hatlTripId is "+ haltTripId.toString()+"  the time is "+ haltArriveTime.toString());
                    mPopupItems.add(halt);
                }
            } catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        protected  void onPostExecute(Void result){
            super.onPostExecute(result);
            mListener.HaltResponse(mPopupItems,mStopId);
        }

    }
}
