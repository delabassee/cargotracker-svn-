package net.java.cargotracker.domain.model.handling;

import net.java.cargotracker.domain.model.handling.HandlingEvent;
import java.util.Arrays;
import java.util.Date;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import org.junit.Test;

public class HandlingEventTest {

    private TrackingId trackingId = new TrackingId("XYZ");
    private RouteSpecification routeSpecification = new RouteSpecification(
            SampleLocations.HONGKONG, SampleLocations.NEWYORK, new Date());
    private Cargo cargo = new Cargo(trackingId, routeSpecification);

    @Test
    public void testNewWithCarrierMovement() {
        HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.HONGKONG,
                SampleVoyages.CM003);
        org.junit.Assert.assertEquals(SampleLocations.HONGKONG, e1.getLocation());

        HandlingEvent e2 = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.NEWYORK,
                SampleVoyages.CM003);
        org.junit.Assert.assertEquals(SampleLocations.NEWYORK, e2.getLocation());

        // These event types prohibit a carrier movement association
        for (HandlingEvent.Type type : Arrays.asList(HandlingEvent.Type.CLAIM,
                HandlingEvent.Type.RECEIVE, HandlingEvent.Type.CUSTOMS)) {
            try {
                HandlingEvent handlingEvent = new HandlingEvent(cargo,
                        new Date(), new Date(), type, SampleLocations.HONGKONG,
                        SampleVoyages.CM003);
                org.junit.Assert.fail("Handling event type " + type
                        + " prohibits carrier movement");
            } catch (IllegalArgumentException expected) {
            }
        }

        // These event types requires a carrier movement association
        for (HandlingEvent.Type type : Arrays.asList(HandlingEvent.Type.LOAD,
                HandlingEvent.Type.UNLOAD)) {
            try {
                HandlingEvent handlingEvent = new HandlingEvent(cargo,
                        new Date(), new Date(), type, SampleLocations.HONGKONG,
                        null);
                org.junit.Assert.fail("Handling event type " + type
                        + " requires carrier movement");
            } catch (NullPointerException expected) {
            }
        }
    }

    @Test
    public void testNewWithLocation() {
        HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.CLAIM, SampleLocations.HELSINKI);
        org.junit.Assert.assertEquals(SampleLocations.HELSINKI, e1.getLocation());
    }

    @Test
    public void testCurrentLocationLoadEvent() throws Exception {
        HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.CHICAGO,
                SampleVoyages.CM004);

        org.junit.Assert.assertEquals(SampleLocations.CHICAGO, ev.getLocation());
    }

    @Test
    public void testCurrentLocationUnloadEvent() throws Exception {
        HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.HAMBURG,
                SampleVoyages.CM004);

        org.junit.Assert.assertEquals(SampleLocations.HAMBURG, ev.getLocation());
    }

    @Test
    public void testCurrentLocationReceivedEvent() throws Exception {
        HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.RECEIVE, SampleLocations.CHICAGO);

        org.junit.Assert.assertEquals(SampleLocations.CHICAGO, ev.getLocation());
    }

    @Test
    public void testCurrentLocationClaimedEvent() throws Exception {
        HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.CLAIM, SampleLocations.CHICAGO);

        org.junit.Assert.assertEquals(SampleLocations.CHICAGO, ev.getLocation());
    }
}