package com.paad;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created with IntelliJ IDEA.
 * User: l344929
 * Date: 8/7/12
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreferencesActivity extends Activity{

    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
    public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";

    SharedPreferences prefs;

    CheckBox autoUpdate;
    Spinner updateFreqSpinner;
    Spinner magnitudeSpinner;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
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

            public void onClick(View view) {
                savePreferences();
                PreferencesActivity.this.setResult(RESULT_OK);
                finish();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                PreferencesActivity.this.setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void populateSpinners(){
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

    private void updateUIFromPreferences() {
        boolean autoUpChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
        int updateFreqIndex = prefs.getInt(PREF_UPDATE_FREQ_INDEX, 2);
        int minMagIndex = prefs.getInt(PREF_MIN_MAG_INDEX, 0);

        updateFreqSpinner.setSelection(updateFreqIndex);
        magnitudeSpinner.setSelection(minMagIndex);
        autoUpdate.setChecked(autoUpChecked);
    }

    private void savePreferences() {
        int updateIndex = updateFreqSpinner.getSelectedItemPosition();
        int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
        boolean autoUpdateChecked = autoUpdate.isChecked();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateChecked);
        editor.putInt(PREF_UPDATE_FREQ_INDEX, updateIndex);
        editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex);
        editor.commit();
    }
}
