package net.java.cargotracker.interfaces.handling.rest;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Transfer object for handling reports.
 */
// TODO Add internationalized messages for constraints.
@XmlRootElement
public class HandlingReport {

    @NotNull
    @Size(min = 16, max = 16)
    private String completionTime;
    @NotNull
    @Size(min = 1)
    private List<String> trackingIds = new ArrayList<>();
    @NotNull
    @Size(min = 4, max = 7)
    private String type;
    @NotNull
    @Size(min = 5, max = 5)
    private String unLocode;
    @Size(min = 4, max = 5)
    private String voyageNumber;

    public String getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(String value) {
        this.completionTime = value;
    }

    public List<String> getTrackingIds() {
        return trackingIds;
    }

    public void setTrackingIds(List<String> trackingIds) {
        this.trackingIds = trackingIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getUnLocode() {
        return unLocode;
    }

    public void setUnLocode(String value) {
        this.unLocode = value;
    }

    public String getVoyageNumber() {
        return voyageNumber;
    }

    public void setVoyageNumber(String value) {
        this.voyageNumber = value;
    }
}