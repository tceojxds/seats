package com.walmartlabs.seats.stores;

import com.walmartlabs.seats.records.SeatHold;
import com.walmartlabs.seats.records.SeatReservation;

import java.util.Optional;

import com.walmartlabs.seats.records.Seat;

/**
 * SeatQueue is a stateful representation of the queue of seats ordered by their
 * desirability
 * 
 * This is an interface so that was can back it by memory, sql, nosql, or
 * whatever is appropriate
 */
public interface SeatQueue {
    /**
     * The number of seats available in the queue
     */
    int available(long timestamp);

    /**
     * The number of seats held + available in the quere
     */
    int size(long timestamp);

    /**
     * Reserves a set of seats that were selected by desirability. This operation is
     * not idempodent.
     */
    SeatHold reserve(long timestamp, int num, long expiration);

    /**
     * Add a seat to the queue
     */
    boolean add(Seat seat);

    Optional<SeatReservation> commit(long timestamp, SeatHold hold);
}
