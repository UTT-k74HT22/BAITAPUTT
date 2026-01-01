package org.example.dto.response;

import java.time.LocalDate;

/**
 * Response DTO cho danh sách tồn kho
 */
public class InventoryResponse {
    private Long id;
    
    // Product info
    private Long productId;
    private String productCode;
    private String productName;
    private String unit;
    
    // Batch info (optional)
    private Long batchId;
    private String batchCode;
    private LocalDate expiryDate;
    
    // Warehouse info
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    
    // Inventory quantities
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer quantityAvailable; // = quantityOnHand - quantityReserved
    
    // Thresholds
    private Integer minThreshold;
    private Integer maxThreshold;
    
    // Status indicators
    private Boolean isLowStock;    // quantityOnHand <= minThreshold
    private Boolean isOverStock;   // quantityOnHand >= maxThreshold
    private Boolean isNearExpiry;  // expiryDate within 3 months
    
    private LocalDate lastStockCheck;

    public InventoryResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getQuantityReserved() {
        return quantityReserved;
    }

    public void setQuantityReserved(Integer quantityReserved) {
        this.quantityReserved = quantityReserved;
    }

    public Integer getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(Integer quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Integer getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(Integer minThreshold) {
        this.minThreshold = minThreshold;
    }

    public Integer getMaxThreshold() {
        return maxThreshold;
    }

    public void setMaxThreshold(Integer maxThreshold) {
        this.maxThreshold = maxThreshold;
    }

    public Boolean getIsLowStock() {
        return isLowStock;
    }

    public void setIsLowStock(Boolean isLowStock) {
        this.isLowStock = isLowStock;
    }

    public Boolean getIsOverStock() {
        return isOverStock;
    }

    public void setIsOverStock(Boolean isOverStock) {
        this.isOverStock = isOverStock;
    }

    public Boolean getIsNearExpiry() {
        return isNearExpiry;
    }

    public void setIsNearExpiry(Boolean isNearExpiry) {
        this.isNearExpiry = isNearExpiry;
    }

    public LocalDate getLastStockCheck() {
        return lastStockCheck;
    }

    public void setLastStockCheck(LocalDate lastStockCheck) {
        this.lastStockCheck = lastStockCheck;
    }
}
