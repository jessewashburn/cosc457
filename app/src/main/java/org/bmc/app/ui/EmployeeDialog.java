package org.bmc.app.ui;

import org.bmc.app.dao.EmployeeDAO;
import org.bmc.app.model.Employee;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing employee records.
 */
public class EmployeeDialog extends JDialog {
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private Employee employee;
    private boolean saved = false;
    
    // Form fields
    private JTextField nameField = new JTextField(30);
    private JComboBox<Employee.Role> roleCombo = new JComboBox<>(Employee.Role.values());
    private JTextField specializationField = new JTextField(30);
    private JTextField contactInfoField = new JTextField(30);
    
    public EmployeeDialog(Frame parent, Employee employee) {
        super(parent, employee == null ? "Add Employee" : "Edit Employee", true);
        this.employee = employee;
        
        setupUI();
        if (employee != null) {
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
        
        // Role combo
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(roleCombo, gbc);
        
        // Specialization field
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(specializationField, gbc);
        
        // Contact info field
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Contact Info:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(contactInfoField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void fillFields() {
        nameField.setText(employee.getName());
        roleCombo.setSelectedItem(employee.getRole());
        specializationField.setText(employee.getSpecialization());
        contactInfoField.setText(employee.getContactInfo());
    }
    
    private void saveEmployee() {
        try {
            // Validate
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (roleCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Role is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (employee == null) {
                // New employee
                Employee newEmployee = new Employee(
                    nameField.getText().trim(),
                    (Employee.Role) roleCombo.getSelectedItem(),
                    specializationField.getText().trim(),
                    contactInfoField.getText().trim()
                );
                employeeDAO.create(newEmployee);
            } else {
                // Update existing
                employee.setName(nameField.getText().trim());
                employee.setRole((Employee.Role) roleCombo.getSelectedItem());
                employee.setSpecialization(specializationField.getText().trim());
                employee.setContactInfo(contactInfoField.getText().trim());
                employeeDAO.update(employee);
            }
            
            saved = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving employee: " + e.getMessage(), 
                "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
