package org.bmc.app.dao;

import org.bmc.app.model.Vendor;
import org.bmc.app.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for Vendor entity
 * @author BMC Systems Team
 */
public class VendorDAO {
    private static final Logger LOGGER = Logger.getLogger(VendorDAO.class.getName());

    public VendorDAO() {
    }

    public List<Vendor> findAll() {
        List<Vendor> vendors = new ArrayList<>();
        String sql = "SELECT vendor_id, name, contact_info, phone, email " +
                     "FROM Vendor ORDER BY name";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vendors.add(mapResultSetToVendor(rs));
            }
            LOGGER.info("Retrieved " + vendors.size() + " vendors");
        } catch (SQLException e) {
            LOGGER.severe("Error finding all vendors: " + e.getMessage());
        }
        return vendors;
    }

    public Vendor findById(Integer id) {
        String sql = "SELECT vendor_id, name, contact_info, phone, email " +
                     "FROM Vendor WHERE vendor_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVendor(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.severe("Error finding vendor by ID: " + e.getMessage());
        }
        return null;
    }

    private Vendor mapResultSetToVendor(ResultSet rs) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setVendorId(rs.getInt("vendor_id"));
        vendor.setName(rs.getString("name"));
        vendor.setContactInfo(rs.getString("contact_info"));
        vendor.setPhone(rs.getString("phone"));
        vendor.setEmail(rs.getString("email"));
        return vendor;
    }
}
