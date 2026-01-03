package org.example.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateInvoiceRequest {
    private Integer orderId;
    private LocalDate dueDate;
    private BigDecimal totalAmount;


    public Integer getOrderId() {return orderId;}
    public void setOrderId(Integer orderId) {this.orderId = orderId;}
    public LocalDate getDueDate() {return dueDate;}
    public void setDueDate(LocalDate dueDate) {this.dueDate = dueDate;}
    public BigDecimal getTotalAmount() {return totalAmount;}
    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}


    }

