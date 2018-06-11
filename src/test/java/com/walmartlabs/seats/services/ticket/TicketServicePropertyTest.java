package com.walmartlabs.seats.services.ticket;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.walmartlabs.seats.records.Seat;
import com.walmartlabs.seats.records.SeatHold;
import com.walmartlabs.seats.services.ticket.rooms.grid.GridConfig;
import com.walmartlabs.seats.services.ticket.rooms.grid.GridRoom;
import com.walmartlabs.seats.stores.TimestampStore;
import com.walmartlabs.seats.stores.memory.MemSeatHoldStore;
import com.walmartlabs.seats.stores.memory.MemSeatQueue;

enum Action {
    // Take hold, confirm before expiration
    TakeHoldConfirmBeforeExp,
    // Take hold, confirm after expiration
    TakeHoldConfirmAfterExp,
    // Take hold, never confirm
    TakeHoldNeverConfirm,
}

/**
 * This is our model of the world. It is a FSM that represents claimed seats
 * 
 */
class Model {
    // These are seats that have been assign to someone
    Set<Seat> claimed;
    // These are the active holds that have not expired
    HashMap<Integer, SeatHold> activeHolds;
    int seatCount;

    public Model() {
        this.claimed = new HashSet<Seat>();
        this.activeHolds = new HashMap<Integer, SeatHold>();
    }

    public void start(int seatCount) {
        this.seatCount = seatCount;
    }

    public void tickClock(long timestamp) {
        HashSet<SeatHold> expired = new HashSet<SeatHold>();

        for (SeatHold hold : this.activeHolds.values()) {
            if (hold.isExpired(timestamp)) {
                expired.add(hold);
            }
        }
        for (SeatHold hold : expired) {
            this.activeHolds.remove(hold.key());
        }
    }

    /**
     * An active hold was made
     * 
     * @return
     */
    public void hold(SeatHold hold) {
        // No hold can have the same key
        this.assertNoHoldCanHaveTheSameKey(hold);
        // No hold can hold an active hold's seats
        this.assertDoubleHold(hold);
        this.activeHolds.put(hold.key(), hold);
    }

    /**
     * A claim was allowed on a hold
     */
    public void claim(SeatHold hold) {
        this.assertNoSeatCanBeDoubleBooked(hold);
        this.addClaims(hold);
        this.activeHolds.remove(hold.key());
    }

    /**
     * A claim was made on an expired hold
     * 
     * This always fails because we can't claim an expired hold
     */
    public void claimExpired(SeatHold hold) {
        this.assertNoExpiredHolds();
    }

    public void done() {
        this.assertAllSeatHaveBeenRedeemed();
    }

    // Invariants
    // * No seat can be double booked
    // * No expired hold can be redeemed
    // * No hold can hold a seat that is already held by an active hold
    // * No hold can have the same key as another hold
    // * All seats must eventually be redeemed

    private void assertAllSeatHaveBeenRedeemed() {
        assertEquals(this.claimed.size(), this.seatCount);

    }

    void assertNoSeatCanBeDoubleBooked(SeatHold hold) {
        // No seat can be double booked
        for (Seat seat : hold.seats()) {
            assertFalse(this.claimed.contains(seat));
        }
    }

    private void assertNoExpiredHolds() {
        assertTrue(false);
    }

    private void assertDoubleHold(SeatHold hold) {
        for (SeatHold active : this.activeHolds.values()) {
            for (Seat seat : hold.seats()) {
                boolean isDoubleHold = active.seats().contains(seat);
                if (isDoubleHold) {
                    assertFalse(isDoubleHold);
                }
            }
        }
    }

    private void assertNoHoldCanHaveTheSameKey(SeatHold hold) {
        Optional<SeatHold> existing = Optional.ofNullable(this.activeHolds.get(hold.key()));
        if (existing.isPresent()) {
            assertEquals(hold, existing);
            return;
        }
    }

    private void addClaims(SeatHold hold) {
        for (Seat seat : hold.seats()) {
            this.claimed.add(seat);
        }
    }

    public void expireHold(SeatHold hold) {
        this.activeHolds.remove(hold.key());
    }
}

class MockTimestampStore implements TimestampStore {
    long timestamp;

    public MockTimestampStore(long timestamp) {
        this.timestamp = timestamp;

    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long timestamp() {
        return this.timestamp;
    }

}

/**
 * TicketServicePropertyTest
 */
public class TicketServicePropertyTest {
    TicketService service;
    MockTimestampStore timestampStore;
    MemSeatQueue q;

    // Our model
    Model model;

    @BeforeEach
    void setup() {
        GridConfig cfg = new GridConfig(9, 33);
        this.q = new MemSeatQueue(cfg.rows * cfg.seatsPerRow);
        GridRoom.fillQueue(cfg, q);

        this.timestampStore = new MockTimestampStore(0);
        this.service = new TicketServiceImpl(this.q, new MemSeatHoldStore(), this.timestampStore, 10);
        this.model = new Model();
    }

    @Test
    public void testActions() {
        SeatHold hold;
        long seed = new Date().getTime();
        Random rand = new Random(seed);
        System.out.printf("Seed: %s\n", seed);
        this.model.start(this.service.numSeatsAvailable());
        while (this.q.size(this.timestampStore.timestamp()) > 0) {
            int num = rand.nextInt(10) + 1;
            this.model.tickClock(this.timestampStore.timestamp() + 1);

            switch (randomAction(rand)) {
            case TakeHoldConfirmBeforeExp:
                hold = this.service.findAndHoldSeats(num, "");
                this.model.hold(hold);

                this.service.reserveSeats(hold.key(), "");
                this.model.claim(hold);
            case TakeHoldConfirmAfterExp:
                hold = this.service.findAndHoldSeats(num, "");
                this.model.hold(hold);

                // Advance time by the expiration
                this.timestampStore.setTimestamp(this.timestampStore.timestamp() + 11);
                this.model.tickClock(this.timestampStore.timestamp());

                String reservation = this.service.reserveSeats(hold.key(), "");
                // If we get anything but null, we've failed
                if (reservation != null) {
                    this.model.claimExpired(hold);
                }
                this.model.expireHold(hold);
                break;
            case TakeHoldNeverConfirm:
                hold = this.service.findAndHoldSeats(num, "");
                this.model.hold(hold);
                break;
            }
        }
        this.model.done();
    }

    Action randomAction(Random rand) {
        int pick = rand.nextInt(Action.values().length);
        return Action.values()[pick];
    }
}
