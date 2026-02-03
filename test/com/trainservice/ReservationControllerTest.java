package com.trainservice;

import com.trainservice.dto.PassengerDto;
import com.trainservice.dto.ReservationRequest;
import com.trainservice.dto.SeatDto;
import com.trainservice.http.NotAnHttpClient;
import com.trainservice.http.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservationControllerTest {

    @Test
    public void testGetMissingIdReturns400() {
        NotAnHttpClient client = new NotAnHttpClient();

        Response response = client.get("/reservation");
        assertEquals(400, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Bad Request", body.get("error"));
    }

    @Test
    public void testGetUnknownBookingReturns404() {
        NotAnHttpClient client = new NotAnHttpClient();

        Response response = client.get("/reservation/does-not-exist");
        assertEquals(404, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Not Found", body.get("error"));
    }

    @Test
    public void testPostDuplicateBookingReturns409() {
        NotAnHttpClient client = new NotAnHttpClient();

        ReservationRequest request = new ReservationRequest(
            "5160",
            LocalDate.of(2021, 4, 1),
            List.of(
                new PassengerDto("Alice", List.of(new SeatDto("A", 11, "Paris", "Amsterdam"))),
                new PassengerDto("Bob", List.of(new SeatDto("A", 12, "Paris", "Amsterdam")))
            )
        );

        Response first = client.post("/reservation", request);
        assertEquals(201, first.getStatusCode());

        Response second = client.post("/reservation", request);
        assertEquals(409, second.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) second.getBody();
        assertEquals("Conflict", body.get("error"));
    }

    @Test
    public void testUnknownRouteReturns404() {
        NotAnHttpClient client = new NotAnHttpClient();

        Response response = client.get("/nope");
        assertEquals(404, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Not Found"));
    }
}
