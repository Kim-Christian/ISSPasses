package com.skyevent.isspasses.service;

import com.skyevent.isspasses.data.Time;

/**
 * Created by Kim-Christian on 2017-05-29.
 */

public interface PassTimesServiceCallback {
    void serviceSuccess(Time passTime);
    void serviceFailure(Exception exception);
}
