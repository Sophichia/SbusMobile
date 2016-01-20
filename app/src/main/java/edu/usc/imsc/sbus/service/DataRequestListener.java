package edu.usc.imsc.sbus.service;

import android.graphics.drawable.Drawable;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.activity.MainActivity;
import edu.usc.imsc.sbus.basicClass.Halt;
import edu.usc.imsc.sbus.basicClass.Hub;
import edu.usc.imsc.sbus.basicClass.Stop;
import edu.usc.imsc.sbus.basicClass.Vehicle;

/**
 * Created by danielCantwell on 11/2/15.
 * Copyright (c) Cantwell Code 2015. All Rights Reserved
 */
public interface DataRequestListener {

    void CurrentTransitResponse(List<Vehicle> vehicles);

    void VehicleDelayResponse(Vehicle v, float seconds);

    void RouteResponse(List<GeoPoint> waypoints);

    void StopsResponse(List<Stop> stops);
    void HubsResponse(List<Hub> hubs, List<Stop> stops);

    void HaltResponse(List<Halt> halts,String stopId);

    /**
     * Created by Mengjia on 15/12/19.
     */



    /**
     * Created by danielCantwell on 11/16/15.
     * Copyright (c) Cantwell Code 2015. All Rights Reserved
     */
    interface MapClickListener {

        void onVehicleClick(Vehicle v);

        void onStopClick(Stop s);

        void onEmptyClick();

        void onHubClick(Hub h);

        void onStopLongPress(Stop s);
    }

    /**
     * This interface use to listener the map status change. Like location or zoom level
     * Created by Mengjia on 15/12/21.
     */
//    interface MapListener {
//        /*
//             * Called when a map is scrolled.
//             */
//        boolean onScroll(ScrollEvent event);
//
//        /*
//         * Called when a map is zoomed.
//         */
//        boolean onZoom(ZoomEvent event);
//    }

    /**
     * Created by danielCantwell on 11/19/15.
     * Copyright (c) Cantwell Code 2015. All Rights Reserved
     */
    abstract class MapOverlay {

        protected ItemizedIconOverlay<OverlayItem> mOverlay;
        protected MainActivity mContext;

        protected MapClickListener mListener;

        protected OverlayItem mActiveItem;
        protected Drawable mIcon;
        protected Drawable mActiveIcon;

        protected ArrayList<OverlayItem> mItems;
        protected boolean mHidden;

        public MapOverlay() {
            mItems = new ArrayList<>();
        }

        public void addItems(List<OverlayItem> items) {
            mItems.addAll(items);
            mOverlay.addItems(items);
        }


        public OverlayItem getActiveItem() {
            return mActiveItem;
        }

        public void removeActiveItem() {
            if (mActiveItem != null) mActiveItem.setMarker(mIcon);
            mActiveItem = null;
        }

        public void hideAllItems(){

        }
        public void clearItems() {
            mItems.clear();
            mOverlay.removeAllItems();
        }

        public void updateAllItems(List<OverlayItem> newItems) {
            mItems.clear();
            mOverlay.removeAllItems();
            addItems(newItems);
        }

    //    public void hideAllItems() {
    //        mOverlay.removeAllItems();
    //        mHidden = true;
    //    }

        public void hideItemsExceptActive() {
            mOverlay.removeAllItems();
            mHidden = true;
            mOverlay.addItem(mActiveItem);
        }

        public void showAllItems() {
            if (mHidden) {
                mOverlay.removeAllItems();
                mOverlay.addItems(mItems);
            }
            mHidden = false;
        }

        public Overlay getOverlay() {
            return mOverlay;
        }
    }

    /**
     * Created by danielCantwell on 4/15/15.
     */
    class MapThread extends Thread {

        private static final int SECONDS_PER_FRAME = 1;
        private MainActivity mainActivity;
        private boolean stop;

        public MapThread(MainActivity a) {
            mainActivity = a;
            stop = false;
        }

        @Override
        public void run() {

            while (!stop) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.displayMapVehicles();
                    }
                });

                try {
                    sleep(SECONDS_PER_FRAME * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopThread() {
            stop = true;
        }
    }

    /**
     * Created by danielCantwell on 11/4/15.
     * Copyright (c) Cantwell Code 2015. All Rights Reserved
     */
    enum RequestType {
        Local, Server
    }
}
