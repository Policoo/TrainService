package com.trainservice;

import com.trainservice.http.NotAnHttpClient;
import com.trainservice.http.Response;
import com.trainservice.service.BookingService;
import com.trainservice.dto.ReservationRequest;
import com.trainservice.dto.PassengerDto;
import com.trainservice.dto.SeatDto;
import java.time.LocalDate;
import java.util.List;

public class TrainService {

    public static void main(String[] args) {
        NotAnHttpClient client = new NotAnHttpClient();

        System.out.println("--- Scenario 1: Successful Booking ---");
        ReservationRequest request1 = new ReservationRequest(
            "5160",
            LocalDate.of(2021, 4, 1),
            List.of(
                new PassengerDto(
                    "Alice",
                    List.of(new SeatDto("A", 11, "Paris", "Amsterdam"))
                ),
                new PassengerDto(
                    "Bob",
                    List.of(new SeatDto("A", 12, "Paris", "Amsterdam"))
                )
            )
        );

        Response response1 = client.post("/reservation", request1);
        System.out.println(response1);

        if (response1.getStatusCode() == 201) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> body = (java.util.Map<String, Object>) response1.getBody();
            String bookingId = (String) body.get("bookingId");

            System.out.println("\n--- Scenario 1b: Get Reservation " + bookingId + " ---");
            Response getResponse = client.get("/reservation/" + bookingId);
            System.out.println(getResponse);
        }

        System.out.println("\n--- Scenario 2: Duplicate Booking (Should Fail) ---");
        Response response2 = client.post("/reservation", request1);
        System.out.println(response2);

        System.out.println("\n--- Scenario 3: Mixed Class Booking (London -> Amsterdam) ---");
        ReservationRequest request3 = new ReservationRequest(
            "5160",
            LocalDate.of(2021, 4, 1),
            List.of(
                new PassengerDto(
                    "Charlie",
                    List.of(
                        new SeatDto("H", 1, "London", "Paris"),
                        new SeatDto("A", 1, "Paris", "Amsterdam")
                    )
                ),
                new PassengerDto(
                    "Dave",
                    List.of(
                        new SeatDto("N", 5, "London", "Paris"),
                        new SeatDto("T", 7, "Paris", "Amsterdam")
                    )
                )
            )
        );

        Response response3 = client.post("/reservation", request3);
        System.out.println(response3);

        System.out.println("\n--- Scenario 4: Duplicate Mixed Booking (Should Fail) ---");
        Response response4 = client.post("/reservation", request3);
        System.out.println(response4);
    }
}
