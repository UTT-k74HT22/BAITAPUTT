package org.example.repository.impl;

import org.example.dto.request.UpdateInventoryThresholdRequest;
import org.example.dto.response.InventoryItemInfo;
import org.example.dto.response.InventoryResponse;
import org.example.repository.BaseRepository;
import org.example.repository.InventoryRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của InventoryRepository
 */
public class InventoryRepositoryImpl extends BaseRepository implements InventoryRepository {

    /**
     * is_low_stock: 1 nếu quantity_on_hand <= min_threshold => sắp hết han
     * @return
     */
    @Override
    public List<InventoryResponse> getAll() {
        String sql = """
            SELECT
                ii.id,
                ii.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                ii.batch_id,
                b.batch_code,
                b.expiry_date,
                ii.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                ii.quantity_on_hand,
                ii.quantity_reserved,
                (ii.quantity_on_hand - ii.quantity_reserved) AS quantity_available,
                ii.min_threshold,
                ii.max_threshold,
                ii.last_stock_check,
                CASE WHEN ii.quantity_on_hand <= ii.min_threshold THEN 1 ELSE 0 END AS is_low_stock, 
                CASE WHEN ii.quantity_on_hand >= ii.max_threshold THEN 1 ELSE 0 END AS is_over_stock,
                CASE WHEN b.expiry_date IS NOT NULL AND DATEDIFF(b.expiry_date, CURDATE()) <= 90 THEN 1 ELSE 0 END AS is_near_expiry
            FROM inventory_items ii
            INNER JOIN products p ON ii.product_id = p.id
            LEFT JOIN batches b ON ii.batch_id = b.id
            INNER JOIN warehouses w ON ii.warehouse_id = w.id
            ORDER BY ii.id DESC
        """;

        return queryForList(sql, this::mapToResponse);
    }

    @Override
    public List<InventoryResponse> getByWarehouse(Long warehouseId) {
        String sql = """
            SELECT
                ii.id,
                ii.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                ii.batch_id,
                b.batch_code,
                b.expiry_date,
                ii.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                ii.quantity_on_hand,
                ii.quantity_reserved,
                (ii.quantity_on_hand - ii.quantity_reserved) AS quantity_available,
                ii.min_threshold,
                ii.max_threshold,
                ii.last_stock_check,
                CASE WHEN ii.quantity_on_hand <= ii.min_threshold THEN 1 ELSE 0 END AS is_low_stock,
                CASE WHEN ii.quantity_on_hand >= ii.max_threshold THEN 1 ELSE 0 END AS is_over_stock,
                CASE WHEN b.expiry_date IS NOT NULL AND DATEDIFF(b.expiry_date, CURDATE()) <= 90 THEN 1 ELSE 0 END AS is_near_expiry
            FROM inventory_items ii
            INNER JOIN products p ON ii.product_id = p.id
            LEFT JOIN batches b ON ii.batch_id = b.id
            INNER JOIN warehouses w ON ii.warehouse_id = w.id
            WHERE ii.warehouse_id = ?
            ORDER BY p.name
        """;

        return queryForList(sql, params(warehouseId), this::mapToResponse);
    }

    @Override
    public List<InventoryResponse> getByProduct(Long productId) {
        String sql = """
            SELECT
                ii.id,
                ii.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                ii.batch_id,
                b.batch_code,
                b.expiry_date,
                ii.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                ii.quantity_on_hand,
                ii.quantity_reserved,
                (ii.quantity_on_hand - ii.quantity_reserved) AS quantity_available,
                ii.min_threshold,
                ii.max_threshold,
                ii.last_stock_check,
                CASE WHEN ii.quantity_on_hand <= ii.min_threshold THEN 1 ELSE 0 END AS is_low_stock,
                CASE WHEN ii.quantity_on_hand >= ii.max_threshold THEN 1 ELSE 0 END AS is_over_stock,
                CASE WHEN b.expiry_date IS NOT NULL AND DATEDIFF(b.expiry_date, CURDATE()) <= 90 THEN 1 ELSE 0 END AS is_near_expiry
            FROM inventory_items ii
            INNER JOIN products p ON ii.product_id = p.id
            LEFT JOIN batches b ON ii.batch_id = b.id
            INNER JOIN warehouses w ON ii.warehouse_id = w.id
            WHERE ii.product_id = ?
            ORDER BY w.name, b.expiry_date
        """;

        return queryForList(sql, params(productId), this::mapToResponse);
    }

