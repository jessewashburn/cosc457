# Baltimore Metal Crafters Database System
## Use Cases Guide

**Copyright © 2025 Baltimore Metal Crafters**  
**Version 1.0.0**  
**Date: November 29, 2025**

---

## Table of Contents

**1 COMMON OPERATIONS FOR ALL USERS** ................................................................................... 3  
&nbsp;&nbsp;&nbsp;&nbsp;1.1 LAUNCHING THE APPLICATION ................................................................................................. 3  
&nbsp;&nbsp;&nbsp;&nbsp;1.2 SEARCHING AND FILTERING DATA ............................................................................................ 4  

**2 CUSTOMER MANAGEMENT** ..................................................................................................... 5  
&nbsp;&nbsp;&nbsp;&nbsp;2.1 ADDING A NEW CUSTOMER ...................................................................................................... 5  
&nbsp;&nbsp;&nbsp;&nbsp;2.2 UPDATING CUSTOMER CONTACT INFORMATION ........................................................................ 6  
&nbsp;&nbsp;&nbsp;&nbsp;2.3 FINDING A CUSTOMER ............................................................................................................. 7  

**3 JOB MANAGEMENT** .................................................................................................................. 8  
&nbsp;&nbsp;&nbsp;&nbsp;3.1 CREATING A NEW JOB ............................................................................................................. 8  
&nbsp;&nbsp;&nbsp;&nbsp;3.2 ASSIGNING MATERIALS TO A JOB ............................................................................................ 10  
&nbsp;&nbsp;&nbsp;&nbsp;3.3 UPDATING JOB STATUS ......................................................................................................... 12  
&nbsp;&nbsp;&nbsp;&nbsp;3.4 UPLOADING JOB PHOTOS ....................................................................................................... 13  
&nbsp;&nbsp;&nbsp;&nbsp;3.5 VIEWING JOB PHOTOS ............................................................................................................ 15  

**4 INVENTORY MANAGEMENT** ................................................................................................... 16  
&nbsp;&nbsp;&nbsp;&nbsp;4.1 ADDING A NEW MATERIAL ...................................................................................................... 16  
&nbsp;&nbsp;&nbsp;&nbsp;4.2 CHECKING STOCK LEVELS ..................................................................................................... 17  
&nbsp;&nbsp;&nbsp;&nbsp;4.3 REORDERING LOW-STOCK MATERIALS ................................................................................... 18  

**5 FINANCIAL OPERATIONS** ....................................................................................................... 19  
&nbsp;&nbsp;&nbsp;&nbsp;5.1 CREATING AN INVOICE ........................................................................................................... 19  
&nbsp;&nbsp;&nbsp;&nbsp;5.2 MARKING AN INVOICE AS PAID .............................................................................................. 21  
&nbsp;&nbsp;&nbsp;&nbsp;5.3 RECEIVING A PURCHASE ORDER ............................................................................................ 22  

---

## 1 Common Operations for All Users

### 1.1 Launching the Application

**Prerequisites:**
- Towson University VPN connection active
- Java 21+ installed

**Steps:**

1) Ensure you are connected to Towson VPN:
   - Open **Cisco AnyConnect Secure Mobility Client**
   - Connect to `vpnc.towson.edu`
   - Login with NetID and password

*[Screenshot: VPN connection dialog]*

2) Navigate to the application folder

3) Double-click `bmc-app-1.0.0.jar` to launch

**Alternative method:**
```bash
java -jar bmc-app-1.0.0.jar
```

*[Screenshot: Application launching]*

4) Wait 2-5 seconds for the main window to appear

5) Verify you see the seven tabs: Customers, Employees, Jobs, Invoices, Materials, Purchase Orders, Reports

*[Screenshot: Main window with tabs visible]*

**Troubleshooting:**
- If connection errors occur, verify VPN is connected
- If application doesn't launch, check Java version: `java -version`

---

### 1.2 Searching and Filtering Data

**Using Table Sorting:**

