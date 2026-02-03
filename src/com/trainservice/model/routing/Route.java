package com.trainservice.model.routing;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Route {
    private final List<RouteStop> stops;
    private final Map<Station, Integer> index;

    public Route(List<RouteStop> stops) {
        this.stops = List.copyOf(stops);
        Map<Station, Integer> tempIndex = new HashMap<>();

        for (int i = 0; i < this.stops.size(); i++) {
            tempIndex.put(this.stops.get(i).station(), i);
        }

        this.index = Collections.unmodifiableMap(tempIndex);
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public int getStationIndex(Station station) {
        return index.getOrDefault(station, -1);
    }
}
