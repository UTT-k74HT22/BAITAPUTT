# 🎯 HƯỚNG DẪN SỬ DỤNG IMPORT EXCEL CHO STOCK MOVEMENT

## ✅ HOÀN TẤT!

Chức năng Import Excel cho Stock Movement đã được hoàn thiện với:

### **1. Backend (Service Layer)**
- ✅ **StockImportService.java** - Service import dữ liệu
  - Validation đầy đủ (format + nghiệp vụ)
  - Check tồn kho khi EXPORT/TRANSFER
  - Transaction để đảm bảo data integrity
  - Lấy userId từ AuthContextManager

### **2. UI (Swing Panel)**
- ✅ **StockMovementManagementPanel.java** - Giao diện quản lý
  - Nút "📥 Import Excel"
  - Nút "📄 Tải Template"
  - Dialog Preview (Valid/Invalid tabs)
  - Loading dialog với progress bar
  - Thông báo kết quả import

### **3. Template Generator**
- ✅ **StockMovementTemplateGenerator.java** - Tạo template Excel
  - Sheet "Dữ liệu" với ví dụ
  - Sheet "Hướng dẫn" chi tiết
  - Định dạng đẹp (màu sắc, border, font)

---

## 📖 HƯỚNG DẪN SỬ DỤNG

### **Bước 1: Tải Template**
1. Mở màn hình **Quản lý Xuất Nhập Kho**
2. Click nút **"📄 Tải Template"**
3. Chọn vị trí lưu file → Click "Lưu"
4. Click "Mở file" để mở Excel

### **Bước 2: Điền Dữ Liệu**
Mở file Excel vừa tải, điền vào sheet "Dữ liệu":

| Loại giao dịch | Kho hàng (ID) | Mã sản phẩm (ID) | Mã lô (ID) | Số lượng | Ghi chú |
|----------------|---------------|------------------|------------|----------|---------|
| IMPORT | 1 | 1 | 1 | 100 | Nhập hàng từ NCC ABC |
| EXPORT | 1 | 2 | 2 | 50 | Xuất bán cho khách |
| ADJUST | 2 | 3 | 3 | 95 | Kiểm kê |

**Lưu ý:**
- **Loại giao dịch:** IMPORT / EXPORT / ADJUST / TRANSFER (viết hoa)
- **ID:** Phải là số nguyên (Long) đã tồn tại trong DB
- **Số lượng:** Phải > 0
- **EXPORT/TRANSFER:** Phải đủ hàng trong kho

### **Bước 3: Import File**
1. Click nút **"📥 Import Excel"**
2. Chọn file Excel đã điền → Click "Mở"
3. Đợi hệ thống đọc file...

### **Bước 4: Kiểm Tra Preview**
Dialog "Xem trước Import" hiển thị:

**Tổng quan:**
- ✅ Hợp lệ: X dòng
- ❌ Lỗi: Y dòng

**Tab "✅ Hợp lệ":**
- Hiển thị các dòng ĐÚNG, sẵn sàng import

**Tab "❌ Lỗi":**
- Hiển thị các dòng SAI, cột "Lỗi" chỉ rõ nguyên nhân:
  - "Không đủ hàng để xuất. Tồn kho: 30, Yêu cầu: 50"
  - "Sản phẩm không tồn tại"
  - "Kho không tồn tại"
  - ...

### **Bước 5: Thực Hiện Import**
1. Kiểm tra dữ liệu hợp lệ
2. Click nút **"Import X dòng"**
3. Đợi hệ thống import...
4. Nhận thông báo:
   - ✅ Thành công: "Import thành công X bản ghi!"
   - ❌ Lỗi: "Lỗi khi import: [chi tiết lỗi]"
5. Bảng dữ liệu tự động refresh

---

## 🔍 VALIDATION RULES

### **1. Loại Giao Dịch**
- Bắt buộc
- Chỉ chấp nhận: IMPORT / EXPORT / ADJUST / TRANSFER
- Phân biệt hoa/thường

