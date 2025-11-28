package org.bmc.app.ui;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.model.Customer;
import javax.swing.*;
import java.awt.*;

/**
 * Modal dialog for creating new customers or editing existing customer records.
 * Provides form validation and database persistence through CustomerDAO.
 * 
 * @author Baltimore Metal Crafters Team
 */
public class CustomerDialog extends JDialog {
    
    private static final int NAME_FIELD_COLS = 30;
    private static final int PHONE_FIELD_COLS = 20;
    private static final int EMAIL_FIELD_COLS = 30;
    private static final int ADDRESS_ROWS = 3;
    private static final int ADDRESS_COLS = 30;
    
    private final CustomerDAO customerDAO;
    private final Customer customer;
    private boolean saved;
    
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;
    
    public CustomerDialog(Frame parent, Customer customer) {
        super(parent, customer == null ? "Add Customer" : "Edit Customer", true);
        this.customerDAO = new CustomerDAO();
        this.customer = customer;
        this.saved = false;
        
        initializeComponents();
        layoutComponents();
        
        if (customer != null) {
            populateFields();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        nameField = new JTextField(NAME_FIELD_COLS);
        phoneField = new JTextField(PHONE_FIELD_COLS);
        emailField = new JTextField(EMAIL_FIELD_COLS);
        addressArea = new JTextArea(ADDRESS_ROWS, ADDRESS_COLS);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
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
        
        addFormRow(panel, gbc, 0, "Name:", nameField);
        addFormRow(panel, gbc, 1, "Phone:", phoneField);
        addFormRow(panel, gbc, 2, "Email:", emailField);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JScrollPane(addressArea), gbc);
        
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
    
    private void populateFields() {
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
        addressArea.setText(customer.getAddress());
    }
    
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (customer == null) {
                createNewCustomer();
            } else {
                updateExistingCustomer();
            }
            saved = true;
            dispose();
        } catch (Exception e) {
            showErrorDialog("Error saving customer: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showErrorDialog("Name is required.");
            nameField.requestFocus();
            return false;
        }
        return true;
    }
    
    private void createNewCustomer() throws Exception {
        Customer newCustomer = new Customer(
            nameField.getText().trim(),
            phoneField.getText().trim(),
            emailField.getText().trim(),
            addressArea.getText().trim()
        );
        customerDAO.create(newCustomer);
    }
    
    private void updateExistingCustomer() throws Exception {
        customer.setName(nameField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setAddress(addressArea.getText().trim());
        customerDAO.update(customer);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
