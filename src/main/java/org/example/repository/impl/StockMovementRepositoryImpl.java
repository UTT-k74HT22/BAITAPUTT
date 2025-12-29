package org.example.repository.impl;

import org.example.dto.request.CreateStockMovementRequest;
import org.example.dto.response.StockMovementResponse;
import org.example.entity.enums.StockMovementType;
import org.example.repository.BaseRepository;
import org.example.repository.StockMovementRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của StockMovementRepository
 */
public class StockMovementRepositoryImpl extends BaseRepository implements StockMovementRepository {

    @Override
    public long create(CreateStockMovementRequest request) {
        String sql = """
            INSERT INTO stock_movements
            (movement_type, product_id, batch_id, warehouse_id,
             quantity, reference_type, reference_id,
             performed_by_user_id, note, movement_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;
        
        return executeInsertAndReturnId(sql, ps -> {
            ps.setString(1, request.getMovementType().name());
            ps.setLong(2, request.getProductId());
            
            if (request.getBatchId() != null) {
                ps.setLong(3, request.getBatchId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            
            ps.setLong(4, request.getWarehouseId());
            ps.setInt(5, request.getQuantity());
            ps.setString(6, request.getReferenceType());
            
            if (request.getReferenceId() != null) {
                ps.setLong(7, request.getReferenceId());
            } else {
                ps.setNull(7, Types.BIGINT);
            }
            
            if (request.getPerformedByUserId() != null) {
                ps.setLong(8, request.getPerformedByUserId());
            } else {
                ps.setNull(8, Types.BIGINT);
            }
            
            ps.setString(9, request.getNote());
        });
    }

    @Override
    public List<StockMovementResponse> getAll() {
        String sql = """
            SELECT
                sm.id,
                sm.movement_type,
                sm.movement_date,
                sm.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                sm.batch_id,
                b.batch_code,
                sm.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                sm.quantity,
                sm.quantity_before,
                sm.quantity_after,
                sm.reference_type,
                sm.reference_id,
                sm.performed_by_user_id,
                up.code AS performed_by_username,
                up.full_name AS performed_by_full_name,
                sm.note
            FROM stock_movements sm
            INNER JOIN products p ON sm.product_id = p.id
            LEFT JOIN batches b ON sm.batch_id = b.id
            INNER JOIN warehouses w ON sm.warehouse_id = w.id
            LEFT JOIN user_profiles up ON sm.performed_by_user_id = up.id
            ORDER BY sm.movement_date DESC, sm.id DESC
        """;
        
        return queryForList(sql, this::mapToResponse);
    }

    @Override
    public List<StockMovementResponse> getByWarehouse(Long warehouseId) {
        String sql = """
            SELECT
                sm.id,
                sm.movement_type,
                sm.movement_date,
                sm.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                sm.batch_id,
                b.batch_code,
                sm.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                sm.quantity,
                sm.quantity_before,
                sm.quantity_after,
                sm.reference_type,
                sm.reference_id,
                sm.performed_by_user_id,
                up.code AS performed_by_username,
                up.full_name AS performed_by_full_name,
                sm.note
            FROM stock_movements sm
            INNER JOIN products p ON sm.product_id = p.id
            LEFT JOIN batches b ON sm.batch_id = b.id
            INNER JOIN warehouses w ON sm.warehouse_id = w.id
            LEFT JOIN user_profiles up ON sm.performed_by_user_id = up.id
            WHERE sm.warehouse_id = ?
            ORDER BY sm.movement_date DESC
        """;
        
        return queryForList(sql, params(warehouseId), this::mapToResponse);
    }

    @Override
    public List<StockMovementResponse> getByProduct(Long productId) {
        String sql = """
            SELECT
                sm.id,
                sm.movement_type,
                sm.movement_date,
                sm.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                sm.batch_id,
                b.batch_code,
                sm.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                sm.quantity,
                sm.quantity_before,
                sm.quantity_after,
                sm.reference_type,
                sm.reference_id,
                sm.performed_by_user_id,
                up.code AS performed_by_username,
                up.full_name AS performed_by_full_name,
                sm.note
            FROM stock_movements sm
            INNER JOIN products p ON sm.product_id = p.id
            LEFT JOIN batches b ON sm.batch_id = b.id
            INNER JOIN warehouses w ON sm.warehouse_id = w.id
            LEFT JOIN user_profiles up ON sm.performed_by_user_id = up.id
            WHERE sm.product_id = ?
            ORDER BY sm.movement_date DESC
        """;
        
        return queryForList(sql, params(productId), this::mapToResponse);
    }

    @Override
    public List<StockMovementResponse> getByMovementType(StockMovementType movementType) {
        String sql = """
            SELECT
                sm.id,
                sm.movement_type,
                sm.movement_date,
                sm.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                sm.batch_id,
                b.batch_code,
                sm.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                sm.quantity,
                sm.quantity_before,
                sm.quantity_after,
                sm.reference_type,
                sm.reference_id,
                sm.performed_by_user_id,
                up.code AS performed_by_username,
                up.full_name AS performed_by_full_name,
                sm.note
            FROM stock_movements sm
            INNER JOIN products p ON sm.product_id = p.id
            LEFT JOIN batches b ON sm.batch_id = b.id
            INNER JOIN warehouses w ON sm.warehouse_id = w.id
            LEFT JOIN user_profiles up ON sm.performed_by_user_id = up.id
            WHERE sm.movement_type = ?
            ORDER BY sm.movement_date DESC
        """;
        
        return queryForList(sql, params(movementType.name()), this::mapToResponse);
    }

    @Override
    public List<StockMovementResponse> getByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        String sql = """
            SELECT
                sm.id,
                sm.movement_type,
                sm.movement_date,
                sm.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                sm.batch_id,
                b.batch_code,
                sm.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                sm.quantity,
                sm.quantity_before,
                sm.quantity_after,
                sm.reference_type,
                sm.reference_id,
                sm.performed_by_user_id,
                up.code AS performed_by_username,
                up.full_name AS performed_by_full_name,
                sm.note
            FROM stock_movements sm
            INNER JOIN products p ON sm.product_id = p.id
            LEFT JOIN batches b ON sm.batch_id = b.id
            INNER JOIN warehouses w ON sm.warehouse_id = w.id
            LEFT JOIN user_profiles up ON sm.performed_by_user_id = up.id
            WHERE sm.movement_date BETWEEN ? AND ?
            ORDER BY sm.movement_date DESC
        """;
        
        return queryForList(sql, params(fromDate, toDate), this::mapToResponse);
    }

    @Override
    public Optional<StockMovementResponse> findById(Long id) {
        String sql = """
            SELECT
                sm.id,
                sm.movement_type,
                sm.movement_date,
                sm.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                sm.batch_id,
                b.batch_code,
                sm.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                sm.quantity,
                sm.quantity_before,
                sm.quantity_after,
                sm.reference_type,
                sm.reference_id,
                sm.performed_by_user_id,
                up.code AS performed_by_username,
                up.full_name AS performed_by_full_name,
                sm.note
            FROM stock_movements sm
            INNER JOIN products p ON sm.product_id = p.id
            LEFT JOIN batches b ON sm.batch_id = b.id
            INNER JOIN warehouses w ON sm.warehouse_id = w.id
            LEFT JOIN user_profiles up ON sm.performed_by_user_id = up.id
            WHERE sm.id = ?
        """;
        
        return queryForObject(sql, params(id), this::mapToResponse);
    }

    @Override
    public List<StockMovementResponse> getHistoryByProductAndWarehouse(Long productId, Long warehouseId) {
        String sql = """
            SELECT
                sm.id,
                sm.movement_type,
                sm.movement_date,
                sm.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                sm.batch_id,
                b.batch_code,
                sm.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                sm.quantity,
                sm.quantity_before,
                sm.quantity_after,
                sm.reference_type,
                sm.reference_id,
                sm.performed_by_user_id,
                up.code AS performed_by_username,
                up.full_name AS performed_by_full_name,
                sm.note
            FROM stock_movements sm
            INNER JOIN products p ON sm.product_id = p.id
            LEFT JOIN batches b ON sm.batch_id = b.id
            INNER JOIN warehouses w ON sm.warehouse_id = w.id
            LEFT JOIN user_profiles up ON sm.performed_by_user_id = up.id
            WHERE sm.product_id = ? AND sm.warehouse_id = ?
            ORDER BY sm.movement_date DESC
        """;
        
        return queryForList(sql, params(productId, warehouseId), this::mapToResponse);
    }

    /**
     * Map ResultSet to StockMovementResponse
     */
    private StockMovementResponse mapToResponse(ResultSet rs) throws SQLException {
        StockMovementResponse response = new StockMovementResponse();
        
        response.setId(rs.getLong("id"));
        
        // Movement info
        response.setMovementType(StockMovementType.valueOf(rs.getString("movement_type")));
        response.setMovementDate(rs.getObject("movement_date", LocalDateTime.class));
        
        // Product info
        response.setProductId(rs.getLong("product_id"));
        response.setProductCode(rs.getString("product_code"));
        response.setProductName(rs.getString("product_name"));
        response.setUnit(rs.getString("unit"));
        
        // Batch info
        response.setBatchId(rs.getObject("batch_id", Long.class));
        response.setBatchCode(rs.getString("batch_code"));
        
        // Warehouse info
        response.setWarehouseId(rs.getLong("warehouse_id"));
        response.setWarehouseCode(rs.getString("warehouse_code"));
        response.setWarehouseName(rs.getString("warehouse_name"));
        
        // Quantities
        response.setQuantity(rs.getInt("quantity"));
        response.setQuantityBefore(rs.getObject("quantity_before", Integer.class));
        response.setQuantityAfter(rs.getObject("quantity_after", Integer.class));
        
        // Reference
        response.setReferenceType(rs.getString("reference_type"));
        response.setReferenceId(rs.getObject("reference_id", Long.class));
        
        // Performed by
        response.setPerformedByUserId(rs.getObject("performed_by_user_id", Long.class));
        response.setPerformedByUsername(rs.getString("performed_by_username"));
        response.setPerformedByFullName(rs.getString("performed_by_full_name"));
        
        response.setNote(rs.getString("note"));
        
        return response;
    }
}
