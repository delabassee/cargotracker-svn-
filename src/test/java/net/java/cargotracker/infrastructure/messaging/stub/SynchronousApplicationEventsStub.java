package net.java.cargotracker.infrastructure.messaging.stub;

import net.java.cargotracker.application.ApplicationEvents;
import net.java.cargotracker.application.CargoInspectionService;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.interfaces.handling.HandlingEventRegistrationAttempt;

public class SynchronousApplicationEventsStub implements ApplicationEvents {

    CargoInspectionService cargoInspectionService;

    public void setCargoInspectionService(
            CargoInspectionService cargoInspectionService) {
        this.cargoInspectionService = cargoInspectionService;
    }

    @Override
    public void cargoWasHandled(HandlingEvent event) {
        System.out.println("EVENT: cargo was handled: " + event);
        cargoInspectionService.inspectCargo(event.getCargo().getTrackingId());
    }

    @Override
    public void cargoWasMisdirected(Cargo cargo) {
        System.out.println("EVENT: cargo was misdirected");
    }

    @Override
    public void cargoHasArrived(Cargo cargo) {
        System.out.println("EVENT: cargo has arrived: "
                + cargo.getTrackingId().getIdString());
    }

    @Override
    public void receivedHandlingEventRegistrationAttempt(
            HandlingEventRegistrationAttempt attempt) {
        System.out.println("EVENT: received handling event registration attempt");
    }
}
