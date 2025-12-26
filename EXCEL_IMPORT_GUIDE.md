# Hướng dẫn tạo file Excel Import cho Product

## Cấu trúc file Excel

### Header Row (Dòng 1) - BẮT BUỘC

Dòng đầu tiên phải có chính xác 10 cột với tên như sau (phân biệt chữ hoa/thường):

| A | B | C | D | E | F | G | H | I | J |
|---|---|---|---|---|---|---|---|---|---|
| Code | Name | Category Code | Manufacturer Code | Barcode | Dosage Form | Unit | Description | Standard Price | Requires Prescription |

### Data Rows (Từ dòng 2 trở đi)

Mỗi dòng đại diện cho 1 sản phẩm cần import.

## Chi tiết các cột

| Cột | Tên | Bắt buộc | Kiểu dữ liệu | Ví dụ | Ghi chú |
|-----|-----|----------|--------------|--------|---------|
| A | Code | ✅ | Text | PRD001 | Mã sản phẩm, phải unique, không trùng với sản phẩm có sẵn |
| B | Name | ✅ | Text | Paracetamol 500mg | Tên sản phẩm |
| C | Category Code | ✅ | Text | CAT001 | Mã danh mục, **phải tồn tại trong hệ thống** |
| D | Manufacturer Code | ❌ | Text | MFG001 | Mã nhà sản xuất, nếu có thì **phải tồn tại trong hệ thống** |
| E | Barcode | ❌ | Text/Number | 8934567890123 | Mã vạch |
| F | Dosage Form | ❌ | Text | Tablet | Dạng bào chế: Tablet, Capsule, Syrup, Injection... |
| G | Unit | ❌ | Text | Box | Đơn vị: Box, Bottle, Tube, Pack... |
| H | Description | ❌ | Text | Pain relief medication | Mô tả sản phẩm |
| I | Standard Price | ❌ | Number | 15000 | Giá tiêu chuẩn, phải là số dương |
| J | Requires Prescription | ❌ | Text | Yes / No | Có cần toa thuốc không? Nhập: Yes, No, True, False, 1, 0 |

## Ví dụ dữ liệu

```
Row 1 (Header):
Code | Name | Category Code | Manufacturer Code | Barcode | Dosage Form | Unit | Description | Standard Price | Requires Prescription

Row 2:
PRD001 | Paracetamol 500mg | CAT001 | MFG001 | 8934567890123 | Tablet | Box | Pain relief medication | 15000 | No

Row 3:
PRD002 | Amoxicillin 250mg | CAT002 | MFG002 | 8934567890456 | Capsule | Box | Antibiotic medication | 45000 | Yes

Row 4:
PRD003 | Vitamin C 1000mg | CAT003 | MFG001 | 8934567890789 | Tablet | Bottle | Vitamin supplement | 120000 | No
```

## Lưu ý quan trọng

### 1. Category và Manufacturer phải tồn tại trước

**Trước khi import Product, bạn PHẢI có sẵn:**
- Category với code như CAT001, CAT002...
- Manufacturer với code như MFG001, MFG002... (nếu cần)

**Cách tạo Category và Manufacturer:**
- Vào màn hình Category Management / Manufacturer Management
- Thêm các danh mục và nhà sản xuất cần thiết
- Ghi nhớ CODE của chúng để điền vào file Excel

### 2. Code không được trùng

- Mỗi sản phẩm phải có code unique
- Hệ thống sẽ báo lỗi nếu code đã tồn tại

### 3. Format dữ liệu

- **Số**: Có thể nhập trực tiếp hoặc dạng text, hệ thống sẽ tự convert
- **Boolean** (Requires Prescription):
  - Giá trị `Yes`, `True`, `1` → True
  - Các giá trị khác → False

### 4. Dòng lỗi sẽ không được import

- Hệ thống sẽ validate toàn bộ file
- Preview sẽ hiển thị:
  - ✅ Dòng hợp lệ (màu xanh)
  - ❌ Dòng lỗi (màu đỏ + thông báo lỗi chi tiết)
- Chỉ các dòng hợp lệ mới được import

## Cách tạo file Excel

### Option 1: Sử dụng chức năng Download Template

1. Mở **Product Management Panel**
2. Click nút **"📥 Download Template"**
3. Lưu file về máy
4. File đã có header và 2 dòng ví dụ

### Option 2: Tạo thủ công

1. Mở Excel (Microsoft Excel, LibreOffice Calc, Google Sheets)
2. Tạo file mới
3. Dòng 1: Nhập chính xác 10 header như bảng trên
4. Từ dòng 2: Điền dữ liệu
5. Lưu file dạng **.xlsx** (Excel 2007+)

## Quy trình import

```
1. Chuẩn bị dữ liệu
   ├─ Tạo Category (nếu chưa có)
   ├─ Tạo Manufacturer (nếu chưa có)
   └─ Điền dữ liệu vào Excel

2. Import
   ├─ Click "📤 Import Excel"
   ├─ Chọn file
   └─ Đợi xử lý...

3. Preview & Validate
   ├─ Xem dòng hợp lệ (green)
   ├─ Xem dòng lỗi (red + error message)
   └─ Quyết định có import không

4. Confirm
   ├─ Click "Import Valid Rows"
   └─ Hoàn tất!
```

## Ví dụ lỗi thường gặp

| Lỗi | Nguyên nhân | Cách fix |
|-----|-------------|----------|
| Code is required | Cột Code để trống | Điền mã sản phẩm |
| Code already exists: PRD001 | Code đã tồn tại | Đổi code khác |
| Category not found: CAT999 | Category code không tồn tại | Tạo Category hoặc sửa code |
| Manufacturer not found: MFG999 | Manufacturer code không tồn tại | Tạo Manufacturer hoặc sửa code |
| Price must be positive | Giá âm hoặc không hợp lệ | Nhập số dương |
| Name is required | Cột Name để trống | Điền tên sản phẩm |

## Template mẫu (Copy vào Excel)

```
Code	Name	Category Code	Manufacturer Code	Barcode	Dosage Form	Unit	Description	Standard Price	Requires Prescription
PRD001	Paracetamol 500mg	CAT001	MFG001	8934567890123	Tablet	Box	Pain relief medication for headaches and fever	15000	No
PRD002	Amoxicillin 250mg	CAT002	MFG002	8934567890456	Capsule	Box	Antibiotic for bacterial infections	45000	Yes
PRD003	Vitamin C 1000mg	CAT003	MFG001	8934567890789	Tablet	Bottle	Dietary supplement to boost immune system	120000	No
PRD004	Ibuprofen 400mg	CAT001	MFG003	8934567891012	Tablet	Box	Anti-inflammatory and pain relief	25000	No
PRD005	Cough Syrup	CAT004	MFG002	8934567891345	Syrup	Bottle	Relief from cough and cold symptoms	35000	No
```

**Lưu ý:** Trong Excel thật, mỗi cột phải tách riêng (không dùng tab như ví dụ trên)

---

## 🎯 Checklist trước khi import

- [ ] File Excel có đúng 10 cột header
- [ ] Header row chính xác (Code, Name, Category Code...)
- [ ] Mỗi Product có Code unique
- [ ] Category Code đã tồn tại trong hệ thống
- [ ] Manufacturer Code đã tồn tại (nếu điền)
- [ ] Giá là số dương
- [ ] File lưu dạng .xlsx
- [ ] Đã backup dữ liệu (an toàn hơn)

---

**Có vấn đề?** Kiểm tra Preview Dialog - nó sẽ chỉ rõ lỗi ở dòng nào, cột nào!