1) Click any column header to sort by that column

*[Screenshot: Column header being clicked]*

2) Click again to reverse sort order (ascending ↔ descending)

**Using Status Filters (Jobs tab):**

1) Locate the **Status** dropdown in the toolbar

2) Select the desired status:
   - All
   - Planned
   - InProgress
   - Completed

*[Screenshot: Status dropdown expanded]*

3) Table updates immediately

**Using Customer Filter (Jobs tab):**

1) Enter a Customer ID in the **Customer ID** field

2) Click the **Filter** button

3) Only jobs for that customer are displayed

**Clearing Filters:**

1) Select "All" from dropdown filters

2) Clear any text fields

3) Click **Refresh** button

---

## 2 Customer Management

### 2.1 Adding a New Customer

**Scenario:** A new customer calls requesting a custom iron gate. You need to add them to the system before creating their job.

**Steps:**

1) Click the **Customers** tab in the top navigation

*[Screenshot: Customers tab selected]*

2) Click the **Add Customer** button in the toolbar

3) The Add Customer dialog appears

*[Screenshot: Add Customer dialog]*

4) Enter the customer information:
   - **Name:** Baltimore Historical Society
   - **Phone:** 410-555-0123
   - **Email:** contact@baltimorehist.org
   - **Address:** 100 Monument St, Baltimore, MD 21201

*[Screenshot: Form with sample data filled in]*

5) Click **Save**

6) The dialog closes and the new customer appears in the table

7) **Note the Customer ID** displayed in the table (you'll need this for creating jobs)

*[Screenshot: New customer row highlighted with Customer ID visible]*

**Verification:**
- New customer appears at the bottom of the table (or top if sorted by ID descending)
- Customer ID is assigned automatically (e.g., Customer ID: 15)

---

### 2.2 Updating Customer Contact Information

**Scenario:** A customer has moved to a new address. You need to update their record.

**Steps:**

1) Go to the **Customers** tab

2) Locate the customer in the table:
   - Scroll through the list, or
   - Click the **Name** column header to sort alphabetically

*[Screenshot: Customer table sorted by name]*

3) Click on the customer's row to select it (row highlights)

4) Click the **Edit** button in the toolbar

*[Screenshot: Edit button enabled after selection]*

5) The Edit Customer dialog appears with current information

6) Update the address field:
   - **Old:** 100 Monument St, Baltimore, MD 21201
   - **New:** 250 Cathedral St, Baltimore, MD 21201

*[Screenshot: Edit dialog with address field modified]*

7) Click **Save**

8) Verify the change appears in the table

**Note:** You can also double-click a row to open the Edit dialog directly.

---

### 2.3 Finding a Customer

**Scenario:** You need to find customer "Smith Metalworks" quickly in a table with 50+ customers.

**Steps:**

1) Go to the **Customers** tab

2) Click the **Name** column header to sort alphabetically

*[Screenshot: Name column header]*

3) Scroll to the "S" section of the table

4) Locate "Smith Metalworks"

**Alternative - Using Job Filter:**

If you remember they have Job #47:

1) Go to **Jobs** tab

2) Sort by **Customer ID** column

3) Note the Customer ID for Job #47

4) Return to **Customers** tab

5) Sort by **Customer ID** and locate that ID

---

## 3 Job Management

### 3.1 Creating a New Job

**Scenario:** Customer #15 (Baltimore Historical Society) has approved your quote for a custom iron gate. Create a job to track this project.

**Steps:**

1) Go to the **Jobs** tab

*[Screenshot: Jobs tab]*

2) Click **Add Job** in the toolbar

3) The Add Job dialog appears

*[Screenshot: Add Job dialog blank]*

4) Fill in the job details:
   - **Customer ID:** 15
   - **Description:** Custom iron gate - Victorian style with ornamental scrollwork
   - **Quote ID:** (leave blank if none)
   - **Start Date:** Click calendar icon, select December 1, 2025
   - **Due Date:** Click calendar icon, select December 20, 2025
   - **Status:** Planned (default)

