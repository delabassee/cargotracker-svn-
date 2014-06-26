package net.java.cargotracker.interfaces.booking.facade.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DTO for a leg in an itinerary.
 */
public class Leg implements Serializable {

    private static final SimpleDateFormat DATE_FORMAT
            = new SimpleDateFormat("MM/dd/yyyy hh:mm a zzzz");

    private final String voyageNumber;
    private final String from;
    private final String to;
    private final String loadTime;
    private final String unloadTime;

    public Leg(String voyageNumber, String from, String to, Date loadTime,
            Date unloadTime) {
        this.voyageNumber = voyageNumber;
        this.from = from;
        this.to = to;
        this.loadTime = DATE_FORMAT.format(loadTime);
        this.unloadTime = DATE_FORMAT.format(unloadTime);
    }

    public String getVoyageNumber() {
        return voyageNumber;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getLoadTime() {
        return loadTime;
    }

    public String getUnloadTime() {
        return unloadTime;
    }

    @Override
    public String toString() {
        return "Leg{" + "voyageNumber=" + voyageNumber + ", from=" + from + ", to=" + to + ", loadTime=" + loadTime + ", unloadTime=" + unloadTime + '}';
    }
}
