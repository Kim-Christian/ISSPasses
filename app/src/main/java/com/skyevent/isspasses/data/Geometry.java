package com.skyevent.isspasses.data;

import org.json.JSONObject;

/**
 * Created by Kim-Christian on 2017-05-29.
 */

public class Geometry implements JSONPopulator {
    private Location location;

    public Location getLocation() {
        return location;
    }

    @Override
    public void populate(JSONObject data) {
        location = new Location();
        location.populate(data.optJSONObject("location"));
    }
}
