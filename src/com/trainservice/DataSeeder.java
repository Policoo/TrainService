package com.trainservice;

import com.trainservice.model.identifiers.ServiceKey;
import com.trainservice.model.routing.Route;
import com.trainservice.model.routing.RouteStop;
import com.trainservice.model.routing.Station;
import com.trainservice.model.train.Carriage;
import com.trainservice.model.train.ComfortClass;
import com.trainservice.model.train.Seat;
import com.trainservice.model.train.Service;
import com.trainservice.repository.InventoryRepository;
import com.trainservice.repository.ServiceRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSeeder {

        public static void seed(ServiceRepository serviceRepo, InventoryRepository inventoryRepo) {

            Station london = new Station("London");
            Station paris = new Station("Paris");
            Station amsterdam = new Station("Amsterdam");
            Station berlin = new Station("Berlin");
            Station lille = new Station("Lille");
            Station calais = new Station("Calais");
            Station brussels = new Station("Brussels");
            Station antwerp = new Station("Antwerp");
            Station osnabruck = new Station("Osnabrück");
            Station hannover = new Station("Hannover");

            List<RouteStop> stops5160 = List.of(
                new RouteStop(london, 0),
                new RouteStop(paris, 450),
                new RouteStop(brussels, 760),
                new RouteStop(amsterdam, 970)
            );
            Route route5160 = new Route(stops5160);

            List<RouteStop> stopsParisLondon = List.of(
                new RouteStop(paris, 0),
                new RouteStop(lille, 225),
                new RouteStop(calais, 330),
                new RouteStop(london, 480)
            );
            Route routeParisLondon = new Route(stopsParisLondon);

            List<RouteStop> stopsParisAmsterdam = List.of(
                new RouteStop(paris, 0),
                new RouteStop(brussels, 310),
                new RouteStop(antwerp, 360),
                new RouteStop(amsterdam, 520)
            );
            Route routeParisAmsterdam = new Route(stopsParisAmsterdam);

            List<RouteStop> stopsAmsterdamBerlin = List.of(
                new RouteStop(amsterdam, 0),
                new RouteStop(osnabruck, 240),
                new RouteStop(hannover, 380),
                new RouteStop(berlin, 650)
            );
            Route routeAmsterdamBerlin = new Route(stopsAmsterdamBerlin);

            Map<String, Carriage> carriages = createStandardCarriages();

            LocalDate date = LocalDate.of(2021, 4, 1);

            seedService(serviceRepo, inventoryRepo, "5160", date, route5160, carriages);
            seedService(serviceRepo, inventoryRepo, "7001", date, routeParisLondon, carriages);
            seedService(serviceRepo, inventoryRepo, "8002", date, routeParisAmsterdam, carriages);
            seedService(serviceRepo, inventoryRepo, "9003", date, routeAmsterdamBerlin, carriages);
        }

        private static Map<String, Carriage> createStandardCarriages() {
            Map<String, Carriage> carriages = new HashMap<>();

            Map<Integer, Seat> seatsA = new HashMap<>();
            for (int i = 1; i <= 12; i++) {
                seatsA.put(i, new Seat(i, ComfortClass.FIRST_CLASS)); 
            }
            carriages.put("A", new Carriage("A", seatsA));

            Map<Integer, Seat> seatsB = new HashMap<>();
            for (int i = 1; i <= 12; i++) {
                seatsB.put(i, new Seat(i, ComfortClass.SECOND_CLASS));
            }
            carriages.put("B", new Carriage("B", seatsB));

            Map<Integer, Seat> seatsH = new HashMap<>();
            for (int i = 1; i <= 12; i++) seatsH.put(i, new Seat(i, ComfortClass.SECOND_CLASS));
            carriages.put("H", new Carriage("H", seatsH));

            Map<Integer, Seat> seatsN = new HashMap<>();
            for (int i = 1; i <= 12; i++) seatsN.put(i, new Seat(i, ComfortClass.SECOND_CLASS));
            carriages.put("N", new Carriage("N", seatsN));

            Map<Integer, Seat> seatsT = new HashMap<>();
            for (int i = 1; i <= 12; i++) seatsT.put(i, new Seat(i, ComfortClass.FIRST_CLASS));
            carriages.put("T", new Carriage("T", seatsT));

            return carriages;
        }

        private static void seedService(ServiceRepository serviceRepo, InventoryRepository inventoryRepo, 
                                     String serviceId, LocalDate date, Route route, Map<String, Carriage> carriages) {
            ServiceKey key = new ServiceKey(serviceId, date);
            Service service = new Service(key, route, carriages);
            serviceRepo.save(service);
            inventoryRepo.initializeService(service);
            System.out.println("Seeded Service: " + key + " with route " + route.getStops().get(0).station().name() + " -> " + route.getStops().get(route.getStops().size()-1).station().name());
        }


    
}
