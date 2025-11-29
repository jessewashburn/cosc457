package org.bmc.app.model;

import java.math.BigDecimal;

/**
 * POItem entity representing line items in a purchase order
 * @author BMC Systems Team
 */
public class POItem {
    private Integer poItemId;
    private Integer poId;
    private Integer materialId;
    private String materialName;
    private Integer quantity;
    private BigDecimal unitPrice;

    public POItem() {
    }

    public POItem(Integer poItemId, Integer poId, Integer materialId, String materialName,
                  Integer quantity, BigDecimal unitPrice) {
        this.poItemId = poItemId;
        this.poId = poId;
        this.materialId = materialId;
        this.materialName = materialName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Integer getPoItemId() {
        return poItemId;
    }

    public void setPoItemId(Integer poItemId) {
        this.poItemId = poItemId;
    }

    public Integer getPoId() {
        return poId;
    }

    public void setPoId(Integer poId) {
        this.poId = poId;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        if (quantity != null && unitPrice != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "POItem{" +
                "poItemId=" + poItemId +
                ", poId=" + poId +
                ", materialId=" + materialId +
                ", materialName='" + materialName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
