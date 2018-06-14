package com.example.michalparysz.weatherapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.michalparysz.weatherapp.Models.Forecast.Forecast;
import com.example.michalparysz.weatherapp.Models.Forecast.Forecastday;
import com.example.michalparysz.weatherapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForecastWeatherFragment extends Fragment {

    ArrayList<String> forecastArray = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    public ForecastWeatherFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forecast_weather_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void reloadViewFragment(Forecast forecast) {
        if (forecast != null) {
            try {
//                forecastArray.add(forecast.getForecastday()[0]);
//                Picasso.get().load("http:" + forecast.getForecastday()[0].getDay().getCondition().getIcon()).into();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
