package edu.usc.imsc.sbus.basicClass;

/**
 * Created by Mengjia on 16/1/3.
 */
public class WatchedItem {
    private String stopId;
    private String stopName;
    private String route;
    private String time;

    public WatchedItem(String stopId,String stopName,String route,String time){
        this.stopId = stopId;
        this.stopName = stopName;
        this.route = route;
        this.time = time;
    }
    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
