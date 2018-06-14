package com.example.michalparysz.weatherapp.Network;

import android.net.NetworkInfo;

import com.example.michalparysz.weatherapp.Models.Result;
import com.example.michalparysz.weatherapp.Models.Weather.Weather;

public interface DownloadCallback<T> {
    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateFromDownload(Result result);

//    void updateFromDownload(Weather weather);
    void stopDownloading(String error);
    /**
     * Get the device's active network status in the form of a NetworkInfo object.
     */
    NetworkInfo getActiveNetworkInfo();

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishDownloading();
}

