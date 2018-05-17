package com.example.michalparysz.weatherapp;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by michalparysz on 27.04.2018.
 */


public class MoonFragment extends Fragment {
    public static final String TAG = "Moon";
    AstroCalculator astroCalculator;

    @BindView(R.id.moonButton)
    Button moonButton;
    @BindView(R.id.sunButton)
    Button sunButton;
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

    @OnClick({R.id.moonButton})
    public void onClickMoonButton(View view) {
        ((MainActivity) getActivity()).setViewPager(0);

    }

    @OnClick({R.id.sunButton})
    public void onClickSunButton(View view) {
        ((MainActivity) getActivity()).setViewPager(1);
    }

    @SuppressLint("SetTextI18n")
    public void reloadMoonFragment() {
        astroCalculator = ((MainActivity) getActivity()).getAstro();
        moonRise.setText(astroCalculator.getMoonInfo().getMoonrise().toString());
        moonSet.setText(astroCalculator.getMoonInfo().getMoonset().toString());
        nextNewMoon.setText(astroCalculator.getMoonInfo().getNextNewMoon().toString());
        nextFullMoon.setText(astroCalculator.getMoonInfo().getNextFullMoon().toString());
        illumination.setText(((Double) astroCalculator.getMoonInfo().getIllumination()).toString());
        monthAge.setText(((Double) astroCalculator.getMoonInfo().getAge()).toString());
    }

}
