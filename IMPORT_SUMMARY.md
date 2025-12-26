# ✅ TÓM TẮT: ĐÃ TẠO XONG HƯỚNG DẪN IMPORT PRODUCT

## 📦 Các file đã tạo cho bạn:

### 1️⃣ **QUICK_IMPORT_GUIDE.md** ⭐ (ĐỌC FILE NÀY TRƯỚC!)
   - Hướng dẫn nhanh, gọn, dễ hiểu
   - Liệt kê đầy đủ Category Code và Manufacturer Code từ database của bạn
   - Ví dụ cụ thể từng dòng

### 2️⃣ **HUONG_DAN_IMPORT_PRODUCT.md** (Chi tiết đầy đủ)
   - Hướng dẫn từng bước chi tiết
   - Giải thích tất cả các cột
   - Xử lý lỗi thường gặp

### 3️⃣ **Product_Import_Template_Vietnamese.csv** (File Excel mẫu)
   - Có sẵn 10 dòng dữ liệu ví dụ
   - Mở bằng Excel, sửa và import ngay được!

---

## 🎯 CÁCH SỬ DỤNG NHANH

### **CÁCH 1: Dùng file CSV mẫu có sẵn** ⭐ Khuyến nghị

```bash
1. Mở file: Product_Import_Template_Vietnamese.csv
2. Xóa các dòng mẫu (GIỮ LẠI dòng header)
3. Điền dữ liệu sản phẩm của bạn
4. Lưu file
5. Import vào Product Management
```

### **CÁCH 2: Tạo mới từ Excel**

```bash
1. Mở Excel
2. Dòng 1: Copy-paste header từ file mẫu
3. Dòng 2+: Điền dữ liệu
4. Lưu file .xlsx
5. Import vào Product Management
```

---

## 📋 10 CỘT BẮT BUỘC CẦN NHỚ

| Cột | Tên | Bắt buộc | Ví dụ |
|-----|-----|----------|--------|
| 1 | Code | ✅ | ASPIRIN300 |
| 2 | Name | ✅ | Aspirin 300mg |
| 3 | Category Code | ✅ | OTC |
| 4 | Manufacturer Code | ❌ | DHG |
| 5 | Barcode | ❌ | 893600123016 |
| 6 | Dosage Form | ❌ | Viên nén |
| 7 | Unit | ❌ | Hộp |
| 8 | Description | ❌ | Giảm đau hạ sốt |
| 9 | Standard Price | ❌ | 18000 |
| 10 | Requires Prescription | ❌ | No |

---

## 🏷️ CATEGORY CODE - DÙNG THEO DATABASE CỦA BẠN

**Từ file `db.sql` của bạn:**

```
MED     - Thuốc (cha)
  ├─ OTC   - Thuốc không kê đơn ⭐ (dùng nhiều)
  └─ ETC   - Thuốc kê đơn ⭐ (dùng nhiều)

SUP     - Thực phẩm chức năng (cha)
  ├─ VIT   - Vitamin & Khoáng chất
  └─ IMM   - Tăng đề kháng

EQUIP   - Thiết bị y tế (cha)
  ├─ BP    - Máy đo huyết áp
  └─ GLU   - Máy đo đường huyết

CARE    - Chăm sóc cá nhân (cha)
  ├─ SKIN  - Chăm sóc da
  └─ BABY  - Chăm sóc trẻ em
```

---

## 🏭 MANUFACTURER CODE - DÙNG THEO DATABASE CỦA BẠN

```
DHG      - Dược Hậu Giang (VN)
TRA      - Traphaco (VN)
IMEX     - Imexpharm (VN)
SANOFI   - Sanofi (Pháp)
GSK      - GlaxoSmithKline (Anh)
PFIZER   - Pfizer (Mỹ)
ABBOTT   - Abbott (Mỹ)
BAYER    - Bayer (Đức)
ROCHE    - Roche (Thụy Sĩ)
MEDELA   - Medela (Thụy Sĩ)
```

---

## 📝 VÍ DỤ CỤ THỂ

### Ví dụ 1: Thuốc OTC (giảm đau thông thường)
```csv
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau hạ sốt,18000,No
```

### Ví dụ 2: Thuốc ETC (kháng sinh cần toa)
```csv
CEFIX200,Cefixime 200mg,ETC,PFIZER,893600123019,Viên nang,Hộp,Kháng sinh thế hệ 3,85000,Yes
```

### Ví dụ 3: Vitamin (TPCN)
```csv
VITAMIN_B,Vitamin B Complex,VIT,ABBOTT,893600123017,Viên nang,Lọ,Bổ sung vitamin B,75000,No
```

### Ví dụ 4: Thiết bị y tế
```csv
GLUCO_METER,Máy đo đường huyết,GLU,ROCHE,893600123023,Thiết bị,Cái,Đo đường huyết tại nhà,680000,No
```

---

## ⚠️ 5 LƯU Ý QUAN TRỌNG

### 1. ✅ Category Code và Manufacturer Code PHẢI TỒN TẠI
   - Nếu dùng `OTC` → Category OTC phải đã có trong database
   - Nếu dùng `DHG` → Manufacturer DHG phải đã có
   - ❌ Nếu chưa có → Vào Category/Manufacturer Management tạo trước!

### 2. ✅ Code sản phẩm KHÔNG được trùng
   - Mỗi sản phẩm phải có Code unique
   - Hệ thống sẽ báo lỗi nếu Code đã tồn tại

