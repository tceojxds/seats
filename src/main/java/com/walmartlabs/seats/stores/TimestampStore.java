package com.walmartlabs.seats.stores;

/**
 * TimestampStore
 */
public interface TimestampStore {

    /**
     * Return a unix timestamp from some clock
     */
    long timestamp();
}