*[Screenshot: Add Job dialog with data filled]*

5) Click **Save**

6) The new job appears in the Jobs table

7) **Note the Job ID** assigned (e.g., Job ID: 23)

*[Screenshot: New job row highlighted]*

**Verification:**
- Job appears in table with Status "Planned"
- Customer ID shows as 15
- Dates display correctly

**Important:** You cannot upload photos when creating a job. Photos can only be added after the job is saved and has a Job ID.

---

### 3.2 Assigning Materials to a Job

**Scenario:** Job #23 (custom iron gate) requires materials. Assign steel, primer, and paint from inventory.

**Steps:**

1) Go to the **Jobs** tab

2) Locate Job #23 in the table (may need to scroll)

3) Click on the job row to select it

*[Screenshot: Job #23 selected]*

4) Click **Manage Materials** button in toolbar

5) The Job Materials dialog opens

*[Screenshot: Job Materials dialog - empty table]*

**Assigning the First Material:**

6) Click **Assign Material** button

7) The Assign Material dialog appears

*[Screenshot: Assign Material dialog]*

8) Select material from dropdown:
   - **Material:** Steel flat bar 1/2" x 2" (Material ID: 5)

*[Screenshot: Material dropdown expanded]*

9) Enter quantity:
   - **Quantity:** 20

10) Click **Assign**

11) The material appears in the top table

12) Verify stock quantity decreased automatically

*[Screenshot: Material assigned, stock updated]*

**Assigning Additional Materials:**

13) Click **Assign Material** again

14) Select: Primer - gray (Material ID: 12)

15) Enter quantity: 3

16) Click **Assign**

17) Repeat for: Black enamel paint (Material ID: 18), Quantity: 2

**Final Step:**

18) Review all assigned materials in the table:
    - Steel flat bar: 20 units
    - Primer - gray: 3 units
    - Black enamel paint: 2 units

*[Screenshot: Job Materials table with three materials listed]*

19) Click **Close**

**Verification:**
- Go to **Materials** tab
- Check that stock quantities decreased for the three materials
- Return to Jobs tab

---

### 3.3 Updating Job Status

**Scenario:** Work on Job #23 (iron gate) has begun. Update status from "Planned" to "InProgress".

**Steps:**

1) Go to the **Jobs** tab

2) Locate Job #23

3) Select the job by clicking its row

4) Click **Edit** button in toolbar

*[Screenshot: Edit button]*

5) The Edit Job dialog opens (two-panel layout)

*[Screenshot: Edit Job dialog]*

6) In the left panel, locate the **Status** dropdown

7) Click the Status dropdown

8) Select **InProgress**

*[Screenshot: Status dropdown with InProgress selected]*

9) Click **Save**

10) The dialog closes

**Verification:**
- Job #23 now shows "InProgress" in the Status column

*[Screenshot: Jobs table showing Job #23 with InProgress status]*

**Filtering to See Only Active Jobs:**

11) Click the Status dropdown in the toolbar

12) Select **InProgress**

13) Table now shows only active jobs

*[Screenshot: Filtered table showing only InProgress jobs]*

---

### 3.4 Uploading Job Photos

**Scenario:** You've completed initial fabrication on Job #23 and need to document progress with photos.

**Prerequisites:**
- Job must already exist and have a Job ID
- Photos must be JPG, PNG, GIF, or BMP format
- Files must be under 10MB each

**Steps:**

1) Go to the **Jobs** tab

2) Select Job #23

3) Click **Edit** (or double-click the row)

4) The Edit Job dialog opens with two panels

*[Screenshot: Edit Job dialog showing left panel (job details) and right panel (photos)]*

5) Look at the **right panel** labeled "Job Photos"

**Uploading First Photo:**

6) Click **Upload Photo** button in right panel

*[Screenshot: Upload Photo button highlighted]*

7) File browser opens

*[Screenshot: File browser dialog]*

8) Navigate to your photos folder

