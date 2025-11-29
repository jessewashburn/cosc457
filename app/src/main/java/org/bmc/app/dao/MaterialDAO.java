package org.bmc.app.dao;

import org.bmc.app.model.Material;
import org.bmc.app.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for Material entity
 * @author BMC Systems Team
 */
public class MaterialDAO {
    private static final Logger LOGGER = Logger.getLogger(MaterialDAO.class.getName());

    public MaterialDAO() {
    }

    public List<Material> findAll() {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT material_id, name, category, stock_quantity, reorder_level, unit_cost " +
                     "FROM Material ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }
            LOGGER.info("Retrieved " + materials.size() + " materials");
        } catch (SQLException e) {
            LOGGER.severe("Error finding all materials: " + e.getMessage());
        }
        return materials;
    }

    public Material findById(Integer id) {
        String sql = "SELECT material_id, name, category, stock_quantity, reorder_level, unit_cost " +
                     "FROM Material WHERE material_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMaterial(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error finding material by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Material> findLowStock() {
        List<Material> lowStockMaterials = new ArrayList<>();
        String sql = "SELECT material_id, name, category, stock_quantity, reorder_level, unit_cost " +
                     "FROM Material WHERE stock_quantity <= reorder_level ORDER BY stock_quantity";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                lowStockMaterials.add(mapResultSetToMaterial(rs));
            }
            LOGGER.info("Found " + lowStockMaterials.size() + " materials with low stock");
        } catch (SQLException e) {
            LOGGER.severe("Error finding low stock materials: " + e.getMessage());
        }
        return lowStockMaterials;
    }

    public boolean save(Material material) {
        String sql = "INSERT INTO Material (name, category, stock_quantity, reorder_level, unit_cost) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, material.getName());
            stmt.setString(2, material.getCategory());
            stmt.setInt(3, material.getStockQuantity() != null ? material.getStockQuantity() : 0);
            stmt.setInt(4, material.getReorderLevel() != null ? material.getReorderLevel() : 5);
            stmt.setBigDecimal(5, material.getUnitCost());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        material.setMaterialId(generatedKeys.getInt(1));
                    }
                }
                LOGGER.info("Material saved successfully: " + material.getName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error saving material: " + e.getMessage());
        }
        return false;
    }

    public boolean update(Material material) {
        String sql = "UPDATE Material SET name = ?, category = ?, stock_quantity = ?, " +
                     "reorder_level = ?, unit_cost = ? WHERE material_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, material.getName());
            stmt.setString(2, material.getCategory());
            stmt.setInt(3, material.getStockQuantity() != null ? material.getStockQuantity() : 0);
            stmt.setInt(4, material.getReorderLevel() != null ? material.getReorderLevel() : 5);
            stmt.setBigDecimal(5, material.getUnitCost());
            stmt.setInt(6, material.getMaterialId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Material updated successfully: " + material.getName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error updating material: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(Integer id) {
        String sql = "DELETE FROM Material WHERE material_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Material deleted successfully: ID " + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.severe("Error deleting material: " + e.getMessage());
        }
        return false;
    }

    public List<Material> search(String keyword) {
        List<Material> materials = new ArrayList<>();
        String sql = "SELECT material_id, name, category, stock_quantity, reorder_level, unit_cost " +
                     "FROM Material WHERE name LIKE ? OR category LIKE ? ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materials.add(mapResultSetToMaterial(rs));
                }
            }
            LOGGER.info("Search found " + materials.size() + " materials matching: " + keyword);
        } catch (SQLException e) {
            LOGGER.severe("Error searching materials: " + e.getMessage());
        }
        return materials;
    }

    private Material mapResultSetToMaterial(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setMaterialId(rs.getInt("material_id"));
        material.setName(rs.getString("name"));
        material.setCategory(rs.getString("category"));
        material.setStockQuantity(rs.getInt("stock_quantity"));
        material.setReorderLevel(rs.getInt("reorder_level"));
        material.setUnitCost(rs.getBigDecimal("unit_cost"));
        return material;
    }
}
