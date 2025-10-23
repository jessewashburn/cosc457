package org.bmc.app.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bmc.app.model.Invoice;
import org.bmc.app.util.DBConnection;

/**
 * Data Access Object for Invoice entity.
 * Provides CRUD operations for invoices in the Baltimore Metal Crafters database.
 */
public class InvoiceDAO {
    
    private static final Logger LOGGER = Logger.getLogger(InvoiceDAO.class.getName());
    
    // SQL Queries
    private static final String INSERT_SQL = 
        "INSERT INTO Invoice (job_id, invoice_date, total_amount, paid) VALUES (?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT i.invoice_id, i.job_id, i.invoice_date, i.total_amount, i.paid, " +
        "j.description as job_description, c.name as customer_name " +
        "FROM Invoice i " +
        "LEFT JOIN Job j ON i.job_id = j.job_id " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE i.invoice_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT i.invoice_id, i.job_id, i.invoice_date, i.total_amount, i.paid, " +
        "j.description as job_description, c.name as customer_name " +
        "FROM Invoice i " +
        "LEFT JOIN Job j ON i.job_id = j.job_id " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "ORDER BY i.invoice_date DESC";
    
    private static final String UPDATE_SQL = 
        "UPDATE Invoice SET job_id = ?, invoice_date = ?, total_amount = ?, paid = ? WHERE invoice_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM Invoice WHERE invoice_id = ?";
    
    private static final String SELECT_BY_JOB_SQL = 
        "SELECT i.invoice_id, i.job_id, i.invoice_date, i.total_amount, i.paid, " +
        "j.description as job_description, c.name as customer_name " +
        "FROM Invoice i " +
        "LEFT JOIN Job j ON i.job_id = j.job_id " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE i.job_id = ? ORDER BY i.invoice_date";
    
    private static final String SELECT_BY_PAYMENT_STATUS_SQL = 
        "SELECT i.invoice_id, i.job_id, i.invoice_date, i.total_amount, i.paid, " +
        "j.description as job_description, c.name as customer_name " +
        "FROM Invoice i " +
        "LEFT JOIN Job j ON i.job_id = j.job_id " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE i.paid = ? ORDER BY i.invoice_date";
    
    private static final String SELECT_OVERDUE_SQL = 
        "SELECT i.invoice_id, i.job_id, i.invoice_date, i.total_amount, i.paid, " +
        "j.description as job_description, c.name as customer_name " +
        "FROM Invoice i " +
        "LEFT JOIN Job j ON i.job_id = j.job_id " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE i.paid = FALSE AND DATEDIFF(CURDATE(), i.invoice_date) > 30 " +
        "ORDER BY i.invoice_date";
    
    private static final String SELECT_AGING_REPORT_SQL = 
        "SELECT i.invoice_id, i.job_id, i.invoice_date, i.total_amount, i.paid, " +
        "j.description as job_description, c.name as customer_name, " +
        "DATEDIFF(CURDATE(), i.invoice_date) as days_outstanding " +
        "FROM Invoice i " +
        "LEFT JOIN Job j ON i.job_id = j.job_id " +
        "LEFT JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE i.paid = FALSE " +
        "ORDER BY days_outstanding DESC";
    
    private static final String SELECT_BY_CUSTOMER_SQL = 
        "SELECT i.invoice_id, i.job_id, i.invoice_date, i.total_amount, i.paid, " +
        "j.description as job_description, c.name as customer_name " +
        "FROM Invoice i " +
        "JOIN Job j ON i.job_id = j.job_id " +
        "JOIN Customer c ON j.customer_id = c.customer_id " +
        "WHERE c.customer_id = ? ORDER BY i.invoice_date DESC";
    
    
    private static final String COUNT_PAYMENTS_SQL = 
        "SELECT COUNT(*) FROM Payment WHERE invoice_id = ?";
    
