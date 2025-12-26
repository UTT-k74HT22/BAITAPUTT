# 📊 HÌNH ẢNH TRỰC QUAN: FILE EXCEL IMPORT PRODUCT

## ✅ ĐÂY LÀ CÁCH FILE EXCEL CỦA BẠN PHẢI TRÔNG NHƯ THẾ NÀO

### 🖼️ Giao diện Excel (Dạng bảng)

```
╔═══════════╦═══════════════════╦══════════════╦══════════════════╦══════════════╦═════════════╦══════╦═════════════════════╦════════════════╦══════════════════════╗
║     A     ║         B         ║      C       ║        D         ║      E       ║      F      ║  G   ║          H          ║       I        ║          J           ║
╠═══════════╬═══════════════════╬══════════════╬══════════════════╬══════════════╬═════════════╬══════╬═════════════════════╬════════════════╬══════════════════════╣
║   Code    ║       Name        ║ Category Code║ Manufacturer Code║   Barcode    ║ Dosage Form ║ Unit ║    Description      ║ Standard Price ║ Requires Prescription║
╠═══════════╬═══════════════════╬══════════════╬══════════════════╬══════════════╬═════════════╬══════╬═════════════════════╬════════════════╬══════════════════════╣
║ASPIRIN300 ║ Aspirin 300mg     ║     OTC      ║       DHG        ║893600123016  ║  Viên nén   ║ Hộp  ║ Giảm đau hạ sốt     ║     18000      ║          No          ║
╠═══════════╬═══════════════════╬══════════════╬══════════════════╬══════════════╬═════════════╬══════╬═════════════════════╬════════════════╬══════════════════════╣
║VITAMIN_B  ║ Vitamin B Complex ║     VIT      ║     ABBOTT       ║893600123017  ║  Viên nang  ║ Lọ   ║ Bổ sung vitamin B   ║     75000      ║          No          ║
╠═══════════╬═══════════════════╬══════════════╬══════════════════╬══════════════╬═════════════╬══════╬═════════════════════╬════════════════╬══════════════════════╣
║CEFIX200   ║ Cefixime 200mg    ║     ETC      ║     PFIZER       ║893600123019  ║  Viên nang  ║ Hộp  ║ Kháng sinh thế hệ 3 ║     85000      ║         Yes          ║
╚═══════════╩═══════════════════╩══════════════╩══════════════════╩══════════════╩═════════════╩══════╩═════════════════════╩════════════════╩══════════════════════╝

    ✅             ✅                ✅               ❌              ❌              ❌         ❌            ❌                 ❌                   ❌
  BẮT BUỘC      BẮT BUỘC        BẮT BUỘC      Không bắt buộc  Không bắt buộc  Không bắt buộc  ...     Không bắt buộc    Không bắt buộc      Không bắt buộc
```

---

## 📝 GIẢI THÍCH TỪNG CỘT

### **Cột A: Code** ✅ BẮT BUỘC
```
ASPIRIN300      ← Mã sản phẩm, phải UNIQUE
VITAMIN_B       ← Có thể dùng chữ, số, gạch dưới
CEFIX200        ← KHÔNG được trùng với sản phẩm đã có
```

### **Cột B: Name** ✅ BẮT BUỘC
```
Aspirin 300mg               ← Tên đầy đủ của sản phẩm
Vitamin B Complex           ← Có thể có khoảng trắng, ký tự đặc biệt
Cefixime 200mg              ← Thường ghi kèm hàm lượng
```

### **Cột C: Category Code** ✅ BẮT BUỘC
```
OTC        ← Thuốc không kê đơn (thuốc OTC phổ biến)
VIT        ← Vitamin & Khoáng chất
ETC        ← Thuốc kê đơn (cần toa thuốc)
IMM        ← Tăng đề kháng
EQUIP      ← Thiết bị y tế (hoặc BP, GLU)
SKIN       ← Chăm sóc da
BABY       ← Sản phẩm cho trẻ em
```

**⚠️ CHỈ được dùng 12 code sau:**
```
MED, OTC, ETC, SUP, VIT, IMM, EQUIP, BP, GLU, CARE, SKIN, BABY
```

### **Cột D: Manufacturer Code** ❌ Không bắt buộc
```
DHG        ← Dược Hậu Giang (VN)
TRA        ← Traphaco (VN)
PFIZER     ← Pfizer (Mỹ)
ABBOTT     ← Abbott (Mỹ)
SANOFI     ← Sanofi (Pháp)
(trống)    ← Có thể để trống nếu không biết
```

**⚠️ Nếu điền, CHỈ được dùng 10 code sau:**
```
DHG, TRA, IMEX, SANOFI, GSK, PFIZER, ABBOTT, BAYER, ROCHE, MEDELA
```

