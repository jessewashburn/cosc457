# TESTING & DATA MANAGEMENT GUIDE - FOR BRYAN

**Hi Bryan!** You're the quality assurance specialist for the Baltimore Metal Crafters application. Your testing and data management work is crucial for project success.

## Quick Setup (Same as Chris)

### Step 1: Check Prerequisites
```bash
java -version    # Need Java 21
mvn -version     # Need Maven 3.9+
mysql --version  # Need MySQL 8.0+
```

### Step 2: Setup Database
```bash
# Start MySQL
net start mysql80

# Login to MySQL as root
mysql -u root -p

# Create database and user
CREATE DATABASE bmc_db;
CREATE USER 'bmc_user'@'localhost' IDENTIFIED BY 'bmc_password123';
GRANT ALL PRIVILEGES ON bmc_db.* TO 'bmc_user'@'localhost';
FLUSH PRIVILEGES;
exit;

# Load the database
cd c:/Users/jesse/COSC457/cosc457
mysql -u bmc_user -p bmc_db < db/schema.sql
mysql -u bmc_user -p bmc_db < db/data.sql
# Password: bmc_password123
```

### Step 3: Run the Application
```bash
cd c:/Users/jesse/COSC457/cosc457/app
mvn compile exec:java
```

## Your Testing & Data Management Tasks

### Phase 1: Application Testing (Start Here!)

#### Functional Testing Checklist
- [ ] **Application Startup**: Launches without errors
- [ ] **Customer Tab**: Loads data, search works, filters work
- [ ] **Employee Tab**: Loads data, role filter works
- [ ] **Job Tab**: Loads data, status and customer filters work
- [ ] **Invoice Tab**: Loads data, payment and overdue filters work
- [ ] **Delete Operations**: Work correctly (be careful!)
- [ ] **Menu Bar**: All menu items respond appropriately
- [ ] **Window Behavior**: Resizing, minimizing, closing work properly

#### How to Test Each Feature
1. **Customer Search**: 
   - Type "Baltimore" in search box
   - Should show 3 results
   - Clear search to see all customers again

2. **Employee Role Filter**:
   - Select "restorer" from dropdown
   - Should show 3 employees
   - Try other roles

3. **Job Status Filter**:
   - Select "InProgress" from dropdown
   - Should show 3 jobs
   - Try other statuses

4. **Invoice Filters**:
   - Select "Unpaid" from payment status
   - Check "Overdue Only" checkbox
   - Try different combinations

5. **Delete Testing** (be careful!):
   - Select a record in any table
   - Click "Delete" button
   - Confirm the operation
   - Verify record is removed from table

### Phase 2: Data Management

#### Current Sample Data
```sql
-- Check what's currently in the database
SELECT 'Customers' as table_name, COUNT(*) as count FROM customers
UNION ALL
SELECT 'Employees', COUNT(*) FROM employees
UNION ALL
SELECT 'Jobs', COUNT(*) FROM jobs
UNION ALL
SELECT 'Invoices', COUNT(*) FROM invoices;
```

#### Add More Test Data

Connect to database:
```bash
mysql -u bmc_user -p bmc_db
# Password: bmc_password123
```

Add more customers:
```sql
INSERT INTO customers (company_name, contact_name, phone, email, address) VALUES
('Baltimore Arts Center', 'Michael Brown', '410-555-8888', 'mbrown@baltarts.org', '456 Culture Ave, Baltimore, MD'),
('Historic Annapolis Foundation', 'Sarah Wilson', '410-555-7777', 'swilson@historicannapolis.org', '321 State Circle, Annapolis, MD'),
('Private Collector - Johnson', 'Robert Johnson', '410-555-6666', 'rjohnson@email.com', '654 Mansion Dr, Towson, MD'),
('Chesapeake Maritime Museum', 'Linda Davis', '410-555-5555', 'ldavis@cbmm.org', '213 Talbot St, St. Michaels, MD'),
('Baltimore County Historical Society', 'James Miller', '410-555-4444', 'jmiller@baltcohistory.org', '987 County Rd, Towson, MD');
```

Add more employees:
```sql
INSERT INTO employees (first_name, last_name, role, email, phone, hire_date) VALUES
('Jennifer', 'Garcia', 'restorer', 'jgarcia@bmc.com', '410-555-3333', '2023-06-15'),
('Kevin', 'Anderson', 'fabricator', 'kanderson@bmc.com', '410-555-2222', '2023-08-20'),
('Amanda', 'Taylor', 'consultant', 'ataylor@bmc.com', '410-555-1111', '2024-01-10');
```

