package edu.usc.imsc.sbus.service;

import org.osmdroid.views.overlay.OverlayItem;

import edu.usc.imsc.sbus.R;
import edu.usc.imsc.sbus.basicClass.Stop;

/**
 * Created by danielCantwell on 4/19/15.
 */
public class StopOverlayItem extends OverlayItem {

    public Stop stop;
    public static final int iconId = R.drawable.ic_stop2;
    public static final int focusedIconId = R.drawable.ic_stop3;

    /**
     *
     * @param s         Stop data related to item
     */
    public StopOverlayItem(Stop s) {
        super("Stop", s.getName(), s.getGeoPoint());
        stop = s;
    }
}