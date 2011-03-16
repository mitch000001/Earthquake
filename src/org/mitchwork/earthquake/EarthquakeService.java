package org.mitchwork.earthquake;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class EarthquakeService extends Service {

    public static final String NEW_EARTHQUAKE_FOUND = "New_Earthquake_Found";

    private Timer updateTimer;
    private float minimumMagnitude;
    EarthquakeLookupTask lastLookup = null;

    @Override
    public void onCreate() {
        updateTimer = new Timer("earthquakeUpdates");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retrieve the shared preferences
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int minMagIndex = prefs.getInt(Preferences.PREF_MIN_MAG, 0);
        if (minMagIndex < 0)
            minMagIndex = 0;

        int freqIndex = prefs.getInt(Preferences.PREF_UPDATE_FREQ, 0);
        if (freqIndex < 0)
            freqIndex = 0;

        boolean autoUpdate = prefs.getBoolean(Preferences.PREF_AUTO_UPDATE, false);

        Resources r = getResources();
        int[] minMagValues = r.getIntArray(R.array.magnitude);
        int[] freqValues = r.getIntArray(R.array.update_freq_values);

        minimumMagnitude = minMagValues[minMagIndex];
        int updateFreq = freqValues[freqIndex];

        updateTimer.cancel();
        if (autoUpdate) {
            updateTimer = new Timer("earthquakeUpdates");
            updateTimer.scheduleAtFixedRate(doRefresh, 0, updateFreq*60*1000);
        }
        else
            refreshEarthquakes();

        return Service.START_STICKY;
    };

    private TimerTask doRefresh = new TimerTask() {
        public void run() {
            refreshEarthquakes();
        }
    };

    private void announceNewQuake(Quake quake) {
        Intent intent = new Intent(NEW_EARTHQUAKE_FOUND);
        intent.putExtra("date", quake.getDate().getTime());
        intent.putExtra("details", quake.getDetails());
        intent.putExtra("longitude", quake.getLocation().getLongitude());
        intent.putExtra("latitude", quake.getLocation().getLatitude());
        intent.putExtra("magnitude", quake.getMagnitude());

        sendBroadcast(intent);
    }

    private void refreshEarthquakes() {
        if (lastLookup == null || lastLookup.getStatus().equals(AsyncTask.Status.FINISHED)) {
            lastLookup = new EarthquakeLookupTask();
            lastLookup.execute((Void[])null);
        }
    }

    private void addNewQuake(Quake _quake) {
    	ContentResolver cr = getContentResolver();
    	// Construct a where clause to make sure we don't already have this
    	// earthquake in the provider.
    	String w = EarthquakeProvider.KEY_DATE + " = " + _quake.getDate().getTime();

    	// If the earthquake is new, insert it into the provider.
        Cursor c =cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
        if (c.getCount()==0) {
    		ContentValues values = new ContentValues();

    		values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
    		values.put(EarthquakeProvider.KEY_DETAILS, _quake.getDetails());

    		double lat = _quake.getLocation().getLatitude();
    		double lng = _quake.getLocation().getLongitude();
    		values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
    		values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
    		values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
    		values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());

    		cr.insert(EarthquakeProvider.CONTENT_URI, values);
            announceNewQuake(_quake);
    	}
        c.close();
    }
}
