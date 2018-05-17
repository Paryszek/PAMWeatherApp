package com.example.michalparysz.weatherapp;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static AstroCalculator astroCalculator;
    private FragmentAdapter _fragmentAdapter;
    private ViewPager viewPager;
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

    public AstroCalculator getAstro() {
        return astroCalculator;
    }
    public void setViewPager(int fragmentNumber) {
        viewPager.setCurrentItem(fragmentNumber);
    }

    @OnClick({R.id.moonButton})
    public void onClickMoonButton(View view) {
        setViewPager(0);
    }

    @OnClick({R.id.sunButton})
    public void onClickSunButton(View view) {
        setViewPager(1);
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
        _fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        _fragmentAdapter.addFragment(new MoonFragment(), "Moon");
        _fragmentAdapter.addFragment(new SunFragment(), "Sun");
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
