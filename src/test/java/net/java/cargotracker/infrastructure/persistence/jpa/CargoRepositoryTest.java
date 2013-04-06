package net.java.cargotracker.infrastructure.persistence.jpa;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;

public class CargoRepositoryTest {

    CargoRepository cargoRepository;
    LocationRepository locationRepository;
    VoyageRepository voyageRepository;
    HandlingEventRepository handlingEventRepository;

    public void testFindByCargoId() {
        TrackingId trackingId = new TrackingId("FGH");
        Cargo cargo = cargoRepository.find(trackingId);
        org.junit.Assert.assertEquals(SampleLocations.STOCKHOLM, cargo.getOrigin());
        org.junit.Assert.assertEquals(SampleLocations.HONGKONG,
                cargo.getRouteSpecification().getOrigin());
        org.junit.Assert.assertEquals(SampleLocations.HELSINKI,
                cargo.getRouteSpecification().getDestination());

        org.junit.Assert.assertNotNull(cargo.getDelivery());

        List<HandlingEvent> events = handlingEventRepository
                .lookupHandlingHistoryOfCargo(trackingId)
                .getDistinctEventsByCompletionTime();
        org.junit.Assert.assertEquals(2, events.size());

        HandlingEvent firstEvent = events.get(0);
        assertHandlingEvent(cargo, firstEvent, HandlingEvent.Type.RECEIVE,
                SampleLocations.HONGKONG, 100, 160, Voyage.NONE);

        HandlingEvent secondEvent = events.get(1);

        Voyage hongkongMelbourneTokyoAndBack = new Voyage.Builder(
                new VoyageNumber("0303"), SampleLocations.HONGKONG).
                addMovement(SampleLocations.MELBOURNE, new Date(), new Date()).
                addMovement(SampleLocations.TOKYO, new Date(), new Date()).
                addMovement(SampleLocations.HONGKONG, new Date(), new Date()).
                build();

        assertHandlingEvent(cargo, secondEvent, HandlingEvent.Type.LOAD,
                SampleLocations.HONGKONG, 150, 110, hongkongMelbourneTokyoAndBack);

        List<Leg> legs = cargo.getItinerary().getLegs();
        org.junit.Assert.assertEquals(3, legs.size());

        Leg firstLeg = legs.get(0);
        assertLeg(firstLeg, "0101", SampleLocations.HONGKONG,
                SampleLocations.MELBOURNE);

        Leg secondLeg = legs.get(1);
        assertLeg(secondLeg, "0101", SampleLocations.MELBOURNE,
                SampleLocations.STOCKHOLM);

        Leg thirdLeg = legs.get(2);
        assertLeg(thirdLeg, "0101", SampleLocations.STOCKHOLM,
                SampleLocations.HELSINKI);
    }

    private void assertHandlingEvent(Cargo cargo, HandlingEvent event,
            HandlingEvent.Type expectedEventType, Location expectedLocation,
            int completionTimeMs, int registrationTimeMs, Voyage voyage) {
        org.junit.Assert.assertEquals(expectedEventType, event.getType());
        org.junit.Assert.assertEquals(expectedLocation, event.getLocation());

//        Date expectedCompletionTime = SampleDataGenerator.offset(completionTimeMs);
//        org.junit.Assert.assertEquals(expectedCompletionTime, event.getCompletionTime());

//        Date expectedRegistrationTime = SampleDataGenerator.offset(registrationTimeMs);
//        org.junit.Assert.assertEquals(expectedRegistrationTime, event.getRegistrationTime());

        org.junit.Assert.assertEquals(voyage, event.getVoyage());
        org.junit.Assert.assertEquals(cargo, event.getCargo());
    }

    public void testFindByCargoIdUnknownId() {
        org.junit.Assert.assertNull(cargoRepository.find(new TrackingId("UNKNOWN")));
    }

    private void assertLeg(Leg firstLeg, String vn, Location expectedFrom,
            Location expectedTo) {
        org.junit.Assert.assertEquals(new VoyageNumber(vn),
                firstLeg.getVoyage().getVoyageNumber());
        org.junit.Assert.assertEquals(expectedFrom, firstLeg.getLoadLocation());
        org.junit.Assert.assertEquals(expectedTo, firstLeg.getUnloadLocation());
    }

    public void testSave() {
        TrackingId trackingId = new TrackingId("AAA");
        Location origin = locationRepository.find(
                SampleLocations.STOCKHOLM.getUnLocode());
        Location destination = locationRepository.find(
                SampleLocations.MELBOURNE.getUnLocode());

        Cargo cargo = new Cargo(trackingId, new RouteSpecification(origin,
                destination, new Date()));
        cargoRepository.store(cargo);

        cargo.assignToRoute(new Itinerary(Arrays.asList(
                new Leg(
                voyageRepository.find(new VoyageNumber("0101")),
                locationRepository.find(SampleLocations.STOCKHOLM.getUnLocode()),
                locationRepository.find(SampleLocations.MELBOURNE.getUnLocode()),
                new Date(), new Date()))));

//        Map<String, Object> map = sjt.queryForMap(
//                "select * from Cargo where tracking_id = ?", trackingId.getIdString());

//        org.junit.Assert.assertEquals("AAA", map.get("TRACKING_ID"));
//
//        Long originId = getLongId(origin);
//        org.junit.Assert.assertEquals(originId, map.get("SPEC_ORIGIN_ID"));
//
//        Long destinationId = getLongId(destination);
//        org.junit.Assert.assertEquals(destinationId, map.get("SPEC_DESTINATION_ID"));
//
//        getSession().clear();

        Cargo loadedCargo = cargoRepository.find(trackingId);
        org.junit.Assert.assertEquals(1, loadedCargo.getItinerary().getLegs().size());
    }

    public void testReplaceItinerary() {
        Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
//        Long cargoId = getLongId(cargo);
//        org.junit.Assert.assertEquals(3, sjt.queryForInt("select count(*) from Leg where cargo_id = ?", cargoId));

        Location legFrom = locationRepository.find(new UnLocode("FIHEL"));
        Location legTo = locationRepository.find(new UnLocode("DEHAM"));
        Itinerary newItinerary = new Itinerary(Arrays.asList(new Leg(
                SampleVoyages.CM004, legFrom, legTo, new Date(), new Date())));

        cargo.assignToRoute(newItinerary);

        cargoRepository.store(cargo);

//        org.junit.Assert.assertEquals(1, sjt.queryForInt("select count(*) from Leg where cargo_id = ?", cargoId));
    }

    public void testFindAll() {
        List<Cargo> all = cargoRepository.findAll();
        org.junit.Assert.assertNotNull(all);
        org.junit.Assert.assertEquals(6, all.size());
    }

    public void testNextTrackingId() {
        TrackingId trackingId = cargoRepository.nextTrackingId();
        org.junit.Assert.assertNotNull(trackingId);

        TrackingId trackingId2 = cargoRepository.nextTrackingId();
        org.junit.Assert.assertNotNull(trackingId2);
        org.junit.Assert.assertFalse(trackingId.equals(trackingId2));
    }
}