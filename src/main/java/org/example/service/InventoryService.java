package org.example.service;

import org.example.dto.request.UpdateInventoryThresholdRequest;
import org.example.dto.response.InventoryResponse;

import java.util.List;

/**
 * Service interface cho quản lý tồn kho
 */
public interface InventoryService {
    
    /**
     * Lấy tất cả tồn kho
     */
    List<InventoryResponse> getAll();
    
    /**
     * Lấy tồn kho theo warehouse
     */
    List<InventoryResponse> getByWarehouse(Long warehouseId);
    
    /**
     * Lấy tồn kho theo product
     */
    List<InventoryResponse> getByProduct(Long productId);
    
    /**
     * Lấy danh sách sắp hết hàng
     */
    List<InventoryResponse> getLowStockItems();
    
    /**
     * Lấy danh sách sắp hết hạn
     */
    List<InventoryResponse> getNearExpiryItems();
    
    /**
     * Cập nhật ngưỡng min/max
     */
    void updateThresholds(Long inventoryItemId, UpdateInventoryThresholdRequest request);
    
    /**
     * Lấy tổng tồn kho của product
     */
    Integer getTotalQuantityByProduct(Long productId);
    
    /**
     * Kiểm tra còn hàng
     */
    boolean hasStock(Long productId, Long warehouseId, Integer requiredQuantity);
}