9) Select file: `gate_fabrication_01.jpg`

10) Click **Open**

11) Description dialog may appear - enter:
    - "Initial frame assembly complete"

*[Screenshot: Description dialog]*

12) The photo uploads and thumbnail appears

*[Screenshot: Thumbnail in photo panel]*

**Uploading Additional Photos:**

13) Click **Upload Photo** again

14) Select: `gate_scrollwork_detail.jpg`

15) Description: "Ornamental scrollwork in progress"

16) Thumbnail appears

17) Repeat for: `gate_welding_closeup.jpg`

18) Description: "Weld joints - quality check"

**Final Step:**

19) Verify all three thumbnails are visible in the right panel

*[Screenshot: Right panel with three photo thumbnails]*

20) Click **Save** on the job dialog

**Verification:**
- Photos are saved to disk at `photos/job_23/`
- Thumbnails remain visible when you edit the job again

**Note:** Photos are saved immediately upon upload, but clicking Save on the job dialog is still required to finalize.

---

### 3.5 Viewing Job Photos

**Scenario:** A customer calls asking about progress on their gate (Job #23). View photos without risking accidental changes.

**Steps:**

1) Go to the **Jobs** tab

2) Select Job #23 by clicking the row

3) Click **View Photos** button in toolbar

*[Screenshot: View Photos button]*

4) The Photo Gallery dialog opens

*[Screenshot: Photo Gallery showing 3-column grid]*

**Gallery Features:**
- Title shows "Photos for Job #23"
- Photos displayed in 3-column grid (250×200px thumbnails)
- Each photo shows filename and description below image
- Scroll bar for many photos

5) Review the three photos:
   - Initial frame assembly complete
   - Ornamental scrollwork in progress
   - Weld joints - quality check

**Viewing Full Size:**

6) Click on any photo thumbnail

7) The Photo Viewer opens showing full-size image (up to 800×600px)

*[Screenshot: Photo Viewer with full-size image]*

8) Click **Close** or press **ESC** to return to gallery

9) Click on another photo to view it

10) Press **ESC** to return to gallery

**Closing Gallery:**

11) Click the **Close** button on the Photo Gallery

12) You return to the Jobs tab

**Note:** Gallery mode is read-only - you cannot upload or delete photos. Use Edit mode for photo management.

---

## 4 Inventory Management

### 4.1 Adding a New Material

**Scenario:** You've started using a new type of brass rod that isn't in the system. Add it to inventory.

**Steps:**

1) Go to the **Materials** tab

*[Screenshot: Materials tab]*

2) Click **Add Material** in toolbar

3) The Add Material dialog appears

*[Screenshot: Add Material dialog]*

4) Enter material information:
   - **Name:** Brass rod 3/8" diameter
   - **Category:** Metals
   - **Stock Quantity:** 50
   - **Reorder Level:** 10
   - **Unit Cost:** 8.50
   - **Vendor ID:** 2

*[Screenshot: Form filled with brass rod data]*

5) Click **Save**

6) The new material appears in the table

*[Screenshot: Materials table with new brass rod entry]*

**Verification:**
- Material has been assigned a Material ID (e.g., ID: 24)
- Stock Quantity shows 50
- Row is NOT highlighted in red (stock above reorder level)

---

### 4.2 Checking Stock Levels

**Scenario:** It's Monday morning. Check which materials need reordering before you start new jobs this week.

**Steps:**

1) Go to the **Materials** tab

2) Scan the table for **red-highlighted rows**

*[Screenshot: Materials table with some red rows]*

3) Red rows indicate materials at or below reorder level

**Example findings:**
- Steel sheet 16ga: Stock 4, Reorder Level 10 → **RED**
- Gold leaf 23k: Stock 2, Reorder Level 5 → **RED**
- Brass rod 3/8": Stock 50, Reorder Level 10 → Normal

4) Note the Material IDs and Vendor IDs for red items:
   - Material ID 3 (Steel sheet), Vendor ID 1
   - Material ID 7 (Gold leaf), Vendor ID 3

