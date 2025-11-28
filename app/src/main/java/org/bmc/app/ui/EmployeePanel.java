package org.bmc.app.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.bmc.app.dao.EmployeeDAO;
import org.bmc.app.model.Employee;

/**
 * Panel for managing employee data with table view and basic operations.
 */
public class EmployeePanel extends JPanel {
    private static final Logger logger = Logger.getLogger(EmployeePanel.class.getName());
    
    private EmployeeDAO employeeDAO;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> roleFilter;
    private JButton addButton, editButton, deleteButton, refreshButton;
    
    public EmployeePanel() {
        this.employeeDAO = new EmployeeDAO();
        initializePanel();
        loadEmployeeData();
    }
    
    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Employee Management"));
        
        // Create toolbar
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
        
        // Create table
        createTable();
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create info panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Role filter
        toolbar.add(new JLabel("Filter by Role:"));
        String[] roles = {"All", "manager", "restorer", "fabricator", "consultant"};
        roleFilter = new JComboBox<>(roles);
        roleFilter.addActionListener(e -> filterByRole());
        toolbar.add(roleFilter);
        
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        
        // CRUD buttons
        addButton = new JButton("Add Employee");
        addButton.addActionListener(e -> addEmployee());
        toolbar.add(addButton);
        
        editButton = new JButton("Edit");
        editButton.addActionListener(e -> editEmployee());
        editButton.setEnabled(false);
        toolbar.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteEmployee());
        deleteButton.setEnabled(false);
        toolbar.add(deleteButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshData());
        toolbar.add(refreshButton);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columnNames = {"ID", "Name", "Role", "Specialization", "Contact Info"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = employeeTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
        
        // Set column widths
        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Name
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Role
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Specialization
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Contact Info
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel infoLabel = new JLabel("Select an employee to edit or delete. Use role filter to narrow results.");
        infoPanel.add(infoLabel);
        
        return infoPanel;
    }
    
    private void loadEmployeeData() {
        try {
            List<Employee> employees = employeeDAO.findAll();
            populateTable(employees);
            logger.info("Loaded " + employees.size() + " employees into table");
        } catch (Exception e) {
            logger.severe("Error loading employee data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error loading employee data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void populateTable(List<Employee> employees) {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Employee employee : employees) {
            Object[] row = {
                employee.getEmployeeId(),
                employee.getName(),
                employee.getRole(),
                employee.getSpecialization(),
                employee.getContactInfo()
            };
            tableModel.addRow(row);
        }
    }
    
    private void filterByRole() {
        String selectedRole = (String) roleFilter.getSelectedItem();
        
        if ("All".equals(selectedRole)) {
            loadEmployeeData();
            return;
        }
        
        try {
            Employee.Role role = Employee.Role.fromString(selectedRole);
            if (role != null) {
                List<Employee> results = employeeDAO.findByRole(role);
                populateTable(results);
                logger.info("Filter by role '" + selectedRole + "' returned " + results.size() + " results");
            } else {
                logger.warning("Invalid role selected: " + selectedRole);
                loadEmployeeData(); // Fallback to showing all
            }
        } catch (Exception e) {
            logger.severe("Error filtering employees: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error filtering employees: " + e.getMessage(),
                "Filter Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addEmployee() {
        EmployeeDialog dialog = new EmployeeDialog((Frame) javax.swing.SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.wasSaved()) {
            refreshData();
        }
    }
    
    private void editEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            Employee employee = employeeDAO.findById(employeeId);
            EmployeeDialog dialog = new EmployeeDialog((Frame) javax.swing.SwingUtilities.getWindowAncestor(this), employee);
            dialog.setVisible(true);
            if (dialog.wasSaved()) {
                refreshData();
            }
        } catch (Exception e) {
            logger.severe("Error loading employee: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading employee: " + e.getMessage(),
                "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        Integer employeeId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee:\n" + name + " (ID: " + employeeId + ")?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (choice == JOptionPane.YES_OPTION) {
            try {
                employeeDAO.delete(employeeId);
                refreshData();
                JOptionPane.showMessageDialog(this,
                    "Employee deleted successfully.",
                    "Delete Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                logger.severe("Error deleting employee: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error deleting employee: " + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void refreshData() {
        loadEmployeeData();
        roleFilter.setSelectedIndex(0); // Reset to "All"
    }
}