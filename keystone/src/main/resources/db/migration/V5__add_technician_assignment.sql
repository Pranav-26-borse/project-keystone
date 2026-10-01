DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_work_orders_technician'
    ) THEN
        ALTER TABLE work_orders
        ADD CONSTRAINT fk_work_orders_technician
        FOREIGN KEY (assigned_technician_id)
        REFERENCES users(id)
        ON DELETE SET NULL;
    END IF;
END $$;