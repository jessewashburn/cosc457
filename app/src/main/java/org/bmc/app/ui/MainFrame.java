package org.bmc.app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * Main application window for Baltimore Metal Crafters database application.
 * Provides tabbed interface for managing customers, employees, jobs, and invoices.
 */
public class MainFrame extends JFrame {
    private static final Logger logger = Logger.getLogger(MainFrame.class.getName());
    
    private JTabbedPane tabbedPane;
    private CustomerPanel customerPanel;
    private EmployeePanel employeePanel;
    private JobPanel jobPanel;
    private InvoicePanel invoicePanel;
    
    public MainFrame() {
        initializeFrame();
        createMenuBar();
        createMainContent();
        setupEventHandlers();
        
        logger.info("Main application window initialized");
    }
    
    private void initializeFrame() {
        setTitle("Baltimore Metal Crafters - Database Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null); // Center on screen
        
        // Set application icon if available
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/app-icon.png")));
        } catch (Exception e) {
            // Icon not found, continue without it
            logger.info("Application icon not found, using default");
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        JMenuItem refreshItem = new JMenuItem("Refresh All Data");
        refreshItem.setMnemonic('R');
        refreshItem.addActionListener(e -> refreshAllPanels());
        
        viewMenu.add(refreshItem);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');
        
        JMenuItem reportsItem = new JMenuItem("Reports");
        reportsItem.setMnemonic('R');
        reportsItem.addActionListener(e -> showReportsDialog());
        
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.setMnemonic('S');
        settingsItem.addActionListener(e -> showSettingsDialog());
        
        toolsMenu.add(reportsItem);
        toolsMenu.addSeparator();
        toolsMenu.add(settingsItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void createMainContent() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        // Create panels for each main entity
        customerPanel = new CustomerPanel();
        employeePanel = new EmployeePanel();
        jobPanel = new JobPanel();
        invoicePanel = new InvoicePanel();
        
        // Add tabs with mnemonics
        tabbedPane.addTab("Customers", null, customerPanel, "Manage customer information");
        tabbedPane.setMnemonicAt(0, java.awt.event.KeyEvent.VK_C);
        
        tabbedPane.addTab("Employees", null, employeePanel, "Manage employee information");
        tabbedPane.setMnemonicAt(1, java.awt.event.KeyEvent.VK_E);
        
        tabbedPane.addTab("Jobs", null, jobPanel, "Manage restoration jobs");
        tabbedPane.setMnemonicAt(2, java.awt.event.KeyEvent.VK_J);
        
        tabbedPane.addTab("Invoices", null, invoicePanel, "Manage billing and invoices");
        tabbedPane.setMnemonicAt(3, java.awt.event.KeyEvent.VK_I);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        
        JLabel statusLabel = new JLabel("Ready - Database connected");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        JLabel timeLabel = new JLabel();
        timeLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        // Update time every second
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        });
        timer.start();
        
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private void setupEventHandlers() {
        // Tab change listener
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String tabName = tabbedPane.getTitleAt(selectedIndex);
            logger.info("Switched to tab: " + tabName);
            
            // Refresh data when switching tabs
            refreshCurrentPanel();
        });
    }
    
    private void refreshAllPanels() {
        customerPanel.refreshData();
        employeePanel.refreshData();
        jobPanel.refreshData();
        invoicePanel.refreshData();
        
        JOptionPane.showMessageDialog(
            this,
            "All data refreshed successfully",
            "Refresh Complete",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void refreshCurrentPanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0 -> customerPanel.refreshData();
            case 1 -> employeePanel.refreshData();
            case 2 -> jobPanel.refreshData();
            case 3 -> invoicePanel.refreshData();
        }
    }
    
    private void showReportsDialog() {
        JOptionPane.showMessageDialog(
            this,
            "Reports functionality will be implemented in a future version.",
            "Reports",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showSettingsDialog() {
        JOptionPane.showMessageDialog(
            this,
            "Settings dialog will be implemented in a future version.",
            "Settings",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void showAboutDialog() {
        String aboutText = """
            Baltimore Metal Crafters Database Management System
            Version 1.0.0
            
            A comprehensive application for managing restoration projects,
            customer relationships, employee records, and billing.
            
            Built with Java 21 and MySQL
            """;
            
        JOptionPane.showMessageDialog(
            this,
            aboutText,
            "About BMC Database System",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Get reference to customer panel for cross-panel operations
     */
    public CustomerPanel getCustomerPanel() {
        return customerPanel;
    }
    
    /**
     * Get reference to job panel for cross-panel operations
     */
    public JobPanel getJobPanel() {
        return jobPanel;
    }
    
    /**
     * Switch to a specific tab programmatically
     */
    public void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(tabIndex);
        }
    }
}