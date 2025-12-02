package org.bmc.app;

import org.bmc.app.ui.MainFrame;
import org.bmc.app.ui.ConnectionStatusDialog;
import org.bmc.app.util.DBConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Main application entry point for Baltimore Metal Crafters database application.
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args) {
        logger.info("Starting Baltimore Metal Crafters Database Application...");
        
        System.out.println("=== BMC Application Starting ===");
        System.out.println("Creating connection status dialog...");
        
        // Show connection status dialog immediately
        SwingUtilities.invokeLater(() -> {
            System.out.println("SwingUtilities.invokeLater executing...");
            try {
                ConnectionStatusDialog statusDialog = new ConnectionStatusDialog();
                System.out.println("Dialog created, making visible...");
                statusDialog.setVisible(true);
                System.out.println("Dialog is now visible");
                
                // Attempt database connection in background
                System.out.println("Starting connection attempt...");
                attemptConnection(statusDialog);
            } catch (Exception e) {
                System.err.println("ERROR creating dialog: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        System.out.println("Main method completed, waiting for Swing EDT...");
    }
    
    /**
     * Attempt to connect to the database and show progress
     */
    private static void attemptConnection(ConnectionStatusDialog statusDialog) {
        System.out.println("Creating SwingWorker for connection...");
        SwingWorker<Connection, String> connectionWorker = new SwingWorker<Connection, String>() {
            private volatile boolean cancelled = false;
            
            @Override
            protected Connection doInBackground() throws Exception {
                System.out.println("SwingWorker.doInBackground() started");
                try {
                    publish("Step 1/4: Loading database configuration...");
                    System.out.println("Getting database URL...");
                    String dbUrl = DBConnection.getDatabaseUrl();
                    System.out.println("Database URL: " + dbUrl);
                    publish("Database URL: " + dbUrl);
                    Thread.sleep(500);
                    
                    if (cancelled) throw new InterruptedException("Connection cancelled");
                    
                    publish("Step 2/4: Checking JDBC driver...");
                    System.out.println("Loading JDBC driver...");
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    System.out.println("Driver loaded successfully");
                    publish("MySQL JDBC driver loaded successfully");
                    Thread.sleep(500);
                    
                    if (cancelled) throw new InterruptedException("Connection cancelled");
                    
                    publish("Step 3/4: Connecting to triton.towson.edu:3360...");
                    publish("Timeout set to 15 seconds - this may take a while on VPN...");
                    System.out.println("Attempting connection with 15 second timeout...");
                    
                    long startTime = System.currentTimeMillis();
                    Connection conn = null;
                    
                    try {
                        // Set a timeout using DriverManager
                        DriverManager.setLoginTimeout(15);
                        System.out.println("Calling DBConnection.getConnection()...");
                        conn = DBConnection.getConnection();
                        long elapsed = System.currentTimeMillis() - startTime;
                        System.out.println("Connection obtained in " + elapsed + "ms");
                        publish("Connection established in " + elapsed + "ms");
                    } catch (java.sql.SQLTimeoutException e) {
                        long elapsed = System.currentTimeMillis() - startTime;
                        System.err.println("SQLTimeoutException after " + elapsed + "ms: " + e.getMessage());
                        throw new Exception("Connection timeout after " + elapsed + "ms - VPN may be slow or disconnected", e);
                    } catch (java.sql.SQLNonTransientConnectionException e) {
                        System.err.println("SQLNonTransientConnectionException: " + e.getMessage());
                        String msg = e.getMessage();
                        if (msg != null) {
                            if (msg.contains("Communications link failure")) {
                                throw new Exception("Cannot reach database server - check VPN connection", e);
                            } else if (msg.contains("Access denied")) {
                                throw new Exception("Access denied - check database credentials", e);
                            }
                        }
                        throw new Exception("Connection error: " + (msg != null ? msg : "Unknown error"), e);
                    } catch (java.sql.SQLException e) {
                        System.err.println("SQLException: " + e.getMessage());
                        String msg = e.getMessage();
                        if (msg != null && msg.contains("timeout")) {
                            throw new Exception("Connection timeout - VPN may be disconnected", e);
                        }
                        throw new Exception("SQL error: " + (msg != null ? msg : "Unknown SQL error"), e);
                    }
                    
                    if (cancelled) {
                        if (conn != null) conn.close();
                        throw new InterruptedException("Connection cancelled");
                    }
                    
                    if (conn != null) {
                        publish("Step 4/4: Verifying database access...");
                        System.out.println("Verifying connection...");
                        if (!conn.isClosed() && conn.isValid(3)) {
                            System.out.println("Connection verified successfully");
                            publish("Database connection verified!");
                            Thread.sleep(300);
                        } else {
                            throw new Exception("Connection established but failed validation");
                        }
                    } else {
                        throw new Exception("Connection returned null - unknown error");
                    }
                    
                    return conn;
                    
                } catch (ClassNotFoundException e) {
                    System.err.println("ClassNotFoundException: " + e.getMessage());
                    throw new Exception("MySQL JDBC driver not found in classpath", e);
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException: " + e.getMessage());
                    throw e;
                } catch (Exception e) {
                    System.err.println("Exception in doInBackground: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    statusDialog.appendDetails(message);
                }
            }
            
            @Override
            protected void done() {
                try {
                    Connection conn = get();
                    if (conn != null) {
                        conn.close(); // Close test connection
                        logger.info("Database connection successful!");
                        statusDialog.showSuccess("Connected successfully!");
                        
                        // Launch the main application after brief delay
                        SwingUtilities.invokeLater(() -> {
                            MainFrame mainFrame = new MainFrame();
                            mainFrame.setVisible(true);
                            logger.info("Application GUI launched successfully");
                        });
                    } else {
                        handleConnectionFailure(statusDialog, "Connection returned null", null);
                    }
                } catch (java.util.concurrent.CancellationException e) {
                    handleConnectionFailure(statusDialog, "Connection was cancelled", e);
                } catch (java.util.concurrent.ExecutionException e) {
                    Throwable cause = e.getCause();
                    handleConnectionFailure(statusDialog, 
                        cause != null ? cause.getMessage() : e.getMessage(), 
                        cause != null ? (Exception)cause : e);
                } catch (Exception e) {
                    handleConnectionFailure(statusDialog, e.getMessage(), e);
                }
            }
        };
        
        connectionWorker.execute();
    }
    
    /**
     * Handle connection failure and offer retry
     */
    private static void handleConnectionFailure(ConnectionStatusDialog statusDialog, String message, Exception e) {
        logger.severe(() -> "Database connection error: " + message);
        if (e != null) {
            e.printStackTrace();
        }
        
        String errorMessage = "Failed to connect to database";
        String details = buildErrorDetails(message, e);
        
        statusDialog.showError(errorMessage, details, () -> attemptConnection(statusDialog));
    }
    
    /**
     * Build detailed error message
     */
    private static String buildErrorDetails(String message, Exception e) {
        StringBuilder details = new StringBuilder();
        
        if (message != null && !message.isEmpty()) {
            details.append("Error: ").append(message).append("\n");
        }
        
        if (e != null) {
            // Add exception type
            details.append("\nException Type: ").append(e.getClass().getSimpleName());
            
            // Add detailed message
            String exceptionMsg = e.getMessage();
            if (exceptionMsg != null && !exceptionMsg.equals(message)) {
                details.append("\nDetails: ").append(exceptionMsg);
            }
            
            // Add specific diagnostics based on error type
            if (e instanceof java.sql.SQLException) {
                java.sql.SQLException sqlEx = (java.sql.SQLException)e;
                details.append("\nSQL State: ").append(sqlEx.getSQLState());
                details.append("\nError Code: ").append(sqlEx.getErrorCode());
            }
        }
        
        // Add helpful troubleshooting hints
        details.append("\n\n=== Troubleshooting ===");
        
        if (message != null) {
            if (message.contains("Communications link failure") || 
                message.contains("timeout") || 
                message.contains("Cannot reach")) {
                details.append("\n✗ Network Issue Detected");
                details.append("\n  → Check Towson University VPN connection");
                details.append("\n  → Verify you can ping triton.towson.edu");
                details.append("\n  → Check if port 3360 is accessible");
            } else if (message.contains("Access denied")) {
                details.append("\n✗ Authentication Issue Detected");
                details.append("\n  → Verify database username (jwashb2)");
                details.append("\n  → Verify database password");
                details.append("\n  → Check DBConnection.java credentials");
            } else if (message.contains("Unknown database")) {
                details.append("\n✗ Database Not Found");
                details.append("\n  → Verify database name: jwashb2db");
                details.append("\n  → Check if database exists on server");
            }
        }
        
        details.append("\n\nCommon Causes:");
        details.append("\n• Not connected to Towson University VPN");
        details.append("\n• VPN connected but unstable/slow");
        details.append("\n• Database server temporarily unavailable");
        details.append("\n• Incorrect credentials in application.properties");
        details.append("\n• Firewall blocking MySQL port 3360");
        details.append("\n• Network timeout (connection too slow)");
        
        details.append("\n\nDatabase Configuration:");
        try {
            details.append("\n• URL: ").append(DBConnection.getDatabaseUrl());
        } catch (Exception ex) {
            details.append("\n• URL: [Error reading configuration]");
        }
        
        return details.toString();
    }
}