package com.example.michalparysz.weatherapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by michalparysz on 27.04.2018.
 */


public class MoonFragment extends Fragment {
    @BindView(R.id.moonRise)
    TextView moonRise;
    public MoonFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AstroCalculator astroCalculator = new AstroCalculator(new AstroDateTime(), new AstroCalculator.Location(50, 50));
        moonRise.setText(astroCalculator.getMoonInfo().getMoonrise().toString());
    }
}
