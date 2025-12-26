# Hướng dẫn sử dụng tính năng Import cho Product

## 📋 Tổng quan

Tính năng Import cho phép bạn nhập hàng loạt sản phẩm từ file Excel vào hệ thống. 

## 🏗️ Kiến trúc

### Core Framework (có thể tái sử dụng cho nhiều màn)

1. **ImportError** - Class đại diện cho lỗi
2. **ImportMapper** - Interface để map Excel → DTO
3. **ImportValidator** - Interface để validate dữ liệu
4. **ImportRowData** - Chứa thông tin 1 dòng
5. **ImportPreviewResponse** - Kết quả preview
6. **AbstractImportService** - Class trừu tượng xử lý logic chung

### Implement cho Product

1. **ImportProductRequest** - DTO cho Product import
2. **ProductImportService** - Service cụ thể cho Product
3. **ProductTemplateGenerator** - Tạo file Excel mẫu
4. **ImportPreviewDialog** - Dialog hiển thị preview

## 📖 Cách sử dụng

### Bước 1: Download Template

1. Mở màn **Product Management**
2. Click nút **"📥 Download Template"**
3. Chọn nơi lưu file → File Excel mẫu sẽ được tạo
4. File sẽ có:
   - Header: Code, Name, Category Code, Manufacturer Code, Barcode, Dosage Form, Unit, Description, Standard Price, Requires Prescription
   - 2 dòng ví dụ để tham khảo

### Bước 2: Điền dữ liệu

Mở file Excel và điền thông tin:

| Column | Required | Description | Example |
|--------|----------|-------------|---------|
| Code | ✅ | Mã sản phẩm (unique) | PRD001 |
| Name | ✅ | Tên sản phẩm | Paracetamol 500mg |
| Category Code | ✅ | Mã danh mục (phải tồn tại) | CAT001 |
| Manufacturer Code | ❌ | Mã nhà sản xuất | MFG001 |
| Barcode | ❌ | Mã vạch | 8934567890123 |
| Dosage Form | ❌ | Dạng bào chế | Tablet |
| Unit | ❌ | Đơn vị | Box |
| Description | ❌ | Mô tả | Pain relief medication |
| Standard Price | ❌ | Giá (số dương) | 15000 |
| Requires Prescription | ❌ | Cần toa thuốc? (Yes/No) | No |

**Lưu ý:**
- Category Code và Manufacturer Code phải tồn tại trong hệ thống trước khi import
- Code không được trùng với sản phẩm đã có

### Bước 3: Import

1. Click nút **"📤 Import Excel"**
2. Chọn file Excel đã điền
3. Hệ thống sẽ preview dữ liệu:
   - ✓ Dòng hợp lệ: màu xanh
   - ✗ Dòng lỗi: màu đỏ, kèm thông báo lỗi chi tiết
4. Kiểm tra kỹ dữ liệu
5. Click **"Import Valid Rows (X)"** để nhập chỉ các dòng hợp lệ
6. Hoàn tất!

## 🔧 Mở rộng cho màn khác

Để implement import cho màn khác (Category, Manufacturer, Customer...), làm theo 3 bước:

### 1. Tạo DTO Request

```java
public class ImportCategoryRequest {
    private String code;
    private String name;
    // ... getters/setters
}
```

### 2. Tạo Import Service

```java
public class CategoryImportService extends AbstractImportService<ImportCategoryRequest> {
    
    @Override
    protected ImportMapper<ImportCategoryRequest> getMapper() {
        return new ImportMapper<>() {
            @Override
            public ImportCategoryRequest mapRow(Row row, int rowIndex) {
                ImportCategoryRequest dto = new ImportCategoryRequest();
                dto.setCode(getCellValue(row, 0));
                dto.setName(getCellValue(row, 1));
                return dto;
            }
            
            @Override
            public String[] getRequiredHeaders() {
                return new String[]{"Code", "Name"};
            }
        };
    }
    
    @Override
    protected ImportValidator<ImportCategoryRequest> getValidator() {
        return (data, rowIndex) -> {
            List<ImportError> errors = new ArrayList<>();
            if (data.getCode() == null || data.getCode().isEmpty()) {
                errors.add(new ImportError(rowIndex, "Code", "Code is required"));
            }
            return errors;
        };
    }
    
    @Override
    protected void saveData(List<ImportCategoryRequest> validData) throws Exception {
        for (ImportCategoryRequest dto : validData) {
            // Save to DB
            categoryRepository.save(...);
        }
    }
}
```

### 3. Tích hợp vào Panel

```java
private void importFromExcel() {
    JFileChooser fileChooser = new JFileChooser();
    // ... chọn file
    
    CategoryImportService importService = new CategoryImportService(categoryRepository);
    ImportPreviewResponse<ImportCategoryRequest> preview = importService.previewFromFile(file);
    
    ImportPreviewDialog<ImportCategoryRequest> dialog = new ImportPreviewDialog<>(
        parentFrame, preview, columnNames, dto -> new Object[]{dto.getCode(), dto.getName()}
    );
    dialog.setVisible(true);
    
    if (dialog.isConfirmed()) {
        importService.applyImport(dialog.getValidData());
        reload();
    }
}
```

## ✨ Ưu điểm

1. **Dễ implement**: Chỉ cần extend AbstractImportService và implement 3 methods
2. **Dễ hiểu**: Logic tách biệt rõ ràng (map, validate, save)
3. **Dễ bảo trì**: Thay đổi logic chung ở abstract class
4. **Dễ mở rộng**: Thêm màn mới chỉ cần tạo service class mới
5. **An toàn**: Validate trước khi lưu, preview trước khi apply

## 🎯 Tính năng

- ✅ Validate dữ liệu realtime
- ✅ Preview trước khi import
- ✅ Hiển thị lỗi chi tiết từng dòng
- ✅ Chỉ import các dòng hợp lệ
- ✅ Download template có sẵn ví dụ
- ✅ Xử lý nhiều định dạng Excel (số, text, boolean)
- ✅ Support optional fields
- ✅ Kiểm tra foreign key (Category, Manufacturer)

