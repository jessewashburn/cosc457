package org.bmc.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PurchaseOrder entity representing orders placed with vendors
 * @author BMC Systems Team
 */
public class PurchaseOrder {
    private Integer poId;
    private Integer vendorId;
    private String vendorName;
    private LocalDate orderDate;
    private BigDecimal totalCost;
    private String status; // Pending, Received, Cancelled

    public PurchaseOrder() {
    }

    public PurchaseOrder(Integer poId, Integer vendorId, String vendorName,
                        LocalDate orderDate, BigDecimal totalCost, String status) {
        this.poId = poId;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.orderDate = orderDate;
        this.totalCost = totalCost;
        this.status = status;
    }

    public Integer getPoId() {
        return poId;
    }

    public void setPoId(Integer poId) {
        this.poId = poId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "poId=" + poId +
                ", vendorId=" + vendorId +
                ", vendorName='" + vendorName + '\'' +
                ", orderDate=" + orderDate +
                ", totalCost=" + totalCost +
                ", status='" + status + '\'' +
                '}';
    }
}
