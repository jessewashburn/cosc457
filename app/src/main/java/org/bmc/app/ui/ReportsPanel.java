package org.bmc.app.ui;

import org.bmc.app.dao.ReportDAO;
import org.bmc.app.dao.ReportDAO.JobDueSoonReport;
import org.bmc.app.dao.ReportDAO.MaterialShortageReport;
import org.bmc.app.dao.ReportDAO.TopCustomerReport;
import org.bmc.app.dao.ReportDAO.EmployeeLaborReport;
import org.bmc.app.dao.ReportDAO.UnpaidInvoiceReport;
import org.bmc.app.dao.ReportDAO.VendorSpendingReport;
import org.bmc.app.dao.ReportDAO.JobCostComparisonReport;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
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
    
    // Material Shortages Report Components
    private JTable materialShortagesTable;
    private DefaultTableModel materialShortagesTableModel;
    
    // Employee Labor Report Components
    private JTable employeeLaborTable;
    private DefaultTableModel employeeLaborTableModel;
    
    // Unpaid Invoices Report Components
    private JTable unpaidInvoicesTable;
    private DefaultTableModel unpaidInvoicesTableModel;
    
    // Vendor Spending Report Components
    private JTable vendorSpendingTable;
    private DefaultTableModel vendorSpendingTableModel;
    
    // Labor Cost Comparison Report Components
    private JTable jobCostComparisonTable;
    private DefaultTableModel jobCostComparisonTableModel;
    
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
        reportTabs.addTab("Material Shortages", createMaterialShortagesPanel());
        reportTabs.addTab("Employee Labor", createEmployeeLaborPanel());
        reportTabs.addTab("Unpaid Invoices (30+ Days)", createUnpaidInvoicesPanel());
        reportTabs.addTab("Vendor Spending by Month", createVendorSpendingPanel());
        reportTabs.addTab("Job Cost Comparison", createJobCostComparisonPanel());
        
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
     * Create the Material Shortages report panel
     */
    private JPanel createMaterialShortagesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Material Shortages Across Active Jobs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMaterialShortagesReport());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Material ID", "Material Name", "Category", "In Stock", "Required", "Shortage", "Jobs Affected"};
        materialShortagesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        materialShortagesTable = new JTable(materialShortagesTableModel);
        materialShortagesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialShortagesTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        materialShortagesTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Material ID
        materialShortagesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Material Name
        materialShortagesTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Category
        materialShortagesTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // In Stock
        materialShortagesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Required
        materialShortagesTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Shortage
        materialShortagesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Jobs Affected
        
        JScrollPane scrollPane = new JScrollPane(materialShortagesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Shows materials where stock is insufficient for active (Planned/InProgress) jobs");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        footerPanel.add(infoLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadMaterialShortagesReport();
        
        return panel;
    }
    
    /**
     * Load material shortages from database
     */
    private void loadMaterialShortagesReport() {
        materialShortagesTableModel.setRowCount(0); // Clear existing rows
        
        List<MaterialShortageReport> shortages = reportDAO.getMaterialShortages();
        
        for (MaterialShortageReport shortage : shortages) {
            Object[] row = {
                shortage.getMaterialId(),
                shortage.getMaterialName(),
                shortage.getCategory() != null ? shortage.getCategory() : "",
                shortage.getStockQuantity(),
                shortage.getTotalRequired(),
                shortage.getShortageAmount(),
                shortage.getActiveJobsAffected()
            };
            materialShortagesTableModel.addRow(row);
        }
        
        logger.info("Loaded " + shortages.size() + " material shortages into table");
    }
    
    /**
     * Create the Employee Labor Hours and Pay report panel
     */
    private JPanel createEmployeeLaborPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Employee Labor Hours and Pay");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadEmployeeLaborReport());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Employee Name", "Role", "Hourly Rate", "Total Hours", "Total Pay", "Jobs Worked"};
        employeeLaborTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        employeeLaborTable = new JTable(employeeLaborTableModel);
        employeeLaborTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeLaborTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        employeeLaborTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        employeeLaborTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Employee Name
        employeeLaborTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Role
        employeeLaborTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Hourly Rate
        employeeLaborTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Total Hours
        employeeLaborTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Total Pay
        employeeLaborTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Jobs Worked
        
        JScrollPane scrollPane = new JScrollPane(employeeLaborTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Shows total hours worked, calculated pay (hours × hourly rate), and number of jobs per employee");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        footerPanel.add(infoLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadEmployeeLaborReport();
        
        return panel;
    }
    
    /**
     * Load employee labor report from database
     */
    private void loadEmployeeLaborReport() {
        employeeLaborTableModel.setRowCount(0); // Clear existing rows
        
        List<EmployeeLaborReport> reports = reportDAO.getEmployeeLaborReport();
        
        for (EmployeeLaborReport report : reports) {
            Object[] row = {
                report.getEmployeeId(),
                report.getEmployeeName(),
                report.getRole(),
                report.getHourlyRate() != null ? CURRENCY_FORMATTER.format(report.getHourlyRate()) : "$0.00",
                report.getTotalHours() != null ? String.format("%.2f", report.getTotalHours()) : "0.00",
                report.getTotalPay() != null ? CURRENCY_FORMATTER.format(report.getTotalPay()) : "$0.00",
                report.getJobCount()
            };
            employeeLaborTableModel.addRow(row);
        }
        
        logger.info("Loaded " + reports.size() + " employee labor reports into table");
    }
    
    /**
     * Create the Unpaid Invoices (30+ Days) report panel
     */
    private JPanel createUnpaidInvoicesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Unpaid Invoices (30+ Days Overdue)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadUnpaidInvoicesReport());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Invoice ID", "Job ID", "Customer", "Job Description", "Invoice Date", "Amount", "Days Overdue"};
        unpaidInvoicesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        unpaidInvoicesTable = new JTable(unpaidInvoicesTableModel);
        unpaidInvoicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unpaidInvoicesTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        unpaidInvoicesTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Invoice ID
        unpaidInvoicesTable.getColumnModel().getColumn(1).setPreferredWidth(60);  // Job ID
        unpaidInvoicesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Customer
        unpaidInvoicesTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Job Description
        unpaidInvoicesTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Invoice Date
        unpaidInvoicesTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Amount
        unpaidInvoicesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Days Overdue
        
        JScrollPane scrollPane = new JScrollPane(unpaidInvoicesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Shows all unpaid invoices that are more than 30 days old");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        footerPanel.add(infoLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadUnpaidInvoicesReport();
        
        return panel;
    }
    
    /**
     * Load unpaid invoices report from database
     */
    private void loadUnpaidInvoicesReport() {
        unpaidInvoicesTableModel.setRowCount(0); // Clear existing rows
        
        List<UnpaidInvoiceReport> invoices = reportDAO.getUnpaidInvoicesOlderThan30Days();
        
        for (UnpaidInvoiceReport invoice : invoices) {
            Object[] row = {
                invoice.getInvoiceId(),
                invoice.getJobId(),
                invoice.getCustomerName(),
                invoice.getJobDescription(),
                invoice.getInvoiceDate() != null ? DATE_FORMATTER.format(invoice.getInvoiceDate()) : "",
                invoice.getTotalAmount() != null ? CURRENCY_FORMATTER.format(invoice.getTotalAmount()) : "$0.00",
                invoice.getDaysOutstanding()
            };
            unpaidInvoicesTableModel.addRow(row);
        }
        
        logger.info("Loaded " + invoices.size() + " unpaid invoices into table");
    }
    
    /**
     * Create the Vendor Spending by Month report panel
     */
    private JPanel createVendorSpendingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Vendor Spending by Month");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadVendorSpendingReport());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Vendor ID", "Vendor Name", "Contact", "Year", "Month", "Total Spending", "PO Count"};
        vendorSpendingTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vendorSpendingTable = new JTable(vendorSpendingTableModel);
        vendorSpendingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vendorSpendingTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        vendorSpendingTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Vendor ID
        vendorSpendingTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Vendor Name
        vendorSpendingTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Contact
        vendorSpendingTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Year
        vendorSpendingTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Month
        vendorSpendingTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Total Spending
        vendorSpendingTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // PO Count
        
        JScrollPane scrollPane = new JScrollPane(vendorSpendingTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Shows total spending per vendor by month (excludes cancelled orders)");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        footerPanel.add(infoLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadVendorSpendingReport();
        
        return panel;
    }
    
    /**
     * Load vendor spending report from database
     */
    private void loadVendorSpendingReport() {
        vendorSpendingTableModel.setRowCount(0); // Clear existing rows
        
        List<VendorSpendingReport> spending = reportDAO.getVendorSpendingByMonth();
        
        for (VendorSpendingReport record : spending) {
            Object[] row = {
                record.getVendorId(),
                record.getVendorName(),
                record.getVendorContact() != null ? record.getVendorContact() : "",
                record.getYear(),
                record.getMonthName(),
                record.getTotalSpending() != null ? CURRENCY_FORMATTER.format(record.getTotalSpending()) : "$0.00",
                record.getPurchaseOrderCount()
            };
            vendorSpendingTableModel.addRow(row);
        }
        
        logger.info("Loaded " + spending.size() + " vendor spending records into table");
    }
    
    /**
     * Create the Labor Cost Comparison report panel
     */
    private JPanel createJobCostComparisonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Job Cost Comparison: Estimated vs Actual");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadJobCostComparisonReport());
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table with comprehensive columns
        String[] columns = {
            "Job ID", "Customer", "Description", "Status",
            "Est. Labor", "Actual Labor", "Labor Var%",
            "Est. Material", "Actual Material", "Material Var%",
            "Est. Total", "Actual Total", "Total Var%"
        };
        jobCostComparisonTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        jobCostComparisonTable = new JTable(jobCostComparisonTableModel);
        jobCostComparisonTable.setAutoCreateRowSorter(true);
        jobCostComparisonTable.setRowHeight(25);
        
        // Add custom renderer for variance columns to show red for over-budget
        jobCostComparisonTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                          boolean isSelected, boolean hasFocus, 
                                                          int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Check if it's a variance percentage column (6, 9, or 12)
                if (column == 6 || column == 9 || column == 12) {
                    String varValue = value.toString();
                    try {
                        double variance = Double.parseDouble(varValue.replace("%", ""));
                        if (variance > 0) {
                            c.setForeground(Color.RED);
                        } else {
                            c.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                        }
                    } catch (NumberFormatException e) {
                        c.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                    }
                } else {
                    c.setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
                }
                
                if (!isSelected) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(table.getSelectionBackground());
                }
                
                return c;
            }
        });
        
        // Set column widths
        jobCostComparisonTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // Job ID
        jobCostComparisonTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Customer
        jobCostComparisonTable.getColumnModel().getColumn(2).setPreferredWidth(150);  // Description
        jobCostComparisonTable.getColumnModel().getColumn(3).setPreferredWidth(80);   // Status
        jobCostComparisonTable.getColumnModel().getColumn(4).setPreferredWidth(90);   // Est. Labor
        jobCostComparisonTable.getColumnModel().getColumn(5).setPreferredWidth(90);   // Actual Labor
        jobCostComparisonTable.getColumnModel().getColumn(6).setPreferredWidth(80);   // Labor Var%
        jobCostComparisonTable.getColumnModel().getColumn(7).setPreferredWidth(100);  // Est. Material
        jobCostComparisonTable.getColumnModel().getColumn(8).setPreferredWidth(100);  // Actual Material
        jobCostComparisonTable.getColumnModel().getColumn(9).setPreferredWidth(90);   // Material Var%
        jobCostComparisonTable.getColumnModel().getColumn(10).setPreferredWidth(90);  // Est. Total
        jobCostComparisonTable.getColumnModel().getColumn(11).setPreferredWidth(90);  // Actual Total
        jobCostComparisonTable.getColumnModel().getColumn(12).setPreferredWidth(80);  // Total Var%
        
        JScrollPane scrollPane = new JScrollPane(jobCostComparisonTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Compares estimated vs actual costs for labor, materials, and total per job. Positive variance = over budget.");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        footerPanel.add(infoLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadJobCostComparisonReport();
        
        return panel;
    }
    
    /**
     * Load job cost comparison report from database
     */
    private void loadJobCostComparisonReport() {
        jobCostComparisonTableModel.setRowCount(0); // Clear existing rows
        
        List<JobCostComparisonReport> comparisons = reportDAO.getJobCostComparison();
        
        for (JobCostComparisonReport comp : comparisons) {
            Object[] row = {
                comp.getJobId(),
                comp.getCustomerName(),
                comp.getDescription(),
                comp.getStatus(),
                CURRENCY_FORMATTER.format(comp.getEstimatedLaborCost()),
                CURRENCY_FORMATTER.format(comp.getActualLaborCost()),
                String.format("%.1f%%", comp.getLaborVariancePercent()),
                CURRENCY_FORMATTER.format(comp.getEstimatedMaterialCost()),
                CURRENCY_FORMATTER.format(comp.getActualMaterialCost()),
                String.format("%.1f%%", comp.getMaterialVariancePercent()),
                CURRENCY_FORMATTER.format(comp.getEstimatedTotalCost()),
                CURRENCY_FORMATTER.format(comp.getActualTotalCost()),
                String.format("%.1f%%", comp.getTotalVariancePercent())
            };
            jobCostComparisonTableModel.addRow(row);
        }
        
        logger.info("Loaded " + comparisons.size() + " job cost comparisons into table");
    }
    
    /**
     * Refresh all reports
     */
    public void refreshReports() {
        loadJobsDueSoonReport();
        loadTopCustomersReport();
        loadMaterialShortagesReport();
        loadEmployeeLaborReport();
        loadUnpaidInvoicesReport();
        loadVendorSpendingReport();
        loadJobCostComparisonReport();
        logger.info("All reports refreshed");
    }
}
