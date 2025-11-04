package org.bmc.app.ui;

import org.bmc.app.dao.JobDAO;
import org.bmc.app.model.Job;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel for managing job data with table view and status filtering.
 */
public class JobPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(JobPanel.class.getName());
    
    private JobDAO jobDAO;
    private JTable jobTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JTextField customerFilter;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    public JobPanel() {
        this.jobDAO = new JobDAO();
        initializePanel();
        loadJobData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Job Management"));
        
        // Create toolbar
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Create table
        createTable();
        JScrollPane scrollPane = new JScrollPane(jobTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create info panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Status filter
        toolbar.add(new JLabel("Status:"));
        String[] statuses = {"All", "Pending", "InProgress", "Completed", "OnHold", "Cancelled"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.addActionListener(e -> filterByStatus());
        toolbar.add(statusFilter);
        
        // Customer filter
        toolbar.add(new JLabel("Customer ID:"));
        customerFilter = new JTextField(8);
        customerFilter.addActionListener(e -> filterByCustomer());
        toolbar.add(customerFilter);
        
        JButton customerSearchButton = new JButton("Filter");
        customerSearchButton.addActionListener(e -> filterByCustomer());
        toolbar.add(customerSearchButton);
        
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        
        // CRUD buttons
        addButton = new JButton("Add Job");
        addButton.addActionListener(e -> addJob());
        toolbar.add(addButton);
        
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> editJob());
        editButton.setEnabled(false);
        toolbar.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteJob());
        deleteButton.setEnabled(false);
        toolbar.add(deleteButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columnNames = {"Job ID", "Customer ID", "Description", "Status", "Start Date", "Due Date", "Estimated Cost"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        jobTable = new JTable(tableModel);
        jobTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = jobTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
        
        // Set column widths
        jobTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Job ID
        jobTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Customer ID
        jobTable.getColumnModel().getColumn(2).setPreferredWidth(300); // Description
        jobTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        jobTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Start Date
        jobTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Due Date
        jobTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Estimated Cost
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel infoLabel = new JLabel("Select a job to edit or delete. Filter by status or customer ID to narrow results.");
        infoPanel.add(infoLabel);
        
        return infoPanel;
    }
    
    private void loadJobData() {
        try {
            List<Job> jobs = jobDAO.findAll();
            populateTable(jobs);
            logger.info("Loaded " + jobs.size() + " jobs into table");
        } catch (Exception e) {
            logger.severe("Error loading job data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error loading job data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateTable(List<Job> jobs) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Job job : jobs) {
            Object[] row = {
                job.getJobId(),
                job.getCustomerId(),
                job.getDescription(),
                job.getStatus().toString(),
                job.getStartDate(),
                job.getDueDate(),
                String.format("$%.2f", job.getEstimatedValue())
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterByStatus() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        if ("All".equals(selectedStatus)) {
            loadJobData();
            return;
        }
        
        try {
            Job.Status status = Job.Status.fromString(selectedStatus);
            if (status != null) {
                List<Job> results = jobDAO.findByStatus(status);
                populateTable(results);
                logger.info("Filter by status '" + selectedStatus + "' returned " + results.size() + " results");
            } else {
                logger.warning("Invalid status selected: " + selectedStatus);
                loadJobData(); // Fallback to showing all
            }
        } catch (Exception e) {
            logger.severe("Error filtering jobs by status: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error filtering jobs: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterByCustomer() {
        String customerIdText = customerFilter.getText().trim();
        
        if (customerIdText.isEmpty()) {
            loadJobData();
            return;
        }
        
        try {
            Integer customerId = Integer.parseInt(customerIdText);
            List<Job> results = jobDAO.findByCustomer(customerId);
            populateTable(results);
            logger.info("Filter by customer ID " + customerId + " returned " + results.size() + " results");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid customer ID (number).",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            logger.severe("Error filtering jobs by customer: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error filtering jobs: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addJob() {
        // TODO: Implement add job dialog
        JOptionPane.showMessageDialog(this,
            "Add Job dialog will be implemented in the next phase.",
            "Feature Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editJob() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // TODO: Implement edit job dialog
        Integer jobId = (Integer) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this,
            "Edit Job dialog for ID " + jobId + " will be implemented in the next phase.",
            "Feature Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteJob() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer jobId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String description = (String) tableModel.getValueAt(selectedRow, 2);
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete job:\n" + description + " (ID: " + jobId + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                jobDAO.delete(jobId);
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Job deleted successfully.",
                    "Delete Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                logger.severe("Error deleting job: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error deleting job: " + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadJobData();
        statusFilter.setSelectedIndex(0); // Reset to "All"
        customerFilter.setText(""); // Clear customer filter
    }
}