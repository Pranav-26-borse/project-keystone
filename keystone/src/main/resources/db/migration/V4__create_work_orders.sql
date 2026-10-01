-- Add code column
ALTER TABLE work_orders
ADD COLUMN IF NOT EXISTS code VARCHAR(30);

-- Add customer_id column
ALTER TABLE work_orders
ADD COLUMN IF NOT EXISTS customer_id BIGINT;

-- Generate codes for any existing records
UPDATE work_orders
SET code = 'WO-' || EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER || '-' ||
           LPAD(id::TEXT, 5, '0')
WHERE code IS NULL;

-- Since the table is currently empty, these can safely be NOT NULL
ALTER TABLE work_orders
ALTER COLUMN code SET NOT NULL;

ALTER TABLE work_orders
ALTER COLUMN customer_id SET NOT NULL;

-- Make work order code unique
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_work_orders_code'
    ) THEN
        ALTER TABLE work_orders
        ADD CONSTRAINT uk_work_orders_code UNIQUE (code);
    END IF;
END $$;

-- Connect work order to customer
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_work_orders_customer'
    ) THEN
        ALTER TABLE work_orders
        ADD CONSTRAINT fk_work_orders_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE CASCADE;
    END IF;
END $$;