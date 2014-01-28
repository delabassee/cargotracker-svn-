package net.java.cargotracker.interfaces.handling.file;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Named("EventItemReader")
public class EventItemReader extends AbstractItemReader {

    private static final Logger logger = Logger.getLogger(
            EventItemReader.class.getName());
    private EventFilesCheckpoint checkpoint;
    private RandomAccessFile currentFile;
    @Inject
    private JobContext jobContext;

    @Override
    public void open(Serializable checkpoint) throws Exception {
        File uploadDirectory = new File(
                jobContext.getProperties().getProperty("upload_directory"));

        if (checkpoint == null) {
            this.checkpoint = new EventFilesCheckpoint();
            logger.log(Level.INFO, "Scanning upload directory: {0}", uploadDirectory);

            if (!uploadDirectory.exists()) {
                logger.log(Level.INFO, "Upload directory does not exist, creating it");
                uploadDirectory.mkdirs();
            } else {
                this.checkpoint.setFiles(Arrays.asList(uploadDirectory.listFiles()));
            }
        } else {
            logger.log(Level.INFO, "Starting from previous checkpoint");
            this.checkpoint = (EventFilesCheckpoint) checkpoint;
        }

        File nextFile = this.checkpoint.nextFile();

        if (nextFile == null) {
            logger.log(Level.INFO, "No files to process");
            currentFile = null;
        } else {
            currentFile = new RandomAccessFile(nextFile, "r");
            currentFile.seek(this.checkpoint.getFilePointer());
        }
    }

    @Override
    public Object readItem() throws Exception {
        if (currentFile != null) {
            String entry = currentFile.readLine();

            if (entry != null) {
                this.checkpoint.nextLine();
                // TODO Better input validation here.
                return new EventItem(entry);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return this.checkpoint;
    }
}