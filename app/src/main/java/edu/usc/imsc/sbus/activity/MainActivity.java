package edu.usc.imsc.sbus.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.usc.imsc.sbus.basicClass.WatchedItem;
import edu.usc.imsc.sbus.service.DataRequestListener;
import edu.usc.imsc.sbus.service.DatabaseHelper;
import edu.usc.imsc.sbus.service.HaltPopAdapter;
import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.service.HaltPopupItemInfoRequest;
import edu.usc.imsc.sbus.service.HubOverlayItem;
import edu.usc.imsc.sbus.service.MainActivityStopAdapter;
import edu.usc.imsc.sbus.service.StopOverlayItem;
import edu.usc.imsc.sbus.service.StopsOverlay;
import edu.usc.imsc.sbus.service.StopsRequest;
import edu.usc.imsc.sbus.service.TransitRequest;
import edu.usc.imsc.sbus.service.VehicleOverlay;
import edu.usc.imsc.sbus.service.VehicleOverlayItem;
import edu.usc.imsc.sbus.basicClass.Halt;
import edu.usc.imsc.sbus.basicClass.Hub;
import edu.usc.imsc.sbus.basicClass.Stop;
import edu.usc.imsc.sbus.basicClass.Vehicle;
import edu.usc.imsc.sbus.service.BuildRoadRequest;
import edu.usc.imsc.sbus.service.HubOverlay;


