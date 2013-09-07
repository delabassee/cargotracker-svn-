package net.java.cargotracker.domain.model.handling;

import java.util.Date;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent.Type;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;

public class HandlingEventFactoryTest {

    HandlingEventFactory factory;
    CargoRepository cargoRepository;
    VoyageRepository voyageRepository;
    LocationRepository locationRepository;
    TrackingId trackingId;
    Cargo cargo;

    protected void setUp() throws Exception {

//        cargoRepository = createMock(CargoRepository.class);
//        voyageRepository = new VoyageRepositoryInMem();
//        locationRepository = new LocationRepositoryInMem();
//        factory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
        trackingId = new TrackingId("ABC");
        RouteSpecification routeSpecification = new RouteSpecification(
                SampleLocations.TOKYO, SampleLocations.HELSINKI, new Date());
        cargo = new Cargo(trackingId, routeSpecification);
    }

    public void testCreateHandlingEventWithCarrierMovement() throws Exception {
//        expect(cargoRepository.find(trackingId)).andReturn(cargo);
//
//        replay(cargoRepository);

        VoyageNumber voyageNumber = SampleVoyages.CM001.getVoyageNumber();
        UnLocode unLocode = SampleLocations.STOCKHOLM.getUnLocode();
        HandlingEvent handlingEvent = factory.createHandlingEvent(
                new Date(), new Date(100), trackingId, voyageNumber, unLocode,
                Type.LOAD);

        org.junit.Assert.assertNotNull(handlingEvent);
        org.junit.Assert.assertEquals(SampleLocations.STOCKHOLM,
                handlingEvent.getLocation());
        org.junit.Assert.assertEquals(SampleVoyages.CM001,
                handlingEvent.getVoyage());
        org.junit.Assert.assertEquals(cargo, handlingEvent.getCargo());
        org.junit.Assert.assertEquals(new Date(100),
                handlingEvent.getCompletionTime());
        org.junit.Assert.assertTrue(handlingEvent.getRegistrationTime().
                before(new Date(System.currentTimeMillis() + 1)));
    }

    public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
//        expect(cargoRepository.find(trackingId)).andReturn(cargo);
//
//        replay(cargoRepository);

        UnLocode unLocode = SampleLocations.STOCKHOLM.getUnLocode();
        HandlingEvent handlingEvent = factory.createHandlingEvent(new Date(),
                new Date(100), trackingId, null, unLocode, Type.CLAIM);

        org.junit.Assert.assertNotNull(handlingEvent);
        org.junit.Assert.assertEquals(SampleLocations.STOCKHOLM,
                handlingEvent.getLocation());
        org.junit.Assert.assertEquals(Voyage.NONE, handlingEvent.getVoyage());
        org.junit.Assert.assertEquals(cargo, handlingEvent.getCargo());
        org.junit.Assert.assertEquals(new Date(100),
                handlingEvent.getCompletionTime());
        org.junit.Assert.assertTrue(handlingEvent.getRegistrationTime()
                .before(new Date(System.currentTimeMillis() + 1)));
    }

    public void testCreateHandlingEventUnknownLocation() throws Exception {
//        expect(cargoRepository.find(trackingId)).andReturn(cargo);
//
//        replay(cargoRepository);

        UnLocode invalid = new UnLocode("NOEXT");
        try {
            factory.createHandlingEvent(
                    new Date(), new Date(100), trackingId,
                    SampleVoyages.CM001.getVoyageNumber(), invalid, Type.LOAD);
            org.junit.Assert.fail("Expected UnknownLocationException");
        } catch (UnknownLocationException expected) {
        }
    }

    public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
//        expect(cargoRepository.find(trackingId)).andReturn(cargo);

//        replay(cargoRepository);
        try {
            VoyageNumber invalid = new VoyageNumber("XXX");
            factory.createHandlingEvent(new Date(), new Date(100), trackingId,
                    invalid, SampleLocations.STOCKHOLM.getUnLocode(), Type.LOAD);
            org.junit.Assert.fail("Expected UnknownVoyageException");
        } catch (UnknownVoyageException expected) {
        }
    }

    public void testCreateHandlingEventUnknownTrackingId() throws Exception {
//        expect(cargoRepository.find(trackingId)).andReturn(null);
//
//        replay(cargoRepository);

        try {
            factory.createHandlingEvent(
                    new Date(), new Date(100), trackingId,
                    SampleVoyages.CM001.getVoyageNumber(),
                    SampleLocations.STOCKHOLM.getUnLocode(), Type.LOAD);
            org.junit.Assert.fail("Expected UnknownCargoException");
        } catch (UnknownCargoException expected) {
        }
    }

    protected void tearDown() throws Exception {
//        verify(cargoRepository);
    }
}
