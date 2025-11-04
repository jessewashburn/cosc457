package org.bmc.app.ui;

import org.bmc.app.dao.InvoiceDAO;
import org.bmc.app.model.Invoice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel for managing invoice data with table view and payment status filtering.
 */
public class InvoicePanel extends JPanel {
    private static final Logger logger = Logger.getLogger(InvoicePanel.class.getName());
    
    private InvoiceDAO invoiceDAO;
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> paymentStatusFilter;
    private JCheckBox overdueOnlyFilter;
    private JButton addButton, editButton, deleteButton, refreshButton, markPaidButton;
    
    public InvoicePanel() {
        this.invoiceDAO = new InvoiceDAO();
        initializePanel();
        loadInvoiceData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Invoice Management"));
        
        // Create toolbar
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Create table
        createTable();
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create info panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Payment status filter
        toolbar.add(new JLabel("Payment Status:"));
        String[] statuses = {"All", "Paid", "Unpaid"};
        paymentStatusFilter = new JComboBox<>(statuses);
        paymentStatusFilter.addActionListener(e -> filterByPaymentStatus());
        toolbar.add(paymentStatusFilter);
        
        // Overdue filter
        overdueOnlyFilter = new JCheckBox("Overdue Only");
        overdueOnlyFilter.addActionListener(e -> filterOverdue());
        toolbar.add(overdueOnlyFilter);
        
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        
        // CRUD buttons
        addButton = new JButton("Add Invoice");
        addButton.addActionListener(e -> addInvoice());
        toolbar.add(addButton);
        
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> editInvoice());
        editButton.setEnabled(false);
        toolbar.add(editButton);
        
        markPaidButton = new JButton("Mark Paid");
        markPaidButton.addActionListener(e -> markAsPaid());
        markPaidButton.setEnabled(false);
        toolbar.add(markPaidButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteInvoice());
        deleteButton.setEnabled(false);
        toolbar.add(deleteButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columnNames = {"Invoice ID", "Job ID", "Amount", "Date Issued", "Due Date", "Payment Status", "Payment Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        invoiceTable = new JTable(tableModel);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = invoiceTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                
                // Enable "Mark Paid" only for unpaid invoices
                if (hasSelection) {
                    int selectedRow = invoiceTable.getSelectedRow();
                    String paymentStatus = (String) tableModel.getValueAt(selectedRow, 5);
                    markPaidButton.setEnabled("Unpaid".equals(paymentStatus));
                } else {
                    markPaidButton.setEnabled(false);
                }
            }
        });
        
        // Set column widths
        invoiceTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Invoice ID
        invoiceTable.getColumnModel().getColumn(1).setPreferredWidth(70);  // Job ID
        invoiceTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
        invoiceTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date Issued
        invoiceTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Due Date
        invoiceTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Payment Status
        invoiceTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Payment Date
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel infoLabel = new JLabel("Select an invoice to edit, delete, or mark as paid. Use filters to find specific invoices.");
        infoPanel.add(infoLabel);
        
        return infoPanel;
    }
    
    private void loadInvoiceData() {
        try {
            List<Invoice> invoices = invoiceDAO.findAll();
            populateTable(invoices);
            logger.info("Loaded " + invoices.size() + " invoices into table");
        } catch (Exception e) {
            logger.severe("Error loading invoice data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error loading invoice data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateTable(List<Invoice> invoices) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Invoice invoice : invoices) {
            Object[] row = {
                invoice.getInvoiceId(),
                invoice.getJobId(),
                String.format("$%.2f", invoice.getAmount()),
                invoice.getDateIssued(),
                invoice.getDueDate(),
                invoice.isPaid() ? "Paid" : "Unpaid",
                invoice.getPaymentDate() != null ? invoice.getPaymentDate().toString() : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterByPaymentStatus() {
        String selectedStatus = (String) paymentStatusFilter.getSelectedItem();
        
        if ("All".equals(selectedStatus)) {
            if (!overdueOnlyFilter.isSelected()) {
                loadInvoiceData();
            } else {
                filterOverdue();
            }
            return;
        }
        
        try {
            boolean isPaid = "Paid".equals(selectedStatus);
            List<Invoice> results = invoiceDAO.findByPaymentStatus(isPaid);
            
            // If overdue filter is also active, further filter the results
            if (overdueOnlyFilter.isSelected() && !isPaid) {
                results = results.stream()
                    .filter(invoice -> invoice.getDueDate().isBefore(LocalDate.now()))
                    .toList();
            }
            
            populateTable(results);
            logger.info("Filter by payment status '" + selectedStatus + "' returned " + results.size() + " results");
        } catch (Exception e) {
            logger.severe("Error filtering invoices by payment status: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error filtering invoices: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterOverdue() {
        if (!overdueOnlyFilter.isSelected()) {
            // If unchecked, just apply payment status filter
            filterByPaymentStatus();
            return;
        }
        
        try {
            List<Invoice> results = invoiceDAO.findOverdue();
            populateTable(results);
            logger.info("Overdue filter returned " + results.size() + " results");
        } catch (Exception e) {
            logger.severe("Error filtering overdue invoices: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error filtering overdue invoices: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addInvoice() {
        // TODO: Implement add invoice dialog
        JOptionPane.showMessageDialog(this,
            "Add Invoice dialog will be implemented in the next phase.",
            "Feature Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void editInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // TODO: Implement edit invoice dialog
        Long invoiceId = (Long) tableModel.getValueAt(selectedRow, 0);
        JOptionPane.showMessageDialog(this,
            "Edit Invoice dialog for ID " + invoiceId + " will be implemented in the next phase.",
            "Feature Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void markAsPaid() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long invoiceId = (Long) tableModel.getValueAt(selectedRow, 0);
        String amount = (String) tableModel.getValueAt(selectedRow, 2);
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Mark invoice " + invoiceId + " (" + amount + ") as paid?",
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                // Get the invoice and update it
                Invoice invoice = invoiceDAO.findById(invoiceId);
                if (invoice != null) {
                    invoice.setPaid(true);
                    invoice.setPaymentDate(LocalDate.now());
                    invoiceDAO.update(invoice);
                    refreshData();
                    JOptionPane.showMessageDialog(this,
                        "Invoice marked as paid successfully.",
                        "Payment Recorded",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                logger.severe("Error marking invoice as paid: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error updating invoice: " + e.getMessage(),
                    "Update Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long invoiceId = (Long) tableModel.getValueAt(selectedRow, 0);
        String amount = (String) tableModel.getValueAt(selectedRow, 2);
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete invoice " + invoiceId + " (" + amount + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                invoiceDAO.delete(invoiceId);
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Invoice deleted successfully.",
                    "Delete Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                logger.severe("Error deleting invoice: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error deleting invoice: " + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadInvoiceData();
        paymentStatusFilter.setSelectedIndex(0); // Reset to "All"
        overdueOnlyFilter.setSelected(false); // Clear overdue filter
    }
}