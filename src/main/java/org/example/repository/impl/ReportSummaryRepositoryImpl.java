package org.example.repository.impl;

import org.example.repository.BaseRepository;
import org.example.repository.ReportSummaryRepository;

import java.math.BigDecimal;

public class ReportSummaryRepositoryImpl
        extends BaseRepository
        implements ReportSummaryRepository {

    @Override
    public long countProducts() {
        String sql = "SELECT COUNT(*) FROM products";
        return queryForObject(sql, rs -> rs.getLong(1)).orElse(0L);
    }

    @Override
    public long countCategories() {
        String sql = "SELECT COUNT(*) FROM categories";
        return queryForObject(sql, rs -> rs.getLong(1)).orElse(0L);
    }

    @Override
    public long countWarehouses() {
        String sql = "SELECT COUNT(*) FROM warehouses";
        return queryForObject(sql, rs -> rs.getLong(1)).orElse(0L);
    }

    @Override
    public long sumInventoryStock() {
        String sql = "SELECT COALESCE(SUM(quantity_on_hand), 0) FROM inventory_items";
        return queryForObject(sql, rs -> rs.getLong(1)).orElse(0L);
    }

    @Override
    public long countExpiredBatches() {
        String sql = """
            SELECT COUNT(*)
            FROM batches
            WHERE expiry_date < CURRENT_DATE
        """;
        return queryForObject(sql, rs -> rs.getLong(1)).orElse(0L);
    }

    @Override
    public BigDecimal sumRevenue() {
        String sql = """
            SELECT COALESCE(SUM(o.total_amount), 0)
            FROM orders o
            WHERE o.status = 'COMPLETED'
        """;

        return queryForObject(sql, rs -> rs.getBigDecimal(1))
                .orElse(BigDecimal.ZERO);
    }
}
