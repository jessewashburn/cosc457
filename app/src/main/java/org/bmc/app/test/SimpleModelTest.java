package org.bmc.app.test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;

import org.bmc.app.model.Customer;
import org.bmc.app.model.Employee;
import org.bmc.app.model.Invoice;
import org.bmc.app.model.Job;

/**
 * Simple test class to demonstrate model object creation and business logic
 */
public class SimpleModelTest {
    
    private static final Logger LOGGER = Logger.getLogger(SimpleModelTest.class.getName());
    
    public static void main(String[] args) {
        LOGGER.info("=== Simple Model Test - Baltimore Metal Crafters ===");
        
        // Test 1: Customer Creation and Validation
        testCustomerModel();
        
        // Test 2: Employee Creation and Business Logic
        testEmployeeModel();
        
        // Test 3: Job Creation and Status Management
        testJobModel();
        
        // Test 4: Invoice Creation and Aging Logic
        testInvoiceModel();
        
        // Test 5: Model Interactions
        testModelInteractions();
        
        LOGGER.info("=== Simple Model Test Completed Successfully ===");
    }
    
    private static void testCustomerModel() {
        LOGGER.info("--- Testing Customer Model ---");
        
        // Valid customer
        Customer customer1 = new Customer(null, "Baltimore Ironworks", "John Doe", "410-555-1234", 
            "contact@baltironworks.com", "123 Harbor Blvd, Baltimore, MD");
        
        LOGGER.info(() -> String.format("Created customer: %s", customer1.getDisplayName()));
        LOGGER.info(() -> String.format("Customer is valid: %b", customer1.isValid()));
        
        // Invalid customer (no name)
        Customer customer2 = new Customer(null, "", "Jane Smith", "410-555-5678", "test@test.com", "456 Main St");
        LOGGER.info(() -> String.format("Invalid customer is valid: %b", customer2.isValid()));
    }
    
    private static void testEmployeeModel() {
        LOGGER.info("--- Testing Employee Model ---");
        
        // Create employees with different roles
        Employee restorer = new Employee(null, "John Smith", Employee.Role.RESTORER, 
            "Historic Ironwork", "john.smith@bmc.com", new java.math.BigDecimal("75.00"));
        Employee fabricator = new Employee(null, "Mary Johnson", Employee.Role.FABRICATOR, 
            "Welding", "mary.johnson@bmc.com", new java.math.BigDecimal("65.00"));
        Employee admin = new Employee(null, "Bob Wilson", Employee.Role.ADMIN, 
            "Project Management", "bob.wilson@bmc.com", new java.math.BigDecimal("85.00"));
        
        LOGGER.info(() -> String.format("Restorer: %s, Is Craftsperson: %b", 
            restorer.getDisplayName(), restorer.isCraftsperson()));
        LOGGER.info(() -> String.format("Fabricator: %s, Is Craftsperson: %b", 
            fabricator.getDisplayName(), fabricator.isCraftsperson()));
        LOGGER.info(() -> String.format("Admin: %s, Is Craftsperson: %b", 
            admin.getDisplayName(), admin.isCraftsperson()));
    }
    
    private static void testJobModel() {
        LOGGER.info("--- Testing Job Model ---");
        
        // Create job
        Job job = new Job();
        job.setCustomerId(1);
        job.setDescription("Restore Victorian iron fence gates");
        job.setStartDate(LocalDate.now().minusDays(5));
        job.setDueDate(LocalDate.now().plusDays(10));
        job.setStatus(Job.Status.PLANNED);
        
        LOGGER.info(() -> String.format("Job: %s", job.getDisplayName()));
        LOGGER.info(() -> String.format("Status: %s", job.getStatusDisplay()));
        LOGGER.info(() -> String.format("Is Active: %b", job.isActive()));
        LOGGER.info(() -> String.format("Is Overdue: %b", job.isOverdue()));
        
        Long daysUntilDue = job.getDaysUntilDue();
        LOGGER.info(() -> String.format("Days until due: %d", daysUntilDue != null ? daysUntilDue : 0));
        
        // Test status progression
        job.setStatus(Job.Status.IN_PROGRESS);
        LOGGER.info(() -> String.format("Status updated to: %s", job.getStatusDisplay()));
        
        job.setStatus(Job.Status.COMPLETED);
        LOGGER.info(() -> String.format("Final status: %s", job.getStatusDisplay()));
        LOGGER.info(() -> String.format("Still active after completion: %b", job.isActive()));
    }
    
