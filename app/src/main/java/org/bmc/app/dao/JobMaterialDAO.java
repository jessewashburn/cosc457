package org.bmc.app.dao;

import org.bmc.app.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for JobMaterial relationships.
 * Manages the many-to-many relationship between jobs and materials.
 */
public class JobMaterialDAO {
    private static final Logger logger = Logger.getLogger(JobMaterialDAO.class.getName());
    
    /**
     * Represents a material assignment to a job with details
     */
    public static class JobMaterialInfo {
        private int jobId;
        private int materialId;
        private String materialName;
        private String category;
        private int quantityUsed;
        private int stockQuantity;
        
        public JobMaterialInfo(int jobId, int materialId, String materialName, 
                              String category, int quantityUsed, int stockQuantity) {
            this.jobId = jobId;
            this.materialId = materialId;
            this.materialName = materialName;
            this.category = category;
            this.quantityUsed = quantityUsed;
            this.stockQuantity = stockQuantity;
        }
        
        public int getJobId() { return jobId; }
        public int getMaterialId() { return materialId; }
        public String getMaterialName() { return materialName; }
        public String getCategory() { return category; }
        public int getQuantityUsed() { return quantityUsed; }
        public int getStockQuantity() { return stockQuantity; }
    }
    
    /**
     * Get all materials assigned to a specific job
     */
    public List<JobMaterialInfo> findByJobId(int jobId) {
        List<JobMaterialInfo> materials = new ArrayList<>();
        
        String sql = "SELECT jm.job_id, jm.material_id, jm.quantity_used, " +
                     "m.name, m.category, m.stock_quantity " +
                     "FROM JobMaterial jm " +
                     "JOIN Material m ON jm.material_id = m.material_id " +
                     "WHERE jm.job_id = ? " +
                     "ORDER BY m.name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jobId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(new JobMaterialInfo(
                        rs.getInt("job_id"),
                        rs.getInt("material_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity_used"),
                        rs.getInt("stock_quantity")
                    ));
                }
            }
            
            logger.info("Found " + materials.size() + " materials for job " + jobId);
            
        } catch (SQLException e) {
            logger.severe("Error fetching materials for job " + jobId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return materials;
    }
    
    /**
     * Assign a material to a job
     */
    public boolean assignMaterial(int jobId, int materialId, int quantityUsed) {
        String sql = "INSERT INTO JobMaterial (job_id, material_id, quantity_used) " +
                     "VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jobId);
            stmt.setInt(2, materialId);
            stmt.setInt(3, quantityUsed);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Assigned material " + materialId + " to job " + jobId + 
                           " (quantity: " + quantityUsed + ")");
                return true;
            }
            
        } catch (SQLException e) {
            logger.severe("Error assigning material to job: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Update the quantity of a material for a job
     */
    public boolean updateQuantity(int jobId, int materialId, int quantityUsed) {
        String sql = "UPDATE JobMaterial SET quantity_used = ? " +
                     "WHERE job_id = ? AND material_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quantityUsed);
            stmt.setInt(2, jobId);
            stmt.setInt(3, materialId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Updated quantity for material " + materialId + " on job " + jobId + 
                           " to " + quantityUsed);
                return true;
            }
            
        } catch (SQLException e) {
            logger.severe("Error updating material quantity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Remove a material assignment from a job
     */
    public boolean removeMaterial(int jobId, int materialId) {
        String sql = "DELETE FROM JobMaterial WHERE job_id = ? AND material_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, jobId);
            stmt.setInt(2, materialId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Removed material " + materialId + " from job " + jobId);
                return true;
            }
            
        } catch (SQLException e) {
            logger.severe("Error removing material from job: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
}
