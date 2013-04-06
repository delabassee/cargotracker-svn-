package net.java.cargotracker.interfaces.booking.facade.internal.assembler;

import net.java.cargotracker.interfaces.booking.facade.internal.assembler.ItineraryCandidateDtoAssembler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;
import net.java.cargotracker.infrastructure.persistence.inmemory.VoyageRepositoryInMem;
import net.java.cargotracker.interfaces.booking.facade.dto.RouteCandidate;

public class ItineraryCandidateDtoAssemblerTest {

    public void testToDTO() throws Exception {
        ItineraryCandidateDtoAssembler assembler =
                new ItineraryCandidateDtoAssembler();

        Location origin = SampleLocations.STOCKHOLM;
        Location destination = SampleLocations.MELBOURNE;

        Itinerary itinerary = new Itinerary(Arrays.asList(new Leg(
                SampleVoyages.CM001, origin, SampleLocations.SHANGHAI,
                new Date(), new Date()), new Leg(SampleVoyages.CM001,
                SampleLocations.ROTTERDAM, destination, new Date(), new Date())));

        RouteCandidate dto = assembler.toDTO(itinerary);

        org.junit.Assert.assertEquals(2, dto.getLegs().size());
        net.java.cargotracker.interfaces.booking.facade.dto.Leg legDTO =
                dto.getLegs().get(0);
        org.junit.Assert.assertEquals("SampleVoyages.CM001", legDTO.getVoyageNumber());
        org.junit.Assert.assertEquals("SESTO", legDTO.getFrom());
        org.junit.Assert.assertEquals("CNSHA", legDTO.getTo());

        legDTO = dto.getLegs().get(1);
        org.junit.Assert.assertEquals("SampleVoyages.CM001", legDTO.getVoyageNumber());
        org.junit.Assert.assertEquals("NLRTM", legDTO.getFrom());
        org.junit.Assert.assertEquals("AUMEL", legDTO.getTo());
    }

    public void testFromDTO() throws Exception {
        ItineraryCandidateDtoAssembler assembler =
                new ItineraryCandidateDtoAssembler();

        List<net.java.cargotracker.interfaces.booking.facade.dto.Leg> legs =
                new ArrayList<net.java.cargotracker.interfaces.booking.facade.dto.Leg>();
        legs.add(new net.java.cargotracker.interfaces.booking.facade.dto.Leg(
                "SampleVoyages.CM001", "AAAAA", "BBBBB", new Date(), new Date()));
        legs.add(new net.java.cargotracker.interfaces.booking.facade.dto.Leg(
                "SampleVoyages.CM001", "BBBBB", "CCCCC", new Date(), new Date()));

//        LocationRepository locationRepository = createMock(LocationRepository.class);
//        expect(locationRepository.find(new UnLocode("AAAAA"))).andReturn(SampleLocations.HONGKONG);
//        expect(locationRepository.find(new UnLocode("BBBBB"))).andReturn(SampleLocations.TOKYO).times(2);
//        expect(locationRepository.find(new UnLocode("CCCCC"))).andReturn(SampleLocations.CHICAGO);

        VoyageRepository voyageRepository = new VoyageRepositoryInMem();

//        replay(locationRepository);

        // Tested call
//        Itinerary itinerary = assembler.fromDTO(new RouteCandidate(legs),
//                voyageRepository, locationRepository);

//        org.junit.Assert.assertNotNull(itinerary);
//        org.junit.Assert.assertNotNull(itinerary.getLegs());
//        org.junit.Assert.assertEquals(2, itinerary.getLegs().size());

//        Leg leg1 = itinerary.getLegs().get(0);
//        org.junit.Assert.assertNotNull(leg1);
//        org.junit.Assert.assertEquals(SampleLocations.HONGKONG, leg1.getLoadLocation());
//        org.junit.Assert.assertEquals(SampleLocations.TOKYO, leg1.getUnloadLocation());

//        Leg leg2 = itinerary.getLegs().get(1);
//        org.junit.Assert.assertNotNull(leg2);
//        org.junit.Assert.assertEquals(SampleLocations.TOKYO, leg2.getLoadLocation());
//        org.junit.Assert.assertEquals(SampleLocations.CHICAGO, leg2.getUnloadLocation());
    }
}
