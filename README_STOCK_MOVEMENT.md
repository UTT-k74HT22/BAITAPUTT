# 📘 MODULE STOCK MOVEMENT - XUẤT NHẬP KHO

## 🎯 CHỨC NĂNG CHÍNH
- Tạo phiếu nhập kho (IMPORT)
- Tạo phiếu xuất kho (EXPORT)
- Điều chỉnh tồn kho (ADJUST)
- Chuyển kho (TRANSFER)
- Xem lịch sử xuất nhập kho

## 🗃️ CẤU TRÚC DỮ LIỆU

```sql
CREATE TABLE stock_movements (
    id BIGINT PRIMARY KEY,
    movement_type ENUM('IMPORT', 'EXPORT', 'ADJUST', 'TRANSFER'),
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    product_id BIGINT NOT NULL,
    batch_id BIGINT,
    warehouse_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    quantity_before INT,                     -- ⭐ Số lượng TRƯỚC giao dịch
    quantity_after INT,                      -- ⭐ Số lượng SAU giao dịch
    reference_type VARCHAR(50),              -- 'ORDER', 'INVOICE', 'MANUAL'
    reference_id BIGINT,
    performed_by_user_id BIGINT,
    note TEXT
);
```

## 💡 CÁC LOẠI GIAO DỊCH

| Loại | Ý nghĩa | Công thức | Ví dụ | Validate |
|------|---------|-----------|-------|----------|
| **IMPORT** | Nhập kho | `after = before + quantity` | 100 + 50 = 150 | quantity > 0 |
| **EXPORT** | Xuất kho | `after = before - quantity` | 100 - 30 = 70 | before >= quantity |
| **ADJUST** | Điều chỉnh | `after = quantity` (absolute) | Trước: 100, Điều chỉnh: 95 → Sau: 95 | quantity >= 0 |
| **TRANSFER** | Chuyển kho | Giảm kho nguồn, tăng kho đích | Kho A: -20, Kho B: +20 | before >= quantity |

## 🔄 LUỒNG XỬ LÝ - ✅ ĐÃ FIX BUG #15 & #16

### **createMovement() - CRITICAL!**

```java
try (Connection conn = DatabaseConfig.getConnection()) {
    conn.setAutoCommit(false); // ✅ Bắt đầu transaction
    
    try {
        // 1. Lấy/tạo inventory_item
        InventoryItemInfo item = inventoryRepository.getOrCreateInventoryItem(
            productId, batchId, warehouseId
        );
        
        // 2. Tính quantity_after
        int quantityBefore = item.getQuantityOnHand();
        int quantityAfter = calculateQuantityAfter(movementType, quantityBefore, quantity);
        
        // 3. Validate business rules
        validateMovement(movementType, quantityBefore, quantity, quantityAfter);
        
        // 4. Insert stock_movements
        stockMovementRepository.insertWithQuantityTracking(request);
        
        // 5. ✅ FIX BUG #16: Update inventory_items.quantity_on_hand
        inventoryRepository.updateQuantity(inventoryItemId, quantityAfter);
        
        // 6. ✅ FIX BUG #15: COMMIT!
        conn.commit();
        
    } catch (Exception e) {
        conn.rollback(); // ❌ Lỗi → ROLLBACK tất cả
        throw new RuntimeException("Lỗi: " + e.getMessage(), e);
    } finally {
        conn.setAutoCommit(true); // Reset
    }
}
```

### **calculateQuantityAfter()**

```java
private int calculateQuantityAfter(StockMovementType type, int before, int quantity) {
    return switch (type) {
        case IMPORT -> before + quantity;       // Nhập: Cộng
        case EXPORT -> before - quantity;       // Xuất: Trừ
        case ADJUST -> quantity;                // Điều chỉnh: Set absolute
        case TRANSFER -> before - quantity;     // Chuyển: Trừ kho nguồn
    };
}
```

### **validateMovement()**

```java
private void validateMovement(StockMovementType type, int before, int qty, int after) {
    // Xuất kho: Phải đủ hàng
    if (type == EXPORT || type == TRANSFER) {
        if (before < qty) {
            throw new IllegalStateException(
                "Không đủ hàng. Tồn: " + before + ", Yêu cầu: " + qty
            );
        }
    }
    
    // Số lượng sau phải >= 0
    if (after < 0) {
        throw new IllegalStateException("Số lượng sau không được âm");
    }
}
```

## 🎯 TẠI SAO CẦN quantity_before & quantity_after?

### **1. Audit Trail**
```
Biết chính xác số lượng trước/sau mỗi giao dịch
```

### **2. Reconciliation**
```
stock_movements:
| id | type   | qty | before | after |
|----|--------|-----|--------|-------|
| 1  | IMPORT | 100 | 0      | 100   |
| 2  | EXPORT | 30  | 100    | 70    |
| 3  | IMPORT | 50  | 70     | 120   |

inventory_items.quantity_on_hand = 120 ✅ ĐÚNG

Nếu quantity_on_hand = 150:
→ SỐ LIỆU SAI! Phát hiện gian lận hoặc bug!
```

