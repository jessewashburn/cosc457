# Baltimore Metal Crafters Database System
## User Guide

**Copyright © 2025 Baltimore Metal Crafters**  
**Version 1.0.0**  
**Date: November 29, 2025**

---

## Table of Contents

**1 INTRODUCING THE BMC DATABASE SYSTEM** .......................................................................... 3  
&nbsp;&nbsp;&nbsp;&nbsp;1.1 WHO SHOULD USE THIS APPLICATION ........................................................................................ 3  
&nbsp;&nbsp;&nbsp;&nbsp;1.2 WHO SHOULD USE THIS GUIDE .................................................................................................. 4  

**2 KEY FEATURES** .......................................................................................................................... 5  
&nbsp;&nbsp;&nbsp;&nbsp;2.1 CUSTOMER MANAGEMENT FEATURES ........................................................................................ 5  
&nbsp;&nbsp;&nbsp;&nbsp;2.2 JOB TRACKING FEATURES ......................................................................................................... 6  
&nbsp;&nbsp;&nbsp;&nbsp;2.3 INVENTORY AND MATERIAL FEATURES ...................................................................................... 7  
&nbsp;&nbsp;&nbsp;&nbsp;2.4 FINANCIAL MANAGEMENT FEATURES ......................................................................................... 8  

**3 A GUIDED TOUR** ....................................................................................................................... 9  
&nbsp;&nbsp;&nbsp;&nbsp;3.1 LAUNCHING THE APPLICATION ................................................................................................... 9  
&nbsp;&nbsp;&nbsp;&nbsp;3.2 MANAGE CUSTOMERS .............................................................................................................. 11  
&nbsp;&nbsp;&nbsp;&nbsp;3.3 MANAGE EMPLOYEES ............................................................................................................... 15  
&nbsp;&nbsp;&nbsp;&nbsp;3.4 MANAGE JOBS .......................................................................................................................... 18  
&nbsp;&nbsp;&nbsp;&nbsp;3.5 MANAGE PHOTOS ..................................................................................................................... 24  
&nbsp;&nbsp;&nbsp;&nbsp;3.6 MANAGE MATERIALS ................................................................................................................ 28  
&nbsp;&nbsp;&nbsp;&nbsp;3.7 MANAGE INVOICES ................................................................................................................... 31  
&nbsp;&nbsp;&nbsp;&nbsp;3.8 MANAGE PURCHASE ORDERS ................................................................................................... 34  
&nbsp;&nbsp;&nbsp;&nbsp;3.9 VIEW REPORTS ......................................................................................................................... 37  

---

## 1 Introducing the BMC Database System

### 1.1 Who Should Use This Application

Metal restoration and custom fabrication businesses require comprehensive tracking of customer relationships, complex multi-stage jobs, material inventory, and accurate billing. Managing these interconnected processes manually or with disparate systems leads to errors, inefficiencies, and lost revenue.

Where retailers have point-of-sale systems and manufacturers have production tracking, restoration and fabrication shops need integrated job management. The BMC Database System provides this integrated solution.

The BMC Database System manages all aspects of the restoration and fabrication business:

- **Customer Relationship Management:** Track contact information, job history, and payment status for all customers
- **Job Lifecycle Tracking:** Monitor projects from initial quote through completion, including status updates and milestone tracking
- **Material Inventory Management:** Track stock levels, monitor usage, receive automated reorder alerts, and maintain vendor relationships
- **Financial Operations:** Generate invoices, track payments, manage purchase orders, and monitor cash flow
- **Visual Documentation:** Store before/during/after photos with each job for customer communication, quality control, and historical records
- **Business Intelligence:** Access pre-built reports for revenue analysis, material usage, employee productivity, and customer activity

The system provides complete visibility into business operations, enabling data-driven decisions about staffing, inventory, pricing, and customer relationships.

### 1.2 Who Should Use This Guide

This guide is intended for all users of the BMC Database System:

- **Shop Managers and Owners:** Learn how to manage the complete business workflow from customer intake through final payment collection. Access reports to monitor business performance, identify trends, and make strategic decisions.

- **Office Staff and Administrators:** Learn how to manage customer records, create and track jobs, generate invoices, process payments, and handle purchase orders for inventory replenishment.

- **Craftspeople and Fabricators:** Learn how to view job details, check material availability, upload progress photos documenting your work, and update job status as work progresses.

For detailed step-by-step instructions on specific operations, users should consult the **Use Cases** document provided with the application.

---

## 2 Key Features

### 2.1 Customer Management Features

The following are key system functions available for managing customer information:

- **Add a customer** to the system with complete contact details including name, phone number, email address, and physical address

- **Edit customer information** when contact details change or need correction

- **View all customers** in a sortable, searchable table format for quick access

- **Search and filter customers** by typing in the search field to quickly locate specific records

- **Review customer job history** by filtering jobs by Customer ID to see all work performed

- **Delete customers** from the system (only permitted if the customer has no associated job records, protecting historical data integrity)

### 2.2 Job Tracking Features

The following are key system functions for tracking restoration and fabrication projects:

- **Create new jobs** associated with specific customers, including detailed descriptions, start dates, due dates, and initial status

- **Edit job details** to update descriptions, adjust start and due dates, and modify estimated costs

- **Track job status** through three defined stages:
  - **Planned:** Job is scheduled but work has not begun
  - **InProgress:** Active work is underway
  - **Completed:** Work is finished and ready for invoicing

- **Assign materials to jobs** to track material consumption, automatically decrementing inventory stock levels

- **Update material quantities** as work progresses and actual usage is determined

- **Upload job photos** to document before/during/after states of restoration or fabrication work

- **View job photos** in gallery mode (grid view of all photos) or full-size viewer mode for detailed inspection

- **Delete photos** that are no longer needed or were uploaded in error

- **Filter jobs by status** to view only Planned, InProgress, or Completed jobs

- **Filter jobs by customer** to see all work for a specific client

- **Delete jobs** (with appropriate safeguards to prevent deletion of jobs with invoices or other critical dependencies)

### 2.3 Inventory and Material Features

The following are key system functions for inventory management:

- **View all materials** with current stock levels, reorder thresholds, unit costs, and vendor information in a comprehensive table

- **Add new materials** to the inventory system as your product offerings expand

- **Edit material details** including stock quantities (manual adjustments), reorder level thresholds, unit costs for job costing, and vendor assignments

