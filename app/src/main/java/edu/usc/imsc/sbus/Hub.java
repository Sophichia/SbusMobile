package edu.usc.imsc.sbus;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Mengjia on 15/12/18.
 */
public class Hub {
    public Hub(){

    }

    public Hub(String id, double latitude, double longitude){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String id;
    public double latitude;
    public double longitude;

    public int stopNumber;
    public GeoPoint getGeoPoint() { return new GeoPoint(latitude, longitude);}
}
