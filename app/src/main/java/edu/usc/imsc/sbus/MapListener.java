package edu.usc.imsc.sbus;

import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
/**
 * This interface use to listener the map status change. Like location or zoom level
 * Created by Mengjia on 15/12/21.
 */
public interface MapListener {
    /*
         * Called when a map is scrolled.
         */
    boolean onScroll(ScrollEvent event);

    /*
     * Called when a map is zoomed.
     */
    boolean onZoom(ZoomEvent event);
}
