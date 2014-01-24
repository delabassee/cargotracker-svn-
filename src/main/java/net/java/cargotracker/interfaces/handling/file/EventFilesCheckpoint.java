package net.java.cargotracker.interfaces.handling.file;

import java.io.Serializable;

public class EventFilesCheckpoint implements Serializable {

    private long lineNum;

    public EventFilesCheckpoint() {
        lineNum = 0;
    }

    public long getLineNum() {
        return lineNum;
    }

    public void nextLine() {
        lineNum++;
    }
}