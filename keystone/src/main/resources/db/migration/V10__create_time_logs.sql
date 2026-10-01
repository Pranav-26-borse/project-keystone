CREATE TABLE IF NOT EXISTS time_logs (
    id BIGSERIAL PRIMARY KEY,

    work_order_id BIGINT NOT NULL,

    technician_id BIGINT NOT NULL,

    start_time TIMESTAMP NOT NULL,

    end_time TIMESTAMP,

    notes VARCHAR(500),

    CONSTRAINT fk_time_logs_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_time_logs_technician
        FOREIGN KEY (technician_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_time_logs_end_time
        CHECK (
            end_time IS NULL
            OR end_time >= start_time
        )
);