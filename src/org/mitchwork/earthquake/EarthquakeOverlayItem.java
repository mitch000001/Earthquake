package org.mitchwork.earthquake;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class EarthquakeOverlayItem extends OverlayItem {

    public EarthquakeOverlayItem(GeoPoint geoPoint, String s, String s1) {
        super(geoPoint, s, s1);
    }

}
