package org.bmc.app.model;

import java.util.Objects;

/**
 * Customer model class representing a customer entity in the Baltimore Metal Crafters system.
 * Maps to the Customer table in the database.
 */
public class Customer {
    
    private Integer customerId;
    private String name;
    private String phone;
    private String email;
    private String address;
    
    /**
     * Default constructor
     */
    public Customer() {
    }
    
    /**
     * Constructor without ID (for new customers)
     * 
     * @param name customer name
     * @param phone customer phone number
     * @param email customer email address
     * @param address customer address
     */
    public Customer(String name, String phone, String email, String address) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
    
    /**
     * Full constructor with ID
     * 
     * @param customerId unique customer ID
     * @param name customer name
     * @param phone customer phone number
     * @param email customer email address
     * @param address customer address
     */
    public Customer(Integer customerId, String name, String phone, String email, String address) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
    
    // Getters and Setters
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    // Business Methods
    
    /**
     * Validates that the customer has required information
     * 
     * @return true if customer has valid name, false otherwise
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }
    
    /**
     * Gets a display-friendly string for the customer
     * 
     * @return formatted customer display string
     */
    public String getDisplayName() {
        if (name == null) {
            return "Unknown Customer";
        }
        
        StringBuilder display = new StringBuilder(name);
        
        if (phone != null && !phone.trim().isEmpty()) {
            display.append(" (").append(phone).append(")");
        }
        
        return display.toString();
    }
    
    /**
     * Checks if customer has contact information
     * 
     * @return true if customer has phone or email
     */
    public boolean hasContactInfo() {
        return (phone != null && !phone.trim().isEmpty()) || 
               (email != null && !email.trim().isEmpty());
    }
    
    // Object overrides
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Customer customer = (Customer) obj;
        return Objects.equals(customerId, customer.customerId) &&
               Objects.equals(name, customer.name) &&
               Objects.equals(phone, customer.phone) &&
               Objects.equals(email, customer.email) &&
               Objects.equals(address, customer.address);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customerId, name, phone, email, address);
    }
    
    @Override
    public String toString() {
        return String.format("Customer{id=%d, name='%s', phone='%s', email='%s', address='%s'}",
                customerId, name, phone, email, address);
    }
}