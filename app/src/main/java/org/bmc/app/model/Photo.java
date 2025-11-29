package org.bmc.app.model;

/**
 * Represents a photo attached to a job.
 * Photos are stored on the file system with metadata in the database.
 */
public class Photo {
    
    private Integer photoId;
    private Integer jobId;
    private String filePath;
    private String description;
    
    /**
     * Constructor for new photos (without ID)
     * 
     * @param jobId ID of the job this photo belongs to
     * @param filePath relative path to the image file
     * @param description optional description of the photo
     */
    public Photo(Integer jobId, String filePath, String description) {
        this.jobId = jobId;
        this.filePath = filePath;
        this.description = description;
    }
    
    /**
     * Full constructor with ID (for existing photos)
     * 
     * @param photoId unique photo ID
     * @param jobId ID of the job this photo belongs to
     * @param filePath relative path to the image file
     * @param description optional description of the photo
     */
    public Photo(Integer photoId, Integer jobId, String filePath, String description) {
        this.photoId = photoId;
        this.jobId = jobId;
        this.filePath = filePath;
        this.description = description;
    }
    
    /**
     * Validates the photo has required fields
     * 
     * @return true if photo has valid jobId and filePath
     */
    public boolean isValid() {
        return jobId != null && filePath != null && !filePath.trim().isEmpty();
    }
    
    /**
     * Gets the filename from the file path
     * 
     * @return filename portion of the path
     */
    public String getFilename() {
        if (filePath == null) {
            return "";
        }
        int lastSlash = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        return lastSlash >= 0 ? filePath.substring(lastSlash + 1) : filePath;
    }
    
    /**
     * Gets a display name for the photo
     * 
     * @return description if available, otherwise filename
     */
    public String getDisplayName() {
        if (description != null && !description.trim().isEmpty()) {
            return description;
        }
        return getFilename();
    }
    
    // Getters and Setters
    
    public Integer getPhotoId() {
        return photoId;
    }
    
    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }
    
    public Integer getJobId() {
        return jobId;
    }
    
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return String.format("Photo[id=%d, jobId=%d, file=%s]", 
            photoId, jobId, getFilename());
    }
}
