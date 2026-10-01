-- ============================================
-- KEYSTONE - INITIAL DATABASE SCHEMA
-- ============================================

-- USERS
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CUSTOMERS
CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- SITES
CREATE TABLE sites (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_site_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);

-- WORK ORDERS
CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    site_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    priority VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    assigned_technician_id BIGINT,
    created_by_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_work_order_site
        FOREIGN KEY (site_id)
        REFERENCES sites(id),

    CONSTRAINT fk_work_order_technician
        FOREIGN KEY (assigned_technician_id)
        REFERENCES users(id),

    CONSTRAINT fk_work_order_creator
        FOREIGN KEY (created_by_id)
        REFERENCES users(id)
);

-- WORK ORDER STATUS HISTORY
CREATE TABLE work_order_status_history (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    changed_by_id BIGINT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_status_history_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id),

    CONSTRAINT fk_status_history_user
        FOREIGN KEY (changed_by_id)
        REFERENCES users(id)
);

-- PARTS
CREATE TABLE parts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    part_number VARCHAR(100) UNIQUE,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    unit_price DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PART USAGE
CREATE TABLE part_usage (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_part_usage_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id),

    CONSTRAINT fk_part_usage_part
        FOREIGN KEY (part_id)
        REFERENCES parts(id)
);

-- TIME LOGS
CREATE TABLE time_logs (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    minutes INTEGER NOT NULL,
    note TEXT,
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_time_log_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id),

    CONSTRAINT fk_time_log_technician
        FOREIGN KEY (technician_id)
        REFERENCES users(id)
);