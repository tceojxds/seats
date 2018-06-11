package com.walmartlabs.seats.services.ticket.rooms.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;

import com.walmartlabs.seats.records.Seat;

import org.junit.jupiter.api.Test;

class GridSeatTest {

    @Test
    void scoreTest() {
        // The most desirable seat in this cfg is 0, 16
        GridConfig cfg = new GridConfig(9, 33);
        GridSeat mvp = new GridSeat(cfg, 0, 16);
        Comparator<Seat> cmp = new Seat.SeatComparator();

        // Sanity check
        assertEquals(mvp.score().value(), 0);
        GridSeat seat;

        seat = new GridSeat(cfg, 0, 10);
        assertEquals(seat.score().value(), 6);
        assertEquals(cmp.compare(mvp, seat), 1);

        seat = new GridSeat(cfg, 0, 25);
        assertEquals(seat.score().value(), 9);
        assertEquals(cmp.compare(mvp, seat), 1);

        seat = new GridSeat(cfg, 5, 10);
        assertEquals(seat.score().value(), 7);
        assertEquals(cmp.compare(mvp, seat), 1);
    }

}