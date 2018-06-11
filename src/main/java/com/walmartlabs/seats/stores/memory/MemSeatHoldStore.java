package com.walmartlabs.seats.stores.memory;

import com.walmartlabs.seats.stores.SeatHoldStore;
import com.walmartlabs.seats.records.SeatHold;
import java.util.HashMap;
import java.util.Optional;

/**
 * MemSeatHold is a store of SeatHolds backed by a HashMap
 */
public class MemSeatHoldStore implements SeatHoldStore {
    HashMap<Integer, SeatHold> holds;
    int index;

    public MemSeatHoldStore() {
        this.holds = new HashMap<Integer, SeatHold>();
        this.index = 0;
    }

    public Optional<SeatHold> load(int seatHoldId) {
        return Optional.ofNullable(holds.get(seatHoldId));
    }

    public SeatHold store(SeatHold hold) {
        hold = hold.withId(this.index);
        this.holds.put(hold.key(), hold);
        this.index += 1;
        return hold;
    }

}
