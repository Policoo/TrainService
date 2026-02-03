package com.trainservice.model.train;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public final class Carriage {
    private final String label;
    private final Map<Integer, Seat> seats;

    public Carriage(String label, Map<Integer, Seat> seats) {
        this.label = label;
        this.seats = Map.copyOf(seats);
    }

    public String getLabel() {
        return label;
    }

    public Map<Integer, Seat> getSeats() {
        return Collections.unmodifiableMap(seats);
    }

    public Optional<Seat> getSeat(int seatNo) {
        return Optional.ofNullable(seats.get(seatNo));
    }
}
