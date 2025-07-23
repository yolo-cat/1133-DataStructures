package util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * FileUtils provides utility methods for file operations related to music files.
 */
public class FileUtils {
    
    // Supported audio file extensions
    public static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(
        "mp3", "wav", "au", "aiff"
    );
    
    /**
     * Checks if a file has a supported audio extension.
     * @param filePath The path to the file
     * @return true if the file has a supported extension
     */
    public static boolean hasSupportedExtension(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        String extension = getFileExtension(filePath);
        return SUPPORTED_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * Gets the file extension from a file path.
     * @param filePath The path to the file
     * @return The file extension (without the dot), or empty string if none
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return "";
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        
        return "";
    }
    
    /**
     * Gets the file name without extension from a file path.
     * @param filePath The path to the file
     * @return The file name without extension
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return "";
        }
        
        File file = new File(filePath);
        String fileName = file.getName();
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        
        return fileName;
    }
    
    /**
     * Validates if a file exists and is readable.
     * @param filePath The path to the file
     * @return true if the file exists and is readable
     */
    public static boolean isValidFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }
    
    /**
     * Formats file size into human-readable format.
     * @param sizeInBytes The size in bytes
     * @return Formatted size string
     */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.1f KB", sizeInBytes / 1024.0);
        } else {
            return String.format("%.1f MB", sizeInBytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Gets a description of supported file types for file choosers.
     * @return A description string of supported formats
     */
    public static String getSupportedFormatsDescription() {
        StringBuilder sb = new StringBuilder("Audio Files (");
        for (int i = 0; i < SUPPORTED_EXTENSIONS.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("*.").append(SUPPORTED_EXTENSIONS.get(i));
        }
        sb.append(")");
        return sb.toString();
    }
}