    private static void testInvoiceModel() {
        LOGGER.info("--- Testing Invoice Model ---");
        
        // Current invoice
        Invoice currentInvoice = new Invoice();
        currentInvoice.setJobId(1);
        currentInvoice.setInvoiceDate(LocalDate.now().minusDays(15));
        currentInvoice.setTotalAmount(new BigDecimal("2500.00"));
        currentInvoice.setPaid(false);
        
        LOGGER.info(() -> String.format("Invoice: %s", currentInvoice.getDisplayName()));
        
        Long daysSince = currentInvoice.getDaysSinceInvoice();
        String agingCategory = currentInvoice.getAgingCategory();
        
        LOGGER.info(() -> String.format("Days since invoice: %d", daysSince != null ? daysSince : 0));
        LOGGER.info(() -> String.format("Aging category: %s", agingCategory));
        
        // Old invoice
        Invoice oldInvoice = new Invoice();
        oldInvoice.setInvoiceDate(LocalDate.now().minusDays(45));
        oldInvoice.setTotalAmount(new BigDecimal("1200.00"));
        oldInvoice.setPaid(false);
        
        Long oldDaysSince = oldInvoice.getDaysSinceInvoice();
        String oldAgingCategory = oldInvoice.getAgingCategory();
        
        LOGGER.info(() -> String.format("Old invoice days: %d, Category: %s", 
            oldDaysSince != null ? oldDaysSince : 0, oldAgingCategory));
        
        // Paid invoice
        Invoice paidInvoice = new Invoice();
        paidInvoice.setPaid(true);
        LOGGER.info(() -> String.format("Paid invoice category: %s", paidInvoice.getAgingCategory()));
    }
    
    private static void testModelInteractions() {
        LOGGER.info("--- Testing Model Interactions ---");
        
        // Create related objects
        Customer customer = new Customer(100, "Heritage Restoration Inc", "Michael Heritage", "410-555-9999", 
            "info@heritage.com", "789 Charles St, Baltimore, MD");
        
        Job job = new Job();
        job.setJobId(200);
        job.setCustomerId(customer.getCustomerId());
        job.setDescription("Restore courthouse railings");
        job.setStartDate(LocalDate.now().minusDays(30));
        job.setDueDate(LocalDate.now().minusDays(5)); // Overdue
        job.setStatus(Job.Status.IN_PROGRESS);
        
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(300);
        invoice.setJobId(job.getJobId());
        invoice.setInvoiceDate(LocalDate.now().minusDays(20));
        invoice.setTotalAmount(new BigDecimal("5500.00"));
        invoice.setPaid(false);
        invoice.setCustomerName(customer.getName()); // Simulated join
        invoice.setJobDescription(job.getDescription()); // Simulated join
        
        Employee assignedEmployee = new Employee(400, "Alice Cooper", Employee.Role.RESTORER, 
            "Restoration", "alice.cooper@bmc.com", new java.math.BigDecimal("75.00"));
        
        // Display relationships
        LOGGER.info(() -> String.format("Customer: %s", customer.getDisplayName()));
        LOGGER.info(() -> String.format("  Job: %s (Status: %s, Overdue: %b)", 
            job.getDisplayName(), job.getStatusDisplay(), job.isOverdue()));
        LOGGER.info(() -> String.format("  Invoice: %s (Age: %s)", 
            invoice.getDisplayName(), invoice.getAgingCategory()));
        LOGGER.info(() -> String.format("  Assigned Employee: %s (Type: %s)", 
            assignedEmployee.getDisplayName(), 
            assignedEmployee.isCraftsperson() ? "Craftsperson" : "Administrative"));
        
        // Business logic demonstration
        if (job.isOverdue() && !invoice.getPaid()) {
            Long daysOverdue = job.getDaysUntilDue();
            Long invoiceAge = invoice.getDaysSinceInvoice();
            
            LOGGER.info(() -> String.format("ALERT: Job is %d days overdue with unpaid invoice (%d days old)", 
                daysOverdue != null ? Math.abs(daysOverdue) : 0,
                invoiceAge != null ? invoiceAge : 0));
        }
    }
}