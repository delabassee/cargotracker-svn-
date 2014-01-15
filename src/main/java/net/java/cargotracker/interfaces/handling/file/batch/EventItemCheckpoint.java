package net.java.cargotracker.interfaces.handling.file.batch;

import java.io.Serializable;

public class EventItemCheckpoint implements Serializable {
    private long lineNum;

    public EventItemCheckpoint() {
        lineNum = 0;
    }

    public long getLineNum() {
        return lineNum;
    }

    public void nextLine() {
        lineNum++;
    }    
}
