package com.trainservice.service;

import com.trainservice.model.booking.Booking;
import com.trainservice.model.booking.Passenger;
import com.trainservice.model.booking.Ticket;
import com.trainservice.dto.PassengerDto;
import com.trainservice.dto.ReservationRequest;
import com.trainservice.dto.SeatDto;
import com.trainservice.model.identifiers.SeatId;
import com.trainservice.model.identifiers.ServiceKey;
import com.trainservice.model.routing.Station;
import com.trainservice.model.train.Service;
import com.trainservice.repository.BookingRepository;
import com.trainservice.repository.InventoryRepository;
import com.trainservice.repository.ServiceRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingService {
    private final ServiceRepository serviceRepo;
    private final BookingRepository bookingRepo;
    private final InventoryRepository inventoryRepo;

    public BookingService(ServiceRepository serviceRepo, BookingRepository bookingRepo, InventoryRepository inventoryRepo) {
        this.serviceRepo = serviceRepo;
        this.bookingRepo = bookingRepo;
        this.inventoryRepo = inventoryRepo;
    }

    public Booking makeReservation(ReservationRequest request) {
        Service service = serviceRepo.findById(request.serviceId(), request.date())
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + request.serviceId()));

        List<ValidatedPassenger> validatedPassengers = validateRequest(request, service);
        return createBooking(service.getKey(), validatedPassengers);
    }

    private List<ValidatedPassenger> validateRequest(ReservationRequest request, Service service) {
        List<ValidatedPassenger> validatedPassengers = new ArrayList<>();
        ServiceKey serviceKey = service.getKey();
        java.util.Map<SeatId, java.util.BitSet> requestOccupancy = new java.util.HashMap<>();

        for (PassengerDto pData : request.passengers()) {
            List<ValidatedTicket> validatedTickets = new ArrayList<>();
            for (SeatDto sData : pData.seats()) {
                SeatId seatId = new SeatId(sData.carriage(), sData.seatNo());
                Station origin = new Station(sData.origin());
                Station dest = new Station(sData.destination());

                int startIdx = service.getRoute().getStationIndex(origin);
                int endIdx = service.getRoute().getStationIndex(dest);

                if (startIdx == -1 || endIdx == -1) {
                    throw new IllegalArgumentException("Invalid station: " + sData.origin() + " to " + sData.destination());
                }
                if (startIdx >= endIdx) {
                    throw new IllegalArgumentException("Invalid direction: " + startIdx + " -> " + endIdx);
                }

                if (service.getCarriage(sData.carriage()).isEmpty() || 
                    service.getCarriage(sData.carriage()).get().getSeat(sData.seatNo()).isEmpty()) {
                     throw new IllegalArgumentException("Seat does not exist: " + seatId);
                }

                if (!inventoryRepo.isAvailable(serviceKey, seatId, startIdx, endIdx)) {
                    throw new IllegalArgumentException("Seat not available: " + seatId + " from " + origin.name() + " to " + dest.name());
                }

                java.util.BitSet seatRequestBits = requestOccupancy.computeIfAbsent(seatId, k -> new java.util.BitSet());
                if (seatRequestBits.nextSetBit(startIdx) != -1 && seatRequestBits.nextSetBit(startIdx) < endIdx) {
                    throw new IllegalArgumentException("Seat double-booked in request: " + seatId + " from " + origin.name() + " to " + dest.name());
                }
                seatRequestBits.set(startIdx, endIdx);

                validatedTickets.add(new ValidatedTicket(seatId, origin, dest, startIdx, endIdx));
            }
            validatedPassengers.add(new ValidatedPassenger(pData.name(), validatedTickets));
        }
        return validatedPassengers;
    }

    private Booking createBooking(ServiceKey serviceKey, List<ValidatedPassenger> validatedPassengers) {
        List<Passenger> passengers = new ArrayList<>();

        for (ValidatedPassenger vp : validatedPassengers) {
            List<Ticket> tickets = new ArrayList<>();
            for (ValidatedTicket vt : vp.tickets()) {
                inventoryRepo.reserve(serviceKey, vt.seatId(), vt.startIdx(), vt.endIdx());

                String ticketId = UUID.randomUUID().toString();
                tickets.add(new Ticket(ticketId, serviceKey, vt.seatId(), vt.origin(), vt.destination()));
            }
            passengers.add(new Passenger(vp.name(), tickets));
        }

        Booking booking = new Booking(UUID.randomUUID().toString(), passengers);
        bookingRepo.save(booking);
        return booking;
    }

    public Booking getBooking(String bookingId) {
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
    }

    private record ValidatedPassenger(String name, List<ValidatedTicket> tickets) {}
    private record ValidatedTicket(SeatId seatId, Station origin, Station destination, int startIdx, int endIdx) {}
}
