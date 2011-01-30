package org.mitchwork.earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class Earthquake extends Activity {
	
	ListView earthquakeListView;
	ArrayAdapter<Quake> aa;
	
	ArrayList<Quake> earthquakes = new ArrayList<Quake>();
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        earthquakeListView = (ListView)this.findViewById(R.id.earthquakeListView);
        
        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<Quake>(this, layoutID, earthquakes);
        earthquakeListView.setAdapter(aa);
    }
}