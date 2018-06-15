package com.example.michalparysz.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.michalparysz.weatherapp.Fragments.ForecastWeatherFragment;
import com.example.michalparysz.weatherapp.Fragments.MoonFragment;
import com.example.michalparysz.weatherapp.Fragments.SunFragment;
import com.example.michalparysz.weatherapp.Fragments.WeatherFragment;
import com.example.michalparysz.weatherapp.Models.Forecast.Forecast;
import com.example.michalparysz.weatherapp.Models.Forecast.WeatherForecast;
import com.example.michalparysz.weatherapp.Models.Result;
import com.example.michalparysz.weatherapp.Models.Weather.Weather;
import com.example.michalparysz.weatherapp.Network.DownloadCallback;
import com.example.michalparysz.weatherapp.Network.NetworkFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DownloadCallback {

    public static int latitude = 0;
    public static int longitude = 0;
    public static Boolean imUnits = false;
    public static String currentCity;
    public static String apiKey = "7725cec239f14e07a9f10908181506";
    public static File path;
    public static boolean connection = true;

    private int refreshPeriod = 60000;
    private  AstroCalculator astroCalculator;
    private FragmentAdapter _fragmentAdapter;
    private ViewPager viewPager;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;

    @BindView(R.id.clock)
    TextView clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.settings_bar));
        ButterKnife.bind(this);
        initAstro();
        initCity();
        path = getBaseContext().getFilesDir();
        viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);
    }

    private void initCity() {
        try {
            String filename = "cities.data";
            File file = new File(getBaseContext().getFilesDir(), filename);
            if (file.exists() || file.length() != 0) {
                FileInputStream fin = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fin);
                currentCity = ((ArrayList<String>) ois.readObject()).get(0);
                fin.close();
                ois.close();
            } else {
                currentCity = "Warsaw";
            }
            if (currentCity == null){
                currentCity = "Warsaw";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            try {
                if (data.hasExtra("currentCity")) {
                    currentCity = data.getStringExtra("currentCity");
                }
                if (data.hasExtra("latitude")) {
                    latitude = Integer.parseInt(data.getStringExtra("latitude"));
                }
                if (data.hasExtra("longitude")) {
                    longitude = Integer.parseInt(data.getStringExtra("longitude"));
                }
                if (data.hasExtra("refreshPeriod")) {
                    refreshPeriod = Integer.parseInt(data.getStringExtra("refreshPeriod"));
                }
                if (data.hasExtra("reloadWeather")) {
                    if(data.getBooleanExtra("reloadWeather", false)) {
                        startDownload();
                    }
                }
                if (data.hasExtra("imUnits")) {
                    if(data.getBooleanExtra("imUnits", false)) {
                        imUnits = true;
                        reloadWeatherView();
                    } else {
                        imUnits = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Intent settings = new Intent(this, SettingsActivity.class);
        settings.putExtra("currentCity", currentCity);
        settings.putExtra("latitude", Integer.valueOf(latitude).toString());
        settings.putExtra("longitude", Integer.valueOf(longitude).toString());
        settings.putExtra("imUnits", imUnits);
        startActivityForResult(settings, 1);
        return super.onOptionsItemSelected(menuItem);
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
                        if(!mDownloading) {
                            startDownload();
                        }
                    }
                });
            }
        }, 5000, refreshPeriod);
        Timer updateTime = new Timer();
        updateTime.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        updateClock();
                        if (!connection)
                            checkIfConnectionIsBack();
                    }
                });
            }
        },0, 1000);
    }

    private void checkIfConnectionIsBack() {
        NetworkInfo networkInfo = getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) && !connection) {
            android.widget.Toast.makeText(getBaseContext(), "Internet Connection is back", Toast.LENGTH_LONG).show();
            startDownload();
            android.widget.Toast.makeText(getBaseContext(), "Our data is up to date after disconnection", Toast.LENGTH_LONG).show();
            connection = true;
        }
    }

    @Override
    public void updateFromDownload(Result result) {
        Weather _weather = result.getWeather();
        WeatherForecast _forecast = result.getWeatherForecast();
        if (_weather.getLocation() != null) {
            try {
                latitude = (int) Double.parseDouble(_weather.getLocation().getLat());
                longitude = (int) Double.parseDouble(_weather.getLocation().getLon());
                initAstro();
                currentCity = _weather.getLocation().getName();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        final Weather Weather = _weather;
        final WeatherForecast WeatherForecast = _forecast;
        // Update your UI here based on result of download.
        runOnUiThread(new Runnable() {
            public void run() {
                if (Weather.getLocation() != null) {
                    WeatherFragment weatherFragment = (WeatherFragment) _fragmentAdapter.getItem(0);
                    weatherFragment.reloadViewFragment(Weather);
                }
                if (WeatherForecast != null) {
                    ForecastWeatherFragment forecastWeatherFragment = (ForecastWeatherFragment) _fragmentAdapter.getItem(1);
                    forecastWeatherFragment.reloadViewFragment(WeatherForecast);
                }
                reloadView();
            }
        });
    }

    @Override
    public void stopDownloading(final String error) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(error.toLowerCase().equals("request timeout"))
                    startDownload();
                android.widget.Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(connectivityManager).getActiveNetworkInfo();
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
            mDownloading = true;
            mNetworkFragment.startDownload();
        } else {
            if(mDownloading && getActiveNetworkInfo().isAvailable()) {
                android.widget.Toast.makeText(getBaseContext(), "Already downloading...", Toast.LENGTH_LONG).show();
            }
            if (!getActiveNetworkInfo().isAvailable()) {
                android.widget.Toast.makeText(getBaseContext(), "No internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    public AstroCalculator getAstro() {
        if (astroCalculator == null) {
            initAstro();
        }
        return astroCalculator;
    }
    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }


    @SuppressLint("SetTextI18n")
    private void initAstro() {
        astroCalculator = new AstroCalculator(getDate(), new AstroCalculator.Location(latitude, longitude));
    }
    @SuppressLint("SetTextI18n")
    private void updateClock() {
        Date astroDateTime = new Date();
        String _hours = addZero(((Integer) astroDateTime.getHours()).toString());
        String _minutes = addZero(((Integer) astroDateTime.getMinutes()).toString());
        String _seconds = addZero(((Integer) astroDateTime.getSeconds()).toString());
        clock.setText(_hours + ":" + _minutes + ":" + _seconds);
    }

    private void reloadView() {
        astroCalculator = new AstroCalculator(getDate(), new AstroCalculator.Location(latitude, longitude));
        MoonFragment moonFragment = (MoonFragment) _fragmentAdapter.getItem(2);
        SunFragment sunFragment = (SunFragment) _fragmentAdapter.getItem(3);
        moonFragment.reloadMoonFragment();
        sunFragment.reloadSunFragment();
    }

    private void reloadWeatherView() {
        WeatherFragment weatherFragment = (WeatherFragment) _fragmentAdapter.getItem(0);
        ForecastWeatherFragment forecastFragment = (ForecastWeatherFragment) _fragmentAdapter.getItem(1);
        weatherFragment.refreashUnits();
        // forecastFragment.reloadSunFragment();
    }
    private void setupViewPager(ViewPager viewPager) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mNetworkFragment = NetworkFragment.getInstance(fragmentManager);
        _fragmentAdapter = new FragmentAdapter(fragmentManager);
        _fragmentAdapter.addFragment(new WeatherFragment(), "Weather");
        _fragmentAdapter.addFragment(new ForecastWeatherFragment(), "ForecastWeather");
        _fragmentAdapter.addFragment(new MoonFragment(), "Moon");
        _fragmentAdapter.addFragment(new SunFragment(), "Sun");
        viewPager.setAdapter(_fragmentAdapter);
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
