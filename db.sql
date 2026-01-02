AUTO_INCREMENT PRIMARY KEY,
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



    INSERT INTO categories (code, name, description, parent_id, display_order)
    VALUES ('MED', 'Thuốc', 'Nhóm thuốc điều trị', NULL, 1),
           ('OTC', 'Thuốc không kê đơn', 'Thuốc OTC', 1, 1),
           ('ETC', 'Thuốc kê đơn', 'Thuốc ETC', 1, 2),

       ('SUP', 'Thực phẩm chức năng', 'Nhóm thực phẩm chức năng', NULL, 2),
       ('VIT', 'Vitamin & Khoáng chất', 'Vitamin và khoáng', 4, 1),
       ('IMM', 'Tăng đề kháng', 'Hỗ trợ miễn dịch', 4, 2),

       ('EQUIP', 'Thiết bị y tế', 'Thiết bị y tế', NULL, 3),
       ('BP', 'Máy đo huyết áp', 'Thiết bị đo huyết áp', 7, 1),
       ('GLU', 'Máy đo đường huyết', 'Thiết bị đo đường huyết', 7, 2),

       ('CARE', 'Chăm sóc cá nhân', 'Chăm sóc sức khỏe cá nhân', NULL, 4),
       ('SKIN', 'Chăm sóc da', 'Sản phẩm chăm sóc da', 10, 1),
       ('BABY', 'Chăm sóc trẻ em', 'Sản phẩm cho trẻ em', 10, 2);


INSERT INTO manufacturers (code, name, country, address, phone, email, contact_person)
VALUES ('DHG', 'Dược Hậu Giang', 'Việt Nam', 'Cần Thơ', '02923895555', 'info@dhgpharma.com.vn', 'Nguyễn Văn A'),
       ('TRA', 'Traphaco', 'Việt Nam', 'Hà Nội', '02437633333', 'contact@traphaco.com.vn', 'Trần Thị B'),
       ('IMEX', 'Imexpharm', 'Việt Nam', 'Đồng Tháp', '02773856789', 'info@imexpharm.com', 'Lê Văn C'),
       ('SANOFI', 'Sanofi', 'Pháp', 'Paris, France', '+33123456789', 'contact@sanofi.com', 'Jean Dupont'),
       ('GSK', 'GlaxoSmithKline', 'Anh', 'London, UK', '+442012345678', 'info@gsk.com', 'David Smith'),
       ('PFIZER', 'Pfizer', 'Mỹ', 'New York, USA', '+12125551234', 'support@pfizer.com', 'Michael Johnson'),
       ('ABBOTT', 'Abbott', 'Mỹ', 'Illinois, USA', '+13125559876', 'info@abbott.com', 'Emily Brown'),
       ('BAYER', 'Bayer', 'Đức', 'Leverkusen, Germany', '+492145123456', 'contact@bayer.com', 'Hans Müller'),
       ('ROCHE', 'Roche', 'Thụy Sĩ', 'Basel, Switzerland', '+41611234567', 'info@roche.com', 'Anna Keller'),
       ('MEDELA', 'Medela', 'Thụy Sĩ', 'Zug, Switzerland', '+41411223344', 'contact@medela.com', 'Laura Meier');


INSERT INTO products
(category_id, manufacturer_id, code, barcode, name, dosage_form, unit, description,
 image_url, standard_price, requires_prescription, status)
