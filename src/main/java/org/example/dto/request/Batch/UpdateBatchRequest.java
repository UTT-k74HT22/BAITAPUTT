package org.example.dto.request.Batch;

import java.time.LocalDate;

/**
 * DTO dùng để cập nhật Batch.
 * Chỉ cho phép sửa:
 * - expiryDate
 * - status
 */
public class UpdateBatchRequest {

    private LocalDate expiryDate;
    /**
     * ACTIVE | EXPIRED | BLOCKED | DEPLETED
     */
    private String status;



    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
