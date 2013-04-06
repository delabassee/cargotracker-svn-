package net.java.cargotracker.domain.model.handling;

import net.java.cargotracker.domain.model.cargo.TrackingId;

/**
 * Thrown when trying to register an event with an unknown tracking id.
 */
public class UnknownCargoException extends CannotCreateHandlingEventException {

    private TrackingId trackingId;

    /**
     * @param trackingId cargo tracking id
     */
    public UnknownCargoException(TrackingId trackingId) {
        this.trackingId = trackingId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "No cargo with tracking id " + trackingId.getIdString()
                + " exists in the system";
    }
}
