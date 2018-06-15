package com.example.michalparysz.weatherapp.Models.Forecast;

import java.io.Serializable;

public class Forecastday implements Serializable
{
    private Astro astro;

    private Day day;

    private String date;

    private String date_epoch;

    public Forecastday() {}

    public Astro getAstro ()
    {
        return astro;
    }

    public void setAstro (Astro astro)
    {
        this.astro = astro;
    }

    public Day getDay ()
    {
        return day;
    }

    public void setDay (Day day)
    {
        this.day = day;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getDate_epoch ()
    {
        return date_epoch;
    }

    public void setDate_epoch (String date_epoch)
    {
        this.date_epoch = date_epoch;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [astro = "+astro+", day = "+day+", date = "+date+", date_epoch = "+date_epoch+"]";
    }
}