package com.example.michalparysz.weatherapp.Models;

import com.example.michalparysz.weatherapp.Models.Forecast.WeatherForecast;
import com.example.michalparysz.weatherapp.Models.Weather.Weather;

import java.io.Serializable;

public class Result implements Serializable{
    private Weather weather;
    private WeatherForecast weatherForecast;

    public Result() {

    }
    public Result (Weather weather, WeatherForecast weatherForecast) {
        this.weather = weather;
        this.weatherForecast = weatherForecast;
    }

    public Weather getWeather() {
        return weather;
    }

    public WeatherForecast getWeatherForecast() {
        return weatherForecast;
    }
}
