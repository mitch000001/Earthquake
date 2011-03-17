package org.mitchwork.earthquake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class EarthquakeMap extends MapActivity {

    Cursor earthquakeCursor;
    EarthquakeReceiver receiver;
    // Drawable pin = this.getResources().getDrawable(R.drawable.quake);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_map);

        Uri earthquakeURI = EarthquakeProvider.CONTENT_URI;
        earthquakeCursor = getContentResolver().query(earthquakeURI,
                                                      null, null, null,
                                                      null);

        MapView earthquakeMap = (MapView)findViewById(R.id.map_view);
        EarthquakeOverlay eo = new EarthquakeOverlay(earthquakeCursor);
        // EarthquakeItemizedOverlay eio = new EarthquakeItemizedOverlay(earthquakeCursor, pin);
        earthquakeMap.getOverlays().add(eo);

    }

    @Override
    public void onResume() {
        earthquakeCursor.requery();

        IntentFilter filter;
        filter = new IntentFilter(EarthquakeService.NEW_EARTHQUAKE_FOUND);
        receiver = new EarthquakeReceiver();
        registerReceiver(receiver, filter);

        super.onResume();
    }

    @Override
    public void onPause() {
        earthquakeCursor.deactivate();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        earthquakeCursor.close();
        super.onDestroy();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public class EarthquakeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            earthquakeCursor.requery();
            MapView earthquakeMap = (MapView)findViewById(R.id.map_view);
            earthquakeMap.invalidate();
        }
    }
}
