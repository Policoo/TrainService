package com.trainservice.repository;

import com.trainservice.model.booking.Booking;
import com.trainservice.model.booking.Ticket;
import com.trainservice.model.identifiers.ServiceKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BookingRepository {
    private final Map<String, Booking> bookings = new HashMap<>();

    public void save(Booking booking) {
        bookings.put(booking.bookingId(), booking);
    }

    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(bookings.get(bookingId));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public List<Booking> findByService(ServiceKey serviceKey) {
        List<Booking> serviceBookings = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            // Check if any passenger has a ticket for this service
            boolean hasTicketForService = booking.passengers().stream()
                .flatMap(p -> p.tickets().stream())
                .anyMatch(t -> t.serviceKey().equals(serviceKey));

            if (hasTicketForService) {
                serviceBookings.add(booking);
            }
        }
        return serviceBookings;
    }
}
