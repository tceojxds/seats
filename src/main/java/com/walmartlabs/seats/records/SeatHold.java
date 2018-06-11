package com.walmartlabs.seats.records;

import java.util.Set;

/**
 * This is an immutable representation of a SeatHold
 * 
 * The SeatHold has the ability to expire or commit the seatSet it holds.
 * 
 */
public class SeatHold {
    long expiration;
    int seatHoldId;
    String email;
    Set<Seat> seatSet;

    public SeatHold() {
        this(0, 0, "", null);
    }

    private SeatHold(int expiration, int seatHoldId, String email, Set<Seat> seatSet) {
        this.expiration = expiration;
        this.seatHoldId = seatHoldId;
        this.email = email;
        this.seatSet = seatSet;
    }

    /**
     * Determine if this SeatHold has expired
     * 
     * @param timestamp is a unix timestamp of the current time
     */
    public boolean isExpired(long timestamp) {
        return this.expiration < timestamp;
    }

    public int key() {
        return this.seatHoldId;
    }

    public Set<Seat> seats() {
        return this.seatSet;
    }

    public SeatHold withExpiration(long expiration) {
        this.expiration = expiration;
        return this;
    }

    public SeatHold withId(int seatHoldId) {
        this.seatHoldId = seatHoldId;
        return this;
    }

    public SeatHold withEmail(String email) {
        this.email = email;
        return this;
    }

    public SeatHold withSeats(Set<Seat> seatSet) {
        this.seatSet = seatSet;
        return this;
    }

    public String toString() {
        return String.format("SeatHold(%s)", this.key());
    }
}
