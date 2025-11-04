package org.bmc.app;

import org.bmc.app.ui.MainFrame;
import org.bmc.app.util.DBConnection;
import java.sql.Connection;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

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
                
                // Launch the GUI on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    // Create and show the main application window
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    logger.info("Application GUI launched successfully");
                });
                
            } else {
                logger.severe("Failed to establish database connection");
                System.exit(1);
            }
        } catch (Exception e) {
            logger.severe(() -> "Database connection error: " + e.getMessage());
            System.exit(1);
        }
    }
}