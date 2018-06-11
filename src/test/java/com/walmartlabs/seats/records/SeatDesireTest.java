package com.walmartlabs.seats.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class SeatDesireTest {

    @Test
    void testCompareTo() {
        // 0 > 1
        assertEquals(new SeatDesire(0).compareTo(new SeatDesire(1)), 1);

        // 1 < 0
        assertEquals(new SeatDesire(1).compareTo(new SeatDesire(0)), -1);

        // 1 == 1
        assertEquals(new SeatDesire(1).compareTo(new SeatDesire(1)), 0);

        // 1 == -1, distance does not have direction
        assertEquals(new SeatDesire(-1).compareTo(new SeatDesire(1)), 0);

    }

}
