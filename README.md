# Baltimore Metal Crafters Database Management System

A comprehensive Java Swing application for managing restoration projects, customer relationships, employee records, and billing for Baltimore Metal Crafters.

## Project Overview

This application provides a complete database management system for a metal restoration business, featuring customer management, employee tracking, job monitoring, and invoice processing with a professional Swing-based user interface.

## Technology Stack

- **Java 21**: Modern Java with latest language features
- **MySQL 8.x**: Robust database server for data persistence  
- **Maven 3.9+**: Dependency management and build automation
- **JDBC**: Direct database connectivity with prepared statements
- **Swing**: Native Java GUI framework for desktop application

## Features

### Core Business Entities
- **Customer Management**: Complete client information and contact tracking
- **Employee Management**: Staff records with role-based organization
- **Job Tracking**: Restoration project monitoring with status workflow
- **Invoice Management**: Billing and payment tracking with overdue detection

### Database Layer
- **17-table normalized schema** with proper relationships and constraints
- **Complete CRUD operations** through Data Access Objects (DAOs)
- **Business logic validation** and status management
- **Comprehensive sample data** for testing and demonstration

### User Interface
- **Tabbed interface** for intuitive navigation between entity types
- **Table-based data display** with sorting and filtering capabilities
- **Search functionality** for efficient data retrieval
- **Professional menu system** with standard application features

## Project Structure

```
cosc457/
├── app/                          # Main Maven application
│   ├── src/main/java/org/bmc/app/
│   │   ├── dao/                  # Data Access Objects
│   │   │   ├── CustomerDAO.java
│   │   │   ├── EmployeeDAO.java
│   │   │   ├── JobDAO.java
│   │   │   └── InvoiceDAO.java
│   │   ├── model/                # Entity classes
│   │   │   ├── Customer.java
│   │   │   ├── Employee.java
│   │   │   ├── Job.java
│   │   │   └── Invoice.java
│   │   ├── ui/                   # User interface components
│   │   │   ├── MainFrame.java
│   │   │   ├── CustomerPanel.java
│   │   │   ├── EmployeePanel.java
│   │   │   ├── JobPanel.java
│   │   │   └── InvoicePanel.java
│   │   ├── util/                 # Database utilities
│   │   │   └── DBConnection.java
│   │   ├── test/                 # Test classes
│   │   │   └── FullDAOIntegrationTest.java
│   │   ├── Main.java             # Console application entry point
│   │   └── MainGUI.java          # GUI application entry point
│   ├── src/main/resources/       # Configuration files
│   │   └── db.properties         # Database connection settings
│   ├── pom.xml                   # Maven configuration
│   └── target/                   # Compiled classes and dependencies
├── db/                           # Database files
│   ├── schema.sql                # Complete database structure
│   ├── data.sql                  # Sample data for testing
│   └── queries.sql               # Business reporting queries
├── docs/                         # Team documentation
│   ├── CHRIS_FRONTEND_GUIDE.md   # Frontend development guide
│   └── BRYAN_TESTING_GUIDE.md    # Testing and QA guide
└── README.md                     # This file
```

## Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.9 or higher  
- MySQL 8.0 or higher

### Database Setup

1. **Create Database and User**:
```sql
CREATE DATABASE bmc_db;
CREATE USER 'bmc_user'@'localhost' IDENTIFIED BY 'bmc_password123';
GRANT ALL PRIVILEGES ON bmc_db.* TO 'bmc_user'@'localhost';
FLUSH PRIVILEGES;
```

2. **Load Schema and Sample Data**:
```bash
mysql -u bmc_user -p bmc_db < db/schema.sql
mysql -u bmc_user -p bmc_db < db/data.sql
```

### Application Execution

```bash
cd app
mvn compile exec:java
```

## Database Schema

The application uses a comprehensive 17-table database structure:

### Core Tables
- `customers`: Client company information and contacts
- `employees`: Staff records with roles and contact details  
- `jobs`: Restoration projects with status tracking
- `invoices`: Billing records with payment status

### Supporting Tables
- `job_materials`: Materials used per job
- `job_employees`: Employee assignments to jobs
- `invoice_items`: Detailed line items for invoices
- `customer_contacts`: Additional customer contact persons
- Status and reference tables for data integrity

## Business Logic

### Job Status Workflow
- **Pending**: Initial assessment and planning
- **InProgress**: Active restoration work
- **Completed**: Work finished, ready for delivery
- **OnHold**: Temporarily suspended
- **Cancelled**: Project terminated

### Employee Roles
- **Manager**: Project oversight and client relations
- **Restorer**: Hands-on restoration and conservation work
- **Fabricator**: Metal fabrication and structural repair
- **Consultant**: Technical expertise and assessment

## Data Access Layer

### DAO Implementation
Each entity has a corresponding DAO class providing:
- Complete CRUD operations (Create, Read, Update, Delete)
- Specialized query methods for business requirements
- Prepared statements for SQL injection prevention
- Proper resource management and error handling

### Example Usage
```java
// Customer operations
CustomerDAO customerDAO = new CustomerDAO();
List<Customer> allCustomers = customerDAO.findAll();
List<Customer> searchResults = customerDAO.searchByName("Baltimore");
Customer newCustomer = customerDAO.create(customer);

// Job status filtering
JobDAO jobDAO = new JobDAO();
List<Job> inProgressJobs = jobDAO.findByStatus(JobStatus.InProgress);
List<Job> customerJobs = jobDAO.findByCustomer(customerId);
```

## Testing

### Integration Testing
Comprehensive DAO integration tests verify:
- Database connectivity and configuration
- CRUD operations for all entities
- Cross-DAO relationship queries
- Data consistency and transaction handling

### Sample Data
The application includes realistic test data:
- 6 customers (museums, historical societies, private collectors)
- 6 employees across different roles
- 5 jobs in various stages of completion
- 3 invoices with different payment statuses

## Configuration

Database connection settings in `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/bmc_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=bmc_user
db.password=bmc_password123
db.driver=com.mysql.cj.jdbc.Driver
```

## Development Team

This project was developed collaboratively with specialized roles:

- **Backend Development**: Database schema, DAO implementation, business logic
- **Frontend Development**: Swing UI components, dialog forms, user interaction
- **Quality Assurance**: Testing, data validation, user experience evaluation

## Code Quality Features

- **Modern Java 21**: Switch expressions, text blocks, enhanced type inference
- **Comprehensive Logging**: Structured logging with java.util.logging
- **Resource Management**: Proper try-with-resources usage
- **SQL Security**: Prepared statements throughout
- **Error Handling**: Graceful exception handling and user feedback

## Future Enhancements

- [ ] Advanced reporting and analytics dashboard
- [ ] Photo documentation integration for restoration projects  
- [ ] Inventory management for materials and tools
- [ ] Time tracking for employee productivity
- [ ] Automated invoice generation and email delivery
- [ ] Web-based interface for remote access

## License

This project is developed for educational purposes as part of COSC 457 - Database Management Systems.