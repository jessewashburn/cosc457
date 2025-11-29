package org.bmc.app.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Utility class for managing photo file storage.
 * Handles copying, organizing, and validating photo files.
 */
public class PhotoStorageUtil {
    
    private static final Logger LOGGER = Logger.getLogger(PhotoStorageUtil.class.getName());
    
    // Base directory for storing photos (relative to application)
    private static final String PHOTOS_BASE_DIR = "photos";
    
    // Supported image formats
    private static final String[] SUPPORTED_FORMATS = {".jpg", ".jpeg", ".png", ".gif", ".bmp"};
    
    // Maximum file size (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * Saves an uploaded photo file for a specific job
     * 
     * @param sourceFile the original file to copy
     * @param jobId the job ID this photo belongs to
     * @param description optional description for filename
     * @return relative path to the saved file, or null if save failed
     */
    public static String savePhoto(File sourceFile, Integer jobId, String description) {
        if (sourceFile == null || !sourceFile.exists() || !sourceFile.isFile()) {
            LOGGER.warning("Source file does not exist or is not a file");
            return null;
        }
        
        if (jobId == null) {
            LOGGER.warning("Job ID is required to save photo");
            return null;
        }
        
        // Validate file type
        if (!isValidImageFile(sourceFile)) {
            LOGGER.warning("Unsupported file format: " + sourceFile.getName());
            return null;
        }
        
        // Validate file size
        if (sourceFile.length() > MAX_FILE_SIZE) {
            LOGGER.warning("File size exceeds maximum allowed: " + sourceFile.length());
            return null;
        }
        
        try {
            // Create job-specific directory
            Path jobDir = getJobPhotoDirectory(jobId);
            Files.createDirectories(jobDir);
            
            // Generate unique filename
            String originalName = sourceFile.getName();
            String extension = getFileExtension(originalName);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String sanitizedDesc = description != null && !description.trim().isEmpty() 
                ? sanitize(description) + "_" 
                : "";
            String newFilename = String.format("job%d_%s%s%s", jobId, sanitizedDesc, timestamp, extension);
            
            // Copy file to destination
            Path destinationPath = jobDir.resolve(newFilename);
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return relative path
            String relativePath = PHOTOS_BASE_DIR + "/job_" + jobId + "/" + newFilename;
            LOGGER.info("Photo saved: " + relativePath);
            return relativePath;
            
        } catch (IOException e) {
            LOGGER.severe("Error saving photo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Deletes a photo file from the filesystem
     * 
     * @param relativePath relative path to the photo file
     * @return true if file was deleted, false otherwise
     */
    public static boolean deletePhoto(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path photoPath = Paths.get(relativePath);
            if (Files.exists(photoPath)) {
                Files.delete(photoPath);
                LOGGER.info("Deleted photo: " + relativePath);
                return true;
            } else {
                LOGGER.warning("Photo file not found: " + relativePath);
                return false;
            }
        } catch (IOException e) {
            LOGGER.severe("Error deleting photo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the absolute file path for a relative photo path
     * 
     * @param relativePath relative path to the photo
     * @return File object for the photo
     */
    public static File getPhotoFile(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return null;
        }
        return new File(relativePath);
    }
    
    /**
     * Checks if a photo file exists
     * 
     * @param relativePath relative path to the photo
     * @return true if file exists
     */
    public static boolean photoExists(String relativePath) {
        File file = getPhotoFile(relativePath);
        return file != null && file.exists() && file.isFile();
    }
    
    /**
     * Gets the directory for storing photos for a specific job
     * 
     * @param jobId the job ID
     * @return Path to the job's photo directory
     */
    private static Path getJobPhotoDirectory(Integer jobId) {
        return Paths.get(PHOTOS_BASE_DIR, "job_" + jobId);
    }
    
    /**
     * Validates if a file is a supported image format
     * 
     * @param file the file to validate
     * @return true if file has a supported image extension
     */
    private static boolean isValidImageFile(File file) {
        String filename = file.getName().toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (filename.endsWith(format)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the file extension including the dot
     * 
     * @param filename the filename
     * @return extension (e.g., ".jpg") or empty string if none
     */
    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
    
    /**
     * Sanitizes a string for use in a filename
     * 
     * @param input the string to sanitize
     * @return sanitized string safe for filenames
     */
    private static String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9_-]", "_").substring(0, Math.min(input.length(), 30));
    }
    
    /**
     * Gets a display-friendly file size string
     * 
     * @param bytes file size in bytes
     * @return formatted file size (e.g., "2.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }
    
    /**
     * Gets the list of supported image format extensions
     * 
     * @return array of supported extensions
     */
    public static String[] getSupportedFormats() {
        return SUPPORTED_FORMATS.clone();
    }
    
    /**
     * Gets the maximum allowed file size
     * 
     * @return maximum file size in bytes
     */
    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
}
