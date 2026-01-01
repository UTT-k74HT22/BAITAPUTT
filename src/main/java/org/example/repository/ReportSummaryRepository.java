package org.example.repository;
import java.math.BigDecimal;

public interface ReportSummaryRepository {
    long countProducts();
    long countCategories();
    long countWarehouses();
    long sumInventoryStock();
    long countExpiredBatches();
    BigDecimal sumRevenue();
}
