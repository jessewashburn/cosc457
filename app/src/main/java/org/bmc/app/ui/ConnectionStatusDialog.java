package org.bmc.app.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog that shows database connection status and progress.
 * Opens immediately on startup to provide feedback to the user.
 */
public class ConnectionStatusDialog extends JDialog {
    private JLabel statusLabel;
    private JTextArea detailsArea;
    private JProgressBar progressBar;
    private JButton retryButton;
    private JButton exitButton;
    
    public ConnectionStatusDialog() {
        setTitle("Database Connection");
        setModal(false);  // Changed to non-modal so it doesn't block
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        
        initComponents();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Baltimore Metal Crafters");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with status info
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        statusLabel = new JLabel("Connecting to database...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        centerPanel.add(progressBar, BorderLayout.CENTER);
        
        detailsArea = new JTextArea(5, 40);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        detailsArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        centerPanel.add(scrollPane, BorderLayout.SOUTH);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        retryButton = new JButton("Retry Connection");
        retryButton.setVisible(false);
        exitButton = new JButton("Exit");
        exitButton.setVisible(false);
        
        buttonPanel.add(retryButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Update the status message
     */
    public void setStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            appendDetails(message);
        });
    }
    
    /**
     * Append details to the log area
     */
    public void appendDetails(String message) {
        SwingUtilities.invokeLater(() -> {
            detailsArea.append(message + "\n");
            detailsArea.setCaretPosition(detailsArea.getDocument().getLength());
        });
    }
    
    /**
     * Show success status
     */
    public void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("✓ " + message);
            statusLabel.setForeground(new Color(0, 128, 0));
            progressBar.setIndeterminate(false);
            progressBar.setValue(100);
            appendDetails("SUCCESS: " + message);
            
            // Auto-close after 1 second
            Timer timer = new Timer(1000, e -> dispose());
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    /**
     * Show error status with retry option
     */
    public void showError(String message, String details, Runnable retryAction) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("✗ " + message);
            statusLabel.setForeground(Color.RED);
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
            appendDetails("ERROR: " + message);
            if (details != null && !details.isEmpty()) {
                appendDetails("Details: " + details);
            }
            
            retryButton.setVisible(true);
            exitButton.setVisible(true);
            
            retryButton.addActionListener(e -> {
                retryButton.setVisible(false);
                exitButton.setVisible(false);
                resetProgress();
                if (retryAction != null) {
                    retryAction.run();
                }
            });
            
            exitButton.addActionListener(e -> System.exit(1));
        });
    }
    
    /**
     * Reset progress bar for retry
     */
    private void resetProgress() {
        statusLabel.setText("Connecting to database...");
        statusLabel.setForeground(Color.BLACK);
        progressBar.setIndeterminate(true);
        detailsArea.append("\n--- Retrying connection ---\n");
    }
    
    /**
     * Add retry button action listener
     */
    public void setRetryAction(Runnable action) {
        retryButton.addActionListener(e -> {
            retryButton.setVisible(false);
            exitButton.setVisible(false);
            resetProgress();
            action.run();
        });
    }
}
