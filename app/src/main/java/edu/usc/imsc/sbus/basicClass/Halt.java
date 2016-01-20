package edu.usc.imsc.sbus.basicClass;

/**
 * Created by Mengjia on 15/12/29.
 */
public class Halt {
    private String route;
    private String time;
    public Halt(String route, String time){
        this.route = route;
        this.time = time;
    }

    public String getRoute() {
        return route;
    }

    public String getTime() {
        return time;
    }
}