public class MainActivity extends BaseActivity implements LocationListener, DataRequestListener,
        DataRequestListener.MapClickListener, MapListener{

    private Location mLocation;

    private MapView mMap;
    private IMapController mMapController;
    private LocationManager mLocationManager;

    private VehicleOverlay mVehicleOverlay;
    private StopsOverlay mStopsOverlay;
    private StopsOverlay mActiveStopsOverlay;
    private HubOverlay mHubOverlay;
    private HubOverlay mActiveHubOverlay;
    private ItemizedIconOverlay mLocationOverlay;
    private OverlayItem mLocationItem;
    private Polyline mVehiclePath;

    private View vehicleInfoBox;
    private TextView vehicleName;
    private TextView stopName;
    private TextView stopTime;
    private TextView vehicleDelay;
    private TextView loadingVehiclesText;


    private View stopInfoBox;
    private View hubInfoBox;
    private TextView selectedStopName;
    private TextView selectedStopTime;
    private TextView selectedHubId;
    private TextView selectedHubStopNumber;
    private ImageButton stopInfoClose;
    private ImageButton vehicleInfoClose;
    private ImageButton hubInfoClose;

    private TableRow rowChangeLocation;
    private Button changeLocationButton;
    private ImageView mapCenterImage;
    private ImageButton locationCancelButton;

    //    private boolean bDefaultZoom = true;
    private int mZoomLevel = 16;
    private int mStopsFilterDistance; // units in meters
    private int mHubFilterDistance;
    private GeoPoint mFilterLocation = null;

    private List<Vehicle> mVehicles;
    private List<Stop> mStops;
    private List<Hub> mHubs;
    private List<Halt> mHalts;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressLocation;

    private boolean mShowingActiveStops = false;
    private boolean mShowingActiveHubs = false;
    private MapThread mapThread;

    private long mLastLocationUpdateTime;

    private Hub currentHub;

    //for popupWindows
    private PopupWindow popupWindow;
    private ListView lv_group;
    private List<Halt> groups;
    private View view;

    //for stopList
    private ArrayList<Stop> nearbyStopsList = new ArrayList<>();
    private MainActivityStopAdapter mMainActivityStopAdapter;
    private ListView mListView;
    private LinearLayout layout_lv;
    private boolean stopListViewClickTag = false;
    private Hub activeHub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mapThread = new MapThread(this);

        mLastLocationUpdateTime = Calendar.getInstance().getTimeInMillis();
        mStopsFilterDistance = mSharedPreferences.getInt("stopRange", 400);
        mHubFilterDistance = mSharedPreferences.getInt("stopRange", 16000);
        loadingVehiclesText = (TextView) findViewById(R.id.text_loading_vehicles);

        /* Initialize the map */
        mMap = (MapView) findViewById(R.id.map);
        MapTileProviderBasic provider = new MapTileProviderBasic(getApplicationContext());
        provider.setTileSource(TileSourceFactory.PUBLIC_TRANSPORT);
        TilesOverlay tilesOverlay = new TilesOverlay(provider, this.getBaseContext());
        mMap.getOverlays().add(tilesOverlay);

        /* Enable Zoom Controls */
        mMap.setMultiTouchControls(true);
        mMap.setMinZoomLevel(14);
        mMap.setMaxZoomLevel(17);

        /* Set a Default Map Point */
        mMapController = mMap.getController();
        mMapController.setZoom(mZoomLevel);
        mMapController.setCenter(new GeoPoint(34.0205, -118.2856));

        //init the stop ListView
        mListView = (ListView)this.findViewById(R.id.lv_stops);
        mMainActivityStopAdapter = new MainActivityStopAdapter(this,nearbyStopsList);
        mListView.setAdapter(mMainActivityStopAdapter);
        layout_lv = (LinearLayout)this.findViewById(R.id.layout_lv);
        layout_lv.setVisibility(View.GONE);
        //mListView.setVisibility(View.GONE);


        // Create and add a Stops Overlay
        mStopsOverlay = new StopsOverlay(this, this, StopsOverlay.Type.Normal);
        mActiveStopsOverlay = new StopsOverlay(this, this, StopsOverlay.Type.Active);
        mMap.getOverlays().add(mStopsOverlay.getOverlay());
        mMap.getOverlays().add(mActiveStopsOverlay.getOverlay());
        // Create and add Vehicle Overlay
        mVehicleOverlay = new VehicleOverlay(this, this);
        mMap.getOverlays().add(mVehicleOverlay.getOverlay());

        //Create and add Hub Overly
        mHubOverlay = new HubOverlay(this, this, HubOverlay.Type.Normal);
        mActiveHubOverlay = new HubOverlay(this, this, HubOverlay.Type.Active);
        mMap.getOverlays().add(mHubOverlay.getOverlay());
        mMap.getOverlays().add(mActiveHubOverlay.getOverlay());

        // Create Location Overlay
        mLocationItem = new OverlayItem("Location", "Location", new GeoPoint(34.0205, -118.2856));
        mLocationItem.setMarker(getResources().getDrawable(R.drawable.ic_action_location_found));
        List<OverlayItem> singleLocationItemList = new ArrayList<>();
        mLocationOverlay = new ItemizedIconOverlay<>(
                singleLocationItemList,
                getResources().getDrawable(R.drawable.ic_action_location_found),
                null,
                new DefaultResourceProxyImpl(this));
        mMap.getOverlays().add(mLocationOverlay);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        /* Set map center point if location exists */
        if (mLocation != null) {
            GeoPoint startPoint = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            mMapController.setCenter(startPoint);
        }

        mVehicles = new ArrayList<>();

        /****************************************
         UI Handling
         ****************************************/

        vehicleInfoBox = findViewById(R.id.vehicle_info);
        vehicleInfoBox.setVisibility(View.GONE);
        vehicleName = (TextView) findViewById(R.id.vehicle_name);
        stopName = (TextView) findViewById(R.id.stop_name);
        stopTime = (TextView) findViewById(R.id.stop_time);


        vehicleDelay = (TextView) findViewById(R.id.delay);
        vehicleInfoClose = (ImageButton) findViewById(R.id.vehicle_info_close);

        stopInfoBox = findViewById(R.id.stop_info);
        stopInfoBox.setVisibility(View.GONE);
        selectedStopName = (TextView) findViewById(R.id.selected_stop_name);
        selectedStopTime = (TextView) findViewById(R.id.selected_stop_time);
        stopInfoClose = (ImageButton) findViewById(R.id.stop_info_close);

        hubInfoBox = findViewById(R.id.hub_info);
        hubInfoBox.setVisibility(View.GONE);
        selectedHubId = (TextView) findViewById(R.id.selected_hub_name);
        selectedHubStopNumber = (TextView) findViewById(R.id.selected_hub_info);
        hubInfoClose = (ImageButton) findViewById(R.id.hub_info_close);

        rowChangeLocation = (TableRow) findViewById(R.id.row_change_location);
        changeLocationButton = (Button) findViewById(R.id.changeLocation);
        mapCenterImage = (ImageView) findViewById(R.id.mapCenter);
        locationCancelButton = (ImageButton) findViewById(R.id.locationCancel);



        /* Button Click Handling */

        vehicleInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVehicleOverlay.removeActiveItem();    // Deactivate Selected Vehicle
                mVehicles.clear();                     // Remove all the vehicles
                mVehicleOverlay.clearItems();          // Remove vehicles from the map
                resetVehicleInfoBox();                 // Remove the vehicle info box
                mMap.getOverlays().remove(mVehiclePath);
                removeActiveStops();                   // Remove the active stops
                mStopsOverlay.showAllItems();          // Show all stops
                mStopsOverlay.removeActiveItem();

                if (mShowingActiveStops) {
                    resetStopInfoBox();
                }
                displayHubInfo(currentHub.getId(), String.valueOf(currentHub.getStopNumber()));
                mapThread.stopThread();                // Stop trying to display the vehicles on the map
                mMap.invalidate();                     // Update the map
            }
        });

        stopInfoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stopListViewClickTag==false){
                    if (mShowingActiveStops) {
                        mActiveStopsOverlay.removeActiveItem();
                    } else {
                        mVehicles.clear();
                        mVehicleOverlay.clearItems();
                        mStopsOverlay.showAllItems();          // Show all stops
                        mStopsOverlay.removeActiveItem();
                        mapThread.stopThread();                // Stop trying to display the vehicles on the map
                    }
                    resetStopInfoBox();
                    displayHubInfo(currentHub.getId(), String.valueOf(currentHub.getStopNumber()));
                    mMap.invalidate();
                }
                else{//have click on the listView Item
                    resetStopInfoBox();
                    mHubOverlay.clearItems();
                    mStopsOverlay.clearItems();
                    if(mShowingActiveHubs){//if have showed active hub before
                        hubInfoBox.setVisibility(View.VISIBLE);
                        List<Stop> nearbyStops = filterStopsBelongHub(mStops,currentHub);
                        currentHub.setStopNumber(nearbyStops.size());
                        displayHubInfo(currentHub.getId(),String.valueOf(currentHub.getStopNumber()));
                        StopsResponse(nearbyStops);
                    }
                    else{

                        mHubOverlay.clearItems();
                        HubsResponse(mHubs, mStops);
                    }

                    if(mShowingActiveStops){
                        mActiveStopsOverlay.removeActiveItem();
                    }
                    else {
                        mVehicles.clear();
                        mVehicleOverlay.clearItems();
                        mStopsOverlay.showAllItems();          // Show all stops
                        mStopsOverlay.removeActiveItem();
                        mapThread.stopThread();                // Stop trying to display the vehicles on the map
                    }
                    mMap.invalidate();
                    stopListViewClickTag=false;

                }
                layout_lv.setVisibility(View.VISIBLE);
            }
        });

        hubInfoClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mShowingActiveHubs = false;
                mStopsOverlay.clearItems();
                mHubOverlay.showAllItems();
                mHubOverlay.removeActiveItem();
                resetHubInfoBox();
                GeoPoint currentLocation = (GeoPoint) mMap.getMapCenter();
                nearbyStopsList.clear();
                //Log.d("test", "current location is " + currentLocation.getLongitude() + "  " + currentLocation.getLatitude());
                //Log.d("test", "current size is " + filterNearbyStops(mStops, currentLocation).size());
                nearbyStopsList.addAll(filterNearbyStops(mStops, currentLocation));
                mMainActivityStopAdapter.notifyDataSetChanged();
                mMap.invalidate();
                HubsResponse(mHubs, mStops);
            }
        });

        changeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterLocation = (GeoPoint) mMap.getMapCenter();
                new StopsRequest(RequestType.Local).getAllStops(MainActivity.this, MainActivity.this);
                mapCenterImage.setVisibility(View.GONE);
                rowChangeLocation.setVisibility(View.GONE);
            }
        });

        locationCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapCenterImage.setVisibility(View.GONE);
                rowChangeLocation.setVisibility(View.GONE);
            }
        });

        /**
         * this listner is used for the stop ListView
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stop clickStop = (Stop) parent.getAdapter().getItem(position);
                Toast.makeText(MainActivity.this, "click" + clickStop.getName(), Toast.LENGTH_SHORT).show();
                clickOnStopListViewItem(clickStop);
            }
        });

        /**
         * this listener is used to let the hub changed when tha map moved.
         * Created by Mengjia on 15/12/21.
         */
        mMap.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent scrollEvent) {
                //if no infoBoxes opened then show the hubs for current location
                if (!vehicleInfoBox.isShown() && !stopInfoBox.isShown() && !hubInfoBox.isShown()) {
                    GeoPoint currentLocation = (GeoPoint) mMap.getMapCenter();
                    //Log.d("Scroll test","now position is " + currentLocation.getLatitude() +" "+ currentLocation.getLongitude());
                    mFilterLocation = currentLocation;
                    if (mHubs != null && mStops != null) {
                        mActiveHubOverlay.clearItems();
                        mHubOverlay.clearItems();       //clear hubs before
                        HubsResponse(mHubs, mStops);
                        //pay an attention here that cannot use xx=filter() statement
                        //cause the adapter will store the reference to another object.
                        nearbyStopsList.clear();
                        nearbyStopsList.addAll(filterNearbyStops(mStops, currentLocation));
                        mMainActivityStopAdapter.notifyDataSetChanged();
                        layout_lv.setVisibility(View.VISIBLE);

                    }

                }
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent zoomEvent) {
                if (!vehicleInfoBox.isShown() && !stopInfoBox.isShown() && !hubInfoBox.isShown()) {
                    if (mHubs != null && mStops != null) {
                        if (mMap.getZoomLevel() >= 15) {
                            mActiveHubOverlay.clearItems();
                            mHubOverlay.clearItems();       //clear hubs before
                            HubsResponse(mHubs, mStops);
                        } else {// for zoom level less than 15, we choose not to show the hub because it is too dense
                            mHubOverlay.clearItems();
                        }
                    }
                }
                return false;
            }
        });
