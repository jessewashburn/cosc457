package org.bmc.app.test;

import java.util.List;
import java.util.logging.Logger;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.dao.EmployeeDAO;
import org.bmc.app.dao.InvoiceDAO;
import org.bmc.app.dao.JobDAO;
import org.bmc.app.model.Customer;
import org.bmc.app.model.Employee;
import org.bmc.app.model.Invoice;
import org.bmc.app.model.Job;
import org.bmc.app.util.DBConnection;

/**
 * Comprehensive integration test for all DAO classes against real database
 */
public class FullDAOIntegrationTest {
    
    private static final Logger LOGGER = Logger.getLogger(FullDAOIntegrationTest.class.getName());
    
    public static void main(String[] args) {
        LOGGER.info("=== Full DAO Integration Test - Real Database ===");
        
        // Test database connection first
        if (!testDatabaseConnection()) {
            LOGGER.severe("Database connection failed - cannot proceed with DAO tests");
            return;
        }
        
        // Run comprehensive DAO tests
        boolean allTestsPassed = true;
        
        allTestsPassed &= testCustomerDAO();
        allTestsPassed &= testEmployeeDAO();
        allTestsPassed &= testJobDAO();
        allTestsPassed &= testInvoiceDAO();
        allTestsPassed &= testCrossDAORelationships();
        
        if (allTestsPassed) {
            LOGGER.info("=== ALL DAO INTEGRATION TESTS PASSED ===");
        } else {
            LOGGER.severe("=== SOME TESTS FAILED ===");
        }
    }
    
