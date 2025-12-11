-- =========================================================
--  HOSPITAL / PHARMACY MANAGEMENT - BASE SCHEMA (UPDATED)
--  Changes:
--    - inventory_items.batch_id made NULLABLE + FK ON DELETE SET NULL
--    - added indexes for performance
--    - order_date / issue_date / payment_date changed to DATETIME
--    - stock_movements.movement_date default CURRENT_TIMESTAMP
--    - added index on (reference_type, reference_id)
-- =========================================================

-- =========================================================
--  DATABASE CREATION
--  Author: Hoang Dinh Dung
-- =========================================================

CREATE DATABASE IF NOT EXISTS HMS
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE HMS;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
--  CORE AUTH
-- =========================================================

CREATE TABLE IF NOT EXISTS accounts (
                                        id              INT AUTO_INCREMENT PRIMARY KEY,
                                        username        VARCHAR(50)  NOT NULL UNIQUE,
                                        password   VARCHAR(255) NOT NULL,
                                        email           VARCHAR(100) UNIQUE,
                                        phone           VARCHAR(20),
                                        role            ENUM('ADMIN', 'EMPLOYEE', 'CUSTOMER') NOT NULL DEFAULT 'EMPLOYEE',
                                        is_active       TINYINT(1) NOT NULL DEFAULT 1,
                                        last_login_at   DATETIME NULL,
                                        created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employees (
                                         id              INT AUTO_INCREMENT PRIMARY KEY,
                                         account_id      INT UNIQUE,
                                         code            VARCHAR(50)  NOT NULL UNIQUE,
                                         full_name       VARCHAR(100) NOT NULL,
                                         position        VARCHAR(100),
                                         department      VARCHAR(100),
                                         phone           VARCHAR(20),
                                         email           VARCHAR(100),
                                         is_active       TINYINT(1) NOT NULL DEFAULT 1,
                                         hired_date      DATE NULL,
                                         created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         CONSTRAINT fk_employees_account
                                             FOREIGN KEY (account_id) REFERENCES accounts(id)
                                                 ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS customers (
                                         id              INT AUTO_INCREMENT PRIMARY KEY,
                                         account_id      INT UNIQUE,
                                         code            VARCHAR(50)  NOT NULL UNIQUE,
                                         name            VARCHAR(100) NOT NULL,
                                         type            VARCHAR(50),
                                         phone           VARCHAR(20) UNIQUE,
                                         email           VARCHAR(100),
                                         address         VARCHAR(255),
                                         tax_code        VARCHAR(50),
                                         status          ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
                                         created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         CONSTRAINT fk_customers_account
                                             FOREIGN KEY (account_id) REFERENCES accounts(id)
                                                 ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- =========================================================
--  PRODUCT CATALOG
-- =========================================================

CREATE TABLE IF NOT EXISTS categories (
                                          id              INT AUTO_INCREMENT PRIMARY KEY,
                                          code            VARCHAR(50)  NOT NULL UNIQUE,
                                          name            VARCHAR(100) NOT NULL,
                                          description     TEXT,
                                          parent_id       INT NULL,
                                          is_active       TINYINT(1) NOT NULL DEFAULT 1,
                                          display_order   INT NOT NULL DEFAULT 0,
                                          created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                          CONSTRAINT fk_categories_parent
                                              FOREIGN KEY (parent_id) REFERENCES categories(id)
                                                  ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS manufacturers (
                                             id              INT AUTO_INCREMENT PRIMARY KEY,
                                             code            VARCHAR(50)  NOT NULL UNIQUE,
                                             name            VARCHAR(150) NOT NULL,
                                             country         VARCHAR(100),
                                             address         VARCHAR(255),
                                             phone           VARCHAR(20),
                                             email           VARCHAR(100),
                                             contact_person  VARCHAR(100),
                                             created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS products (
                                        id                      INT AUTO_INCREMENT PRIMARY KEY,
                                        category_id             INT NOT NULL,
                                        manufacturer_id         INT NULL,
                                        code                    VARCHAR(50)  NOT NULL UNIQUE,
                                        barcode                 VARCHAR(100) UNIQUE,
                                        name                    VARCHAR(150) NOT NULL,
                                        dosage_form             VARCHAR(100),
                                        unit                    VARCHAR(50),
                                        description             TEXT,
                                        image_url               VARCHAR(255),
                                        standard_price          DECIMAL(18,2) NOT NULL DEFAULT 0,
                                        requires_prescription   TINYINT(1) NOT NULL DEFAULT 0,
                                        status                  ENUM('ACTIVE', 'INACTIVE', 'DISCONTINUED') NOT NULL DEFAULT 'ACTIVE',
                                        created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        CONSTRAINT fk_products_category
                                            FOREIGN KEY (category_id) REFERENCES categories(id)
                                                ON UPDATE CASCADE ON DELETE RESTRICT,
                                        CONSTRAINT fk_products_manufacturer
                                            FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id)
                                                ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_manufacturer ON products(manufacturer_id);

-- =========================================================
--  INVENTORY
-- =========================================================

CREATE TABLE IF NOT EXISTS warehouses (
                                          id              INT AUTO_INCREMENT PRIMARY KEY,
                                          code            VARCHAR(50)  NOT NULL UNIQUE,
                                          name            VARCHAR(150) NOT NULL,
                                          address         VARCHAR(255),
                                          phone           VARCHAR(20),
                                          manager_name    VARCHAR(100),
                                          is_active       TINYINT(1) NOT NULL DEFAULT 1,
                                          created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS batches (
                                       id              INT AUTO_INCREMENT PRIMARY KEY,
                                       product_id      INT NOT NULL,
                                       batch_code      VARCHAR(100) NOT NULL UNIQUE,
                                       import_price    DECIMAL(18,2) NOT NULL DEFAULT 0,
                                       manufacture_date DATE NULL,
                                       expiry_date     DATE NULL,
                                       supplier_name   VARCHAR(150),
                                       status          ENUM('ACTIVE', 'EXPIRED', 'BLOCKED', 'DEPLETED') NOT NULL DEFAULT 'ACTIVE',
                                       created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_batches_product
                                           FOREIGN KEY (product_id) REFERENCES products(id)
                                               ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- index for product lookups in batches
CREATE INDEX idx_batches_product ON batches(product_id);

CREATE TABLE IF NOT EXISTS inventory_items (
                                               id                  INT AUTO_INCREMENT PRIMARY KEY,
                                               product_id          INT NOT NULL,
                                               batch_id            INT NULL,
                                               warehouse_id        INT NOT NULL,
                                               quantity_on_hand    INT NOT NULL DEFAULT 0,
                                               quantity_reserved   INT NOT NULL DEFAULT 0,
                                               min_threshold       INT NOT NULL DEFAULT 0,
                                               max_threshold       INT NOT NULL DEFAULT 0,
                                               last_stock_check    DATE NULL,
                                               created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                               CONSTRAINT uq_inventory_items UNIQUE (product_id, batch_id, warehouse_id),
                                               CONSTRAINT fk_inventory_product
                                                   FOREIGN KEY (product_id) REFERENCES products(id)
                                                       ON UPDATE CASCADE ON DELETE RESTRICT,
                                               CONSTRAINT fk_inventory_batch
                                                   FOREIGN KEY (batch_id) REFERENCES batches(id)
                                                       ON UPDATE CASCADE ON DELETE SET NULL,
                                               CONSTRAINT fk_inventory_warehouse
                                                   FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
                                                       ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Indexes to speed up inventory queries
CREATE INDEX idx_inventory_product ON inventory_items(product_id);
CREATE INDEX idx_inventory_batch ON inventory_items(batch_id);
CREATE INDEX idx_inventory_warehouse ON inventory_items(warehouse_id);

CREATE TABLE IF NOT EXISTS stock_movements (
                                               id                          INT AUTO_INCREMENT PRIMARY KEY,
                                               movement_type               ENUM('IMPORT', 'EXPORT', 'ADJUST', 'TRANSFER') NOT NULL,
                                               product_id                  INT NOT NULL,
                                               batch_id                    INT NULL,
                                               warehouse_id                INT NOT NULL,
                                               quantity                    INT NOT NULL,
                                               quantity_before             INT NULL,
                                               quantity_after              INT NULL,
                                               movement_date               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               reason                      VARCHAR(255),
                                               reference_type              VARCHAR(50),
                                               reference_id                INT,
                                               performed_by_employee_id    INT,
                                               note                        VARCHAR(255),
                                               created_at                  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               CONSTRAINT fk_movements_product
                                                   FOREIGN KEY (product_id) REFERENCES products(id)
                                                       ON UPDATE CASCADE ON DELETE RESTRICT,
                                               CONSTRAINT fk_movements_batch
                                                   FOREIGN KEY (batch_id) REFERENCES batches(id)
                                                       ON UPDATE CASCADE ON DELETE SET NULL,
                                               CONSTRAINT fk_movements_warehouse
                                                   FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
                                                       ON UPDATE CASCADE ON DELETE RESTRICT,
                                               CONSTRAINT fk_movements_employee
                                                   FOREIGN KEY (performed_by_employee_id) REFERENCES employees(id)
                                                       ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_movements_product ON stock_movements(product_id);
CREATE INDEX idx_movements_warehouse ON stock_movements(warehouse_id);
CREATE INDEX idx_movements_employee ON stock_movements(performed_by_employee_id);
-- index for quick lookup by reference (e.g., order/invoice/import slip id)
CREATE INDEX idx_movements_reference ON stock_movements(reference_type, reference_id);

-- =========================================================
--  SALES / BILLING
-- =========================================================

CREATE TABLE IF NOT EXISTS orders (
                                      id                      INT AUTO_INCREMENT PRIMARY KEY,
                                      customer_id             INT NOT NULL,
                                      order_number            VARCHAR(50) NOT NULL UNIQUE,
                                      order_date              DATETIME NOT NULL,
                                      status                  ENUM('NEW','CONFIRMED','PROCESSING','SHIPPED','COMPLETED','CANCELED')
                                          NOT NULL DEFAULT 'NEW',
                                      subtotal                DECIMAL(18,2) NOT NULL DEFAULT 0,
                                      discount                DECIMAL(18,2) NOT NULL DEFAULT 0,
                                      tax                     DECIMAL(18,2) NOT NULL DEFAULT 0,
                                      total_amount            DECIMAL(18,2) NOT NULL DEFAULT 0,
                                      shipping_address        VARCHAR(255),
                                      shipping_method         VARCHAR(100),
                                      created_by_employee_id  INT,
                                      note                    VARCHAR(255),
                                      created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_orders_customer
                                          FOREIGN KEY (customer_id) REFERENCES customers(id)
                                              ON UPDATE CASCADE ON DELETE RESTRICT,
                                      CONSTRAINT fk_orders_employee
                                          FOREIGN KEY (created_by_employee_id) REFERENCES employees(id)
                                              ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status   ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);

CREATE TABLE IF NOT EXISTS order_items (
                                           id              INT AUTO_INCREMENT PRIMARY KEY,
                                           order_id        INT NOT NULL,
                                           product_id      INT NOT NULL,
                                           batch_id        INT NULL,
                                           quantity        INT NOT NULL,
                                           unit_price      DECIMAL(18,2) NOT NULL,
                                           discount        DECIMAL(18,2) NOT NULL DEFAULT 0,
                                           line_total      DECIMAL(18,2) NOT NULL,
                                           note            VARCHAR(255),
                                           CONSTRAINT fk_order_items_order
                                               FOREIGN KEY (order_id) REFERENCES orders(id)
                                                   ON UPDATE CASCADE ON DELETE CASCADE,
                                           CONSTRAINT fk_order_items_product
                                               FOREIGN KEY (product_id) REFERENCES products(id)
                                                   ON UPDATE CASCADE ON DELETE RESTRICT,
                                           CONSTRAINT fk_order_items_batch
                                               FOREIGN KEY (batch_id) REFERENCES batches(id)
                                                   ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);

CREATE TABLE IF NOT EXISTS invoices (
                                        id              INT AUTO_INCREMENT PRIMARY KEY,
                                        order_id        INT NOT NULL,
                                        invoice_number  VARCHAR(50) NOT NULL UNIQUE,
                                        issue_date      DATETIME NOT NULL,
                                        due_date        DATETIME NULL,
                                        total_amount    DECIMAL(18,2) NOT NULL,
                                        paid_amount     DECIMAL(18,2) NOT NULL DEFAULT 0,
                                        status          ENUM('NEW','PAID','PARTIAL','CANCELED') NOT NULL DEFAULT 'NEW',
                                        tax_code        VARCHAR(50),
                                        created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        CONSTRAINT fk_invoices_order
                                            FOREIGN KEY (order_id) REFERENCES orders(id)
                                                ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_invoices_order  ON invoices(order_id);
CREATE INDEX idx_invoices_status ON invoices(status);

CREATE TABLE IF NOT EXISTS payments (
                                        id              INT AUTO_INCREMENT PRIMARY KEY,
                                        invoice_id      INT NOT NULL,
                                        payment_number  VARCHAR(50) NOT NULL UNIQUE,
                                        payment_date    DATETIME NOT NULL,
                                        amount          DECIMAL(18,2) NOT NULL,
                                        method          VARCHAR(50) NOT NULL,
                                        transaction_code VARCHAR(100),
                                        status          ENUM('SUCCESS','FAILED','PENDING','CANCELED') NOT NULL DEFAULT 'SUCCESS',
                                        currency        VARCHAR(10) NOT NULL DEFAULT 'VND',
                                        note            VARCHAR(255),
                                        created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        CONSTRAINT fk_payments_invoice
                                            FOREIGN KEY (invoice_id) REFERENCES invoices(id)
                                                ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_status  ON payments(status);

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
--  SEED ADMIN ACCOUNT (unchanged)
-- =========================================================

INSERT INTO accounts (username, password, email, phone, role, is_active)
SELECT 'admin', '123456789', 'admin@example.com', '0123456789', 'ADMIN', 1
WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE username = 'admin'
);

INSERT INTO accounts (username, password, email, phone, role, is_active)
SELECT 'employee01', '123456789', 'employee01@example.com', '0900000001', 'EMPLOYEE', 1
WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE username = 'employee01'
);

SET @EMP_ACC_ID = (SELECT id FROM accounts WHERE username = 'employee01');

INSERT INTO employees (account_id, code, full_name, position, department, phone, email, is_active, hired_date)
SELECT @EMP_ACC_ID, 'EMP001', 'Nguyen Van A', 'Dược sĩ', 'Kho thuốc', '0900000001', 'employee01@example.com', 1, '2022-01-01'
WHERE NOT EXISTS (
    SELECT 1 FROM employees WHERE code = 'EMP001'
);

INSERT INTO accounts (username, password, email, phone, role, is_active)
SELECT 'customer01', '123456789', 'customer01@example.com', '0900000002', 'CUSTOMER', 1
WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE username = 'customer01'
);

SET @CUS_ACC_ID = (SELECT id FROM accounts WHERE username = 'customer01');

INSERT INTO customers (account_id, code, name, type, phone, email, address, tax_code, status)
SELECT @CUS_ACC_ID, 'CUS001', 'Tran Thi B', 'RETAIL', '0900000002', 'customer01@example.com',
       '123 Lê Lợi, Quận 1, TP.HCM', '1234567890', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM customers WHERE code = 'CUS001'
);