    @Override
    public Optional<InventoryResponse> findByProductAndWarehouse(Long productId, Long warehouseId, Long batchId) {
        String sql = """
            SELECT
                ii.id,
                ii.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                ii.batch_id,
                b.batch_code,
                b.expiry_date,
                ii.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                ii.quantity_on_hand,
                ii.quantity_reserved,
                (ii.quantity_on_hand - ii.quantity_reserved) AS quantity_available,
                ii.min_threshold,
                ii.max_threshold,
                ii.last_stock_check,
                CASE WHEN ii.quantity_on_hand <= ii.min_threshold THEN 1 ELSE 0 END AS is_low_stock,
                CASE WHEN ii.quantity_on_hand >= ii.max_threshold THEN 1 ELSE 0 END AS is_over_stock,
                CASE WHEN b.expiry_date IS NOT NULL AND DATEDIFF(b.expiry_date, CURDATE()) <= 90 THEN 1 ELSE 0 END AS is_near_expiry
            FROM inventory_items ii
            INNER JOIN products p ON ii.product_id = p.id
            LEFT JOIN batches b ON ii.batch_id = b.id
            INNER JOIN warehouses w ON ii.warehouse_id = w.id
            WHERE ii.product_id = ?
              AND ii.warehouse_id = ?
              AND (ii.batch_id = ? OR (ii.batch_id IS NULL AND ? IS NULL))
        """;

        return queryForObject(sql, params(productId, warehouseId, batchId, batchId), this::mapToResponse);
    }

    @Override
    public List<InventoryResponse> getLowStockItems() {
        String sql = """
            SELECT
                ii.id,
                ii.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                ii.batch_id,
                b.batch_code,
                b.expiry_date,
                ii.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                ii.quantity_on_hand,
                ii.quantity_reserved,
                (ii.quantity_on_hand - ii.quantity_reserved) AS quantity_available,
                ii.min_threshold,
                ii.max_threshold,
                ii.last_stock_check,
                1 AS is_low_stock,
                CASE WHEN ii.quantity_on_hand >= ii.max_threshold THEN 1 ELSE 0 END AS is_over_stock,
                CASE WHEN b.expiry_date IS NOT NULL AND DATEDIFF(b.expiry_date, CURDATE()) <= 90 THEN 1 ELSE 0 END AS is_near_expiry
            FROM inventory_items ii
            INNER JOIN products p ON ii.product_id = p.id
            LEFT JOIN batches b ON ii.batch_id = b.id
            INNER JOIN warehouses w ON ii.warehouse_id = w.id
            WHERE ii.quantity_on_hand <= ii.min_threshold
            ORDER BY ii.quantity_on_hand ASC
        """;

        return queryForList(sql, this::mapToResponse);
    }

    @Override
    public List<InventoryResponse> getNearExpiryItems() {
        String sql = """
            SELECT
                ii.id,
                ii.product_id,
                p.code AS product_code,
                p.name AS product_name,
                p.unit,
                ii.batch_id,
                b.batch_code,
                b.expiry_date,
                ii.warehouse_id,
                w.code AS warehouse_code,
                w.name AS warehouse_name,
                ii.quantity_on_hand,
                ii.quantity_reserved,
                (ii.quantity_on_hand - ii.quantity_reserved) AS quantity_available,
                ii.min_threshold,
                ii.max_threshold,
                ii.last_stock_check,
                CASE WHEN ii.quantity_on_hand <= ii.min_threshold THEN 1 ELSE 0 END AS is_low_stock,
                CASE WHEN ii.quantity_on_hand >= ii.max_threshold THEN 1 ELSE 0 END AS is_over_stock,
                1 AS is_near_expiry
            FROM inventory_items ii
            INNER JOIN products p ON ii.product_id = p.id
            INNER JOIN batches b ON ii.batch_id = b.id
            INNER JOIN warehouses w ON ii.warehouse_id = w.id
            WHERE b.expiry_date IS NOT NULL
              AND DATEDIFF(b.expiry_date, CURDATE()) <= 90
              AND DATEDIFF(b.expiry_date, CURDATE()) >= 0
            ORDER BY b.expiry_date ASC
        """;

        return queryForList(sql, this::mapToResponse);
    }

