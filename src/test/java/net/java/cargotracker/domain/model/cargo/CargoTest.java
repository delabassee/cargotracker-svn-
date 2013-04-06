package net.java.cargotracker.domain.model.cargo;

import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.TransportStatus;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.cargo.RoutingStatus;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import net.java.cargotracker.application.util.DateUtil;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingHistory;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import org.junit.Test;

public class CargoTest {

    private List<HandlingEvent> events = new ArrayList<>();
    private Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"),
            SampleLocations.STOCKHOLM).addMovement(SampleLocations.HAMBURG,
            new Date(), new Date()).addMovement(SampleLocations.HONGKONG,
            new Date(), new Date()).addMovement(SampleLocations.MELBOURNE,
            new Date(), new Date()).build();

    @Test
    public void testConstruction() {
        TrackingId trackingId = new TrackingId("XYZ");
        Date arrivalDeadline = DateUtil.toDate("2009-03-13");
        RouteSpecification routeSpecification = new RouteSpecification(
                SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE,
                arrivalDeadline);

        Cargo cargo = new Cargo(trackingId, routeSpecification);

        org.junit.Assert.assertEquals(RoutingStatus.NOT_ROUTED,
                cargo.getDelivery().getRoutingStatus());
        org.junit.Assert.assertEquals(TransportStatus.NOT_RECEIVED,
                cargo.getDelivery().getTransportStatus());
        org.junit.Assert.assertEquals(Location.UNKNOWN,
                cargo.getDelivery().getLastKnownLocation());
        org.junit.Assert.assertEquals(Voyage.NONE,
                cargo.getDelivery().getCurrentVoyage());
    }

    @Test
    public void testRoutingStatus() {
        Cargo cargo = new Cargo(new TrackingId("XYZ"),
                new RouteSpecification(SampleLocations.STOCKHOLM,
                SampleLocations.MELBOURNE, new Date()));
        final Itinerary good = new Itinerary();
        Itinerary bad = new Itinerary();
        RouteSpecification acceptOnlyGood = new RouteSpecification(
                cargo.getOrigin(), cargo.getRouteSpecification().getDestination(),
                new Date()) {
            @Override
            public boolean isSatisfiedBy(Itinerary itinerary) {
                return itinerary == good;
            }
        };

        cargo.specifyNewRoute(acceptOnlyGood);

        org.junit.Assert.assertEquals(RoutingStatus.NOT_ROUTED,
                cargo.getDelivery().getRoutingStatus());

        cargo.assignToRoute(bad);
        org.junit.Assert.assertEquals(RoutingStatus.MISROUTED,
                cargo.getDelivery().getRoutingStatus());

        cargo.assignToRoute(good);
        org.junit.Assert.assertEquals(RoutingStatus.ROUTED,
                cargo.getDelivery().getRoutingStatus());
    }

    @Test
    public void testlastKnownLocationUnknownWhenNoEvents() {
        Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(
                SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, new Date()));

        org.junit.Assert.assertEquals(Location.UNKNOWN,
                cargo.getDelivery().getLastKnownLocation());
    }

    @Test
    public void testlastKnownLocationReceived() throws Exception {
        Cargo cargo = populateCargoReceivedStockholm();

        org.junit.Assert.assertEquals(SampleLocations.STOCKHOLM,
                cargo.getDelivery().getLastKnownLocation());
    }

    @Test
    public void testlastKnownLocationClaimed() throws Exception {
        Cargo cargo = populateCargoClaimedMelbourne();

        org.junit.Assert.assertEquals(SampleLocations.MELBOURNE,
                cargo.getDelivery().getLastKnownLocation());
    }

    @Test
    public void testlastKnownLocationUnloaded() throws Exception {
        Cargo cargo = populateCargoOffHongKong();

        org.junit.Assert.assertEquals(SampleLocations.HONGKONG,
                cargo.getDelivery().getLastKnownLocation());
    }

    @Test
    public void testlastKnownLocationloaded() throws Exception {
        Cargo cargo = populateCargoOnHamburg();

        org.junit.Assert.assertEquals(SampleLocations.HAMBURG,
                cargo.getDelivery().getLastKnownLocation());
    }

    @Test
    public void testIsUnloadedAtFinalDestination() {
        Cargo cargo = setUpCargoWithItinerary(SampleLocations.HANGZOU,
                SampleLocations.TOKYO, SampleLocations.NEWYORK);
        org.junit.Assert.assertFalse(cargo.getDelivery().isUnloadedAtDestination());

        // Adding an event unrelated to unloading at final destination
        events.add(new HandlingEvent(cargo, new Date(10), new Date(),
                HandlingEvent.Type.RECEIVE, SampleLocations.HANGZOU));
        cargo.deriveDeliveryProgress(new HandlingHistory(events));
        org.junit.Assert.assertFalse(cargo.getDelivery().isUnloadedAtDestination());

        Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"),
                SampleLocations.HANGZOU).addMovement(SampleLocations.NEWYORK,
                new Date(), new Date()).build();

        // Adding an unload event, but not at the final destination
        events.add(new HandlingEvent(cargo, new Date(20), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.TOKYO, voyage));
        cargo.deriveDeliveryProgress(new HandlingHistory(events));
        org.junit.Assert.assertFalse(cargo.getDelivery().isUnloadedAtDestination());

        // Adding an event in the final destination, but not unload
        events.add(new HandlingEvent(cargo, new Date(30), new Date(),
                HandlingEvent.Type.CUSTOMS, SampleLocations.NEWYORK));
        cargo.deriveDeliveryProgress(new HandlingHistory(events));
        org.junit.Assert.assertFalse(cargo.getDelivery().isUnloadedAtDestination());

        // Finally, cargo is unloaded at final destination
        events.add(new HandlingEvent(cargo, new Date(40), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.NEWYORK, voyage));
        cargo.deriveDeliveryProgress(new HandlingHistory(events));
        org.junit.Assert.assertTrue(cargo.getDelivery().isUnloadedAtDestination());
    }

    // TODO: Generate test data some better way
    private Cargo populateCargoReceivedStockholm() throws Exception {
        Cargo cargo = new Cargo(new TrackingId("XYZ"),
                new RouteSpecification(SampleLocations.STOCKHOLM,
                SampleLocations.MELBOURNE, new Date()));

        HandlingEvent he = new HandlingEvent(cargo, getDate("2007-12-01"),
                new Date(), HandlingEvent.Type.RECEIVE, SampleLocations.STOCKHOLM);
        events.add(he);
        cargo.deriveDeliveryProgress(new HandlingHistory(events));

        return cargo;
    }

    private Cargo populateCargoClaimedMelbourne() throws Exception {
        Cargo cargo = populateCargoOffMelbourne();

        events.add(new HandlingEvent(cargo, getDate("2007-12-09"), new Date(),
                HandlingEvent.Type.CLAIM, SampleLocations.MELBOURNE));
        cargo.deriveDeliveryProgress(new HandlingHistory(events));

        return cargo;
    }

    private Cargo populateCargoOffHongKong() throws Exception {
        Cargo cargo = new Cargo(new TrackingId("XYZ"),
                new RouteSpecification(SampleLocations.STOCKHOLM,
                SampleLocations.MELBOURNE, new Date()));

        events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.STOCKHOLM, voyage));
        events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.HAMBURG, voyage));

        events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.HAMBURG, voyage));
        events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.HONGKONG, voyage));

        cargo.deriveDeliveryProgress(new HandlingHistory(events));
        return cargo;
    }

    private Cargo populateCargoOnHamburg() throws Exception {
        Cargo cargo = new Cargo(new TrackingId("XYZ"),
                new RouteSpecification(SampleLocations.STOCKHOLM,
                SampleLocations.MELBOURNE, new Date()));

        events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.STOCKHOLM, voyage));
        events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.HAMBURG, voyage));
        events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.HAMBURG, voyage));

        cargo.deriveDeliveryProgress(new HandlingHistory(events));

        return cargo;
    }

    private Cargo populateCargoOffMelbourne() throws Exception {
        Cargo cargo = new Cargo(new TrackingId("XYZ"),
                new RouteSpecification(SampleLocations.STOCKHOLM,
                SampleLocations.MELBOURNE, new Date()));

        events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.STOCKHOLM, voyage));
        events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.HAMBURG, voyage));

        events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.HAMBURG, voyage));
        events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.HONGKONG, voyage));

        events.add(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.HONGKONG, voyage));
        events.add(new HandlingEvent(cargo, getDate("2007-12-07"), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.MELBOURNE, voyage));

        cargo.deriveDeliveryProgress(new HandlingHistory(events));

        return cargo;
    }

    /*
     private Cargo populateCargoOnHongKong() throws Exception {
     Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE, new Date()));

     events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, SampleLocations.STOCKHOLM, voyage));
     events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, SampleLocations.HAMBURG, voyage));

     events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, SampleLocations.HAMBURG, voyage));
     events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, SampleLocations.HONGKONG, voyage));

     events.add(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, SampleLocations.HONGKONG, voyage));

     cargo.deriveDeliveryProgress(new HandlingHistory(events));
     return cargo;
     }
     */
    @Test
    public void testIsMisdirected() throws Exception {
        //A cargo with no itinerary is not misdirected
        Cargo cargo = new Cargo(new TrackingId("TRKID"), new RouteSpecification(
                SampleLocations.SHANGHAI, SampleLocations.GOTHENBURG, new Date()));
        org.junit.Assert.assertFalse(cargo.getDelivery().isMisdirected());

        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI,
                SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);

        //A cargo with no handling events is not misdirected
        org.junit.Assert.assertFalse(cargo.getDelivery().isMisdirected());

        Collection<HandlingEvent> handlingEvents = new ArrayList<HandlingEvent>();

        //Happy path
        handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20),
                HandlingEvent.Type.RECEIVE, SampleLocations.SHANGHAI));
        handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40),
                HandlingEvent.Type.LOAD, SampleLocations.SHANGHAI, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60),
                HandlingEvent.Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80),
                HandlingEvent.Type.LOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(90), new Date(100),
                HandlingEvent.Type.UNLOAD, SampleLocations.GOTHENBURG, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(110), new Date(120),
                HandlingEvent.Type.CLAIM, SampleLocations.GOTHENBURG));
        handlingEvents.add(new HandlingEvent(cargo, new Date(130), new Date(140),
                HandlingEvent.Type.CUSTOMS, SampleLocations.GOTHENBURG));

        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new HandlingHistory(events));
        org.junit.Assert.assertFalse(cargo.getDelivery().isMisdirected());

        //Try a couple of failing ones

        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI,
                SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);
        handlingEvents = new ArrayList<HandlingEvent>();

        handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.RECEIVE, SampleLocations.HANGZOU));
        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new HandlingHistory(events));

        org.junit.Assert.assertTrue(cargo.getDelivery().isMisdirected());


        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI,
                SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);
        handlingEvents = new ArrayList<HandlingEvent>();

        handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20),
                HandlingEvent.Type.RECEIVE, SampleLocations.SHANGHAI));
        handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40),
                HandlingEvent.Type.LOAD, SampleLocations.SHANGHAI, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60),
                HandlingEvent.Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80),
                HandlingEvent.Type.LOAD, SampleLocations.ROTTERDAM, voyage));

        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new HandlingHistory(events));

        org.junit.Assert.assertTrue(cargo.getDelivery().isMisdirected());


        cargo = setUpCargoWithItinerary(SampleLocations.SHANGHAI,
                SampleLocations.ROTTERDAM, SampleLocations.GOTHENBURG);
        handlingEvents = new ArrayList<HandlingEvent>();

        handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20),
                HandlingEvent.Type.RECEIVE, SampleLocations.SHANGHAI));
        handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40),
                HandlingEvent.Type.LOAD, SampleLocations.SHANGHAI, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60),
                HandlingEvent.Type.UNLOAD, SampleLocations.ROTTERDAM, voyage));
        handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.CLAIM, SampleLocations.ROTTERDAM));

        events.addAll(handlingEvents);
        cargo.deriveDeliveryProgress(new HandlingHistory(events));

        org.junit.Assert.assertTrue(cargo.getDelivery().isMisdirected());
    }

    private Cargo setUpCargoWithItinerary(Location origin, Location midpoint,
            Location destination) {
        Cargo cargo = new Cargo(new TrackingId("CARGO1"),
                new RouteSpecification(origin, destination, new Date()));

        Itinerary itinerary = new Itinerary(
                Arrays.asList(
                new Leg(voyage, origin, midpoint, new Date(), new Date()),
                new Leg(voyage, midpoint, destination, new Date(), new Date())));

        cargo.assignToRoute(itinerary);
        return cargo;
    }

    /**
     * Parse an ISO 8601 (YYYY-MM-DD) String to Date
     *
     * @param isoFormat String to parse.
     * @return Created date instance.
     * @throws ParseException Thrown if parsing fails.
     */
    private Date getDate(String isoFormat) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(isoFormat);
    }
}
