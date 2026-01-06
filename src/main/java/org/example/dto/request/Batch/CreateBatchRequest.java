package org.example.dto.request.Batch;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO dùng để tạo mới Batch (Lô hàng).
 */
public class CreateBatchRequest {

    private String batchCode;
    private Long productId;

    private BigDecimal importPrice;

    private LocalDate manufactureDate;
    private LocalDate expiryDate;

    private String supplierName;

    /**
     * ACTIVE | EXPIRED | BLOCKED | DEPLETED
     */
    private String status;



    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