    @Override
    public void updateThresholds(Long inventoryItemId, UpdateInventoryThresholdRequest request) {
        String sql = """
            UPDATE inventory_items
            SET min_threshold = ?,
                max_threshold = ?
            WHERE id = ?
        """;

        executeUpdateOrThrow(
            sql,
            params(request.getMinThreshold(), request.getMaxThreshold(), inventoryItemId),
            "Inventory item not found with id: " + inventoryItemId
        );
    }

    @Override
    public Integer getTotalQuantityByProduct(Long productId) {
        String sql = """
            SELECT COALESCE(SUM(quantity_on_hand), 0) AS total_quantity
            FROM inventory_items
            WHERE product_id = ?
        """;

        return queryForObject(sql, params(productId), rs -> rs.getInt("total_quantity"))
                .orElse(0);
    }

    @Override
    public boolean hasStock(Long productId, Long warehouseId, Integer requiredQuantity) {
        String sql = """
            SELECT COALESCE(SUM(quantity_on_hand - quantity_reserved), 0) AS available_quantity
            FROM inventory_items
            WHERE product_id = ? AND warehouse_id = ?
        """;

        Integer availableQuantity = queryForObject(
            sql,
            params(productId, warehouseId),
            rs -> rs.getInt("available_quantity")
        ).orElse(0);

        return availableQuantity >= requiredQuantity;
    }

    @Override
    public InventoryItemInfo getOrCreateInventoryItem(Long productId, Long batchId, Long warehouseId) {
        // Defensive validation to provide a clear error before hitting DB constraints
        if (productId == null) {
            throw new IllegalArgumentException("productId is required when creating or fetching inventory item");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("warehouseId is required when creating or fetching inventory item");
        }
         // Kiểm tra đã tồn tại chưa
         String selectSql = """
             SELECT id, quantity_on_hand
             FROM inventory_items
             WHERE product_id = ? 
               AND warehouse_id = ?
               AND (batch_id = ? OR (batch_id IS NULL AND ? IS NULL))
         """;

        Optional<InventoryItemInfo> existing = queryForObject(
            selectSql,
            params(productId, warehouseId, batchId, batchId),
            rs -> new InventoryItemInfo((int) rs.getLong("id"), rs.getInt("quantity_on_hand"))
        );

        if (existing.isPresent()) {
            return existing.get();
        }

        // Chưa tồn tại → Tạo mới với quantity = 0
        String insertSql = """
            INSERT INTO inventory_items
            (product_id, batch_id, warehouse_id, quantity_on_hand, quantity_reserved, min_threshold, max_threshold)
            VALUES (?, ?, ?, 0, 0, 10, 1000)
            ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)
        """;

        long newId = executeInsertAndReturnId(insertSql, ps -> {
            ps.setLong(1, productId);

            if (batchId != null) {
                ps.setLong(2, batchId);
            } else {
                ps.setNull(2, Types.BIGINT);
            }

            ps.setLong(3, warehouseId);
        });

        // Lấy lại quantity sau khi insert/update
        return queryForObject(
            selectSql,
            params(productId, warehouseId, batchId, batchId),
            rs -> new InventoryItemInfo((int) rs.getLong("id"), rs.getInt("quantity_on_hand"))
        ).orElse(new InventoryItemInfo((int) newId, 0));
    }

    @Override
    public void updateQuantity(Long inventoryItemId, Integer newQuantity) {
        String sql = """
            UPDATE inventory_items
            SET quantity_on_hand = ?,
                last_stock_check = CURDATE()
            WHERE id = ?
        """;

        executeUpdateOrThrow(
            sql,
            params(newQuantity, inventoryItemId),
            "Inventory item not found with id: " + inventoryItemId
        );
    }

