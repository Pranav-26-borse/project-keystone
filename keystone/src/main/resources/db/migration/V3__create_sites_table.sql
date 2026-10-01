-- Add phone column if it does not already exist
ALTER TABLE sites
ADD COLUMN IF NOT EXISTS phone VARCHAR(20);

-- Make sure existing rows have a created_at value
UPDATE sites
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

-- Make created_at mandatory
ALTER TABLE sites
ALTER COLUMN created_at SET NOT NULL;

-- Add customer foreign key only if it does not already exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conrelid = 'sites'::regclass
          AND confrelid = 'customers'::regclass
          AND contype = 'f'
    ) THEN
        ALTER TABLE sites
        ADD CONSTRAINT fk_sites_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE CASCADE;
    END IF;
END $$;