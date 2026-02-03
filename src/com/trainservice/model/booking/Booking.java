package com.trainservice.model.booking;

import com.trainservice.model.identifiers.ServiceKey;
import java.util.List;

public record Booking(String bookingId, List<Passenger> passengers) {}
