package com.walmartlabs.seats.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.walmartlabs.seats.records.Seat;
import com.walmartlabs.seats.records.Seat.SeatComparator;

import org.junit.jupiter.api.Test;

class MockSeat implements Seat {
    final private int _score;

    public MockSeat(int score) {
        this._score = score;
    }

    public SeatDesire score() {
        return new SeatDesire(this._score);
    }
}

class SeatComparatorTest {

    @Test
    void testCompare() {
        SeatComparator cmp = new SeatComparator();

        // A score of zero is always more desireable
        assertEquals(cmp.compare(new MockSeat(0), new MockSeat(1)), 1);

        // Even though 2 > -1, -1 is closer to zero so it is more desirable
        assertEquals(cmp.compare(new MockSeat(-1), new MockSeat(2)), 1);

        // Even though 1 > -1, both are equidistant from 0 so they're equally desirable
        assertEquals(cmp.compare(new MockSeat(-1), new MockSeat(1)), 0);
    }

}
