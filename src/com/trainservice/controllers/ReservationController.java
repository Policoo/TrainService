package com.trainservice.controllers;

import com.trainservice.http.ErrorResponse;
import com.trainservice.http.Response;
import com.trainservice.http.SimpleResponse;
import com.trainservice.service.BookingService;
import com.trainservice.dto.ReservationRequest;
import com.trainservice.model.booking.Booking;
import com.trainservice.router.Controller;
import java.util.Map;
import java.util.HashMap;

public class ReservationController extends Controller {

    private final BookingService bookingService;

    public ReservationController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public Response doGet(Map<String, String> parameters, Object body) {
        String bookingId = parameters.get("id");
        if (bookingId == null) {
             return new ErrorResponse(400, "Bad Request", "Booking ID is required");
        }

        try {
            Booking booking = bookingService.getBooking(bookingId);
            return new SimpleResponse(200, booking);
        } catch (IllegalArgumentException e) {
            return new ErrorResponse(404, "Not Found", e.getMessage());
        }
    }

    @Override
    public Response doPost(Map<String, String> parameters, Object body) {
        try {
            if (!(body instanceof ReservationRequest)) {
                return new SimpleResponse(400, "Invalid request body: Expected ReservationRequest object");
            }

            ReservationRequest request = (ReservationRequest) body;
            Booking booking = bookingService.makeReservation(request);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("bookingId", booking.bookingId());
            responseBody.put("passengers", booking.passengers());

            return new SimpleResponse(201, responseBody);

        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            int statusCode = 400;
            String errorType = "Bad Request";

            if (msg.contains("not available") || msg.contains("double-booked")) {
                statusCode = 409;
                errorType = "Conflict";
            }

            return new ErrorResponse(statusCode, errorType, msg);

        } catch (Exception e) {

            return new ErrorResponse(500, "Internal Server Error", e.getMessage());

        }

    }
 
}


