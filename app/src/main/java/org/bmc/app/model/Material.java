package org.bmc.app.model;

import java.math.BigDecimal;

/**
 * Material entity representing inventory items
 * @author BMC Systems Team
 */
public class Material {
    private Integer materialId;
    private String name;
    private String category;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private BigDecimal unitCost;
    private Integer vendorId;
    private String vendorName;

    public Material() {
    }

    public Material(Integer materialId, String name, String category, 
                   Integer stockQuantity, Integer reorderLevel, BigDecimal unitCost) {
        this.materialId = materialId;
        this.name = name;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.unitCost = unitCost;
    }

    public Material(Integer materialId, String name, String category, 
                   Integer stockQuantity, Integer reorderLevel, BigDecimal unitCost,
                   Integer vendorId, String vendorName) {
        this.materialId = materialId;
        this.name = name;
        this.category = category;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.unitCost = unitCost;
        this.vendorId = vendorId;
        this.vendorName = vendorName;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
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

    public boolean isLowStock() {
        return stockQuantity != null && reorderLevel != null 
               && stockQuantity <= reorderLevel;
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialId=" + materialId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", reorderLevel=" + reorderLevel +
                ", unitCost=" + unitCost +
                ", vendorId=" + vendorId +
                ", vendorName='" + vendorName + '\'' +
                '}';
    }
}
