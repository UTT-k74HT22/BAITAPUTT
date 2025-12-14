package org.example.entity;

import java.time.LocalDate;

public class InventoryItem extends BaseEntity {

    private Long productId;
    private Long batchId;
    private Long warehouseId;

    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer minThreshold;
    private Integer maxThreshold;

    private LocalDate lastStockCheck;

    public InventoryItem() {}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
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

    public LocalDate getLastStockCheck() {
        return lastStockCheck;
    }

    public void setLastStockCheck(LocalDate lastStockCheck) {
        this.lastStockCheck = lastStockCheck;
    }
}
