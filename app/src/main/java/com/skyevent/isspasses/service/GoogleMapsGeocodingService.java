package com.skyevent.isspasses.service;

import android.net.Uri;
import android.os.AsyncTask;

import com.skyevent.isspasses.data.Geometry;

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

public class GoogleMapsGeocodingService {
    private GeocodingServiceCallback callback;
    private String location;
    private Exception error;
    private String KEY = "";//private key needed

    public GoogleMapsGeocodingService(GeocodingServiceCallback callback) {
        this.callback = callback;
    }

    public String getLocation() {
        return location;
    }

    public void refreshLocation(final String address) {
        this.location = address;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                String endpoint = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + KEY;
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
                    JSONObject results = ((JSONArray)data.get("results")).getJSONObject(0);
                    String status = data.optString("status");
                    if (!status.equals("OK")) {
                        callback.serviceFailure(new GoogleMapsGeocodingService.AddressException("No information found for " + location));
                        return;
                    }
                    Geometry geometry = new Geometry();
                    geometry.populate(results.optJSONObject("geometry"));
                    callback.serviceSuccess(geometry);
                } catch (JSONException e) {
                    callback.serviceFailure(e);
                }
            }
        }.execute(location);
    }

    public class AddressException extends Exception {
        public AddressException(String detailMessage) {
            super(detailMessage);
        }
    }
}