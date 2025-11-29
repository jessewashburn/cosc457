package org.bmc.app.model;

/**
 * Vendor entity representing material suppliers
 * @author BMC Systems Team
 */
public class Vendor {
    private Integer vendorId;
    private String name;
    private String contactInfo;
    private String phone;
    private String email;

    public Vendor() {
    }

    public Vendor(Integer vendorId, String name, String contactInfo, String phone, String email) {
        this.vendorId = vendorId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.phone = phone;
        this.email = email;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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

    @Override
    public String toString() {
        return name != null ? name : "";
    }
}
