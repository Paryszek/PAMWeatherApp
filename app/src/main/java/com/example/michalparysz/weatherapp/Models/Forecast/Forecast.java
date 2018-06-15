package com.example.michalparysz.weatherapp.Models.Forecast;

import java.io.Serializable;

public class Forecast implements Serializable
{
    private Forecastday[] forecastday;

    public Forecast() {}

    public Forecastday[] getForecastday ()
    {
        return forecastday;
    }

    public void setForecastday (Forecastday[] forecastday)
    {
        this.forecastday = forecastday;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [forecastday = "+forecastday+"]";
    }
}