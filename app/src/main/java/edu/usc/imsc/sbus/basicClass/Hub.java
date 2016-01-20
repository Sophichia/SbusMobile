package edu.usc.imsc.sbus.basicClass;

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

    private String id;
    private double latitude;
    private double longitude;

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    private int stopNumber;
    public GeoPoint getGeoPoint() { return new GeoPoint(latitude, longitude);}

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }
}
