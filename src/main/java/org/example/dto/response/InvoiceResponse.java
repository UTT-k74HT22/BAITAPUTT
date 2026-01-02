package org.example.dto.response;
import org.example.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.entity.enums.InvoiceStatus;


public class InvoiceResponse {
    private Integer id;
    private String invoiceNumber;
    private Integer orderId;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private InvoiceStatus status;
    private String taxCode;

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getInvoiceNumber() {return invoiceNumber;}
    public void setInvoiceNumber(String invoiceNumber) {this.invoiceNumber = invoiceNumber;}
    public Integer getOrderId() {return orderId;}
    public void setOrderId(Integer orderId) {this.orderId = orderId;}
    public LocalDateTime getIssueDate() {return issueDate;}
    public void setIssueDate(LocalDateTime issueDate) {this.issueDate = issueDate;}
    public LocalDateTime getDueDate() {return dueDate;}
    public void setDueDate(LocalDateTime dueDate) {this.dueDate = dueDate;}
    public BigDecimal getTotalAmount() {return totalAmount;}
    public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}
    public BigDecimal getPaidAmount() {return paidAmount;}
    public void setPaidAmount(BigDecimal paidAmount) {this.paidAmount = paidAmount;}
    public InvoiceStatus getStatus() {return status;}
    public void setStatus(InvoiceStatus status) {this.status = status;}
    public String getTaxCode() {return taxCode;}
    public void setTaxCode(String taxCode) {this.taxCode = taxCode;}

}
