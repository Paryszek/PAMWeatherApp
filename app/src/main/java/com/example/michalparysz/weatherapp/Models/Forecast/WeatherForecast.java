package com.example.michalparysz.weatherapp.Models.Forecast;

import java.io.Serializable;

public class WeatherForecast implements Serializable
{
    private Location location;

    public Location getLocation() { return this.location; }

    public void setLocation(Location location) { this.location = location; }

    private Current current;

    public Current getCurrent() { return this.current; }

    public void setCurrent(Current current) { this.current = current; }

    private Forecast forecast;

    public WeatherForecast() {}

    public Forecast getForecast() { return this.forecast; }

    public void setForecast(Forecast forecast) { this.forecast = forecast; }
}