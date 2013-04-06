package net.java.cargotracker.infrastructure.persistence.inmemory;

import java.util.*;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.handling.HandlingHistory;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.SampleLocations;

/**
 * CargoRepositoryInMem implement the CargoRepository interface but is a test
 * class not intended for usage in real application.
 * <p/>
 * It setup a simple local hash with a number of Cargo's with TrackingId as key
 * defined at compile time.
 * <p/>
 */
public class CargoRepositoryInMem implements CargoRepository {

    private Map<String, Cargo> cargoDb;
    private HandlingEventRepository handlingEventRepository;

    /**
     * Constructor.
     */
    public CargoRepositoryInMem() {
        cargoDb = new HashMap<>();
    }

    @Override
    public Cargo find(TrackingId trackingId) {
        return cargoDb.get(trackingId.getIdString());
    }

    @Override
    public void store(Cargo cargo) {
        cargoDb.put(cargo.getTrackingId().getIdString(), cargo);
    }

    public TrackingId nextTrackingId() {
        String random = UUID.randomUUID().toString().toUpperCase();
        return new TrackingId(
                random.substring(0, random.indexOf("-")));
    }

    public List<Cargo> findAll() {
        return new ArrayList(cargoDb.values());
    }

    public void init() throws Exception {
        TrackingId xyz = new TrackingId("XYZ");
        Cargo cargoXYZ = createCargoWithDeliveryHistory(
                xyz, SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, handlingEventRepository.lookupHandlingHistoryOfCargo(xyz));
        cargoDb.put(xyz.getIdString(), cargoXYZ);

        TrackingId zyx = new TrackingId("ZYX");
        Cargo cargoZYX = createCargoWithDeliveryHistory(
                zyx, SampleLocations.MELBOURNE, SampleLocations.STOCKHOLM, handlingEventRepository.lookupHandlingHistoryOfCargo(zyx));
        cargoDb.put(zyx.getIdString(), cargoZYX);

        TrackingId abc = new TrackingId("ABC");
        Cargo cargoABC = createCargoWithDeliveryHistory(
                abc, SampleLocations.STOCKHOLM, SampleLocations.HELSINKI, handlingEventRepository.lookupHandlingHistoryOfCargo(abc));
        cargoDb.put(abc.getIdString(), cargoABC);

        TrackingId cba = new TrackingId("CBA");
        Cargo cargoCBA = createCargoWithDeliveryHistory(
                cba, SampleLocations.HELSINKI, SampleLocations.STOCKHOLM, handlingEventRepository.lookupHandlingHistoryOfCargo(cba));
        cargoDb.put(cba.getIdString(), cargoCBA);
    }

    public void setHandlingEventRepository(
            HandlingEventRepository handlingEventRepository) {
        this.handlingEventRepository = handlingEventRepository;
    }

    public static Cargo createCargoWithDeliveryHistory(TrackingId trackingId,
            Location origin,
            Location destination,
            HandlingHistory handlingHistory) {

        RouteSpecification routeSpecification = new RouteSpecification(origin, destination, new Date());
        Cargo cargo = new Cargo(trackingId, routeSpecification);
        cargo.deriveDeliveryProgress(handlingHistory);

        return cargo;
    }
}