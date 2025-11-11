# FRONTEND DEVELOPMENT GUIDE - FOR CHRIS

**Hi Chris!** This is your complete guide to developing the Swing UI dialogs for the Baltimore Metal Crafters application.

## Quick Setup (5 minutes)

### Step 1: Check Prerequisites
```bash
java -version    # Need Java 21
mvn -version     # Need Maven 3.9+
mysql --version  # Need MySQL 8.0+
```

### Step 2: Setup Database (Copy-paste these commands)
```bash
# Start MySQL
net start mysql80

# Login to MySQL as root
mysql -u root -p

# In MySQL console, run these (copy-paste exactly):
CREATE DATABASE bmc_db;
CREATE USER 'bmc_user'@'localhost' IDENTIFIED BY 'bmc_password123';
GRANT ALL PRIVILEGES ON bmc_db.* TO 'bmc_user'@'localhost';
FLUSH PRIVILEGES;
exit;

# Load the database
cd c:/Users/jesse/COSC457/cosc457
mysql -u bmc_user -p bmc_db < db/schema.sql
mysql -u bmc_user -p bmc_db < db/data.sql
# Password for both: bmc_password123
```

### Step 3: Run the App
```bash
cd c:/Users/jesse/COSC457/cosc457/app
mvn compile exec:java
```

**You should see a window with 4 tabs!**

## What You Need to Build

The app works but needs dialog boxes for adding/editing records. Currently clicking "Add" or "Edit" just shows "Coming Soon" messages.

### Your Tasks:
1. **Create CustomerDialog.java** - Form to add/edit customers
2. **Create EmployeeDialog.java** - Form to add/edit employees  
3. **Create JobDialog.java** - Form to add/edit jobs
4. **Create InvoiceDialog.java** - Form to add/edit invoices

### Where to Put Them:
```
app/src/main/java/org/bmc/app/ui/
├── CustomerPanel.java     (already done)
├── EmployeePanel.java     (already done)
├── JobPanel.java          (already done)
├── InvoicePanel.java      (already done)
├── MainFrame.java         (already done)
├── CustomerDialog.java    (you create this)
├── EmployeeDialog.java    (you create this)
├── JobDialog.java         (you create this)
└── InvoiceDialog.java     (you create this)
```

## Dialog Template (Start With This!)

### CustomerDialog.java
```java
package org.bmc.app.ui;

import org.bmc.app.dao.CustomerDAO;
import org.bmc.app.model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerDialog extends JDialog {
    private CustomerDAO customerDAO = new CustomerDAO();
    private Customer customer;
    private boolean saved = false;
    
    // Form fields
    private JTextField companyField = new JTextField(20);
    private JTextField contactField = new JTextField(20);
    private JTextField phoneField = new JTextField(15);
    private JTextField emailField = new JTextField(25);
    private JTextField addressField = new JTextField(30);
    
    public CustomerDialog(Frame parent, Customer customer) {
        super(parent, customer == null ? "Add Customer" : "Edit Customer", true);
        this.customer = customer;
        
        setupUI();
        if (customer != null) {
            fillFields();
        }
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add form fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Company:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(companyField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(contactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(addressField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveCustomer());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(getParent());
    }
    
    private void fillFields() {
        companyField.setText(customer.getCompanyName());
        contactField.setText(customer.getContactName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
        addressField.setText(customer.getAddress());
    }
    
    private void saveCustomer() {
        try {
            // Validate
            if (companyField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Company name is required!");
                return;
            }
            
            if (customer == null) {
                // New customer
                Customer newCustomer = new Customer(
                    companyField.getText().trim(),
                    contactField.getText().trim(),
                    phoneField.getText().trim(),
                    emailField.getText().trim(),
                    addressField.getText().trim()
                );
                customerDAO.create(newCustomer);
            } else {
                // Update existing
                customer.setCompanyName(companyField.getText().trim());
                customer.setContactName(contactField.getText().trim());
                customer.setPhone(phoneField.getText().trim());
                customer.setEmail(emailField.getText().trim());
                customer.setAddress(addressField.getText().trim());
                customerDAO.update(customer);
            }
            
            saved = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public boolean wasSaved() {
        return saved;
    }
}
```

## Connect Dialog to Panel

In CustomerPanel.java, replace the `addCustomer()` and `editCustomer()` methods:

```java
private void addCustomer() {
    CustomerDialog dialog = new CustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
    dialog.setVisible(true);
    if (dialog.wasSaved()) {
        refreshData(); // Reload the table
    }
}

private void editCustomer() {
    int selectedRow = customerTable.getSelectedRow();
    if (selectedRow == -1) return;
    
    Long customerId = (Long) tableModel.getValueAt(selectedRow, 0);
    try {
        Customer customer = customerDAO.findById(customerId);
        CustomerDialog dialog = new CustomerDialog((Frame) SwingUtilities.getWindowAncestor(this), customer);
        dialog.setVisible(true);
        if (dialog.wasSaved()) {
            refreshData(); // Reload the table
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading customer: " + e.getMessage());
    }
}
```

## Dialog Requirements for Each Entity

### EmployeeDialog.java Fields:
- First Name (JTextField)
- Last Name (JTextField)
- Role (JComboBox with: "manager", "restorer", "fabricator", "consultant")
- Email (JTextField)
- Phone (JTextField)
- Hire Date (JTextField - format: YYYY-MM-DD)

### JobDialog.java Fields:
- Customer ID (JComboBox populated from CustomerDAO.findAll())
- Description (JTextArea)
- Status (JComboBox with: "Pending", "InProgress", "Completed", "OnHold", "Cancelled")
- Start Date (JTextField - format: YYYY-MM-DD)
- Due Date (JTextField - format: YYYY-MM-DD)
- Estimated Cost (JTextField - decimal number)

### InvoiceDialog.java Fields:
- Job ID (JComboBox populated from JobDAO.findAll())
- Amount (JTextField - decimal number)
- Date Issued (JTextField - format: YYYY-MM-DD)
- Due Date (JTextField - format: YYYY-MM-DD)
- Is Paid (JCheckBox)
- Payment Date (JTextField - format: YYYY-MM-DD, enable only if paid)

## Test Your Work

1. Run the app: `mvn compile exec:java`
2. Click "Add Customer" - your dialog should open
3. Fill the form and click "Save"
4. The table should refresh with your new customer!

## Quick Help

**Common Issues:**
- **Can't connect to database**: Run the database setup commands again
- **Compilation errors**: Make sure you're in the `app` folder
- **Dialog doesn't show**: Check your layout code
- **Data doesn't save**: Check for exceptions in console

**Pro Tips:**
- Copy the CustomerDialog template for other dialogs
- Change the fields for each entity type
- Test after each dialog you create
- Look at the existing model classes to see what fields each entity has

## Working with Bryan

Bryan is testing the application. When you finish the dialogs:
1. Let Bryan know it's ready to test
2. He'll try to break it and give you feedback
3. Fix any issues he finds
4. Move on to the next dialog

