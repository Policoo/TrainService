package com.trainservice.model.booking;

import java.util.List;

public record Passenger(String name, List<Ticket> tickets) {}