    @Override
    public Integer getCurrentQuantity(Long productId, Long batchId, Long warehouseId) {
        String sql = """
            SELECT id, quantity_on_hand, batch_id
            FROM inventory_items
            WHERE product_id = ?
              AND warehouse_id = ?
              AND (batch_id = ? OR (batch_id IS NULL AND ? IS NULL))
        """;

        Optional<Integer> result = queryForObject(
            sql,
            params(productId, warehouseId, batchId, batchId),
            rs -> rs.getInt("quantity_on_hand")
        );

        return result.orElse(0);
    }

    @Override
    public void updateStock(Long productId, Long batchId, Long warehouseId, Integer newQuantity) {
        // Validate newQuantity
        if (newQuantity == null) {
            throw new IllegalArgumentException("newQuantity không được null");
        }
        if (newQuantity < 0) {
            throw new IllegalArgumentException("newQuantity không được âm: " + newQuantity);
        }

        String sql = """
        UPDATE inventory_items
        SET quantity_on_hand = ?, last_stock_check = CURDATE()
        WHERE product_id = ? AND warehouse_id = ?
          AND (batch_id = ? OR (batch_id IS NULL AND ? IS NULL))
    """;

        executeUpdateOrThrow(
                sql,
                params(newQuantity, productId, warehouseId, batchId, batchId),
                "Không tìm thấy tồn kho để cập nhật!"
        );
    }
    /**
     * Thêm parameter movementType thay vì hard-code EXPORT
     * */
    @Override
    public void insertStockMovement(Long productId, Long batchId, Long warehouseId,
                                    Integer quantity, Integer before, Integer after,
                                    Long userId, String note, String movementType) {
        String sql = """
            INSERT INTO stock_movements
            (movement_type, product_id, batch_id, warehouse_id, quantity, quantity_before, quantity_after, performed_by_user_id, note)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        executeUpdate(sql, ps -> {
            ps.setString(1, movementType != null ? movementType : "EXPORT");
            ps.setLong(2, productId);

            if (batchId != null) ps.setLong(3, batchId);
            else ps.setNull(3, Types.INTEGER);

            ps.setLong(4, warehouseId);
            ps.setInt(5, quantity);
            ps.setInt(6, before);
            ps.setInt(7, after);

            if (userId != null) ps.setLong(8, userId);
            else ps.setNull(8, Types.INTEGER);

            ps.setString(9, note);
        });

    }

    /**
         * Map ResultSet to InventoryResponse
         */
    private InventoryResponse mapToResponse(ResultSet rs) throws SQLException {
        InventoryResponse response = new InventoryResponse();

        response.setId(rs.getLong("id"));

        // Product info
        response.setProductId(rs.getLong("product_id"));
        response.setProductCode(rs.getString("product_code"));
        response.setProductName(rs.getString("product_name"));
        response.setUnit(rs.getString("unit"));

        // Batch info
        response.setBatchId(rs.getObject("batch_id", Long.class));
        response.setBatchCode(rs.getString("batch_code"));
        response.setExpiryDate(rs.getObject("expiry_date", LocalDate.class));

        // Warehouse info
        response.setWarehouseId(rs.getLong("warehouse_id"));
        response.setWarehouseCode(rs.getString("warehouse_code"));
        response.setWarehouseName(rs.getString("warehouse_name"));

        // Quantities
        response.setQuantityOnHand(rs.getInt("quantity_on_hand"));
        response.setQuantityReserved(rs.getInt("quantity_reserved"));
        response.setQuantityAvailable(rs.getInt("quantity_available"));

        // Thresholds
        response.setMinThreshold(rs.getInt("min_threshold"));
        response.setMaxThreshold(rs.getInt("max_threshold"));

        // Status indicators
        response.setIsLowStock(rs.getBoolean("is_low_stock"));
        response.setIsOverStock(rs.getBoolean("is_over_stock"));
        response.setIsNearExpiry(rs.getBoolean("is_near_expiry"));

        response.setLastStockCheck(rs.getObject("last_stock_check", LocalDate.class));

        return response;
    }
}
