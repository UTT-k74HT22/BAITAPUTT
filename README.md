# 🏥 Hệ Thống Quản Lý Nhà Thuốc (Hospital Management System)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Apache POI](https://img.shields.io/badge/Apache%20POI-5.2.5-green.svg)](https://poi.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> Ứng dụng Desktop quản lý toàn diện cho nhà thuốc, xây dựng bằng Java Swing với kiến trúc phân tầng chuẩn MVC.

---

## 📋 Mục Lục

- [Giới Thiệu](#-giới-thiệu)
- [Tính Năng](#-tính-năng)
- [Kiến Trúc & Công Nghệ](#-kiến-trúc--công-nghệ)
- [Những Điểm Triển Khai Chuẩn Chỉnh](#-những-điểm-triển-khai-chuẩn-chỉnh)
- [Cài Đặt](#-cài-đặt)
- [Hướng Dẫn Sử Dụng](#-hướng-dẫn-sử-dụng)
- [Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
- [Database Schema](#-database-schema)
- [Screenshots](#-screenshots)
- [Đóng Góp](#-đóng-góp)
- [Nhóm Phát Triển](#-nhóm-phát-triển)
- [License](#-license)

---

## 🎯 Giới Thiệu

**Hệ Thống Quản Lý Nhà Thuốc** là ứng dụng desktop được phát triển nhằm số hóa và tối ưu hóa quy trình quản lý cho các nhà thuốc vừa và nhỏ. Hệ thống cung cấp đầy đủ các chức năng từ quản lý sản phẩm, tồn kho, khách hàng, nhân viên đến báo cáo thống kê và xuất nhập dữ liệu.

### 🎓 Đề Tài Đồ Án

- **Môn học:** Java Nâng Cao
- **Trường:** Đại học Công Nghệ Giao Thông Vận Tải (UTT)
- **Năm học:** 2025-2026
- **Thời gian thực hiện:** Tháng 10/2025

---

## ✨ Tính Năng

### 🔐 Quản Lý Tài Khoản & Phân Quyền

- ✅ Đăng ký tài khoản mới với mã hóa mật khẩu BCrypt
- ✅ Đăng nhập với xác thực bảo mật
- ✅ Phân quyền theo vai trò (Admin, Nhân viên, Quản lý kho)
- ✅ Quản lý profile người dùng

### 📦 Quản Lý Sản Phẩm

- ✅ **CRUD đầy đủ:** Thêm, sửa, xóa, tìm kiếm sản phẩm
- ✅ **Phân loại:** Quản lý theo danh mục và nhà sản xuất
- ✅ **Thông tin chi tiết:** Mã sản phẩm, barcode, dạng bào chế, giá, trạng thái
- ✅ **Quản lý toa thuốc:** Đánh dấu sản phẩm cần kê đơn
- ✅ **Filter nâng cao:** Lọc theo trạng thái (Active/Inactive/Discontinued)

### 📊 Quản Lý Danh Mục & Nhà Sản Xuất

- ✅ Quản lý cây danh mục phân cấp (Category hierarchy)
- ✅ Quản lý thông tin nhà sản xuất
- ✅ Sắp xếp thứ tự hiển thị
- ✅ Kích hoạt/Vô hiệu hóa danh mục

### 🏢 Quản Lý Kho

- ✅ Quản lý nhiều kho hàng
- ✅ Theo dõi tồn kho theo từng kho
- ✅ Quản lý lô hàng (Batch) với hạn sử dụng
- ✅ Cảnh báo hàng sắp hết hạn
- ✅ Nhập/Xuất kho với lịch sử đầy đủ

### 👥 Quản Lý Khách Hàng & Nhân Viên

- ✅ **Khách hàng:** Thông tin cá nhân, lịch sử mua hàng, điểm thưởng
- ✅ **Nhân viên:** Profile, chức vụ, lịch sử làm việc
- ✅ Tìm kiếm và filter nhanh
- ✅ Quản lý thông tin liên hệ

### 📈 Dashboard & Báo Cáo

- ✅ Tổng quan doanh thu theo ngày/tháng/năm
- ✅ Thống kê sản phẩm bán chạy
- ✅ Biểu đồ trực quan (Chart)
- ✅ Báo cáo tồn kho
- ✅ Báo cáo công nợ

### 📄 Import/Export Dữ Liệu

#### Import Excel
- ✅ **Template Generator:** Tạo file Excel mẫu tự động
- ✅ **Validation 2 tầng:**
  - Validate định dạng file (header, kiểu dữ liệu)
  - Validate business logic (trùng mã, foreign key tồn tại)
- ✅ **Preview trước khi import:**
  - Hiển thị dòng hợp lệ/không hợp lệ
  - Chi tiết lỗi từng dòng
  - Chỉ import dòng hợp lệ
- ✅ **Xử lý lỗi thông minh:** Không crash khi gặp dòng lỗi

#### Export Excel
- ✅ Xuất danh sách sản phẩm, khách hàng, nhân viên...
- ✅ **Formatting chuyên nghiệp:**
  - Header với style màu sắc
  - Freeze pane (đóng băng tiêu đề)
  - Auto-size columns
  - Badge màu cho status
- ✅ **Format dữ liệu:** Date (dd/MM/yyyy), Number, Currency
- ✅ Mở file/folder ngay sau khi export

#### Export PDF
- ✅ Xuất chi tiết sản phẩm
- ✅ In hóa đơn, phiếu xuất kho
- ✅ Báo cáo với logo và thông tin doanh nghiệp

### 🔍 Tìm Kiếm & Lọc

- ✅ Tìm kiếm toàn văn (Full-text search)
- ✅ Lọc theo nhiều tiêu chí
- ✅ Sắp xếp đa cột
- ✅ Highlight kết quả tìm kiếm

---

## 🏗️ Kiến Trúc & Công Nghệ

### Mô Hình Kiến Trúc: **Layered Architecture (MVC Variant)**

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│              (View - Java Swing Components)              │
│  - ProductManagementPanel, CustomerManagementPanel...   │
│  - Dialog, Form, Custom Components                      │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│                    Controller Layer                      │
│  - ProductController, CustomerController...             │
│  - Điều phối giữa View và Service                       │
│  - Validation input đầu vào                             │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│                     Service Layer                        │
│  - ProductService, CategoryService...                   │
│  - Business Logic, Transaction Management               │
│  - Orchestration nhiều Repository                       │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│                   Repository Layer                       │
│  - ProductRepository, BaseRepository                    │
│  - Data Access Logic, SQL Execution                     │
│  - JDBC Template (tự build)                             │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────┐
│                    Database (MySQL)                      │
│  - products, categories, customers, orders...           │
└─────────────────────────────────────────────────────────┘
```

### Công Nghệ Sử Dụng

| Công nghệ | Version | Mục đích |
|-----------|---------|----------|
| **Java** | 17 | Core language, Desktop app |
| **Swing** | Built-in | GUI Framework |
| **MySQL** | 8.0.33 | Relational Database |
| **JDBC** | Built-in | Database connectivity |
| **Apache POI** | 5.2.5 | Import/Export Excel (.xlsx) |
| **OpenPDF** | 1.3.30 | Generate PDF reports |
| **BCrypt** | 0.4 | Password hashing |
| **Log4j2** | 2.20.0 | Logging framework |
| **Maven** | 3.x | Build tool, Dependency management |

---

## 🌟 Những Điểm Triển Khai Chuẩn Chỉnh

### 1. ⚙️ Design Patterns

#### ✅ **Template Method Pattern**
```java
// AbstractImportService<T> - Base class cho tất cả Import service
public abstract class AbstractImportService<T> {
    protected abstract ImportMapper<T> getMapper();
    protected abstract ImportValidator<T> getValidator();
    protected abstract void saveData(List<T> validData);
    
    // Template method - Định nghĩa luồng chung
    public ImportPreviewResponse<T> previewFromFile(File file) {
        // 1. Validate headers
        // 2. Map data
        // 3. Validate business
        // 4. Return preview
    }
}

// ProductImportService chỉ cần implement 3 method
public class ProductImportService extends AbstractImportService<ImportProductRequest> {
    @Override protected ImportMapper<ImportProductRequest> getMapper() { ... }
    @Override protected ImportValidator<ImportProductRequest> getValidator() { ... }
    @Override protected void saveData(List<ImportProductRequest> validData) { ... }
}
```

**Lợi ích:**
- ♻️ Tái sử dụng logic chung cho tất cả entity
- 🔒 Đảm bảo luồng xử lý nhất quán
- 🚀 Dễ mở rộng cho entity mới

#### ✅ **Strategy Pattern**
```java
// Interface định nghĩa contract
public interface ExcelSheetWriter<T> {
    String sheetName();
    String title();
    String[] headers();
    void create(Sheet sheet, ExcelStyles styles, List<T> data);
}

// Mỗi entity có Strategy riêng
public class ProductExcelWriter implements ExcelSheetWriter<ProductResponse> { ... }
public class CategoryExcelWriter implements ExcelSheetWriter<CategoryResponse> { ... }

// Client code
ExcelExporter.exportWithDialog(data, new ProductExcelWriter(), parent);
```

**Lợi ích:**
- 🎯 Tách biệt logic export từng loại dữ liệu
- 🔄 Dễ swap implementation
- 📦 SOLID: Open/Closed Principle

#### ✅ **Repository Pattern**
```java
// Interface
public interface ProductRepository {
    long insert(CreateProductRequest request);
    List<ProductResponse> getAll();
    Optional<ProductResponse> findByCode(String code);
    void updateByCode(String code, UpdateProductRequest request);
    void softDeleteByCode(String code);
}

// Implementation
public class ProductRepositoryImpl extends BaseRepository implements ProductRepository {
    // Chỉ chứa SQL, không có business logic
}
```

**Lợi ích:**
- 🧪 Testable: Mock được Repository khi test Service
- 🔀 Có thể đổi database mà không ảnh hưởng Service
- 🎨 Separation of Concerns

#### ✅ **DTO Pattern**
```java
// Request DTOs (UI → Service)
CreateProductRequest, UpdateProductRequest

// Response DTOs (Service → UI)  
ProductResponse, ProductDetailResponse

// Import DTOs
ImportProductRequest
```

**Lợi ích:**
- 🛡️ Không expose Entity ra UI
- ✂️ Tách biệt input/output model
- 🔐 Bảo mật: Không trả về sensitive fields

### 2. 🏛️ Clean Architecture Principles

#### ✅ **Separation of Concerns**
- **UI Layer:** Chỉ render và handle events
- **Controller:** Điều phối, không chứa business logic
- **Service:** Business logic, transaction orchestration
- **Repository:** Chỉ SQL, không biết business rules

#### ✅ **Dependency Rule**
```
View → Controller → Service → Repository → Database
(Dependency luôn hướng vào trong, không bao giờ ngược lại)
```

#### ✅ **Single Responsibility Principle**
```java
// ❌ BAD: God Object
public class ProductPanel {
    void renderUI() { ... }
    void validateInput() { ... }
    void saveToDatabase() { ... }  // WRONG!
}

// ✅ GOOD: Mỗi class một trách nhiệm
ProductPanel        → UI only
ProductController   → Orchestration
ProductService      → Business logic
ProductRepository   → Data access
```

### 3. 🔧 JDBC Best Practices

#### ✅ **BaseRepository - Mini JdbcTemplate**
```java
public abstract class BaseRepository {
    // Template methods
    protected <T> List<T> queryForList(String sql, PreparedStatementSetter setter, ResultSetMapper<T> mapper);
    protected <T> Optional<T> queryForObject(String sql, PreparedStatementSetter setter, ResultSetMapper<T> mapper);
    protected long executeInsertAndReturnId(String sql, PreparedStatementSetter setter);
    protected int executeUpdate(String sql, PreparedStatementSetter setter);
    protected boolean exists(String sql, PreparedStatementSetter setter);
    
    // Transaction support
    protected <T> T inTransaction(TransactionCallback<T> callback);
}
```

**Đặc điểm:**
- ✅ Try-with-resources cho Connection/PreparedStatement/ResultSet
- ✅ Centralized error handling
- ✅ Transaction support với commit/rollback tự động
- ✅ Generated keys handling (auto-increment ID)
- ✅ Functional interface cho cleaner code

#### ✅ **SQL Injection Prevention**
```java
// ✅ SAFE: PreparedStatement với parameters
String sql = "SELECT * FROM products WHERE code = ?";
queryForObject(sql, params(code), this::mapToRow);

// ❌ UNSAFE: String concatenation
String sql = "SELECT * FROM products WHERE code = '" + code + "'";
```

#### ✅ **Resource Management**
```java
// ✅ Luôn đóng resource trong try-with-resources
try (Connection conn = getConnection();
     PreparedStatement ps = conn.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()) {
    // Process results
}
// Auto-close: rs → ps → conn
```

### 4. 📊 Import/Export Architecture

#### ✅ **Import với Validation Pipeline**
```
File Excel
    ↓
Validate Headers (Format)
    ↓
Map Row → DTO
    ↓
Validate Business Logic
    ↓
Preview Valid/Invalid Rows
    ↓
User Confirm
    ↓
Transaction: Save All or Rollback
```

**Tính năng nổi bật:**
- 📋 Template Generator tự động
- 🔍 Validation 2 tầng (Format + Business)
- 👁️ Preview trước khi import
- 🎯 Chỉ import dòng hợp lệ
- ❌ Error handling không crash

#### ✅ **Export với Reusable Components**
```java
// Centralized styles
ExcelStyles styles = new ExcelStyles(workbook);
styles.get(StyleKey.TITLE);
styles.get(StyleKey.HEADER);
styles.get(StyleKey.BADGE_ACTIVE);

// Abstract helper cho format cell
AbstractExcelWriter.cell(row, col, value, style);
// Auto-detect: String, Number, LocalDate, BigDecimal...
```

### 5. 🔒 Security Best Practices

#### ✅ **Password Hashing với BCrypt**
```java
// Register
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

// Login  
boolean matched = BCrypt.checkpw(plainPassword, hashedPassword);
```

**Đặc điểm:**
- 🔐 One-way hashing (không thể decrypt)
- 🧂 Automatic salting
- ⚙️ Configurable work factor (12 rounds)

#### ✅ **SQL Injection Prevention**
- Dùng PreparedStatement 100%
- Không có raw SQL concatenation

### 6. 📝 Code Quality

#### ✅ **Naming Convention**
```java
// Classes: PascalCase
ProductServiceImpl, AbstractExcelWriter

// Methods: camelCase + verb
getAll(), findByCode(), validateHeaders()

// Variables: camelCase + meaningful
validRows, categoryRepository, headerRow

// Constants: UPPER_SNAKE_CASE
DB_URL, DEFAULT_PAGE_SIZE
```

#### ✅ **Meaningful Names**
```java
// ✅ GOOD
List<ProductResponse> activeProducts = productService.findByStatus(ACTIVE);

// ❌ BAD  
List<ProductResponse> list = productService.get(1);
```

#### ✅ **DRY Principle**
- BaseRepository: Tránh lặp JDBC boilerplate
- AbstractExcelWriter: Tránh lặp cell formatting
- UiFactory: Tạo components tái sử dụng

### 7. 🎨 UI/UX Best Practices

#### ✅ **Consistent UI Components**
```java
// UiFactory - Centralized component creation
JButton btnSave = UiFactory.button("Lưu", UiTheme.PRIMARY, event -> save());
JTextField txtSearch = UiFactory.textField(20, 240);
```

#### ✅ **User Feedback**
- ✅ Loading indicators cho thao tác lâu
- ✅ Confirm dialog trước khi xóa
- ✅ Success/Error messages rõ ràng
- ✅ Preview trước khi import/export

#### ✅ **Keyboard Shortcuts**
- Enter: Submit form
- Escape: Close dialog
- Ctrl+F: Focus search box

---

## 🚀 Cài Đặt

### Yêu Cầu Hệ Thống

- ☕ **Java Development Kit (JDK):** 17 hoặc cao hơn
- 🗄️ **MySQL Server:** 8.0 hoặc cao hơn
- 🔨 **Maven:** 3.6+ (hoặc dùng Maven wrapper đi kèm)
- 💻 **IDE khuyến nghị:** IntelliJ IDEA, Eclipse, NetBeans

### Các Bước Cài Đặt

#### 1. Clone Repository

```bash
git clone https://github.com/your-org/hospital-management-system.git
cd hospital-management-system
```

#### 2. Cấu Hình Database

**Tạo database:**
```sql
CREATE DATABASE HMS CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Import schema:**
```bash
mysql -u root -p HMS < db.sql
```

#### 3. Cấu Hình Kết Nối

Sửa file `src/main/java/org/example/config/DatabaseConfig.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/HMS?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_password"; // Đổi password của bạn
```

**Hoặc** tạo file `src/main/resources/application.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/HMS
db.username=root
db.password=your_password
```

#### 4. Build Project

```bash
# Sử dụng Maven wrapper (khuyến nghị)
./mvnw clean install

# Hoặc Maven global
mvn clean install
```

#### 5. Chạy Ứng Dụng

**Cách 1: Từ IDE**
- Mở project trong IntelliJ IDEA / Eclipse / NetBeans
- Run `src/main/java/org/example/Main.java`

**Cách 2: Từ Command Line**
```bash
./mvnw exec:java -Dexec.mainClass="org.example.Main"
```

**Cách 3: Chạy JAR file**
```bash
java -jar target/BAITAPUTT-1.0-SNAPSHOT.jar
```

### 6. Đăng Nhập Hệ Thống

**Tài khoản mặc định:**
- **Username:** `admin`
- **Password:** `admin123`

> ⚠️ **Lưu ý:** Đổi password mặc định ngay sau lần đầu đăng nhập!

---

## 📖 Hướng Dẫn Sử Dụng

### Quản Lý Sản Phẩm

1. **Thêm sản phẩm mới:**
   - Click nút "Thêm"
   - Nhập thông tin: Mã, Tên, Danh mục, Giá...
   - Click "Lưu"

2. **Import hàng loạt:**
   - Click "Download Template" → Tải file Excel mẫu
   - Điền dữ liệu vào file Excel
   - Click "Import Excel" → Chọn file
   - Xem preview → Confirm import

3. **Export danh sách:**
   - Click "Export Excel"
   - Chọn nơi lưu file
   - File Excel tự động mở

### Quản Lý Kho

1. **Nhập hàng:**
   - Menu "Kho" → "Nhập hàng"
   - Chọn kho, nhà cung cấp
   - Thêm sản phẩm và số lượng
   - Xác nhận nhập

2. **Kiểm tra tồn kho:**
   - Menu "Kho" → "Tồn kho"
   - Filter theo kho/sản phẩm
   - Export báo cáo Excel

---

## 📁 Cấu Trúc Dự Án

```
BAITAPUTT/
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── config/                 # Cấu hình (Database, App)
│   │   │   ├── controller/             # Controllers (điều phối)
│   │   │   ├── service/                # Business logic
│   │   │   │   └── impl/               # Service implementations
│   │   │   ├── repository/             # Data access layer
│   │   │   │   └── impl/               # Repository implementations
│   │   │   ├── entity/                 # Domain models
│   │   │   │   └── enums/              # Enumerations
│   │   │   ├── dto/                    # Data Transfer Objects
│   │   │   │   ├── request/            # Request DTOs
│   │   │   │   └── response/           # Response DTOs
│   │   │   ├── view/                   # UI Components (Swing)
│   │   │   │   ├── base/               # Base components
│   │   │   │   ├── dialog/             # Dialogs
│   │   │   │   └── component/          # Custom components
│   │   │   ├── router/                 # Navigation
│   │   │   ├── utils/                  # Utilities
│   │   │   │   ├── excel/              # Excel import/export
│   │   │   │   ├── pdf/                # PDF generation
│   │   │   │   └── importer/           # Import framework
│   │   │   │       ├── core/           # Core import logic
│   │   │   │       └── template/       # Template generators
│   │   │   ├── exception/              # Custom exceptions
│   │   │   └── Main.java               # Entry point
│   │   └── resources/
│   │       ├── log4j2.xml              # Logging config
│   │       └── application.properties  # App properties
│   └── test/java/                      # Unit tests
├── db.sql                              # Database schema
├── pom.xml                             # Maven config
├── README.md                           # Documentation
└── LICENSE                             # License file
```

---

## 🗄️ Database Schema

### Core Tables

```sql
-- Sản phẩm
products (id, code, name, category_id, manufacturer_id, standard_price, status, ...)

-- Danh mục
categories (id, code, name, parent_id, display_order, is_active, ...)

-- Nhà sản xuất
manufacturers (id, code, name, country, ...)

-- Kho
warehouses (id, code, name, location, ...)

-- Lô hàng
batches (id, product_id, warehouse_id, quantity, expiry_date, ...)

-- Khách hàng
customer_profiles (id, code, first_name, last_name, phone, email, ...)

-- Nhân viên
employee_profiles (id, code, first_name, last_name, position, ...)

-- Tài khoản
accounts (id, username, password_hash, role, ...)
```

### Relationships

```
categories (1) ─────< (N) products
manufacturers (1) ───< (N) products  
products (1) ────────< (N) batches
warehouses (1) ──────< (N) batches
```

---

## 📸 Screenshots

### 1. Màn Hình Đăng Nhập
![Login](docs/screenshots/login.png)

### 2. Dashboard
![Dashboard](docs/screenshots/dashboard.png)

### 3. Quản Lý Sản Phẩm
![Product Management](docs/screenshots/products.png)

### 4. Import Excel Preview
![Import Preview](docs/screenshots/import-preview.png)

### 5. Export Excel
![Export Excel](docs/screenshots/export-excel.png)

---

## 🤝 Đóng Góp

Chúng tôi hoan nghênh mọi đóng góp từ cộng đồng!

### Cách Đóng Góp

1. Fork repository
2. Tạo feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add some AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Mở Pull Request

### Coding Standards

- Tuân thủ Java Code Conventions
- Viết Javadoc cho public methods
- Unit test coverage ≥ 70%
- Không commit log files, credentials

---

## 👥 Nhóm Phát Triển

Dự án được phát triển bởi nhóm sinh viên Đại học Công Nghệ Giao Thông Vận Tải (UTT):

| STT | Họ và Tên | Vai Trò | Nhiệm Vụ Chính |
|-----|-----------|---------|----------------|
| 1 | **Hoàng Đình Dũng** | **👑 Team Leader** | Architecture Design, Database Design, BaseRepository, Import/Export Framework |
| 2 | **Đỗ Hoàn** | Backend Developer | Service Layer, Repository Implementation, Transaction Management |
| 3 | **Bùi Danh Toản** | Backend Developer | Controller Layer, Business Logic, Validation |
| 4 | **Nguyễn Quốc Toản** | Frontend Developer | UI/UX Design, Swing Components, Dialog Management |
| 5 | **Đỗ Bá Huy** | Full-stack Developer | Excel/PDF Export, Testing, Documentation |

### Liên Hệ

- 📧 Email: hoangdinhdung@gmail.com
- 🌐 GitHub: [Hospital Management System](https://github.com/your-org/hms)
- 📚 Documentation: [Wiki](https://github.com/your-org/hms/wiki)

---

## 📄 License

Dự án được phát hành dưới giấy phép [MIT License](LICENSE).

```
MIT License

Copyright (c) 2024 Hospital Management System Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

---

## 🙏 Lời Cảm Ơn

- Cảm ơn **Thầy/Cô giáo hướng dẫn** đã tận tình chỉ bảo
- Cảm ơn **Đại học Công Nghệ Giao Thông Vận Tải** đã tạo điều kiện học tập
- Cảm ơn cộng đồng **Apache POI**, **MySQL**, **Java Swing** vì các công cụ tuyệt vời

---

## 🔮 Roadmap

### Version 2.0 (Kế Hoạch)

- [ ] Migration sang Spring Boot
- [ ] RESTful API cho Mobile App
- [ ] Real-time notification với WebSocket
- [ ] Barcode scanning integration
- [ ] Multi-language support (EN, VI)
- [ ] Cloud deployment (AWS/Azure)
- [ ] Advanced analytics với AI/ML
- [ ] Unit Testing coverage ≥ 80%

---

## 📞 Hỗ Trợ

Nếu bạn gặp vấn đề hoặc có câu hỏi:

1. 🐛 **Bug Report:** [Issues](https://github.com/your-org/hms/issues)
2. 💡 **Feature Request:** [Discussions](https://github.com/your-org/hms/discussions)
3. 📖 **Documentation:** [Wiki](https://github.com/your-org/hms/wiki)
4. 📧 **Email:** support@hms-utt.edu.vn

---

<div align="center">

**⭐ Nếu dự án hữu ích, hãy cho chúng tôi một star! ⭐**

Made with ❤️ by UTT Students

</div>
