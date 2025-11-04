package org.bmc.app.test;

import java.util.logging.Logger;

import org.bmc.app.util.DBConnection;

/**
 * Quick test to verify database connectivity
 */
public class DatabaseConnectionTest {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnectionTest.class.getName());
    
    public static void main(String[] args) {
        LOGGER.info("=== Database Connection Test ===");
        
        try {
            // Test database connection
            boolean connected = DBConnection.testConnection();
            
            if (connected) {
                LOGGER.info("SUCCESS: Database connection established!");
                LOGGER.info(() -> String.format("Connected to: %s", DBConnection.getDatabaseUrl()));
            } else {
                LOGGER.warning("FAILED: Could not connect to database");
                LOGGER.warning("Please check your application.properties file");
            }
            
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("ERROR: %s", e.getMessage()));
            LOGGER.info("Common issues:");
            LOGGER.info("1. Check MySQL server is running");
            LOGGER.info("2. Verify database 'bmc_db' exists");
            LOGGER.info("3. Update password in application.properties");
            LOGGER.info("4. Ensure MySQL connector JAR is in classpath");
        }
    }
}