- **Visual low-stock alerts** with red row highlighting when inventory reaches or falls below reorder level, preventing stockouts

- **Assign materials to jobs** which automatically decrements stock quantities in real-time

- **Remove materials from jobs** which automatically restores stock quantities if materials are not used

- **Update material quantities** on active jobs as consumption is measured

- **Delete materials** from the system (only permitted if not assigned to any jobs, protecting data integrity)

### 2.4 Financial Management Features

The following are key system functions for billing and vendor management:

- **Create invoices** associated with completed jobs, including calculated totals based on materials and labor

- **Edit invoice amounts** and dates as needed for corrections or adjustments

- **Mark invoices as paid** when payment is received, clearly distinguishing outstanding balances from collected revenue

- **Filter invoices** to view only unpaid bills for accounts receivable management

- **Track invoice payment status** with clear visual indicators (Paid: Yes/No)

- **Create purchase orders** for ordering materials from vendors

- **Add line items to purchase orders** specifying materials, quantities, and negotiated unit prices

- **Receive purchase orders** which automatically updates inventory stock levels when materials arrive

- **Cancel purchase orders** when vendors cannot fulfill orders or orders are no longer needed

- **Track purchase order status** through three states:
  - **Pending:** Order placed, awaiting delivery
  - **Received:** Materials delivered and inventory updated
  - **Cancelled:** Order voided, no inventory impact

- **Generate business reports** including:
  - Jobs by Status (counts and values)
  - Revenue Report (invoiced, paid, outstanding)
  - Material Usage (consumption patterns)
  - Employee Productivity (hours and job completion)
  - Customer Activity (top customers, revenue)
  - Low Stock Alerts (reorder recommendations)
  - Outstanding Invoices (aging report)

---

## 3 A Guided Tour

This section provides an overview of each major section of the BMC Database System and teaches users how to use the features.

### 3.1 Launching the Application

**Prerequisites:**
1. **VPN Connection Required:** You must be connected to Towson University VPN to access the database
   - Download Cisco AnyConnect from https://vpnc.towson.edu
   - Connect to `vpnc.towson.edu` with your NetID and password
   - Authenticate with Duo if prompted

2. **Java Requirement:** Java 21 or higher must be installed on your computer

**Starting the Application:**

**Option 1:** Double-click the JAR file
- Locate `bmc-app-1.0.0.jar` in your installation folder
- Double-click to launch

**Option 2:** Run from command line
```bash
java -jar bmc-app-1.0.0.jar
```

*[Screenshot: Application icon and launch command]*

**Initial Loading:**

The application takes 2-5 seconds to launch. You will see the main window appear with a tabbed interface.

*[Screenshot: Main application window on startup]*

**Main Window Layout:**

Upon launching, the application displays a tabbed interface with seven main sections across the top navigation bar:

1. **Customers** - Customer contact and relationship management
2. **Employees** - Employee records, roles, and specializations
3. **Jobs** - Project tracking and lifecycle management
4. **Invoices** - Billing and payment tracking
5. **Materials** - Inventory and stock management
6. **Purchase Orders** - Vendor ordering and receiving
7. **Reports** - Business analytics and summaries

*[Screenshot: Main window with all seven tabs visible in navigation bar]*

**Interface Organization:**

The interface is organized into three main areas:

1. **Top Navigation Bar:** Seven tabs for switching between major sections of the application

2. **Toolbar:** Action buttons that change based on the active tab. Common buttons include:
   - **Add** - Create a new record
   - **Edit** - Modify the selected record
   - **Delete** - Remove the selected record
   - **Refresh** - Reload data from the database
   - Additional section-specific buttons (e.g., "View Photos" in Jobs tab)

3. **Main Display Area:** Data tables showing records with sortable columns and scroll bars

*[Screenshot: Interface layout with three areas labeled]*

**Default View:**

The application always opens to the **Customers** tab, as customer information is the starting point for most business workflows.

### 3.2 Manage Customers

To access customer information, ensure the **Customers** tab is selected in the top navigation bar. The main display area shows a table of all customers in the system.

*[Screenshot: Customers tab showing full table with sample customer data]*

**Customer Table Columns:**

The customer table displays the following information:

- **Customer ID:** Unique identifier automatically assigned by the system (used when creating jobs)
- **Name:** Customer's full name or business name
- **Phone:** Contact phone number
- **Email:** Email address for electronic communication
- **Address:** Physical street address for delivery, pickup, or site visits

**Viewing and Navigating Customer Records:**

All customers are displayed in the table by default. You can:

- **Scroll through the list** using the vertical scroll bar on the right
- **Sort by any column** by clicking the column header (click again to reverse sort order)
- **Select a customer** by clicking anywhere on their row (row highlights when selected)

*[Screenshot: Customer table with one row selected/highlighted]*

**Adding a New Customer:**

To add a new customer to the system:

1. Click the **Add Customer** button in the toolbar at the top of the window

2. The **Add Customer** dialog window appears

*[Screenshot: Add Customer dialog with empty form fields]*

3. Fill in the customer information:
   - **Name:** (Required) Enter the customer's full name or business name
   - **Phone:** (Optional) Enter contact phone number in any format
   - **Email:** (Optional) Enter email address
   - **Address:** (Optional) Enter complete street address including city, state, ZIP

4. Click the **Save** button to create the customer record

5. The dialog closes automatically and the new customer appears in the table

**Important Notes:**
- The **Customer ID** is automatically assigned by the system when you save
- Make note of the Customer ID, as you will need it when creating jobs for this customer
- Only the Name field is required; all other fields are optional but recommended for complete records

**Editing Customer Information:**

To update an existing customer's information:

1. **Select the customer** by clicking on their row in the table (row highlights in blue)

2. Click the **Edit** button in the toolbar (this button is enabled only after selecting a customer)

*[Screenshot: Toolbar with Edit button highlighted/enabled]*

3. The **Edit Customer** dialog appears with the customer's current information pre-filled in all fields

*[Screenshot: Edit Customer dialog with populated data]*

4. Modify any fields as needed:
   - Change the customer's name
   - Update phone number
   - Update email address
   - Update physical address

5. Click **Save** to commit the changes

6. The dialog closes and changes are immediately reflected in the customer table

**Alternative Method:** You can also double-click a customer row to open the Edit dialog directly.

**Deleting a Customer:**

To remove a customer from the system:

1. **Select the customer** by clicking on their row in the table

