-- Add more completed jobs for customers to demonstrate repeat customer report
-- Customer 1 (Restoration Society of Baltimore) already has 2 jobs, need 2 more completed
INSERT INTO Job (customer_id, description, start_date, due_date, status, estimated_labor_cost, estimated_material_cost) 
VALUES 
(1, 'Historic Window Frame Restoration', '2025-08-15', '2025-09-30', 'Completed', 3500.00, 2000.00),
(1, 'Victorian Railing Refinishing', '2025-07-01', '2025-08-15', 'Completed', 2800.00, 1500.00);

-- Customer 2 (City of Baltimore Parks Dept) has 1 job, need 3 more completed
INSERT INTO Job (customer_id, description, start_date, due_date, status, estimated_labor_cost, estimated_material_cost) 
VALUES 
(2, 'Park Gate Replacement', '2025-06-01', '2025-07-15', 'Completed', 4500.00, 3000.00),
(2, 'Historic Bench Set Restoration', '2025-05-01', '2025-06-15', 'Completed', 5000.00, 2500.00),
(2, 'Fountain Hardware Repair', '2025-04-01', '2025-05-15', 'Completed', 2200.00, 1200.00);

-- Mark some existing jobs as completed
UPDATE Job SET status = 'Completed' WHERE job_id IN (1, 4);
