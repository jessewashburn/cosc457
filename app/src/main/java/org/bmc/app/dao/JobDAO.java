package org.bmc.app.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bmc.app.model.Job;
import org.bmc.app.util.DBConnection;

/**
 * Data Access Object for Job entity.
 * Provides CRUD operations for jobs in the Baltimore Metal Crafters database.
 */
public class JobDAO {
    
    private static final Logger LOGGER = Logger.getLogger(JobDAO.class.getName());
    
    // SQL Queries
    private static final String INSERT_SQL = 
        "INSERT INTO Job (customer_id, quote_id, description, start_date, due_date, status) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT j.job_id, j.customer_id, j.quote_id, j.description, j.start_date, j.due_date, j.status, " +
        "c.name as customer_name FROM Job j " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id WHERE j.job_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT j.job_id, j.customer_id, j.quote_id, j.description, j.start_date, j.due_date, j.status, " +
        "c.name as customer_name FROM Job j " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id ORDER BY j.due_date, j.job_id";
    
    private static final String UPDATE_SQL = 
        "UPDATE Job SET customer_id = ?, quote_id = ?, description = ?, start_date = ?, due_date = ?, status = ? " +
        "WHERE job_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM Job WHERE job_id = ?";
    
    private static final String SELECT_BY_STATUS_SQL = 
        "SELECT j.job_id, j.customer_id, j.quote_id, j.description, j.start_date, j.due_date, j.status, " +
        "c.name as customer_name FROM Job j " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id WHERE j.status = ? ORDER BY j.due_date";
    
    private static final String SELECT_BY_CUSTOMER_SQL = 
        "SELECT j.job_id, j.customer_id, j.quote_id, j.description, j.start_date, j.due_date, j.status, " +
        "c.name as customer_name FROM Job j " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id WHERE j.customer_id = ? ORDER BY j.start_date DESC";
    
    private static final String SELECT_DUE_SOON_SQL = 
        "SELECT j.job_id, j.customer_id, j.quote_id, j.description, j.start_date, j.due_date, j.status, " +
        "c.name as customer_name FROM Job j " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE j.status IN ('Planned', 'InProgress') AND j.due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
        "ORDER BY j.due_date";
    
    private static final String SELECT_OVERDUE_SQL = 
        "SELECT j.job_id, j.customer_id, j.quote_id, j.description, j.start_date, j.due_date, j.status, " +
        "c.name as customer_name FROM Job j " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE j.status IN ('Planned', 'InProgress') AND j.due_date < CURDATE() ORDER BY j.due_date";
    
    private static final String COUNT_RELATED_RECORDS_SQL = 
        "SELECT " +
        "(SELECT COUNT(*) FROM Invoice WHERE job_id = ?) + " +
        "(SELECT COUNT(*) FROM WorkLog WHERE job_id = ?) + " +
        "(SELECT COUNT(*) FROM JobMaterial WHERE job_id = ?) + " +
        "(SELECT COUNT(*) FROM Notes WHERE job_id = ?) + " +
        "(SELECT COUNT(*) FROM Photo WHERE job_id = ?) + " +
        "(SELECT COUNT(*) FROM Shipment WHERE job_id = ?) as total_count";
    
