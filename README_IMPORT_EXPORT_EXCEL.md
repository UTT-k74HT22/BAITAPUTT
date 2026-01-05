# 📘 MODULE IMPORT/EXPORT EXCEL

## 🎯 CHỨC NĂNG CHÍNH
- **Import:** Nhập dữ liệu từ file Excel vào database
- **Export:** Xuất dữ liệu từ database ra file Excel

## 📥 IMPORT FLOW

### **Luồng xử lý**
```
User chọn file Excel (.xlsx)
    ↓
JFileChooser → Chọn file
    ↓
AbstractImportService.importFromExcel()
    ↓
1. Đọc file Excel (Apache POI)
2. Validate từng row
3. Parse data → Entity
4. Bulk insert vào DB
    ↓
Hiển thị thông báo: "Import 100 bản ghi thành công"
```

### **AbstractImportService.java**

```java
public abstract class AbstractImportService<T> {
    
    public ImportResult importFromExcel(File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            List<T> entities = new ArrayList<>();
            List<String> errors = new ArrayList<>();
            
            // Bỏ qua header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                
                try {
                    // ⭐ Template method pattern
                    T entity = parseRow(row, i);
                    
                    // Validate
                    List<String> rowErrors = validateEntity(entity, i);
                    if (!rowErrors.isEmpty()) {
                        errors.addAll(rowErrors);
                        continue;
                    }
                    
                    entities.add(entity);
                } catch (Exception e) {
                    errors.add("Row " + (i+1) + ": " + e.getMessage());
                }
            }
            
            // Bulk insert
            if (!entities.isEmpty()) {
                saveEntities(entities);
            }
            
            return new ImportResult(entities.size(), errors);
        }
    }
    
    // ⭐ Subclass phải implement
    protected abstract T parseRow(Row row, int rowNum);
    protected abstract List<String> validateEntity(T entity, int rowNum);
    protected abstract void saveEntities(List<T> entities);
}
```

### **ProductImportService extends AbstractImportService<Product>**

```java
@Override
protected Product parseRow(Row row, int rowNum) {
    Product p = new Product();
    p.setCode(getCellValue(row, 0));        // Cột A: Mã sản phẩm
    p.setName(getCellValue(row, 1));        // Cột B: Tên
    p.setPrice(getNumericValue(row, 2));    // Cột C: Giá
    p.setCategoryId(getNumericValue(row, 3)); // Cột D: Category ID
    return p;
}

@Override
protected List<String> validateEntity(Product p, int rowNum) {
    List<String> errors = new ArrayList<>();
    
    if (p.getCode() == null || p.getCode().isEmpty()) {
        errors.add("Row " + (rowNum+1) + ": Mã sản phẩm bắt buộc");
    }
    
    if (p.getPrice() < 0) {
        errors.add("Row " + (rowNum+1) + ": Giá không được âm");
    }
    
    return errors;
}

@Override
protected void saveEntities(List<Product> products) {
    productRepository.bulkInsert(products);
}
```

### **Apache POI - Đọc cell**

```java
private String getCellValue(Row row, int colIdx) {
    Cell cell = row.getCell(colIdx);
    if (cell == null) return "";
    
    return switch (cell.getCellType()) {
        case STRING -> cell.getStringCellValue().trim();
        case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
        case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
        case FORMULA -> cell.getCellFormula();
        default -> "";
    };
}

private double getNumericValue(Row row, int colIdx) {
    Cell cell = row.getCell(colIdx);
    if (cell == null) return 0.0;
    
    return switch (cell.getCellType()) {
        case NUMERIC -> cell.getNumericCellValue();
        case STRING -> {
            try {
                yield Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                yield 0.0;
            }
        }
        default -> 0.0;
    };
}
```

## 📤 EXPORT FLOW

### **Luồng xử lý**
```
User click "Xuất Excel"
    ↓
Controller.handleExportExcel()
    ↓
ExcelWriter.export(data, outputFile)
    ↓
1. Tạo Workbook + Sheet
2. Tạo header row (bold, background màu)
3. Ghi từng row dữ liệu
4. Auto-size columns
5. Lưu file
    ↓
Desktop.open(file) → Mở Excel
```

### **AbstractExcelWriter.java**

```java
public abstract class AbstractExcelWriter<T> {
    
    public void export(List<T> data, File outputFile) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");
            
            // 1. Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // 2. Write header
            Row headerRow = sheet.createRow(0);
            String[] headers = getHeaders();
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 3. Write data rows
            int rowNum = 1;
            for (T item : data) {
                Row row = sheet.createRow(rowNum++);
                writeRow(row, item, dataStyle);
            }
            
            // 4. Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 5. Save file
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
        }
    }
    
    // ⭐ Subclass implement
    protected abstract String[] getHeaders();
    protected abstract void writeRow(Row row, T item, CellStyle style);
}
```

### **ProductExcelWriter extends AbstractExcelWriter<Product>**

```java
@Override
protected String[] getHeaders() {
    return new String[]{"Mã SP", "Tên SP", "Giá", "Danh mục"};
}

@Override
protected void writeRow(Row row, Product p, CellStyle style) {
    createCell(row, 0, p.getCode(), style);
    createCell(row, 1, p.getName(), style);
    createCell(row, 2, p.getPrice(), style);
    createCell(row, 3, p.getCategoryName(), style);
}

private void createCell(Row row, int col, Object value, CellStyle style) {
    Cell cell = row.createCell(col);
    
    if (value instanceof String) {
        cell.setCellValue((String) value);
    } else if (value instanceof Number) {
        cell.setCellValue(((Number) value).doubleValue());
    }
    
    cell.setCellStyle(style);
}
```

