package org.bmc.app.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bmc.app.model.Employee;
import org.bmc.app.util.DBConnection;

/**
 * Data Access Object for Employee entity.
 * Provides CRUD operations for employees in the Baltimore Metal Crafters database.
 */
public class EmployeeDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EmployeeDAO.class.getName());
    
    // SQL Queries
    private static final String INSERT_SQL = 
        "INSERT INTO Employee (name, role, specialization, contact_info, hourly_rate) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID_SQL = 
        "SELECT employee_id, name, role, specialization, contact_info, hourly_rate FROM Employee WHERE employee_id = ?";
    
    private static final String SELECT_ALL_SQL = 
        "SELECT employee_id, name, role, specialization, contact_info, hourly_rate FROM Employee ORDER BY name";
    
    private static final String UPDATE_SQL = 
        "UPDATE Employee SET name = ?, role = ?, specialization = ?, contact_info = ?, hourly_rate = ? WHERE employee_id = ?";
    
    private static final String DELETE_SQL = 
        "DELETE FROM Employee WHERE employee_id = ?";
    
    private static final String SELECT_BY_ROLE_SQL = 
        "SELECT employee_id, name, role, specialization, contact_info, hourly_rate FROM Employee WHERE role = ? ORDER BY name";
    
    private static final String SEARCH_BY_NAME_SQL = 
        "SELECT employee_id, name, role, specialization, contact_info, hourly_rate FROM Employee " +
        "WHERE name LIKE ? ORDER BY name";
    
    private static final String COUNT_WORKLOGS_SQL = 
        "SELECT COUNT(*) FROM WorkLog WHERE employee_id = ?";
    
    private static final String SELECT_WORKLOAD_SQL = 
        "SELECT e.employee_id, e.name, e.role, e.specialization, e.contact_info, e.hourly_rate, " +
        "COALESCE(SUM(w.hours_worked), 0) as total_hours " +
        "FROM Employee e LEFT JOIN WorkLog w ON e.employee_id = w.employee_id " +
        "GROUP BY e.employee_id, e.name, e.role, e.specialization, e.contact_info, e.hourly_rate " +
        "ORDER BY total_hours DESC";
    
    /**
     * Creates a new employee in the database
     * 
     * @param employee Employee object to create (ID will be auto-generated)
     * @return Employee object with generated ID, or null if creation failed
     */
    public Employee create(Employee employee) {
        if (employee == null || !employee.isValid()) {
            LOGGER.warning("Cannot create invalid employee");
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getRole().getValue());
            pstmt.setString(3, employee.getSpecialization());
            pstmt.setString(4, employee.getContactInfo());
            pstmt.setBigDecimal(5, employee.getHourlyRate());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    employee.setEmployeeId(rs.getInt(1));
                    LOGGER.info(() -> String.format("Created employee with ID: %d", employee.getEmployeeId()));
                    return employee;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating employee", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves an employee by ID
     * 
     * @param employeeId ID of the employee to retrieve
     * @return Employee object if found, null otherwise
     */
    public Employee findById(Integer employeeId) {
        if (employeeId == null) {
            return null;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID_SQL);
            pstmt.setInt(1, employeeId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding employee by ID: " + employeeId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return null;
    }
    
    /**
     * Retrieves all employees from the database
     * 
     * @return List of all employees, ordered by name
     */
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
            LOGGER.info(() -> String.format("Retrieved %d employees", employees.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all employees", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return employees;
    }
    
    /**
     * Updates an existing employee in the database
     * 
     * @param employee Employee object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean update(Employee employee) {
        if (employee == null || employee.getEmployeeId() == null || !employee.isValid()) {
            LOGGER.warning("Cannot update invalid employee");
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(UPDATE_SQL);
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getRole().getValue());
            pstmt.setString(3, employee.getSpecialization());
            pstmt.setString(4, employee.getContactInfo());
            pstmt.setBigDecimal(5, employee.getHourlyRate());
            pstmt.setInt(6, employee.getEmployeeId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Updated employee ID: %d", employee.getEmployeeId()));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating employee", e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Deletes an employee from the database
     * Note: This will fail if the employee has associated work logs (foreign key constraint)
     * 
     * @param employeeId ID of the employee to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean delete(Integer employeeId) {
        if (employeeId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(DELETE_SQL);
            pstmt.setInt(1, employeeId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                LOGGER.info(() -> String.format("Deleted employee ID: %d", employeeId));
                return true;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error deleting employee ID: " + employeeId, e);
        } finally {
            closeResources(conn, pstmt, null);
        }
        
        return false;
    }
    
    /**
     * Retrieves employees by role
     * 
     * @param role Employee role to filter by
     * @return List of employees with the specified role
     */
    public List<Employee> findByRole(Employee.Role role) {
        List<Employee> employees = new ArrayList<>();
        
        if (role == null) {
            return employees;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ROLE_SQL);
            pstmt.setString(1, role.getValue());
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d employees with role: %s", employees.size(), role));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding employees by role", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return employees;
    }
    
    /**
     * Searches for employees by name (partial match)
     * 
     * @param namePattern Pattern to search for (use % for wildcards)
     * @return List of matching employees
     */
    public List<Employee> searchByName(String namePattern) {
        List<Employee> employees = new ArrayList<>();
        
        if (namePattern == null || namePattern.trim().isEmpty()) {
            return employees;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SEARCH_BY_NAME_SQL);
            pstmt.setString(1, "%" + namePattern + "%");
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
            LOGGER.info(() -> String.format("Found %d employees matching: %s", employees.size(), namePattern));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching employees by name", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return employees;
    }
    
    /**
     * Checks if an employee can be safely deleted (has no associated work logs)
     * 
     * @param employeeId ID of the employee to check
     * @return true if employee can be deleted, false otherwise
     */
    public boolean canDelete(Integer employeeId) {
        if (employeeId == null) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(COUNT_WORKLOGS_SQL);
            pstmt.setInt(1, employeeId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;  // Can delete if no work logs
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if employee can be deleted", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return false;
    }
    
    /**
     * Gets employees with their workload summary (total hours worked)
     * 
     * @return List of employees with workload information
     */
    public List<Employee> findAllWithWorkload() {
        List<Employee> employees = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(SELECT_WORKLOAD_SQL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Employee employee = mapResultSetToEmployee(rs);
                // Note: total_hours would need to be added as a field to Employee model
                // or handled in a separate DTO for workload reports
                employees.add(employee);
            }
            
            LOGGER.info(() -> String.format("Retrieved %d employees with workload data", employees.size()));
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving employees with workload", e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        
        return employees;
    }
    
    /**
     * Gets all craftspeople (restorers and fabricators)
     * 
     * @return List of employees who are craftspeople
     */
    public List<Employee> findCraftspeople() {
        List<Employee> craftspeople = new ArrayList<>();
        craftspeople.addAll(findByRole(Employee.Role.RESTORER));
        craftspeople.addAll(findByRole(Employee.Role.FABRICATOR));
        return craftspeople;
    }
    
    /**
     * Maps a ResultSet row to an Employee object
     * 
     * @param rs ResultSet positioned at an employee row
     * @return Employee object
     * @throws SQLException if database access error occurs
     */
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        String roleString = rs.getString("role");
        Employee.Role role = Employee.Role.fromString(roleString);
        
        return new Employee(
            rs.getInt("employee_id"),
            rs.getString("name"),
            role,
            rs.getString("specialization"),
            rs.getString("contact_info"),
            rs.getBigDecimal("hourly_rate")
        );
    }
    
    /**
     * Safely closes database resources
     * 
     * @param conn Connection to close
     * @param pstmt PreparedStatement to close
     * @param rs ResultSet to close
     */
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
            }
        }
        
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing PreparedStatement", e);
            }
        }
        
        DBConnection.closeConnection(conn);
    }
}