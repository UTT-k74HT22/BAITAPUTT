package org.example.service.impl;

import org.example.dto.request.UpdateInventoryThresholdRequest;
import org.example.dto.response.InventoryResponse;
import org.example.repository.InventoryRepository;
import org.example.repository.impl.InventoryRepositoryImpl;
import org.example.service.InventoryService;

import java.util.List;

/**
 * Service implementation cho quản lý tồn kho
 */
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository = new InventoryRepositoryImpl();

    @Override
    public List<InventoryResponse> getAll() {
        return inventoryRepository.getAll();
    }

    @Override
    public List<InventoryResponse> getByWarehouse(Long warehouseId) {
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID không được để trống");
        }
        return inventoryRepository.getByWarehouse(warehouseId);
    }

    @Override
    public List<InventoryResponse> getByProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID không được để trống");
        }
        return inventoryRepository.getByProduct(productId);
    }

    @Override
    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.getLowStockItems();
    }

    @Override
    public List<InventoryResponse> getNearExpiryItems() {
        return inventoryRepository.getNearExpiryItems();
    }

    @Override
    public void updateThresholds(Long inventoryItemId, UpdateInventoryThresholdRequest request) {
        if (inventoryItemId == null) {
            throw new IllegalArgumentException("Inventory Item ID không được để trống");
        }
        
        if (request.getMinThreshold() == null || request.getMaxThreshold() == null) {
            throw new IllegalArgumentException("Min threshold và Max threshold không được để trống");
        }
        
        if (request.getMinThreshold() < 0) {
            throw new IllegalArgumentException("Min threshold phải >= 0");
        }
        
        if (request.getMaxThreshold() <= request.getMinThreshold()) {
            throw new IllegalArgumentException("Max threshold phải lớn hơn Min threshold");
        }
        
        inventoryRepository.updateThresholds(inventoryItemId, request);
    }

    @Override
    public Integer getTotalQuantityByProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID không được để trống");
        }
        return inventoryRepository.getTotalQuantityByProduct(productId);
    }

    @Override
    public boolean hasStock(Long productId, Long warehouseId, Integer requiredQuantity) {
        if (productId == null || warehouseId == null) {
            throw new IllegalArgumentException("Product ID và Warehouse ID không được để trống");
        }
        
        if (requiredQuantity == null || requiredQuantity <= 0) {
            throw new IllegalArgumentException("Required quantity phải > 0");
        }
        
        return inventoryRepository.hasStock(productId, warehouseId, requiredQuantity);
    }
    /**
     * Cập nhật số lượng tồn kho
     * */
    @Override
    public void updateStock(Long productId, Long batchId, Long warehouseId, Integer newQuantity) {
        inventoryRepository.updateStock(productId, batchId, warehouseId, newQuantity);
    }
    /**
     * Thêm giao dịch tồn kho
     * */
    @Override
    public void insertStockMovement(Long productId, Long batchId, Long warehouseId,
                                    Integer quantity, Integer before, Integer after,
                                    Long userId, String note, String movementType) {
        inventoryRepository.insertStockMovement(productId, batchId, warehouseId,
                quantity, before, after, userId, note, movementType);
    }

    @Override
    public Integer getCurrentQuantity(Long productId, Long batchId, Long warehouseId) {
        return inventoryRepository.getCurrentQuantity(productId, batchId, warehouseId);
    }
}
