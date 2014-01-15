package net.java.cargotracker.interfaces.handling.file.batch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.batch.api.chunk.ItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import net.java.cargotracker.application.ApplicationEvents;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.interfaces.handling.HandlingEventRegistrationAttempt;
import static net.java.cargotracker.interfaces.handling.file.UploadDirectoryScanner.ISO_8601_FORMAT;

@Dependent
@Named("EventItemWriter")
public class EventItemWriter implements ItemWriter{
    
    @Inject
    private ApplicationEvents applicationEvents;
    
    @Override
    public void open(Serializable ckpnt) throws Exception{}
    
    @Override
    public void writeItems(List<Object> items) throws Exception {
        
        for(int i=0;i<items.size();i++){
        EventItem item = (EventItem)items.get(i);
        Date completionTime = new SimpleDateFormat(ISO_8601_FORMAT).parse(item.getCompletionTimeValue());
            TrackingId trackingId = new TrackingId(item.getTrackingIdValue());
            VoyageNumber voyageNumber = new VoyageNumber(item.getVoyageNumberValue());
            HandlingEvent.Type eventType = HandlingEvent.Type.valueOf(item.getEventTypeValue());
            UnLocode unLocode = new UnLocode(item.getUnLocodeValue());

            HandlingEventRegistrationAttempt attempt =
                new HandlingEventRegistrationAttempt(new Date(), completionTime,
                trackingId, voyageNumber, eventType, unLocode);
            applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
            
           
        }
    }
    
    @Override
    public Serializable checkpointInfo() throws Exception {
        return new EventItemCheckpoint();
    }
    
    @Override
    public void close() throws Exception {}
}
