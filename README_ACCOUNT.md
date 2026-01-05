# 📘 MODULE ACCOUNT - QUẢN LÝ TÀI KHOẢN

## 📋 MỤC LỤC
1. [Tổng quan](#tổng-quan)
2. [Luồng xử lý](#luồng-xử-lý)
3. [Chi tiết code](#chi-tiết-code)
4. [Câu hỏi vấn đáp](#câu-hỏi-vấn-đáp)
5. [Demo scenarios](#demo-scenarios)

---

## 🎯 TỔNG QUAN

### **Chức năng chính:**
- ✅ Đăng nhập (Login)
- ✅ Đăng ký (Register)
- ✅ Xem danh sách tài khoản
- ✅ Tìm kiếm tài khoản
- ✅ Tạo/Sửa/Xóa tài khoản
- ✅ Phân quyền (Role: ADMIN, EMPLOYEE, CUSTOMER)
- ✅ Export Excel

### **Kiến trúc:**
```
View (LoginForm, AccountManagementPanel)
  ↓
Controller (AccountController)
  ↓
Service (AuthService, AccountService)
  ↓
Repository (AccountRepository)
  ↓
Database (accounts table)
```

---

## 🔄 LUỒNG XỬ LÝ

### **1. ĐĂNG NHẬP (Login)**

```
User nhập username/password
  ↓
LoginForm.onLogin()
  ↓
AuthService.login(LoginRequest)
  ├─ Validate input (null/blank check)
  ├─ AccountRepository.findByUsername(username)
  │  └─ SQL: SELECT * FROM accounts WHERE username = ?
  ├─ Check account.isActive()
  ├─ BCrypt.checkpw(plainPassword, hashedPassword)
  ├─ AccountRepository.updateLastLogin(accountId)  ← FIX BUG #6
  │  └─ SQL: UPDATE accounts SET last_login_at = NOW() WHERE id = ?
  ├─ AuthContextManager.setUser(userId, role)
  └─ Return Account
  ↓
MainFrame(username, role)
```

**Code quan trọng:**
```java
// AuthServiceImpl.java
public Account login(LoginRequest request) {
    // 1. Validate
    if (username == null || username.isBlank()) return null;
    if (password == null || password.isBlank()) return null;
    
    // 2. Query DB
    Account account = accountRepository.findByUsername(username);
    if (account == null) return null;
    
    // 3. Check active
    if (!account.isActive()) {
        log.info("Login failed: account inactive");
        return null;
    }
    
    // 4. Verify password
    if (!BCrypt.checkpw(password, account.getPassword())) {
        log.info("Login failed: incorrect password");
        return null;
    }
    
    // 5. Update last login ✅ FIX BUG
    accountRepository.updateLastLogin(account.getId());
    
    // 6. Set session
    currentAccount = account;
    Long userProfileId = accountRepository.findUserIdByAccountId(account.getId());
    currentUserProfileId = userProfileId;
    
    return currentAccount;
}
```

---

### **2. ĐĂNG KÝ (Register)**

```
User nhập thông tin đăng ký
  ↓
RegisterForm.onRegister()
  ↓
AuthService.register(RegisterRequest)
  ├─ validateInfo(request)
  │  ├─ Username: >= 3 ký tự, <= 50 ký tự
  │  ├─ Password: >= 6 ký tự, <= 100 ký tự
  │  ├─ Password không chỉ toàn khoảng trắng
  │  ├─ Password = Confirm Password
  │  └─ Username chưa tồn tại
  ├─ UserCreationService.registerCustomer(request)
  │  ├─ Hash password: BCrypt.hashpw(password, BCrypt.gensalt(12))
  │  ├─ INSERT INTO accounts (username, password, role, is_active)
  │  ├─ INSERT INTO user_profiles (account_id, code, full_name, ...)
  │  └─ INSERT INTO customer_profiles (profile_id, ...)
  └─ Transaction commit
```

**Code validate password - FIX BUG #9:**
```java
private void validateInfo(RegisterRequest request) {
    // Password length
    if (password.length() < 6) {
        throw new RuntimeException("Password phải có ít nhất 6 ký tự");
    }
    if (password.length() > 100) {
        throw new RuntimeException("Password không được quá 100 ký tự");
    }
    
    // Không chỉ khoảng trắng
    if (password.trim().length() < 6) {
        throw new RuntimeException("Password không hợp lệ (chỉ chứa khoảng trắng)");
    }
    
    // Confirm password
    if (!request.isPasswordConfirmed()) {
        throw new RuntimeException("Password và Confirm Password không khớp!");
    }
    
    // Username format
    if (username.length() < 3) {
        throw new RuntimeException("Username phải có ít nhất 3 ký tự");
    }
    
    // Check exists
    if (accountRepository.existsByUsername(username.trim())) {
        throw new RuntimeException("Username đã tồn tại!");
    }
}
```

---

### **3. QUẢN LÝ TÀI KHOẢN (CRUD)**

#### **a. Xem danh sách:**
```java
// AccountController
public List<Account> getAllAccounts() {
    return accountService.getAll();
}

// AccountRepository
public List<Account> findAll() {
    String sql = """
        SELECT id, username, password, role, is_active, last_login_at
        FROM accounts
    """;
    return queryForList(sql, this::mapRow);
}
```

#### **b. Tìm kiếm:**
```java
// Tìm chính xác username
public Account findByUsername(String username) {
    String sql = """
        SELECT id, username, password, role, is_active, last_login_at
        FROM accounts
        WHERE username = ?
    """;
    return queryForObject(sql, params(username), this::mapRow).orElse(null);
}
```

#### **c. Tạo tài khoản:**
```java
public long createAccount(CreateAccountRequest request) {
    return userCreationService.createAccount(request);
}

// Trong UserCreationService:
public long createAccount(CreateAccountRequest request) {
    try (Connection conn = getConnection()) {
        conn.setAutoCommit(false);
        try {
            // 1. Hash password
            String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));
            
            // 2. Insert account
            Account account = new Account();
            account.setUsername(request.getUsername());
            account.setPassword(hashedPassword);
            account.setRole(request.getRole());
            account.setActive(true);
            long accountId = accountRepository.insert(conn, account);
            
            // 3. Insert user_profile
            // 4. Insert employee_profile hoặc customer_profile
            
            conn.commit();
            return accountId;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        }
    }
}
```

#### **d. Cập nhật:**
```java
public void updateRoleAndStatus(long accountId, Role role, Boolean active) {
    String sql = """
        UPDATE accounts
        SET role = ?, is_active = ?
        WHERE id = ?
    """;
    executeUpdate(sql, params(role.name(), active, accountId));
}
```

#### **e. Xóa (Soft Delete):**
```java
public void deleteById(Long id) {
    String sql = """
        UPDATE accounts
        SET is_active = FALSE
        WHERE id = ?
    """;
    executeUpdate(sql, params(id));
}
```

**⚠️ Vấn đề:** Không kiểm tra FK constraints!

---

## 🔐 BẢO MẬT

### **1. BCrypt Password Hashing**

**Tại sao dùng BCrypt?**
- ✅ Salt tự động → Mỗi password hash ra khác nhau
- ✅ Chậm (work factor 12) → Chống brute force
- ✅ Adaptive → Có thể tăng độ khó theo thời gian

**Không dùng MD5/SHA:**
- ❌ Quá nhanh → GPU crack millions hash/giây
- ❌ Không có salt → Rainbow table attack
- ❌ Deprecated → Không an toàn

**Code:**
```java
// Đăng ký
String hashed = BCrypt.hashpw("myPassword123", BCrypt.gensalt(12));
// → $2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW

// Đăng nhập
boolean match = BCrypt.checkpw("myPassword123", hashed); // true
boolean match2 = BCrypt.checkpw("wrong", hashed);        // false
```

### **2. Session Management**

```java
public class AuthContextManager {
    private static Long userId;
    private static Role role;
    
    public static void setUser(Long id, Role userRole) {
        userId = id;
        role = userRole;
    }
    
    public static boolean isAdmin() {
        return role == Role.ADMIN;
    }
    
    public static void clear() {
        userId = null;
        role = null;
    }
}
```

**⚠️ Lưu ý:** Dùng static → Không thread-safe. OK cho desktop app (1 user), nhưng không phù hợp cho web app.

---

## 🔍 FILTER & SEARCH

### **AccountManagementPanel - Stream API (Server-side)**

```java
private void applyFilters() {
    // 1. Load ALL data từ DB
    List<Account> data = controller.getAllAccounts();
    
    // 2. Filter bằng Java Stream
    String role = (String) roleFilter.getSelectedItem();
    String status = (String) statusFilter.getSelectedItem();
    
    List<Account> filtered = data.stream()
        .filter(a -> "ALL".equals(role) || a.getRole().name().equals(role))
        .filter(a -> "ALL".equals(status) ||
                ("ACTIVE".equals(status) && a.isActive()) ||
                ("INACTIVE".equals(status) && !a.isActive()))
        .collect(Collectors.toList());
    
    // 3. Re-render table
    renderTable(filtered);
}
```

**Đặc điểm:**
- ❌ Query all mỗi lần filter
- ❌ Clear table rồi add lại rows → UI flicker
- ❌ Chậm với nhiều records

**So sánh với TableRowSorter (các màn khác):**
- ✅ TableRowSorter: Query 1 lần, filter trên memory
- ✅ Nhanh, smooth, real-time

---

## 📊 EXPORT EXCEL

### **Luồng:**
```
User click "Export Excel"
  ↓
AccountController.getAllAccountResponses(component)
  ↓
ExcelExporter.exportWithDialog(data, AccountExcelWriter, parent)
  ├─ JFileChooser → Chọn nơi lưu
  ├─ Create XSSFWorkbook
  ├─ AccountExcelWriter.create()
  │  ├─ Title row (merged)
  │  ├─ Header row (bold, colored)
  │  ├─ Data rows
  │  └─ Auto-size columns
  └─ Write to file
```

**Code:**
```java
@Override
public void create(Sheet sheet, ExcelStyles styles, List<AccountResponse> data) {
    // 1. Title
    Row titleRow = sheet.createRow(0);
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("DANH SÁCH TÀI KHOẢN");
    titleCell.setCellStyle(styles.get(StyleKey.TITLE));
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
    
    // 2. Headers
    Row headerRow = sheet.createRow(1);
    for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
        cell.setCellStyle(styles.get(StyleKey.HEADER));
    }
    
    // 3. Data
    int rowNum = 2;
    for (AccountResponse account : data) {
        Row row = sheet.createRow(rowNum++);
        cell(row, 0, stt++, styles.get(StyleKey.DATA_CENTER));
        cell(row, 1, account.getId(), styles.get(StyleKey.DATA_CENTER));
        cell(row, 2, account.getUsername(), styles.get(StyleKey.DATA));
        cell(row, 3, account.getRole().name(), styles.get(StyleKey.DATA_CENTER));
        
        // Badge style cho status
        String status = account.isActive() ? "ACTIVE" : "INACTIVE";
        CellStyle badgeStyle = account.isActive() 
            ? styles.get(StyleKey.BADGE_ACTIVE) 
            : styles.get(StyleKey.BADGE_INACTIVE);
        cell(row, 4, status, badgeStyle);
        
        cell(row, 5, account.getLastLoginAt(), styles.get(StyleKey.DATA_CENTER));
    }
}
```

---

## ❓ CÂU HỎI VẤN ĐÁP

### **Q1: Giải thích luồng đăng nhập chi tiết?**
**A:** 
1. User nhập username/password vào LoginForm
2. Click button → gọi AuthService.login()
3. Service query DB: `SELECT * FROM accounts WHERE username = ?`
4. Kiểm tra account có tồn tại và is_active = true
5. So sánh password: `BCrypt.checkpw(input, hashed)`
6. Update last_login_at vào DB
7. Lưu userId và role vào AuthContextManager (session)
8. Chuyển sang MainFrame với quyền tương ứng

### **Q2: Tại sao dùng BCrypt thay vì MD5?**
**A:**
- BCrypt có salt tự động → Mỗi password hash khác nhau
- BCrypt chậm (work factor 12) → Chống brute force
- MD5 quá nhanh → GPU crack được millions hash/giây
- MD5 không có salt → Rainbow table attack

### **Q3: Soft delete là gì? Tại sao dùng?**
**A:**
- Soft delete = set `is_active = FALSE` thay vì `DELETE FROM`
- Lý do:
  1. Giữ audit trail (ai làm gì khi nào)
  2. Tránh mất dữ liệu FK (orders, invoices...)
  3. Có thể khôi phục
  4. Analytics dữ liệu lịch sử

### **Q4: Validate password gồm những gì?**
**A:**
1. Không null/blank
2. Độ dài >= 6, <= 100 ký tự
3. Không chỉ toàn khoảng trắng
4. Password = Confirm Password
5. Username >= 3, <= 50 ký tự
6. Username chưa tồn tại trong DB

### **Q5: Filter account dùng cách nào?**
**A:** Dùng Stream API (server-side):
- Load ALL accounts từ DB
- Filter bằng `.stream().filter()`
- Re-render table
- ⚠️ Nhược điểm: Chậm với nhiều records, query lại DB mỗi lần

### **Q6: Bug bạn đã fix là gì?**
**A:** 
- **BUG #6:** Login không update last_login_at
  - Thêm method `updateLastLogin()` trong Repository
  - Gọi sau khi verify password thành công
  - SQL: `UPDATE accounts SET last_login_at = NOW() WHERE id = ?`

---

## 🎬 DEMO SCENARIOS

### **Scenario 1: Demo đăng nhập thành công**
```
1. Mở LoginForm
2. Nhập username: "admin", password: "admin123"
3. Click "Đăng nhập"
4. → Hiện message "Đăng nhập thành công"
5. → MainFrame hiển thị với menu đầy đủ (ADMIN)
6. Kiểm tra DB: last_login_at đã update
```

### **Scenario 2: Demo đăng nhập sai password**
```
1. Nhập username: "admin", password: "wrong"
2. Click "Đăng nhập"
3. → Message "Sai tài khoản hoặc mật khẩu!"
4. → Vẫn ở LoginForm
```

### **Scenario 3: Demo đăng ký tài khoản**
```
1. Click "Chưa có tài khoản? Đăng ký ngay"
2. Nhập:
   - Username: "testuser"
   - Password: "test123"
   - Confirm: "test123"
   - Full name: "Test User"
3. Click "Đăng ký"
4. → Tạo account với role CUSTOMER
5. → Tự động login và vào MainFrame
```

### **Scenario 4: Demo filter account**
```
1. Vào AccountManagementPanel
2. Dropdown "Role": ADMIN
3. Dropdown "Status": ACTIVE
4. → Table chỉ hiện accounts ADMIN và ACTIVE
```

### **Scenario 5: Demo export Excel**
```
1. Click "Export Excel"
2. Chọn Desktop/accounts.xlsx
3. → File được tạo với:
   - Title row merged
   - Headers màu xanh bold
   - Data rows với badge ACTIVE/INACTIVE
4. Mở file → Check format
```

---

## 🐛 BUGS ĐÃ FIX

### **BUG #6: Login không update last_login_at**
```java
// ❌ TRƯỚC:
public Account login(LoginRequest request) {
    // ... validate & verify ...
    currentAccount = account;
    return account;
}

// ✅ SAU:
public Account login(LoginRequest request) {
    // ... validate & verify ...
    accountRepository.updateLastLogin(account.getId()); // ← FIX
    currentAccount = account;
    return account;
}
```

### **BUG #9: Không validate password strength**
```java
// ❌ TRƯỚC:
if (request.getPassword() == null || request.getPassword().isBlank()) {
    throw new RuntimeException("Password không được để trống");
}

// ✅ SAU:
String password = request.getPassword();
if (password.length() < 6) {
    throw new RuntimeException("Password phải có ít nhất 6 ký tự");
}
if (password.trim().length() < 6) {
    throw new RuntimeException("Password không hợp lệ (chỉ chứa khoảng trắng)");
}
// ... thêm các validate khác
```

---

## 📝 CHECKLIST ÔN TẬP

- [ ] Giải thích được luồng đăng nhập đầy đủ
- [ ] So sánh BCrypt vs MD5/SHA
- [ ] Giải thích soft delete vs hard delete
- [ ] Validate password có những gì
- [ ] Filter dùng Stream API hoạt động ra sao
- [ ] Export Excel gồm những bước nào
- [ ] Bugs đã fix và cách fix
- [ ] Demo được các scenarios

---

**🎯 Mục tiêu:** Nắm vững module Account để trả lời tự tin khi thi vấn đáp!
