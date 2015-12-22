package edu.usc.imsc.sbus;

/**
 * Created by danielCantwell on 11/16/15.
 * Copyright (c) Cantwell Code 2015. All Rights Reserved
 */
public interface MapClickListener {

    void onVehicleClick(Vehicle v);

    void onStopClick(Stop s);

    void onEmptyClick();

    void onHubClick(Hub h);
}
