package com.example.michalparysz.weatherapp.Models;

import com.example.michalparysz.weatherapp.Models.Forecast.Forecast;
import com.example.michalparysz.weatherapp.Models.Weather.Weather;

public class Result {
    private Weather weather;
    private Forecast forecast;

    public Result() {

    }
    public Result (Weather weather, Forecast forecast) {
        this.weather = weather;
        this.forecast = forecast;
    }

    public Weather getWeather() {
        return weather;
    }

    public Forecast getForecast() {
        return forecast;
    }
}
