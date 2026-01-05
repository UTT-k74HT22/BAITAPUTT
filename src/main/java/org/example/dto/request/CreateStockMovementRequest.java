package org.example.dto.request;

import org.example.entity.enums.StockMovementType;

/**
 * Request DTO để tạo giao dịch xuất/nhập kho
 */
public class CreateStockMovementRequest {
    
    private StockMovementType movementType;
    
    private Long productId;
    private Long batchId;  // Optional
    private Long warehouseId;
    
    private Integer quantity;
    
    private String referenceType;  // Optional: "IMPORT_ORDER", "SALE_ORDER", "STOCK_CHECK"
    private Long referenceId;      // Optional
    
    private Long performedByUserId;
    private String note;

    private Integer quantityBefore;
    private Integer quantityAfter;



    public CreateStockMovementRequest() {}

    // Getters and Setters
    public StockMovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(StockMovementType movementType) {
        this.movementType = movementType;
    }

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public Long getPerformedByUserId() {
        return performedByUserId;
    }

    public void setPerformedByUserId(Long performedByUserId) {
        this.performedByUserId = performedByUserId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getQuantityBefore() {
        return quantityBefore;
    }

    public void setQuantityBefore(Integer quantityBefore) {
        this.quantityBefore = quantityBefore;
    }

    public Integer getQuantityAfter() {
        return quantityAfter;
    }

    public void setQuantityAfter(Integer quantityAfter) {
        this.quantityAfter = quantityAfter;
    }
}
