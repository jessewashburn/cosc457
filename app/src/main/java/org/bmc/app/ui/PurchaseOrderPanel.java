package org.bmc.app.ui;

import org.bmc.app.dao.POItemDAO;
import org.bmc.app.dao.PurchaseOrderDAO;
import org.bmc.app.model.POItem;
import org.bmc.app.model.PurchaseOrder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Panel for managing purchase orders
 * @author BMC Systems Team
 */
public class PurchaseOrderPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(PurchaseOrderPanel.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final PurchaseOrderDAO purchaseOrderDAO;
    private final POItemDAO poItemDAO;
    private JTable purchaseOrderTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public PurchaseOrderPanel() {
        this.purchaseOrderDAO = new PurchaseOrderDAO();
        this.poItemDAO = new POItemDAO();
        initializePanel();
        loadPurchaseOrderData();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addButton = new JButton("Add Purchase Order");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton viewItemsButton = new JButton("View Items");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addPurchaseOrder());
        editButton.addActionListener(e -> editPurchaseOrder());
        deleteButton.addActionListener(e -> deletePurchaseOrder());
        viewItemsButton.addActionListener(e -> viewItems());
        refreshButton.addActionListener(e -> loadPurchaseOrderData());

        toolbarPanel.add(addButton);
        toolbarPanel.add(editButton);
        toolbarPanel.add(deleteButton);
        toolbarPanel.add(viewItemsButton);
        toolbarPanel.add(refreshButton);

        add(toolbarPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        searchButton.addActionListener(e -> searchPurchaseOrders());
        searchField.addActionListener(e -> searchPurchaseOrders());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        toolbarPanel.add(searchPanel);

        // Table
        String[] columns = {"PO #", "Vendor", "Order Date", "Total Cost", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        purchaseOrderTable = new JTable(tableModel);
        purchaseOrderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        purchaseOrderTable.setAutoCreateRowSorter(true);
        sorter = new TableRowSorter<>(tableModel);
        purchaseOrderTable.setRowSorter(sorter);

        // Double-click to edit
        purchaseOrderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editPurchaseOrder();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(purchaseOrderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Purchase orders loaded");
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void loadPurchaseOrderData() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderDAO.findAll();
        updateTable(purchaseOrders);
        LOGGER.info("Loaded " + purchaseOrders.size() + " purchase orders into table");
    }

    private void updateTable(List<PurchaseOrder> purchaseOrders) {
        tableModel.setRowCount(0);
        for (PurchaseOrder po : purchaseOrders) {
            Object[] row = {
                po.getPoId(),
                po.getVendorName(),
                po.getOrderDate() != null ? po.getOrderDate().format(DATE_FORMATTER) : "",
                String.format("$%.2f", po.getTotalCost()),
                po.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addPurchaseOrder() {
        PurchaseOrderDialog dialog = new PurchaseOrderDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            PurchaseOrder po = dialog.getPurchaseOrder();
            if (purchaseOrderDAO.save(po)) {
                // Save line items
                List<POItem> items = dialog.getItems();
                for (POItem item : items) {
                    item.setPoId(po.getPoId());
                    poItemDAO.save(item);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Purchase order added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPurchaseOrderData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to add purchase order.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editPurchaseOrder() {
        int selectedRow = purchaseOrderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to edit.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = purchaseOrderTable.convertRowIndexToModel(selectedRow);
        Integer poId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        PurchaseOrder po = purchaseOrderDAO.findById(poId);
        if (po == null) {
            JOptionPane.showMessageDialog(this, 
                "Purchase order not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        PurchaseOrderDialog dialog = new PurchaseOrderDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), po);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            PurchaseOrder updatedPO = dialog.getPurchaseOrder();
            if (purchaseOrderDAO.update(updatedPO)) {
                // Delete existing items and save new ones
                poItemDAO.deleteByPurchaseOrder(updatedPO.getPoId());
                List<POItem> items = dialog.getItems();
                for (POItem item : items) {
                    item.setPoId(updatedPO.getPoId());
                    poItemDAO.save(item);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Purchase order updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPurchaseOrderData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update purchase order.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePurchaseOrder() {
        int selectedRow = purchaseOrderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to delete.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = purchaseOrderTable.convertRowIndexToModel(selectedRow);
        Integer poId = (Integer) tableModel.getValueAt(modelRow, 0);
        String vendor = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Purchase Order #" + poId + " (" + vendor + ")?\n" +
            "This will also delete all line items.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            poItemDAO.deleteByPurchaseOrder(poId);
            if (purchaseOrderDAO.delete(poId)) {
                JOptionPane.showMessageDialog(this, 
                    "Purchase order deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPurchaseOrderData();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete purchase order.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewItems() {
        int selectedRow = purchaseOrderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to view items.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = purchaseOrderTable.convertRowIndexToModel(selectedRow);
        Integer poId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        List<POItem> items = poItemDAO.findByPurchaseOrder(poId);
        
        // Create dialog to display items
        JDialog itemsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Line Items for PO #" + poId, true);
        itemsDialog.setLayout(new BorderLayout(10, 10));
        
        String[] columns = {"Material", "Quantity", "Unit Price", "Line Total"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0);
        
        for (POItem item : items) {
            Object[] row = {
                item.getMaterialName(),
                item.getQuantity(),
                String.format("$%.2f", item.getUnitPrice()),
                String.format("$%.2f", item.getLineTotal())
            };
            itemsModel.addRow(row);
        }
        
        JTable itemsTable = new JTable(itemsModel);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        itemsDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> itemsDialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        itemsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        itemsDialog.pack();
        itemsDialog.setLocationRelativeTo(this);
        itemsDialog.setVisible(true);
    }

    private void searchPurchaseOrders() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPurchaseOrderData();
        } else {
            List<PurchaseOrder> results = purchaseOrderDAO.search(keyword);
            updateTable(results);
            LOGGER.info("Search found " + results.size() + " purchase orders");
        }
    }

    public void refreshData() {
        loadPurchaseOrderData();
    }
}
