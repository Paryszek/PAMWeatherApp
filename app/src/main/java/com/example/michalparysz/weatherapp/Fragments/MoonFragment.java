package com.example.michalparysz.weatherapp.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.michalparysz.weatherapp.MainActivity;
import com.example.michalparysz.weatherapp.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by michalparysz on 27.04.2018.
 */

public class MoonFragment extends Fragment {
    public static final String TAG = "Moon";
    AstroCalculator astroCalculator;

    @BindView(R.id.moonRise)
    TextView moonRise;
    @BindView(R.id.moonSet)
    TextView moonSet;
    @BindView(R.id.nextNewMoon)
    TextView nextNewMoon;
    @BindView(R.id.nextFullMoon)
    TextView nextFullMoon;
    @BindView(R.id.illumination)
    TextView illumination;
    @BindView(R.id.monthAge)
    TextView monthAge;

    public MoonFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moon_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void reloadMoonFragment() {
        try {
            astroCalculator = ((MainActivity) getActivity()).getAstro();
            moonRise.setText(formatDate(astroCalculator.getMoonInfo().getMoonrise()));
            moonSet.setText(formatDate(astroCalculator.getMoonInfo().getMoonset()));
            nextNewMoon.setText(formatDate(astroCalculator.getMoonInfo().getNextNewMoon()));
            nextFullMoon.setText(formatDate(astroCalculator.getMoonInfo().getNextFullMoon()));
            illumination.setText(((Double) astroCalculator.getMoonInfo().getIllumination()).toString());
            monthAge.setText(((Double) astroCalculator.getMoonInfo().getAge()).toString());
        } catch (Exception e) {}
    }

    private String formatDate(AstroDateTime astroDateTime) {
        String date;
        try {
            String _hours = addZero(((Integer) astroDateTime.getHour()).toString());
            String _minutes = addZero(((Integer) astroDateTime.getMinute()).toString());
            String _seconds = addZero(((Integer) astroDateTime.getSecond()).toString());
            String _days = addZero(((Integer) astroDateTime.getDay()).toString());
            String _months = addZero(((Integer) astroDateTime.getMonth()).toString());
            String _years = addZero(((Integer) astroDateTime.getYear()).toString());
            date = _hours + ":" + _minutes + ":" + _seconds + " " + _days + "." + _months + '.' + _years;
        } catch(NullPointerException e) {
            date = "none";
        }
        return date;
    }

    private String addZero(String value) {
        return value.length() == 1 ? "0" + value : value;
    }

}
