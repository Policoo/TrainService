package com.trainservice.dto;

import java.time.LocalDate;
import java.util.List;

public record ReservationRequest(String serviceId, LocalDate date, List<PassengerDto> passengers) {}
