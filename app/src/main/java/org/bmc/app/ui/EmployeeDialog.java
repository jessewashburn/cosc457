package org.bmc.app.ui;

import org.bmc.app.dao.EmployeeDAO;
import org.bmc.app.model.Employee;
import javax.swing.*;
import java.awt.*;

/**
 * Modal dialog for creating new employees or editing existing employee records.
 * Provides form validation and database persistence through EmployeeDAO.
 * 
 * @author Baltimore Metal Crafters Team
 */
public class EmployeeDialog extends JDialog {
    
    private static final int TEXT_FIELD_COLS = 30;
    
    private final EmployeeDAO employeeDAO;
    private final Employee employee;
    private boolean saved;
    
    private JTextField nameField;
    private JComboBox<Employee.Role> roleCombo;
    private JTextField specializationField;
    private JTextField contactInfoField;
    
    public EmployeeDialog(Frame parent, Employee employee) {
        super(parent, employee == null ? "Add Employee" : "Edit Employee", true);
        this.employeeDAO = new EmployeeDAO();
        this.employee = employee;
        this.saved = false;
        
        initializeComponents();
        layoutComponents();
        
        if (employee != null) {
            populateFields();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        nameField = new JTextField(TEXT_FIELD_COLS);
        roleCombo = new JComboBox<>(Employee.Role.values());
        specializationField = new JTextField(TEXT_FIELD_COLS);
        contactInfoField = new JTextField(TEXT_FIELD_COLS);
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
        addFormRow(panel, gbc, 1, "Role:", roleCombo);
        addFormRow(panel, gbc, 2, "Specialization:", specializationField);
        addFormRow(panel, gbc, 3, "Contact Info:", contactInfoField);
        
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
        nameField.setText(employee.getName());
        roleCombo.setSelectedItem(employee.getRole());
        specializationField.setText(employee.getSpecialization());
        contactInfoField.setText(employee.getContactInfo());
    }
    
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (employee == null) {
                createNewEmployee();
            } else {
                updateExistingEmployee();
            }
            saved = true;
            dispose();
        } catch (Exception e) {
            showErrorDialog("Error saving employee: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showErrorDialog("Name is required.");
            nameField.requestFocus();
            return false;
        }
        
        if (roleCombo.getSelectedItem() == null) {
            showErrorDialog("Role is required.");
            roleCombo.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void createNewEmployee() throws Exception {
        Employee newEmployee = new Employee(
            nameField.getText().trim(),
            (Employee.Role) roleCombo.getSelectedItem(),
            specializationField.getText().trim(),
            contactInfoField.getText().trim()
        );
        employeeDAO.create(newEmployee);
    }
    
    private void updateExistingEmployee() throws Exception {
        employee.setName(nameField.getText().trim());
        employee.setRole((Employee.Role) roleCombo.getSelectedItem());
        employee.setSpecialization(specializationField.getText().trim());
        employee.setContactInfo(contactInfoField.getText().trim());
        employeeDAO.update(employee);
    }
    
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
