# 🎯 TÓM TẮT NHANH: CÁCH TẠO FILE EXCEL IMPORT PRODUCT

## 📋 CẤU TRÚC FILE (10 Cột)

### Dòng 1 (Header):
```
Code | Name | Category Code | Manufacturer Code | Barcode | Dosage Form | Unit | Description | Standard Price | Requires Prescription
```

### Từ dòng 2 trở đi: Dữ liệu sản phẩm

---

## ✅ CÁC CỘT BẮT BUỘC

| Cột | Bắt buộc | Ví dụ |
|-----|----------|--------|
| **Code** | ✅ | ASPIRIN300 |
| **Name** | ✅ | Aspirin 300mg |
| **Category Code** | ✅ | OTC |
| Manufacturer Code | ❌ | DHG |
| Barcode | ❌ | 893600123016 |
| Dosage Form | ❌ | Viên nén |
| Unit | ❌ | Hộp |
| Description | ❌ | Giảm đau hạ sốt |
| Standard Price | ❌ | 18000 |
| Requires Prescription | ❌ | No |

---

## 📝 DANH SÁCH CATEGORY CODE (Dùng trong database của bạn)

| Code | Tên | Ghi chú |
|------|-----|---------|
| `MED` | Thuốc | Danh mục cha |
| `OTC` | Thuốc không kê đơn | Con của MED |
| `ETC` | Thuốc kê đơn | Con của MED |
| `SUP` | Thực phẩm chức năng | Danh mục cha |
| `VIT` | Vitamin & Khoáng chất | Con của SUP |
| `IMM` | Tăng đề kháng | Con của SUP |
| `EQUIP` | Thiết bị y tế | Danh mục cha |
| `BP` | Máy đo huyết áp | Con của EQUIP |
| `GLU` | Máy đo đường huyết | Con của EQUIP |
| `CARE` | Chăm sóc cá nhân | Danh mục cha |
| `SKIN` | Chăm sóc da | Con của CARE |
| `BABY` | Chăm sóc trẻ em | Con của CARE |

---

## 🏭 DANH SÁCH MANUFACTURER CODE

| Code | Tên | Quốc gia |
|------|-----|----------|
| `DHG` | Dược Hậu Giang | Việt Nam |
| `TRA` | Traphaco | Việt Nam |
| `IMEX` | Imexpharm | Việt Nam |
| `SANOFI` | Sanofi | Pháp |
| `GSK` | GlaxoSmithKline | Anh |
| `PFIZER` | Pfizer | Mỹ |
| `ABBOTT` | Abbott | Mỹ |
| `BAYER` | Bayer | Đức |
| `ROCHE` | Roche | Thụy Sĩ |
| `MEDELA` | Medela | Thụy Sĩ |

---

## 📄 VÍ DỤ MỘT DÒNG ĐẦY ĐỦ

```csv
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau hạ sốt,18000,No
```

**Giải thích:**
- **Code**: ASPIRIN300 (duy nhất)
- **Name**: Aspirin 300mg
- **Category Code**: OTC (thuốc không kê đơn)
- **Manufacturer Code**: DHG (Dược Hậu Giang)
- **Barcode**: 893600123016
- **Dosage Form**: Viên nén
- **Unit**: Hộp
- **Description**: Giảm đau hạ sốt
- **Standard Price**: 18000 VNĐ
- **Requires Prescription**: No (không cần toa)

---

## 🚀 CÁCH TẠO FILE NHANH

### **Option 1: Sử dụng Excel/Google Sheets**

1. Mở Excel
2. **Dòng 1**: Copy-paste header này:
   ```
   Code,Name,Category Code,Manufacturer Code,Barcode,Dosage Form,Unit,Description,Standard Price,Requires Prescription
   ```
3. **Dòng 2+**: Điền dữ liệu sản phẩm
4. Lưu file `.xlsx` hoặc `.csv`

### **Option 2: Sử dụng file Template có sẵn**

Trong thư mục dự án có file: **`Product_Import_Template_Vietnamese.csv`**

1. Mở file này
2. Xóa các dòng mẫu (giữ lại header)
3. Điền dữ liệu của bạn
4. Lưu lại

### **Option 3: Tải từ hệ thống**

1. Vào màn hình **Product Management**
2. Click **"📥 Download Template"**
3. File Excel mẫu sẽ tự động tải về

---

## ⚠️ LƯU Ý QUAN TRỌNG

### 1. ✅ Category và Manufacturer phải tồn tại trước

- Nếu dùng `Category Code = "OTC"` → Category OTC phải đã có trong database
- Nếu dùng `Manufacturer Code = "DHG"` → Manufacturer DHG phải đã có

### 2. ✅ Code không được trùng

- Mỗi sản phẩm phải có Code unique
- Hệ thống sẽ báo lỗi nếu Code đã tồn tại

### 3. ✅ Requires Prescription

- `Yes`, `True`, `1` → Cần toa thuốc
- `No`, `False`, `0`, (trống) → Không cần toa

### 4. ✅ Standard Price

- Chỉ nhập số, không có dấu phân cách
- ✅ ĐÚNG: `18000`
- ❌ SAI: `18,000` hoặc `18.000`

---

## 📥 CÁCH IMPORT

1. Vào **Product Management**
2. Click **"📤 Import Excel"**
3. Chọn file đã tạo
4. Xem **Preview**:
   - ✅ Dòng hợp lệ (màu xanh)
   - ❌ Dòng lỗi (màu đỏ + thông báo)
5. Click **"Import Valid Rows"**
6. Hoàn tất!

---

## 🎯 TỔNG KẾT

**3 bước đơn giản:**

1. **Tạo file Excel** với 10 cột (xem cấu trúc trên)
2. **Điền dữ liệu** (chú ý Category Code và Manufacturer Code phải đúng)
3. **Import** vào hệ thống

**File mẫu sẵn có:** `Product_Import_Template_Vietnamese.csv`

---

## 📞 Xử lý lỗi thường gặp

| Lỗi | Nguyên nhân | Giải pháp |
|-----|-------------|-----------|
| `Category with code 'XXX' not found` | Category không tồn tại | Vào Category Management tạo category trước |
| `Manufacturer with code 'YYY' not found` | Manufacturer không tồn tại | Vào Manufacturer Management tạo trước hoặc để trống |
| `Product with code 'ZZZ' already exists` | Code bị trùng | Đổi Code thành mã khác |
| `Code is required` | Thiếu Code | Điền đầy đủ Code cho tất cả dòng |
| `Standard price must be positive` | Giá âm/0 | Nhập giá dương |

---

**✨ Chúc bạn import thành công!**

Xem hướng dẫn chi tiết tại: `HUONG_DAN_IMPORT_PRODUCT.md`