//        mMap.setMapListener(new MapListener() {
//            @Override
//            public boolean onScroll(ScrollEvent scrollEvent) {
//                return false;
//            }
//
//            @Override
//            public boolean onZoom(final ZoomEvent zoomEvent) {
//
//                if (mShowingActiveStops) {
//                    mZoomLevel = zoomEvent.getZoomLevel();
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            mActiveStopsOverlay.updateIconSize(zoomEvent.getZoomLevel());
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mMap.invalidate();
//                                }
//                            });
//                        }
//                    }.run();
//                }
//                return false;
//            }
//        });

        /* Make sure the user has location turned on */
        mProgressLocation = new ProgressDialog(this);
        mProgressLocation.setMessage("Searching for location. Please ensure GPS is on.");
        mProgressLocation.setCancelable(false);

        if (mLocation == null) {
            mProgressLocation.show();
        } else {
            GeoPoint startPoint = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            mMapController.setCenter(startPoint);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        if (mSharedPreferences.contains("searchLatitude") && mSharedPreferences.contains("searchLongitude")) {
            float lat = mSharedPreferences.getFloat("searchLatitude", 0);
            float lon = mSharedPreferences.getFloat("searchLongitude", 0);
            mFilterLocation = new GeoPoint(lat, lon);
            mSharedPreferences.edit().remove("searchLatitude").commit();
            mSharedPreferences.edit().remove("searchLongitude").commit();

            new StopsRequest(RequestType.Local).getAllStops(this, this);
            mMapController.setCenter(new GeoPoint(lat, lon));
        }
        /*
        Log.d("nearby stops test","work on onResume()");
        if(mStops!=null){
             nearbyStopsList = filterNearbyStops(mStops,new GeoPoint(34.0205, -118.2856));

        }
        else{
            Log.d("nearby stops test","set nearby Stops to 0");
            nearbyStopsList.add(new Stop("None Id","No Nearby Stops"));
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(this);
    }

    /*
     * *************************************************************************
     *                      MENU / APP BAR FUNCTIONS
     * *************************************************************************
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        /*
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location:
                if (mLocation != null) {
                    mMapController.setCenter(new GeoPoint(mLocation));
                } else {
                    Toast.makeText(this, "Current Location Not Known", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.watchedItem://change into a new activity show the watchedItem
                //rowChangeLocation.setVisibility(View.VISIBLE);
                //mapCenterImage.setVisibility(View.VISIBLE);
                startActivity(new Intent(MainActivity.this, WatchedItemActivity.class));
                return true;
            case R.id.search:
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
     * *************************************************************************
     *                      MAP DISPLAY FUNCTIONS
     * *************************************************************************
     */


    /**
     * Add the list of vehicles to the map overlay
     */
    public void displayMapVehicles() {
        findViewById(R.id.text_loading_vehicles).setVisibility(View.GONE);

        VehicleOverlayItem activeItem = (VehicleOverlayItem) mVehicleOverlay.getActiveItem();
        Vehicle activeVehicle = new Vehicle();
        if (activeItem != null) {
            activeVehicle = activeItem.vehicle;
        }

        List<OverlayItem> items = new ArrayList<>();
        for (Vehicle v : mVehicles) {
            if (v.getCurrentLocation() != null) {
                VehicleOverlayItem vItem = new VehicleOverlayItem(v);
                int zoomLevel = mMap.getZoomLevel();
                float fontSize;
                switch (zoomLevel) {
                    case 12: fontSize = 40f; break;
                    case 13: fontSize = 45f; break;
                    case 14: fontSize = 50f; break;
                    case 15: fontSize = 55f; break;
                    case 16: fontSize = 60f; break;
                    case 17: fontSize = 65f; break;
                    default: fontSize = 70f; break;
                }
                if (v.getTripId().equals(activeVehicle.getTripId())) {
//                    vItem.setMarker(vItem.getActiveIcon(this));
                    Drawable icon = getResources().getDrawable(VehicleOverlayItem.focusedIconId);
                    Drawable textIcon = VehicleOverlayItem.createMarkerIcon(icon, v.getRouteId(), VehicleOverlayItem.Type.Active, fontSize);
                    vItem.setMarker(textIcon);
                    v.determineCurrentLocationIndex();
                    displayVehicleInfo(v.getStopHeadsign(), v.getStops().get(v.getNextStop()).getName(), v.getStops().get(v.getNextStop()).getArrivalTime());

                } else {
//                    vItem.setMarker(vItem.getNormalIcon(this));
                    Drawable icon = getResources().getDrawable(VehicleOverlayItem.iconId);
                    Drawable textIcon = VehicleOverlayItem.createMarkerIcon(icon, v.getRouteId(), VehicleOverlayItem.Type.Normal, fontSize);
                    vItem.setMarker(textIcon);
                }
//                    vItem.setMarker(getResources().getDrawable(VehicleOverlayItem.focusedIconId));
                items.add(vItem);
            }
        }

        mVehicleOverlay.updateAllItems(items);
        mMap.invalidate();
    }

    private void displayVehicleRoute(final Vehicle v) {
        new BuildRoadRequest(this).execute(v);
    }

    private void displayActiveStops(Vehicle v) {
        List<OverlayItem> sItems = new ArrayList<>();
        for (Stop s : v.getStops()) {
            StopOverlayItem sItem = new StopOverlayItem(s);
            sItem.setMarker(getResources().getDrawable(R.drawable.stop_large));
            sItems.add(sItem);
        }
        mActiveStopsOverlay.updateAllItems(sItems);
        mShowingActiveStops = true;
        mMap.invalidate();
    }



    /*
     * **************************************************************************
     *                      Location Listener Overrides
     * **************************************************************************
     */

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        GeoPoint myLocation = new GeoPoint(mLocation);
        if (mProgressLocation.isShowing()) {

            mProgressLocation.dismiss();
            mMapController.setCenter(myLocation);

            /* Start loading the stops, either from server or from sqlite */
            new StopsRequest(RequestType.Local).getAllStops(this, this);

            updateLocation(myLocation);
        } else if (mLastLocationUpdateTime < (Calendar.getInstance().getTimeInMillis() - 10000)) {
            updateLocation(myLocation);
        }
    }

    /**
     * Update the user's location
     *
     * @param myLocation - the user's current location
     */
    private void updateLocation(GeoPoint myLocation) {
        mLocationOverlay.removeAllItems();
        mLocationItem = new OverlayItem("Location", "Location", myLocation);
        mLocationOverlay.addItem(mLocationItem);
        mMap.invalidate();
        mLastLocationUpdateTime = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /*
     * ***************************************************************************
     *                      Data Request Overrides
     * ***************************************************************************
     */

    @Override
    public void StopsResponse(List<Stop> stops) {
        // filter stops based on location
        //GeoPoint g = (mFilterLocation != null) ? mFilterLocation : new GeoPoint(mLocation);
        //List<Stop> nearbyStops = filterStopsBelongHub(stops, mActiveHubOverlay.getActiveItem().);

        // create overlay items
        List<OverlayItem> stopOverlayItems = new ArrayList<>();
        for (Stop s : stops) {
            stopOverlayItems.add(new StopOverlayItem(s));
        }

        // add all overlay items
        mStopsOverlay.addItems(stopOverlayItems);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingVehiclesText.setVisibility(View.INVISIBLE);
                mMap.invalidate();
            }
        });
    }

    @Override
    public void HubsResponse(List<Hub> hubs, List<Stop> stops) {
        //filter hubs based on location
        mStops = stops;
        // Log.d("stop test ","stops in HubsResponse is " + mStops.size());
        mHubs = hubs;
        GeoPoint g = (mFilterLocation !=null) ? mFilterLocation: new GeoPoint(mLocation);
        List<Hub> nearbyHubs = filterNearbyHubs(hubs,g);
        //Log.d("list test","nearby hub size is"+nearbyHubs.size());
        //create overlay items
        List<OverlayItem> hubOverlayItems = new ArrayList<>();
        for(Hub h : nearbyHubs){
            hubOverlayItems.add(new HubOverlayItem(h));
        }

        //add all overlay items
        mHubOverlay.addItems(hubOverlayItems);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingVehiclesText.setVisibility(View.INVISIBLE);
                mMap.invalidate();
            }
        });
    }

    @Override
    public void CurrentTransitResponse(List<Vehicle> vehicles) {
        mVehicles = vehicles;
        if (mVehicles != null && !mVehicles.isEmpty()) {

            for (Vehicle v : mVehicles) {
                Collections.sort(v.getStops(), new StopSequenceComparator());
            }

            // This will update the buses every 5 seconds

            mapThread = new MapThread(MainActivity.this);
            mapThread.start();

        } else {
            Toast.makeText(this, "Vehicles Not Found", Toast.LENGTH_SHORT).show();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingVehiclesText.setVisibility(View.INVISIBLE);
                mMap.invalidate();
            }
        });
    }

    @Override
    public void VehicleDelayResponse(Vehicle v, float seconds) {
        Calendar c = Calendar.getInstance();

        int prediction = (int) seconds;

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        int currentTime = (hour * 3600) + (minute * 60) + second;

        String nextStopArrivalTime = v.getStops().get(v.getNextStop()).getArrivalTime();
        int nextHour = Integer.valueOf(nextStopArrivalTime.substring(0, 2));
        int nextMinute = Integer.valueOf(nextStopArrivalTime.substring(3, 5));
        int nextSecond = Integer.valueOf(nextStopArrivalTime.substring(6, 8));

        int nextTime = (nextHour * 3600) + (nextMinute * 60) + nextSecond;

        int timeDifference = nextTime - currentTime;
        final int delay = prediction - timeDifference;

        if (delay > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vehicleDelay.setText("+ " + delay + "s");
                }
            });
        }
        Log.d("Delay", String.valueOf(delay));
    }

    @Override
    public void RouteResponse(List<GeoPoint> waypoints) {
        if (mVehiclePath == null) {
            mMap.getOverlays().remove(mVehiclePath);
        }
        mVehiclePath = new Polyline(MainActivity.this);
        mVehiclePath.setPoints(waypoints);
        mVehiclePath.setColor(Color.YELLOW);
        mVehiclePath.setWidth(5);

        mMap.getOverlays().remove(mVehicleOverlay);
        mMap.getOverlays().remove(mStopsOverlay);
        mMap.getOverlays().remove(mActiveStopsOverlay);
        mMap.getOverlays().add(mVehiclePath);
        mMap.getOverlays().add(mStopsOverlay.getOverlay());
        mMap.getOverlays().add(mActiveStopsOverlay.getOverlay());
        mMap.getOverlays().add(mVehicleOverlay.getOverlay());


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.invalidate();
            }
        });
    }

    public void HaltResponse(List<Halt> halts,String stopId){
        groups = halts;
        showWindow(getWindow().getDecorView(),stopId);
    }
    /*
     * ***************************************************************************
     *                      Map Item Clicks
     * ***************************************************************************
     */

    @Override
    public void onVehicleClick(Vehicle v) {

        resetVehicleInfoBox();              // Hide details for previously selected vehicle
        resetStopInfoBox();
        resetHubInfoBox();
        removeActiveStops();                // Remove Active Stops
//        mStopsOverlay.hideAllItems();       // Temporarily hide all stops on the map
        // Show the details for the selected vehicle
        displayVehicleInfo(v.getStopHeadsign(), v.getStops().get(v.getNextStop()).getName(), v.getStops().get(v.getNextStop()).getArrivalTime());
        displayVehicleRoute(v);             // Show the route the vehicle will take
        displayActiveStops(v);              // Show the stops related to the selected vehicle

        mMap.invalidate();
    }

    @Override
    public void onStopClick(Stop s) {
        layout_lv.setVisibility(View.GONE);
        if (!mShowingActiveStops) {                    // If clicking on a normal stop
            mVehicleOverlay.clearItems();           // Hide all vehicles on the map
            mMap.getOverlays().remove(mVehiclePath);// Hide the active vehicle route

            resetVehicleInfoBox();                  // Hide details for the previously selected vehicle

            new TransitRequest().getStopTransit(this, this, s.getId());  // Show the vehicles that pass through the stop
            loadingVehiclesText.setText("Searching for vehicles that stop here ...");
            loadingVehiclesText.setVisibility(View.VISIBLE);
//            mStopsOverlay.hideAllItems();       // Temporarily hide all stops on the map
            mStopsOverlay.hideItemsExceptActive();
        }
        resetHubInfoBox();
        resetStopInfoBox();                     // Hide details for the previously selected stop
        displayStopInfo(s.getName(), s.getArrivalTime()); // Show information for the selected stop
        //Log.d("test","arrival Time is "+ s.getArrivalTime()); result is null here
        mMap.invalidate();


    }

    @Override
    public void onHubClick(Hub h){
        if(!mShowingActiveHubs){                 //If clicking on a normal hub
            currentHub = h;
            mShowingActiveHubs = true;           //mark true
            mVehicleOverlay.clearItems();       //Hide all vehicles on the map
            mMap.getOverlays().remove(mVehiclePath);//Hide the active vehicle route
            resetVehicleInfoBox();                //Hide details for the previously selected vehicle
            mHubOverlay.clearItems();
            //show the stops belong to this hub
            List<Stop> nearbyStops = filterStopsBelongHub(mStops, h);
            currentHub.setStopNumber(nearbyStops.size());
            nearbyStopsList.clear();                  //update stopListView
            nearbyStopsList.addAll(nearbyStops);
            mMainActivityStopAdapter.notifyDataSetChanged();
            layout_lv.setVisibility(View.VISIBLE);
            displayHubInfo(h.getId(), String.valueOf(h.getStopNumber()));
            StopsResponse(nearbyStops);
        }
        else{
            currentHub = null;
            mShowingActiveHubs = false;
            mStopsOverlay.clearItems();       //hide all the stop
            mHubOverlay.showAllItems();
        }
        mMap.invalidate();
    }

    public void onStopLongPress(Stop s){
        HaltPopupItemInfoRequest popRequest = new HaltPopupItemInfoRequest();
        popRequest.getPopupItem(s.getId(),this);

    }
    @Override
    public void onEmptyClick() {

    }

    @Override
    public boolean onScroll(ScrollEvent event){
        Log.d("scroll test", "the map has moved");
        return true;
    }

    @Override
    public boolean onZoom(ZoomEvent event){
        return true;
    }

    /****************************************************************************
     *                       Helper Functions                                 ***
     ****************************************************************************/


    /**
     * Filter Nearby Stops
     *
     * @param stops
     * @return - a list of stops that are within a specified distance to the user
     */
    ArrayList<Stop> filterNearbyStops(List<Stop> stops, GeoPoint g) {
        ArrayList<Stop> nearbyStops = new ArrayList<>();
        for (Stop s : stops) {
            if (stopIsWithinXMiles(s, mStopsFilterDistance, g)) {
                nearbyStops.add(s);
            }
        }
        //Log.d("list test","nearbyStops size is "+nearbyStops.size());
        return nearbyStops;
    }

    /**
     * @param s - The stop that you want to measure the distance to
     * @param x - The distance (in meters) you want to compare to
     * @return - true/false if the distance to the stop is within the range of x
     * If the stop location or user location is null, this returns true
     */
    boolean stopIsWithinXMiles(Stop s, int x, GeoPoint g) {
        GeoPoint stopLocation = new GeoPoint(s.getLatitude(), s.getLongitude());

        if (stopLocation != null && g != null) {
            if (g.distanceTo(stopLocation) < x) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    //find stops which belong to the hub
    public List<Stop> filterStopsBelongHub(List<Stop> stops, Hub h){
        List<Stop> belongToThisHub = new ArrayList<>();
        for(Stop s :stops){
            if(s.getHubId().equals(h.getId())){
                belongToThisHub.add(s);
            }
        }
        return belongToThisHub;
    }
    public List<Hub> filterNearbyHubs(List<Hub> hubs, GeoPoint g){
        List<Hub> nearbyHubs = new ArrayList<>();
        for(Hub h : hubs){
            if(hubIsWithinXMiles(h, mHubFilterDistance/mMap.getZoomLevel(), g)){
                nearbyHubs.add(h);
            }
        }
        return nearbyHubs;
    }

    boolean hubIsWithinXMiles(Hub h, int x, GeoPoint g){
        GeoPoint hubLocation = new GeoPoint(h.getLatitude(), h.getLongitude());

        if(hubLocation != null && g !=null){
            if(g.distanceTo(hubLocation) < x){
                return true;
            }
            else
                return false;
        }
        else
            return true;
    }
    private void resetVehicleInfoBox() {
        vehicleInfoBox.setVisibility(View.GONE);
        vehicleName.setText("Vehicle Name");
        stopName.setText("Stop Name");
        stopTime.setText("Stop Time");
        vehicleDelay.setText("");
    }

    private void displayVehicleInfo(String vName, String nextStop, String arrivalTime) {
        vehicleName.setText(vName);
        stopName.setText(nextStop);
        stopTime.setText(arrivalTime);
        vehicleInfoBox.setVisibility(View.VISIBLE);
    }

    private void resetStopInfoBox() {
        stopInfoBox.setVisibility(View.GONE);
        selectedStopName.setText("Stop Name");
        selectedStopTime.setText("Arrival Time");
    }

    private void displayStopInfo(String name, String arrivalTime) {
        selectedStopName.setText(name);
        selectedStopTime.setText(arrivalTime);
        stopInfoBox.setVisibility(View.VISIBLE);
    }

    private void resetHubInfoBox(){
        hubInfoBox.setVisibility(View.GONE);
        selectedHubId.setText("Hub Id");
        selectedHubStopNumber.setText("Stop Number");
    }

    private void displayHubInfo(String hubId, String stopNumber){
        selectedHubId.setText("Hub "+ hubId + " has ");
        selectedHubStopNumber.setText(stopNumber + " Stops");
        hubInfoBox.setVisibility(View.VISIBLE);
    }

    private void removeActiveStops() {
        mActiveStopsOverlay.clearItems();
        mShowingActiveStops = false;
    }

    private class StopSequenceComparator implements Comparator<Stop> {
        @Override
        public int compare(Stop o1, Stop o2) {
            return o1.getStopSequence() - o2.getStopSequence();
        }
    }

    /**
     * used for popupWindow
     * @param parent
     * created by Mengjia
     */
    private void showWindow(View parent, final String stopId){
        if(popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.halt_pop_list, null);

            lv_group = (ListView) view.findViewById(R.id.lvGroup);

            HaltPopAdapter popAdapter = new HaltPopAdapter(this, groups);
            lv_group.setAdapter(popAdapter);

            popupWindow = new PopupWindow(view, 800, 700);
        }
            final String mStopId = stopId;
            lv_group.setAdapter(null);//clear the old information
            HaltPopAdapter popAdapter = new HaltPopAdapter(this, groups);
            lv_group.setAdapter(popAdapter);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);

            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            int xPos = windowManager.getDefaultDisplay().getWidth()/2-popupWindow.getWidth()/2;
            //popupWindow.showAsDropDown(parent, xPos, 0);
            popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
            //the item click event
            lv_group.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    //search the number of now watched Item
                    DatabaseHelper dbh = new DatabaseHelper(MainActivity.this);
                    long numberOfWatched = dbh.retrieveHaltsNumber();
                    if(numberOfWatched>=5){//watched Item fulled
                        Toast.makeText(MainActivity.this, "The Watched Item Number Is Fulled",Toast.LENGTH_SHORT).show();
                    }
                    else{//add the watched Item to the database
                        String stopName = dbh.retrieveStopName(mStopId);
                        SQLiteDatabase db = dbh.beginWriting();
                        dbh.insertHalt(db,mStopId,stopName,groups.get(position));
                        Toast.makeText(MainActivity.this, "Successfully Add The Watchable-Item",Toast.LENGTH_SHORT).show();
                        dbh.endWriting(db);
                        //after successfully added watched Item, set alert and notification
                        setAlarm();
                    }
                    dbh.close();
                }
            });



    }
    public void setAlarm(){
        Intent intent = new Intent("ELITOR_CLOCK");
        intent.putExtra("msg", "your watched Item 231231 is coming soon");

        PendingIntent pi = PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);

        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);
    }

    /**
     * called when click on the stopListView Item
     */
    public void clickOnStopListViewItem(Stop s){
        stopListViewClickTag = true;
        if(!mShowingActiveHubs){//if haven't showing hub
            //hide all the hub
            //Log.d("test","want to clear hub Items");
            mHubOverlay.clearItems();
            List<OverlayItem> stopTmpOverlayItem = new ArrayList<>();
            stopTmpOverlayItem.add(new StopOverlayItem(s));

            mStopsOverlay.addItems(stopTmpOverlayItem);
            displayStopInfo(s.getName(), s.getArrivalTime());//show the stop info
            mMap.invalidate();
        }
        else{// if have showed the hub, then just like the normal click Stop
            resetHubInfoBox();
            mStopsOverlay.clearItems();
            List<OverlayItem> stopTmpOverlayItem = new ArrayList<>();
            stopTmpOverlayItem.add(new StopOverlayItem(s));
            mStopsOverlay.addItems(stopTmpOverlayItem);
            displayStopInfo(s.getName(), s.getArrivalTime());
            mMap.invalidate();
            //Log.d("test", " do not want to clear hub Items");
        }
        layout_lv.setVisibility(View.GONE);


    }
}
