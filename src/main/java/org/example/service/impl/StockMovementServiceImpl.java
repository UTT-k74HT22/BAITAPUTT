package org.example.service.impl;

import org.example.config.DatabaseConfig;
import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.entity.enums.StockMovementType;
import org.example.repository.InventoryRepository;
import org.example.repository.StockMovementRepository;
import org.example.repository.impl.InventoryRepositoryImpl;
import org.example.repository.impl.StockMovementRepositoryImpl;
import org.example.service.StockMovementService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                InventoryItemInfo inventoryInfo = getOrCreateInventoryItem(
                    conn,
                    request.getProductId(),
                    request.getBatchId(),
                    request.getWarehouseId()
                );
                
                // 2. Tính toán quantity mới
                int quantityBefore = inventoryInfo.quantityOnHand;
                int quantityAfter = calculateQuantityAfter(
                    request.getMovementType(),
                    quantityBefore,
                    request.getQuantity()
                );
                
                // 3. Validate business rules
                validateMovement(request.getMovementType(), quantityBefore, request.getQuantity(), quantityAfter);
                
                // 4. Cập nhật inventory
                updateInventoryQuantity(conn, inventoryInfo.inventoryItemId, quantityAfter);
                
                // 5. Tạo stock movement record với quantity_before và quantity_after
                insertStockMovement(conn, request, quantityBefore, quantityAfter);
                
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
                    String.format("Không đủ hàng để xuất. Tồn kho: %d, Yêu cầu: %d", quantityBefore, quantity)
                );
            }
        }
        
        // Quantity sau phải >= 0
        if (quantityAfter < 0) {
            throw new IllegalStateException("Quantity sau giao dịch không được âm");
        }
    }

    /**
     * Lấy hoặc tạo mới inventory item
     */
    private InventoryItemInfo getOrCreateInventoryItem(
        Connection conn,
        Long productId,
        Long batchId,
        Long warehouseId
    ) throws SQLException {
        
        // Kiểm tra đã tồn tại chưa
        String selectSql = """
            SELECT id, quantity_on_hand
            FROM inventory_items
            WHERE product_id = ? 
              AND warehouse_id = ?
              AND (batch_id = ? OR (batch_id IS NULL AND ? IS NULL))
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setLong(1, productId);
            ps.setLong(2, warehouseId);
            
            if (batchId != null) {
                ps.setLong(3, batchId);
                ps.setLong(4, batchId);
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Đã tồn tại
                    return new InventoryItemInfo(
                        rs.getLong("id"),
                        rs.getInt("quantity_on_hand")
                    );
                }
            }
        }
        
        // Chưa tồn tại → Tạo mới với quantity = 0
        String insertSql = """
            INSERT INTO inventory_items
            (product_id, batch_id, warehouse_id, quantity_on_hand, quantity_reserved, min_threshold, max_threshold)
            VALUES (?, ?, ?, 0, 0, 10, 1000)
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, productId);
            
            if (batchId != null) {
                ps.setLong(2, batchId);
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            
            ps.setLong(3, warehouseId);
            
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new InventoryItemInfo(rs.getLong(1), 0);
                }
            }
        }
        
        throw new IllegalStateException("Không thể tạo inventory item");
    }

    /**
     * Cập nhật quantity trong inventory
     */
    private void updateInventoryQuantity(Connection conn, Long inventoryItemId, int newQuantity) throws SQLException {
        String sql = """
            UPDATE inventory_items
            SET quantity_on_hand = ?,
                last_stock_check = CURDATE()
            WHERE id = ?
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setLong(2, inventoryItemId);
            ps.executeUpdate();
        }
    }

    /**
     * Insert stock movement record
     */
    private void insertStockMovement(
        Connection conn,
        CreateStockMovementRequest request,
        int quantityBefore,
        int quantityAfter
    ) throws SQLException {
        
        String sql = """
            INSERT INTO stock_movements
            (movement_type, product_id, batch_id, warehouse_id,
             quantity, quantity_before, quantity_after,
             reference_type, reference_id, performed_by_user_id, note, movement_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, request.getMovementType().name());
            ps.setLong(2, request.getProductId());
            
            if (request.getBatchId() != null) {
                ps.setLong(3, request.getBatchId());
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            
            ps.setLong(4, request.getWarehouseId());
            ps.setInt(5, request.getQuantity());
            ps.setInt(6, quantityBefore);
            ps.setInt(7, quantityAfter);
            ps.setString(8, request.getReferenceType());
            
            if (request.getReferenceId() != null) {
                ps.setLong(9, request.getReferenceId());
            } else {
                ps.setNull(9, java.sql.Types.BIGINT);
            }
            
            if (request.getPerformedByUserId() != null) {
                ps.setLong(10, request.getPerformedByUserId());
            } else {
                ps.setNull(10, java.sql.Types.BIGINT);
            }
            
            ps.setString(11, request.getNote());
            
            ps.executeUpdate();
        }
    }

    /**
     * Helper class để lưu thông tin inventory item
     */
    private static class InventoryItemInfo {
        final Long inventoryItemId;
        final int quantityOnHand;
        
        InventoryItemInfo(Long inventoryItemId, int quantityOnHand) {
            this.inventoryItemId = inventoryItemId;
            this.quantityOnHand = quantityOnHand;
        }
    }
}
