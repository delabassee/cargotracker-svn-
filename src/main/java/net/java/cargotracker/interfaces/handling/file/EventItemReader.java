package net.java.cargotracker.interfaces.handling.file;

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
public class EventItemReader implements ItemReader {

    private EventFilesCheckpoint checkpoint;
    private BufferedReader fileReader;
    @Inject
    private JobContext jobContext;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        if (checkpoint == null) {
            this.checkpoint = new EventFilesCheckpoint();
        } else {
            this.checkpoint = (EventFilesCheckpoint) checkpoint;
        }

        // TODO scan all files in the directory.
        String fileName = jobContext.getProperties().getProperty("event_file_name");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream iStream = classLoader.getResourceAsStream(fileName);
        fileReader = new BufferedReader(new InputStreamReader(iStream));

        for (int i = 0; i < this.checkpoint.getLineNum(); i++) {
            fileReader.readLine();
        }
    }

    @Override
    public void close() throws Exception {
        // TODO Should delete be put here?

        fileReader.close();
    }

    @Override
    public Object readItem() throws Exception {
        String entry = fileReader.readLine();

        if (entry != null) {
            this.checkpoint.nextLine();
            // TODO Better input validation here.
            return new EventItem(entry);
        } else {
            return null;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return this.checkpoint;
    }
}
