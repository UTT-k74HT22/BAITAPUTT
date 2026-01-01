package org.example.dto.response;
import java.util.Date;



import java.util.Date;

/**
 * DTO để hiển thị chi tiết báo cáo tồn kho + xuất nhập
 * Mỗi row đại diện cho 1 inventory item và 1 movement (nếu có)
 */
public class ReportDetailResponse {
    private int inventoryId;       // ID bản ghi inventory
    private String productCode;    // Mã sản phẩm
    private String productName;    // Tên sản phẩm
    private String batchCode;      // Mã lô, có thể null nếu chưa có batch
    private String warehouseName;  // Tên kho chứa hàng
    private int quantityOnHand;    // Số lượng hiện có
    private int quantityReserved;  // Số lượng đã đặt nhưng chưa xuất
    private int minThreshold;      // Ngưỡng tối thiểu
    private int maxThreshold;      // Ngưỡng tối đa
    private String movementType;   // Loại giao dịch: IMPORT/EXPORT/ADJUST/TRANSFER
    private int movementQuantity;  // Số lượng trong giao dịch
    private Date movementDate;     // Ngày giao dịch

    // ===== Getters & Setters =====
    // Bên dưới là tất cả các getter và setter
    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public int getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(int quantityOnHand) { this.quantityOnHand = quantityOnHand; }
    public int getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(int quantityReserved) { this.quantityReserved = quantityReserved; }
    public int getMinThreshold() { return minThreshold; }
    public void setMinThreshold(int minThreshold) { this.minThreshold = minThreshold; }
    public int getMaxThreshold() { return maxThreshold; }
    public void setMaxThreshold(int maxThreshold) { this.maxThreshold = maxThreshold; }
    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public int getMovementQuantity() { return movementQuantity; }
    public void setMovementQuantity(int movementQuantity) { this.movementQuantity = movementQuantity; }
    public Date getMovementDate() { return movementDate; }
    public void setMovementDate(Date movementDate) { this.movementDate = movementDate; }
}