    private static boolean testDatabaseConnection() {
        LOGGER.info("--- Testing Database Connection ---");
        try {
            boolean connected = DBConnection.testConnection();
            if (connected) {
                LOGGER.info(() -> String.format("✓ Connected to: %s", DBConnection.getDatabaseUrl()));
                return true;
            } else {
                LOGGER.severe("✗ Database connection failed");
                return false;
            }
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("✗ Connection error: %s", e.getMessage()));
            return false;
        }
    }
    
    private static boolean testCustomerDAO() {
        LOGGER.info("--- Testing CustomerDAO ---");
        CustomerDAO dao = new CustomerDAO();
        
        try {
            // Test findAll
            List<Customer> customers = dao.findAll();
            LOGGER.info(() -> String.format("✓ Found %d customers", customers.size()));
            
            // Test search
            List<Customer> baltimoreCustomers = dao.searchByName("Baltimore");
            LOGGER.info(() -> String.format("✓ Search found %d Baltimore customers", baltimoreCustomers.size()));
            
            // Test create, update, delete cycle
            Customer testCustomer = new Customer(null, "Test Integration Customer", 
                "555-TEST", "test@integration.com", "123 Test Street");
            
            Customer created = dao.create(testCustomer);
            if (created != null && created.getCustomerId() != null) {
                LOGGER.info(() -> String.format("✓ Created customer ID: %d", created.getCustomerId()));
                
                // Test findById
                Customer found = dao.findById(created.getCustomerId());
                if (found != null) {
                    LOGGER.info("✓ Found customer by ID");
                    
                    // Test update
                    found.setPhone("555-UPDATED");
                    boolean updated = dao.update(found);
                    if (updated) {
                        LOGGER.info("✓ Updated customer successfully");
                        
                        // Cleanup
                        boolean deleted = dao.delete(found.getCustomerId());
                        if (deleted) {
                            LOGGER.info("✓ Deleted test customer");
                        } else {
                            LOGGER.warning("✗ Failed to delete test customer");
                        }
                    } else {
                        LOGGER.warning("✗ Failed to update customer");
                    }
                } else {
                    LOGGER.warning("✗ Failed to find customer by ID");
                }
            } else {
                LOGGER.warning("✗ Failed to create customer");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("✗ CustomerDAO test failed: %s", e.getMessage()));
            return false;
        }
    }
    
    private static boolean testEmployeeDAO() {
        LOGGER.info("--- Testing EmployeeDAO ---");
        EmployeeDAO dao = new EmployeeDAO();
        
        try {
            // Test findAll
            List<Employee> employees = dao.findAll();
            LOGGER.info(() -> String.format("✓ Found %d employees", employees.size()));
            
            // Test findCraftspeople
            List<Employee> craftspeople = dao.findCraftspeople();
            LOGGER.info(() -> String.format("✓ Found %d craftspeople", craftspeople.size()));
            
            // Test create employee
            Employee testEmployee = new Employee(null, "Test Integration Employee", 
                Employee.Role.RESTORER, "Test Specialization", "test.employee@bmc.com", 
                new java.math.BigDecimal("75.00"));
            
            Employee created = dao.create(testEmployee);
            if (created != null && created.getEmployeeId() != null) {
                LOGGER.info(() -> String.format("✓ Created employee ID: %d", created.getEmployeeId()));
                
                // Cleanup
                boolean deleted = dao.delete(created.getEmployeeId());
                if (deleted) {
                    LOGGER.info("✓ Deleted test employee");
                } else {
                    LOGGER.warning("✗ Failed to delete test employee");
                }
            } else {
                LOGGER.warning("✗ Failed to create employee");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("✗ EmployeeDAO test failed: %s", e.getMessage()));
            return false;
        }
    }
    
    private static boolean testJobDAO() {
        LOGGER.info("--- Testing JobDAO ---");
        JobDAO dao = new JobDAO();
        
        try {
            // Test findAll
            List<Job> jobs = dao.findAll();
            LOGGER.info(() -> String.format("✓ Found %d jobs", jobs.size()));
            
            // Test findByStatus
            List<Job> activeJobs = dao.findByStatus(Job.Status.IN_PROGRESS);
            LOGGER.info(() -> String.format("✓ Found %d in-progress jobs", activeJobs.size()));
            
            // Test with existing customer (assuming ID 1 exists from seed data)
            List<Job> customerJobs = dao.findByCustomer(1);
            LOGGER.info(() -> String.format("✓ Found %d jobs for customer 1", customerJobs.size()));
            
            return true;
            
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("✗ JobDAO test failed: %s", e.getMessage()));
            return false;
        }
    }
    
    private static boolean testInvoiceDAO() {
        LOGGER.info("--- Testing InvoiceDAO ---");
        InvoiceDAO dao = new InvoiceDAO();
        
        try {
            // Test findAll
            List<Invoice> invoices = dao.findAll();
            LOGGER.info(() -> String.format("✓ Found %d invoices", invoices.size()));
            
            // Test findUnpaid
            List<Invoice> unpaidInvoices = dao.findUnpaid();
            LOGGER.info(() -> String.format("✓ Found %d unpaid invoices", unpaidInvoices.size()));
            
            // Test findOverdue
            List<Invoice> overdueInvoices = dao.findOverdue();
            LOGGER.info(() -> String.format("✓ Found %d overdue invoices", overdueInvoices.size()));
            
            return true;
            
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("✗ InvoiceDAO test failed: %s", e.getMessage()));
            return false;
        }
    }
    
    private static boolean testCrossDAORelationships() {
        LOGGER.info("--- Testing Cross-DAO Relationships ---");
        
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            JobDAO jobDAO = new JobDAO();
            InvoiceDAO invoiceDAO = new InvoiceDAO();
            
            // Get first customer and their related data
            List<Customer> customers = customerDAO.findAll();
            if (customers.isEmpty()) {
                LOGGER.warning("✗ No customers found for relationship test");
                return false;
            }
            
            Customer firstCustomer = customers.get(0);
            LOGGER.info(() -> String.format("Testing relationships for: %s", firstCustomer.getDisplayName()));
            
            // Find jobs for this customer
            List<Job> customerJobs = jobDAO.findByCustomer(firstCustomer.getCustomerId());
            LOGGER.info(() -> String.format("✓ Customer has %d jobs", customerJobs.size()));
            
            // Find invoices for customer's jobs
            int totalInvoices = 0;
            for (Job job : customerJobs) {
                List<Invoice> jobInvoices = invoiceDAO.findByJob(job.getJobId());
                totalInvoices += jobInvoices.size();
            }
            final int finalInvoiceCount = totalInvoices;
            LOGGER.info(() -> String.format("✓ Customer's jobs have %d total invoices", finalInvoiceCount));
            
            return true;
            
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("✗ Cross-DAO relationship test failed: %s", e.getMessage()));
            return false;
        }
    }
}