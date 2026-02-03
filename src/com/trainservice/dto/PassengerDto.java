package com.trainservice.dto;

import java.util.List;

public record PassengerDto(String name, List<SeatDto> seats) {}
