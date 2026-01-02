package org.example.repository;

import org.example.dto.request.UpdateInventoryThresholdRequest;
import org.example.dto.response.InventoryResponse;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Inventory operations
 */
public interface InventoryRepository {
    
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
     * Lấy tồn kho cụ thể theo product + warehouse (+ batch optional)
     */
    Optional<InventoryResponse> findByProductAndWarehouse(Long productId, Long warehouseId, Long batchId);
    
    /**
     * Lấy danh sách sắp hết hàng (low stock)
     */
    List<InventoryResponse> getLowStockItems();
    
    /**
     * Lấy danh sách sắp hết hạn (near expiry)
     */
    List<InventoryResponse> getNearExpiryItems();
    
    /**
     * Cập nhật ngưỡng min/max cho inventory item
     */
    void updateThresholds(Long inventoryItemId, UpdateInventoryThresholdRequest request);
    
    /**
     * Lấy tổng số lượng tồn của product (tất cả kho)
     */
    Integer getTotalQuantityByProduct(Long productId);
    
    /**
     * Kiểm tra còn hàng hay không
     */
    boolean hasStock(Long productId, Long warehouseId, Integer requiredQuantity);
    
    /**
     * Lấy hoặc tạo mới inventory item (dùng trong transaction)
     * Trả về ID và quantity hiện tại
     */
    InventoryItemInfo getOrCreateInventoryItem(Long productId, Long batchId, Long warehouseId);
    
    /**
     * Cập nhật số lượng tồn kho (dùng trong transaction)
     */
    void updateQuantity(Long inventoryItemId, Integer newQuantity);
    
    /**
     * Lấy số lượng hiện tại của inventory item
     */
    Integer getCurrentQuantity(Long productId, Long batchId, Long warehouseId);
    
    /**
     * DTO để trả về thông tin inventory item cơ bản
     */
    record InventoryItemInfo(Long inventoryItemId, Integer quantityOnHand) {}

    void updateStock(Long productId, Long batchId, Long warehouseId, Integer newQuantity);

    void insertStockMovement(Long productId, Long batchId, Long warehouseId,
                             Integer quantity, Integer before, Integer after,
                             Long userId, String note);
}
