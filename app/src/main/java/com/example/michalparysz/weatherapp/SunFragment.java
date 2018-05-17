package com.example.michalparysz.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by michalparysz on 27.04.2018.
 */

public class SunFragment extends Fragment {
    public static final String TAG = "Moon";
    AstroCalculator astroCalculator;

    @BindView(R.id.sunrise)
    TextView sunrise;
    @BindView(R.id.sunriseAzimuth)
    TextView sunriseAzimuth;
    @BindView(R.id.sunset)
    TextView sunset;
    @BindView(R.id.sunsetAzimuth)
    TextView sunsetAzimuth;
    @BindView(R.id.civilSunrise)
    TextView civilSunrise;
    @BindView(R.id.civilSunset)
    TextView civilSunset;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sun_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void reloadSunFragment() {
        astroCalculator = ((MainActivity) getActivity()).getAstro();
        sunrise.setText(formatDate(astroCalculator.getSunInfo().getSunrise()));
        sunriseAzimuth.setText(((Double) astroCalculator.getSunInfo().getAzimuthRise()).toString());
        sunset.setText(formatDate(astroCalculator.getSunInfo().getSunset()));
        sunsetAzimuth.setText(((Double) astroCalculator.getSunInfo().getAzimuthSet()).toString());
        civilSunrise.setText(formatDate(astroCalculator.getSunInfo().getTwilightMorning()));
        civilSunset.setText(formatDate(astroCalculator.getSunInfo().getTwilightEvening()));
    }

    private String formatDate(AstroDateTime astroDateTime) {
        String _hours = addZero(((Integer) astroDateTime.getHour()).toString());
        String _minutes = addZero(((Integer) astroDateTime.getMinute()).toString());
        String _seconds = addZero(((Integer) astroDateTime.getSecond()).toString());
        String _days = addZero(((Integer) astroDateTime.getDay()).toString());
        String _months = addZero(((Integer) astroDateTime.getMonth()).toString());
        String _years = addZero(((Integer) astroDateTime.getYear()).toString());
        return _hours + ":" + _minutes + ":" + _seconds + " " + _days + "." + _months + '.' + _years;
    }

    private String addZero(String value) {
        return value.length() == 1 ? "0" + value : value;
    }

}