    /**
     * Creates a new job in the database
     * 
     * @param job Job object to create (ID will be auto-generated)
     * @return Job object with generated ID, or null if creation failed
     */
    public Job create(Job job) {
        if (job == null || !job.isValid()) {
            LOGGER.warning("Cannot create invalid job");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, job.getCustomerId());
            if (job.getQuoteId() != null) {
                pstmt.setInt(2, job.getQuoteId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, job.getDescription());
            pstmt.setDate(4, job.getStartDate() != null ? Date.valueOf(job.getStartDate()) : null);
            pstmt.setDate(5, job.getDueDate() != null ? Date.valueOf(job.getDueDate()) : null);
            pstmt.setString(6, job.getStatus().getValue());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    job.setJobId(rs.getInt(1));
                    LOGGER.info(() -> "Created job with ID: " + job.getJobId());
                    return job;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating job", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves a job by ID
     * 
     * @param jobId ID of the job to retrieve
     * @return Job object if found, null otherwise
     */
    public Job findById(Integer jobId) {
        if (jobId == null) {
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            pstmt.setInt(1, jobId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToJob(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding job by ID: " + jobId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves all jobs from the database
     * 
     * @return List of all jobs, ordered by due date
     */
    public List<Job> findAll() {
        List<Job> jobs = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
            LOGGER.info(() -> String.format("Retrieved %d jobs", jobs.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all jobs", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return jobs;
    }
    
    /**
     * Updates an existing job in the database
     * 
     * @param job Job object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Job job) {
        if (job == null || job.getJobId() == null || !job.isValid()) {
            LOGGER.warning("Cannot update invalid job");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_SQL);
            
            pstmt.setInt(1, job.getCustomerId());
            if (job.getQuoteId() != null) {
                pstmt.setInt(2, job.getQuoteId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, job.getDescription());
            pstmt.setDate(4, job.getStartDate() != null ? Date.valueOf(job.getStartDate()) : null);
            pstmt.setDate(5, job.getDueDate() != null ? Date.valueOf(job.getDueDate()) : null);
            pstmt.setString(6, job.getStatus().getValue());
            pstmt.setInt(7, job.getJobId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Updated job ID: %d", job.getJobId()));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating job", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Deletes a job from the database
     * Note: This will fail if the job has associated records (foreign key constraints)
     * 
     * @param jobId ID of the job to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(Integer jobId) {
        if (jobId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, jobId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Deleted job ID: %d", jobId));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error deleting job ID: " + jobId, e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Retrieves jobs by status
     * 
     * @param status Job status to filter by
     * @return List of jobs with the specified status
     */
    public List<Job> findByStatus(Job.Status status) {
        List<Job> jobs = new ArrayList<>();
        
        if (status == null) {
            return jobs;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_STATUS_SQL);
            pstmt.setString(1, status.getValue());
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d jobs with status: %s", jobs.size(), status));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding jobs by status", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return jobs;
    }
    
    /**
     * Retrieves jobs for a specific customer
     * 
     * @param customerId ID of the customer
     * @return List of jobs for the customer, ordered by start date (newest first)
     */
    public List<Job> findByCustomer(Integer customerId) {
        List<Job> jobs = new ArrayList<>();
        
        if (customerId == null) {
            return jobs;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_CUSTOMER_SQL);
            pstmt.setInt(1, customerId);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d jobs for customer ID: %d", jobs.size(), customerId));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding jobs by customer", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return jobs;
    }
    
    /**
     * Retrieves jobs due within specified number of days
     * 
     * @param days number of days to look ahead
     * @return List of jobs due soon
     */
    public List<Job> findDueSoon(int days) {
        List<Job> jobs = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_DUE_SOON_SQL);
            pstmt.setInt(1, days);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d jobs due within %d days", jobs.size(), days));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding jobs due soon", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return jobs;
    }
    
    /**
     * Retrieves overdue jobs (past due date and not completed)
     * 
     * @return List of overdue jobs
     */
    public List<Job> findOverdue() {
        List<Job> jobs = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_OVERDUE_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobs.add(mapResultSetToJob(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d overdue jobs", jobs.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding overdue jobs", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return jobs;
    }
    
    /**
     * Gets all active jobs (planned or in progress)
     * 
     * @return List of active jobs
     */
    public List<Job> findActive() {
        List<Job> activeJobs = new ArrayList<>();
        activeJobs.addAll(findByStatus(Job.Status.PLANNED));
        activeJobs.addAll(findByStatus(Job.Status.IN_PROGRESS));
        return activeJobs;
    }
    
    /**
     * Checks if a job can be safely deleted (has no associated records)
     * 
     * @param jobId ID of the job to check
     * @return true if job can be deleted, false otherwise
     */
    public boolean canDelete(Integer jobId) {
        if (jobId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(COUNT_RELATED_RECORDS_SQL);
            // Set the jobId for all 6 parameters
            for (int i = 1; i <= 6; i++) {
                pstmt.setInt(i, jobId);
            }
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_count") == 0;  // Can delete if no related records
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if job can be deleted", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Maps a ResultSet row to a Job object
     * 
     * @param rs ResultSet positioned at a job row
     * @return Job object
     * @throws SQLException if database access error occurs
     */
    private Job mapResultSetToJob(ResultSet rs) throws SQLException {
        String statusString = rs.getString("status");
        Job.Status status = Job.Status.fromString(statusString);
        
        Date startDate = rs.getDate("start_date");
        Date dueDate = rs.getDate("due_date");
        
        Job job = new Job(
            rs.getInt("job_id"),
            rs.getInt("customer_id"),
            rs.getObject("quote_id", Integer.class), // Handle nullable quote_id
            rs.getString("description"),
            startDate != null ? startDate.toLocalDate() : null,
            dueDate != null ? dueDate.toLocalDate() : null,
            status
        );
        
        // Set additional display fields if available
        job.setCustomerName(rs.getString("customer_name"));
        
        return job;
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