package net.java.cargotracker.application.internal;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import net.java.cargotracker.application.ApplicationEvents;
import net.java.cargotracker.application.CargoInspected;
import net.java.cargotracker.application.CargoInspectionService;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.handling.HandlingHistory;
import org.apache.commons.lang3.Validate;

@Stateless
public class DefaultCargoInspectionService implements CargoInspectionService {

    @Inject
    private ApplicationEvents applicationEvents;
    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private HandlingEventRepository handlingEventRepository;
    
    @Inject
    @CargoInspected
    private Event<Cargo> cargoInspected;
    
    private static final Logger logger = Logger.getLogger(
            DefaultCargoInspectionService.class.getName());

    @Override
    public void inspectCargo(TrackingId trackingId) {
        // TODO Remove Apache commons dependency, it's not that essential.
        Validate.notNull(trackingId, "Tracking ID is required");

        Cargo cargo = cargoRepository.find(trackingId);
        

        if (cargo == null) {
            logger.log(Level.WARNING, "Can't inspect non-existing cargo {0}", trackingId);
            return;
        }

        HandlingHistory handlingHistory = handlingEventRepository
                .lookupHandlingHistoryOfCargo(trackingId);

        cargo.deriveDeliveryProgress(handlingHistory);
        
        
        if (cargo.getDelivery().isMisdirected()) {
            applicationEvents.cargoWasMisdirected(cargo);
        }

        if (cargo.getDelivery().isUnloadedAtDestination()) {
            applicationEvents.cargoHasArrived(cargo);
        }

        
        cargoRepository.store(cargo);
        
        cargoInspected.fire(cargo); //Fire the event
        
        
        
    }
}