package org.bmc.app.ui;

import org.bmc.app.dao.MaterialDAO;
import org.bmc.app.model.Material;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel for managing materials and inventory
 * @author BMC Systems Team
 */
public class MaterialPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(MaterialPanel.class.getName());
    
    private final MaterialDAO materialDAO;
    private JTable materialTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public MaterialPanel() {
        this.materialDAO = new MaterialDAO();
        initializePanel();
        loadMaterialData();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Material");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton updateStockButton = new JButton("Update Stock");
        JButton reorderAlertButton = new JButton("Reorder Alerts");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addMaterial());
        editButton.addActionListener(e -> editMaterial());
        deleteButton.addActionListener(e -> deleteMaterial());
        updateStockButton.addActionListener(e -> updateStock());
        reorderAlertButton.addActionListener(e -> showReorderAlerts());
        refreshButton.addActionListener(e -> loadMaterialData());

        toolbarPanel.add(addButton);
        toolbarPanel.add(editButton);
        toolbarPanel.add(deleteButton);
        toolbarPanel.add(updateStockButton);
        toolbarPanel.add(reorderAlertButton);
        toolbarPanel.add(refreshButton);

        add(toolbarPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        searchButton.addActionListener(e -> searchMaterials());
        searchField.addActionListener(e -> searchMaterials());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        toolbarPanel.add(searchPanel);

        // Table
        String[] columns = {"ID", "Name", "Category", "Vendor", "Stock Qty", "Reorder Level", "Unit Cost"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        materialTable = new JTable(tableModel);
        materialTable.setAutoCreateRowSorter(true);
        materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialTable.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(tableModel);
        materialTable.setRowSorter(sorter);
        
        // Custom renderer to highlight low stock items in red
        materialTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                          boolean isSelected, boolean hasFocus,
                                                          int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Get stock quantity and reorder level from the table
                    int modelRow = table.convertRowIndexToModel(row);
                    Integer stockQty = (Integer) table.getModel().getValueAt(modelRow, 4);
                    Integer reorderLevel = (Integer) table.getModel().getValueAt(modelRow, 5);
                    
                    if (stockQty != null && reorderLevel != null && stockQty <= reorderLevel) {
                        c.setBackground(new Color(255, 200, 200)); // Light red
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                }
                
                return c;
            }
        });

        // Double-click to edit
        materialTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editMaterial();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(materialTable);
        add(scrollPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Materials loaded");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void loadMaterialData() {
        List<Material> materials = materialDAO.findAll();
        updateTable(materials);
        LOGGER.info("Loaded " + materials.size() + " materials into table");
    }

    private void updateTable(List<Material> materials) {
        tableModel.setRowCount(0);
        for (Material material : materials) {
            Object[] row = {
                material.getMaterialId(),
                material.getName(),
                material.getCategory(),
                material.getVendorName() != null ? material.getVendorName() : "",
                material.getStockQuantity(),
                material.getReorderLevel(),
                String.format("$%.2f", material.getUnitCost())
            };
            tableModel.addRow(row);
        }
    }

    private void addMaterial() {
        MaterialDialog dialog = new MaterialDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Material material = dialog.getMaterial();
            if (materialDAO.save(material)) {
                JOptionPane.showMessageDialog(this, 
                    "Material added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadMaterialData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add material.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editMaterial() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a material to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = materialTable.convertRowIndexToModel(selectedRow);
        Integer materialId = (Integer) tableModel.getValueAt(modelRow, 0);
        Material material = materialDAO.findById(materialId);

        if (material != null) {
            MaterialDialog dialog = new MaterialDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), material);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                if (materialDAO.update(material)) {
                    JOptionPane.showMessageDialog(this, 
                        "Material updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadMaterialData();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update material.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteMaterial() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a material to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = materialTable.convertRowIndexToModel(selectedRow);
        Integer materialId = (Integer) tableModel.getValueAt(modelRow, 0);
        String materialName = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete material: " + materialName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (materialDAO.delete(materialId)) {
                JOptionPane.showMessageDialog(this, 
                    "Material deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadMaterialData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete material.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStock() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a material to update stock.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = materialTable.convertRowIndexToModel(selectedRow);
        Integer materialId = (Integer) tableModel.getValueAt(modelRow, 0);
        Material material = materialDAO.findById(materialId);

        if (material != null) {
            String input = JOptionPane.showInputDialog(this,
                    "Enter new stock quantity for " + material.getName() + ":",
                    material.getStockQuantity());

            if (input != null) {
                try {
                    int newQuantity = Integer.parseInt(input.trim());
                    if (newQuantity < 0) {
                        JOptionPane.showMessageDialog(this,
                                "Stock quantity cannot be negative.",
                                "Invalid Input",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    material.setStockQuantity(newQuantity);
                    if (materialDAO.update(material)) {
                        JOptionPane.showMessageDialog(this,
                                "Stock updated successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadMaterialData();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to update stock.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a valid number.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showReorderAlerts() {
        List<Material> lowStockMaterials = materialDAO.findLowStock();
        
        if (lowStockMaterials.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All materials are adequately stocked!",
                    "Reorder Alerts",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Materials needing reorder:\n\n");
        for (Material material : lowStockMaterials) {
            message.append(String.format("%s: %d in stock (reorder at %d)\n",
                    material.getName(),
                    material.getStockQuantity(),
                    material.getReorderLevel()));
        }

        JOptionPane.showMessageDialog(this,
                message.toString(),
                "Reorder Alerts (" + lowStockMaterials.size() + " materials)",
                JOptionPane.WARNING_MESSAGE);
    }

    private void searchMaterials() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadMaterialData();
        } else {
            List<Material> results = materialDAO.search(keyword);
            updateTable(results);
            LOGGER.info("Search returned " + results.size() + " materials");
        }
    }
}
