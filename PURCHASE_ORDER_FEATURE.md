# Purchase Order Management Feature

## Overview
Complete Purchase Order management system added to the Baltimore Metal Crafters application. Allows full CRUD operations for purchase orders with line items.

## Components Created

### 1. Model Classes
- **PurchaseOrder.java** (`org.bmc.app.model`)
  - Fields: poId, vendorId, vendorName, orderDate, totalCost, status
  - Represents a purchase order from a vendor
  
- **POItem.java** (`org.bmc.app.model`)
  - Fields: poItemId, poId, materialId, materialName, quantity, unitPrice
  - Method: `getLineTotal()` - calculates quantity × unitPrice
  - Represents individual line items in a purchase order

### 2. Data Access Objects (DAOs)
- **PurchaseOrderDAO.java** (`org.bmc.app.dao`)
  - `findAll()` - Returns all POs with vendor names (JOINs Vendor table)
  - `findById(Integer poId)` - Find specific PO
  - `save(PurchaseOrder po)` - Create new PO, returns auto-generated ID
  - `update(PurchaseOrder po)` - Update existing PO
  - `delete(Integer poId)` - Delete PO
  - `search(String keyword)` - Search by vendor name, status, or PO ID
  - `findByVendor(Integer vendorId)` - Filter by vendor
  
- **POItemDAO.java** (`org.bmc.app.dao`)
  - `findByPurchaseOrder(Integer poId)` - Get all line items for a PO (JOINs Material table)
  - `save(POItem item)` - Add line item
  - `update(POItem item)` - Update line item
  - `delete(Integer poItemId)` - Delete single line item
  - `deleteByPurchaseOrder(Integer poId)` - Delete all items for a PO (used before update)

### 3. User Interface Components
- **PurchaseOrderDialog.java** (`org.bmc.app.ui`)
  - Modal dialog for creating/editing purchase orders
  - **Vendor Selection**: Dropdown populated from VendorDAO
  - **Order Date**: Text field with yyyy-MM-dd format validation
  - **Status**: Dropdown (Pending, Received, Cancelled)
  - **Line Items Table**: Shows Material, Quantity, Unit Price, Line Total
  - **Add Item Button**: Opens sub-dialog with:
    - Material dropdown (populated from MaterialDAO)
    - Quantity spinner (1-9999)
    - Unit price text field
  - **Remove Item Button**: Deletes selected line item
  - **Total Cost**: Auto-calculated sum of all line items (read-only)
  - **Validation**: Ensures vendor selected, date formatted correctly
  
- **PurchaseOrderPanel.java** (`org.bmc.app.ui`)
  - Main panel displaying all purchase orders in table format
  - **Table Columns**: PO #, Vendor, Order Date, Total Cost, Status
  - **Toolbar Buttons**:
    - **Add Purchase Order**: Opens dialog to create new PO
    - **Edit**: Opens dialog to modify selected PO
    - **Delete**: Removes PO and all line items (with confirmation)
    - **View Items**: Shows read-only table of line items for selected PO
    - **Refresh**: Reloads data from database
  - **Search**: Text field to search by vendor name, status, or PO ID
  - **Features**:
    - Double-click row to edit
    - Auto-sorting by clicking column headers
    - Status bar showing operation status

### 4. Integration
- **MainFrame.java** - Updated to include:
  - New "Purchase Orders" tab (between Materials and Reports)
  - Tab mnemonic: Alt+P
  - Refresh integration in menu

## How to Use

### Adding a Purchase Order
1. Click **Add Purchase Order** button
2. Select vendor from dropdown
3. Enter order date (format: yyyy-MM-dd)
4. Select status (typically "Pending" for new orders)
5. Click **Add Item** to add line items:
   - Select material
   - Enter quantity
   - Enter unit price
   - Click OK
6. Repeat step 5 for all items
7. Total cost auto-calculates
8. Click **Save** to create the purchase order

### Editing a Purchase Order
1. Select a row in the table (or double-click)
2. Click **Edit** button
3. Modify fields as needed
4. Add/remove line items
5. Click **Save** to update

### Viewing Line Items
1. Select a row in the table
2. Click **View Items** button
3. Read-only dialog shows all materials, quantities, prices

### Deleting a Purchase Order
1. Select a row in the table
2. Click **Delete** button
3. Confirm deletion (removes PO and all line items)

### Searching
1. Enter search term in search field (vendor name, status, or PO ID)
2. Click **Search** or press Enter
3. Clear search field and click Search to show all

## Database Schema Used

### PurchaseOrder Table
- `po_id` (INT, Primary Key, Auto-increment)
- `vendor_id` (INT, Foreign Key → Vendor)
- `order_date` (DATE)
- `total_cost` (DECIMAL(10,2))
- `status` (VARCHAR)

### POItem Table
- `po_item_id` (INT, Primary Key, Auto-increment)
- `po_id` (INT, Foreign Key → PurchaseOrder)
- `material_id` (INT, Foreign Key → Material)
- `quantity` (INT)
- `unit_price` (DECIMAL(10,2))

## Testing Recommendations

1. **Create a Purchase Order**:
   - Test with multiple line items
   - Verify total cost auto-calculates correctly
   - Check that vendor name displays in main table

2. **Edit a Purchase Order**:
   - Modify existing line items
   - Add new items
   - Remove items
   - Verify changes persist after save

3. **Delete a Purchase Order**:
   - Confirm line items also deleted
   - Check vendor spending report updates

4. **Search Functionality**:
   - Search by vendor name
   - Search by status
   - Search by PO ID

5. **Vendor Spending Report**:
   - Navigate to Reports → "Vendor Spending by Month"
   - Add new POs with different dates
   - Verify report shows updated PO counts and totals

## Build Status
✅ All classes compiled successfully
✅ Maven build: SUCCESS
✅ No compilation errors
✅ JAR packaged: `target/bmc-app-1.0.0.jar`

## Run Application
```bash
cd /c/Users/jesse/cosc457/app
java -jar target/bmc-app-1.0.0.jar
```

Navigate to the **Purchase Orders** tab to access the new feature.
