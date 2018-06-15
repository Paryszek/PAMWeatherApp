package com.example.michalparysz.weatherapp.Models.Weather;

import java.io.Serializable;

public class Weather implements Serializable {
    private Location location;

    private Current current;

    public Weather() {}

    public Location getLocation ()
    {
        return location;
    }

    public void setLocation (Location location)
    {
        this.location = location;
    }

    public Current getCurrent ()
    {
        return current;
    }

    public void setCurrent (Current current)
    {
        this.current = current;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [location = "+location+", current = "+current+"]";
    }
}