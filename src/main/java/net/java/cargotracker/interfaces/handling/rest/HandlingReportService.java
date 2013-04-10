package net.java.cargotracker.interfaces.handling.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import net.java.cargotracker.application.ApplicationEvents;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.interfaces.handling.HandlingEventRegistrationAttempt;
import net.java.cargotracker.interfaces.handling.HandlingReport;

/**
 * This REST endpoint implementation performs basic validation and parsing of
 * incoming data, and in case of a valid registration attempt, sends an
 * asynchronous message with the information to the handling event registration
 * system for proper registration.
 */
@Singleton // TODO Make this a stateless bean for better scalability.
@Startup
@Path("/handling")
public class HandlingReportService {

    public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm";
    @Inject
    private ApplicationEvents applicationEvents;

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public HandlingReport test() {
        HandlingReport report = new HandlingReport();

        report.setType("test");
        report.setUnLocode("test");
        report.setVoyageNumber("test");
        report.setCompletionTime("test");
        report.setTrackingIds(Arrays.asList(new String[]{"test1", "test2"}));

        return report;
    }

    @POST
    @Path("/report")
    @Consumes(MediaType.APPLICATION_JSON)
    // TODO Better exception handling.
    public void submitReport(@NotNull @Valid HandlingReport handlingReport) {
        try {
            Date completionTime = new SimpleDateFormat(ISO_8601_FORMAT).parse(
                    handlingReport.getCompletionTime());
            VoyageNumber voyageNumber = new VoyageNumber(
                    handlingReport.getVoyageNumber());
            HandlingEvent.Type type = HandlingEvent.Type.valueOf(
                    handlingReport.getType());
            UnLocode unLocode = new UnLocode(handlingReport.getUnLocode());

            for (String trackingIdValue : handlingReport.getTrackingIds()) {
                TrackingId trackingId = new TrackingId(trackingIdValue);

                Date registrationTime = new Date();
                HandlingEventRegistrationAttempt attempt =
                        new HandlingEventRegistrationAttempt(registrationTime,
                        completionTime, trackingId, voyageNumber, type, unLocode);

                applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
            }
        } catch (ParseException ex) {
            throw new RuntimeException("Error parsing completion time", ex);
        }
    }
}