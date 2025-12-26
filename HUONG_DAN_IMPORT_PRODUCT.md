# 📦 HƯỚNG DẪN IMPORT SẢN PHẨM (PRODUCT) - NHÀ THUỐC

## 🎯 Mục đích
Hướng dẫn chi tiết cách tạo file Excel để import hàng loạt sản phẩm vào hệ thống quản lý nhà thuốc.

---

## 📋 CẤU TRÚC FILE EXCEL

### **Header (Dòng 1) - BẮT BUỘC**

File Excel phải có **chính xác 10 cột** theo thứ tự sau:

| Cột | Tên Header | Bắt buộc | Kiểu dữ liệu |
|-----|------------|----------|--------------|
| A | Code | ✅ | Text |
| B | Name | ✅ | Text |
| C | Category Code | ✅ | Text |
| D | Manufacturer Code | ❌ | Text |
| E | Barcode | ❌ | Text/Number |
| F | Dosage Form | ❌ | Text |
| G | Unit | ❌ | Text |
| H | Description | ❌ | Text |
| I | Standard Price | ❌ | Number |
| J | Requires Prescription | ❌ | Yes/No |

---

## 📝 CHI TIẾT CÁC CỘT

### **A. Code** ✅ BẮT BUỘC
- **Mô tả**: Mã sản phẩm (duy nhất trong hệ thống)
- **Quy tắc**: 
  - Không được trùng với sản phẩm đã có
  - Nên dùng chữ HOA và số
  - Không có khoảng trắng (dùng _ hoặc -)
- **Ví dụ**: `PARA500`, `ASPIRIN300`, `VITAMIN_B`

### **B. Name** ✅ BẮT BUỘC
- **Mô tả**: Tên đầy đủ của sản phẩm
- **Ví dụ**: `Paracetamol 500mg`, `Aspirin 300mg`, `Vitamin B Complex`

### **C. Category Code** ✅ BẮT BUỘC
- **Mô tả**: Mã danh mục sản phẩm (PHẢI TỒN TẠI trong database)
- **Danh sách Category Code có sẵn**:

| Code | Tên danh mục | Mô tả |
|------|--------------|-------|
| `MED` | Thuốc | Nhóm thuốc điều trị (cha) |
| `OTC` | Thuốc không kê đơn | Thuốc OTC (con của MED) |
| `ETC` | Thuốc kê đơn | Thuốc ETC (con của MED) |
| `SUP` | Thực phẩm chức năng | TPCN (cha) |
| `VIT` | Vitamin & Khoáng chất | Vitamin (con của SUP) |
| `IMM` | Tăng đề kháng | Hỗ trợ miễn dịch (con của SUP) |
| `EQUIP` | Thiết bị y tế | Thiết bị (cha) |
| `BP` | Máy đo huyết áp | Đo huyết áp (con của EQUIP) |
| `GLU` | Máy đo đường huyết | Đo đường huyết (con của EQUIP) |
| `CARE` | Chăm sóc cá nhân | Chăm sóc sức khỏe (cha) |
| `SKIN` | Chăm sóc da | Sản phẩm da (con của CARE) |
| `BABY` | Chăm sóc trẻ em | Sản phẩm cho bé (con của CARE) |

> ⚠️ **LƯU Ý**: Nếu cần thêm category mới, vào màn hình **Category Management** để tạo trước khi import!

### **D. Manufacturer Code** ❌ Không bắt buộc
- **Mô tả**: Mã nhà sản xuất (nếu điền thì PHẢI TỒN TẠI trong database)
- **Danh sách Manufacturer Code có sẵn**:

| Code | Tên nhà sản xuất | Quốc gia |
|------|------------------|----------|
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

> ⚠️ **LƯU Ý**: Nếu cần thêm manufacturer mới, vào màn hình **Manufacturer Management** để tạo trước!

### **E. Barcode** ❌ Không bắt buộc
- **Mô tả**: Mã vạch sản phẩm
- **Ví dụ**: `893600123001`, `8934567890123`

### **F. Dosage Form** ❌ Không bắt buộc
- **Mô tả**: Dạng bào chế
- **Ví dụ**: `Viên nén`, `Viên nang`, `Viên sủi`, `Syrup`, `Gel`, `Kem`, `Tiêm`, `Thuốc nước`, `Thiết bị`, `Bột`

### **G. Unit** ❌ Không bắt buộc
- **Mô tả**: Đơn vị tính
- **Ví dụ**: `Hộp`, `Chai`, `Lọ`, `Tuýp`, `Gói`, `Lon`, `Cái`, `Vỉ`, `Ống`

### **H. Description** ❌ Không bắt buộc
- **Mô tả**: Mô tả chi tiết về sản phẩm
- **Ví dụ**: `Giảm đau hạ sốt`, `Kháng sinh phổ rộng`, `Bổ sung vitamin`

### **I. Standard Price** ❌ Không bắt buộc
- **Mô tả**: Giá tiêu chuẩn (VNĐ)
- **Quy tắc**: Phải là số dương
- **Ví dụ**: `15000`, `55000`, `120000`

