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
 * Modal dialog for creating new jobs or editing existing job records.
 * Provides form validation, customer selection, and database persistence through JobDAO.
 * 
 * @author BMC Systems Team
 */
public class JobDialog extends JDialog {
    
    private static final int DESC_ROWS = 4;
    private static final int DESC_COLS = 30;
    private static final int DATE_FIELD_COLS = 15;
    private static final String DATE_FORMAT_HINT = " (YYYY-MM-DD)";
    
    private final JobDAO jobDAO;
    private final CustomerDAO customerDAO;
    private final Job job;
    private boolean saved;
    
    private JComboBox<CustomerItem> customerCombo;
    private JTextArea descriptionArea;
    private JComboBox<Job.Status> statusCombo;
    private JTextField startDateField;
    private JTextField dueDateField;
    private JTextField estimatedLaborCostField;
    private JTextField estimatedMaterialCostField;
    
    /**
     * Wrapper class for displaying customer information in combo box.
     */
    private static class CustomerItem {
        final Integer id;
        final String name;
        
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
        this.jobDAO = new JobDAO();
        this.customerDAO = new CustomerDAO();
        this.job = job;
        this.saved = false;
        
        initializeComponents();
        layoutComponents();
        loadCustomers();
        
        if (job != null) {
            populateFields();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        customerCombo = new JComboBox<>();
        descriptionArea = new JTextArea(DESC_ROWS, DESC_COLS);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        statusCombo = new JComboBox<>(Job.Status.values());
        startDateField = new JTextField(DATE_FIELD_COLS);
        dueDateField = new JTextField(DATE_FIELD_COLS);
        estimatedLaborCostField = new JTextField(DATE_FIELD_COLS);
        estimatedMaterialCostField = new JTextField(DATE_FIELD_COLS);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        addFormRow(panel, gbc, 0, "Customer:", customerCombo);
        
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        addFormRow(panel, gbc, 2, "Status:", statusCombo);
        addFormRow(panel, gbc, 3, "Start Date:", createDatePanel(startDateField));
        addFormRow(panel, gbc, 4, "Due Date:", createDatePanel(dueDateField));
        addFormRow(panel, gbc, 5, "Est. Labor Cost:", estimatedLaborCostField);
        addFormRow(panel, gbc, 6, "Est. Material Cost:", estimatedMaterialCostField);
        
        return panel;
    }
    
    private JPanel createDatePanel(JTextField dateField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(dateField);
        panel.add(new JLabel(DATE_FORMAT_HINT));
        return panel;
    }
    
    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.findAll();
            customerCombo.removeAllItems();
            for (Customer c : customers) {
                customerCombo.addItem(new CustomerItem(c.getCustomerId(), c.getName()));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading customers: " + e.getMessage());
        }
    }
    
    private void populateFields() {
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
        if (job.getEstimatedLaborCost() != null) {
            estimatedLaborCostField.setText(job.getEstimatedLaborCost().toString());
        }
        if (job.getEstimatedMaterialCost() != null) {
            estimatedMaterialCostField.setText(job.getEstimatedMaterialCost().toString());
        }
    }
    
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        try {
            CustomerItem selectedCustomer = (CustomerItem) customerCombo.getSelectedItem();
            LocalDate startDate = parseDateField(startDateField);
            LocalDate dueDate = parseDateField(dueDateField);
            
            if (job == null) {
                createNewJob(selectedCustomer.id, startDate, dueDate);
            } else {
                updateExistingJob(selectedCustomer.id, startDate, dueDate);
            }
            
            saved = true;
            dispose();
        } catch (Exception e) {
            showErrorDialog("Error saving job: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (customerCombo.getSelectedItem() == null) {
            showErrorDialog("Customer is required.");
            customerCombo.requestFocus();
            return false;
        }
        
        if (descriptionArea.getText().trim().isEmpty()) {
            showErrorDialog("Description is required.");
            descriptionArea.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private LocalDate parseDateField(JTextField field) throws DateTimeParseException {
        String text = field.getText().trim();
        return text.isEmpty() ? null : LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    private void createNewJob(Integer customerId, LocalDate startDate, LocalDate dueDate) throws Exception {
        Job newJob = new Job(
            customerId,
            null,
            descriptionArea.getText().trim(),
            startDate,
            dueDate,
            (Job.Status) statusCombo.getSelectedItem(),
            parseEstimatedLaborCost(),
            parseEstimatedMaterialCost()
        );
        jobDAO.create(newJob);
    }
    
    private void updateExistingJob(Integer customerId, LocalDate startDate, LocalDate dueDate) throws Exception {
        job.setCustomerId(customerId);
        job.setDescription(descriptionArea.getText().trim());
        job.setStartDate(startDate);
        job.setDueDate(dueDate);
        job.setStatus((Job.Status) statusCombo.getSelectedItem());
        job.setEstimatedLaborCost(parseEstimatedLaborCost());
        job.setEstimatedMaterialCost(parseEstimatedMaterialCost());
        jobDAO.update(job);
    }
    
    private java.math.BigDecimal parseEstimatedLaborCost() {
        String text = estimatedLaborCostField.getText().trim();
        if (text.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        try {
            return new java.math.BigDecimal(text);
        } catch (NumberFormatException e) {
            return java.math.BigDecimal.ZERO;
        }
    }
    
    private java.math.BigDecimal parseEstimatedMaterialCost() {
        String text = estimatedMaterialCostField.getText().trim();
        if (text.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        try {
            return new java.math.BigDecimal(text);
        } catch (NumberFormatException e) {
            return java.math.BigDecimal.ZERO;
        }
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
