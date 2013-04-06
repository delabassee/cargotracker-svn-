package net.java.cargotracker.infrastructure.persistence.jpa;

import java.util.Date;
import java.util.List;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.UnLocode;

public class HandlingEventRepositoryTest {

    HandlingEventRepository handlingEventRepository;
    CargoRepository cargoRepository;
    LocationRepository locationRepository;

    public void testSave() {
        Location location = locationRepository.find(new UnLocode("SESTO"));

        Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
        Date completionTime = new Date(10);
        Date registrationTime = new Date(20);
        HandlingEvent event = new HandlingEvent(cargo, completionTime,
                registrationTime, HandlingEvent.Type.CLAIM, location);

        handlingEventRepository.store(event);

//        Map<String, Object> result = sjt.queryForMap("select * from HandlingEvent where id = ?", getLongId(event));
//        org.junit.Assert.assertEquals(1L, result.get("CARGO_ID"));
//        org.junit.Assert.assertEquals(new Date(10), result.get("COMPLETIONTIME"));
//        org.junit.Assert.assertEquals(new Date(20), result.get("REGISTRATIONTIME"));
//        org.junit.Assert.assertEquals("HandlingEvent.Type.CLAIM", result.get("TYPE"));
        // TODO: the rest of the columns
    }

    public void testFindEventsForCargo() throws Exception {
        TrackingId trackingId = new TrackingId("XYZ");
        List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).getDistinctEventsByCompletionTime();
        org.junit.Assert.assertEquals(12, handlingEvents.size());
    }
}