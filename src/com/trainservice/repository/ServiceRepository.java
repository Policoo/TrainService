package com.trainservice.repository;

import com.trainservice.model.train.Service;
import com.trainservice.model.identifiers.ServiceKey;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class ServiceRepository {
    private final Map<ServiceKey, Service> services = new HashMap<>();

    public void save(Service service) {
        services.put(service.getKey(), service);
    }

    public Optional<Service> findById(String serviceId, LocalDate date) {
        return Optional.ofNullable(services.get(new ServiceKey(serviceId, date)));
    }

    public Map<ServiceKey, Service> findAll() {
        return Map.copyOf(services);
    }
}