Add more jobs with different statuses:
```sql
INSERT INTO jobs (customer_id, description, status, start_date, due_date, estimated_cost) VALUES
(7, 'Restore antique weathervane', 'Pending', '2024-11-15', '2024-12-15', 1200.00),
(8, 'Repair historic gate latches', 'InProgress', '2024-10-01', '2024-11-30', 2800.00),
(9, 'Conservation of ship bell', 'Completed', '2024-08-01', '2024-09-15', 3500.00),
(10, 'Restoration of iron fence sections', 'OnHold', '2024-09-01', '2024-12-01', 4200.00),
(11, 'Repair of historic door hardware', 'Cancelled', '2024-07-01', '2024-08-01', 800.00);
```

Add overdue invoices for testing:
```sql
INSERT INTO invoices (job_id, amount, date_issued, due_date, is_paid, payment_date) VALUES
(1, 2500.00, '2024-08-01', '2024-09-01', FALSE, NULL),  -- Overdue
(2, 1800.00, '2024-09-01', '2024-10-01', FALSE, NULL),  -- Overdue
(6, 1200.00, '2024-10-15', '2024-11-15', FALSE, NULL),  -- Current
(7, 2800.00, '2024-10-01', '2024-11-01', TRUE, '2024-10-25'); -- Paid
```

### Phase 3: Quality Assurance (After Chris adds dialogs)

#### Dialog Testing Checklist
When Chris creates each dialog, test:

**CustomerDialog**:
- [ ] Opens when clicking "Add Customer"
- [ ] Opens when clicking "Edit Customer" with selected row
- [ ] All fields are editable
- [ ] Required field validation works (try empty company name)
- [ ] Save button creates/updates record
- [ ] Cancel button closes without saving
- [ ] Table refreshes after successful save

**EmployeeDialog**:
- [ ] Role dropdown has correct options
- [ ] Date format validation
- [ ] Email format validation (if implemented)
- [ ] All CRUD operations work

**JobDialog**:
- [ ] Customer dropdown populated from database
- [ ] Status dropdown has correct options
- [ ] Date validation (start date before due date)
- [ ] Cost field accepts decimal numbers
- [ ] Description field accepts multi-line text

**InvoiceDialog**:
- [ ] Job dropdown populated from database
- [ ] Amount field validates as number
- [ ] Date fields validate format
- [ ] Payment date enabled/disabled based on "paid" checkbox
- [ ] Calculations work correctly

#### Edge Case Testing
Try these scenarios to break the dialogs:
- Enter very long text in fields
- Enter special characters (@#$%^&*)
- Leave required fields empty
- Enter invalid dates (February 30, 2024-13-45)
- Enter negative numbers for costs/amounts
- Enter letters in number fields

### Phase 4: User Experience Testing

#### Usability Checklist
- [ ] Interface is intuitive for new users
- [ ] Error messages are clear and helpful
- [ ] Buttons are appropriately enabled/disabled
- [ ] Tab order makes sense in dialogs
- [ ] Keyboard shortcuts work (Enter to save, Escape to cancel)
- [ ] Window sizing is appropriate
- [ ] Text is readable and well-formatted

## Test Reporting

### Bug Report Template
```
BUG REPORT #___
Date: ___________
Tester: Bryan
Component: Customer/Employee/Job/Invoice Dialog

DESCRIPTION:
Brief description of the issue

STEPS TO REPRODUCE:
1. 
2. 
3. 

EXPECTED BEHAVIOR:
What should happen

ACTUAL BEHAVIOR:
What actually happens

SEVERITY: High/Medium/Low
PRIORITY: High/Medium/Low
```

### Test Status Tracking
Keep a simple checklist:

```
TESTING STATUS - Baltimore Metal Crafters App

APPLICATION CORE:
[DONE] Database Connection
[DONE] Customer Panel - Basic Functions
[DONE] Employee Panel - Basic Functions  
[DONE] Job Panel - Basic Functions
[DONE] Invoice Panel - Basic Functions
[WARN] Delete Operations - [note any issues]

DIALOGS (waiting for Chris):
[WAIT] CustomerDialog - Not yet available
[WAIT] EmployeeDialog - Not yet available
[WAIT] JobDialog - Not yet available
[WAIT] InvoiceDialog - Not yet available

DATA QUALITY:
[DONE] Sample data loaded
[DONE] Additional test data added
[DONE] Edge case data created
[DONE] Overdue invoices for testing

BUGS FOUND:
1. [List any bugs found]
2. 
3. 

IMPROVEMENTS SUGGESTED:
1. [List suggestions]
2. 
3. 
```

### Testing Priorities
1. **First Priority**: Basic functionality (can add/edit records)
2. **Second Priority**: Validation (prevents bad data)
3. **Third Priority**: User experience (feels professional)
4. **Fourth Priority**: Edge cases (unusual scenarios)

## Success Criteria

Your testing is successful when:
- [ ] All basic functions work without crashes
- [ ] Data integrity is maintained (no corrupted records)
- [ ] User can complete common tasks intuitively
- [ ] Error messages guide users to correct problems
- [ ] Application feels professional and polished