2. Click the **Delete** button in the toolbar

3. A confirmation dialog appears asking you to confirm the deletion

*[Screenshot: Delete confirmation dialog with warning message]*

4. Click **Yes** to permanently delete the customer, or **No** to cancel

**Important Constraints:**

You **cannot delete a customer who has associated jobs** in the system. This protection ensures:
- Historical business records are preserved
- Job data integrity is maintained
- Past work can always be traced to the customer

If you attempt to delete a customer with existing jobs, an error message appears:

*[Screenshot: Error message: "Cannot delete customer with existing jobs"]*

In this case, the customer record remains in the system. If you need to indicate the customer is no longer active, consider adding a note to their name (e.g., "INACTIVE - John Smith").

### 3.3 Manage Employees

Click the **Employees** tab in the top navigation bar to access employee management. The main display area shows a table of all employees in your organization.

*[Screenshot: Employees tab showing table with employee data]*

**Employee Table Columns:**

The employee table displays:

- **Employee ID:** Unique identifier automatically assigned by the system
- **Name:** Employee's full name
- **Role:** Employee type (Restorer, Fabricator, or Admin)
- **Specialization:** Specific skills or areas of expertise (e.g., "Gold leafing", "TIG welding", "Finishing")
- **Contact Info:** Phone number or email address
- **Hourly Rate:** Pay rate used for labor cost calculations in job costing

**Filtering Employees by Role:**

Above the employee table, you'll see a **Role** filter dropdown menu. Use this to view specific types of employees:

*[Screenshot: Role filter dropdown expanded showing options]*

1. Click the **Role** dropdown
2. Select from the available options:
   - **All** - Show all employees (default)
   - **Restorer** - Show only restoration specialists
   - **Fabricator** - Show only fabricators and welders
   - **Admin** - Show only administrative staff

3. The table immediately updates to show only employees with the selected role

**When to Use Role Filtering:**
- Assigning specialized restoration jobs to qualified craftspeople
- Scheduling fabricators for shop work
- Identifying administrative staff for office tasks
- Viewing team composition by specialty

**Adding a New Employee:**

To add an employee to the system:

1. Click the **Add Employee** button in the toolbar

2. The **Add Employee** dialog appears

*[Screenshot: Add Employee dialog with empty fields]*

3. Complete the employee information:
   - **Name:** (Required) Employee's full name
   - **Role:** (Required) Select from dropdown:
     - Restorer
     - Fabricator
     - Admin
   - **Specialization:** (Optional) Describe specific skills (e.g., "Antique metal restoration", "Architectural metalwork", "Powder coating")
   - **Contact Info:** (Optional) Phone number or email address
   - **Hourly Rate:** (Optional) Enter hourly pay rate for cost tracking (default: $0.00)

*[Screenshot: Role dropdown expanded showing three options]*

4. Click **Save** to create the employee record

5. The dialog closes and the new employee appears in the table

**Hourly Rate Considerations:**
- Used for calculating labor costs on jobs
- Can be left at $0.00 if not tracking labor costs
- Does not affect employee pay (this is for job costing only)

**Editing Employee Information:**

To update an existing employee's record:

1. Select the employee by clicking on their row in the table

2. Click the **Edit** button in the toolbar

3. The **Edit Employee** dialog appears with current information pre-filled

*[Screenshot: Edit Employee dialog with populated data]*

4. Modify any fields except Employee ID:
   - Update name
   - Change role (if responsibilities change)
   - Update or add specialization details
   - Update contact information
   - Adjust hourly rate

5. Click **Save** to commit changes

**Note About Hourly Rate Changes:**
- Changing an employee's hourly rate does NOT affect historical work records
- The new rate applies only to future work assignments
- Past job costs remain calculated with the rate that was in effect at that time

**Deleting an Employee:**

To remove an employee from the system:

1. Select the employee in the table

2. Click the **Delete** button in the toolbar

3. Confirm the deletion in the dialog that appears

**Important Constraint:**

Employees **cannot be deleted if they have work log entries** in the system. This protects:
- Historical labor records
- Job cost accuracy
- Past performance data

If you attempt to delete an employee with work history, an error message explains the constraint.

*[Screenshot: Error message about employee with work logs]*

### 3.4 Manage Jobs

Click the **Jobs** tab to access the job management interface. This is the core of the BMC system where you track all restoration and fabrication projects from start to finish.

*[Screenshot: Jobs tab showing table with multiple job records]*

**Jobs Table Columns:**

The jobs table displays:

- **Job ID:** Unique job identifier assigned by the system
- **Customer ID:** The customer who commissioned this work
- **Description:** Brief description of the project
- **Status:** Current stage (Planned, InProgress, or Completed)
- **Start Date:** When work begins or began
- **Due Date:** Target completion date
- **Estimated Cost:** Calculated project value based on materials and labor

**Toolbar Buttons:**

The Jobs tab includes several action buttons:

*[Screenshot: Jobs toolbar with all buttons visible]*

- **Add Job** - Create a new project
- **Edit** - Modify job details and manage photos (requires selecting a job first)
- **Delete** - Remove a job with confirmation (requires selecting a job first)
- **Manage Materials** - Assign/remove materials for the selected job
- **View Photos** - Open read-only photo gallery for the selected job
- **Refresh** - Reload job data from the database

**Filtering Jobs by Status:**

To view only jobs in a specific stage:

1. Locate the **Status** dropdown in the toolbar

*[Screenshot: Status dropdown expanded]*

2. Click the dropdown and select:
   - **All** - Show all jobs regardless of status (default)
   - **Planned** - Show only scheduled jobs not yet started
   - **InProgress** - Show only active jobs with work underway
   - **Completed** - Show only finished jobs ready for invoicing

3. The table updates immediately to show only matching jobs

**Filtering Jobs by Customer:**

To view all jobs for a specific customer:

1. Locate the **Customer ID** text field in the toolbar

*[Screenshot: Customer ID filter field and button]*

2. Enter the Customer ID number

3. Click the **Filter** button next to the field

4. The table shows only jobs for that customer

**Clearing Filters:**

To return to viewing all jobs:
- Select **All** from the Status dropdown
- Clear the Customer ID field
- Click **Refresh**

**Adding a New Job:**

To create a new restoration or fabrication project:

1. Click the **Add Job** button in the toolbar

2. The **Add Job** dialog appears

*[Screenshot: Add Job dialog with empty fields]*

