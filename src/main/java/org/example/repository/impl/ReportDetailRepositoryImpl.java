package org.example.repository.impl;

import org.example.dto.response.ReportDetailResponse;
import org.example.repository.BaseRepository;
import org.example.repository.ReportDetailRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Repository implementation cho báo cáo tồn kho + xuất nhập
 */
public class ReportDetailRepositoryImpl extends BaseRepository implements ReportDetailRepository {

    @Override
    public List<ReportDetailResponse> getReportDetails(
            String warehouseName,
            String productCode,
            Date fromDate,
            Date toDate
    ) {
        StringBuilder sql = new StringBuilder("""
        SELECT
            ii.id AS inventory_id,
            p.code AS product_code,
            p.name AS product_name,
            b.batch_code,
            w.name AS warehouse_name,
            ii.quantity_on_hand,
            ii.quantity_reserved,
            ii.min_threshold,
            ii.max_threshold,
            sm.movement_type,
            sm.quantity AS movement_quantity,
            sm.movement_date
        FROM inventory_items ii
        JOIN products p ON ii.product_id = p.id
        JOIN warehouses w ON ii.warehouse_id = w.id
        LEFT JOIN batches b ON ii.batch_id = b.id
        LEFT JOIN stock_movements sm
            ON sm.product_id = ii.product_id
            AND sm.warehouse_id = ii.warehouse_id
            AND (ii.batch_id IS NULL OR sm.batch_id = ii.batch_id)
            AND (? IS NULL OR sm.movement_date >= ?)
            AND (? IS NULL OR sm.movement_date <= ?)
        WHERE 1=1
    """);

        if (warehouseName != null && !warehouseName.isBlank()) {
            sql.append(" AND w.name = ?");
        }
        if (productCode != null && !productCode.isBlank()) {
            sql.append(" AND p.code = ?");
        }

        sql.append(" ORDER BY w.name, p.name, b.batch_code, sm.movement_date");

        return queryForList(sql.toString(), ps -> {
            int i = 1;

            // JOIN date conditions (4 params)
            ps.setTimestamp(i++, fromDate == null ? null : new java.sql.Timestamp(fromDate.getTime()));
            ps.setTimestamp(i++, fromDate == null ? null : new java.sql.Timestamp(fromDate.getTime()));
            ps.setTimestamp(i++, toDate == null ? null : new java.sql.Timestamp(toDate.getTime()));
            ps.setTimestamp(i++, toDate == null ? null : new java.sql.Timestamp(toDate.getTime()));

            if (warehouseName != null && !warehouseName.isBlank()) {
                ps.setString(i++, warehouseName);
            }
            if (productCode != null && !productCode.isBlank()) {
                ps.setString(i++, productCode);
            }
        }, this::mapRowToReportDetail);
    }


    private ReportDetailResponse mapRowToReportDetail(ResultSet rs) throws SQLException {
        ReportDetailResponse dto = new ReportDetailResponse();
        dto.setInventoryId(rs.getInt("inventory_id"));
        dto.setProductCode(rs.getString("product_code"));
        dto.setProductName(rs.getString("product_name"));
        dto.setBatchCode(rs.getString("batch_code"));
        dto.setWarehouseName(rs.getString("warehouse_name"));
        dto.setQuantityOnHand(rs.getInt("quantity_on_hand"));
        dto.setQuantityReserved(rs.getInt("quantity_reserved"));
        dto.setMinThreshold(rs.getInt("min_threshold"));
        dto.setMaxThreshold(rs.getInt("max_threshold"));
        dto.setMovementType(rs.getString("movement_type"));
        dto.setMovementQuantity(rs.getInt("movement_quantity"));
        dto.setMovementDate(rs.getTimestamp("movement_date"));
        return dto;
    }
}
