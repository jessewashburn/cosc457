package org.bmc.app.dao;

import org.bmc.app.model.PurchaseOrder;
import org.bmc.app.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for PurchaseOrder entity
 * @author BMC Systems Team
 */
public class PurchaseOrderDAO {
    private static final Logger LOGGER = Logger.getLogger(PurchaseOrderDAO.class.getName());

    public PurchaseOrderDAO() {
    }

    public List<PurchaseOrder> findAll() {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = "SELECT po.po_id, po.vendor_id, v.name AS vendor_name, po.order_date, " +
                     "po.total_cost, po.status " +
                     "FROM PurchaseOrder po " +
                     "JOIN Vendor v ON po.vendor_id = v.vendor_id " +
                     "ORDER BY po.order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                purchaseOrders.add(mapResultSetToPurchaseOrder(rs));
            }
            LOGGER.info("Retrieved " + purchaseOrders.size() + " purchase orders");
        } catch (SQLException e) {
            LOGGER.severe("Error finding all purchase orders: " + e.getMessage());
        }
        return purchaseOrders;
    }

    public PurchaseOrder findById(Integer id) {
        String sql = "SELECT po.po_id, po.vendor_id, v.name AS vendor_name, po.order_date, " +
                     "po.total_cost, po.status " +
                     "FROM PurchaseOrder po " +
                     "JOIN Vendor v ON po.vendor_id = v.vendor_id " +
                     "WHERE po.po_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPurchaseOrder(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error finding purchase order by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(PurchaseOrder po) {
        String sql = "INSERT INTO PurchaseOrder (vendor_id, order_date, total_cost, status) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, po.getVendorId());
            stmt.setDate(2, po.getOrderDate() != null ? Date.valueOf(po.getOrderDate()) : Date.valueOf(LocalDate.now()));
            stmt.setBigDecimal(3, po.getTotalCost() != null ? po.getTotalCost() : BigDecimal.ZERO);
            stmt.setString(4, po.getStatus() != null ? po.getStatus() : "Pending");
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        po.setPoId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("Purchase order saved successfully: PO #" + po.getPoId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error saving purchase order: " + e.getMessage());
        }
        return false;
    }

    public boolean update(PurchaseOrder po) {
        String sql = "UPDATE PurchaseOrder SET vendor_id = ?, order_date = ?, total_cost = ?, " +
                     "status = ? WHERE po_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, po.getVendorId());
            stmt.setDate(2, po.getOrderDate() != null ? Date.valueOf(po.getOrderDate()) : null);
            stmt.setBigDecimal(3, po.getTotalCost() != null ? po.getTotalCost() : BigDecimal.ZERO);
            stmt.setString(4, po.getStatus());
            stmt.setInt(5, po.getPoId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Purchase order updated successfully: PO #" + po.getPoId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error updating purchase order: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM PurchaseOrder WHERE po_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Purchase order deleted successfully: ID " + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error deleting purchase order: " + e.getMessage());
        }
        return false;
    }

    public List<PurchaseOrder> search(String keyword) {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = "SELECT po.po_id, po.vendor_id, v.name AS vendor_name, po.order_date, " +
                     "po.total_cost, po.status " +
                     "FROM PurchaseOrder po " +
                     "JOIN Vendor v ON po.vendor_id = v.vendor_id " +
                     "WHERE v.name LIKE ? OR po.status LIKE ? OR CAST(po.po_id AS CHAR) LIKE ? " +
                     "ORDER BY po.order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    purchaseOrders.add(mapResultSetToPurchaseOrder(rs));
                }
            }
            LOGGER.info("Search found " + purchaseOrders.size() + " purchase orders matching: " + keyword);
        } catch (SQLException e) {
            LOGGER.severe("Error searching purchase orders: " + e.getMessage());
        }
        return purchaseOrders;
    }

    public List<PurchaseOrder> findByVendor(Integer vendorId) {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = "SELECT po.po_id, po.vendor_id, v.name AS vendor_name, po.order_date, " +
                     "po.total_cost, po.status " +
                     "FROM PurchaseOrder po " +
                     "JOIN Vendor v ON po.vendor_id = v.vendor_id " +
                     "WHERE po.vendor_id = ? " +
                     "ORDER BY po.order_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    purchaseOrders.add(mapResultSetToPurchaseOrder(rs));
                }
            }
            LOGGER.info("Found " + purchaseOrders.size() + " purchase orders for vendor ID: " + vendorId);
        } catch (SQLException e) {
            LOGGER.severe("Error finding purchase orders by vendor: " + e.getMessage());
        }
        return purchaseOrders;
    }

    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoId(rs.getInt("po_id"));
        po.setVendorId(rs.getInt("vendor_id"));
        po.setVendorName(rs.getString("vendor_name"));
        Date orderDate = rs.getDate("order_date");
        if (orderDate != null) {
            po.setOrderDate(orderDate.toLocalDate());
        }
        po.setTotalCost(rs.getBigDecimal("total_cost"));
        po.setStatus(rs.getString("status"));
        return po;
    }
}
