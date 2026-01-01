package org.example.dto.request.Order;

import java.math.BigDecimal;

public class OrderItemRequest {
    /**
     * ID sản phẩm
     * */
    private Long productId;
    /**
     * ID lô hàng
     * */
    private Long batchId;
    /**
     * Số lượng
     * */
    private int quantity;
    /**
     * Giá
     * */
    private BigDecimal unitPrice;
    /**
     * Ghi chú
     * */
    private String note;

    public Long getBatchId() {
        return batchId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
