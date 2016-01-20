package edu.usc.imsc.sbus.basicClass;

import org.osmdroid.util.GeoPoint;

/**
 * Created by danielCantwell on 3/24/15.
 */
public class Stop {

    public Stop() {

    }

    public Stop(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Stop(String id, String name, double latitude, double longitude, String hubId) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hubId = hubId;
    }

    public Stop(String id, String name){
        this.id=id;
        this.name=name;
    }

    private String id;
    private String stopHeadsign;
    private int stopSequence;
    private String name;
    private double latitude;
    private double longitude;
    private String hubId;

    public void setId(String id) {
        this.id = id;
    }

    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setHubId(String hubId) {
        this.hubId = hubId;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getId() {

        return id;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getHubId() {
        return hubId;
    }

    public int getSequence() {
        return sequence;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    private int sequence;
    private String arrivalTime;

    public GeoPoint getGeoPoint() {
        return new GeoPoint(latitude, longitude);
    }
}
