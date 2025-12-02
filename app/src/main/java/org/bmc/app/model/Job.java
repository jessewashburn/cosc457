package org.bmc.app.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Job model class representing a job entity in the Baltimore Metal Crafters system.
 * Maps to the Job table in the database.
 */
public class Job {
    
    /**
     * Enum for job status
     */
    public enum Status {
        PLANNED("Planned"),
        IN_PROGRESS("InProgress"),
        COMPLETED("Completed");
        
        private final String value;
        
        Status(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Status fromString(String status) {
            if (status == null) return null;
            
            for (Status s : Status.values()) {
                if (s.value.equalsIgnoreCase(status)) {
                    return s;
                }
            }
            return null;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
    
    private Integer jobId;
    private Integer customerId;
    private Integer employeeId;
    private Integer quoteId;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Status status;
    private BigDecimal estimatedLaborCost;
    private BigDecimal estimatedMaterialCost;
    
    // Additional fields for display purposes (not stored in Job table)
    private String customerName;
    private String employeeName;
    private BigDecimal estimatedValue;
    
    /**
     * Default constructor
     */
    public Job() {
        this.status = Status.PLANNED; // Default status
    }
    
    /**
     * Constructor without ID (for new jobs)
     * 
     * @param customerId ID of the customer
     * @param quoteId ID of the related quote (optional)
     * @param description job description
     * @param startDate job start date
     * @param dueDate job due date
     * @param status job status
     */
    public Job(Integer customerId, Integer quoteId, String description, 
               LocalDate startDate, LocalDate dueDate, Status status, 
               BigDecimal estimatedLaborCost, BigDecimal estimatedMaterialCost) {
        this.customerId = customerId;
        this.quoteId = quoteId;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = status != null ? status : Status.PLANNED;
        this.estimatedLaborCost = estimatedLaborCost;
        this.estimatedMaterialCost = estimatedMaterialCost;
    }
    
    /**
     * Full constructor with ID
     * 
     * @param jobId unique job ID
     * @param customerId ID of the customer
     * @param quoteId ID of the related quote (optional)
     * @param description job description
     * @param startDate job start date
     * @param dueDate job due date
     * @param status job status
     */
    public Job(Integer jobId, Integer customerId, Integer quoteId, String description,
               LocalDate startDate, LocalDate dueDate, Status status) {
        this.jobId = jobId;
        this.customerId = customerId;
        this.quoteId = quoteId;
        this.description = description;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = status != null ? status : Status.PLANNED;
    }
    
    // Getters and Setters
    
    public Integer getJobId() {
        return jobId;
    }
    
    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public Integer getQuoteId() {
        return quoteId;
    }
    
    public void setQuoteId(Integer quoteId) {
        this.quoteId = quoteId;
    }
    
    public Integer getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }
    
    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }
    
    public BigDecimal getEstimatedLaborCost() {
        return estimatedLaborCost;
    }
    
    public void setEstimatedLaborCost(BigDecimal estimatedLaborCost) {
        this.estimatedLaborCost = estimatedLaborCost;
    }
    
    public BigDecimal getEstimatedMaterialCost() {
        return estimatedMaterialCost;
    }
    
    public void setEstimatedMaterialCost(BigDecimal estimatedMaterialCost) {
        this.estimatedMaterialCost = estimatedMaterialCost;
    }
    
    // Business Methods
    
    /**
     * Validates that the job has required information
     * 
     * @return true if job has valid customer ID and description, false otherwise
     */
    public boolean isValid() {
        return customerId != null && 
               description != null && !description.trim().isEmpty();
    }
    
    /**
     * Gets a display-friendly string for the job
     * 
     * @return formatted job display string
     */
    public String getDisplayName() {
        if (description == null) {
            return "Job #" + (jobId != null ? jobId : "New");
        }
        
        StringBuilder display = new StringBuilder();
        
        if (jobId != null) {
            display.append("Job #").append(jobId).append(": ");
        }
        
        display.append(description);
        
        if (customerName != null) {
            display.append(" (").append(customerName).append(")");
        }
        
        return display.toString();
    }
    
    /**
     * Calculates days until due date
     * 
     * @return days until due (negative if overdue), null if no due date
     */
    public Long getDaysUntilDue() {
        if (dueDate == null) {
            return null;
        }
        
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
    
    /**
     * Checks if the job is overdue
     * 
     * @return true if job is past due date and not completed
     */
    public boolean isOverdue() {
        if (dueDate == null || status == Status.COMPLETED) {
            return false;
        }
        
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * Checks if the job is due soon (within specified days)
     * 
     * @param days number of days to check
     * @return true if job is due within the specified days
     */
    public boolean isDueSoon(int days) {
        Long daysUntil = getDaysUntilDue();
        return daysUntil != null && daysUntil >= 0 && daysUntil <= days;
    }
    
    /**
     * Checks if the job is active (planned or in progress)
     * 
     * @return true if job status is planned or in progress
     */
    public boolean isActive() {
        return status == Status.PLANNED || status == Status.IN_PROGRESS;
    }
    
    /**
     * Gets the status as a display-friendly string
     * 
     * @return status with proper formatting
     */
    public String getStatusDisplay() {
        if (status == null) return "Unknown";
        if (status == Status.PLANNED) {
            return "Planned";
        } else if (status == Status.IN_PROGRESS) {
            return "In Progress";
        } else if (status == Status.COMPLETED) {
            return "Completed";
        } else {
            return "Unknown";
        }
    }
    
    /**
     * Calculates estimated duration in days
     * 
     * @return days between start and due date, null if either date is missing
     */
    public Long getEstimatedDuration() {
        if (startDate == null || dueDate == null) {
            return null;
        }
        
        return ChronoUnit.DAYS.between(startDate, dueDate);
    }
    
    // Object overrides
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Job job = (Job) obj;
        return Objects.equals(jobId, job.jobId) &&
               Objects.equals(customerId, job.customerId) &&
               Objects.equals(quoteId, job.quoteId) &&
               Objects.equals(description, job.description) &&
               Objects.equals(startDate, job.startDate) &&
               Objects.equals(dueDate, job.dueDate) &&
               Objects.equals(status, job.status);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(jobId, customerId, quoteId, description, startDate, dueDate, status);
    }
    
    @Override
    public String toString() {
        return String.format("Job{id=%d, customerId=%d, quoteId=%d, description='%s', startDate=%s, dueDate=%s, status=%s}",
                jobId, customerId, quoteId, description, startDate, dueDate, status);
    }
}