3. Complete the job information:

   - **Customer ID:** (Required) Enter the ID of the customer for this job
     - You must use an existing Customer ID from the Customers tab
     - If you don't know the ID, go to Customers tab first to find it
   
   - **Description:** (Required) Describe the work to be performed
     - Example: "Restore Victorian iron railing with gold leaf accents"
     - Example: "Fabricate custom stainless steel handrail for main entrance"
   
   - **Quote ID:** (Optional) Reference number if a written quote was provided to the customer
   
   - **Start Date:** (Optional) When work will begin
     - Click the calendar icon to select a date
     - Or leave blank to set later
   
   - **Due Date:** (Optional) Target completion date
     - Click the calendar icon to select a date
     - Used for scheduling and deadline tracking
   
   - **Status:** (Optional) Current job stage
     - Defaults to "Planned" for new jobs
     - Can be changed to InProgress or Completed later

*[Screenshot: Add Job dialog with calendar date picker visible]*

4. Click **Save** to create the job

5. The dialog closes and the new job appears in the table

**Important Notes:**

- The **Job ID** is automatically assigned - note this number for future reference
- The Customer ID **must exist** in the Customers table or you'll receive an error
- You cannot upload photos when creating a new job - photos can only be added after the job is saved and has a Job ID

*[Screenshot: Error message for invalid Customer ID]*

**Editing Job Details:**

To modify an existing job:

1. Select the job in the table (click the row)

2. Click the **Edit** button in the toolbar (or double-click the row)

3. The **Edit Job** dialog opens with a split-panel layout:

*[Screenshot: Edit Job dialog showing two-panel layout]*

**Left Panel - Job Details:**
- Job ID (read-only, cannot be changed)
- Customer ID
- Description
- Quote ID
- Start Date (with calendar picker)
- Due Date (with calendar picker)
- Status (dropdown menu)
- Estimated Cost

**Right Panel - Job Photos** (only appears for jobs that have been saved):
- Photo upload button
- Thumbnail grid of uploaded photos
- Photo management controls

4. Modify any fields in the left panel as needed

5. Click **Save** to commit all changes

**Updating Job Status:**

The Status field has three options that reflect the job lifecycle:

*[Screenshot: Status dropdown showing three options]*

- **Planned** - Job is scheduled but work has not started yet
  - Use when: Job is quoted and accepted but work hasn't begun
  - Materials can be assigned in this stage
  
- **InProgress** - Active work is currently underway
  - Use when: Craftspeople are actively working on the project
  - Upload progress photos during this stage
  - Update material quantities as they're consumed
  
- **Completed** - Work is finished and ready for invoicing
  - Use when: All work is done and quality-checked
  - Upload final "after" photos
  - Ready to create invoice

**Best Practice:** Update status promptly as jobs progress through stages to maintain accurate workload visibility.

**Managing Job Materials:**

Materials track what inventory items are consumed by each job and automatically update stock levels.

To assign materials to a job:

1. Select a job in the table

2. Click the **Manage Materials** button in the toolbar

3. The **Job Materials** dialog opens

*[Screenshot: Job Materials dialog showing assigned materials table]*

The dialog displays:
- **Top Section:** Table of materials currently assigned to this job
  - Material ID
  - Material Name
  - Category
  - Quantity Used on this job
  - Current Stock Available
  
- **Bottom Section:** Management buttons

**Assigning a Material:**

1. Click the **Assign Material** button

2. A material selection dialog appears

*[Screenshot: Assign Material dialog with material dropdown]*

3. Select a material from the dropdown menu (shows Material ID and Name)

4. Enter the **Quantity** needed for this job

5. Click **Assign**

6. The material appears in the top table

7. The material's stock quantity **automatically decreases** by the amount assigned

**Updating Material Quantity:**

If actual usage differs from the original estimate:

1. Select the material in the top table

2. Click the **Update Quantity** button

3. Enter the new quantity

4. Click **Update**

5. Stock adjusts automatically:
   - If quantity increases, stock decreases further
   - If quantity decreases, stock is partially restored

**Removing a Material:**

If a material was assigned by mistake or isn't needed:

1. Select the material in the top table

2. Click the **Remove Material** button

3. Confirm the removal

4. The material is unassigned from the job

5. Stock quantity is **fully restored** (the full assigned quantity is returned to inventory)

**Closing the Dialog:**

Click the **Close** button when finished managing materials. All changes are saved immediately - there is no separate "Save" button needed.

**Deleting a Job:**

To permanently remove a job from the system:

1. Select the job in the table

2. Click the **Delete** button in the toolbar

3. A confirmation dialog appears displaying the job description and Job ID

*[Screenshot: Delete job confirmation dialog]*

4. Review the information carefully

5. Click **Yes** to confirm deletion, or **No** to cancel

**What Gets Deleted:**

When you delete a job, the following data is permanently removed:

- The job record itself
- All associated photos (both database records AND physical image files)
- Job stage records
- Notes and comments
- Work log entries
- Material assignments (**stock is automatically restored** to inventory)

**Critical Constraint:**

Jobs **cannot be deleted if they have invoices**. This protection ensures:
- Financial records remain intact
- Accounting audit trail is preserved
- Revenue history is not corrupted

If you attempt to delete a job with invoices, an error message explains the constraint:

*[Screenshot: Error message about job with invoices]*

You must delete the invoice first (if appropriate) before deleting the job.

### 3.5 Manage Photos

Photos are essential for documenting restoration and fabrication work. The BMC system supports uploading photos, viewing them in multiple modes, and managing photo collections for each job.

**Photo Capabilities:**
- Upload before/during/after photos
- View photos in grid gallery mode
- View individual photos at full size
- Add descriptions to photos
- Delete unwanted photos
- Automatic file organization by job

**Supported File Formats:** JPG, JPEG, PNG, GIF, BMP  
**Maximum File Size:** 10MB per photo  
**Storage:** Files saved to `photos/job_{id}/` directory on disk

**Uploading Photos (Edit Mode):**

Photos can only be uploaded when **editing an existing job** that has been saved and has a Job ID assigned. You cannot upload photos when creating a new job.

To upload photos:

1. Go to the **Jobs** tab

2. Select a job in the table

3. Click the **Edit** button (or double-click the row)

4. The Edit Job dialog opens with two panels

5. Look at the **right panel** labeled "Job Photos"

