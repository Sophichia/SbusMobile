package edu.usc.imsc.sbus.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import edu.usc.imsc.sbus.service.DataRequestListener;
import edu.usc.imsc.sbus.basicClass.Halt;
import edu.usc.imsc.sbus.basicClass.Hub;
import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Stop;
import edu.usc.imsc.sbus.service.StopsRequest;
import edu.usc.imsc.sbus.basicClass.Vehicle;


public class WelcomeActivity extends Activity implements DataRequestListener {

    private SharedPreferences sp;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mProgress = (ProgressBar) findViewById(R.id.progressBar);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getBoolean("firstOpen", true) || stopsNeedRefresh()) {
            Log.d("Welcome", "First Open");
            sp.edit().putBoolean("firstOpen", false).commit();
            StopsRequest sr = new StopsRequest(RequestType.Server);
            sr.requestProgressUpdate();
            sr.getAllStops(this, this);
        } else {
            Log.d("Welcome", "Already Opened");
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean stopsNeedRefresh() {
        // Call server to check if a refresh is needed
        return false;
    }

    @Override
    public void CurrentTransitResponse(List<Vehicle> vehicles) {

    }

    @Override
    public void VehicleDelayResponse(Vehicle v, float seconds) {

    }

    @Override
    public void RouteResponse(List<GeoPoint> waypoints) {

    }
    @Override
    public void HaltResponse(List<Halt> halts,String stopId){

    }
    @Override
    public void StopsResponse(List<Stop> stops) {
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void HubsResponse(List<Hub> hub, List<Stop> stops){
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
    public void setProgressMax(int max) {
        mProgress.setMax(max);
    }

    public void setProgressCurrent(int current) {
        mProgress.setProgress(current);
        Log.d("PROGRESS BAR", "updating progress to " + String.valueOf(current));
    }
}
