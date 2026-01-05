package org.example.controller;

import org.example.dto.request.UpdateInventoryThresholdRequest;
import org.example.dto.response.InventoryResponse;
import org.example.service.InventoryService;
import java.util.List;

/**
 * Controller xử lý nghiệp vụ tồn kho
 */
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Lấy tất cả tồn kho
     */
    public List<InventoryResponse> getAll() {
        return inventoryService.getAll();
    }

    /**
     * Lấy tồn kho theo warehouse
     */
    public List<InventoryResponse> getByWarehouse(Long warehouseId) {
        return inventoryService.getByWarehouse(warehouseId);
    }

    /**
     * Lấy tồn kho theo product
     */
    public List<InventoryResponse> getByProduct(Long productId) {
        return inventoryService.getByProduct(productId);
    }

    /**
     * Lấy danh sách sắp hết hàng
     */
    public List<InventoryResponse> getLowStockItems() {
        return inventoryService.getLowStockItems();
    }

    /**
     * Lấy danh sách sắp hết hạn
     */
    public List<InventoryResponse> getNearExpiryItems() {
        return inventoryService.getNearExpiryItems();
    }

    /**
     * Cập nhật ngưỡng min/max
     */
    public void updateThresholds(Long inventoryItemId, UpdateInventoryThresholdRequest request) {
        inventoryService.updateThresholds(inventoryItemId, request);
    }

    /**
     * Lấy tổng tồn kho của product (tất cả kho)
     */
    public Integer getTotalQuantityByProduct(Long productId) {
        return inventoryService.getTotalQuantityByProduct(productId);
    }

    /**
     * Kiểm tra còn hàng theo product + warehouse
     */
    public boolean hasStock(Long productId, Long warehouseId, Integer requiredQuantity) {
        return inventoryService.hasStock(productId, warehouseId, requiredQuantity);
    }
    /**
     * Cập nhật số lượng tồn kho theo product + batch + warehouse
     */
    public void updateStock(Long productId, Long batchId, Long warehouseId, Integer newQuantity) {
        inventoryService.updateStock(productId, batchId, warehouseId, newQuantity);
    }

    /**
     * Ghi log lịch sử nhập/xuất kho
     */
    public void insertStockMovement(Long productId, Long batchId, Long warehouseId,
                                    Integer quantity, Integer before, Integer after,
                                    Long performedByUserId, String note, String movementType) {
        inventoryService.insertStockMovement(productId, batchId, warehouseId,
                quantity, before, after, performedByUserId, note, movementType);
    }
    /**
     * Lấy số lượng tồn kho hiện tại
     */
    public Integer getCurrentQuantity(Long productId, Long batchId, Long warehouseId) {
        int soLuong = inventoryService.getCurrentQuantity(productId, batchId, warehouseId);
        System.out.println("So luong ton kho hien tai: " + soLuong);
        return soLuong;
    }
}
