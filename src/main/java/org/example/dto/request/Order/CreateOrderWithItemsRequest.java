package org.example.dto.request.Order;

import java.math.BigDecimal;
import java.util.List;

public class CreateOrderWithItemsRequest {
    /**
     * ID khách hàng
     */
    private Long customerId;
    /**
     * Địa chỉ giao hàng
     */
    private String shippingAddress;
    /**
     * Ghi chú
     * */
    private String note;
    /**
     *
     * Giảm giá
     * */
    private BigDecimal discount;
    /**
     *
     * Danh sách sản phẩm
     * */
    private List<OrderItemRequest> items;

    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getShippingAddress() {
        return shippingAddress;
    }
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public BigDecimal getDiscount() {
        return discount;
    }
    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}
