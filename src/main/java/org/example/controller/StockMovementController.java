package org.example.controller;

import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.entity.enums.StockMovementType;
import org.example.service.StockMovementService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller xử lý nghiệp vụ xuất nhập kho
 */
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    /**
     * Tạo giao dịch xuất/nhập kho
     */
    public void createMovement(CreateStockMovementRequest request) {
        stockMovementService.createMovement(request);
    }

    /**
     * Lấy tất cả giao dịch
     */
    public List<StockMovementResponse> getAll() {
        return stockMovementService.getAll();
    }

    /**
     * Lấy giao dịch theo warehouse
     */
    public List<StockMovementResponse> getByWarehouse(Long warehouseId) {
        return stockMovementService.getByWarehouse(warehouseId);
    }

    /**
     * Lấy giao dịch theo product
     */
    public List<StockMovementResponse> getByProduct(Long productId) {
        return stockMovementService.getByProduct(productId);
    }

    /**
     * Lấy giao dịch theo loại
     */
    public List<StockMovementResponse> getByMovementType(StockMovementType movementType) {
        return stockMovementService.getByMovementType(movementType);
    }

    /**
     * Lấy giao dịch trong khoảng thời gian
     */
    public List<StockMovementResponse> getByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        return stockMovementService.getByDateRange(fromDate, toDate);
    }

    /**
     * Lấy lịch sử xuất nhập của sản phẩm tại kho
     */
    public List<StockMovementResponse> getHistoryByProductAndWarehouse(Long productId, Long warehouseId) {
        return stockMovementService.getHistoryByProductAndWarehouse(productId, warehouseId);
    }
}
