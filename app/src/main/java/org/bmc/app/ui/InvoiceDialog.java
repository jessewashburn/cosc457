package org.bmc.app.ui;

import org.bmc.app.dao.InvoiceDAO;
import org.bmc.app.dao.JobDAO;
import org.bmc.app.model.Invoice;
import org.bmc.app.model.Job;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Dialog for adding or editing invoice records.
 */
public class InvoiceDialog extends JDialog {
    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private JobDAO jobDAO = new JobDAO();
    private Invoice invoice;
    private boolean saved = false;
    
    // Form fields
    private JComboBox<JobItem> jobCombo = new JComboBox<>();
    private JTextField totalAmountField = new JTextField(15);
    private JTextField invoiceDateField = new JTextField(15);
    private JCheckBox paidCheckBox = new JCheckBox("Invoice Paid");
    
    // Helper class for job combo box
    private static class JobItem {
        Integer id;
        String description;
        
        JobItem(Integer id, String description) {
            this.id = id;
            this.description = description;
        }
        
        @Override
        public String toString() {
            return "Job #" + id + ": " + (description.length() > 40 ? description.substring(0, 40) + "..." : description);
        }
    }
    
    public InvoiceDialog(Frame parent, Invoice invoice) {
        super(parent, invoice == null ? "Add Invoice" : "Edit Invoice", true);
        this.invoice = invoice;
        
        setupUI();
        loadJobs();
        if (invoice != null) {
            fillFields();
        } else {
            // Set default date to today
            invoiceDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
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
        
        // Job combo
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Job:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(jobCombo, gbc);
        
        // Total amount field
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Total Amount:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        amountPanel.add(new JLabel("$"));
        amountPanel.add(totalAmountField);
        formPanel.add(amountPanel, gbc);
        
        // Invoice date field
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Invoice Date:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.add(invoiceDateField);
        datePanel.add(new JLabel(" (YYYY-MM-DD)"));
        formPanel.add(datePanel, gbc);
        
        // Paid checkbox
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Payment Status:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(paidCheckBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveInvoice());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void loadJobs() {
        try {
            List<Job> jobs = jobDAO.findAll();
            jobCombo.removeAllItems();
            for (Job j : jobs) {
                jobCombo.addItem(new JobItem(j.getJobId(), j.getDescription()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading jobs: " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void fillFields() {
        // Select the right job
        for (int i = 0; i < jobCombo.getItemCount(); i++) {
            JobItem item = jobCombo.getItemAt(i);
            if (item.id.equals(invoice.getJobId())) {
                jobCombo.setSelectedIndex(i);
                break;
            }
        }
        
        if (invoice.getTotalAmount() != null) {
            totalAmountField.setText(invoice.getTotalAmount().toString());
        }
        
        if (invoice.getInvoiceDate() != null) {
            invoiceDateField.setText(invoice.getInvoiceDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        paidCheckBox.setSelected(invoice.getPaid() != null && invoice.getPaid());
    }
    
    private void saveInvoice() {
        try {
            // Validate
            if (jobCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Job is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (totalAmountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Total amount is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse amount
            BigDecimal totalAmount;
            try {
                totalAmount = new BigDecimal(totalAmountField.getText().trim());
                if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format!", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse date
            LocalDate invoiceDate;
            try {
                invoiceDate = LocalDate.parse(invoiceDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JobItem selectedJob = (JobItem) jobCombo.getSelectedItem();
            
            if (invoice == null) {
                // New invoice
                Invoice newInvoice = new Invoice(
                    selectedJob.id,
                    invoiceDate,
                    totalAmount,
                    paidCheckBox.isSelected()
                );
                invoiceDAO.create(newInvoice);
            } else {
                // Update existing
                invoice.setJobId(selectedJob.id);
                invoice.setInvoiceDate(invoiceDate);
                invoice.setTotalAmount(totalAmount);
                invoice.setPaid(paidCheckBox.isSelected());
                invoiceDAO.update(invoice);
            }
            
            saved = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving invoice: " + e.getMessage(), 
                "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
