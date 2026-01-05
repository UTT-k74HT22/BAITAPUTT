package org.example.dto.response;

public class InventoryItemInfo {
    private Integer inventoryItemId;
    private Integer productId;
    private Integer warehouseId;
    
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer minThreshold;
    private Integer maxThreshold;

    public InventoryItemInfo() {}

    public InventoryItemInfo(Integer inventoryItemId, Integer productId, Integer warehouseId, Integer quantityOnHand, Integer quantityReserved, Integer minThreshold, Integer maxThreshold) {
        this.inventoryItemId = inventoryItemId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantityOnHand = quantityOnHand;
        this.quantityReserved = quantityReserved;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    public InventoryItemInfo(Integer id, Integer quantityOnHand) {
        this.inventoryItemId = id;
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Integer inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
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
}
