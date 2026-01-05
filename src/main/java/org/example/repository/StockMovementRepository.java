package org.example.repository;

import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.entity.enums.StockMovementType;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Stock Movement operations
 */
public interface StockMovementRepository {
    
    /**
     * Tạo giao dịch xuất/nhập kho
     * @return ID của giao dịch
     */
    long create(CreateStockMovementRequest request);
    
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
     * Lấy giao dịch theo loại (IMPORT/EXPORT/ADJUST/TRANSFER)
     */
    List<StockMovementResponse> getByMovementType(StockMovementType movementType);
    
    /**
     * Lấy giao dịch trong khoảng thời gian
     */
    List<StockMovementResponse> getByDateRange(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * Lấy giao dịch theo ID
     */
    Optional<StockMovementResponse> findById(Long id);
    
    /**
     * Lấy lịch sử xuất nhập của một sản phẩm tại một kho
     */
    List<StockMovementResponse> getHistoryByProductAndWarehouse(Long productId, Long warehouseId);
    
    /**
     * Tạo giao dịch xuất/nhập kho với tracking quantity before/after (dùng trong transaction)
     * @return ID của giao dịch
     */
    long insertWithQuantityTracking(CreateStockMovementRequest request) throws SQLException;
}
