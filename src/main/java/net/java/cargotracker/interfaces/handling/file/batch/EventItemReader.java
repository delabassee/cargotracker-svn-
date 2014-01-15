package net.java.cargotracker.interfaces.handling.file.batch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import javax.batch.api.chunk.ItemReader;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named("EventItemReader")
public class EventItemReader implements ItemReader{
 
    private EventItemCheckpoint checkpoint;
    private String fileName;
    private BufferedReader breader;
    @Inject
    private JobContext jobCtx;
    
    public EventItemReader(){}
    
    
    @Override
    public void open(Serializable ckpt) throws Exception {
        /* Use the checkpoint if this is a restart */
        if (ckpt == null) {
            checkpoint = new EventItemCheckpoint();
        } else {
            checkpoint = (EventItemCheckpoint) ckpt;
        }
        
        /* Read from the Events file embedded within the application */ 
        fileName = jobCtx.getProperties().getProperty("event_file_name");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(fileName);
        breader = new BufferedReader(new InputStreamReader(iStream));
        
        for (int i=0; i<checkpoint.getLineNum(); i++)
            breader.readLine();
    }
    
    @Override
    public void close() throws Exception {
        breader.close();
    }
    
    @Override
    public Object readItem() throws Exception {
        String entry = breader.readLine();
        if (entry != null) {
            checkpoint.nextLine();
            return new EventItem(entry);
        } else
            return null;
    }
    
    
    @Override
    public Serializable checkpointInfo() throws Exception {
        return checkpoint;
    }
}
