package com.walmartlabs.seats.services.ticket;

import java.util.Optional;

import com.walmartlabs.seats.records.SeatHold;
import com.walmartlabs.seats.records.SeatReservation;
import com.walmartlabs.seats.stores.SeatHoldStore;
import com.walmartlabs.seats.stores.SeatQueue;
import com.walmartlabs.seats.stores.TimestampStore;

/**
 * TicketServiceImpl implements a TicketService that uses a room that is a
 * rectangle.
 */
public class TicketServiceImpl implements TicketService {

    final SeatQueue seats;
    final SeatHoldStore holds;
    final TimestampStore ts;
    final long holdLifespan;

    /**
     * Create a rectangular room using rows and number of seatsPerRow
     * 
     * @param seats        an implementation of a SeatQueue, it is assumed that the
     *                     queue has already been populated
     * @param holds        implementation of a SeatHoldStore for keeping track of
     *                     the holds
     * @param ts           The timestamp store to get timestamps from
     * @param holdLifespan The number of milliseconds that a hold can live for
     */
    public TicketServiceImpl(SeatQueue seats, SeatHoldStore holds, TimestampStore ts, long holdLifespan) {
        this.seats = seats;
        this.holds = holds;
        this.ts = ts;
        this.holdLifespan = holdLifespan;
    }

    public int numSeatsAvailable() {
        return this.seats.available(this.ts.timestamp());
    }

    /**
     * Find and hold the best available seats for a customer
     *
     * @param numSeats      the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related
     *         information
     */
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        // TODO: clarify what should happen when there are no more seats to reserve
        long timestamp = this.ts.timestamp();
        long expiration = timestamp + this.holdLifespan;
        SeatHold hold = seats.reserve(timestamp, numSeats, expiration).withEmail(customerEmail);
        return this.holds.store(hold);
    }

    /**
     * Commit seats held for a specific customer
     *
     * @param seatHoldId    the seat hold identifier
     * @param customerEmail the email address of the customer to which the seat hold
     *                      is assigned
     * @return a reservation confirmation code
     */
    public String reserveSeats(int seatHoldId, String customerEmail) {
        long timestamp = this.ts.timestamp();
        Optional<SeatHold> hold = holds.load(seatHoldId);
        if (!hold.isPresent()) {
            // TODO: clarify what should happend if the seatHoldId isn't valid
            return null;
        }
        Optional<SeatReservation> reservation = this.seats.commit(timestamp, hold.get());

        if (!reservation.isPresent()) {
            // TODO: clarify what should happen when the hold has expired
            return null;
        }
        return reservation.get().confirmationKey();
    }
}
