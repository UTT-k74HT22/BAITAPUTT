package org.example.dto.response;

import org.example.entity.enums.ProductStatus;

import java.math.BigDecimal;

public class ProductResponse {

    private Long id;
    private String code;
    private String name;

    private String categoryName;
    private String manufacturerName;

    private BigDecimal standardPrice;
    private boolean requiresPrescription;

    private ProductStatus status;

    // =========================
    // Constructors
    // =========================

    public ProductResponse() {
    }

    public ProductResponse(Long id,
                           String code,
                           String name,
                           String categoryName,
                           String manufacturerName,
                           BigDecimal standardPrice,
                           boolean requiresPrescription,
                           ProductStatus status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.categoryName = categoryName;
        this.manufacturerName = manufacturerName;
        this.standardPrice = standardPrice;
        this.requiresPrescription = requiresPrescription;
        this.status = status;
    }

    // =========================
    // Getter / Setter
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public BigDecimal getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(BigDecimal standardPrice) {
        this.standardPrice = standardPrice;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public void setRequiresPrescription(boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
