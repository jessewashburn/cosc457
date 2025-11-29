package org.bmc.app.ui;

import org.bmc.app.model.Material;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Dialog for adding/editing materials
 * @author BMC Systems Team
 */
public class MaterialDialog extends JDialog {
    private JTextField nameField;
    private JTextField categoryField;
    private JSpinner stockQuantitySpinner;
    private JSpinner reorderLevelSpinner;
    private JTextField unitCostField;
    
    private boolean confirmed = false;
    private Material material;

    public MaterialDialog(Frame parent, Material material) {
        super(parent, material == null ? "Add Material" : "Edit Material", true);
        this.material = material;
        initializeComponents();
        if (material != null) {
            populateFields();
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field (required)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name: *"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Category field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryField = new JTextField(20);
        formPanel.add(categoryField, gbc);

        // Stock Quantity spinner
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Stock Quantity:"), gbc);
        gbc.gridx = 1;
        stockQuantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        formPanel.add(stockQuantitySpinner, gbc);

        // Reorder Level spinner
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Reorder Level:"), gbc);
        gbc.gridx = 1;
        reorderLevelSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 9999, 1));
        formPanel.add(reorderLevelSpinner, gbc);

        // Unit Cost field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Unit Cost:"), gbc);
        gbc.gridx = 1;
        unitCostField = new JTextField(20);
        unitCostField.setText("0.00");
        formPanel.add(unitCostField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                updateMaterial();
                dispose();
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(saveButton);
    }

    private void populateFields() {
        if (material != null) {
            nameField.setText(material.getName());
            categoryField.setText(material.getCategory());
            if (material.getStockQuantity() != null) {
                stockQuantitySpinner.setValue(material.getStockQuantity());
            }
            if (material.getReorderLevel() != null) {
                reorderLevelSpinner.setValue(material.getReorderLevel());
            }
            if (material.getUnitCost() != null) {
                unitCostField.setText(material.getUnitCost().toString());
            }
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Material name is required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        try {
            new BigDecimal(unitCostField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Unit cost must be a valid number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            unitCostField.requestFocus();
            return false;
        }

        return true;
    }

    private void updateMaterial() {
        if (material == null) {
            material = new Material();
        }
        material.setName(nameField.getText().trim());
        material.setCategory(categoryField.getText().trim());
        material.setStockQuantity((Integer) stockQuantitySpinner.getValue());
        material.setReorderLevel((Integer) reorderLevelSpinner.getValue());
        material.setUnitCost(new BigDecimal(unitCostField.getText().trim()));
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Material getMaterial() {
        return material;
    }
}
