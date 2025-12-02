package org.bmc.app.ui;

import org.bmc.app.dao.MaterialDAO;
import org.bmc.app.dao.POItemDAO;
import org.bmc.app.dao.VendorDAO;
import org.bmc.app.model.Material;
import org.bmc.app.model.POItem;
import org.bmc.app.model.PurchaseOrder;
import org.bmc.app.model.Vendor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for adding/editing purchase orders
 * @author BMC Systems Team
 */
public class PurchaseOrderDialog extends JDialog {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private JComboBox<Vendor> vendorComboBox;
    private JTextField orderDateField;
    private JComboBox<String> statusComboBox;
    private JTextField totalCostField;
    
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private List<POItem> items;
    
    private boolean confirmed = false;
    private PurchaseOrder purchaseOrder;
    private VendorDAO vendorDAO;
    private MaterialDAO materialDAO;
    private POItemDAO poItemDAO;

    public PurchaseOrderDialog(Frame parent, PurchaseOrder purchaseOrder) {
        super(parent, purchaseOrder == null ? "Add Purchase Order" : "Edit Purchase Order", true);
        this.purchaseOrder = purchaseOrder;
        this.vendorDAO = new VendorDAO();
        this.materialDAO = new MaterialDAO();
        this.poItemDAO = new POItemDAO();
        this.items = new ArrayList<>();
        
        initializeComponents();
        if (purchaseOrder != null) {
            populateFields();
            loadItems();
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(700, 500);
        
        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Vendor combo box
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Vendor: *"), gbc);
        gbc.gridx = 1;
        vendorComboBox = new JComboBox<>();
        List<Vendor> vendors = vendorDAO.findAll();
        for (Vendor vendor : vendors) {
            vendorComboBox.addItem(vendor);
        }
        formPanel.add(vendorComboBox, gbc);

        // Order Date field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Order Date (yyyy-mm-dd): *"), gbc);
        gbc.gridx = 1;
        orderDateField = new JTextField(20);
        orderDateField.setText(LocalDate.now().format(DATE_FORMATTER));
        formPanel.add(orderDateField, gbc);

        // Status combo box
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(new String[]{"Pending", "Received", "Cancelled"});
        formPanel.add(statusComboBox, gbc);

        // Total Cost field (read-only, calculated)
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Total Cost:"), gbc);
        gbc.gridx = 1;
        totalCostField = new JTextField(20);
        totalCostField.setEditable(false);
        totalCostField.setText("$0.00");
        formPanel.add(totalCostField, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Items table panel
        JPanel itemsPanel = new JPanel(new BorderLayout(5, 5));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Line Items"));
        
        String[] columns = {"Material", "Quantity", "Unit Price", "Line Total"};
        itemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setAutoCreateRowSorter(true);
        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
        itemsScrollPane.setPreferredSize(new Dimension(650, 200));
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        
        // Items buttons
        JPanel itemsButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add Item");
        JButton removeItemButton = new JButton("Remove Item");
        
        addItemButton.addActionListener(e -> addItem());
        removeItemButton.addActionListener(e -> removeItem());
        
        itemsButtonPanel.add(addItemButton);
        itemsButtonPanel.add(removeItemButton);
        itemsPanel.add(itemsButtonPanel, BorderLayout.SOUTH);
        
        add(itemsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (validateInput()) {
                confirmed = true;
                updatePurchaseOrder();
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
        if (purchaseOrder != null) {
            // Set vendor selection
            if (purchaseOrder.getVendorId() != null) {
                for (int i = 0; i < vendorComboBox.getItemCount(); i++) {
                    Vendor vendor = vendorComboBox.getItemAt(i);
                    if (vendor != null && vendor.getVendorId().equals(purchaseOrder.getVendorId())) {
                        vendorComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            if (purchaseOrder.getOrderDate() != null) {
                orderDateField.setText(purchaseOrder.getOrderDate().format(DATE_FORMATTER));
            }
            
            if (purchaseOrder.getStatus() != null) {
                statusComboBox.setSelectedItem(purchaseOrder.getStatus());
            }
            
            if (purchaseOrder.getTotalCost() != null) {
                totalCostField.setText(String.format("$%.2f", purchaseOrder.getTotalCost()));
            }
        }
    }

    private void loadItems() {
        if (purchaseOrder != null && purchaseOrder.getPoId() != null) {
            items = poItemDAO.findByPurchaseOrder(purchaseOrder.getPoId());
            refreshItemsTable();
        }
    }

    private void addItem() {
        // Create dialog for adding item
        JDialog itemDialog = new JDialog(this, "Add Line Item", true);
        itemDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Material combo box
        gbc.gridx = 0; gbc.gridy = 0;
        itemDialog.add(new JLabel("Material:"), gbc);
        gbc.gridx = 1;
        JComboBox<Material> materialComboBox = new JComboBox<>();
        List<Material> materials = materialDAO.findAll();
        for (Material material : materials) {
            materialComboBox.addItem(material);
        }
        materialComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                         boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Material) {
                    setText(((Material) value).getName());
                }
                return this;
            }
        });
        itemDialog.add(materialComboBox, gbc);
        
        // Quantity spinner
        gbc.gridx = 0; gbc.gridy = 1;
        itemDialog.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        itemDialog.add(quantitySpinner, gbc);
        
        // Unit Price field
        gbc.gridx = 0; gbc.gridy = 2;
        itemDialog.add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 1;
        JTextField unitPriceField = new JTextField("0.00");
        itemDialog.add(unitPriceField, gbc);
        
        // Auto-populate unit price when material is selected
        materialComboBox.addActionListener(e -> {
            Material selectedMaterial = (Material) materialComboBox.getSelectedItem();
            if (selectedMaterial != null && selectedMaterial.getUnitCost() != null) {
                unitPriceField.setText(selectedMaterial.getUnitCost().toString());
            }
        });
        
        // Trigger initial population if a material is already selected
        if (materialComboBox.getSelectedItem() != null) {
            Material initialMaterial = (Material) materialComboBox.getSelectedItem();
            if (initialMaterial.getUnitCost() != null) {
                unitPriceField.setText(initialMaterial.getUnitCost().toString());
            }
        }
        
        // Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            try {
                Material selectedMaterial = (Material) materialComboBox.getSelectedItem();
                if (selectedMaterial == null) {
                    JOptionPane.showMessageDialog(itemDialog, "Please select a material.");
                    return;
                }
                
                POItem item = new POItem();
                item.setMaterialId(selectedMaterial.getMaterialId());
                item.setMaterialName(selectedMaterial.getName());
                item.setQuantity((Integer) quantitySpinner.getValue());
                item.setUnitPrice(new BigDecimal(unitPriceField.getText().trim()));
                
                items.add(item);
                refreshItemsTable();
                calculateTotal();
                itemDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(itemDialog, "Invalid unit price format.");
            }
        });
        
        cancelButton.addActionListener(e -> itemDialog.dispose());
        
        btnPanel.add(okButton);
        btnPanel.add(cancelButton);
        itemDialog.add(btnPanel, gbc);
        
        itemDialog.pack();
        itemDialog.setLocationRelativeTo(this);
        itemDialog.setVisible(true);
    }

