package org.bmc.app.ui;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.dao.JobDAO;
import org.bmc.app.model.Customer;
import org.bmc.app.model.Job;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for adding or editing job records.
 */
public class JobDialog extends JDialog {
    private JobDAO jobDAO = new JobDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private Job job;
    private boolean saved = false;
    
    // Form fields
    private JComboBox<CustomerItem> customerCombo = new JComboBox<>();
    private JTextArea descriptionArea = new JTextArea(4, 30);
    private JComboBox<Job.Status> statusCombo = new JComboBox<>(Job.Status.values());
    private JTextField startDateField = new JTextField(15);
    private JTextField dueDateField = new JTextField(15);
    
    // Helper class for customer combo box
    private static class CustomerItem {
        Integer id;
        String name;
        
        CustomerItem(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name + " (ID: " + id + ")";
        }
    }
    
    public JobDialog(Frame parent, Job job) {
        super(parent, job == null ? "Add Job" : "Edit Job", true);
        this.job = job;
        
        setupUI();
        loadCustomers();
        if (job != null) {
            fillFields();
        }
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Customer combo
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(customerCombo, gbc);
        
        // Description area
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);
        
        // Status combo
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(statusCombo, gbc);
        
        // Start date field
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startDatePanel.add(startDateField);
        startDatePanel.add(new JLabel(" (YYYY-MM-DD)"));
        formPanel.add(startDatePanel, gbc);
        
        // Due date field
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Due Date:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel dueDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dueDatePanel.add(dueDateField);
        dueDatePanel.add(new JLabel(" (YYYY-MM-DD)"));
        formPanel.add(dueDatePanel, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveJob());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.findAll();
            customerCombo.removeAllItems();
            for (Customer c : customers) {
                customerCombo.addItem(new CustomerItem(c.getCustomerId(), c.getName()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void fillFields() {
        // Select the right customer
        for (int i = 0; i < customerCombo.getItemCount(); i++) {
            CustomerItem item = customerCombo.getItemAt(i);
            if (item.id.equals(job.getCustomerId())) {
                customerCombo.setSelectedIndex(i);
                break;
            }
        }
        
        descriptionArea.setText(job.getDescription());
        statusCombo.setSelectedItem(job.getStatus());
        
        if (job.getStartDate() != null) {
            startDateField.setText(job.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        if (job.getDueDate() != null) {
            dueDateField.setText(job.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
    }
    
    private void saveJob() {
        try {
            // Validate
            if (customerCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Customer is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (descriptionArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Description is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse dates
            LocalDate startDate = null;
            LocalDate dueDate = null;
            
            try {
                if (!startDateField.getText().trim().isEmpty()) {
                    startDate = LocalDate.parse(startDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
                if (!dueDateField.getText().trim().isEmpty()) {
                    dueDate = LocalDate.parse(dueDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            CustomerItem selectedCustomer = (CustomerItem) customerCombo.getSelectedItem();
            
            if (job == null) {
                // New job
                Job newJob = new Job(
                    selectedCustomer.id,
                    null,  // quoteId
                    descriptionArea.getText().trim(),
                    startDate,
                    dueDate,
                    (Job.Status) statusCombo.getSelectedItem()
                );
                jobDAO.create(newJob);
            } else {
                // Update existing
                job.setCustomerId(selectedCustomer.id);
                job.setDescription(descriptionArea.getText().trim());
                job.setStartDate(startDate);
                job.setDueDate(dueDate);
                job.setStatus((Job.Status) statusCombo.getSelectedItem());
                jobDAO.update(job);
            }
            
            saved = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving job: " + e.getMessage(), 
                "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
