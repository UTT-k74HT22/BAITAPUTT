# 📦 TÍNH NĂNG IMPORT PRODUCT - HOÀN CHỈNH

## ✅ Đã triển khai

### 📁 Core Framework (Có thể tái sử dụng)

```
src/main/java/org/example/
├── utils/importer/core/
│   ├── ImportError.java              ✅ Class lỗi
│   ├── ImportMapper.java             ✅ Interface map Excel → DTO
│   ├── ImportValidator.java          ✅ Interface validate
│   └── AbstractImportService.java    ✅ Service trừu tượng (logic chung)
│
├── dto/response/
│   ├── ImportRowData.java            ✅ Thông tin 1 dòng import
│   └── ImportPreviewResponse.java    ✅ Kết quả preview
```

### 📁 Product Import Implementation

```
src/main/java/org/example/
├── dto/request/
│   └── ImportProductRequest.java     ✅ DTO cho Product
│
├── service/impl/
│   └── ProductImportService.java     ✅ Service xử lý Product import
│
├── utils/importer/template/
│   └── ProductTemplateGenerator.java ✅ Tạo Excel template
│
├── view/dialog/
│   └── ImportPreviewDialog.java      ✅ Dialog preview (Swing UI)
│
└── view/
    └── ProductManagementPanel.java   ✅ Đã tích hợp 2 nút:
                                         - 📥 Download Template
                                         - 📤 Import Excel
```

### 📁 Demo & Utils

```
src/main/java/org/example/utils/importer/demo/
├── CreateExcelExample.java           ✅ Tạo file Excel example
└── ConvertCsvToXlsx.java            ✅ Convert CSV → XLSX
```

### 📁 Documentation & Examples

```
project_root/
├── IMPORT_GUIDE.md                   ✅ Hướng dẫn tổng quan
├── EXCEL_IMPORT_GUIDE.md            ✅ Hướng dẫn chi tiết tạo Excel
├── Product_Import_Example.csv       ✅ File CSV mẫu (10 sản phẩm)
└── README_IMPORT.md                 📄 File này
```

---

## 🚀 Cách sử dụng

### 1. Chuẩn bị dữ liệu

**Trước tiên phải có:**
- ✅ Category (ví dụ: CAT001, CAT002, CAT003...)
- ✅ Manufacturer (ví dụ: MFG001, MFG002, MFG003...)

### 2. Tạo file Excel

**Option A: Download Template từ UI**
```
1. Chạy ProductManagementPanel
2. Click "📥 Download Template"
3. Lưu file → Có sẵn header + 2 dòng ví dụ
4. Điền dữ liệu vào file
```

**Option B: Sử dụng file CSV mẫu**
```
1. Mở file: Product_Import_Example.csv
2. Save as → Chọn định dạng Excel (.xlsx)
3. Hoặc edit trực tiếp CSV (Excel có thể mở)
```

**Option C: Tạo từ đầu**
```
Xem file: EXCEL_IMPORT_GUIDE.md
```

### 3. Import vào hệ thống

```
1. Chạy ProductManagementPanel
2. Click "📤 Import Excel"
3. Chọn file Excel
4. Xem Preview:
   - ✅ Dòng hợp lệ (màu xanh)
   - ❌ Dòng lỗi (màu đỏ + message)
5. Click "Import Valid Rows"
6. Hoàn tất!
```

---

## 📝 Cấu trúc file Excel

### Header Row (Bắt buộc - Dòng 1)

| A | B | C | D | E | F | G | H | I | J |
|---|---|---|---|---|---|---|---|---|---|
| Code | Name | Category Code | Manufacturer Code | Barcode | Dosage Form | Unit | Description | Standard Price | Requires Prescription |

### Data Rows (Từ dòng 2)

Xem ví dụ trong file `Product_Import_Example.csv`

---

## 🔍 Validation Rules

| Field | Rule | Example Error |
|-------|------|---------------|
| Code | Required, Unique | "Code is required", "Code already exists: PRD001" |
| Name | Required | "Name is required" |
| Category Code | Required, Must exist | "Category Code is required", "Category not found: CAT999" |
| Manufacturer Code | Optional, Must exist if provided | "Manufacturer not found: MFG999" |
| Standard Price | Optional, Must be positive | "Price must be positive" |

---

## 🛠️ Mở rộng cho màn khác

Để implement import cho **Category**, **Manufacturer**, **Customer**... chỉ cần 3 bước:

### Bước 1: Tạo DTO Request
```java
public class ImportCategoryRequest {
    private String code;
    private String name;
    // ...getters/setters
}
```

