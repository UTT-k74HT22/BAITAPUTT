package org.example.dto.request;

/**
 * Request DTO để cập nhật ngưỡng tồn kho
 */
public class UpdateInventoryThresholdRequest {
    
    private Integer minThreshold;
    private Integer maxThreshold;

    public UpdateInventoryThresholdRequest() {}

    public Integer getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(Integer minThreshold) {
        this.minThreshold = minThreshold;
    }

    public Integer getMaxThreshold() {
        return maxThreshold;
    }

    public void setMaxThreshold(Integer maxThreshold) {
        this.maxThreshold = maxThreshold;
    }
}
