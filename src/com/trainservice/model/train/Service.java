package com.trainservice.model.train;

import com.trainservice.model.identifiers.SeatId;
import com.trainservice.model.identifiers.ServiceKey;
import com.trainservice.model.routing.Route;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class Service {
    private final ServiceKey key;
    private final Route route;
    private final Map<String, Carriage> carriages;

    public Service(ServiceKey key, Route route, Map<String, Carriage> carriages) {
        this.key = key;
        this.route = route;
        this.carriages = Map.copyOf(carriages);
    }

    public ServiceKey getKey() {
        return key;
    }

    public Route getRoute() {
        return route;
    }

    public Map<String, Carriage> getCarriages() {
        return Collections.unmodifiableMap(carriages);
    }

    public Optional<Carriage> getCarriage(String label) {
        return Optional.ofNullable(carriages.get(label));
    }

    /**
     * Convenience method to get all seats in the service flattened by SeatId.
     */
    public Map<SeatId, Seat> getAllSeats() {
        Map<SeatId, Seat> allSeats = new HashMap<>();
        for (Carriage carriage : carriages.values()) {
            for (Seat seat : carriage.getSeats().values()) {
                allSeats.put(new SeatId(carriage.getLabel(), seat.seatNo()), seat);
            }
        }
        return Collections.unmodifiableMap(allSeats);
    }
}
