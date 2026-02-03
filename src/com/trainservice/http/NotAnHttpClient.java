package com.trainservice.http;

import com.trainservice.DataSeeder;
import com.trainservice.controllers.ReservationController;
import com.trainservice.repository.BookingRepository;
import com.trainservice.repository.InventoryRepository;
import com.trainservice.repository.ServiceRepository;
import com.trainservice.service.BookingService;
import com.trainservice.router.Router;

public final class NotAnHttpClient implements HTTPClient {
    private final Router router;

    public NotAnHttpClient() {
        ServiceRepository serviceRepository = new ServiceRepository();
        BookingRepository bookingRepository = new BookingRepository();
        InventoryRepository inventoryRepository = new InventoryRepository();

        DataSeeder.seed(serviceRepository, inventoryRepository);

        BookingService bookingService = new BookingService(serviceRepository, bookingRepository, inventoryRepository);

        this.router = new Router();
        ReservationController reservationController = new ReservationController(bookingService);
        this.router.register("/reservation", reservationController);
        this.router.register("/reservation/{id}", reservationController);
    }

    @Override
    public Response post(String url, Object body) {
        return router.dispatch("POST", url, body);
    }

    @Override
    public Response get(String url) {
        return router.dispatch("GET", url, null);
    }
}
