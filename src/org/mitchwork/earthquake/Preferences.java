package org.mitchwork.earthquake;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.Spinner;

public class Preferences extends Activity {
	
	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
	public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";
	
	SharedPreferences prefs;
	
	CheckBox autoUpdate;
	Spinner updateFreqSpinner;
	Spinner magnitudeSpinner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		
		updateFreqSpinner = (Spinner)findViewById(R.id.spinner_update_freq);
		magnitudeSpinner = (Spinner)findViewById(R.id.spinner_quake_mag);
		autoUpdate = (CheckBox)findViewById(R.id.checkbox_auto_update);
		
		populateSpinners();
		
		Context context = getApplicationContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		updateUIFromPreferences();
		
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				savedPreferences();
				Preferences.this.setResult(RESULT_OK);
				finish();
				
			}

		});
		
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Preferences.this.setResult(RESULT_CANCELED);
				finish();
				
			}
		});
	}
	
	private void savedPreferences() {
		int updateIndex = updateFreqSpinner.getSelectedItemPosition();
		int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
		boolean autoUpdateChecked = autoUpdate.isChecked();
		
		Editor editor = prefs.edit();
		editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateChecked);
		editor.putInt(PREF_AUTO_UPDATE, updateIndex);
		editor.putInt(PREF_MIN_MAG, minMagIndex);
		editor.commit();		
	}
	
	private void updateUIFromPreferences() {
		boolean autoUpChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
		int updateFreqIndex = prefs.getInt(PREF_UPDATE_FREQ, 2);
		int minMagIndex = prefs.getInt(PREF_MIN_MAG, 0);
		
		updateFreqSpinner.setSelection(updateFreqIndex);
		magnitudeSpinner.setSelection(minMagIndex);
		autoUpdate.setChecked(autoUpChecked);
	}

	private void populateSpinners() {
		// Populate the update frequency spinner
		ArrayAdapter<CharSequence> fAdapter;
		fAdapter = ArrayAdapter.createFromResource(this, R.array.update_freq_options, 
													android.R.layout.simple_spinner_item);
		int spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
		fAdapter.setDropDownViewResource(spinner_dd_item);
		updateFreqSpinner.setAdapter(fAdapter);
		
		// Populate the minimum magnitude spinner
		ArrayAdapter<CharSequence> mAdapter;
		mAdapter = ArrayAdapter.createFromResource(this, 
				R.array.magnitude_options, 
				android.R.layout.simple_spinner_item);
		mAdapter.setDropDownViewResource(spinner_dd_item);
		magnitudeSpinner.setAdapter(mAdapter);
	}
}
