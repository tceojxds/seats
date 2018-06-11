package com.walmartlabs.seats.stores.memory;

import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

import com.walmartlabs.seats.records.Seat;
import com.walmartlabs.seats.records.SeatHold;
import com.walmartlabs.seats.records.SeatReservation;
import com.walmartlabs.seats.stores.SeatQueue;

/**
 * MemSeatQueue is a thread unsafe SeatQueue that is backed by a PriorityQueue
 */
public class MemSeatQueue implements SeatQueue {

    PriorityQueue<Seat> _queue;
    HashSet<SeatHold> holds;

    /**
     * @param lifespan is the number of milliseconds that a hold should live for
     */
    public MemSeatQueue(int size) {
        this._queue = new PriorityQueue<Seat>(size, new Seat.SeatComparator());
        this.holds = new HashSet<SeatHold>();
    }

    public int available(long timestamp) {
        return this.queue(timestamp).size();
    }

    public int size(long timestamp) {
        int size = this.available(timestamp);
        for (SeatHold hold : this.holds) {
            size += hold.seats().size();
        }
        return size;
    }

    private PriorityQueue<Seat> queue(long timestamp) {
        this.expireHolds(timestamp);
        return this._queue;
    }

    public SeatHold reserve(long timestamp, int num, long expiration) {
        SeatHold hold = new SeatHold().withExpiration(expiration);
        HashSet<Seat> seats = new HashSet<Seat>();
        for (int i = 0; i < num; i++) {
            Optional<Seat> seat = Optional.ofNullable(this.queue(timestamp).poll());
            // TODO: What happends when there are no longer any seats to reserve?
            if (seat.isPresent()) {
                seats.add(seat.get());
            }
        }
        this.holds.add(hold);
        return hold.withSeats(seats);
    }

    public boolean add(Seat seat) {
        return this._queue.add(seat);
    }

    private void expireHolds(long timestamp) {
        HashSet<SeatHold> expired = new HashSet<SeatHold>();

        // Build a collection of expired holds
        for (SeatHold hold : this.holds) {
            if (hold.isExpired(timestamp)) {
                // Return the seats back to the queue
                expired.add(hold);
            }
        }

        // Process the expired holds
        for (SeatHold hold : expired) {
            this.addMany(hold.seats());
            this.holds.remove(hold);
        }
    }

    public Optional<SeatReservation> commit(long timestamp, SeatHold hold) {
        if (hold.isExpired(timestamp)) {
            return Optional.empty();
        }
        this.holds.remove(hold);
        return Optional.of(new SeatReservation(hold));
    }

    void addMany(Set<Seat> seats) {
        for (Seat seat : seats) {
            this.add(seat);
        }
    }

    public int seatsLeft() {
        return 0;
    }
}
