package org.bmc.app.ui;

import org.bmc.app.dao.MaterialDAO;
import org.bmc.app.model.Material;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for assigning materials to a job or updating material quantities.
 */
public class JobMaterialDialog extends JDialog {
    private JComboBox<MaterialItem> materialComboBox;
    private JSpinner quantitySpinner;
    private JButton saveButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;
    private int selectedMaterialId = -1;
    private int quantity = 1;
    
    private MaterialDAO materialDAO;
    
    /**
     * Wrapper class for Material to display in combo box
     */
    private static class MaterialItem {
        private Material material;
        
        public MaterialItem(Material material) {
            this.material = material;
        }
        
        public Material getMaterial() {
            return material;
        }
        
        @Override
        public String toString() {
            return material.getName() + " (Stock: " + material.getStockQuantity() + ")";
        }
    }
    
    public JobMaterialDialog(Frame parent, String title, Integer existingMaterialId, Integer existingQuantity) {
        super(parent, title, true);
        this.materialDAO = new MaterialDAO();
        
        initializeComponents(existingMaterialId, existingQuantity);
        layoutComponents();
        setupListeners();
        
        setSize(450, 200);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents(Integer existingMaterialId, Integer existingQuantity) {
        // Load materials
        List<Material> materials = materialDAO.findAll();
        materialComboBox = new JComboBox<>();
        
        for (Material material : materials) {
            MaterialItem item = new MaterialItem(material);
            materialComboBox.addItem(item);
            
            // Pre-select if editing
            if (existingMaterialId != null && material.getMaterialId() == existingMaterialId) {
                materialComboBox.setSelectedItem(item);
            }
        }
        
        // Disable material selection if editing
        if (existingMaterialId != null) {
            materialComboBox.setEnabled(false);
        }
        
        // Quantity spinner
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
            existingQuantity != null ? existingQuantity : 1, // initial value
            1,          // min
            9999,       // max
            1           // step
        );
        quantitySpinner = new JSpinner(spinnerModel);
        
        // Buttons
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Material label and combo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Material:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(materialComboBox, gbc);
        
        // Quantity label and spinner
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Quantity Used:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(quantitySpinner, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        saveButton.addActionListener(e -> onSave());
        cancelButton.addActionListener(e -> onCancel());
        
        // Enter key saves
        getRootPane().setDefaultButton(saveButton);
    }
    
    private void onSave() {
        // Validate
        MaterialItem selectedItem = (MaterialItem) materialComboBox.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a material", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        selectedMaterialId = selectedItem.getMaterial().getMaterialId();
        quantity = (Integer) quantitySpinner.getValue();
        
        confirmed = true;
        dispose();
    }
    
    private void onCancel() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public int getSelectedMaterialId() {
        return selectedMaterialId;
    }
    
    public int getQuantity() {
        return quantity;
    }
}
