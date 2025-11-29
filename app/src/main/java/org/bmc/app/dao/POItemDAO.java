package org.bmc.app.dao;

import org.bmc.app.model.POItem;
import org.bmc.app.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for POItem entity
 * @author BMC Systems Team
 */
public class POItemDAO {
    private static final Logger LOGGER = Logger.getLogger(POItemDAO.class.getName());

    public POItemDAO() {
    }

    public List<POItem> findByPurchaseOrder(Integer poId) {
        List<POItem> items = new ArrayList<>();
        String sql = "SELECT poi.po_item_id, poi.po_id, poi.material_id, m.name AS material_name, " +
                     "poi.quantity, poi.unit_price " +
                     "FROM POItem poi " +
                     "JOIN Material m ON poi.material_id = m.material_id " +
                     "WHERE poi.po_id = ? " +
                     "ORDER BY poi.po_item_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToPOItem(rs));
                }
            }
            LOGGER.info("Retrieved " + items.size() + " items for PO #" + poId);
        } catch (SQLException e) {
            LOGGER.severe("Error finding PO items: " + e.getMessage());
        }
        return items;
    }

    public POItem findById(Integer id) {
        String sql = "SELECT poi.po_item_id, poi.po_id, poi.material_id, m.name AS material_name, " +
                     "poi.quantity, poi.unit_price " +
                     "FROM POItem poi " +
                     "JOIN Material m ON poi.material_id = m.material_id " +
                     "WHERE poi.po_item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPOItem(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error finding PO item by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean save(POItem item) {
        String sql = "INSERT INTO POItem (po_id, material_id, quantity, unit_price) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getPoId());
            stmt.setInt(2, item.getMaterialId());
            stmt.setInt(3, item.getQuantity() != null ? item.getQuantity() : 1);
            stmt.setBigDecimal(4, item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setPoItemId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("PO item saved successfully: Item #" + item.getPoItemId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error saving PO item: " + e.getMessage());
        }
        return false;
    }

    public boolean update(POItem item) {
        String sql = "UPDATE POItem SET material_id = ?, quantity = ?, unit_price = ? " +
                     "WHERE po_item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, item.getMaterialId());
            stmt.setInt(2, item.getQuantity() != null ? item.getQuantity() : 1);
            stmt.setBigDecimal(3, item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO);
            stmt.setInt(4, item.getPoItemId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("PO item updated successfully: Item #" + item.getPoItemId());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error updating PO item: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM POItem WHERE po_item_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("PO item deleted successfully: ID " + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error deleting PO item: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteByPurchaseOrder(Integer poId) {
        String sql = "DELETE FROM POItem WHERE po_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Deleted " + rowsAffected + " items for PO #" + poId);
            return true;
        } catch (SQLException e) {
            LOGGER.severe("Error deleting PO items: " + e.getMessage());
        }
        return false;
    }

    private POItem mapResultSetToPOItem(ResultSet rs) throws SQLException {
        POItem item = new POItem();
        item.setPoItemId(rs.getInt("po_item_id"));
        item.setPoId(rs.getInt("po_id"));
        item.setMaterialId(rs.getInt("material_id"));
        item.setMaterialName(rs.getString("material_name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        return item;
    }
}
