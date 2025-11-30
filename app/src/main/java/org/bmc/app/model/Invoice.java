package org.bmc.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Invoice model class representing an invoice entity in the Baltimore Metal Crafters system.
 * Maps to the Invoice table in the database.
 */
public class Invoice {
    
    private Integer invoiceId;
    private Integer jobId;
    private LocalDate invoiceDate;
    private BigDecimal laborCost;
    private BigDecimal materialCost;
    private BigDecimal totalAmount;
    private Boolean paid;
    
    // Additional fields for display purposes (not stored in Invoice table)
    private String jobDescription;
    private String customerName;
    private BigDecimal amountPaid;
    private BigDecimal balanceDue;
    
    /**
     * Default constructor
     */
    public Invoice() {
        this.invoiceDate = LocalDate.now();
        this.paid = false;
    }
    
    /**
     * Constructor without ID (for new invoices)
     * 
     * @param jobId ID of the related job
     * @param invoiceDate invoice date
     * @param totalAmount total invoice amount
     * @param paid payment status
     */
    public Invoice(Integer jobId, LocalDate invoiceDate, BigDecimal totalAmount, Boolean paid) {
        this.jobId = jobId;
        this.invoiceDate = invoiceDate != null ? invoiceDate : LocalDate.now();
        this.totalAmount = totalAmount;
        this.paid = paid != null ? paid : false;
    }
    
    /**
     * Full constructor with ID
     * 
     * @param invoiceId unique invoice ID
     * @param jobId ID of the related job
     * @param invoiceDate invoice date
     * @param totalAmount total invoice amount
     * @param paid payment status
     */
    public Invoice(Integer invoiceId, Integer jobId, LocalDate invoiceDate, 
                   BigDecimal totalAmount, Boolean paid) {
        this.invoiceId = invoiceId;
        this.jobId = jobId;
        this.invoiceDate = invoiceDate != null ? invoiceDate : LocalDate.now();
        this.totalAmount = totalAmount;
        this.paid = paid != null ? paid : false;
    }
    
    // Getters and Setters
    
    public Integer getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public Integer getJobId() {
        return jobId;
    }
    
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
    
    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }
    
    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    
    public BigDecimal getLaborCost() {
        return laborCost;
    }
    
    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }
    
    public BigDecimal getMaterialCost() {
        return materialCost;
    }
    
    public void setMaterialCost(BigDecimal materialCost) {
        this.materialCost = materialCost;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Boolean getPaid() {
        return paid;
    }
    
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
    
    public String getJobDescription() {
        return jobDescription;
    }
    
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public BigDecimal getAmountPaid() {
        return amountPaid;
    }
    
    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }
    
    public BigDecimal getBalanceDue() {
        return balanceDue;
    }
    
    public void setBalanceDue(BigDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }
    
    // Business Methods
    
    /**
     * Validates that the invoice has required information
     * 
     * @return true if invoice has valid job ID and total amount, false otherwise
     */
    public boolean isValid() {
        return jobId != null && 
               totalAmount != null && 
               totalAmount.compareTo(BigDecimal.ZERO) >= 0;
    }
    
    /**
     * Gets a display-friendly string for the invoice
     * 
     * @return formatted invoice display string
     */
    public String getDisplayName() {
        StringBuilder display = new StringBuilder();
        
        if (invoiceId != null) {
            display.append("Invoice #").append(invoiceId);
        } else {
            display.append("New Invoice");
        }
        
        if (customerName != null) {
            display.append(" - ").append(customerName);
        }
        
        if (totalAmount != null) {
            display.append(" ($").append(totalAmount).append(")");
        }
        
        return display.toString();
    }
    
    /**
     * Calculates days since invoice date
     * 
     * @return days since invoice date, null if no invoice date
     */
    public Long getDaysSinceInvoice() {
        if (invoiceDate == null) {
            return null;
        }
        
        return ChronoUnit.DAYS.between(invoiceDate, LocalDate.now());
    }
    
    /**
     * Gets aging category for accounts receivable
     * 
     * @return aging category as string
     */
    public String getAgingCategory() {
        if (paid != null && paid) {
            return "Paid";
        }
        
        Long daysSince = getDaysSinceInvoice();
        if (daysSince == null) {
            return "Unknown";
        }
        
        if (daysSince <= 30) {
            return "Current (0-30 days)";
        } else if (daysSince <= 60) {
            return "31-60 days";
        } else if (daysSince <= 90) {
            return "61-90 days";
        } else {
            return "Over 90 days";
        }
    }
    
    /**
     * Checks if the invoice is overdue (unpaid after 30 days)
     * 
     * @return true if invoice is overdue
     */
    public boolean isOverdue() {
        if (paid != null && paid) {
            return false;
        }
        
        Long daysSince = getDaysSinceInvoice();
        return daysSince != null && daysSince > 30;
    }
    
    /**
     * Checks if the invoice is seriously overdue (unpaid after 90 days)
     * 
     * @return true if invoice is seriously overdue
     */
    public boolean isSeriouslyOverdue() {
        if (paid != null && paid) {
            return false;
        }
        
        Long daysSince = getDaysSinceInvoice();
        return daysSince != null && daysSince > 90;
    }
    
    /**
     * Gets payment status as display string
     * 
     * @return payment status for display
     */
    public String getPaymentStatusDisplay() {
        if (paid != null && paid) {
            return "Paid";
        } else {
            return "Unpaid";
        }
    }
    
    /**
     * Calculates balance due (total amount minus payments)
     * Uses the balanceDue field if set, otherwise returns total amount for unpaid invoices
     * 
     * @return balance due amount
     */
    public BigDecimal calculateBalanceDue() {
        if (balanceDue != null) {
            return balanceDue;
        }
        
        if (paid != null && paid) {
            return BigDecimal.ZERO;
        }
        
        if (totalAmount == null) {
            return BigDecimal.ZERO;
        }
        
        if (amountPaid != null) {
            return totalAmount.subtract(amountPaid);
        }
        
        return totalAmount;
    }
    
    /**
     * Gets a formatted total amount string
     * 
     * @return formatted currency string
     */
    public String getFormattedTotalAmount() {
        if (totalAmount == null) {
            return "$0.00";
        }
        
        return String.format("$%.2f", totalAmount);
    }
    
    /**
     * Gets a formatted balance due string
     * 
     * @return formatted currency string
     */
    public String getFormattedBalanceDue() {
        BigDecimal balance = calculateBalanceDue();
        return String.format("$%.2f", balance);
    }
    
    /**
     * Marks the invoice as paid
     */
    public void markAsPaid() {
        this.paid = true;
        this.balanceDue = BigDecimal.ZERO;
        this.amountPaid = this.totalAmount;
    }
    
    /**
     * Marks the invoice as unpaid
     */
    public void markAsUnpaid() {
        this.paid = false;
        this.amountPaid = BigDecimal.ZERO;
        this.balanceDue = this.totalAmount;
    }
    
    // Object overrides
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Invoice invoice = (Invoice) obj;
        return Objects.equals(invoiceId, invoice.invoiceId) &&
               Objects.equals(jobId, invoice.jobId) &&
               Objects.equals(invoiceDate, invoice.invoiceDate) &&
               Objects.equals(totalAmount, invoice.totalAmount) &&
               Objects.equals(paid, invoice.paid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, jobId, invoiceDate, totalAmount, paid);
    }
    
    @Override
    public String toString() {
        return String.format("Invoice{id=%d, jobId=%d, invoiceDate=%s, totalAmount=%s, paid=%s}",
                invoiceId, jobId, invoiceDate, totalAmount, paid);
    }
}