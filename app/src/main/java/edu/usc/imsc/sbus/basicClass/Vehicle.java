package edu.usc.imsc.sbus.basicClass;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.usc.imsc.sbus.basicClass.Stop;

/**
 * Created by danielCantwell on 3/17/15.
 */
public class Vehicle {

    boolean hasFocus;

    private String routeId;
    private String serviceId;
    private String shapeId;
    private String tripId;

    private String routeLongName;
    private String routeShortName;
    private String stopHeadsign;

    private String arrivalTime;
    private String routeColor;

    private List<Stop> stops;
    private List<GeoPoint> waypoints;

    private int currentLocationIndex;
    private int prevStop, nextStop;

    public Vehicle() {
        stops = new ArrayList<>();
        hasFocus = false;
        currentLocationIndex = 0;
        prevStop = 0;
        nextStop = 0;

        tripId = null;
        arrivalTime = null;
        stopHeadsign = null;
        routeId = null;
        serviceId = null;
        shapeId = null;
        routeShortName = null;
        routeLongName = null;
        routeColor = null;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public List<GeoPoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<GeoPoint> waypoints) {
        this.waypoints = waypoints;
    }

    public int getCurrentLocationIndex() {
        return currentLocationIndex;
    }

    public void setCurrentLocationIndex(int currentLocationIndex) {
        this.currentLocationIndex = currentLocationIndex;
    }

    public int getPrevStop() {
        return prevStop;
    }

    public void setPrevStop(int prevStop) {
        this.prevStop = prevStop;
    }

    public int getNextStop() {
        return nextStop;
    }

    public void setNextStop(int nextStop) {
        this.nextStop = nextStop;
    }

    public void determineCurrentLocationIndex() {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(Calendar.getInstance().getTime());

        int i;
        for (i = currentLocationIndex; i < stops.size(); i++) {
            // if the current time is greater than the arrival time
            if (stops.get(i).getArrivalTime().compareTo(time) < 0) break;
            /*
            string.compareTo(argument)
            returns:
            The value 0 if the argument is a string lexicographically equal to this string;
            a value less than 0 if the argument is a string lexicographically greater than this string;
            and a value greater than 0 if the argument is a string lexicographically less than this string.
             */

        }

        if (i == 0 || i == stops.size()) return;

        currentLocationIndex = i - 1;
        prevStop = currentLocationIndex;
        nextStop = i;
    }

    public GeoPoint getCurrentLocation() {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(Calendar.getInstance().getTime());

        int i;
        for (i = currentLocationIndex; i < stops.size(); i++) {
            // if the arrival time is greater than the current time
            if (stops.get(i).getArrivalTime().compareTo(time) > 0) break;
            /*
            string.compareTo(argument)
            returns:
            The value 0 if the argument is a string lexicographically equal to this string;
            a value less than 0 if the argument is a string lexicographically greater than this string;
            and a value greater than 0 if the argument is a string lexicographically less than this string.
             */
        }

        if (i == 0 || i == stops.size()) return null;

        currentLocationIndex = i - 1;
        prevStop = i - 1;
        nextStop = i;

        stopHeadsign = stops.get(nextStop).getStopHeadsign();

        String preTime = stops.get(i - 1).getArrivalTime();
        String nextTime = stops.get(i).getArrivalTime();

        int hourDif_NextPrev    = Integer.parseInt(nextTime.substring(0, 2)) - Integer.parseInt(preTime.substring(0, 2));
        int minuteDif_NextPrev  = Integer.parseInt(nextTime.substring(3, 5)) - Integer.parseInt(preTime.substring(3, 5));
        float timeDif_NextPrev  = (hourDif_NextPrev * 60 + minuteDif_NextPrev) * 60 - 10;

        int hourDif_NextCurrent    = Integer.parseInt(nextTime.substring(0, 2)) - Integer.parseInt(time.substring(0, 2));
        int minuteDif_NextCurrent  = Integer.parseInt(nextTime.substring(3, 5)) - Integer.parseInt(time.substring(3, 5));
        float timeDif_NextCurrent  = (hourDif_NextCurrent * 60 + minuteDif_NextCurrent) * 60 - Integer.parseInt(time.substring(6, 8));

        float fractionTime = timeDif_NextPrev != 0 ? timeDif_NextCurrent / timeDif_NextPrev : 0;

        double lat, lon;

        if (timeDif_NextPrev - timeDif_NextCurrent < 0) {
            lat = stops.get(i - 1).getLatitude();
            lon = stops.get(i - 1).getLongitude();
        } else {
            lat = timeDif_NextPrev != 0 ? stops.get(i).getLatitude() - fractionTime * (stops.get(i).getLatitude() - stops.get(i - 1).getLatitude()) : stops.get(i).getLatitude();
            lon = timeDif_NextPrev != 0 ? stops.get(i).getLongitude() - fractionTime * (stops.get(i).getLongitude() - stops.get(i - 1).getLongitude()) : stops.get(i).getLongitude();
        }

        return new GeoPoint(lat, lon);
    }
}
