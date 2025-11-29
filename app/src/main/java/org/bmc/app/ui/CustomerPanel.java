package org.bmc.app.ui;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel for managing customer data with table view and CRUD operations.
 */
public class CustomerPanel extends JPanel {
    private static final Logger logger = Logger.getLogger(CustomerPanel.class.getName());
    
    private CustomerDAO customerDAO;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    public CustomerPanel() {
        this.customerDAO = new CustomerDAO();
        initializePanel();
        loadCustomerData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Customer Management"));
        
        // Create toolbar
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Create table
        createTable();
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create info panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Search functionality
        toolbar.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> performSearch());
        toolbar.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        toolbar.add(searchButton);
        
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        
        // CRUD buttons
        addButton = new JButton("Add Customer");
        addButton.addActionListener(e -> addCustomer());
        toolbar.add(addButton);
        
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> editCustomer());
        editButton.setEnabled(false);
        toolbar.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteCustomer());
        deleteButton.setEnabled(false);
        toolbar.add(deleteButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columnNames = {"ID", "Company Name", "Contact Name", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = customerTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
        
        // Set column widths
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Company
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Contact
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Email
        customerTable.getColumnModel().getColumn(5).setPreferredWidth(250); // Address
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel infoLabel = new JLabel("Select a customer to edit or delete. Use search to find specific customers.");
        infoPanel.add(infoLabel);
        
        return infoPanel;
    }
    
    private void loadCustomerData() {
        try {
            List<Customer> customers = customerDAO.findAll();
            populateTable(customers);
            logger.info("Loaded " + customers.size() + " customers into table");
        } catch (Exception e) {
            logger.severe("Error loading customer data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error loading customer data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateTable(List<Customer> customers) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Customer customer : customers) {
            Object[] row = {
                customer.getCustomerId(),
                customer.getName(),
                customer.getContactName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress()
            };
            tableModel.addRow(row);
        }
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadCustomerData(); // Show all customers
            return;
        }
        
        try {
            List<Customer> results = customerDAO.searchByName(searchTerm);
            populateTable(results);
            logger.info("Search for '" + searchTerm + "' returned " + results.size() + " results");
        } catch (Exception e) {
            logger.severe("Error searching customers: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error searching customers: " + e.getMessage(),
                "Search Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addCustomer() {
        CustomerDialog dialog = new CustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.wasSaved()) {
            refreshData();
        }
    }
    
    private void editCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            Customer customer = customerDAO.findById(customerId);
            CustomerDialog dialog = new CustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), customer);
            dialog.setVisible(true);
            if (dialog.wasSaved()) {
                refreshData();
            }
        } catch (Exception e) {
            logger.severe("Error loading customer: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading customer: " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer customerId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String companyName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete customer:\n" + companyName + " (ID: " + customerId + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                customerDAO.delete(customerId);
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Customer deleted successfully.",
                    "Delete Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                logger.severe("Error deleting customer: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error deleting customer: " + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadCustomerData();
        searchField.setText(""); // Clear search field
    }
}