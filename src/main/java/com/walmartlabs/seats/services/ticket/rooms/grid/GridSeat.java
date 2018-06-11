package com.walmartlabs.seats.services.ticket.rooms.grid;

import com.walmartlabs.seats.records.Seat;
import com.walmartlabs.seats.records.SeatDesire;

public class GridSeat implements Seat {
    final GridConfig cfg;
    final int row;
    final int col;
    final Point frontAndCenter;

    public GridSeat(GridConfig cfg, int row, int col) {
        this.cfg = cfg;
        this.row = row;
        this.col = col;
        this.frontAndCenter = new Point(0, this.cfg.seatsPerRow / 2);
    }

    public SeatDesire score() {
        Point adjusted = new Point(this.row, this.col);
        return new SeatDesire(frontAndCenter.distance(adjusted));
    }

    public String toString() {
        return String.format("GridSeat(%s,%s)", this.row, this.col);
    }

    public boolean equal(GridSeat y) {
        return this.row == y.row && this.col == y.col;
    }

}

class Point {
    final int row;
    final int col;

    public Point(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int distance(Point y) {
        // our old friend the pythagorean theorem
        double distance = Math.sqrt(Math.pow(y.col - this.col, 2) + Math.pow(y.row - this.row, 2));
        return (int) distance;
    }
}
