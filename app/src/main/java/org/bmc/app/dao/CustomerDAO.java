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

import org.bmc.app.model.Customer;
import org.bmc.app.util.DBConnection;

/**
 * Data Access Object for Customer entity.
 * Provides CRUD operations for customers in the Baltimore Metal Crafters database.
 */
public class CustomerDAO {
    
    private static final Logger LOGGER = Logger.getLogger(CustomerDAO.class.getName());
    
    // SQL Queries
    private static final String INSERT_SQL = 
        "INSERT INTO Customer (name, contact_name, phone, email, address) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT customer_id, name, contact_name, phone, email, address FROM Customer WHERE customer_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT customer_id, name, contact_name, phone, email, address FROM Customer ORDER BY name";
    
    private static final String UPDATE_SQL = 
        "UPDATE Customer SET name = ?, contact_name = ?, phone = ?, email = ?, address = ? WHERE customer_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM Customer WHERE customer_id = ?";
    
    private static final String SEARCH_BY_NAME_SQL = 
        "SELECT customer_id, name, contact_name, phone, email, address FROM Customer " +
        "WHERE name LIKE ? ORDER BY name";
    
    private static final String COUNT_JOBS_SQL = 
        "SELECT COUNT(*) FROM Job WHERE customer_id = ?";
    
    /**
     * Creates a new customer in the database
     * 
     * @param customer Customer object to create (ID will be auto-generated)
     * @return Customer object with generated ID, or null if creation failed
     */
    public Customer create(Customer customer) {
        if (customer == null || !customer.isValid()) {
            LOGGER.warning("Cannot create invalid customer");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContactName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getAddress());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    customer.setCustomerId(rs.getInt(1));
                    LOGGER.info(() -> String.format("Created customer with ID: %d", customer.getCustomerId()));
                    return customer;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating customer", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves a customer by ID
     * 
     * @param customerId ID of the customer to retrieve
     * @return Customer object if found, null otherwise
     */
    public Customer findById(Integer customerId) {
        if (customerId == null) {
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            pstmt.setInt(1, customerId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by ID: " + customerId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves all customers from the database
     * 
     * @return List of all customers, ordered by name
     */
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            
            LOGGER.info(() -> String.format("Retrieved %d customers", customers.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all customers", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return customers;
    }
    
    /**
     * Updates an existing customer in the database
     * 
     * @param customer Customer object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Customer customer) {
        if (customer == null || customer.getCustomerId() == null || !customer.isValid()) {
            LOGGER.warning("Cannot update invalid customer");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_SQL);
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContactName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getAddress());
            pstmt.setInt(6, customer.getCustomerId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Updated customer ID: %d", customer.getCustomerId()));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating customer", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Deletes a customer from the database
     * Note: This will fail if the customer has associated jobs (foreign key constraint)
     * 
     * @param customerId ID of the customer to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(Integer customerId) {
        if (customerId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, customerId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Deleted customer ID: %d", customerId));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error deleting customer ID: " + customerId, e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Searches for customers by name (partial match)
     * 
     * @param namePattern Pattern to search for (use % for wildcards)
     * @return List of matching customers
     */
    public List<Customer> searchByName(String namePattern) {
        List<Customer> customers = new ArrayList<>();
        
        if (namePattern == null || namePattern.trim().isEmpty()) {
            return customers;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SEARCH_BY_NAME_SQL);
            pstmt.setString(1, "%" + namePattern + "%");
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d customers matching: %s", customers.size(), namePattern));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching customers by name", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return customers;
    }
    
    /**
     * Checks if a customer can be safely deleted (has no associated jobs)
     * 
     * @param customerId ID of the customer to check
     * @return true if customer can be deleted, false otherwise
     */
    public boolean canDelete(Integer customerId) {
        if (customerId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(COUNT_JOBS_SQL);
            pstmt.setInt(1, customerId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;  // Can delete if no jobs
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if customer can be deleted", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Maps a ResultSet row to a Customer object
     * 
     * @param rs ResultSet positioned at a customer row
     * @return Customer object
     * @throws SQLException if database access error occurs
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customer(
            rs.getInt("customer_id"),
            rs.getString("name"),
            rs.getString("contact_name"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("address")
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