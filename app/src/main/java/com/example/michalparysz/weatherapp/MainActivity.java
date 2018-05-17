package com.example.michalparysz.weatherapp;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity{
    private static AstroCalculator astroCalculator;
    private FragmentAdapter _fragmentAdapter;
    private ViewPager viewPager;
    @BindView(R.id.latitudeInput)
    TextInputLayout latiduteInput;
    @BindView(R.id.longitudeInput)
    TextInputLayout longitudeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initAstro();
        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);
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
        int latitudeValue = Integer.parseInt(Objects.requireNonNull(latiduteInput.getEditText()).getText().toString());
        int longitudeValue = Integer.parseInt(Objects.requireNonNull(longitudeInput.getEditText()).getText().toString());
        astroCalculator = new AstroCalculator(getDate(), new AstroCalculator.Location(latitudeValue, longitudeValue));
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
    private void initAstro() {
        Objects.requireNonNull(latiduteInput.getEditText()).setText("0");
        Objects.requireNonNull(longitudeInput.getEditText()).setText("0");
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
}
