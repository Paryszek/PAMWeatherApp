package com.example.michalparysz.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.latitudeInput)
    TextInputLayout latiduteInput;
    @BindView(R.id.longitudeInput)
    TextInputLayout longitudeInput;
    @BindView(R.id.refreshInput)
    TextInputLayout refreshInput;
    @BindView(R.id.cityInput)
    TextInputLayout cityInput;

    private String latitude = "0";
    private String longitude = "0";
    private String refreshPeriod = "60000";
    private String currentCity = "Warsaw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        checkForValuesFromParentActivity();
        setupToolBar();
        initValues();
    }

    private void checkForValuesFromParentActivity() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                currentCity = extras.getString("currentCity");
                latitude = extras.getString("latitude");
                longitude = extras.getString("longitude");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //The key argument here must match that used in the other activity
        }
    }

    private void initValues() {
        try {
            Objects.requireNonNull(latiduteInput.getEditText()).setText(latitude);
            Objects.requireNonNull(longitudeInput.getEditText()).setText(longitude);
            Objects.requireNonNull(refreshInput.getEditText()).setText(refreshPeriod);
            Objects.requireNonNull(cityInput.getEditText()).setText(currentCity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent= new Intent();
                intent.putExtra("currentCity", currentCity);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("refreshPeriod" ,refreshPeriod);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.zatwierdz)
    public void OnClickZatwierdz() {
        try {
            int latV = Integer.parseInt(Objects.requireNonNull(latiduteInput.getEditText()).getText().toString());
            int longV = Integer.parseInt(Objects.requireNonNull(longitudeInput.getEditText()).getText().toString());
            int refreshV = Integer.parseInt(Objects.requireNonNull(refreshInput.getEditText()).getText().toString());
            String cityName = Objects.requireNonNull(cityInput.getEditText()).getText().toString();
            if (validation(latV, "latidute") && validation(longV, "longitude") && validation(refreshV, "refreshPeriod")) {
                latitude = String.valueOf(latV);
                longitude = String.valueOf(longV);
                refreshPeriod = String.valueOf(refreshV);
                currentCity = cityName;
            } else {
                android.widget.Toast.makeText(getBaseContext(), "Wrong inputs, please try again", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void setupToolBar() {
        setSupportActionBar((Toolbar)findViewById(R.id.settings_bar));
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);
    }

    private boolean validation(int value, String type) {
        switch (type) {
            case "latidute":
                if (-90 <= value && value <= 90) return true;
                break;
            case "longitude":
                if (-180 <= value && value <= 180) return true;
                break;
            case "refreshPeriod":
                if (value >= 30000) return true;
                break;
        }
        return false;
    }
}
