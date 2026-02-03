package com.trainservice.repository;

import com.trainservice.model.identifiers.SeatId;
import com.trainservice.model.identifiers.ServiceKey;
import com.trainservice.model.train.Service;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InventoryRepository {
    private final Map<ServiceKey, Map<SeatId, BitSet>> occupancy = new ConcurrentHashMap<>();

    public void initializeService(Service service) {
        Map<SeatId, BitSet> seatOccupancy = new HashMap<>();
        int stopCount = service.getRoute().getStops().size();
        service.getAllSeats().keySet().forEach(seatId -> 
            seatOccupancy.put(seatId, new BitSet(stopCount))
        );

        occupancy.put(service.getKey(), seatOccupancy);
    }

    public boolean isAvailable(ServiceKey key, SeatId seatId, int startStopIndex, int endStopIndex) {
        Map<SeatId, BitSet> serviceOccupancy = occupancy.get(key);
        if (serviceOccupancy == null) {
             return false;
        }
        BitSet seatBits = serviceOccupancy.get(seatId);
        if (seatBits == null) {
            return false;
        }

        int nextSet = seatBits.nextSetBit(startStopIndex);
        return nextSet == -1 || nextSet >= endStopIndex;
    }

    public void reserve(ServiceKey key, SeatId seatId, int startStopIndex, int endStopIndex) {
        Map<SeatId, BitSet> serviceOccupancy = occupancy.get(key);
        if (serviceOccupancy == null) {
             throw new IllegalArgumentException("Service inventory not found: " + key);
        }
        BitSet seatBits = serviceOccupancy.get(seatId);
        if (seatBits == null) {
            throw new IllegalArgumentException("Seat inventory not found: " + seatId);
        }

        seatBits.set(startStopIndex, endStopIndex);
    }

    public BitSet getOccupancy(ServiceKey key, SeatId seatId) {
        Map<SeatId, BitSet> serviceOccupancy = occupancy.get(key);
        if (serviceOccupancy != null && serviceOccupancy.containsKey(seatId)) {
            return (BitSet) serviceOccupancy.get(seatId).clone();
        }
        return new BitSet();
    }
}
