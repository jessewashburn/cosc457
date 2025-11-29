-- Add estimated costs to jobs for meaningful cost comparison report
UPDATE Job SET estimated_labor_cost = 28000.00, estimated_material_cost = 16000.00 WHERE job_id = 1;
UPDATE Job SET estimated_labor_cost = 9500.00, estimated_material_cost = 3500.00 WHERE job_id = 2;
UPDATE Job SET estimated_labor_cost = 1500.00, estimated_material_cost = 800.00 WHERE job_id = 3;
UPDATE Job SET estimated_labor_cost = 750.00, estimated_material_cost = 600.00 WHERE job_id = 4;
UPDATE Job SET estimated_labor_cost = 150.00, estimated_material_cost = 50.00 WHERE job_id = 5;
