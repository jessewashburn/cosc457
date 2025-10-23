package org.bmc.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection utility class for Baltimore Metal Crafters application.
 * Manages JDBC connections to MySQL database using configuration from application.properties.
 */
public class DBConnection {
    
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static String DB_DRIVER;
    
    // Static block to load configuration on class initialization
    static {
        loadConfiguration();
    }
    
    /**
     * Loads database configuration from application.properties file
     */
    private static void loadConfiguration() {
        Properties props = new Properties();
        
        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties file");
            }
            
            props.load(input);
            
            DB_URL = props.getProperty("db.url");
            DB_USER = props.getProperty("db.user");
            DB_PASSWORD = props.getProperty("db.password");
            DB_DRIVER = props.getProperty("db.driver");
            
            // Validate required properties
            if (DB_URL == null || DB_USER == null || DB_PASSWORD == null || DB_DRIVER == null) {
                throw new RuntimeException("Missing required database configuration properties");
            }
            
            // Load JDBC driver
            Class.forName(DB_DRIVER);
            
            LOGGER.info("Database configuration loaded successfully");
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading configuration file", e);
            throw new RuntimeException("Failed to load database configuration", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC driver not found", e);
            throw new RuntimeException("MySQL JDBC driver not found in classpath", e);
        }
    }
    
    /**
     * Creates and returns a new database connection
     * 
     * @return Connection object to the BMC database
     * @throws SQLException if connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            LOGGER.fine("Database connection established");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            throw e;
        }
    }
    
    /**
     * Tests the database connection
     * 
     * @return true if connection successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Safely closes a database connection
     * 
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    LOGGER.fine("Database connection closed");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
    
    /**
     * Gets the database URL for informational purposes
     * 
     * @return database URL (without credentials)
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
}