package com.trainservice.model.booking;

import com.trainservice.model.identifiers.SeatId;
import com.trainservice.model.identifiers.ServiceKey;
import com.trainservice.model.routing.Station;

public record Ticket(String ticketId, ServiceKey serviceKey, SeatId seatId, Station origin, Station destination) {}
