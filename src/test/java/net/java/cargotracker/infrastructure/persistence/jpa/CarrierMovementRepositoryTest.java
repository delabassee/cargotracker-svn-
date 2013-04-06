package net.java.cargotracker.infrastructure.persistence.jpa;

import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;

public class CarrierMovementRepositoryTest {

    VoyageRepository voyageRepository;

    public void testFind() throws Exception {
        Voyage voyage = voyageRepository.find(new VoyageNumber("0101"));
        org.junit.Assert.assertNotNull(voyage);
        org.junit.Assert.assertEquals("0101",
                voyage.getVoyageNumber().getIdString());
        /* TODO adapt
         org.junit.Assert.assertEquals(SampleLocations.STOCKHOLM, carrierMovement.departureLocation());
         org.junit.Assert.assertEquals(SampleLocations.HELSINKI, carrierMovement.arrivalLocation());
         org.junit.Assert.assertEquals(DateTestUtil.DateUtil.toDate("2007-09-23", "02:00"), carrierMovement.departureTime());
         org.junit.Assert.assertEquals(DateTestUtil.DateUtil.toDate("2007-09-23", "03:00"), carrierMovement.arrivalTime());
         */
    }
}