ALTER TABLE users
ADD COLUMN IF NOT EXISTS customer_id BIGINT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_users_customer'
    ) THEN
        ALTER TABLE users
        ADD CONSTRAINT fk_users_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE SET NULL;
    END IF;
END $$;