### Bước 2: Tạo Import Service
```java
public class CategoryImportService extends AbstractImportService<ImportCategoryRequest> {
    @Override
    protected ImportMapper<ImportCategoryRequest> getMapper() { ... }
    
    @Override
    protected ImportValidator<ImportCategoryRequest> getValidator() { ... }
    
    @Override
    protected void saveData(List<ImportCategoryRequest> validData) { ... }
}
```

### Bước 3: Tích hợp vào UI
```java
private void importFromExcel() {
    // Chọn file
    // Preview với ImportPreviewDialog
    // Apply import
}
```

Xem chi tiết trong `IMPORT_GUIDE.md`

---

## 🎯 Điểm mạnh của kiến trúc

✅ **Dễ implement** - Chỉ cần extends AbstractImportService  
✅ **Dễ hiểu** - Logic tách biệt: map → validate → save  
✅ **Dễ bảo trì** - Sửa chung ở abstract class  
✅ **Dễ mở rộng** - Copy pattern cho entity khác  
✅ **An toàn** - Validate → Preview → Confirm → Save  
✅ **User-friendly** - Preview rõ ràng, báo lỗi chi tiết  

---

## 📊 Flow xử lý

```
[User chọn file Excel]
        ↓
[AbstractImportService.previewFromFile()]
        ↓
    ┌───────────────────────────┐
    │ Đọc file Excel            │
    │ Validate headers          │
    │ ┌─────────────────────┐   │
    │ │ For each data row:  │   │
    │ │  → Map (Mapper)     │   │
    │ │  → Validate (Valid) │   │
    │ │  → Add to list      │   │
    │ └─────────────────────┘   │
    └───────────────────────────┘
        ↓
[ImportPreviewResponse]
    - validRows []
    - invalidRows []
        ↓
[ImportPreviewDialog hiển thị]
    - Table với dòng xanh/đỏ
    - Error messages
        ↓
[User click "Import Valid Rows"]
        ↓
[AbstractImportService.applyImport()]
        ↓
    ┌───────────────────────────┐
    │ saveData()                │
    │  → Convert DTO to Entity  │
    │  → Save to DB             │
    └───────────────────────────┘
        ↓
[Success!]
```

---

## 🧪 Test

### Manual Test

1. **Test với file đúng format:**
   - Sử dụng `Product_Import_Example.csv`
   - Đảm bảo có Category và Manufacturer tương ứng
   - Kết quả: Tất cả dòng hợp lệ

2. **Test với file lỗi:**
   - Sửa Category Code thành code không tồn tại
   - Kết quả: Dòng đó báo lỗi "Category not found"

3. **Test với code trùng:**
   - Thêm sản phẩm có code đã tồn tại
   - Kết quả: Dòng đó báo lỗi "Code already exists"

### Unit Test (TODO)

```java
@Test
public void testProductImportMapper() {
    // Test map Excel row → DTO
}

@Test
public void testProductImportValidator() {
    // Test validate logic
}
```

---

## 🐛 Troubleshooting

### Lỗi: "Cannot resolve symbol 'ImportProductRequest'"

**Nguyên nhân:** IDE cache chưa refresh  
**Giải pháp:**
1. File → Invalidate Caches / Restart
2. Hoặc: mvn clean compile

### Lỗi: "invalid target release: 17"

**Nguyên nhân:** Java version hiện tại là 11, pom.xml set là 17  
**Giải pháp:**
1. Update Java version lên 17
2. Hoặc sửa pom.xml: `<maven.compiler.target>11</maven.compiler.target>`

### Lỗi: "Category not found"

**Nguyên nhân:** Category Code trong Excel không tồn tại trong DB  
**Giải pháp:**
1. Vào Category Management
2. Tạo Category với code tương ứng
3. Hoặc sửa Excel để dùng code đã có

---

## 📞 Hỗ trợ

Có vấn đề? Kiểm tra:
1. ✅ `EXCEL_IMPORT_GUIDE.md` - Hướng dẫn chi tiết format Excel
2. ✅ `IMPORT_GUIDE.md` - Hướng dẫn tổng quan
3. ✅ Preview Dialog - Xem lỗi cụ thể của từng dòng

---

## 📅 Version

**v1.0** - December 26, 2025
- ✅ Core Framework
- ✅ Product Import
- ✅ Preview Dialog
- ✅ Documentation
- ✅ Example files

**TODO:**
- [ ] Category Import
- [ ] Manufacturer Import
- [ ] Customer Import
- [ ] Unit Tests
- [ ] Batch import (import nhiều entity cùng lúc)

---

**Chúc bạn sử dụng tính năng Import thành công! 🎉**

