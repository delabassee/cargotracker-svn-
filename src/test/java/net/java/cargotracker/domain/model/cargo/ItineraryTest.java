package net.java.cargotracker.domain.model.cargo;

import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import org.junit.Test;

public class ItineraryTest {

    private Voyage voyage = new Voyage.Builder(new VoyageNumber("0123"),
            SampleLocations.SHANGHAI).addMovement(SampleLocations.ROTTERDAM,
            new Date(), new Date()).addMovement(SampleLocations.GOTHENBURG,
            new Date(), new Date()).build();
    private Voyage wrongVoyage = new Voyage.Builder(new VoyageNumber("666"),
            SampleLocations.NEWYORK).addMovement(SampleLocations.STOCKHOLM,
            new Date(), new Date()).addMovement(SampleLocations.HELSINKI,
            new Date(), new Date()).build();

    @Test
    public void testCargoOnTrack() {
        TrackingId trackingId = new TrackingId("CARGO1");
        RouteSpecification routeSpecification = new RouteSpecification(
                SampleLocations.SHANGHAI, SampleLocations.GOTHENBURG, new Date());
        Cargo cargo = new Cargo(trackingId, routeSpecification);

        Itinerary itinerary = new Itinerary(Arrays.asList(new Leg(voyage,
                SampleLocations.SHANGHAI, SampleLocations.ROTTERDAM, new Date(),
                new Date()), new Leg(voyage, SampleLocations.ROTTERDAM,
                SampleLocations.GOTHENBURG, new Date(), new Date())));

        //Happy path
        HandlingEvent event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.RECEIVE, SampleLocations.SHANGHAI);
        org.junit.Assert.assertTrue(itinerary.isExpected(event));

        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.SHANGHAI, voyage);
        org.junit.Assert.assertTrue(itinerary.isExpected(event));

        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.ROTTERDAM, voyage);
        org.junit.Assert.assertTrue(itinerary.isExpected(event));

        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.ROTTERDAM, voyage);
        org.junit.Assert.assertTrue(itinerary.isExpected(event));

        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.GOTHENBURG, voyage);
        org.junit.Assert.assertTrue(itinerary.isExpected(event));

        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.CLAIM, SampleLocations.GOTHENBURG);
        org.junit.Assert.assertTrue(itinerary.isExpected(event));

        //Customs event changes nothing
        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.CUSTOMS, SampleLocations.GOTHENBURG);
        org.junit.Assert.assertTrue(itinerary.isExpected(event));

        //Received at the wrong location
        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.RECEIVE, SampleLocations.HANGZOU);
        org.junit.Assert.assertFalse(itinerary.isExpected(event));

        //Loaded to onto the wrong ship, correct location
        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.LOAD, SampleLocations.ROTTERDAM, wrongVoyage);
        org.junit.Assert.assertFalse(itinerary.isExpected(event));

        //Unloaded from the wrong ship in the wrong location
        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.UNLOAD, SampleLocations.HELSINKI, wrongVoyage);
        org.junit.Assert.assertFalse(itinerary.isExpected(event));

        event = new HandlingEvent(cargo, new Date(), new Date(),
                HandlingEvent.Type.CLAIM, SampleLocations.ROTTERDAM);
        org.junit.Assert.assertFalse(itinerary.isExpected(event));
    }

    @Test
    public void testNextExpectedEvent() {
    }

    @Test
    public void testCreateItinerary() {
        try {
            Itinerary itinerary = new Itinerary(new ArrayList<Leg>());
            org.junit.Assert.fail("An empty itinerary is not OK");
        } catch (IllegalArgumentException iae) {
            //Expected
        }

        try {
            List<Leg> legs = null;
            Itinerary itinerary = new Itinerary(legs);
            org.junit.Assert.fail("Null itinerary is not OK");
        } catch (NullPointerException npe) {
            //Expected
        }
    }
}