# ⚡ QUICK TEST - IMPORT STOCK MOVEMENT

## 🚀 TEST NGAY

### **Test 1: Download Template** ⏱️ 30s
```
1. Chạy ứng dụng
2. Đăng nhập
3. Vào "Quản lý Xuất Nhập Kho"
4. Click "📄 Tải Template"
5. Lưu file
6. Mở Excel → Thấy 2 sheets (Dữ liệu + Hướng dẫn)
✅ PASS nếu file mở được và có đủ nội dung
```

---

### **Test 2: Import File Hợp Lệ** ⏱️ 1 phút
```
1. Mở template vừa tải
2. Xóa 3 dòng ví dụ
3. Điền dữ liệu test:

| Loại     | Kho | SP | Lô | SL  | Ghi chú        |
|----------|-----|----|----| ----|----------------|
| IMPORT   | 1   | 1  | 1  | 100 | Test import 1  |
| IMPORT   | 1   | 2  | 2  | 50  | Test import 2  |

4. Lưu file
5. Click "📥 Import Excel"
6. Chọn file vừa lưu
7. Đợi loading...
8. Preview hiện:
   - ✅ Hợp lệ: 2 dòng
   - ❌ Lỗi: 0
9. Click "Import 2 dòng"
10. Đợi loading...
11. Thấy thông báo "✅ Import thành công 2 bản ghi!"
12. Bảng refresh → Thấy 2 dòng mới

✅ PASS nếu import thành công và dữ liệu hiển thị đúng
```

---

### **Test 3: Import Có Lỗi** ⏱️ 1 phút
```
1. Tạo file Excel mới với dữ liệu:

| Loại     | Kho | SP  | Lô | SL  | Ghi chú           |
|----------|-----|-----|----|-----|-------------------|
| IMPORT   | 1   | 1   | 1  | 50  | OK                |
| export   | 1   | 2   | 2  | 10  | SAI: viết thường  |
| EXPORT   | 999 | 1   | 1  | 10  | SAI: kho ko tồn tại |

2. Import file
3. Preview hiện:
   - ✅ Hợp lệ: 1 dòng
   - ❌ Lỗi: 2 dòng
4. Click tab "❌ Lỗi"
5. Thấy cột "Lỗi" hiển thị:
   - Dòng 3: "Loại giao dịch không hợp lệ..."
   - Dòng 4: "Kho không tồn tại"

✅ PASS nếu validation hiển thị đúng lỗi
```

---

### **Test 4: Export Không Đủ Hàng** ⏱️ 1 phút
```
SETUP:
- Giả sử Product #1, Batch #1, Warehouse #1 có tồn kho = 30

TEST:
1. Tạo file:

| Loại   | Kho | SP | Lô | SL  | Ghi chú             |
|--------|-----|----|----| ----|---------------------|
| EXPORT | 1   | 1  | 1  | 50  | Xuất quá nhiều      |

2. Import file
3. Preview hiện:
   - ✅ Hợp lệ: 0
   - ❌ Lỗi: 1
4. Tab "Lỗi" hiển thị:
   - "Không đủ hàng để xuất. Tồn kho: 30, Yêu cầu: 50"

✅ PASS nếu validate đúng tồn kho
```

---

### **Test 5: Transaction Rollback** ⏱️ 2 phút
```
SETUP:
- Kiểm tra số lượng record hiện tại: SELECT COUNT(*) FROM stock_movements;
- Ví dụ: currentCount = 10

TEST:
1. Tạo file với 3 dòng:

| Loại   | Kho | SP | Lô | SL  | Ghi chú    |
|--------|-----|----|----| ----|------------|
| IMPORT | 1   | 1  | 1  | 100 | OK         |
| IMPORT | 1   | 2  | 2  | 50  | OK         |
| EXPORT | 999 | 1  | 1  | 10  | SAI: kho 999 |

2. Import → Preview: 2 valid, 1 invalid
3. Click "Import 2 dòng"
4. Loading...
5. Thấy lỗi "Kho không tồn tại"
6. Kiểm tra DB: SELECT COUNT(*) FROM stock_movements;
7. Vẫn = currentCount (10) → Không insert gì

✅ PASS nếu transaction rollback đúng
```

---

## 🎯 CHECKLIST TEST ĐẦY ĐỦ

- [ ] Test 1: Download Template
- [ ] Test 2: Import thành công
- [ ] Test 3: Import có lỗi validation
- [ ] Test 4: Export không đủ hàng
- [ ] Test 5: Transaction rollback
- [ ] Test 6: Preview dialog hiển thị đẹp
- [ ] Test 7: Loading dialog hoạt động
- [ ] Test 8: Refresh table sau import
- [ ] Test 9: Mở file Excel sau download
- [ ] Test 10: Import file lớn (100+ dòng)

---

## 🐛 NẾU GẶP LỖI

### **Lỗi: FileNotFoundException khi download template**
```
Nguyên nhân: Không có quyền ghi file
Giải pháp: Chọn folder khác hoặc chạy as Administrator
```

### **Lỗi: "Vui lòng đăng nhập để thực hiện import"**
```
Nguyên nhân: Chưa đăng nhập hoặc session hết hạn
Giải pháp: Đăng nhập lại
```

### **Lỗi: Loading dialog không đóng**
```
Nguyên nhân: Exception trong background thread
Giải pháp: Check console log, fix exception
```

### **Lỗi: Preview không hiển thị dữ liệu**
```
Nguyên nhân: File Excel không đúng format
Giải pháp: Dùng template đã tải, không tự tạo file mới
```

---

## 📊 KẾT QUẢ MONG ĐỢI

**Sau khi chạy 5 tests:**
- ✅ Download template: OK
- ✅ Import thành công: OK
- ✅ Validation lỗi: OK
- ✅ Check tồn kho: OK
- ✅ Transaction rollback: OK

**→ HOÀN THÀNH 100%! 🎉**

---

## 🎓 DEMO CHO GIÁO VIÊN (5 phút)

```
1. Mở ứng dụng → Đăng nhập
   ⏱️ 30s

2. Vào "Quản lý Xuất Nhập Kho"
   ⏱️ 10s

3. Demo Download Template
   - Click "Tải Template"
   - Mở file → Giải thích 2 sheets
   ⏱️ 1 phút

4. Demo Import Thành Công
   - Điền 2 dòng IMPORT
   - Import → Preview
   - Import → Success
   ⏱️ 2 phút

5. Demo Lỗi Validation
   - Điền dòng có lỗi (viết thường)
   - Import → Preview hiện lỗi
   - Giải thích validation rules
   ⏱️ 1.5 phút

TỔNG: ~5 phút
```

---

**✅ SẴN SÀNG DEMO!**
