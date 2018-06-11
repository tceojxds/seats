package com.walmartlabs.seats.stores.clock;

import java.util.Date;

import com.walmartlabs.seats.stores.TimestampStore;

/**
 * ClockTimestampStore
 */
public class ClockTimestampStore implements TimestampStore {

    public long timestamp() {
        return new Date().getTime();
    }

}
