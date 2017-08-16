package com.skyevent.isspasses.service;

import com.skyevent.isspasses.data.Geometry;

/**
 * Created by Kim-Christian on 2017-05-29.
 */

public interface GeocodingServiceCallback {
    void serviceSuccess(Geometry geometry);
    void serviceFailure(Exception exception);
}
