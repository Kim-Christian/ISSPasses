package com.skyevent.isspasses.data;

import org.json.JSONObject;

/**
 * Created by Kim-Christian on 2017-05-29.
 */

public class Location implements JSONPopulator {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public void populate(JSONObject data) {
        latitude = data.optDouble("lat");
        longitude = data.optDouble("lng");
    }
}
