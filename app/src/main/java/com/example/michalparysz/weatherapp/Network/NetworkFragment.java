package com.example.michalparysz.weatherapp.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.example.michalparysz.weatherapp.MainActivity;
import com.example.michalparysz.weatherapp.Models.Forecast.Forecast;
import com.example.michalparysz.weatherapp.Models.Forecast.Forecastday;
import com.example.michalparysz.weatherapp.Models.Forecast.WeatherForecast;
import com.example.michalparysz.weatherapp.Models.Result;
import com.example.michalparysz.weatherapp.Models.Weather.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.michalparysz.weatherapp.MainActivity.apiKey;
import static com.example.michalparysz.weatherapp.MainActivity.currentCity;
import static com.example.michalparysz.weatherapp.MainActivity.latitude;
import static com.example.michalparysz.weatherapp.MainActivity.longitude;
import static com.example.michalparysz.weatherapp.MainActivity.path;

public class NetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";
    private static ExecutorService FULL_TASK_EXECUTOR;
    private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;
    static {
        FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
    }
    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static NetworkFragment getInstance(FragmentManager fragmentManager) {
        NetworkFragment networkFragment = new NetworkFragment();
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        mCallback = (DownloadCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload() {
        cancelDownload();
        mDownloadTask = new DownloadTask(mCallback);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mDownloadTask.executeOnExecutor(FULL_TASK_EXECUTOR);
        } else {
            mDownloadTask.execute();
        }
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }
}

/**
 * Implementation of AsyncTask designed to fetch data from the network.
 */

class DownloadTask extends AsyncTask<String, Integer, Result> {
    private DownloadCallback<Result> mCallback;

    DownloadTask(DownloadCallback<Result> callback) {
        setCallback(callback);
    }


    private void setCallback(DownloadCallback<Result> callback) {
        mCallback = callback;
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */


    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                // If no connectivity, cancel task and update Callback with null data.
                mCallback.stopDownloading("No internet connection");
                MainActivity.connection = false;
                try {
                    mCallback.updateFromDownload(getDataFromFile());
                    mCallback.stopDownloading("Data from file, it can be outdated");
                } catch (Exception e) {
                    mCallback.stopDownloading("Error trying to get data from file");
                    e.printStackTrace();
                }
                cancel(true);
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected Result doInBackground(String... params) {
        Result result = null;
        if (!isCancelled()) {
            try {
                URL urlWeather = null;
                URL urlForecast = null;
                if (currentCity != null && !currentCity.isEmpty()) {
                    urlWeather = new URL("http://api.apixu.com/v1/current.json?key=" + apiKey + "&q=" + currentCity);
                    urlForecast = new URL("http://api.apixu.com/v1/forecast.json?key=" + apiKey + "&q=" + currentCity + "&days=1");
                } else {
                    urlWeather = new URL("http://api.apixu.com/v1/current.json?key=" + apiKey + "&q=" + latitude + "," + longitude);
                    urlForecast = new URL("http://api.apixu.com/v1/forecast.json?key=" + apiKey + "&q=" + latitude + "," + longitude + "&days=1");
                }
                result = new Result(downloadUrlWeather(urlWeather), downloadUrlWeatherForecast(urlForecast));
                if (result.getWeather() != null & result.getWeatherForecast() != null)
                    saveDataToFile(result);
                mCallback.updateFromDownload(result);
            } catch(SocketTimeoutException e) {
                onPostExecute(new Result());
                mCallback.stopDownloading("Request timeout");
            } catch(Exception e) {
                onPostExecute(new Result());
                mCallback.stopDownloading("Unknown request error");
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private Weather downloadUrlWeather(URL url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        Weather result = new Weather();
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(12000);
            connection.setConnectTimeout(12000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                mCallback.stopDownloading("Weather api not responding");
                throw new IOException("HTTP error code: " + connection.getResponseCode());
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStreamToWeather(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    Weather readStreamToWeather(InputStream stream)
            throws IOException, UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        Gson g = new Gson();
        Weather w = new Weather();
        try {
            w = g.fromJson(reader, Weather.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return w;
    }

    private WeatherForecast downloadUrlWeatherForecast(URL url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        WeatherForecast result = new WeatherForecast();
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(6000);
            connection.setConnectTimeout(6000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                mCallback.stopDownloading("Weather api not responding");
                throw new IOException("HTTP error code: " + connection.getResponseCode());
            }
            stream = connection.getInputStream();
            if (stream != null) {
                result = readStreamToWeatherForecast(stream);
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    WeatherForecast readStreamToWeatherForecast(InputStream stream)
            throws IOException, UnsupportedEncodingException {
        Reader reader = new InputStreamReader(stream, "UTF-8");
        Gson g = new Gson();
        WeatherForecast w = new WeatherForecast();
        try {
            w = g.fromJson(reader, WeatherForecast.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return w;
    }

    private void saveDataToFile(Result obj) throws IOException {
        File file = new File(path, "weather.data");
        FileOutputStream fout = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(obj);
        fout.close();
        oos.close();
    }

    private Result getDataFromFile() throws IOException, ClassNotFoundException{
        Result resultToReturn = new Result(new Weather(), new WeatherForecast());
        File file = new File(path, "weather.data");
        if (!file.exists() || file.length() == 0) {
            file.createNewFile();
        }
        FileInputStream fin = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fin);
        resultToReturn = (Result) ois.readObject();
        fin.close();
        ois.close();
        return resultToReturn;
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    @Override
    protected void onPostExecute(Result result) {
        if (mCallback != null) {
            mCallback.finishDownloading();
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled(Result result) {}


}

