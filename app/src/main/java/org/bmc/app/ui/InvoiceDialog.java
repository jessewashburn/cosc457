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
 * Modal dialog for creating new invoices or editing existing invoice records.
 * Provides form validation, job selection, and database persistence through InvoiceDAO.
 * 
 * @author BMC Systems Team
 */
public class InvoiceDialog extends JDialog {
    
    private static final int AMOUNT_FIELD_COLS = 15;
    private static final int DATE_FIELD_COLS = 15;
    private static final String DATE_FORMAT_HINT = " (YYYY-MM-DD)";
    private static final int JOB_DESC_MAX_LENGTH = 40;
    
    private final InvoiceDAO invoiceDAO;
    private final JobDAO jobDAO;
    private final Invoice invoice;
    private boolean saved;
    
    private JComboBox<JobItem> jobCombo;
    private JTextField laborCostField;
    private JTextField materialCostField;
    private JTextField totalAmountField;
    private JTextField invoiceDateField;
    private JCheckBox paidCheckBox;
    
    /**
     * Wrapper class for displaying job information in combo box.
     */
    private static class JobItem {
        final Integer id;
        final String description;
        
        JobItem(Integer id, String description) {
            this.id = id;
            this.description = description;
        }
        
        @Override
        public String toString() {
            if (description.length() > JOB_DESC_MAX_LENGTH) {
                return "Job #" + id + ": " + description.substring(0, JOB_DESC_MAX_LENGTH) + "...";
            }
            return "Job #" + id + ": " + description;
        }
    }
    
    public InvoiceDialog(Frame parent, Invoice invoice) {
        super(parent, invoice == null ? "Add Invoice" : "Edit Invoice", true);
        this.invoiceDAO = new InvoiceDAO();
        this.jobDAO = new JobDAO();
        this.invoice = invoice;
        this.saved = false;
        
        initializeComponents();
        layoutComponents();
        loadJobs();
        
        if (invoice != null) {
            populateFields();
        } else {
            setDefaultValues();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        jobCombo = new JComboBox<>();
        laborCostField = new JTextField(AMOUNT_FIELD_COLS);
        materialCostField = new JTextField(AMOUNT_FIELD_COLS);
        totalAmountField = new JTextField(AMOUNT_FIELD_COLS);
        invoiceDateField = new JTextField(DATE_FIELD_COLS);
        paidCheckBox = new JCheckBox("Invoice Paid");
        
        // Add listeners to auto-calculate total
        laborCostField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
        });
        materialCostField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateTotal(); }
        });
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
        
        addFormRow(panel, gbc, 0, "Job:", jobCombo);
        addFormRow(panel, gbc, 1, "Labor Cost:", createAmountPanel(laborCostField));
        addFormRow(panel, gbc, 2, "Material Cost:", createAmountPanel(materialCostField));
        addFormRow(panel, gbc, 3, "Total Amount:", createAmountPanel(totalAmountField));
        addFormRow(panel, gbc, 4, "Invoice Date:", createDatePanel());
        addFormRow(panel, gbc, 5, "Payment Status:", paidCheckBox);
        
        return panel;
    }
    
    private JPanel createAmountPanel(JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(new JLabel("$"));
        panel.add(field);
        return panel;
    }
    
    private JPanel createDatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(invoiceDateField);
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
    
    private void loadJobs() {
        try {
            List<Job> jobs = jobDAO.findAll();
            jobCombo.removeAllItems();
            for (Job j : jobs) {
                jobCombo.addItem(new JobItem(j.getJobId(), j.getDescription()));
            }
        } catch (Exception e) {
            showErrorDialog("Error loading jobs: " + e.getMessage());
        }
    }
    
    private void populateFields() {
        for (int i = 0; i < jobCombo.getItemCount(); i++) {
            JobItem item = jobCombo.getItemAt(i);
            if (item.id.equals(invoice.getJobId())) {
                jobCombo.setSelectedIndex(i);
                break;
            }
        }
        
        if (invoice.getLaborCost() != null) {
            laborCostField.setText(invoice.getLaborCost().toString());
        }
        
        if (invoice.getMaterialCost() != null) {
            materialCostField.setText(invoice.getMaterialCost().toString());
        }
        
        if (invoice.getTotalAmount() != null) {
            totalAmountField.setText(invoice.getTotalAmount().toString());
        }
        
        if (invoice.getInvoiceDate() != null) {
            invoiceDateField.setText(invoice.getInvoiceDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        paidCheckBox.setSelected(invoice.getPaid() != null && invoice.getPaid());
    }
    
    private void setDefaultValues() {
        invoiceDateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        laborCostField.setText("0.00");
        materialCostField.setText("0.00");
    }
    
    private void calculateTotal() {
        try {
            BigDecimal labor = new BigDecimal(laborCostField.getText().trim().isEmpty() ? "0" : laborCostField.getText().trim());
            BigDecimal material = new BigDecimal(materialCostField.getText().trim().isEmpty() ? "0" : materialCostField.getText().trim());
            BigDecimal total = labor.add(material);
            totalAmountField.setText(total.toString());
        } catch (NumberFormatException e) {
            // Ignore invalid input during typing
        }
    }
    
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        try {
            JobItem selectedJob = (JobItem) jobCombo.getSelectedItem();
            BigDecimal amount = parseAmount();
            LocalDate date = parseDate();
            
            if (invoice == null) {
                createNewInvoice(selectedJob.id, amount, date);
            } else {
                updateExistingInvoice(selectedJob.id, amount, date);
            }
            
            saved = true;
            dispose();
        } catch (Exception e) {
            showErrorDialog("Error saving invoice: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (jobCombo.getSelectedItem() == null) {
            showErrorDialog("Job is required.");
            jobCombo.requestFocus();
            return false;
        }
        
        if (totalAmountField.getText().trim().isEmpty()) {
            showErrorDialog("Total amount is required.");
            totalAmountField.requestFocus();
            return false;
        }
        
        try {
            BigDecimal amount = new BigDecimal(totalAmountField.getText().trim());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                showErrorDialog("Amount must be positive.");
                totalAmountField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Invalid amount format.");
            totalAmountField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private BigDecimal parseAmount() {
        return new BigDecimal(totalAmountField.getText().trim());
    }
    
    private LocalDate parseDate() throws DateTimeParseException {
        return LocalDate.parse(invoiceDateField.getText().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    private BigDecimal parseLaborCost() {
        String text = laborCostField.getText().trim();
        return text.isEmpty() ? BigDecimal.ZERO : new BigDecimal(text);
    }
    
    private BigDecimal parseMaterialCost() {
        String text = materialCostField.getText().trim();
        return text.isEmpty() ? BigDecimal.ZERO : new BigDecimal(text);
    }
    
    private void createNewInvoice(Integer jobId, BigDecimal amount, LocalDate date) throws Exception {
        Invoice newInvoice = new Invoice(
            jobId,
            date,
            amount,
            paidCheckBox.isSelected()
        );
        newInvoice.setLaborCost(parseLaborCost());
        newInvoice.setMaterialCost(parseMaterialCost());
        invoiceDAO.create(newInvoice);
    }
    
    private void updateExistingInvoice(Integer jobId, BigDecimal amount, LocalDate date) throws Exception {
        invoice.setJobId(jobId);
        invoice.setInvoiceDate(date);
        invoice.setLaborCost(parseLaborCost());
        invoice.setMaterialCost(parseMaterialCost());
        invoice.setTotalAmount(amount);
        invoice.setPaid(paidCheckBox.isSelected());
        invoiceDAO.update(invoice);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
