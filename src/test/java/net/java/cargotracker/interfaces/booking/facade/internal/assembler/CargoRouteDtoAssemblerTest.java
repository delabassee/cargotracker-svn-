package net.java.cargotracker.interfaces.booking.facade.internal.assembler;

import net.java.cargotracker.interfaces.booking.facade.internal.assembler.CargoRouteDtoAssembler;
import java.util.Arrays;
import java.util.Date;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.interfaces.booking.facade.dto.CargoRoute;

public class CargoRouteDtoAssemblerTest {

    public void testToDTO() throws Exception {
        CargoRouteDtoAssembler assembler = new CargoRouteDtoAssembler();

        Location origin = SampleLocations.STOCKHOLM;
        Location destination = SampleLocations.MELBOURNE;
        Cargo cargo = new Cargo(new TrackingId("XYZ"),
                new RouteSpecification(origin, destination, new Date()));

        Itinerary itinerary = new Itinerary(Arrays.asList(new Leg(
                SampleVoyages.CM001, origin, SampleLocations.SHANGHAI,
                new Date(), new Date()), new Leg(SampleVoyages.CM001,
                SampleLocations.ROTTERDAM, destination, new Date(), new Date())));

        cargo.assignToRoute(itinerary);

        CargoRoute dto = assembler.toDto(cargo);

        org.junit.Assert.assertEquals(2, dto.getLegs().size());

        net.java.cargotracker.interfaces.booking.facade.dto.Leg legDto =
                dto.getLegs().get(0);
        org.junit.Assert.assertEquals("SampleVoyages.CM001", legDto.getVoyageNumber());
        org.junit.Assert.assertEquals("SESTO", legDto.getFrom());
        org.junit.Assert.assertEquals("CNSHA", legDto.getTo());

        legDto = dto.getLegs().get(1);
        org.junit.Assert.assertEquals("SampleVoyages.CM001", legDto.getVoyageNumber());
        org.junit.Assert.assertEquals("NLRTM", legDto.getFrom());
        org.junit.Assert.assertEquals("AUMEL", legDto.getTo());
    }

    public void testToDTO_NoItinerary() throws Exception {
        CargoRouteDtoAssembler assembler = new CargoRouteDtoAssembler();

        Cargo cargo = new Cargo(new TrackingId("XYZ"),
                new RouteSpecification(SampleLocations.STOCKHOLM,
                SampleLocations.MELBOURNE, new Date()));
        CargoRoute dto = assembler.toDto(cargo);

        org.junit.Assert.assertEquals("XYZ", dto.getTrackingId());
        org.junit.Assert.assertEquals("SESTO", dto.getOrigin());
        org.junit.Assert.assertEquals("AUMEL", dto.getFinalDestination());
        org.junit.Assert.assertTrue(dto.getLegs().isEmpty());
    }
}