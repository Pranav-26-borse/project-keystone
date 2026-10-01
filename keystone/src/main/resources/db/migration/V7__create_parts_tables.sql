CREATE TABLE IF NOT EXISTS parts (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(150) NOT NULL,

    part_number VARCHAR(50) NOT NULL UNIQUE,

    quantity_available INTEGER NOT NULL,

    unit_price NUMERIC(10, 2) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_parts_quantity_available
        CHECK (quantity_available >= 0),

    CONSTRAINT chk_parts_unit_price
        CHECK (unit_price >= 0)
);


CREATE TABLE IF NOT EXISTS part_usage (
    id BIGSERIAL PRIMARY KEY,

    work_order_id BIGINT NOT NULL,

    part_id BIGINT NOT NULL,

    quantity_used INTEGER NOT NULL,

    used_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_part_usage_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_part_usage_part
        FOREIGN KEY (part_id)
        REFERENCES parts(id)
        ON DELETE RESTRICT,

    CONSTRAINT uk_part_usage_work_order_part
        UNIQUE (work_order_id, part_id),

    CONSTRAINT chk_part_usage_quantity
        CHECK (quantity_used > 0)
);