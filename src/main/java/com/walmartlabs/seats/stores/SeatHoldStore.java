
package com.walmartlabs.seats.stores;

import java.util.Optional;

import com.walmartlabs.seats.records.SeatHold;

public interface SeatHoldStore {

    public Optional<SeatHold> load(int seatHoldId);

    public SeatHold store(SeatHold hold);
}
