package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.smartpantrymanager.util.Prefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Settings screen: expiring-soon alerts toggle and default unit preference. */
public class SettingsActivity extends AppCompatActivity {

    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SwitchCompat switchExpiry = findViewById(R.id.switchExpiry);
        Spinner spDefaultUnit = findViewById(R.id.spDefaultUnit);
        BottomNavigationView nav = findViewById(R.id.bottomNav);

        switchExpiry.setChecked(Prefs.expiryAlertsEnabled(this));
        switchExpiry.setOnCheckedChangeListener((button, checked) -> Prefs.setExpiryAlerts(this, checked));

        spDefaultUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));
        String current = Prefs.defaultUnit(this);
        for (int i = 0; i < UNITS.length; i++) {
            if (UNITS[i].equals(current)) spDefaultUnit.setSelection(i);
        }
        spDefaultUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Prefs.setDefaultUnit(SettingsActivity.this, UNITS[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        NavHelper.setup(this, nav, R.id.nav_settings);
    }
}
