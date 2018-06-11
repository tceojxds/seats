package com.walmartlabs.seats.services.ticket.rooms.grid;

import com.walmartlabs.seats.stores.SeatQueue;

public class GridRoom {
    static public void fillQueue(GridConfig cfg, SeatQueue q) {
        for (int row = 0; row < cfg.rows; row++) {
            for (int col = 0; col < cfg.seatsPerRow; col++) {
                q.add(new GridSeat(cfg, row, col));
            }
        }
    }
}