### **2. Kho Hàng (ID)**
- Bắt buộc
- Phải là số nguyên
- Phải tồn tại trong bảng `warehouses`
- Kho phải đang ACTIVE

### **3. Mã Sản Phẩm (ID)**
- Bắt buộc
- Phải là số nguyên
- Phải tồn tại trong bảng `products`

### **4. Mã Lô (ID)**
- Bắt buộc
- Phải là số nguyên
- Phải tồn tại trong bảng `batches`
- Lô phải thuộc sản phẩm tương ứng

### **5. Số Lượng**
- Bắt buộc
- Phải > 0
- **EXPORT/TRANSFER:** Phải <= tồn kho hiện tại

### **6. Ghi Chú**
- Tùy chọn
- Giới hạn 500 ký tự

---

## ⚠️ CÁC TRƯỜNG HỢP LỖI THƯỜNG GẶP

### **Lỗi 1: "Loại giao dịch không hợp lệ"**
**Nguyên nhân:** Nhập sai tên loại giao dịch
```
❌ SAI: import, Import, nhập
✅ ĐÚNG: IMPORT
```

### **Lỗi 2: "Không đủ hàng để xuất"**
**Nguyên nhân:** EXPORT số lượng > tồn kho
```
Tồn kho: 30
Yêu cầu xuất: 50
→ ❌ KHÔNG ĐỦ HÀNG
```

**Giải pháp:**
- Giảm số lượng xuất
- Hoặc nhập thêm hàng trước

### **Lỗi 3: "Sản phẩm không tồn tại"**
**Nguyên nhân:** Product ID không có trong database
```
❌ SAI: Product ID = 999 (không tồn tại)
✅ ĐÚNG: Product ID = 1 (có trong DB)
```

### **Lỗi 4: "Kho không tồn tại"**
**Nguyên nhân:** Warehouse ID không có trong database

### **Lỗi 5: "Lô không tồn tại"**
**Nguyên nhân:** Batch ID không có trong database

### **Lỗi 6: "Số lượng phải > 0"**
**Nguyên nhân:** Nhập số âm hoặc 0
```
❌ SAI: 0, -10
✅ ĐÚNG: 1, 50, 100
```

---

## 🔄 TRANSACTION & ROLLBACK

**Cơ chế hoạt động:**

```
BEGIN TRANSACTION
  ├─ Import dòng 1: IMPORT 100 sp → ✅ OK
  ├─ Import dòng 2: EXPORT 50 sp → ✅ OK
  └─ Import dòng 3: EXPORT 200 sp → ❌ KHÔNG ĐỦ HÀNG
ROLLBACK
→ TẤT CẢ DỮ LIỆU BỊ HỦY (như chưa import gì)
```

**Lợi ích:**
- ✅ Đảm bảo data integrity
- ✅ All or nothing (không import nửa vời)
- ✅ Dễ rollback khi có lỗi

---

## 📝 VÍ DỤ FILE EXCEL

### **File hợp lệ 100%:**

| Loại | Kho | SP | Lô | SL | Ghi chú |
|------|-----|----|----|----|----|
| IMPORT | 1 | 1 | 1 | 100 | Nhập từ NCC A |
| IMPORT | 1 | 2 | 2 | 50 | Nhập từ NCC B |
| EXPORT | 1 | 1 | 1 | 30 | Xuất cho KH01 |

**Kết quả:**
- ✅ 3 dòng hợp lệ
- ❌ 0 lỗi
- → Import thành công 3 bản ghi

---

### **File có lỗi:**

| Loại | Kho | SP | Lô | SL | Ghi chú |
|------|-----|----|----|----|----|
| IMPORT | 1 | 1 | 1 | 100 | OK |
| export | 1 | 2 | 2 | 50 | ❌ Viết thường |
| EXPORT | 999 | 1 | 1 | 30 | ❌ Kho không tồn tại |
| EXPORT | 1 | 1 | 1 | 200 | ❌ Không đủ hàng |

**Kết quả:**
- ✅ 1 dòng hợp lệ
- ❌ 3 lỗi
- → Chỉ import được 1 bản ghi (sau khi sửa lỗi)