### 3. ✅ Requires Prescription (Toa thuốc)
   - `Yes`, `True`, `1` → Cần toa (thuốc kê đơn)
   - `No`, `False`, `0`, (trống) → Không cần (thuốc OTC)

### 4. ✅ Standard Price (Giá)
   - Chỉ nhập số, không có dấu phân cách
   - ✅ ĐÚNG: `18000`
   - ❌ SAI: `18,000` hoặc `18.000`

### 5. ✅ Encoding (Mã hóa)
   - File CSV lưu dạng **UTF-8** để hiển thị đúng tiếng Việt
   - Excel: File → Save As → chọn **CSV UTF-8**

---

## 🚀 QUY TRÌNH IMPORT

```
📝 BƯỚC 1: Tạo/sửa file Excel
   ↓
✅ BƯỚC 2: Kiểm tra dữ liệu
   • Đủ 10 cột header
   • Category Code đúng
   • Manufacturer Code đúng
   • Code không trùng
   ↓
📤 BƯỚC 3: Import
   • Product Management → Import Excel
   • Chọn file
   ↓
👀 BƯỚC 4: Xem Preview
   • ✅ Dòng xanh = OK
   • ❌ Dòng đỏ = Lỗi (xem thông báo)
   ↓
💾 BƯỚC 5: Xác nhận
   • Click "Import Valid Rows"
   ↓
🎉 HOÀN TẤT!
```

---

## 🐛 XỬ LÝ LỖI NHANH

| Lỗi | Sửa thế nào |
|-----|-------------|
| `Category with code 'XXX' not found` | Dùng đúng code: OTC, ETC, VIT, IMM, EQUIP, BP, GLU, CARE, SKIN, BABY |
| `Manufacturer with code 'YYY' not found` | Dùng đúng code: DHG, TRA, IMEX, SANOFI, GSK, PFIZER, ABBOTT, BAYER, ROCHE, MEDELA HOẶC để trống |
| `Product with code 'ZZZ' already exists` | Đổi Code khác, ví dụ: PARA500 → PARA500_V2 |
| `Code is required` | Điền đầy đủ Code cho tất cả dòng |
| `Standard price must be positive` | Nhập giá dương, ví dụ: 15000 |

---

## 📚 CẤU TRÚC THƯ MỤC

```
C:\DungHD\BAITAPUTT\
├── db.sql                                   ← Database (có Category, Manufacturer, Product)
├── Product_Import_Template_Vietnamese.csv   ← File Excel mẫu ⭐
├── QUICK_IMPORT_GUIDE.md                    ← Hướng dẫn nhanh ⭐
├── HUONG_DAN_IMPORT_PRODUCT.md              ← Hướng dẫn chi tiết
├── IMPORT_SUMMARY.md                        ← File này (tổng hợp)
└── ...
```

---

## 🎯 CHECKLIST TRƯỚC KHI IMPORT

- [ ] Đã đọc QUICK_IMPORT_GUIDE.md
- [ ] Đã mở file Product_Import_Template_Vietnamese.csv
- [ ] File có đúng 10 cột header
- [ ] Tất cả dòng có: Code, Name, Category Code
- [ ] Category Code thuộc danh sách: MED, OTC, ETC, SUP, VIT, IMM, EQUIP, BP, GLU, CARE, SKIN, BABY
- [ ] Manufacturer Code thuộc danh sách (hoặc để trống)
- [ ] Code không trùng với sản phẩm đã có
- [ ] Standard Price là số dương
- [ ] Đã lưu file dạng .xlsx hoặc .csv UTF-8

---

## 💡 MẸO HAY

### Mẹo 1: Tạo nhiều sản phẩm cùng lúc
- Copy dòng mẫu trong file CSV
- Paste nhiều lần
- Sửa từng dòng: Code, Name, Barcode
- Category Code và Manufacturer Code có thể giống nhau

### Mẹo 2: Kiểm tra nhanh Category Code
- Mở file `db.sql`
- Tìm dòng: `INSERT INTO categories`
- Xem cột `code` → Đó là các Category Code hợp lệ

### Mẹo 3: Xử lý lỗi hàng loạt
- Nếu nhiều dòng lỗi cùng lý do (ví dụ: Category không tồn tại)
- Sửa file Excel một lần
- Import lại (không cần tạo file mới)

---

## 📞 KẾT LUẬN

**✨ Bạn đã có đầy đủ công cụ để import product:**

1. ✅ File Excel mẫu: `Product_Import_Template_Vietnamese.csv`
2. ✅ Danh sách Category Code (12 code)
3. ✅ Danh sách Manufacturer Code (10 code)
4. ✅ Ví dụ cụ thể từng loại sản phẩm
5. ✅ Hướng dẫn xử lý lỗi

**🎯 Bắt đầu ngay:**
1. Mở file `Product_Import_Template_Vietnamese.csv`
2. Xóa dòng mẫu, điền dữ liệu của bạn
3. Import vào Product Management

**📖 Cần thêm chi tiết?**
- Đọc: `QUICK_IMPORT_GUIDE.md` (nhanh)
- Hoặc: `HUONG_DAN_IMPORT_PRODUCT.md` (chi tiết)

---

**🎉 Chúc bạn import thành công!**

_Nếu gặp lỗi, tham khảo phần "Xử lý lỗi" trong QUICK_IMPORT_GUIDE.md_