**Alternative - Using Reports:**

5) Go to **Reports** tab

6) Select **Low Stock Alert Report**

*[Screenshot: Low Stock Alert Report]*

7) Report lists materials below reorder level with:
   - How far below threshold
   - Vendor information
   - Recommended action

**Next Step:** Create purchase orders for low-stock items (see Section 4.3)

---

### 4.3 Reordering Low-Stock Materials

**Scenario:** Steel sheet 16ga (Material ID: 3) is at critical stock (4 units, reorder level 10). Order more from Vendor #1.

**Steps:**

1) Go to **Purchase Orders** tab

*[Screenshot: Purchase Orders tab]*

2) Click **Add Purchase Order**

3) Select **Vendor** from dropdown:
   - Vendor: Eastern Metal Supply (Vendor ID: 1)

*[Screenshot: Vendor dropdown expanded]*

4) Order Date defaults to today (read-only)

5) Status defaults to "Pending"

**Adding Line Items:**

6) In the line items grid, first row:
   - **Material:** Steel sheet 16ga (Material ID: 3)
   - **Quantity:** 20 (enough to exceed reorder level)
   - **Unit Price:** 45.00 (check current vendor pricing)

*[Screenshot: Line item filled in]*

7) Total calculates automatically: $900.00

**Adding a Second Item:**

If ordering multiple items from same vendor:

8) Second row:
   - **Material:** Steel flat bar 1/2" x 2" (Material ID: 5)
   - **Quantity:** 30
   - **Unit Price:** 12.75

9) Total updates: $1,282.50

*[Screenshot: Two line items with total]*

10) Review the **Total Cost** at bottom

11) Click **Save**

**Verification:**
- Purchase Order appears in table with Status "Pending" (yellow)
- Note the PO ID (e.g., PO #8)

*[Screenshot: PO table showing new order with yellow Pending status]*

---

## 5 Financial Operations

### 5.1 Creating an Invoice

**Scenario:** Job #23 (custom iron gate) is complete. Create an invoice for $2,400.

**Prerequisites:**
- Job must exist (Job #23)
- Calculate total before creating invoice

**Calculating the Invoice Total:**

**Step 1 - Materials Cost:**

1) Go to Jobs tab

2) Select Job #23

3) Click **Manage Materials**

4) Review materials and quantities:
   - Steel flat bar: 20 @ $12.75 = $255.00
   - Primer: 3 @ $18.00 = $54.00
   - Paint: 2 @ $25.00 = $50.00
   - **Materials Total: $359.00**

5) Click Close

**Step 2 - Add Labor and Markup:**

Assume:
- Labor: 24 hours @ $45/hr = $1,080.00
- Overhead: 20% = $287.80
- Profit margin: 30% = $431.70

**Total: $2,400.00** (rounded)

**Creating the Invoice:**

1) Go to **Invoices** tab

*[Screenshot: Invoices tab]*

2) Click **Add Invoice**

3) The Add Invoice dialog appears

*[Screenshot: Add Invoice dialog]*

4) Enter invoice information:
   - **Job ID:** 23
   - **Total Amount:** 2400.00
   - **Invoice Date:** (defaults to today)
   - **Paid:** (leave unchecked)

*[Screenshot: Form filled with invoice data]*

5) Click **Save**

6) Invoice appears in table

*[Screenshot: Invoices table with new invoice]*

**Verification:**
- Invoice ID assigned (e.g., Invoice #15)
- Job ID shows 23
- Total Amount shows $2,400.00
- Paid column shows "No"

---

### 5.2 Marking an Invoice as Paid

**Scenario:** Customer for Invoice #15 (Job #23) has sent payment via check. Mark the invoice as paid.

**Steps:**

1) Go to **Invoices** tab

2) Locate Invoice #15 in the table

*[Screenshot: Invoice #15 in table with Paid: No]*

3) Click the row to select it