VALUES
    (2, 1, 'PARA500', '893600123001', 'Paracetamol 500mg', 'Viên nén', 'Hộp', 'Giảm đau hạ sốt', NULL, 15000, 0, 'ACTIVE'),
    (3, 2, 'AMOX500', '893600123002', 'Amoxicillin 500mg', 'Viên nang', 'Hộp', 'Kháng sinh', NULL, 55000, 1, 'ACTIVE'),
    (3, 3, 'CEPHA250', '893600123003', 'Cephalexin 250mg', 'Viên nang', 'Hộp', 'Kháng sinh cephalosporin', NULL, 65000, 1, 'ACTIVE'),
    (2, 4, 'PANADOL', '893600123004', 'Panadol Extra', 'Viên nén', 'Hộp', 'Giảm đau nhanh', NULL, 30000, 0, 'ACTIVE'),
    (3, 5, 'AUGMENTIN', '893600123005', 'Augmentin 625mg', 'Viên nén', 'Hộp', 'Kháng sinh phổ rộng', NULL, 120000, 1, 'ACTIVE'),

    (5, 7, 'VITC500', '893600123006', 'Vitamin C 500mg', 'Viên sủi', 'Tuýp', 'Tăng đề kháng', NULL, 45000, 0, 'ACTIVE'),
    (6, 8, 'IMMUNO', '893600123007', 'Immuno Boost', 'Viên nang', 'Hộp', 'Hỗ trợ miễn dịch', NULL, 98000, 0, 'ACTIVE'),

    (8, 9, 'BPOMRON', '893600123008', 'Máy đo huyết áp Omron', 'Thiết bị', 'Cái', 'Đo huyết áp điện tử', NULL, 750000, 0, 'ACTIVE'),
    (9, 9, 'GLUROCHE', '893600123009', 'Máy đo đường huyết Roche', 'Thiết bị', 'Cái', 'Đo đường huyết', NULL, 680000, 0, 'ACTIVE'),

    (11, 6, 'ACNEGEL', '893600123010', 'Gel trị mụn', 'Gel', 'Tuýp', 'Điều trị mụn', NULL, 85000, 0, 'ACTIVE'),
    (11, 8, 'SUNSCREEN', '893600123011', 'Kem chống nắng SPF50', 'Kem', 'Tuýp', 'Bảo vệ da', NULL, 120000, 0, 'ACTIVE'),

    (12, 10, 'BABYMILK', '893600123012', 'Sữa cho trẻ sơ sinh', 'Bột', 'Lon', 'Dinh dưỡng cho bé', NULL, 420000, 0, 'ACTIVE'),
    (12, 10, 'BABYCARE', '893600123013', 'Dung dịch vệ sinh cho bé', 'Dung dịch', 'Chai', 'Chăm sóc trẻ em', NULL, 68000, 0, 'ACTIVE'),

    (2, 1, 'IBU400', '893600123014', 'Ibuprofen 400mg', 'Viên nén', 'Hộp', 'Giảm đau kháng viêm', NULL, 28000, 0, 'ACTIVE'),
    (3, 5, 'ZITHRO', '893600123015', 'Azithromycin 500mg', 'Viên nén', 'Hộp', 'Kháng sinh macrolid', NULL, 135000, 1, 'ACTIVE');


-- SPRINT WAREHOUSE AND INVENTORY
# warehouses
# → batches
# → inventory_items
# → stock_movements


-- Kho mẫu
INSERT INTO warehouses (code, name, address, phone, manager_name)
VALUES
    ('WH-HN', 'Kho Hà Nội', 'Cầu Giấy, Hà Nội', '02439998888', 'Nguyễn Văn Kho'),
    ('WH-HCM', 'Kho Hồ Chí Minh', 'Quận 7, TP.HCM', '02838887777', 'Trần Thị Kho'),
    ('WH-CT', 'Kho Cần Thơ', 'Ninh Kiều, Cần Thơ', '02923886666', 'Lê Văn Kho');

-- Lô hàng mẫu
INSERT INTO batches
(product_id, batch_code, import_price, manufacture_date, expiry_date, supplier_name, status)
VALUES
-- Paracetamol
(1, 'PARA-2024-01', 9000, '2024-01-01', '2026-01-01', 'Công ty Dược A', 'ACTIVE'),
(1, 'PARA-2023-06', 8500, '2023-06-01', '2025-06-01', 'Công ty Dược A', 'ACTIVE'),

-- Amoxicillin
(2, 'AMOX-2024-02', 35000, '2024-02-01', '2026-02-01', 'Công ty Dược B', 'ACTIVE'),

-- Vitamin C
(6, 'VITC-2024-03', 28000, '2024-03-01', '2026-03-01', 'Công ty Dược C', 'ACTIVE'),

