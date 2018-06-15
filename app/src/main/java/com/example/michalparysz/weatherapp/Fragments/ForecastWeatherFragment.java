package com.example.michalparysz.weatherapp.Fragments;

import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import com.example.michalparysz.weatherapp.Models.Forecast.Day;
import com.example.michalparysz.weatherapp.Models.Forecast.Forecast;
import com.example.michalparysz.weatherapp.Models.Forecast.Forecastday;
import com.example.michalparysz.weatherapp.Models.Forecast.WeatherForecast;
import com.example.michalparysz.weatherapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.michalparysz.weatherapp.MainActivity.imUnits;

public class ForecastWeatherFragment extends Fragment {
    @BindView(R.id.weekDayName)
    TextView weekDayName;
    @BindView(R.id.avgtemp)
    TextView avgtemp;
    @BindView(R.id.minTemp)
    TextView minTemp;
    @BindView(R.id.maxTemp)
    TextView maxTemp;
    @BindView(R.id.forecastWeatherIcon)
    ImageView forecastWeatherIcon;

    ArrayList<String> forecastArray = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private int dayCount = 0;
    private WeatherForecast _weatherForecast;

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

    public void reloadViewFragment(WeatherForecast weatherForecast) {
        _weatherForecast = weatherForecast;
        if (weatherForecast != null) {
            try {
                Forecastday day = weatherForecast.getForecast().getForecastday()[dayCount];
                weekDayName.setText(dayOfTheWeek(day.getDate()));
                if (imUnits)
                    avgtemp.setText(day.getDay().getAvgtemp_f() + "° F");
                else
                    avgtemp.setText(day.getDay().getAvgtemp_c() + "° C");
                if (imUnits)
                    minTemp.setText(day.getDay().getMintemp_f() + "° F");
                else
                    minTemp.setText(day.getDay().getMintemp_c() + "° C");
                if (imUnits)
                    maxTemp.setText(day.getDay().getMaxtemp_f() + "° F");
                else
                    maxTemp.setText(day.getDay().getMaxtemp_c() + "° C");
                Picasso.get().load("http:" + day.getDay().getCondition().getIcon()).into(forecastWeatherIcon);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void refreashUnits() {
        reloadViewFragment(_weatherForecast);
    }

    @OnClick(R.id.prevButton)
    public void OnClickPrevButton() {
        if (dayCount > 0) {
            dayCount -= 1;
            reloadViewFragment(_weatherForecast);
        }
    }

    @OnClick(R.id.nextButton)
    public void OnClickNextButton() {
        if (dayCount < 9) {
            dayCount += 1;
            reloadViewFragment(_weatherForecast);
        }
    }

    private String dayOfTheWeek(String date) {
        String dayOfTheWeek = "Unknown";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            Date _date = format.parse(date);
            Calendar c = Calendar.getInstance();
            c.set(_date.getYear(), _date.getMonth(), _date.getDay());
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            switch(dayOfWeek) {
                case 1:
                    dayOfTheWeek = "Sunday";
                    break;
                case 2:
                    dayOfTheWeek = "Monday";
                    break;
                case 3:
                    dayOfTheWeek = "Tuesday";
                    break;
                case 4:
                    dayOfTheWeek = "Wednesday";
                    break;
                case 5:
                    dayOfTheWeek = "Thursday";
                    break;
                case 6:
                    dayOfTheWeek = "Friday";
                    break;
                case 7:
                    dayOfTheWeek = "Saturday";
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayOfTheWeek;
    }
}