    /**
     * Creates a new invoice in the database
     * 
     * @param invoice Invoice object to create (ID will be auto-generated)
     * @return Invoice object with generated ID, or null if creation failed
     */
    public Invoice create(Invoice invoice) {
        if (invoice == null || !invoice.isValid()) {
            LOGGER.warning("Cannot create invalid invoice");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setInt(1, invoice.getJobId());
            pstmt.setDate(2, Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setBigDecimal(3, invoice.getTotalAmount());
            pstmt.setBoolean(4, invoice.getPaid());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    invoice.setInvoiceId(rs.getInt(1));
                    LOGGER.info(() -> String.format("Created invoice with ID: %d", invoice.getInvoiceId()));
                    return invoice;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating invoice", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves an invoice by ID
     * 
     * @param invoiceId ID of the invoice to retrieve
     * @return Invoice object if found, null otherwise
     */
    public Invoice findById(Integer invoiceId) {
        if (invoiceId == null) {
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            pstmt.setInt(1, invoiceId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToInvoice(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding invoice by ID: " + invoiceId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves all invoices from the database
     * 
     * @return List of all invoices, ordered by invoice date (newest first)
     */
    public List<Invoice> findAll() {
        List<Invoice> invoices = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            
            LOGGER.info(() -> String.format("Retrieved %d invoices", invoices.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all invoices", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return invoices;
    }
    
    /**
     * Updates an existing invoice in the database
     * 
     * @param invoice Invoice object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Invoice invoice) {
        if (invoice == null || invoice.getInvoiceId() == null || !invoice.isValid()) {
            LOGGER.warning("Cannot update invalid invoice");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_SQL);
            
            pstmt.setInt(1, invoice.getJobId());
            pstmt.setDate(2, Date.valueOf(invoice.getInvoiceDate()));
            pstmt.setBigDecimal(3, invoice.getTotalAmount());
            pstmt.setBoolean(4, invoice.getPaid());
            pstmt.setInt(5, invoice.getInvoiceId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Updated invoice ID: %d", invoice.getInvoiceId()));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating invoice", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Deletes an invoice from the database
     * Note: This will fail if the invoice has associated payments (foreign key constraint)
     * 
     * @param invoiceId ID of the invoice to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(Integer invoiceId) {
        if (invoiceId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, invoiceId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Deleted invoice ID: %d", invoiceId));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error deleting invoice ID: " + invoiceId, e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Retrieves invoices for a specific job
     * 
     * @param jobId ID of the job
     * @return List of invoices for the job, ordered by invoice date
     */
    public List<Invoice> findByJob(Integer jobId) {
        List<Invoice> invoices = new ArrayList<>();
        
        if (jobId == null) {
            return invoices;
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
                invoices.add(mapResultSetToInvoice(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d invoices for job ID: %d", invoices.size(), jobId));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding invoices by job", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return invoices;
    }
    
    /**
     * Retrieves invoices by payment status
     * 
     * @param paid true for paid invoices, false for unpaid
     * @return List of invoices with the specified payment status
     */
    public List<Invoice> findByPaymentStatus(boolean paid) {
        List<Invoice> invoices = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_PAYMENT_STATUS_SQL);
            pstmt.setBoolean(1, paid);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            
            String status = paid ? "paid" : "unpaid";
            LOGGER.info(() -> String.format("Found %d %s invoices", invoices.size(), status));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding invoices by payment status", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return invoices;
    }
    
    /**
     * Retrieves overdue invoices (unpaid and more than 30 days old)
     * 
     * @return List of overdue invoices
     */
    public List<Invoice> findOverdue() {
        List<Invoice> invoices = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_OVERDUE_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d overdue invoices", invoices.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding overdue invoices", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return invoices;
    }
    
    /**
     * Retrieves aging report for unpaid invoices
     * 
     * @return List of unpaid invoices with aging information
     */
    public List<Invoice> getAgingReport() {
        List<Invoice> invoices = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_AGING_REPORT_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Invoice invoice = mapResultSetToInvoice(rs);
                // The days_outstanding could be used to set additional properties
                // or returned in a separate DTO for reporting
                invoices.add(invoice);
            }
            
            LOGGER.info(() -> String.format("Generated aging report with %d unpaid invoices", invoices.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error generating aging report", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return invoices;
    }
    
    /**
     * Retrieves invoices for a specific customer
     * 
     * @param customerId ID of the customer
     * @return List of invoices for the customer, ordered by invoice date (newest first)
     */
    public List<Invoice> findByCustomer(Integer customerId) {
        List<Invoice> invoices = new ArrayList<>();
        
        if (customerId == null) {
            return invoices;
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
                invoices.add(mapResultSetToInvoice(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d invoices for customer ID: %d", invoices.size(), customerId));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding invoices by customer", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return invoices;
    }
    
    /**
     * Gets unpaid invoices (convenience method)
     * 
     * @return List of unpaid invoices
     */
    public List<Invoice> findUnpaid() {
        return findByPaymentStatus(false);
    }
    
    /**
     * Gets paid invoices (convenience method)
     * 
     * @return List of paid invoices
     */
    public List<Invoice> findPaid() {
        return findByPaymentStatus(true);
    }
    
    /**
     * Marks an invoice as paid
     * 
     * @param invoiceId ID of the invoice to mark as paid
     * @return true if successful, false otherwise
     */
    public boolean markAsPaid(Integer invoiceId) {
        Invoice invoice = findById(invoiceId);
        if (invoice != null) {
            invoice.markAsPaid();
            return update(invoice);
        }
        return false;
    }
    
    /**
     * Marks an invoice as unpaid
     * 
     * @param invoiceId ID of the invoice to mark as unpaid
     * @return true if successful, false otherwise
     */
    public boolean markAsUnpaid(Integer invoiceId) {
        Invoice invoice = findById(invoiceId);
        if (invoice != null) {
            invoice.markAsUnpaid();
            return update(invoice);
        }
        return false;
    }
    
    /**
     * Checks if an invoice can be safely deleted (has no associated payments)
     * 
     * @param invoiceId ID of the invoice to check
     * @return true if invoice can be deleted, false otherwise
     */
    public boolean canDelete(Integer invoiceId) {
        if (invoiceId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(COUNT_PAYMENTS_SQL);
            pstmt.setInt(1, invoiceId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;  // Can delete if no payments
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if invoice can be deleted", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Maps a ResultSet row to an Invoice object
     * 
     * @param rs ResultSet positioned at an invoice row
     * @return Invoice object
     * @throws SQLException if database access error occurs
     */
    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Date invoiceDate = rs.getDate("invoice_date");
        
        Invoice invoice = new Invoice(
            rs.getInt("invoice_id"),
            rs.getInt("job_id"),
            invoiceDate != null ? invoiceDate.toLocalDate() : null,
            rs.getBigDecimal("total_amount"),
            rs.getBoolean("paid")
        );
        
        // Set additional display fields if available
        invoice.setJobDescription(rs.getString("job_description"));
        invoice.setCustomerName(rs.getString("customer_name"));
        
        return invoice;
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