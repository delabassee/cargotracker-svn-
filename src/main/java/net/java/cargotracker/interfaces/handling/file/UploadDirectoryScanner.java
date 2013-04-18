package net.java.cargotracker.interfaces.handling.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;
import net.java.cargotracker.application.ApplicationEvents;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.interfaces.handling.HandlingEventRegistrationAttempt;

/**
 * Periodically scans a certain directory for files and attempts to parse
 * handling event registrations from the contents.
 * <p/>
 * Files that fail to parse are moved into a separate directory, successful
 * files are deleted.
 */
// TODO Revisit exception handling
@Stateless
public class UploadDirectoryScanner {

    public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm";
    // TODO Inject these from a producer and/or properties file?
    private File uploadDirectory = new File("/tmp/upload");
    private File parseFailureDirectory = new File("/tmp/failed");
    // TODO Use injection instead?
    private final static Logger logger = Logger.getLogger(
            UploadDirectoryScanner.class.getName());
    @Inject
    private ApplicationEvents applicationEvents;

    @PostConstruct
    public void init() {
        if (uploadDirectory.equals(parseFailureDirectory)) {
            throw new RuntimeException(
                    "Upload and parse failed directories must not be the same directory: "
                    + uploadDirectory); // Disables this singleton.
        }

        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        if (!parseFailureDirectory.exists()) {
            parseFailureDirectory.mkdirs();
        }
    }

    @Schedule(minute = "*", hour = "*") // Runs every thirty minutes
    public void processFiles() {
        logger.log(Level.INFO, "Scanning upload directory...");
        for (File file : uploadDirectory.listFiles()) {
            try {
                parse(file);
                delete(file);
                logger.log(Level.INFO,
                        "Import of file: {0} complete", file.getName());
            } catch (IOException ex) {
                logger.log(Level.SEVERE,
                        "Failed parsing file: " + file.getName(), ex);
            }
        }
    }

    private void parse(File file) throws IOException {
        List<String> rejectedLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                try {
                    parseLine(line);
                } catch (ParseException | IllegalArgumentException e) {
                    logger.log(Level.SEVERE,
                            "Rejected line: \n{0}\nReason is: {1}",
                            new Object[]{line, e});
                    rejectedLines.add(line);
                }
            }
        }

        if (!rejectedLines.isEmpty()) {
            writeRejectedLinesToFile(toRejectedFilename(file), rejectedLines);
        }
    }

    private String toRejectedFilename(File file) {
        return file.getName() + ".reject";
    }

    private void writeRejectedLinesToFile(String filename,
            List<String> rejectedLines) throws IOException {
        try (FileWriter writer = new FileWriter(
                        new File(parseFailureDirectory, filename))) {
            for (String line : rejectedLines) {
                writer.write(line + "\n");
            }
        }
    }

    private void parseLine(String line)
            throws ParseException, IllegalArgumentException {
        String[] columns = line.split("\t");
        if (columns.length == 5) {
            queueAttempt(columns[0], columns[1], columns[2], columns[3], columns[4]);
        } else if (columns.length == 4) {
            queueAttempt(columns[0], columns[1], "", columns[2], columns[3]);
        } else {
            throw new IllegalArgumentException(
                    "Wrong number of columns on line: " + line + ", must be 4 or 5");
        }
    }

    private void queueAttempt(String completionTimeValue,
            String trackingIdValue, String voyageNumberValue,
            String unLocodeValue, String eventTypeValue) throws ParseException {
        Date completionTime = new SimpleDateFormat(ISO_8601_FORMAT).parse(
                completionTimeValue);
        TrackingId trackingId = new TrackingId(trackingIdValue);
        VoyageNumber voyageNumber = new VoyageNumber(voyageNumberValue);
        HandlingEvent.Type eventType = HandlingEvent.Type.valueOf(eventTypeValue);
        UnLocode unLocode = new UnLocode(unLocodeValue);

        HandlingEventRegistrationAttempt attempt =
                new HandlingEventRegistrationAttempt(new Date(), completionTime,
                trackingId, voyageNumber, eventType, unLocode);
        applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
    }

    private void delete(File file) {
        if (!file.delete()) {
            logger.log(Level.SEVERE, "Could not delete {0}", file.getName());
        }
    }
}