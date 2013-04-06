package net.java.cargotracker.infrastructure.persistence.inmemory;

import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.domain.model.voyage.Voyage;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;

public class VoyageRepositoryInMem implements VoyageRepository {

    @Override
    public Voyage find(VoyageNumber voyageNumber) {
        return SampleVoyages.lookup(voyageNumber);
    }
}
