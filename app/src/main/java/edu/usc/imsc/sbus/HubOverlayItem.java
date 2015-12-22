package edu.usc.imsc.sbus;

import org.osmdroid.views.overlay.OverlayItem;

/**
 * Created by Mengjia on 15/12/19.
 */
public class HubOverlayItem extends OverlayItem {

    public Hub hub;
    public static final int iconId = R.drawable.ic_bus_stop;
    public static final int focusedIconId = R.drawable.ic_bus_stop_selected;

    public HubOverlayItem(Hub h) {
        super("Hub", h.id, h.getGeoPoint());
        this.hub = h;
    }
}
