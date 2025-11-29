package org.bmc.app.dao;

import org.bmc.app.util.DBConnection;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Data Access Object for generating reports.
 * Provides methods for complex queries and analytics.
 */
public class ReportDAO {
    private static final Logger logger = Logger.getLogger(ReportDAO.class.getName());
    
    /**
     * Represents a job due in the next 7 days
     */
    public static class JobDueSoonReport {
        private int jobId;
        private String customerName;
        private String description;
        private LocalDate dueDate;
        private String status;
        private long daysUntilDue;
        
        public JobDueSoonReport(int jobId, String customerName, String description, 
                                LocalDate dueDate, String status, long daysUntilDue) {
            this.jobId = jobId;
            this.customerName = customerName;
            this.description = description;
            this.dueDate = dueDate;
            this.status = status;
            this.daysUntilDue = daysUntilDue;
        }
        
        public int getJobId() { return jobId; }
        public String getCustomerName() { return customerName; }
        public String getDescription() { return description; }
        public LocalDate getDueDate() { return dueDate; }
        public String getStatus() { return status; }
        public long getDaysUntilDue() { return daysUntilDue; }
    }
    
    /**
     * Get all jobs due within the next 7 days (excluding completed jobs)
     */
    public List<JobDueSoonReport> getJobsDueInNext7Days() {
        List<JobDueSoonReport> jobs = new ArrayList<>();
        
        String sql = "SELECT j.job_id, c.name AS customer_name, j.description, " +
                     "j.due_date, j.status " +
                     "FROM Job j " +
                     "JOIN Customer c ON j.customer_id = c.customer_id " +
                     "WHERE j.status != 'Completed' " +
                     "AND j.due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
                     "ORDER BY j.due_date ASC, c.name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            LocalDate today = LocalDate.now();
            
            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String customerName = rs.getString("customer_name");
                String description = rs.getString("description");
                Date dueDateSql = rs.getDate("due_date");
                LocalDate dueDate = dueDateSql != null ? dueDateSql.toLocalDate() : null;
                String status = rs.getString("status");
                
                long daysUntilDue = dueDate != null ? ChronoUnit.DAYS.between(today, dueDate) : 0;
                
                jobs.add(new JobDueSoonReport(jobId, customerName, description, 
                                               dueDate, status, daysUntilDue));
            }
            
