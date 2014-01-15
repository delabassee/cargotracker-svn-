package net.java.cargotracker.interfaces.handling.file.batch;

import javax.batch.api.chunk.ItemProcessor;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 * Just a simple pass through to demonstrate a processor code
 * @author VIJNAIR
 */
@Dependent
@Named("EventItemProcessor")
public class EventItemProcessor implements ItemProcessor{
    
    @Override
    public Object processItem(Object item) {
        return (EventItem)item;
    }
    
    
}
