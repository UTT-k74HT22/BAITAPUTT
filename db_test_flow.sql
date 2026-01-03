-- =========================================================
--  HMS - DATABASE SETUP FOR TESTING MAIN FLOW
--  Bao gồm: Quản lý kho, Xuất nhập, Order, Payment
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
    code        VARCHAR(50)  NOT NULL UNIQUE,
    full_name   VARCHAR(150) NOT NULL,
    phone       VARCHAR(20) UNIQUE,
    email       VARCHAR(100),
    address     VARCHAR(255),
    status      ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_profiles_account FOREIGN KEY (account_id) REFERENCES accounts(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_user_profiles_status ON user_profiles(status);

-- =========================================================
--  EMPLOYEE PROFILES
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
    CONSTRAINT fk_employee_profile FOREIGN KEY (profile_id) REFERENCES user_profiles(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_emp_department ON employee_profiles(department);
CREATE INDEX idx_emp_position ON employee_profiles(position);

-- =========================================================
--  CUSTOMER PROFILES
-- =========================================================
CREATE TABLE customer_profiles (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    profile_id    INT NOT NULL UNIQUE,
    customer_type ENUM('RETAIL','WHOLESALE') NOT NULL,
    tax_code      VARCHAR(50),
    company_name  VARCHAR(150),
    credit_limit  DECIMAL(18,2) NOT NULL DEFAULT 0,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_profile FOREIGN KEY (profile_id) REFERENCES user_profiles(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_customer_type ON customer_profiles(customer_type);

-- =========================================================
--  PRODUCT CATALOG
-- =========================================================
CREATE TABLE categories (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(50)  NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    description     TEXT,
    parent_id       INT,
    display_order   INT DEFAULT 0,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cat_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE manufacturers (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(50)  NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    country         VARCHAR(100),
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         VARCHAR(255),
    website         VARCHAR(255),
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE products (
    id                      INT AUTO_INCREMENT PRIMARY KEY,
    code                    VARCHAR(50)  NOT NULL UNIQUE,
    name                    VARCHAR(255) NOT NULL,
    category_id             INT,
    manufacturer_id         INT,
    unit                    VARCHAR(50)  NOT NULL,
    unit_price              DECIMAL(18,2) NOT NULL,
    description             TEXT,
    requires_batch_tracking TINYINT(1) DEFAULT 1,
    is_active               TINYINT(1) DEFAULT 1,
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_product_manufacturer FOREIGN KEY (manufacturer_id) REFERENCES manufacturers(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- =========================================================
--  INVENTORY
-- =========================================================
CREATE TABLE warehouses (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(50)  NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    address         VARCHAR(255),
    manager_name    VARCHAR(100),
    phone           VARCHAR(20),
    capacity        INT,
    is_active       TINYINT(1) DEFAULT 1,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE batches (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    batch_code       VARCHAR(50) NOT NULL UNIQUE,
    product_id       INT NOT NULL,
    manufacturing_date DATE,
    expiry_date      DATE,
    notes            TEXT,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_batch_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE inventory_items (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    product_id        INT NOT NULL,
    batch_id          INT,
    warehouse_id      INT NOT NULL,
    quantity_on_hand  INT NOT NULL DEFAULT 0,
    quantity_reserved INT NOT NULL DEFAULT 0,
    min_threshold     INT DEFAULT 10,
    max_threshold     INT DEFAULT 1000,
    last_stock_check  DATE,
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_inv_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_inv_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY uk_product_batch_warehouse (product_id, batch_id, warehouse_id)
) ENGINE=InnoDB;

CREATE TABLE stock_movements (
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    movement_type        ENUM('IMPORT','EXPORT','ADJUST','TRANSFER') NOT NULL,
    product_id           INT NOT NULL,
    batch_id             INT,
    warehouse_id         INT NOT NULL,
    quantity             INT NOT NULL,
    quantity_before      INT NOT NULL,
    quantity_after       INT NOT NULL,
    reference_type       VARCHAR(50),
    reference_id         INT,
    performed_by_user_id INT,
    note                 TEXT,
    movement_date        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sm_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_sm_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_sm_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_sm_user FOREIGN KEY (performed_by_user_id) REFERENCES user_profiles(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- =========================================================
--  SALES / BILLING
-- =========================================================
CREATE TABLE orders (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    customer_id         INT NOT NULL,
    order_number        VARCHAR(50) NOT NULL UNIQUE,
    order_date          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status              ENUM('NEW','CONFIRMED','PROCESSING','SHIPPED','COMPLETED','CANCELED') NOT NULL DEFAULT 'NEW',
    total_amount        DECIMAL(18,2) NOT NULL DEFAULT 0,
    discount            DECIMAL(18,2) DEFAULT 0,
    tax                 DECIMAL(18,2) DEFAULT 0,
    shipping_address    VARCHAR(255),
    note                TEXT,
    created_by_user_id  INT,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES user_profiles(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_order_creator FOREIGN KEY (created_by_user_id) REFERENCES user_profiles(id)
        ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE order_items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    order_id   INT NOT NULL,
    product_id INT NOT NULL,
    batch_id   INT,
    warehouse_id INT NOT NULL,
    quantity   INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    line_total DECIMAL(18,2) NOT NULL,
    note       VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_oi_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
        ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_oi_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE invoices (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    order_id       INT NOT NULL,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    issue_date     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date       DATETIME,
    total_amount   DECIMAL(18,2) NOT NULL,
    paid_amount    DECIMAL(18,2) NOT NULL DEFAULT 0,
    status         ENUM('NEW','PARTIAL','PAID','CANCELED') NOT NULL DEFAULT 'NEW',
    tax_code       VARCHAR(50),
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_invoice_order FOREIGN KEY (order_id) REFERENCES orders(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE payments (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    invoice_id       INT NOT NULL,
    payment_number   VARCHAR(50) NOT NULL UNIQUE,
    payment_date     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount           DECIMAL(18,2) NOT NULL,
    method           VARCHAR(50) NOT NULL,
    transaction_code VARCHAR(100),
    status           ENUM('PENDING','COMPLETED','FAILED','CANCELED') NOT NULL DEFAULT 'COMPLETED',
    currency         VARCHAR(10) DEFAULT 'VND',
    note             TEXT,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- =========================================================
--  SAMPLE DATA FOR TESTING
-- =========================================================

-- 1. ACCOUNTS & USER PROFILES
INSERT INTO accounts (username, password, role, is_active) VALUES
('admin', 'admin123', 'ADMIN', 1),
('nhanvien1', 'nv123', 'EMPLOYEE', 1),
('khachhang1', 'kh123', 'CUSTOMER', 1);

UPDATE accounts SET password = '$2a$12$ZbxOL6/mxxY65NraoXE2GeeS2HFNVtZvJhlNt7Vtg.XNJ5HKNnd9q' WHERE username = 'admin';

INSERT INTO user_profiles (account_id, code, full_name, phone, email, address, status) VALUES
(1, 'ADMIN001', 'Quản trị viên', '0901234567', 'admin@hms.vn', 'Hà Nội', 'ACTIVE'),
(2, 'EMP001', 'Nguyễn Văn A', '0902345678', 'nva@hms.vn', 'Hà Nội', 'ACTIVE'),
(3, 'CUS001', 'Trần Thị B', '0903456789', 'ttb@gmail.com', 'TP.HCM', 'ACTIVE');

INSERT INTO employee_profiles (profile_id, position, department, hired_date, base_salary) VALUES
(1, 'Admin', 'IT', '2024-01-01', 20000000),
(2, 'Nhân viên bán hàng', 'Kinh doanh', '2024-06-01', 12000000);

INSERT INTO customer_profiles (profile_id, customer_type, tax_code, company_name, credit_limit) VALUES
(3, 'RETAIL', NULL, NULL, 50000000);

-- 2. CATEGORIES & MANUFACTURERS
INSERT INTO categories (code, name, description, parent_id, display_order) VALUES
('MED', 'Thuốc', 'Nhóm sản phẩm thuốc', NULL, 1),
('MED-PAIN', 'Thuốc giảm đau', 'Thuốc giảm đau, hạ sốt', 1, 1),
('MED-ANTIBIOTIC', 'Kháng sinh', 'Thuốc kháng sinh', 1, 2);

INSERT INTO manufacturers (code, name, country, contact_person, phone, email) VALUES
('DHG', 'Dược Hậu Giang', 'Việt Nam', 'Nguyễn Văn C', '02923891433', 'info@dhgpharma.com'),
('TRAPHACO', 'Traphaco', 'Việt Nam', 'Trần Văn D', '02433574181', 'info@traphaco.com');

-- 3. PRODUCTS
INSERT INTO products (code, name, category_id, manufacturer_id, unit, unit_price, description, requires_batch_tracking, is_active) VALUES
('PARA500', 'Paracetamol 500mg', 2, 1, 'Viên', 2000, 'Thuốc giảm đau, hạ sốt', 1, 1),
('AMOXI500', 'Amoxicillin 500mg', 3, 2, 'Viên', 5000, 'Kháng sinh nhóm Penicillin', 1, 1),
('VITA-C', 'Vitamin C 1000mg', 1, 1, 'Viên', 3000, 'Bổ sung vitamin C', 1, 1);

-- 4. WAREHOUSES
INSERT INTO warehouses (code, name, address, manager_name, phone, capacity, is_active) VALUES
('WH01', 'Kho chính Hà Nội', 'Số 123 Đường ABC, Hà Nội', 'Lê Văn E', '0904567890', 10000, 1),
('WH02', 'Kho chi nhánh HCM', 'Số 456 Đường XYZ, TP.HCM', 'Phạm Thị F', '0905678901', 5000, 1);

-- 5. BATCHES
INSERT INTO batches (batch_code, product_id, manufacturing_date, expiry_date, notes) VALUES
('PARA-2024-001', 1, '2024-01-15', '2026-01-15', 'Lô sản xuất đầu năm 2024'),
('PARA-2024-002', 1, '2024-06-01', '2026-06-01', 'Lô sản xuất giữa năm 2024'),
('AMOXI-2024-001', 2, '2024-03-10', '2026-03-10', 'Lô nhập khẩu tháng 3/2024'),
('VITA-2024-001', 3, '2024-02-20', '2027-02-20', 'Vitamin C lô tháng 2');

-- 6. INVENTORY ITEMS - TỒN KHO BAN ĐẦU
INSERT INTO inventory_items (product_id, batch_id, warehouse_id, quantity_on_hand, quantity_reserved, min_threshold, max_threshold) VALUES
-- Paracetamol tại kho Hà Nội
(1, 1, 1, 500, 0, 100, 1000),
(1, 2, 1, 300, 0, 100, 1000),
-- Amoxicillin tại kho Hà Nội
(2, 3, 1, 400, 0, 50, 800),
-- Vitamin C tại kho Hà Nội
(3, 4, 1, 600, 0, 100, 1000),
-- Paracetamol tại kho HCM
(1, 1, 2, 200, 0, 50, 500);

-- =========================================================
--  TEST DATA STATUS
-- =========================================================
-- ✅ Account ADMIN (id=1, user_profile_id=1) - để tạo đơn
-- ✅ Account EMPLOYEE (id=2, user_profile_id=2) - nhân viên bán hàng
-- ✅ Account CUSTOMER (id=3, user_profile_id=3) - khách hàng
-- ✅ 3 Products với batches
-- ✅ 2 Warehouses
-- ✅ Tồn kho đầy đủ (quantity_on_hand > 0, quantity_reserved = 0)

-- =========================================================
--  TEST FLOW SCENARIOS
-- =========================================================
-- Scenario 1: Tạo đơn hàng mới
/*
employeeId = 2 (user_profile_id của nhân viên)
customerId = 3 (user_profile_id của khách hàng)
items = [
  {productId: 1, batchId: 1, warehouseId: 1, quantity: 10, unitPrice: 2000},
  {productId: 2, batchId: 3, warehouseId: 1, quantity: 5, unitPrice: 5000}
]
→ Sau khi tạo:
  - inventory_items: quantity_reserved tăng (Paracetamol +10, Amoxicillin +5)
  - order status = NEW
*/

-- Scenario 2: Xác nhận đơn hàng
/*
→ Sau khi confirm:
  - inventory_items: quantity_on_hand giảm, quantity_reserved giảm
  - stock_movements: có 2 records (EXPORT)
  - order status = CONFIRMED
*/

-- Scenario 3: Tạo invoice và thanh toán
/*
→ Tạo invoice từ order
→ Tạo payment:
  - invoice: paid_amount tăng, status = PAID
  - order: status = COMPLETED
*/

SELECT '✅ Database đã được tạo và sẵn sàng test main flow!' AS STATUS;



-- TEST DATA

-- CHECK KHOẢN TỒN KHO BAN ĐẦU
-- Xem tồn kho ban đầu
SELECT
    p.name AS product_name,
    b.batch_code,
    w.name AS warehouse_name,
    ii.quantity_on_hand,
    ii.quantity_reserved,
    (ii.quantity_on_hand - ii.quantity_reserved) AS available
FROM inventory_items ii
         JOIN products p ON ii.product_id = p.id
         JOIN batches b ON ii.batch_id = b.id
         JOIN warehouses w ON ii.warehouse_id = w.id;


-- TEST SCENARIO 1: TẠO ĐƠN HÀNG MỚI
-- 1. Xem order vừa tạo
SELECT * FROM orders ORDER BY id DESC LIMIT 1;
-- Status phải là 'NEW'

-- 2. Xem items
SELECT * FROM order_items WHERE order_id = 1;

-- 3. Xem tồn kho (quantity_reserved phải tăng)
SELECT
    p.name,
    ii.quantity_on_hand,
    ii.quantity_reserved,
    (ii.quantity_on_hand - ii.quantity_reserved) AS available
FROM inventory_items ii
         JOIN products p ON ii.product_id = p.id
WHERE (ii.product_id = 1 AND ii.batch_id = 1)
   OR (ii.product_id = 2 AND ii.batch_id = 3);


-- TEST SCENARIO 2: XÁC NHẬN ĐƠN HÀNG
-- 1. Order status
SELECT status FROM orders WHERE id = 1;
-- Phải là 'CONFIRMED'

-- 2. Tồn kho (quantity_on_hand giảm, quantity_reserved về 0)
SELECT
    p.name,
    ii.quantity_on_hand,
    ii.quantity_reserved
FROM inventory_items ii
         JOIN products p ON ii.product_id = p.id
WHERE (ii.product_id = 1 AND ii.batch_id = 1)
   OR (ii.product_id = 2 AND ii.batch_id = 3);

-- 3. Stock movements (phải có 2 records)
SELECT
    sm.movement_type,
    p.name,
    sm.quantity,
    sm.quantity_before,
    sm.quantity_after,
    sm.reference_type,
    sm.reference_id
FROM stock_movements sm
         JOIN products p ON sm.product_id = p.id
ORDER BY sm.id DESC;