            logger.info("Found " + jobs.size() + " jobs due in next 7 days");
            
        } catch (SQLException e) {
            logger.severe("Error fetching jobs due soon: " + e.getMessage());
            e.printStackTrace();
        }
        
        return jobs;
    }
    
    /**
     * Represents top customer by revenue
     */
    public static class TopCustomerReport {
        private int customerId;
        private String customerName;
        private String phone;
        private String email;
        private int jobCount;
        private BigDecimal totalRevenue;
        
        public TopCustomerReport(int customerId, String customerName, String phone, 
                                String email, int jobCount, BigDecimal totalRevenue) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.phone = phone;
            this.email = email;
            this.jobCount = jobCount;
            this.totalRevenue = totalRevenue;
        }
        
        public int getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public int getJobCount() { return jobCount; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
    }
    
    /**
     * Get top customers by revenue in the past quarter (90 days)
     */
    public List<TopCustomerReport> getTopCustomersByRevenue() {
        List<TopCustomerReport> customers = new ArrayList<>();
        
        String sql = "SELECT c.customer_id, c.name, c.phone, c.email, " +
                     "COUNT(DISTINCT j.job_id) AS job_count, " +
                     "COALESCE(SUM(i.total_amount), 0) AS total_revenue " +
                     "FROM Customer c " +
                     "INNER JOIN Job j ON c.customer_id = j.customer_id " +
                     "LEFT JOIN Invoice i ON j.job_id = i.job_id " +
                     "WHERE i.invoice_date >= DATE_SUB(CURDATE(), INTERVAL 90 DAY) " +
                     "GROUP BY c.customer_id, c.name, c.phone, c.email " +
                     "HAVING total_revenue > 0 " +
                     "ORDER BY total_revenue DESC, c.name ASC " +
                     "LIMIT 20";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String customerName = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                int jobCount = rs.getInt("job_count");
                BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                
                customers.add(new TopCustomerReport(customerId, customerName, phone, 
                                                    email, jobCount, totalRevenue));
            }
            
            logger.info("Found " + customers.size() + " top customers by revenue");
            
        } catch (SQLException e) {
            logger.severe("Error fetching top customers by revenue: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customers;
    }
    
    /**
     * Represents material shortage information
     */
    public static class MaterialShortageReport {
        private int materialId;
        private String materialName;
        private String category;
        private int stockQuantity;
        private int totalRequired;
        private int shortageAmount;
        private int activeJobsAffected;
        
        public MaterialShortageReport(int materialId, String materialName, String category,
                                     int stockQuantity, int totalRequired, int shortageAmount,
                                     int activeJobsAffected) {
            this.materialId = materialId;
            this.materialName = materialName;
            this.category = category;
            this.stockQuantity = stockQuantity;
            this.totalRequired = totalRequired;
            this.shortageAmount = shortageAmount;
            this.activeJobsAffected = activeJobsAffected;
        }
        
        public int getMaterialId() { return materialId; }
        public String getMaterialName() { return materialName; }
        public String getCategory() { return category; }
        public int getStockQuantity() { return stockQuantity; }
        public int getTotalRequired() { return totalRequired; }
        public int getShortageAmount() { return shortageAmount; }
        public int getActiveJobsAffected() { return activeJobsAffected; }
    }
    
    /**
     * Get materials with shortages across active jobs
     * Returns materials where total quantity needed for active jobs exceeds current stock
     */
    public List<MaterialShortageReport> getMaterialShortages() {
        List<MaterialShortageReport> shortages = new ArrayList<>();
        
        String sql = "SELECT m.material_id, m.name, m.category, m.stock_quantity, " +
                     "COALESCE(SUM(jm.quantity_used), 0) AS total_required, " +
                     "COUNT(DISTINCT j.job_id) AS active_jobs_affected, " +
                     "(COALESCE(SUM(jm.quantity_used), 0) - m.stock_quantity) AS shortage_amount " +
                     "FROM Material m " +
                     "INNER JOIN JobMaterial jm ON m.material_id = jm.material_id " +
                     "INNER JOIN Job j ON jm.job_id = j.job_id " +
                     "WHERE j.status IN ('Planned', 'InProgress') " +
                     "GROUP BY m.material_id, m.name, m.category, m.stock_quantity " +
                     "HAVING shortage_amount > 0 " +
                     "ORDER BY shortage_amount DESC, m.name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int materialId = rs.getInt("material_id");
                String materialName = rs.getString("name");
                String category = rs.getString("category");
                int stockQuantity = rs.getInt("stock_quantity");
                int totalRequired = rs.getInt("total_required");
                int activeJobsAffected = rs.getInt("active_jobs_affected");
                int shortageAmount = rs.getInt("shortage_amount");
                
                shortages.add(new MaterialShortageReport(materialId, materialName, category,
                                                         stockQuantity, totalRequired, 
                                                         shortageAmount, activeJobsAffected));
            }
            
            logger.info("Found " + shortages.size() + " materials with shortages");
            
        } catch (SQLException e) {
            logger.severe("Error fetching material shortages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return shortages;
    }
    
    /**
     * Represents employee labor hours and pay report
     */
    public static class EmployeeLaborReport {
        private int employeeId;
        private String employeeName;
        private String role;
        private BigDecimal hourlyRate;
        private BigDecimal totalHours;
        private BigDecimal totalPay;
        private int jobCount;
        
        public EmployeeLaborReport(int employeeId, String employeeName, String role,
                                   BigDecimal hourlyRate, BigDecimal totalHours, 
                                   BigDecimal totalPay, int jobCount) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
            this.role = role;
            this.hourlyRate = hourlyRate;
            this.totalHours = totalHours;
            this.totalPay = totalPay;
            this.jobCount = jobCount;
        }
        
        public int getEmployeeId() { return employeeId; }
        public String getEmployeeName() { return employeeName; }
        public String getRole() { return role; }
        public BigDecimal getHourlyRate() { return hourlyRate; }
        public BigDecimal getTotalHours() { return totalHours; }
        public BigDecimal getTotalPay() { return totalPay; }
        public int getJobCount() { return jobCount; }
    }
    
    /**
     * Get employee labor hours and calculated pay
     * Shows total hours worked, hourly rate, calculated pay, and number of jobs worked on
     */
    public List<EmployeeLaborReport> getEmployeeLaborReport() {
        List<EmployeeLaborReport> reports = new ArrayList<>();
        
        String sql = "SELECT e.employee_id, e.name, e.role, e.hourly_rate, " +
                     "COALESCE(SUM(w.hours_worked), 0) AS total_hours, " +
                     "COALESCE(SUM(w.hours_worked * e.hourly_rate), 0) AS total_pay, " +
                     "COUNT(DISTINCT w.job_id) AS job_count " +
                     "FROM Employee e " +
                     "LEFT JOIN WorkLog w ON e.employee_id = w.employee_id " +
                     "GROUP BY e.employee_id, e.name, e.role, e.hourly_rate " +
                     "ORDER BY total_hours DESC, e.name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String employeeName = rs.getString("name");
                String role = rs.getString("role");
                BigDecimal hourlyRate = rs.getBigDecimal("hourly_rate");
                BigDecimal totalHours = rs.getBigDecimal("total_hours");
                BigDecimal totalPay = rs.getBigDecimal("total_pay");
                int jobCount = rs.getInt("job_count");
                
                reports.add(new EmployeeLaborReport(employeeId, employeeName, role,
                                                    hourlyRate, totalHours, totalPay, jobCount));
            }
            
            logger.info("Generated labor report for " + reports.size() + " employees");
            
        } catch (SQLException e) {
            logger.severe("Error generating employee labor report: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reports;
    }
    
    /**
     * Get employee labor report for a specific employee
     */
    public EmployeeLaborReport getEmployeeLaborReport(int employeeId) {
        String sql = "SELECT e.employee_id, e.name, e.role, e.hourly_rate, " +
                     "COALESCE(SUM(w.hours_worked), 0) AS total_hours, " +
                     "COALESCE(SUM(w.hours_worked * e.hourly_rate), 0) AS total_pay, " +
                     "COUNT(DISTINCT w.job_id) AS job_count " +
                     "FROM Employee e " +
                     "LEFT JOIN WorkLog w ON e.employee_id = w.employee_id " +
                     "WHERE e.employee_id = ? " +
                     "GROUP BY e.employee_id, e.name, e.role, e.hourly_rate";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EmployeeLaborReport(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getBigDecimal("hourly_rate"),
                        rs.getBigDecimal("total_hours"),
                        rs.getBigDecimal("total_pay"),
                        rs.getInt("job_count")
                    );
                }
            }
            
        } catch (SQLException e) {
            logger.severe("Error generating labor report for employee " + employeeId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Represents unpaid invoice aging report
     */
    public static class UnpaidInvoiceReport {
        private int invoiceId;
        private int jobId;
        private String customerName;
        private String jobDescription;
        private LocalDate invoiceDate;
        private BigDecimal totalAmount;
        private long daysOutstanding;
        
        public UnpaidInvoiceReport(int invoiceId, int jobId, String customerName,
                                   String jobDescription, LocalDate invoiceDate,
                                   BigDecimal totalAmount, long daysOutstanding) {
            this.invoiceId = invoiceId;
            this.jobId = jobId;
            this.customerName = customerName;
            this.jobDescription = jobDescription;
            this.invoiceDate = invoiceDate;
            this.totalAmount = totalAmount;
            this.daysOutstanding = daysOutstanding;
        }
        
        public int getInvoiceId() { return invoiceId; }
        public int getJobId() { return jobId; }
        public String getCustomerName() { return customerName; }
        public String getJobDescription() { return jobDescription; }
        public LocalDate getInvoiceDate() { return invoiceDate; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public long getDaysOutstanding() { return daysOutstanding; }
    }
    
    /**
     * Get unpaid invoices older than 30 days
     */
    public List<UnpaidInvoiceReport> getUnpaidInvoicesOlderThan30Days() {
        List<UnpaidInvoiceReport> invoices = new ArrayList<>();
        
        String sql = "SELECT i.invoice_id, i.job_id, c.name AS customer_name, " +
                     "j.description AS job_description, i.invoice_date, i.total_amount, " +
                     "DATEDIFF(CURDATE(), i.invoice_date) AS days_outstanding " +
                     "FROM Invoice i " +
                     "JOIN Job j ON i.job_id = j.job_id " +
                     "JOIN Customer c ON j.customer_id = c.customer_id " +
                     "WHERE i.paid = FALSE " +
                     "AND DATEDIFF(CURDATE(), i.invoice_date) > 30 " +
                     "ORDER BY days_outstanding DESC, i.invoice_date ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int invoiceId = rs.getInt("invoice_id");
                int jobId = rs.getInt("job_id");
                String customerName = rs.getString("customer_name");
                String jobDescription = rs.getString("job_description");
                Date invoiceDateSql = rs.getDate("invoice_date");
                LocalDate invoiceDate = invoiceDateSql != null ? invoiceDateSql.toLocalDate() : null;
                BigDecimal totalAmount = rs.getBigDecimal("total_amount");
                long daysOutstanding = rs.getLong("days_outstanding");
                
                invoices.add(new UnpaidInvoiceReport(invoiceId, jobId, customerName,
                                                      jobDescription, invoiceDate,
                                                      totalAmount, daysOutstanding));
            }
            
            logger.info("Found " + invoices.size() + " unpaid invoices older than 30 days");
            
        } catch (SQLException e) {
            logger.severe("Error fetching unpaid invoices: " + e.getMessage());
            e.printStackTrace();
        }
        
        return invoices;
    }
    
    /**
     * Represents vendor spending summary by month
     */
    public static class VendorSpendingReport {
        private int vendorId;
        private String vendorName;
        private String vendorContact;
        private int year;
        private int month;
        private String monthName;
        private BigDecimal totalSpending;
        private int purchaseOrderCount;
        
        public VendorSpendingReport(int vendorId, String vendorName, String vendorContact,
                                    int year, int month, String monthName,
                                    BigDecimal totalSpending, int purchaseOrderCount) {
            this.vendorId = vendorId;
            this.vendorName = vendorName;
            this.vendorContact = vendorContact;
            this.year = year;
            this.month = month;
            this.monthName = monthName;
            this.totalSpending = totalSpending;
            this.purchaseOrderCount = purchaseOrderCount;
        }
        
        public int getVendorId() { return vendorId; }
        public String getVendorName() { return vendorName; }
        public String getVendorContact() { return vendorContact; }
        public int getYear() { return year; }
        public int getMonth() { return month; }
        public String getMonthName() { return monthName; }
        public BigDecimal getTotalSpending() { return totalSpending; }
        public int getPurchaseOrderCount() { return purchaseOrderCount; }
    }
    
    /**
     * Get vendor spending summarized by month
     */
    public List<VendorSpendingReport> getVendorSpendingByMonth() {
        List<VendorSpendingReport> spending = new ArrayList<>();
        
        String sql = "SELECT v.vendor_id, v.name AS vendor_name, v.contact_info, " +
                     "YEAR(po.order_date) AS year, MONTH(po.order_date) AS month, " +
                     "DATE_FORMAT(po.order_date, '%M') AS month_name, " +
                     "SUM(po.total_cost) AS total_spending, " +
                     "COUNT(po.po_id) AS purchase_order_count " +
                     "FROM Vendor v " +
                     "JOIN PurchaseOrder po ON v.vendor_id = po.vendor_id " +
                     "WHERE po.status != 'Cancelled' " +
                     "GROUP BY v.vendor_id, v.name, v.contact_info, YEAR(po.order_date), MONTH(po.order_date) " +
                     "ORDER BY year DESC, month DESC, total_spending DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int vendorId = rs.getInt("vendor_id");
                String vendorName = rs.getString("vendor_name");
                String vendorContact = rs.getString("contact_info");
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                String monthName = rs.getString("month_name");
                BigDecimal totalSpending = rs.getBigDecimal("total_spending");
                int purchaseOrderCount = rs.getInt("purchase_order_count");
                
                spending.add(new VendorSpendingReport(vendorId, vendorName, vendorContact,
                                                       year, month, monthName,
                                                       totalSpending, purchaseOrderCount));
            }
            
            logger.info("Found " + spending.size() + " vendor spending records by month");
            
        } catch (SQLException e) {
            logger.severe("Error fetching vendor spending by month: " + e.getMessage());
            e.printStackTrace();
        }
        
        return spending;
    }
    
    /**
     * Represents comprehensive cost comparison (estimated vs actual) for a job, including labor and materials
     */
    public static class JobCostComparisonReport {
        private int jobId;
        private String customerName;
        private String description;
        private String status;
        private BigDecimal estimatedLaborCost;
        private BigDecimal actualLaborCost;
        private BigDecimal laborVariance;
        private double laborVariancePercent;
        private BigDecimal estimatedMaterialCost;
        private BigDecimal actualMaterialCost;
        private BigDecimal materialVariance;
        private double materialVariancePercent;
        private BigDecimal estimatedTotalCost;
        private BigDecimal actualTotalCost;
        private BigDecimal totalVariance;
        private double totalVariancePercent;
        
        public JobCostComparisonReport(int jobId, String customerName, String description, String status,
                                       BigDecimal estimatedLaborCost, BigDecimal actualLaborCost,
                                       BigDecimal laborVariance, double laborVariancePercent,
                                       BigDecimal estimatedMaterialCost, BigDecimal actualMaterialCost,
                                       BigDecimal materialVariance, double materialVariancePercent,
                                       BigDecimal estimatedTotalCost, BigDecimal actualTotalCost,
                                       BigDecimal totalVariance, double totalVariancePercent) {
            this.jobId = jobId;
            this.customerName = customerName;
            this.description = description;
            this.status = status;
            this.estimatedLaborCost = estimatedLaborCost;
            this.actualLaborCost = actualLaborCost;
            this.laborVariance = laborVariance;
            this.laborVariancePercent = laborVariancePercent;
            this.estimatedMaterialCost = estimatedMaterialCost;
            this.actualMaterialCost = actualMaterialCost;
            this.materialVariance = materialVariance;
            this.materialVariancePercent = materialVariancePercent;
            this.estimatedTotalCost = estimatedTotalCost;
            this.actualTotalCost = actualTotalCost;
            this.totalVariance = totalVariance;
            this.totalVariancePercent = totalVariancePercent;
        }
        
        public int getJobId() { return jobId; }
        public String getCustomerName() { return customerName; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
        public BigDecimal getEstimatedLaborCost() { return estimatedLaborCost; }
        public BigDecimal getActualLaborCost() { return actualLaborCost; }
        public BigDecimal getLaborVariance() { return laborVariance; }
        public double getLaborVariancePercent() { return laborVariancePercent; }
        public BigDecimal getEstimatedMaterialCost() { return estimatedMaterialCost; }
        public BigDecimal getActualMaterialCost() { return actualMaterialCost; }
        public BigDecimal getMaterialVariance() { return materialVariance; }
        public double getMaterialVariancePercent() { return materialVariancePercent; }
        public BigDecimal getEstimatedTotalCost() { return estimatedTotalCost; }
        public BigDecimal getActualTotalCost() { return actualTotalCost; }
        public BigDecimal getTotalVariance() { return totalVariance; }
        public double getTotalVariancePercent() { return totalVariancePercent; }
    }
    
    /**
     * Compare estimated vs actual costs per job (labor + materials + total).
     * Calculates variance and percentage difference for each category.
     * Only includes jobs that have either estimated or actual costs.
     */
    public List<JobCostComparisonReport> getJobCostComparison() {
        List<JobCostComparisonReport> comparisons = new ArrayList<>();
        
        String sql = 
            "SELECT " +
            "    j.job_id, " +
            "    c.name AS customer_name, " +
            "    j.description, " +
            "    j.status, " +
            "    COALESCE(j.estimated_labor_cost, 0) AS estimated_labor_cost, " +
            "    COALESCE(j.estimated_material_cost, 0) AS estimated_material_cost, " +
            "    COALESCE(SUM(w.hours_worked * e.hourly_rate), 0) AS actual_labor_cost, " +
            "    COALESCE(SUM(jm.quantity_used * m.unit_cost), 0) AS actual_material_cost " +
            "FROM Job j " +
            "INNER JOIN Customer c ON j.customer_id = c.customer_id " +
            "LEFT JOIN WorkLog w ON j.job_id = w.job_id " +
            "LEFT JOIN Employee e ON w.employee_id = e.employee_id " +
            "LEFT JOIN JobMaterial jm ON j.job_id = jm.job_id " +
            "LEFT JOIN Material m ON jm.material_id = m.material_id " +
            "GROUP BY j.job_id, c.name, j.description, j.status, j.estimated_labor_cost, j.estimated_material_cost " +
            "HAVING (j.estimated_labor_cost > 0 OR actual_labor_cost > 0 OR j.estimated_material_cost > 0 OR actual_material_cost > 0) " +
            "ORDER BY j.job_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String customerName = rs.getString("customer_name");
                String description = rs.getString("description");
                String status = rs.getString("status");
                
                BigDecimal estimatedLabor = rs.getBigDecimal("estimated_labor_cost");
                BigDecimal estimatedMaterial = rs.getBigDecimal("estimated_material_cost");
                BigDecimal actualLabor = rs.getBigDecimal("actual_labor_cost");
                BigDecimal actualMaterial = rs.getBigDecimal("actual_material_cost");
                
                // Calculate labor variance
                BigDecimal laborVariance = actualLabor.subtract(estimatedLabor);
                double laborVariancePercent = 0.0;
                if (estimatedLabor.compareTo(BigDecimal.ZERO) > 0) {
                    laborVariancePercent = laborVariance.divide(estimatedLabor, 4, RoundingMode.HALF_UP)
                                                       .multiply(new BigDecimal(100))
                                                       .doubleValue();
                }
                
                // Calculate material variance
                BigDecimal materialVariance = actualMaterial.subtract(estimatedMaterial);
                double materialVariancePercent = 0.0;
                if (estimatedMaterial.compareTo(BigDecimal.ZERO) > 0) {
                    materialVariancePercent = materialVariance.divide(estimatedMaterial, 4, RoundingMode.HALF_UP)
                                                             .multiply(new BigDecimal(100))
                                                             .doubleValue();
                }
                
                // Calculate total costs
                BigDecimal estimatedTotal = estimatedLabor.add(estimatedMaterial);
                BigDecimal actualTotal = actualLabor.add(actualMaterial);
                BigDecimal totalVariance = actualTotal.subtract(estimatedTotal);
                double totalVariancePercent = 0.0;
                if (estimatedTotal.compareTo(BigDecimal.ZERO) > 0) {
                    totalVariancePercent = totalVariance.divide(estimatedTotal, 4, RoundingMode.HALF_UP)
                                                       .multiply(new BigDecimal(100))
                                                       .doubleValue();
                }
                
                comparisons.add(new JobCostComparisonReport(
                    jobId, customerName, description, status,
                    estimatedLabor, actualLabor, laborVariance, laborVariancePercent,
                    estimatedMaterial, actualMaterial, materialVariance, materialVariancePercent,
                    estimatedTotal, actualTotal, totalVariance, totalVariancePercent
                ));
            }
            
            logger.info("Generated job cost comparison for " + comparisons.size() + " jobs");
            
        } catch (SQLException e) {
            logger.severe("Error generating job cost comparison: " + e.getMessage());
            e.printStackTrace();
        }
        
        return comparisons;
    }
    
    /**
     * Represents labor cost comparison (estimated vs actual) for a job
     * @deprecated Use JobCostComparisonReport instead for comprehensive cost tracking
     */
    @Deprecated
    public static class LaborCostComparisonReport {
        private int jobId;
        private String customerName;
        private String description;
        private BigDecimal estimatedLaborCost;
        private BigDecimal actualLaborCost;
        private BigDecimal variance;
        private double variancePercent;
        private String status;
        
        public LaborCostComparisonReport(int jobId, String customerName, String description,
                                         BigDecimal estimatedLaborCost, BigDecimal actualLaborCost,
                                         BigDecimal variance, double variancePercent, String status) {
            this.jobId = jobId;
            this.customerName = customerName;
            this.description = description;
            this.estimatedLaborCost = estimatedLaborCost;
            this.actualLaborCost = actualLaborCost;
            this.variance = variance;
            this.variancePercent = variancePercent;
            this.status = status;
        }
        
        public int getJobId() { return jobId; }
        public String getCustomerName() { return customerName; }
        public String getDescription() { return description; }
        public BigDecimal getEstimatedLaborCost() { return estimatedLaborCost; }
        public BigDecimal getActualLaborCost() { return actualLaborCost; }
        public BigDecimal getVariance() { return variance; }
        public double getVariancePercent() { return variancePercent; }
        public String getStatus() { return status; }
    }
    
    /**
     * Compare estimated vs actual labor costs per job.
     * Calculates variance and percentage difference.
     * Only includes jobs that have either estimated or actual labor costs.
     * @deprecated Use getJobCostComparison() instead for comprehensive cost tracking
     */
    @Deprecated
    public List<LaborCostComparisonReport> getLaborCostComparison() {
        List<LaborCostComparisonReport> comparisons = new ArrayList<>();
        
        String sql = 
            "SELECT " +
            "    j.job_id, " +
            "    c.name AS customer_name, " +
            "    j.description, " +
            "    j.status, " +
            "    COALESCE(j.estimated_labor_cost, 0) AS estimated_labor_cost, " +
            "    COALESCE(SUM(w.hours_worked * e.hourly_rate), 0) AS actual_labor_cost " +
            "FROM Job j " +
            "INNER JOIN Customer c ON j.customer_id = c.customer_id " +
            "LEFT JOIN WorkLog w ON j.job_id = w.job_id " +
            "LEFT JOIN Employee e ON w.employee_id = e.employee_id " +
            "GROUP BY j.job_id, c.name, j.description, j.status, j.estimated_labor_cost " +
            "HAVING estimated_labor_cost > 0 OR actual_labor_cost > 0 " +
            "ORDER BY j.job_id";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int jobId = rs.getInt("job_id");
                String customerName = rs.getString("customer_name");
                String description = rs.getString("description");
                String status = rs.getString("status");
                BigDecimal estimatedCost = rs.getBigDecimal("estimated_labor_cost");
                BigDecimal actualCost = rs.getBigDecimal("actual_labor_cost");
                
                // Calculate variance (actual - estimated)
                BigDecimal variance = actualCost.subtract(estimatedCost);
                
                // Calculate variance percentage
                double variancePercent = 0.0;
                if (estimatedCost.compareTo(BigDecimal.ZERO) > 0) {
                    variancePercent = variance.divide(estimatedCost, 4, RoundingMode.HALF_UP)
                                              .multiply(new BigDecimal(100))
                                              .doubleValue();
                }
                
                comparisons.add(new LaborCostComparisonReport(
                    jobId, customerName, description, estimatedCost, actualCost,
                    variance, variancePercent, status
                ));
            }
            
            logger.info("Generated labor cost comparison for " + comparisons.size() + " jobs");
            
        } catch (SQLException e) {
            logger.severe("Error generating labor cost comparison: " + e.getMessage());
            e.printStackTrace();
        }
        
        return comparisons;
    }
}
