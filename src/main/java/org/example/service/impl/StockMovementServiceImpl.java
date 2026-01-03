package org.example.service.impl;

import org.example.config.DatabaseConfig;
import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.entity.enums.StockMovementType;
import org.example.repository.InventoryRepository;
import org.example.repository.InventoryRepository.InventoryItemInfo;
import org.example.repository.StockMovementRepository;
import org.example.repository.impl.InventoryRepositoryImpl;
import org.example.repository.impl.StockMovementRepositoryImpl;
import org.example.service.StockMovementService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation cho quản lý xuất nhập kho
 * 
 * BUSINESS LOGIC QUAN TRỌNG:
 * - Khi IMPORT: Tăng quantity_on_hand
 * - Khi EXPORT: Giảm quantity_on_hand (phải check còn hàng)
 * - Khi ADJUST: Điều chỉnh quantity_on_hand (có thể + hoặc -)
 * - Tự động ghi lại quantity_before và quantity_after
 * - Tất cả trong Transaction để đảm bảo data integrity
 */
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository = new StockMovementRepositoryImpl();
    private final InventoryRepository inventoryRepository = new InventoryRepositoryImpl();

    @Override
    public void createMovement(CreateStockMovementRequest request) {
        validateRequest(request);
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // 1. Lấy thông tin inventory hiện tại
                InventoryItemInfo inventoryInfo = inventoryRepository.getOrCreateInventoryItem(
                    request.getProductId(),
                    request.getBatchId(),
                    request.getWarehouseId()
                );
                
                // 2. Tính toán quantity mới
                int quantityBefore = inventoryInfo.quantityOnHand();
                int quantityAfter = calculateQuantityAfter(
                    request.getMovementType(),
                    quantityBefore,
                    request.getQuantity()
                );
                
                // 3. Validate business rules
                validateMovement(request.getMovementType(), quantityBefore, request.getQuantity(), quantityAfter);
                
                // 4. Cập nhật inventory
                inventoryRepository.updateQuantity(inventoryInfo.inventoryItemId(), quantityAfter);
                
                // 5. Tạo stock movement record với quantity_before và quantity_after
                stockMovementRepository.insertWithQuantityTracking(request, quantityBefore, quantityAfter);
                
                conn.commit();
                
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Lỗi khi tạo giao dịch xuất nhập kho: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kết nối database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StockMovementResponse> getAll() {
        return stockMovementRepository.getAll();
    }

    @Override
    public List<StockMovementResponse> getByWarehouse(Long warehouseId) {
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID không được để trống");
        }
        return stockMovementRepository.getByWarehouse(warehouseId);
    }

    @Override
    public List<StockMovementResponse> getByProduct(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID không được để trống");
        }
        return stockMovementRepository.getByProduct(productId);
    }

    @Override
    public List<StockMovementResponse> getByMovementType(StockMovementType movementType) {
        if (movementType == null) {
            throw new IllegalArgumentException("Movement Type không được để trống");
        }
        return stockMovementRepository.getByMovementType(movementType);
    }

    @Override
    public List<StockMovementResponse> getByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        if (fromDate == null || toDate == null) {
            throw new IllegalArgumentException("From Date và To Date không được để trống");
        }
        
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From Date phải trước To Date");
        }
        
        return stockMovementRepository.getByDateRange(fromDate, toDate);
    }

    @Override
    public List<StockMovementResponse> getHistoryByProductAndWarehouse(Long productId, Long warehouseId) {
        if (productId == null || warehouseId == null) {
            throw new IllegalArgumentException("Product ID và Warehouse ID không được để trống");
        }
        return stockMovementRepository.getHistoryByProductAndWarehouse(productId, warehouseId);
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Validate request
     */
    private void validateRequest(CreateStockMovementRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request không được null");
        }
        
        if (request.getMovementType() == null) {
            throw new IllegalArgumentException("Movement Type không được để trống");
        }
        
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID không được để trống");
        }
        
        if (request.getWarehouseId() == null) {
            throw new IllegalArgumentException("Warehouse ID không được để trống");
        }
        
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity phải > 0");
        }
    }

    /**
     * Tính toán quantity sau khi thực hiện giao dịch
     */
    private int calculateQuantityAfter(StockMovementType movementType, int quantityBefore, int quantity) {
        return switch (movementType) {
            case IMPORT -> quantityBefore + quantity;
            case EXPORT -> quantityBefore - quantity;
            case ADJUST -> quantity; // Adjust là set absolute value
            case TRANSFER -> quantityBefore - quantity; // Transfer OUT
        };
    }

    /**
     * Validate business rules
     */
    private void validateMovement(StockMovementType movementType, int quantityBefore, int quantity, int quantityAfter) {
        // Xuất kho: phải có đủ hàng
        if (movementType == StockMovementType.EXPORT || movementType == StockMovementType.TRANSFER) {
            if (quantityBefore < quantity) {
                throw new IllegalStateException(
                    String.format("Không đủ hàng để xuất. Tồn kho: %d, Yêu cầu xuất: %d", quantityBefore, quantity)
                );
            }
        }

        // ADJUST: quantity là giá trị tuyệt đối mới (phải >= 0)
        if (movementType == StockMovementType.ADJUST) {
            if (quantity < 0) {
                throw new IllegalArgumentException("Số lượng điều chỉnh không được âm");
            }
        }

        // Quantity sau phải >= 0
        if (quantityAfter < 0) {
            throw new IllegalStateException("Số lượng sau giao dịch không được âm");
        }
    }
}
