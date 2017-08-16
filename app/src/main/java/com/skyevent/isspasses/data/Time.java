package com.skyevent.isspasses.data;

import org.json.JSONObject;

/**
 * Created by Kim-Christian on 2017-05-29.
 */

public class Time implements JSONPopulator {
    private int duration;
    private int risetime;

    public int getDuration() {
        return duration;
    }

    public int getRisetime() {
        return risetime;
    }

    @Override
    public void populate(JSONObject data) {
        duration = data.optInt("duration");
        risetime = data.optInt("risetime");
    }
}
