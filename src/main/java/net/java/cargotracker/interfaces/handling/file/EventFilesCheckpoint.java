package net.java.cargotracker.interfaces.handling.file;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class EventFilesCheckpoint implements Serializable {

    private List<File> files = new LinkedList<>();
    private int fileIndex = 0;
    private long filePointer = 0;
    private long lineNum = 0;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public long getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public long getFilePointer() {
        return filePointer;
    }

    public void setFilePointer(long filePointer) {
        this.filePointer = filePointer;
    }

    public long getLineNum() {
        return lineNum;
    }

    public void nextLine() {
        lineNum++;
    }

    public File nextFile() {
        if (files.size() > fileIndex) {
            return files.get(fileIndex++);
        } else {
            return null;
        }
    }
}
