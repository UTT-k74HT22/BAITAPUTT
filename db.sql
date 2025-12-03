CREATE DATABASE IF NOT EXISTS qlsv;
USE qlsv;

CREATE TABLE IF NOT EXISTS accounts (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        username VARCHAR(50) NOT NULL UNIQUE,
                                        password VARCHAR(255) NOT NULL,
                                        full_name VARCHAR(100),
                                        role VARCHAR(50),
                                        is_active TINYINT(1) NOT NULL DEFAULT 1
);

-- Insert user admin nếu chưa tồn tại
INSERT INTO accounts (username, password, full_name, role, is_active)
SELECT 'admin', '123456', 'Quản trị viên', 'ADMIN', 1
WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE username = 'admin'
);