---

## 🎯 DEMO CHO GIÁO VIÊN

### **Scenario 1: Import Thành Công**
1. Tải template
2. Điền 3 dòng IMPORT hợp lệ
3. Import → Preview hiện 3 valid
4. Click "Import 3 dòng"
5. Thông báo "✅ Import thành công 3 bản ghi!"
6. Bảng refresh, thấy 3 dòng mới

### **Scenario 2: Import Có Lỗi Validation**
1. Điền file với:
   - Dòng 1: OK
   - Dòng 2: Loại = "export" (viết thường) → ❌
   - Dòng 3: Product ID = 999 → ❌
2. Import → Preview:
   - Tab "Hợp lệ": 1 dòng
   - Tab "Lỗi": 2 dòng (hiện chi tiết lỗi)
3. Sửa lỗi → Import lại

### **Scenario 3: Import Không Đủ Hàng**
1. Kiểm tra tồn kho Product #1, Batch #1, Warehouse #1 = 50
2. Điền file: EXPORT 100 (> 50)
3. Import → Preview hiện lỗi:
   - "Không đủ hàng. Tồn: 50, Yêu cầu: 100"
4. Giảm xuống 30 → Import thành công

### **Scenario 4: Transaction Rollback**
1. Điền 3 dòng:
   - Dòng 1: IMPORT 100 → ✅
   - Dòng 2: EXPORT 30 → ✅
   - Dòng 3: EXPORT 200 → ❌ (không đủ hàng)
2. Import → Lỗi
3. Kiểm tra DB → Không có dòng nào được insert (rollback)

---

## 🐛 BUGS ĐÃ FIX

### **BUG #1: Validation check cả IMPORT**
```java
// ❌ TRƯỚC: Check cả IMPORT (sai logic)
if (type == EXPORT || type == TRANSFER || type == IMPORT) {
    checkStock(); // IMPORT không cần check!
}

// ✅ SAU: Chỉ check EXPORT/TRANSFER
if (type == EXPORT || type == TRANSFER) {
    checkStock();
}
```

### **BUG #2: Hardcode userId = 1L**
```java
// ❌ TRƯỚC
movementRequest.setPerformedByUserId(1L);

// ✅ SAU
Long currentUserId = AuthContextManager.getCurrentUserId();
if (currentUserId == null) {
    throw new RuntimeException("Vui lòng đăng nhập");
}
movementRequest.setPerformedByUserId(currentUserId);
```

---

## ✅ CHECKLIST HOÀN THÀNH

- [x] StockImportService validation nghiệp vụ
- [x] Transaction rollback
- [x] UI nút Import Excel + Download Template
- [x] Preview dialog (Valid/Invalid tabs)
- [x] Loading dialog với progress bar
- [x] Template generator với hướng dẫn
- [x] Fix bugs validation & userId
- [x] Test cases đầy đủ
- [x] README hướng dẫn

---

## 🎓 CÂU HỎI VẤN ĐÁP

**Q1: Import Excel cho Stock Movement khác gì Product?**

**A:**
- **Stock Movement:** Phức tạp hơn
  - Validate nghiệp vụ (đủ hàng để xuất)
  - Transaction CRITICAL
  - Update inventory_items
  - 4 loại giao dịch

- **Product:** Đơn giản
  - Chỉ validate format
  - Bulk insert
  - Không ảnh hưởng nghiệp vụ

**Q2: Tại sao cần transaction?**

**A:** Đảm bảo ALL OR NOTHING
- Import 100 dòng
- Dòng 50 lỗi
- → Rollback TẤT CẢ (không insert nửa vời)

**Q3: Validation ở đâu?**

**A:**
1. **Format:** Mapper (parse Excel → DTO)
2. **Business:** Validator (check tồn kho, FK, logic)
3. **Database:** Service (transaction, insert)

**Q4: Demo import thành công?**

**A:** (Thực hiện scenario 1 ở trên)

---

**🎉 HOÀN TẤT! Sẵn sàng demo cho giáo viên!**
