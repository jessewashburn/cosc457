-- Make Job 2 over budget for demonstration
UPDATE Job SET estimated_labor_cost = 8000.00, estimated_material_cost = 2500.00 WHERE job_id = 2;
