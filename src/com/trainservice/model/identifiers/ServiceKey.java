package com.trainservice.model.identifiers;

import java.time.LocalDate;

public record ServiceKey(String serviceId, LocalDate date) {}
