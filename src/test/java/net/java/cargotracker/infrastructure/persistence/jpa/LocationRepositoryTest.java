package net.java.cargotracker.infrastructure.persistence.jpa;

import java.util.List;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.UnLocode;

public class LocationRepositoryTest {

    private LocationRepository locationRepository;

    public void testFind() throws Exception {
        UnLocode melbourne = new UnLocode("AUMEL");
        Location location = locationRepository.find(melbourne);
        org.junit.Assert.assertNotNull(location);
        org.junit.Assert.assertEquals(melbourne, location.getUnLocode());

        org.junit.Assert.assertNull(locationRepository.find(new UnLocode("NOLOC")));
    }

    public void testFindAll() throws Exception {
        List<Location> allLocations = locationRepository.findAll();

        org.junit.Assert.assertNotNull(allLocations);
        org.junit.Assert.assertEquals(7, allLocations.size());
    }
}