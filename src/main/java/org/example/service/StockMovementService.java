package org.example.service;

import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.entity.enums.StockMovementType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface cho quản lý xuất nhập kho
 */
public interface StockMovementService {
    
    /**
     * Tạo giao dịch xuất/nhập kho
     * Tự động cập nhật inventory và ghi lại quantity_before/after
     */
    void createMovement(CreateStockMovementRequest request);
    
    /**
     * Lấy tất cả giao dịch
     */
    List<StockMovementResponse> getAll();
    
    /**
     * Lấy giao dịch theo warehouse
     */
    List<StockMovementResponse> getByWarehouse(Long warehouseId);
    
    /**
     * Lấy giao dịch theo product
     */
    List<StockMovementResponse> getByProduct(Long productId);
    
    /**
     * Lấy giao dịch theo loại
     */
    List<StockMovementResponse> getByMovementType(StockMovementType movementType);
    
    /**
     * Lấy giao dịch trong khoảng thời gian
     */
    List<StockMovementResponse> getByDateRange(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * Lấy lịch sử xuất nhập của sản phẩm tại kho
     */
    List<StockMovementResponse> getHistoryByProductAndWarehouse(Long productId, Long warehouseId);
}
