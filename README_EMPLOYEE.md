# 📘 MODULE EMPLOYEE - QUẢN LÝ NHÂN VIÊN

## 📋 MỤC LỤC
1. [Tổng quan](#tổng-quan)
2. [Cấu trúc dữ liệu](#cấu-trúc-dữ-liệu)
3. [Luồng xử lý](#luồng-xử-lý)
4. [Filter & Search](#filter--search)
5. [Export Excel](#export-excel)
6. [Câu hỏi vấn đáp](#câu-hỏi-vấn-đáp)

---

## 🎯 TỔNG QUAN

### **Chức năng chính:**
- ✅ Xem danh sách nhân viên
- ✅ Xem chi tiết nhân viên
- ✅ Tìm kiếm & Filter (keyword, department, status)
- ✅ Cập nhật thông tin nhân viên
- ✅ Xóa nhân viên (soft delete)
- ✅ Export Excel (basic & detailed)

### **Kiến trúc:**
```
View (EmployeeManagementPanel)
  ↓
Controller (EmployeeController)
  ↓
Service (EmployeeService)
  ↓
Repository (EmployeeProfileRepository, UserProfileRepository)
  ↓
Database (accounts, user_profiles, employee_profiles)
```

---

## 🗃️ CẤU TRÚC DỮ LIỆU

### **Quan hệ bảng:**
```
accounts (1) ←→ (1) user_profiles (1) ←→ (1) employee_profiles
```

### **Chi tiết các bảng:**

#### **1. accounts:**
```sql
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE', 'CUSTOMER'),
    is_active BOOLEAN DEFAULT TRUE,
    last_login_at TIMESTAMP
);
```

#### **2. user_profiles:**
```sql
CREATE TABLE user_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,        -- EMP001, CUS001
    full_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    status ENUM('ACTIVE', 'INACTIVE'),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
```

#### **3. employee_profiles:**
```sql
CREATE TABLE employee_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    position VARCHAR(100),                   -- Manager, Developer, Tester
    department VARCHAR(100),                 -- IT, HR, Finance
    hired_date DATE,
    base_salary DECIMAL(15,2),
    FOREIGN KEY (profile_id) REFERENCES user_profiles(id)
);
```

### **Tại sao tách 3 bảng?**

**accounts:** Thông tin đăng nhập (username, password, role)  
**user_profiles:** Thông tin cá nhân chung (tất cả user đều có)  
**employee_profiles:** Thông tin riêng nhân viên (chỉ employee có)

**Lợi ích:**
- ✅ Tránh null fields (customer không có position/salary)
- ✅ Dễ mở rộng (thêm doctor_profiles, supplier_profiles...)
- ✅ Normalization (tuân thủ 3NF)

---

## 🔄 LUỒNG XỬ LÝ

### **SQL JOIN 3 bảng:**
```sql
SELECT
    a.username,
    up.full_name,
    up.phone,
    ep.position,
    ep.department,
    ep.base_salary
FROM employee_profiles ep
JOIN user_profiles up ON ep.profile_id = up.id
JOIN accounts a ON up.account_id = a.id
WHERE up.code = 'EMP001'
```

---

## 🔍 FILTER & SEARCH

### **Cách hoạt động: TableRowSorter (Client-side)**

```java
private void applyFilters() {
    String kw = keywordField.getText().trim().toLowerCase();
    
    sorter.setRowFilter(new RowFilter<>() {
        @Override
        public boolean include(Entry entry) {
            String haystack = (username + code + fullName + phone + position).toLowerCase();
            if (!kw.isEmpty() && !haystack.contains(kw)) return false;
            return true; // Hiện row
        }
    });
}
```

**Đặc điểm:**
- ✅ Query DB 1 lần khi load
- ✅ Filter trên memory  
- ✅ Real-time filtering
- ✅ Smooth UX

---

## 📊 EXPORT EXCEL

### **2 loại export:**
1. **EmployeeExcelWriter:** Basic
2. **EmployeeDetailExcelWriter:** Full (+ Email, Address, Salary...)

---

## 📝 CHECKLIST ÔN TẬP

- [ ] Giải thích cấu trúc 3 bảng
- [ ] Viết SQL JOIN 3 bảng
- [ ] Filter dùng TableRowSorter
- [ ] So sánh 2 loại export Excel
- [ ] Demo được scenarios

**🎯 Nắm vững module Employee!**
