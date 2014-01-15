package net.java.cargotracker.interfaces.handling.file;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 * Periodically scans a certain directory for files and attempts to parse
 * handling event registrations from the contents by calling a batch job
 * <p/>
 * Files that fail to parse are moved into a separate directory, successful
 * files are deleted.
 */
// TODO Revisit exception handling
@Stateless
public class UploadDirectoryScanner {

    public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm";
    
    private final static Logger logger = Logger.getLogger(
            UploadDirectoryScanner.class.getName());

    private JobOperator jobOperator;
    private long execID;

    @Schedule(minute = "*/3", hour = "*") // Runs every thirty minutes
    public void processFiles() {
        logger.log(Level.INFO, "Scanning upload directory...");
    
        jobOperator = BatchRuntime.getJobOperator();
        execID = jobOperator.start("EventHandlerJob", null);
    }

    
}