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

    @OnClick({R.id.moonButton})
    public void onClickMoonButton(View view) {
        ((MainActivity) getActivity()).setViewPager(0);
    }

    @OnClick({R.id.sunButton})
    public void onClickSunButton(View view) {
        ((MainActivity) getActivity()).setViewPager(1);
    }

    @SuppressLint("SetTextI18n")
    public void reloadSunFragment() {
        astroCalculator = ((MainActivity) getActivity()).getAstro();
        sunrise.setText(astroCalculator.getSunInfo().getSunrise().toString());
        sunriseAzimuth.setText(((Double) astroCalculator.getSunInfo().getAzimuthRise()).toString());
        sunset.setText(astroCalculator.getSunInfo().getSunset().toString());
        sunsetAzimuth.setText(((Double) astroCalculator.getSunInfo().getAzimuthSet()).toString());
        civilSunrise.setText(astroCalculator.getSunInfo().getTwilightMorning().toString());
        civilSunset.setText(astroCalculator.getSunInfo().getTwilightEvening().toString());
    }

}
