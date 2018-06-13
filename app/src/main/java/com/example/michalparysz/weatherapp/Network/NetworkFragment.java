package com.example.michalparysz.weatherapp.Network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.ProgressBar;

import com.example.michalparysz.weatherapp.MainActivity;
import com.example.michalparysz.weatherapp.Models.Weather.Weather;
import com.example.michalparysz.weatherapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import static com.example.michalparysz.weatherapp.MainActivity.currentCity;
import static com.example.michalparysz.weatherapp.Network.NetworkFragment.mUrlString;

public class NetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";

    private static final String URL_KEY = "7214cc918a3244bfa71224638181006";
    private static ExecutorService FULL_TASK_EXECUTOR;
    private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;
    public static String mUrlString;
    static {
        FULL_TASK_EXECUTOR = (ExecutorService) Executors.newCachedThreadPool();
    }
    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static NetworkFragment getInstance(FragmentManager fragmentManager, String url) {
        NetworkFragment networkFragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        networkFragment.setArguments(args);
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUrlString = getArguments().getString(URL_KEY);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        mCallback = (DownloadCallback) context;
        ((MainActivity) getActivity()).startDownload();
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

class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {
    private DownloadCallback<Weather> mCallback;

    public DownloadTask(DownloadCallback<Weather> callback) {
        setCallback(callback);
    }


    void setCallback(DownloadCallback<Weather> callback) {
        mCallback = callback;
    }

    /**
     * Wrapper class that serves as a union of a result value and an exception. When the download
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    static class Result {
        public String mResultValue;
        public Exception mException;
        public Result(String resultValue) {
            mResultValue = resultValue;
        }
        public Result(Exception exception) {
            mException = exception;
        }
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
                mCallback.updateFromDownload(null);
                cancel(true);
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected Result doInBackground(String... params) {
        Weather weather = null;
        List<String> urls = new ArrayList<>();
        urls.add(mUrlString);
        if (!isCancelled() && !urls.isEmpty()) {
            String urlString = urls.get(0);
            try {
                URL url = new URL("https://api.apixu.com/v1/current.json?key=7214cc918a3244bfa71224638181006&q=" + currentCity);
                weather = downloadUrl(url);
                mCallback.updateFromDownload(weather);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return new Result("");
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private Weather downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpsURLConnection connection = null;
        Weather result = new Weather();
        try {
            connection = (HttpsURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream);
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    Weather readStream(InputStream stream)
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

    /**
     * Updates the DownloadCallback with the result.
     */
    @Override
    protected void onPostExecute(Result result) {
        if (result != null && mCallback != null) {
            if (result.mException != null) {
                mCallback.updateFromDownload(new Weather());
            } else if (result.mResultValue != null) {
                mCallback.updateFromDownload(new Weather());
            }
            mCallback.finishDownloading();
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled(Result result) {
    }
}

