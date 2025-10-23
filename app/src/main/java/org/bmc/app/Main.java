package org.bmc.app;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.model.Customer;
import org.bmc.app.util.DBConnection;

/**
 * Main application entry point for Baltimore Metal Crafters DB App.
 * Provides a simple console interface to test database operations.
 */
public class Main {
    
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        LOGGER.info("Starting Baltimore Metal Crafters DB Application");
        
        // Test database connection
        if (testDatabaseConnection()) {
            LOGGER.info("Database connection successful");
            
            // Test Customer DAO operations
            testCustomerOperations();
            
        } else {
            LOGGER.severe("Failed to connect to database. Please check your configuration.");
            System.exit(1);
        }
        
        LOGGER.info("Application completed");
    }
    
    /**
     * Tests the database connection
     */
    private static boolean testDatabaseConnection() {
        try {
            boolean connected = DBConnection.testConnection();
            if (connected) {
                LOGGER.info(() -> String.format("Connected to: %s", DBConnection.getDatabaseUrl()));
                return true;
            } else {
                LOGGER.warning("Database connection test failed");
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Database connection error", e);
            return false;
        }
    }
    
    /**
     * Tests basic Customer CRUD operations
     */
    private static void testCustomerOperations() {
        LOGGER.info("Testing Customer DAO operations...");
        
        CustomerDAO customerDAO = new CustomerDAO();
        
        try {
            // Test: Find all customers
            List<Customer> allCustomers = customerDAO.findAll();
            LOGGER.info(() -> String.format("Found %d existing customers", allCustomers.size()));
            
            // Display first few customers
            int count = 0;
            for (Customer customer : allCustomers) {
                if (count++ < 3) {  // Show first 3 customers
                    LOGGER.info(() -> String.format("Customer: %s", customer.getDisplayName()));
                }
            }
            
            // Test: Search for a customer
            List<Customer> searchResults = customerDAO.searchByName("Baltimore");
            LOGGER.info(() -> String.format("Search for 'Baltimore' found %d results", searchResults.size()));
            
            // Test: Find by ID (if we have customers)
            if (!allCustomers.isEmpty()) {
                Customer firstCustomer = allCustomers.get(0);
                Customer foundCustomer = customerDAO.findById(firstCustomer.getCustomerId());
                if (foundCustomer != null) {
                    LOGGER.info(() -> String.format("Successfully retrieved customer by ID: %s", foundCustomer.getDisplayName()));
                }
            }
            
            // Test: Create a new test customer
            Customer testCustomer = new Customer(
                "Test Customer Corp", 
                "555-TEST-001", 
                "test@testcorp.com", 
                "123 Test Street, Test City, TS 12345"
            );
            
            Customer createdCustomer = customerDAO.create(testCustomer);
            if (createdCustomer != null) {
                LOGGER.info(() -> String.format("Created test customer with ID: %d", createdCustomer.getCustomerId()));
                
                // Test: Update the customer
                createdCustomer.setPhone("555-TEST-002");
                boolean updated = customerDAO.update(createdCustomer);
                if (updated) {
                    LOGGER.info("Updated customer phone number");
                }
                
                // Test: Check if we can delete (should be able to since it's new)
                boolean canDelete = customerDAO.canDelete(createdCustomer.getCustomerId());
                LOGGER.info(() -> String.format("Can delete test customer: %b", canDelete));
                
                // Clean up: Delete the test customer
                boolean deleted = customerDAO.delete(createdCustomer.getCustomerId());
                if (deleted) {
                    LOGGER.info("Cleaned up test customer");
                } else {
                    LOGGER.warning("Failed to delete test customer - manual cleanup may be needed");
                }
            }
            
            LOGGER.info("Customer DAO tests completed successfully");
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during Customer DAO testing", e);
        }
    }
}