### **ExcelStyles.java - Định dạng**

```java
public static CellStyle createHeaderStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    
    // Background màu xanh nhạt
    style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    
    // Border
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    
    // Font bold
    Font font = wb.createFont();
    font.setBold(true);
    font.setFontHeightInPoints((short) 12);
    style.setFont(font);
    
    // Căn giữa
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    
    return style;
}

public static CellStyle createDataStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    
    // Border
    style.setBorderBottom(BorderStyle.THIN);
    style.setBorderTop(BorderStyle.THIN);
    style.setBorderLeft(BorderStyle.THIN);
    style.setBorderRight(BorderStyle.THIN);
    
    // Font
    Font font = wb.createFont();
    font.setFontHeightInPoints((short) 11);
    style.setFont(font);
    
    return style;
}
```

## 📊 VÍ DỤ FILE EXCEL

### **Import Template - products_template.xlsx**
```
| Mã SP    | Tên SP       | Giá     | Danh mục ID |
|----------|--------------|---------|-------------|
| PRD001   | iPhone 15    | 25000000| 1           |
| PRD002   | Samsung S24  | 22000000| 1           |
| PRD003   | Paracetamol  | 5000    | 2           |
```

### **Export Output - employees_20260105.xlsx**
```
| Mã NV  | Tên NV        | Chức vụ | Lương      | Trạng thái |
|--------|---------------|---------|------------|------------|
| EMP001 | Nguyễn Văn A  | Manager | 15000000   | Hoạt động  |
| EMP002 | Trần Thị B    | Staff   | 8000000    | Hoạt động  |
```

## ❓ CÂU HỎI VẤN ĐÁP

### **Q1: Apache POI là gì?**
**A:** Thư viện Java để đọc/ghi file Microsoft Office (Excel, Word, PowerPoint)
- **XSSFWorkbook:** File .xlsx (Excel 2007+)
- **HSSFWorkbook:** File .xls (Excel cũ)

### **Q2: Import flow làm gì?**
**A:**
1. Đọc file Excel
2. Validate từng row (check required, format, logic)
3. Parse row → Entity (Product, Employee, etc.)
4. Bulk insert vào DB

### **Q3: Tại sao dùng AbstractImportService?**
**A:** **Template Method Pattern**
- Base class định nghĩa flow chung (đọc file, validate, save)
- Subclass implement logic riêng (parseRow, validateEntity)
- **Lợi ích:** Code reuse, dễ extend

### **Q4: Export Excel cần làm gì?**
**A:**
1. Create Workbook + Sheet
2. Create header row (bold, màu background)
3. Write data rows
4. Auto-size columns
5. Save file

### **Q5: CellStyle làm gì?**
**A:** Định dạng cell (font, màu, border, alignment)
```java
CellStyle headerStyle:
- Bold font
- Blue background
- Border
- Center align

CellStyle dataStyle:
- Normal font
- Border
```

### **Q6: Demo import sản phẩm?**
**A:**
```
1. Chuẩn bị file products.xlsx:
   | Mã SP  | Tên SP      | Giá     | Danh mục |
   | PRD001 | iPhone 15   | 25000000| 1        |

2. Click "Import Excel" → Chọn file

3. ProductImportService:
   - Đọc row 2 (row 1 là header)
   - Parse: code=PRD001, name=iPhone 15, price=25000000, categoryId=1
   - Validate: OK
   - Insert vào products table

4. Hiển thị: "Import thành công 1 bản ghi"
```

### **Q7: Nếu import bị lỗi?**
**A:** Có 2 loại lỗi:

**Lỗi validation (row cụ thể):**
```
Row 3: Mã sản phẩm bắt buộc
Row 5: Giá không được âm
→ Skip row lỗi, import row OK
```

**Lỗi file:**
```
File không đúng định dạng .xlsx
→ Dừng, báo lỗi
```

## 🔧 CODE PATTERNS

### **1. Template Method Pattern**
```java
abstract class AbstractImportService {
    final importFromExcel() {        // Fixed flow
        parseRow()     // ← Subclass implement
        validate()     // ← Subclass implement
        save()         // ← Subclass implement
    }
}
```

### **2. Strategy Pattern (Export)**
```java
interface ExcelWriter {
    void export(List<T> data, File file);
}

ProductExcelWriter implements ExcelWriter
EmployeeExcelWriter implements ExcelWriter
```

### **3. Factory Pattern (CellStyle)**
```java
ExcelStyles.createHeaderStyle(workbook)
ExcelStyles.createDataStyle(workbook)
ExcelStyles.createCurrencyStyle(workbook)
```

## 📝 CHECKLIST ÔN TẬP

- [ ] Apache POI là gì, dùng để làm gì
- [ ] Import flow: đọc → validate → parse → save
- [ ] Export flow: create workbook → header → data → save
- [ ] Template Method Pattern trong AbstractImportService
- [ ] CellStyle định dạng header vs data
- [ ] Demo import/export sản phẩm

**🎯 Nắm vững Import/Export Excel!**
