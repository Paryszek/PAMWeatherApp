package com.example.michalparysz.weatherapp;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by michalparysz on 27.04.2018.
 */


public class MoonFragment extends Fragment {
    public static final String TAG = "Moon";
    @BindView(R.id.moonButton)
    Button moonButton;
    @BindView(R.id.sunButton)
    Button sunButton;
    @BindView(R.id.moonRise)
    TextView moonRise;
    public MoonFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.moon_fragment, container, false);
        ButterKnife.bind(this, view);
        AstroCalculator astroCalculator = new AstroCalculator(new AstroDateTime(), new AstroCalculator.Location(50, 50));
        moonRise.setText(astroCalculator.getMoonInfo().getMoonrise().toString());
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
}
