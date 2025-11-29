package org.bmc.app.model;

import java.util.Objects;

/**
 * Employee model class representing an employee entity in the Baltimore Metal Crafters system.
 * Maps to the Employee table in the database.
 */
public class Employee {
    
    /**
     * Enum for employee roles
     */
    public enum Role {
        RESTORER("restorer"),
        FABRICATOR("fabricator"), 
        ADMIN("admin");
        
        private final String value;
        
        Role(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Role fromString(String role) {
            if (role == null) return null;
            
            for (Role r : Role.values()) {
                if (r.value.equalsIgnoreCase(role)) {
                    return r;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
    
    private Integer employeeId;
    private String name;
    private Role role;
    private String specialization;
    private String contactInfo;
    private java.math.BigDecimal hourlyRate;
    
    /**
     * Default constructor
     */
    public Employee() {
    }
    
    /**
     * Constructor without ID (for new employees)
     * 
     * @param name employee name
     * @param role employee role
     * @param specialization employee specialization
     * @param contactInfo employee contact information
     * @param hourlyRate hourly pay rate
     */
    public Employee(String name, Role role, String specialization, String contactInfo, java.math.BigDecimal hourlyRate) {
        this.name = name;
        this.role = role;
        this.specialization = specialization;
        this.contactInfo = contactInfo;
        this.hourlyRate = hourlyRate;
    }
    
    /**
     * Full constructor with ID
     * 
     * @param employeeId unique employee ID
     * @param name employee name
     * @param role employee role
     * @param specialization employee specialization
     * @param contactInfo employee contact information
     * @param hourlyRate hourly pay rate
     */
    public Employee(Integer employeeId, String name, Role role, String specialization, String contactInfo, java.math.BigDecimal hourlyRate) {
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
        this.specialization = specialization;
        this.contactInfo = contactInfo;
        this.hourlyRate = hourlyRate;
    }
    
    // Getters and Setters
    
    public Integer getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    
    public String getContactInfo() {
        return contactInfo;
    }
    
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
    
    public java.math.BigDecimal getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(java.math.BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    // Business Methods
    
    /**
     * Validates that the employee has required information
     * 
     * @return true if employee has valid name and role, false otherwise
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() && role != null;
    }
    
    /**
     * Gets a display-friendly string for the employee
     * 
     * @return formatted employee display string
     */
    public String getDisplayName() {
        if (name == null) {
            return "Unknown Employee";
        }
        
        StringBuilder display = new StringBuilder(name);
        
        if (role != null) {
            display.append(" (").append(role.toString()).append(")");
        }
        
        if (specialization != null && !specialization.trim().isEmpty()) {
            display.append(" - ").append(specialization);
        }
        
        return display.toString();
    }
    
    /**
     * Checks if employee is in an administrative role
     * 
     * @return true if employee role is admin
     */
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    /**
     * Checks if employee is a craftsperson (restorer or fabricator)
     * 
     * @return true if employee is restorer or fabricator
     */
    public boolean isCraftsperson() {
        return role == Role.RESTORER || role == Role.FABRICATOR;
    }
    
    /**
     * Gets the role as a capitalized display string
     * 
     * @return role with proper capitalization
     */
    public String getRoleDisplay() {
        if (role == null) return "Unknown";
        
        String roleStr = role.getValue();
        return roleStr.substring(0, 1).toUpperCase() + roleStr.substring(1);
    }
    
    // Object overrides
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Employee employee = (Employee) obj;
        return Objects.equals(employeeId, employee.employeeId) &&
               Objects.equals(name, employee.name) &&
               Objects.equals(role, employee.role) &&
               Objects.equals(specialization, employee.specialization) &&
               Objects.equals(contactInfo, employee.contactInfo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, name, role, specialization, contactInfo);
    }
    
    @Override
    public String toString() {
        return String.format("Employee{id=%d, name='%s', role=%s, specialization='%s', contactInfo='%s'}",
                employeeId, name, role, specialization, contactInfo);
    }
}