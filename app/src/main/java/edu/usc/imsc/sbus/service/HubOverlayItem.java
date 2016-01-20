package edu.usc.imsc.sbus.service;

import org.osmdroid.views.overlay.OverlayItem;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Hub;

/**
 * Created by Mengjia on 16/1/19.
 */
public class HubOverlayItem extends OverlayItem {

    public Hub hub;
    public static final int iconId = R.drawable.ic_hub;
    public static final int focusedIconId = R.drawable.ic_hub;

    public HubOverlayItem(Hub h) {
        super("Hub", h.getId(), h.getGeoPoint());
        this.hub = h;
    }
}
