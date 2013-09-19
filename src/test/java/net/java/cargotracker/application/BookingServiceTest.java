package net.java.cargotracker.application;

import net.java.cargotracker.application.internal.DefaultBookingService;
import net.java.cargotracker.application.util.DateUtil;
import net.java.cargotracker.application.util.SampleDataGenerator;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.Delivery;
import net.java.cargotracker.domain.model.cargo.HandlingActivity;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.RoutingStatus;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.cargo.TransportStatus;
import net.java.cargotracker.domain.model.handling.CannotCreateHandlingEventException;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingEventFactory;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.handling.HandlingHistory;
import net.java.cargotracker.domain.model.handling.UnknownCargoException;
import net.java.cargotracker.domain.model.handling.UnknownLocationException;
import net.java.cargotracker.domain.model.handling.UnknownVoyageException;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.CarrierMovement;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.domain.model.voyage.Schedule;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;
import net.java.cargotracker.domain.service.RoutingService;
import net.java.cargotracker.domain.shared.AbstractSpecification;
import net.java.cargotracker.domain.shared.AndSpecification;
import net.java.cargotracker.domain.shared.DomainObjectUtils;
import net.java.cargotracker.domain.shared.NotSpecification;
import net.java.cargotracker.domain.shared.OrSpecification;
import net.java.cargotracker.domain.shared.Specification;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaCargoRepository;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaHandlingEventRepository;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaLocationRepository;
import net.java.cargotracker.infrastructure.persistence.jpa.JpaVoyageRepository;
import net.java.cargotracker.infrastructure.routing.ExternalRoutingService;
import net.java.pathfinder.api.TransitEdge;
import net.java.pathfinder.api.TransitPath;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Application layer integration test covering a number of otherwise fairly
 * trivial components that largely do not warrant their own tests.
 *
 * @author Reza
 */
@RunWith(Arquillian.class)
public class BookingServiceTest {

    BookingService bookingService;
    CargoRepository cargoRepository;
    LocationRepository locationRepository;
    RoutingService routingService;

    // @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "cargo-tracker-test.war")
                // Application layer component directly under test.
                .addClass(BookingService.class)
                // Domain layer components.
                .addClass(TrackingId.class)
                .addClass(UnLocode.class)
                .addClass(Itinerary.class)
                .addClass(Leg.class)
                .addClass(Voyage.class)
                .addClass(VoyageNumber.class)
                .addClass(Schedule.class)
                .addClass(CarrierMovement.class)
                .addClass(Location.class)
                .addClass(HandlingEvent.class)
                .addClass(Cargo.class)
                .addClass(RouteSpecification.class)
                .addClass(AbstractSpecification.class)
                .addClass(Specification.class)
                .addClass(AndSpecification.class)
                .addClass(OrSpecification.class)
                .addClass(NotSpecification.class)
                .addClass(Delivery.class)
                .addClass(TransportStatus.class)
                .addClass(HandlingActivity.class)
                .addClass(RoutingStatus.class)
                .addClass(HandlingHistory.class)
                .addClass(DomainObjectUtils.class)
                .addClass(CargoRepository.class)
                .addClass(LocationRepository.class)
                .addClass(VoyageRepository.class)
                .addClass(HandlingEventRepository.class)
                .addClass(HandlingEventFactory.class)
                .addClass(CannotCreateHandlingEventException.class)
                .addClass(UnknownCargoException.class)
                .addClass(UnknownVoyageException.class)
                .addClass(UnknownLocationException.class)
                .addClass(RoutingService.class)
                // Application layer components
                .addClass(DefaultBookingService.class)
                // Infrastructure layer components.
                .addClass(JpaCargoRepository.class)
                .addClass(JpaVoyageRepository.class)
                .addClass(JpaHandlingEventRepository.class)
                .addClass(JpaLocationRepository.class)
                .addClass(ExternalRoutingService.class)
                // Interface components
                .addClass(TransitPath.class)
                .addClass(TransitEdge.class)
                // Sample data.
                .addClass(SampleDataGenerator.class)
                .addClass(SampleLocations.class)
                .addClass(SampleVoyages.class)
                .addClass(DateUtil.class)
                .addClass(DateUtil.class)
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsWebInfResource("test-web.xml", "web.xml")
                .addAsWebInfResource("test-ejb-jar.xml", "ejb-jar.xml")
                .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .resolve("org.apache.commons:commons-lang3")
                        .withTransitivity().asFile());
    }

    @Test
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
        //TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, new Date());
        // org.junit.Assert.assertEquals(expectedTrackingId, trackingId);
    }

    protected void tearDown() throws Exception {
//        verify(cargoRepository, locationRepository);
    }
}
