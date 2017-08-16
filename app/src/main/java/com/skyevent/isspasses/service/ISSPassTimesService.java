package com.skyevent.isspasses.service;

import android.os.AsyncTask;

import com.skyevent.isspasses.data.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Kim-Christian on 2017-05-29.
 */

public class ISSPassTimesService {
    private PassTimesServiceCallback callback;
    private Exception error;

    public ISSPassTimesService(PassTimesServiceCallback callback) {
        this.callback = callback;
    }

    public void refreshTime(double latitude, double longitude, int numberOfPasses) {
        if (latitude < -71) {
            latitude = -71;
        } else if (latitude > 71) {
            latitude = 71;
        }
        if (longitude < -180) {
            longitude = -180;
        } else if (longitude > 180) {
            longitude = 180;
        }
        if (numberOfPasses < 1) {
            numberOfPasses = 1;
        } else if ( numberOfPasses > 100) {
            numberOfPasses = 100;
        }
        final String lat = Double.toString(latitude);
        final String lon = Double.toString(longitude);
        final String n = Integer.toString(numberOfPasses);

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String endpoint = "http://api.open-notify.org/iss-pass.json?lat="
                        + lat + "&lon=" + lon + "&n=" + n;
                try {
                    URL url = new URL(endpoint);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return result.toString();
                } catch (Exception e) {
                    error = e;
                }

                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                if (s == null && error != null) {
                    callback.serviceFailure(error);
                    return;
                }
                try {
                    JSONObject data = new JSONObject(s);
                    JSONObject response = ((JSONArray)data.get("response")).getJSONObject(0);
                    String message = data.optString("message");
                    if (!message.equals("success")) {
                        callback.serviceFailure(new ISSPassTimesService.LocationException("No information found for the location"));
                        return;
                    }
                    Time passTime = new Time();
                    passTime.populate(response);
                    callback.serviceSuccess(passTime);
                } catch (JSONException e) {
                    callback.serviceFailure(e);
                }
            }
        }.execute();
    }

    public class LocationException extends Exception {
        public LocationException(String detailMessage) {
            super(detailMessage);
        }
    }
}