### **J. Requires Prescription** ❌ Không bắt buộc
- **Mô tả**: Có cần toa thuốc không?
- **Giá trị hợp lệ**:
  - `Yes`, `True`, `1` → Cần toa (ETC)
  - `No`, `False`, `0`, (trống) → Không cần (OTC)

---

## 📄 VÍ DỤ DỮ LIỆU

### Ví dụ đầy đủ (10 sản phẩm mẫu):

```csv
Code,Name,Category Code,Manufacturer Code,Barcode,Dosage Form,Unit,Description,Standard Price,Requires Prescription
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau hạ sốt,18000,No
VITAMIN_B,Vitamin B Complex,VIT,ABBOTT,893600123017,Viên nang,Lọ,Bổ sung vitamin B,75000,No
LACTO_PRO,Lactobacillus Probiotic,IMM,TRA,893600123018,Gói,Hộp,Hỗ trợ tiêu hóa,65000,No
CEFIX200,Cefixime 200mg,ETC,PFIZER,893600123019,Viên nang,Hộp,Kháng sinh thế hệ 3,85000,Yes
OMEZ20,Omeprazole 20mg,ETC,SANOFI,893600123020,Viên nang,Hộp,Điều trị viêm loét dạ dày,45000,Yes
METFORM850,Metformin 850mg,ETC,GSK,893600123021,Viên nén,Hộp,Điều trị tiểu đường type 2,32000,Yes
CALCI_D3,Calcium + D3,VIT,ABBOTT,893600123022,Viên sủi,Tuýp,Bổ sung canxi vitamin D3,55000,No
THERMO_01,Nhiệt kế điện tử,EQUIP,OMRON,893600123023,Thiết bị,Cái,Đo nhiệt độ cơ thể,180000,No
MASK_N95,Khẩu trang N95,CARE,3M,893600123024,Khẩu trang,Hộp,Khẩu trang y tế,125000,No
BABYWIPES,Khăn ướt cho bé,BABY,MEDELA,893600123025,Khăn,Gói,Chăm sóc trẻ em,45000,No
```

### Hiển thị dạng bảng Excel:

| Code | Name | Category Code | Manufacturer Code | Barcode | Dosage Form | Unit | Description | Standard Price | Requires Prescription |
|------|------|---------------|-------------------|---------|-------------|------|-------------|----------------|----------------------|
| ASPIRIN300 | Aspirin 300mg | OTC | DHG | 893600123016 | Viên nén | Hộp | Giảm đau hạ sốt | 18000 | No |
| VITAMIN_B | Vitamin B Complex | VIT | ABBOTT | 893600123017 | Viên nang | Lọ | Bổ sung vitamin B | 75000 | No |
| CEFIX200 | Cefixime 200mg | ETC | PFIZER | 893600123019 | Viên nang | Hộp | Kháng sinh thế hệ 3 | 85000 | Yes |

---

## 🚀 CÁCH TẠO FILE EXCEL

### **Cách 1: Tải Template có sẵn** ⭐ _(Khuyến nghị)_

1. Mở màn hình **Product Management**
2. Click nút **"📥 Download Template"**
3. Chọn vị trí lưu file
4. Mở file Excel → Đã có header và dữ liệu mẫu
5. **Xóa các dòng mẫu** (giữ lại header)
6. Điền dữ liệu của bạn
7. Lưu file

### **Cách 2: Tạo thủ công**

1. Mở **Microsoft Excel / LibreOffice Calc / Google Sheets**
2. Tạo file mới
3. **Dòng 1**: Nhập chính xác 10 header (copy từ bảng trên)
4. **Từ dòng 2**: Điền dữ liệu sản phẩm
5. Lưu file dạng **.xlsx** hoặc **.csv** (UTF-8)

### **Cách 3: Sử dụng file mẫu có sẵn**

Sử dụng file: `Product_Import_Template_Vietnamese.csv` (đã có trong thư mục dự án)

---

## 📥 CÁCH IMPORT VÀO HỆ THỐNG

### **Bước 1: Chuẩn bị**

✅ Kiểm tra **Category Code** đã tồn tại  
✅ Kiểm tra **Manufacturer Code** đã tồn tại  
✅ Đảm bảo **Code** sản phẩm không trùng  

### **Bước 2: Import**

1. Mở màn hình **Product Management**
2. Click nút **"📤 Import Excel"**
3. Chọn file Excel đã chuẩn bị
4. Hệ thống sẽ **Preview** dữ liệu:
   - ✅ **Dòng hợp lệ**: Hiển thị màu xanh
   - ❌ **Dòng lỗi**: Hiển thị màu đỏ + thông báo lỗi chi tiết

### **Bước 3: Kiểm tra Preview**

Xem kỹ các dòng lỗi (nếu có):
- **Lỗi phổ biến**:
  - `Category with code 'XXX' not found` → Category không tồn tại
  - `Manufacturer with code 'YYY' not found` → Manufacturer không tồn tại
  - `Product with code 'ZZZ' already exists` → Code bị trùng
  - `Code is required` → Thiếu Code
  - `Name is required` → Thiếu Name