    private void removeItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            items.remove(selectedRow);
            refreshItemsTable();
            calculateTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
        }
    }

    private void refreshItemsTable() {
        itemsTableModel.setRowCount(0);
        for (POItem item : items) {
            Object[] row = {
                item.getMaterialName(),
                item.getQuantity(),
                String.format("$%.2f", item.getUnitPrice()),
                String.format("$%.2f", item.getLineTotal())
            };
            itemsTableModel.addRow(row);
        }
    }

    private void calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (POItem item : items) {
            total = total.add(item.getLineTotal());
        }
        totalCostField.setText(String.format("$%.2f", total));
    }

    private boolean validateInput() {
        if (vendorComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a vendor.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            LocalDate.parse(orderDateField.getText().trim(), DATE_FORMATTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use yyyy-mm-dd.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            orderDateField.requestFocus();
            return false;
        }

        return true;
    }

    private void updatePurchaseOrder() {
        if (purchaseOrder == null) {
            purchaseOrder = new PurchaseOrder();
        }
        
        Vendor selectedVendor = (Vendor) vendorComboBox.getSelectedItem();
        purchaseOrder.setVendorId(selectedVendor.getVendorId());
        purchaseOrder.setVendorName(selectedVendor.getName());
        purchaseOrder.setOrderDate(LocalDate.parse(orderDateField.getText().trim(), DATE_FORMATTER));
        purchaseOrder.setStatus((String) statusComboBox.getSelectedItem());
        
        // Calculate total from items
        BigDecimal total = BigDecimal.ZERO;
        for (POItem item : items) {
            total = total.add(item.getLineTotal());
        }
        purchaseOrder.setTotalCost(total);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public List<POItem> getItems() {
        return items;
    }
}
