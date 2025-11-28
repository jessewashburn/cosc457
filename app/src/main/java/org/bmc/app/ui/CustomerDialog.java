package org.bmc.app.ui;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.model.Customer;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing customer records.
 */
public class CustomerDialog extends JDialog {
    private CustomerDAO customerDAO = new CustomerDAO();
    private Customer customer;
    private boolean saved = false;
    
    // Form fields
    private JTextField nameField = new JTextField(30);
    private JTextField phoneField = new JTextField(20);
    private JTextField emailField = new JTextField(30);
    private JTextArea addressArea = new JTextArea(3, 30);
    
    public CustomerDialog(Frame parent, Customer customer) {
        super(parent, customer == null ? "Add Customer" : "Edit Customer", true);
        this.customer = customer;
        
        setupUI();
        if (customer != null) {
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
        
        // Name field
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(nameField, gbc);
        
        // Phone field
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(phoneField, gbc);
        
        // Email field
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);
        
        // Address field
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        formPanel.add(addressScroll, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveCustomer());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void fillFields() {
        nameField.setText(customer.getName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
        addressArea.setText(customer.getAddress());
    }
    
    private void saveCustomer() {
        try {
            // Validate
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (customer == null) {
                // New customer
                Customer newCustomer = new Customer(
                    nameField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    addressArea.getText().trim()
                );
                customerDAO.create(newCustomer);
            } else {
                // Update existing
                customer.setName(nameField.getText().trim());
                customer.setPhone(phoneField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                customer.setAddress(addressArea.getText().trim());
                customerDAO.update(customer);
            }
            
            saved = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving customer: " + e.getMessage(), 
                "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
