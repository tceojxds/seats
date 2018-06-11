package com.walmartlabs.seats.records;

/**
 * SeatReservation is an immutable representation of a committed set of Seats
 */
public class SeatReservation {
    final String key;

    public SeatReservation(SeatHold hold) {
        // We currently don't have to lookup the key so, I'm not going to predict
        // the future and spend brian cycles
        // common up with a way to generate a unique confirmation key.
        // Does the key need to be unique per email address? Globally unique? Human
        // readible? These properties will
        // dictate how much work is required to generate the key.
        this.key = String.format("%s", hold.key());
    }

    /**
     * This is a human readible confirmationKey that is used for getting the
     * reservation.
     */
    public String confirmationKey() {
        return this.key;
    }

}
