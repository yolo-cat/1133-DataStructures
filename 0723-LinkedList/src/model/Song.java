package model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Song class represents a single song with its metadata and file information.
 * Encapsulates song file path, name, format, and other relevant information.
 */
public class Song {
    private String filePath;
    private String fileName;
    private String displayName;
    private String format;
    private long fileSize;
    
    /**
     * Constructor that creates a Song object from a file path.
     * @param filePath The absolute path to the song file
     */
    public Song(String filePath) {
        this.filePath = filePath;
        initializeFromPath(filePath);
    }
    
    /**
     * Constructor with custom display name.
     * @param filePath The absolute path to the song file
     * @param displayName Custom display name for the song
     */
    public Song(String filePath, String displayName) {
        this.filePath = filePath;
        this.displayName = displayName;
        initializeFromPath(filePath);
    }
    
    /**
     * Initialize song properties from file path.
     */
    private void initializeFromPath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        File file = new File(filePath);
        this.fileName = file.getName();
        this.fileSize = file.length();
        
        // Extract format from file extension
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            this.format = fileName.substring(lastDotIndex + 1).toLowerCase();
        } else {
            this.format = "unknown";
        }
        
        // Set display name if not already set
        if (this.displayName == null) {
            // Remove file extension for display name
            if (lastDotIndex > 0) {
                this.displayName = fileName.substring(0, lastDotIndex);
            } else {
                this.displayName = fileName;
            }
        }
    }
    
    /**
     * Checks if the song file exists and is readable.
     * @return true if file exists and is readable, false otherwise
     */
    public boolean isValidFile() {
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }
    
    /**
     * Checks if the song format is supported.
     * @return true if format is supported, false otherwise
     */
    public boolean isSupportedFormat() {
        return format.equals("mp3") || format.equals("wav") || 
               format.equals("au") || format.equals("aiff");
    }
    
    // Getters
    public String getFilePath() {
        return filePath;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getFormat() {
        return format;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    // Setters
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Returns formatted file size as a string.
     * @return File size in human-readable format
     */
    public String getFormattedFileSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Song song = (Song) obj;
        return filePath.equals(song.filePath);
    }
    
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}