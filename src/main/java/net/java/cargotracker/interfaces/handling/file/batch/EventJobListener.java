package net.java.cargotracker.interfaces.handling.file.batch;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.listener.JobListener;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("EventJobListener")
public class EventJobListener implements JobListener{
    private static final Logger logger = Logger.getLogger("EventJobListener");
    
    public EventJobListener() { }
    
    @Override
    public void beforeJob() throws Exception {
        logger.log(Level.INFO, "The Event Handling -> File Batch Job has started");
    }

    @Override
    public void afterJob() throws Exception {
        logger.log(Level.INFO, "The Event Handling -> File Batch Job has completed");
    }
}
