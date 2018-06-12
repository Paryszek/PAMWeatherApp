package com.example.michalparysz.weatherapp.Models.Weather;

public class Weather {
    private Location location;

    private Current current;

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