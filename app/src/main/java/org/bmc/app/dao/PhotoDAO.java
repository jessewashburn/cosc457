package org.bmc.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bmc.app.model.Photo;
import org.bmc.app.util.DBConnection;

/**
 * Data Access Object for Photo entity.
 * Handles database operations for job photos.
 */
public class PhotoDAO {
    
    private static final Logger LOGGER = Logger.getLogger(PhotoDAO.class.getName());
    
    // SQL Queries
    private static final String INSERT_SQL = 
        "INSERT INTO Photo (job_id, file_path, description) VALUES (?, ?, ?)";
    
    private static final String SELECT_BY_JOB_SQL = 
        "SELECT photo_id, job_id, file_path, description FROM Photo WHERE job_id = ? ORDER BY photo_id";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT photo_id, job_id, file_path, description FROM Photo WHERE photo_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM Photo WHERE photo_id = ?";
    
    private static final String DELETE_BY_JOB_SQL = 
        "DELETE FROM Photo WHERE job_id = ?";
    
    /**
     * Creates a new photo record in the database
     * 
     * @param photo Photo object to create (ID will be auto-generated)
     * @return Photo object with generated ID, or null if creation failed
     */
    public Photo create(Photo photo) {
        if (photo == null || !photo.isValid()) {
            LOGGER.warning("Cannot create invalid photo");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, photo.getJobId());
            pstmt.setString(2, photo.getFilePath());
            pstmt.setString(3, photo.getDescription());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    photo.setPhotoId(rs.getInt(1));
                    LOGGER.info(() -> String.format("Created photo with ID: %d for job: %d", 
                        photo.getPhotoId(), photo.getJobId()));
                    return photo;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating photo", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves all photos for a specific job
     * 
     * @param jobId ID of the job
     * @return List of photos for the job, empty list if none found
     */
    public List<Photo> findByJobId(Integer jobId) {
        List<Photo> photos = new ArrayList<>();
        
        if (jobId == null) {
            return photos;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_JOB_SQL);
            pstmt.setInt(1, jobId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                photos.add(mapResultSetToPhoto(rs));
            }
            
            LOGGER.info(() -> String.format("Retrieved %d photos for job: %d", photos.size(), jobId));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving photos for job: " + jobId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return photos;
    }
    
    /**
     * Retrieves a photo by its ID
     * 
     * @param photoId ID of the photo
     * @return Photo object if found, null otherwise
     */
    public Photo findById(Integer photoId) {
        if (photoId == null) {
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            pstmt.setInt(1, photoId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPhoto(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding photo by ID: " + photoId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Deletes a photo from the database
     * Note: This does NOT delete the physical file - caller must handle that
     * 
     * @param photoId ID of the photo to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(Integer photoId) {
        if (photoId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, photoId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Deleted photo ID: %d", photoId));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error deleting photo ID: " + photoId, e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Deletes all photos for a specific job
     * Note: This does NOT delete the physical files - caller must handle that
     * 
     * @param jobId ID of the job
     * @return number of photos deleted
     */
    public int deleteByJobId(Integer jobId) {
        if (jobId == null) {
            return 0;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(DELETE_BY_JOB_SQL);
            pstmt.setInt(1, jobId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Deleted %d photos for job ID: %d", rowsAffected, jobId));
            }
            
            return rowsAffected;
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error deleting photos for job ID: " + jobId, e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return 0;
    }
    
    /**
     * Maps a ResultSet row to a Photo object
     * 
     * @param rs ResultSet positioned at a photo row
     * @return Photo object
     * @throws SQLException if database access error occurs
     */
    private Photo mapResultSetToPhoto(ResultSet rs) throws SQLException {
        return new Photo(
            rs.getInt("photo_id"),
            rs.getInt("job_id"),
            rs.getString("file_path"),
            rs.getString("description")
        );
    }
    
    /**
     * Safely closes database resources
     * 
     * @param conn Connection to close
     * @param pstmt PreparedStatement to close
     * @param rs ResultSet to close
     */
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
            }
        }
        
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing PreparedStatement", e);
            }
        }
        
        DBConnection.closeConnection(conn);
    }
}