*[Screenshot: Edit Job dialog with right panel showing "Upload Photo" button]*

6. Click the **Upload Photo** button

7. A file browser dialog opens

*[Screenshot: File browser for selecting photo]*

8. Navigate to your photo file on your computer

9. Select the photo and click **Open**

10. (Optional) A text field may appear asking for a description - enter details like:
    - "Before restoration - front view"
    - "Cleaned and prepped surface"
    - "After powder coating application"
    - "Final product - detail shot"

11. The photo uploads and a **thumbnail** appears in the right panel

12. Repeat steps 6-11 for additional photos

13. Click **Save** on the job dialog to finalize (saves both job changes and photo uploads)

**Photo Thumbnails in Edit Mode:**

Each uploaded photo appears as a thumbnail (200×150 pixels) showing:

*[Screenshot: Photo thumbnail with labels pointing to elements]*

- Preview image
- Filename or description text
- **X button** (top-right corner) for deletion

**Viewing Photos (Gallery Mode):**

To view all photos for a job without entering edit mode:

1. Go to the **Jobs** tab

2. Select a job in the table

3. Click the **View Photos** button in the toolbar

4. The **Photo Gallery** dialog opens

*[Screenshot: Photo Gallery dialog with title and grid layout]*

**Gallery Features:**

- **Window Title:** "Photos for Job #[number]"
- **Grid Layout:** Photos displayed in 3 columns for easy browsing
- **Larger Thumbnails:** 250×200 pixels (bigger than edit mode thumbnails)
- **Photo Information:** Filename and description displayed below each image
- **Scroll Bar:** Vertical scroll bar for jobs with many photos
- **Close Button:** Exit the gallery when done

**Benefits of Gallery Mode:**
- Review work progress without risk of accidental changes
- Show customers before/after results during consultations
- Browse job documentation quickly
- No ability to delete or upload (read-only safety)

**Viewing Full-Size Photos:**

From either Edit mode or Gallery mode, you can view any photo at full resolution:

1. Click on any photo thumbnail

2. The **Photo Viewer** dialog opens

*[Screenshot: Photo Viewer dialog showing full-size image]*

3. The image displays at full size:
   - Scaled to fit window (maximum 800×600 pixels)
   - Aspect ratio maintained (no stretching or distortion)
   - Larger images scaled down proportionally
   - Smaller images displayed at actual size

4. The photo name appears at the top of the window

5. Click the **Close** button or press **ESC** key to return to previous screen

**When to Use Full-Size View:**
- Inspecting detail work quality
- Zooming in on specific areas
- Sharing your screen with customers during remote consultations
- Documenting issues or damage
- Verifying restoration accuracy

**Deleting Photos:**

Photos can only be deleted in **Edit mode** (not in Gallery mode or Full-Size View).

To delete a photo:

1. Select the job in the Jobs tab

2. Click **Edit**

3. The Edit Job dialog opens with photos in the right panel

4. Find the photo thumbnail you want to delete

5. Click the **X button** in the top-right corner of that thumbnail

*[Screenshot: Photo thumbnail with X button highlighted]*

6. A confirmation dialog appears: "Delete this photo?"

*[Screenshot: Photo deletion confirmation dialog]*

7. Click **Yes** to confirm deletion, or **No** to cancel

8. If you clicked Yes, the thumbnail disappears immediately

9. Click **Save** on the job dialog to finalize the deletion

**What Happens When You Delete:**

- The photo record is removed from the database
- The physical image file is deleted from the `photos/job_{id}/` directory
- The deletion is **permanent and cannot be undone**
- No backup copy is retained

**Warning:** Only delete photos you're absolutely certain are not needed. Consider keeping progress photos for customer relations and quality records.

### 3.6 Manage Materials

Click the **Materials** tab to access inventory management. This section tracks all materials used in restoration and fabrication work, monitoring stock levels and costs.

*[Screenshot: Materials tab showing table with some red-highlighted rows]*

**Materials Table Columns:**

- **Material ID:** Unique identifier assigned by the system
- **Name:** Clear description of the material (e.g., "Gold leaf - 23k", "Steel sheet 16ga")
- **Category:** Classification type (Metals, Finishes, Hardware, Fasteners, Abrasives, etc.)
- **Stock Quantity:** Current inventory count
- **Reorder Level:** Threshold for low-stock alert (when to reorder)
- **Unit Cost:** Price per unit for job costing calculations
- **Vendor ID:** Reference to supplier in vendor records

**Visual Low-Stock Alerts:**

The system provides immediate visual feedback for inventory management:

- Materials at or below their reorder level are **highlighted in red**
- This color-coding makes it instantly obvious which materials need reordering
- No need to scan numbers - red rows demand attention

*[Screenshot: Materials table showing mix of normal rows and red-highlighted low-stock rows]*

**Example:**
- Material with Stock Quantity: 8, Reorder Level: 10 → **Red highlight** (below threshold)
- Material with Stock Quantity: 15, Reorder Level: 10 → Normal display (sufficient stock)

**Adding Materials:**

To add a new material to inventory:

1. Click the **Add Material** button in the toolbar

2. The **Add Material** dialog appears

*[Screenshot: Add Material dialog with empty fields]*

3. Complete the material information:

   - **Name:** (Required) Clear, descriptive name
     - Good: "Bronze rod 1/2\" round"
     - Good: "Tung oil finish - 1 quart"
     - Avoid: "Stuff" or "Metal #2"
   
   - **Category:** (Optional) Type classification
     - Examples: Metals, Finishes, Hardware, Fasteners, Abrasives, Consumables
     - Used for organizing and reporting
   
   - **Stock Quantity:** (Optional) Current inventory count
     - Default: 0 (if left blank)
     - Enter initial stock when adding material
   
   - **Reorder Level:** (Optional) Alert threshold
     - Default: 5 (if left blank)
     - Set based on usage patterns and lead time
   
   - **Unit Cost:** (Optional) Price per unit
     - Default: $0.00 (if left blank)
     - Used for job costing and profitability analysis
   
   - **Vendor ID:** (Optional) Supplier reference
     - Links to vendor records for reordering

4. Click **Save** to create the material record

5. The dialog closes and the material appears in the table

**Editing Materials:**

To update existing material information:

1. Select the material by clicking its row in the table

2. Click the **Edit** button in the toolbar

3. The **Edit Material** dialog appears with current information pre-filled

*[Screenshot: Edit Material dialog with populated data]*

