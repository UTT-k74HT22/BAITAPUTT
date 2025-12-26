package org.example.dto.request.Product;

import org.example.entity.enums.ProductStatus;

import java.math.BigDecimal;

/**
 * DTO dùng để cập nhật thông tin sản phẩm.
 */
public class UpdateProductRequest {

    /** ID danh mục (categories.id) */
    private Long categoryId;

    /** ID nhà sản xuất (manufacturers.id), có thể null */
    private Long manufacturerId;

    /** Mã vạch */
    private String barcode;

    /** Tên sản phẩm */
    private String name;

    /** Dạng bào chế */
    private String dosageForm;

    /** Đơn vị tính */
    private String unit;

    /** Mô tả */
    private String description;

    /** Giá chuẩn */
    private BigDecimal standardPrice;

    /** Có cần toa thuốc không */
    private boolean requiresPrescription;

    /** Trạng thái sản phẩm */
    private ProductStatus status;

    // =========================
    // Getter / Setter
    // =========================

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