4) Click **Edit** button in toolbar

*[Screenshot: Edit button]*

5) The Edit Invoice dialog opens

*[Screenshot: Edit Invoice dialog showing unpaid invoice]*

6) Locate the **Paid** checkbox

7) **Check the box** to mark as paid

*[Screenshot: Paid checkbox checked]*

8) Click **Save**

9) The invoice now shows "Yes" in the Paid column

*[Screenshot: Invoice table showing Paid: Yes]*

**Verification:**
- Paid status changed from "No" to "Yes"
- Invoice ID and amount remain unchanged

**Filtering Unpaid Invoices:**

To see only outstanding bills:

10) Use table sorting - click **Paid** column header

11) All "No" entries group together

*[Screenshot: Table sorted by Paid status]*

---

### 5.3 Receiving a Purchase Order

**Scenario:** The steel shipment from PO #8 has arrived. Receive the purchase order to update inventory.

**Prerequisites:**
- PO must exist (PO #8)
- Materials must be physically received and verified

**Steps:**

1) **Verify the shipment:**
   - Check packing slip matches PO #8
   - Count quantities (Steel sheet: 20, Steel flat bar: 30)
   - Inspect for damage

2) Go to **Purchase Orders** tab

*[Screenshot: Purchase Orders tab with PO #8 highlighted in yellow]*

3) Locate PO #8 (yellow background = Pending status)

4) Select the row by clicking it

5) Click **Edit** button

*[Screenshot: Edit button]*

6) The Edit Purchase Order dialog opens

*[Screenshot: Edit PO dialog showing Pending status]*

7) Verify the line items match what you received:
   - Steel sheet 16ga: 20 units
   - Steel flat bar 1/2" x 2": 30 units

8) Locate the **Status** dropdown

9) Click the dropdown and select **Received**

*[Screenshot: Status dropdown with Received selected]*

10) Click **Save**

**What Happens Automatically:**

The system immediately:
- Updates inventory:
  - Steel sheet stock: 4 + 20 = **24 units**
  - Steel flat bar stock: 15 + 30 = **45 units**
- Clears red low-stock alerts (if stock now above reorder level)
- Changes PO status to "Received" with green background

*[Screenshot: Materials tab showing updated stock quantities, red alerts cleared]*

**Verification:**

11) Go to **Materials** tab

12) Locate Steel sheet 16ga (Material ID: 3)

13) Verify Stock Quantity now shows 24 (was 4)

14) Verify row is NO LONGER red (24 > reorder level of 10)

15) Return to **Purchase Orders** tab

16) Verify PO #8 shows **green background** (Received status)

*[Screenshot: PO table with PO #8 showing green Received status]*

**Important:** Only mark PO as Received after you've physically counted and verified the materials. Do not mark based on shipping notification alone.

---

## Appendix: Quick Reference

**Common Button Actions:**
- **Add:** Create new record
- **Edit:** Modify selected record
- **Delete:** Remove selected record (with confirmation)
- **Save:** Commit changes and close dialog
- **Cancel/Close:** Exit without saving
- **Refresh:** Reload data from database

**Job Status Progression:**
1. **Planned** → Job created, materials assigned
2. **InProgress** → Work has started, upload progress photos
3. **Completed** → Work finished, create invoice

**Inventory Alerts:**
- **Red row:** Stock at or below reorder level → Create PO
- **Normal row:** Stock sufficient

**Purchase Order Status:**
- **Yellow (Pending):** Order placed, awaiting delivery
- **Green (Received):** Delivered, inventory updated
- **Gray (Cancelled):** Order voided

**Photo Management:**
- **Upload:** Edit mode only (Edit Job dialog, right panel)
- **View Gallery:** View Photos button (read-only, 3-column grid)
- **View Full-Size:** Click any thumbnail in Edit or Gallery mode
- **Delete:** Edit mode only (X button on thumbnail)

---

*End of Use Cases Guide*

*For overview and feature descriptions, consult the User Guide provided with the application.*
