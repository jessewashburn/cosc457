-- Migration script to add vendor_id column to Material table
-- Baltimore Metal Crafters Database
-- Date: 2025-11-29

USE jwashb2db;

-- Add vendor_id column to Material table
ALTER TABLE Material
ADD COLUMN vendor_id INT NULL;

-- Add foreign key constraint to Vendor table
ALTER TABLE Material
ADD CONSTRAINT fk_material_vendor
FOREIGN KEY (vendor_id) REFERENCES Vendor(vendor_id);

-- Optional: Update existing materials with vendor information
-- (This would need to be done manually based on actual vendor data)

SELECT 'Migration completed: vendor_id column added to Material table' AS Status;
