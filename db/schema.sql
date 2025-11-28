-- ===========================================================
-- Baltimore Metal Crafters Database (jwashb2db)
-- Phase 2 Physical Schema
-- ===========================================================

-- Using existing database jwashb2db
USE jwashb2db;

-- ===========================================================
-- 1. CUSTOMER
-- ===========================================================
CREATE TABLE Customer (
  customer_id      INT AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(100) NOT NULL,
  phone            VARCHAR(30),
  email            VARCHAR(100),
  address          VARCHAR(255)
);

-- ===========================================================
-- 2. EMPLOYEE
-- ===========================================================
CREATE TABLE Employee (
  employee_id      INT AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(100) NOT NULL,
  role             ENUM('restorer','fabricator','admin') NOT NULL,
  specialization   VARCHAR(100),
  contact_info     VARCHAR(150)
);

-- ===========================================================
-- 3. QUOTE / QUOTE ITEM
-- ===========================================================
CREATE TABLE Quote (
  quote_id         INT AUTO_INCREMENT PRIMARY KEY,
  customer_id      INT NOT NULL,
  date_created     DATE DEFAULT (CURRENT_DATE),
  total_estimate   DECIMAL(10,2),
  approved         BOOLEAN DEFAULT FALSE,
  notes            TEXT,
  FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

CREATE TABLE QuoteItem (
  quote_item_id    INT AUTO_INCREMENT PRIMARY KEY,
  quote_id         INT NOT NULL,
  description      VARCHAR(255),
  est_labor_cost   DECIMAL(10,2),
  est_material_cost DECIMAL(10,2),
  FOREIGN KEY (quote_id) REFERENCES Quote(quote_id)
);

-- ===========================================================
-- 4. JOB / STAGES / NOTES / PHOTOS
-- ===========================================================
CREATE TABLE Job (
  job_id           INT AUTO_INCREMENT PRIMARY KEY,
  customer_id      INT NOT NULL,
  quote_id         INT,
  description      VARCHAR(255),
  start_date       DATE,
  due_date         DATE,
  status           ENUM('Planned','InProgress','Completed') DEFAULT 'Planned',
  FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
  FOREIGN KEY (quote_id) REFERENCES Quote(quote_id)
);

CREATE TABLE JobStage (
  stage_id         INT AUTO_INCREMENT PRIMARY KEY,
  job_id           INT NOT NULL,
  stage_name       VARCHAR(50),
  start_date       DATE,
  end_date         DATE,
  FOREIGN KEY (job_id) REFERENCES Job(job_id)
);

CREATE TABLE Notes (
  note_id          INT AUTO_INCREMENT PRIMARY KEY,
  job_id           INT NOT NULL,
  note_text        TEXT,
  created_at       DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (job_id) REFERENCES Job(job_id)
);

CREATE TABLE Photo (
  photo_id         INT AUTO_INCREMENT PRIMARY KEY,
  job_id           INT NOT NULL,
  file_path        VARCHAR(255),
  description      VARCHAR(255),
  FOREIGN KEY (job_id) REFERENCES Job(job_id)
);

-- ===========================================================
-- 5. MATERIAL / JOBMATERIAL
-- ===========================================================
CREATE TABLE Material (
  material_id      INT AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(100) NOT NULL,
  category         VARCHAR(100),
  stock_quantity   INT DEFAULT 0,
  reorder_level    INT DEFAULT 5,
  unit_cost        DECIMAL(10,2)
);

CREATE TABLE JobMaterial (
  job_id           INT NOT NULL,
  material_id      INT NOT NULL,
  quantity_used    INT DEFAULT 1,
  PRIMARY KEY (job_id, material_id),
  FOREIGN KEY (job_id) REFERENCES Job(job_id),
  FOREIGN KEY (material_id) REFERENCES Material(material_id)
);

-- ===========================================================
-- 6. VENDOR / PURCHASE ORDER / PO ITEM
-- ===========================================================
CREATE TABLE Vendor (
  vendor_id        INT AUTO_INCREMENT PRIMARY KEY,
  name             VARCHAR(100) NOT NULL,
  contact_info     VARCHAR(150),
  phone            VARCHAR(30),
  email            VARCHAR(100)
);

CREATE TABLE PurchaseOrder (
  po_id            INT AUTO_INCREMENT PRIMARY KEY,
  vendor_id        INT NOT NULL,
  order_date       DATE DEFAULT (CURRENT_DATE),
  total_cost       DECIMAL(10,2),
  status           ENUM('Pending','Received','Cancelled') DEFAULT 'Pending',
  FOREIGN KEY (vendor_id) REFERENCES Vendor(vendor_id)
);

CREATE TABLE POItem (
  po_item_id       INT AUTO_INCREMENT PRIMARY KEY,
  po_id            INT NOT NULL,
  material_id      INT NOT NULL,
  quantity         INT DEFAULT 1,
  unit_price       DECIMAL(10,2),
  FOREIGN KEY (po_id) REFERENCES PurchaseOrder(po_id),
  FOREIGN KEY (material_id) REFERENCES Material(material_id)
);

-- ===========================================================
-- 7. INVOICE / PAYMENT / SHIPMENT
-- ===========================================================
CREATE TABLE Invoice (
  invoice_id       INT AUTO_INCREMENT PRIMARY KEY,
  job_id           INT NOT NULL,
  invoice_date     DATE DEFAULT (CURRENT_DATE),
  total_amount     DECIMAL(10,2),
  paid             BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (job_id) REFERENCES Job(job_id)
);

CREATE TABLE Payment (
  payment_id       INT AUTO_INCREMENT PRIMARY KEY,
  invoice_id       INT NOT NULL,
  payment_date     DATE DEFAULT (CURRENT_DATE),
  amount           DECIMAL(10,2),
  method           ENUM('Cash','Card','Check','Other'),
  FOREIGN KEY (invoice_id) REFERENCES Invoice(invoice_id)
);

CREATE TABLE Shipment (
  shipment_id      INT AUTO_INCREMENT PRIMARY KEY,
  job_id           INT NOT NULL,
  ship_date        DATE,
  carrier          VARCHAR(100),
  tracking_number  VARCHAR(100),
  FOREIGN KEY (job_id) REFERENCES Job(job_id)
);

-- ===========================================================
-- 8. WORK LOG
-- ===========================================================
CREATE TABLE WorkLog (
  worklog_id       INT AUTO_INCREMENT PRIMARY KEY,
  job_id           INT NOT NULL,
  employee_id      INT NOT NULL,
  stage_id         INT,
  hours_worked     DECIMAL(5,2),
  work_date        DATE DEFAULT (CURRENT_DATE),
  FOREIGN KEY (job_id) REFERENCES Job(job_id),
  FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
  FOREIGN KEY (stage_id) REFERENCES JobStage(stage_id)
);