package net.java.cargotracker.infrastructure.persistence.inmemory;

import java.util.Arrays;
import java.util.List;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.location.UnLocode;

public class LocationRepositoryInMem implements LocationRepository {

    @Override
    public Location find(UnLocode unLocode) {
        for (Location location : Arrays.asList(SampleLocations.CHICAGO,
                SampleLocations.DALLAS, SampleLocations.GOTHENBURG,
                SampleLocations.HAMBURG, SampleLocations.HANGZOU,
                SampleLocations.HELSINKI, SampleLocations.HONGKONG,
                SampleLocations.MELBOURNE, SampleLocations.NEWYORK,
                SampleLocations.ROTTERDAM, SampleLocations.SHANGHAI,
                SampleLocations.STOCKHOLM, SampleLocations.TOKYO)) {
            if (location.getUnLocode().equals(unLocode)) {
                return location;
            }
        }
        return null;
    }

    @Override
    public List<Location> findAll() {
        return Arrays.asList(SampleLocations.CHICAGO,
                SampleLocations.DALLAS, SampleLocations.GOTHENBURG,
                SampleLocations.HAMBURG, SampleLocations.HANGZOU,
                SampleLocations.HELSINKI, SampleLocations.HONGKONG,
                SampleLocations.MELBOURNE, SampleLocations.NEWYORK,
                SampleLocations.ROTTERDAM, SampleLocations.SHANGHAI,
                SampleLocations.STOCKHOLM, SampleLocations.TOKYO);
    }
}
