ALTER TABLE part_usage
ADD COLUMN IF NOT EXISTS quantity_used INTEGER;

ALTER TABLE part_usage
ADD CONSTRAINT chk_part_usage_quantity
CHECK (quantity_used > 0);