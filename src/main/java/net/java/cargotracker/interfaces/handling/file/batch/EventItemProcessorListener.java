package net.java.cargotracker.interfaces.handling.file.batch;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Dependent
@Named("EventItemProcessorListener")
public class EventItemProcessorListener implements ItemProcessListener{
    private static final Logger logger = Logger.getLogger("EventItemProcessorListener");
    
    public EventItemProcessorListener() { }

    @Override
    public void beforeProcess(Object o) throws Exception {
        EventItem event = (EventItem) o;
        logger.log(Level.INFO, "Processing event " + event);
    }

    @Override
    public void afterProcess(Object o, Object o1) throws Exception { }

    @Override
    public void onProcessError(Object o, Exception excptn) throws Exception {
        EventItem event = (EventItem) o;
        logger.log(Level.WARNING, "Error processing event " + event);
    }
}
