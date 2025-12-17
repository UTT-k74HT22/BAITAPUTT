-- =========================================================
--  HMS - RESET & RECREATE (SPLIT USER SCHEMA)
-- =========================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Drop tables (order matters)
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;

DROP TABLE IF EXISTS stock_movements;
DROP TABLE IF EXISTS inventory_items;
DROP TABLE IF EXISTS batches;
DROP TABLE IF EXISTS warehouses;

DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS manufacturers;
DROP TABLE IF EXISTS categories;

DROP TABLE IF EXISTS customer_profiles;
DROP TABLE IF EXISTS employee_profiles;
DROP TABLE IF EXISTS user_profiles;
DROP TABLE IF EXISTS accounts;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
--  ACCOUNTS (AUTH)
-- =========================================================
CREATE TABLE accounts (
                          id            INT AUTO_INCREMENT PRIMARY KEY,
                          username      VARCHAR(50)  NOT NULL UNIQUE,
                          password      VARCHAR(255) NOT NULL,
                          role          ENUM('ADMIN','EMPLOYEE','CUSTOMER') NOT NULL,
                          is_active     TINYINT(1) NOT NULL DEFAULT 1,
                          last_login_at DATETIME,
                          created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE INDEX idx_accounts_role ON accounts(role);

-- =========================================================
--  USER PROFILES (COMMON)
-- =========================================================
CREATE TABLE user_profiles (
                               id          INT AUTO_INCREMENT PRIMARY KEY,
                               account_id  INT NOT NULL UNIQUE,

                               code        VARCHAR(50)  NOT NULL UNIQUE,   -- EMP001 / CUS001
                               full_name   VARCHAR(150) NOT NULL,
                               phone       VARCHAR(20) UNIQUE,
                               email       VARCHAR(100),
                               address     VARCHAR(255),

                               status      ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
                               created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               CONSTRAINT fk_profiles_account
                                   FOREIGN KEY (account_id) REFERENCES accounts(id)
                                       ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_user_profiles_status ON user_profiles(status);

-- =========================================================
--  EMPLOYEE PROFILES (EMPLOYEE-SPECIFIC)
-- =========================================================
CREATE TABLE employee_profiles (
                                   id          INT AUTO_INCREMENT PRIMARY KEY,
                                   profile_id  INT NOT NULL UNIQUE,

                                   position    VARCHAR(100),
                                   department  VARCHAR(100),
                                   hired_date  DATE,
                                   base_salary DECIMAL(18,2) NOT NULL DEFAULT 0,

                                   created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_employee_profile
                                       FOREIGN KEY (profile_id) REFERENCES user_profiles(id)
                                           ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_emp_department ON employee_profiles(department);
CREATE INDEX idx_emp_position ON employee_profiles(position);

-- =========================================================
--  CUSTOMER PROFILES (CUSTOMER-SPECIFIC)
-- =========================================================
CREATE TABLE customer_profiles (
                                   id            INT AUTO_INCREMENT PRIMARY KEY,
                                   profile_id    INT NOT NULL UNIQUE,

                                   customer_type ENUM('RETAIL','WHOLESALE') NOT NULL,
                                   tax_code      VARCHAR(50),

                                   created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                   CONSTRAINT fk_customer_profile
                                       FOREIGN KEY (profile_id) REFERENCES user_profiles(id)
                                           ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_customer_type ON customer_profiles(customer_type);

-- =========================================================
--  PRODUCT CATALOG
-- =========================================================
CREATE TABLE categories (
                            id              INT AUTO_INCREMENT PRIMARY KEY,
                            code            VARCHAR(50) NOT NULL UNIQUE,
                            name            VARCHAR(100) NOT NULL,
                            description     TEXT,
                            parent_id       INT,
                            is_active       TINYINT(1) NOT NULL DEFAULT 1,
                            display_order   INT NOT NULL DEFAULT 0,
                            created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            CONSTRAINT fk_categories_parent
                                FOREIGN KEY (parent_id) REFERENCES categories(id)
                                    ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE manufacturers (
                               id              INT AUTO_INCREMENT PRIMARY KEY,
                               code            VARCHAR(50) NOT NULL UNIQUE,
                               name            VARCHAR(150) NOT NULL,
                               country         VARCHAR(100),
                               address         VARCHAR(255),
                               phone           VARCHAR(20),
                               email           VARCHAR(100),
                               contact_person  VARCHAR(100),
                               created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE products (
                          id                      INT AUTO_INCREMENT PRIMARY KEY,
                          category_id             INT NOT NULL,
                          manufacturer_id         INT,
                          code                    VARCHAR(50) NOT NULL UNIQUE,
                          barcode                 VARCHAR(100) UNIQUE,
                          name                    VARCHAR(150) NOT NULL,
                          dosage_form             VARCHAR(100),
                          unit                    VARCHAR(50),
                          description             TEXT,
                          image_url               VARCHAR(255),
                          standard_price          DECIMAL(18,2) NOT NULL DEFAULT 0,
                          requires_prescription   TINYINT(1) NOT NULL DEFAULT 0,
                          status                  ENUM('ACTIVE','INACTIVE','DISCONTINUED') NOT NULL DEFAULT 'ACTIVE',
                          created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          CONSTRAINT fk_products_category
                              FOREIGN KEY (category_id) REFERENCES categories(id)
                                  ON UPDATE CASCADE ON DELETE RESTRICT,
                          CONSTRAINT fk_products_manufacturer
                              FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id)
                                  ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- =========================================================
--  INVENTORY
-- =========================================================
CREATE TABLE warehouses (
                            id              INT AUTO_INCREMENT PRIMARY KEY,
                            code            VARCHAR(50) NOT NULL UNIQUE,
                            name            VARCHAR(150) NOT NULL,
                            address         VARCHAR(255),
                            phone           VARCHAR(20),
                            manager_name    VARCHAR(100),
                            is_active       TINYINT(1) NOT NULL DEFAULT 1,
                            created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE batches (
                         id               INT AUTO_INCREMENT PRIMARY KEY,
                         product_id       INT NOT NULL,
                         batch_code       VARCHAR(100) NOT NULL UNIQUE,
                         import_price     DECIMAL(18,2) NOT NULL DEFAULT 0,
                         manufacture_date DATE,
                         expiry_date      DATE,
                         supplier_name    VARCHAR(150),
                         status           ENUM('ACTIVE','EXPIRED','BLOCKED','DEPLETED') NOT NULL DEFAULT 'ACTIVE',
                         created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_batches_product
                             FOREIGN KEY (product_id) REFERENCES products(id)
                                 ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE inventory_items (
                                 id                INT AUTO_INCREMENT PRIMARY KEY,
                                 product_id         INT NOT NULL,
                                 batch_id           INT,
                                 warehouse_id       INT NOT NULL,
                                 quantity_on_hand   INT NOT NULL DEFAULT 0,
                                 quantity_reserved  INT NOT NULL DEFAULT 0,
                                 min_threshold      INT NOT NULL DEFAULT 0,
                                 max_threshold      INT NOT NULL DEFAULT 0,
                                 last_stock_check   DATE,
                                 created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 UNIQUE (product_id, batch_id, warehouse_id),
                                 CONSTRAINT fk_inventory_product
                                     FOREIGN KEY (product_id) REFERENCES products(id),
                                 CONSTRAINT fk_inventory_batch
                                     FOREIGN KEY (batch_id) REFERENCES batches(id)
                                         ON DELETE SET NULL,
                                 CONSTRAINT fk_inventory_warehouse
                                     FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
) ENGINE=InnoDB;

CREATE TABLE stock_movements (
                                 id                   INT AUTO_INCREMENT PRIMARY KEY,
                                 movement_type        ENUM('IMPORT','EXPORT','ADJUST','TRANSFER') NOT NULL,
                                 product_id           INT NOT NULL,
                                 batch_id             INT,
                                 warehouse_id         INT NOT NULL,
                                 quantity             INT NOT NULL,
                                 quantity_before      INT,
                                 quantity_after       INT,
                                 movement_date        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 reference_type       VARCHAR(50),
                                 reference_id         INT,
                                 performed_by_user_id INT,
                                 note                 VARCHAR(255),
                                 created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_sm_product
                                     FOREIGN KEY (product_id) REFERENCES products(id),
                                 CONSTRAINT fk_sm_batch
                                     FOREIGN KEY (batch_id) REFERENCES batches(id)
                                         ON DELETE SET NULL,
                                 CONSTRAINT fk_sm_warehouse
                                     FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
                                 CONSTRAINT fk_sm_user
                                     FOREIGN KEY (performed_by_user_id) REFERENCES user_profiles(id)
                                         ON DELETE SET NULL
) ENGINE=InnoDB;

-- =========================================================
--  SALES / BILLING
-- =========================================================
CREATE TABLE orders (
                        id                  INT AUTO_INCREMENT PRIMARY KEY,
                        customer_id         INT NOT NULL,
                        order_number        VARCHAR(50) NOT NULL UNIQUE,
                        order_date          DATETIME NOT NULL,
                        status              ENUM('NEW','CONFIRMED','PROCESSING','SHIPPED','COMPLETED','CANCELED')
                            NOT NULL DEFAULT 'NEW',
                        subtotal            DECIMAL(18,2) NOT NULL DEFAULT 0,
                        discount            DECIMAL(18,2) NOT NULL DEFAULT 0,
                        tax                 DECIMAL(18,2) NOT NULL DEFAULT 0,
                        total_amount        DECIMAL(18,2) NOT NULL DEFAULT 0,
                        shipping_address    VARCHAR(255),
                        created_by_user_id  INT,
                        note                VARCHAR(255),
                        created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id) REFERENCES user_profiles(id),
                        CONSTRAINT fk_orders_creator
                            FOREIGN KEY (created_by_user_id) REFERENCES user_profiles(id)
                                ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE order_items (
                             id         INT AUTO_INCREMENT PRIMARY KEY,
                             order_id   INT NOT NULL,
                             product_id INT NOT NULL,
                             batch_id   INT,
                             quantity   INT NOT NULL,
                             unit_price DECIMAL(18,2) NOT NULL,
                             discount   DECIMAL(18,2) NOT NULL DEFAULT 0,
                             line_total DECIMAL(18,2) NOT NULL,

                             CONSTRAINT fk_oi_order
                                 FOREIGN KEY (order_id) REFERENCES orders(id)
                                     ON DELETE CASCADE,
                             CONSTRAINT fk_oi_product
                                 FOREIGN KEY (product_id) REFERENCES products(id),
                             CONSTRAINT fk_oi_batch
                                 FOREIGN KEY (batch_id) REFERENCES batches(id)
                                     ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE invoices (
                          id             INT AUTO_INCREMENT PRIMARY KEY,
                          order_id       INT NOT NULL,
                          invoice_number VARCHAR(50) NOT NULL UNIQUE,
                          issue_date     DATETIME NOT NULL,
                          due_date       DATETIME,
                          total_amount   DECIMAL(18,2) NOT NULL,
                          paid_amount    DECIMAL(18,2) NOT NULL DEFAULT 0,
                          status         ENUM('NEW','PAID','PARTIAL','CANCELED') NOT NULL DEFAULT 'NEW',
                          created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT fk_invoice_order
                              FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;

CREATE TABLE payments (
                          id             INT AUTO_INCREMENT PRIMARY KEY,
                          invoice_id     INT NOT NULL,
                          payment_number VARCHAR(50) NOT NULL UNIQUE,
                          payment_date   DATETIME NOT NULL,
                          amount         DECIMAL(18,2) NOT NULL,
                          method         VARCHAR(50) NOT NULL,
                          status         ENUM('SUCCESS','FAILED','PENDING','CANCELED') NOT NULL DEFAULT 'SUCCESS',
                          created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_payment_invoice
                              FOREIGN KEY (invoice_id) REFERENCES invoices(id)
) ENGINE=InnoDB;

-- =========================================================
--  DONE
-- =========================================================

-- Example: set first account as ADMIN
UPDATE accounts SET role = 'ADMIN' WHERE id = 1;
