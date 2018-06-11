package com.walmartlabs.seats.records;

public class SeatDesire {
    private int distance;

    public SeatDesire(int distance) {
        this.distance = Math.abs(distance);
    }

    public int value() {
        return this.distance;
    }

    public int compareTo(SeatDesire y) {
        int xValue = this.value();
        int yValue = y.value();

        if (xValue < yValue) {
            return 1;
        } else if (xValue == yValue) {
            return 0;
        } else {
            return -1;
        }
    }
}
