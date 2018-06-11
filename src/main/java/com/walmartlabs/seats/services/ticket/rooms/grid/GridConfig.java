package com.walmartlabs.seats.services.ticket.rooms.grid;

public class GridConfig {
    final public int rows;
    final public int seatsPerRow;

    public GridConfig(int rows, int seatsPerRow) {
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
    }
}
