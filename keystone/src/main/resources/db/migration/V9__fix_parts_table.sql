ALTER TABLE parts
ADD COLUMN IF NOT EXISTS quantity_available INTEGER;

ALTER TABLE parts
ADD COLUMN IF NOT EXISTS unit_price NUMERIC(10, 2);

ALTER TABLE parts
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP
    DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE parts
ALTER COLUMN quantity_available SET DEFAULT 0;

UPDATE parts
SET quantity_available = 0
WHERE quantity_available IS NULL;

UPDATE parts
SET unit_price = 0
WHERE unit_price IS NULL;

UPDATE parts
SET created_at = CURRENT_TIMESTAMP
WHERE created_at IS NULL;

ALTER TABLE parts
ALTER COLUMN quantity_available SET NOT NULL;

ALTER TABLE parts
ALTER COLUMN unit_price SET NOT NULL;

ALTER TABLE parts
ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE parts
ADD CONSTRAINT chk_parts_quantity_available
CHECK (quantity_available >= 0);

ALTER TABLE parts
ADD CONSTRAINT chk_parts_unit_price
CHECK (unit_price >= 0);