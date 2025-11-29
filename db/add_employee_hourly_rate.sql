-- ===========================================================
-- Add hourly_rate column to Employee table
-- Migration script for labor cost tracking
-- ===========================================================

USE jwashb2db;

-- Add hourly_rate column to Employee table
ALTER TABLE Employee 
ADD COLUMN hourly_rate DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Hourly pay rate for labor cost calculations';

-- Update existing employees with reasonable hourly rates
UPDATE Employee SET hourly_rate = 75.00 WHERE role = 'restorer';
UPDATE Employee SET hourly_rate = 65.00 WHERE role = 'fabricator';
UPDATE Employee SET hourly_rate = 85.00 WHERE role = 'admin';

-- Display updated employees
SELECT employee_id, name, role, hourly_rate FROM Employee;
