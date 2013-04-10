package net.java.cargotracker.interfaces.booking.web;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import net.java.cargotracker.interfaces.booking.facade.BookingServiceFacade;
import net.java.cargotracker.interfaces.booking.facade.dto.Location;

/**
 * Handles registering cargo. Operates against a dedicated service facade, and
 * could easily be rewritten as a thick Swing client. Completely separated from
 * the domain layer, unlike the tracking user interface.
 * <p/>
 * In order to successfully keep the domain model shielded from user interface
 * considerations, this approach is generally preferred to the one taken in the
 * tracking controller. However, there is never any one perfect solution for all
 * situations, so we've chosen to demonstrate two polarized ways to build user
 * interfaces.
 */
@Named
@ViewScoped
public class Registration implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String FORMAT = "MM/dd/yyyy";
    List<Location> locations;
    private String arrivalDeadline = FORMAT;
    private String originUnlocode;
    private String destinationUnlocode;
    @Inject
    private BookingServiceFacade bookingServiceFacade;

    public List<Location> getLocations() {
        return locations;
    }

    public String getArrivalDeadline() {
        return arrivalDeadline;
    }

    public void setArrivalDeadline(String arrivalDeadline) {
        this.arrivalDeadline = arrivalDeadline;
    }

    public String getOriginUnlocode() {
        return originUnlocode;
    }

    public void setOriginUnlocode(String originUnlocode) {
        this.originUnlocode = originUnlocode;
    }

    public String getDestinationUnlocode() {
        return destinationUnlocode;
    }

    public void setDestinationUnlocode(String destinationUnlocode) {
        this.destinationUnlocode = destinationUnlocode;
    }

    @PostConstruct
    public void init() {
        locations = bookingServiceFacade.listShippingLocations();
    }

    public String register() {
        String trackingId = "";

        try {
            trackingId = bookingServiceFacade.bookNewCargo(originUnlocode,
                    destinationUnlocode,
                    new SimpleDateFormat(FORMAT).parse(arrivalDeadline));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "show.xhtml?faces-redirect=true&trackingId=" + trackingId;
    }
}