### **Cột E: Barcode** ❌ Không bắt buộc
```
893600123016    ← Mã vạch 13 số
8934567890123   ← Hoặc mã vạch khác
(trống)         ← Có thể để trống
```

### **Cột F: Dosage Form** ❌ Không bắt buộc
```
Viên nén        ← Thuốc dạng viên nén
Viên nang       ← Thuốc dạng viên nang
Viên sủi        ← Vitamin dạng sủi
Syrup           ← Thuốc nước
Gel, Kem        ← Sản phẩm bôi ngoài da
Thiết bị        ← Máy móc y tế
Bột             ← Sữa bột, thuốc bột
(trống)         ← Có thể để trống
```

### **Cột G: Unit** ❌ Không bắt buộc
```
Hộp             ← Đơn vị phổ biến nhất
Chai, Lọ        ← Dạng lỏng
Tuýp            ← Viên sủi, kem
Lon             ← Sữa bột
Cái             ← Thiết bị
Gói, Vỉ         ← Đơn vị khác
(trống)         ← Có thể để trống
```

### **Cột H: Description** ❌ Không bắt buộc
```
Giảm đau hạ sốt                 ← Mô tả ngắn gọn
Bổ sung vitamin B               ← Công dụng chính
Kháng sinh thế hệ 3             ← Thông tin thuốc
(trống)                         ← Có thể để trống
```

### **Cột I: Standard Price** ❌ Không bắt buộc
```
18000       ← CHỈ nhập số, KHÔNG có dấu phẩy
75000       ← Đơn vị: VNĐ
85000       ← Phải là số dương
(trống)     ← Có thể để trống

❌ SAI: 18,000 hoặc 18.000
✅ ĐÚNG: 18000
```

### **Cột J: Requires Prescription** ❌ Không bắt buộc
```
No          ← Không cần toa (thuốc OTC)
Yes         ← Cần toa thuốc (thuốc kê đơn)
True        ← Tương đương Yes
False       ← Tương đương No
1           ← Tương đương Yes
0           ← Tương đương No
(trống)     ← Mặc định là No
```

---

## 🎨 MÀU SẮC TRONG EXCEL (Khuyến nghị)

