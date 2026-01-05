package org.example.repository.impl;

import org.example.dto.request.Batch.CreateBatchRequest;
import org.example.dto.request.Batch.UpdateBatchRequest;
import org.example.dto.response.Batch.BatchResponse;
import org.example.repository.BaseRepository;
import org.example.repository.BatchRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của BatchRepository.
 */
public class BatchRepositoryImpl extends BaseRepository implements BatchRepository {

    /**
     * Tạo mới lô hàng
     * */
    @Override
    public long insert(CreateBatchRequest request) {
        String sql = """
            INSERT INTO batches
            (batch_code, product_id, import_price,
             manufacture_date, expiry_date, supplier_name, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        return executeInsertAndReturnId(sql, ps -> {
            ps.setString(1, request.getBatchCode());
            ps.setLong(2, request.getProductId());
            ps.setBigDecimal(3, request.getImportPrice());
            ps.setDate(4, java.sql.Date.valueOf(request.getManufactureDate()));
            ps.setDate(5, java.sql.Date.valueOf(request.getExpiryDate()));
            ps.setString(6, request.getSupplierName());
            ps.setString(7, request.getStatus()); // ACTIVE
        });
    }
    /**
     * Cho phép chỉnh sửa trạng thái và date
     * */
    @Override
    public void update(Long batchId, UpdateBatchRequest request) {
        String sql = """
            UPDATE batches
            SET expiry_date = ?,
                status = ?
            WHERE id = ?
        """;

        executeUpdateOrThrow(
                sql,
                ps -> {
                    ps.setDate(1, java.sql.Date.valueOf(request.getExpiryDate()));
                    ps.setString(2, request.getStatus());
                    ps.setLong(3, batchId);
                },
                "Batch not found with id: " + batchId
        );
    }


    /**
     * Cho phép disable lô hàng
     * */
    @Override
    public void disable(Long batchId) {
        String sql = """
            UPDATE batches
            SET status = 'BLOCKED'
            WHERE id = ?
        """;

        executeUpdateOrThrow(
                sql,
                params(batchId),
                "Batch not found with id: " + batchId
        );
    }
    /**
     * Lấy toàn bộ lô hàng
     * */
    @Override
    public List<BatchResponse> findAll() {
        String sql = """
            SELECT
                b.id,
                b.batch_code,
                b.product_id,
                p.name AS productName,
                b.import_price,
                b.manufacture_date,
                b.expiry_date,
                b.supplier_name,
                b.status
            FROM batches b
            JOIN products p ON b.product_id = p.id
            ORDER BY b.expiry_date
        """;

        return queryForList(sql, this::mapToRow);
    }

    @Override
    public BatchResponse findById(Long id) {
        String sql = """
                SELECT
                    b.id,
                    b.batch_code,
                    b.expiry_date,
                    b.status
                FROM batches b
                WHERE b.id = ?
                """;
        return queryForObject(sql, params(id), this::mapToRow).orElse(null);
    }

    /**
     * Lấy danh sách lô hàng theo sản phẩm
     * */
    @Override
    public List<BatchResponse> findByProduct(Long productId) {
        String sql = """
            SELECT
                b.id,
                b.batch_code,
                b.product_id,
                p.name AS productName,
                b.import_price,
                b.manufacture_date,
                b.expiry_date,
                b.supplier_name,
                b.status
            FROM batches b
            JOIN products p ON b.product_id = p.id
            WHERE b.product_id = ?
            ORDER BY b.expiry_date
        """;

        return queryForList(sql, params(productId), this::mapToRow);
    }
    /**
     * Lấy chi tiết 1 lô hàng theo ID
     * */
    @Override
    public Optional<BatchResponse> findDetail(Long batchId) {
        String sql = """
            SELECT
                b.id,
                b.batch_code,
                b.product_id,
                p.name AS productName,
                b.import_price,
                b.manufacture_date,
                b.expiry_date,
                b.supplier_name,
                b.status
            FROM batches b
            JOIN products p ON b.product_id = p.id
            WHERE b.id = ?
        """;

        return queryForObject(sql, params(batchId), this::mapToRow);
    }
    /**
     * Lấy các lô hàng sắp hết hạn trong N ngày tới.
     * */
    @Override
    public List<BatchResponse> findExpiringBetween(
            LocalDate fromDate,
            LocalDate toDate
    ) {
        String sql = """
        SELECT
            b.id,
            b.batch_code,
            b.product_id,
            p.name AS productName,
            b.manufacture_date,
            b.expiry_date,
            b.status
        FROM batches b
        JOIN products p ON b.product_id = p.id
        WHERE b.status = 'ACTIVE'
          AND b.expiry_date BETWEEN ? AND ?
        ORDER BY b.expiry_date ASC
    """;

        return queryForList(
                sql,
                ps -> {
                    ps.setDate(1, java.sql.Date.valueOf(fromDate));
                    ps.setDate(2, java.sql.Date.valueOf(toDate));
                },
                this::mapToRow
        );
    }

    /**
     * Chuyển đổi ResultSet thành BatchResponse
     * */
    private BatchResponse mapToRow(ResultSet rs) throws SQLException {
        BatchResponse b = new BatchResponse();
        b.setId(rs.getLong("id"));
        b.setBatchCode(rs.getString("batch_code"));
        b.setProductId(rs.getLong("product_id"));
        b.setProductName(rs.getString("productName"));
        b.setImportPrice(rs.getBigDecimal("import_price"));
        b.setManufactureDate(rs.getDate("manufacture_date").toLocalDate());
        b.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        b.setSupplierName(rs.getString("supplier_name"));
        b.setStatus(rs.getString("status"));
        return b;
    }
}
