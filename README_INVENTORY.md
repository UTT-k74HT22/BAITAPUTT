# 📘 MODULE INVENTORY - QUẢN LÝ TỒN KHO

## 🎯 CHỨC NĂNG CHÍNH
- Xem tồn kho tất cả sản phẩm
- Lọc theo kho, sản phẩm
- Cảnh báo sắp hết hàng, sắp hết hạn
- Cập nhật ngưỡng min/max

## 🗃️ CẤU TRÚC DỮ LIỆU

```sql
CREATE TABLE inventory_items (
    id BIGINT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    batch_id BIGINT,
    warehouse_id BIGINT NOT NULL,
    quantity_on_hand INT DEFAULT 0,          -- Tồn kho thực tế
    quantity_reserved INT DEFAULT 0,         -- Đã đặt hàng
    min_threshold INT DEFAULT 10,            -- Ngưỡng tối thiểu
    max_threshold INT DEFAULT 1000,          -- Ngưỡng tối đa
    last_stock_check DATE,
    UNIQUE(product_id, batch_id, warehouse_id)
);
```

## 💡 KHÁI NIỆM QUAN TRỌNG

### **quantity_on_hand vs quantity_reserved vs quantity_available**

```
VÍ DỤ:
- Trong kho có 100 sản phẩm (quantity_on_hand = 100)
- Khách đặt 30 sản phẩm chưa xuất (quantity_reserved = 30)
- Còn 70 có thể bán (quantity_available = 100 - 30 = 70)

CÔNG THỨC:
quantity_available = quantity_on_hand - quantity_reserved
```

**SQL:**
```sql
SELECT 
    quantity_on_hand,
    quantity_reserved,
    (quantity_on_hand - quantity_reserved) AS quantity_available
FROM inventory_items
```

## 🚨 CẢNH BÁO TỒN KHO

### **1. Sắp hết hàng (Low Stock)**
```sql
SELECT * FROM inventory_items
WHERE quantity_on_hand <= min_threshold
ORDER BY quantity_on_hand ASC;
```
**Ví dụ:** min_threshold=10, quantity=8 → ⚠️ SẮP HẾT!

### **2. Sắp hết hạn (Near Expiry)**
```sql
SELECT * FROM inventory_items ii
JOIN batches b ON ii.batch_id = b.id
WHERE DATEDIFF(b.expiry_date, CURDATE()) <= 90
  AND DATEDIFF(b.expiry_date, CURDATE()) >= 0
ORDER BY b.expiry_date ASC;
```
**Ví dụ:** Hôm nay 05/01/2026, HSD 15/03/2026 → Còn 69 ngày → ⚠️ CẢNH BÁO!

### **3. Dư thừa (Over Stock)**
```sql
SELECT * FROM inventory_items
WHERE quantity_on_hand >= max_threshold;
```

## 🔄 LUỒNG XỬ LÝ

### **getOrCreateInventoryItem() - ✅ ĐÃ FIX BUG #10**

```java
public InventoryItemInfo getOrCreateInventoryItem(Long productId, Long batchId, Long warehouseId) {
    // 1. Kiểm tra đã tồn tại?
    SELECT id, quantity_on_hand 
    FROM inventory_items
    WHERE product_id = ? AND batch_id = ? AND warehouse_id = ?
    
    // 2. Nếu có → return
    if (existing.isPresent()) return existing.get();
    
    // 3. Nếu chưa → Tạo mới
    // ✅ FIX: Dùng ON DUPLICATE KEY để tránh race condition
    INSERT INTO inventory_items (product_id, batch_id, warehouse_id, quantity_on_hand)
    VALUES (?, ?, ?, 0)
    ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);
    
    return new InventoryItemInfo(newId, 0);
}
```

**Tại sao cần ON DUPLICATE KEY?**
```
RACE CONDITION:
Thread A: SELECT → không tìm thấy → INSERT → OK
Thread B: SELECT → không tìm thấy → INSERT → ❌ DUPLICATE KEY ERROR!

FIX:
Thread A: INSERT ON DUPLICATE KEY → OK, id=1
Thread B: INSERT ON DUPLICATE KEY → OK, id=1 (không insert, lấy ID của A)
```

## 🔍 FILTER - TableRowSorter

```java
protected void applyFilters() {
    String kw = keywordField.getText().trim().toLowerCase();
    WarehouseOption warehouse = warehouseFilter.getSelectedItem();
    String status = stockStatusFilter.getSelectedItem();
    
    sorter.setRowFilter(new RowFilter<>() {
        public boolean include(Entry e) {
            // Ghép nhiều cột: warehouse, product code, product name, batch code
            String haystack = (e.getValue(1) + " " + e.getValue(2) + " " + 
                              e.getValue(3) + " " + e.getValue(4)).toLowerCase();
            
            // Keyword
            if (!kw.isEmpty() && !haystack.contains(kw)) return false;
            
            // Warehouse filter
            if (warehouse != null && warehouse.id != null) {
                if (!e.getValue(1).toString().contains(warehouse.name)) return false;
            }
            
            // Status filter
            if (!"TẤT CẢ".equals(status)) {
                if (!e.getValue(11).toString().contains(status)) return false;
            }
            
            return true;
        }
    });
}
```

## ❓ CÂU HỎI VẤN ĐÁP

### **Q1: Giải thích quantity_on_hand, quantity_reserved, quantity_available?**
**A:** 
- **on_hand:** Tồn kho thực tế trong kho
- **reserved:** Đã được đặt hàng nhưng chưa xuất
- **available = on_hand - reserved:** Còn có thể bán

**Ví dụ:** Có 100 sản phẩm, đặt trước 30 → Còn bán được 70

### **Q2: Cảnh báo sắp hết hàng tính như thế nào?**
**A:** `WHERE quantity_on_hand <= min_threshold`

Ví dụ: min=10, hiện có 8 → Cảnh báo!

### **Q3: Sắp hết hạn là bao nhiêu ngày?**
**A:** <= 90 ngày (3 tháng)

SQL: `DATEDIFF(expiry_date, CURDATE()) <= 90`

### **Q4: getOrCreateInventoryItem() làm gì? Tại sao cần ON DUPLICATE KEY?**
**A:** 
- **Làm gì:** Check có inventory_item chưa? Chưa có → tạo mới
- **ON DUPLICATE KEY:** Tránh lỗi khi 2 thread cùng insert
- **Cần UNIQUE INDEX:** `(product_id, batch_id, warehouse_id)`

### **Q5: Bug bạn đã fix?**
**A:** **BUG #10 - Race condition:**
- **Trước:** INSERT trực tiếp → 2 thread → DUPLICATE KEY ERROR
- **Sau:** INSERT ON DUPLICATE KEY UPDATE → OK

**BUG #11 - Không validate newQuantity:**
```java
// ✅ Thêm validation
if (newQuantity < 0) {
    throw new IllegalArgumentException("newQuantity không được âm");
}
```

**BUG #13 - Quá nhiều debug log:**
- Xóa hết System.out.println trong getCurrentQuantity()

## 📝 CHECKLIST ÔN TẬP

- [ ] Giải thích on_hand vs reserved vs available
- [ ] Công thức tính sắp hết hàng, sắp hết hạn
- [ ] getOrCreateInventoryItem() hoạt động thế nào
- [ ] ON DUPLICATE KEY để làm gì
- [ ] TableRowSorter filter multi-column
- [ ] Bugs đã fix

**🎯 Nắm vững Inventory!**
