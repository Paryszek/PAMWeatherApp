package com.example.michalparysz.weatherapp;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.michalparysz.weatherapp.Fragments.*;
import com.example.michalparysz.weatherapp.Network.DownloadCallback;
import com.example.michalparysz.weatherapp.Network.NetworkFragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements DownloadCallback {
    private static AstroCalculator astroCalculator;
    private FragmentAdapter _fragmentAdapter;
    private ViewPager viewPager;
    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment mNetworkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean mDownloading = false;

    private int _latitude;
    private int _longitude;
    private int _refreshPeriod;

    @BindView(R.id.latitudeInput)
    TextInputLayout latiduteInput;
    @BindView(R.id.longitudeInput)
    TextInputLayout longitudeInput;
    @BindView(R.id.refreshInput)
    TextInputLayout refreshInput;

    @BindView(R.id.clock)
    TextView clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initAstro();
        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timer updateData = new Timer();
        updateData.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        reloadView();
                    }
                });
            }
        }, 0, _refreshPeriod);
        Timer updateTime = new Timer();
        updateTime.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateClock();
                    }
                });
            }
        },0, 1000);
    }

    @Override
    public void updateFromDownload(String result) {
        // Update your UI here based on result of download.
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
            //
                break;
            case Progress.CONNECT_SUCCESS:
            //
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
            //
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
            //
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
            //
                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }
    }

    public void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }

    public AstroCalculator getAstro() {
        return astroCalculator;
    }
    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }

    @OnClick(R.id.zatwierdz)
    public void OnClickZatwierdz() {
        try {
            int latV = Integer.parseInt(Objects.requireNonNull(latiduteInput.getEditText()).getText().toString());
            int longV = Integer.parseInt(Objects.requireNonNull(longitudeInput.getEditText()).getText().toString());
            int refreshV = Integer.parseInt(Objects.requireNonNull(refreshInput.getEditText()).getText().toString());
            if (validation(latV, "latidute") && validation(longV, "longitude") && validation(refreshV, "refreshPeriod")) {
                _latitude = latV;
                _longitude = longV;
                _refreshPeriod = refreshV;
                reloadView();
            } else {
                Toast.makeText(this, "Wrong inputs, please try again", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {}
    }
    @SuppressLint("SetTextI18n")
    private void updateClock() {
        Date astroDateTime = new Date();
        String _hours = addZero(((Integer) astroDateTime.getHours()).toString());
        String _minutes = addZero(((Integer) astroDateTime.getMinutes()).toString());
        String _seconds = addZero(((Integer) astroDateTime.getSeconds()).toString());
        clock.setText(_hours + ":" + _minutes + ":" + _seconds);
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
                if (value >= 0) return true;
                break;
        }
        return false;
    }
    private void reloadView() {
        astroCalculator = new AstroCalculator(getDate(), new AstroCalculator.Location(_latitude, _longitude));
        MoonFragment moonFragment = (MoonFragment) _fragmentAdapter.getItem(0);
        SunFragment sunFragment = (SunFragment) _fragmentAdapter.getItem(1);
        moonFragment.reloadMoonFragment();
        sunFragment.reloadSunFragment();
    }
    private void setupViewPager(ViewPager viewPager) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mNetworkFragment = NetworkFragment.getInstance(fragmentManager, "http://api.apixu.com/v1/current.json");
        _fragmentAdapter = new FragmentAdapter(fragmentManager);
        _fragmentAdapter.addFragment(new MoonFragment(), "Moon");
        _fragmentAdapter.addFragment(new SunFragment(), "Sun");
        _fragmentAdapter.addFragment(new WeatherFragment(), "Weather");
        _fragmentAdapter.addFragment(new AddWeatherFragment(), "AddWeather");
        _fragmentAdapter.addFragment(new ForecastWeatherFragment(), "ForecastWeather");
        viewPager.setAdapter(_fragmentAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void initAstro() {
        _refreshPeriod = 10000;
        Objects.requireNonNull(latiduteInput.getEditText()).setText("0");
        Objects.requireNonNull(longitudeInput.getEditText()).setText("0");
        Objects.requireNonNull(refreshInput.getEditText()).setText(((Integer) _refreshPeriod).toString());
        int latitudeValue = Integer.parseInt(Objects.requireNonNull(latiduteInput.getEditText()).getText().toString());
        int longitudeValue = Integer.parseInt(Objects.requireNonNull(latiduteInput.getEditText()).getText().toString());
        astroCalculator = new AstroCalculator(getDate(), new AstroCalculator.Location(latitudeValue, longitudeValue));
    }

    private AstroDateTime getDate() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);

        int timezoneOffset =  Calendar.getInstance().get(Calendar.ZONE_OFFSET) / 3600000;
        return new AstroDateTime(year, month, day, hour, minute, second, timezoneOffset, true);
    }

    private String addZero(String value) {
        return value.length() == 1 ? "0" + value : value;
    }

}
