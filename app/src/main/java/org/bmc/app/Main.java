package org.bmc.app;

import org.bmc.app.ui.MainFrame;
import org.bmc.app.util.DBConnection;
import java.sql.Connection;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for Baltimore Metal Crafters database application.
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        logger.info("Starting Baltimore Metal Crafters Database Application...");
        
        // Test database connection first
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                logger.info("Database connection successful!");
                logger.info("Connected to: " + conn.getMetaData().getURL());
                
                // Launch the GUI on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Set system look and feel
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
                        
                        // Create and show the main application window
                        MainFrame mainFrame = new MainFrame();
                        mainFrame.setVisible(true);
                        
                        logger.info("Application GUI launched successfully");
                    } catch (Exception e) {
                        logger.severe("Error launching GUI: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
                
            } else {
                logger.severe("Failed to establish database connection");
                System.exit(1);
            }
        } catch (Exception e) {
            logger.severe("Database connection error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
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