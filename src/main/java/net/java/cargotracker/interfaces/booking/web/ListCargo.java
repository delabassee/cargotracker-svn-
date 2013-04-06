package net.java.cargotracker.interfaces.booking.web;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import net.java.cargotracker.interfaces.booking.facade.BookingServiceFacade;
import net.java.cargotracker.interfaces.booking.facade.dto.CargoRoute;

/**
 * Handles listing cargo. Operates against a dedicated service facade, and could
 * easily be rewritten as a thick Swing client. Completely separated from the
 * domain layer, unlike the tracking user interface.
 * <p/>
 * In order to successfully keep the domain model shielded from user interface
 * considerations, this approach is generally preferred to the one taken in the
 * tracking controller. However, there is never any one perfect solution for all
 * situations, so we've chosen to demonstrate two polarized ways to build user
 * interfaces.
 */
@Named
@RequestScoped
public class ListCargo {

    private List<CargoRoute> cargos;
    @Inject
    private BookingServiceFacade bookingServiceFacade;

    public List<CargoRoute> getCargos() {
        return cargos;
    }

    @PostConstruct
    public void init() {
        cargos = bookingServiceFacade.listAllCargos();
    }
}