4. Modify any fields:
   - Update name or category
   - **Manually adjust stock quantity** (for inventory corrections)
   - Adjust reorder level threshold
   - Update unit cost
   - Change vendor assignment

5. Click **Save** to commit changes

**Automatic Stock Updates:**

While you can manually edit stock quantities, the system also updates stock automatically:

- **Materials assigned to jobs:** Quantity **decreases** by amount assigned
- **Materials removed from jobs:** Quantity **increases** by amount removed
- **Material quantity updated on job:** Stock adjusts up or down based on change
- **Purchase orders received:** Quantity **increases** by PO amount

This automatic tracking ensures inventory accuracy without constant manual adjustments.

**Example Flow:**
1. Stock starts at 20 units
2. Assign 5 units to Job #101 → Stock drops to 15
3. Update Job #101 to use 7 units instead → Stock drops to 13
4. Remove material from Job #101 → Stock returns to 20
5. Receive PO for 50 units → Stock increases to 70

**Monitoring Low Stock:**

Check the Materials tab regularly (recommend weekly) to identify red-highlighted materials. When you see red rows:

1. Note the Material ID and Vendor ID
2. Check usage patterns (how quickly it's being consumed)
3. Consider lead time for delivery
4. Create a purchase order to reorder (see Section 3.8)

**Alternative:** Use the **Low Stock Alert Report** (Reports tab) for a dedicated view of materials needing reorder.

**Deleting Materials:**

To remove a material from the system:

1. Select the material in the table

2. Click the **Delete** button in the toolbar

3. Confirm the deletion

**Important Constraints:**

Materials **cannot be deleted** if they are:
- Currently assigned to any jobs (active or completed)
- Referenced in purchase order line items

This protection ensures:
- Job cost accuracy is maintained
- Historical material usage data is preserved
- Purchase order records remain valid

*[Screenshot: Error message about material assigned to jobs]*

If you need to discontinue a material, leave it in the system but consider adding "DISCONTINUED" to the name.

### 3.7 Manage Invoices

Click the **Invoices** tab to access billing and payment tracking. This section manages all customer invoices and payment status.

*[Screenshot: Invoices tab showing table with invoice records]*

**Invoices Table Columns:**

- **Invoice ID:** Unique identifier assigned by the system
- **Job ID:** The job being billed (links to specific project)
- **Invoice Date:** When the invoice was created
- **Total Amount:** Bill total in dollars
- **Paid:** Payment status (Yes or No)

**Adding an Invoice:**

To bill a customer for completed work:

1. Click the **Add Invoice** button in the toolbar

2. The **Add Invoice** dialog appears

*[Screenshot: Add Invoice dialog with empty fields]*

3. Complete the invoice information:

   - **Job ID:** (Required) Enter the Job ID being billed
     - Must be an existing job in the system
     - Typically use Completed jobs
   
   - **Total Amount:** (Required) Enter the calculated invoice total
     - See "Calculating Invoice Amount" below
   
   - **Invoice Date:** (Optional) Date of the invoice
     - Defaults to today's date
     - Can be changed if backdating is needed
   
   - **Paid:** (Optional) Payment status checkbox
     - Default: Unchecked (unpaid)
     - Check if payment already received
     - Typically left unchecked and marked paid later

4. Click **Save** to create the invoice

5. The dialog closes and the invoice appears in the table

**Calculating Invoice Amount:**

The BMC system does not automatically calculate invoice totals - you must determine the amount. Consider these cost components:

**Material Costs:**
1. Go to Jobs tab → Select the job → Click **Manage Materials**
2. For each assigned material, multiply: Quantity Used × Unit Cost
3. Sum all material costs

**Labor Costs:**
1. Estimate or track hours worked on the job
2. Multiply hours by employee hourly rates
3. Sum all labor costs

**Additional Charges:**
- Overhead markup (percentage of material + labor)
- Profit margin
- Delivery or installation fees
- Rush fees if applicable
- Sales tax (if required)

**Total Calculation:**
```
Total = Material Costs + Labor Costs + Overhead + Profit + Other Fees + Tax
```

**Important:** The Job ID **must exist** in the Jobs table. If you enter an invalid Job ID, you'll receive an error message.

*[Screenshot: Error message for invalid Job ID]*

**Editing an Invoice:**

To update an existing invoice:

1. Select the invoice in the table

2. Click the **Edit** button in the toolbar

3. The **Edit Invoice** dialog appears with current information

*[Screenshot: Edit Invoice dialog with populated data]*

4. Modify any fields:
   - Update total amount (if calculation was incorrect)
   - Change invoice date
   - Check/uncheck Paid status

5. Click **Save** to commit changes

**Note:** You **cannot change the Job ID** after an invoice is created. If you associated the invoice with the wrong job:
1. Delete this invoice
2. Create a new invoice with the correct Job ID

**Marking as Paid:**

When you receive payment from the customer:

1. Select the invoice in the table

2. Click **Edit**

3. In the Edit Invoice dialog, find the **Paid** checkbox

*[Screenshot: Edit Invoice dialog with Paid checkbox checked]*

4. **Check the box** to mark as paid

5. Click **Save**

6. The invoice now shows "Yes" in the Paid column

**Benefits of Tracking Payment Status:**
- Quickly identify outstanding balances
- Filter to see only unpaid invoices for accounts receivable
- Track cash flow and collection rates
- Generate aging reports (via Reports tab)

**Deleting an Invoice:**

To remove an invoice from the system:

1. Select the invoice in the table

2. Click the **Delete** button in the toolbar

3. Confirm the deletion in the dialog that appears

*[Screenshot: Delete invoice confirmation]*

**When to Delete:**
- Invoice was created by mistake
- Invoice was associated with wrong job (delete and recreate)
- Duplicate invoice was accidentally created

**Best Practice:** Use **Edit** to correct invoice amounts rather than deleting and recreating. This preserves the invoice ID and any references to it.

### 3.8 Manage Purchase Orders

Click the **Purchase Orders** tab to manage material ordering from vendors. This section tracks orders from placement through receipt, automatically updating inventory.

*[Screenshot: Purchase Orders tab showing table with color-coded status]*

**Purchase Orders Table Columns:**

- **PO ID:** Unique purchase order identifier
- **Vendor ID:** Supplier reference number
- **Vendor Name:** Supplier business name (for easy identification)
- **Order Date:** When the PO was created
- **Total Cost:** Sum of all line items in the order
- **Status:** Current state (Pending, Received, or Cancelled)

**Status Color Coding:**

Purchase orders are color-coded by status for quick visual recognition:

- **Yellow background:** Pending (order placed, awaiting delivery)
- **Green background:** Received (materials arrived and inventory updated)
- **Gray background:** Cancelled (order voided, no inventory impact)

*[Screenshot: PO table showing different color-coded rows]*

**Creating a Purchase Order:**

To order materials from a vendor:

1. Click the **Add Purchase Order** button in the toolbar

2. The **Create Purchase Order** dialog appears

*[Screenshot: Create Purchase Order dialog with vendor dropdown and line items grid]*

3. Select a **Vendor** from the dropdown menu
   - Shows Vendor ID and Name
   - Must have vendors set up in system first

4. The **Order Date** defaults to today (read-only, cannot be changed)

5. The **Status** defaults to "Pending"

6. Add line items to the order:

**Adding Line Items:**

The line items grid has columns:
- Material (dropdown)
- Quantity (number)
- Unit Price (dollars)
- Total (calculated automatically)

For each material you're ordering:

1. Click in the **Material** dropdown column

2. Select a material from the list (shows Material ID and Name)

3. Enter the **Quantity** needed

4. Enter the **Unit Price**
   - May differ from the material's stored Unit Cost
   - Use the vendor's current quoted price
   - Allows for price negotiations or bulk discounts

5. The **Total** calculates automatically (Quantity × Unit Price)

6. Add another row for additional materials (use **Add Item** button if available)

7. The **Total Cost** at the bottom sums all line items

*[Screenshot: PO dialog with multiple line items filled in]*

8. Review all information carefully

9. Click **Save** to create the purchase order

**Before Creating a PO:**

Best practices:
- Check current vendor pricing (may have changed)
- Verify material stock needs via Materials tab (look for red alerts)
- Consider lead times for delivery (don't wait until stock is critical)
- Order enough to exceed reorder level threshold

**Editing a Purchase Order:**

To modify an existing purchase order:

1. Select the PO in the table

2. Click the **Edit** button in the toolbar

3. The **Edit Purchase Order** dialog appears

*[Screenshot: Edit Purchase Order dialog]*

4. Available modifications:
   - **Change vendor** (only if status is Pending)
   - **Add/remove/edit line items**
   - **Update quantities**
   - **Update unit prices**
   - **Change status** (Pending → Received or Cancelled)

5. Click **Save** to commit changes

**Note:** You cannot modify the **Order Date** - this is locked to maintain an accurate audit trail.

**Receiving a Purchase Order:**

When materials arrive from the vendor:

1. Physically **verify the materials** match the PO:
   - Count quantities
   - Check material types
   - Inspect for damage

2. Select the PO in the table

3. Click **Edit**

4. Change the **Status** dropdown to "Received"

*[Screenshot: Edit PO dialog with Status dropdown showing "Received" selected]*

5. Click **Save**

**What Happens Automatically:**

When you mark a PO as "Received":

1. For each line item in the PO:
   - The material's **Stock Quantity increases** by the PO quantity
   - Example: Material had 5 units, PO for 20 units → Now 25 units in stock

2. **Red low-stock alerts clear** if stock is now above reorder level

3. Status changes to "Received" with green background

4. PO becomes part of historical record (can view but typically not edited further)

**Important:** Only mark as "Received" after you have **physically received and verified** the materials. Do not mark as received based on vendor shipping notification alone.

**Cancelling a Purchase Order:**

If an order is cancelled by the vendor or is no longer needed:

1. Select the PO in the table

2. Click **Edit**

3. Change **Status** dropdown to "Cancelled"

4. Click **Save**

**What Happens:**
- Status changes to "Cancelled" with gray background
- **No inventory update occurs** (stock remains unchanged)
- PO is retained for historical records

**When to Cancel:**
- Vendor cannot fulfill order
- Materials no longer needed (project cancelled)
- Found materials from different source
- Pricing no longer acceptable

**Deleting a Purchase Order:**

To permanently remove a PO from the system:

1. Select the PO in the table

2. Click the **Delete** button in the toolbar

3. Confirm the deletion

**When to Delete vs. Cancel:**
- **Cancel:** Preferred method - maintains historical record
- **Delete:** Only if PO was created by mistake or is a duplicate

**Best Practice:** Cancel POs rather than deleting them to maintain complete purchasing history for vendor management and cost analysis.

### 3.9 View Reports

Click the **Reports** tab to access business analytics. This section provides pre-built reports that analyze your business data without requiring manual calculations or spreadsheets.

*[Screenshot: Reports tab showing list of available reports]*

**Available Reports:**

The Reports tab offers seven different reports covering various aspects of business performance. Each report includes visual elements, filtering options, and exportable data.

---

**1. Jobs by Status Report**

Shows the distribution of jobs across the three status categories.

*[Screenshot: Jobs by Status report with bar chart]*

**Information Displayed:**
- Count of jobs in each status (Planned, InProgress, Completed)
- Total estimated value for each status category
- Percentage breakdown

**Use Cases:**
- Workload planning (how many active jobs?)
- Resource allocation (is work balanced across statuses?)
- Pipeline visibility (enough planned work?)
- Completion rate tracking

**Interpretation:**
- High "Planned" count: Good pipeline, but watch capacity
- High "InProgress" count: May indicate bottlenecks
- High "Completed" count: Good productivity, time to invoice

---

**2. Revenue Report**

Tracks financial performance over time periods.

*[Screenshot: Revenue report with line graph and date range selector]*

**Information Displayed:**
- Total amount invoiced
- Total amount paid/collected
- Outstanding balance (invoiced but unpaid)
- Collection rate percentage
- Month-over-month or year-over-year trends

**Controls:**
- Date range selector (custom start and end dates)
- Time grouping (by month, quarter, year)

**Visual Elements:**
- Line graph showing invoiced vs. paid over time
- Bar chart comparing periods

**Use Cases:**
- Monthly financial reviews
- Cash flow analysis
- Identifying slow-paying customers (high outstanding balance)
- Setting revenue targets based on trends
- Preparing financial statements

---

**3. Material Usage Report**

Lists materials by consumption patterns.

*[Screenshot: Material Usage report table sorted by quantity used]*

**Information Displayed:**
- Material ID and Name
- Category
- Total quantity used (across all jobs)
- Current stock quantity
- Total cost (quantity used × unit cost)
- Number of jobs using this material

**Sortable Columns:**
- Click any column header to sort
- Identify top 10 most-used materials
- Find highest-cost materials

**Use Cases:**
- Reorder planning (which materials used most?)
- Bulk purchase decisions (negotiate better pricing)
- Inventory optimization (stock high-usage items)
- Cost analysis (which materials drive project costs?)
- Vendor negotiations (usage volume)

---

**4. Employee Productivity Report**

Aggregates work statistics by employee.

*[Screenshot: Employee Productivity report with time period dropdown]*

**Information Displayed:**
- Employee ID and Name
- Total hours worked
- Number of jobs completed
- Total labor cost (hours × hourly rate)
- Average hours per job
- Productivity ranking

**Filters:**
- Time period dropdown (week, month, quarter, year, all-time)
- Role filter (Restorer, Fabricator, Admin)

**Use Cases:**
- Performance reviews (objective data)
- Workload balancing (is work distributed evenly?)
- Scheduling decisions (who has capacity?)
- Cost analysis (labor cost per project type)
- Bonus or incentive calculations

---

**5. Customer Activity Report**

Lists customers by engagement and revenue.

*[Screenshot: Customer Activity report sorted by total revenue]*

**Information Displayed:**
- Customer ID and Name
- Total number of jobs
- Total revenue (sum of paid invoices)
- Average job value
- Last job date
- Status indicator (Active, Inactive)

**Sorting:**
- Sort by Total Revenue to find top customers
- Sort by Last Job Date to find inactive customers

**Use Cases:**
- Identifying top customers (focus relationship management)
- Marketing prioritization (who to contact for repeat business)
- Customer retention (follow up with inactive customers)
- Pricing strategy (average job value by customer)
- Sales targeting (customers with high job frequency)

**Example Insights:**
- Customer with 15 jobs but low revenue: High-volume, low-margin
- Customer with 2 jobs but high revenue: Premium work, good margin
- Last job date 2+ years ago: Reach out to reactivate

---

**6. Low Stock Alert Report**

Real-time inventory status showing materials needing reorder.

*[Screenshot: Low Stock Alert report with critical items highlighted]*

**Information Displayed:**
- Material ID and Name
- Category
- Current stock quantity
- Reorder level threshold
- Difference (how far below threshold)
- Vendor ID and Name
- Last order date
- Status indicator (Critical, Warning)

**Sorting:**
- Materials most urgently needed at top
- Color-coded severity (red = critical, yellow = warning)

**Features:**
- **Quick PO creation buttons** (if implemented): Click to create PO for that material
- **Vendor contact info**: Quick reference for calling to order

**Use Cases:**
- Weekly inventory checks (every Monday)
- Preventing stockouts during busy periods
- Bulk ordering planning (order multiple from same vendor)
- Lead time management (order before critical)

**Best Practice:** Run this report every Monday morning and create purchase orders for any red/critical items immediately.

---

**7. Outstanding Invoices Report**

Lists all unpaid invoices with aging information.

*[Screenshot: Outstanding Invoices report with aging columns]*

**Information Displayed:**
- Invoice ID
- Customer ID and Name
- Job ID and Description
- Invoice Date
- Due Date (if applicable)
- Amount
- Days Outstanding
- Aging bucket (Current, 1-30, 31-60, 60+)

**Sortable:**
- Sort by Days Outstanding to prioritize collections
- Sort by Amount to focus on largest balances

**Visual Elements:**
- Color-coding by age (green = current, yellow = 30+, red = 60+)
- Summary totals by aging bucket

**Use Cases:**
- Accounts receivable management
- Collection prioritization (oldest first)
- Customer payment pattern analysis
- Cash flow forecasting
- Identifying problem accounts

**Example Actions:**
- 1-30 days: Send friendly payment reminder
- 31-60 days: Phone call to discuss payment
- 60+ days: Consider formal collection procedures

---

**General Report Features:**

All reports include:
- **Print button**: Print the report
- **Export button**: Save to PDF or Excel (if implemented)
- **Refresh button**: Reload with current data
- **Help button**: Explanation of report metrics

**Best Practices:**
- Run weekly: Low Stock Alert, Outstanding Invoices
- Run monthly: Revenue Report, Material Usage
- Run quarterly: Customer Activity, Employee Productivity
- Run as-needed: Jobs by Status (check anytime)

---

*This completes the Guided Tour section. For detailed step-by-step instructions on specific tasks, see the Use Cases document.*

---

## Appendix: Troubleshooting Common Issues

**Application Won't Launch**
- Ensure Java 21 or higher is installed: `java -version`
- Try running from terminal: `java -jar bmc-app-1.0.0.jar`
- Check for error messages in the console

**Database Connection Error**
- Verify you're connected to Towson University VPN
- Check internet connection
- Try reconnecting to VPN
- Wait 30 seconds and try again (server may be temporarily busy)

**Photos Not Displaying**
- Photo file may have been manually moved/deleted
- Check if file exists in `photos/job_{id}/` directory
- Try re-uploading the photo
- Verify file format (JPG, PNG, GIF, BMP only)

**Cannot Upload Large Photo**
- 10MB limit per file
- Resize or compress the image before uploading
- Use image editing software to reduce file size

**Changes Not Saving**
- Verify you clicked **Save**, not **Cancel** or **Close**
- Check for validation error messages (red text in dialog)
- Ensure all required fields are filled in
- Click **Refresh** button to reload data and verify

**Cannot Delete Record**
- Record is referenced by other data (constraint violation)
- Examples:
  - Customer has jobs → Delete jobs first
  - Material assigned to jobs → Remove from jobs first
  - Job has invoices → Delete invoices first
  - Employee has work logs → Cannot delete (data protection)

**Stock Alert Not Showing Red**
- Check if Reorder Level is set appropriately
- Edit the material → Increase Reorder Level → Save
- Stock must be at or below Reorder Level to trigger red alert

**Filter Not Working**
- Check if you clicked the **Filter** or **Submit** button
- Try clicking **Refresh** to reset
- Clear filter field and select "All" from dropdowns

**Slow Performance**
- Large photo files can slow loading
- Check internet connection speed
- Close and restart application
- Contact administrator if persistent

---

*End of User Guide*

*For step-by-step instructions on specific operations, consult the Use Cases document provided with the application.*
