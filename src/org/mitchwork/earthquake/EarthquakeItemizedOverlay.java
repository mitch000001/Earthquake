package org.mitchwork.earthquake;


import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;

public class EarthquakeItemizedOverlay extends ItemizedOverlay<EarthquakeOverlayItem> {

    ArrayList<EarthquakeOverlayItem> quakeLocations;
    Cursor earthquakes;

    public EarthquakeItemizedOverlay(Cursor cursor, Drawable drawable) {
        super(boundCenterBottom(drawable));

        earthquakes = cursor;

        quakeLocations = new ArrayList<EarthquakeOverlayItem>();
        refreshQuakeLocations();
        earthquakes.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                refreshQuakeLocations();
            }
        });
    }

    private void refreshQuakeLocations() {
        if (earthquakes.moveToFirst())
            do {
                Double lat =
                   earthquakes.getFloat(EarthquakeProvider.LATITUDE_COLUMN) * 1E6;
                Double lng =
                   earthquakes.getFloat(EarthquakeProvider.LONGITUDE_COLUMN) *1E6;
                Double mag =
                   earthquakes.getDouble(EarthquakeProvider.MAGNITUDE_COLUMN);
                String magString = mag.toString();
                String snippet =
                   earthquakes.getString(EarthquakeProvider.DETAILS_COLUMN);

                GeoPoint geoPoint = new GeoPoint(lng.intValue(), lat.intValue());
                EarthquakeOverlayItem eoi = new EarthquakeOverlayItem(geoPoint, magString, snippet);
                addOverlay(eoi);

            } while (earthquakes.moveToNext());
    }

    @Override
    protected EarthquakeOverlayItem createItem(int i) {
        return quakeLocations.get(i);
    }

    @Override
    public int size() {
        return quakeLocations.size();
    }

    public void addOverlay(EarthquakeOverlayItem overlay) {
        quakeLocations.add(overlay);
        populate();
    }
}
