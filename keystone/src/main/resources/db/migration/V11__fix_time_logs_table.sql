ALTER TABLE time_logs
ADD COLUMN IF NOT EXISTS work_order_id BIGINT;

ALTER TABLE time_logs
ADD COLUMN IF NOT EXISTS technician_id BIGINT;

ALTER TABLE time_logs
ADD COLUMN IF NOT EXISTS start_time TIMESTAMP;

ALTER TABLE time_logs
ADD COLUMN IF NOT EXISTS end_time TIMESTAMP;

ALTER TABLE time_logs
ADD COLUMN IF NOT EXISTS notes VARCHAR(500);

-- This table is currently empty, so required values can safely be made NOT NULL
ALTER TABLE time_logs
ALTER COLUMN work_order_id SET NOT NULL;

ALTER TABLE time_logs
ALTER COLUMN technician_id SET NOT NULL;

ALTER TABLE time_logs
ALTER COLUMN start_time SET NOT NULL;

-- Add foreign keys only if they do not already exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_time_logs_work_order'
    ) THEN
        ALTER TABLE time_logs
        ADD CONSTRAINT fk_time_logs_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id)
        ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_time_logs_technician'
    ) THEN
        ALTER TABLE time_logs
        ADD CONSTRAINT fk_time_logs_technician
        FOREIGN KEY (technician_id)
        REFERENCES users(id)
        ON DELETE CASCADE;
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_time_logs_end_time'
    ) THEN
        ALTER TABLE time_logs
        ADD CONSTRAINT chk_time_logs_end_time
        CHECK (end_time IS NULL OR end_time >= start_time);
    END IF;
END $$;