package org.bmc.app.ui;

import org.bmc.app.dao.ReportDAO;
import org.bmc.app.dao.ReportDAO.JobDueSoonReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel for displaying various business reports and analytics.
 */
public class ReportsPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(ReportsPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private ReportDAO reportDAO;
    private JTabbedPane reportTabs;
    
    // Jobs Due Soon Report Components
    private JTable jobsDueTable;
    private DefaultTableModel jobsDueTableModel;
    
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
        // Future reports will be added here as additional tabs
        
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
     * Refresh all reports
     */
    public void refreshReports() {
        loadJobsDueSoonReport();
        logger.info("All reports refreshed");
    }
}
