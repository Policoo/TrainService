package com.trainservice;

import com.trainservice.dto.PassengerDto;
import com.trainservice.dto.ReservationRequest;
import com.trainservice.dto.SeatDto;
import com.trainservice.model.booking.Booking;
import com.trainservice.model.identifiers.ServiceKey;
import com.trainservice.model.routing.Route;
import com.trainservice.model.routing.RouteStop;
import com.trainservice.model.routing.Station;
import com.trainservice.model.train.Carriage;
import com.trainservice.model.train.ComfortClass;
import com.trainservice.model.train.Seat;
import com.trainservice.model.train.Service;
import com.trainservice.repository.BookingRepository;
import com.trainservice.repository.InventoryRepository;
import com.trainservice.repository.ServiceRepository;
import com.trainservice.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    private BookingService bookingService;
    private ServiceRepository serviceRepo;
    private BookingRepository bookingRepo;
    private InventoryRepository inventoryRepo;
    private ServiceKey serviceKey;

    @BeforeEach
    public void setup() {
        serviceRepo = new ServiceRepository();
        bookingRepo = new BookingRepository();
        inventoryRepo = new InventoryRepository();

        Station stationA = new Station("A");
        Station stationB = new Station("B");
        Station stationC = new Station("C");

        List<RouteStop> stops = List.of(
            new RouteStop(stationA, 0),
            new RouteStop(stationB, 100),
            new RouteStop(stationC, 200)
        );
        Route route = new Route(stops);

        Map<Integer, Seat> seats = new HashMap<>();
        seats.put(1, new Seat(1, ComfortClass.FIRST_CLASS));
        Carriage carriage = new Carriage("C1", seats);

        serviceKey = new ServiceKey("S1", LocalDate.now());
        Service service = new Service(serviceKey, route, Map.of("C1", carriage));

        serviceRepo.save(service);
        inventoryRepo.initializeService(service);

        bookingService = new BookingService(serviceRepo, bookingRepo, inventoryRepo);
    }

    @Test
    public void testMakeReservation_Success() {
        ReservationRequest request = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 1, "A", "B"))))
        );

        Booking booking = bookingService.makeReservation(request);

        assertNotNull(booking);
        assertEquals(1, booking.passengers().size());
        assertEquals("P1", booking.passengers().get(0).name());
    }

    @Test
    public void testMakeReservation_SeatAlreadyTaken() {
        ReservationRequest request = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 1, "A", "B"))))
        );

        bookingService.makeReservation(request);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.makeReservation(request);
        });

        assertTrue(exception.getMessage().contains("Seat not available"));
    }

    @Test
    public void testMakeReservation_InvalidStation() {
        ReservationRequest request = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 1, "A", "Z"))))
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.makeReservation(request);
        });

        assertTrue(exception.getMessage().contains("Invalid station"));
    }

    @Test
    public void testMakeReservation_MultiSegment_Success() {
        ReservationRequest request = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 1, "A", "C"))))
        );

        Booking booking = bookingService.makeReservation(request);
        assertNotNull(booking);

        ReservationRequest partial = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P2", List.of(new SeatDto("C1", 1, "B", "C"))))
        );

        assertThrows(IllegalArgumentException.class, () -> bookingService.makeReservation(partial));
    }

    @Test
    public void testMakeReservation_PartialOverlap() {
        ReservationRequest req1 = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 1, "A", "B"))))
        );
        bookingService.makeReservation(req1);

        ReservationRequest req2 = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P2", List.of(new SeatDto("C1", 1, "A", "C"))))
        );
        assertThrows(IllegalArgumentException.class, () -> bookingService.makeReservation(req2), "Overlap A->B vs A->C should fail");

        ReservationRequest req3 = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P3", List.of(new SeatDto("C1", 1, "B", "C"))))
        );
        assertDoesNotThrow(() -> bookingService.makeReservation(req3), "Non-overlapping B->C should succeed");
    }

    @Test
    public void testMakeReservation_InvalidDirection() {
        ReservationRequest request = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 1, "C", "A"))))
        );
        assertThrows(IllegalArgumentException.class, () -> bookingService.makeReservation(request));
    }

    @Test
    public void testMakeReservation_NonExistentSeat() {
        ReservationRequest request = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 99, "A", "B"))))
        );
        Exception ex = assertThrows(IllegalArgumentException.class, () -> bookingService.makeReservation(request));
        assertTrue(ex.getMessage().contains("Seat does not exist"));
    }

    @Test
    public void testMakeReservation_UnknownService() {
        ReservationRequest request = new ReservationRequest(
            "UNKNOWN",
            serviceKey.date(),
            List.of(new PassengerDto("P1", List.of(new SeatDto("C1", 1, "A", "B"))))
        );
        assertThrows(IllegalArgumentException.class, () -> bookingService.makeReservation(request));
    }

    @Test
    public void testMakeReservation_AtomicFailure() {
        bookingService.makeReservation(new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P_Blocker", List.of(new SeatDto("C1", 1, "A", "B"))))
        ));

        ReservationRequest mixedRequest = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(
                new PassengerDto("P1_Fail", List.of(new SeatDto("C1", 1, "A", "B"))),
                new PassengerDto("P2_OK", List.of(new SeatDto("C1", 1, "B", "C")))
            )
        );

        assertThrows(IllegalArgumentException.class, () -> bookingService.makeReservation(mixedRequest));

        ReservationRequest verifyRequest = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(new PassengerDto("P_Verify", List.of(new SeatDto("C1", 1, "B", "C"))))
        );
        assertDoesNotThrow(() -> bookingService.makeReservation(verifyRequest), "Atomic rollback failed: B->C should still be available");
    }

    @Test
    public void testMakeReservation_InternalDoubleBooking() {
        ReservationRequest request = new ReservationRequest(
            serviceKey.serviceId(),
            serviceKey.date(),
            List.of(
                new PassengerDto("P1", List.of(new SeatDto("C1", 1, "A", "B"))),
                new PassengerDto("P2", List.of(new SeatDto("C1", 1, "A", "B")))
            )
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.makeReservation(request);
        });

        assertTrue(exception.getMessage().contains("double-booked in request"), "Should detect internal double-booking: " + exception.getMessage());
    }
}
