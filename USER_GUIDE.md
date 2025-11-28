# BMC Database Management System - User Guide

## Quick Start

### 1. Connect to Towson VPN (REQUIRED)

**Download & Install:**
- Go to https://vpnc.towson.edu
- Login with your NetID and password
- Download and install Cisco AnyConnect

**Connect:**
1. Open **Cisco AnyConnect Secure Mobility Client**
2. Enter: `vpnc.towson.edu`
3. Click **Connect**
4. Login with your NetID and password
5. Authenticate with Duo if prompted
6. Accept the warning message

**Important:** You MUST be connected to VPN before running the application.

### 2. Run the Application

**Option 1: Double-click the JAR file**
- Simply double-click `bmc-app-1.0.0.jar` to launch

**Option 2: Run from command line**
```bash
java -jar bmc-app-1.0.0.jar
```

**Requirements:** Java 25 or higher

### 3. Use the Application

- **Customers Tab**: View, add, edit, delete customer records
- **Employees Tab**: Manage employee information
- **Jobs Tab**: Track restoration projects
- **Invoices Tab**: Handle billing and payments

**Search**: Type in the search box and click "Search" to filter records

**Add**: Click "Add" button to create new records

**Edit**: Select a row, click "Edit" to modify

**Delete**: Select a row, click "Delete" to remove

## Troubleshooting

**"Communication link failure"**
- Make sure VPN is connected
- Reconnect to VPN if needed

**"Access denied"**
- Contact instructor for database credentials

**"UnsupportedClassVersionError"**
- Install Java 25 or higher

## Support

- **VPN Issues**: https://ots.towson.edu/vpn or call 410-704-5151
- **Database Access**: Contact your instructor
