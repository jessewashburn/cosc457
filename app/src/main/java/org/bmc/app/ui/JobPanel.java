package org.bmc.app.ui;

import org.bmc.app.dao.JobDAO;
import org.bmc.app.dao.JobMaterialDAO;
import org.bmc.app.dao.JobMaterialDAO.JobMaterialInfo;
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
    private JobMaterialDAO jobMaterialDAO;
    private JTable jobTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JTextField customerFilter;
    private JButton addButton, editButton, deleteButton, refreshButton, manageMaterialsButton, viewPhotosButton;
    
    public JobPanel() {
        this.jobDAO = new JobDAO();
        this.jobMaterialDAO = new JobMaterialDAO();
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
        
        manageMaterialsButton = new JButton("Manage Materials");
        manageMaterialsButton.addActionListener(e -> manageMaterials());
        manageMaterialsButton.setEnabled(false);
        toolbar.add(manageMaterialsButton);
        
        viewPhotosButton = new JButton("View Photos");
        viewPhotosButton.addActionListener(e -> viewPhotos());
        viewPhotosButton.setEnabled(false);
        toolbar.add(viewPhotosButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columnNames = {"Job ID", "Customer", "Employee", "Description", "Status", "Start Date", "Due Date", "Estimated Cost"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        jobTable = new JTable(tableModel);
        jobTable.setAutoCreateRowSorter(true);
        jobTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = jobTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                manageMaterialsButton.setEnabled(hasSelection);
                viewPhotosButton.setEnabled(hasSelection);
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
            // Calculate total estimated cost from labor + material
            java.math.BigDecimal totalEstimate = java.math.BigDecimal.ZERO;
            if (job.getEstimatedLaborCost() != null) {
                totalEstimate = totalEstimate.add(job.getEstimatedLaborCost());
            }
            if (job.getEstimatedMaterialCost() != null) {
                totalEstimate = totalEstimate.add(job.getEstimatedMaterialCost());
            }
            
            Object[] row = {
                job.getJobId(),
                job.getCustomerName() != null ? job.getCustomerName() : job.getCustomerId(),
                job.getEmployeeName() != null ? job.getEmployeeName() : (job.getEmployeeId() != null ? job.getEmployeeId().toString() : "(None)"),
                job.getDescription(),
                job.getStatus().toString(),
                job.getStartDate(),
                job.getDueDate(),
                String.format("$%.2f", totalEstimate)
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
        JobDialog dialog = new JobDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.wasSaved()) {
            refreshData();
        }
    }
    
    private void editJob() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer jobId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            Job job = jobDAO.findById(jobId);
            JobDialog dialog = new JobDialog((Frame) SwingUtilities.getWindowAncestor(this), job);
            dialog.setVisible(true);
            if (dialog.wasSaved()) {
                refreshData();
            }
        } catch (Exception e) {
            logger.severe("Error loading job: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading job: " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
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
    
    private void manageMaterials() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer jobId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String description = (String) tableModel.getValueAt(selectedRow, 2);
        
        // Show materials management dialog
        JobMaterialsDialog dialog = new JobMaterialsDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            jobId, 
            description
        );
        dialog.setVisible(true);
    }
    
    private void viewPhotos() {
        int selectedRow = jobTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer jobId = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        // Show photo gallery dialog
        PhotoGalleryDialog dialog = new PhotoGalleryDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            jobId
        );
        dialog.setVisible(true);
    }
    
    public void refreshData() {
        loadJobData();
        statusFilter.setSelectedIndex(0); // Reset to "All"
        customerFilter.setText(""); // Clear customer filter
    }
    
    /**
     * Dialog for managing materials assigned to a job
     */
    private class JobMaterialsDialog extends JDialog {
        private int jobId;
        private JTable materialsTable;
        private DefaultTableModel materialsTableModel;
        private JButton assignButton, updateButton, removeButton, closeButton;
        
        public JobMaterialsDialog(Frame parent, int jobId, String jobDescription) {
            super(parent, "Manage Materials for Job: " + jobDescription, true);
            this.jobId = jobId;
            
            initializeDialog();
            loadMaterials();
            
            setSize(700, 400);
            setLocationRelativeTo(parent);
        }
        
        private void initializeDialog() {
            setLayout(new BorderLayout(10, 10));
            
            // Header
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
            JLabel titleLabel = new JLabel("Materials Assigned to This Job");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerPanel.add(titleLabel);
            add(headerPanel, BorderLayout.NORTH);
            
            // Table
            String[] columns = {"Material ID", "Material Name", "Category", "Quantity Used", "Stock Available"};
            materialsTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            materialsTable = new JTable(materialsTableModel);
            materialsTable.setAutoCreateRowSorter(true);
            materialsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            materialsTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = materialsTable.getSelectedRow() != -1;
                    updateButton.setEnabled(hasSelection);
                    removeButton.setEnabled(hasSelection);
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(materialsTable);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            add(scrollPane, BorderLayout.CENTER);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
            
            assignButton = new JButton("Assign Material");
            assignButton.addActionListener(e -> assignMaterial());
            buttonPanel.add(assignButton);
            
            updateButton = new JButton("Update Quantity");
            updateButton.addActionListener(e -> updateQuantity());
            updateButton.setEnabled(false);
            buttonPanel.add(updateButton);
            
            removeButton = new JButton("Remove Material");
            removeButton.addActionListener(e -> removeMaterial());
            removeButton.setEnabled(false);
            buttonPanel.add(removeButton);
            
            closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void loadMaterials() {
            materialsTableModel.setRowCount(0);
            List<JobMaterialInfo> materials = jobMaterialDAO.findByJobId(jobId);
            
            for (JobMaterialInfo mat : materials) {
                Object[] row = {
                    mat.getMaterialId(),
                    mat.getMaterialName(),
                    mat.getCategory() != null ? mat.getCategory() : "",
                    mat.getQuantityUsed(),
                    mat.getStockQuantity()
                };
                materialsTableModel.addRow(row);
            }
        }
        
        private void assignMaterial() {
            JobMaterialDialog dialog = new JobMaterialDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Assign Material to Job",
                null,
                null
            );
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                boolean success = jobMaterialDAO.assignMaterial(
                    jobId, 
                    dialog.getSelectedMaterialId(), 
                    dialog.getQuantity()
                );
                
                if (success) {
                    loadMaterials();
                    JOptionPane.showMessageDialog(this,
                        "Material assigned successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to assign material. Material may already be assigned to this job.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        private void updateQuantity() {
            int selectedRow = materialsTable.getSelectedRow();
            if (selectedRow == -1) return;
            
            int materialId = (Integer) materialsTableModel.getValueAt(selectedRow, 0);
            int currentQuantity = (Integer) materialsTableModel.getValueAt(selectedRow, 3);
            
            JobMaterialDialog dialog = new JobMaterialDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Update Material Quantity",
                materialId,
                currentQuantity
            );
            dialog.setVisible(true);
            
            if (dialog.isConfirmed()) {
                boolean success = jobMaterialDAO.updateQuantity(
                    jobId, 
                    materialId, 
                    dialog.getQuantity()
                );
                
                if (success) {
                    loadMaterials();
                    JOptionPane.showMessageDialog(this,
                        "Quantity updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to update quantity.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        private void removeMaterial() {
            int selectedRow = materialsTable.getSelectedRow();
            if (selectedRow == -1) return;
            
            int materialId = (Integer) materialsTableModel.getValueAt(selectedRow, 0);
            String materialName = (String) materialsTableModel.getValueAt(selectedRow, 1);
            
            int choice = JOptionPane.showConfirmDialog(this,
                "Remove " + materialName + " from this job?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = jobMaterialDAO.removeMaterial(jobId, materialId);
                
                if (success) {
                    loadMaterials();
                    JOptionPane.showMessageDialog(this,
                        "Material removed successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to remove material.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}