### **Header (Dòng 1):**
- Màu nền: **Xanh dương đậm** (#4472C4)
- Màu chữ: **Trắng**
- Font: **Arial Bold 12pt**

### **Dữ liệu (Từ dòng 2):**
- Cột bắt buộc (A, B, C): Màu nền **vàng nhạt** (#FFF2CC)
- Cột không bắt buộc: Màu nền **trắng**

---

## 📐 ĐỘ RỘNG CỘT KHUYẾN NGHỊ

| Cột | Tên | Độ rộng (pixels) |
|-----|-----|------------------|
| A | Code | 120 |
| B | Name | 200 |
| C | Category Code | 120 |
| D | Manufacturer Code | 150 |
| E | Barcode | 120 |
| F | Dosage Form | 120 |
| G | Unit | 80 |
| H | Description | 250 |
| I | Standard Price | 120 |
| J | Requires Prescription | 150 |

---

## ✅ VÍ DỤ THỰC TẾ (Copy vào Excel)

### **Thuốc OTC (Không kê đơn):**
```
Code          Name               Category Code  Manufacturer Code  Barcode        Dosage Form  Unit  Description           Standard Price  Requires Prescription
ASPIRIN300    Aspirin 300mg      OTC            DHG                893600123016   Viên nén     Hộp   Giảm đau hạ sốt       18000           No
LOXO150       Loxoprofen 150mg   OTC            DHG                893600123027   Viên nén     Hộp   Giảm đau kháng viêm   22000           No
```

### **Thuốc ETC (Kê đơn):**
```
Code          Name               Category Code  Manufacturer Code  Barcode        Dosage Form  Unit  Description           Standard Price  Requires Prescription
CEFIX200      Cefixime 200mg     ETC            PFIZER             893600123019   Viên nang    Hộp   Kháng sinh thế hệ 3   85000           Yes
CLARI500      Clarithromycin 500 ETC            SANOFI             893600123029   Viên nén     Hộp   Kháng sinh macrolide  95000           Yes
METFORM850    Metformin 850mg    ETC            GSK                893600123021   Viên nén     Hộp   Điều trị tiểu đường   32000           Yes
```

### **Thực phẩm chức năng:**
```
Code          Name               Category Code  Manufacturer Code  Barcode        Dosage Form  Unit  Description           Standard Price  Requires Prescription
VITAMIN_B     Vitamin B Complex  VIT            ABBOTT             893600123017   Viên nang    Lọ    Bổ sung vitamin B     75000           No
CALCI_D3      Calcium + D3       VIT            ABBOTT             893600123022   Viên sủi     Tuýp  Bổ sung canxi D3      55000           No
LACTO_PRO     Lactobacillus      IMM            TRA                893600123018   Gói          Hộp   Hỗ trợ tiêu hóa       65000           No
```

### **Thiết bị y tế:**
```
Code          Name                   Category Code  Manufacturer Code  Barcode        Dosage Form  Unit  Description           Standard Price  Requires Prescription
GLUCO_METER   Máy đo đường huyết     GLU            ROCHE              893600123023   Thiết bị     Cái   Đo đường huyết        680000          No
BP_MONITOR    Máy đo huyết áp điện tử BP            ROCHE              893600123024   Thiết bị     Cái   Đo huyết áp tự động   750000          No
```

### **Chăm sóc cá nhân:**
```
Code          Name                    Category Code  Manufacturer Code  Barcode        Dosage Form  Unit  Description           Standard Price  Requires Prescription
SUNSCREEN50   Kem chống nắng SPF50    SKIN           BAYER              893600123025   Kem          Tuýp  Bảo vệ da khỏi UV     120000          No
MOISTURIZER   Kem dưỡng ẩm da         SKIN           BAYER              893600123030   Kem          Tuýp  Dưỡng ẩm chống khô    95000           No
BABYMILK01    Sữa bột cho bé 0-6 tháng BABY          ABBOTT             893600123026   Bột          Lon   Dinh dưỡng trẻ sơ sinh 420000         No
```

---

## 🚫 SAI VÀ ĐÚNG

### ❌ **SAI** - Thiếu cột header
```
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau,18000,No
```
→ Không có dòng header!

### ✅ **ĐÚNG** - Có đầy đủ header
```
Code,Name,Category Code,Manufacturer Code,Barcode,Dosage Form,Unit,Description,Standard Price,Requires Prescription
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau,18000,No
```

---

### ❌ **SAI** - Category Code không tồn tại
```
ASPIRIN300,Aspirin 300mg,MEDICINE,DHG,893600123016,Viên nén,Hộp,Giảm đau,18000,No
```
→ Category Code `MEDICINE` không có trong database!

### ✅ **ĐÚNG** - Dùng đúng Category Code
```
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau,18000,No
```
→ Category Code `OTC` có trong database

---

### ❌ **SAI** - Giá có dấu phẩy
```
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau,18,000,No
```
→ Excel sẽ hiểu nhầm thành nhiều cột!

### ✅ **ĐÚNG** - Giá chỉ có số
```
ASPIRIN300,Aspirin 300mg,OTC,DHG,893600123016,Viên nén,Hộp,Giảm đau,18000,No
```

---

## 📥 CÁCH MỞ FILE CSV TRONG EXCEL

### **Cách 1: Double-click file**
```
1. Double-click file .csv
2. Excel tự động mở
```

### **Cách 2: Import dữ liệu (Khuyến nghị cho tiếng Việt)**
```
1. Mở Excel
2. Data → Get Data → From File → From Text/CSV
3. Chọn file .csv
4. Chọn Encoding: UTF-8
5. Click Load
```

### **Cách 3: Mở trực tiếp**
```
1. Excel → File → Open
2. Chọn file .csv
3. Nếu bị lỗi font: Save As → chọn "CSV UTF-8"
```

---

## 🎯 CHECKLIST CUỐI CÙNG

Trước khi import, kiểm tra file Excel của bạn:

- [ ] ✅ Có đúng 10 cột (không thiếu, không thừa)
- [ ] ✅ Dòng 1 là header (Code, Name, Category Code...)
- [ ] ✅ Tất cả dòng dữ liệu có: Code, Name, Category Code
- [ ] ✅ Category Code thuộc 12 code: MED, OTC, ETC, SUP, VIT, IMM, EQUIP, BP, GLU, CARE, SKIN, BABY
- [ ] ✅ Manufacturer Code thuộc 10 code (hoặc để trống)
- [ ] ✅ Code không trùng với sản phẩm đã có
- [ ] ✅ Standard Price chỉ có số (không có dấu phẩy, chấm)
- [ ] ✅ Requires Prescription là: Yes, No, True, False, 1, 0, hoặc trống
- [ ] ✅ File lưu dạng .xlsx hoặc .csv (UTF-8)

---

## 📞 TỔNG KẾT

**✨ File Excel của bạn phải:**
1. Có **10 cột** (đúng thứ tự)
2. Dòng 1 là **header** (chính xác tên cột)
3. Từ dòng 2: **dữ liệu sản phẩm**
4. Code, Name, Category Code: **Bắt buộc**
5. Category Code & Manufacturer Code: **Đúng với database**

**📁 File mẫu hoàn chỉnh:** `Product_Import_Template_Vietnamese.csv`

**📖 Hướng dẫn chi tiết:** `QUICK_IMPORT_GUIDE.md`

---

**🎉 Bây giờ bạn đã biết chính xác file Excel phải như thế nào!**

_Mở file Product_Import_Template_Vietnamese.csv để xem ví dụ thực tế_

