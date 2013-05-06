package net.java.cargotracker.interfaces.booking.facade.internal.assembler;

import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.Leg;
import net.java.cargotracker.domain.model.cargo.RoutingStatus;
import net.java.cargotracker.interfaces.booking.facade.dto.CargoRoute;

public class CargoRouteDtoAssembler {

    public CargoRoute toDto(Cargo cargo) {
        CargoRoute dto = new CargoRoute(cargo.getTrackingId().getIdString(),
                cargo.getOrigin().getName(), cargo.getRouteSpecification().getDestination()
                .getName(), cargo.getRouteSpecification().getArrivalDeadline(),
                cargo.getDelivery().getRoutingStatus()
                .sameValueAs(RoutingStatus.MISROUTED));
        for (Leg leg : cargo.getItinerary().getLegs()) {
            dto.addLeg(leg.getVoyage().getVoyageNumber().getIdString(), leg
                    .getLoadLocation().getUnLocode().getIdString(), leg.getUnloadLocation()
                    .getUnLocode().getIdString(), leg.getLoadTime(), leg.getUnloadTime());
        }

        return dto;
    }
}
