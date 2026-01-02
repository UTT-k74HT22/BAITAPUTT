package org.example.dto.response.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    /**
     * ID đơn hàng
     * Số đơn hàng
     * Trạng thái
     * Tổng tiền
     * Ngày tạo
     * Ghi chú
     * Tên khách hàng
     * SĐT
     * Email
     * ID khách hàng
     * Địa chỉ giao hàng
     * Tên người tạo
     * Email người tạo
     * SĐT người tạo
     * */
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String note;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Long customerId;
    private String shippingAddress;
    private String creatorName;
    private String creatorEmail;
    private String creatorPhone;


    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorPhone() {
        return creatorPhone;
    }

    public void setCreatorPhone(String creatorPhone) {
        this.creatorPhone = creatorPhone;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Long getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
}
