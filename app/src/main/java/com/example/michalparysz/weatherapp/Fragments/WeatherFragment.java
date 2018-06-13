package com.example.michalparysz.weatherapp.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.michalparysz.weatherapp.Models.Weather.Weather;
import com.example.michalparysz.weatherapp.R;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;



public class WeatherFragment extends Fragment {

    public WeatherFragment() {}
    @BindView(R.id.cityname)
    TextView cityname;
    @BindView(R.id.lat)
    TextView lat;
    @BindView(R.id.lon)
    TextView lon;
    @BindView(R.id.temp)
    TextView temp;
    @BindView(R.id.hpa)
    TextView hpa;
    @BindView(R.id.weatherIcon)
    ImageView weatherIcon;
    @BindView(R.id.country)
    TextView country;
    @BindView(R.id.windSpeed)
    TextView windSpeed;
    @BindView(R.id.windDegree)
    TextView windDegree;
    @BindView(R.id.windDirection)
    TextView windDirection;
    @BindView(R.id.humidity)
    TextView humidity;
    @BindView(R.id.visibility)
    TextView visibility;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public void reloadViewFragment(Weather weather) {
        if (weather.getLocation() != null) {
            try {
                cityname.setText(weather.getLocation().getName());
                country.setText(weather.getLocation().getCountry());
                lat.setText(weather.getLocation().getLat() + "°");
                lon.setText(weather.getLocation().getLon() + "°");
                temp.setText(weather.getCurrent().getTemp_c() + "° C");
                hpa.setText(weather.getCurrent().getPressure_mb() + " hPa");
                windSpeed.setText(weather.getCurrent().getWind_kph() + " KPH");
                windDirection.setText(weather.getCurrent().getWind_dir());
                windDegree.setText(weather.getCurrent().getWind_degree() + "°");
                humidity.setText(weather.getCurrent().getHumidity() + "%");
                visibility.setText(weather.getCurrent().getVis_km() + "KM");
                Picasso.get().load("http:" + weather.getCurrent().getCondition().getIcon()).into(weatherIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