### **3. Rollback**
```
Nếu cần rollback giao dịch #3:
→ Set quantity_on_hand = quantity_before của #3 = 70
```

## 🐛 BUGS ĐÃ FIX - CRITICAL!

### **BUG #15: Transaction không commit**

```java
// ❌ TRƯỚC:
try (Connection conn = ...) {
    conn.setAutoCommit(false);
    try {
        stockMovementRepository.insert(...);
        // ❌ THIẾU commit()
    } finally {
        conn.setAutoCommit(true); // Chỉ reset, KHÔNG commit!
    }
}
// → KẾT QUẢ: Data bị ROLLBACK, không lưu gì cả!

// ✅ SAU:
try (Connection conn = ...) {
    conn.setAutoCommit(false);
    try {
        stockMovementRepository.insert(...);
        inventoryRepository.updateQuantity(...);
        conn.commit(); // ✅ PHẢI CÓ!
    } catch (Exception e) {
        conn.rollback();
        throw e;
    }
}
```

### **BUG #16: Không update inventory**

```java
// ❌ TRƯỚC:
// Chỉ INSERT vào stock_movements
// Không UPDATE inventory_items.quantity_on_hand

// ✅ SAU:
stockMovementRepository.insertWithQuantityTracking(request);
inventoryRepository.updateQuantity(inventoryItemId, quantityAfter); // ← FIX!
```

## 🔍 FILTER - TableRowSorter

```java
protected void applyFilters() {
    sorter.setRowFilter(new RowFilter<>() {
        public boolean include(Entry e) {
            // Multi-column search
            String haystack = (warehouse + productCode + productName + 
                              batchCode + performer + note).toLowerCase();
            
            if (!keyword.isEmpty() && !haystack.contains(keyword)) return false;
            
            // Filter by warehouse
            if (selectedWarehouse != null && !warehouse.equals(selectedWarehouse.name)) 
                return false;
            
            // Filter by movement type
            if (selectedType != null) {
                String typeDisplay = switch (selectedType) {
                    case IMPORT -> "Nhập";
                    case EXPORT -> "Xuất";
                    case ADJUST -> "Điều chỉnh";
                    case TRANSFER -> "Chuyển";
                };
                if (!e.getValue(1).toString().equals(typeDisplay)) return false;
            }
            
            return true;
        }
    });
}
```

## ❓ CÂU HỎI VẤN ĐÁP

### **Q1: IMPORT vs EXPORT vs ADJUST vs TRANSFER?**
**A:**
- **IMPORT:** Nhập kho → `after = before + quantity`
- **EXPORT:** Xuất kho → `after = before - quantity` (check đủ hàng)
- **ADJUST:** Điều chỉnh → `after = quantity` (set absolute value)
- **TRANSFER:** Chuyển kho → Giảm kho nguồn, tăng kho đích

### **Q2: Tại sao cần lưu quantity_before & quantity_after?**
**A:**
1. **Audit:** Biết chính xác số lượng mỗi giao dịch
2. **Reconciliation:** Đối soát với inventory_items
3. **Detect fraud:** Phát hiện gian lận/bug

### **Q3: Transaction hoạt động thế nào?**
**A:**
```
BEGIN TRANSACTION
  → INSERT stock_movements
  → UPDATE inventory_items
  → COMMIT (✅ Thành công)
Hoặc:
  → ROLLBACK (❌ Lỗi → Như chưa làm gì)
```

**Tại sao cần:**
- Đảm bảo data integrity
- All or nothing (không insert 1 nửa)

### **Q4: Xuất kho khi không đủ hàng, hệ thống làm gì?**
**A:**
```java
validateMovement() check:
if (before < quantity) {
    throw new IllegalStateException("Không đủ hàng. Tồn: " + before + ", Yêu cầu: " + quantity);
}
→ Exception → Rollback → Không insert gì
```

### **Q5: Bug CRITICAL bạn đã fix?**
**A:** 
**BUG #15 & #16:**
- **Vấn đề:** Transaction không commit + Không update inventory
- **Hậu quả:** Data không lưu, inventory không cập nhật
- **Fix:** Thêm `conn.commit()` + `inventoryRepository.updateQuantity()`

### **Q6: Demo nhập 100 iPhone vào kho Hà Nội?**
**A:**
```
1. Click "Tạo phiếu nhập (IMPORT)"
2. Nhập:
   - Sản phẩm: iPhone 15
   - Lô: BATCH001
   - Kho: Hà Nội
   - Số lượng: 100
3. Click "Lưu"
4. Kiểm tra:
   - stock_movements: type=IMPORT, qty=100, before=0, after=100
   - inventory_items: quantity_on_hand=100
```

## 📝 CHECKLIST ÔN TẬP

- [ ] So sánh 4 loại giao dịch
- [ ] Giải thích transaction flow
- [ ] Tại sao cần quantity_before/after
- [ ] Validate business rules
- [ ] Bugs #15 & #16 và cách fix
- [ ] Demo scenarios

**🎯 Nắm vững Stock Movement - Module QUAN TRỌNG NHẤT!**
