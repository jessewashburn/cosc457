# Baltimore Metal Crafters DB App

A Java Swing application backed by MySQL that manages customers, employees, quotes, jobs, services, materials, invoices, and work logs for a custom metal restoration shop.

## Features

* Customer and employee management
* Item and service tracking
* Quotes that convert to jobs with staged workflows
* Materials inventory and vendor purchase orders
* Invoices and payments with basic aging
* Reports for jobs due soon, material shortages, revenue, and repeat clients

## Tech stack

* Java 21, Swing (UI)
* MySQL 8.x, JDBC
* Maven (build)

## Repo structure

```
.
├── app/
│   ├── src/main/java/
│   │   └── org/bmc/app/            # Swing UI and controllers
│   │       ├── Main.java           # App entry point
│   │       ├── ui/                 # Frames, panels, dialogs
│   │       ├── dao/                # JDBC DAOs
│   │       ├── model/              # POJOs
│   │       └── util/               # DB connection, config
│   └── src/main/resources/
│       └── application.properties  # DB settings
├── db/
│   ├── schema.sql                  # DDL
│   ├── data.sql                    # Seed data
│   └── queries.sql                 # Showcase queries
├── docs/
│   ├── ERD.png                     # ERD image placeholder
│   ├── Report1.md                  # Class report draft
│   └── screenshots/                # UI screenshots for Report 2
├── pom.xml
└── README.md
```

## Getting started

### 1) Prerequisites

* Java 21
* Maven 3.9+
* MySQL 8.x
* A MySQL user with permission to create a schema

### 2) Create database and seed data

Update credentials as needed, then:

```bash
mysql -u root -p -e "CREATE DATABASE bmc_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;"
mysql -u root -p bmc_db < db/schema.sql
mysql -u root -p bmc_db < db/data.sql
```

### 3) Configure the app

Create `app/src/main/resources/application.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/bmc_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=your_password

# optional UI settings
app.title=Baltimore Metal Crafters
```

### 4) Build and run

```bash
cd app
mvn clean package
java -jar target/bmc-app-1.0.0.jar
```

Or run from sources:

```bash
mvn exec:java -Dexec.mainClass="org.bmc.app.Main"
```

## Sample SQL objects

Tables (defined in `db/schema.sql`):

* `customer`, `quote`, `quote_item`
* `job`, `job_stage`, `work_log`
* `employee`, `service`, `material`, `job_material`
* `vendor`, `purchase_order`, `po_item`
* `invoice`, `payment`

Example queries (in `db/queries.sql`):

```sql
-- Jobs due in the next 7 days
SELECT j.job_id, c.name AS customer, j.due_date, j.status
FROM job j
JOIN customer c ON c.customer_id = j.customer_id
WHERE j.status IN ('Planned','InProgress')
  AND j.due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
ORDER BY j.due_date;

-- Employee workload summary
SELECT e.employee_id, e.name, SUM(w.hours_worked) AS total_hours
FROM employee e
JOIN work_log w ON w.employee_id = e.employee_id
GROUP BY e.employee_id, e.name
ORDER BY total_hours DESC;
```

## App overview

* **Customers**: CRUD and job history
* **Employees**: CRUD and workload tracking
* **Quotes**: header plus line items; convert to job
* **Jobs**: stages tab, materials tab, work logs tab
* **Inventory**: materials with reorder alerts
* **Billing**: invoices, payments, aging report
* **Reports**: jobs due soon, shortages, revenue, repeat customers, employee utilization
