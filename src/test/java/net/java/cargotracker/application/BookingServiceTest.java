package net.java.cargotracker.application;

import java.util.Date;
import net.java.cargotracker.application.internal.DefaultBookingService;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.service.RoutingService;

public class BookingServiceTest {

    DefaultBookingService bookingService;
    CargoRepository cargoRepository;
    LocationRepository locationRepository;
    RoutingService routingService;

    protected void setUp() throws Exception {
//        cargoRepository = createMock(CargoRepository.class);
//        locationRepository = createMock(LocationRepository.class);
//        routingService = createMock(RoutingService.class);
//        bookingService = new DefaultBookingService(cargoRepository, locationRepository, routingService);
    }

    public void testRegisterNew() {
        TrackingId expectedTrackingId = new TrackingId("TRK1");
        UnLocode fromUnlocode = new UnLocode("USCHI");
        UnLocode toUnlocode = new UnLocode("SESTO");

//        expect(cargoRepository.nextTrackingId()).andReturn(expectedTrackingId);
//        expect(locationRepository.find(fromUnlocode)).andReturn(CHICAGO);
//        expect(locationRepository.find(toUnlocode)).andReturn(SampleLocations.STOCKHOLM);
//
//        cargoRepository.store(isA(Cargo.class));

//        replay(cargoRepository, locationRepository);

        TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, new Date());
        org.junit.Assert.assertEquals(expectedTrackingId, trackingId);
    }

    protected void tearDown() throws Exception {
//        verify(cargoRepository, locationRepository);
    }
}