### **Bước 4: Thực hiện Import**

1. Nếu **tất cả dòng hợp lệ** (màu xanh):
   - Click **"Import Valid Rows (X)"**
   - Tất cả sản phẩm sẽ được thêm vào hệ thống

2. Nếu **có dòng lỗi** (màu đỏ):
   - Click **"Import Valid Rows (X)"** → Chỉ import các dòng hợp lệ
   - HOẶC: Click **"Cancel"** → Sửa file Excel → Import lại

### **Bước 5: Hoàn tất**

✅ Kiểm tra danh sách sản phẩm đã được thêm  
✅ Refresh lại màn hình Product Management  

---

## ⚠️ LƯU Ý QUAN TRỌNG

### **1. Category và Manufacturer phải tồn tại trước**

❌ **SAI**: Import product có `Category Code = "NEWCAT"` nhưng chưa tạo category này  
✅ **ĐÚNG**: Tạo category với code `NEWCAT` trước → Sau đó mới import product

**Cách tạo Category/Manufacturer**:
- Vào màn hình **Category Management** / **Manufacturer Management**
- Thêm danh mục/nhà sản xuất mới
- Ghi nhớ **CODE** để điền vào Excel

### **2. Code sản phẩm phải duy nhất**

❌ **SAI**: Import 2 sản phẩm cùng code `PARA500`  
✅ **ĐÚNG**: Mỗi sản phẩm có code riêng biệt: `PARA500`, `PARA1000`

### **3. Dòng lỗi sẽ KHÔNG được import**

- Hệ thống chỉ import **các dòng hợp lệ** (màu xanh)
- Các dòng lỗi (màu đỏ) sẽ bị bỏ qua
- Bạn cần **sửa file Excel** và **import lại** nếu muốn thêm các dòng bị lỗi

### **4. Format dữ liệu**

- **Số** (Standard Price): Nhập trực tiếp số, không có dấu phẩy hay chấm phân cách hàng nghìn
  - ✅ ĐÚNG: `15000`, `120000`
  - ❌ SAI: `15,000`, `120.000`

- **Boolean** (Requires Prescription):
  - `Yes`, `True`, `1` → Cần toa thuốc
  - `No`, `False`, `0`, (để trống) → Không cần toa

### **5. Encoding (Mã hóa ký tự)**

- Nếu sử dụng file **.csv**: Lưu dạng **UTF-8** để hiển thị đúng tiếng Việt
- Nếu sử dụng file **.xlsx**: Không cần lo lắng về encoding

---

## 🐛 TROUBLESHOOTING (Xử lý lỗi)

### **Lỗi: "Category with code 'XXX' not found"**

**Nguyên nhân**: Category Code không tồn tại trong database  
**Giải pháp**:
1. Kiểm tra lại danh sách Category Code (xem bảng trên)
2. Nếu cần category mới → Vào **Category Management** để tạo
3. Sửa lại Excel với đúng code
4. Import lại

### **Lỗi: "Manufacturer with code 'YYY' not found"**

**Nguyên nhân**: Manufacturer Code không tồn tại  
**Giải pháp**:
1. Kiểm tra lại danh sách Manufacturer Code (xem bảng trên)
2. Nếu cần manufacturer mới → Vào **Manufacturer Management** để tạo
3. Hoặc: Để trống cột Manufacturer Code (không bắt buộc)
4. Import lại

### **Lỗi: "Product with code 'ZZZ' already exists"**

**Nguyên nhân**: Sản phẩm với code này đã có trong hệ thống  
**Giải pháp**:
1. Đổi Code thành mã khác (ví dụ: `PARA500` → `PARA500_V2`)
2. HOẶC: Xóa dòng này khỏi Excel (nếu không cần thêm)
3. Import lại

### **Lỗi: "Code is required" hoặc "Name is required"**

**Nguyên nhân**: Thiếu thông tin bắt buộc  
**Giải pháp**:
1. Điền đầy đủ Code và Name cho tất cả các dòng
2. Import lại

### **Lỗi: "Standard price must be positive"**

**Nguyên nhân**: Giá âm hoặc = 0  
**Giải pháp**:
1. Sửa giá thành số dương (ví dụ: `15000`)
2. Import lại

---

## 📊 TỔNG KẾT

| Thông tin | Chi tiết |
|-----------|----------|
| **File format** | .xlsx hoặc .csv (UTF-8) |
| **Số cột bắt buộc** | 10 cột |
| **Cột bắt buộc** | Code, Name, Category Code |
| **Số dòng tối đa** | Không giới hạn (khuyến nghị < 1000 dòng/lần) |
| **Encoding** | UTF-8 (cho tiếng Việt) |

---

## 📞 Liên hệ hỗ trợ

Nếu gặp vấn đề, vui lòng kiểm tra lại:
1. ✅ File Excel có đúng 10 cột header
2. ✅ Category Code và Manufacturer Code đã tồn tại
3. ✅ Code sản phẩm không trùng
4. ✅ Các trường bắt buộc đã điền đầy đủ

---

**Chúc bạn import thành công! 🎉**

