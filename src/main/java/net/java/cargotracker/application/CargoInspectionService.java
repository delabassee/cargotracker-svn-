package net.java.cargotracker.application;

import net.java.cargotracker.domain.model.cargo.TrackingId;

public interface CargoInspectionService {

    /**
     * Inspect cargo and send relevant notifications to interested parties, for
     * example if a cargo has been misdirected, or unloaded at the final
     * destination.
     */
    void inspectCargo(TrackingId trackingId);
}
