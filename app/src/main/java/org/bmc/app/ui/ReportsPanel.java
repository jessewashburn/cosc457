package org.bmc.app.ui;

import org.bmc.app.dao.ReportDAO;
import org.bmc.app.dao.ReportDAO.JobDueSoonReport;
import org.bmc.app.dao.ReportDAO.TopCustomerReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Panel for displaying various business reports and analytics.
 */
public class ReportsPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ReportsPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
    
    private ReportDAO reportDAO;
    private JTabbedPane reportTabs;
    
    // Jobs Due Soon Report Components
    private JTable jobsDueTable;
    private DefaultTableModel jobsDueTableModel;
    
    // Top Customers Report Components
    private JTable topCustomersTable;
    private DefaultTableModel topCustomersTableModel;
    
    public ReportsPanel() {
        this.reportDAO = new ReportDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        createReportTabs();
        
        logger.info("Reports panel initialized");
    }
    
    private void createReportTabs() {
        reportTabs = new JTabbedPane();
        
        // Add each report as a separate tab
        reportTabs.addTab("Jobs Due Soon", createJobsDueSoonPanel());
        reportTabs.addTab("Top Customers", createTopCustomersPanel());
        
        add(reportTabs, BorderLayout.CENTER);
    }
    
    /**
     * Create the Jobs Due in Next 7 Days report panel
     */
    private JPanel createJobsDueSoonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Jobs Due in Next 7 Days");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadJobsDueSoonReport());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Job ID", "Customer", "Description", "Due Date", "Days Until Due", "Status"};
        jobsDueTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jobsDueTable = new JTable(jobsDueTableModel);
        jobsDueTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobsDueTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        jobsDueTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // Job ID
        jobsDueTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer
        jobsDueTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Description
        jobsDueTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Due Date
        jobsDueTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Days Until Due
        jobsDueTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        
        JScrollPane scrollPane = new JScrollPane(jobsDueTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Shows all non-completed jobs due within the next 7 days");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        footerPanel.add(infoLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadJobsDueSoonReport();
        
        return panel;
    }
    
    /**
     * Load jobs due in next 7 days from database
     */
    private void loadJobsDueSoonReport() {
        jobsDueTableModel.setRowCount(0); // Clear existing rows
        
        List<JobDueSoonReport> jobs = reportDAO.getJobsDueInNext7Days();
        
        for (JobDueSoonReport job : jobs) {
            Object[] row = {
                job.getJobId(),
                job.getCustomerName(),
                job.getDescription() != null ? job.getDescription() : "",
                job.getDueDate() != null ? job.getDueDate().format(DATE_FORMATTER) : "",
                formatDaysUntilDue(job.getDaysUntilDue()),
                job.getStatus()
            };
            jobsDueTableModel.addRow(row);
        }
        
        logger.info("Loaded " + jobs.size() + " jobs due soon into table");
    }
    
    /**
     * Format days until due with appropriate text
     */
    private String formatDaysUntilDue(long days) {
        if (days < 0) {
            return "OVERDUE";
        } else if (days == 0) {
            return "TODAY";
        } else if (days == 1) {
            return "1 day";
        } else {
            return days + " days";
        }
    }
    
    /**
     * Create the Top Customers by Revenue report panel
     */
    private JPanel createTopCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Top Customers by Revenue (Past 90 Days)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTopCustomersReport());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Rank", "Customer ID", "Customer Name", "Phone", "Email", "Jobs", "Total Revenue"};
        topCustomersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        topCustomersTable = new JTable(topCustomersTableModel);
        topCustomersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topCustomersTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        topCustomersTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // Rank
        topCustomersTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Customer ID
        topCustomersTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Customer Name
        topCustomersTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone
        topCustomersTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Email
        topCustomersTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // Jobs
        topCustomersTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Total Revenue
        
        JScrollPane scrollPane = new JScrollPane(topCustomersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Shows top 20 customers ranked by invoice revenue in the past quarter (90 days)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        footerPanel.add(infoLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadTopCustomersReport();
        
        return panel;
    }
    
    /**
     * Load top customers by revenue from database
     */
    private void loadTopCustomersReport() {
        topCustomersTableModel.setRowCount(0); // Clear existing rows
        
        List<TopCustomerReport> customers = reportDAO.getTopCustomersByRevenue();
        
        int rank = 1;
        for (TopCustomerReport customer : customers) {
            Object[] row = {
                rank++,
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getPhone() != null ? customer.getPhone() : "",
                customer.getEmail() != null ? customer.getEmail() : "",
                customer.getJobCount(),
                CURRENCY_FORMATTER.format(customer.getTotalRevenue())
            };
            topCustomersTableModel.addRow(row);
        }
        
        logger.info("Loaded " + customers.size() + " top customers into table");
    }
    
    /**
     * Refresh all reports
     */
    public void refreshReports() {
        loadJobsDueSoonReport();
        loadTopCustomersReport();
        logger.info("All reports refreshed");
    }
}
