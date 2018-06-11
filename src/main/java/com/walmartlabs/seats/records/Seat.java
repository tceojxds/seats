package com.walmartlabs.seats.records;

import java.util.Comparator;

/**
 * Seat is an immutable representation of a Seat
 */
public interface Seat {
    public class SeatComparator implements Comparator<Seat> {
        public int compare(Seat x, Seat y) {
            return x.score().compareTo(y.score());
        }
    }

    /**
     * The desiriablity of the seat is the unsigned distance from zero. The closer
     * to zero, the more desirable. 0 is the most desirable. positive and negitive
     * infinity are the least desirable.
     * 
     * Java does not have unsigned ints. Any negative numbers will lose their sign
     */
    SeatDesire score();
}