-- Augmentin
(5, 'AUG-2023-05', 90000, '2023-05-01', '2025-05-01', 'Công ty Dược D', 'ACTIVE'),

-- Batch đã hết hạn (test logic)
(3, 'CEPHA-2022-01', 40000, '2022-01-01', '2024-01-01', 'Công ty Dược E', 'EXPIRED');

-- Hàng tồn kho mẫu
INSERT INTO inventory_items
(product_id, batch_id, warehouse_id, quantity_on_hand, quantity_reserved, min_threshold, max_threshold)
VALUES
-- Kho Hà Nội
(1, 1, 1, 500, 50, 100, 1000),   -- Paracetamol
(1, 2, 1, 300, 20, 100, 1000),
(2, 3, 1, 200, 10, 50, 500),
(6, 4, 1, 150, 0, 30, 300),

-- Kho HCM
(1, 1, 2, 400, 30, 100, 1000),
(5, 5, 2, 120, 0, 30, 300),

-- Kho Cần Thơ
(6, 4, 3, 100, 0, 30, 300);

-- Giao dịch xuất nhập kho mẫu
INSERT INTO stock_movements
(movement_type, product_id, batch_id, warehouse_id,
 quantity, quantity_before, quantity_after,
 reference_type, reference_id, performed_by_user_id, note)
VALUES
-- Nhập kho Paracetamol HN
('IMPORT', 1, 1, 1, 500, 0, 500, 'IMPORT_ORDER', 1001, 1, 'Nhập hàng đầu năm'),

-- Xuất kho Paracetamol
('EXPORT', 1, 1, 1, 50, 500, 450, 'SALE_ORDER', 2001, 1, 'Bán lẻ'),

-- Nhập Amoxicillin
('IMPORT', 2, 3, 1, 200, 0, 200, 'IMPORT_ORDER', 1002, 1, 'Nhập kháng sinh'),

-- Điều chỉnh tồn Vitamin C
('ADJUST', 6, 4, 1, -10, 150, 140, 'STOCK_CHECK', NULL, 1, 'Hỏng bao bì');


UPDATE accounts
SET password = '$2a$12$ZbxOL6/mxxY65NraoXE2GeeS2HFNVtZvJhlNt7Vtg.XNJ5HKNnd9q'
WHERE id = 1;




--Test đơn hàng
-- thêm để miêu tả thuốc dùng như nào
ALTER TABLE order_items
ADD COLUMN note VARCHAR(255);


INSERT INTO accounts (username, password, role)
VALUES ('emp01', '123456', 'EMPLOYEE');

INSERT INTO user_profiles (account_id, code, full_name, phone, status)
VALUES (LAST_INSERT_ID(), 'EMP001', 'Nguyễn Văn Nhân Viên', '0909000001', 'ACTIVE');

INSERT INTO employee_profiles (profile_id, position, department)
VALUES (LAST_INSERT_ID(), 'Bán hàng', 'Sales');


INSERT INTO accounts (username, password, role)
VALUES ('cus01', '123456', 'CUSTOMER');

INSERT INTO user_profiles (account_id, code, full_name, phone, status)
VALUES (LAST_INSERT_ID(), 'CUS001123', 'Trần Thị Kháchk', '0909000002', 'ACTIVE');

INSERT INTO customer_profiles (profile_id, customer_type)
VALUES (LAST_INSERT_ID(), 'RETAIL');


INSERT INTO products
(category_id, manufacturer_id, code, name, standard_price, status)
VALUES
(1, 1, 'P001', 'Paracetamol 500mg', 15000, 'ACTIVE'),
(1, 1, 'P002', 'Amoxicillin 500mg', 45000, 'ACTIVE');

INSERT INTO batches
(product_id, batch_code, import_price, manufacture_date, expiry_date, status)
VALUES
(1, 'BATCH-P001-01', 10000, '2024-01-01', '2026-01-01', 'ACTIVE'),
(2, 'BATCH-P002-01', 30000, '2024-02-01', '2026-02-01', 'ACTIVE');


ALTER TABLE order_items ADD COLUMN warehouse_id BIGINT NULL;
ALTER TABLE order_items ADD COLUMN note varchar(255);